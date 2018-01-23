/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.shipments.lineitems.integration.ShipmentLineItemsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Test class for {@link ShipmentLineItemsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemLookupImplTest {

	private static final String FAILURE_MESSAGE = "failure";
	private static final String SCOPE = "testScope";
	private static final String LINE_ITEM_ID = "123=";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentLineItemsLookupStrategy mockShipmentLineItemLookupStrategy;
	@Mock
	private TransformToResourceState<ShipmentLineItemEntity, ShipmentLineItemEntity> shipmentLineItemTransformer;
	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, ShipmentEntity> shipmentLineItemLinksTransformer;
	@Mock
	private Collection<String> mockShipmentLineItemIDs;
	@Mock
	private ShipmentEntity mockShipmentEntity;
	@Mock
	private ResourceState<ShipmentEntity> mockShipment;
	@Mock
	private ResourceState<ShipmentLineItemEntity> mockShipmentLineItem;
	@Mock
	private ShipmentLineItemEntity mockShipmentLineItemEntity;
	@Mock
	private ResourceState<LinksEntity> mockLinksRepresentation;
	@InjectMocks
	private ShipmentLineItemsLookupImpl shipmentLineItemsLookup;

	@Before
	public void setUp() {
		when(mockShipment.getEntity()).thenReturn(mockShipmentEntity);
		when(mockShipment.getScope()).thenReturn(SCOPE);
	}

	@Test
	public void testFindWhenSuccessful() {
		when(mockShipmentLineItemLookupStrategy.find(any(String.class), any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShipmentLineItemEntity));
		when(shipmentLineItemTransformer.transform(SCOPE, mockShipmentLineItemEntity))
				.thenReturn(mockShipmentLineItem);

		ExecutionResult<ResourceState<ShipmentLineItemEntity>> result = shipmentLineItemsLookup.find(mockShipment, LINE_ITEM_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockShipmentLineItem);
	}

	@Test
	public void testFindWhenNotFoundFailureResultFromStrategy() {
		when(mockShipmentLineItemLookupStrategy.find(any(String.class), any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemEntity> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemsLookup.find(mockShipment, LINE_ITEM_ID);
	}

	@Test
	public void testFindWhenStateFailureResultFromStrategy() {
		when(mockShipmentLineItemLookupStrategy.find(any(String.class), any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemEntity> createStateFailure(FAILURE_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		shipmentLineItemsLookup.find(mockShipment, LINE_ITEM_ID);
	}

	@Test
	public void testFindAllWhenSuccessful() {
		when(mockShipmentLineItemLookupStrategy.findLineItemIds(any(String.class), any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShipmentLineItemIDs));
		when(shipmentLineItemLinksTransformer.transform(mockShipmentLineItemIDs, mockShipment))
				.thenReturn(mockLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result = shipmentLineItemsLookup.findAll(mockShipment);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockLinksRepresentation);
	}

	@Test
	public void testFindAllWhenNotFoundFailureResultFromStrategy() {
		when(mockShipmentLineItemLookupStrategy.findLineItemIds(any(String.class), any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.<Collection<String>> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemsLookup.findAll(mockShipment);
	}

	@Test
	public void testFindAllWhenStateFailureResultFromStrategy() {
		when(mockShipmentLineItemLookupStrategy.findLineItemIds(any(String.class), any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.<Collection<String>> createStateFailure(FAILURE_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		shipmentLineItemsLookup.findAll(mockShipment);
	}
}
