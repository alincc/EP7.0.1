/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Resource command for reading a purchase line item.
 */
public interface ReadPurchaseLineItemResourceCommand extends Command<ResourceState<PurchaseLineItemEntity>> {

	/**
	 * Builds the Command.
	 */
	interface Builder extends Command.Builder<ReadPurchaseLineItemResourceCommand> {

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

		/**
		 * Sets the line item id.
		 *
		 * @param lineItemId the Line Item Id.
		 * @return the builder
		 */
		Builder setLineItemId(String lineItemId);
	}
}
