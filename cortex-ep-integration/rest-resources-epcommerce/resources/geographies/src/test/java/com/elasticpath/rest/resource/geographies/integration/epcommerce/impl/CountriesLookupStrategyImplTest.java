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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.ArrayUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;

/**
 * Tests for {@link GeographiesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CountriesLookupStrategyImplTest {

	private static final String SCOPE = "scope";
	private static final String IRELAND_CODE = "IRL";
	private static final String IRELAND = "Ireland";
	private static final String CANADA_CODE = "CA";
	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful.";

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
	public void testRetrievalOfAllCountriesWhenSuccessful() {

		CountriesIdentifier countriesIdentifier = mock(CountriesIdentifier.class);
		CountryIdentifier[] countries = arrangeToReturnTwoCountryCodes(countriesIdentifier);

		Iterable<CountryIdentifier> result = geographiesLookupStrategy.getAllCountries(countriesIdentifier);

		assertThat("The country identifiers returned should match expected", result, containsInAnyOrder(countries));
	}

	@Test
	public void testRetrievalOfAllCountriesWhenNoCountriesAreReturned() {

		arrangeToReturnEmptySetForCountryCodes();
		CountriesIdentifier countriesIdentifier = mock(CountriesIdentifier.class);

		Iterable<CountryIdentifier> result = geographiesLookupStrategy.getAllCountries(countriesIdentifier);

		assertThat("no country codes should be returned", result, emptyIterable());
	}

	@Test
	public void testRetrievalOfCountryByIdWhenSuccesful() {

		CountryIdentifier countryIdentifier = arrangeToReturnIrelandForCountryCode();

		ExecutionResult<CountryEntity> result = geographiesLookupStrategy.getCountry(countryIdentifier);
		CountryEntity countryEntity = result.getData();

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Display name should be set to Ireland", countryEntity.getDisplayName(), IRELAND);
		assertEquals("Name should be set to IRL", countryEntity.getName(), IRELAND_CODE);
	}

	@Test
	public void testRetrievalOfCountryByIdWhenCountryNotFound() {

		CountryIdentifier countryIdentifier = arrangeToReturnNullForCountryCode();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		geographiesLookupStrategy.getCountry(countryIdentifier);
	}

	private CountryIdentifier[] arrangeToReturnTwoCountryCodes(final CountriesIdentifier countriesIdentifier) {

		String[] countryCodes = { IRELAND_CODE, CANADA_CODE };
		when(geography.getCountryCodes())
				.thenReturn(Sets.newSet(countryCodes));
		Collection<CountryIdentifier> countries = new ArrayList<>();
		for (String countryCode : countryCodes) {
		countries.add(CountryIdentifier.builder()
				.withCountryId(StringIdentifier.of(countryCode))
				.withCountries(countriesIdentifier)
				.build());
		}
		return countries.toArray(new CountryIdentifier[countries.size()]);
	}

	private String[] arrangeToReturnEmptySetForCountryCodes() {

		String[] countryCodes = ArrayUtils.EMPTY_STRING_ARRAY;
		when(geography.getCountryCodes())
				.thenReturn(Sets.newSet(countryCodes));
		return countryCodes;
	}

	private CountryIdentifier arrangeToReturnIrelandForCountryCode() {

		when(geography.getCountryDisplayName(anyString(), any(Locale.class)))
				.thenReturn(IRELAND);
		return getCountryIdentifier();
	}

	private CountryIdentifier arrangeToReturnNullForCountryCode() {

		when(geography.getCountryDisplayName(anyString(), any(Locale.class))).thenReturn(null);
		return getCountryIdentifier();
	}

	private static CountryIdentifier getCountryIdentifier() {
		CountriesIdentifier countries = CountriesIdentifier.builder()
				.withScope(StringIdentifier.of(SCOPE))
				.build();
		return CountryIdentifier.builder()
				.withCountryId(StringIdentifier.of(IRELAND_CODE))
				.withCountries(countries)
				.build();
	}
}
