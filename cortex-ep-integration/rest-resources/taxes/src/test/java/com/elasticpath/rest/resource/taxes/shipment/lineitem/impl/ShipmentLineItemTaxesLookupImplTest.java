/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.resource.taxes.impl.AbstractTaxesLookupImplContractTest;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.ShipmentLineItemTaxesLookupStrategy;

/**
 * Unit test for {@link ShipmentLineItemTaxesLookupImpl}.
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "unused"})
public class ShipmentLineItemTaxesLookupImplTest extends AbstractTaxesLookupImplContractTest<ShipmentLineItemEntity> {

	private static final String DECODED_PURCHASE_ID = "testPurchaseId";
	private static final String ENCODED_PURCHASE_ID = Base32Util.encode(DECODED_PURCHASE_ID);
	private static final String DECODED_SHIPMENT_ID = "testShipmentId";
	private static final String ENCODED_SHIPMENT_ID = Base32Util.encode(DECODED_SHIPMENT_ID);
	private static final String DECODED_LINE_ITEM_ID = "testLineItemId";

	@Mock
	private ShipmentLineItemTaxesLookupStrategy lookupStrategy;

	@InjectMocks
	private ShipmentLineItemTaxesLookupImpl taxesLookup;

	@Override
	protected void arrangeLookupStrategyToReturnTaxResult(final ExecutionResult<TaxesEntity> taxesEntityResult) {
		when(lookupStrategy.getTaxes(SCOPE, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID, DECODED_LINE_ITEM_ID)).thenReturn(taxesEntityResult);
	}

	@Override
	protected ShipmentLineItemEntity createTestInputEntity() {
		final ShipmentLineItemEntity mockLineItem = mock(ShipmentLineItemEntity.class);
		when(mockLineItem.getPurchaseId()).thenReturn(DECODED_PURCHASE_ID); // ENCODED_PURCHASE_ID);
		when(mockLineItem.getShipmentId()).thenReturn(DECODED_SHIPMENT_ID); // ENCODED_SHIPMENT_ID);
		when(mockLineItem.getLineItemId()).thenReturn(DECODED_LINE_ITEM_ID); // ENCODED_SHIPMENT_ID);

		return mockLineItem;
	}

	@Override
	protected TaxesLookup<ShipmentLineItemEntity> createTaxesLookupUnderTest() {
		return taxesLookup;
	}

}
