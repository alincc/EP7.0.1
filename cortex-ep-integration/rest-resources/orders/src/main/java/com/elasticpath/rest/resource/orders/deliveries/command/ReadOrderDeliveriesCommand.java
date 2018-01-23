/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command to read deliveries of an order.
 */
public interface ReadOrderDeliveriesCommand extends Command<ResourceState<LinksEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<ReadOrderDeliveriesCommand> {

		/**
		 * Sets the order {@link ResourceState}.
		 *
		 * @param order the order {@link ResourceState}
		 * @return this {@link ReadOrderDeliveriesCommand.Builder}
		 */
		Builder setOrder(ResourceState<OrderEntity> order);
	}
}
