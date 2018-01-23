/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.FluentIterable;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.GeographiesMediaTypes;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.geographies.GeographiesLookup;
import com.elasticpath.rest.resource.geographies.constant.GeographiesResourceConstants;
import com.elasticpath.rest.resource.geographies.integration.GeographiesLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Implementation of {@link GeographiesLookup}.
 */
@Singleton
@Named("geographiesLookup")
public class GeographiesLookupImpl implements GeographiesLookup {
	private final GeographiesLookupStrategy geographiesLookupStrategy;
	private final IdentifierTransformerProvider transformerProvider;

	/**
	 * Constructor.
	 *
	 * @param transformerProvider identifier transformer provider
	 * @param geographiesLookupStrategy the geographies lookup strategy
	 */
	@Inject
	public GeographiesLookupImpl(
			@Named("identifierTransformerProvider")
			final IdentifierTransformerProvider transformerProvider,
			@Named("geographiesLookupStrategy")
			final GeographiesLookupStrategy geographiesLookupStrategy) {

		this.geographiesLookupStrategy = geographiesLookupStrategy;
		this.transformerProvider = transformerProvider;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getAllCountries(final CountriesIdentifier countriesIdentifier) {
			Iterable<CountryIdentifier> countries = geographiesLookupStrategy.getAllCountries(countriesIdentifier);
			ResourceState<LinksEntity> links = ResourceState.<LinksEntity>builder()
					.withResourceInfo(GeographiesResourceConstants.RESOURCE_INFO)
					.withSelf(SelfFactory.createSelf(
							transformerProvider
									.forClass(CountriesIdentifier.class)
									.identifierToUri(countriesIdentifier),
							CollectionsMediaTypes.LINKS.id()))
					.withLinks(FluentIterable.from(countries)
						.transform(countryId -> transformerProvider.forClass(CountryIdentifier.class).identifierToUri(countryId))
						.transform(uri -> ElementListFactory.createElement(uri, GeographiesMediaTypes.COUNTRY.id())))
					.build();
			return ExecutionResultFactory.createReadOK(links);
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getAllRegions(final RegionsIdentifier regionsIdentifier) {
		Iterable<RegionIdentifier> regions = geographiesLookupStrategy.getAllRegions(regionsIdentifier);
		ResourceState<LinksEntity> links = ResourceState.<LinksEntity>builder()
				.withResourceInfo(GeographiesResourceConstants.RESOURCE_INFO)
				.withSelf(SelfFactory.createSelf(
					transformerProvider
						.forClass(RegionsIdentifier.class)
						.identifierToUri(regionsIdentifier),
					CollectionsMediaTypes.LINKS.id()))
				.withLinks(FluentIterable.from(regions)
					.transform(regionId -> transformerProvider.forClass(RegionIdentifier.class).identifierToUri(regionId))
					.transform(uri -> ElementListFactory.createElement(uri, GeographiesMediaTypes.REGION.id())))
				.build();
		return ExecutionResultFactory.createReadOK(links);
	}

	@Override
	public ExecutionResult<ResourceState<CountryEntity>> getCountry(final CountryIdentifier countryIdentifier) {

		CountryEntity country = Assign.ifSuccessful(geographiesLookupStrategy.getCountry(countryIdentifier));
		return ExecutionResultFactory.createReadOK(ResourceState.Builder.create(country)
				.withResourceInfo(GeographiesResourceConstants.RESOURCE_INFO)
				.withSelf(SelfFactory.createSelf(
						transformerProvider
								.forClass(CountryIdentifier.class)
								.identifierToUri(countryIdentifier),
						GeographiesMediaTypes.COUNTRY.id()))
				.build()
		);
	}

	@Override
	public ExecutionResult<ResourceState<RegionEntity>> getRegion(final RegionIdentifier regionIdentifier) {

		RegionEntity region = Assign.ifSuccessful(geographiesLookupStrategy.getRegion(regionIdentifier));
		return ExecutionResultFactory.createReadOK(ResourceState.Builder.create(region)
				.withResourceInfo(GeographiesResourceConstants.RESOURCE_INFO)
				.withSelf(SelfFactory.createSelf(
						transformerProvider
								.forClass(RegionIdentifier.class)
								.identifierToUri(regionIdentifier),
						GeographiesMediaTypes.REGION.id()))
				.build()
		);
	}
}
