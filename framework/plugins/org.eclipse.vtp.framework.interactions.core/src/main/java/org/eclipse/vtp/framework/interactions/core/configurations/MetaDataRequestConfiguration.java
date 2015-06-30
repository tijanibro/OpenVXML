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
package org.eclipse.vtp.framework.interactions.core.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MetaDataRequestConfiguration implements IConfiguration, InteractionsConstants
{
	private static final String inputTagName = "input";
	private static final String outputTagName = "output";
	/** The value to use for the left-hand side of the operation. */
	private String input = "";
	/** The value to use for the right-hand side of the operation. */
	private String output = "";

	public MetaDataRequestConfiguration()
	{
		System.out.println("CONSTRUCTING CONFIGURATION"); //TODO cleanup

	}

	public String getInput()
	{
		return input;
	}
	
	public void setInput(String input)
	{
		this.input = input == null ? "" : input;
	}


	public String getOutput()
	{
		return output;
	}
	
	public void setOutput(String output)
	{
		this.output = output == null ? "" : output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		System.out.println("CONFIG IS LOADING"); //TODO cleanup

		input = "";
		NodeList inputList = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, inputTagName);
		System.out.println("INPUTLIST.GETLENGTH(): "+ inputList.getLength()); //TODO cleanup

		if (inputList.getLength() > 0)
		{
			Element inputElement = (Element)inputList.item(0);
			input = inputElement.getAttribute(NAME_VALUE);
		}
		
		output = "";
		NodeList outputList = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, outputTagName);
		System.out.println("OUTPUTLIST.GETLENGTH():" + outputList.getLength()); //TODO cleanup
		if (outputList.getLength() > 0)
		{
			Element outputElement = (Element)outputList.item(0);
			output = outputElement.getAttribute(NAME_VALUE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		System.out.println("CONFIG IS SAVING"); //TODO cleanup

		String inName = inputTagName;
		String outName = outputTagName;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			inName = prefix + ":" + inName;
			outName = prefix + ":" + outName;
		}
		
		Element inputElement = configurationElement.getOwnerDocument().createElementNS(
				NAMESPACE_URI, inName);
		inputElement.setAttribute(NAME_VALUE, input);
		configurationElement.appendChild(inputElement);
		
		Element outputElement = configurationElement.getOwnerDocument().createElementNS(
				NAMESPACE_URI, outName);
		outputElement.setAttribute(NAME_VALUE, output);
		configurationElement.appendChild(outputElement);
	}
}
