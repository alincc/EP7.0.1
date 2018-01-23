/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
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
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;

/**
 * @param <T> the type of <@link ResourceEntity> handled by the TaxesLookup under test
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTaxesLookupImplContractTest<T extends ResourceEntity> {

	/** */
	protected static final String SCOPE = "testScope";
	public static final String URI = "/uri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private TaxesEntity taxesEntity;

	@Mock
	private TaxesUriBuilderFactory taxesUriBuilderFactory;

	@Before
	public void setUp() {
		when(taxesUriBuilderFactory.get())
				.thenReturn(new TaxesUriBuilderImpl("taxes"));
	}

	@Test
	public void testGetTaxesSuccessfully() {

		ResourceState<T> testInputResourceState = createTestInputResourceState();
		arrangeLookupStrategyToReturnTaxResult(ExecutionResultFactory.createReadOK(taxesEntity));

		ExecutionResult<ResourceState<TaxesEntity>> taxesResult = createTaxesLookupUnderTest().getTaxes(testInputResourceState);

		assertExecutionResult(taxesResult)
			.isSuccessful()
			.data(expectedTaxesState());
	}

	@Test
	public void testGetTaxesWithLookupStrategyFailure() {

		ResourceState<T> testInputResourceState = createTestInputResourceState();
		arrangeLookupStrategyToReturnTaxResult(ExecutionResultFactory.<TaxesEntity> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		createTaxesLookupUnderTest().getTaxes(testInputResourceState);
	}
	
	private ResourceState<T> createTestInputResourceState() {
		T inputEntity = createTestInputEntity();

		return ResourceState.Builder
				.create(inputEntity)
				.withScope(SCOPE)
				.withSelf(createSelf(URI))
				.build();
	}

	private ResourceState<TaxesEntity> expectedTaxesState() {
		return ResourceState.Builder
				.create(taxesEntity)
				.withSelf(createSelf("/taxes" + URI))
				.build();
	}

	/**
	 * @return the {@link TaxesLookup} under test.
	 */
	protected abstract TaxesLookup<T> createTaxesLookupUnderTest();
	
	/**
	 * @param taxesEntityResult the taxesEntityResult to be returned by the lookup strategy
	 */
	protected abstract void arrangeLookupStrategyToReturnTaxResult(ExecutionResult<TaxesEntity> taxesEntityResult);

	
	protected abstract T createTestInputEntity();

}
