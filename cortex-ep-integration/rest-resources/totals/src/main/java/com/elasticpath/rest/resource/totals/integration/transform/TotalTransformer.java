/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.TotalsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transforms a {@link TotalEntity} and an other {@link ResourceState}to a {@link TotalEntity}.
 */
@Singleton
@Named("totalTransformer")
public class TotalTransformer {

	private final TotalsUriBuilderFactory totalsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param totalsUriBuilderFactory the totals URI builder factory.
	 */
	@Inject
	TotalTransformer(
			@Named("totalsUriBuilderFactory")
			final TotalsUriBuilderFactory totalsUriBuilderFactory) {
		this.totalsUriBuilderFactory = totalsUriBuilderFactory;
	}

	/**
	 * Transform a totalEntity and other resource state into a resource state totals entity.
	 * @param totalEntity the total entity
	 * @param otherResourceState the other resource state
	 * @param rel the rel to the other resource
	 * @return total entity wrapped in a resource state.
	 */
	public ResourceState<TotalEntity>  transform(final TotalEntity totalEntity, final ResourceState<?> otherResourceState, final String rel) {

		String selfUri = totalsUriBuilderFactory.get().setSourceUri(ResourceStateUtil.getSelfUri(otherResourceState)).build();
		Self self = SelfFactory.createSelf(selfUri);
		ResourceLink relLink = ResourceLinkFactory.createFromSelf(otherResourceState.getSelf(), rel, TotalResourceRels.TOTAL_REV);
		return ResourceState.Builder.create(totalEntity)
				.withSelf(self)
				.withScope(otherResourceState.getScope())
				.addingLinks(relLink)
				.build();
	}
}
