/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command to read billing information on an order.
 */
public interface ReadBillingInfoCommand extends Command<ResourceState<InfoEntity>> {

	/**
	 * Builds a {@link ReadBillingInfoCommand}.
	 */
	interface Builder extends Command.Builder<ReadBillingInfoCommand> {

		/**
		 * Sets the order identifier.
		 * 
		 * @param orderId The order ID
		 * @return This builder
		 */
		Builder setOrderId(String orderId);

		/**
		 * Sets the order scope.
		 * 
		 * @param scope The order scope
		 * @return This builder
		 */
		Builder setScope(String scope);
	}
}