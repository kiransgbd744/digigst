package com.ey.advisory.app.services.validation.purchase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocListRulesValidator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.DocRulesValidatorService;
import com.ey.advisory.common.ProcessingContext;

@Component("PurchaseDocRulesValidatorService")
public class PurchaseDocRulesValidatorService
		implements DocRulesValidatorService<InwardTransDocument, String> {

	@Autowired
	@Qualifier("DefaultPurchaseDocListRulesValidator")
	private DocListRulesValidator<InwardTransDocument, String> chain;

	@Override
	public DocRulesValidationResult<String> validate(
			List<InwardTransDocument> documents, ProcessingContext context) {

		return new DocRulesValidationResult<>(
				chain.validate(documents, context));
	}

}
