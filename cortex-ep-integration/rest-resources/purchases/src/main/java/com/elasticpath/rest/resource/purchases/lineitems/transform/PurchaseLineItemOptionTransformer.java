/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.transform;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Transforms a into a {@link com.elasticpath.rest.schema.ResourceState}.
 */
@Singleton
@Named("purchaseLineItemOptionTransformer")
public class PurchaseLineItemOptionTransformer {

	/**
	 * Transform purchase line item option to representation.
	 *
	 * @param purchaseLineItemOptionEntity the purchase line item option dto
	 * @param lineItemUri the line item uri
	 * @return the purchase line item option representation
	 */
	public ResourceState<PurchaseLineItemOptionEntity> transformToRepresentation(
			final PurchaseLineItemOptionEntity purchaseLineItemOptionEntity,
			final String lineItemUri) {


		String optionId = Base32Util.encode(purchaseLineItemOptionEntity.getOptionId());
		String optionValueId = Base32Util.encode(purchaseLineItemOptionEntity.getSelectedValueId());
		String optionsUri = URIUtil.format(lineItemUri, Options.URI_PART);
		String selfUri = URIUtil.format(optionsUri, optionId);
		Self self = SelfFactory.createSelf(selfUri);
		String optionValueUri = URIUtil.format(selfUri, Values.URI_PART, optionValueId);

		ResourceLink optionsLink = ResourceLinkFactory.createNoRev(optionsUri, CollectionsMediaTypes.LINKS.id(), ListElementRels.LIST);
		ResourceLink optionValueLink = ResourceLinkFactory.create(optionValueUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM_OPTION_VALUE.id(),
				PurchaseLineItemsResourceRels.VALUE_REL, PurchaseLineItemsResourceRels.OPTION_REV);

		return ResourceState.Builder.create(purchaseLineItemOptionEntity)
				.withSelf(self)
				.withResourceInfo(ResourceInfo.builder()
					.withMaxAge(PurchaseResourceConstants.MAX_AGE)
					.build())
				.addingLinks(optionValueLink, optionsLink)
				.build();
	}
}
