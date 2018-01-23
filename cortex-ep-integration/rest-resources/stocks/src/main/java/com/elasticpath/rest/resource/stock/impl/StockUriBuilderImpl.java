/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.stock.items.Items;
import com.elasticpath.rest.schema.uri.StockUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Default implementation of {@link StockUriBuilder}.
 */
@Named("stockUriBuilder")
public class StockUriBuilderImpl implements StockUriBuilder {

	private final String resourceServerName;
	
	private String scope;
	
	private String itemId;
	
	/**
	 * Constructor accepting the resourceServerName.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	StockUriBuilderImpl(@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}
	
	@Override
	public StockUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope is required.";
		assert itemId != null : "itemId is required.";
		return URIUtil.format(resourceServerName, Items.URI_PART, scope, itemId);
	}

	@Override
	public StockUriBuilder setItemId(final String itemId) {
		this.itemId = itemId;
		return this;
	}

}
