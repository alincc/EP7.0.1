/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.OrderCouponWriter;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponWriterStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Sets the payment info on a cart order.
 */
@Singleton
@Named("orderCouponWriter")
public final class OrderCouponWriterImpl implements OrderCouponWriter {
	private final OrderCouponWriterStrategy orderCouponWriterStrategy;
	private final TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> couponDetailsTransformer;

	/**
	 * Constructor.
	 *
	 * @param orderCouponWriterStrategy the {@link com.elasticpath.rest.resource.coupons.integration.OrderCouponWriterStrategy}
	 * @param couponDetailsTransformer the {@link com.elasticpath.rest.resource.coupons.transformer.CouponDetailsTransformer}
	 */
	@Inject
	public OrderCouponWriterImpl(
			@Named("orderCouponWriterStrategy")
			final OrderCouponWriterStrategy orderCouponWriterStrategy,
			@Named("couponDetailsTransformer")
			final TransformRfoToResourceState<CouponEntity, CouponEntity, OrderEntity> couponDetailsTransformer) {
		this.orderCouponWriterStrategy = orderCouponWriterStrategy;
		this.couponDetailsTransformer = couponDetailsTransformer;
	}

	@Override
	public ExecutionResult<Void> deleteCouponFromOrder(final ResourceState<OrderEntity> resourceState, final String couponId) {
		String decodedOrderId = Base32Util.decode(resourceState.getEntity().getOrderId());
		String decodedCouponId = Base32Util.decode(couponId);
		String scope = resourceState.getScope();
		return orderCouponWriterStrategy.deleteCouponForOrder(scope, decodedOrderId, decodedCouponId);
	}

	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> createCouponForOrder(final ResourceState<OrderEntity> resourceState,
			final ResourceState<CouponEntity> form) {

		String scope = resourceState.getScope();
		String decodedOrderId = Base32Util.decode(resourceState.getEntity().getOrderId());

		CouponEntity couponEntity = form.getEntity();

		ExecutionResult<CouponEntity> result = orderCouponWriterStrategy.createCouponForOrder(scope, decodedOrderId, couponEntity);

		CouponEntity coupon = Assign.ifSuccessful(result);

		ResourceState<CouponEntity> representation = couponDetailsTransformer.transform(coupon, resourceState);

		return ExecutionResultFactory.createCreateOK(representation.getSelf().getUri(), isNotNewCoupon(result));

	}
	
	private boolean isNotNewCoupon(final ExecutionResult<CouponEntity> createResult) {
		return ResourceStatus.READ_OK.equals(createResult.getResourceStatus());
	}

}
