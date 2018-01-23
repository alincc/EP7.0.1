/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.integration.epcommerce.transform;

import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link StoreProduct} into {@link AvailabilityEntity} and vice versa.
 */
@Singleton
@Named("storeProductAvailabilityTransformer")
public class StoreProductAvailabilityTransformer extends AbstractDomainTransformer<Pair<StoreProduct, ProductSku>, AvailabilityEntity> {

	private final DateTransformer dateTransformer;

	/**
	 * Default Constructor.
	 *
	 * @param dateTransformer the date transformer
	 */
	@Inject
	public StoreProductAvailabilityTransformer(
			@Named("dateTransformer")
			final DateTransformer dateTransformer) {

		this.dateTransformer = dateTransformer;
	}

	@Override
	public Pair<StoreProduct, ProductSku> transformToDomain(final AvailabilityEntity itemAvailabilityDto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public AvailabilityEntity transformToEntity(final Pair<StoreProduct, ProductSku> storeProductSku, final Locale locale) {
		StoreProduct storeProduct = storeProductSku.getFirst();
		String skuCode = storeProductSku.getSecond().getSkuCode();
		AvailabilityEntity.Builder builder = AvailabilityEntity.builder()
				.withState(Objects.toString(storeProduct.getSkuAvailability(skuCode), StringUtils.EMPTY));
		if (storeProduct.getExpectedReleaseDate() != null) {
			DateEntity releaseDate = dateTransformer.transformToEntity(storeProduct.getExpectedReleaseDate(), locale);
			builder.withReleaseDate(releaseDate);
		}

		return builder.build();
	}
}
