package com.ey.advisory.app.services.strcutvalidation;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class BillToStateValidationRule implements ValidationRule {
	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository StatecodeRepository;
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(obj!=null){
			if(obj.toString().length() > 2){
				errorLocations.add(GSTConstants.BillToState);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "EXXXX",
						"Invalid Bill to State",
						location));
			}
		}
		/*errorLocations.add(GSTConstants.TAXABLE_VALUE);
		TransDocProcessingResultLoc location 
		                     = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
				"Invalid Taxable Value",
				location));*/
		/*if (obj!=null) {
			String s=obj.toString();
			StatecodeRepository = StaticContextHolder.
					getBean("StatecodeRepository",StatecodeRepository.class);
			int n=StatecodeRepository.findStateCode(s);
			if(n<=0){
			
				
				errorLocations.add(GSTConstants.BillToState);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "EXXXX",
						"Invalid Bill to State",
						location));
			}*/
			/*Converter converter = converterFactory.getConverter(obj,
					String.class);
			if (converter != null) {
				// List<String> cgstinList = new ArrayList<>();
				String cgstin = (String) converter.convert(obj);
				if (!(cgstin.length() == 15 && gstinLookUpServiceImpl
						.isValidGstinForGroup(cgstin, "ern00002"))) {
					return errors;
				}
			}*/
		//}
		return errors;
	}

}
