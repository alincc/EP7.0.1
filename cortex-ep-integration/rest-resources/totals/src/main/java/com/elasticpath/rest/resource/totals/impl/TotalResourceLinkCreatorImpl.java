/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.definition.totals.TotalsMediaTypes;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TotalsUriBuilderFactory;

/**
 * Builds the resource links for totals from other resources.
 */
@Singleton
@Named("totalResourceLinkCreator")
public final class TotalResourceLinkCreatorImpl implements TotalResourceLinkCreator {

	private final TotalsUriBuilderFactory totalsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param totalsUriBuilderFactory the totals URI builder factory.
	 */
	@Inject
	TotalResourceLinkCreatorImpl(
			@Named("totalsUriBuilderFactory")
			final TotalsUriBuilderFactory totalsUriBuilderFactory) {
		this.totalsUriBuilderFactory = totalsUriBuilderFactory;
	}

	@Override
	public Collection<ResourceLink> createLinkToOtherResource(final String resourceUri, final String rev) {
		String totalsUri = totalsUriBuilderFactory.get()
				.setSourceUri(resourceUri)
				.build();
		ResourceLink resourceLink = ResourceLinkFactory.create(totalsUri,
				TotalsMediaTypes.TOTAL.id(),
				TotalResourceRels.TOTAL_REL,
				rev);

		return Collections.singleton(resourceLink);
	}

	@Override
	public Collection<ResourceLink> createLinkToOtherResource(final String resourceUri,
																final ExecutionResult<ResourceState<TotalEntity>> totalsResult,
																final String rev) {
		Collection<ResourceLink> resultLinks;

		if (totalsResult.isSuccessful()) {
			resultLinks = createLinkToOtherResource(resourceUri, rev);
		} else {
			resultLinks = Collections.emptyList();
		}

		return resultLinks;
	}

}
