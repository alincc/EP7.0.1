/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * Default implementation of {@link TaxDocumentRepository}.
 */
@Singleton
@Named("taxDocumentRepository")
public class TaxDocumentRepositoryImpl implements TaxDocumentRepository {

	private static final String DOCUMENT_NOT_FOUND_MESSAGE = "No tax document was found.";

	private final TaxDocumentService taxDocumentService;

	/**
	 * Constructor. 
	 * 
	 * @param taxDocumentService a {@link TaxDocumentService}
	 */
	@Inject
	public TaxDocumentRepositoryImpl(
			@Named("taxDocumentService")
			final TaxDocumentService taxDocumentService) {
		this.taxDocumentService = taxDocumentService;
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<TaxJournalRecord>> getTaxDocument(final TaxDocumentId taxDocumentId, final String itemTaxCode) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				List<TaxJournalRecord> records = Assign.ifNotNull(taxDocumentService.find(taxDocumentId, itemTaxCode),
						OnFailure.returnNotFound(DOCUMENT_NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(records);
			}
		}.execute();
	}

}
