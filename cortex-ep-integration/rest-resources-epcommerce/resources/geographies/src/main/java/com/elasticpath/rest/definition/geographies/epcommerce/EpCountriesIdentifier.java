/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.geographies.epcommerce;

import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Typed {@link CountriesIdentifier} adaptation.
 */
public interface EpCountriesIdentifier extends CountriesIdentifier {

	@Override
	IdentifierPart<String> getScope();
}
