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
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Contract test for {@link OrderTotalLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class OrderTotalLookupImplTest extends AbstractTotalLookupContractTest<OrderEntity> {

	private static final String SCOPE = "scope";

	private static final String ORDER_ID = "xyz";

	private static final String ENCODED_ORDER_ID = Base32Util.encode(ORDER_ID);

	@Mock
	private TotalLookupStrategy mockStrategy;

	@InjectMocks
	private OrderTotalLookupImpl totalLookup;

	@Override
	protected OrderTotalLookupImpl createTotalLookupUnderTest() {
		return totalLookup;
	}
	@Override
	ResourceState<OrderEntity> createRepresentation() {
		OrderEntity entity = OrderEntity.builder()
				.withOrderId(ENCODED_ORDER_ID)
				.build();

		return ResourceState.Builder.create(entity)
				.withSelf(resourceSelf)
				.withScope(SCOPE)
				.build();
	}

	@Override
	protected void arrangeLookupToReturnTotals(final TotalEntity totalDto) {
		when(mockStrategy.getOrderTotal(SCOPE, ORDER_ID)).thenReturn(ExecutionResultFactory.createReadOK(totalDto));
	}

	@Override
	protected void arrangeLookupToReturnNotFound() {
		when(mockStrategy.getOrderTotal(SCOPE, ORDER_ID)).thenReturn(ExecutionResultFactory.<TotalEntity> createNotFound());
	}

	@Override
	protected String getRel() {
		return TotalResourceRels.ORDER_REL;
	}
}