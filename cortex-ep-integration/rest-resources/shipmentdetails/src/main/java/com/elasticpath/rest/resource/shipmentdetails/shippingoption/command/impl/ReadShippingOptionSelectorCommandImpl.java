/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.common.selector.SingleSelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.ReadShippingOptionSelectorCommand;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.constants.ShippingOptionInfoConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Reads the Shipping Option Selector.
 */
@Named
final class ReadShippingOptionSelectorCommandImpl implements ReadShippingOptionSelectorCommand {

	private final String resourceServerName;
	private final ShippingOptionLookup shippingOptionLookup;

	private String shipmentDetailsId;
	private String scope;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName   the resource server name
	 * @param shippingOptionLookup the shipping option lookup
	 */
	@Inject
	ReadShippingOptionSelectorCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("shippingOptionLookup")
			final ShippingOptionLookup shippingOptionLookup) {

		this.shippingOptionLookup = shippingOptionLookup;
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ExecutionResult<ResourceState<SelectorEntity>> execute() {

		Collection<String> shippingOptionIds = Assign.ifSuccessful(
				shippingOptionLookup.getShippingOptionIdsForShipmentDetail(scope, shipmentDetailsId));

		SelectorResourceStateBuilder selectorRepresentationBuilder = new SingleSelectorResourceStateBuilder();
		addChoiceLinksToSelector(shippingOptionIds, selectorRepresentationBuilder);

		String chosenId;
		try {
			chosenId = Assign.ifSuccessful(
					shippingOptionLookup.getSelectedShipmentOptionIdForShipmentDetails(scope, shipmentDetailsId)
			);
		} catch (BrokenChainException bce) {
			chosenId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(chosenId)) {
			String chosenShippingOptionUri = createShippingOptionUri(chosenId);
			String chosenUri = createChoiceChosenUri(chosenShippingOptionUri);
			ResourceLink chosenLink = ResourceLinkFactory.createUriType(chosenUri, CollectionsMediaTypes.LINKS.id());
			selectorRepresentationBuilder.setSelection(chosenLink);
		}

		String shippingOptionInfoUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId, ShippingOptionInfo.URI_PART);
		String selfUri = URIUtil.format(shippingOptionInfoUri, Selector.URI_PART);
		ResourceLink infoLink = ResourceLinkFactory.create(shippingOptionInfoUri, ControlsMediaTypes.INFO.id(),
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REL, SelectorRepresentationRels.SELECTOR);

		ResourceState<SelectorEntity> selectorRepresentation = selectorRepresentationBuilder.setSelfUri(selfUri)
				.setName(ShippingOptionInfoConstants.SELECTOR_DISPLAY_NAME)
				.addLink(infoLink)
				.build();

		return ExecutionResultFactory.createReadOK(selectorRepresentation);
	}


	private void addChoiceLinksToSelector(final Collection<String> shippingOptionIds,
											final SelectorResourceStateBuilder selectorRepresentationBuilder) {
		for (String shippingOptionId : shippingOptionIds) {
			String shippingOptionUri = createShippingOptionUri(shippingOptionId);
			String choiceUri = createChoiceChosenUri(shippingOptionUri);
			selectorRepresentationBuilder.addChoice(ResourceLinkFactory.createUriType(choiceUri, CollectionsMediaTypes.LINKS.id()));
		}
	}

	private String createChoiceChosenUri(final String shippingOptionUri) {
		return URIUtil.format(resourceServerName, scope,
				shipmentDetailsId, ShippingOptionInfo.URI_PART, Selector.URI_PART, shippingOptionUri);
	}

	private String createShippingOptionUri(final String shippingOptionId) {
		return URIUtil.format(resourceServerName, scope, shipmentDetailsId, ShippingOption.URI_PART, shippingOptionId);
	}


	/**
	 * Builder.
	 */
	@Named("readShippingOptionSelectorCommandBuilder")
	public static class BuilderImpl implements ReadShippingOptionSelectorCommand.Builder {

		private final ReadShippingOptionSelectorCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadShippingOptionSelectorCommandImpl command) {
			this.command = command;
		}

		@Override
		public ReadShippingOptionSelectorCommand build() {
			assert command.scope != null : "scope should not be null";
			assert command.shipmentDetailsId != null : "shipment Details Id should not be null";
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
