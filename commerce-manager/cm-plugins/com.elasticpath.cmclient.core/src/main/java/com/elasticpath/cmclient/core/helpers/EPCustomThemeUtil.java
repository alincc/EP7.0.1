/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import com.elasticpath.cmclient.core.util.ServiceUtil;

/**
 * Sets RWT custom variant.
 * <p>
 * CustomStyle contains keys that will be attached to specific widgets.
 * To style this widget use key that has been set to it inside of CSS.
 * <p>
 * CSS:
 * | *.ImageHyperlink {
 * |		font: bold 12px "Noto Sans", Arial, Helvetica, sans-serif;
 * | }
 * ImageHyperlink is <CustomStyle> in this case.
 */
public final class EPCustomThemeUtil {
	/**
	 * Ids used to target specific widgets that need styling.
	 * Refer to such object with the following ids in the CSS file.
	 */
	public enum CustomStyle {
		/**
		 * Section.
		 */
		SECTION("Section"), //$NON-NLS-1$
		/**
		 * ImageHyperlink.
		 */
		IMAGE_HYPERLINK("ImageHyperlink"), //$NON-NLS-1$
		/**
		 * Bold Label.
		 */
		BOLD("StyleBold"), //$NON-NLS-1$
		/**
		 * IPagination.
		 */
		PAGINATION("IPagination"), //$NON-NLS-1$
		/**
		 * Main tool bar.
		 */
		MAIN_TOOLBAR("MainToolBar"), //$NON-NLS-1$
		/**
		 * Tool bar block which is separated by big separators.
		 */
		MAIN_TOOLBAR_BLOCK("MainToolBarBlock"), //$NON-NLS-1$
		/**
		 * Tool bar item.
		 */
		MAIN_TOOLBAR_ITEM("MainToolBarItem"), //$NON-NLS-1$
		/**
		 * Small separator.
		 */
		MAIN_TOOLBAR_SMALL_SEPARATOR("MainToolbarSmallSeparator"); //$NON-NLS-1$

		private final String cssId;

		/**
		 * Constructor.
		 *
		 * @param cssId id used in css
		 */
		CustomStyle(final String cssId) {
			this.cssId = cssId;
		}
	}

	private static final String INPUT_TYPE = "$input"; //$NON-NLS-1$
	private static final String EL_TYPE = "$el"; //$NON-NLS-1$
	private static final String UNDERSCORE = "_"; //$NON-NLS-1$
	private static final String STYLE_ID = "style_id"; //$NON-NLS-1$

	private EPCustomThemeUtil() {
		//utility class
	}

	/**
	 * @param widget               widget.
	 * @param style                id used in css to style widget
	 * @param appendWithWidgetType flag that concatenates widget type to css id
	 */
	public static void setCustomStyle(final Widget widget, final CustomStyle style, final boolean appendWithWidgetType) {
		if (widget != null && !widget.isDisposed()) {
			String elementType = widget instanceof Text ? INPUT_TYPE : EL_TYPE;
			String widgetId = WidgetUtil.getId(widget);

			String value = style.cssId;
			if (appendWithWidgetType) {
				value += UNDERSCORE + widget.getClass().getSimpleName();
			}

			widget.setData(RWT.CUSTOM_VARIANT, value);

			//Add attribute to HTMl for editing purposes
			//TODO To be removed
			executeJavascript(
				"rap.getObject('", widgetId, "').",
				elementType,
				".attr('" + STYLE_ID + "', '", value + "');");
		}
	}

	private static void executeJavascript(final String... widgetScripts) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("try{");
		for (String widgetScript : widgetScripts) {
			stringBuilder.append(widgetScript);
		}
		stringBuilder.append("}catch(e){}");
        JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
        executor.execute(stringBuilder.toString());
	}
}
