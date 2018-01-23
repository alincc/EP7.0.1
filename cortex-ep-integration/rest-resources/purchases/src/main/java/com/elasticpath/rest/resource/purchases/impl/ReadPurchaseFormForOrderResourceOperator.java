/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.PurchasesResourceLinkFactory;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.info.NeedInfoUtil;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource Operator for reading a Purchase form.
 */
@Singleton
@Named("readPurchaseFormForOrderResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ReadPurchaseFormForOrderResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final PurchasesResourceLinkFactory purchasesResourceLinkFactory;
	private final PurchaseLookup purchaseLookup;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName           resource server name
	 * @param purchasesResourceLinkFactory purchases link factory
	 * @param purchaseLookup               purchase lookup
	 */
	@Inject
	ReadPurchaseFormForOrderResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("purchaseLinkFactory")
			final PurchasesResourceLinkFactory purchasesResourceLinkFactory,
			@Named("purchaseLookup")
			final PurchaseLookup purchaseLookup) {

		this.resourceServerName = resourceServerName;
		this.purchasesResourceLinkFactory = purchasesResourceLinkFactory;
		this.purchaseLookup = purchaseLookup;
	}


	/**
	 * Process READ operation on Purchases form.
	 *
	 * @param orderRep the other resource
	 * @param operation the resource operation
	 * @return the {@link com.elasticpath.rest.OperationResult} with a {@link ResourceState}
	 */
	@Path({AnyResourceUri.PATH_PART, Form.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processPurchaseFormRead(
			@AnyResourceUri(readLinks = true)
			final ResourceState<OrderEntity> orderRep,
			final ResourceOperation operation) {

		String orderId = orderRep.getEntity().getOrderId();
		String orderUri = ResourceStateUtil.getSelfUri(orderRep);
		String formUri = URIUtil.format(resourceServerName, orderUri, Form.URI_PART);
		Self self = SelfFactory.createSelf(formUri);

		String scope = orderRep.getScope();
		boolean isOrderPurchasable = Assign.ifSuccessful(purchaseLookup.isOrderPurchasable(scope, orderId));
		Collection<ResourceLink> links = new ArrayList<>();
		if (NeedInfoUtil.hasNeedInfoLinks(orderRep)) {
			Collection<ResourceLink> needInfoLinks = NeedInfoUtil.getNeedInfoLinks(orderRep);
			links.addAll(needInfoLinks);
		} else if (isOrderPurchasable) {
			ResourceLink submitOrderLink = purchasesResourceLinkFactory.createSubmitOrderLink(resourceServerName, orderUri);
			links.add(submitOrderLink);
		}
		ResourceState<PurchaseEntity> resourceState = ResourceState.Builder
				.create(PurchaseEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.withLinks(links)
				.build();

		return OperationResultFactory.createReadOK(resourceState, operation);
	}
}
