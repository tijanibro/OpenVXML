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
package org.eclipse.vtp.desktop.projects.core.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.framework.util.Guid;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;
import com.openmethods.openvxml.desktop.model.branding.internal.Brand;
import com.openmethods.openvxml.desktop.model.branding.internal.DefaultBrandManager;

public class WorkflowProjectConfigurationPropertyPage extends PropertyPage
{
	private BrandConfigurationScreen screen = new BrandConfigurationScreen();
	private OpenVXMLProject applicationProject = null;
	private ConfigurationBrandManager brandManager = null;
	private IBrandingProjectAspect brandingAspect = null;

	public WorkflowProjectConfigurationPropertyPage()
	{
		DefaultBrandManager defaultManager = new DefaultBrandManager();
		defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
		brandManager = new ConfigurationBrandManager(defaultManager);
		screen.init(brandManager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element)
	{
		super.setElement(element);
		try
        {
	        if(element instanceof OpenVXMLProject)
	        	applicationProject = (OpenVXMLProject)element;
	        else if(element instanceof IProject)
	        {
	        	IProject project = (IProject)element;
	        	if(WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject(project))
	        		applicationProject = (OpenVXMLProject)WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
	        	else
	        		throw new RuntimeException("Unsupported element type");
	        }
	        else
        		throw new RuntimeException("Unsupported element type");
	        if(applicationProject.getParentProject() == null)
	        {
		        brandingAspect = (IBrandingProjectAspect)applicationProject.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
		        brandManager = new ConfigurationBrandManager(brandingAspect.getBrandManager());
		        screen.init(brandManager);
	        }
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new FillLayout());
		if(applicationProject.getParentProject() == null)
		{
			screen.createContents(comp);
		}
		else
		{
			Label inheritLabel = new Label(comp, SWT.NONE);
			inheritLabel.setText("This project is currently inheriting its configuration from the " + applicationProject.getParentProject().getName() + " Umbrella project.");
		}
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		if(applicationProject.getParentProject() == null)
		{
			if(applicationProject != null)
			{
				brandManager = new ConfigurationBrandManager(brandingAspect.getBrandManager());
			}
			else
			{
				DefaultBrandManager defaultManager = new DefaultBrandManager();
				defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
				brandManager = new ConfigurationBrandManager(defaultManager);
			}
			screen.init(brandManager);
		}
		super.performDefaults();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		if(applicationProject.getParentProject() == null)
		{
			brandManager.saveTo(brandingAspect.getBrandManager(), false);
			applicationProject.storeBuildPath();
		}
		return true;
	}
}
