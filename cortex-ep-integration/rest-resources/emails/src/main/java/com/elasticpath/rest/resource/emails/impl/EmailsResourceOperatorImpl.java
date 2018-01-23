/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.emails.EmailLookup;
import com.elasticpath.rest.resource.emails.command.ReadEmailFormCommand;
import com.elasticpath.rest.resource.emails.command.ReadEmailListCommand;
import com.elasticpath.rest.resource.emails.integration.EmailWriterStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Processes the resource operation on emails.
 */
@Singleton
@Named("emailsResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART})
public final class EmailsResourceOperatorImpl implements ResourceOperator {

	private final String resourceServerName;
	private final EmailLookup emailLookup;
	private final Provider<ReadEmailFormCommand.Builder> readEmailFormCommandBuilder;
	private final EmailWriterStrategy emailWriterStrategy;
	private final Provider<ReadEmailListCommand.Builder> readEmailListCommandBuilder;

	/**
	 * Instantiates a new email resource operator.
	 * @param resourceServerName resource server name
	 * @param emailLookup the read email lookup
	 * @param readEmailFormCommandBuilder the read email form command builder
	 * @param emailWriterStrategy the create email writer
	 * @param readEmailListCommandBuilder the read email list command builder
	 */
	@Inject
	EmailsResourceOperatorImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("emailLookup")
			final EmailLookup emailLookup,
			@Named("readEmailFormCommandBuilder")
			final Provider<ReadEmailFormCommand.Builder> readEmailFormCommandBuilder,
			@Named("emailWriterStrategy")
			final EmailWriterStrategy emailWriterStrategy,
			@Named("readEmailListCommandBuilder")
			final Provider<ReadEmailListCommand.Builder> readEmailListCommandBuilder) {
		this.resourceServerName = resourceServerName;
		this.emailLookup = emailLookup;
		this.readEmailFormCommandBuilder = readEmailFormCommandBuilder;
		this.emailWriterStrategy = emailWriterStrategy;
		this.readEmailListCommandBuilder = readEmailListCommandBuilder;
	}


	/**
	 * Process reading an email.
	 *
	 * @param scope the scope
	 * @param emailId the email id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadEmail(
			@Scope
			final String scope,
			@ResourceId
			final String emailId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<EmailEntity>> result = emailLookup.getEmail(scope, emailId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles READ form operation for an email.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path(Form.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadForm(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Command<ResourceState<EmailEntity>> command = readEmailFormCommandBuilder.get()
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<EmailEntity>> result = command.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process creating or updating an email.
	 *
	 * @param scope the scope
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.CREATE)
	public OperationResult processCreateEmail(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		EmailEntity emailForm = getPostedEntity(operation);
		Ensure.successful(emailWriterStrategy.createEmail(emailForm));
		String emailId = Base32Util.encode(emailForm.getEmail());
		String locationUri = URIUtil.format(resourceServerName, scope, emailId);

		ExecutionResult<ResourceState<ResourceEntity>> result = ExecutionResultFactory.createCreateOK(locationUri, false);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process reading a list of emails for a user.
	 *
	 * @param scope the scope
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadEmails(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Command<ResourceState<LinksEntity>> command = readEmailListCommandBuilder.get()
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private EmailEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		return ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), EmailEntity.class);
	}
}
