/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.value.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionValueUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionValueUriBuilderFactory;

/**
 * URI builder factory for {@linkplain ShipmentLineItemOptionValueUriBuilder}}.
 */
@Singleton
@Named("shipmentLineItemOptionValueUriBuilderFactory")
public final class ShipmentLineItemOptionValueUriBuilderFactoryImpl
		extends AbstractProviderDecoratorImpl<ShipmentLineItemOptionValueUriBuilder> implements
		ShipmentLineItemOptionValueUriBuilderFactory {
	/**
	 * Constructor.
	 * 
	 * @param shipmentLineItemUriBuilderProvider the ShipmentLineItemOptionValueUriBuilder provider.
	 */
	@Inject
	public ShipmentLineItemOptionValueUriBuilderFactoryImpl(@Named("shipmentLineItemOptionValueUriBuilder")
	final Provider<ShipmentLineItemOptionValueUriBuilder> shipmentLineItemUriBuilderProvider) {
		super(shipmentLineItemUriBuilderProvider);
	}
}