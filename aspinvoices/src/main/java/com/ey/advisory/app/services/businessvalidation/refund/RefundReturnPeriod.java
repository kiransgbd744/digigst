package com.ey.advisory.app.services.businessvalidation.refund;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class RefundReturnPeriod implements 
                             BusinessRuleValidator<RefundsExcelEntity> {
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Override
	public List<ProcessingResult> validate(RefundsExcelEntity document,
			ProcessingContext context) {
		

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getRetPeriod() != null
				&& !document.getRetPeriod().isEmpty()) {
			String tax = "01" + document.getRetPeriod().trim();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod = LocalDate.parse(tax, formatter);
			if (returnPeriod.compareTo(pregst) < 0) {
				errorLocations.add(RETURN_PREIOD);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1880",
						"Return Period cannot be before 072017", location));

			}
		}
		return errors;
	}

}
