package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1AOrgUom
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity item,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (item.getOrgUom() == null || item.getOrgUom().isEmpty()) {
			item.setOrgUom(GSTConstants.OTH);
		}
		String uom = item.getOrgUom();
		String orgHsn = item.getOrgHsnOrSac();

		/*if (uom.equalsIgnoreCase("NA")) {
			return errors;
		}*/
		
		
		uomCache = StaticContextHolder.getBean("DefaultUomCache",
				UomCache.class);
		int n = uomCache.finduom(trimAndConvToUpperCase(uom));

		if (n <= 0) {
			item.setOrgUom(GSTConstants.OTH);
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ORGUOM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER5013",
					"Invalid OrgUom.", location));
			return errors;
		}
		if(orgHsn != null && !orgHsn.isEmpty()){
			 if(GSTConstants.NA.equalsIgnoreCase(item.getOrgUom()) 
    			 && !orgHsn.startsWith("99")){
     		
				 Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ORGUOM);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER5013",
							"Invalid OrgUom.", location));
					return errors;
     	}
		}

		return errors;
	}

}
