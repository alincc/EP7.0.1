/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.integration.epcommerce.impl;

import static com.elasticpath.rest.ResourceTypeFactory.adaptResourceEntity;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.shipments.lineitems.option.integration.ShipmentLineItemOptionsLookupStrategy;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Implementation of {@link ShipmentLineItemOptionsLookupStrategy}.
 */
@Singleton
@Named("shipmentLineItemOptionsLookupStrategy")
public class ShipmentLineItemOptionsLookupStrategyImpl implements ShipmentLineItemOptionsLookupStrategy {

	private final ProductSkuRepository productSkuRepository;
	private final ShipmentRepository shipmentRepository;
	private final AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionEntity> skuOptionTransformer;
	private final AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionValueEntity> skuOptionValueTransformer;
	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param productSkuRepository      the ProductSkuRepository
	 * @param skuOptionTransformer      the SkuOptionTransformer
	 * @param skuOptionValueTransformer the SkuOptionValueTransformer
	 * @param shipmentRepository        the order shipment repository
	 * @param resourceOperationContext  the resource operation context
	 */
	@Inject
	public ShipmentLineItemOptionsLookupStrategyImpl(
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
			@Named("skuOptionTransformer")
			final AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionEntity> skuOptionTransformer,
			@Named("skuOptionValueTransformer")
			final AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionValueEntity> skuOptionValueTransformer,
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.productSkuRepository = productSkuRepository;
		this.shipmentRepository = shipmentRepository;
		this.skuOptionTransformer = skuOptionTransformer;
		this.skuOptionValueTransformer = skuOptionValueTransformer;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public ExecutionResult<Collection<String>> findLineItemOptionIds(final ShipmentLineItemEntity entity) {

		OrderSku orderSku = Assign.ifSuccessful(getOrderSku(entity));
		ProductSku productSku = productSkuRepository.getProductSkuWithAttributesByGuid(orderSku.getSkuGuid()).getData();
		Collection<String> optionValueKeys = Assign.ifNotEmpty(productSku.getOptionValueCodes(),
				OnFailure.returnNotFound("No options found for line item."));

		return ExecutionResultFactory.createReadOK(optionValueKeys);
	}

	@Override
	public ExecutionResult<ShipmentLineItemOptionEntity> findLineItemOption(final ShipmentLineItemEntity entity, final String optionId) {

		SkuOptionValue optionValue = Assign.ifSuccessful(getSkuOptionValue(entity, optionId));

		return ExecutionResultFactory.createReadOK(createOptionEntity(entity, optionValue));
	}

	@Override
	public ExecutionResult<ShipmentLineItemOptionValueEntity> findLineItemOptionValue(final ShipmentLineItemOptionEntity entity,
																						final String valueId) {

		String optionId = entity.getLineItemOptionId();
		ShipmentLineItemEntity shipmentLineItemEntity = adaptResourceEntity(entity, ShipmentLineItemEntity.class);
		SkuOptionValue optionValue = Assign.ifSuccessful(getSkuOptionValue(shipmentLineItemEntity, optionId));
		Ensure.isTrue(valueId.equals(optionValue.getGuid()),
				OnFailure.returnNotFound("Option value not found."));

		return ExecutionResultFactory.createReadOK(createOptionValueEntity(optionValue));
	}

	private ExecutionResult<OrderSku> getOrderSku(final ShipmentLineItemEntity shipmentEntity) {
		OrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository.find(shipmentEntity.getPurchaseId(),
				shipmentEntity.getShipmentId()));
		OrderSku orderSku = Assign.ifNotNull(getOrderSkuByGuid(orderShipment.getShipmentOrderSkus(), shipmentEntity.getLineItemId()),
				OnFailure.returnNotFound("Line item not found."));

		return ExecutionResultFactory.createReadOK(orderSku);
	}

	private ExecutionResult<SkuOptionValue> getSkuOptionValue(final ShipmentLineItemEntity entity, final String optionId) {
		OrderSku orderSku = Assign.ifSuccessful(getOrderSku(entity));
		ProductSku productSku = productSkuRepository.getProductSkuWithAttributesByGuid(orderSku.getSkuGuid()).getData();
		SkuOptionValue skuOption = Assign.ifNotNull(productSku.getOptionValueMap().get(optionId),
				OnFailure.returnNotFound("Option not found for item"));
		return ExecutionResultFactory.createReadOK(skuOption);
	}

	private ShipmentLineItemOptionEntity createOptionEntity(
			final ShipmentLineItemEntity entity,
			final SkuOptionValue optionValue) {

		ShipmentLineItemOptionEntity shipmentEntity = adaptResourceEntity(entity, ShipmentLineItemOptionEntity.class);

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		ShipmentLineItemOptionEntity resultEntity = skuOptionTransformer.transformToEntity(optionValue, locale);
		return ShipmentLineItemOptionEntity.builderFrom(resultEntity)
				.withLineItemUri(shipmentEntity.getLineItemUri())
				.withLineItemId(shipmentEntity.getLineItemId())
				.withShipmentId(shipmentEntity.getShipmentId())
				.withPurchaseId(shipmentEntity.getPurchaseId())
				.build();
	}

	private ShipmentLineItemOptionValueEntity createOptionValueEntity(final SkuOptionValue optionValue) {

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return skuOptionValueTransformer.transformToEntity(optionValue, locale);
	}

	private OrderSku getOrderSkuByGuid(final Set<OrderSku> orderSkus, final String skuGuid) {

		for (OrderSku orderSku : orderSkus) {
			if (orderSku.getGuid().equals(skuGuid)) {
				return orderSku;
			}
		}
		return null;
	}
}
