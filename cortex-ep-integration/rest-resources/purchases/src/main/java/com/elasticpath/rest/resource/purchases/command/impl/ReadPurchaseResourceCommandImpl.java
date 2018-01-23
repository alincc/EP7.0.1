/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.command.ReadPurchaseResourceCommand;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Executes read commands on purchases.
 */
@Named
final class ReadPurchaseResourceCommandImpl implements ReadPurchaseResourceCommand {

	private final PurchaseLookup lookup;

	private String scope;
	private String purchaseId;


	/**
	 * Constructor.
	 *
	 * @param lookup Purchase lookup
	 */
	@Inject
	ReadPurchaseResourceCommandImpl(
			@Named("purchaseLookup")
			final PurchaseLookup lookup) {

		this.lookup = lookup;
	}


	@Override
	public ExecutionResult<ResourceState<PurchaseEntity>> execute() {
		return lookup.findPurchaseById(scope, purchaseId);
	}


	/**
	 * Builds a {@link ReadPurchaseResourceCommandImpl}.
	 */
	@Named("readPurchaseResourceCommandBuilder")
	static class BuilderImpl implements ReadPurchaseResourceCommand.Builder {

		private final ReadPurchaseResourceCommandImpl command;


		/**
		 * Constructor.
		 *
		 * @param command Command instance.
		 */
		@Inject
		BuilderImpl(final ReadPurchaseResourceCommandImpl command) {
			this.command = command;
		}


		@Override
		public ReadPurchaseResourceCommand build() {
			assert command.scope != null : "scope required";
			assert command.purchaseId != null : "purchaseId required";
			return command;
		}

		@Override
		public BuilderImpl setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public BuilderImpl setPurchaseId(final String purchaseId) {
			command.purchaseId = purchaseId;
			return this;
		}
	}
}
