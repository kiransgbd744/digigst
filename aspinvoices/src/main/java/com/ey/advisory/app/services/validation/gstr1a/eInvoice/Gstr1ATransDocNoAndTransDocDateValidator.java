package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ATransDocNoAndTransDocDateValidator")
public class Gstr1ATransDocNoAndTransDocDateValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> MODE = ImmutableList.of(GSTConstants.RAIL,
			GSTConstants.AIR, GSTConstants.SHIP);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (MODE.contains(
				trimAndConvToUpperCase(document.getTransportMode()))) {

			if (document.getTransportDocNo() == null
					|| document.getTransportDocNo().isEmpty()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TRANS_DOC_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15005",
						"transportDocNo Cannot  be left blank", location));

			}
			if (document.getTransportDocDate() == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TRANS_DOC_DATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15006",
						"transportDocDate Cannot  be left blank", location));

			}
		}
		return errors;
	}

}
