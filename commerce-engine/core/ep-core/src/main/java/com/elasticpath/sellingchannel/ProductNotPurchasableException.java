/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.InvalidBusinessStateException;

/**
 * Exception thrown when product is not able to purchased.
 */
public class ProductNotPurchasableException extends EpSystemException implements InvalidBusinessStateException {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Creates a new <code>ProductNotPurchasableException</code> object with the given message.
	 *
	 * @param message the reason for this <code>ProductNotPurchasableException</code>.
	 */
	public ProductNotPurchasableException(final String message) {
		super(message);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Creates a new <code>ProductNotPurchasableException</code> object with the given message.
	 *
	 * @param message                 the reason for this <code>ProductNotPurchasableException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>ProductNotPurchasableException</code>.
	 */
	public ProductNotPurchasableException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}


	/**
	 * Creates a new <code>ProductNotPurchasableException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>ProductNotPurchasableException</code>.
	 * @param cause   the <code>Throwable</code> that caused this <code>ProductNotPurchasableException</code>.
	 */
	public ProductNotPurchasableException(final String message, final Throwable cause) {
		super(message, cause);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Creates a new <code>ProductNotPurchasableException</code> object using the given message and cause exception.
	 *
	 * @param message                 the reason for this <code>ProductNotPurchasableException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>ProductNotPurchasableException</code>.
	 * @param cause                   the <code>Throwable</code> that caused this <code>ProductNotPurchasableException</code>.
	 */
	public ProductNotPurchasableException(
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
