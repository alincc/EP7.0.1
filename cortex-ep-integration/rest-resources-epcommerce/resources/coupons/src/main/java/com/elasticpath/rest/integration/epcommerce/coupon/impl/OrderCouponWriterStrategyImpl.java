/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponWriterStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Coupon writer strategy.
 */
@Singleton
@Named("orderCouponWriterStrategy")
public class OrderCouponWriterStrategyImpl implements OrderCouponWriterStrategy {
	private final ShoppingCartRepository shoppingCartRepository;

	private final CouponRepository couponRepository;

	private final CartOrderRepository cartOrderRepository;
	
	private final AbstractDomainTransformer<Coupon, CouponEntity> couponTransformer;

	/**
	 * Constructor.
	 * 
	 * @param shoppingCartRepository shopping cart repository.
	 * @param cartOrderRepository cart order repository.
	 * @param couponRepository coupon repository.
	 * @param couponTransformer coupon to coupon entity transformer.
	 */
	@Inject
	public OrderCouponWriterStrategyImpl(
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository, 
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("couponRepository")
			final CouponRepository couponRepository, 
			@Named("couponTransformer")
			final AbstractDomainTransformer<Coupon, CouponEntity> couponTransformer) {
		this.shoppingCartRepository = shoppingCartRepository;
		this.cartOrderRepository = cartOrderRepository;
		this.couponRepository = couponRepository;
		this.couponTransformer = couponTransformer;
	}

	@Override
	public ExecutionResult<Void> deleteCouponForOrder(final String storeCode, final String cartOrderGuid, final String couponCode) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		boolean removedCoupon = cartOrder.removeCoupon(couponCode);
		if (removedCoupon) {
			Ensure.successful(cartOrderRepository.saveCartOrder(cartOrder));
		}
		return ExecutionResultFactory.createDeleteOK();
	}

	@Override
	public ExecutionResult<CouponEntity> createCouponForOrder(final String storeCode, final String cartOrderGuid, final CouponEntity form) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(cartOrder.getShoppingCartGuid()));
		String couponCode = form.getCode();

		String shopperEmail = shoppingCart.getShopper().getCustomer().getEmail();
		boolean valid = Assign.ifSuccessful(couponRepository.isCouponValidInStore(couponCode, storeCode, shopperEmail));

		if (valid) {
			Coupon coupon = Assign.ifSuccessful(couponRepository.findByCouponCode(couponCode));
			boolean isNewlyAdded = cartOrder.addCoupon(coupon.getCouponCode());
			if (isNewlyAdded) {
				Ensure.successful(cartOrderRepository.saveCartOrder(cartOrder));
			}
			CouponEntity couponEntity = couponTransformer.transformToEntity(coupon);
			return ExecutionResultFactory.createCreateOKWithData(couponEntity, !isNewlyAdded);
		}
		return ExecutionResultFactory.createStateFailure("Coupon code is not valid");
	}
}
