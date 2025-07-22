/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04RulesValidator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.DocRulesValidatorService;
import com.ey.advisory.common.ProcessingContext;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04DocRulesValidatorService")
public class Itc04DocRulesValidatorService
		implements DocRulesValidatorService<Itc04HeaderEntity, String> {

	@Autowired
	@Qualifier("Itc04DocListRulesValidator")
	private Itc04RulesValidator<Itc04HeaderEntity, String> chain;

	@Override
	public DocRulesValidationResult<String> validate(
			List<Itc04HeaderEntity> documents, ProcessingContext context) {

		return new DocRulesValidationResult<>(
				chain.validate(documents, context));
	}

}