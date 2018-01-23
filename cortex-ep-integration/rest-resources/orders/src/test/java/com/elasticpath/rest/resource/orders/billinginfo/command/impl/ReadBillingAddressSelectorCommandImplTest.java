/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingAddressSelectorCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ReadBillingAddressSelectorCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadBillingAddressSelectorCommandImplTest {

	private static final String ADDRESS_ID = "ADDRESS_ID";
	private static final String BILLING_ADDRESS_URI = "/mock/billing/address/uri";
	private static final String SELECTED_BILLING_ADDRESS_URI = "/mock/billing/address/1/uri";
	private static final String ORDER_URI = "/mock/order/uri";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String SCOPE = "SCOPE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory;

	@Mock
	private AddressUriBuilderFactory addressUriBuilderFactory;

	@Mock
	private SelectorResourceStateBuilder selectorRepresentationBuilder;
	@Mock
	private BillingInfoLookup billingInfoLookup;

	@Mock
	private OrderLookup orderLookup;


	/**
	 * Sets up common elements of the test.
	 */
	@Before
	public void setUp() {
		OrderEntity orderEntity = OrderEntity.builder().withOrderId(ORDER_ID).build();
		ResourceState<OrderEntity> orderResourceState = ResourceState.Builder.create(orderEntity)
				.withSelf(createSelf(ORDER_URI, OrdersMediaTypes.ORDER
						.id())).withScope(SCOPE).build();
		ExecutionResult<ResourceState<OrderEntity>> executionResult = ExecutionResultFactory.createCreateOKWithData(orderResourceState, true);

		when(orderLookup.findOrderByOrderId(SCOPE, ORDER_ID)).thenReturn(executionResult);
	}

	/**
	 * Test that a failure result is returned upon {@link BillingInfoLookup#findAddressForOrder(String, String)} failure.
	 */
	@Test
	public void testReadSelectedBillingAddressServerFailure() {
		createUriBuilderFactoryExpectations(BILLING_ADDRESS_URI, "");

		String errorMessage = "Failure getting selected address";
		shouldFindAddressForOrderWithResult(ExecutionResultFactory.<String>createServerError(errorMessage));

		ReadBillingAddressSelectorCommandImpl readBillingInfoSelectorCommand = createReadBillingAddressSelectorCommand();

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		readBillingInfoSelectorCommand.execute();
	}

	/**
	 * Test that a successful result with no selected choice is returned when {@link BillingInfoLookup#findAddressForOrder(String, String)}
	 * returns not found.
	 */
	@Test
	public void testReadSelectedBillingAddressNotFound() {
		createUriBuilderFactoryExpectations(BILLING_ADDRESS_URI, "");

		// Create the SelectorRepresentation with no selectedAddressUri, since none was found
		ResourceState<SelectorEntity> testSelectorRepresentation = createSelectorRepresentation();

		shouldFindAddressForOrderWithResult(ExecutionResultFactory.<String>createNotFound(""));

		ReadBillingAddressSelectorCommandImpl readBillingInfoSelectorCommand = createReadBillingAddressSelectorCommand();
		ExecutionResult<ResourceState<SelectorEntity>> commandResult = readBillingInfoSelectorCommand.execute();

		assertEquals(ResourceStatus.READ_OK, commandResult.getResourceStatus());
		assertEquals(testSelectorRepresentation, commandResult.getData());
	}

	/**
	 * Test that a successful result with no selected choice is returned when {@link BillingInfoLookup#findAddressForOrder(String, String)}
	 * is successful with an invalid billing address.
	 */
	@Test
	public void testReadSelectedBillingAddressIsNotValidBillingAddress() {
		String invalidSelectedAddressUri = "/invalid/billing/address/uri";

		createUriBuilderFactoryExpectations(BILLING_ADDRESS_URI, invalidSelectedAddressUri);

		// Create the SelectorRepresentation with no selectedAddressUri, since it didn't match a billingAddressLink
		ResourceState<SelectorEntity> expectedSelectorRepresentation = createSelectorRepresentation();

		shouldFindAddressForOrderWithResult(ExecutionResultFactory.createReadOK(ADDRESS_ID));

		ReadBillingAddressSelectorCommandImpl readBillingInfoSelectorCommand = createReadBillingAddressSelectorCommand();
		ExecutionResult<ResourceState<SelectorEntity>> commandResult = readBillingInfoSelectorCommand.execute();

		assertEquals(ResourceStatus.READ_OK, commandResult.getResourceStatus());
		assertEquals(expectedSelectorRepresentation, commandResult.getData());
	}

	/**
	 * Test that a successful result with no selected choice is returned when {@link BillingInfoLookup#findAddressForOrder(String, String)}
	 * is successful with an invalid billing address.
	 */
	@Test
	public void testReadSelectedBillingAddressIsValid() {
		createBillingAddressLinks();
		String selectedAddressUri = SELECTED_BILLING_ADDRESS_URI;
		createUriBuilderFactoryExpectations(BILLING_ADDRESS_URI, selectedAddressUri);

		ResourceState<SelectorEntity> expectedSelectorRepresentation = createSelectorRepresentation();

		shouldFindAddressForOrderWithResult(ExecutionResultFactory.createReadOK(ADDRESS_ID));

		ReadBillingAddressSelectorCommandImpl readBillingInfoSelectorCommand = createReadBillingAddressSelectorCommand();
		ExecutionResult<ResourceState<SelectorEntity>> commandResult = readBillingInfoSelectorCommand.execute();

		assertEquals(ResourceStatus.READ_OK, commandResult.getResourceStatus());
		assertEquals(expectedSelectorRepresentation, commandResult.getData());
	}

	private void shouldFindAddressForOrderWithResult(final ExecutionResult<String> result) {
		when(billingInfoLookup.findAddressForOrder(SCOPE, ORDER_ID))
				.thenReturn(result);
	}

	private void createUriBuilderFactoryExpectations(final String billingAddressUri, final String selectedAddressUri) {
		BillingAddressListUriBuilder pbauBuilder = Mockito.mock(BillingAddressListUriBuilder.class);
		AddressUriBuilder pauBuilder = Mockito.mock(AddressUriBuilder.class);

		when(billingAddressUriBuilderFactory.get())
				.thenReturn(pbauBuilder);
		when(pbauBuilder.setScope(any(String.class)))
				.thenReturn(pbauBuilder);
		when(pbauBuilder.build())
				.thenReturn(billingAddressUri);

		when(addressUriBuilderFactory.get())
				.thenReturn(pauBuilder);
		when(pauBuilder.setAddressId(any(String.class)))
				.thenReturn(pauBuilder);
		when(pauBuilder.setScope(any(String.class)))
				.thenReturn(pauBuilder);
		when(pauBuilder.build())
				.thenReturn(selectedAddressUri);
	}

	private ReadBillingAddressSelectorCommandImpl createReadBillingAddressSelectorCommand() {

		ReadBillingAddressSelectorCommandImpl cmd = new ReadBillingAddressSelectorCommandImpl(
				addressUriBuilderFactory,
				selectorRepresentationBuilder,
			billingAddressUriBuilderFactory,
				billingInfoLookup,
				orderLookup);

		ReadBillingAddressSelectorCommand.Builder commandBuilder = new ReadBillingAddressSelectorCommandImpl.BuilderImpl(cmd);
		commandBuilder.setBillingAddressLinks(createBillingAddressLinks())
				.setScope(SCOPE)
				.setOrderId(ORDER_ID)
				.build();

		return cmd;
	}

	private ResourceState<SelectorEntity> createSelectorRepresentation() {
		String selfUri = URIUtil.format(ORDER_URI, BillingAddressInfo.URI_PART, Selector.URI_PART);

		SelectorEntity selectorEntity = SelectorEntity.builder().build();
		ResourceState<SelectorEntity> testSelectorRepresentation = ResourceState.Builder.create(selectorEntity)
				.withSelf(createSelf(selfUri, ControlsMediaTypes.SELECTOR
						.id())).build();

		when(selectorRepresentationBuilder.setName(BillingInfoConstants.BILLING_ADDRESS_SELECTOR_NAME))
				.thenReturn(selectorRepresentationBuilder);

		when(selectorRepresentationBuilder.setSelfUri(selfUri))
				.thenReturn(selectorRepresentationBuilder);

		when(selectorRepresentationBuilder.build())
				.thenReturn(testSelectorRepresentation);

		return testSelectorRepresentation;
	}

	private ResourceState<LinksEntity> createBillingAddressLinks() {
		LinksEntity linksEntity = LinksEntity.builder()
				.build();
		return ResourceState.Builder
				.create(linksEntity)
				.withSelf(createSelf("", CollectionsMediaTypes.LINKS
						.id()))
				.withScope(SCOPE)
				.build();
	}
}
