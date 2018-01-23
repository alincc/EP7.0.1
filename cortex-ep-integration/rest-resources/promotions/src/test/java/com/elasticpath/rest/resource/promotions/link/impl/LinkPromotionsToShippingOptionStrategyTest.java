/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import static org.mockito.Mockito.when;

import org.mockito.Mock;

import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test class for {@link com.elasticpath.rest.resource.promotions.link.impl.LinkPromotionsToShippingOptionStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkPromotionsToShippingOptionStrategyTest extends AbstractLinkAppliedPromotionsContractTest<ShippingOptionEntity> {

	static final String SHIPMENT_DETAILS_URI = "/shipmentdetails/uri";

	@Mock
	private ResourceState<ShippingOptionEntity> resourceState;

	@Override
	LinkPromotionsToShippingOptionStrategy createLinkStrategyUnderTest() {
		return new LinkPromotionsToShippingOptionStrategy(promotionsLinkCreator);
	}

	@Override
	ResourceState<ShippingOptionEntity> createLinkingRepresentationUnderTest() {
		Self self = SelfFactory.createSelf(SHIPMENT_DETAILS_URI, ShipmentdetailsMediaTypes.SHIPPING_OPTION.id());
		when(resourceState.getSelf()).thenReturn(self);
		when(resourceState.getScope()).thenReturn(SCOPE);
		return resourceState;
	}

	protected String getSourceUri() {
		return SHIPMENT_DETAILS_URI;
	}
}
