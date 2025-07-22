
package com.ey.advisory.app.services.validation.eInvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.DocRulesValidatorService;
import com.ey.advisory.common.ProcessingContext;

@Service("EinvoiceExtraSalesDocRulesValidatorService")
public class EinvoiceExtraSalesDocRulesValidatorService implements 
			DocRulesValidatorService<OutwardTransDocument, String> {

	@Autowired
	@Qualifier("EinvoiceDefaultExtraSalesDocListRulesValidator")
	private DocListRulesValidator<OutwardTransDocument, String> chain;
	
	@Override
	public DocRulesValidationResult<String> validate(
			List<OutwardTransDocument> documents, ProcessingContext context) {
		
		return new DocRulesValidationResult<>(
					chain.validate(documents, context));
	}

}
