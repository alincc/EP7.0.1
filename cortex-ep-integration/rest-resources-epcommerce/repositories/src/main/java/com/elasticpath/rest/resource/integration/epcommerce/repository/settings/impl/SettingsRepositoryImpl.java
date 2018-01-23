/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.settings.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;


/**
 * Implementation of SettingsRepository.
 */
@Singleton
@Named("settingsRepository")
public class SettingsRepositoryImpl implements SettingsRepository {

	private final SettingsReader settingsReader;


	/**
	 * Instantiates a new settings repository.
	 *
	 * @param settingsReader the settings lookup
	 */
	@Inject
	public SettingsRepositoryImpl(
			@Named("cachedSettingsReader")
			final SettingsReader settingsReader) {

		this.settingsReader = settingsReader;
	}


	@Override
	@CacheResult
	public ExecutionResult<String> getStringSettingValue(final String path, final String context) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				SettingValue settingValue = Assign.ifSuccessful(getSettingValue(path, context));
				return ExecutionResultFactory.createReadOK(settingValue.getValue());
			}
		}.execute();
	}

	@SuppressWarnings("PMD.AvoidCatchingThrowable")
	private ExecutionResult<SettingValue> getSettingValue(final String path, final String context) {

		//cannot be final
		ExecutionResult<SettingValue> result;

		try {
			SettingValue settingValue = settingsReader.getSettingValue(path, context);

			result = settingValue == null
							? ExecutionResultFactory.<SettingValue>createNotFound(
									"Setting value for path [" + path + "] and context [" + context + "] is not found")
							: ExecutionResultFactory.createReadOK(settingValue);

		} catch (Exception e) {
			result = ExecutionResultFactory.createServerError("Unable to resolve setting value");
		}
		return result;
	}
}
