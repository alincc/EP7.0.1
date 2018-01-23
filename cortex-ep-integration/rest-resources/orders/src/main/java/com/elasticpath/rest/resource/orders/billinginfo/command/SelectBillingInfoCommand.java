/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Marker interface to type the Command.
 */
public interface SelectBillingInfoCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<SelectBillingInfoCommand> {

		/**
		 * Sets the order ID.
		 * 
		 * @param orderId The order ID.
		 * @return this builder
		 */
		Builder setOrderId(String orderId);

		/**
		 * Sets the scope for the order resource.
		 * 
		 * @param scope The scope
		 * @return this builder
		 */
		Builder setScope(String scope);

		/**
		 * Sets the {@link ResourceState}&lt;{@link AddressEntity}>.
		 * 
		 * @param billingAddressResourceState The billing address resource state
		 * @return this builder
		 */
		Builder setBillingAddress(ResourceState<AddressEntity> billingAddressResourceState);
	}
}