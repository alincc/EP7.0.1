/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.billing.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Read the default shipping address.
 */
public interface ReadDefaultBillingAddressCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * Command Builder.
	 */
	interface Builder extends Command.Builder<ReadDefaultBillingAddressCommand> {

		/**
		 * Set the scope for the command.
		 *
		 * @param scope The scope.
		 * @return This builder instance.
		 */
		Builder setScope(String scope);
	}
}
