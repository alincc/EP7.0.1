/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

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
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.resource.shipments.lineitems.option.integration.ShipmentLineItemOptionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Test class for {@link ShipmentLineItemOptionLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionLookupImplTest {

	private static final String FAILURE_MESSAGE = "failure";
	private static final String OPTION_ID = "123=";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentLineItemOptionsLookupStrategy mockShipmentLineItemOptionLookupStrategy;
	@Mock
	private TransformRfoToResourceState<ShipmentLineItemOptionEntity, ShipmentLineItemOptionEntity, ShipmentLineItemEntity>
			shipmentLineItemOptionTransformer;
	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, ShipmentLineItemEntity> shipmentLineItemOptionLinksTransformer;
	@Mock
	private TransformRfoToResourceState<ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionEntity>
			shipmentLineItemOptionValueTransformer;
	@Mock
	private Collection<String> mockShipmentLineItemOptionIDs;
	@Mock
	private ResourceState<ShipmentLineItemOptionEntity> mockOption;
	@Mock
	private ShipmentLineItemOptionEntity mockOptionEntity;
	@Mock
	private ResourceState<ShipmentLineItemEntity> mockShipmentLineItem;
	@Mock
	private ResourceState<ShipmentLineItemOptionValueEntity> mockOptionValue;
	@Mock
	private ShipmentLineItemOptionValueEntity mockOptionValueEntity;
	@Mock
	private ResourceState<LinksEntity> mockLinksRepresentation;
	private ShipmentLineItemOptionLookupImpl shipmentLineItemOptionLookup;

	@Before
	public void setUp() {
		shipmentLineItemOptionLookup = new ShipmentLineItemOptionLookupImpl(mockShipmentLineItemOptionLookupStrategy,
				shipmentLineItemOptionTransformer, shipmentLineItemOptionLinksTransformer, shipmentLineItemOptionValueTransformer);
		when(mockOption.getEntity()).thenReturn(mockOptionEntity);
		when(mockOptionValue.getEntity()).thenReturn(mockOptionValueEntity);
		when(mockShipmentLineItem.getEntity()).thenReturn(mock(ShipmentLineItemEntity.class));
	}

	@Test
	public void testFindWhenSuccessful() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOption(any(ShipmentLineItemEntity.class), anyString()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockOptionEntity));
		when(shipmentLineItemOptionTransformer.transform(mockOptionEntity, mockShipmentLineItem))
				.thenReturn(mockOption);

		ExecutionResult<ResourceState<ShipmentLineItemOptionEntity>> result = shipmentLineItemOptionLookup.find(mockShipmentLineItem, OPTION_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockOption);
	}

	@Test
	public void testFindWhenNotFoundFailureResultFromStrategy() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOption(any(ShipmentLineItemEntity.class), anyString()))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemOptionEntity> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemOptionLookup.find(mockShipmentLineItem, OPTION_ID);
	}

	@Test
	public void testFindWhenStateFailureResultFromStrategy() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOption(any(ShipmentLineItemEntity.class), anyString()))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemOptionEntity> createStateFailure(FAILURE_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		shipmentLineItemOptionLookup.find(mockShipmentLineItem, OPTION_ID);
	}

	@Test
	public void testFindAllWhenSuccessful() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOptionIds(any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShipmentLineItemOptionIDs));
		when(shipmentLineItemOptionLinksTransformer.transform(mockShipmentLineItemOptionIDs, mockShipmentLineItem))
				.thenReturn(mockLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result = shipmentLineItemOptionLookup.findAll(mockShipmentLineItem);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockLinksRepresentation);
	}

	@Test
	public void testFindAllWhenNotFoundFailureResultFromStrategy() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOptionIds(any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.<Collection<String>> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemOptionLookup.findAll(mockShipmentLineItem);
	}

	@Test
	public void testFindAllWhenStateFailureResultFromStrategy() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOptionIds(any(ShipmentLineItemEntity.class)))
				.thenReturn(ExecutionResultFactory.<Collection<String>> createStateFailure(FAILURE_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		shipmentLineItemOptionLookup.findAll(mockShipmentLineItem);
	}

	@Test
	public void testFindAllComponentsWhenSuccessful() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOptionValue(any(ShipmentLineItemOptionEntity.class),	any(String.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(mockOptionValueEntity));
		when(shipmentLineItemOptionValueTransformer.transform(mockOptionValueEntity, mockOption))
				.thenReturn(mockOptionValue);

		ExecutionResult<ResourceState<ShipmentLineItemOptionValueEntity>> result =
				shipmentLineItemOptionLookup.findOptionValues(mockOption, OPTION_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockOptionValue);
	}

	@Test
	public void testFindAllComponentsWhenNotFoundFailureResultFromStrategy() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOptionValue(any(ShipmentLineItemOptionEntity.class),	any(String.class)))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemOptionValueEntity> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemOptionLookup.findOptionValues(mockOption, OPTION_ID);
	}

	@Test
	public void testFindAllComponentsWhenStateFailureResultFromStrategy() {
		when(mockShipmentLineItemOptionLookupStrategy.findLineItemOptionValue(any(ShipmentLineItemOptionEntity.class),	any(String.class)))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemOptionValueEntity> createStateFailure(FAILURE_MESSAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		shipmentLineItemOptionLookup.findOptionValues(mockOption, OPTION_ID);
	}
}
