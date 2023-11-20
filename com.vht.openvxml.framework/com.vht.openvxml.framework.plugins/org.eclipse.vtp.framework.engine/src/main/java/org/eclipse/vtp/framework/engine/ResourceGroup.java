/*--------------------------------------------------------------------------
F * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.engine;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.voice.services.ExternalServer;
import org.eclipse.vtp.framework.interactions.voice.services.ExternalServerManager;
import org.eclipse.vtp.framework.interactions.voice.services.ExternalServerManagerListener;
import org.osgi.framework.Bundle;

/**
 * A group of public resources.
 * 
 * @author Lonnie Pryor
 */
public class ResourceGroup implements IResourceManager,
		ExternalServerManagerListener {
	/** The bundle to load from. */
	private final Bundle bundle;
	/** The base path to publish. */
	private final String path;
	private Object lock = new Object();
	private HashSet<String> index = new HashSet<String>();
	private static Map<String, Boolean> bundleList = new HashMap<String, Boolean> ();

	/**
	 * Creates a new ResourceGroup.
	 * 
	 * @param bundle
	 *            The bundle to load from.
	 * @param path
	 *            The base path to publish.
	 */
	public ResourceGroup(Bundle bundle, String path) {
		System.out.println("-----media ResourceGroup path: ---" + path);
		ExternalServerManager.getInstance().addListener(this);
		this.bundle = bundle;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		this.path = path;
		URL indexURL = bundle.getResource("files.index");
		System.out.println("-----media ResourceGroup indexURL: ---" + indexURL.toString());
		System.out.println("-----media ResourceGroup indexURL getPath: ---" + indexURL.getPath());
		System.out.println("-----media ResourceGroup indexURL getFile: ---" + indexURL.getFile());
		if (indexURL != null) {
			System.out.println("-----media ResourceGroup indexURL !=null: ---");
			if(!bundleList.containsKey(ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"))){
				System.out.println("-----media ResourceGroup bundleList !contains key: ---" + ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"));
				bundleList.put(ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"), true);
			}
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						indexURL.openConnection().getInputStream()));
				String line = br.readLine();
				System.out.println("-----media ResourceGroup line: ---");
				while (line != null) {
					index.add(line);
					System.out.println("-----media ResourceGroup line : ---" + line);
					line = br.readLine();
				}
				System.out.println("-----media ResourceGroup line end: ---");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						HashSet<String> localIndex = new HashSet<String>();
						ExternalServerManager.Logging logging = ExternalServerManager
								.getInstance().getLogging();
						List<ExternalServer> locations = ExternalServerManager
								.getInstance().getLocations();
						if (locations.size() > 0) {
							boolean connected = false;
							System.out.println("-----media ResourceGroup location start: ---");
							
							for (ExternalServer server : locations) {
								String location = server.getLocation();
								System.out.println("-----media ResourceGroup location: ---" + location);
								
								if (!location.endsWith("/")) {
									location = location + "/";
								}
								location = location
										+ ResourceGroup.this.bundle
												.getHeaders()
												.get("Bundle-Name") + "/";
								System.out.println("-----media ResourceGroup location 2: ---" + location);
								
								if (logging == ExternalServerManager.Logging.ALWAYS) {
									System.out
											.println("Attempting to load index from: "
													+ location);
								}
								try {
									URI indexURL = new URI(location);
									DefaultHttpClient httpclient = new DefaultHttpClient();
									HttpGet httpGet = new HttpGet(indexURL);

									HttpResponse response = httpclient
											.execute(httpGet);
									int statusCode = response.getStatusLine()
											.getStatusCode();
									if (statusCode != 200) {
										throw new Exception(
												"Error during request. "
														+ response
																.getStatusLine());
									}
									ResponseHandler<String> responseHandler = new BasicResponseHandler();
									String responseBody = responseHandler
											.handleResponse(response);
									BufferedReader br = new BufferedReader(
											new InputStreamReader(
													new ByteArrayInputStream(
															responseBody
																	.getBytes())));
									String line = br.readLine();
									System.out.println("-----media ResourceGroup localIndex line start: ---");
									while (line != null) {
										if (logging == ExternalServerManager.Logging.ALWAYS) {
											System.out
													.println(ResourceGroup.this.bundle
															.getHeaders()
															.get("Bundle-Name")
															+ " " + line);
										}
										System.out.println("-----media ResourceGroup localIndex line: ---" + line);
										localIndex.add(line);
										line = br.readLine();
									}
									System.out.println("-----media ResourceGroup localIndex line end ---" );
									br.close();
									index = localIndex;
									connected = true;
                                    					bundleList.put(ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"), true);
									server.setStatus(true);
									break;
								} catch (Exception e) {
									if (logging == ExternalServerManager.Logging.ALWAYS || 
											(logging == ExternalServerManager.Logging.FIRSTFAILURE && bundleList.get(ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"))&& !connected)){
										System.out.println("Unable to connect to external media server @ "+ location);
										e.printStackTrace();
									}
									server.setStatus(false);
								}
							}
							if (!connected
									&& logging != ExternalServerManager.Logging.NONE
									&&((logging == ExternalServerManager.Logging.FIRSTFAILURE && bundleList.get(ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"))))) {
								System.out.println("Unable to load index for "
										+ ResourceGroup.this.bundle
												.getHeaders()
												.get("Bundle-Name")
										+ " from any external media servers");
							}
							bundleList.put(ResourceGroup.this.bundle.getHeaders().get("Bundle-Name"), false);
						}
						try {
							synchronized (lock) {
								lock.wait(30000);
							}
						} catch (Exception ex) {

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, bundle.getSymbolicName() + "-index");
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Returns the requested resource.
	 * 
	 * @param fullResourcePath
	 *            The path of the resource to return.
	 * @return The requested resource.
	 */
	public URL getResource(String fullResourcePath) {
		System.out.println("resolving resource 1: fullResourcePath: "+ fullResourcePath);
		System.out.println("resolving resource 1: path: "+ path);
		if (!fullResourcePath.startsWith("/")) {
			fullResourcePath = "/" + fullResourcePath;
		}
		System.out.println("resolving resource 1: " + path + fullResourcePath);
		URL ret = bundle.getEntry(path + fullResourcePath);
		System.out.println("resolving resource 1: URL : ");
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IResourceManager#
	 * listResources(java.lang.String)
	 */
	@Override
	public String[] listResources(String fullDirectoryPath) {
		if (!fullDirectoryPath.startsWith("/")) {
			fullDirectoryPath = "/" + fullDirectoryPath;
		}
		LinkedList<String> list = new LinkedList<String>();
		for (Enumeration<String> e = bundle.getEntryPaths(path
				+ fullDirectoryPath); e != null && e.hasMoreElements();) {
			list.add(e.nextElement());
		}
		return list.toArray(new String[list.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IResourceManager#
	 * isDirectoryResource(java.lang.String)
	 */
	@Override
	public boolean isDirectoryResource(String fullDirectoryPath) {
		return fullDirectoryPath.endsWith("/"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.media.IResourceManager#
	 * isFileResource(java.lang.String)
	 */
	@Override
	public boolean isFileResource(String fullFilePath) {
		System.out.println("-----media conversation ResourceGroup isFileResource fullFilePath----------- : "+ fullFilePath);
		if (fullFilePath.startsWith("/")) {
			fullFilePath = fullFilePath.substring(1);
		}
		int slashIndex = fullFilePath.indexOf('/');
		if (slashIndex >= 0) {
			String prefix = fullFilePath.substring(0, slashIndex);
			System.out.println("-----media conversation ResourceGroup isFileResource prefix----------- : "+ prefix);
			
			String libraryFile = "/" + prefix + "/.library";
			System.out.println("-----media conversation ResourceGroup isFileResource libraryFile----------- : "+ libraryFile);
			
			if (!index.contains(libraryFile)
					&& getResource(libraryFile) == null) {
				System.out.println("-----media conversation ResourceGroup isFileResource !index.contains----------- : "+ libraryFile);
				
				fullFilePath = "Default/" + fullFilePath;
				System.out.println("-----media conversation ResourceGroup isFileResource !index.contains fullFilePath----------- : "+ fullFilePath);
				
			}
		} else {
			fullFilePath = "Default/" + fullFilePath;
			System.out.println("-----media conversation ResourceGroup isFileResource index contains fullFilePath----------- : "+ fullFilePath);
			
		}
		fullFilePath = "/" + fullFilePath;
		System.out.println("-----media conversation ResourceGroup isFileResource fullFilePath 1----------- : "+ fullFilePath);
		
		System.out.println("-----media conversation ResourceGroup isFileResource !isDirectoryResource(fullFilePath)---------- : "+ !isDirectoryResource(fullFilePath));
		
		System.out.println("-----media conversation ResourceGroup isFileResource (index.contains(fullFilePath)----------- : "+ (index.contains(fullFilePath)));
		Boolean s = (getResource(fullFilePath) != null);
		System.out.println("-----media conversation ResourceGroup isFileResource getResource(fullFilePath) != null----------- : "+ s);
		
		String joinedIndex = String.join(",", index);
		
		System.out.println("-----media conversation ResourceGroup isFileResource index firstIndex ----------- : "+ joinedIndex);
		
		if (joinedIndex.contains("Media Libraries")) {
			fullFilePath = "Media Libraries" + fullFilePath;
		}
		System.out.println("-----media conversation ResourceGroup isFileResource fullFilePath 2 ----------- : "+ fullFilePath);
		System.out.println("-----media conversation ResourceGroup isFileResource !isDirectoryResource(fullFilePath)---------- : "+ !isDirectoryResource(fullFilePath));
		
		System.out.println("-----media conversation ResourceGroup isFileResource (index.contains(fullFilePath)----------- : "+ (index.contains(fullFilePath)));
		s = (getResource(fullFilePath) != null);
			System.out.println("-----media conversation ResourceGroup isFileResource getResource(fullFilePath) != null----------- : "+ s);
			
		return !isDirectoryResource(fullFilePath)
				&& (index.contains(fullFilePath) || getResource(fullFilePath) != null);
	}

	@Override
	public boolean hasMediaLibrary(String libraryId) {
		String libraryPath = "/" + libraryId + "/.library";
		//-----media start
		System.out.println("-----media resourceManager libraryPath: "+ libraryPath);
		System.out.println("-----media resourceManager index: ---");  
		
		String joinedIndex = String.join(",", index);
		if (joinedIndex.contains("Media Libraries")) {
			libraryPath = "Media Libraries" + libraryPath;
		}
		// -----media start
		System.out.println("-----media resourceManager libraryPath 3: "
				+ libraryPath);
		// -----media end
		System.out.println("-----media resourceManager index End: ---");
		//-----media end
		System.out.println("-----media resourceManager index getResource: ---");
		getResource(libraryPath);
		System.out.println("-----media resourceManager index getResource End: ---");
		System.out.println("-----media resourceManager index index.contains(libraryPath): ---" + index.contains(libraryPath));
		return index.contains(libraryPath) || getResource(libraryPath) != null;
	}

	@Override
	public void locationsChanged() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}
}
