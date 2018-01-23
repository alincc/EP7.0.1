/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

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
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetail;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.ReadShippingOptionInfoCommand;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.constants.ShippingOptionInfoConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadShippingOptionInfoCommandImpl}.
 */
@Named
final class ReadShippingOptionInfoCommandImpl implements ReadShippingOptionInfoCommand {

	private final String resourceServerName;
	private final ShippingOptionLookup shippingOptionLookup;
	private final DeliveryUriBuilderFactory deliveryUriBuilderFactory;
	private final ShipmentDetailsLookup shipmentDetailsLookup;

	private String scope;
	private String shipmentDetailsId;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName        the resource server name
	 * @param deliveryUriBuilderFactory the delivery uri builder factory
	 * @param shippingOptionLookup      the shipping option lookup
	 * @param shipmentDetailsLookup     the shipment details lookup
	 */
	@Inject
	ReadShippingOptionInfoCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("deliveryUriBuilderFactory")
			final DeliveryUriBuilderFactory deliveryUriBuilderFactory,
			@Named("shippingOptionLookup")
			final ShippingOptionLookup shippingOptionLookup,
			@Named("shipmentDetailsLookup")
			final ShipmentDetailsLookup shipmentDetailsLookup) {

		this.deliveryUriBuilderFactory = deliveryUriBuilderFactory;
		this.shippingOptionLookup = shippingOptionLookup;
		this.shipmentDetailsLookup = shipmentDetailsLookup;
		this.resourceServerName = resourceServerName;
	}


	@Override
	public ExecutionResult<ResourceState<InfoEntity>> execute() {

		ShipmentDetail shipmentDetail = Assign.ifSuccessful(shipmentDetailsLookup.getShipmentDetail(scope, shipmentDetailsId));
		Collection<ResourceLink> links = new ArrayList<>();
		String selfUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId, ShippingOptionInfo.URI_PART);
		ResourceLink selectorLink = ResourceLinkFactory.create(
				URIUtil.format(selfUri, Selector.URI_PART),
				ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR,
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REV);
		links.add(selectorLink);

		String deliveryUri = deliveryUriBuilderFactory.get()
				.setScope(scope)
				.setOrderId(shipmentDetail.getOrderId())
				.setDeliveryId(shipmentDetail.getDeliveryId())
				.build();
		ResourceLink deliveryLink = ResourceLinkFactory.create(
				deliveryUri,
				OrdersMediaTypes.DELIVERY.id(),
				ShipmentDetailsRels.DELIVERY_REL,
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REV);
		links.add(deliveryLink);

		String shippingOptionId;
		try {
			shippingOptionId = Assign.ifSuccessful(
					shippingOptionLookup.getSelectedShipmentOptionIdForShipmentDetails(scope, shipmentDetailsId)
			);
		} catch (BrokenChainException bce) {
			shippingOptionId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(shippingOptionId)) {
			ResourceLink shipmentOptionLink = ResourceLinkFactory.createNoRev(
					URIUtil.format(resourceServerName, scope, shipmentDetailsId, ShippingOption.URI_PART, shippingOptionId),
					ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(),
					ShipmentDetailsRels.SHIPPINGOPTION_REL);
			links.add(shipmentOptionLink);
		}

		ResourceState<InfoEntity> shippingOptionInfoRepresentation = ResourceState.Builder
				.create(InfoEntity.builder()
						.withName(ShippingOptionInfoConstants.SHIPPING_OPTION_INFO_NAME)
						.build())
				.withSelf(SelfFactory.createSelf(selfUri))
				.withScope(scope)
				.withLinks(links)
				.build();

		return ExecutionResultFactory.createReadOK(shippingOptionInfoRepresentation);
	}

	/**
	 * The read shipping option info command builder.
	 */
	@Named("readShippingOptionInfoCommandBuilder")
	public static class BuilderImpl implements ReadShippingOptionInfoCommand.Builder {

		/**
		 * The command.
		 */
		private final ReadShippingOptionInfoCommandImpl command;

		/**
		 * Instantiates a new builder.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadShippingOptionInfoCommandImpl command) {
			this.command = command;
		}

		@Override
		public ReadShippingOptionInfoCommand build() {
			assert command.scope != null : "scope must be set";
			assert command.shipmentDetailsId != null : "shipment details must be set";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public Builder setShipmentDetailsId(final String shipmentDetailsId) {
			command.shipmentDetailsId = shipmentDetailsId;
			return this;
		}
	}
}
