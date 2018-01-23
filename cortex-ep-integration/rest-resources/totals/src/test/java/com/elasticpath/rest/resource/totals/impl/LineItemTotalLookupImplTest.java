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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Contract test for {@link LineItemTotalLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class LineItemTotalLookupImplTest extends AbstractTotalLookupContractTest<LineItemEntity> {

	private static final String SCOPE = "scope";

	private static final String CART_ID = "abcde";

	private static final String ENCODED_CART_ID = Base32Util.encode(CART_ID);

	private static final String LINE_ITEM_ID = "12345";

	private static final String ENCODED_LINE_ITEM_ID = Base32Util.encode(LINE_ITEM_ID);


	@Mock
	private TotalLookupStrategy mockStrategy;

	@InjectMocks
	private LineItemTotalLookupImpl totalLookup;

	@Override
	TotalLookup<LineItemEntity> createTotalLookupUnderTest() {
		return totalLookup;
	}


	@Override
	ResourceState<LineItemEntity> createRepresentation() {
		LineItemEntity entity = LineItemEntity.builder()
				.withCartId(ENCODED_CART_ID)
				.withLineItemId(ENCODED_LINE_ITEM_ID)
				.build();

		return ResourceState.Builder.create(entity)
				.withSelf(resourceSelf)
				.withScope(SCOPE)
				.build();
	}

	@Override
	void arrangeLookupToReturnTotals(final TotalEntity totalDto) {
		when(mockStrategy.getLineItemTotal(SCOPE, CART_ID, LINE_ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(totalDto));
	}

	@Override
	void arrangeLookupToReturnNotFound() {
		when(mockStrategy.getLineItemTotal(SCOPE, CART_ID, LINE_ITEM_ID)).thenReturn(ExecutionResultFactory.<TotalEntity> createNotFound());
	}

	@Override
	String getRel() {
		return TotalResourceRels.LINE_ITEM_REL;
	}
}