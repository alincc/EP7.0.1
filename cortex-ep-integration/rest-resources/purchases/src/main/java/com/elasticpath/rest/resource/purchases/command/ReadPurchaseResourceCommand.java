/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Executes a read on the purchases resource.
 */
public interface ReadPurchaseResourceCommand extends Command<ResourceState<PurchaseEntity>> {

	/**
	 * Builds {@link ReadPurchaseResourceCommand}s.
	 */
	interface Builder extends Command.Builder<ReadPurchaseResourceCommand> {

		/**
		 * Set the scope.
		 *
		 * @param scope the scope
		 * @return the builder
		 */
		Builder setScope(String scope);

		/**
		 * Set the purchase ID.
		 *
		 * @param purchaseId the purchase ID
		 * @return the builder
		 */
		Builder setPurchaseId(String purchaseId);
	}
}
