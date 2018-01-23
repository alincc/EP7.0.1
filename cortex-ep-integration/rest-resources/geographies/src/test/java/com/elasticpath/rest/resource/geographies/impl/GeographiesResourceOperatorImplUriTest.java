/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests URI-related annotations on {@link GeographiesResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ GeographiesResourceOperatorImpl.class })
public class GeographiesResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String RESOURCE_NAME = "geographies";
	private static final String SCOPE = "scope";
	private static final String COUNTRIES_URI = URIUtil.format(RESOURCE_NAME, SCOPE, "countries");
	private static final String COUNTRY_ID = "countryid=";
	private static final String COUNTRY_URI = URIUtil.format(COUNTRIES_URI, COUNTRY_ID);
	private static final String REGIONS_URI = URIUtil.format(COUNTRY_URI, "regions");
	private static final String REGION_ID = "regionid=";
	private static final String REGION_URI = URIUtil.format(REGIONS_URI, REGION_ID);

	@Spy
	private final GeographiesResourceOperatorImpl resourceOperator = new GeographiesResourceOperatorImpl(null, null);
	@Mock
	private OperationResult mockOperationResult;

	@Test
	public void testReadAllCountries() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(COUNTRIES_URI);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadCountries(operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCountries(operation);
	}

	@Test
	public void testReadSpecificCountry() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(COUNTRY_URI);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadCountry(operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadCountry(operation);
	}

	@Test
	public void testReadAllRegions() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(REGIONS_URI);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadRegions(operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadRegions(operation);
	}

	@Test
	public void testReadSpecificRegion() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(REGION_URI);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadRegion(operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadRegion(operation);
	}
}
