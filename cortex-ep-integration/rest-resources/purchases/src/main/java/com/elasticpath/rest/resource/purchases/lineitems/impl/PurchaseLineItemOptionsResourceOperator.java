/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Read options for purchase line item .
 */
@Singleton
@Named("purchaseLineItemOptionsResourceOperator")
@Path({AnyResourceUri.PATH_PART, Options.PATH_PART})
public final class PurchaseLineItemOptionsResourceOperator implements ResourceOperator {

	private final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup;

	/**
	 * Default Constructor.
	 *
	 * @param purchaseLineItemOptionsLookup the purchase line item options lookup
	 */
	@Inject
	PurchaseLineItemOptionsResourceOperator(
			@Named("purchaseLineItemOptionsLookup")
			final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup) {

		this.purchaseLineItemOptionsLookup = purchaseLineItemOptionsLookup;
	}

	/**
	 * Process READ operation on line item options.
	 *
	 * @param purchaseLineItem the purchase line item
	 * @param operation the Resource Operation
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptions(
			@AnyResourceUri
			final ResourceState<PurchaseLineItemEntity> purchaseLineItem,
			final ResourceOperation operation) {

		PurchaseLineItemEntity entity = purchaseLineItem.getEntity();
		String scope = purchaseLineItem.getScope();
		String purchaseId = entity.getPurchaseId();
		String lineItemId = entity.getLineItemId();
		Self lineItemSelf = purchaseLineItem.getSelf();
		Collection<String> optionsIds = Assign.ifSuccessful(purchaseLineItemOptionsLookup.findOptionIdsForLineItem(scope, purchaseId,
				lineItemId));
		ResourceState<LinksEntity> lineItemOptions = createLinksRepresentation(lineItemSelf, optionsIds);
		return OperationResultFactory.createReadOK(lineItemOptions, operation);
	}

	private ResourceState<LinksEntity> createLinksRepresentation(final Self lineItemSelf, final Collection<String> optionIds) {

		ResourceLink lineItemLink = ResourceLinkFactory.createFromSelf(lineItemSelf,
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REL, PurchaseLineItemsResourceRels.OPTIONS_REV);

		String optionsUri = URIUtil.format(lineItemSelf.getUri(), Options.URI_PART);
		Self optionsSelf = SelfFactory.createSelf(optionsUri);

		Collection<ResourceLink> links = new ArrayList<>();
		links.add(lineItemLink);

		for (String optionId : optionIds) {
			String optionUri = URIUtil.format(optionsUri, optionId);
			ResourceLink optionLink = ElementListFactory.createElementOfList(optionUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM_OPTION.id());
			links.add(optionLink);
		}

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(optionsSelf)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.withLinks(links)
				.build();
	}
}
