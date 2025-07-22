package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
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

public class Gstr6OriginalCreditNoteNumber implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) return errors;

		if(obj.toString().matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")){
			String mobileDecimalFormatStr = (String.valueOf(obj.toString().trim())).trim();
			BigDecimal mobileDecimalFormat = BigDecimal.ZERO;
			mobileDecimalFormat = new BigDecimal(mobileDecimalFormatStr);
			String mobileNo = String.valueOf(mobileDecimalFormat.longValue());
			row[12]=mobileNo;
			if(mobileNo.length() > 16){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CREDIT_NOTE_NUM);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3023",
						"Invalid Original Credit Note Number", location));
				return errors;
				
			}
		}
		if (!obj.toString().matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")) {
			String orgDocNo = obj.toString().trim();
			String regex = "^[a-zA-Z0-9/-]*$";
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(orgDocNo);
			if (orgDocNo.length() > 16
					|| !matcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CREDIT_NOTE_NUM);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3023",
						"Invalid Original Credit Note Number", location));
				return errors;
			}
		}
		return errors;

	}
}
