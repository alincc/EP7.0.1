/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoWriter;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * For setting the address for an order delivery.
 */
@Singleton
@Named("selectShippingAddressResourceOperator")
@Path(ResourceName.PATH_PART)
public final class SelectShippingAddressResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final DestinationInfoWriter destinationInfoWriter;


	/**
	 * Default constructor.
	 * @param resourceServerName                 the resource server name
	 * @param destinationInfoWriter              the destination info writer
	 */
	@Inject
	SelectShippingAddressResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("destinationInfoWriter")
			final DestinationInfoWriter destinationInfoWriter) {

		this.resourceServerName = resourceServerName;
		this.destinationInfoWriter = destinationInfoWriter;
	}


	/**
	 * Process select shipment destination info choice.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param address the address
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, DestinationInfo.PATH_PART, Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSelectShipmentDestinationInfoChoice(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			@AnyResourceUri
			final ResourceState<AddressEntity> address,
			final ResourceOperation operation) {

		ExecutionResult<Void> addressWriterResult =
				destinationInfoWriter.updateShippingAddressForShipment(scope, shipmentDetailsId, address);
		Ensure.successful(addressWriterResult);
		String shipmentUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId);
		String addressSelectorUri = URIUtil.format(shipmentUri, DestinationInfo.URI_PART, Selector.URI_PART);

		ExecutionResult<ResourceState<ResourceEntity>> result =
				ExecutionResultFactory.createCreateOK(addressSelectorUri, isExisting(addressWriterResult));
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private boolean isExisting(final ExecutionResult<Void> executionResult) {
		return ResourceStatus.READ_OK.equals(executionResult.getResourceStatus());
	}

}
