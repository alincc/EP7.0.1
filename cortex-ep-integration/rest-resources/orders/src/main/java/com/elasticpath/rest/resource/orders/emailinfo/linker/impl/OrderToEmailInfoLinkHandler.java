/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.linker.impl;

import static com.elasticpath.rest.definition.emails.EmailsMediaTypes.EMAIL;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoConstants.EMAIL_INFO_NAME;
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels.EMAIL_FORM_REL;
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels.EMAIL_INFO_REV;
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels.EMAIL_REL;
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.ORDER_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.EmailsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;

/**
 * Link handler.
 */
@Singleton
@Named("orderToEmailInfoLinkHandler")
public class OrderToEmailInfoLinkHandler implements ResourceStateLinkHandler<InfoEntity> {

	@Inject
	@Named("ordersUriBuilderFactory")
	private OrdersUriBuilderFactory ordersUriBuilderFactory;

	@Inject
	@Named("emailFormUriBuilderFactory")
	private EmailFormUriBuilderFactory emailFormUriBuilderFactory;

	@Inject
	@Named("emailsUriBuilderFactory")
	private EmailsUriBuilderFactory emailsUriBuilderFactory;

	@Inject
	@Named("emailInfoLookup")
	private EmailInfoLookup emailInfoLookup;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<InfoEntity> resourceState) {

		InfoEntity infoEntity = resourceState.getEntity();
		if (!EMAIL_INFO_NAME.equals(infoEntity.getName())) {
			return newArrayList();
		}

		String scope = resourceState.getScope();
		String orderId = infoEntity.getInfoId();

		Collection<ResourceLink> resourceLinks = newArrayList(
				createOrderLink(orderId, scope),
				createEmailFormLink(scope)
		);

		createEmailLink(resourceLinks, scope, orderId);

		return resourceLinks;
	}

	private ResourceLink createOrderLink(final String orderId,
											final String scope) {

		String orderUri = ordersUriBuilderFactory.get()
				.setOrderId(orderId)
				.setScope(scope)
				.build();

		return create(
				orderUri,
				ORDER.id(),
				ORDER_REL,
				EMAIL_INFO_REV
		);
	}

	private ResourceLink createEmailFormLink(final String scope) {

		String emailFormUri = emailFormUriBuilderFactory.get()
				.setScope(scope)
				.build();

		return createNoRev(
				emailFormUri,
				EMAIL.id(),
				EMAIL_FORM_REL
		);
	}

	private void createEmailLink(final Collection<ResourceLink> resourceLinks,
									final String scope,
									final String orderId) {

		String emailId;
		try {
			emailId = Assign.ifSuccessful(emailInfoLookup.findEmailIdForOrder(scope, orderId));
		} catch (BrokenChainException bce) {
			emailId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (!isNullOrEmpty(emailId)) {
			String emailUri = emailsUriBuilderFactory.get()
					.setEmailId(emailId)
					.setScope(scope)
					.build();

			resourceLinks.add(createNoRev(
					emailUri,
					EMAIL.id(),
					EMAIL_REL
			));
		}
	}
}
