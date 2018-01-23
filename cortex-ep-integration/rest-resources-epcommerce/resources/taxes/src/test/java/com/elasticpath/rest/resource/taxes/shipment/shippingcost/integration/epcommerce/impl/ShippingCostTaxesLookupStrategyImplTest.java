/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.shippingcost.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;

/**
 * Tests for {@link ShippingCostTaxesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingCostTaxesLookupStrategyImplTest {

	private static final boolean FOR_SUCCESS = true;
	private static final boolean FOR_FAILURE = false;
	private static final BigDecimal RECORD_1_AMOUNT = BigDecimal.valueOf(10.10);
	private static final BigDecimal RECORD_2_AMOUNT = BigDecimal.valueOf(20.20);
	private static final BigDecimal TAX_TOTAL = RECORD_1_AMOUNT.add(RECORD_2_AMOUNT);
	private static final String RECORD_1_DISPLAY = "$10.10";
	private static final String RECORD_2_DISPLAY = "$20.20";
	private static final String TAX_TOTAL_DISPLAY = "$30.30";
	private static final String ZERO_DISPLAY = "$0.00";
	private static final String RECORD_1_NAME = "testTax1";
	private static final String RECORD_2_NAME = "testTax2";
	private static final String CURRENCY_CODE_CAD = "CAD";
	private static final String CURRENCY_CODE_USD = "USD";
	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance(LOCALE);
	private static final String DECODED_PURCHASE_ID = "testPurchaseId";
	private static final String DECODED_SHIPMENT_ID = "testShipmentId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock private ResourceOperationContext resourceOperationContext;
	@Mock private ShipmentRepository shipmentRepository;
	@Mock private TaxDocumentRepository taxDocumentRepository;
	@Mock private MoneyFormatter moneyFormatter;
	@Mock private PhysicalOrderShipment orderShipment;
	@Mock private TaxDocumentId taxDocumentId;
	@Mock private Subject subject;

	@InjectMocks private ShippingCostTaxesLookupStrategyImpl realLookupStrategyImpl;

	private ShippingCostTaxesLookupStrategyImpl lookupStrategyImpl;
	private final List<TaxJournalRecord> taxDocument = new LinkedList<>();

	/**
	 * Prepare common test conditions. This default setup is geared towards success cases, such that
	 * failure cases only need to mock a single dependency change.
	 */
	@Before
	public void setUp() {
		lookupStrategyImpl = Mockito.spy(realLookupStrategyImpl);
		Mockito.doReturn(LOCALE).when(lookupStrategyImpl).getLocaleForSubject(subject);
		mockResourceOperationContext();
		mockShipmentRepository(FOR_SUCCESS);
		mockTaxDocumentRepository(FOR_SUCCESS);
		mockOrderShipment(ShipmentType.PHYSICAL);
	}

	/**
	 * Test that the success case of {@link ShippingCostTaxesLookupStrategyImpl#getTaxes} correctly populates
	 * the {@link NamedCostEntity}s in {@link TaxesEntity}.
	 */
	@Test
	public void testGetTaxesSuccessCostPopulatedCorrectly() {
		mockMoneyTransformer();
		mockCorrectTaxRecords();

		ExecutionResult<TaxesEntity> taxesResult = lookupStrategyImpl.getTaxes(null, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID);

		assertTrue("Lookup should be successful.", taxesResult.isSuccessful());
		TaxesEntity taxesEntity = taxesResult.getData();
		Collection<NamedCostEntity> taxEntities = taxesEntity.getCost();
		assertEquals("Number of tax entities should equal number of tax records.", taxDocument.size(), taxEntities.size());
		Iterator<NamedCostEntity> taxEntityIterator = taxEntities.iterator();

		NamedCostEntity taxEntity1 = taxEntityIterator.next();
		assertEquals("Tax entity 1 should have tax record 1's currency code.", CURRENCY_CODE_CAD, taxEntity1.getCurrency());
		assertEquals("Tax entity 1 should have tax record 1's amount.", RECORD_1_AMOUNT, taxEntity1.getAmount());
		assertEquals("Tax entity 1 should have tax record 1's name/title.", RECORD_1_NAME, taxEntity1.getTitle());
		assertEquals("Tax entity 1 should transformed display amount.", RECORD_1_DISPLAY, taxEntity1.getDisplay());

		NamedCostEntity taxEntity2 = taxEntityIterator.next();
		assertEquals("Tax entity 2 should have tax record 2's currency code.", CURRENCY_CODE_CAD, taxEntity2.getCurrency());
		assertEquals("Tax entity 2 should have tax record 2's amount.", RECORD_2_AMOUNT, taxEntity2.getAmount());
		assertEquals("Tax entity 2 should have tax record 2's name/title.", RECORD_2_NAME, taxEntity2.getTitle());
		assertEquals("Tax entity 2 should transformed display amount.", RECORD_2_DISPLAY, taxEntity2.getDisplay());
	}

	/**
	 * Test that the success case of {@link ShippingCostTaxesLookupStrategyImpl#getTaxes} correctly populates
	 * the total in {@link TaxesEntity}.
	 */
	@Test
	public void testGetTaxesSuccessTotalPopulatedCorrectly() {
		mockCorrectTaxRecords();
		when(moneyFormatter.formatCurrency(CURRENCY, TAX_TOTAL, LOCALE)).thenReturn(TAX_TOTAL_DISPLAY);

		ExecutionResult<TaxesEntity> taxesResult = lookupStrategyImpl.getTaxes(null, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID);

		assertTrue("Lookup should be successful.", taxesResult.isSuccessful());
		TaxesEntity taxesDto = taxesResult.getData();
		CostEntity total = taxesDto.getTotal();
		assertEquals("Tax total's currency should be the input currency.", CURRENCY_CODE_CAD, total.getCurrency());
		assertEquals("Tax total's amount should be the input amount.", TAX_TOTAL, total.getAmount());
		assertEquals("Tax total's display should be the input display.", TAX_TOTAL_DISPLAY, total.getDisplay());
	}

	/**
	 * Test the case of {@link ShippingCostTaxesLookupStrategyImpl#getTaxes} when {@link TaxDocumentRepository} returns
	 * an empty collection.
	 */
	@Test
	public void testGetTaxesSuccessWithEmptyTaxDocument() {
		when(moneyFormatter.formatCurrency(CURRENCY, BigDecimal.ZERO, LOCALE)).thenReturn(ZERO_DISPLAY);

		ExecutionResult<TaxesEntity> taxesResult = lookupStrategyImpl.getTaxes(null, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID);

		assertTrue("Lookup should be a success.", taxesResult.isSuccessful());
		TaxesEntity taxesDto = taxesResult.getData();
		Collection<NamedCostEntity> taxEntities = taxesDto.getCost();
		assertTrue("Tax document should be empty", taxEntities.isEmpty());
		CostEntity total = taxesDto.getTotal();
		assertEquals("Tax total's currency should be the input currency.", CURRENCY_CODE_CAD, total.getCurrency());
		assertEquals("Tax total's amount should be the input amount.", BigDecimal.ZERO, total.getAmount());
		assertEquals("Tax total's display should be the input display.", ZERO_DISPLAY, total.getDisplay());
	}

	/**
	 * Test the case of {@link ShippingCostTaxesLookupStrategyImpl#getTaxes} when {@link TaxDocumentRepository} fails.
	 */
	@Test
	public void testGetTaxesWithTaxDocumentRepositoryFailure() {
		mockTaxDocumentRepository(FOR_FAILURE);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategyImpl.getTaxes(null, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID);
	}

	/**
	 * Test the case of {@link ShippingCostTaxesLookupStrategyImpl#getTaxes} when {@link ShipmentRepository} fails.
	 */
	@Test
	public void testGetTaxesWithShipmentRepositoryFailure() {
		mockShipmentRepository(FOR_FAILURE);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategyImpl.getTaxes(null, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID);
	}

	/**
	 * Test the case of {@link ShippingCostTaxesLookupStrategyImpl#getTaxes} when the tax document contains
	 * tax records with multiple currencies.
	 */
	@Test(expected = IllegalStateException.class)
	public void testGetTaxesWithMultipleTaxCurrencies() {
		TaxJournalRecord record1 = Mockito.mock(TaxJournalRecord.class);
		when(record1.getCurrency()).thenReturn(CURRENCY_CODE_CAD);
		when(record1.getTaxAmount()).thenReturn(RECORD_1_AMOUNT);
		when(record1.getTaxName()).thenReturn(RECORD_1_NAME);
		taxDocument.add(record1);
		TaxJournalRecord record2 = Mockito.mock(TaxJournalRecord.class);
		when(record2.getCurrency()).thenReturn(CURRENCY_CODE_USD);
		when(record2.getTaxAmount()).thenReturn(RECORD_2_AMOUNT);
		when(record2.getTaxName()).thenReturn(RECORD_2_NAME);
		taxDocument.add(record2);

		lookupStrategyImpl.getTaxes(null, DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID);
	}

	private void mockCorrectTaxRecords() {
		TaxJournalRecord record1 = Mockito.mock(TaxJournalRecord.class);
		when(record1.getCurrency()).thenReturn(CURRENCY_CODE_CAD);
		when(record1.getTaxAmount()).thenReturn(RECORD_1_AMOUNT);
		when(record1.getTaxName()).thenReturn(RECORD_1_NAME);
		taxDocument.add(record1);
		TaxJournalRecord record2 = Mockito.mock(TaxJournalRecord.class);
		when(record2.getCurrency()).thenReturn(CURRENCY_CODE_CAD);
		when(record2.getTaxAmount()).thenReturn(RECORD_2_AMOUNT);
		when(record2.getTaxName()).thenReturn(RECORD_2_NAME);
		taxDocument.add(record2);
	}

	private void mockMoneyTransformer() {
		when(moneyFormatter.formatCurrency(CURRENCY, RECORD_1_AMOUNT, LOCALE)).thenReturn(RECORD_1_DISPLAY);
		when(moneyFormatter.formatCurrency(CURRENCY, RECORD_2_AMOUNT, LOCALE)).thenReturn(RECORD_2_DISPLAY);
	}

	private void mockResourceOperationContext() {
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private void mockTaxDocumentRepository(final boolean shouldSucceed) {
		ExecutionResult<Collection<TaxJournalRecord>> taxDocumentResult;
		if (shouldSucceed) {
			taxDocumentResult = ExecutionResultFactory.<Collection<TaxJournalRecord>>createReadOK(taxDocument);
		} else {
			taxDocumentResult = ExecutionResultFactory.createNotFound();
		}
		when(taxDocumentRepository.getTaxDocument(taxDocumentId, TaxCode.TAX_CODE_SHIPPING)).thenReturn(taxDocumentResult);
	}

	private void mockOrderShipment(final ShipmentType shipmentType) {
		when(orderShipment.getTaxDocumentId()).thenReturn(taxDocumentId);
		when(orderShipment.getOrderShipmentType()).thenReturn(shipmentType);
		Money money = Money.valueOf(BigDecimal.TEN, CURRENCY);
		when(orderShipment.getShippingTaxMoney()).thenReturn(money);
	}

	private void mockShipmentRepository(final boolean shouldSucceed) {
		ExecutionResult<PhysicalOrderShipment> orderShipmentResult;
		if (shouldSucceed) {
			orderShipmentResult = ExecutionResultFactory.createReadOK(orderShipment);
		} else {
			orderShipmentResult = ExecutionResultFactory.createNotFound();
		}
		when(shipmentRepository.find(DECODED_PURCHASE_ID, DECODED_SHIPMENT_ID)).thenReturn(orderShipmentResult);
	}

}
