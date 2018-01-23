/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.OrderShipmentTaxTransformer;

/**
 * Test cases for {@link ShipmentTaxesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTaxesLookupStrategyImplTest {

	private static final String ORDER_GUID = "test-order";
	private static final String SHIPMENT_GUID = "test-shipment";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock 
	private OrderShipmentTaxTransformer taxTransformer;
	@Mock
	private TaxesEntity mockTaxesEntity;
	
	@InjectMocks
	private ShipmentTaxesLookupStrategyImpl shipmentTaxesLookupStrategy;
	
	@Before
	public void setUp() {
		
		PhysicalOrderShipment orderShipment = mock(PhysicalOrderShipment.class);
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(ExecutionResultFactory.createReadOK(orderShipment));
		when(resourceOperationContext.getSubject()).thenReturn(mock(Subject.class));
		when(taxTransformer.transformToEntity(eq(orderShipment), any(Locale.class))).thenReturn(mockTaxesEntity);
	}

	@Test
	public void testGetTaxesWhenOrderShipmentNotFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(
				ExecutionResultFactory.<PhysicalOrderShipment> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentTaxesLookupStrategy.getTaxes(ORDER_GUID, SHIPMENT_GUID);
	}

	@Test
	public void testGetTaxesForSuccess() {

		ExecutionResult<TaxesEntity> executionResult = shipmentTaxesLookupStrategy.getTaxes(ORDER_GUID, SHIPMENT_GUID);

		assertExecutionResult(executionResult)
			.isSuccessful()
			.resourceStatus(ResourceStatus.READ_OK)
			.data(mockTaxesEntity);
	}

}
