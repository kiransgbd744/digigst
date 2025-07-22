package com.ey.advisory.app.services.doc.gstr1a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.DocRulesValidatorService;
import com.ey.advisory.common.ProcessingContext;

@Service("Gstr1ASalesDocRulesValidatorService")
public class Gstr1ASalesDocRulesValidatorService implements
		DocRulesValidatorService<Gstr1AOutwardTransDocument, String> {

	@Autowired
	@Qualifier("Gstr1ADefaultSalesDocListRulesValidator")
	private DocListRulesValidator<Gstr1AOutwardTransDocument, String> chain;

	@Override
	public DocRulesValidationResult<String> validate(
			List<Gstr1AOutwardTransDocument> documents,
			ProcessingContext context) {

		return new DocRulesValidationResult<>(
				chain.validate(documents, context));
	}

}
