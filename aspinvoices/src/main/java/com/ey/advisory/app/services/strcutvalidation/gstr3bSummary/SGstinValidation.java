package com.ey.advisory.app.services.strcutvalidation.gstr3bSummary;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class SGstinValidation implements ValidationRule {
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;
	
	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER052-1",
					"Supplier GSTIN is mandatory.",
					location));
			return errors;
		}
		if(obj.toString().trim().length() != 15){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER052-2",
					"Invalid Supplier GSTIN.",
					location));
			return errors;
		}
//		if (!gstinList.contains(gstin.trim())) {
//			  
//			  String errMsg = "Supplier GSTIN is not as per On-Boarding data"; Set<String>
//			  errorLocations = new HashSet<>();
//			  errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
//			  TransDocProcessingResultLoc location = new TransDocProcessingResultLoc( idx,
//			  errorLocations.toArray()); validationResult.add(new
//			  ProcessingResult(APP_VALIDATION, "ER-1007-2", errMsg, location)); return; }
//			 
//
//			rowData[index] = rowData[index].toString().trim();
//		}

			return errors;

	}

}
