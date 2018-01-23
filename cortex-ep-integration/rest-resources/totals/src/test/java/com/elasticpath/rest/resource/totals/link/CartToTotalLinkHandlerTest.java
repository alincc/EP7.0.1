/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import static org.mockito.Mockito.when;

import java.util.Collections;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link CartToTotalLinkHandler}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class CartToTotalLinkHandlerTest extends AbstractLinkToTotalsContractTest<CartEntity> {

	@Override
	ResourceState<CartEntity> createRepresentationUnderTest() {
		CartEntity entity = CartEntity.builder().build();
		return ResourceState.Builder.create(entity)
				.withSelf(self)
				.build();
	}

	@Override
	ResourceStateLinkHandler<CartEntity> createLinkCommandStrategyUnderTest() {
		return new CartToTotalLinkHandler(totalResourceLinkCreator, totalLookup);
	}

	@Override
	void arrangeTotalLookupToReturnTotals() {
		when(totalLookup.getTotal(testRepresentation)).thenReturn(executionResult);
	}


	@Override
	void arrangeTotalResourceLinkHelperToReturnResourceLink() {
		when(totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, executionResult, TotalResourceRels.CART_REV))
				.thenReturn(Collections.singleton(resourceLink));
	}

}
