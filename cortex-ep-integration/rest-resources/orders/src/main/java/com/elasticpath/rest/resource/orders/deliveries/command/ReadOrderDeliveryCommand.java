/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command for reading a single order delivery.
 */
public interface ReadOrderDeliveryCommand extends Command<ResourceState<DeliveryEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<ReadOrderDeliveryCommand> {

		/**
		 * Sets the delivery id.
		 * 
		 * @param deliveryId the delivery id
		 * @return the builder
		 */
		Builder setDeliveryId(String deliveryId);

		/**
		 * Sets the {@link ResourceState}&lt;{@link OrderEntity}>.
		 * 
		 * @param order the order
		 * @return this builder
		 */
		Builder setOrder(ResourceState<OrderEntity> order);
	}
}