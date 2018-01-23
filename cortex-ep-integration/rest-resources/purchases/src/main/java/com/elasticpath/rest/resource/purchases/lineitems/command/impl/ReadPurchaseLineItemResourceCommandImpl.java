/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.command.ReadPurchaseLineItemResourceCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Executes command on purchase line item.
 */
@Named
final class ReadPurchaseLineItemResourceCommandImpl implements ReadPurchaseLineItemResourceCommand {

	private final PurchaseLineItemLookup purchaseLineItemLookup;

	private String scope;
	private String purchaseId;
	private String lineItemId;


	/**
	 * Default constructor.
	 *
	 * @param purchaseLineItemLookup the purchase line item lookup.
	 */
	@Inject
	ReadPurchaseLineItemResourceCommandImpl(
			@Named("purchaseLineItemLookup")
			final PurchaseLineItemLookup purchaseLineItemLookup) {

		this.purchaseLineItemLookup = purchaseLineItemLookup;
	}


	@Override
	public ExecutionResult<ResourceState<PurchaseLineItemEntity>> execute() {
		String parentLineItemUri = StringUtils.EMPTY;
		String parentLineItemId = StringUtils.EMPTY;
		return purchaseLineItemLookup.getPurchaseLineItem(scope, purchaseId, lineItemId, parentLineItemUri, parentLineItemId);
	}


	/**
	 * Builder for {@link ReadPurchaseLineItemResourceCommandImpl}.
	 */
	@Named("readPurchaseLineItemResourceCommandBuilder")
	static class BuilderImpl implements ReadPurchaseLineItemResourceCommand.Builder {

		private final ReadPurchaseLineItemResourceCommandImpl command;


		/**
		 * Constructor.
		 *
		 * @param command the command instance.
		 */
		@Inject
		BuilderImpl(final ReadPurchaseLineItemResourceCommandImpl command) {
			this.command = command;
		}


		@Override
		public ReadPurchaseLineItemResourceCommand build() {
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadPurchaseLineItemResourceCommand.Builder setPurchaseId(final String purchaseId) {
			command.purchaseId = purchaseId;
			return this;
		}

		@Override
		public ReadPurchaseLineItemResourceCommand.Builder setLineItemId(final String lineItemId) {
			command.lineItemId = lineItemId;
			return this;
		}
	}
}
