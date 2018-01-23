package com.elasticpath.repo.camelimport.services;

import java.io.FileNotFoundException;

import com.elasticpath.importexport.common.exception.ConfigurationException;

/**
 * Methods to import data into catalog.
 */
public interface ImporterService {

	/**
	 * Import data from a directory.
	 *
	 * @param dataDir data directory.
	 * @throws FileNotFoundException file not found for importconfiguration.xml
	 * @throws ConfigurationException file configuration example
	 */
	void executeImport(String dataDir) throws FileNotFoundException, ConfigurationException;

	/**
	 * Import data from a directory into a changeset.
	 *
	 * @param dataDir data directory.
	 * @param changeSetGuid changeset guid.
	 * @throws FileNotFoundException file not found for importconfiguration.xml
	 * @throws ConfigurationException file configuration example
	 */
	void executeImport(String dataDir, String changeSetGuid) throws FileNotFoundException, ConfigurationException;
}
