/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.Iterator;
import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Exit</code> class represents the &lt;exit&gt; VXML element.  This
 * element causes the VXML interpreter to immediately stop execution of the
 * current VXML document and halt the VXML application.
 *
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Exit extends Action
{
	/** A flag that allows this to function as Submit-Next */
	private boolean submit = false;
	
	/** URL Parameters for use with Submit-Next */
//	private final LinkedList<String> urlParameters = new LinkedList<String>();
	
	/** The method to use for Submit-Next. */
	private String method = null;
	
	/** The url to use for Submit-Next. */
	private String url = null;
	
	/** The list of variables to return. */
	private final LinkedList<String> names = new LinkedList<String>();

	/**
	 * Creates a new instance of <code>Exit</code>.
	 */
	public Exit(String[] names)
	{
		if(names != null)
		{
			for(String name : names)
				this.names.add(name);
		}
	}

	public boolean isSubmit() {
		return submit;
	}

	public void setSubmit(boolean submit) {
		this.submit = submit;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

//	public LinkedList<String> getUrlParameters() {
//		return urlParameters;
//	}
	
	//TODO add and remove parameter methods?
	

	public LinkedList<String> getNames() {
		return names;
	}

	/**
	 * Adds the variable name to the list of variable names that will be copied
	 * into the calling dialog's scope.
	 * 
	 * @param name The variable name to add.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void addName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		names.add(name);
	}

	/**
	 * Removes the variable name from the list of variable names that will be
	 * copied into the calling dialog's scope.
	 * 
	 * @param name The variable name to remove.
	 * @throws IllegalArgumentException If the supplied name is empty.
	 * @throws NullPointerException If the supplied name is <code>null</code>.
	 */
	public void removeName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		names.remove(name);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.VXMLWidget#writeWidget(
	 *      org.xml.sax.ContentHandler)
	 */
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		// Start and end the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		
		if(submit)
		{
			outputHandler.startElement(NAMESPACE_URI_VXML, NAME_SUBMIT, NAME_SUBMIT, attributes);
			// Write the children.
//			writeParameters(outputHandler);
//			writeEventHandlers(outputHandler);

			outputHandler.endElement(NAMESPACE_URI_VXML, NAME_SUBMIT, NAME_SUBMIT);
		}
		else
		{
			outputHandler.startElement(NAMESPACE_URI_VXML, NAME_EXIT, NAME_EXIT,
					attributes);
			outputHandler.endElement(NAMESPACE_URI_VXML, NAME_EXIT, NAME_EXIT);
		}
	}

	/**
	 * Write the attribute members of this action to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		if(submit && method != null)
			writeAttribute(attributes, null, null, NAME_METHOD, TYPE_CDATA, method);

		if (names.size() > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator<String> i = names.iterator(); i.hasNext();)
			{
				buffer.append(i.next());
				if (i.hasNext())
					buffer.append(' ');
			}
			writeAttribute(attributes, null, null, NAME_NAMELIST, TYPE_CDATA, buffer
					.toString());
		}
		
		if(submit && method != null)
			writeAttribute(attributes, null, null, NAME_METHOD, TYPE_CDATA, method);
	}
	
}
