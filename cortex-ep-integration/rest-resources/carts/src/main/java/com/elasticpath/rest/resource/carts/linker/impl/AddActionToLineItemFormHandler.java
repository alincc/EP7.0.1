/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.resource.dispatch.linker.FormLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Add line item to cart {@link com.elasticpath.rest.schema.ResourceLink} to line item handler.
 */
@Singleton
@Named("addActionToLineItemFormHandler")
public class AddActionToLineItemFormHandler implements FormLinkHandler<LineItemEntity> {
	private final ItemsUriBuilderFactory itemsUriBuilderFactory;
	private final CartsUriBuilderFactory cartsUriBuilderFactory;
	private final LineItemLookup lineItemLookup;

	/**
	 * Constructor.
	 *
	 * @param itemsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory}
	 * @param cartsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartsUriBuilderFactory}
	 * @param lineItemLookup the {@link com.elasticpath.rest.resource.carts.lineitems.LineItemLookup}
	 */
	@Inject
	public AddActionToLineItemFormHandler(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory,
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory,
			@Named("lineItemLookup")
			final LineItemLookup lineItemLookup) {
		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
		this.lineItemLookup = lineItemLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItemEntity) {
		String itemId = lineItemEntity.getEntity()
									.getItemId();
		String scope = lineItemEntity.getScope();
		ExecutionResult<Boolean> itemPurchasableResult = lineItemLookup.isItemPurchasable(scope, itemId);

		if (itemPurchasableResult.isSuccessful() && itemPurchasableResult.getData()) {
			String itemUri = itemsUriBuilderFactory.get()
												.setScope(scope)
												.setItemId(itemId)
												.build();
			String cartLineItemUri = cartsUriBuilderFactory
					.get()
					.setCartId(Default.URI_PART)
					.setScope(scope)
					.setItemUri(itemUri)
					.build();
			return Collections.singleton(ResourceLinkFactory.createUriRel(cartLineItemUri,
																		LineItemRepresentationRels.ADD_TO_DEFAULT_CART_ACTION_REL));
		} else {
			return Collections.emptyList();
		}
	}
}
