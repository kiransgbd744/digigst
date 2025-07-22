package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

@Component("Gstr1AEinvoiceDefaultExtraSalesDocListRulesValidator")
public class Gstr1AEinvoiceDefaultExtraSalesDocListRulesValidator
		implements DocListRulesValidator<Gstr1AOutwardTransDocument, String> {

	@Autowired
	@Qualifier("DefaultGstr1AOutwardTransDocKeyGenerator")
	private DocKeyGenerator<Gstr1AOutwardTransDocument, String> keyGen;

	@Autowired
	@Qualifier("Gstr1AEinvoiceExtraSalesDocRulesValidatorChain")
	private DocRulesValidatorChain<Gstr1AOutwardTransDocument> chain;

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<Gstr1AOutwardTransDocument> documents,
			ProcessingContext context) {

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		for (Gstr1AOutwardTransDocument doc : documents) {
			String docKey = keyGen.generateKey(doc);
			List<ProcessingResult> results = chain.validate(doc, context);
			retResultMap.put(docKey, results);
		}

		return retResultMap;
	}

}
