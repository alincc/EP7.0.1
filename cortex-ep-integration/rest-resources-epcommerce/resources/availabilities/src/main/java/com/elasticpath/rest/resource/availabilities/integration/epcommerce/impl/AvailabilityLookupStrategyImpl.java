/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.availabilities.integration.AvailabilityLookupStrategy;
import com.elasticpath.rest.resource.availabilities.integration.epcommerce.transform.StoreProductAvailabilityTransformer;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;

/**
 * DCE-specific implementation of {@link AvailabilityLookupStrategy}.
 */
@Singleton
@Named("availabilityLookupStrategy")
public class AvailabilityLookupStrategyImpl implements AvailabilityLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final StoreProductRepository storeProductRepository;
	private final ItemRepository itemRepository;
	private final ShoppingCartRepository shoppingCartRepository;
	private final StoreProductAvailabilityTransformer storeProductAvailabilityTransformer;
	private final LineItemTransformer lineItemTransformer;

	/**
	 * Default Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param storeProductRepository the store product repository
	 * @param shoppingCartRepository the shopping cart repository
	 * @param itemRepository the item repository
	 * @param storeProductAvailabilityTransformer the store product availability transformer
	 * @param lineItemTransformer the line item transformer
	 */
	@Inject
	public AvailabilityLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("storeProductAvailabilityTransformer")
			final StoreProductAvailabilityTransformer storeProductAvailabilityTransformer,
			@Named("lineItemTransformer")
			final LineItemTransformer lineItemTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.storeProductRepository = storeProductRepository;
		this.itemRepository = itemRepository;
		this.shoppingCartRepository = shoppingCartRepository;
		this.storeProductAvailabilityTransformer = storeProductAvailabilityTransformer;
		this.lineItemTransformer = lineItemTransformer;
	}

	/**
	 * Gets the availability.
	 *
	 * @param storeCode the storecode
	 * @param itemId the item id
	 * @return the availability
	 */
	@Override
	public ExecutionResult<AvailabilityEntity> getAvailability(final String storeCode, final String itemId) {

		ProductSku skuResult = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		StoreProduct storeProduct = Assign.ifSuccessful(
				storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(storeCode, skuResult.getProduct().getGuid()));

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		AvailabilityEntity itemAvailabilityDto =
				storeProductAvailabilityTransformer.transformToEntity(new Pair(storeProduct, skuResult), locale);
		return ExecutionResultFactory.createReadOK(itemAvailabilityDto);
	}

	@Override
	public ExecutionResult<Collection<LineItemEntity>> getUnavailableLineItems(final String storeCode, final String cartGuid) {
		ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(cartGuid));
		List<ShoppingItem> cartItems = shoppingCart.getCartItems();

		Collection<LineItemEntity> unavailableLineItems = new ArrayList<>();

		StoreProduct storeProduct;
		String itemId;

		for (ShoppingItem shoppingItem : cartItems) {
			itemId = shoppingItem.getSkuGuid();
			storeProduct = Assign.ifSuccessful(storeProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(storeCode, itemId));

			if (!storeProduct.isSkuAvailable(storeProduct.getSkuByGuid(itemId).getSkuCode())) {
				LineItem lineItem = ResourceTypeFactory.createResourceEntity(LineItem.class)
											.setShoppingItem(shoppingItem)
											.setCartId(cartGuid)
											.setItemId(itemId);

				LineItemEntity lineItemEntity = lineItemTransformer.transformToEntity(lineItem);

				unavailableLineItems.add(lineItemEntity);
			}
		}

		return  ExecutionResultFactory.createReadOK(unavailableLineItems);
	}
}
