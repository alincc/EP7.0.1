/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.lookups.BatchItemsActionIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsFormIdentifier;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.definition.lookups.LookupsMediaTypes;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Adds a code link to item.
 */
@Singleton
@Named("addBatchItemsLinkToLookups")
public final class AddBatchItemsLinkToLookups implements ResourceStateLinkHandler<LinksEntity> {

	private final IdentifierTransformerProvider idTransformerProvider;

	/**
	 * Constructor.
	 *
	 * @param idTransformerProvider the identifier transformer provider
	 */
	@Inject
	AddBatchItemsLinkToLookups(
			@Named("identifierTransformerProvider")
			final IdentifierTransformerProvider idTransformerProvider) {

		this.idTransformerProvider = idTransformerProvider;
	}


	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> resourceState) {

		LookupsIdentifier lookupsId;

		try {
			lookupsId = idTransformerProvider.forClass(LookupsIdentifier.class)
					.uriToIdentifier(resourceState.getSelf().getUri());
		} catch (Exception e) {
			//not actually a Lookups.
			return ImmutableList.of();
		}

		BatchItemsFormIdentifier formId = BatchItemsFormIdentifier.builder()
				.withBatchItemsAction(BatchItemsActionIdentifier.builder().withLookups(lookupsId).build())
				.build();

		String formUri = idTransformerProvider.forClass(BatchItemsFormIdentifier.class)
				.identifierToUri(formId);

		ResourceLink link = ResourceLinkFactory.createNoRev(formUri, LookupsMediaTypes.CODES.id(), LookupResourceRels.BATCH_ITEMS_LOOKUP_FORM_REL);

		return ImmutableList.of(link);
	}
}
