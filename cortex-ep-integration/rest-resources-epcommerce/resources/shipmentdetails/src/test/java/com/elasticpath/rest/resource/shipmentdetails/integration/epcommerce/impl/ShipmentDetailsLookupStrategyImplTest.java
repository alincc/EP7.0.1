/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.StringUtils;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;
import com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.ShipmentDetailsIntegrationProperties;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Tests for {@link ShipmentDetailsLookupStrategyImpl}.
 */
public class ShipmentDetailsLookupStrategyImplTest {

	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful.";
	private static final String CART_GUID = "cart_guid";
	private static final String STORE_CODE = "storeCode";
	private static final String CART_ORDER_GUID = "cartOrderGuid";
	private static final String DELIVERY_ID = "deliveryId";
	private static final String CUST_GUID = "cust guid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final Set<ShipmentType> shipmentTypes = new HashSet<>();
	private final ShoppingCartRepository mockShoppingCartRepository = context.mock(ShoppingCartRepository.class);
	private final CartOrderRepository mockCartOrderRepository = context.mock(CartOrderRepository.class);
	private final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class);
	private final CartOrder cartOrder = new CartOrderImpl();
	private final ShipmentDetailsLookupStrategyImpl strategy = new ShipmentDetailsLookupStrategyImpl(
			mockShoppingCartRepository, mockCartOrderRepository);

	/**
	 * Test method for {@link com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy
	 * #getShipmentDetailsIdForOrderAndDelivery(String, String)}.
	 */
	@Test
	public void testGetShipmentDetailsIdForOrderAndDelivery() {
		ExecutionResult<String> shipmentDetailsIdResult =
				strategy.getShipmentDetailsIdForOrderAndDelivery(CART_ORDER_GUID, DELIVERY_ID);
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, shipmentDetailsIdResult.isSuccessful());
		assertEquals("Shipment details id does not match expected value.", expectedShipmentDetailsId, shipmentDetailsIdResult.getData());
	}

	/**
	 * Test method for {@link ShipmentDetailsLookupStrategyImpl#getShipmentDetailsIds(String, String)}.
	 */
	@Test
	public void testGetShipmentDetailsIdsForScopeAndProfileId() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID,
				ShipmentDetailsIntegrationProperties.PHYSICAL_SHIPMENT_IDENTIFIER);
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindCartOrderGuidsByCustomer(ExecutionResultFactory.createReadOK(Arrays.asList(CART_ORDER_GUID)));
		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		mockGetShoppingCart(ExecutionResultFactory.createReadOK(mockShoppingCart));
		shipmentTypes.add(ShipmentType.PHYSICAL);
		mockGetShipmentTypes(shipmentTypes);

		ExecutionResult<Collection<String>> shipmentDetailsIdsResult = strategy.getShipmentDetailsIds(STORE_CODE, CUST_GUID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, shipmentDetailsIdsResult.isSuccessful());
		assertTrue("Result shipment details ID does not match expected id.",
				CollectionUtil.containsOnly(Collections.singleton(expectedShipmentDetailsId), shipmentDetailsIdsResult.getData()));
	}

	/**
	 * Ensure get shipment ids for scope and customer with no associated shipment id is unsuccessful.
	 */
	@Test
	public void ensureGetShipmentDetailsIdsForScopeAndProfileIdWithNoAssociatedShipmentDetailsIdIsUnsuccessful() {
		mockFindCartOrderGuidsByCustomer(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetailsIds(STORE_CODE, CUST_GUID);
	}

	/**
	 * Test get shipment detail.
	 */
	@Test
	public void testGetShipmentDetail() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		shipmentTypes.add(ShipmentType.PHYSICAL);
		mockGetShipmentTypes(shipmentTypes);
		mockGetShoppingCart(ExecutionResultFactory.createReadOK(mockShoppingCart));


		ExecutionResult<ShipmentDetailsDto> shipmentDetailResult = strategy.getShipmentDetail(STORE_CODE, expectedShipmentDetailsId);
		ShipmentDetailsDto expectedDto = ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class);
		expectedDto.setOrderCorrelationId(CART_ORDER_GUID).setDeliveryCorrelationId(DELIVERY_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, shipmentDetailResult.isSuccessful());
		assertEquals("Shipment detail dto does not match expected dto.", expectedDto, shipmentDetailResult.getData());
	}

	/**
	 * Test get shipment detail when no physical shipment found.
	 */
	@Test
	public void testGetShipmentDetailWhenNoPhysicalShipmentFound() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		mockGetShipmentTypes(shipmentTypes);
		mockGetShoppingCart(ExecutionResultFactory.createReadOK(mockShoppingCart));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetail(STORE_CODE, expectedShipmentDetailsId);
	}

	/**
	 * Test get shipment detail when cart order not found.
	 */
	@Test
	public void testGetShipmentDetailWhenCartOrderNotFound() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);
		mockFindShoppingCartGuidByCartOrderGuid(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetail(STORE_CODE, expectedShipmentDetailsId);
	}

	/**
	 * Test get shipment detail for cart with bundle item.
	 */
	@Test
	public void testGetShipmentDetailForCartWithBundleItem() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);
		cartOrder.setShoppingCartGuid(CART_GUID);
		shipmentTypes.add(ShipmentType.PHYSICAL);

		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		mockGetShoppingCart(ExecutionResultFactory.createReadOK(mockShoppingCart));
		mockGetShipmentTypes(shipmentTypes);

		ExecutionResult<ShipmentDetailsDto> shipmentDetailResult = strategy.getShipmentDetail(STORE_CODE, expectedShipmentDetailsId);
		ShipmentDetailsDto expectedDto = ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class);
		expectedDto.setOrderCorrelationId(CART_ORDER_GUID).setDeliveryCorrelationId(DELIVERY_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, shipmentDetailResult.isSuccessful());
		assertEquals("Shipment Detail dto does not match expected dto.", expectedDto, shipmentDetailResult.getData());
	}

	/**
	 * Test get shipment detail for cart with bundle item but with no physical shipments.
	 */
	@Test
	public void testGetShipmentDetailForCartWithBundleItemButWithNoPhysicalShipments() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		mockGetShoppingCart(ExecutionResultFactory.createReadOK(mockShoppingCart));
		mockGetShipmentTypes(shipmentTypes);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetail(STORE_CODE, expectedShipmentDetailsId);
	}

	/**
	 * Test get shipment detail for cart with bundles but error finding cart.
	 */
	@Test
	public void testGetShipmentDetailForCartWithBundlesButErrorFindingCart() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		mockGetShoppingCart(ExecutionResultFactory.createNotFound(StringUtils.EMPTY));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetail(STORE_CODE, expectedShipmentDetailsId);
	}

	/**
	 * Test get shipment detail when error decoding shipment id.
	 */
	@Test
	public void testGetShipmentDetailWhenErrorDecodingShipmentDetailsId() {
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		strategy.getShipmentDetail(STORE_CODE, null);
	}

	@Test
	public void ensureShipmentDetailsIdsForOrderCanBeRetrievedSuccessfully() {
		String expectedShipmentDetailsId = getExpectedShipmentDetailsId(CART_ORDER_GUID,
																		ShipmentDetailsIntegrationProperties.PHYSICAL_SHIPMENT_IDENTIFIER);
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(CART_GUID);
		mockGetShoppingCart(ExecutionResultFactory.createReadOK(mockShoppingCart));
		shipmentTypes.add(ShipmentType.PHYSICAL);
		mockGetShipmentTypes(shipmentTypes);

		ExecutionResult<Collection<String>> result = strategy.getShipmentDetailsIdsForOrder(STORE_CODE, CART_ORDER_GUID);

		assertThat(result.getData(), hasItem(expectedShipmentDetailsId));
	}

	@Test
	public void ensureNotFoundReturnedWhenOrderForShipmentDetailIdsNotFound() {
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetailsIdsForOrder(STORE_CODE, CART_ORDER_GUID);
	}

	@Test
	public void ensureNotFoundReturnedWhenShoppingCartForShipmentDetailsNotFound() {
		cartOrder.setShoppingCartGuid(CART_GUID);

		mockFindShoppingCartGuidByCartOrderGuid(null);
		mockGetShoppingCart(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getShipmentDetailsIdsForOrder(STORE_CODE, CART_ORDER_GUID);
	}

	private <T> void mockFindShoppingCartGuidByCartOrderGuid(final T result) {
		context.checking(new Expectations() {
			{
				allowing(mockCartOrderRepository).getShoppingCartGuid(STORE_CODE, CART_ORDER_GUID);
				will(returnValue(result));
			}
		});
	}



	private void mockGetShipmentTypes(final Set<ShipmentType> shipmentType) {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCart).getShipmentTypes();
				will(returnValue(shipmentType));
			}
		});
	}

	private <T> void mockGetShoppingCart(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).getShoppingCart(CART_GUID);
				will(returnValue(executionResult));
			}
		});
	}

	private <T> void mockFindCartOrderGuidsByCustomer(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockCartOrderRepository).findCartOrderGuidsByCustomer(STORE_CODE, CUST_GUID);
				will(returnValue(executionResult));
			}
		});
	}

	private String getExpectedShipmentDetailsId(final String cartOrderGuid, final String deliveryId) {
		Map<String, String> shipmentFieldValues = new TreeMap<>();
		shipmentFieldValues.put(ShipmentDetailsIntegrationProperties.ORDER_ID_KEY, cartOrderGuid);
		shipmentFieldValues.put(ShipmentDetailsIntegrationProperties.DELIVERY_ID_KEY, deliveryId);

		return CompositeIdUtil.encodeCompositeId(shipmentFieldValues);
	}
}
