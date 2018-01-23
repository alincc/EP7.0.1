/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static java.util.Arrays.asList;

import static junit.framework.TestCase.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.validator.ShoppingItemDtoValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Test for {@link ShoppingCartRepositoryImpl}.
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartRepositoryImplTest {
	private static final String CART_GUID = "cart";
	private static final String USER_GUID = "user";
	private static final String SKU_CODE = "sku";
	private static final String STORE_CODE = "store";

	private static final String SUCCESSFUL_OPERATION = "The operation should have been successful";
	public static final String THE_OPERATION_SHOULD_HAVE_SUCCEEDED = "The operation should have succeeded";
	public static final String CART_ASSERTION_ERROR =
			"The result should be the cart returned by the core service";
	public static final String PLACEHOLDER_KEY = "Placeholder Key";
	public static final String PLACEHOLDER_VALUE = "Placeholder Value";

	@Mock
	private ShoppingCartService shoppingCartService;

	@Mock
	private CartDirectorService cartDirectorService;

	@Mock
	private CartPostProcessor cartPostProcessor;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private Shopper mockShopper;

	@Mock
	private CustomerSessionRepository customerSessionRepository;

	@Mock
	private ShoppingItemDtoValidator shoppingItemDtoValidator;

	@Mock
	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	@Mock
	private ExceptionTransformer exceptionTransformer;

	private final Subject testSubject = TestSubjectFactory.createWithScopeAndUserId(STORE_CODE, USER_GUID);

	@InjectMocks
	private ShoppingCartRepositoryImpl repository;

	@Before
	public void setUp() {
		given(shoppingItemDtoValidator.validate(any(ShoppingItemDto.class))).willReturn(ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test the behaviour of get default shopping cart.
	 */
	@Test
	public void testGetDefaultShoppingCart() {
		CustomerSession mockCustomerSession = createMockCustomerSession();
		ShoppingCart cart = createMockShoppingCart();

		expectCartPostProcessing(cart);
		when(shoppingCartService.findOrCreateByShopper((mockShopper))).thenReturn(cart);
		when(shoppingCartService.saveIfNotPersisted(cart)).thenReturn(cart);

		ExecutionResult<ShoppingCart> result = repository.getDefaultShoppingCart();

		verifyPostProcess(cart, mockCustomerSession);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(CART_ASSERTION_ERROR, cart, result.getData());
	}

	/**
	 * Test the behaviour of get default shopping cart when cart not found.
	 */
	@Test
	public void testGetDefaultShoppingCartWhenCartNotFound() {
		CustomerSession mockCustomerSession = createMockCustomerSession();

		when(mockCustomerSession.getShopper()).thenReturn(mockShopper);
		when(shoppingCartService.findOrCreateByShopper((mockShopper))).thenReturn(null);

		ExecutionResult<ShoppingCart> result = repository.getDefaultShoppingCart();
		assertTrue("The operation should have failed", result.isFailure());
		assertEquals("The status should be NOT FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	/**
	 * Test the behaviour of get shopping cart.
	 */
	@Test
	public void testGetShoppingCart() {
		CustomerSession mockCustomerSession = createMockCustomerSession();
		ShoppingCart cart = createMockShoppingCart();

		expectCartPostProcessing(cart);
		when(shoppingCartService.findByGuid(CART_GUID)).thenReturn(cart);

		ExecutionResult<ShoppingCart> result = repository.getShoppingCart(CART_GUID);

		verifyPostProcess(cart, mockCustomerSession);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(CART_ASSERTION_ERROR, cart, result.getData());
	}

	@Test
	public void givenCustomerGetShoppingCart() {
		CustomerSession mockCustomerSession = createMockCustomerSession();
		Customer mockCustomer = mock(Customer.class);
		when(mockCustomer.getGuid()).thenReturn(USER_GUID);
		when(customerSessionRepository.findCustomerSessionByGuid(USER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		ShoppingCart cart = createMockShoppingCart();

		expectCartPostProcessing(cart);
		when(shoppingCartService.findOrCreateByShopper((mockShopper))).thenReturn(cart);
		when(shoppingCartService.saveIfNotPersisted(cart)).thenReturn(cart);

		ExecutionResult<ShoppingCart> result = repository.getShoppingCart(mockCustomer.getGuid(), STORE_CODE);

		verifyPostProcess(cart, mockCustomerSession);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(CART_ASSERTION_ERROR, cart, result.getData());
	}

	/**
	 * Test the behaviour of get shopping cart when cart not found.
	 */
	@Test
	public void testGetShoppingCartWhenCartNotFound() {
		createMockCustomerSession();

		when(shoppingCartService.findByGuid(CART_GUID)).thenReturn(null);

		ExecutionResult<ShoppingCart> result = repository.getShoppingCart(CART_GUID);
		assertTrue("The operation should have failed", result.isFailure());
		assertEquals("The status should be NOT FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void givenValidCartAndStoreVerifyShoppingCartExists() {
		when(shoppingCartService.shoppingCartExistsForStore(CART_GUID, STORE_CODE)).thenReturn(true);

		boolean result = repository.verifyShoppingCartExistsForStore(CART_GUID, STORE_CODE);
		assertTrue("Cart Should have existed", result);
	}

	@Test
	public void testVerifyShoppingCartExistsThrowsException() {
		when(shoppingCartService.shoppingCartExistsForStore(CART_GUID, STORE_CODE)).thenReturn(false);

		boolean result = repository.verifyShoppingCartExistsForStore(CART_GUID, STORE_CODE);
		assertFalse("Cart Should have not existed", result);
	}

	@Test
	public void testAddItemToCartHappyPath() {
		ShoppingCart cart = createMockShoppingCart();
		ShoppingItemDto item = new ShoppingItemDto(SKU_CODE, 1);
		ShoppingItem addedItem = new ShoppingItemImpl();
		addedItem.setUidPk(1L);

		when(shoppingItemDtoFactory.createDto(SKU_CODE, 1)).thenReturn(item);
		when(cartDirectorService.addItemToCart(cart, item)).thenReturn(cart);
		when(cart.getCartItem(SKU_CODE)).thenReturn(addedItem);

		ExecutionResult<ShoppingCart> shoppingCartExecutionResult = repository.addItemToCart(cart, SKU_CODE, 1, Collections.emptyMap());
		assertTrue(THE_OPERATION_SHOULD_HAVE_SUCCEEDED, shoppingCartExecutionResult.isSuccessful());
		assertEquals("The result should be populated correctly",
				addedItem, shoppingCartExecutionResult.getData().getCartItem(SKU_CODE));
	}

	@Test
	public void testUpdateCartItemHappyPath() {
		ShoppingCart cart = createMockShoppingCart();
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_CODE, 2);
		when(shoppingItemDtoFactory.createDto(SKU_CODE, 2)).thenReturn(shoppingItemDto);

		ExecutionResult<Void> result = repository.updateCartItem(cart, 1L, SKU_CODE, 2);

		assertNull(cart.getCartItem(SKU_CODE));
		assertTrue(THE_OPERATION_SHOULD_HAVE_SUCCEEDED, result.isSuccessful());

		verify(cartDirectorService).updateCartItem(cart, 1L, shoppingItemDto);
	}

	@Test
	public void shouldNotUpdateCartItemWhenProductIsNotPurchasable() {
		ShoppingCart cart = createMockShoppingCart();
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_CODE, 2);
		when(shoppingItemDtoFactory.createDto(SKU_CODE, 2)).thenReturn(shoppingItemDto);

		String productNotPurchasableError = "error message";
		StructuredErrorMessage structuredErrorMessage = mock(StructuredErrorMessage.class);
		when(cartDirectorService.updateCartItem(any(ShoppingCart.class), anyLong(), any(ShoppingItemDto.class)))
				.thenThrow(
						new ProductNotPurchasableException(
								productNotPurchasableError,
								asList(structuredErrorMessage)
						)
				);
		Message mockMessage = mock(Message.class);
		when(exceptionTransformer.getExecutionResult(any(ProductNotPurchasableException.class))).thenReturn(ExecutionResultFactory
				.createStateFailureWithMessages(productNotPurchasableError, asList(mockMessage)));

		ExecutionResult<Void> result = repository.updateCartItem(cart, 1L, SKU_CODE, 2);

		assertNull(cart.getCartItem(SKU_CODE));
		assertTrue(result.isFailure());
		assertThat(result.getStructuredErrorMessages())
				.containsOnly(mockMessage);

		verify(cartDirectorService).updateCartItem(cart, 1L, shoppingItemDto);
	}

	@Test
	public void testRemoveItemFromCartHappyPath() {
		ShoppingCart cart = createMockShoppingCart();

		ExecutionResult<Void> result = repository.removeItemFromCart(cart, 1L);
		assertTrue(THE_OPERATION_SHOULD_HAVE_SUCCEEDED, result.isSuccessful());
		verify(cartDirectorService).removeItemsFromCart(cart, 1L);
	}

	@Test
	public void testRemoveAllItemsFromCartHappyPath() {
		ShoppingCart cart = createMockShoppingCart();
		when(shoppingCartService.shoppingCartExistsForStore(CART_GUID, STORE_CODE)).thenReturn(true);
		createMockCustomerSession();
		when(shoppingCartService.findByGuid(CART_GUID)).thenReturn(cart);

		ExecutionResult<Void> result = repository.removeAllItemsFromCart(STORE_CODE, CART_GUID);

		assertTrue(THE_OPERATION_SHOULD_HAVE_SUCCEEDED, result.isSuccessful());
		verify(cartDirectorService).clearItems(cart);
	}

	/**
	 * Test the behaviour of get default shopping cart guid.
	 */
	@Test
	public void testGetDefaultShoppingCartGuid() {
		setResourceOperationContext();
		when(shoppingCartService.findByCustomerAndStore(USER_GUID, STORE_CODE.toUpperCase(Locale.getDefault())))
				.thenReturn(asList(CART_GUID, "OTHER_GUID"));

		ExecutionResult<String> result = repository.getDefaultShoppingCartGuid(STORE_CODE);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals("The result should be the first cart guid returned by the core service", CART_GUID, result.getData());
	}

	/**
	 * Test the behaviour of get default shopping cart guid when no carts found.
	 */
	@Test
	public void testGetDefaultShoppingCartGuidWhenNoCartsFound() {
		setResourceOperationContext();

		when(shoppingCartService.findByCustomerAndStore(USER_GUID, STORE_CODE))
				.thenReturn(Collections.<String>emptyList());

		ExecutionResult<String> result = repository.getDefaultShoppingCartGuid(STORE_CODE);
		assertTrue("The operation should have failed", result.isFailure());
		assertEquals("The status should be NOT FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testFindAllCarts() {
		when(shoppingCartService.findByCustomerAndStore(USER_GUID, STORE_CODE.toUpperCase(Locale.getDefault())))
				.thenReturn(asList(CART_GUID, "OTHER_GUID"));

		ExecutionResult<Collection<String>> result = repository.findAllCarts(USER_GUID, STORE_CODE);
		assertTrue("The operation should be successful", result.isSuccessful());
		assertEquals("There was an incorrect number carts", 2, result.getData().size());
	}

	@Test
	public void testUpdateCartItemWithFields() {
		ShoppingCart cart = createMockShoppingCart();
		ShoppingItemDto item = new ShoppingItemDto(SKU_CODE, 1);
		ShoppingItem addedItem = new ShoppingItemImpl();
		addedItem.setUidPk(1L);

		when(shoppingItemDtoFactory.createDto(SKU_CODE, 1)).thenReturn(item);
		when(cartDirectorService.addItemToCart(cart, item)).thenReturn(cart);
		when(cart.getCartItem(SKU_CODE)).thenReturn(addedItem);

		ExecutionResult<ShoppingCart> shoppingCartExecutionResult = repository.addItemToCart(cart, SKU_CODE, 1, Collections.emptyMap());
		assertTrue(THE_OPERATION_SHOULD_HAVE_SUCCEEDED, shoppingCartExecutionResult.isSuccessful());

		assertEquals(shoppingCartExecutionResult.getData().getCartItem(SKU_CODE).getFields().size(), 0);

		addedItem.setFieldValue(PLACEHOLDER_KEY, PLACEHOLDER_VALUE);
		ExecutionResult<Void> result = repository.updateCartItem(cart, 1L, SKU_CODE, 1);


		assertEquals(cart.getCartItem(SKU_CODE), addedItem);
		assertEquals(cart.getCartItem(SKU_CODE).getFields().size(), 1);
		assertTrue(THE_OPERATION_SHOULD_HAVE_SUCCEEDED, result.isSuccessful());


	}

	private ShoppingCart createMockShoppingCart() {
		ShoppingCart cart = mock(ShoppingCart.class);
		when(cart.getGuid()).thenReturn(CART_GUID);
		when(cart.getShopper()).thenReturn(mockShopper);

		return cart;
	}

	private CustomerSession createMockCustomerSession() {
		CustomerSession mockCustomerSession = mock(CustomerSession.class);

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		when(mockCustomerSession.getShopper()).thenReturn(mockShopper);

		return mockCustomerSession;
	}

	private void expectCartPostProcessing(final ShoppingCart cart) {
		when(mockShopper.getCurrentShoppingCart()).thenReturn(cart);
	}

	private void verifyPostProcess(final ShoppingCart cart,
			final CustomerSession customerSession) {
		verify(cartPostProcessor, atLeastOnce()).postProcessCart(cart, cart.getShopper(), customerSession);
	}

	private void setResourceOperationContext() {
		when(resourceOperationContext.getSubject()).thenReturn(testSubject);
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_GUID);
	}
}
