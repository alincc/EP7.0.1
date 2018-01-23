/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Strategy for resolving the shipment details ID parameter.
 */
@Singleton
@Named("shipmentDetailsIdParameterStrategy")
public final class ShipmentDetailsIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final ShipmentDetailsLookup shipmentDetailsLookup;

	/**
	 * Constructor.
	 *
	 * @param shipmentDetailsLookup the shipment details lookup
	 */
	@Inject
	ShipmentDetailsIdParameterStrategy(
			@Named("shipmentDetailsLookup")
			final ShipmentDetailsLookup shipmentDetailsLookup) {

		this.shipmentDetailsLookup = shipmentDetailsLookup;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		return shipmentDetailsLookup.findShipmentDetailsIds(scope, userId).getData();
	}
}
