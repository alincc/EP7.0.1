/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.AddressWriter;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.transform.AddressTransformer;
import com.elasticpath.rest.resource.addresses.validator.impl.AddressValidator;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Resource operator for the sub resource Address.
 */
@Singleton
@Named("addressResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, ResourceId.PATH_PART})
public class AddressResourceOperatorImpl implements ResourceOperator {

	private final AddressValidator addressValidator;

	private final AddressLookupStrategy addressLookupStrategy;
	private final AddressTransformer addressTransformer;
	private final ResourceOperationContext resourceOperationContext;

	private final AddressWriter addressWriter;

	/**
	 * Constructor.
	 * @param addressValidator address validator
	 * @param addressWriter address writer
	 * @param addressLookupStrategy address lookup
	 * @param addressTransformer address list transformer
	 * @param resourceOperationContext used to look up user id
	 */
	@Inject
	AddressResourceOperatorImpl(
			@Named("addressValidator")
			final AddressValidator addressValidator,
			@Named("addressWriter")
			final AddressWriter addressWriter,
			@Named("addressLookupStrategy")
			final AddressLookupStrategy addressLookupStrategy,
			@Named("addressTransformer")
			final AddressTransformer addressTransformer,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.addressValidator = addressValidator;
		this.addressWriter = addressWriter;
		this.addressLookupStrategy = addressLookupStrategy;
		this.addressTransformer = addressTransformer;
		this.resourceOperationContext = resourceOperationContext;
	}

	/**
	 * Handles the READ operations for a given address.
	 *
	 * @param scope the scope
	 * @param addressId address ID
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadAddress(
			@Scope
			final String scope,
			@ResourceId
			final String addressId,
			final ResourceOperation operation) {

		String userIdentifier = resourceOperationContext.getUserIdentifier();
		String decodedAddressId = Base32Util.decode(addressId);
		AddressEntity addressEntity = Assign.ifSuccessful(addressLookupStrategy.find(scope, userIdentifier, decodedAddressId));

		return OperationResultFactory.createReadOK(addressTransformer.transform(scope, addressEntity), operation);
	}

	/**
	 * Handles the UPDATE operations for a given address.
	 *
	 * @param addressId address ID
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.UPDATE)
	public OperationResult processUpdateAddress(
			@ResourceId
			final String addressId,
			final ResourceOperation operation) {

		AddressEntity addressForm = getPostedEntity(operation);

		ExecutionResult<Void> executionResult = addressWriter.updateAddress(addressId, addressForm);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(
				executionResult,
				operation
		);
	}

	/**
	 * Handles the DELETE operations for a given address.
	 *
	 * @param addressId address ID
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.DELETE)
	public OperationResult processDeleteAddress(
			@ResourceId
			final String addressId,
			final ResourceOperation operation) {

		ExecutionResult<Void> executionResult = addressWriter.deleteAddress(addressId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(
				executionResult,
				operation);
	}

	private AddressEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		AddressEntity addressForm = ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), AddressEntity.class);
		Ensure.successful(addressValidator.validate(addressForm));
		return addressForm;
	}
}
