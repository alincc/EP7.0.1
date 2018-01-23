/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * The LineItem Transformer.
 */
@Singleton
@Named("lineItemDetailsTransformer")
public final class LineItemDetailsTransformer implements TransformToResourceState<LineItemEntity, LineItemEntity> {

	private final CartsUriBuilderFactory cartsUriBuilderFactory;
	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;

	/**
	 * Default Constructor.
	 *
	 * @param cartsUriBuilderFactory         carts URI Builder Provider.
	 * @param cartLineItemsUriBuilderFactory carts line items URI Builder Provider.
	 */
	@Inject
	public LineItemDetailsTransformer(
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory,
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory) {

		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
	}

	/**
	 * Transforms a {@link LineItemEntity} to a {@link ResourceState}.
	 *
	 * @param scope  the scope
	 * @param entity the line item entity
	 * @return the cart line item representation
	 */
	public ResourceState<LineItemEntity> transform(final String scope, final LineItemEntity entity) {
		String cartId = Base32Util.encode(entity.getCartId());
		String lineItemId = Base32Util.encode(entity.getLineItemId());
		String itemId = entity.getItemId();
		int lineItemQuantity = entity.getQuantity();
		final LineItemConfigurationEntity configuration = entity.getConfiguration();

		String cartUri = cartsUriBuilderFactory.get()
											.setScope(scope)
										.setCartId(cartId)
										.build();


		String selfUri = cartLineItemsUriBuilderFactory.get()
				.setLineItemId(lineItemId)
				.setSourceUri(cartUri)
				.build();

		Self self = SelfFactory.createSelf(selfUri);
		LineItemEntity lineItemEntity = LineItemEntity.builder()
												.withCartId(cartId)
												.withItemId(itemId)
												.withLineItemId(lineItemId)
												.withQuantity(lineItemQuantity)
												.withConfiguration(configuration)
												.build();
		return ResourceState.Builder.create(lineItemEntity)
				.withSelf(self)
				.withScope(scope)
				.build();
	}

	/**
	 * Creates a {@link LineItemEntity} for LineItemWriterStrategy.
	 *
	 * @param cartId     the optional cart id, can be null
	 * @param lineItemId the optional line item id, can be null
	 * @param itemId     the optional item id, can be null
	 * @param quantity   the optional quantity, can be null
	 * @return the {@link LineItemEntity} from given parameters.
	 */
	public LineItemEntity transformToDto(final String cartId, final String lineItemId, final String itemId, final Integer quantity) {
		return LineItemEntity.builder()
								.withCartId(Base32Util.decode(cartId))
								.withItemId(itemId)
								.withLineItemId(Base32Util.decode(lineItemId))
								.withQuantity(quantity)
								.build();
	}
}
