/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
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
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionWriter;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Select shipping option.
 */
@Singleton
@Named("selectShippingOptionResourceOperator")
@Path(ResourceName.PATH_PART)
public final class SelectShippingOptionResourceOperator implements ResourceOperator {

	private final ShippingOptionWriter shippingOptionWriter;
	private final String resourceServerName;

	/**
	 * Default Constructor.
	 * @param shippingOptionWriter the shipping option writer
	 * @param resourceServerName   the resource server name
	 */
	@Inject
	SelectShippingOptionResourceOperator(
			@Named("shippingOptionWriter")
			final ShippingOptionWriter shippingOptionWriter,
			@Named("resourceServerName")
			final String resourceServerName) {

		this.shippingOptionWriter = shippingOptionWriter;
		this.resourceServerName = resourceServerName;
	}


	/**
	 * Process select shipping option info choice.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingOption the shipping option
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, ShippingOptionInfo.PATH_PART, Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSelectShippingOptionInfoChoice(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			@AnyResourceUri
			final ResourceState<ShippingOptionEntity> shippingOption,
			final ResourceOperation operation) {


		boolean selected = Assign.ifSuccessful(shippingOptionWriter.selectShippingOptionForShipment(scope, shipmentDetailsId,
				shippingOption.getEntity().getShippingOptionId()));
		String redirectUri = URIUtil.format(resourceServerName, scope, shipmentDetailsId, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		ExecutionResult<ResourceState<ResourceEntity>> result = ExecutionResultFactory.createCreateOK(redirectUri, selected);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

}
