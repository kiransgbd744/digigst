package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.FY;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9FyValidator
		implements BusinessRuleValidator<Gstr9HsnAsEnteredEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr9HsnAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String fy = document.getFy() != null && !document.getFy().isEmpty()
				? "0103" + document.getFy().trim().substring(5) : null;

		if (fy != null) {
			List<String> errorLocations = new ArrayList<>();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod = LocalDate.parse(fy, formatter);
			if (returnPeriod.compareTo(pregst) < 0) {
				errorLocations.add(RETURN_PREIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6188",
						"Tax Period cannot be before 2017", location));
				return errors;

			}
		}
		return errors;
	}

}

		//List<String> errorLocations = new ArrayList<>();
		/*String fyYear = document.getFy();// 2016-2017
		if (fyYear.contains("-")) {
			String fiArray[] = fyYear.split("-");
			if (fyYear != null && !fyYear.isEmpty()
					&& Integer.parseInt(fiArray[0]) < 2017) {
				errorLocations.add(FY);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6195",
						"Tax Period cannot be before 2017", location));
				return errors;
			}
		} else {
			errorLocations.add(FY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6196",
					"Invalid Tax Period", location));
			return errors;
		}
		return errors;
	}

}
*/