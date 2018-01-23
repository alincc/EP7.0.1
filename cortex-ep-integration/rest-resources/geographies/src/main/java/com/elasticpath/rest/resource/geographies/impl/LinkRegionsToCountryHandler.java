/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import static com.elasticpath.rest.schema.util.ResourceStateUtil.getSelfType;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.GeographiesMediaTypes;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.geographies.rel.GeographiesResourceRels;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create link from a Country to it's Regions.
 */
@Singleton
@Named("linkRegionsToCountryHandler")
public final class LinkRegionsToCountryHandler {

	private final IdentifierTransformerProvider transformerProvider;

	/**
	 * Constructor.
	 *
	 * @param transformerProvider identifier transformer provider
	 */
	@Inject
	LinkRegionsToCountryHandler(
		@Named("identifierTransformerProvider")
		final IdentifierTransformerProvider transformerProvider) {
		this.transformerProvider = transformerProvider;
	}

	/**
	 * Creates appropriate links, if any, for the given representation.
	 *
	 * @param representation the representation to attach links to
	 * @return the Collection of links to attach.
	 */
	Collection<ResourceLink> createLinks(final ResourceState<?> representation) {

		if (GeographiesMediaTypes.COUNTRY.id().equals(getSelfType(representation))) {
			CountryIdentifier countryIdentifier = transformerProvider
					.forClass(CountryIdentifier.class)
					.uriToIdentifier(ResourceStateUtil.getSelfUri(representation));
			RegionsIdentifier regionsIdentifier = RegionsIdentifier.builder()
					.withCountry(countryIdentifier)
					.build();

			String regionsUri = transformerProvider.forClass(RegionsIdentifier.class)
				.identifierToUri(regionsIdentifier);
			ResourceLink regionsLink = ResourceLinkFactory.createNoRev(
				regionsUri,
				CollectionsMediaTypes.LINKS.id(),
				GeographiesResourceRels.REGIONS_REL);

			return Collections.singleton(regionsLink);
		} else {
			return Collections.emptyList();
		}
	}
}
