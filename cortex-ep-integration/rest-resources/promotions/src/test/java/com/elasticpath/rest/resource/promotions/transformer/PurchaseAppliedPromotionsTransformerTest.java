/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.promotions.PromotionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Test class for {@link PromotionsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchaseAppliedPromotionsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "promotions";
	private static final String PROMOTION_ID = "12345";
	private static final String ENCODED_PROMOTION_ID = Base32Util.encode(PROMOTION_ID);
	private static final String SOURCE_URI = "/some/uri/structure";

	@Mock
	private PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	@Mock
	private ResourceState<ResourceEntity> otherRepresentation;

	private PurchaseAppliedPromotionsTransformer purchaseAppliedPromotionsTransformer;

	@Before
	public void setUp() {
		when(promotionsUriBuilderFactory.get()).thenAnswer(invocation -> new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME));
		purchaseAppliedPromotionsTransformer = new PurchaseAppliedPromotionsTransformer(promotionsUriBuilderFactory);

		Self mockSelf = mock(Self.class);
		when(otherRepresentation.getSelf()).thenReturn(mockSelf);
		when(mockSelf.getUri()).thenReturn(SOURCE_URI);
	}

	@Test
	public void testSelfWhenTransformingIdCollectionToLinksRepresentation() {
		String expectedSelfUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(SOURCE_URI)
				.setPromotionType(AppliedPromotions.URI_PART)
				.build();
		Self expectedSelf = SelfFactory.createSelf(expectedSelfUri);

		ResourceState<LinksEntity> actualState =
				purchaseAppliedPromotionsTransformer.transform(Collections.singleton(PROMOTION_ID), otherRepresentation);

		assertResourceState(actualState)
			.self(expectedSelf);
	}


	@Test
	public void testLinkWhenTransformingIdCollectionToLinksRepresentation() {
		String expectedUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(SOURCE_URI)
				.setPromotionId(ENCODED_PROMOTION_ID)
				.build();
		ResourceLink expectedLink = ElementListFactory.createElement(expectedUri, PromotionsMediaTypes.PROMOTION.id());

		ResourceState<LinksEntity> actualState =
				purchaseAppliedPromotionsTransformer.transform(Collections.singleton(PROMOTION_ID), otherRepresentation);

		assertResourceState(actualState)
			.containsLinks(expectedLink);
	}
}
