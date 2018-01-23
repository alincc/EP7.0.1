/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import static org.mockito.Mockito.when;

import java.util.Collections;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link com.elasticpath.rest.resource.totals.link.LineItemToTotalLinkHandler}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkLineItemTotalStrategyTest extends AbstractLinkToTotalsContractTest<LineItemEntity> {

	@Override
	ResourceState<LineItemEntity> createRepresentationUnderTest() {
		LineItemEntity entity = LineItemEntity.builder().build();
		return ResourceState.Builder.create(entity)
				.withSelf(self)
				.build();
	}

	@Override
	ResourceStateLinkHandler<LineItemEntity> createLinkCommandStrategyUnderTest() {
		return new LineItemToTotalLinkHandler(totalResourceLinkCreator, totalLookup);
	}

	@Override
	void arrangeTotalLookupToReturnTotals() {
		when(totalLookup.getTotal(testRepresentation)).thenReturn(executionResult);
	}

	@Override
	void arrangeTotalResourceLinkHelperToReturnResourceLink() {
		when(totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, executionResult, TotalResourceRels.LINE_ITEM_REV))
				.thenReturn(Collections.singleton(resourceLink));
	}
}
