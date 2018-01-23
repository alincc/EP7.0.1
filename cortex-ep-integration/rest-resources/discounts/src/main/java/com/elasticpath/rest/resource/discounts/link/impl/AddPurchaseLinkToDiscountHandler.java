/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Add purchase link to discount {@link ResourceStateLinkHandler}.
 */
@Singleton
@Named("addPurchaseLinkToDiscountHandler")
public class AddPurchaseLinkToDiscountHandler implements ResourceStateLinkHandler<DiscountEntity> {

	private final PurchaseUriBuilderFactory purchaseUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param purchaseUriBuilderFactory the {@link PurchaseUriBuilderFactory}.
	 */
	@Inject
	public AddPurchaseLinkToDiscountHandler(
			@Named("purchaseUriBuilderFactory")
			final PurchaseUriBuilderFactory purchaseUriBuilderFactory) {
		this.purchaseUriBuilderFactory = purchaseUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<DiscountEntity> discount) {
		String purchaseId = discount.getEntity()
									.getPurchaseId();
		if (purchaseId != null) {
			String purchaseUri = purchaseUriBuilderFactory.get()
												.setPurchaseId(purchaseId)
												.setScope(discount.getScope())
												.build();
			return Collections.singleton(ResourceLinkFactory.create(purchaseUri, PurchasesMediaTypes.PURCHASE.id(),
									DiscountsResourceRels.PURCHASE_REL, DiscountsResourceRels.DISCOUNT_REV));
		}

		return Collections.emptyList();
	}
}
