package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.ORG_RETURN_PERIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr7OrgReturnPeriodValidator implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String returnPeriod = document.getReturnPeriod();
		String origReturnPeriod = document.getOrgRetPeriod();
		
		if(StringUtils.isNotBlank(returnPeriod) && StringUtils.isNotBlank(origReturnPeriod)) {
			int orgMonth = Integer.parseInt(origReturnPeriod.substring(0, 2));
			int orgYear = Integer.parseInt(origReturnPeriod.substring(2));
			int rpMonth = Integer.parseInt(returnPeriod.substring(0, 2));
			int rpYear = Integer.parseInt(returnPeriod.substring(2));
			LocalDate orgDate = LocalDate.of(orgYear, orgMonth, 1);
			LocalDate rpDate = LocalDate.of(rpYear, rpMonth, 1);
			
			if(orgDate.isAfter(rpDate)) {
				errorLocations.add(ORG_RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3057", "Original Return Period should be before Return Period",
						location));
				return errors;
			}
		}
		
		if (document.getOrgRetPeriod() != null && !document.getOrgRetPeriod().isEmpty()) {
			String tax = "01" + document.getReturnPeriod().trim();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod1 = LocalDate.parse(tax, formatter);
			if (returnPeriod1.compareTo(pregst) < 0) {
				errorLocations.add(ORG_RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2028", "Original Return Period cannot be before 072017",
						location));
				return errors;

			}
		}
		return errors;
	}
}
	
	


