/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.OrderCouponsLookup;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Coupons look up.
 */
@Named("orderCouponsLookup")
@Singleton
public final class OrderCouponsLookupImpl implements OrderCouponsLookup {

	private final OrderCouponsLookupStrategy orderCouponsLookupStrategy;
	private final TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> couponDetailsTransformer;
	private final TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> couponFormTransformer;
	private final TransformRfoToResourceState<InfoEntity, Iterable<String>, OrderEntity> couponInfoTransformer;

	/**
	 * Constructor.
	 *
	 * @param orderCouponsLookupStrategy order coupons lookup
	 * @param couponDetailsTransformer coupon details transformer
	 * @param couponFormTransformer coupon form transformer
	 * @param couponInfoTransformer coupon info transformer
	 */
	@Inject
	OrderCouponsLookupImpl(
			@Named("orderCouponsLookupStrategy")
			final OrderCouponsLookupStrategy orderCouponsLookupStrategy,
			@Named("couponDetailsTransformer")
			final TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> couponDetailsTransformer,
			@Named("couponFormTransformer")
			final TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> couponFormTransformer,
			@Named("couponInfoTransformer")
			final TransformRfoToResourceState<InfoEntity, Iterable<String>, OrderEntity> couponInfoTransformer) {

		this.orderCouponsLookupStrategy = orderCouponsLookupStrategy;
		this.couponDetailsTransformer = couponDetailsTransformer;
		this.couponFormTransformer = couponFormTransformer;
		this.couponInfoTransformer = couponInfoTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<CouponEntity>> getCouponDetailsForOrder(
			final ResourceState<OrderEntity> orderState, final String couponId) {

		String decodedCouponId = Base32Util.decode(couponId);
		String decodedOrderId = Base32Util.decode(orderState.getEntity().getOrderId());
		String scope = orderState.getScope();

		CouponEntity coupon =
				Assign.ifSuccessful(orderCouponsLookupStrategy.getCouponDetailsForOrder(scope, decodedOrderId, decodedCouponId));

		CouponEntity enhancedCoupon = CouponEntity.builderFrom(coupon)
				.withParentId(decodedOrderId)
				.withParentType(orderState.getSelf().getType())
				.build();

		ResourceState<CouponEntity> couponRepresentation =
				couponDetailsTransformer.transform(enhancedCoupon, orderState);

		return ExecutionResultFactory.createReadOK(couponRepresentation);
	}

	@Override
	public ExecutionResult<ResourceState<InfoEntity>> getCouponInfoForOrder(final ResourceState<OrderEntity> orderState) {
		String scope = orderState.getScope();
		String decodedOrderId = Base32Util.decode(orderState.getEntity().getOrderId());
		Collection<String> couponIds = Assign.ifSuccessful(orderCouponsLookupStrategy.findCouponIdsForOrder(scope, decodedOrderId));

		return ExecutionResultFactory.createReadOK(couponInfoTransformer.transform(couponIds, orderState));
	}

	@Override
	public ExecutionResult<ResourceState<CouponEntity>> getCouponFormForOrder(final ResourceState<OrderEntity> orderState) {
		CouponEntity couponEntity = CouponEntity.builder()
				.withCode(StringUtils.EMPTY)
				.build();
		return ExecutionResultFactory.createReadOK(couponFormTransformer.transform(couponEntity, orderState));
	}

}


