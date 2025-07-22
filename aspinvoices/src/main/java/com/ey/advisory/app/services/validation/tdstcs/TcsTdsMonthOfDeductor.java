package com.ey.advisory.app.services.validation.tdstcs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
public class TcsTdsMonthOfDeductor implements
		B2csBusinessRuleValidator<Gstr2XExcelTcsTdsEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr2XExcelTcsTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getDeductorUplMonth() != null
				&& !document.getDeductorUplMonth().isEmpty()) {
			String montOfDed = "01" + document.getDeductorUplMonth();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01); 
			// Calculate the last day of the month.
			LocalDate returnPeriod = LocalDate.parse(montOfDed, formatter);
			if (returnPeriod.compareTo(pregst) < 0) {
				errorLocations.add(GSTConstants.TDS_MONTH);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1332",
						"MonthofDeductor/CollectorUpload cannot be before July 2017",
						location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("MonthofDeductor/CollectorUpload cannot be "
							+ "before July 2017 error is {} ", errors);
					
				}
				return errors;

			}
		}
		return errors;
	}

}
