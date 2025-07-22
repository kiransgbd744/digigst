package com.ey.advisory.app.services.validation.purchase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

@Component("DefaultPurchaseDocListRulesValidator")
public class DefaultPurchaseDocListRulesValidator
		implements DocListRulesValidator<InwardTransDocument, String> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultPurchaseDocListRulesValidator.class);

	@Autowired
	@Qualifier("PurchasesDocRulesValidatorChain")
	private DocRulesValidatorChain<InwardTransDocument> chain;

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> keyGen;

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<InwardTransDocument> documents, ProcessingContext context) {

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		for (InwardTransDocument doc : documents) {
			
			if (GSTConstants.CAN.equalsIgnoreCase(doc.getSupplyType()) 
					|| doc.isDeleted())
				continue;
			
				
			String docKey = keyGen.generateKey(doc);
			List<ProcessingResult> results = chain.validate(doc, context);
			retResultMap.put(docKey, results);
		}

		/**
		 * Log the performance stats for Business validators
		 */

		LOGGER.error("Inward Validators Performance: {}",
				PerfStatistics.getValidatorStat());
		PerfUtil.logEventToFile(PerfStatistics.getValidatorStats(),
				PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_BREAKUP");

		return retResultMap;
	}

}
