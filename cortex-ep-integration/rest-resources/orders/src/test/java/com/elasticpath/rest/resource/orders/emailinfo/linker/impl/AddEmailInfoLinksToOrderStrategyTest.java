/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.linker.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.elasticpath.rest.chain.BrokenChainException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfo;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoLookup;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link AddEmailInfoLinksToOrderStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddEmailInfoLinksToOrderStrategyTest {

	private static final String ORDER_URI = "/mockOrderUri";
	private static final String SCOPE = "SCOPE";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String EMAIL_ID = "EMAIL_ID";

	@Mock
	private EmailInfoLookup emailInfoLookup;

	@InjectMocks
	private AddEmailInfoLinksToOrderStrategy strategy;

	/**
	 * Test create links when no emailInfo needInfo.
	 */
	@Test
	public void testCreateLinksWithNoNeedInfo() {
		shouldFindEmailForOrderWithResult(ExecutionResultFactory.<String>createReadOK(EMAIL_ID));

		Iterable<ResourceLink> links = strategy.getLinks(createOrderResourceState());
		assertThat("There should be one link", Lists.newArrayList(links), Matchers.hasSize(1));
		assertThat("Email info link should be present.", links, Matchers.hasItem(createEmailInfoLink()));
		assertThat("Need info link should not be present.", links, Matchers.not(Matchers.hasItem(createEmailNeedInfoLink())));
	}

	/**
	 * Test create links when emailInfo has needInfo.
	 */
	@Test
	public void testCreateLinksWithNeedInfo() {
		shouldFindEmailForOrderWithResult(ExecutionResultFactory.<String>createNotFound());

		when(strategy.getLinks(createOrderResourceState())).thenThrow(BrokenChainException.class);
		Iterable<ResourceLink> links = strategy.getLinks(createOrderResourceState());
		assertThat("There should be two links", Lists.newArrayList(links), Matchers.hasSize(2));
		assertThat("Email info link should be present.", links, Matchers.hasItem(createEmailInfoLink()));
		assertThat("Need info link should be present.", links, Matchers.hasItem(createEmailNeedInfoLink()));
	}

	private void shouldFindEmailForOrderWithResult(final ExecutionResult<String> result) {
		when(emailInfoLookup.findEmailIdForOrder(SCOPE, ORDER_ID)).thenReturn(result);
	}

	private ResourceState<OrderEntity> createOrderResourceState() {
		OrderEntity orderEntity = OrderEntity.builder().withOrderId(ORDER_ID).build();
		return ResourceState.Builder.create(orderEntity).withSelf(SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id()))
				.withScope(SCOPE).build();
	}

	private ResourceLink createEmailInfoLink() {
		String emailInfoUri = URIUtil.format(ResourceStateUtil.getSelfUri(createOrderResourceState()), EmailInfo.URI_PART);
		return ResourceLinkFactory.create(emailInfoUri,
				ControlsMediaTypes.INFO.id(),
				EmailInfoRepresentationRels.EMAIL_INFO_REL,
				EmailInfoRepresentationRels.ORDER_REV);
	}

	private ResourceLink createEmailNeedInfoLink() {
		String emailInfoUri = URIUtil.format(ResourceStateUtil.getSelfUri(createOrderResourceState()), EmailInfo.URI_PART);
		return ResourceLinkFactory.createNoRev(emailInfoUri,
				ControlsMediaTypes.INFO.id(),
				NeedInfoRels.NEEDINFO);
	}
}
