/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.settings.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test that {@link SettingsRepositoryImpl} behaves as expected.
 */
public class SettingsRepositoryImplTest {

	private static final String SETTING_PATH = "/SETTINGS/PATH";
	private static final String STORE_CODE = "store";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final SettingsReader settingsReader = context.mock(SettingsReader.class);

	private final SettingsRepository repository = new SettingsRepositoryImpl(settingsReader);

	/**
	 * Test the behaviour of get string setting value.
	 */
	@Test
	public void testGetStringSettingValue() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SETTING_PATH, STORE_CODE);
				will(returnValue(mockSettingValue));
				oneOf(mockSettingValue).getValue();
				will(returnValue("some value"));
			}
		});

		ExecutionResult<String> result = repository.getStringSettingValue(SETTING_PATH, STORE_CODE);
		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result should be the value of the setting value returned by the core service", "some value", result.getData());
	}

	/**
	 * Test the behaviour of get string setting value when the settings reader fails.
	 */
	@Test
	public void testGetStringSettingValueWhenReaderFails() {
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SETTING_PATH, STORE_CODE);
				will(throwException(new EpServiceException("No such setting")));
			}
		});

		ExecutionResult<String> result = repository.getStringSettingValue(SETTING_PATH, STORE_CODE);
		assertTrue("The operation should have been failed", result.isFailure());
		assertEquals("The status should be SERVER ERROR", ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}

}
