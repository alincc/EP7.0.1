/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.recommendations.mapper.RecommendationsGroupMapper;

/**
 * Test for {@link RecommendationsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecommendationsTransformerTest {

	private static final String NAME = "name";

	@InjectMocks
	private RecommendationsTransformer transformer;

	@Mock
	private ProductAssociationType productAssociationType;
	@Mock
	private RecommendationsGroupMapper<ProductAssociationType> recommendationsGroupMapper;

	@Test
	public void testRecommendationsEntityHasNameAfterSuccessfulTransformation() {
		when(recommendationsGroupMapper.fromCommerceToCortex(productAssociationType)).thenReturn(NAME);

		RecommendationsEntity result = transformer.transformToEntity(productAssociationType);

		assertEquals("Name should be correctly set after transformation.", NAME, result.getName());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedOpFromTransformToDomain() {
		RecommendationsEntity entity = mock(RecommendationsEntity.class);

		transformer.transformToDomain(entity);
	}
}
