package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class NewUom
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity item,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (item.getNewUom() == null || item.getNewUom().isEmpty()) {
			item.setNewUom(GSTConstants.OTH);
		}

		String uom = item.getNewUom();
		String newHsn = item.getNewHsnOrSac();
		/*if (uom.equalsIgnoreCase("NA")) {
			return errors;
		}*/
		
		
		
		uomCache = StaticContextHolder.getBean("DefaultUomCache",
				UomCache.class);
		int n = uomCache.finduom(trimAndConvToUpperCase(uom));

		if (n <= 0) {
			item.setOrgUom(GSTConstants.OTH);
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.NEW_UOM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER5021",
					"Invalid NewUqc.", location));
			return errors;
		}
		if(newHsn != null && !newHsn.isEmpty()){
			 if(GSTConstants.NA.equalsIgnoreCase(item.getNewUom()) 
	      			 && !newHsn.startsWith("99")){
	       		
					 Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.NEW_UOM);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());

						errors.add(new ProcessingResult(APP_VALIDATION, "ER5021",
								"Invalid NewUqc.", location));
						
						return errors;
					
	       	}
			}

		return errors;
	}

}
