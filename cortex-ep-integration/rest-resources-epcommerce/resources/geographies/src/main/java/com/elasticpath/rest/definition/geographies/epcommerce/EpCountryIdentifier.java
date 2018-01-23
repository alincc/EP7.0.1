/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.geographies.epcommerce;

import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Typed {@link CountryIdentifier} adaptation.
 */
public interface EpCountryIdentifier extends CountryIdentifier {

	@Override
	EpCountriesIdentifier getCountries();

	@Override
	IdentifierPart<String> getCountryId();
}
