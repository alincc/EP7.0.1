/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.linker.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.needinfo.NeedInfoFromInfoCommand;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for  {@link LinkToBillingInfoStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LinkToBillingInfoStrategyTest {
	private static final String ORDER_ID = "ORDER_ID";
	private static final String SCOPE = "SCOPE";
	private static final String RESOURCE_NAME = "RESOURCE_NAME";
	private static final String ORDER_URI = URIUtil.format(RESOURCE_NAME, SCOPE, ORDER_ID);

	@Mock
	private Provider<NeedInfoFromInfoCommand.Builder> needInfoProvider;

	private final ResourceLink expectedBillingInfo = createExpectedBillingInfoLink(ORDER_URI);
	private final ResourceLink expectedNeedInfo = createExpectedNeedInfoLink(ORDER_URI);

	private ResourceState<OrderEntity> orderResourceState;

	@Before
	public void setUp() {
		OrderEntity orderEntity = OrderEntity.builder().withOrderId(ORDER_ID).build();
		Self orderSelf = SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id());
		orderResourceState = ResourceState.Builder.create(orderEntity).withSelf(orderSelf).build();
	}

	@Test
	public void testBillingInfoLinkSuccessfullyCreated() {

		mockNeedInfoProvider(orderResourceState, expectedNeedInfo);
		LinkToBillingInfoStrategy linkToBillingInfoStrategy = new LinkToBillingInfoStrategy(needInfoProvider);

		Iterable<ResourceLink> links = linkToBillingInfoStrategy.getLinks(orderResourceState);

		assertThat("The result links should contain the expected link.", links, Matchers.hasItems(expectedBillingInfo));
	}

	@Test
	public void testNeedInfoLinkPresent() {
		mockNeedInfoProvider(orderResourceState, expectedNeedInfo);
		LinkToBillingInfoStrategy linkToBillingInfoStrategy = new LinkToBillingInfoStrategy(needInfoProvider);

		Iterable<ResourceLink> links = linkToBillingInfoStrategy.getLinks(orderResourceState);

		assertThat(Lists.newArrayList(links), Matchers.hasSize(2));
		assertThat("The result links should contain the expected link.", links, Matchers.hasItems(expectedNeedInfo));
	}

	private void mockNeedInfoProvider(final ResourceState<OrderEntity> orderRepresentation, final ResourceLink expectedNeedInfo) {
		final NeedInfoFromInfoCommand.Builder builder = mock(NeedInfoFromInfoCommand.Builder.class);
		final NeedInfoFromInfoCommand mockNeedInfoCommand = mock(NeedInfoFromInfoCommand.class);
		when(needInfoProvider.get()).thenReturn(builder);
		when(builder.setInfoUri(any(String.class))).thenReturn(builder);
		when(builder.setInfoRel(any(String.class))).thenReturn(builder);
		when(builder.setNeededRel(BillingInfoRepresentationRels.BILLING_ADDRESS_REL)).thenReturn(builder);
		when(builder.setResourceState(orderRepresentation)).thenReturn(builder);
		when(builder.build()).thenReturn(mockNeedInfoCommand);

		Collection<ResourceLink> selectorLinks = Collections.singleton(expectedNeedInfo);
		final ExecutionResult<Collection<ResourceLink>> selectorResult = ExecutionResultFactory.createReadOK(selectorLinks);
		when(mockNeedInfoCommand.execute()).thenReturn(selectorResult);
	}

	private ResourceLink createExpectedBillingInfoLink(final String orderUri) {
		String expectedUri = URIUtil.format(orderUri, BillingAddressInfo.URI_PART);
		ResourceLink expectedBillingInfoLink = ResourceLinkFactory.create(expectedUri,
				ControlsMediaTypes.INFO.id(),
				BillingInfoRepresentationRels.BILLING_ADDRESS_INFO_REL,
				OrdersRepresentationRels.ORDER_REV);
		return expectedBillingInfoLink;
	}

	private ResourceLink createExpectedNeedInfoLink(final String orderUri) {
		String expectedUri = URIUtil.format(orderUri, BillingAddressInfo.URI_PART);
		ResourceLink expectedOrderNeedInfo = ResourceLinkFactory.createNoRev(expectedUri,
				ControlsMediaTypes.SELECTOR.id(),
				NeedInfoRels.NEEDINFO);
		return expectedOrderNeedInfo;
	}
}
