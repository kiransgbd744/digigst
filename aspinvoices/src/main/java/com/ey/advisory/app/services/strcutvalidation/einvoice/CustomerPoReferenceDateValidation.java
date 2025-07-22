package com.ey.advisory.app.services.strcutvalidation.einvoice;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class CustomerPoReferenceDateValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) {
			return errors;
		}
		
		/*if (obj.toString().trim().length() > 20) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CUSTOMER_PO_REFERENCE_DATE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10107",
					          "Invalid Customer POReference Date.", location));
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
				errorLocations.add(GSTConstants.CUSTOMER_PO_REFERENCE_DATE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10107",
						          "Invalid Customer POReference Date.", location));
				return errors;
				
			}
		}*/
		LocalDate date = DateFormatForStructuralValidatons.parseObjToDate(
                obj.toString().trim());
        if (date == null ) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CUSTOMER_PO_REFERENCE_DATE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10107",
						          "Invalid Customer POReference Date.", location));
				return errors;
			} 
		
		return errors;
	}

}
