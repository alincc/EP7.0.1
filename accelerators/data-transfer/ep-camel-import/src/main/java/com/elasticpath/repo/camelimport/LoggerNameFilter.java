/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.repo.camelimport;

import java.util.List;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Denies the log even unless the logger name begin with an expected prefix. E.g., only allow "com.elasticpath.importexport" logging.
 */
public final class LoggerNameFilter extends Filter {

	private List<String> prefixes;

	@Override
	public int decide(final LoggingEvent event) {
		int returnValue = Filter.DENY;
		String loggerName = event.getLoggerName();
		if (loggerName != null && prefixes != null) {
			for (String prefix : prefixes) {
				if (loggerName.startsWith(prefix)) {
					returnValue = Filter.ACCEPT;
					break;
				}
			}
		}
		return returnValue;
	}

	public void setPrefixes(final List<String> prefixes) {
		this.prefixes = prefixes;
	}
}