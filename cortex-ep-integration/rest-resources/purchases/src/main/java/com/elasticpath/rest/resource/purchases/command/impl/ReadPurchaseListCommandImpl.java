/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.command.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.command.ReadPurchaseListCommand;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Executes a read command on the purchase list.
 */
@Named
final class ReadPurchaseListCommandImpl implements ReadPurchaseListCommand {

	private final String resourceServerName;
	private final ResourceOperationContext resourceOperationContext;
	private final PurchaseLookup lookup;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName       resource server name
	 * @param resourceOperationContext resource operation context
	 * @param lookup                   Purchase lookup
	 */
	@Inject
	ReadPurchaseListCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("purchaseLookup")
			final PurchaseLookup lookup) {

		this.resourceServerName = resourceServerName;
		this.resourceOperationContext = resourceOperationContext;
		this.lookup = lookup;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {

		String userId = resourceOperationContext.getUserIdentifier();
		Collection<String> purchaseIdList = Assign.ifSuccessful(lookup.findPurchaseIds(scope, userId));
		String selfUri = URIUtil.format(resourceServerName, scope);
		Self self = SelfFactory.createSelf(selfUri);
		ResourceState<LinksEntity> resourceState = ResourceState.Builder
				.create(LinksEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.addingLinks(ElementListFactory.createElementsOfList(selfUri, purchaseIdList, PurchasesMediaTypes.PURCHASE.id()))
				.build();
		return ExecutionResultFactory.createReadOK(resourceState);

	}

	/**
	 * Builds a {@link ReadPurchaseListCommandImpl}.
	 */
	@Named("readPurchaseListCommandBuilder")
	static class BuilderImpl implements ReadPurchaseListCommand.Builder {

		private final ReadPurchaseListCommandImpl command;


		/**
		 * Constructor.
		 *
		 * @param command Command instance.
		 */
		@Inject
		BuilderImpl(final ReadPurchaseListCommandImpl command) {
			this.command = command;
		}


		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadPurchaseListCommand build() {
			assert command.scope != null : "scope required";
			return command;
		}
	}
}
