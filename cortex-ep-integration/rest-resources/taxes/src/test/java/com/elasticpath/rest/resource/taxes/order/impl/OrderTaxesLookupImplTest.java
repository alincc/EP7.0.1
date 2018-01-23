/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.taxes.impl.TaxesUriBuilderImpl;
import com.elasticpath.rest.resource.taxes.order.integration.OrderTaxesLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Tests for {@link OrderTaxesLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderTaxesLookupImplTest {

	private static final String SCOPE = "testScope";
	private static final String DECODED_ORDER_ID = "testOrderId";
	private static final String ENCODED_ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String ORDER_URI = "/order-uri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderTaxesLookupStrategy orderTaxesLookupStrategy;
	@Mock
	private OrderEntity orderEntity;
	@Mock
	private TaxesEntity taxesEntity;
	@Mock
	private TaxesUriBuilderFactory taxesUriBuilderFactory;

	private ResourceState<OrderEntity> orderState;

	@InjectMocks
	private OrderTaxesLookupImpl orderTaxesLookupImpl;

	@Before
	public void setUp() {
		when(taxesUriBuilderFactory.get())
				.thenReturn(new TaxesUriBuilderImpl("taxes"));
	}

	@Test
	public void testGetTaxesSuccess() {
		arrangeResourceStates();
		ExecutionResult<TaxesEntity> taxesEntityResult = ExecutionResultFactory.createReadOK(taxesEntity);
		mockLookupStrategy(taxesEntityResult);

		ExecutionResult<ResourceState<TaxesEntity>> taxesResult = orderTaxesLookupImpl.getTaxes(orderState);

		AssertExecutionResult.assertExecutionResult(taxesResult)
				.isSuccessful()
				.data(expectedTaxesState());
	}

	@Test
	public void testGetTaxesWithLookupStrategyFailure() {
		arrangeResourceStates();
		ExecutionResult<TaxesEntity> taxesEntityResult = ExecutionResultFactory.createNotFound();
		mockLookupStrategy(taxesEntityResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderTaxesLookupImpl.getTaxes(orderState);
	}

	private void arrangeResourceStates() {
		when(orderEntity.getOrderId())
				.thenReturn(ENCODED_ORDER_ID);

		orderState = ResourceState.Builder
				.create(orderEntity)
				.withScope(SCOPE)
				.withSelf(createSelf(ORDER_URI))
				.build();
	}

	private void mockLookupStrategy(final ExecutionResult<TaxesEntity> taxesEntityResult) {
		when(orderTaxesLookupStrategy.getTaxes(SCOPE, DECODED_ORDER_ID))
				.thenReturn(taxesEntityResult);
	}

	private ResourceState<TaxesEntity> expectedTaxesState() {
		return ResourceState.Builder
				.create(taxesEntity)
				.withScope(SCOPE)
				.withSelf(createSelf("/taxes" + ORDER_URI))
				.build();
	}
}
