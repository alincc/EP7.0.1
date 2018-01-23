/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.shiro.subject.PrincipalCollection;
import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;

/**
 * Test class for {@link PurchaseIdParameterStrategy}.
 */
public final class PurchaseIdParameterStrategyTest {

	private static final String PURCHASE_ID = "a purchase Id";
	private static final String USER_ID = "7F4E992F-9CFC-E648-BA11-DF1D5B23968F";
	private static final String SCOPE = "a scope";
	private static final PrincipalCollection PRINCIPALS = TestSubjectFactory.createCollectionWithScopeAndUserId(SCOPE, USER_ID);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private PurchaseLookup mockLookup;

	private PurchaseIdParameterStrategy permissionStrategy;


	/**
	 * Sets up the test.
	 */
	@Before
	public void setUp() {
		permissionStrategy = new PurchaseIdParameterStrategy(mockLookup);
	}

	/**
	 * Tests getting a parameter value.
	 */
	@Test
	public void testGetParameterValue() {
		context.checking(new Expectations() {
			{
				allowing(mockLookup).findPurchaseIds(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Collections.singleton(PURCHASE_ID))));
			}
		});
		String purchaseIdString = permissionStrategy.getParameterValue(PRINCIPALS);

		assertEquals("Purchase id is wrong", PURCHASE_ID, purchaseIdString);
	}

	/**
	 * Test handling Failure Result.
	 */
	@Test
	public void testExecutionResultFailure() {
		context.checking(new Expectations() {
			{
				allowing(mockLookup).findPurchaseIds(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createNotFound("Not Found")));
			}
		});

		String purchaseId = permissionStrategy.getParameterValue(PRINCIPALS);
		assertThat("Purchase id should be empty", purchaseId, Matchers.isEmptyOrNullString());
	}
}
