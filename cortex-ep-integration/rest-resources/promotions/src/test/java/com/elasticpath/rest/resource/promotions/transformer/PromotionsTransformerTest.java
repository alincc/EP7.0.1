/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
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
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;
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
public final class PromotionsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "promotions";
	private static final String PROMOTION_ID = "12345";
	private static final String ENCODED_PROMOTION_ID = Base32Util.encode(PROMOTION_ID);
	private static final String SCOPE = "scope";

	@Mock
	private PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	private PromotionsTransformer promotionsTransformer;

	@Before
	public void setUp() {
		when(promotionsUriBuilderFactory.get()).thenAnswer(invocation -> new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME));
		promotionsTransformer =  new PromotionsTransformer(promotionsUriBuilderFactory);
	}

	@Test
	public void testSelfWhenTransformingIdCollectionToLinksRepresentation() {
		String expectedSelfUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME).setScope(SCOPE).build();
		Self expectedSelf = SelfFactory.createSelf(expectedSelfUri);

		ResourceState<LinksEntity> actualState =
				promotionsTransformer.transform(SCOPE, Collections.singleton(PROMOTION_ID));

		assertResourceState(actualState)
			.self(expectedSelf);
	}


	@Test
	public void testLinkWhenTransformingIdCollectionToLinksRepresentation() {
		String expectedUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME).setScope(SCOPE).setPromotionId(ENCODED_PROMOTION_ID).build();
		ResourceLink expectedLink = ElementListFactory.createElement(expectedUri, PromotionsMediaTypes.PROMOTION.id());

		ResourceState<LinksEntity> actualState =
				promotionsTransformer.transform(SCOPE, Collections.singleton(PROMOTION_ID));

		assertResourceState(actualState)
			.containsLink(expectedLink);
	}
}
