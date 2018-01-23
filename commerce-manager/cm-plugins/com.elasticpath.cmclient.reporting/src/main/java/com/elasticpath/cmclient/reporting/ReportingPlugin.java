/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.reporting;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.reporting.service.impl.CmReportServiceImpl;

/**
 * The activator class controls the plug-in life cycle.
 */
public class ReportingPlugin extends AbstractEpUIPlugin {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting"; //$NON-NLS-1$

	// The shared instance
	private static ReportingPlugin plugin;
	
	private static IReportEngine engine;
	
	/**
	 * The constructor.
	 */
	public ReportingPlugin() {
		//empty
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerCmReportService();
		engine = initializeEngine();
	}

	@Override
	protected void loadLocalizedMessages() {
		ReportingMessages.load();
	}


	/**
	 * Initialize the Birt Engine.
	 * @return IReportEngine the initialized Birt engine object
	 * @throws BirtException if initialization fails
	 */
	public static IReportEngine initializeEngine() throws BirtException {
		final EngineConfig config = new EngineConfig();
		
		Platform.startup(config);
		
		final IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		final IReportEngine engine = factory.createReportEngine(config);
		engine.changeLogLevel(java.util.logging.Level.WARNING);

		return engine;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		ReportingImageRegistry.disposeAllImages();
		deregisterCmReportService();
		engine.destroy();
		super.stop(context);
	}


	private static final String CM_REPORT_SERVICE_NAME = "cmReportService"; //$NON-NLS-1$
	private void registerCmReportService() {
		getCmSingletonUtil().addBean(CM_REPORT_SERVICE_NAME, new CmReportServiceImpl());
	}

	private void deregisterCmReportService() {
		getCmSingletonUtil().addBean(CM_REPORT_SERVICE_NAME, null);
	}

	private CmSingletonUtil getCmSingletonUtil() {
		return LoginManager.getInstance().getCmSingletonUtil();
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static ReportingPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Gets the reporting engine which is initialized when plugin starts.
	 * @return IReportEngine
	 */
	public static IReportEngine getEngine() {
		return engine;
	}
}
