/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.impl;

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoConstants.EMAIL_INFO_NAME;
import static com.elasticpath.rest.schema.ResourceState.Builder.create;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfo;
import com.elasticpath.rest.resource.orders.emailinfo.ReadEmailInfoCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadEmailInfoCommand}.
 */
@Named
public class ReadEmailInfoCommandImpl implements ReadEmailInfoCommand {

	private ResourceState<OrderEntity> orderResourceState;

	@Override
	public ExecutionResult<ResourceState<InfoEntity>> execute() {

		String orderUri = orderResourceState.getSelf()
				.getUri();
		String selfUri = URIUtil.format(orderUri, EmailInfo.URI_PART);

		OrderEntity entity = orderResourceState.getEntity();

		InfoEntity infoEntity = InfoEntity.builder()
				.withName(EMAIL_INFO_NAME)
				.withInfoId(entity.getOrderId())
				.build();
		ResourceState.Builder<InfoEntity> infoRepresentationBuilder = create(infoEntity)
				.withSelf(createSelf(selfUri))
				.withScope(orderResourceState.getScope());

		return createReadOK(infoRepresentationBuilder.build());
	}

	/**
	 * Builder.
	 */
	@Named("readEmailInfoCommandBuilder")
	public static class BuilderImpl implements ReadEmailInfoCommand.Builder {

		private final ReadEmailInfoCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadEmailInfoCommandImpl command) {

			this.command = command;
		}

		@Override
		public ReadEmailInfoCommand.Builder setOrderResourceState(final ResourceState<OrderEntity> orderResourceState) {

			command.orderResourceState = orderResourceState;
			return this;
		}

		@Override
		public ReadEmailInfoCommandImpl build() {

			assert command.orderResourceState != null : "ResourceState<OrderEntity> must be set.";
			return command;
		}
	}
}