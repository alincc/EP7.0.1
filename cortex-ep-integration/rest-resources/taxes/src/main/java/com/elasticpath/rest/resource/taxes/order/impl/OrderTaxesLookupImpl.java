/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.impl;

import static com.elasticpath.rest.chain.Assign.ifSuccessful;
import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.id.util.Base32Util.decode;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.resource.taxes.order.integration.OrderTaxesLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;

/**
 * TaxesLookup.
 */
@Named("orderTaxesLookup")
public class OrderTaxesLookupImpl implements TaxesLookup<OrderEntity> {

	private final OrderTaxesLookupStrategy orderTaxesLookupStrategy;
	private final TaxesUriBuilderFactory taxesUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param orderTaxesLookupStrategy a {@link OrderTaxesLookupStrategy}
	 * @param taxesUriBuilderFactory   {@link TaxesUriBuilderFactory}
	 */
	@Inject
	public OrderTaxesLookupImpl(
			@Named("orderTaxesLookupStrategy")
			final OrderTaxesLookupStrategy orderTaxesLookupStrategy,
			@Named("taxesUriBuilderFactory")
			final TaxesUriBuilderFactory taxesUriBuilderFactory) {
		this.orderTaxesLookupStrategy = orderTaxesLookupStrategy;
		this.taxesUriBuilderFactory = taxesUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<TaxesEntity>> getTaxes(final ResourceState<OrderEntity> orderState) {

		String scope = orderState.getScope();
		String orderId = decode(orderState.getEntity().getOrderId());
		TaxesEntity taxesEntity = ifSuccessful(orderTaxesLookupStrategy.getTaxes(scope, orderId));
		ResourceState<TaxesEntity> taxesState = resourceState(taxesEntity, scope, orderState.getSelf().getUri());

		return createReadOK(taxesState);
	}

	private ResourceState<TaxesEntity> resourceState(final TaxesEntity taxesEntity,
													final String scope,
													final String orderUri) {
		return ResourceState.Builder
				.create(taxesEntity)
				.withScope(scope)
				.withSelf(self(orderUri))
				.build();
	}

	private Self self(final String orderUri) {

		String taxesUri = taxesUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.build();
		return createSelf(taxesUri);
	}
}
