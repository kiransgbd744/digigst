package com.ey.advisory.app.services.validation.gstr1a.InvoiceFile;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

public class Gstr1ANetIssueVal
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredInvEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String cancelled = document.getCancelled();
		String totalNumber = document.getTotalNumber();
		String netIssue = document.getNetNumber();
		Long netNum = 0L;
		if (netIssue != null && !netIssue.isEmpty()) {
			netNum = Long.valueOf(netIssue);
		}
		Long total = 0L;
		if (totalNumber != null && !totalNumber.isEmpty()) {
			total = Long.valueOf(totalNumber);
		}
		Long can = 0L;
		if (cancelled != null && !cancelled.isEmpty()) {
			can = Long.valueOf(cancelled);
		}
		Long totMinusCan = total - can;
		if (netNum != totMinusCan && !netNum.equals(totMinusCan)) {
			errorLocations.add(GSTConstants.NET_NUM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5213",
					"Invalid Net number.", location));
			return errors;
		} else if (netNum.compareTo(totMinusCan) != 0) {
			errorLocations.add(GSTConstants.NET_NUM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5213",
					"Invalid Net number.", location));
			return errors;

		}
		return errors;
	}

}
