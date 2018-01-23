/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds a URI for an item definition.
 */
@Named("itemDefinitionsUriBuilder")
public final class ItemDefinitionsUriBuilderImpl implements ItemDefinitionsUriBuilder {
	private String scope;
	private String itemId;

	private final String resourceServerName;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public ItemDefinitionsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public ItemDefinitionsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public ItemDefinitionsUriBuilder setItemId(final String itemId) {
		this.itemId = itemId;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope, itemId);
	}
}
