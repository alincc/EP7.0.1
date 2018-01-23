/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command for reading line item IDs for a purchase.
 */
public interface ReadPurchaseLineItemsCommand extends Command<ResourceState<LinksEntity>> {

	/**
	 * Builds {@link ReadPurchaseLineItemsCommand}s.
	 */
	interface Builder extends Command.Builder<ReadPurchaseLineItemsCommand> {

		/**
		 * Set the purchase ID.
		 *
		 * @param purchaseId the purchase ID
		 * @return the builder
		 */
		Builder setPurchaseId(String purchaseId);

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope
		 * @return the builder
		 */
		Builder setScope(String scope);
	}
}
