/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.link.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.availabilities.integration.AvailabilityLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Test class for {@link com.elasticpath.rest.resource.availabilities.link.impl.AddAvailabilityNeedInfoLinksToOrderStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddAvailabilityNeedInfoLinksToOrderStrategyTest {

	private static final String ENCODED_CART_ID = "gnstoztdha4tmljtgu2dcljugjtdcllbmi3wkljvmjqtgyjrgfrdcnztha=";
	private static final String DECODED_CART_ID = "3e7fc896-3541-42f1-ab7e-5ba3a11b1738";
	private static final String SCOPE = "SCOPE";

	@Mock
	private AvailabilityLookupStrategy availabilityLookupStrategy;

	@Mock
	private TransformToResourceState<LineItemEntity, LineItemEntity> lineItemDetailsTransformer;

	@InjectMocks
	private AddAvailabilityNeedInfoLinksToOrderStrategy fixture;

	@Mock
	private ResourceState<OrderEntity> order;
	@Mock
	private OrderEntity orderEntity;

	@Before
	public void init() {
		when(order.getEntity()).thenReturn(orderEntity);
		when(orderEntity.getCartId()).thenReturn(ENCODED_CART_ID);
		when(order.getScope()).thenReturn(SCOPE);
	}

	@Test
	public void shouldReturnNonEmptyListOfNeedInfoLinksWhenUnavailableItemsExist() {

		LineItemEntity unavailableLineItemEntity = Mockito.mock(LineItemEntity.class);
		ResourceState<LineItemEntity> lineItem = Mockito.mock(ResourceState.class);
		Self lineItemSelf = Mockito.mock(Self.class);
		String selfURI = "self uri";

		when(availabilityLookupStrategy.getUnavailableLineItems(SCOPE, DECODED_CART_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(Arrays.asList(unavailableLineItemEntity)));
		when(lineItemDetailsTransformer.transform(SCOPE, unavailableLineItemEntity)).thenReturn(lineItem);
		when(lineItem.getSelf()).thenReturn(lineItemSelf);
		when(lineItemSelf.getUri()).thenReturn(selfURI);

		Collection<ResourceLink> actualLinks = fixture.getLinks(order);

		assertEquals("The collection must have 1 needInfo link", 1, actualLinks.size());

		ResourceLink needInfoLink = actualLinks.iterator().next();
		assertEquals("Resource link must be needinfo",  NeedInfoRels.NEEDINFO, needInfoLink.getRel());


	}

	@Test
	public void shouldReturnEmptyListWhenUnavailableItemsDoNotExist() {
		when(availabilityLookupStrategy.getUnavailableLineItems(SCOPE, DECODED_CART_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(Collections.<LineItemEntity>emptyList()));

		Collection<ResourceLink> actualLinks = fixture.getLinks(order);

		assertTrue("The list of needinfo links must be empty when all cart items are available", actualLinks.isEmpty());
	}
}

