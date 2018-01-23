/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.shipping.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Command for reading default shipping address.
 */
public interface ReadDefaultShippingAddressCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * Builder.
	 */
	interface Builder extends Command.Builder<ReadDefaultShippingAddressCommand> {

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope
		 * @return the builder
		 */
		Builder setScope(String scope);
	}
}
