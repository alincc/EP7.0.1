/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.transform.TotalTransformer;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implements {@link TotalLookup} for {@link LineItemEntity} by connecting to core.
 */
@Named("lineItemTotalLookup")
public class LineItemTotalLookupImpl implements TotalLookup<LineItemEntity> {

	private final TotalLookupStrategy totalLookupStrategy;

	private final TotalTransformer totalTransformer;

	/**
	 * Constructor.
	 *
	 * @param totalLookupStrategy the total lookup strategy
	 * @param totalTransformer the total transformer
	 */
	@Inject
	LineItemTotalLookupImpl(@Named("totalLookupStrategy")
	final TotalLookupStrategy totalLookupStrategy, @Named("totalTransformer")
	final TotalTransformer totalTransformer) {

		this.totalLookupStrategy = totalLookupStrategy;
		this.totalTransformer = totalTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<TotalEntity>> getTotal(final ResourceState<LineItemEntity> cartLineItemRepresentation) {
		final LineItemEntity lineItemEntity = cartLineItemRepresentation.getEntity();
		String cartId = lineItemEntity.getCartId();
		String lineItemId = lineItemEntity.getLineItemId();
		String scope = cartLineItemRepresentation.getScope();
		String decodedCartId = Base32Util.decode(cartId);
		String decodedLineItemId = Base32Util.decode(lineItemId);

		ExecutionResult<TotalEntity> lineItemTotalResult = totalLookupStrategy.getLineItemTotal(scope, decodedCartId, decodedLineItemId);
		return processResult(cartLineItemRepresentation, TotalResourceRels.LINE_ITEM_REL, lineItemTotalResult);
	}

	private ExecutionResult<ResourceState<TotalEntity>> processResult(final ResourceState resourceState, final String resourceRel,
			final ExecutionResult<TotalEntity> totalResult) {

		TotalEntity totalDto = Assign.ifSuccessful(totalResult);
		ResourceState<TotalEntity> total = totalTransformer.transform(totalDto, resourceState, resourceRel);

		return ExecutionResultFactory.createReadOK(total);
	}

}