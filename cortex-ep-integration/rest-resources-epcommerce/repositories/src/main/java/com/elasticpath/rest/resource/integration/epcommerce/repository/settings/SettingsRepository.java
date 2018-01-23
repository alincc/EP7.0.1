/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.settings;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Repository for Settings Values.
 */
public interface SettingsRepository {

	/**
	 * Retrieves the string setting value for a setting path.
	 *
	 * @param path the path to setting.
	 * @param context the context.
	 * @return the string setting value.
	 */
	ExecutionResult<String> getStringSettingValue(String path, String context);
}
