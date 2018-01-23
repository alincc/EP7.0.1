/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.resource.lookups.constant.LookupConstants;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;

/**
 * Transforms a code entity into a resource state.
 */
@Singleton
@Named("codeEntityTransformer")
public class CodeEntityTransformer implements TransformRfoToResourceState<CodeEntity, CodeEntity, ItemEntity> {

	private final ItemLookupUriBuilderFactory itemLookupUriBuilderFactory;

	/**
	 * Constructor.
	 * @param itemLookupUriBuilderFactory the item lookup uri builder.
	 */
	@Inject
	CodeEntityTransformer(
			@Named("itemLookupUriBuilderFactory")
			final ItemLookupUriBuilderFactory itemLookupUriBuilderFactory) {

		this.itemLookupUriBuilderFactory = itemLookupUriBuilderFactory;
	}

	@Override
	public ResourceState<CodeEntity> transform(final CodeEntity codeEntity, final ResourceState<ItemEntity> item) {
		Self self = SelfFactory.createSelf(itemLookupUriBuilderFactory.get().setSourceUri(item.getSelf().getUri()).build());

		ResourceLink itemLink = ResourceLinkFactory.create(
				item.getSelf().getUri(),
				ItemsMediaTypes.ITEM.id(),
				LookupResourceRels.ITEM_REL,
				LookupResourceRels.CODE_REL);

		return ResourceState.Builder.create(codeEntity)
				.withSelf(self)
				.withResourceInfo(LookupConstants.TEN_MINUTES)
				.addingLinks(itemLink)
				.build();
	}
}
