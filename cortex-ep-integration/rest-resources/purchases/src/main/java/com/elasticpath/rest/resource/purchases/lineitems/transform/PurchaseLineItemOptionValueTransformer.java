/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.transform;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
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
 * Transforms line item option value information to a {@link com.elasticpath.rest.schema.ResourceState}.
 */
@Singleton
@Named("purchaseLineItemOptionValueTransformer")
public class PurchaseLineItemOptionValueTransformer {

	/**
	 * Transform to representation.
	 *
	 * @param lineItemUri the line item uri
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param optionValueDto the option value dto
	 * @return the purchase line item option value representation
	 */
	public ResourceState<PurchaseLineItemOptionValueEntity> transformToRepresentation(
			final String lineItemUri,
			final String optionId,
			final String valueId,
			final PurchaseLineItemOptionValueEntity optionValueDto) {

		String optionUri = URIUtil.format(lineItemUri, Options.URI_PART, optionId);
		ResourceLink optionLink = ResourceLinkFactory.createNoRev(optionUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM_OPTION.id(),
				PurchaseLineItemsResourceRels.OPTION_REL);

		String selfUri = URIUtil.format(optionUri, Values.URI_PART, valueId);
		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(optionValueDto)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.addingLinks(optionLink)
				.build();
	}
}
