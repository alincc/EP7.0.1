/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.geographies.epcommerce;

import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Typed {@link RegionIdentifier} adaptation.
 */
public interface EpRegionIdentifier extends RegionIdentifier {

	@Override
	EpRegionsIdentifier getRegions();

	@Override
	IdentifierPart<String> getRegionId();
}
