/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.command.impl;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.emails.command.ReadEmailFormCommand;
import com.elasticpath.rest.resource.emails.rel.EmailResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test for {@link ReadEmailFormCommandImpl}.
 */
public final class ReadEmailFormCommandImplTest {

	private static final String RESOURCE_NAME = "RESOURCE_NAME";
	private static final String SCOPE = "SCOPE";
	private static final String BASE_URI = URIUtil.format(RESOURCE_NAME, SCOPE);
	private static final String FORM_URI = URIUtil.format(RESOURCE_NAME, SCOPE, Form.URI_PART);

	/**
	 * Test execute.
	 */
	@Test
	public void testExecute() {
		ResourceLink expectedCreateEmailLink = ResourceLinkFactory.createUriRel(BASE_URI, EmailResourceRels.CREATE_EMAIL_ACTION_REL);

		ReadEmailFormCommand readEmailFormCommand = createReadEmailFormCommand(RESOURCE_NAME, SCOPE);

		ExecutionResult<ResourceState<EmailEntity>> result = readEmailFormCommand.execute();

		assertTrue("This should result in a successful operation.", result.isSuccessful());
		assertEquals("This should have the expected resource status.", ResourceStatus.READ_OK, result.getResourceStatus());

		ResourceState<EmailEntity> emailRepresentation = result.getData();

		assertResourceState(emailRepresentation)
			.self(SelfFactory.createSelf(FORM_URI))
			.linkCount(1)
			.containsLink(expectedCreateEmailLink);
	}

	private ReadEmailFormCommand createReadEmailFormCommand(final String rootResourceName, final String scope) {
		ReadEmailFormCommandImpl readEmailFormCommand = new ReadEmailFormCommandImpl(rootResourceName);
		ReadEmailFormCommand.Builder builder = new ReadEmailFormCommandImpl.BuilderImpl(readEmailFormCommand).setScope(scope);
		return builder.build();
	}

}
