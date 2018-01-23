/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.impl;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.stocks.StockEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.stock.integration.StockLookupStrategy;
import com.elasticpath.rest.resource.stock.rel.StockResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;

/**
 * Processes the resource operation on Stock.
 */
@Singleton
@Named("stockResourceOperator")
@Path({ ResourceName.PATH_PART })
public class StockResourceOperatorImpl implements ResourceOperator {

	private final StockLookupStrategy stockLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param stockLookupStrategy for reading
	 */
	@Inject
	protected StockResourceOperatorImpl(
			@Named("stockLookupStrategy")
			final StockLookupStrategy stockLookupStrategy) {
		this.stockLookupStrategy = stockLookupStrategy;
	}

	/**
	 * Handles the read operations for the stock resource.
	 *
	 * @param item the item for which stock is requested
	 * @param operation the resource operation being processed
	 * @return the operation result
	 */
	@Path({ SingleResourceUri.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@SingleResourceUri
			final ResourceState<ItemEntity> item,
			final ResourceOperation operation) {

		String itemId = item.getEntity().getItemId();
		String scope = item.getScope();

		StockEntity stockEntity = Assign.ifSuccessful(stockLookupStrategy.getStockByItemId(scope, itemId));
		Self self = createSelf(operation.getUri());

		String itemUri = item.getSelf().getUri();
		ResourceLink itemLink = ResourceLinkFactory.create(itemUri, ItemsMediaTypes.ITEM.id(),
				StockResourceRels.ITEM_REL, StockResourceRels.STOCK_REV);

		ResourceState<StockEntity> stockRepresentation = ResourceState.Builder.create(stockEntity)
				.withScope(scope)
				.withSelf(self)
				.addingLinks(itemLink)
				.build();

		return OperationResultFactory.createReadOK(stockRepresentation, operation);
	}

}
