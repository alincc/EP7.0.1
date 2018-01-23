/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.command.impl;

import java.util.ArrayList;
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
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.command.ReadPurchaseLineItemsCommand;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Command for reading line item IDs for a purchase.
 */
@Named
final class ReadPurchaseLineItemsCommandImpl implements ReadPurchaseLineItemsCommand {

	private final String resourceServerName;
	private final ResourceOperationContext operationContext;
	private final PurchaseLineItemLookup purchaseLineItemLookup;

	private String scope;
	private String purchaseId;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName     the resource server name
	 * @param operationContext       the resource operation context
	 * @param purchaseLineItemLookup the purchase line item lookup
	 */
	@Inject
	ReadPurchaseLineItemsCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("purchaseLineItemLookup")
			final PurchaseLineItemLookup purchaseLineItemLookup) {

		this.resourceServerName = resourceServerName;
		this.operationContext = operationContext;
		this.purchaseLineItemLookup = purchaseLineItemLookup;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {

		Collection<String> lineItemIds = Assign.ifSuccessful(purchaseLineItemLookup.getLineItemIdsForPurchase(scope, purchaseId));
		String selfUri = operationContext.getResourceOperation().getUri();
		Collection<ResourceLink> links = new ArrayList<>();

		for (String lineItemId : lineItemIds) {
			String lineItemUri = URIUtil.format(selfUri, lineItemId);
			ResourceLink lineItemLink = ElementListFactory.createElementOfList(lineItemUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());
			links.add(lineItemLink);
		}

		String purchaseUri = URIUtil.format(resourceServerName, scope, purchaseId);
		links.add(ResourceLinkFactory.create(purchaseUri, PurchasesMediaTypes.PURCHASE.id(),
				PurchaseResourceRels.PURCHASE_REL, PurchaseLineItemsResourceRels.PURCHASE_LINEITEMS_REV));

		Self self = SelfFactory.createSelf(selfUri);
		ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.withLinks(links)
				.build();

		return ExecutionResultFactory.createReadOK(resourceState);
	}

	/**
	 * Builds a {@link ReadPurchaseLineItemsCommandImpl}.
	 */
	@Named("readPurchaseLineItemsCommandBuilder")
	static class BuilderImpl implements ReadPurchaseLineItemsCommand.Builder {

		private final ReadPurchaseLineItemsCommandImpl command;


		/**
		 * Default constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadPurchaseLineItemsCommandImpl command) {
			this.command = command;
		}


		@Override
		public ReadPurchaseLineItemsCommand build() {
			assert command.scope != null : "scope required";
			assert command.purchaseId != null : "purchaseId required";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public Builder setPurchaseId(final String purchaseId) {
			command.purchaseId = purchaseId;
			return this;
		}
	}
}
