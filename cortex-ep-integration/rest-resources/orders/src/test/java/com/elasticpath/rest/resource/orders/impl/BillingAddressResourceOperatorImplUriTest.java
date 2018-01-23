/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.impl.BillingInfoResourceOperatorImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests correct dispatch of order uris to orders resource.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BillingInfoResourceOperatorImpl.class})
public final class BillingAddressResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String ORDER_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String SCOPE = "scope";
	private static final String RESOURCE_NAME = "orders";
	private static final String PROFILE_ID = "profileId=";
	private static final String ADDRESS_URI = URIUtil.formatRelative("mockAddressUri/mockAddressId=");
	private static final String BILLING_ADDRESS_LIST_URI = URIUtil.format("/profiles/", SCOPE, PROFILE_ID, "/addresses/billing");

	@Mock
	private BillingInfoResourceOperatorImpl billingInfoResourceOperator;

	@Test
	public void testUriDispatchToBillingInfoProcessReadAddressMethod() {

		String uri = URIUtil.format(RESOURCE_NAME, SCOPE, ORDER_ID, BillingAddressInfo.URI_PART, Selector.PATH_PART, ADDRESS_URI);
		ResourceOperation readAddressOperation = createRead(uri);
		mediaType(AddressesMediaTypes.ADDRESS);
		readOther(readAddressOperation);
		when(billingInfoResourceOperator.processReadAddress(anyString(), anyAddressEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(readAddressOperation, billingInfoResourceOperator);

		verify(billingInfoResourceOperator).processReadAddress(anyString(), anyAddressEntity(), anyString(), anyResourceOperation());
	}

	@Test
	public void testUriDispatchToBillingInfoSelector() {

		String uri = URIUtil.format(RESOURCE_NAME, SCOPE, ORDER_ID, BillingAddressInfo.URI_PART, Selector.PATH_PART, BILLING_ADDRESS_LIST_URI);
		ResourceOperation readAddressOperation = createRead(uri);
		when(billingInfoResourceOperator.processReadBillingAddressSelector(anyString(), anyString(), anyLinksEntity(), anyResourceOperation()))
				.thenReturn(operationResult);
		mediaType(CollectionsMediaTypes.LINKS);
		readOther(readAddressOperation);
		dispatchMethod(readAddressOperation, billingInfoResourceOperator);

		verify(billingInfoResourceOperator).processReadBillingAddressSelector(anyString(), anyString(), anyLinksEntity(), anyResourceOperation());
	}

	@Test
	public void testUriDispatchToBillingInfo() {

		String uri = URIUtil.format(RESOURCE_NAME, SCOPE, ORDER_ID, BillingAddressInfo.URI_PART);
		ResourceOperation readAddressOperation = createRead(uri);
		when(billingInfoResourceOperator.processReadBillingAddressInfo(anyString(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(readAddressOperation, billingInfoResourceOperator);

		verify(billingInfoResourceOperator).processReadBillingAddressInfo(anyString(), anyString(), anyResourceOperation());
	}

	private static ResourceState<AddressEntity> anyAddressEntity() {
		return any();
	}

	private static ResourceState<LinksEntity> anyLinksEntity() {
		return any();
	}
}
