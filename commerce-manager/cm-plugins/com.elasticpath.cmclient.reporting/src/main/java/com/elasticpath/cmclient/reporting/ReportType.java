/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.reporting;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


/**
 * A Report that can be added to the Reporting navigation view. 
 * A ReportType is represented by a configElement as an extension to the Reporting
 * plugin, so this class knows how to parse such an element.
 */
public class ReportType implements Comparable<ReportType> {
	private static final Logger LOG = Logger.getLogger(ReportType.class);

	private static final String EXTENSION_NAME = "reports"; //$NON-NLS-1$

	private static final String TAG_REPORTTYPE = "report"; //$NON-NLS-1$
	
	private static final String DESIGN_FILE = "designFile"; //$NON-NLS-1$

	private static final String ATT_ID = "id"; //$NON-NLS-1$

	private static final String ATT_NAME = "name"; //$NON-NLS-1$

	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private final IConfigurationElement configElement;

	private final String sectionId;

	private final String name;
	
	private final URL fileLocation;

	private IReport report;

	private static List<ReportType> cachedTypes;

	/**
	 * Constructs a ReportType from a configurationElement.
	 * 
	 * @param configElement the extension point configuration element that defines a ReportType
	 */
	public ReportType(final IConfigurationElement configElement) {
		this.configElement = configElement;
		sectionId = getAttribute(configElement, ATT_ID);
		name = getAttribute(configElement, ATT_NAME);
		fileLocation = getResource(configElement, DESIGN_FILE);
		// Make sure that the class is defined, but don't load it
		getAttribute(configElement, ATT_CLASS);
	}

	/**
	 * Returns the report's name.
	 * 
	 * @return name the name of the report.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a report or null if initiation of the report failed.
	 *
	 * @return the report
	 */
	public IReport getReport() {
		if (report != null) {
			return report;
		}
		try {
			report = (IReport) configElement.createExecutableExtension(ATT_CLASS);
		} catch (Exception e) {
			LOG.error("Failed to instantiate report: " //$NON-NLS-1$
					+ configElement.getAttribute(ATT_CLASS) + " in type: " //$NON-NLS-1$
					+ sectionId + " in plugin: " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier(), e);
		}
		return report;
	}

	private static String getAttribute(final IConfigurationElement configElement, final String name) {
		String value = configElement.getAttribute(name);
		if (value == null) {
			throw new IllegalArgumentException("Missing " + name + " attribute"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return value;
	}
	
	private static URL getResource(final IConfigurationElement configElement, final String name) {
		String resource = configElement.getAttribute(name);
		URL url = null;
		Bundle bundle = null;
		String bundleId = ""; //$NON-NLS-1$

		if (resource != null) {
			bundleId = configElement.getContributor().getName();
			bundle = Platform.getBundle(bundleId);
			if (bundle == null) {
				return null;
			}
			try {
				url = FileLocator.resolve(FileLocator.find(bundle, new Path(resource), null));
			} catch (IOException e) {
				LOG.debug(e.getMessage());
			}		
		}
		return url;
		
	}

	/**
	 * Return all the <code>ReportType</code>s that are plugged into the platform,
	 * sorted alphabetically by report name.
	 * 
	 * @return all the <code>ReportType</code>s that are plugged into the platform
	 */
	public static List<ReportType> getReportTypes() {
		if (cachedTypes != null) {
			return cachedTypes;
		}
		cachedTypes = new ArrayList<ReportType>();
		LOG.debug("Retrieving all plugin extension points for " + EXTENSION_NAME); //$NON-NLS-1$
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(ReportingPlugin.PLUGIN_ID, EXTENSION_NAME).getExtensions();
		LOG.debug("Retrieved " + extensions.length + " extensions"); //$NON-NLS-1$ //$NON-NLS-2$
		for (IExtension extension : extensions) {
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				ReportType reportType = parseItem(configElement);
				if (reportType != null) {
					cachedTypes.add(reportType);
				}
			}
		}
		Collections.sort(cachedTypes);
		return Collections.unmodifiableList(cachedTypes);
	}
	
	/**
	 * Finds first report type with the same reportName or null if no such report has been found.
	 * 
	 * @param reportName the id of the report
	 * @return report needed
	 */
	public static ReportType findReportTypeByName(final String reportName) {
		getReportTypes();
		for (ReportType reportType : cachedTypes) {
			if (reportType.getName().equalsIgnoreCase(reportName)) {
				return reportType;
			}
		}
		return null;
	}

	/**
	 * Parse a configuration element representing a ReportType and return an object instance.
	 * 
	 * @param configElement an element representing the ReportType
	 * @return an instance of ReportType
	 */
	public static ReportType parseItem(final IConfigurationElement configElement) {
		if (!configElement.getName().equals(TAG_REPORTTYPE)) {
			LOG.error("Unknown element: " + configElement.getName()); //$NON-NLS-1$
			return null;
		}
		try {
			LOG.debug("Creating new ReportType"); //$NON-NLS-1$
			return new ReportType(configElement);
		} catch (Exception e) {
			String msg = "Failed to load ReportType with name " //$NON-NLS-1$
					+ configElement.getAttribute(ATT_NAME) + " in " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier();
			LOG.error(msg, e);
			return null;
		}
	}

	/**
	 * {@inheritDoc}	 
	 */
	public int compareTo(final ReportType reportType) {
		return this.getName().compareTo(reportType.getName());
	}

	/**
	 * Gets the location of the design file.
	 * @return path as URL
	 */
	public URL getFileLocation() {
		return fileLocation;
	}
}
