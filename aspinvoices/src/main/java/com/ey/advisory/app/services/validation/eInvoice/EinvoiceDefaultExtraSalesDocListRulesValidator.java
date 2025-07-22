package com.ey.advisory.app.services.validation.eInvoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

@Component("EinvoiceDefaultExtraSalesDocListRulesValidator")
public class EinvoiceDefaultExtraSalesDocListRulesValidator
		implements DocListRulesValidator<OutwardTransDocument, String> {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DocKeyGenerator<OutwardTransDocument, String> keyGen;

	
	@Autowired
	@Qualifier("EinvoiceExtraSalesDocRulesValidatorChain")
	private DocRulesValidatorChain<OutwardTransDocument> chain;
	
	

	@Override
	public Map<String, List<ProcessingResult>> validate(
			List<OutwardTransDocument> documents, ProcessingContext context) {
		
		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		for (OutwardTransDocument doc: documents) {
			String docKey = keyGen.generateKey(doc);
			List<ProcessingResult> results = chain.validate(doc, context);
			retResultMap.put(docKey, results);
		}
		
		return retResultMap;
	}

	

}
