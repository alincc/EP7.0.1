/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Contains an image registry for the plugin, and disposes of all loaded images
 * when the plugin is unloaded.
 */
public final class ReportingImageRegistry {
	
	/** RunReport Icon. */
	public static final ImageDescriptor RUN_REPORT = getImageDescriptor("report_run.png"); //$NON-NLS-1$
	/** Excel Export Icon. */
	public static final ImageDescriptor EXPORT_EXCEL = getImageDescriptor("report_excel.png"); //$NON-NLS-1$
	/** PDF Export Icon. */
	public static final ImageDescriptor EXPORT_PDF = getImageDescriptor("report_pdf.png"); //$NON-NLS-1$
	/** CSV Export Icon. */
	public static final ImageDescriptor EXPORT_CSV = getImageDescriptor("report_csv.png"); //$NON-NLS-1$
		
	private ReportingImageRegistry() {
		//util class
	}
	
	// HashMap for storing the images
	private static final Map<ImageDescriptor, Image> IMAGE_MAP = new HashMap<ImageDescriptor, Image>();
	
	/**
	 * Returns and instance of <code>Image</code> of an <code>ImageDescriptor</code>.
	 * 
	 * @param imageDescriptor the image descriptor
	 * @return instance of an <code>Image</code>
	 */
	public static Image getImage(final ImageDescriptor imageDescriptor) {
		if (ReportingImageRegistry.IMAGE_MAP.containsKey(imageDescriptor)) {
			return ReportingImageRegistry.IMAGE_MAP.get(imageDescriptor);
		}
		final Image image = imageDescriptor.createImage();
		ReportingImageRegistry.IMAGE_MAP.put(imageDescriptor, image);
		return image;
	}
	
	/**
	 * Disposes all the images in the <code>HashMap</code>.
	 * 
	 */
	static void disposeAllImages() {
		for (final ImageDescriptor desc : ReportingImageRegistry.IMAGE_MAP.keySet()) {
			final Image image = ReportingImageRegistry.IMAGE_MAP.get(desc);
			if (!image.isDisposed()) {
				image.dispose();
			}
		}
	}
	
	/**
	 * Gets the <code>ImageDescriptor</code> of an image by its name.
	 * 
	 * @param imageName the image file name
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String imageName) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ReportingPlugin.PLUGIN_ID, "/icons/" + imageName); //$NON-NLS-1$
	}	
}
