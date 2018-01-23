/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionUriBuilderFactory;

/**
 * URI builder factory for {@linkplain ShipmentLineItemOptionUriBuilder}.
 */
@Singleton
@Named("shipmentLineItemOptionUriBuilderFactory")
public final class ShipmentLineItemOptionUriBuilderFactoryImpl extends
		AbstractProviderDecoratorImpl<ShipmentLineItemOptionUriBuilder> implements
		ShipmentLineItemOptionUriBuilderFactory {

	/**
	 * Constructor.
	 * 
	 * @param shipmentLineItemUriBuilderProvider the ShipmentLineItemOptionUriBuilder provider
	 */
	@Inject
	public ShipmentLineItemOptionUriBuilderFactoryImpl(@Named("shipmentLineItemOptionUriBuilder")
	final Provider<ShipmentLineItemOptionUriBuilder> shipmentLineItemUriBuilderProvider) {
		super(shipmentLineItemUriBuilderProvider);
	}
}