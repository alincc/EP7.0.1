/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.repo.camelimport;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.or.ObjectRenderer;

import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.MessageResolver;

/**
 * Performs the property substitution on Message objects when they are logged.
 */
public class IEMessageRenderer implements ObjectRenderer {

	private MessageResolver messageResolver;

	/** {@inheritDoc} */
	public String doRender(final Object message) {
		Message theMessage = (Message) message;
		return messageResolver.resolve(theMessage) + getExceptionInfo(theMessage.getException());
	}

	private String getExceptionInfo(final Throwable exception) {
		if (exception == null) {
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		stringWriter.write(". Associated exception: ");
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * Gets the message resolver.
	 * 
	 * @param messageResolver The message resolver.
	 */
	public void setMessageResolver(final MessageResolver messageResolver) {
		this.messageResolver = messageResolver;
	}
}