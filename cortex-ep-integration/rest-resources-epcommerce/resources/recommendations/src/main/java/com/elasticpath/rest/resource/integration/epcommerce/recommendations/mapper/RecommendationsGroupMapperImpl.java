/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.mapper;

import java.util.Locale;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import com.elasticpath.domain.catalog.ProductAssociationType;

/**
 * Translates commerce-engine product association types to Cortex recommendation groups, and vice versa.
 */
@Singleton
@Named("recommendationsGroupMapper")
public class RecommendationsGroupMapperImpl implements RecommendationsGroupMapper<ProductAssociationType> {

	private static Map<String, ProductAssociationType> groupsMap;

	/**
	 * Initialize the map using the ProductAssociationType in the classpath.
	 */
	static {
		ImmutableMap.Builder<String, ProductAssociationType> builder = new ImmutableMap.Builder<>();
		for (ProductAssociationType productAssociationType : ProductAssociationType.values()) {
			builder.put(productAssociationType.getName().toLowerCase(Locale.getDefault()), productAssociationType);
		}

		groupsMap = builder.build();
	}

	@Override
	public String fromCommerceToCortex(final ProductAssociationType commerceGroupType) {
		Preconditions.checkArgument(commerceGroupType != null);
		Preconditions.checkArgument(commerceGroupType.getName() != null);
		return commerceGroupType.getName().toLowerCase();
	}

	@Override
	public ProductAssociationType fromCortexToCommerce(final String cortexGroupName) {
		Preconditions.checkArgument(cortexGroupName != null);
		String lowerCaseKey = cortexGroupName.toLowerCase(Locale.getDefault());
		Preconditions.checkArgument(groupsMap.containsKey(lowerCaseKey));
		return groupsMap.get(lowerCaseKey);
	}
}
