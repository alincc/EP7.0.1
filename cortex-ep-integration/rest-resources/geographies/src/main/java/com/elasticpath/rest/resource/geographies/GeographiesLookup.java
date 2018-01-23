/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for Geographies data.
 */
public interface GeographiesLookup {

	/**
	 * Gets all the countries for a given scope.
	 *
	 * @param countriesIdentifier the countries Identifier
	 * @return a links representation to the Country resources.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAllCountries(CountriesIdentifier countriesIdentifier);

	/**
	 * Gets all the regions for a given country.
	 *
	 * @param regionsIdentifier the regions identifier
	 * @return a links representation to the Region resources.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAllRegions(RegionsIdentifier regionsIdentifier);

	/**
	 * Get the country data by the given country ID.
	 *
	 * @param countryIdentifier the country identifier
	 * @return the execution result with a country entity representation
	 */
	ExecutionResult<ResourceState<CountryEntity>> getCountry(CountryIdentifier countryIdentifier);

	/**
	 * Get the region data by the given region identifier.
	 *
	 * @param regionIdentifier  regionIdentifier
	 * @return the execution result with a region entity representation
	 */
	ExecutionResult<ResourceState<RegionEntity>> getRegion(RegionIdentifier regionIdentifier);
}
