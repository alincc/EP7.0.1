/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.lookups.epcommerce;

import java.util.List;

import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * EP overload of BatchItemsIdentifier.
 */
public interface EpBatchItemsIdentifier extends BatchItemsIdentifier {

	@Override
	IdentifierPart<List<String>> getBatchId();
}
