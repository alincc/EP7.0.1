/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.link.impl;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.recommendations.rel.RecommendationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Helper class for building recommendations link from other resources.
 */
public final class RecommendationsLinkCreationHelper {

	/**
	 * Forcing non instantiation with private default constructor.
	 */
	private RecommendationsLinkCreationHelper() { }

	/**
	 * Create a link to recommendations for a source.
	 * If source does not have recommendations, an empty list is returned.
	 *
	 * @param resourceServerName the resource server name
	 * @param hasRecommendations the result from the lookup, that checked to see if any recommendations were present
	 * @param sourceSelf the source self object
	 * @return links to add to source representation.
	 */
	public static Collection<ResourceLink> buildRecommendationsLink(final Self sourceSelf,
															final ExecutionResult<Boolean> hasRecommendations,
															final String resourceServerName) {
		Collection<ResourceLink> linksToAdd;
		if (hasRecommendations.isSuccessful() && hasRecommendations.getData()) {
			String uri = URIUtil.format(resourceServerName, sourceSelf.getUri());
			ResourceLink link =
					ResourceLinkFactory.createNoRev(uri, CollectionsMediaTypes.LINKS.id(), RecommendationsResourceRels.RECOMMENDATIONS_REL);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}
		return linksToAdd;
	}
}
