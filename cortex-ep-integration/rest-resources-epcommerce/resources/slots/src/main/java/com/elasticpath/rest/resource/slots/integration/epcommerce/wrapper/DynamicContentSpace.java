/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.wrapper;


import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * Wraps ContentSpace and Dynamic Content to pass to transformer.
 */
public class DynamicContentSpace {

	private final ContentSpace contentSpace;
	private final DynamicContent dynamicContent;

	/**
	 * Constructor.
	 *
	 * @param contentSpace the content space
	 * @param dynamicContent the dynamic content
	 */
	public DynamicContentSpace(final ContentSpace contentSpace, final DynamicContent dynamicContent) {
		this.contentSpace = contentSpace;
		this.dynamicContent = dynamicContent;
	}

	/**
	 * Gets the content space object.
	 *
	 * @return the {@link ContentSpace}
	 */
	public ContentSpace getContentSpace() {
		return contentSpace;
	}

	/**
	 * Gets the dynamic content object.
	 *
	 * @return the {@link DynamicContent}
	 */
	public DynamicContent getDynamicContent() {
		return dynamicContent;
	}
}
