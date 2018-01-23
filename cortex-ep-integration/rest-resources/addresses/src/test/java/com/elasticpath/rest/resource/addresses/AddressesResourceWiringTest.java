/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses;

import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressWriterStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests addresses resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases"})
public class AddressesResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "defaultShippingAddressLookupStrategy")
	private DefaultAddressLookupStrategy defaultShippingAddressLookupStrategy;

	@ReplaceWithMock(beanName = "defaultBillingAddressLookupStrategy")
	private DefaultAddressLookupStrategy defaultBillingAddressLookupStrategy;

	@ReplaceWithMock(beanName = "addressLookupStrategy")
	private AddressLookupStrategy addressLookupStrategy;

	@ReplaceWithMock(beanName = "addressWriterStrategy")
	private AddressWriterStrategy addressWriterStrategy;

}
