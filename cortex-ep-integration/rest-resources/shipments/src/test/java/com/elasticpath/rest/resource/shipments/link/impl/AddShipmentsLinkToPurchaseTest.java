/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.link.impl;

import static com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels.PURCHASE_REL;
import static com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels.SHIPMENTS_REL;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.impl.ShipmentsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Unit test for {@link AddShipmentsLinkToPurchase}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddShipmentsLinkToPurchaseTest {

	private static final String RESOURCE_SERVER_NAME = "shipments";
	private static final String ENCODED_PURCHASE_ID = Base32Util.encode("blah");
	private static final String PURCHASE_URI = URIUtil.format("purchases/mobee/", ENCODED_PURCHASE_ID);

	@Mock
	private ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;
	@InjectMocks
	private AddShipmentsLinkToPurchase linkStrategy;


	@Before
	public void setUp() {
		when(shipmentsUriBuilderFactory.get()).thenAnswer(invocation -> new ShipmentsUriBuilderImpl(RESOURCE_SERVER_NAME));
	}

	@Test
	public void testShipmentsLinkCreated() {
		ResourceState<PurchaseEntity> representation = createOtherRepresentation();
		ResourceLink expectedLink = buildExpectedPurchasesLink();

		Iterable<ResourceLink> links = linkStrategy.getLinks(representation);

		assertThat("The created links should be the same as expected", links, Matchers.hasItems(expectedLink));
	}

	private ResourceState<PurchaseEntity> createOtherRepresentation() {
		Self self = mock(Self.class);
		when(self.getUri()).thenReturn(PURCHASE_URI);
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withPurchaseId(ENCODED_PURCHASE_ID)
				.build();
		return ResourceState.Builder.create(purchaseEntity)
				.withSelf(self)
				.build();
	}

	private ResourceLink buildExpectedPurchasesLink() {
		String expectedUri = new ShipmentsUriBuilderImpl(RESOURCE_SERVER_NAME).setSourceUri(PURCHASE_URI).build();
		return ResourceLinkFactory.create(expectedUri, CollectionsMediaTypes.LINKS.id(), SHIPMENTS_REL, PURCHASE_REL);
	}

}
