/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.addresses.billing.Billing;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link BillingAddressListUriBuilderImpl}.
 */
public final class BillingAddressListUriBuilderImplTest {

	private static final String MOCK_SCOPE = "mockScope";
	private static final String MOCK_ROOT_NAME = "mockRootName";


	/**
	 * Construct a valid uri.
	 */
	@Test
	public void constructValidBillingAddressUri() {

		BillingAddressListUriBuilder billingAddressUriBuilder = new BillingAddressListUriBuilderImpl(MOCK_ROOT_NAME);
		String billingAddressUri = billingAddressUriBuilder
				.setScope(MOCK_SCOPE)
				.build();

		String expectedBillingAddressUri = URIUtil.format(MOCK_ROOT_NAME, MOCK_SCOPE, Billing.URI_PART);

		assertEquals(expectedBillingAddressUri, billingAddressUri);
	}
}
