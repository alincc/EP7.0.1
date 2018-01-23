/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.carts.lineitems.Memberships;
import com.elasticpath.rest.resource.carts.rel.CartRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * LinkHandler that adds a cart memberships link into an item.
 */
@Singleton
@Named("cartMembershipToItemLinkHandler")
public class CartMembershipToItemLinkHandler implements ResourceStateLinkHandler<ItemEntity> {

	private final String resourceServerName;
	
	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public CartMembershipToItemLinkHandler(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}
	
	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemEntity> resourceState) {
		
		String selfUri = resourceState.getSelf().getUri();
		String cartMembershipsUri = URIUtil.format(resourceServerName, Memberships.URI_PART, selfUri);
		
		ResourceLink link = ResourceLinkFactory.createNoRev(cartMembershipsUri,
				CollectionsMediaTypes.LINKS.id(),
				CartRepresentationRels.CART_MEMBERSHIPS_REL);
		
		return Collections.singleton(link);
	}

}
