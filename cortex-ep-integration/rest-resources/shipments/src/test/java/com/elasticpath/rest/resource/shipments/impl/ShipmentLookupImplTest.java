/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

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
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.integration.ShipmentLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Test class for {@link ShipmentLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLookupImplTest {

	private static final String SCOPE = "scope";

	private static final String SHIPMENT_ID = "12345";

	private static final String PURCHASE_ID = "5434435";

	private static final Collection<String> SHIPMENT_IDS = Arrays.asList("1", "2", "3");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentLookupStrategy mockShipmentLookupStrategy;

	@Mock
	private TransformToResourceState<ShipmentEntity, ShipmentEntity> mockShipmentTransformer;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, PurchaseEntity> shipmentLinksTransformer;

	@InjectMocks
	private ShipmentLookupImpl shipmentLookup;

	@Mock
	private ShipmentEntity expectedShippingEntity;

	@Mock
	private ResourceState<LinksEntity> expectedLinksRepresentation;

	private final ResourceState<PurchaseEntity> purchaseRepresentation = createPurchase();

	private final ResourceState<ShipmentEntity> expectedShipmentRepresentation = createShipmentRepresentation();

	@Before
	public void setUp() {
		when(mockShipmentTransformer.transform(SCOPE, expectedShippingEntity)).thenReturn(expectedShipmentRepresentation);
		when(shipmentLinksTransformer.transform(SHIPMENT_IDS, purchaseRepresentation)).thenReturn(expectedLinksRepresentation);
	}

	@Test
	public void testGetShipmentForPurchaseWhenSuccessful() {
		when(mockShipmentLookupStrategy.find(any(ShipmentEntity.class))).thenReturn(
				ExecutionResultFactory.createReadOK(expectedShippingEntity));

		ExecutionResult<ResourceState<ShipmentEntity>> result = shipmentLookup.getShipmentForPurchase(purchaseRepresentation, SHIPMENT_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedShipmentRepresentation);
	}

	@Test
	public void testGetShipmentForPurchaseWhenNotFoundFailureResultFromStrategy() {
		when(mockShipmentLookupStrategy.find(any(ShipmentEntity.class))).thenReturn(
				ExecutionResultFactory.<ShipmentEntity> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLookup.getShipmentForPurchase(purchaseRepresentation, SHIPMENT_ID);
	}

	@Test
	public void testGetShipmentForPurchaseWhenStateFailureResultFromStrategy() {
		when(mockShipmentLookupStrategy.find(any(ShipmentEntity.class))).thenReturn(
				ExecutionResultFactory.<ShipmentEntity> createStateFailure("failure"));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		shipmentLookup.getShipmentForPurchase(purchaseRepresentation, SHIPMENT_ID);
	}

	@Test
	public void testGetAllShipmentsForPurchaseWhenSuccessful() {

		when(mockShipmentLookupStrategy.findShipmentIds(anyString(), anyString())).thenReturn(ExecutionResultFactory.createReadOK(SHIPMENT_IDS));
		ExecutionResult<ResourceState<LinksEntity>> result = shipmentLookup.getShipmentsForPurchase(purchaseRepresentation);

		assertExecutionResult(result).isSuccessful().resourceStatus(ResourceStatus.READ_OK).data(expectedLinksRepresentation);
	}

	@Test
	public void testGetAllShipmentsForPurchaseWhenNotFoundFailureResultFromStrategy() {

		when(mockShipmentLookupStrategy.findShipmentIds(anyString(), anyString())).thenReturn(
				ExecutionResultFactory.<Collection<String>> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLookup.getShipmentsForPurchase(purchaseRepresentation);
	}

	private ResourceState<PurchaseEntity> createPurchase() {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withPurchaseId(PURCHASE_ID)
				.build();
		return ResourceState.Builder.create(purchaseEntity)
				.withScope(SCOPE)
				.build();
	}

	private ResourceState<ShipmentEntity> createShipmentRepresentation() {
		return ResourceState.Builder.create(ShipmentEntity.builder().build())
				.withScope(SCOPE)
				.build();
	}
}
