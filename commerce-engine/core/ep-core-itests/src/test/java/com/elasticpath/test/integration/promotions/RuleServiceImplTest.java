/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.test.integration.promotions;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration tests that ensure the rule service behaves as expected.
 */
public class RuleServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private RuleService ruleService;

	@Autowired
	private RuleSetService ruleSetService;

	@Autowired
	private StoreService storeService;

	/**
	 * Test persisting and loading display name.
	 */
	@DirtiesDatabase
	@Test
	public void testDisplayName() {
		final String englishDisplayName = "English Display Name";
		final String canadianDisplayName = "Canadian Display Name";
		final String germanDisplayName = "Deutsch Angezeigter Name";
		final String ruleCode = "ruleCode";

		// Create the rule
		Rule rule = createRule(ruleCode);

		// Add display names in several locales
		LocalizedProperties localizedProperties = rule.getLocalizedProperties();
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, englishDisplayName);
		final Locale canadianEnglish = new Locale("en", "CA");
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, canadianEnglish, canadianDisplayName);
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.GERMAN, germanDisplayName);

		// Save the rule and reload it
		ruleService.add(rule);
		Rule loadedRule = ruleService.findByRuleCode(ruleCode);

		assertEquals("Rule should contain the English display name", englishDisplayName, loadedRule.getDisplayName(Locale.ENGLISH));
		assertEquals("Rule should contain the German display name", germanDisplayName, loadedRule.getDisplayName(Locale.GERMAN));
		assertEquals("Rule should contain the Canadian English display name", canadianDisplayName, loadedRule.getDisplayName(canadianEnglish));
	}

	/**
	 * Test finding the active rule id and selling context by scenario and store code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindActiveRuleIdSellingContextByScenarioAndStore() {
		final String ruleCodeOne = "activeRuleInStoreOne";
		final String ruleCodeTwo = "activeRuleInStoreTwo";
		final String ruleCodeThree = "inactiveRuleInStoreOne";
		final String storeCodeOne = "storeCodeOne";
		final String storeCodeTwo = "storeCodeTwo";

		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);
		Store storeOne = scenario.getStore();
		storeOne.setCode(storeCodeOne);
		storeService.saveOrUpdate(storeOne);

		Store storeTwo = getTac().getPersistersFactory().getStoreTestPersister().persistStore(scenario.getCatalog(), scenario.getWarehouse(), storeCodeTwo, "USD");

		Rule activeRuleInStoreOne = createRule(ruleCodeOne, storeOne, true);
		ruleService.add(activeRuleInStoreOne);

		Rule activeRuleInStoreTwo = createRule(ruleCodeTwo, storeTwo, true);
		ruleService.add(activeRuleInStoreTwo);

		Rule inactiveRuleInStoreOne = createRule(ruleCodeThree, storeOne, false);
		ruleService.add(inactiveRuleInStoreOne);

		List<Object[]> results = ruleService.findActiveRuleIdSellingContextByScenarioAndStore(RuleScenarios.CART_SCENARIO, storeCodeOne);

		assertEquals("One result should have been found", 1, results.size());
		assertThat("The result should contain the expected rule id", results.get(0), hasItemInArray(activeRuleInStoreOne.getUidPk()));
		assertThat("The result should contain the expected selling context", results.get(0),
						  hasItemInArray(activeRuleInStoreOne.getSellingContext()));
	}

	private Rule createRule(final String ruleCode, final Store storeCode, final boolean isEnabled) {
		Rule rule = createRule(ruleCode);
		rule.setEnabled(isEnabled);
		rule.setStore(storeCode);

		SellingContext sellingContext = getBeanFactory().getBean(ContextIdNames.SELLING_CONTEXT);
		sellingContext.setGuid(ruleCode);
		sellingContext.setName(ruleCode);
		sellingContext.setPriority(1);

		rule.setSellingContext(sellingContext);

		return rule;
	}

	private Rule createRule(final String ruleCode) {
		RuleSet ruleSet = getBeanFactory().getBean(ContextIdNames.RULE_SET);
		ruleSet.setLastModifiedDate(new Date());
		ruleSet.setName(ruleCode);
		ruleSet.setScenario(RuleScenarios.CART_SCENARIO);
		ruleSet = ruleSetService.add(ruleSet);

		RuleParameter ruleParam = getBeanFactory().getBean(ContextIdNames.RULE_PARAMETER);
		ruleParam.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParam.setValue("10");
		RuleAction ruleAction = getBeanFactory().getBean(ContextIdNames.CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION);
		ruleAction.getParameters().clear();
		ruleAction.addParameter(ruleParam);

		Rule rule = getBeanFactory().getBean(ContextIdNames.PROMOTION_RULE);
		rule.setName(ruleCode);
		rule.setCode(ruleCode);
		rule.setRuleSet(ruleSet);
		rule.addAction(ruleAction);

		return rule;
	}
}
