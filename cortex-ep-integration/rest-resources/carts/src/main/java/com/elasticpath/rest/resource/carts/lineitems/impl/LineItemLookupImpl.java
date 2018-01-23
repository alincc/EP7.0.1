/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Looks up line items.
 */
@Singleton
@Named("lineItemLookup")
public final class LineItemLookupImpl implements LineItemLookup {

	private final LineItemLookupStrategy lineItemLookupStrategy;
	private final TransformToResourceState<LineItemEntity, LineItemEntity> lineItemDetailsTransformer;


	/**
	 * Constructor.
	 *
	 * @param lineItemLookupStrategy the line item lookup strategy
	 * @param lineItemDetailsTransformer the cart line item transformer
	 */
	@Inject
	LineItemLookupImpl(
			@Named("lineItemLookupStrategy")
			final LineItemLookupStrategy lineItemLookupStrategy,
			@Named("lineItemDetailsTransformer")
			final TransformToResourceState<LineItemEntity, LineItemEntity> lineItemDetailsTransformer) {

		this.lineItemLookupStrategy = lineItemLookupStrategy;
		this.lineItemDetailsTransformer = lineItemDetailsTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<LineItemEntity>> find(final ResourceState<CartEntity> cartRepresentation,
																	final String lineItemId) {

		String decodedCartId = Base32Util.decode(cartRepresentation.getEntity().getCartId());
		String decodedLineItemId = Base32Util.decode(lineItemId);
		String scope = cartRepresentation.getScope();
		LineItemEntity lineItemEntity = Assign.ifSuccessful(lineItemLookupStrategy.getLineItem(scope, decodedCartId, decodedLineItemId));
		return ExecutionResultFactory.createReadOK(lineItemDetailsTransformer.transform(scope, lineItemEntity));
	}

	@Override
	public ExecutionResult<Collection<String>> findIdsForCart(final String cartId, final String scope) {

		String decodedCartId = Base32Util.decode(cartId);
		Collection<String> lineItemIds = Assign.ifSuccessful(lineItemLookupStrategy.getLineItemIdsForCart(scope, decodedCartId));
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(lineItemIds));
	}

	@Override
	public ExecutionResult<Boolean> isItemPurchasable(final String scope, final String itemId) {
		return lineItemLookupStrategy.isItemPurchasable(scope, itemId);
	}
}
