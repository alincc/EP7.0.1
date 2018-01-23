/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;

/**
 * Test class for {@link StoreProductAvailabilityTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreProductAvailabilityTransformerTest {

	private static final String SKU_CODE = "skuCode";

	@Mock
	private DateTransformer dateTransformer;

	@InjectMocks
	private StoreProductAvailabilityTransformer storeProductAvailabilityTransformer;

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		storeProductAvailabilityTransformer.transformToDomain(null);
	}

	/**
	 * Test transform to entity.
	 */
	@Test
	public void testInternalTransformToEntity() {

		StoreProduct mockStoreProduct = mock(StoreProduct.class);
		ProductSku mockProductSku = mock(ProductSku.class);
		Date releaseDate = new Date();
		DateEntity dateEntity = ResourceTypeFactory.createResourceEntity(DateEntity.class);

		when(mockProductSku.getSkuCode()).thenReturn(SKU_CODE);
		when(mockStoreProduct.getExpectedReleaseDate()).thenReturn(releaseDate);
		when(dateTransformer.transformToEntity(releaseDate, Locale.ENGLISH))
			.thenReturn(dateEntity);
		when(mockStoreProduct.getSkuAvailability(SKU_CODE))
			.thenReturn(Availability.AVAILABLE);

		Pair<StoreProduct, ProductSku> storeProductSku = new Pair<>(mockStoreProduct, mockProductSku);

		AvailabilityEntity itemAvailabilityDto = storeProductAvailabilityTransformer.transformToEntity(storeProductSku, Locale.ENGLISH);

		assertEquals("State of item availability does not match expected state.",
				Objects.toString(Availability.AVAILABLE, StringUtils.EMPTY), itemAvailabilityDto.getState());
		assertEquals("Availability release date value does not match expected value.",
				dateEntity, itemAvailabilityDto.getReleaseDate());
	}

	/**
	 * Test transform to entity when expected release date is null.
	 */
	@Test
	public void testInternalTransformToEntityWithNullExpectedReleaseDate() {
		StoreProduct mockStoreProduct = mock(StoreProduct.class);
		ProductSku mockProductSku = mock(ProductSku.class);

		when(mockProductSku.getSkuCode()).thenReturn(SKU_CODE);
		when(mockStoreProduct.getExpectedReleaseDate()).thenReturn(null);
		when(mockStoreProduct.getSkuAvailability(SKU_CODE))
			.thenReturn(Availability.AVAILABLE);

		Pair<StoreProduct, ProductSku> storeProductSku = new Pair<>(mockStoreProduct, mockProductSku);

		AvailabilityEntity itemAvailabilityDto = storeProductAvailabilityTransformer.transformToEntity(storeProductSku, Locale.ENGLISH);

		assertEquals("State of item availability does not match expected state.",
				Objects.toString(Availability.AVAILABLE, StringUtils.EMPTY), itemAvailabilityDto.getState());
		assertNull("Release date from DTO should be null", itemAvailabilityDto.getReleaseDate());
	}
}
