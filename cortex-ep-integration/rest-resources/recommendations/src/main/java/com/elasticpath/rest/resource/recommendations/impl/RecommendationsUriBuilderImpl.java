/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.RecommendationsUriBuilder}.
 */
@Named("recommendationsUriBuilder")
public final class RecommendationsUriBuilderImpl implements RecommendationsUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String recommendationGroup;
	private String sourceURI;
	private String pageNumber;
	private String pagePart;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public RecommendationsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public RecommendationsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}


	@Override
	public RecommendationsUriBuilder setRecommendationGroup(final String recommendationGroup) {
		this.recommendationGroup = recommendationGroup;
		return this;
	}

	@Override
	public RecommendationsUriBuilder setSourceUri(final String sourceURI) {
		this.sourceURI = sourceURI;
		return this;
	}

	@Override
	public RecommendationsUriBuilder setPageNumber(final int pageNumber) {
		this.pagePart = PageNumber.URI_PART;
		this.pageNumber = String.valueOf(pageNumber);
		return this;	}

	@Override
	public String build() {
		assert !(sourceURI == null && scope == null) : "either scope or sourceURI must be set.";
		assert !(sourceURI != null && scope != null) : "scope not required if sourceURI set.";

		return URIUtil.format(resourceServerName, sourceURI, scope, recommendationGroup, pagePart, pageNumber);
	}
}
