/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaLibrariesFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObjectContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IPromptSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is a concrete implementation of <code>IPersonaProject</code> and
 * provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public abstract class MediaProject extends MediaObject implements IMediaProject {
	private static final String HASHPREFIX = "MEDIAPROJECT";
	protected IProject project = null;
	private String id = null;

	/**
	 * Creates a new <code>Personaproject</code> with the given eclipse project
	 * resource.
	 *
	 * @param project
	 *            The eclipse project resource this persona project represents
	 */
	public MediaProject(IProject project) {
		super();
		this.project = project;
		try {
			IFile buildPath = project.getFile(".config");
			//-----media start
			System.out.println(" -----media MediaProject buildPath getFullPath :"+ buildPath.getFullPath());
			//-----media end
			DocumentBuilderFactory buildFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = buildFactory.newDocumentBuilder();

			if (!buildPath.isSynchronized(IResource.DEPTH_INFINITE)) {
				buildPath.refreshLocal(IResource.DEPTH_INFINITE, null);
			}


			
			Document doc = builder.parse(buildPath.getContents());
			//-----media start
			 ByteArrayOutputStream contentsResult = new ByteArrayOutputStream();
			 byte[] buffer = new byte[5024];
			 for (int length; (length = buildPath.getContents().read(buffer)) != -1; ) {
				 contentsResult.write(buffer, 0, length);
			 }
			// StandardCharsets.UTF_8.name() > JDK 7
			String  fullContentsResult = contentsResult.toString("UTF-8");
			System.out.println(" -----media MediaProject buildPath.getContents() :"+ fullContentsResult);
			System.out.println(" -----media MediaProject buildPath.getContents() end ------ :");
			//-----media end
			Element root = doc.getDocumentElement();
			loadConfig(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadConfig(Element rootElement) {
		id = rootElement.getAttribute("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IPersonaProject#getPersonaId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IPersonaProject#getPromptSet()
	 */
	@Override
	public IPromptSet getPromptSet() {
		return new PromptSet(project.getFile("Prompts.xml"), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IPersonaProject#getMediaFilesFolder
	 * ()
	 */
	@Override
	public IMediaLibrariesFolder getMediaLibrariesFolder() {
		IFolder f = project.getFolder("Default");
		System.out.println("-----media getMediaLibrariesFolder");
		if (!f.exists()) {
			System.out.println("-----media getMediaLibrariesFolder folder missing");
			
			throw new RuntimeException("Media Libraries filder is missing");
		}

		return new MediaLibrariesFolder(this, f);
	}

	@Override
	public List<IMediaObject> getChildren() {
		List<IMediaObject> children = new LinkedList<IMediaObject>();
		children.add(getPromptSet());
		children.add(getMediaLibrariesFolder());
		return children;
	}

	@Override
	protected String getObjectId() {
		return project.getFullPath().toPortableString();
	}

	@Override
	public IMediaProject getProject() {
		return this;
	}

	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public IMediaObjectContainer getParent() {
		return null;
	}

	@Override
	public IProject getUnderlyingProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IMediaProject) {
			return project.equals(((IMediaProject) obj).getUnderlyingProject());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (HASHPREFIX + project.toString()).hashCode();
	}
}
