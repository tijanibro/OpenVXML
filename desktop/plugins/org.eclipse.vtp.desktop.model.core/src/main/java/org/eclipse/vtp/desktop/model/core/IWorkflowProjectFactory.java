package org.eclipse.vtp.desktop.model.core;

import org.eclipse.core.resources.IProject;

/**
 * @author trip
 *
 */
public interface IWorkflowProjectFactory
{	
	public IOpenVXMLProject createWorkflowProject(String name);
	
	public IOpenVXMLProject convertToWorkflowProject(IProject project);
}
