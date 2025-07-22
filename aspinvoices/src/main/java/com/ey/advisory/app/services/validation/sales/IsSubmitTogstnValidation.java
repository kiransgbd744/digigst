package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.docs.DefaultOutwardTransDocKeyGenerator;
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
public class IsSubmitTogstnValidation
		implements DocRulesValidator<OutwardTransDocument> {
	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator dockey;
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;
		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;
		if (document.getDocNo() == null || document.getDocNo().isEmpty())
			return errors;
		if (document.getFinYear() == null || document.getFinYear().isEmpty())
			return errors;
		dockey = StaticContextHolder
				.getBean("DefaultOutwardTransDocKeyGenerator",
						DefaultOutwardTransDocKeyGenerator.class);
		//String key = dockey.generateKey(document);
		docRepository = StaticContextHolder.getBean("DocRepository",
				DocRepository.class);
		
		List<Boolean> docList = docRepository
				.findDocsCountByDocKey(document.getDocKey());
		if (docList == null || docList.isEmpty())
			return errors;
		Boolean isSubmitted = docList.get(0);
		if (isSubmitted) {
			errorLocations.add(GSTConstants.RETURN_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0516",
					"Record has been reported in earlier Return Period",
					location));
		}
		return errors;
	}

}
