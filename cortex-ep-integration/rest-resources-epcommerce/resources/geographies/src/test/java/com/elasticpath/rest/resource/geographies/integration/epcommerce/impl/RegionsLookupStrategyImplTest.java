/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;

/**
 * Tests for {@link GeographiesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionsLookupStrategyImplTest {

	private static final String GALWAY_CODE = "GA";
	private static final String KERRY_CODE = "KE";
	private static final String IRELAND = "Ireland";
	private static final String GALWAY = "Galway";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	Geography geography;
	@Mock
	ResourceOperationContext resourceOperationContext;

	@InjectMocks
	GeographiesLookupStrategyImpl geographiesLookupStrategy;

	@Before
	public void setUp() {

		Subject mockSubject = mock(Subject.class);
		Collection<Principal> principals = Collections.emptySet();
		when(mockSubject.getPrincipals()).thenReturn(principals);
		when(resourceOperationContext.getSubject()).thenReturn(mockSubject);
	}

	@Test
	public void givenCountryWithRegionsWhenRetrievingAllRegionsShouldSucceed() {

		RegionsIdentifier regionsIdentifier = getRegionsIdentifier();

		RegionIdentifier[] regionIdentifiers = arrangeToReturnTwoRegions(regionsIdentifier);

		Iterable<RegionIdentifier> result = geographiesLookupStrategy.getAllRegions(regionsIdentifier);

		assertThat(result, containsInAnyOrder(regionIdentifiers));
	}

	@Test
	public void givenCountryWithRegionsWhenRetrievingAllRegionsShouldReturnEmptyDataSet() {

		when(geography.getSubCountryCodes(IRELAND))
				.thenReturn(Collections.emptySet());
		RegionsIdentifier regionsIdentifier = getRegionsIdentifier();

		Iterable<RegionIdentifier> result = geographiesLookupStrategy.getAllRegions(regionsIdentifier);

		assertThat(result, emptyIterable());
	}

	@Test
	public void testSuccessfulRetrievalOfRegionById() {

		RegionIdentifier regionIdentifier = arrangeToReturnIrelandForRegionCode();

		ExecutionResult<RegionEntity> result = geographiesLookupStrategy.getRegion(regionIdentifier);

		RegionEntity region = result.getData();
		assertTrue(result.isSuccessful());
		assertEquals(GALWAY, region.getDisplayName());
		assertEquals(GALWAY_CODE, region.getName());
	}

	@Test
	public void testWhenRetrievalOfRegionByIDFails() {

		RegionIdentifier regionIdentifier = arrangeToReturnNullForRegionCode();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		geographiesLookupStrategy.getRegion(regionIdentifier);
	}

	private RegionIdentifier[] arrangeToReturnTwoRegions(final RegionsIdentifier regionsIdentifier) {
		Set<String> regionCodes = ImmutableSet.of(GALWAY_CODE, KERRY_CODE);
		when(geography.getSubCountryCodes(IRELAND))
				.thenReturn(regionCodes);

		return Iterables.toArray(Iterables.transform(regionCodes, regionCode ->
			RegionIdentifier.builder()
				.withRegionId(StringIdentifier.of(regionCode))
				.withRegions(regionsIdentifier)
				.build()),
			RegionIdentifier.class);
	}


	RegionIdentifier arrangeToReturnIrelandForRegionCode() {

		when(geography.getSubCountryDisplayName(anyString(), anyString(), any(Locale.class)))
				.thenReturn(GALWAY);

		arrangeCommonSetOfMocksForTestsToFunction();

		return getRegionIdentifier();
	}

	RegionIdentifier arrangeToReturnNullForRegionCode() {

		when(geography.getSubCountryDisplayName(anyString(), anyString(), any(Locale.class)))
			.thenReturn(null);

		arrangeCommonSetOfMocksForTestsToFunction();

		return getRegionIdentifier();
	}

	private static RegionsIdentifier getRegionsIdentifier() {
		CountryIdentifier country = CountryIdentifier.builder()
				.withCountryId(StringIdentifier.of(IRELAND))
				.withCountries(mock(CountriesIdentifier.class))
				.build();
		return RegionsIdentifier.builder()
				.withCountry(country)
				.build();
	}

	private static RegionIdentifier getRegionIdentifier() {
		return RegionIdentifier.builder()
				.withRegionId(StringIdentifier.of(GALWAY_CODE))
				.withRegions(getRegionsIdentifier())
				.build();
	}

	void arrangeCommonSetOfMocksForTestsToFunction() {

		Subject mockSubject = mock(Subject.class);

		when(mockSubject.getPrincipals())
				.thenReturn(Collections.emptyList());

		when(resourceOperationContext.getSubject())
				.thenReturn(mockSubject);
	}
}
