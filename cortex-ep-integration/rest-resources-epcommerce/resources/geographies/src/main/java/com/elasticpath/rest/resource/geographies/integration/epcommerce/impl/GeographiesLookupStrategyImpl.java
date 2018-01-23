/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.integration.epcommerce.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.Iterables;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.rest.TypeAdapter;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.definition.geographies.epcommerce.EpCountryIdentifier;
import com.elasticpath.rest.definition.geographies.epcommerce.EpRegionIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.geographies.integration.GeographiesLookupStrategy;

/**
 * Lookup strategy for geographies resource.
 */
@Singleton
@Named("geographiesLookupStrategy")
public class GeographiesLookupStrategyImpl implements GeographiesLookupStrategy {

	private static final String COUNTRY_NOT_FOUND_MESSAGE = "Cannot find country with scope %s";
	private static final String REGION_NOT_FOUND_MESSAGE = "region not found.";

	private final ResourceOperationContext resourceOperationContext;
	private final Geography geography;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param geography the CE geography service
	 */
	@Inject
	public GeographiesLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("geography")
			final Geography geography) {

		this.resourceOperationContext = resourceOperationContext;
		this.geography = geography;
	}

	@Override
	public Iterable<CountryIdentifier> getAllCountries(final CountriesIdentifier countriesIdentifier) {

		Iterable<String> countryCodes =  geography.getCountryCodes();

		return Iterables.transform(countryCodes, countryCode ->
			CountryIdentifier.builder()
				.withCountries(countriesIdentifier)
				.withCountryId(StringIdentifier.of(countryCode))
				.build());
	}

	@Override
	public Iterable<RegionIdentifier> getAllRegions(final RegionsIdentifier regionsIdentifier) {

		EpCountryIdentifier epCountryIdentifer = TypeAdapter.narrow(regionsIdentifier.getCountry(), EpCountryIdentifier.class);
		String countryId = epCountryIdentifer.getCountryId().getValue();
		Iterable<String> regionCodes = geography.getSubCountryCodes(countryId);

		return Iterables.transform(regionCodes, regionCode ->
			RegionIdentifier.builder()
				.withRegions(regionsIdentifier)
				.withRegionId(StringIdentifier.of(regionCode))
				.build());
	}

	@Override
	public ExecutionResult<CountryEntity> getCountry(final CountryIdentifier countryIdentifier) {

		EpCountryIdentifier epCountryIdentifer = TypeAdapter.narrow(countryIdentifier, EpCountryIdentifier.class);

		String scope = epCountryIdentifer.getCountries().getScope().getValue();
		String countryCode = epCountryIdentifer.getCountryId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		String displayName = Assign.ifNotNull(geography.getCountryDisplayName(countryCode, locale),
				OnFailure.returnNotFound(COUNTRY_NOT_FOUND_MESSAGE, scope));

		CountryEntity countryEntity = CountryEntity.builder()
				.withDisplayName(displayName)
				.withName(countryCode)
				.build();
		return ExecutionResultFactory.createReadOK(countryEntity);
	}

	@Override
	public ExecutionResult<RegionEntity> getRegion(final RegionIdentifier regionIdentifier) {

		EpRegionIdentifier epRegionIdentifier = TypeAdapter.narrow(regionIdentifier, EpRegionIdentifier.class);

		String countryCode = epRegionIdentifier
				.getRegions()
				.getCountry()
				.getCountryId()
				.getValue();
		String subCountryCode = epRegionIdentifier
				.getRegionId()
				.getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		String subCountryDisplayName = Assign.ifNotNull(
				geography.getSubCountryDisplayName(countryCode, subCountryCode, locale),
				OnFailure.returnNotFound(REGION_NOT_FOUND_MESSAGE));

		RegionEntity regionEntity = RegionEntity.builder()
				.withDisplayName(subCountryDisplayName)
				.withName(subCountryCode)
				.build();
		return ExecutionResultFactory.createReadOK(regionEntity);
	}
}
