/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.DefaultCartIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;

@RunWith(MockitoJUnitRunner.class)
public class RootToDefaultCartLinkHandlerTest {

	private static final String SCOPE = "scope";

	@Mock
	IdentifierTransformerProvider transformerProvider;
	@Mock
	ResourceOperationContext operationContext;
	@Mock
	IdentifierTransformer scopeTransformer;
	@Mock
	IdentifierPart scopeIdentifier;
	@Mock
	ResourceIdentifier rootIdentifier;

	@InjectMocks
	RootToDefaultCartLinkHandler classUnderTest;

	@Test
	public void testToIdentifiers() {
		when(transformerProvider.forUriPart("base.scope"))
			.thenReturn(scopeTransformer);
		when(scopeTransformer.uriPartsToIdentifier(any(Iterator.class)))
			.thenReturn(scopeIdentifier);

		Subject testSubject = TestSubjectFactory.createWithScope(SCOPE);
		when(operationContext.getSubject())
			.thenReturn(testSubject);

		Iterable<ResourceIdentifier> actualIterable = classUnderTest.toIdentifiers(rootIdentifier);

		List<ResourceIdentifier> actual = ImmutableList.copyOf(actualIterable);
		assertThat(actual, Matchers.hasSize(1));
		ResourceIdentifier actualIdentifier = actual.get(0);
		assertThat(actualIdentifier, Matchers.instanceOf(DefaultCartIdentifier.class));
		DefaultCartIdentifier typedIdentifier = (DefaultCartIdentifier) actualIdentifier;
		assertEquals(scopeIdentifier, typedIdentifier.getScope());
	}
}