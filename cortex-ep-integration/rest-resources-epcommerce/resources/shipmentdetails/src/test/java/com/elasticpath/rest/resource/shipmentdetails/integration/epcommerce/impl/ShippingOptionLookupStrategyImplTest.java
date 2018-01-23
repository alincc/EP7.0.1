/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingServiceLevelRepository;
import com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.ShipmentDetailsIntegrationProperties;
import com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.transform.ShippingServiceLevelTransformer;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Tests for {@link ShippingOptionLookupStrategyImpl}.
 */
@SuppressWarnings({"PMD.TooManyMethods"})
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionLookupStrategyImplTest {

	private static final Locale SUBJECT_LOCALE = Locale.CANADA;
	private static final String SHIPPING_ADDRESS_GUID = "SHIPPING_ADDRESS_GUID";
	private static final String NOT_FOUND_ERROR_MESSAGE = "Not Found Error Message";
	private static final String SHIPPINGOPTION_GUID = "shippingOptionGuid";
	private static final String SHIPPING_SERVICE_LEVEL_GUID = "ShippingServiceLevelGuid";
	private static final String STORE_CODE = "store_code";
	private static final String CART_ORDER_GUID = "cartOrderGuid";
	private static final String SHIPMENT_DETAILS_ID = "shipmentDetailsId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	ResourceOperationContext resourceOperationContext;
	@Mock
	private ShippingServiceLevelTransformer shippingServiceLevelTransformer;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private ShippingServiceLevelRepository shippingServiceLevelRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@InjectMocks
	private ShippingOptionLookupStrategyImpl shippingOptionLookupStrategy;

	/**
	 * Test get shipping option for shipping details.
	 */
	@Test
	public void testGetShippingOptionForShippingDetails() {
		ShippingOptionDto expectedShippingOptionDto = mock(ShippingOptionDto.class);
		CartOrder cartOrder = mock(CartOrder.class);
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShippingPricingSnapshot shippingPricingSnapshot = mock(ShippingPricingSnapshot.class);
		ShippingServiceLevel shippingServiceLevel = createMockShippingServiceLevel();

		shouldFindSubject();
		shouldFindByShipmentDetailsIdWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldFindCartFromCartOrder(ExecutionResultFactory.createReadOK(shoppingCart));
		shouldFindShippingPricingSnapshotFromCart(shoppingCart, shippingPricingSnapshot, shippingServiceLevel);
		shouldFindShippingServiceLevelByGuidWithResult(ExecutionResultFactory.createReadOK(shippingServiceLevel));
		shouldTransformToEntity(shippingServiceLevel, shippingPricingSnapshot, expectedShippingOptionDto);

		ExecutionResult<ShippingOptionDto> result = shippingOptionLookupStrategy.getShippingOptionForShipmentDetails(
				STORE_CODE, SHIPMENT_DETAILS_ID, SHIPPINGOPTION_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(expectedShippingOptionDto);
	}


	/**
	 * Test get shipping option for shipping details with failure getting order.
	 */
	@Test
	public void testGetShippingOptionForShippingDetailsWithFailureGettingOrder() {
		shouldFindShippingServiceLevelByGuidWithResult(
			ExecutionResultFactory.<ShippingServiceLevel>createNotFound(NOT_FOUND_ERROR_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingOptionLookupStrategy.getShippingOptionForShipmentDetails(
				STORE_CODE, SHIPMENT_DETAILS_ID, SHIPPINGOPTION_GUID);
	}


	/**
	 * Test get shipping option IDs for shipment detail.
	 */
	@Test
	public void testGetShippingOptionIdsForShipmentDetail() {
		shouldFindShippingServiceLevelsForShipmentWithResult(
			ExecutionResultFactory.createReadOK(Collections.singletonList(SHIPPING_SERVICE_LEVEL_GUID)));

		ExecutionResult<Collection<String>> result = shippingOptionLookupStrategy.getShippingOptionIdsForShipmentDetails(STORE_CODE,
				SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(Arrays.asList(SHIPPING_SERVICE_LEVEL_GUID));
	}

	/**
	 * Test get shipping option ids for shipment detail with order not found.
	 */
	@Test
	public void testGetShippingOptionIdsForShipmentDetailWhenNotFoundError() {
		shouldFindShippingServiceLevelsForShipmentWithResult(
			ExecutionResultFactory.<Collection<String>>createNotFound(NOT_FOUND_ERROR_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingOptionLookupStrategy.getShippingOptionIdsForShipmentDetails(
			STORE_CODE, SHIPMENT_DETAILS_ID);
	}

	/**
	 * Test get selected shipping option for shipment details.
	 */
	@Test
	public void testGetSelectedShippingOptionForShipmentDetails() {

		when(shippingServiceLevelRepository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(SHIPPING_SERVICE_LEVEL_GUID));

		ExecutionResult<String> result = shippingOptionLookupStrategy.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(SHIPPING_SERVICE_LEVEL_GUID);
	}

	/**
	 * Test get selected shipping option for shipment details with no order found.
	 */
	@Test
	public void testGetSelectedShippingOptionForShipmentDetailsWhenLevelsNotFound() {
		when(shippingServiceLevelRepository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID))
				.thenReturn(ExecutionResultFactory.createNotFound(NOT_FOUND_ERROR_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingOptionLookupStrategy.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);
	}

	/**
	 * Test is shipping destination selected for shipment details.
	 */
	@Test
	public void testIsShippingDestinationSelectedForShipmentDetails() {
		CartOrder cartOrder = createMockCartOrderWithShippingAddress(SHIPPING_ADDRESS_GUID);

		shouldFindByShipmentDetailsIdWithResult(ExecutionResultFactory.createReadOK(cartOrder));

		ExecutionResult<Boolean> result = shippingOptionLookupStrategy.isShippingDestinationSelectedForShipmentDetails(STORE_CODE,
				SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(true);
	}

	/**
	 * Test is shipping destination selected for shipment details for no address.
	 */
	@Test
	public void testIsShippingDestinationSelectedForShipmentDetailsForNoAddress() {
		CartOrder cartOrder = createMockCartOrderWithShippingAddress(StringUtils.EMPTY);

		shouldFindByShipmentDetailsIdWithResult(ExecutionResultFactory.createReadOK(cartOrder));

		ExecutionResult<Boolean> result = shippingOptionLookupStrategy.isShippingDestinationSelectedForShipmentDetails(STORE_CODE,
				SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(false);
	}

	/**
	 * Test is shipping destination selected fails when no cart order found.
	 */
	@Test
	public void testIsShippingDestinationSelectedFailsWhenNoCartOrderFound() {
		shouldFindByShipmentDetailsIdWithResult(ExecutionResultFactory.<CartOrder>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingOptionLookupStrategy.isShippingDestinationSelectedForShipmentDetails(STORE_CODE,
				SHIPMENT_DETAILS_ID);
	}

	/**
	 * Test is supported shipping option type.
	 */
	@Test
	public void testIsSupportedShippingOptionTypeForSupportedType() {
		ExecutionResult<Boolean> result =
				shippingOptionLookupStrategy.isSupportedShippingOptionType(ShipmentDetailsIntegrationProperties.PHYSICAL_SHIPMENT_IDENTIFIER);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(true);

		result = shippingOptionLookupStrategy.isSupportedShippingOptionType("digital");
		
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(false);
	}

	/**
	 * Test is supported shipping option type.
	 */
	@Test
	public void testIsSupportedShippingOptionTypeForUnSupportedType() {
		ExecutionResult<Boolean> result =
				shippingOptionLookupStrategy.isSupportedShippingOptionType("unsupportedType");
		
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(false);
	}
	private void shouldFindSubject() {
		String userId = "userid";
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, userId, SUBJECT_LOCALE);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	private void shouldFindShippingServiceLevelByGuidWithResult(final ExecutionResult<ShippingServiceLevel> result) {
		when(shippingServiceLevelRepository.findByGuid(any(String.class), any(String.class), any(String.class))).thenReturn(result);
	}

	private void shouldFindShippingServiceLevelsForShipmentWithResult(final ExecutionResult<Collection<String>> result) {
		when(shippingServiceLevelRepository.findShippingServiceLevelGuidsForShipment(STORE_CODE, SHIPMENT_DETAILS_ID)).thenReturn(result);
	}

	private void shouldTransformToEntity(final ShippingServiceLevel shippingServiceLevel, final ShippingPricingSnapshot shippingPricingSnapshot,
										final ShippingOptionDto expectedShippingOptionDto) {
		when(shippingServiceLevelTransformer.transformToEntity(shippingServiceLevel, shippingPricingSnapshot, SUBJECT_LOCALE))
				.thenReturn(expectedShippingOptionDto);
	}

	private void shouldFindByShipmentDetailsIdWithResult(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByShipmentDetailsId(STORE_CODE, SHIPMENT_DETAILS_ID)).thenReturn(result);
	}

	private void shouldFindCartFromCartOrder(final ExecutionResult<ShoppingCart> cartResult) {
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, SHIPMENT_DETAILS_ID,
																CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID)).thenReturn(cartResult);
	}

	private void shouldFindShippingPricingSnapshotFromCart(final ShoppingCart shoppingCart, final ShippingPricingSnapshot shippingPricingSnapshot,
															final ShippingServiceLevel shippingServiceLevel) {
		final ShoppingCartPricingSnapshot cartPricingSnapshot = mock(ShoppingCartPricingSnapshot.class);

		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart)).thenReturn(
			ExecutionResultFactory.createReadOK(cartPricingSnapshot));
		when(cartPricingSnapshot.getShippingPricingSnapshot(shippingServiceLevel)).thenReturn(shippingPricingSnapshot);
	}

	private ShippingServiceLevel createMockShippingServiceLevel() {
		ShippingServiceLevel shippingServiceLevel = mock(ShippingServiceLevel.class);
		when(shippingServiceLevel.getGuid()).thenReturn(SHIPPING_SERVICE_LEVEL_GUID);
		return shippingServiceLevel;
	}

	private CartOrder createMockCartOrderWithShippingAddress(final String shippingAddressGuid) {
		CartOrder cartOrder = mock(CartOrder.class);
		when(cartOrder.getGuid()).thenReturn(CART_ORDER_GUID);
		when(cartOrder.getShippingAddressGuid()).thenReturn(shippingAddressGuid);
		return cartOrder;
	}

}
