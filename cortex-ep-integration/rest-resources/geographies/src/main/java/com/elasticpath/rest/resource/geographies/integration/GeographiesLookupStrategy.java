/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;

/**
 * The Interface GeographiesLookupStrategy.
 */
public interface GeographiesLookupStrategy {

	/**
	 * Get all countries for the given scope.
	 *
	 * @param countriesIdentifier the countries identifier
	 * @return the collection of country Identifiers
	 */
	Iterable<CountryIdentifier> getAllCountries(CountriesIdentifier countriesIdentifier);

	/**
	 * Get all regions for the given country and scope.
	 *
	 * @param regionsIdentifier the regions identifier
	 * @return a collection of region identifiers
	 */
	Iterable<RegionIdentifier> getAllRegions(RegionsIdentifier regionsIdentifier);

	/**
	 * Gets the country by given ID.
	 *
	 * @param countryIdentifier the country identifier
	 * @return the {@link CountryEntity}
	 */
	ExecutionResult<CountryEntity> getCountry(CountryIdentifier countryIdentifier);

	/**
	 * Gets the region by given id.
	 *
	 * @param regionIdentifier  the region identifier
	 * @return the {@link RegionEntity}
	 */
	ExecutionResult<RegionEntity> getRegion(RegionIdentifier regionIdentifier);
}
