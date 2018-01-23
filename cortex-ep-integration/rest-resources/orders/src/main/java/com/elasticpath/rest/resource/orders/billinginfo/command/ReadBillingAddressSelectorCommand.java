/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Marker interface to type the Command.
 */
public interface ReadBillingAddressSelectorCommand extends Command<ResourceState<SelectorEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<ReadBillingAddressSelectorCommand> {

		/**
		 * Sets the {@link ResourceState}&lt;{@link LinksEntity}>.
		 * 
		 * @param billingAddressLinks The order
		 * @return This builder
		 */
		Builder setBillingAddressLinks(ResourceState<LinksEntity> billingAddressLinks);

		/**
		 * Sets the order ID.
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