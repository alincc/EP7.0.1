/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;

/**
 * URI builder factory for shipment line item.
 */
@Singleton
@Named("shipmentLineItemUriBuilderFactory")
public final class ShipmentLineItemUriBuilderFactoryImpl extends
		AbstractProviderDecoratorImpl<ShipmentLineItemUriBuilder> implements
		ShipmentLineItemUriBuilderFactory {
	/**
	 * Constructor.
	 * 
	 * @param shipmentLineItemUriBuilderProvider the shipment line item URI builder provider.
	 */
	@Inject
	public ShipmentLineItemUriBuilderFactoryImpl(@Named("shipmentLineItemUriBuilder")
	final Provider<ShipmentLineItemUriBuilder> shipmentLineItemUriBuilderProvider) {
		super(shipmentLineItemUriBuilderProvider);
	}
}