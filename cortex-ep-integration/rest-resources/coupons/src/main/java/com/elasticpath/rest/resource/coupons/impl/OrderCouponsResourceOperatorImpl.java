/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.resource.coupons.OrderCouponWriter;
import com.elasticpath.rest.resource.coupons.OrderCouponsLookup;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Info;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on order coupons.
 */
@Singleton
@Named("orderCouponsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class OrderCouponsResourceOperatorImpl implements ResourceOperator {

	private final OrderCouponWriter orderCouponWriter;
	private final OrderCouponsLookup orderCouponsLookup;


	/**
	 * Initialize the resource operator.
	 *
	 * @param orderCouponsLookup order coupons lookup
	 * @param orderCouponWriter order coupon writer
	 */
	@Inject
	OrderCouponsResourceOperatorImpl(
			@Named("orderCouponsLookup")
			final OrderCouponsLookup orderCouponsLookup,
			@Named("orderCouponWriter")
			final OrderCouponWriter orderCouponWriter) {
		this.orderCouponsLookup = orderCouponsLookup;
		this.orderCouponWriter = orderCouponWriter;
	}

	/**
	 * Handles READ for specific coupon from order resource.
	 *
	 * @param orderRepresentation the order representation to be used in the read.
	 * @param couponId coupon id
	 * @param operation operation
	 * @return the operation result.
	 */
	@Path({ SingleResourceUri.PATH_PART, ResourceId.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadCouponDetailsForOrder(
			@SingleResourceUri
			final ResourceState<OrderEntity> orderRepresentation,
			@ResourceId
			final String couponId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<CouponEntity>> couponDetailsForOrder =
				orderCouponsLookup.getCouponDetailsForOrder(orderRepresentation, couponId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(couponDetailsForOrder, operation);
	}

	/**
	 * Handles READ couponinfo from order resource.
	 *
	 * @param orderRepresentation the order representation.
	 * @param operation resource operation.
	 * @return the operation result.
	 */
	@Path({ SingleResourceUri.PATH_PART, Info.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadCouponInfoForOrder(
			@SingleResourceUri
			final ResourceState<OrderEntity> orderRepresentation,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<InfoEntity>> couponInfoForOrder =
				orderCouponsLookup.getCouponInfoForOrder(orderRepresentation);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(couponInfoForOrder, operation);
	}


	/**
	 * Handles DELETE for specific coupon from order resource.
	 *
	 * @param orderEntityResourceState order representation
	 * @param couponId coupon id
	 * @param operation operation
	 * @return the operation result.
	 */
	@Path({ SingleResourceUri.PATH_PART, ResourceId.PATH_PART })
	@OperationType(Operation.DELETE)
	public OperationResult processDeleteCouponFromOrder(
			@SingleResourceUri
			final ResourceState<OrderEntity> orderEntityResourceState,
			@ResourceId
			final String couponId,
			final ResourceOperation operation) {

		ExecutionResult<Void> voidExecutionResult =
				orderCouponWriter.deleteCouponFromOrder(orderEntityResourceState, couponId);
		ExecutionResult<ResourceState<ResourceEntity>> result =
				ExecutionResultFactory.create(voidExecutionResult.getErrorMessage(), voidExecutionResult.getResourceStatus(), null);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process READ operation on Coupon form.
	 *
	 * @param orderRepresentation order representation
	 * @param operation the resource operation
	 * @return the {@link OperationResult}
	 */
	@Path({SingleResourceUri.PATH_PART, Form.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadCouponFormForOrder(
			@SingleResourceUri
			final ResourceState<OrderEntity> orderRepresentation,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<CouponEntity>> couponFormForOrder =
				orderCouponsLookup.getCouponFormForOrder(orderRepresentation);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(couponFormForOrder, operation);
	}

	/**
	 * Process creating (apply) a coupon to order resource.
	 *
	 * @param resourceState order representation
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({SingleResourceUri.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processCreateCouponForOrder(
			@SingleResourceUri
			final ResourceState<OrderEntity> resourceState,
			final ResourceOperation operation) {

		CouponEntity entity = getPostedEntity(operation);

		ResourceState<CouponEntity> couponForm = ResourceState.Builder.create(entity).build();
		ExecutionResult<ResourceState<ResourceEntity>> result = orderCouponWriter.createCouponForOrder(resourceState, couponForm);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private CouponEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		return ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), CouponEntity.class);
	}
}
