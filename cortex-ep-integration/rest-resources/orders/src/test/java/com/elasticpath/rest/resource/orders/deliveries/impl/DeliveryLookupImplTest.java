/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.rest.ResourceStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.deliveries.transformer.DeliveryTransformer;
import com.elasticpath.rest.resource.orders.integration.deliveries.DeliveryLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests for {@link DeliveryLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DeliveryLookupImplTest {

	private static final String NOT_FOUND = "not found";
	private static final String DECODED_ORDER_ID = "DECODED_ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String SCOPE = "SCOPE";
	private static final String DECODED_DELIVERY_ID = "DECODED_DELIVERY_ID";
	private static final String DELIVERY_ID = Base32Util.encode(DECODED_DELIVERY_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private DeliveryTransformer deliveryTransformer;
	@Mock
	private DeliveryLookupStrategy deliveryLookupStrategy;

	@InjectMocks
	private DeliveryLookupImpl deliveryLookup;


	/**
	 * Tests getting the deliveries.
	 */
	@Test
	public void testGetDeliveryIds() {
		shouldGetDeliveryIdsWithResult(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(DECODED_DELIVERY_ID)));

		ExecutionResult<Collection<String>> deliveriesResult = deliveryLookup.getDeliveryIds(SCOPE, ORDER_ID);

		assertTrue("This should be a successful operation.", deliveriesResult.isSuccessful());
		assertThat("The deliveryIds should be the same.", deliveriesResult.getData(), Matchers.contains(DELIVERY_ID));
	}

	/**
	 * Tests getting the deliveries with failure from strategy.
	 */
	@Test
	public void testGetDeliveryIdsWithFailureFromStrategy() {
		shouldGetDeliveryIdsWithResult(ExecutionResultFactory.<Collection<String>>createNotFound(NOT_FOUND));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		deliveryLookup.getDeliveryIds(SCOPE, ORDER_ID);
	}

	/**
	 * Tests the find by id method.
	 */
	@Test
	public void testFindById() {
		DeliveryEntity deliveryEntity = DeliveryEntity.builder().build();
		ResourceState<DeliveryEntity> deliveryRepresentation = ResourceState.Builder.create(deliveryEntity).build();

		shouldFindByIdAndOrderIdWithResult(ExecutionResultFactory.createReadOK(deliveryEntity));
		shouldTransformToRepresentationWithResult(deliveryEntity, deliveryRepresentation);

		ExecutionResult<ResourceState<DeliveryEntity>> result = deliveryLookup.findByIdAndOrderId(SCOPE, ORDER_ID, DELIVERY_ID);

		assertTrue("This should be a successful result.", result.isSuccessful());
	}

	/**
	 * Tests the find by id method with strategy returning error.
	 */
	@Test
	public void testFindByIdWithStrategyReturningError() {
		shouldFindByIdAndOrderIdWithResult(ExecutionResultFactory.<DeliveryEntity>createNotFound(NOT_FOUND));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		deliveryLookup.findByIdAndOrderId(SCOPE, ORDER_ID, DELIVERY_ID);
	}

	private void shouldGetDeliveryIdsWithResult(final ExecutionResult<Collection<String>> result) {
		when(deliveryLookupStrategy.getDeliveryIds(SCOPE, DECODED_ORDER_ID))
				.thenReturn(result);
	}

	private void shouldFindByIdAndOrderIdWithResult(final ExecutionResult<DeliveryEntity> result) {
		when(deliveryLookupStrategy.findByIdAndOrderId(SCOPE, DECODED_ORDER_ID, DECODED_DELIVERY_ID))
				.thenReturn(result);
	}

	private void shouldTransformToRepresentationWithResult(final DeliveryEntity deliveryEntity,
			final ResourceState<DeliveryEntity> deliveryRepresentation) {
		when(deliveryTransformer.transformToRepresentation(SCOPE, deliveryEntity, ORDER_ID)).thenReturn(deliveryRepresentation);
	}
}
