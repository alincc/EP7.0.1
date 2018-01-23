/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Read Shipping Option Choice.
 */
@Singleton
@Named("readShippingOptionChoiceResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ReadShippingOptionChoiceResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final ShippingOptionLookup shippingOptionLookup;

	/**
	 * Constructor.
	 * @param resourceServerName the resource server name
	 * @param shippingOptionLookup the shipping option lookup
	 */
	@Inject
	ReadShippingOptionChoiceResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("shippingOptionLookup")
			final ShippingOptionLookup shippingOptionLookup) {

		this.resourceServerName = resourceServerName;
		this.shippingOptionLookup = shippingOptionLookup;
	}


	/**
	 * Process read shipping option info choice.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingOption the shipping option
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, ShippingOptionInfo.PATH_PART, Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShippingOptionInfoChoice(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			@AnyResourceUri
			final ResourceState<ShippingOptionEntity> shippingOption,
			final ResourceOperation operation) {

		String shippingOptionUri = shippingOption.getSelf().getUri();
		String selfUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId,
				ShippingOptionInfo.URI_PART, Selector.URI_PART, shippingOption.getSelf().getUri());
		Self self = SelfFactory.createSelf(selfUri);

		String selectedShippingOptionId;
		try {
			selectedShippingOptionId = Assign.ifSuccessful(
					shippingOptionLookup.getSelectedShipmentOptionIdForShipmentDetails(scope, shipmentDetailsId)
			);
		} catch (BrokenChainException bce) {
			selectedShippingOptionId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}
		Collection<ResourceLink> links = new ArrayList<>();
		if (ObjectUtils.notEqual(shippingOption.getEntity().getShippingOptionId(), selectedShippingOptionId)) {
			ResourceLink selectActionLink = ResourceLinkFactory.createUriRel(selfUri, SelectorRepresentationRels.SELECT_ACTION);
			links.add(selectActionLink);
		}

		addDescriptionLink(links, shippingOptionUri);
		addSelectorLink(links, scope, shipmentDetailsId);

		ResourceState<LinksEntity> resourceState = ResourceState.Builder
				.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();
		return OperationResultFactory.createReadOK(resourceState, operation);
	}

	private void addSelectorLink(final Collection<ResourceLink> links, final String scope, final String shipmentDetailsId) {
		String selectorUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId,
				ShippingOptionInfo.URI_PART, Selector.URI_PART);
		links.add(ResourceLinkFactory.createNoRev(selectorUri, ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR));
	}

	private void addDescriptionLink(final Collection<ResourceLink> links, final String shippingOptionUri) {
		ResourceLink descriptionLink = ResourceLinkFactory.createNoRev(shippingOptionUri,
				ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(), SelectorRepresentationRels.DESCRIPTION);
		links.add(descriptionLink);
	}
}
