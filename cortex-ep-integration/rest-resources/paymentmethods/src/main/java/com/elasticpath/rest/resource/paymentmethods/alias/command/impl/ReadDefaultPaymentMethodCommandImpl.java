/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;

/**
 * Implementation of {@link ReadDefaultPaymentMethodCommand}.
 */
@Named
public final class ReadDefaultPaymentMethodCommandImpl implements ReadDefaultPaymentMethodCommand {

	private final DefaultPaymentMethodLookup defaultPaymentMethodLookup;
	private final PaymentMethodUriBuilder paymentMethodUriBuilder;
	private final ResourceOperationContext resourceOperationContext;

	private String scope;


	/**
	 * Constructor for injection.
	 *
	 * @param defaultPaymentMethodLookup the default payment method id lookup
	 * @param paymentMethodUriBuilder the read payment method command builder
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	public ReadDefaultPaymentMethodCommandImpl(
			@Named("defaultPaymentMethodLookup")
			final DefaultPaymentMethodLookup defaultPaymentMethodLookup,
			@Named("paymentMethodUriBuilder")
			final PaymentMethodUriBuilder paymentMethodUriBuilder,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.defaultPaymentMethodLookup = defaultPaymentMethodLookup;
		this.paymentMethodUriBuilder = paymentMethodUriBuilder;
		this.resourceOperationContext = resourceOperationContext;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {

		String userId = resourceOperationContext.getUserIdentifier();
		String defaultPaymentMethodId = Assign.ifSuccessful(
				defaultPaymentMethodLookup.getDefaultPaymentMethodId(scope, userId));

		String paymentMethodUri = paymentMethodUriBuilder
				.setScope(scope)
				.setPaymentMethodId(Base32Util.encode(defaultPaymentMethodId))
				.build();

		return ExecutionResultFactory.createSeeOther(paymentMethodUri);
	}

	/**
	 * Read default payment method command builder.
	 */
	@Named("readDefaultPaymentMethodCommandBuilder")
	public static class BuilderImpl implements ReadDefaultPaymentMethodCommand.Builder {

		private final ReadDefaultPaymentMethodCommandImpl cmd;

		/**
		 * Constructor for injection.
		 *
		 * @param cmd the cmd
		 */
		@Inject
		public BuilderImpl(final ReadDefaultPaymentMethodCommandImpl cmd) {
			this.cmd = cmd;
		}

		@Override
		public Builder setScope(final String scope) {
			cmd.scope = scope;
			return this;
		}

		@Override
		public ReadDefaultPaymentMethodCommand build() {
			assert cmd.scope != null : "Scope must not be null";
			return cmd;
		}
	}
}
