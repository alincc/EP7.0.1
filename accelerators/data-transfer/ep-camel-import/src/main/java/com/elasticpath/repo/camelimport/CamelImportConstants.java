package com.elasticpath.repo.camelimport;

/**
 * Camel import constants.
 */
public class CamelImportConstants {

	/**
	 * The constant used to locate the catalog feed data.
	 */
	public static final String IMPORT_DATA_DIR_PATH = "IMPORT_DATA_DIR_PATH";

	/**
	 * The constant used by properties and headers to get the changeset guid from an exchange.
	 */
	public static final String CHANGESET_GUID = "CHANGESET_GUID";

	/** The log appender for creating a new log file on a per-route, pre-execution basis. */
	public static final String APPENDER = "per-execution-log-appender";

	/** The log file path for the per-route, pre-execution logging. */
	public static final String LOG_FILE_PATH = "per-execution-log-file";

	/** The log file extension. */
	public static final String FILE_EXTENSION = ".log";

	/** The log file success suffix. */
	public static final String SUCCESS_SUFFIX = "SUCCESS";

	/** The log file failure suffix. */
	public static final String FAILURE_SUFFIX = "FAILURE";

	/** The log file prefix. */
	public static final String FILENAME_PREFIX = "Import";
}