/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.transform.TotalTransformer;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Abstract contract test for {@link TotalLookup} implementations.
 *
 * @param <R> the representation type
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTotalLookupContractTest<R extends ResourceEntity> {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private TotalTransformer mockTotalTransformer;

	private TotalLookup<R> totalLookup;

	private ResourceState<R> testRepresentation;

	private final TotalEntity entity = TotalEntity.builder().build();

	private final ResourceState<TotalEntity> totalRepresentation = ResourceState.Builder.create(entity).build();

	protected final Self resourceSelf = SelfFactory.createSelf("/resourceUri");

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		testRepresentation = createRepresentation();
		totalLookup = createTotalLookupUnderTest();
	}

	/** */
	@Test
	public void testGetOrderTotal() {
		arrangeLookupToReturnTotals(entity);

		when(mockTotalTransformer.transform(entity, testRepresentation, getRel()))
				.thenReturn(totalRepresentation);

		ExecutionResult<ResourceState<TotalEntity>> result = totalLookup.getTotal(testRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.data(totalRepresentation);
	}

	/** */
	@Test
	public void testGetOrderTotalWithNoTotalFound() {

		arrangeLookupToReturnNotFound();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		totalLookup.getTotal(testRepresentation);
	}

	/**
	 * .
	 *
	 * @return the TotalLookup under test
	 */
	abstract TotalLookup<R> createTotalLookupUnderTest();

	/**
	 * .
	 *
	 * @return the representation
	 */
	abstract ResourceState<R> createRepresentation();

	/**
	 * .
	 *
	 * @param totalDto the total DTO
	 */
	abstract void arrangeLookupToReturnTotals(TotalEntity totalDto);

	/**
	 * .
	 */
	abstract void arrangeLookupToReturnNotFound();

	/**
	 * @return the Rel string
	 */
	abstract String getRel();

}