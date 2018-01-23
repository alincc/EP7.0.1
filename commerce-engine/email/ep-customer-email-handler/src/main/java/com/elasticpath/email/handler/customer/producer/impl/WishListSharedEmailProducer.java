/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.WishListEmailPropertyHelper;
import com.elasticpath.email.handler.producer.EmailProducer;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.store.StoreService;

/**
 * Producer for the share wish list email.
 */
public class WishListSharedEmailProducer implements EmailProducer {

	private EmailComposer emailComposer;

	private WishListEmailPropertyHelper wishListEmailPropertyHelper;

	private WishListService wishListService;

	private StoreService storeService;

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String WISH_LIST_UID_KEY = "wishListUid";

	private static final String WISH_LIST_MESSAGE_KEY = "wishListMessage";

	private static final String LOCALE_KEY = "locale";

	private static final String STORE_CODE_KEY = "storeCode";

	private static final String WISH_LIST_SENDER_KEY = "wishListSender";

	private static final String WISH_LIST_RECIPIENTS_KEY = "wishListRecipients";

	@Override
	public Collection<Email> createEmails(final String guid, final Map<String, Object> emailData) throws EmailException {
		final WishList wishList = getWishList(emailData);

		final Locale locale = getLocale(emailData);
		final Store store = getStore(emailData);

		final WishListMessage wishListMessage = getWishlistMessage(emailData);
		final Collection<InternetAddress> recipients = getRecipients(emailData);

		final EmailProperties wishlistEmailProperties = getWishListEmailPropertyHelper().getWishListEmailProperties(wishListMessage, wishList,
				store, locale);

		final List<Email> emails = new ArrayList<>();
		for (final InternetAddress address : recipients) {
			final Email email = getEmailComposer().composeMessage(wishlistEmailProperties);
			final List<InternetAddress> toAddresses = new ArrayList<>();
			toAddresses.add(address);
			email.setTo(toAddresses);
			emails.add(email);
		}

		return emails;
	}

	/**
	 * Retrieves a {@link WishList} with the given guid.
	 * 
	 * @param emailData email contextual data
	 * @return a {@link WishList}
	 * @throws IllegalArgumentException if an {@link WishList} can not be retrieved from the given parameters
	 */
	protected WishList getWishList(final Map<String, Object> emailData) {

		final Long uid = Long.valueOf((Integer) getObjectFromEmailData(WISH_LIST_UID_KEY, emailData));
		final WishList wishList = getWishListService().get(uid);

		if (wishList == null) {
			throw new IllegalArgumentException("Could not locate a WishList with uid [" + uid + "]");
		}

		return wishList;
	}

	/**
	 * Retrieves the {@link WishListMessage} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link WishListMessage}
	 * @throws IllegalArgumentException if the wishlist message can not be retrieved from the given parameters
	 */
	protected WishListMessage getWishlistMessage(final Map<String, Object> emailData) {
		final String messageText = (String) getObjectFromEmailData(WISH_LIST_MESSAGE_KEY, emailData);

		final String sender = (String) getObjectFromEmailData(WISH_LIST_SENDER_KEY, emailData);

		final WishListMessage wishListMessage = new WishListMessageImpl();
		wishListMessage.setMessage(messageText);
		wishListMessage.setSenderName(sender);
		return wishListMessage;
	}

	/**
	 * Builds a collection of wish list recipient {@link InternetAddress}es from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Collection} of {@link InternetAddress}es
	 * @throws IllegalArgumentException if the list of recipients can not be retrieved from the given parameters
	 */
	protected Collection<InternetAddress> getRecipients(final Map<String, Object> emailData) {
		final String recipientString = (String) getObjectFromEmailData(WISH_LIST_RECIPIENTS_KEY, emailData);

		final List<String> recipientStrings = Arrays.asList(recipientString.split(","));

		if (recipientStrings.isEmpty()) {
			throw new IllegalArgumentException("At least one recipient must be listed for the Wish List to be shared.");
		}

		final List<InternetAddress> recipients = new ArrayList<>();
		for (final String recipient : recipientStrings) {
			try {
				recipients.add(new InternetAddress(recipient));
			} catch (final AddressException e) {
				throw new IllegalArgumentException("An invalid email address was provided for the Wish List", e);
			}
		}
		return recipients;
	}

	/**
	 * Retrieves the {@link Locale} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Locale}
	 * @throws IllegalArgumentException if the Locale can not be retrieved from the given parameters
	 */
	protected Locale getLocale(final Map<String, Object> emailData) {
		final String languageTag = (String) getObjectFromEmailData(LOCALE_KEY, emailData);
		return Locale.forLanguageTag(languageTag);
	}

	/**
	 * Retrieves the {@link Store} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Store}
	 * @throws IllegalArgumentException if the Store can not be retrieved from the given parameters
	 */
	protected Store getStore(final Map<String, Object> emailData) {

		final String storeCode = (String) getObjectFromEmailData(STORE_CODE_KEY, emailData);
		final Store store = storeService.findStoreWithCode(storeCode);

		if (store == null) {
			throw new IllegalArgumentException("No store could be found with storeCode '" + storeCode + "'.");
		}

		return store;
	}

	/**
	 * Retrieves an Object from the given {@code Map} of email contextual data.
	 * 
	 * @param key the object key
	 * @param emailData email contextual data
	 * @return the Object
	 * @throws IllegalArgumentException if the Object can not be retrieved from the given parameters
	 */
	protected Object getObjectFromEmailData(final String key, final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(key) || emailData.get(key) == null) {
			throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE, key));
		}

		return emailData.get(key);
	}

	public void setWishListEmailPropertyHelper(final WishListEmailPropertyHelper wishListEmailPropertyHelper) {
		this.wishListEmailPropertyHelper = wishListEmailPropertyHelper;
	}

	public WishListEmailPropertyHelper getWishListEmailPropertyHelper() {
		return wishListEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	public EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	public WishListService getWishListService() {
		return wishListService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

}
