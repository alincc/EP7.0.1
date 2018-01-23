/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.policy.StatePolicy;

/**
 * Test that the default state policy governable implementation applies policies and
 * adds action containers as expected.
 */
public class DefaultStatePolicyGovernableImplTest {

	private static final String TEST_CONTAINER = "testContainer"; //$NON-NLS-1$

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private StatePolicy statePolicy;
	
	private DefaultStatePolicyGovernableImpl defaultStateGovernableImpl;
	
	/**
	 * Set up required for all tests.
	 * 
	 * @throws java.lang.Exception in case of errors during setup
	 */
	@Before
	public void setUp() throws Exception {
		defaultStateGovernableImpl = new DefaultStatePolicyGovernableImpl();
	}

	/**
	 * Test applying a state policy iterates through the action containers.
	 */
	@Test
	public void testApplyStatePolicy() {
		defaultStateGovernableImpl.addPolicyActionContainer(TEST_CONTAINER);
		defaultStateGovernableImpl.applyStatePolicy(statePolicy);
		verify(statePolicy).apply(any(PolicyActionContainer.class));
	}
	
	/**
	 * Test adding a policy container.
	 */
	@Test
	public void testAddPolicyActionContainer() {
		defaultStateGovernableImpl.addPolicyActionContainer(TEST_CONTAINER);

		Map<String, PolicyActionContainer> actionContainers = defaultStateGovernableImpl.getPolicyActionContainers();
		assertEquals("There should be one container", 1, actionContainers.size()); //$NON-NLS-1$
		assertTrue("Containers map should contain the test container key", actionContainers.containsKey(TEST_CONTAINER)); //$NON-NLS-1$
		
		PolicyActionContainer container = actionContainers.get(TEST_CONTAINER);
		assertNotNull("action container should not be null", container); //$NON-NLS-1$
		assertEquals("New container should have the correct name", TEST_CONTAINER, container.getName()); //$NON-NLS-1$
	}
}
