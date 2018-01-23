/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.testutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.editor.IFormPage;
import org.json.JSONObject;

import com.elasticpath.cmclient.core.helpers.EPCustomThemeUtil;
import com.elasticpath.cmclient.core.helpers.EPCustomThemeUtil.CustomStyle;
import com.elasticpath.cmclient.core.helpers.TestIdMapManager;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.util.ServiceUtil;

/**
 * Adds ids to frontend widgets that can be used by client-side javascript and test automation.
 * <WidgetsSupported>
 * MainMenu; 	(it's components)
 * CTabItem
 * CoolBar;
 * Table;
 * ToolBar;
 * Tree;
 * Text, Label, Button, Combo, Hyperlink; 	(created through <class>EpControlFactory<class/>)
 * </WidgetsSupported>
 */

public class EPWidgetIdUtil implements TestIdUtil {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String QUOTE = "'"; //$NON-NLS-1$
    private static final String FILE_PATH = "javascript/ep-test-support.js"; //$NON-NLS-1$
    private static int uniqueId;
    private static boolean initialized;

    /**
     * Constructor.
     */
    public EPWidgetIdUtil() {
        //no-op constructor.
    }

    /**
     * Sets consequent ids to the widgets every time this function is called.
     *
     * @param widget widget
     */
    @Override
    @Deprecated //TODO remove this function
    public void setUniqueId(final Widget widget) {
        setId(widget, Integer.toString(uniqueId));
        uniqueId++;
    }

    /**
     * Loads Javascript file to RWT resource manager.
     *
     * @throws IOException javascript file wasn't found
     */
    @Override
    public void initialize() throws IOException {
        TestIdMapManager.initializeEncodingMarkers();
        ResourceManager resourceManager = RWT.getResourceManager();
        if (!resourceManager.isRegistered(EPWidgetIdUtil.FILE_PATH)) {

            try (InputStream inputStream = EPWidgetIdUtil.class.getClassLoader().getResourceAsStream(EPWidgetIdUtil.FILE_PATH)) {
                resourceManager.register(EPWidgetIdUtil.FILE_PATH, inputStream);
            }
        }

        ClientFileLoader rwtService = ServiceUtil.getRWTService(ClientFileLoader.class);
        rwtService.requireJs(resourceManager.getLocation(EPWidgetIdUtil.FILE_PATH));
        this.setAppearance();
        initialized = true;
    }

    /**
     * Calls the EPTest javascript object to override SetAppearance calls.
     */
    @Override
    public void setAppearance() {
        String callClientJavaScriptFunction = "EPTest.overrideSetAppearances()";
        JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
        executor.execute(callClientJavaScriptFunction);
    }


    /**
     * Sets the testId onto the widget's id attribute.
     *
     * @param widget the Widget.
     * @param testId the testId
     */
    @Override
    public void setId(final Widget widget, final String testId) {
        if (widget != null && notDisposed(widget) && UITestUtil.isEnabled()) {
            String widgetId = WidgetUtil.getId(widget);
            String testIdJavaScriptSafe = (testId == null) ? EMPTY_STRING : clean(testId);
            String widgetType = widget.getClass().getSimpleName();

            mapWidgetIdToTestId(widgetId, testIdJavaScriptSafe, widgetType);
        }
    }

    private void mapWidgetIdToTestId(final String widgetId, final String testId, final String widgetType) {
        String callClientJavaScriptFunction = "EPTest.mapWidgetIdToTestId("
                + QUOTE + widgetId + QUOTE + ","
                + QUOTE + testId + QUOTE + ","
                + QUOTE + widgetType + QUOTE + ");";
        JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
        executor.execute(callClientJavaScriptFunction);
    }

    /**
     * Cleans the values, makes them easier to access via frontend code and ensures JavaScriptExecutor will not fail.
     *
     * @param value the value to clean up
     * @return acceptable value for JS
     */
    private String clean(final String value) {
        String processedValue = value.replaceAll("\n", SPACE);
        return processedValue.replaceAll("&", EMPTY_STRING);
    }

    private boolean notDisposed(final Widget widget) {
        return !widget.isDisposed();
    }

    /**
     * Attaches test ids to each TableItem.
     *
     * @param table table with items
     */
    @Override
    public void setTestIdsToTableItems(final Table table) {
        int testIdNum = 0;
        for (TableItem tableItem : table.getItems()) {
            this.setId(tableItem, tableItem.getText() + testIdNum);
            testIdNum++;
        }
    }

    /**
     * Attaches test ids to each CTabItem.
     *
     * @param tabFolder tabFolder containing all of the CTabItems
     */
    @Override
    public void setTestIdsToTabFolderItems(final IEpTabFolder tabFolder) {
        //Set test ids to the tabs
        for (CTabItem item : tabFolder.getSwtTabFolder().getItems()) {
            this.setId(item, item.getText());
        }
    }

    /**
     * Add test ids to the tabs inside of tab folder.
     *
     * @param tabFolder tab folder
     * @param page      form page
     */
    @Override
    public void addIdToMultiPageEditorTabFolder(final Composite tabFolder, final IFormPage page) {
        if (tabFolder instanceof CTabFolder) {
            CTabFolder folder = (CTabFolder) tabFolder;
            this.setId(folder, page.getId() + "_" + page.getIndex());
            CTabItem[] tabItems = folder.getItems();
            for (CTabItem tabItem : tabItems) {
                this.setId(tabItem, tabItem.getText());
            }
        }
    }

    /**
     * Add test ids to the tool bar Manager.
     *
     * @param toolBarManager tool bar manager
     * @param toolBarId      id for the toolbar
     */
    @Override
    public void setTestIdsToToolBarManager(final IToolBarManager toolBarManager, final String toolBarId) {
        //No pagination for this toolbar is present so pass the length of the toolbar so style will be not applied
        setTestIdsToToolBarManagerWithStyleForPagination(toolBarManager, toolBarManager.getItems().length, toolBarId);
    }

    /**
     * Add test ids to the tool bar Manager.
     *
     * @param toolBarManager        tool bar manager
     * @param beginningOfPagination index that indicates where all of the pagination tool items start in the ToolBar
     * @param toolBarId             id for the toolbar
     */
    @Override
    public void setTestIdsToToolBarManagerWithStyleForPagination(final IToolBarManager toolBarManager, final int beginningOfPagination,
            final String toolBarId) {
        if (toolBarManager instanceof ToolBarManager) {
            toolBarManager.update(true);
            ToolBarManager tbm = (ToolBarManager) toolBarManager;
            ToolBar control = tbm.getControl();

            setId(control, toolBarId);

            if (control != null) {
                setToolItemIdsForControl(beginningOfPagination, control);
            }
        }
    }

    private void setToolItemIdsForControl(final int beginningOfPagination, final ToolBar control) {
        int index = 0;
        for (ToolItem item : control.getItems()) {

            String text = item.getToolTipText();
            if (StringUtils.isEmpty(text)) {
                text = item.getText();
            }

            if (StringUtils.isNotEmpty(text)) {
                setId(item, text);
            }
            //Assuming pagination is at the end of the ToolBar
            if (index >= beginningOfPagination) {
                EPCustomThemeUtil.setCustomStyle(item, CustomStyle.PAGINATION, true);
                EPCustomThemeUtil.setCustomStyle(item.getControl(), CustomStyle.PAGINATION, true);
            }
            index++;
        }
    }

    /**
     * Add test ids to the Tree and its items.
     * All the tree items would get ids based on the text rather then localized messages
     * as all the items are created by user and are not predefined.
     *
     * @param tree   tree
     * @param treeId id to be assigned to the tree
     */
    @Override
    public void setTestIdsToTreeAndItsItems(final Tree tree, final String treeId) {
        if (tree == null) {
            return;
        }
        setId(tree, treeId);

        //Loop through the items and assign ids to them
        for (TreeItem item : tree.getItems()) {
            setId(item, item.getText());
        }
        addTreeListener(tree);
    }

    private void addTreeListener(final Tree tree) {
        tree.addTreeListener(new TreeListener() {
            @Override
            public void treeCollapsed(final TreeEvent treeEvent) {
                //empty
            }

            @Override
            public void treeExpanded(final TreeEvent treeEvent) {
                //Loop through the items and assign ids to them, call recursive function that will assign ids to its subtrees
                for (TreeItem item : tree.getItems()) {
                    assignIdsToSubTree(item);
                }
            }
        });
    }

    //Recursive call
    private void assignIdsToSubTree(final TreeItem tree) {
        for (TreeItem treeItem : tree.getItems()) {
            setId(treeItem, treeItem.getText());
            assignIdsToSubTree(treeItem);
        }
    }

    /**
     * Calls the setPostLoginWindowId() method on the client side to set an ID to the post login window.
     */
    @Override
    public void setPostLoginWindowId() {
        if (UITestUtil.isEnabled()) {
            String callClientJavaScriptFunction = "EPTest.setPostLoginWindowId()";
            JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
            executor.execute(callClientJavaScriptFunction);
        }
    }

    /**
     * send test-id maps to the client.
     * @param minifiedMap shortId to qualifiedFieldName map
     */
    @Override
    public void sendTestIdMapsToClient(final Map<String, String> minifiedMap) {
        if (initialized) {
            JSONObject minifiedJsonMap = new JSONObject(minifiedMap);
            String minifiedJsonString = minifiedJsonMap.toString();

            String callClientJavaScriptFunction = "EPTest.storeMinifiedMap(" + minifiedJsonString + ")";
            JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
            executor.execute(callClientJavaScriptFunction);
        }
    }

}
