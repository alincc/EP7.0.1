/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.ShipmentLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests {@link ShipmentResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentResourceOperatorImplTest {

	private static final String SHIPMENT_ID = "SHIPMENT_ID";
	private static final String PURCHAE_ID = "PurchaseId=";
	private static final String ENCODED_PURCHAE_ID = Base32Util.encode(PURCHAE_ID);
	private static final String ENCODED_SHIPMENT_ID = Base32Util.encode(SHIPMENT_ID);

	private static final String TEST_URI = "/testuri";

	private static final ResourceOperation READ_FOR_PURCHASE_OPERATION = TestResourceOperationFactory.createRead(TEST_URI);
	private final PurchaseEntity purchaseEntity = PurchaseEntity.builder().withPurchaseId(ENCODED_PURCHAE_ID).build();
	private final ResourceState<PurchaseEntity> purchase = ResourceState.Builder.create(purchaseEntity).build();
	@Mock
	private ShipmentLookup mockShipmentLookup;
	@InjectMocks
	private ShipmentResourceOperatorImpl resourceOperator;
	@Mock
	private ResourceState<ShipmentEntity> mockShipment;
	@Mock
	private ResourceState<LinksEntity> linksResource;

	@Test
	public void testProcessReadShipmentWhenLookupReturnsReadOkay() {
		when(mockShipmentLookup.getShipmentForPurchase(Matchers.<ResourceState<PurchaseEntity>>any(), eq(SHIPMENT_ID))).thenReturn(
				ExecutionResultFactory.createReadOK(mockShipment));

		OperationResult result = resourceOperator.processRead(purchase, ENCODED_SHIPMENT_ID, READ_FOR_PURCHASE_OPERATION);

		assertOperationResult(result)
				.resourceState(mockShipment)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShipmentWhenLookupReturnsNotFound() {
		when(mockShipmentLookup.getShipmentForPurchase(Matchers.<ResourceState<PurchaseEntity>>any(), eq(SHIPMENT_ID))).thenReturn(
				ExecutionResultFactory.<ResourceState<ShipmentEntity>> createNotFound());

		OperationResult result = resourceOperator.processRead(purchase, ENCODED_SHIPMENT_ID, READ_FOR_PURCHASE_OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShipmentWhenLookupReturnsServerError() {
		when(mockShipmentLookup.getShipmentForPurchase(Matchers.<ResourceState<PurchaseEntity>>any(), eq(SHIPMENT_ID))).thenReturn(
				ExecutionResultFactory.<ResourceState<ShipmentEntity>> createServerError("Server Error"));

		OperationResult result = resourceOperator.processRead(purchase, ENCODED_SHIPMENT_ID, READ_FOR_PURCHASE_OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessReadAllShipmentsWhenLookupReturnsReadOkay() {
		when(mockShipmentLookup.getShipmentsForPurchase(Matchers.<ResourceState<PurchaseEntity>>any())).thenReturn(
				ExecutionResultFactory.createReadOK(linksResource));

		OperationResult result = resourceOperator.processReadShipments(purchase, READ_FOR_PURCHASE_OPERATION);

		assertOperationResult(result)
				.resourceState(linksResource)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadAllShipmentsWhenLookupReturnsServerError() {
		when(mockShipmentLookup.getShipmentsForPurchase(Matchers.<ResourceState<PurchaseEntity>>any())).thenReturn(
				ExecutionResultFactory.<ResourceState<LinksEntity>>createServerError("Server Error"));

		OperationResult result = resourceOperator.processReadShipments(purchase, READ_FOR_PURCHASE_OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

}
