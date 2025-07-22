/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.purchase.DefaultPurchaseDocListRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04DocListRulesValidator")
@Slf4j
public class Itc04DocListRulesValidator
		implements Itc04RulesValidator<Itc04HeaderEntity, String> {

	@Autowired
	@Qualifier("Itc04DocRulesValidatorChain")
	private DocRulesValidatorChain<Itc04HeaderEntity> chain;

	@Autowired
	@Qualifier("Itc04DocKeyGenerator")
	private DocKeyGenerator<Itc04HeaderEntity, String> keyGen;

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<Itc04HeaderEntity> documents, ProcessingContext context) {

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		for (Itc04HeaderEntity doc : documents) {
			
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
