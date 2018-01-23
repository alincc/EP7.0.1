/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilderFactory;

/**
 * URI builder factory for shipment line items.
 */
@Singleton
@Named("shipmentLineItemsUriBuilderFactory")
public final class ShipmentLineItemsUriBuilderFactoryImpl extends
		AbstractProviderDecoratorImpl<ShipmentLineItemsUriBuilder> implements
		ShipmentLineItemsUriBuilderFactory {
	/**
	 * Constructor.
	 * 
	 * @param shipmentLineItemsUriBuilderProvider the shipment line items URI builder provider.
	 */
	@Inject
	public ShipmentLineItemsUriBuilderFactoryImpl(@Named("shipmentLineItemsUriBuilder")
	final Provider<ShipmentLineItemsUriBuilder> shipmentLineItemsUriBuilderProvider) {
		super(shipmentLineItemsUriBuilderProvider);
	}
}