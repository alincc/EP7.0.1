/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds URIs for cart line items.
 */
public interface CartLineItemsUriBuilder extends ReadFromOtherUriBuilder<CartLineItemsUriBuilder> {


	/**
	 * Set the cart line item ID.
	 *
	 * @param lineItemId the cart line item ID.
	 * @return the builder
	 */
	CartLineItemsUriBuilder setLineItemId(String lineItemId);
}
