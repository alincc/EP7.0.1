/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl;

import java.util.Collection;
import java.util.HashSet;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.AppliedPromotionRuleAware;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRuleMatcher;
import com.elasticpath.service.rules.RuleService;

/**
 * AbstractPromotionRuleMatcher encapsulates the algorithm of filtering a collection of
 * applied rules using a predicate.
 * @param <ID> the type of the applied rule records.
 * @param <RULE> The rule type, either {@link com.elasticpath.domain.rules.AppliedRule} or {@link com.elasticpath.domain.rules.Rule}
 */
public abstract class AbstractPromotionRuleMatcher<ID, RULE> implements PromotionRuleMatcher<ID, RULE> {

	/** The rule service. */
	private final RuleService ruleService;

	/**
	 * Constructor.
	 * @param ruleService The rule service.
	 */
	public AbstractPromotionRuleMatcher(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * <p>
	 * {@inheritDoc}.
	 * </p>
	 * <p>
	 * This implementation uses the template method to encode the common algorithm
	 * of filtering a collection of applied rules using a predicate.
	 * The variability in the algorithm is in the way that rule codes are
	 * retrieved, see {@link #getGuid(Object)}, and in the way that rules themselves are
	 * retrieved, see {@link #getRule(Object)}.
	 * </p>
	 * @param ruleSource the rule aware input object to search.
	 * @param rulePredicate The promotion rule predicate.
	 * @return Collection<String> The matching promotion ids.
	 */
	@Override
	public Collection<String> findMatchingAppliedRules(final AppliedPromotionRuleAware<ID> ruleSource, final RulePredicate<RULE> rulePredicate) {

		Collection<ID> appliedRules = ruleSource.getAppliedRules();

		Collection<String> appliedPromotions = new HashSet<>();
		for (ID appliedRule : appliedRules) {
			RULE rule = getRule(appliedRule);
			if (rulePredicate.isSatisfied(rule)) {
				appliedPromotions.add(getGuid(rule));
			}
		}
		return appliedPromotions;
	}

	/**
	 * Gets the identifier for a rule.
	 * @param rule The rule.
	 * @return The rule identifier
	 */
	protected abstract String getGuid(RULE rule);

	/**
	 * Gets the rule for a given id.
	 * @param appliedRule The rule id.
	 * @return The rule.
	 */
	protected abstract RULE getRule(ID appliedRule);

	/**
	 * Helper method to look up a rule by id.
	 * @param ruleId The rule id.
	 * @return The Rule.
	 */
	// TODO: Move this method to CE (probably RuleService).
	protected Rule getRuleById(final Long ruleId) {
		String promotionId = Assign.ifNotNull(ruleService.findRuleCodeById(ruleId), OnFailure.returnNotFound());
		return Assign.ifNotNull(ruleService.findByRuleCode(promotionId), OnFailure.returnNotFound());
	}
}