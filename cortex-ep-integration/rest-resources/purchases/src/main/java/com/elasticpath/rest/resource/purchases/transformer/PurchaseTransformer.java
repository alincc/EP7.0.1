/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.PurchasesResourceLinkFactory;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.PurchaseListUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * The Purchase Transformer.
 */
@Singleton
@Named("purchaseTransformer")
public class PurchaseTransformer {

	private final String resourceServerName;
	private final PurchasesResourceLinkFactory purchasesResourceLinkFactory;
	private final PurchaseListUriBuilderFactory purchaseListUriBuilderFactory;

	/**
	 * Default Constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param purchasesResourceLinkFactory the purchase resource link factory
	 * @param purchaseListUriBuilderFactory purchase list URI Builder Provider.
	 */
	@Inject
	public PurchaseTransformer(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("purchaseLinkFactory")
			final PurchasesResourceLinkFactory purchasesResourceLinkFactory,
			@Named("purchaseListUriBuilderFactory")
			final PurchaseListUriBuilderFactory purchaseListUriBuilderFactory) {

		this.resourceServerName = resourceServerName;
		this.purchasesResourceLinkFactory = purchasesResourceLinkFactory;
		this.purchaseListUriBuilderFactory = purchaseListUriBuilderFactory;
	}


	/**
	 * Transforms an {@link PurchaseEntity} to an {@link ResourceState}.
	 *
	 * @param scope the scope
	 * @param dto the dto
	 * @return the representation
	 */
	public ResourceState<PurchaseEntity> transformToRepresentation(final String scope, final PurchaseEntity dto) {
		String orderCorrelationId = null;
		if (dto.getOrderId() != null) {
			orderCorrelationId = Base32Util.encode(dto.getOrderId());
		}
		String purchaseNumber = dto.getPurchaseId();
		String encodedPurchaseId = Base32Util.encode(purchaseNumber);
		PurchaseEntity encodedEntity = PurchaseEntity.builderFrom(dto)
				.withPurchaseId(encodedPurchaseId)
				.withPurchaseNumber(purchaseNumber)
				.withOrderId(orderCorrelationId)
				.build();

		//		.setStatus(PurchaseStatus.valueOf(dto.getStatus()))

		String purchaseListUri = purchaseListUriBuilderFactory.get()
				.setScope(scope)
				.build();
		ResourceLink purchaseListLink = ElementListFactory.createListWithoutElement(purchaseListUri, CollectionsMediaTypes.LINKS.id());

		return ResourceState.Builder
				.create(encodedEntity)
				.withSelf(purchasesResourceLinkFactory.createSelf(resourceServerName, scope, encodedPurchaseId))
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
			.withScope(scope)
				.addingLinks(purchaseListLink)
				.build();
	}
}
