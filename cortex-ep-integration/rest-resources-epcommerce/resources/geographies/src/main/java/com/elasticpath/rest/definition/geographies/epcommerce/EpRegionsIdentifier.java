/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.geographies.epcommerce;

import com.elasticpath.rest.definition.geographies.RegionsIdentifier;

/**
 * Typed {@link RegionsIdentifier} adaptation.
 */
public interface EpRegionsIdentifier extends RegionsIdentifier {

	@Override
	EpCountryIdentifier getCountry();
}
