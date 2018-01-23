/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.integration.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.TypeAdapter;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definition.lookups.epcommerce.EpBatchItemsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringListIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.lookups.integration.ItemLookupLookupStrategy;

/**
 * Lookup Strategy for Item Lookup.
 */
@Singleton
@Named("itemLookupLookupStrategy")
public class ItemLookupLookupStrategyImpl implements ItemLookupLookupStrategy {

	private final ItemRepository itemRepository;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Default constructor.
	 *
	 * @param itemRepository item repository
	 * @param productSkuRepository product sku repository
	 */
	@Inject
	public ItemLookupLookupStrategyImpl(
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository) {

		this.itemRepository = itemRepository;
		this.productSkuRepository = productSkuRepository;
	}


	@Override
	public ExecutionResult<CodeEntity> getItemLookupByItem(final String itemId) {
		String skuCode = Assign.ifSuccessful(itemRepository.getSkuCodeForItemId(itemId));
		CodeEntity codeEntity = CodeEntity.builder().withCode(skuCode).build();
		return ExecutionResultFactory.createReadOK(codeEntity);
	}

	@Override
	public ExecutionResult<String> getItemIdByCode(final String skuCode) {
		ProductSku sku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByCode(skuCode));
		String itemId = Assign.ifSuccessful(itemRepository.getItemIdForSku(sku));
		return ExecutionResultFactory.createReadOK(itemId);
	}

	@Override
	public IdentifierPart<List<String>> getBatchIdForCodes(final Iterable<String> skuCodes) {
		return StringListIdentifier.of(skuCodes);
	}

	@Override
	public Iterable<ItemIdentifier> getItemIdsForBatchId(final BatchItemsIdentifier batchItemsId) {
		EpBatchItemsIdentifier epBatchId = TypeAdapter.narrow(batchItemsId, EpBatchItemsIdentifier.class);
		IdentifierPart scopeId = epBatchId.getBatchItemsAction().getLookups().getScope();
		FluentIterable<String> codes = FluentIterable.from(epBatchId.getBatchId().getValue());

		return codes.transform(skuCode -> {
			ExecutionResult<ProductSku> result = productSkuRepository.getProductSkuWithAttributesByCode(skuCode);
			if (result.isSuccessful()) {
				ProductSku sku = result.getData();
				IdentifierPart<Map<String, String>> itemId = itemRepository.getItemIdForProductSku(sku);
				return ItemIdentifier.builder()
					.withItems(ItemsIdentifier.builder().withScope(scopeId).build())
					.withItemId(itemId)
					.build();
			}
			return null;
		}).filter(Predicates.notNull());
	}
}
