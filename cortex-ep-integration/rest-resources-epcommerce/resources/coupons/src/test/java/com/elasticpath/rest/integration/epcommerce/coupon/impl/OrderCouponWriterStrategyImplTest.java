/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Coupon writer strategy.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderCouponWriterStrategyImplTest {
	private static final String STORE_CODE = "STORE_CODE";

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";

	private static final String CUSTOMER_EMAIL = "CUSTOMER_EMAIL";

	private static final String COUPON_CODE = "COUPON_CODE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private AbstractDomainTransformer<Coupon, CouponEntity> couponTransformer;

	@InjectMocks
	public OrderCouponWriterStrategyImpl orderCouponWriterStrategy;

	@Mock
	private CartOrder cartOrder;

	@Mock
	private Coupon coupon;

	@Mock
	private CouponEntity couponEntity;

	@Before
	public void setUp() {
		when(couponEntity.getCode()).thenReturn(COUPON_CODE);
		when(coupon.getCouponCode()).thenReturn(COUPON_CODE);
		setUpShoppingCart();
	}

	@Test
	public void testSuccessfulCreationOfNewCoupon() {
		setUpCartOrderRepositoryToReturnCartOrder(cartOrder);
		setupCouponRepositoryToReturnValidityOfCoupon(true);
		setUpCouponRepositoryToReturnCoupon();
		setUpCouponTransformerToTransformCoupon();
		allowingCouponToBeAddedAndSaved(true, true);

		ExecutionResult<CouponEntity> result = orderCouponWriterStrategy.createCouponForOrder(STORE_CODE, CART_ORDER_GUID, couponEntity);
		
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(couponEntity)
			.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void testSuccessfulCreationOfExistingCoupon() {
		setUpCartOrderRepositoryToReturnCartOrder(cartOrder);
		setupCouponRepositoryToReturnValidityOfCoupon(true);
		setUpCouponRepositoryToReturnCoupon();
		setUpCouponTransformerToTransformCoupon();
		allowingCouponToBeAddedAndSaved(true, false);

		ExecutionResult<CouponEntity> result = orderCouponWriterStrategy.createCouponForOrder(STORE_CODE, CART_ORDER_GUID, couponEntity);
		
		AssertExecutionResult.assertExecutionResult(result)
		.isSuccessful()
		.data(couponEntity)
		.resourceStatus(ResourceStatus.READ_OK);
	}
	
	@Test
	public void testServerErrorWhenCreatingCoupon() {
		setUpCartOrderRepositoryToReturnCartOrder(cartOrder);
		setupCouponRepositoryToReturnValidityOfCoupon(true);
		allowingCouponToBeAddedAndSaved(false, true);
		setUpCouponRepositoryToReturnCoupon();

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		orderCouponWriterStrategy.createCouponForOrder(STORE_CODE, CART_ORDER_GUID, couponEntity);
	}

	@Test
	public void testStateFailureWhenCreatingInvalidCoupon() {
		setUpCartOrderRepositoryToReturnCartOrder(cartOrder);
		setupCouponRepositoryToReturnValidityOfCoupon(false);

		ExecutionResult<CouponEntity> result = orderCouponWriterStrategy.createCouponForOrder(STORE_CODE, CART_ORDER_GUID, couponEntity);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.STATE_FAILURE);

	}

	@Test
	public void testCreateCouponNotFoundForCartOrder() {
		setUpCartOrderRepositoryToReturnCartOrder(null);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderCouponWriterStrategy.createCouponForOrder(STORE_CODE, CART_ORDER_GUID, couponEntity);
	}
	
	@Test
	public void testSuccessfulDeleteOfExistingCoupon() {
		allowingSuccessfulDeleteOfExistingCoupon(true, true);
		
		ExecutionResult<Void> executionResult = orderCouponWriterStrategy.deleteCouponForOrder(STORE_CODE, CART_ORDER_GUID, COUPON_CODE);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.resourceStatus(ResourceStatus.DELETE_OK);
	}


	@Test
	public void testSuccessfulDeleteOfNonExistingCoupon() {
		allowingSuccessfulDeleteOfExistingCoupon(true, false);
		
		ExecutionResult<Void> executionResult = orderCouponWriterStrategy.deleteCouponForOrder(STORE_CODE, CART_ORDER_GUID, COUPON_CODE);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.resourceStatus(ResourceStatus.DELETE_OK);
	}
	
	
	@Test
	public void testDeletingCouponReturnsServerError() {
		allowingSuccessfulDeleteOfExistingCoupon(false, true);

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		orderCouponWriterStrategy.deleteCouponForOrder(STORE_CODE, CART_ORDER_GUID, COUPON_CODE);
	}

	private void setUpCartOrderRepositoryToReturnCartOrder(final CartOrder cartOrder) {
		if (cartOrder == null) {
			when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.<CartOrder>createNotFound());
			return;
		}
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrder.getShoppingCartGuid()).thenReturn(SHOPPING_CART_GUID);
	}

	private void setUpShoppingCart() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		when(shoppingCartRepository.getShoppingCart(SHOPPING_CART_GUID)).thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		Shopper shopper = mock(Shopper.class);
		when(shoppingCart.getShopper()).thenReturn(shopper);
		Customer customer = mock(Customer.class);
		when(shopper.getCustomer()).thenReturn(customer);
		when(customer.getEmail()).thenReturn(CUSTOMER_EMAIL);
	}

	private void setupCouponRepositoryToReturnValidityOfCoupon(final boolean valid) {
		when(couponRepository.isCouponValidInStore(COUPON_CODE, STORE_CODE, CUSTOMER_EMAIL)).thenReturn(ExecutionResultFactory.createReadOK(valid));
	}

	private void setUpCouponTransformerToTransformCoupon() {
		when(couponTransformer.transformToEntity(coupon)).thenReturn(couponEntity);
	}

	private void setUpCouponRepositoryToReturnCoupon() {
		when(couponRepository.findByCouponCode(COUPON_CODE)).thenReturn(ExecutionResultFactory.createReadOK(coupon));
	}

	private void allowingCouponToBeAddedAndSaved(final boolean allowing, final boolean isNewlyAdded) {
		when(cartOrder.addCoupon(COUPON_CODE)).thenReturn(isNewlyAdded);
		if (allowing) {
			when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		} else {
			when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.<CartOrder>createServerError("test server error"));
		}
	}
	
	private void allowingSuccessfulDeleteOfExistingCoupon(final boolean allowing, final boolean existing) {
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrder.removeCoupon(COUPON_CODE)).thenReturn(existing);
		if (allowing) {
			when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		} else {
			when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.<CartOrder>createServerError("test server error"));
		}
	}
}
