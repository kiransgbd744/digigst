package com.ey.advisory.app.services.strcutvalidation.inward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
public class EWayBillNumberValidationRule implements ValidationRule {

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
			row[113]=mobileNo;
			if(mobileNo.length() > 16){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.EWay_BillNo);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1117",
						"Invalid E-Way Bill Number", location));
				return errors;
				
			}
		}
		if (!obj.toString().matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")) {
			String docNo = obj.toString().toLowerCase();
			String regex = "^[a-zA-Z0-9/-]*$";
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(docNo);
			if (docNo.length() > 16
					|| !matcher.matches()) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.EWay_BillNo);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1117",
						"Invalid E-Way Bill Number", location));
				return errors;
			}
		}
		return errors;
	}

}
