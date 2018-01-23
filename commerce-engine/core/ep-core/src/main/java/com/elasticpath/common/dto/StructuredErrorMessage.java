/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.dto;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

/**
 * Represents a commerce message data object.
 */
public class StructuredErrorMessage {

	private final String messageId;
	private final String debugMessage;
	private final Map<String, String> data;

	/**
	 * Constructor.
	 *
	 * @param messageId         messageId
	 * @param debugMessage      debug message
	 * @param data              data
	 */
	public StructuredErrorMessage(
			final String messageId,
			final String debugMessage,
			final Map<String, String> data) {

		this.messageId = messageId;
		this.debugMessage = debugMessage;
		this.data = data == null
				? null
				: ImmutableMap.copyOf(data);
	}

	/**
	 * Get the id of the message.
	 *
	 * @return  message Id
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Get the debug message for the structured message.
	 *
	 * @return the debug message
	 */
	public String getDebugMessage() {
		return debugMessage;
	}

	/**
	 * Get the map of additional information related to the message. This includes
	 * values that can be used to replace placeholders in message templates.
	 *
	 * @return the map.
	 */
	public Map<String, String> getData() {
		return data;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		StructuredErrorMessage that = (StructuredErrorMessage) other;

		return Objects.equals(messageId, that.messageId)
			&& Objects.equals(debugMessage, that.debugMessage)
			&& Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messageId, debugMessage, data);
	}
}
