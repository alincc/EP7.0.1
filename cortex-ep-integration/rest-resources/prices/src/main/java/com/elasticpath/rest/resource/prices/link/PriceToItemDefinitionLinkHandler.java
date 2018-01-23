/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilderFactory;

/**
 * Create a link to item definitions from price range.
 */
@Singleton
@Named("priceToItemDefinitionLinkHandler")
public final class PriceToItemDefinitionLinkHandler implements ResourceStateLinkHandler<PriceRangeEntity> {
	private final ItemDefinitionsUriBuilderFactory itemDefinitionsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param itemDefinitionsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilderFactory}
	 */
	@Inject
	PriceToItemDefinitionLinkHandler(
			@Named("itemDefinitionsUriBuilderFactory")
			final ItemDefinitionsUriBuilderFactory itemDefinitionsUriBuilderFactory) {
		this.itemDefinitionsUriBuilderFactory = itemDefinitionsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<PriceRangeEntity> priceRangeEntity) {
		String itemDefinitionsUri = itemDefinitionsUriBuilderFactory.get()
											.setItemId(priceRangeEntity.getEntity().getItemId())
											.setScope(priceRangeEntity.getScope())
											.build();
		return Collections.singleton(ResourceLinkFactory.create(itemDefinitionsUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION.id(),
																PriceRepresentationRels.ITEM_DEFINITION_REL,
																PriceRepresentationRels.FROM_PRICE_REV));
	}
}
