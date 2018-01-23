/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.common.selector.SingleSelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.ReadDestinationInfoSelectorCommand;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.constants.DestinationInfoConstants;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceLinkUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadDestinationInfoSelectorCommand}.
 */
@Named
final class ReadDestinationInfoSelectorCommandImpl implements ReadDestinationInfoSelectorCommand {

	private final String resourceServerName;
	private final ReadResourceCommand.Builder readResourceCommandBuilder;
	private final DestinationInfoLookup destinationInfoLookup;
	private final AddressUriBuilderFactory addressUriBuilderFactory;
	private final ShippingAddressListUriBuilderFactory shippingAddressUriBuilderFactory;

	private String scope;
	private String shipmentDetailsId;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName                       the resource server name
	 * @param readResourceCommandBuilderProvider       the read resource command builder provider
	 * @param shippingAddressUriBuilderFactory the shipping address uri builder factory
	 * @param addressUriBuilderFactory         the address uri builder factory
	 * @param destinationInfoLookup                    the destination info lookup
	 */
	@Inject
	ReadDestinationInfoSelectorCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("readResourceCommandBuilderProvider")
			final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider,
			@Named("shippingAddressesListUriBuilderFactory")
			final ShippingAddressListUriBuilderFactory shippingAddressUriBuilderFactory,
			@Named("addressUriBuilderFactory")
			final AddressUriBuilderFactory addressUriBuilderFactory,
			@Named("destinationInfoLookup")
			final DestinationInfoLookup destinationInfoLookup) {

		this.resourceServerName = resourceServerName;
		this.readResourceCommandBuilder = readResourceCommandBuilderProvider.get();
		this.shippingAddressUriBuilderFactory = shippingAddressUriBuilderFactory;
		this.addressUriBuilderFactory = addressUriBuilderFactory;
		this.destinationInfoLookup = destinationInfoLookup;
	}


	@Override
	public ExecutionResult<ResourceState<SelectorEntity>> execute() {
		ResourceState<LinksEntity> shippingAddresses = readShippingAddresses();

		String destinationInfoUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId, DestinationInfo.URI_PART);
		String selfUri = URIUtil.format(destinationInfoUri, Selector.URI_PART);
		SelectorResourceStateBuilder selectorRepresentationBuilder = new SingleSelectorResourceStateBuilder()
				.setSelfUri(selfUri)
				.setName(DestinationInfoConstants.DESTINATION_SELECTOR_NAME);

		List<ResourceLink> elementLinks = ResourceLinkUtil.findLinksByRel(shippingAddresses, ListElementRels.ELEMENT);
		for (ResourceLink link : elementLinks) {
			ResourceLink shippingAddressChoiceLink = ResourceLinkFactory.createUriType(URIUtil.format(selfUri, link.getUri()),
					CollectionsMediaTypes.LINKS.id());
			selectorRepresentationBuilder.addChoice(shippingAddressChoiceLink);
		}

		String selectedAddressId;
		try {
			selectedAddressId = Assign.ifSuccessful(
					destinationInfoLookup.findSelectedAddressIdForShipment(scope, shipmentDetailsId)
			);
		} catch (BrokenChainException bce) {
			selectedAddressId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(selectedAddressId)) {
			String selectedAddressUri = addressUriBuilderFactory.get()
					.setAddressId(selectedAddressId)
					.setScope(scope)
					.build();
			ResourceLink chosenLink = ResourceLinkFactory.createUriType(URIUtil.format(selfUri, selectedAddressUri),
					CollectionsMediaTypes.LINKS.id());
			selectorRepresentationBuilder.setSelection(chosenLink);
		}

		ResourceLink destinationInfoLink = ResourceLinkFactory.create(destinationInfoUri, ControlsMediaTypes.INFO.id(),
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL, SelectorRepresentationRels.SELECTOR);
		selectorRepresentationBuilder.addLink(destinationInfoLink);

		return ExecutionResultFactory.createReadOK(selectorRepresentationBuilder.build());
	}

	@SuppressWarnings("unchecked")
	private ResourceState<LinksEntity> readShippingAddresses() {
		String shippingAddressesUri = shippingAddressUriBuilderFactory.get()
				.setScope(scope)
				.build();
		return (ResourceState<LinksEntity>) ExecutionResultChain.executeCommand(
				readResourceCommandBuilder.setExpectedType(CollectionsMediaTypes.LINKS.id())
						.setResourceUri(shippingAddressesUri)
						.build());
	}


	/**
	 * The Class BuilderImpl.
	 */
	@Named("readDestinationInfoSelectorCommandBuilder")
	public static class BuilderImpl implements ReadDestinationInfoSelectorCommand.Builder {

		private final ReadDestinationInfoSelectorCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadDestinationInfoSelectorCommandImpl command) {
			this.command = command;
		}

		@Override
		public ReadDestinationInfoSelectorCommand.Builder setShipmentDetailsId(final String shipmentDetailsId) {
			command.shipmentDetailsId = shipmentDetailsId;
			return this;
		}

		@Override
		public ReadDestinationInfoSelectorCommand.Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadDestinationInfoSelectorCommand build() {
			assert command.scope != null : "scope must be set";
			assert command.shipmentDetailsId != null : "shipmentDetailsId must be set.";
			return command;
		}
	}
}
