/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.InvalidBusinessStateException;

/**
 * Exception is thrown when the shipment complete can't be executed.
 */
public class CompleteShipmentFailedException extends EpServiceException implements InvalidBusinessStateException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * The constructor.
	 *
	 * @param message the message
	 * @deprecated use {@link #CompleteShipmentFailedException(String, Collection <StructuredErrorMessage>)} instead.
	 */
	@Deprecated
	public CompleteShipmentFailedException(final String message) {
		super(message);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * The constructor.
	 *
	 * @param message                 the reason for this <code>CompleteShipmentFailedException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>CompleteShipmentFailedException</code>.
	 */
	public CompleteShipmentFailedException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message   the message
	 * @param throwable the cause
	 * @deprecated use {@link #CompleteShipmentFailedException(String, Collection<StructuredErrorMessage>, Throwable)} instead.
	 */
	@Deprecated
	public CompleteShipmentFailedException(final String message, final Throwable throwable) {
		super(message, throwable);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message                 the reason for this <code>CompleteShipmentFailedException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>CompleteShipmentFailedException</code>.
	 * @param cause                   the <code>Throwable</code> that caused this <code>CompleteShipmentFailedException</code>.
	 */
	public CompleteShipmentFailedException(
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
