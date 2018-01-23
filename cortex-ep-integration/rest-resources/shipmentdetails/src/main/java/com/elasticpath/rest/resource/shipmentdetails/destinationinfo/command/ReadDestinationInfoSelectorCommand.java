/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Read Delivery Address Info Selector Command Interface.
 */
public interface ReadDestinationInfoSelectorCommand extends Command<ResourceState<SelectorEntity>> {

	/**
	 * The Builder.
	 */
	interface Builder extends Command.Builder<ReadDestinationInfoSelectorCommand> {

		/**
		 * Sets the shipment details id.
		 *
		 * @param shipmentDetailsId the shipment details id
		 * @return the builder
		 */
		Builder setShipmentDetailsId(String shipmentDetailsId);

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope
		 * @return the builder
		 */
		Builder setScope(String scope);
	}
}
