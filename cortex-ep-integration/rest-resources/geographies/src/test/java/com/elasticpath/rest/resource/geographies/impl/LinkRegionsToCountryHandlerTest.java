/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.GeographiesMediaTypes;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.transform.ResourceIdentifierTransformer;
import com.elasticpath.rest.resource.geographies.rel.GeographiesResourceRels;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.test.AssertResourceLink;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

@RunWith(MockitoJUnitRunner.class)
public class LinkRegionsToCountryHandlerTest {

	private static final String COUNTRY_ID = "CA";
	private static final String COUNTRY_URI = URIUtil.format("countries", "mobee", COUNTRY_ID);
	private static final String REGIONS_URI = URIUtil.format(COUNTRY_URI, "regionid");

	@Mock
	private IdentifierTransformerProvider mockTransformerProvider;
	@Mock
	private ResourceIdentifierTransformer<CountryIdentifier> mockCountryTransformer;
	@Mock
	private CountryIdentifier mockCountryIdentifier;
	@Mock
	private ResourceIdentifierTransformer<RegionsIdentifier> mockRegionsTransformer;

	@InjectMocks
	private LinkRegionsToCountryHandler handler;

	@Test
	public void testCreateLinks() throws Exception {
		when(mockTransformerProvider.forClass(CountryIdentifier.class))
				.thenReturn(mockCountryTransformer);
		when(mockCountryTransformer.uriToIdentifier(COUNTRY_URI))
				.thenReturn(mockCountryIdentifier);

		when(mockTransformerProvider.forClass(RegionsIdentifier.class))
				.thenReturn(mockRegionsTransformer);
		when(mockRegionsTransformer.identifierToUri(any(RegionsIdentifier.class)))
				.thenReturn(REGIONS_URI);

		ResourceState<ResourceEntity> notCountryRepresentation = createRepresentation("uri", "not a country");
		Collection<ResourceLink> links = handler.createLinks(notCountryRepresentation);
		assertThat(links, empty());

		ResourceState<ResourceEntity> countryRepresentation = createRepresentation(COUNTRY_URI, GeographiesMediaTypes.COUNTRY.id());
		links = handler.createLinks(countryRepresentation);

		assertThat(links, hasSize(1));
		AssertResourceLink.assertResourceLink(CollectionUtil.first(links))
			.type(CollectionsMediaTypes.LINKS.id())
			.rel(GeographiesResourceRels.REGIONS_REL)
			.uri(REGIONS_URI);
	}

	private ResourceState<ResourceEntity> createRepresentation(final String uri, final String type) {
		Self self = SelfFactory.createSelf(uri, type);
		return ResourceState.builder()
				.withSelf(self)
				.build();
	}
}