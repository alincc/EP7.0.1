/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Creates items Uri.
 */
@Named("itemsUriBuilder")
public class ItemsUriBuilderImpl implements ItemsUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String itemId;


	/**
	 * Construct an ItemsUriBuilder.
	 *
	 * @param resourceServerName The resourceServer name.
	 */
	@Inject
	ItemsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public ItemsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public ItemsUriBuilder setItemId(final String itemId) {
		this.itemId = itemId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert itemId != null : "itemId required.";
		return URIUtil.format(resourceServerName, scope, itemId);
	}
}
