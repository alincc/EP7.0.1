/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.nls;

/**
 * LocalizedMessage object containing an id and a value.
 * The id is constructed from the bundle simple name and the property key.
 * The value is the property value.
 */
@SuppressWarnings("PMD.ShortVariable")
public final class LocalizedMessage {
	private final String id;
	private final String value;

	/**
	 * Constructor.
	 *
	 * @param id    property-key
	 * @param value localized property-value
	 */
	public LocalizedMessage(final String id, final String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * localized property value.
	 *
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * unique identifier for localized message, constructed from the bundle simple name
	 * and the property key.
	 *
	 * @return id
	 */
	public String getId() {
		return id;
	}

}
