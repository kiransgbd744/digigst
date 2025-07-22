package com.ey.advisory.app.services.validation.NilNonExpt;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NilNonExptedUQC implements
		B2csBusinessRuleValidator<Gstr1NilNonExemptedAsEnteredEntity> {
	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	@Override
	public List<ProcessingResult> validate(
			Gstr1NilNonExemptedAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getUqc() == null || document.getUqc().isEmpty()) {
			document.setUqc(GSTConstants.OTH);
		}
		String uom = document.getUqc();
		String hsnOrSac = document.getHsn();

		/*if (uom.equalsIgnoreCase("NA")) {
			return errors;
		
		}*/
				
		uomCache = StaticContextHolder.getBean("DefaultUomCache",
				UomCache.class);
		int n = uomCache.finduom(trimAndConvToUpperCase(uom));

		if (n <= 0) {

			document.setUqc(GSTConstants.OTH);
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.UQC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5706",
					"Invalid UQC.", location));
			
			return errors;


		}
		 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"hsn value {} ",
						hsnOrSac);
			}
		 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"uqc value {} ",
						document.getUqc());
			}
		
		if(hsnOrSac != null && !hsnOrSac.isEmpty()){
			 if(GSTConstants.NA.equalsIgnoreCase(document.getUqc()) 
     			 && !hsnOrSac.startsWith("99")){
      		
				 Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.UQC);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER5706",
							"Invalid UQC.", location));
					
					 if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"errors  {} ",
									errors);
						}
					
					return errors;
			
      	}
		}


		return errors;
	}
}
