/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import com.elasticpath.cmclient.admin.AdminSectionType;
import com.elasticpath.cmclient.admin.IAdminSection;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * The view that allows a user to select a specific view from different admin plugins.
 */
public class AdminSectionsNavigationView extends ViewPart {
	/** View ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.views.AdminSectionsNavigationView"; //$NON-NLS-1$
	private static final int SECTION_STYLE = ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE;

	@Override
	public void createPartControl(final Composite parent) {
		FormToolkit toolkit = EpControlFactory.getInstance().createFormToolkit();
		ScrolledForm form = toolkit.createScrolledForm(parent);
		Composite formBody = form.getBody();
		formBody.setLayout(new TableWrapLayout());
		formBody.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

		for (AdminSectionType adminSectionType : AdminSectionType.getSections()) {
			IAdminSection adminSection = adminSectionType.getAdminSection();

			Section section = toolkit.createSection(formBody, SECTION_STYLE);
			section.setText(adminSectionType.getLocalizedMessage().getValue());
			section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
			section.setEnabled(adminSection.isAuthorized());
			section.setExpanded(adminSection.isAuthorized());

			adminSection.createControl(toolkit, section, getSite());
		}
	}

	@Override
	public void setFocus() {
		// not implemented
	}

}
