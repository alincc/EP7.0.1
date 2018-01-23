/**
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.commons.exception.UserIdNonExistException;
import com.elasticpath.commons.util.impl.PasswordGeneratorImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerAuthentication;
import com.elasticpath.domain.customer.CustomerDeleted;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerDeletedImpl;
import com.elasticpath.domain.customer.impl.CustomerGroupImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.auth.UserIdentityService;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.security.SaltFactory;
import com.elasticpath.service.shopper.ShopperCleanupService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.CustomerBuilder;
import com.elasticpath.test.factory.TestCustomerProfileFactory;
import com.elasticpath.validation.ConstraintViolationTransformer;

/**
 * Test <code>CustomerServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects" })
public class CustomerServiceImplTest  {

	private static final String LIST = "list";
	private static final String CUSTOMER_FIND_BY_EMAIL = "CUSTOMER_FIND_BY_EMAIL_IN_STORES";
	private static final String CUSTOMER_FIND_BY_USERID = "CUSTOMER_FIND_BY_USERID_IN_STORES";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String GUID1 = "GUID1";
	private static final String EMAIL_ADDRESS_CAMEL_CASE = "MyName@MyDomain.com";
	private static final String EMAIL_ADDRESS = EMAIL_ADDRESS_CAMEL_CASE.toLowerCase(Locale.ENGLISH);
	private static final String TEST_STORE_CODE = "SAMPLE_STORECODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory beanExpectations;

	private StoreService storeService;
	private CustomerGroupService customerGroupService;
	private UserIdentityService userIdentityService;
	private TimeService timeService;
	private IndexNotificationService indexNotificationService;
	private ShopperCleanupService shopperCleanupService;
	private PersistenceEngine persistenceEngine;
	private FetchPlanHelper fetchPlanHelper;
	private Store store;
	private EventMessageFactory eventMessageFactory;
	private EventMessagePublisher eventMessagePublisher;

	private CustomerServiceImpl customerServiceImpl;

	private Validator validator;
	private ConstraintViolationTransformer constraintViolationTransformer;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		beanExpectations = new BeanFactoryExpectationsFactory(context, beanFactory);
		final AttributeService attributeService = context.mock(AttributeService.class);
		final Map<String, Attribute> profileMap = new TestCustomerProfileFactory().getProfile();

		final SettingValue settingValue = context.mock(SettingValue.class);
		final SettingsService settingsService = context.mock(SettingsService.class);

		indexNotificationService = context.mock(IndexNotificationService.class);
		storeService = context.mock(StoreService.class);
		timeService = context.mock(TimeService.class);
		persistenceEngine = context.mock(PersistenceEngine.class);
		fetchPlanHelper = context.mock(FetchPlanHelper.class);
		customerGroupService = context.mock(CustomerGroupService.class);
		shopperCleanupService = context.mock(ShopperCleanupService.class);
		userIdentityService = context.mock(UserIdentityService.class);
		eventMessageFactory = context.mock(EventMessageFactory.class);
		eventMessagePublisher = context.mock(EventMessagePublisher.class);
		validator = context.mock(Validator.class);
		constraintViolationTransformer = context.mock(ConstraintViolationTransformer.class);

		context.checking(new Expectations() {
			{
				allowing(attributeService).getCustomerProfileAttributesMap(); will(returnValue(profileMap));
				allowing(settingValue).getValue(); will(returnValue("1"));
				allowing(settingsService).getSettingValue("COMMERCE/SYSTEM/userIdMode"); will(returnValue(settingValue));
				allowing(timeService).getCurrentTime(); will(returnValue(new Date()));
				allowing(beanFactory).getBeanImplClass(ContextIdNames.CUSTOMER); will(returnValue(CustomerImpl.class));
			}
		});

		customerServiceImpl = new CustomerServiceImpl();
		customerServiceImpl.setPersistenceEngine(persistenceEngine);
		customerServiceImpl.setSettingsReader(settingsService);
		customerServiceImpl.setStoreService(storeService);
		customerServiceImpl.setTimeService(timeService);
		customerServiceImpl.setCustomerGroupService(customerGroupService);
		customerServiceImpl.setShopperCleanupService(shopperCleanupService);
		customerServiceImpl.setUserIdentityService(userIdentityService);
		customerServiceImpl.setIndexNotificationService(indexNotificationService);
		customerServiceImpl.setEventMessageFactory(eventMessageFactory);
		customerServiceImpl.setEventMessagePublisher(eventMessagePublisher);

		customerServiceImpl.setValidator(validator);
		customerServiceImpl.setConstraintViolationTransformer(constraintViolationTransformer);

		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_SERVICE, attributeService);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.SETTINGS_SERVICE, settingsService);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.PASSWORDENCODER, new ShaPasswordEncoder());

		context.checking(new Expectations() {
			{
				allowing(attributeService).getCustomerProfileAttributesMap(); will(returnValue(profileMap));
				allowing(settingValue).getValue(); will(returnValue("1"));
				allowing(settingsService).getSettingValue("COMMERCE/SYSTEM/userIdMode"); will(returnValue(settingValue));
				allowing(timeService).getCurrentTime(); will(returnValue(new Date()));
				allowing(beanFactory).getBeanImplClass(ContextIdNames.CUSTOMER); will(returnValue(CustomerImpl.class));
			}
		});

		setupStore();
	}

	@After
	public void tearDown() {
		beanExpectations.close();
	}

	private void customerGroupSetup() {
		final CustomerGroup customerGroup = getCustomerGroup();
		context.checking(new Expectations() {
			{
				allowing(customerGroupService).getDefaultGroup(); will(returnValue(customerGroup));
			}
		});
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test(expected = EpServiceException.class)
	public void testSetPersistenceEngine() {
		customerServiceImpl.setPersistenceEngine(null);
		customerServiceImpl.add(new CustomerImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(customerServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.add(Customer)'.
	 * @throws EmailException in case of an email exception.
	 */
	@Test
	public void testAdd() {
		final Customer customer = createCustomer();
		customer.setEmail("test@elasticpath.com");
		customer.setStoreCode(TEST_STORE_CODE);

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).save(customer);
				allowing(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_EMAIL, LIST,
						storeUids, customer.getEmail()); will(returnValue(new ArrayList<Customer>()));
				allowing(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_USERID, LIST,
						storeUids, customer.getEmail()); will(returnValue(new ArrayList<Customer>()));
				oneOf(userIdentityService).add(customer.getUserId(), customer.getClearTextPassword());
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(storeService).findValidStoreCode(TEST_STORE_CODE); will(returnValue(TEST_STORE_CODE));
				Map<String, Object> data = null;
				allowing(eventMessageFactory).createEventMessage(with(any(EventType.class)), with(any(String.class)), with(equal(data)));
				will(returnValue(context.mock(EventMessage.class)));
				oneOf(eventMessagePublisher).publish(with(any(EventMessage.class)));
			}
		});

		customerGroupSetup();
		customerServiceImpl.add(customer);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.add(Customer)'.
	 * Anonymous is now treated almost the same, before it was sending email to
	 * non-anonymous customers.
	 */
	@Test
	public void testAddAnonymous() {
		final Customer customer = createCustomer();
		final Store store = new StoreImpl();
		store.setCode(TEST_STORE_CODE);
		customer.setEmail("test@elasticpath.com");
		customer.setAnonymous(true);
		customer.setStoreCode(TEST_STORE_CODE);

		final Object[] parameters = new Object[] { customer.getEmail() };
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).save(customer);
				allowing(persistenceEngine).retrieveByNamedQuery(CUSTOMER_FIND_BY_EMAIL, parameters); will(returnValue(new ArrayList<Customer>()));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(storeService).findValidStoreCode(TEST_STORE_CODE); will(returnValue(TEST_STORE_CODE));
			}
		});

		this.customerGroupSetup();
		customerServiceImpl.add(customer);
	}

	@Test
	public void testUpdateAnonymous() throws EpValidationException {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);
		this.customerGroupSetup();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).update(customer); will(returnValue(customer));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
			}
		});

		customerServiceImpl.update(customer);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.add(Customer)'. Tests case where the customer has not yet been persisted.
	 */
	@Test
	public void testAddNewCustomerAddress() {
		final Customer customer = createCustomer();
		customer.setUidPk(0);
		final CustomerAddress address = new CustomerAddressImpl();
		context.checking(new Expectations() {
			{
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(persistenceEngine).update(customer); will(returnValue(customer));
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});
		this.customerGroupSetup();
		customer.addAddress(address);
		Customer returnedCustomer = customerServiceImpl.update(customer);
		assertTrue(returnedCustomer.getAddresses().contains(address));
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.add(Customer)'. Tests case where the customer is already known.
	 */
	@Test
	public void testAddExistingCustomerAddress() {
		final Customer customer = createCustomer();
		final Customer updatedCustomer = createCustomer();
		this.customerGroupSetup();
		this.setupUpdate(customer, updatedCustomer);

		final CustomerAddress address = new CustomerAddressImpl();
		address.setUidPk(1);
		context.checking(new Expectations() {
			{
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});
		customer.addAddress(address);
		final Customer returnedCustomer = customerServiceImpl.update(customer);
		assertSame(updatedCustomer, returnedCustomer);
	}

	private void setupUpdate(final Customer customer, final Customer updatedCustomer) {
		final long uidPk = 1L;
		customer.setUidPk(uidPk);
		customer.setEmail(EMAIL_ADDRESS);
		customer.setUserId(EMAIL_ADDRESS);
		customer.setGuid(GUID1);
		updatedCustomer.setUidPk(uidPk);
		updatedCustomer.setEmail(EMAIL_ADDRESS);
		updatedCustomer.setUserId(EMAIL_ADDRESS);
		updatedCustomer.setGuid(GUID1);


		final List<Long> duplicateIdOrEmailCount = new ArrayList<>();
		duplicateIdOrEmailCount.add(0L);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("OTHER_CUSTOMER_COUNT_BY_USERID_BY_STORE_EXCLUDING_ANONYMOUS",
						EMAIL_ADDRESS, TEST_STORE_CODE, GUID1); will(returnValue(duplicateIdOrEmailCount));
				oneOf(persistenceEngine).update(customer); will(returnValue(updatedCustomer));
			}
		});
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.update(Customer)'.
	 */
	@Test
	public void testUpdate() {
		final Customer customer = createCustomer();
		final Customer updatedCustomer = createCustomer();
		updatedCustomer.setGuid(customer.getGuid());
		this.customerGroupSetup();
		this.setupUpdate(customer, updatedCustomer);
		context.checking(new Expectations() {
			{
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});
		final Customer returnedCustomer = customerServiceImpl.update(customer);
		assertSame(updatedCustomer, returnedCustomer);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.delete(Customer)'.
	 */
	@Test
	public void testDelete() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_DELETED, CustomerDeletedImpl.class);

		final Customer customer = new CustomerImpl();
		final long uid = 1000L;
		customer.setUidPk(uid);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).delete(customer);
				oneOf(shopperCleanupService).removeShoppersByCustomer(customer); will(returnValue(0));
				oneOf(persistenceEngine).save(with(any(CustomerDeleted.class)));
				oneOf(userIdentityService).remove(customer.getUserId());
			}
		});

		customerServiceImpl.remove(customer);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.emailExists(String)'.
	 */
	@Test
	public void testEmailExists() {
		final String email = "exist_email_address@xxx.xxx";
		final String existEmailAddress = email;
		final Customer customer1 = createCustomer();
		final Store store = new StoreImpl();
		store.setCode(TEST_STORE_CODE);
		customer1.initialize();

		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerServiceImpl);

		final long uidPk1 = 1L;
		customer1.setUidPk(uidPk1);
		customer1.setEmail(email);
		customer1.setGuid(GUID1);

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());

		customer1.setStoreCode(TEST_STORE_CODE);
		final List<Customer> customerList = new ArrayList<>();
		customerList.add(customer1);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_EMAIL, LIST,
						storeUids, email); will(returnValue(customerList));
			}
		});
		assertTrue(customerServiceImpl.isEmailExists(existEmailAddress, TEST_STORE_CODE));
	}

	/**
	 * Test that if a user is found in another store then it is seen to exist (to support shared stores).
	 */
	@Test
	public void testEmailExistsInOtherStore() {
		final String email = "exist_email_address@xxx.xxx";
		final String existEmailAddress = email;
		final Customer customer1 = createCustomer();
		final Store store = new StoreImpl();
		store.setCode(TEST_STORE_CODE);

		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerServiceImpl);

		final long uidPk1 = 1L;
		customer1.setUidPk(uidPk1);
		customer1.setEmail(email);
		customer1.setGuid(GUID1);

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());

		customer1.setStoreCode("OtherStore");
		final List<Customer> customerList = new ArrayList<>();
		customerList.add(customer1);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_EMAIL, LIST,
						storeUids, email); will(returnValue(customerList));
			}
		});
		assertTrue("The customer should be found", customerServiceImpl.isEmailExists(existEmailAddress, TEST_STORE_CODE));
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.emailExists(String)'.
	 */
	@Test
	public void testNullEmail() {
		assertFalse(customerServiceImpl.isUserIdExists(null, null));
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final Customer customer1 = createCustomer();
		customer1.setFirstName("aaa");
		final Customer customer2 = createCustomer();
		customer2.setFirstName("bbb");
		final List<Customer> customerList = new ArrayList<>();
		customerList.add(customer1);
		customerList.add(customer2);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CUSTOMER_SELECT_ALL"); will(returnValue(customerList));
			}
		});
		final List<Customer> retrievedCustomerList = customerServiceImpl.list();
		assertEquals(customerList, retrievedCustomerList);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER, CustomerImpl.class);

		final long uid = 1234L;
		final Customer customer = createCustomer();
		customer.setFirstName("aaa");
		customer.setUidPk(uid);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(CustomerImpl.class, uid); will(returnValue(customer));
			}
		});
		final Customer loadedCustomer = customerServiceImpl.load(uid);
		assertSame(customer, loadedCustomer);
	}

	/**
	 * Test method for {@link CustomerServiceImpl#get(long, FetchGroupLoadTuner).
	 */
	@Test
	public void testGetWithFGLoadTuner() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER, CustomerImpl.class);
		customerServiceImpl.setFetchPlanHelper(fetchPlanHelper);

		final long uid = 1234L;
		final long nonExistUid = 3456L;
		final Customer customer = context.mock(Customer.class);
		final FetchGroupLoadTuner fGLoadTuner = context.mock(FetchGroupLoadTuner.class);
		context.checking(new Expectations() {
			{
				allowing(customer).getUidPk(); will(returnValue(uid));
				allowing(customer).getCustomerAuthentication(); will(returnValue(null));
				allowing(customer).setCustomerAuthentication(with(any(CustomerAuthentication.class)));

				oneOf(persistenceEngine).get(CustomerImpl.class, uid); will(returnValue(customer));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(fGLoadTuner);
				oneOf(fetchPlanHelper).clearFetchPlan();

				oneOf(persistenceEngine).get(CustomerImpl.class, nonExistUid); will(returnValue(null));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(fGLoadTuner);
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(customer, customerServiceImpl.get(uid, fGLoadTuner));
		assertNull(customerServiceImpl.get(nonExistUid, fGLoadTuner));
		assertEquals(0, customerServiceImpl.get(0, fGLoadTuner).getUidPk());
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoadAnNonExistCustomer() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER, CustomerImpl.class);

		final long uid = 1234L;
		final Customer customer = new CustomerImpl();
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(CustomerImpl.class, uid); will(returnValue(customer));
			}
		});
		assertSame(customer, customerServiceImpl.load(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testFindByEmail() {
		final String email = EMAIL_ADDRESS;
		final Customer customer = createCustomer();
		customer.setEmail(email);
		customer.setStoreCode(TEST_STORE_CODE);
		final List<Customer> customerList = new ArrayList<>();
		customerList.add(customer);

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_EMAIL, LIST,
						storeUids, email); will(returnValue(customerList));
			}
		});

		final Customer retrievedCustomer = customerServiceImpl.findByEmail(email, store.getCode());
		assertSame(customerList.get(0), retrievedCustomer);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.findByEmail(String)'.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByEmailWithNullEmail() {
		customerServiceImpl.findByEmail(null, null);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testFindByEmailWithNullReturn() {
		final String email = EMAIL_ADDRESS;
		final List<Customer> emptyList = new ArrayList<>();

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_EMAIL, LIST,
						storeUids, email); will(returnValue(emptyList));
			}
		});

		assertNull(customerServiceImpl.findByEmail(email, TEST_STORE_CODE));
	}

	/**
	 * Tests that the email is made lower case before a database operation is performed.
	 */
	@Test
	public void testFindByEmailWithCamelCase() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerServiceImpl);
		final CustomerImpl customer = createCustomer();
		customer.setEmail(EMAIL_ADDRESS_CAMEL_CASE);

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				exactly(2).of(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_EMAIL, LIST,
						storeUids, EMAIL_ADDRESS); will(returnValue(Arrays.asList(customer)));
			}
		});

		assertEquals("Customer object should not be changed", customer, customerServiceImpl.
				findByEmail(EMAIL_ADDRESS_CAMEL_CASE, TEST_STORE_CODE));
		// check the same by searching by lower case email
		assertEquals("Customer object should not be changed", customer, customerServiceImpl.
				findByEmail(EMAIL_ADDRESS, TEST_STORE_CODE));
	}

	/**
	 * Tests that the the email address is not changed on the customer object when persisting the
	 * customer to the data store.
	 * @throws EmailException in case of email exception
	 */
	@Test
	public void testAddNewCustomerWithCamelCaseEmail() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerServiceImpl);
		final CustomerImpl customer = createCustomer();
		customer.setEmail(EMAIL_ADDRESS_CAMEL_CASE);
		customer.setGuid(GUID1);

		customerGroupSetup();

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_USERID, LIST,
						storeUids, EMAIL_ADDRESS_CAMEL_CASE); will(returnValue(Collections.emptyList()));
				oneOf(storeService).findValidStoreCode(TEST_STORE_CODE); will(returnValue(TEST_STORE_CODE));
				oneOf(persistenceEngine).save(customer);
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				Map<String, Object> data = null;
				allowing(eventMessageFactory).createEventMessage(with(any(EventType.class)), with(any(String.class)), with(equal(data)));
				will(returnValue(context.mock(EventMessage.class)));
				oneOf(eventMessagePublisher).publish(with(any(EventMessage.class)));
			}
		});
		// make sure that the mode userId == email is set
		Customer updatedCustomer = customerServiceImpl.addByAuthenticate(customer, true);

		assertEquals("Email should not be changed", EMAIL_ADDRESS_CAMEL_CASE, updatedCustomer.getEmail());
		assertEquals("UserId should be equal to email", EMAIL_ADDRESS_CAMEL_CASE, updatedCustomer.getUserId());
	}

	/**
	 * Tests that the the email address is not changed on the customer object when persisting the
	 * customer to the data store.
	 * @throws EmailException in case of email exception
	 */
	@Test(expected = EpServiceException.class)
	public void testFailWhileAddingNewCustomerWithCamelCaseEmailWhenStoreCodeIsNotValid() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerServiceImpl);
		final CustomerImpl customer = createCustomer();
		customer.setEmail(EMAIL_ADDRESS_CAMEL_CASE);
		customer.setGuid(GUID1);

		customerGroupSetup();

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_USERID, LIST,
						storeUids, EMAIL_ADDRESS_CAMEL_CASE);
				will(returnValue(Collections.emptyList()));
				oneOf(storeService).findValidStoreCode(TEST_STORE_CODE); will(throwException(new EpServiceException("invalid store code")));
			}
		});
		// make sure that the mode userId == email is set
		Customer updatedCustomer = customerServiceImpl.addByAuthenticate(customer, true);

		assertEquals("Email should not be changed", EMAIL_ADDRESS_CAMEL_CASE, updatedCustomer.getEmail());
		assertEquals("UserId should be equal to email", EMAIL_ADDRESS_CAMEL_CASE, updatedCustomer.getUserId());
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.setPassword(Customer, String)'.
	 */
	@Test
	public void testSetPassword() {
		final Customer customer = createCustomer();
		final Customer updatedCustomer = createCustomer();
		final String email = "wesley.coelho@elasticpath.com";
		customer.setEmail(email);
		customer.setGuid(GUID1);
		updatedCustomer.setEmail(email);
		updatedCustomer.setGuid(GUID1);

		final List<Customer> customerList = new ArrayList<>();
		customerList.add(customer);
		final List<String> guidList = new ArrayList<>();
		guidList.add(GUID1);

		@SuppressWarnings("unchecked")
		final SaltFactory<String> saltFactory = context.mock(SaltFactory.class);
		beanExpectations.oneBeanFactoryGetBean(ContextIdNames.SALT_FACTORY, saltFactory);

		this.setupUpdate(customer, updatedCustomer);
		context.checking(new Expectations() {
			{
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(userIdentityService).setPassword(customer.getUserId(), NEW_PASSWORD);
				oneOf(saltFactory).createSalt(); will(returnValue("salt"));
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});

		this.customerGroupSetup();
		customer.setCustomerAuthentication(new CustomerAuthenticationImpl());

		final Customer returnedCustomer = this.customerServiceImpl.setPassword(customer, NEW_PASSWORD);

		assertEquals(NEW_PASSWORD, customer.getClearTextPassword());
		assertEquals(updatedCustomer, returnedCustomer);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.changePasswordAndSendEmail(Customer, String)'.
	 * @throws EmailException in case of email exception
	 */
	@Test
	public void testChangePasswordAndSendEmail() {
		final Customer customer = context.mock(Customer.class, "customer");
		final Customer updatedCustomer = context.mock(Customer.class, "updated customer");
		final CustomerService txCustomerService = context.mock(CustomerService.class, "transactional customer service");

		beanExpectations.oneBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, txCustomerService);
		context.checking(new Expectations() {
			{
				allowing(updatedCustomer).getGuid();
				will(returnValue(GUID1));
				oneOf(txCustomerService).setPassword(customer, NEW_PASSWORD); will(returnValue(updatedCustomer));
				Map<String, Object> data = null;
				allowing(eventMessageFactory).createEventMessage(with(any(EventType.class)), with(any(String.class)), with(equal(data)));
				will(returnValue(context.mock(EventMessage.class)));
				oneOf(eventMessagePublisher).publish(with(any(EventMessage.class)));
			}
		});

		customerServiceImpl.changePasswordAndSendEmail(customer, NEW_PASSWORD);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.resetPassword(String)'.
	 * @throws EmailException in case of email error
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testResetPassword() {
		final String email = "a@elasticpath.com";
		final Customer customer = createCustomer();
		final Customer updatedCustomer = createCustomer();
		customer.setEmail(email);
		customer.setUserId(email);
		customer.setGuid(GUID1);
		customer.setStoreCode(TEST_STORE_CODE);
		updatedCustomer.setEmail(email);
		updatedCustomer.setUserId(email);
		updatedCustomer.setGuid(GUID1);
		updatedCustomer.setStoreCode(TEST_STORE_CODE);
		this.setupUpdate(customer, updatedCustomer);
		this.customerGroupSetup();

		final List<Customer> customerList = new ArrayList<>();
		customerList.add(customer);

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());

		final SaltFactory<String> saltFactory = context.mock(SaltFactory.class);
		beanExpectations.oneBeanFactoryGetBean(ContextIdNames.SALT_FACTORY, saltFactory);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_USERID, LIST,
						storeUids, email); will(returnValue(customerList));

				allowing(eventMessageFactory).createEventMessage(with(any(EventType.class)), with(any(String.class)), with(any(Map.class)));
				will(returnValue(context.mock(EventMessage.class)));
				oneOf(eventMessagePublisher).publish(with(any(EventMessage.class)));

				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(userIdentityService).setPassword(with(customer.getUserId()), with(any(String.class)));

				oneOf(saltFactory).createSalt(); will(returnValue("salt"));
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});
		beanExpectations.allowingBeanFactoryGetBean("customerService", customerServiceImpl);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.PASSWORD_GENERATOR, new PasswordGeneratorImpl());

		this.customerServiceImpl.resetPassword(email, store.getCode());
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.resetPassword(String)'.
	 */
	@Test(expected = UserIdNonExistException.class)
	public void testResetPasswordWithNonExistUserId() {
		final String userId = "a@a.com";
		final List<Customer> customerList = new ArrayList<>();

		final Set<Long> storeUids = new HashSet<>();
		storeUids.add(store.getUidPk());
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CUSTOMER_FIND_BY_USERID, LIST,
						storeUids, userId); will(returnValue(customerList));
			}
		});

		this.customerServiceImpl.resetPassword(userId, TEST_STORE_CODE);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.findByUids(customerUids)'.
	 */
	@Test
	public void testFindByUids() {
		final long three = 3L;
		final List<Long> customerUids = new ArrayList<>();
		customerUids.add(1L);
		customerUids.add(2L);
		customerUids.add(three);

		final List<Customer> customers = new ArrayList<>();
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery("CUSTOMER_BY_UIDS", customerUids.toArray()); will(returnValue(customers));
				allowing(persistenceEngine).retrieveByNamedQueryWithList("CUSTOMER_FIND_BY_UIDS", LIST, customerUids); will(returnValue(customers));
			}
		});
		assertSame(customers, this.customerServiceImpl.findByUids(customerUids));

		// Should return an empty list if no product uid is given.
		List<Customer> result = this.customerServiceImpl.findByUids(new ArrayList<>());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Tests that when addOrUpdateCustomerShippingAddress is called, the address is correctly added to the customer's list of addresses and set as
	 * the customer's preferred shipping address.
	 */
	@Test
	public void testAddOrUpdateCustomerShippingAddress() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER, CustomerImpl.class);

		final long uidPk = 1L;
		final Customer customer = createCustomer();
		customer.setGuid(GUID1);

		final CustomerAddress address = new CustomerAddressImpl();

		setupUpdate(customer, customer);
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(null);
				oneOf(fetchPlanHelper).clearFetchPlan();
				oneOf(persistenceEngine).get(CustomerImpl.class, uidPk); will(returnValue(customer));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});
		customerServiceImpl.setFetchPlanHelper(fetchPlanHelper);
		customerGroupSetup();

		final Customer returnedCustomer = customerServiceImpl.addOrUpdateCustomerShippingAddress(customer, address);

		assertNotNull("Customer's list of addresses should not be null.", returnedCustomer.getAddresses());
		assertFalse("Customer's list of addresses should not be empty.", returnedCustomer.getAddresses().isEmpty());
		assertTrue("Customer's list of addresses should contain the address that was just added.", returnedCustomer.getAddresses().contains(address));
		assertNotNull("Customer's preferred shipping address should have been set.", returnedCustomer.getPreferredShippingAddress());
		assertSame("Customer's preferred shipping address should be the same as the passed in address.", address, returnedCustomer
				.getPreferredShippingAddress());
	}

	/**
	 * Tests that when addOrUpdateCustomerBillingAddress is called, the address is correctly added to the customer's list of addresses and set as
	 * the customer's preferred shipping address.
	 */
	@Test
	public void testAddOrUpdateCustomerBillingAddress() {
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER, CustomerImpl.class);

		final long uidPk = 1L;
		final Customer customer = createCustomer();
		customer.setGuid(GUID1);

		final CustomerAddress address = new CustomerAddressImpl();

		setupUpdate(customer, customer);
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(null);
				oneOf(fetchPlanHelper).clearFetchPlan();
				oneOf(persistenceEngine).get(CustomerImpl.class, uidPk); will(returnValue(customer));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CUSTOMER, customer.getUidPk());
				oneOf(validator).validate(customer); will(returnValue(null));
			}
		});
		customerServiceImpl.setFetchPlanHelper(fetchPlanHelper);
		customerGroupSetup();
		final Customer returnedCustomer = customerServiceImpl.addOrUpdateCustomerBillingAddress(customer, address);

		assertNotNull("Customer's list of addresses should not be null.", returnedCustomer.getAddresses());
		assertFalse("Customer's list of addresses should not be empty.", returnedCustomer.getAddresses().isEmpty());
		assertTrue("Customer's list of addresses should contain the address that was just added.", returnedCustomer.getAddresses().contains(address));
		assertNotNull("Customer's preferred billing address should have been set.", returnedCustomer.getPreferredBillingAddress());
		assertSame("Customer's preferred billing address should be the same as the passed in address.", address, returnedCustomer
				.getPreferredBillingAddress());
	}

	@Test(expected = EpServiceException.class)
	public void testShouldThrowExceptionWhenStoreIsNotFoundForGivenCode() {
		final String invalidStoreCode = "INVALID_STORE_CODE";

		context.checking(new Expectations() {
			{
				oneOf(storeService).findStoreWithCode(invalidStoreCode);
				will(returnValue(null));

			}
		});

		customerServiceImpl.findByUserId("userId", invalidStoreCode, false);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = EpValidationException.class)
	public void testShouldThrowEpValidationExceptionWhenCustomerViolationIsNotEmpty() {
		ConstraintViolation<Customer> constraintViolation = context.mock(ConstraintViolation.class);

		Set<ConstraintViolation<Customer>> customerViolations = new HashSet<>();
		customerViolations.add(constraintViolation);

		final Customer customer = createCustomer();
		context.checking(new Expectations() {
			{
				oneOf(validator).validate(customer); will(returnValue(customerViolations));
				oneOf(constraintViolationTransformer).transform(customerViolations); will(returnValue(null));
			}
		});
		customerServiceImpl.update(customer);
	}

	/**
	 * @return a new <code>CustomerGroup</code> instance.
	 */
	private CustomerGroup getCustomerGroup() {
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setGuid(new RandomGuidImpl().toString());
		customerGroup.initialize();
		return customerGroup;
	}

	/**
	 * Returns a new <code>Customer</code> instance.
	 *
	 * @return a new <code>Customer</code> instance.
	 */
	private CustomerImpl createCustomer() {
		return CustomerBuilder.newCustomer()
				.withGuid(new RandomGuidImpl().toString())
				.withStoreCode(TEST_STORE_CODE)
				.build();
	}

	private void setupStore() {
		store = new StoreImpl();
		store.setCode(TEST_STORE_CODE);
		store.setDefaultLocale(Locale.US);
		store.setDefaultCurrency(Currency.getInstance(Locale.US));
		store.setContentEncoding("UTF-8");
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(TEST_STORE_CODE); will(returnValue(store));
			}
		});
	}

}
