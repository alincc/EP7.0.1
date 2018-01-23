/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.orders.deliveries.DeliveryLookup;
import com.elasticpath.rest.resource.orders.deliveries.command.ReadOrderDeliveryCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command for reading a single order delivery.
 */
@Named
public final class ReadOrderDeliveryCommandImpl implements ReadOrderDeliveryCommand {

	private final DeliveryLookup deliveryLookup;

	private ResourceState<OrderEntity> order;

	private String deliveryId;

	/**
	 * Default constructor.
	 *
	 * @param deliveryLookup the delivery lookup
	 */
	@Inject
	public ReadOrderDeliveryCommandImpl(@Named("deliveryLookup")
	final DeliveryLookup deliveryLookup) {
		this.deliveryLookup = deliveryLookup;
	}

	@Override
	public ExecutionResult<ResourceState<DeliveryEntity>> execute() {
		return deliveryLookup.findByIdAndOrderId(order.getScope(), order.getEntity().getOrderId(), deliveryId);
	}

	/**
	 * Read order delivery command builder.
	 */
	@Named("readOrderDeliveryCommandBuilder")
	public static class BuilderImpl implements ReadOrderDeliveryCommand.Builder {

		private final ReadOrderDeliveryCommandImpl command;

		/**
		 * Default constructor.
		 *
		 * @param command the command
		 */
		@Inject
		public BuilderImpl(final ReadOrderDeliveryCommandImpl command) {
			this.command = command;
		}

		@Override
		public ReadOrderDeliveryCommand.Builder setDeliveryId(final String deliveryId) {
			command.deliveryId = deliveryId;
			return this;
		}

		@Override
		public Builder setOrder(final ResourceState<OrderEntity> order) {
			command.order = order;
			return this;
		}

		@Override
		public ReadOrderDeliveryCommand build() {
			assert command.deliveryId != null : "delivery ID must be set";
			assert command.order != null : "Order must be set";
			return command;
		}
	}
}
