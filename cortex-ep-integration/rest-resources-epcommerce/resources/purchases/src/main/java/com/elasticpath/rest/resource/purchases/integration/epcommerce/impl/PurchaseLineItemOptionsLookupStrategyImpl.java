/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.SkuOptionTransformer;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.SkuOptionValueTransformer;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemOptionsLookupStrategy;

/**
 * EP Commerce strategy for finding line items options related details.
 */
@Singleton
@Named("purchaseLineItemOptionsLookupStrategy")
public class PurchaseLineItemOptionsLookupStrategyImpl implements PurchaseLineItemOptionsLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final OrderRepository orderRepository;
	private final SkuOptionTransformer skuOptionTransformer;
	private final SkuOptionValueTransformer skuOptionValueTransformer;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext  the resource operation context
	 * @param orderRepository           the order repository
	 * @param skuOptionTransformer      the sku option transformer
	 * @param skuOptionValueTransformer the sku option value transformer
	 * @param productSkuRepository      product sku repository
	 */
	@Inject
	public PurchaseLineItemOptionsLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("skuOptionTransformer")
			final SkuOptionTransformer skuOptionTransformer,
			@Named("skuOptionValueTransformer")
			final SkuOptionValueTransformer skuOptionValueTransformer,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository) {

		this.resourceOperationContext = resourceOperationContext;
		this.orderRepository = orderRepository;
		this.skuOptionTransformer = skuOptionTransformer;
		this.skuOptionValueTransformer = skuOptionValueTransformer;
		this.productSkuRepository = productSkuRepository;
	}

	@Override
	public ExecutionResult<Collection<String>> findOptionIds(final String storeCode, final String orderGuid, final String shoppingItemGuid) {

		ShoppingItem shoppingItem = Assign.ifSuccessful(getShoppingItem(storeCode, orderGuid, shoppingItemGuid));
		final ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(shoppingItem.getSkuGuid()));
		Collection<String> optionValueKeys = Assign.ifNotEmpty(productSku.getOptionValueCodes(),
				OnFailure.returnNotFound("No options found for line item."));
		return ExecutionResultFactory.createReadOK(optionValueKeys);
	}

	@Override
	public ExecutionResult<PurchaseLineItemOptionEntity> findOption(
			final String storeCode, final String orderGuid, final String shoppingItemGuid, final String optionKey) {

		SkuOptionValue optionValue = Assign.ifSuccessful(getOptionValue(storeCode, orderGuid, shoppingItemGuid, optionKey));
		return createOptionDto(optionValue);
	}

	@Override
	public ExecutionResult<PurchaseLineItemOptionValueEntity> findOptionValue(
			final String storeCode, final String orderGuid, final String shoppingItemGuid, final String optionKey, final String optionValueKey) {

		SkuOptionValue optionValue = Assign.ifSuccessful(getOptionValue(storeCode, orderGuid, shoppingItemGuid, optionKey));
		Ensure.isTrue(optionValueKey.equals(optionValue.getGuid()),
				OnFailure.returnNotFound("Option value not found."));
		return createOptionValueDto(optionValue);
	}

	private ExecutionResult<ShoppingItem> getShoppingItem(final String storeCode, final String orderGuid, final String shoppingItemGuid) {

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
		ShoppingItem shoppingItem = Assign.ifNotNull(order.getShoppingItemByGuid(shoppingItemGuid),
				OnFailure.returnNotFound("Line item with id %s not found.", shoppingItemGuid));
		return ExecutionResultFactory.createReadOK(shoppingItem);
	}

	private ExecutionResult<SkuOptionValue> getOptionValue(
			final String storeCode, final String orderGuid, final String shoppingItemGuid, final String optionKey) {

		ShoppingItem shoppingItem = Assign.ifSuccessful(getShoppingItem(storeCode, orderGuid, shoppingItemGuid));
		final ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(shoppingItem.getSkuGuid()));
		SkuOptionValue skuOption = Assign.ifNotNull(productSku.getOptionValueMap().get(optionKey),
				OnFailure.returnNotFound("option not found for item"));
		return ExecutionResultFactory.createReadOK(skuOption);
	}

	private ExecutionResult<PurchaseLineItemOptionEntity> createOptionDto(final SkuOptionValue optionValue) {

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		PurchaseLineItemOptionEntity optionDto = skuOptionTransformer.transformToEntity(optionValue, locale);
		return ExecutionResultFactory.createReadOK(optionDto);
	}

	private ExecutionResult<PurchaseLineItemOptionValueEntity> createOptionValueDto(final SkuOptionValue optionValue) {

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		PurchaseLineItemOptionValueEntity optionValueDto = skuOptionValueTransformer.transformToEntity(optionValue, locale);
		return ExecutionResultFactory.createReadOK(optionValueDto);
	}

}
