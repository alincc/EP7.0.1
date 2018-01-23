/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;

/**
 * Test that <code>AbstractStatePolicyImpl</code> behaves as expected.
 */
public class AbstractStatePolicyImplTest {

	private static final String TEST_CONTAINER = "testContainer"; //$NON-NLS-1$

	private StatePolicy abstractStatePolicy;

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private BeanFactory beanFactory;
	@Mock
	private ChangeSetHelper changeSetHelper;

	/**
	 * Set up objects required for every test.
	 * 
	 * @throws java.lang.Exception in case of errors.
	 */
	@Before
	public void setUp() throws Exception {
		ServiceLocator.setBeanFactory(beanFactory);
		when(beanFactory.getBean(ChangeSetHelper.BEAN_ID)).thenReturn(changeSetHelper);

		abstractStatePolicy = new AbstractStatePolicyImpl() {

			@Override
			public EpState determineState(final PolicyActionContainer targetContainer) {
				if (TEST_CONTAINER.equals(targetContainer.getName())) {
					return EpState.EDITABLE;
				}
				return EpState.READ_ONLY;
			}

			@Override
			public void init(final Object dependentObject) {
				// Not required
			}
		};
	}

	/**
	 * Test that the apply method calls apply on the governables and sets the right state
	 * for state change targets.
	 */
	@Test
	public void testApply() {
		final PolicyActionContainer namedTestContainer = new PolicyActionContainer(TEST_CONTAINER);
		final PolicyActionContainer otherTestContainer = new PolicyActionContainer("other"); //$NON-NLS-1$
		final StatePolicyDelegate governable = mock(StatePolicyDelegate.class);
		final StateChangeTarget target = mock(StateChangeTarget.class);
		final StateChangeTarget otherTarget = mock(StateChangeTarget.class);

		namedTestContainer.addDelegate(governable);
		namedTestContainer.addTarget(target);
		abstractStatePolicy.apply(namedTestContainer);
		
		otherTestContainer.addTarget(otherTarget);
		abstractStatePolicy.apply(otherTestContainer);

		verify(governable).applyStatePolicy(abstractStatePolicy);
		verify(target).setState(EpState.EDITABLE);
		verify(otherTarget).setState(EpState.READ_ONLY);
	}

}
