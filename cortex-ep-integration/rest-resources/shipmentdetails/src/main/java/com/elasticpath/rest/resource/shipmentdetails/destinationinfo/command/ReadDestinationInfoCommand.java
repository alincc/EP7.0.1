/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Command to read shipping address information on an order.
 */
public interface ReadDestinationInfoCommand extends Command<ResourceState<InfoEntity>> {

	/**
	 * Builds a {@link ReadDestinationInfoCommand}.
	 */
	interface Builder extends Command.Builder<ReadDestinationInfoCommand> {

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
