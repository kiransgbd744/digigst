package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class InwardCanValidation implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docRepository;
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (!GSTConstants.CAN.equalsIgnoreCase(document.getSupplyType()))
			return errors;

		if (document.getDocKey() == null || document.getDocKey().isEmpty())
			return errors;

		docRepository = StaticContextHolder.getBean("InwardTransDocRepository",
				InwardTransDocRepository.class);

		List<Boolean> docList = docRepository
				.findDocsCountByDocKey(document.getDocKey());

		if (docList == null || docList.isEmpty()) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location 
			= new TransDocProcessingResultLoc(
					errorLocations.toArray());

			errors.add(
					new ProcessingResult(APP_VALIDATION, "ER1156",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
			return errors;
		}
      boolean isSubmitted = docList.get(0);
		
		if (isSubmitted) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					errorLocations.toArray());
			errors.add(
					new ProcessingResult(APP_VALIDATION, "ER1157",
							"Document cannot be cancelled as the same has "
									+ "been submitted on GSTN portal",
							location));
			return errors;

		}
		
		return errors;

	}

}
