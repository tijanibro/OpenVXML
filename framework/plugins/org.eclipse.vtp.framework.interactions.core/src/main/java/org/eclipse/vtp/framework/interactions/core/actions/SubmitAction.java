package org.eclipse.vtp.framework.interactions.core.actions;

import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.actions.ExitAction;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.ExitConfiguration;
import org.eclipse.vtp.framework.common.controller.IController;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SubmitConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.conversation.IEndMessage;

/**
 * FinalAction.
 * 
 * @author Lonnie Pryor
 */
public class SubmitAction extends ExitAction
{
	/** The conversation to use. */
	private final IConversation conversation;
	/** Comment for variableRegistry. */
	private final IVariableRegistry variableRegistry;
	/** The configurations to use. */
	protected final AssignmentConfiguration[] configurations;
	private final SubmitConfiguration submitConfiguration;

	/**
	 * Creates a new FinalAction.
	 * 
	 * @param context
	 * @param controller
	 * @param configuration
	 */
	public SubmitAction(IActionContext context, IController controller,
			SubmitConfiguration submitConfiguration, ExitConfiguration configuration, IConversation conversation,
			IVariableRegistry variableRegistry,
			AssignmentConfiguration[] configurations)
	{
		super(context, controller, configuration, configurations);
		this.conversation = conversation;
		this.variableRegistry = variableRegistry;
		this.configurations = configurations;
		this.submitConfiguration = submitConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.actions.ExitAction#execute()
	 */
	public IActionResult execute()
	{
		IEndMessage endMessage = conversation.createEndMessage(configurations);
		MediaConfiguration mediaConfig = submitConfiguration.getMediaConfiguration();
		endMessage.setVariableValue("*submit_destination", conversation.resolveProperty(mediaConfig.getPropertyConfiguration("destination"), false, false));
		endMessage.setVariableValue("*submit_method", conversation.resolveProperty(mediaConfig.getPropertyConfiguration("method"), false, false));
		endMessage.setVariableValue("*submit_isSubmit", "true");
		endMessage.enqueue();
		
		return context.createResult(IActionResult.RESULT_NAME_ABORT);
	}
}
