/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Interface for command to read a Billing Address.
 */
public interface ReadBillingAddressChoiceCommand extends Command<ResourceState<LinksEntity>> {

	/**
	 * Constructs an {@link ReadBillingAddressChoiceCommand}.
	 */
	interface Builder extends Command.Builder<ReadBillingAddressChoiceCommand> {

		/**
		 * Set the scope for the command.
		 *
		 * @param scope the scope
		 * @return this builder instance
		 */
		Builder setScope(String scope);

		/**
		 * Set the order id for the command.
		 *
		 * @param orderId the order id
		 * @return this builder instance
		 */
		Builder setOrderId(String orderId);

		/**
		 * Sets the billing address {@link com.elasticpath.rest.schema.ResourceState}.
		 *
		 * @param billingAddress the billing address {@link com.elasticpath.rest.schema.ResourceState}
		 * @return the builder
		 */
		Builder setBillingAddressResourceState(ResourceState<AddressEntity> billingAddress);
	}
}
