/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Test class for {@link PromotionsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PromotionDetailsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "promotions";
	private static final String PROMOTION_NAME = "name";
	private static final String PROMOTION_DISPLAY_NAME = "Display Name";
	private static final String PROMOTION_DISPLAY_DESCRIPTION = "description";
	private static final String PROMOTION_DISPLAY_CONDITIONS = "conditions";
	private static final String PROMOTION_ID = "12345";
	private static final String ENCODED_PROMOTION_ID = Base32Util.encode(PROMOTION_ID);
	private static final String SCOPE = "scope";

	@Mock
	private PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	private PromotionDetailsTransformer promotionDetailsTransformer;

	private final PromotionEntity entityToTransform = PromotionEntity.builder()
			.withName(PROMOTION_NAME)
			.withDisplayName(PROMOTION_DISPLAY_NAME)
			.withDisplayDescription(PROMOTION_DISPLAY_DESCRIPTION)
			.withDisplayConditions(PROMOTION_DISPLAY_CONDITIONS)
			.withPromotionId(PROMOTION_ID)
			.build();


	@Before
	public void setUp() {
		when(promotionsUriBuilderFactory.get()).thenAnswer(invocation -> new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME));
		promotionDetailsTransformer =  new PromotionDetailsTransformer(promotionsUriBuilderFactory);
	}


	@Test
	public void testSelfIsCorrectWhenTransformingPromotionEntityToRepresentation() {
		Self expectedSelf = buildExpectedSelf();

		ResourceState<PromotionEntity> actualState =
				promotionDetailsTransformer.transform(SCOPE, entityToTransform);

		assertResourceState(actualState)
			.self(expectedSelf);
	}


	@Test
	public void testRepresentationIsCorrectWhenTransformingPromotionEntityToRepresentation() {
		ResourceState<PromotionEntity> expectedState = buildExpectedPromotionResourceState();

		ResourceState<PromotionEntity> actualState =
				promotionDetailsTransformer.transform(SCOPE, entityToTransform);

		assertEquals("Expected representation does not match actual.", expectedState, actualState);
	}


	private ResourceState<PromotionEntity> buildExpectedPromotionResourceState() {
		Self expectedSelf = buildExpectedSelf();

		PromotionEntity expectedTransformedEntity = PromotionEntity.builder()
				.withName(PROMOTION_NAME)
				.withDisplayName(PROMOTION_DISPLAY_NAME)
				.withDisplayDescription(PROMOTION_DISPLAY_DESCRIPTION)
				.withDisplayConditions(PROMOTION_DISPLAY_CONDITIONS)
				.withPromotionId(ENCODED_PROMOTION_ID)
				.build();

		return ResourceState.Builder.create(expectedTransformedEntity)
				.withSelf(expectedSelf)
				.build();
	}


	private Self buildExpectedSelf() {
		String expectedSelfUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME).setScope(SCOPE).setPromotionId(ENCODED_PROMOTION_ID).build();
		return SelfFactory.createSelf(expectedSelfUri);
	}
}
