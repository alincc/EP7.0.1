/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument;

import java.util.Collection;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with TaxDocuments.
 */
public interface TaxDocumentRepository {

	/**
	 * Find a tax document by ID and type.
	 * 
	 * @param taxDocumentId the tax document ID
	 * @param taxItemCode the tax item code
	 * @return a {@link Collection} of {@link TaxJournalRecord}s
	 */
	ExecutionResult<Collection<TaxJournalRecord>> getTaxDocument(TaxDocumentId taxDocumentId, String taxItemCode);

}
