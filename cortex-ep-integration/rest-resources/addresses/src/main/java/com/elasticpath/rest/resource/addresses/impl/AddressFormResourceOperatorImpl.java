/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.resource.addresses.AddressWriter;
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.addresses.validator.impl.AddressValidator;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource operator for the addresses resource.
 */
@Singleton
@Named("addressFormResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART})
public final class AddressFormResourceOperatorImpl implements ResourceOperator {

	private final String resourceServerName;
	private final AddressValidator addressValidator;
	private final AddressWriter addressWriter;
	private final AddressLinkCreationHelper addressLinkCreationHelper;

	/**
	 * Constructor.
	 * @param resourceServerName  resource Server Name
	 * @param addressValidator address validator
	 * @param addressWriter address writer
	 * @param addressLinkCreationHelper link creation helper
	 */
	@Inject
	AddressFormResourceOperatorImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("addressValidator")
			final AddressValidator addressValidator,
			@Named("addressWriter")
			final AddressWriter addressWriter,
			@Named("addressLinkCreationHelper")
			final AddressLinkCreationHelper addressLinkCreationHelper) {

		this.resourceServerName = resourceServerName;
		this.addressValidator = addressValidator;
		this.addressWriter = addressWriter;
		this.addressLinkCreationHelper = addressLinkCreationHelper;
	}

	/**
	 * Process read on address form.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path(Form.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadAddressForm(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		String baseUri = URIUtil.format(resourceServerName, scope);

		String formSelfUri = URIUtil.format(baseUri, Form.URI_PART);
		Self formSelf = SelfFactory.createSelf(formSelfUri);

		ResourceState<AddressEntity> addressEntityResourceState = ResourceState.Builder.create(createEmptyAddressEntity())
				.withSelf(formSelf)
				.withScope(scope)
				.withLinks(createAddressFormLinks(baseUri))
				.withResourceInfo(
						ResourceInfo.builder()
								.withMaxAge(AddressResourceRels.MAX_AGE)
								.build()
				)
				.build();

		return OperationResultFactory.createReadOK(addressEntityResourceState, operation);
	}

	/**
	 * Creates address from address form.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.CREATE)
	public OperationResult processCreateAddressForm(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		AddressEntity addressForm = getAddressEntity(operation);

		ExecutionResult<String> newAddressResult = addressWriter.createAddress(addressForm, scope);
		String newAddressId = Assign.ifSuccessful(newAddressResult);

		String resultLocation = URIUtil.format(resourceServerName, scope, newAddressId);
		boolean isExistingAddress = ResourceStatus.READ_OK.equals(newAddressResult.getResourceStatus());
		ExecutionResult<ResourceState<ResourceEntity>> result =  ExecutionResultFactory.createCreateOK(resultLocation, isExistingAddress);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private AddressEntity getAddressEntity(final ResourceOperation operation) {
		ResourceState address = operation.getResourceState();
		Ensure.notNull(address, OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		AddressEntity addressForm = ResourceTypeFactory.adaptResourceEntity(address.getEntity(), AddressEntity.class);
		Ensure.successful(addressValidator.validate(addressForm));
		return addressForm;
	}

	private Collection<ResourceLink> createAddressFormLinks(final String baseUri) {
		Collection<ResourceLink> resourceLinks = new ArrayList<ResourceLink>();
		resourceLinks.add(addressLinkCreationHelper.createSubmitCreateAddressLink(baseUri));
		return resourceLinks;
	}

	private AddressEntity createEmptyAddressEntity() {
		AddressDetailEntity addressDetailsEntity = AddressDetailEntity.builder()
				.withCountryName(StringUtils.EMPTY)
				.withRegion(StringUtils.EMPTY)
				.withLocality(StringUtils.EMPTY)
				.withPostalCode(StringUtils.EMPTY)
				.withStreetAddress(StringUtils.EMPTY)
				.withExtendedAddress(StringUtils.EMPTY)
				.build();

		NameEntity name = NameEntity.builder()
				.withFamilyName(StringUtils.EMPTY)
				.withGivenName(StringUtils.EMPTY)
				.build();

		return AddressEntity.builder()
				.withAddress(addressDetailsEntity)
				.withName(name).build();
	}
}
