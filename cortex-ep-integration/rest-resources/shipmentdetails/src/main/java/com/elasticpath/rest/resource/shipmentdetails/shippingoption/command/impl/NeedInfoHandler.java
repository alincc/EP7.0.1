/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import java.util.Collection;

import com.elasticpath.rest.schema.ResourceLink;

/**
 * Determines if there are any matching needinfos for the given URI and needed rel.
 */
public interface NeedInfoHandler {
	/**
	 * Given the URI of an info resource, determines if there are any {@code needinfo}s for a given {@code rel}.
	 * @param infoUri the URI of an info resource
	 * @param rel the rel to match
	 * @return a {@link Collection} of needinfo {@link ResourceLink}s
	 */
	Collection<ResourceLink> getNeedInfoLinksForInfo(String infoUri, String rel);
}
