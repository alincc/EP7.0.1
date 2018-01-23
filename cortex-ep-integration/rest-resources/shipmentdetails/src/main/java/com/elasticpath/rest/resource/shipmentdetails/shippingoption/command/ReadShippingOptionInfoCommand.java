/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Read Shipping Option Info Command.
 */
public interface ReadShippingOptionInfoCommand extends Command<ResourceState<InfoEntity>> {

	/**
	 * The Builder.
	 */
	interface Builder extends Command.Builder<ReadShippingOptionInfoCommand> {

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope
		 * @return the builder
		 */
		Builder setScope(String scope);

		/**
		 * Sets the shipment details Id.
		 *
		 * @param shipmentDetailsId the shipment details id.
		 * @return the builder
		 */
		Builder setShipmentDetailsId(String shipmentDetailsId);
	}
}
