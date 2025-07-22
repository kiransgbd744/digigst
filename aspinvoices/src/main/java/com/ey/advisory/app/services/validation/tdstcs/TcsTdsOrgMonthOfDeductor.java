package com.ey.advisory.app.services.validation.tdstcs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TcsTdsOrgMonthOfDeductor
		implements B2csBusinessRuleValidator<Gstr2XExcelTcsTdsEntity> {

	private static final List<String> CATEGORIES = Arrays.asList("TCSA",
			"TDSA");

	@Override
	public List<ProcessingResult> validate(Gstr2XExcelTcsTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		String recordType = document.getRecordType();
		String orgDeductorUplMonth = document.getOrgDeductorUplMonth();

		if (CATEGORIES.contains(recordType)) {
			if (orgDeductorUplMonth == null
					|| orgDeductorUplMonth.trim().isEmpty()) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ORG_MON_OF_DEDUCTOR);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1339",
						"OriginalMonthofDeductor/CollectorUpload cannot be left blank",
						location));
				return errors;
			}
		}
		if (document.getOrgDeductorUplMonth() != null
				&& !document.getOrgDeductorUplMonth().isEmpty()) {
			String orgMpnOfDed = "01" + document.getOrgDeductorUplMonth();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod = LocalDate.parse(orgMpnOfDed, formatter);
			if (returnPeriod.compareTo(pregst) < 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ORG_MON_OF_DEDUCTOR);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1338",
						"OriginalMonthofDeductor/CollectorUpload cannot be before July 2017",
						location));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("OriginalMonthofDeductor/CollectorUpload cannot"
							+ " be before July 2017 error is {} ", errors);
					
				}
				return errors;
			}
		}
		return errors;
	}

}
