/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.discounts.DiscountsMediaTypes;
import com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Responsible for building discount links.
 */
@Singleton
@Named
final class DiscountsLinkCreator {

	private final DiscountsUriBuilderFactory discountsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param discountsUriBuilderFactory the promotions URI builder factory.
	 */
	@Inject
	DiscountsLinkCreator(
			@Named("discountsUriBuilderFactory")
			final DiscountsUriBuilderFactory discountsUriBuilderFactory) {
		this.discountsUriBuilderFactory = discountsUriBuilderFactory;
	}

	/**
	 * Create a link to discount for a source.
	 * If source does not have discounts, an empty list is returned.
	 *
	 * @param sourceUri the self uri of the source
	 * @return links to add to source representation.
	 */
	Collection<ResourceLink> buildDiscountsLink(final String sourceUri) {
		String discountsUri = discountsUriBuilderFactory.get().setSourceUri(sourceUri).build();
		ResourceLink link =	ResourceLinkFactory.createNoRev(discountsUri, DiscountsMediaTypes.DISCOUNT.id(), DiscountsResourceRels.DISCOUNT_REL);
		return Collections.singleton(link);
	}
}
