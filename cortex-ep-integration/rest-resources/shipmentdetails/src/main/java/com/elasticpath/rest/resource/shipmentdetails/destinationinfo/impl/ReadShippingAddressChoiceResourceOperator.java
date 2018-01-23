/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

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
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Read a shipment address choice.
 */
@Singleton
@Named("readShippingAddressChoiceResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ReadShippingAddressChoiceResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final DestinationInfoLookup destinationInfoLookup;
	private final AddressUriBuilderFactory addressUriBuilderFactory;

	/**
	 * Default constructor.
	 * @param resourceServerName         the resource server name
	 * @param destinationInfoLookup      the destination info lookup
	 * @param addressUriBuilderFactory   the address uri builder factory
	 */
	@Inject
	ReadShippingAddressChoiceResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("destinationInfoLookup")
			final DestinationInfoLookup destinationInfoLookup,
			@Named("addressUriBuilderFactory")
			final AddressUriBuilderFactory addressUriBuilderFactory) {

		this.resourceServerName = resourceServerName;
		this.destinationInfoLookup = destinationInfoLookup;
		this.addressUriBuilderFactory = addressUriBuilderFactory;
	}


	/**
	 * Process read shipment destination info choice.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingAddress the shipping address
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, DestinationInfo.PATH_PART, Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentDestinationInfoChoice(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			@AnyResourceUri
			final ResourceState<AddressEntity> shippingAddress,
			final ResourceOperation operation) {


		String selectorUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId, DestinationInfo.URI_PART, Selector.URI_PART);
		String shippingAddressUri = ResourceStateUtil.getSelfUri(shippingAddress);
		String selfUri = URIUtil.format(selectorUri, shippingAddressUri);
		ResourceLink addressLink = ResourceLinkFactory.createNoRev(shippingAddressUri, AddressesMediaTypes.ADDRESS.id(),
				SelectorRepresentationRels.DESCRIPTION);
		ResourceLink destinationInfoSelectorLink = ResourceLinkFactory.createNoRev(selectorUri, ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR);
		Collection<ResourceLink> links = new ArrayList<>();
		links.add(addressLink);
		links.add(destinationInfoSelectorLink);

		String selectedAddressId;
		try {
			selectedAddressId = Assign.ifSuccessful(
					destinationInfoLookup.findSelectedAddressIdForShipment(scope, shipmentDetailsId)
			);
		} catch (BrokenChainException bce) {
			selectedAddressId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		String selectedAddressUri = null;
		//look up the selected addressinfo for the order.
		//will only be successful if address is already chosen.
		if (StringUtils.isNotEmpty(selectedAddressId)) {
			selectedAddressUri = addressUriBuilderFactory.get()
					.setAddressId(selectedAddressId)
					.setScope(scope)
					.build();
		}

		//put the select action link on if the address is not selected.
		if (ObjectUtils.notEqual(selectedAddressUri, shippingAddressUri)) {
			ResourceLink selectactionLink = ResourceLinkFactory.createUriRel(selfUri, SelectorRepresentationRels.SELECT_ACTION);
			links.add(selectactionLink);
		}

		Self self = SelfFactory.createSelf(selfUri);
		ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();

		return OperationResultFactory.createReadOK(resourceState, operation);
	}
}
