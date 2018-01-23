/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * Tests for {@link TaxDocumentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxDocumentRepositoryImplTest {

	private static final String TAX_ITEM_CODE = "testTaxItemCode";

	@Mock private TaxDocumentService taxDocumentService;
	@Mock private TaxDocumentId taxDocumentId;
	@Mock private List<TaxJournalRecord> records;

	@InjectMocks private TaxDocumentRepositoryImpl taxDocumentRepositoryImpl;

	/**
	 * Test {@link TaxDocumentRepositoryImpl#getTaxDocument} success case.
	 */
	@Test
	public void testGetTaxDocumentSuccess() {
		when(taxDocumentService.find(taxDocumentId, TAX_ITEM_CODE)).thenReturn(records);

		ExecutionResult<Collection<TaxJournalRecord>> taxDocumentResult = taxDocumentRepositoryImpl.getTaxDocument(taxDocumentId, TAX_ITEM_CODE);

		AssertExecutionResult.assertExecutionResult(taxDocumentResult)
				.isSuccessful()
				.data(records);
	}

	/**
	 * Test {@link TaxDocumentRepositoryImpl#getTaxDocument} case when the call to {@link TaxDocumentService} returns null.
	 */
	@Test
	public void testGetTaxDocumentWithServiceReturningNull() {
		when(taxDocumentService.find(taxDocumentId, TAX_ITEM_CODE)).thenReturn(null);

		ExecutionResult<Collection<TaxJournalRecord>> taxDocumentResult = taxDocumentRepositoryImpl.getTaxDocument(taxDocumentId, TAX_ITEM_CODE);

		assertTrue("Tax document lookup should be a failure.", taxDocumentResult.isFailure());
	}

}
