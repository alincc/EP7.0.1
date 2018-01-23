/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Transforms a PurchaseLineItem entity to a PurchaseLineItemRepresentation.
 */
@Singleton
@Named("purchaseLineItemTransformer")
public class PurchaseLineItemTransformer {

	private final String resourceServerName;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public PurchaseLineItemTransformer(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	/**
	 * transforms a purchase line item entity to a representation.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param purchaseLineItemId the purchase line item id
	 * @param purchaseLineItemEntity the purchase line item entity.
	 * @param parentSelfUri the parent self uri
	 * @return the purchase line item representation.
	 */
	public ResourceState<PurchaseLineItemEntity> transformToRepresentation(
			final String scope,
			final String purchaseId,
			final String purchaseLineItemId,
			final PurchaseLineItemEntity purchaseLineItemEntity,
			final String parentSelfUri) {

		String listUri;
		if (StringUtils.isEmpty(parentSelfUri)) {
			listUri = URIUtil.format(resourceServerName, scope, purchaseId, LineItems.URI_PART);
		} else {
			listUri = URIUtil.format(parentSelfUri, Components.URI_PART);
		}

		String selfUri = URIUtil.format(listUri, purchaseLineItemId);
		Self self =	SelfFactory.createSelf(selfUri);

		String purchaseUri = URIUtil.format(resourceServerName, scope, purchaseId);
		ResourceLink purchaseLink = ResourceLinkFactory.createNoRev(purchaseUri, PurchasesMediaTypes.PURCHASE.id(),
				PurchaseResourceRels.PURCHASE_REL);

		ResourceLink listLink = ElementListFactory.createListWithoutElement(listUri, CollectionsMediaTypes.LINKS.id());

		PurchaseLineItemEntity updatedLineItemEntity = PurchaseLineItemEntity.builderFrom(purchaseLineItemEntity)
				.withLineItemId(purchaseLineItemId)
				.withPurchaseId(purchaseId)
				.build();

		return ResourceState.Builder.create(updatedLineItemEntity)
				.addingLinks(purchaseLink, listLink)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.withScope(scope)
				.build();
	}
}
