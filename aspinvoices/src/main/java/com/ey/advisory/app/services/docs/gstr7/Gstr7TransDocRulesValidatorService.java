package com.ey.advisory.app.services.docs.gstr7;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.DocRulesValidatorService;
import com.ey.advisory.common.ProcessingContext;

@Service("Gstr7TransDocRulesValidatorService")
public class Gstr7TransDocRulesValidatorService
		implements DocRulesValidatorService<Gstr7TransDocHeaderEntity, String> {

	@Autowired
	@Qualifier("Gstr7TransSalesDocListRulesValidator")
	private Gstr7TransDocListRulesValidator<Gstr7TransDocHeaderEntity, String> chain;

	@Override
	public DocRulesValidationResult<String> validate(
			List<Gstr7TransDocHeaderEntity> documents,
			ProcessingContext context) {

		return new DocRulesValidationResult<>(
				chain.validate(documents, context));
	}

}
