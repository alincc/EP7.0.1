/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.impl;

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
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.taxes.impl.TaxesUriBuilderImpl;
import com.elasticpath.rest.resource.taxes.shipment.integration.ShipmentTaxesLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Tests for {@link ShipmentTaxesLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTaxesLookupImplTest {

	private static final String SCOPE = "testScope";
	private static final String DECODED_PURCHASE_ID = "testPurchaseId";
	private static final String DECODED_SHIPMENT_ID = "testShipmentId";
	private static final String SHIPMENT_URI = "/shipment-uri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentTaxesLookupStrategy shipmentTaxesLookupStrategy;
	@Mock
	private ShipmentEntity shipmentEntity;
	@Mock
	private TaxesEntity taxesEntity;

	private ResourceState<ShipmentEntity> shipmentState;

	@Mock
	private TaxesUriBuilderFactory taxesUriBuilderFactory;

	@InjectMocks
	private ShipmentTaxesLookupImpl shipmentTaxesLookupImpl;

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

		ExecutionResult<ResourceState<TaxesEntity>> taxesResult = shipmentTaxesLookupImpl.getTaxes(shipmentState);

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

		shipmentTaxesLookupImpl.getTaxes(shipmentState);
	}

	private void arrangeResourceStates() {
		when(shipmentEntity.getPurchaseId())
				.thenReturn(DECODED_PURCHASE_ID);
		when(shipmentEntity.getShipmentId())
				.thenReturn(DECODED_SHIPMENT_ID);
		shipmentState = ResourceState.Builder
				.create(shipmentEntity)
				.withScope(SCOPE)
				.withSelf(createSelf(SHIPMENT_URI))
				.build();
	}

	private void mockLookupStrategy(final ExecutionResult<TaxesEntity> taxesEntityResult) {
		when(shipmentTaxesLookupStrategy.getTaxes(DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID))
				.thenReturn(taxesEntityResult);
	}

	private ResourceState<TaxesEntity> expectedTaxesState() {
		return ResourceState.Builder
				.create(taxesEntity)
				.withSelf(createSelf("/taxes" + SHIPMENT_URI))
				.build();
	}
}
