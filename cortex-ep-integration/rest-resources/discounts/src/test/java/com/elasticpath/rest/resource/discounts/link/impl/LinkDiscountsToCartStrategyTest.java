/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Unit test for discount link creation.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases") // tests are defined in the superclass
public class LinkDiscountsToCartStrategyTest extends AbstractLinkDiscountsContractTest<CartEntity> {

	@Mock
	CartEntity cartEntity;

	@Override
	ResourceState<CartEntity> createLinkingRepresentationUnderTest() {

		return ResourceState.Builder
				.create(cartEntity)
				.withSelf(SelfFactory.createSelf(SOURCE_URI, CartsMediaTypes.CART
						.id()))
				.build();
	}

	@Override
	ResourceStateLinkHandler<CartEntity> createLinkStrategyUnderTest() {

		return new LinkDiscountsToCartStrategy(discountsLinkCreator);
	}
}
