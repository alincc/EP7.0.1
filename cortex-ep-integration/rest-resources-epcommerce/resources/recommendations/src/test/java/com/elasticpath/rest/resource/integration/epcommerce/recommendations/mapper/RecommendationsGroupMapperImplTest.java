/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.mapper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.elasticpath.domain.catalog.ProductAssociationType;

/**
 * Test for {@link RecommendationsGroupMapperImpl}.
 */
public class RecommendationsGroupMapperImplTest {

	private final RecommendationsGroupMapperImpl recommendationsGroupMapper = new RecommendationsGroupMapperImpl();

	@Test
	public void testAccessoriesCommerceToCortexSuccessfullyMapped() {
		testCommerceToCortex(ProductAssociationType.ACCESSORY, ProductAssociationType.ACCESSORY.getName().toLowerCase());
	}

	@Test
	public void testAccessoriesCortexToCommerceSuccessfullyMapped() {
		testCortexToCommerce(ProductAssociationType.ACCESSORY.getName().toLowerCase(), ProductAssociationType.ACCESSORY);
	}

	@Test
	public void testCrosssellsCommerceToCortexSuccessfullyMapped() {
		testCommerceToCortex(ProductAssociationType.CROSS_SELL, ProductAssociationType.CROSS_SELL.getName().toLowerCase());
	}

	@Test
	public void testCrosssellsCortexToCommerceSuccessfullyMapped() {
		testCortexToCommerce(ProductAssociationType.CROSS_SELL.getName().toLowerCase(), ProductAssociationType.CROSS_SELL);
	}

	@Test
	public void testReplacementsCommerceToCortexSuccessfullyMapped() {
		testCommerceToCortex(ProductAssociationType.REPLACEMENT, ProductAssociationType.REPLACEMENT.getName().toLowerCase());
	}

	@Test
	public void testReplacementsCortexToCommerceSuccessfullyMapped() {
		testCortexToCommerce(ProductAssociationType.REPLACEMENT.getName().toLowerCase(), ProductAssociationType.REPLACEMENT);
	}

	@Test
	public void testTopsellersCommerceToCortexSuccessfullyMapped() {
		testCommerceToCortex(ProductAssociationType.RECOMMENDATION, ProductAssociationType.RECOMMENDATION.getName().toLowerCase());
	}

	@Test
	public void testTopsellersCortexToCommerceSuccessfullyMapped() {
		testCortexToCommerce(ProductAssociationType.RECOMMENDATION.getName().toLowerCase(), ProductAssociationType.RECOMMENDATION);
	}

	@Test
	public void testUpsellsCommerceToCortexSuccessfullyMapped() {
		testCommerceToCortex(ProductAssociationType.UP_SELL, ProductAssociationType.UP_SELL.getName().toLowerCase());
	}

	@Test
	public void testUpsellsCortexToCommerceSuccessfullyMapped() {
		testCortexToCommerce(ProductAssociationType.UP_SELL.getName().toLowerCase(), ProductAssociationType.UP_SELL);
	}

	@Test
	public void testWarrantiesCommerceToCortexSuccessfullyMapped() {
		testCommerceToCortex(ProductAssociationType.WARRANTY, ProductAssociationType.WARRANTY.getName().toLowerCase());
	}

	@Test
	public void testWarrantiesCortexToCommerceSuccessfullyMapped() {
		testCortexToCommerce(ProductAssociationType.WARRANTY.getName().toLowerCase(), ProductAssociationType.WARRANTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnsupportedCortexGroupName() {
		String cortexName = "unsupportedName";
		recommendationsGroupMapper.fromCortexToCommerce(cortexName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnsupportedCommerceGroupName() {
		ProductAssociationType productAssociationType = mock(ProductAssociationType.class);
		recommendationsGroupMapper.fromCommerceToCortex(productAssociationType);
	}

	private void testCommerceToCortex(final ProductAssociationType commerceGroup,
			final String expectedCortexGroup) {
		String cortexName = recommendationsGroupMapper.fromCommerceToCortex(commerceGroup);
		assertEquals("Should match.", expectedCortexGroup, cortexName);
	}

	private void testCortexToCommerce(final String cortexGroup,
			final ProductAssociationType expectedCommerceGroup) {
		ProductAssociationType productAssociationType = recommendationsGroupMapper.fromCortexToCommerce(cortexGroup);
		assertEquals("Should match.", expectedCommerceGroup, productAssociationType);
	}
}
