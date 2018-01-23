/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static com.elasticpath.rest.test.AssertSelf.assertSelf;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
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
import com.elasticpath.rest.id.transform.ResourceIdentifierTransformer;
import com.elasticpath.rest.resource.geographies.constant.GeographiesResourceConstants;
import com.elasticpath.rest.resource.geographies.integration.GeographiesLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link GeographiesLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeographiesLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String COUNTRY_ID = "CA";
	private static final String COUNTRY_URI = URIUtil.format("geographies", SCOPE, COUNTRY_ID);
	private static final String REGIONS_URI = URIUtil.format(COUNTRY_URI, "regions");
	private static final String REGION_ID = "BC";
	private static final String REGION_URI = URIUtil.format(REGIONS_URI, REGION_ID);
	private static final String NOT_FOUND_MESSAGE = "Not found";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private GeographiesLookupStrategy mockLookupStrategy;
	@Mock
	private IdentifierTransformerProvider mockTransformerProvider;
	@Mock
	private ResourceIdentifierTransformer<RegionsIdentifier> mockRegionsTransformer;
	@Mock
	private ResourceIdentifierTransformer<CountriesIdentifier> mockCountriesTransformer;
	@Mock
	private ResourceIdentifierTransformer<CountryIdentifier> mockCountryTransformer;
	@Mock
	private ResourceIdentifierTransformer<RegionIdentifier> mockRegionTransformer;

	@InjectMocks
	private GeographiesLookupImpl geographiesLookup;

	@Before
	public void setUp() {
		prepareCountryTransformers();
		prepareRegionTransformers();
	}

	@Test
	public void testSuccessGetCountry() {
		CountryIdentifier countryIdentifier = mock(CountryIdentifier.class);
		CountryEntity countryEntity = CountryEntity.builder().build();
		when(mockLookupStrategy.getCountry(countryIdentifier))
				.thenReturn(ExecutionResultFactory.createReadOK(countryEntity));

		ExecutionResult<ResourceState<CountryEntity>> result = geographiesLookup.getCountry(countryIdentifier);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK);
		assertSelf(result.getData().getSelf())
				.type(GeographiesMediaTypes.COUNTRY.id())
				.uri(COUNTRY_URI);
		assertEquals(GeographiesResourceConstants.RESOURCE_INFO, result.getData().getResourceInfo());
	}

	@Test
	public void testGetCountryNotFoundThrowsBrokenChainException() {
		CountryIdentifier countryIdentifier = mock(CountryIdentifier.class);
		when(mockLookupStrategy.getCountry(countryIdentifier))
			.thenReturn(ExecutionResultFactory.<CountryEntity>createNotFound(NOT_FOUND_MESSAGE));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		geographiesLookup.getCountry(countryIdentifier);
	}

	@Test
	public void testSuccessGetRegion() {
		RegionIdentifier regionIdentifier = mock(RegionIdentifier.class);
		RegionEntity regionEntity = RegionEntity.builder().build();
		when(mockLookupStrategy.getRegion(regionIdentifier))
				.thenReturn(ExecutionResultFactory.createReadOK(regionEntity));

		ExecutionResult<ResourceState<RegionEntity>> result = geographiesLookup.getRegion(regionIdentifier);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK);
		assertSelf(result.getData().getSelf())
				.type(GeographiesMediaTypes.REGION.id())
				.uri(REGION_URI);
		assertEquals(GeographiesResourceConstants.RESOURCE_INFO, result.getData().getResourceInfo());
	}

	@Test
	public void testGetRegionNotFoundTriggersBrokenChainException() {
		RegionIdentifier regionIdentifier = mock(RegionIdentifier.class);
		when(mockLookupStrategy.getRegion(regionIdentifier))
			.thenReturn(ExecutionResultFactory.<RegionEntity>createNotFound(NOT_FOUND_MESSAGE));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		geographiesLookup.getRegion(regionIdentifier);
	}

	@Test
	public void testSuccessGetAllCountries() {
		CountriesIdentifier countriesIdentifier = mock(CountriesIdentifier.class);
		CountryIdentifier countryIdentifier = mock(CountryIdentifier.class);
		Iterable<CountryIdentifier> countries = Collections.singleton(countryIdentifier);
		when(mockLookupStrategy.getAllCountries(countriesIdentifier))
				.thenReturn(countries);

		ExecutionResult<ResourceState<LinksEntity>> result = geographiesLookup.getAllCountries(countriesIdentifier);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK);

		List<ResourceLink> actualLinks = result.getData().getLinks();
		assertThat(actualLinks, hasSize(1));
		assertResourceLink(actualLinks.get(0))
				.type(GeographiesMediaTypes.COUNTRY.id())
				.uri(COUNTRY_URI);
		assertEquals(GeographiesResourceConstants.RESOURCE_INFO, result.getData().getResourceInfo());
	}

	@Test
	public void testSuccessGetAllRegions() {
		RegionsIdentifier regionsIdentifier = mock(RegionsIdentifier.class);
		RegionIdentifier regionIdentifier = mock(RegionIdentifier.class);
		Iterable<RegionIdentifier> regions = Collections.singleton(regionIdentifier);
		when(mockLookupStrategy.getAllRegions(regionsIdentifier))
				.thenReturn(regions);

		ExecutionResult<ResourceState<LinksEntity>> result = geographiesLookup.getAllRegions(regionsIdentifier);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK);

		List<ResourceLink> actualLinks = result.getData().getLinks();
		assertThat(actualLinks, hasSize(1));
		assertResourceLink(actualLinks.get(0))
				.type(GeographiesMediaTypes.REGION.id())
				.uri(REGION_URI);
		assertEquals(GeographiesResourceConstants.RESOURCE_INFO, result.getData().getResourceInfo());
	}

	private void prepareCountryTransformers() {
		when(mockTransformerProvider.forClass(CountriesIdentifier.class))
			.thenReturn(mockCountriesTransformer);
		when(mockTransformerProvider.forClass(CountryIdentifier.class))
			.thenReturn(mockCountryTransformer);
		when(mockCountryTransformer.identifierToUri(any(CountryIdentifier.class)))
			.thenReturn(COUNTRY_URI);
	}

	private void prepareRegionTransformers() {
		when(mockTransformerProvider.forClass(RegionsIdentifier.class))
			.thenReturn(mockRegionsTransformer);
		when(mockTransformerProvider.forClass(RegionIdentifier.class))
			.thenReturn(mockRegionTransformer);
		when(mockRegionTransformer.identifierToUri(any(RegionIdentifier.class)))
			.thenReturn(REGION_URI);
	}
}
