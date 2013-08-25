/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowProjectFactory;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature;

/**
 * @author trip
 *
 */
public class WorkflowProjectFactory implements IWorkflowProjectFactory
{

	/**
	 * 
	 */
	public WorkflowProjectFactory()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowProjectFactory#convertToWorkflowProject(org.eclipse.core.resources.IProject)
	 */
	public IOpenVXMLProject convertToWorkflowProject(IProject project)
	{
		return new OpenVXMLProject(project);
	}

	public IOpenVXMLProject createWorkflowProject(String name)
	{
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
		    .getProject(name);
		try
		{
			newProject.create(null);
			newProject.open(null);
			
			IProjectDescription desc = newProject.getDescription();
			desc.setNatureIds(new String[] {WorkflowProjectNature.NATURE_ID});
			newProject.setDescription(desc, null);
			return convertToWorkflowProject(newProject);
		}
		catch(Exception ce)
		{
			ce.printStackTrace();
		}
		return null;
	}

}
