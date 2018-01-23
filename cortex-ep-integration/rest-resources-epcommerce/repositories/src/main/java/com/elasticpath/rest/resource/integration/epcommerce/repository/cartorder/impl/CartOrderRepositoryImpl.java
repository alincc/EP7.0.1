/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.service.cartorder.CartOrderCouponService;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.rules.CartOrderCouponAutoApplier;

/**
 * The facade for {@link CartOrder} related operations.
 */
@Singleton
@Named("cartOrderRepository")
public class CartOrderRepositoryImpl implements CartOrderRepository {

	private static final Logger LOG = LoggerFactory.getLogger(CartOrderRepositoryImpl.class);
	private static final String ORDER_ID_KEY = OrderEntity.ORDER_ID_PROPERTY;
	private static final String ORDER_WITH_GUID_NOT_FOUND = "No cart order with GUID %s was found in store %s.";
	private static final String ORDER_WITH_CART_GUID_NOT_FOUND = "No cart order with cart GUID %s was found.";
	private static final String NO_CART_ORDERS_FOR_CUSTOMER = "No cart orders for customer with GUID %s were found in store %s.";
	private static final String NO_BILLING_ADDRESS_FOUND = "No billing address found.";
	private static final String NO_SHIPPING_ADDRESS_FOUND = "No shipping address found.";

	private final CartOrderService coreCartOrderService;
	private final CartOrderCouponService coreCartOrderCouponService;
	private final ShoppingCartRepository shoppingCartRepository;
	private final CartOrderShippingService coreCartOrderShippingService;
	private final CartOrderCouponAutoApplier cartOrderCouponAutoApplier;

	/**
	 * Constructor.
	 * @param coreCartOrderService the core cart order service
	 * @param shoppingCartRepository the shopping cart repository
	 * @param coreCartOrderShippingService the core cart order shipping service
	 * @param coreCartOrderCouponService the core cart order coupon service
	 * @param cartOrderCouponAutoApplier cart order coupon auto applier
	 */
	@Inject
	public CartOrderRepositoryImpl(
			@Named("cartOrderService")
			final CartOrderService coreCartOrderService,
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("cartOrderShippingService")
			final CartOrderShippingService coreCartOrderShippingService,
			@Named("cartOrderCouponService")
			final CartOrderCouponService coreCartOrderCouponService,
			@Named("cartOrderCouponAutoApplier")
			final CartOrderCouponAutoApplier cartOrderCouponAutoApplier) {

		this.coreCartOrderService = coreCartOrderService;
		this.shoppingCartRepository = shoppingCartRepository;
		this.coreCartOrderShippingService = coreCartOrderShippingService;
		this.coreCartOrderCouponService = coreCartOrderCouponService;
		this.cartOrderCouponAutoApplier = cartOrderCouponAutoApplier;
	}

	@Override
	@CacheResult
	public ExecutionResult<CartOrder> findByGuid(final String storeCode, final String cartOrderGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartOrder cartOrder = Assign.ifNotNull(coreCartOrderService.findByStoreCodeAndGuid(storeCode, cartOrderGuid),
						OnFailure.returnNotFound(ORDER_WITH_GUID_NOT_FOUND, cartOrderGuid, storeCode));

				return ExecutionResultFactory.createReadOK(cartOrder);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<CartOrder> findByCartGuid(final String cartGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartOrder cartOrder = Assign.ifNotNull(coreCartOrderService.findByShoppingCartGuid(cartGuid),
						OnFailure.returnNotFound(ORDER_WITH_CART_GUID_NOT_FOUND, cartGuid));
				return ExecutionResultFactory.createReadOK(cartOrder);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<CartOrder> findByShipmentDetailsId(final String storeCode, final String shipmentDetailsId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {

				final String cartOrderGuid = Assign.ifSuccessful(getCartOrderGuidByShipmentDetailsId(shipmentDetailsId));

				return findByGuid(storeCode, cartOrderGuid);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<String> getCartOrderGuidByShipmentDetailsId(final String shipmentDetailsId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {

				final Map<String, String> shipmentFieldValueMap = Assign.ifNotNull(CompositeIdUtil.decodeCompositeId(shipmentDetailsId),
																				OnFailure.returnServerError("could not decode shipmentDetailsId"));
				return ExecutionResultFactory.createReadOK(shipmentFieldValueMap.get(ORDER_ID_KEY));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<String>> findCartOrderGuidsByCustomer(final String storeCode, final String customerGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Collection<String> cartOrderGuids = Assign.ifNotNull(
						coreCartOrderService.findCartOrderGuidsByCustomerGuid(storeCode, customerGuid),
						OnFailure.returnNotFound(NO_CART_ORDERS_FOR_CUSTOMER, customerGuid, storeCode));
				return ExecutionResultFactory.createReadOK(cartOrderGuids);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "billingAddress")
	public ExecutionResult<Address> getBillingAddress(final CartOrder cartOrder) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Address address = Assign.ifNotNull(coreCartOrderService.getBillingAddress(cartOrder),
						OnFailure.returnNotFound(NO_BILLING_ADDRESS_FOUND));

				return ExecutionResultFactory.createReadOK(address);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "shippingAddress")
	public ExecutionResult<Address> getShippingAddress(final CartOrder cartOrder) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Address address = Assign.ifNotNull(coreCartOrderService.getShippingAddress(cartOrder),
						OnFailure.returnNotFound(NO_SHIPPING_ADDRESS_FOUND));
				return ExecutionResultFactory.createReadOK(address);
			}
		}.execute();
	}

	@Override
	@CacheRemove(typesToInvalidate = {CartOrder.class, Address.class, ShoppingCart.class,
		ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<CartOrder> saveCartOrder(final CartOrder cartOrder) {
		//cannot be final
		ExecutionResult<CartOrder> result;
		try {
			result = ExecutionResultFactory.createCreateOKWithData(coreCartOrderService.saveOrUpdate(cartOrder), true);
		} catch (Exception e) {
			result = ExecutionResultFactory.createServerError("Unable to save cart order");
			LOG.error("Unable to save cart order " + cartOrder.toString(), e);
		}
		return result;
	}

	@Override
	@CacheResult
	public ExecutionResult<CartOrder> getCartOrder(final String storeCode, final String guid, final FindCartOrder findBy) {

		CartOrder cartOrder;
		switch (findBy) {
			case BY_CART_GUID:
				cartOrder = Assign.ifSuccessful(findByCartGuid(guid));
				break;
			case BY_ORDER_GUID:
				cartOrder = Assign.ifSuccessful(findByGuid(storeCode, guid));
				break;
			case BY_SHIPMENT_DETAILS_ID:
				cartOrder = Assign.ifSuccessful(findByShipmentDetailsId(storeCode, guid));
				break;
			default:
				return ExecutionResultFactory.createServerError("Invalid FindCartOrder criteria: " + findBy);
		}

		return ExecutionResultFactory.createReadOK(cartOrder);
	}

	@Override
	@CacheResult(uniqueIdentifier = "enrichedCartByGuid")
	public ExecutionResult<ShoppingCart> getEnrichedShoppingCart(final String storeCode, final String guid, final FindCartOrder findBy) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final CartOrder cartOrder = Assign.ifSuccessful(getCartOrder(storeCode, guid, findBy));

				return getEnrichedShoppingCart(storeCode, cartOrder);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "enrichedCart")
	public ExecutionResult<ShoppingCart> getEnrichedShoppingCart(final String storeCode, final CartOrder cartOrder) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {

					// NOTE: We have a good cart with (almost) all of the applicable promotions on it here.
					// We still need to configure the shipping information below, which catalog promos here.
					// The problem is that we throw them all away when we fireRules during (and after) updating the shipping info.
					ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(cartOrder.getShoppingCartGuid()));

					// update the coupon codes on the shopping cart.
					ShoppingCart enrichedShoppingCart = coreCartOrderCouponService.populateCouponCodesOnShoppingCart(shoppingCart, cartOrder);

					// Update shipping information on the cart as shipping info is transient.
					enrichedShoppingCart = coreCartOrderShippingService.populateAddressAndShippingFields(enrichedShoppingCart, cartOrder);

					// It is necessary to fireRules() and re-compute the cart price information
					// again after updating the shipping info on the cart.
					// There is an unfortunate side effect of fireRules() where by we lose
					// the record of the applied catalog promotions on the cart as a result
					// of calling fireRules().
					//
					// Update: no longer required.  A ShoppingCartPricingSnapshot is generated on-demand
					// when derived values are required.
					// pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);

					// HACK: Re-apply the catalog promos to the cart due to us losing the
					// information when we called fireRules() above.
					//
					// Update: begrudgingly moved to PricingSnapshotService.getPricingSnapshotForCart.
					// shoppingCartRepository.reApplyCatalogPromotions(enrichedShoppingCart);

				return ExecutionResultFactory.createReadOK(enrichedShoppingCart);
			}
		}.execute();
	}

	@Override
	@CacheRemove(typesToInvalidate = {Address.class})
	public ExecutionResult<Boolean> updateShippingAddressOnCartOrder(final String shippingAddressGuid, final String cartOrderGuid,
			final String storeCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartOrder cartOrder = Assign.ifSuccessful(findByGuid(storeCode, cartOrderGuid));
				boolean updatedAddress = coreCartOrderShippingService.updateCartOrderShippingAddress(shippingAddressGuid, cartOrder, storeCode);
				if (updatedAddress) {
					Ensure.successful(saveCartOrder(cartOrder));
				}
				return ExecutionResultFactory.createReadOK(updatedAddress);
			}
		}.execute();
	}

	@Override
	@CacheRemove(typesToInvalidate = {CartOrder.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public  ExecutionResult<Boolean> filterAndAutoApplyCoupons(final CartOrder cartOrder, final Store store, final String customerEmailAddress) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				boolean isCouponsApplied = Assign.ifNotNull(
						cartOrderCouponAutoApplier.filterAndAutoApplyCoupons(cartOrder, store, customerEmailAddress),
						OnFailure.returnServerError("Server error when auto applying coupons to cart order"));
				return ExecutionResultFactory.createReadOK(isCouponsApplied);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public Collection<ShippingServiceLevel> findShippingServiceLevels(final String storeCode, final Address shippingAddress) {
		return coreCartOrderShippingService.findShippingServiceLevels(storeCode, shippingAddress);
	}

	@Override
	@CacheResult
	public String getShoppingCartGuid(final String storeCode, final String cartOrderGuid) {
		return coreCartOrderService.getShoppingCartGuid(storeCode, cartOrderGuid);
	}
}
