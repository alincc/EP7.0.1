/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.AddressWriter;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressWriterStrategy;

/**
 * Core implementation of {@link AddressWriter}.
 */
@Singleton
@Named("addressWriter")
public final class AddressWriterImpl implements AddressWriter {

	private final AddressWriterStrategy addressWriterStrategy;
	private final ResourceOperationContext operationContext;

	/**
	 * Constructor.
	 * @param addressWriterStrategy address writer
	 * @param operationContext get user identifier
	 */
	@Inject
	AddressWriterImpl(
			@Named("addressWriterStrategy")
			final AddressWriterStrategy addressWriterStrategy,
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext) {

		this.addressWriterStrategy = addressWriterStrategy;
		this.operationContext = operationContext;
	}

	@Override
	public ExecutionResult<Void> updateAddress(final String addressId, final AddressEntity addressEntity) {
		String decodedAddressId = Base32Util.decode(addressId);
		String userId = getUserId();
		AddressEntity updatedAddressEntity = updateAddressEntity(addressEntity, decodedAddressId);
		return addressWriterStrategy.update(updatedAddressEntity, userId);
	}

	@Override
	public ExecutionResult<Void> deleteAddress(final String addressId) {
		String userId = getUserId();
		String decodedAddressId = Base32Util.decode(addressId);
		return addressWriterStrategy.delete(userId, decodedAddressId);
	}

	@Override
	public ExecutionResult<String> createAddress(final AddressEntity addressEntity, final String scope) {

		String userId = getUserId();

		ExecutionResult<String> createAddressResult = addressWriterStrategy.create(userId, addressEntity, scope);
		Ensure.successful(createAddressResult);

		return ExecutionResultFactory.createCreateOKWithData(
				Base32Util.encode(createAddressResult.getData()), isExistingAddress(createAddressResult));

	}

	private AddressEntity updateAddressEntity(
			final AddressEntity addressEntity,
			final String decodedAddressId) {
		return AddressEntity.builderFrom(addressEntity).withAddressId(decodedAddressId).build();
	}

	private String getUserId() {
		return operationContext.getUserIdentifier();
	}

	private boolean isExistingAddress(final ExecutionResult<String> createAddressResult) {
		return ResourceStatus.READ_OK.equals(createAddressResult.getResourceStatus());
	}
}
