/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Executes a read on the purchase list.
 */
public interface ReadPurchaseListCommand extends Command<ResourceState<LinksEntity>> {

	/**
	 * Builds {@link ReadPurchaseListCommand}s.
	 */
	interface Builder extends Command.Builder<ReadPurchaseListCommand> {

		/**
		 * Set the scope.
		 *
		 * @param scope the scope
		 * @return this builder instance
		 */
		Builder setScope(String scope);
	}
}
