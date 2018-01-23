/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.transform;

import static com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels.PURCHASE_REL;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * The Shipment Links Transformer.
 */
@Singleton
@Named("shipmentLinksTransformer")
public final class ShipmentLinksTransformer implements
		TransformRfoToResourceState<LinksEntity, Collection<String>, PurchaseEntity> {

	private final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;
	/**
	 * Constructor.
	 *  @param shipmentsUriBuilderFactory the shipmentsUriBuilderFactory
	 *
	 */
	@Inject
	ShipmentLinksTransformer(
			@Named("shipmentsUriBuilderFactory")
			final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory) {
		this.shipmentsUriBuilderFactory = shipmentsUriBuilderFactory;
	}

	@Override
	public ResourceState<LinksEntity> transform(
			final Collection<String> shipmentIds, final ResourceState<PurchaseEntity> purchaseRepresentation) {

		String purchaseUri = ResourceStateUtil.getSelfUri(purchaseRepresentation);

		ShipmentsUriBuilder shipmentsUriBuilder = shipmentsUriBuilderFactory.get();
		shipmentsUriBuilder.setSourceUri(purchaseUri);
		String uri = shipmentsUriBuilder.build();
		Self self = SelfFactory.createSelf(uri);

		Collection<ResourceLink> links = new ArrayList<>();

		ResourceLink purchaseLink = ResourceLinkFactory.createNoRev(purchaseUri, PurchasesMediaTypes.PURCHASE.id(), PURCHASE_REL);
		links.add(purchaseLink);


		for (String shipmentId : shipmentIds) {
			String elementUri = shipmentsUriBuilder.setShipmentId(shipmentId).build();

			ResourceLink link = ElementListFactory.createElementOfList(elementUri, ShipmentsMediaTypes.SHIPMENT.id());
			links.add(link);
		}

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();
	}
}
