/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * A factory to create ResourceLinks for Purchases.
 */
@Singleton
@Named("purchaseLinkFactory")
public final class PurchasesResourceLinkFactory {

	/**
	 * Creates a self link.
	 *
	 * @param uri the uri in the link
	 * @return a fully initialized self link.
	 */

	public Self createSelf(final String uri) {
		return SelfFactory.createSelf(uri);
	}

	/**
	 * Creates a self link.
	 *
	 * @param resourceName the resource root name
	 * @param scope the scope
	 * @param purchaseId the purchase ID
	 * @return a fully initialized self link.
	 */
	public Self createSelf(final String resourceName, final String scope, final String purchaseId) {
		return createSelf(URIUtil.format(resourceName, scope, purchaseId));
	}

	/**
	 * Creates a link to the purchase form.
	 *
	 * @param resourceName resource root name.
	 * @param orderUri the purchase URI.
	 * @return A resource link to the purchase form.
	 */
	public ResourceLink createPurchaseFormResourceLink(final String resourceName, final String orderUri) {
		return ResourceLinkFactory.createNoRev(
				URIUtil.format(resourceName, orderUri, Form.URI_PART),
				PurchasesMediaTypes.PURCHASE.id(),
				PurchaseResourceRels.PURCHASE_FORM_REL);
	}

	/**
	 * Creates a Link to follow to purchase an order.
	 *
	 * @param resourceName the resource name
	 * @param orderUri the order uri
	 * @return the link.
	 */
	public ResourceLink createSubmitOrderLink(final String resourceName, final String orderUri) {
		String uri = URIUtil.format(resourceName, orderUri);
		return ResourceLinkFactory.createUriRel(
				uri,
				PurchaseResourceRels.SUBMIT_ORDER_ACTION_REL);
	}
}
