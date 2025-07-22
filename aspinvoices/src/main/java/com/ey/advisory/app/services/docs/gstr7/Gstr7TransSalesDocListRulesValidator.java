package com.ey.advisory.app.services.docs.gstr7;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr7TransSalesDocListRulesValidator")
@Slf4j
public class Gstr7TransSalesDocListRulesValidator implements
		Gstr7TransDocListRulesValidator<Gstr7TransDocHeaderEntity, String> {

	@Autowired
	@Qualifier("Gstr7TransHeaderDocKeyGenerator")
	private DocKeyGenerator<Gstr7TransDocHeaderEntity, String> keyGen;

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<Gstr7TransDocHeaderEntity> documents,
			ProcessingContext context) {
		Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> chain = getValidatorChain();
		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		PerfUtil.logEventToFile(PerfStatistics.getValidatorStats(),
				PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_BREAKUP_START");
		for (Gstr7TransDocHeaderEntity doc : documents) {
			if (doc.isError())
				continue;
			// why we are not doing bussiness validation for Can records
//			if (GSTConstants.CAN.equalsIgnoreCase(doc.getSupplyType()))
//				continue;
			String docKey = keyGen.generateKey(doc);
			List<ProcessingResult> results = chain.validate(doc, context);
			if (LOGGER.isDebugEnabled()) {
				//
			}
			retResultMap.put(docKey, results);

		}
		PerfUtil.logEventToFile(PerfStatistics.getValidatorStats(),
				PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_BREAKUP_END");
		return retResultMap;
	}

	/**
	 * Get the E_InvoiceDocRulesValidatorChain to use. Currently, this method
	 * uses the beans with predefined names for static RulesValidatorChain and
	 * Dynamic Rules Validator chain. Instead it can look for an Validator
	 * configuration using the config manager to load the appropriate bean, so
	 * that we can switch implementations.
	 * 
	 * @param useStubs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> getValidatorChain() {

		/*
		 * String beanName = !useDynamicValidator ?
		 * APIConstants.E_INVOICESTATIC_VALIDATOR_CHAIN :
		 * APIConstants.DYNAMIC_SALES_VALIDATOR_CHAIN;
		 */
		String beanName = APIConstants.GSTR7_TRANS_VALIDATOR_CHAIN;

		return (Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity>) StaticContextHolder
				.getBean(beanName, Gstr7TransDocRulesValidator.class);
	}
}
