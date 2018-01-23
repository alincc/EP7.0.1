/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotionusage.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageMessages;
import com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageReportSection;
import com.elasticpath.cmclient.reporting.promotionusage.parameters.PromotionUsageParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.rules.RuleService;

/**
* The service is responsible for building a Promotion Usage prepared JPA query and a
* corresponding parameter list, then sending the query to the server-side service.
*/
public class PromotionUsageServiceImpl {
	
	private static final int RULENAME_IDX = 3;
	
	private PromotionUsageParameters parameter;
	
	
	/**
	 * Object array hold following fields:
	 * Promotion Name [String];
	 * Coupon Code [String];
	 * Store [String];
	 * Start Date [Data];
	 * End Date [Data];
	 * Number of orders [int];
	 * Currency [String];
	 * Total Revenue [BigDecimal];
	 * % of total orders [BigDecimal] (from 0.00 to 1.00).
	 * 
	 * @return List of described arrays to use in reporting layout.
	 */
	public List<Object[]> getData() {	
		
		final PromotionUsageParameters params = getParameter();
		
		if (ArrayUtils.isEmpty(params.getStoreUidPkList())) {
			return Collections.emptyList();
		}

		final Currency currency;
		if (params.getCurrencies() == null) {
			currency = null;
		} else {
			currency = Currency.getInstance(params.getCurrencies()[0]);
		}

		final List<Object[]> result = getRuleService().getPromotionUsageData(storeUidPksAsList(params.getStoreUidPkList()), currency,
				params.getStartDate(), params.getEndDate(), params.isOnlyPromotionsWithCouponCodes());

		Collections.sort(result, new Comparator<Object[]>() {
			public int compare(final Object[] rawObject1, final Object[] rawObject2) {
				return String.CASE_INSENSITIVE_ORDER.compare((String) rawObject1[RULENAME_IDX], (String) rawObject2[RULENAME_IDX]);
			}
		});

		return result;
	}
	
	private List<Long> storeUidPksAsList(final long[] uidPks) {
		if (ArrayUtils.isEmpty(uidPks)) {
			return Collections.emptyList();
		}
		final List<Long> list = new LinkedList<Long>();
		for (long uidPk : uidPks) {
			list.add(uidPk);
		}
		return list;
	}
	
	private RuleService getRuleService() {
		return LoginManager.getInstance().getBean(ContextIdNames.RULE_SERVICE);
	}
	
	
	/** 
	 * @return {@link GiftCertificateDetailsParameters}, that used for create JPA query parameters.
	 */
	public PromotionUsageParameters getParameter() {
		if (parameter == null) {
			loadParameters();
		}
		return parameter;
	}

	private void loadParameters() {
		Map<String, Object> params = getGiftCertificateDetailsReportParameters();
		parameter = (PromotionUsageParameters) params.get(PromotionUsageReportSection.PARAMETER_PARAMETERS);
	}
	
	/**
	 * @return parameters for the CustomerRegistrationReport
	 */
	Map<String, Object> getGiftCertificateDetailsReportParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		for (ReportType reportType : ReportType.getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(
					PromotionUsageMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		return params;
	}

}
