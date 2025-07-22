package com.ey.advisory.app.services.validation.eInvoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

@Component("EinvoiceDefaultSalesDocListRulesValidator")
public class EinvoiceDefaultSalesDocListRulesValidator
		implements DocListRulesValidator<OutwardTransDocument, String> {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DocKeyGenerator<OutwardTransDocument, String> keyGen;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<OutwardTransDocument> documents, ProcessingContext context) {
		Config config = configManager.getConfig("BUSINESSRULES",
				"rules.validator.use_dynamic_chain");
		boolean useDynamicValidator = Boolean
				.parseBoolean(config != null ? config.getValue() : "false");
		DocRulesValidatorChain<OutwardTransDocument> chain = getValidatorChain(
				useDynamicValidator);
		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		PerfUtil.logEventToFile(PerfStatistics.getValidatorStats(),
				PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_BREAKUP_START");
		for (OutwardTransDocument doc : documents) {
			if (doc.getIsError())
				continue;
			if (GSTConstants.CAN.equalsIgnoreCase(doc.getSupplyType()))
				continue;
			String docKey = keyGen.generateKey(doc);
			List<ProcessingResult> results = chain.validate(doc, context);
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
	private DocRulesValidatorChain<OutwardTransDocument> getValidatorChain(
			boolean useDynamicValidator) {

		/*
		 * String beanName = !useDynamicValidator ?
		 * APIConstants.E_INVOICESTATIC_VALIDATOR_CHAIN :
		 * APIConstants.DYNAMIC_SALES_VALIDATOR_CHAIN;
		 */
		String beanName = APIConstants.E_INVOICESTATIC_VALIDATOR_CHAIN;

		return (DocRulesValidatorChain<OutwardTransDocument>) StaticContextHolder
				.getBean(beanName, DocRulesValidatorChain.class);
	}

}
