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
package org.eclipse.vtp.desktop.projects.core.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;

import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObject;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectSet;

/**
 * This wizard walks the user through the steps required to create a
 * new business object for an application.  The user is prompted to
 * enter a name for the new business object.  This name must be unique
 * among the current business objects in the application.  The business
 * object is automatically created by this wizard and so requires no
 * actions from the caller of the wizard.
 *
 * @author Trip
 */
public class CreateBusinessObjectWizard extends Wizard implements INewWizard,
IExecutableExtension
{
	private Map<String,ProjectProperties> projectPropertiesList = new HashMap<String,ProjectProperties>();
	
	/**
	 * The wizard page that collects the name of the new business object.
	 */
	private BusinessObjectWizardPage boWizPage = null;

	IConfigurationElement configElement = null;
	
	/**
	 * Creates a new <code>CreateBusinessObjectWizard</code> instance for
	 * the given business object set.
	 *
	 * @param objectSet The business object set that will contain the new
	 * business object.
	 */
	public CreateBusinessObjectWizard()
	{
		super();
		boWizPage = new BusinessObjectWizardPage();
		addPage(boWizPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig,
		String propertyName, Object data)
	{
		configElement = cfig;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		//set up projectPropertiesList
		//get all the projects in the workspace
		IProject[] projs = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		//set up natures for project type recognition and display
		String workflowNature = org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature.NATURE_ID;
		String workflowNature5 = org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature5_0.NATURE_ID;
		String applicationNature = org.eclipse.vtp.desktop.projects.core.builder.VoiceApplicationNature.NATURE_ID;//legacy type
		String fragmentNature = org.eclipse.vtp.desktop.projects.core.builder.VoiceApplicationFragmentNature.NATURE_ID;//legacy type
		
		//TODO fix project setup and access the static NATURE_ID for these
//		String voiceNature = org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature.NATURE_ID;
//		String voiceNature5 = org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature5_0.NATURE_ID;
//		String personaNature = org.eclipse.vtp.desktop.projects.voice.builder.VoicePersonaNature.NATURE_ID;//legacy type
		String voiceNature = "org.eclipse.vtp.desktop.model.interactive.voice.VoiceProjectNature";
		String voiceNature5 = "org.eclipse.vtp.desktop.model.interactive.voice.VoiceProjectNature5_0";
		String personaNature = "org.eclipse.vtp.desktop.projects.voice.VoicePersonaNature";//legacy type
		
		for(IProject proj : projs)
		{
			ProjectProperties projProps = new ProjectProperties();
			
			//set the project name
			projProps.setName(proj.getName());
			
			//set the project type
			try{
			if(proj.getNature(workflowNature)!= null)
				projProps.setType("Workflow");
			}catch(Exception e){e.printStackTrace();}
			try{
			if(proj.getNature(workflowNature5)!= null)
				projProps.setType("Workflow");
			}catch(Exception e){e.printStackTrace();}
			try{
			if(proj.getNature(voiceNature)!= null)
				projProps.setType("Voice");
			}catch(Exception e){e.printStackTrace();}
			try{
			if(proj.getNature(voiceNature5)!= null)
				projProps.setType("Voice");
			}catch(Exception e){e.printStackTrace();}
			try{
			if(proj.getNature(applicationNature)!= null)
				projProps.setType("(Legacy) Application");
			}catch(Exception e){e.printStackTrace();}
			try{
			if(proj.getNature(fragmentNature)!= null)
				projProps.setType("(Legacy) Fragment");
			}catch(Exception e){e.printStackTrace();}
			try{
			if(proj.getNature(personaNature)!= null)
				projProps.setType("(Legacy) Voice");
			}catch(Exception e){e.printStackTrace();}

			//set the business object set
			if ("Workflow".equals(projProps.getType()))
			{
				OpenVXMLProject ovxmlProj = new OpenVXMLProject(proj);
				String aspectID = com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect.ASPECT_ID;
				IBusinessObjectProjectAspect boProjAspect = (IBusinessObjectProjectAspect) ovxmlProj.getProjectAspect(aspectID);
				projProps.setBoSet(boProjAspect.getBusinessObjectSet());
			}
			this.projectPropertiesList.put(projProps.getName(), projProps);
		}			
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			String workflowName = boWizPage.getSelectedWorkflowProjectName();
			ProjectProperties projProps = projectPropertiesList.get(workflowName);
			IBusinessObjectSet objectSet = projProps.getBoSet();
			
			IBusinessObject bo = objectSet.createBusinessObject(boWizPage.boNameField.getText());
			objectSet.refresh();
			IDE.openEditor(PlatformUI.getWorkbench()
					  .getActiveWorkbenchWindow()
					  .getActivePage(), bo.getUnderlyingFile());

			return true;
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}

		return false;
	}
	
	private class BusinessObjectWizardPage extends WizardPage
	{
		private static final String MESSAGE_NO_WORKFLOW_SELECTED = "Please select a Workflow project for this Business Object.";
		private static final String MESSAGE_EMPTY_BO_NAME = "Business Object name cannot be empty";
		private static final String MESSAGE_BO_NAME_TAKEN = "A Business Object already exists with that name.";
		
		Text boNameField = null;
		Tree tree = null;
		
		public BusinessObjectWizardPage()
		{
			super("BusinessObjectPage",
				"Enter a name for the new business object", null);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setPageComplete(false);

			//base composite to build on
			Composite comp = new Composite(parent, SWT.NONE);
			
			tree = createProjSelector(comp);
			
			//name label
			Label boNameLabel = new Label(comp, SWT.NONE);
			boNameLabel.setText("Business Object Name:");
			boNameLabel.setSize(boNameLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			
			//name field
			boNameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			
			boNameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
	                String text = e.text;
	                char[] chars = text.toCharArray();
					String currentName = boNameField.getText().substring(0, e.start) + e.text + boNameField.getText(e.end, (boNameField.getText().length() - 1));
	                if(currentName.length() > 255)
	                {
	                	e.doit = false;
	                	return;
	                }
	                for(int i = 0; i < chars.length; i++)
	                {
	                	if(Character.isLetterOrDigit(chars[i]))
	                		continue;
	                	if(chars[i] == '$')
	                		continue;
	                	if(chars[i] == '_')
	                		continue;
	                	if(chars[i] == '.')
	                		continue;
	                	e.doit = false;
	                	return;
	                }
	                
                }
				
			});
			boNameField.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						if(!isWorkflowProjectSelected())
						{
							setErrorMessage(MESSAGE_NO_WORKFLOW_SELECTED);
							setPageComplete(false);
							return;
						}
						
						if(boNameField.getText() == null || boNameField.getText().equals(""))
						{
							setErrorMessage(MESSAGE_EMPTY_BO_NAME);
							setPageComplete(false);
							return;
						}
						
						if(!isBoNameAvailable())
						{
							setErrorMessage(MESSAGE_BO_NAME_TAKEN);
							setPageComplete(false);
							return;
						}

						setErrorMessage(null);
						setPageComplete(true);
					}
				});
			
			//Layout section
			comp.setLayout(new FormLayout());

			FormData treeData = new FormData();
			treeData.left = new FormAttachment(0, 10);
			treeData.top = new FormAttachment(0, 30);
			treeData.right = new FormAttachment(100, 10);
			treeData.bottom = new FormAttachment(75,30);
			tree.setLayoutData(treeData);

			FormData boNameLabelData = new FormData();
			boNameLabelData.left = new FormAttachment(0, 10);
			boNameLabelData.top = new FormAttachment(tree, 30);
			boNameLabelData.right = new FormAttachment(0, 10 + boNameLabel.getSize().x);
			boNameLabelData.bottom = new FormAttachment(100, 30 + boNameLabel.getSize().y);
			boNameLabel.setLayoutData(boNameLabelData);

			FormData boNameFieldData = new FormData();
			boNameFieldData.left = new FormAttachment(boNameLabel, 10);
			boNameFieldData.top = new FormAttachment(tree, 30);
			boNameFieldData.right = new FormAttachment(100, 10);
			boNameLabelData.bottom = new FormAttachment(100, 30 + boNameField.getSize().y);
			boNameField.setLayoutData(boNameFieldData);
				
			setControl(comp);
		}
		
		/**
		 * Creates a Tree to use for viewing available projects and selecting which
		 * Workflow will contain the new Business Object
		 * 
		 * @param comp - the Composite on which to put the Tree
		 * @return a reference to the Tree
		 */
		private Tree createProjSelector(Composite comp)
		{
			//create the tree
			Tree tree = new Tree(comp, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			tree.setHeaderVisible(true);

			tree.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if(!isWorkflowProjectSelected())
					{
						setErrorMessage(MESSAGE_NO_WORKFLOW_SELECTED);
						setPageComplete(false);
						return;
					}
					
					if(boNameField.getText() == null || boNameField.getText().equals(""))
					{
						setErrorMessage(MESSAGE_EMPTY_BO_NAME);
						setPageComplete(false);
						return;
					}
					
					if(!isBoNameAvailable())
					{
						setErrorMessage(MESSAGE_BO_NAME_TAKEN);
						setPageComplete(false);
						return;
					}

					setErrorMessage(null);
					setPageComplete(true);
					
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					Object srcObj = e.getSource();
					if(!(srcObj instanceof Tree))
						return;
					
					Tree tree = (Tree)srcObj;
					if(tree.getSelectionCount() != 1)
						return;
					
					TreeItem selection = tree.getSelection()[0];
					if(selection.getItems().length > 0)
						selection.setExpanded(!selection.getExpanded());
				}
			});
			
			//set up the columns
			TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
			nameColumn.setText("Project Name");
			nameColumn.setWidth(260);
			TreeColumn typeColumn = new TreeColumn(tree, SWT.CENTER);
			typeColumn.setText("Type");
			typeColumn.setWidth(130);
			TreeColumn boColumn = new TreeColumn(tree, SWT.RIGHT);
			boColumn.setText("Business Objects");
			boColumn.setWidth(210);

			Iterator<String> i = projectPropertiesList.keySet().iterator();
			while(i.hasNext())
			{
				ProjectProperties projProps = projectPropertiesList.get(i.next());
				
				String projBoCount = "N/A";
				List <IBusinessObject> bos = new ArrayList<IBusinessObject>();
				
				if ("Workflow".equals(projProps.getType()))
				{
					bos = projProps.getBoSet().getBusinessObjects();
					projBoCount = bos.size() + " Business Object" + (bos.size() == 1 ? "" : "s");
				}
				
				//create the tree item
				TreeItem projItem = new TreeItem(tree, SWT.NONE);
				projItem.setText(new String[] {projProps.getName(),projProps.getType(), projBoCount});
				for(IBusinessObject bo : bos)
				{
					TreeItem boItem = new TreeItem(projItem, SWT.NONE);
					boItem.setText(new String[] {"-","Business Object",bo.getName()});
				}
			}

			return tree;
		}

		/**
		 * 
		 * @return The name of the currently selected workflow project in the tree or 
		 * null if the selection is empty, multiple, or a not a workflow or one of its members
		 */
		private String getSelectedWorkflowProjectName()
		{
			//Make sure one item is selected
			if(tree.getSelection().length != 1)
				return null;
			
			//Get the selection and make sure it's a project
			TreeItem selection = tree.getSelection()[0];
			if("Business Object".equals(selection.getText(1)))
				selection = selection.getParentItem();
			
			//is it a workflow?
			if("Workflow".equals(selection.getText(1)))
				return selection.getText(0);
			
			return null;
		}
		
		/**
		 * 
		 * @return true if a workflow project or one of its members is selected
		 */
		private boolean isWorkflowProjectSelected()
		{
			if(getSelectedWorkflowProjectName() != null)
				return true;
			return false;
		}
		
		/**
		 * 
		 * @return true if a workflow project is selected and the
		 * proposed name is neither empty nor taken
		 */
		private boolean isBoNameAvailable()
		{
			String proposedBoName = boNameField.getText();
			if(proposedBoName == null || proposedBoName.equals(""))
				return false;
			
			//get the workflowname
			String workflowName = getSelectedWorkflowProjectName();
			if(workflowName == null)
				return false;
			
			//Is the proposed name taken
			ProjectProperties projProps = projectPropertiesList.get(workflowName);
			for(IBusinessObject bo : projProps.getBoSet().getBusinessObjects())
			{
				if(bo.getName().equalsIgnoreCase(proposedBoName))
					return false;
			}

			return true;
		}
		
		
	}
	
	class ProjectProperties
	{
		private String name = "";
		private String type = "Other";
		private IBusinessObjectSet boSet = null;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public IBusinessObjectSet getBoSet() {
			return boSet;
		}
		public void setBoSet(IBusinessObjectSet boSet) {
			this.boSet = boSet;
		}
	}
	
}
