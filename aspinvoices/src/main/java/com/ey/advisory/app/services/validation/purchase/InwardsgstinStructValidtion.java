package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import com.google.common.base.Strings;

public class InwardsgstinStructValidtion 
implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document, 
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (Strings.isNullOrEmpty(document.getSgstin())) return errors;
		String sgstin = document.getSgstin().trim();
		if (GSTConstants.URP.equalsIgnoreCase(sgstin))
			return errors;
		if (sgstin.length() == 15 || sgstin.trim().length() == 10) {
			// nothing
		} else {
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location 
			= new TransDocProcessingResultLoc(null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, 
					"ER1052", "Invalid Supplier GSTIN.", location));
			return errors;
		}
		
		if (sgstin.length() == 15) {
			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";
			
			String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";
				Pattern pattern = Pattern.compile(regex);
			
				Pattern pattern1 = Pattern.compile(regex1);
				
				  Matcher matcher = pattern.matcher(sgstin);
				  Matcher matcher1 = pattern1.matcher(sgstin);
			if (matcher.matches() || matcher1.matches()) {
				
			}else{
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1052",
					"Invalid Supplier GSTIN.", location));
			return errors;
		}
		}
		else if (sgstin.length() == 10) {
			String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
					
			
			String regex1 = "^[A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z]$";
				Pattern pattern = Pattern.compile(regex);
			
				Pattern pattern1 = Pattern.compile(regex1);
				
				  Matcher matcher = pattern.matcher(sgstin);
				  Matcher matcher1 = pattern1.matcher(sgstin);
			if (matcher.matches() || matcher1.matches()) {
				
			}else{
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1052",
					"Invalid Supplier GSTIN.", location));
			return errors;
		}
		}

		return errors;
	}

}
