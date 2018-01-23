/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilderFactory;

/**
 * URI builder factory for {@linkplain ShipmentLineItemOptionsUriBuilder}.
 */
@Singleton
@Named("shipmentLineItemOptionsUriBuilderFactory")
public final class ShipmentLineItemOptionsUriBuilderFactoryImpl extends
		AbstractProviderDecoratorImpl<ShipmentLineItemOptionsUriBuilder> implements
		ShipmentLineItemOptionsUriBuilderFactory {

	/**
	 * Constructor.
	 * 
	 * @param shipmentLineItemOptionsUriBuilderProvider the {@linkplain ShipmentLineItemOptionsUriBuilder} provider.
	 */
	@Inject
	public ShipmentLineItemOptionsUriBuilderFactoryImpl(@Named("shipmentLineItemOptionsUriBuilder")
	final Provider<ShipmentLineItemOptionsUriBuilder> shipmentLineItemOptionsUriBuilderProvider) {
		super(shipmentLineItemOptionsUriBuilderProvider);
	}
}