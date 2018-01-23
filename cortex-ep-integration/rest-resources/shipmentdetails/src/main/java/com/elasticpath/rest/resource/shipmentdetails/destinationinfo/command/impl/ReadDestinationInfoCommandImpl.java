/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetail;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.ReadDestinationInfoCommand;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.constants.DestinationInfoConstants;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadDestinationInfoCommand}.
 */
@Named
final class ReadDestinationInfoCommandImpl implements ReadDestinationInfoCommand {

	private static final int LINKS_CAPACITY = 3;

	private final String resourceServerName;
	private final DestinationInfoLookup destinationInfoLookup;
	private final ShipmentDetailsLookup shipmentDetailsLookup;
	private final AddressUriBuilderFactory addressUriBuilderFactory;
	private final DeliveryUriBuilderFactory deliveryUriBuilderFactory;
	private final AddressFormUriBuilderFactory addressFormUriBuilderFactory;

	private String scope;
	private String shipmentDetailsId;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName                   the resource server name
	 * @param destinationInfoLookup                the destination info lookup
	 * @param shipmentDetailsLookup                the shipment details lookup
	 * @param addressUriBuilderFactory             the address uri builder factory
	 * @param addressFormUriBuilderFactory the address form uri builder factory
	 * @param deliveryUriBuilderFactory            the delivery uri builder factory
	 */
	@Inject
	ReadDestinationInfoCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("destinationInfoLookup")
			final DestinationInfoLookup destinationInfoLookup,
			@Named("shipmentDetailsLookup")
			final ShipmentDetailsLookup shipmentDetailsLookup,
			@Named("addressUriBuilderFactory")
			final AddressUriBuilderFactory addressUriBuilderFactory,
			@Named("addressFormUriBuilderFactory")
			final AddressFormUriBuilderFactory addressFormUriBuilderFactory,
			@Named("deliveryUriBuilderFactory")
			final DeliveryUriBuilderFactory deliveryUriBuilderFactory) {

		this.resourceServerName = resourceServerName;
		this.destinationInfoLookup = destinationInfoLookup;
		this.shipmentDetailsLookup = shipmentDetailsLookup;
		this.addressUriBuilderFactory = addressUriBuilderFactory;
		this.addressFormUriBuilderFactory = addressFormUriBuilderFactory;
		this.deliveryUriBuilderFactory = deliveryUriBuilderFactory;
	}


	@Override
	public ExecutionResult<ResourceState<InfoEntity>> execute() {

		Collection<ResourceLink> addressInfoRepresentationLinks = new ArrayList<>(LINKS_CAPACITY);
		String selectedAddressId;
		try {
			selectedAddressId = Assign.ifSuccessful(
					destinationInfoLookup.findSelectedAddressIdForShipment(scope, shipmentDetailsId)
			);
		} catch (BrokenChainException bce) {
			selectedAddressId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(selectedAddressId)) {
			addressInfoRepresentationLinks.add(getSelectedAddressLink(selectedAddressId));
		}

		String destinationInfoUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId, DestinationInfo.URI_PART);
		addressInfoRepresentationLinks.add(createDestinationInfoSelectorLink(destinationInfoUri));

		ShipmentDetail shipmentDetail = Assign.ifSuccessful(shipmentDetailsLookup.getShipmentDetail(scope, shipmentDetailsId));
		ResourceLink deliveryLink = createDeliveryLink(scope, shipmentDetail.getOrderId(), shipmentDetail.getDeliveryId());
		addressInfoRepresentationLinks.add(deliveryLink);

		ResourceLink addressFormLink = createAddressFormLink(scope);
		addressInfoRepresentationLinks.add(addressFormLink);

		ResourceState<InfoEntity> destinationInfoRepresentation = ResourceState.Builder
				.create(InfoEntity.builder()
						.withName(DestinationInfoConstants.DESTINATION_INFO_NAME)
						.build())
				.withSelf(SelfFactory.createSelf(destinationInfoUri))
				.withScope(scope)
				.withLinks(addressInfoRepresentationLinks)
				.build();


		return ExecutionResultFactory.createReadOK(destinationInfoRepresentation);
	}

	private ResourceLink getSelectedAddressLink(final String selectedAddressId) {
		String selectedAddressUri = addressUriBuilderFactory.get()
				.setAddressId(selectedAddressId)
				.setScope(scope)
				.build();
		return ResourceLinkFactory.createNoRev(selectedAddressUri, AddressesMediaTypes.ADDRESS.id(), DestinationInfoRepresentationRels
				.DESTINATION_REL);
	}

	private ResourceLink createDestinationInfoSelectorLink(final String destinationInfoUri) {
		String addressInfoSelectorUri = URIUtil.format(destinationInfoUri, Selector.URI_PART);
		return ResourceLinkFactory.create(addressInfoSelectorUri, ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR,
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL);
	}

	private ResourceLink createDeliveryLink(final String scope, final String orderId, final String deliveryId) {
		String deliveryUri = deliveryUriBuilderFactory.get()
				.setScope(scope)
				.setOrderId(orderId)
				.setDeliveryId(deliveryId)
				.build();
		return ResourceLinkFactory.create(deliveryUri, OrdersMediaTypes.DELIVERY.id(), DestinationInfoRepresentationRels.DELIVERY_REL,
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL);
	}

	private ResourceLink createAddressFormLink(final String scope) {
		String addressFormUri = addressFormUriBuilderFactory.get()
				.setScope(scope)
				.build();
		return ResourceLinkFactory.createNoRev(addressFormUri, AddressesMediaTypes.ADDRESS.id(),
				DestinationInfoRepresentationRels.ADDRESS_FORM_REL);
	}

	/**
	 * Read billing information command builder.
	 */
	@Named("readDestinationInfoCommandBuilder")
	public static class BuilderImpl implements ReadDestinationInfoCommand.Builder {

		private final ReadDestinationInfoCommandImpl command;

		/**
		 * Instantiates a new builder.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadDestinationInfoCommandImpl command) {
			this.command = command;
		}

		@Override
		public ReadDestinationInfoCommand.Builder setShipmentDetailsId(final String shipmentDetailsId) {
			command.shipmentDetailsId = shipmentDetailsId;
			return this;
		}

		@Override
		public ReadDestinationInfoCommand.Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadDestinationInfoCommand build() {
			assert command.scope != null : "scope must be set";
			assert command.shipmentDetailsId != null : "shipmentDetailsId must be set.";
			return command;
		}
	}
}
