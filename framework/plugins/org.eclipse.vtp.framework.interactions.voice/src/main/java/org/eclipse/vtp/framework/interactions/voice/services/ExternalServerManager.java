package org.eclipse.vtp.framework.interactions.voice.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExternalServerManager
{
	public static ExternalServerManager getInstance()
	{
		return instance;
	}
	
	private static final ExternalServerManager instance = new ExternalServerManager();
	
	private List<String> serverLocations = new ArrayList<String>();

	private ExternalServerManager()
	{
		try
		{
			InitialContext jndiContext = new InitialContext();
			String mediaServerCountString = (String)jndiContext.lookup("java:comp/env/media-server-count");
			if(mediaServerCountString != null)
			{
				int count = 0;
				try
				{
					count = Integer.parseInt(mediaServerCountString);
				}
				catch (NumberFormatException e)
				{
				}
				for(int i = 1; i < count + 1; i++)
				{
					try
					{
						String serverPrefix = (String)jndiContext.lookup("java:comp/env/media-server-" + i);
						serverLocations.add(serverPrefix);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		catch (NamingException e)
		{
			e.printStackTrace();
		}
	}

	public List<String> getLocations()
	{
		return Collections.unmodifiableList(serverLocations);
	}
	
	public void clearLocations()
	{
		serverLocations.clear();
	}
	
	public void addLocation(String location)
	{
		serverLocations.add(location);
	}
}
