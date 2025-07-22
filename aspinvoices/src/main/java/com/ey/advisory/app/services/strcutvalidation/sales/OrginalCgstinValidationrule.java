package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.services.strcutvalidation.DataConverterFactory;
import com.ey.advisory.app.services.strcutvalidation.GstinDetailsLookUpServiceImpl;
import com.ey.advisory.app.services.strcutvalidation.StateServiceImpl;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class OrginalCgstinValidationrule  implements ValidationRule {
	

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                   TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
		String cgstin = null;
		
			if(obj!=null){
			cgstin=obj.toString();
			
			 if(obj.toString().length() != 15 
					 
					 || !cgstin.matches("[a-zA-Z0-9]+")){
			
				errorLocations.add(GSTConstants.ORG_CGSTN);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER063",
						"Invalid Original Customer GSTIN",
						location));
				
		}
		}
		
		return errors;
	}

}
