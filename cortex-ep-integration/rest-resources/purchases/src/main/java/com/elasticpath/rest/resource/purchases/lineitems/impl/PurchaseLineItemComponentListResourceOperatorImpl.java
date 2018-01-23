/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import java.util.Collection;
import java.util.LinkedHashSet;

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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;


/**
 * The Command for reading purchase line item components list.
 */
@Singleton
@Named("purchaseLineItemComponentListResourceOperatorImpl")
@Path({AnyResourceUri.PATH_PART, Components.PATH_PART})
public final class PurchaseLineItemComponentListResourceOperatorImpl implements ResourceOperator {

	private final PurchaseLineItemLookup purchaseLineItemLookup;


	/**
	 * Instantiates a new read purchase line item component list command impl.
	 *
	 * @param purchaseLineItemLookup the purchase line item lookup
	 */
	@Inject
	public PurchaseLineItemComponentListResourceOperatorImpl(
			@Named("purchaseLineItemLookup")
			final PurchaseLineItemLookup purchaseLineItemLookup) {

		this.purchaseLineItemLookup = purchaseLineItemLookup;
	}


	/**
	 * Process READ operation on a single line item element for a purchase.
	 *
	 * @param purchaseLineItem the purchase line item
	 * @param operation the Resource Operation
	 * @return the {@link com.elasticpath.rest.OperationResult} with a links {@link ResourceState}
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadLineItemComponentsList(
			@AnyResourceUri
			final ResourceState<PurchaseLineItemEntity> purchaseLineItem,
			final ResourceOperation operation) {

		PurchaseLineItemEntity entity = purchaseLineItem.getEntity();
		Collection<String> componentIdsForLineItemId = Assign.ifSuccessful(
				purchaseLineItemLookup.getComponentIdsForLineItemId(purchaseLineItem.getScope(),
						entity.getPurchaseId(), entity.getLineItemId()));

		String selfUri = URIUtil.format(ResourceStateUtil.getSelfUri(purchaseLineItem), Components.URI_PART);

		Self self = SelfFactory.createSelf(selfUri);
		ResourceLink parentLink = ResourceLinkFactory.create(ResourceStateUtil.getSelfUri(purchaseLineItem),
				PurchasesMediaTypes.PURCHASE_LINE_ITEM.id(),
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REL,
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_COMPONENTS_REL);

		Collection<ResourceLink> links = getResourceLinksForComponents(componentIdsForLineItemId, selfUri);
		links.add(parentLink);

		ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(LinksEntity.builder().build())
				.withLinks(links)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.build();

		return OperationResultFactory.createReadOK(resourceState, operation);
	}

	private Collection<ResourceLink> getResourceLinksForComponents(final Collection<String> componentIds, final String selfUri) {

		Collection<ResourceLink> links = new LinkedHashSet<>(componentIds.size());
		for (String componentId : componentIds) {
			String uri = URIUtil.format(selfUri, componentId);
			ResourceLink link = ElementListFactory.createElementOfList(uri, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());
			links.add(link);
		}

		return links;
	}
}
