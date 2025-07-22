package com.ey.advisory.app.services.strcutvalidation.einvoice;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class ProjectReferenceValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) {
			return errors;
		}
		
		/*if (obj.toString().trim().length() > 20) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.PROJECT_REFERENCE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10106",
					          "Invalid Project Reference.", location));
			return errors;
		}
		if(obj.toString().matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")){
			String mobileDecimalFormatStr = (String.valueOf(obj.toString().trim())).trim();
			BigDecimal DocNODecimalFormat = BigDecimal.ZERO;
			DocNODecimalFormat = new BigDecimal(mobileDecimalFormatStr);
			String docNO = String.valueOf(DocNODecimalFormat.longValue());
			row[25]=docNO;
			if(docNO.length() > 20){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.PROJECT_REFERENCE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10106",
						          "Invalid Project Reference.", location));
				return errors;
				
			}
		}*/
		if(!obj.toString().matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")){
			String docNo = obj.toString().trim();
			String regex = "^['('')'a-zA-Z0-9/-]*$"; 
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(docNo);
			if (docNo.trim().length() > 20 || !matcher.matches()) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.PROJECT_REFERENCE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10106",
						          "Invalid Project Reference.", location));
				return errors;
			} 
		}
		return errors;
	}

}

