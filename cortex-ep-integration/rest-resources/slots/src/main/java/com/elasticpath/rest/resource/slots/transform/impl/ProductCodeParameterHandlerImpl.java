/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.slots.ItemIdLookup;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.resource.slots.transform.SlotParameterHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Slot parameter handler to handle the product code and construct an item URI.
 * <p/>
 * Though this handles productCodes, it generates item links, since the item is the saleable good.
 */
@Singleton
@Named("productCodeParameterHandler")
public final class ProductCodeParameterHandlerImpl implements SlotParameterHandler {

	private final ItemsUriBuilderFactory itemsUriBuilderFactory;
	private final ItemIdLookup itemIdLookup;


	/**
	 * Default Constructor.
	 *
	 * @param itemsUriBuilderFactory the products uri builder factory
	 * @param itemIdLookup the slots lookup
	 */
	@Inject
	public ProductCodeParameterHandlerImpl(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory,
			@Named("itemIdLookup")
			final ItemIdLookup itemIdLookup) {

		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
		this.itemIdLookup = itemIdLookup;
	}


	@Override
	public void handle(final String scope, final SlotEntity.Builder entityBuilder, final Collection<ResourceLink> links, final SlotDto slotDto) {
		SlotParameterDto slotParameterDto = slotDto.getParameters().get("productCode");
		if (slotParameterDto != null) {
			// find the default item for the specified product code
			String decodedProductId = slotParameterDto.getValue();
			String productId = Base32Util.encode(decodedProductId);
			ExecutionResult<String> itemIdResult = itemIdLookup.getDefaultItemIdForProduct(scope, productId);

			// attach the item link to the SlotRepresentation
			if (itemIdResult.isSuccessful()) {
				String itemUri = itemsUriBuilderFactory.get()
						.setItemId(itemIdResult.getData())
						.setScope(scope)
						.build();

				ResourceLink itemLink = ResourceLinkFactory.createNoRev(itemUri, ItemsMediaTypes.ITEM.id(),
						SlotsResourceRels.ON_EVENT_REL);

				links.add(itemLink);
			}
		}
	}
}
