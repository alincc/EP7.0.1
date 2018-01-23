/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link OrderToTotalLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkOrderTotalStrategyTest  extends AbstractLinkToTotalsContractTest<OrderEntity> {

	@Override
	ResourceState<OrderEntity> createRepresentationUnderTest() {
		OrderEntity entity = OrderEntity.builder().build();
		return ResourceState.Builder.create(entity)
				.withSelf(self)
				.build();
	}

	@Override
	ResourceStateLinkHandler<OrderEntity> createLinkCommandStrategyUnderTest() {
		return new OrderToTotalLinkHandler(totalResourceLinkCreator);
	}

	@Override
	void arrangeTotalLookupToReturnTotals() {
		//none needed
	}


	@Override
	void arrangeTotalResourceLinkHelperToReturnResourceLink() {
		when(totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, TotalResourceRels.ORDER_REV))
				.thenReturn(Collections.singleton(resourceLink));
	}
}
