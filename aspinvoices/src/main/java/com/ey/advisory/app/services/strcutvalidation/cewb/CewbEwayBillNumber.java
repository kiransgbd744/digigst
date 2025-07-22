package com.ey.advisory.app.services.strcutvalidation.cewb;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class CewbEwayBillNumber implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Object ewbN = CommonUtility.exponentialAndZeroCheck(obj);// EWBNo

		if (!isPresent(ewbN)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.EWay_BillNo);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6115",
					"E-Waybill is mandatory.", location));
			return errors;
		}

		if (ewbN.toString().trim().length() > 12) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.EWay_BillNo);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6116",
					"Invalid E-Waybill number.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}

		return errors;
	}

}
