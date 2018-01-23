/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.lookups.BatchItemsFormIdentifier;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.definition.lookups.LookupsMediaTypes;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.transform.ResourceIdentifierTransformer;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.test.AssertResourceLink;

@RunWith(MockitoJUnitRunner.class)
public class AddBatchItemsLinkToLookupsTest {

	static final String LOOKUPS_URI = "/lookups/scope";
	static final String FORM_URI = "/lookups/scope/items/batch/form";
	static final String ADDRESSES_URI = "/addresses/scope";

	@Mock
	IdentifierTransformerProvider idTransformerProvider;
	@InjectMocks
	AddBatchItemsLinkToLookups classUnderTest;

	@Mock
	ResourceIdentifierTransformer lookupsTransformer;
	@Mock
	LookupsIdentifier lookupsId;
	@Mock
	ResourceIdentifierTransformer batchFormTransformer;

	@Test
	public void testGetLinksToLookups() throws Exception {
		ResourceState state = ResourceState.builder()
			.withSelf(SelfFactory.createSelf(LOOKUPS_URI))
			.build();

		when(idTransformerProvider.forClass(LookupsIdentifier.class))
			.thenReturn(lookupsTransformer);
		when(lookupsTransformer.uriToIdentifier(LOOKUPS_URI))
			.thenReturn(lookupsId);
		when(idTransformerProvider.forClass(BatchItemsFormIdentifier.class))
			.thenReturn(batchFormTransformer);
		when(batchFormTransformer.identifierToUri(any(BatchItemsFormIdentifier.class)))
			.thenReturn(FORM_URI);

		Iterable<ResourceLink> actual = classUnderTest.getLinks(state);

		AssertResourceLink.assertResourceLink(Iterables.getFirst(actual, null))
			.uri(FORM_URI)
			.rel(LookupResourceRels.BATCH_ITEMS_LOOKUP_FORM_REL)
			.type(LookupsMediaTypes.CODES.id());
	}

	@Test
	public void testGetLinksToSomethingElse() throws Exception {
		ResourceState state = ResourceState.builder()
			.withSelf(SelfFactory.createSelf(ADDRESSES_URI))
			.build();

		when(idTransformerProvider.forClass(LookupsIdentifier.class))
			.thenReturn(lookupsTransformer);
		when(lookupsTransformer.uriToIdentifier(ADDRESSES_URI))
			.thenThrow(new IllegalArgumentException());

		Iterable<ResourceLink> actual = classUnderTest.getLinks(state);

		assertThat(actual, Matchers.emptyIterable());
	}
}