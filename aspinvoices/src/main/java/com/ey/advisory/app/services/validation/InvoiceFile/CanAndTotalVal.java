package com.ey.advisory.app.services.validation.InvoiceFile;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class CanAndTotalVal
		implements B2csBusinessRuleValidator<Gstr1AsEnteredInvEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String cancelled = document.getCancelled();
		String totalNumber = document.getTotalNumber();
		Integer total = 0;
		if (totalNumber != null && !totalNumber.isEmpty()) {
			total = Integer.valueOf(totalNumber);
		}
		Integer can = 0;
		if (cancelled != null && !cancelled.isEmpty()) {
			can = Integer.valueOf(cancelled);
		}
		if (total < can) {
			errorLocations.add(GSTConstants.CAN);
			errorLocations.add(GSTConstants.TOTAL_NUM);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5516",
					"Count in Cancelled column cannot be more than count in "
					+ "Total Number column.",
					location));
		}
		return errors;
	}

}
