/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertSelf.assertSelf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionWriter;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for SelectShippingOptionCommandImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public final class SelectShippingOptionResourceOperatorTest {

	private static final String SHIPPING_OPTION_ID = "shipping_option_id";
	private static final String SHIPPING_OPTION_URI = "shipping_option_uri";
	private static final String SHIPMENT_DETAILS_ID = "shipment_details_id";
	private static final String SCOPE = "scope";
	private static final String RESOURCE = "shipmentdetails";
	private static final String SHIPMENT_DETAILS_URI = URIUtil.format(RESOURCE, SCOPE, SHIPMENT_DETAILS_ID);
	private static final ResourceState<ShippingOptionEntity> SHIPPING_OPTION = ResourceState.Builder
			.create(ShippingOptionEntity.builder()
					.withShippingOptionId(SHIPPING_OPTION_ID)
					.build())
			.withSelf(SelfFactory.createSelf(SHIPPING_OPTION_URI))
			.build();
	private static final ResourceOperation CREATE_OP = TestResourceOperationFactory.createCreate("/uri", SHIPPING_OPTION);
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShippingOptionWriter mockShippingOptionWriter;
	private SelectShippingOptionResourceOperator classUnderTest;

	@Before
	public void setUp() {
		classUnderTest = new SelectShippingOptionResourceOperator(mockShippingOptionWriter, RESOURCE);
	}


	/**
	 * Test select shipping option with no previous selection.
	 */
	@Test
	public void testSelectShippingOptionWithNoPreviousSelection() {
		when(mockShippingOptionWriter.selectShippingOptionForShipment(SCOPE, SHIPMENT_DETAILS_ID, SHIPPING_OPTION_ID))
				.thenReturn(ExecutionResultFactory.createCreateOKWithData(false, false));

		OperationResult result = classUnderTest.processSelectShippingOptionInfoChoice(SCOPE, SHIPMENT_DETAILS_ID,
				SHIPPING_OPTION, CREATE_OP);

		assertTrue("Execution result should be successful.", result.isSuccessful());
		assertEquals(ResourceStatus.CREATE_OK, result.getResourceStatus());
		assertRedirectUri(result.getResourceState());
	}

	/**
	 * Test select shipping option when error on update.
	 */
	@Test
	public void testSelectShippingOptionWhenErrorOnUpdate() {
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));
		when(mockShippingOptionWriter.selectShippingOptionForShipment(SCOPE, SHIPMENT_DETAILS_ID, SHIPPING_OPTION_ID))
				.thenReturn(ExecutionResultFactory.<Boolean>createServerError(StringUtils.EMPTY));

		classUnderTest.processSelectShippingOptionInfoChoice(SCOPE, SHIPMENT_DETAILS_ID,
				SHIPPING_OPTION, CREATE_OP);
	}


	private void assertRedirectUri(final ResourceState<?> representation) {
		String expectedRedirectUri = URIUtil.format(SHIPMENT_DETAILS_URI, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		assertSelf(representation.getSelf())
				.uri(expectedRedirectUri);
	}
}
