/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Contract test for {@link CartTotalLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class CartTotalLookupImplTest extends AbstractTotalLookupContractTest<CartEntity> {

	private static final String SCOPE = "scope";

	private static final String CART_ID = "abcde";

	private static final String ENCODED_CART_ID = Base32Util.encode(CART_ID);

	@Mock
	private TotalLookupStrategy mockStrategy;

	@InjectMocks
	private CartTotalLookupImpl totalLookup;

	@Override
	TotalLookup<CartEntity> createTotalLookupUnderTest() {
		return totalLookup;
	}

	@Override
	ResourceState<CartEntity> createRepresentation() {
		CartEntity entity = CartEntity.builder()
				.withCartId(ENCODED_CART_ID)
				.build();

		return ResourceState.Builder.create(entity)
				.withSelf(resourceSelf)
				.withScope(SCOPE)
				.build();
	}

	@Override
	void arrangeLookupToReturnTotals(final TotalEntity totalDto) {
		when(mockStrategy.getCartTotal(SCOPE, CART_ID)).thenReturn(ExecutionResultFactory.createReadOK(totalDto));
	}

	@Override
	void arrangeLookupToReturnNotFound() {
		when(mockStrategy.getCartTotal(SCOPE, CART_ID)).thenReturn(ExecutionResultFactory.<TotalEntity> createNotFound());
	}

	@Override
	String getRel() {
		return TotalResourceRels.CART_REL;
	}
}