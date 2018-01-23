/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.transform.ShipmentLineItemTaxesEntityTransformer;

/**
 * Unit tests for {@link ShipmentLineItemTaxesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemTaxesLookupStrategyImplTest {

	private static final String SCOPE = "scope";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SHIPMENT_ID = "shipmentId";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String SKU_CODE = "SKU_CODE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock private PhysicalOrderShipment mockOrderShipment;
	@Mock private TaxesEntity expectedTaxesEntity;

	@Mock private ResourceOperationContext resourceOperationContext;
	@Mock private ShipmentRepository shipmentRepository;
	@Mock private TaxDocumentRepository taxDocumentRepository;
	@Mock private ShipmentLineItemTaxesEntityTransformer transformer;

	@InjectMocks
	private ShipmentLineItemTaxesLookupStrategyImpl lookupStrategy;

	@Test
	public void testGetTaxesSuccessful() {

		arrangeSuccessFlow();

		ExecutionResult<TaxesEntity> result = lookupStrategy.getTaxes(SCOPE, PURCHASE_ID, SHIPMENT_ID, LINE_ITEM_ID);

		assertExecutionResult(result).isSuccessful().data(expectedTaxesEntity);
	}

	@Test
	public void testGetTaxesFailsWhenStoreCodeIsInvalid() {

		arrangeSuccessFlow();
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID)).thenReturn(ExecutionResultFactory.<PhysicalOrderShipment> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.getTaxes(SCOPE, PURCHASE_ID, SHIPMENT_ID, LINE_ITEM_ID);
	}

	@Test
	public void testGetTaxesFailsWhenShipmentIsNotFound() {

		arrangeSuccessFlow();
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID)).thenReturn(ExecutionResultFactory.<PhysicalOrderShipment>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.getTaxes(SCOPE, PURCHASE_ID, SHIPMENT_ID, LINE_ITEM_ID);
	}

	@Test
	public void testGetTaxesFailsWhenLineItemIsNotFound() {

		arrangeSuccessFlow();
		Set<OrderSku> orderSkus = Collections.singleton(createOrderSku("UNMATCHING_GUID", "ID"));
		when(mockOrderShipment.getShipmentOrderSkus()).thenReturn(orderSkus);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.getTaxes(SCOPE, PURCHASE_ID, SHIPMENT_ID, LINE_ITEM_ID);
	}

	@Test
	public void testGetTaxesFailsWhenTaxRecordsAreNotFound() {

		arrangeSuccessFlow();
		when(taxDocumentRepository.getTaxDocument(any(TaxDocumentId.class), eq(SKU_CODE))).thenReturn(
				ExecutionResultFactory.<Collection<TaxJournalRecord>> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.getTaxes(SCOPE, PURCHASE_ID, SHIPMENT_ID, LINE_ITEM_ID);
	}

	private void arrangeSuccessFlow() {

		Subject subject = mock(Subject.class);
		when(resourceOperationContext.getSubject()).thenReturn(subject);

		PhysicalOrderShipment mockOrderShipment = createOrderShipment();
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID)).thenReturn(createReadOK(mockOrderShipment));

		Collection<TaxJournalRecord> taxJournalRecords = Collections.emptySet();
		when(taxDocumentRepository.getTaxDocument(any(TaxDocumentId.class), eq(SKU_CODE))).thenReturn(createReadOK(taxJournalRecords));

		when(transformer.transform(any(OrderSku.class), eq(taxJournalRecords), any(Locale.class))).thenReturn(expectedTaxesEntity);
	}

	private PhysicalOrderShipment createOrderShipment() {

		when(mockOrderShipment.getTaxDocumentId()).thenReturn(mock(TaxDocumentId.class));

		Set<OrderSku> orderSkus = new HashSet<>(2);
		orderSkus.add(createOrderSku(SKU_CODE, LINE_ITEM_ID));
		orderSkus.add(createOrderSku("otherSkuCode", "otherLineItemId"));
		when(mockOrderShipment.getShipmentOrderSkus()).thenReturn(orderSkus);

		return mockOrderShipment;
	}

	private OrderSku createOrderSku(final String skuCode, final String lineItemId) {
		OrderSku orderSku = mock(OrderSku.class);
		when(orderSku.getSkuCode()).thenReturn(skuCode);
		when(orderSku.getGuid()).thenReturn(lineItemId);
		return orderSku;
	}
}
