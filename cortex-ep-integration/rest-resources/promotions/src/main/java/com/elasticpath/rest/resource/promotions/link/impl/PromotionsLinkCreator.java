/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.PossiblePromotions;
import com.elasticpath.rest.resource.promotions.rel.PromotionsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Responsible for building promotions links for carts.
 */
@Singleton
@Named("promotionsLinkCreator")
final class PromotionsLinkCreator {

	private final PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param promotionsUriBuilderFactory the promotions URI builder factory.
	 */
	@Inject
	PromotionsLinkCreator(
			@Named("promotionsUriBuilderFactory")
			final PromotionsUriBuilderFactory promotionsUriBuilderFactory) {
		this.promotionsUriBuilderFactory = promotionsUriBuilderFactory;
	}

	/**
	 * Create a link to promotions for a source.
	 * If source does not have promotions, an empty list is returned.
	 *
	 *
	 * @param sourceUri the self uri of the source
	 * @return links to add to source representation.
	 */
	Collection<ResourceLink> buildAppliedPromotionsLink(final String sourceUri) {
		String promotionsUri = promotionsUriBuilderFactory.get().setSourceUri(sourceUri).setPromotionType(AppliedPromotions.URI_PART).build();
		ResourceLink link = ResourceLinkFactory.createNoRev(promotionsUri, CollectionsMediaTypes.LINKS.id(), PromotionsResourceRels.PROMOTIONS_REL);
		return Collections.singleton(link);
	}

	/**
	 * Create a link to possible promotions for a source.
	 * If source does not have promotions, an empty list is returned.
	 *
	 * @param sourceUri the self uri of the source
	 * @param hasPromotions the result from the lookup, that checked to see if any promotions were present
	 * @return links to add to source representation.
	 */
	Collection<ResourceLink> buildPossiblePromotionsLink(final String sourceUri, final ExecutionResult<Boolean> hasPromotions) {
		Collection<ResourceLink> linksToAdd;
		if (hasPromotions.isSuccessful() && hasPromotions.getData()) {
			String promotionsUri = promotionsUriBuilderFactory.get().setSourceUri(sourceUri).setPromotionType(PossiblePromotions.URI_PART).build();
			ResourceLink link =	ResourceLinkFactory.createNoRev(promotionsUri, CollectionsMediaTypes.LINKS.id(), PromotionsResourceRels
			.POSSIBLE_REL);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}
