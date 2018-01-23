/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command to read email information on an order.
 */
public interface ReadEmailInfoCommand extends Command<ResourceState<InfoEntity>> {

	/**
	 * Builds a {@link ReadEmailInfoCommand}.
	 */
	interface Builder extends Command.Builder<ReadEmailInfoCommand> {

		/**
		 * Sets the {@link ResourceState}&lt;{@link OrderEntity}>.
		 * 
		 * @param orderResourceState The orderResourceState
		 * @return The builder
		 */
		Builder setOrderResourceState(ResourceState<OrderEntity> orderResourceState);
	}
}
