package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("DocNoStructureValidation")
public class DocNoStructureValidation implements DocRulesValidator<OutwardTransDocument> {
	

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
			if (document.getDocNo() != null
					&& !document.getDocNo().isEmpty()) {

				String docNo = document.getDocNo().trim();
				
				String regex = "^[a-zA-Z0-9/-]*$";
				Pattern pattern = Pattern.compile(regex);

				Matcher matcher = pattern.matcher(docNo);
				if (!matcher.matches()) {
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location 
					    = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0032",
							"Invalid Document Number.", location));
					return errors;
				}

			}
		
		return errors;
	}

}
