/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.service.order.OrderService;

/**
 * The facade for {@link OrderShipment} related operations.
 */
@Singleton
@Named("shipmentRepository")
public class ShipmentRepositoryImpl implements ShipmentRepository {

	private static final String SHIPMENT_NOT_FOUND = "Shipment not found";

	private static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private final OrderService orderService;

	private final OrderRepository orderRepository;

	/**
	 * Initialize.
	 *
	 * @param orderService the order service
	 * @param orderRepository the order repository
	 */
	@Inject
	public ShipmentRepositoryImpl(
			@Named("orderService")
			final OrderService orderService,
			@Named("orderRepository")
			final OrderRepository orderRepository) {

		this.orderService = orderService;
		this.orderRepository = orderRepository;
	}

	@Override
	@CacheResult
	public ExecutionResult<PhysicalOrderShipment> find(final String orderGuid, final String shipmentGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {

				OrderShipment orderShipment
						= Assign.ifNotNull(orderService.findOrderShipment(shipmentGuid, ShipmentType.PHYSICAL),
							OnFailure.returnNotFound(SHIPMENT_NOT_FOUND));
				// Check that the requested order matches the requested shipment so that
				// we don't allow access to other customers shipments.
				// TODO: I would prefer if this check was added to the orderService query above as:
				// orderService.findOrderShipment(orderGuid, orderShipmentGuid, type)
				Ensure.isTrue(orderGuid.equals(orderShipment.getOrder().getGuid()),
						OnFailure.returnNotFound(SHIPMENT_NOT_FOUND));
				return ExecutionResultFactory.createReadOK(orderShipment);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<PhysicalOrderShipment>> findAll(final String storeCode, final String orderGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {

				Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
				List<PhysicalOrderShipment> physicalShipments = order.getPhysicalShipments();

				if (CollectionUtils.isEmpty(physicalShipments)) {
					physicalShipments = Collections.emptyList();
				}

				return ExecutionResultFactory.createReadOK(physicalShipments);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<OrderSku>> getOrderSkusForShipment(final String scope, final String purchaseId, final String shipmentId) {
		OrderShipment shipment = Assign.ifSuccessful(find(purchaseId, shipmentId));
		Collection<OrderSku> shipmentOrderSkus = shipment.getShipmentOrderSkus();
		return ExecutionResultFactory.createReadOK(shipmentOrderSkus);
	}

	@Override
	@CacheResult
	public ExecutionResult<OrderSku> getOrderSku(final String scope,
												final String purchaseId,
												final String shipmentId,
												final String lineItemId,
												final String parentOrderSkuGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {

				OrderShipment shipment = Assign.ifSuccessful(find(purchaseId, shipmentId));
				OrderSku orderSku = Assign.ifNotNull(getOrderSkuByGuid(shipment.getShipmentOrderSkus(), lineItemId),
						OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));

				// now check to see if we are getting the proper level in our object graph.
				if (!isTopLevel(parentOrderSkuGuid, orderSku)) {
					Ensure.isTrue(isComponent(orderSku), OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));
					if (parentOrderSkuGuid != null) {
						Ensure.isTrue(parentOrderSkuGuid.equals(orderSku.getParent().getGuid()), OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));
					}
				}

				return ExecutionResultFactory.createReadOK(orderSku);
			}
		}.execute();
	}

	private OrderSku getOrderSkuByGuid(final Set<OrderSku> orderSkus, final String skuGuid) {
		for (OrderSku orderSku : orderSkus) {
			if (orderSku.getGuid().equals(skuGuid)) {
				return orderSku;
			}
		}

		return null;
	}

	private boolean isTopLevel(final String parentOrderSkuGuid, final OrderSku orderSku) {
		return parentOrderSkuGuid == null && orderSku.getParent() == null;
	}

	private boolean isBundleComponent(final OrderSku orderSku) {
		return orderSku.getParent() != null;
	}

	private boolean isComponent(final OrderSku orderSku) {
		return isBundleComponent(orderSku);
	}
}