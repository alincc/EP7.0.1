/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.command.impl;

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.resource.orders.deliveries.DeliveryConstants.DELIVERY_LIST_NAME;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.resource.orders.deliveries.command.ReadOrderDeliveriesCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Command to read deliveries of an order.
 */
@Named
public final class ReadOrderDeliveriesCommandImpl implements ReadOrderDeliveriesCommand {

	private ResourceState<OrderEntity> order;

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {
		String orderUri = ResourceStateUtil.getSelfUri(order);
		String deliveriesSelfUri = URIUtil.format(orderUri, Deliveries.URI_PART);

		LinksEntity linksEntity = LinksEntity.builder()
				.withName(DELIVERY_LIST_NAME)
				.withElementListId(order.getEntity()
						.getOrderId())
				.build();
		ResourceState<LinksEntity> linksRepresentation = ResourceState.Builder
				.create(linksEntity)
				.withScope(order.getScope())
				.withSelf(createSelf(deliveriesSelfUri))
				.build();

		return createReadOK(linksRepresentation);
	}

	/**
	 * Read order deliveries command builder.
	 */
	@Named("readOrderDeliveriesCommandBuilder")
	public static class BuilderImpl implements ReadOrderDeliveriesCommand.Builder {

		private final ReadOrderDeliveriesCommandImpl command;

		/**
		 * Default constructor.
		 *
		 * @param command the command
		 */
		@Inject
		public BuilderImpl(final ReadOrderDeliveriesCommandImpl command) {
			this.command = command;
		}

		@Override
		public Builder setOrder(final ResourceState<OrderEntity> order) {
			command.order = order;
			return this;
		}

		@Override
		public ReadOrderDeliveriesCommand build() {
			assert command.order != null : "The order resource state must be set";
			return command;
		}
	}
}
