package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

import com.openmethods.openvxml.desktop.model.branding.IBrand;

public interface IMediaProviderManager
{
	public IInteractiveWorkflowProject getInteractiveWorkflowProject();
	
	public IMediaProject getMediaProject(String interactionType, IBrand brand, String language);
	
	public IMediaProvider getMediaProvider(String interactionType, IBrand brand, String language);
}
