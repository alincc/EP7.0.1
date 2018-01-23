/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.validator.ShoppingItemDtoValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * The facade for shopping cart related operations.
 */
@Singleton
@Named("shoppingCartRepository")
public class ShoppingCartRepositoryImpl implements ShoppingCartRepository {

	private static final String DEFAULT_CART_NOT_FOUND = "Default cart cannot be found.";
	private static final String CART_WAS_NOT_FOUND = "No cart was found with GUID = %s.";

	private final ShoppingCartService shoppingCartService;
	private final CartDirectorService cartDirectorService;
	private final ResourceOperationContext resourceOperationContext;
	private final CustomerSessionRepository customerSessionRepository;
	private final ShoppingItemDtoFactory shoppingItemDtoFactory;
	private final ShoppingItemDtoValidator shoppingItemDtoValidator;
	private final CartPostProcessor cartPostProcessor;
	private final ExceptionTransformer exceptionTransformer;

	/**
	 * Constructor.
	 *
	 * @param shoppingCartService               the shoppingCartService
	 * @param cartDirectorService               the cart director service
	 * @param resourceOperationContext          the resource operation context
	 * @param customerSessionRepository         the customer session repo
	 * @param shoppingItemDtoFactory            the shopping item dto factory
	 * @param shoppingItemDtoValidator          the ShoppingItemDtoValidator
	 * @param cartPostProcessor                 the {@link CartPostProcessor}
	 * @param exceptionTransformer              the exception transformer
	 */
	@Inject
	@SuppressWarnings("parameternumber")
	ShoppingCartRepositoryImpl(
			@Named("shoppingCartService") final ShoppingCartService shoppingCartService,
			@Named("cartDirectorService") final CartDirectorService cartDirectorService,
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext,
			@Named("customerSessionRepository") final CustomerSessionRepository customerSessionRepository,
			@Named("shoppingItemDtoFactory") final ShoppingItemDtoFactory shoppingItemDtoFactory,
			@Named("shoppingItemDtoValidator") final ShoppingItemDtoValidator shoppingItemDtoValidator,
			@Named("cartPostProcessor") final CartPostProcessor cartPostProcessor,
			@Named("exceptionTransformer") final ExceptionTransformer exceptionTransformer) {

		this.shoppingCartService = shoppingCartService;
		this.cartDirectorService = cartDirectorService;
		this.resourceOperationContext = resourceOperationContext;
		this.customerSessionRepository = customerSessionRepository;
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
		this.shoppingItemDtoValidator = shoppingItemDtoValidator;
		this.cartPostProcessor = cartPostProcessor;
		this.exceptionTransformer = exceptionTransformer;
	}


	@Override
	public ExecutionResult<ShoppingCart> getDefaultShoppingCart() {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
				return getDefaultCart(customerSession);
			}
		}.execute();
	}

	@Override
	@CacheResult
	@SuppressWarnings("deprecation")
	public ExecutionResult<ShoppingCart> getShoppingCart(final String cartGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
				final ShoppingCart cart = shoppingCartService.findByGuid(cartGuid);
				Ensure.notNull(cart, OnFailure.returnNotFound(CART_WAS_NOT_FOUND, cartGuid));

				cartPostProcessor.postProcessCart(cart, cart.getShopper(), customerSession);

				return ExecutionResultFactory.createReadOK(cart);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public boolean verifyShoppingCartExistsForStore(final String cartGuid, final String storeCode) {
		return shoppingCartService.shoppingCartExistsForStore(cartGuid, storeCode);
	}

	@Override
	@CacheResult
	public ExecutionResult<ShoppingCart> getShoppingCart(final String customerGuid, final String storeCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CustomerSession customerSession = Assign.ifSuccessful(
							customerSessionRepository.findCustomerSessionByGuid(customerGuid));
				return getDefaultCart(customerSession);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public  ExecutionResult<Collection<String>> findAllCarts(final String customerGuid, final String storeCode) {
		Collection<String> cartOrderGuids = getShoppingCarts(customerGuid, storeCode);
		return ExecutionResultFactory.createReadOK(cartOrderGuids);
	}

	/**
	 * Get the default cart of a shopper.
	 *
	 * @param shopper the shopper
	 * @return the cart
	 */
	ShoppingCart getDefaultCart(final Shopper shopper) {
		final ShoppingCart cart = shoppingCartService.findOrCreateByShopper(shopper);
		return shoppingCartService.saveIfNotPersisted(cart);
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<ShoppingCart> addItemToCart(final ShoppingCart cart, final String skuCode, final int quantity,
		   final Map<String, String> fields) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ShoppingItemDto shoppingItemDto = shoppingItemDtoFactory.createDto(skuCode, quantity);
				shoppingItemDto.setItemFields(fields);
				Ensure.successful(shoppingItemDtoValidator.validate(shoppingItemDto));

				try {
					ShoppingCart updatedCart = cartDirectorService.addItemToCart(cart, shoppingItemDto);
					return ExecutionResultFactory.createCreateOKWithData(updatedCart, true);
				} catch (EpValidationException error) {
					return exceptionTransformer.getExecutionResult(error);
				}
			}
		}.execute();
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, WishList.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<ShoppingCart> moveItemToCart(final ShoppingCart cart, final String wishlistLineItemGuid, final String skuCode,
			final int quantity, final Map<String, String> fields) {
		ShoppingItemDto dto = shoppingItemDtoFactory.createDto(skuCode, quantity);
		dto.setItemFields(fields);
		Ensure.successful(shoppingItemDtoValidator.validate(dto));

		try {
			ShoppingCart shoppingCart = cartDirectorService.moveItemFromWishListToCart(cart, dto,
					wishlistLineItemGuid);
			return ExecutionResultFactory.createCreateOKWithData(shoppingCart, true);
		} catch (EpValidationException error) {
			return exceptionTransformer.getExecutionResult(error);
		}
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingItem.class})
	public ExecutionResult<ShoppingItem> moveItemToWishlist(final ShoppingCart cart, final String cartLineItemGuid) {
		ShoppingItem shoppingItem = cartDirectorService.moveItemFromCartToWishList(cart, cartLineItemGuid);
		return ExecutionResultFactory.createCreateOKWithData(shoppingItem, true);
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<Void> updateCartItem(
			final ShoppingCart cart, final long shoppingItemUid, final String skuCode, final int quantity) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				Map<String, String> itemFields = Optional.ofNullable(cart.getCartItem(skuCode))
						.map(ShoppingItem::getFields)
						.orElse(Collections.emptyMap());

				ShoppingItemDto shoppingItemDto = shoppingItemDtoFactory.createDto(skuCode, quantity);
				shoppingItemDto.setItemFields(itemFields);
				Ensure.successful(shoppingItemDtoValidator.validate(shoppingItemDto));

				try {
					cartDirectorService.updateCartItem(cart, shoppingItemUid, shoppingItemDto);
				} catch (EpValidationException error) {
					return exceptionTransformer.getExecutionResult(error);
				} catch (ProductNotPurchasableException error) {
					return exceptionTransformer.getExecutionResult(error);
				}

				return ExecutionResultFactory.createUpdateOK();
			}
		}.execute();
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<Void> removeItemFromCart(final ShoppingCart cart, final long shoppingItemUid) {
		cartDirectorService.removeItemsFromCart(cart, shoppingItemUid);

		return ExecutionResultFactory.createDeleteOK();
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<Void> removeAllItemsFromCart(final String storeCode, final String cartGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Ensure.isTrue(verifyShoppingCartExistsForStore(cartGuid, storeCode), OnFailure.returnNotFound("cart not found in store."));
				ShoppingCart cart = Assign.ifSuccessful(getShoppingCart(cartGuid));
				cartDirectorService.clearItems(cart);

				return ExecutionResultFactory.createDeleteOK();
			}
		}.execute();
	}

	@Override
	public ExecutionResult<String> getDefaultShoppingCartGuid(final String storeCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				String customerGuid = resourceOperationContext.getUserIdentifier();
				Collection<String> cartGuids = Assign.ifSuccessful(findAllCarts(customerGuid, storeCode));
				return ExecutionResultFactory.createReadOK(CollectionUtil.first(cartGuids));
			}
		}.execute();
	}

	@Override
	public void reApplyCatalogPromotions(final ShoppingCart cart) {
		cartDirectorService.reApplyCatalogPromotions(cart);
	}

	private Collection<String> getShoppingCarts(final String customerGuid, final String storeCode) {
		final String storeCodeUpperCase = storeCode.toUpperCase(Locale.getDefault());
		List<String> carts = shoppingCartService.findByCustomerAndStore(customerGuid, storeCodeUpperCase);

		return Assign.ifNotEmpty(carts,
				OnFailure.returnNotFound(CART_WAS_NOT_FOUND));
	}

	private ExecutionResult<?> getDefaultCart(final CustomerSession customerSession) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Shopper shopper = customerSession.getShopper();

				ShoppingCart cart = Assign.ifNotNull(getDefaultCart(shopper), OnFailure.returnNotFound(DEFAULT_CART_NOT_FOUND));
				cartPostProcessor.postProcessCart(cart, cart.getShopper(), customerSession);

				return ExecutionResultFactory.createReadOK(cart);
			}
		}.execute();
	}
}
