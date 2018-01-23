/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sellingchannel;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.UnavailableException;

/**
 * Thrown when the user attempts to view a product that is not currently available.
 */
public class ProductUnavailableException extends EpSystemException implements UnavailableException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Creates a new <code>ProductUnavailableException</code> object with the given message.
	 *
	 * @param message the reason for this <code>EpWebException</code>.
	 * @deprecated use {@link #ProductUnavailableException(String, Collection <StructuredErrorMessage>)} instead.
	 */
	@Deprecated
	public ProductUnavailableException(final String message) {
		super(message);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Creates a new <code>ProductUnavailableException</code> object with the given message.
	 *
	 * @param message                 the reason for this <code>ProductUnavailableException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>ProductUnavailableException</code>.
	 */
	public ProductUnavailableException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}

	/**
	 * Creates a new <code>ProductUnavailableException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>ProductUnavailableException</code>.
	 * @param cause   the <code>Throwable</code> that caused this <code>ProductUnavailableException</code>.
	 * @deprecated use {@link #ProductUnavailableException(String, Collection<StructuredErrorMessage>, Throwable)} instead.
	 */
	@Deprecated
	public ProductUnavailableException(final String message, final Throwable cause) {
		super(message, cause);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Creates a new <code>ProductUnavailableException</code> object using the given message and cause exception.
	 *
	 * @param message                 the reason for this <code>ProductUnavailableException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>ProductUnavailableException</code>.
	 * @param cause                   the <code>Throwable</code> that caused this <code>ProductUnavailableException</code>.
	 */
	public ProductUnavailableException(
			final String message,
			final Collection<StructuredErrorMessage> structuredErrorMessages,
			final Throwable cause) {
		super(message, cause);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}

}
