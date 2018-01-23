/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.WishListEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.store.StoreService;

/**
 * Test class for {@link WishListSharedEmailProducer}.
 */
public class WishListSharedEmailProducerTest {

	private static final String WISH_LIST_SENDER_KEY = "wishListSender";

	private static final String WISH_LIST_RECIPIENTS_KEY = "wishListRecipients";

	private WishListSharedEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final WishListEmailPropertyHelper wishListEmailPropertyHelper = context.mock(WishListEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private final WishListService wishlistService = context.mock(WishListService.class);

	private final StoreService storeService = context.mock(StoreService.class);

	private static final long WISH_LIST_UID = 1L;

	private static final String WISH_LIST_GUID = "WISH_LIST_GUID";

	private static final String TEST_STORE_CODE = "TEST_STORE";

	private static final String LOCALE_KEY = "locale";

	private static final String WISH_LIST_MESSAGE_KEY = "wishListMessage";

	private static final String WISH_LIST_UID_KEY = "wishListUid";

	private static final String STORE_CODE_KEY = "storeCode";

	@Before
	public void setUp() {
		emailProducer = new WishListSharedEmailProducer();

		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setWishListEmailPropertyHelper(wishListEmailPropertyHelper);
		emailProducer.setWishListService(wishlistService);
		emailProducer.setStoreService(storeService);
	}

	@Test
	public void verifyWishlistSharedEmailIsCreated() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));

				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(context.mock(Store.class)));

				final EmailProperties emailProperties = context.mock(EmailProperties.class);
				oneOf(wishListEmailPropertyHelper).getWishListEmailProperties(with(any(WishListMessage.class)), with(any(WishList.class)),
						with(any(Store.class)), with(any(Locale.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();

		final Collection<Email> actualEmail = emailProducer.createEmails(WISH_LIST_GUID, additionalData);

		assertSame("Unexpected email created by producer", expectedEmail, actualEmail.iterator().next());
	}

	@Test
	public void verifyMultipleWishlistSharedEmailsAreCreated() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));

				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(context.mock(Store.class)));

				final EmailProperties emailProperties = context.mock(EmailProperties.class);
				allowing(wishListEmailPropertyHelper).getWishListEmailProperties(with(any(WishListMessage.class)), with(any(WishList.class)),
						with(any(Store.class)), with(any(Locale.class)));
				will(returnValue(emailProperties));

				allowing(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();
		additionalData.put(WISH_LIST_RECIPIENTS_KEY, "test1@test.com,test2@test.com,test3@test.com");
		final Collection<Email> actualEmails = emailProducer.createEmails(WISH_LIST_GUID, additionalData);

		final int expectedEmailCount = 3;
		assertEquals("Incorrect number of emails produced.", expectedEmailCount, actualEmails.size());
		
		for (final Email actualEmail : actualEmails) {
			assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
		}
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));

				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(context.mock(Store.class)));

				final EmailProperties emailProperties = context.mock(EmailProperties.class);
				oneOf(wishListEmailPropertyHelper).getWishListEmailProperties(with(any(WishListMessage.class)), with(any(WishList.class)),
						with(any(Store.class)), with(any(Locale.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();
		emailProducer.createEmails(WISH_LIST_GUID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoWishListIsFound() throws Exception {

		emailProducer.createEmails(WISH_LIST_GUID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoLocaleIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();
		additionalData.put(LOCALE_KEY, null);
		emailProducer.createEmails(WISH_LIST_GUID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoStoreIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));

				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(null));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();
		emailProducer.createEmails(WISH_LIST_GUID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoWishlistMessageIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));

				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(context.mock(Store.class)));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();
		additionalData.remove(WISH_LIST_MESSAGE_KEY);
		emailProducer.createEmails(WISH_LIST_GUID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenInvalidRecipientEmailGiven() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(wishlistService).get(WISH_LIST_UID);
				will(returnValue(context.mock(WishList.class)));

				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(context.mock(Store.class)));
			}
		});

		final Map<String, Object> additionalData = createValidAdditionalData();
		additionalData.put(WISH_LIST_RECIPIENTS_KEY, "");
		emailProducer.createEmails(WISH_LIST_GUID, additionalData);
	}

	private Map<String, Object> createValidAdditionalData() {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(LOCALE_KEY, Locale.CANADA.toLanguageTag());
		additionalData.put(STORE_CODE_KEY, TEST_STORE_CODE);
		additionalData.put(WISH_LIST_MESSAGE_KEY, "Test Message Text");
		additionalData.put(WISH_LIST_SENDER_KEY, "James Bond");
		additionalData.put(WISH_LIST_RECIPIENTS_KEY, "test@test.com");
		additionalData.put(WISH_LIST_UID_KEY, 1);
		return additionalData;
	}

}
