package com.ey.advisory.app.services.doc.gstr1a;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
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

@Component("Gstr1ADefaultSalesDocListRulesValidator")
public class Gstr1ADefaultSalesDocListRulesValidator
		implements DocListRulesValidator<Gstr1AOutwardTransDocument, String> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ADefaultSalesDocListRulesValidator.class);

	@Autowired
	@Qualifier("DefaultGstr1AOutwardTransDocKeyGenerator")
	private DocKeyGenerator<Gstr1AOutwardTransDocument, String> keyGen;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<Gstr1AOutwardTransDocument> documents,
			ProcessingContext context) {
		Config config = configManager.getConfig("BUSINESSRULES",
				"rules.validator.use_dynamic_chain");
		boolean useDynamicValidator = Boolean
				.parseBoolean(config != null ? config.getValue() : "false");
		DocRulesValidatorChain<Gstr1AOutwardTransDocument> chain = getValidatorChain(
				useDynamicValidator);
		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		for (Gstr1AOutwardTransDocument doc : documents) {
			if (doc.getIsError())
				continue;
			if (GSTConstants.CAN.equalsIgnoreCase(doc.getSupplyType()))
				continue;
			String docKey = keyGen.generateKey(doc);
			List<ProcessingResult> results = chain.validate(doc, context);
			retResultMap.put(docKey, results);
		}

		/**
		 * Log the performance stats for Business validators
		 */

		PerfUtil.logEventToFile(PerfStatistics.getValidatorStats(),
				PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_BREAKUP");
		return retResultMap;
	}

	/**
	 * Get the SalesDocRulesValidatorChain to use. Currently, this method uses
	 * the beans with predefined names for static RulesValidatorChain and
	 * Dynamic Rules Validator chain. Instead it can look for an Validator
	 * configuration using the config manager to load the appropriate bean, so
	 * that we can switch implementations.
	 * 
	 * @param useStubs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DocRulesValidatorChain<Gstr1AOutwardTransDocument> getValidatorChain(
			boolean useDynamicValidator) {

		String beanName = !useDynamicValidator
				? APIConstants.STATIC_SALES_VALIDATOR_CHAIN
				: APIConstants.DYNAMIC_SALES_VALIDATOR_CHAIN;
		return (DocRulesValidatorChain<Gstr1AOutwardTransDocument>) StaticContextHolder
				.getBean(beanName, DocRulesValidatorChain.class);
	}

}
