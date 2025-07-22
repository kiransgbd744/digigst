package com.ey.advisory.app.services.validation.gstr1a.HsnSacSummery;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredHsnEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1AUom
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredHsnEntity> {

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	/*
	 * private static final List<String> UQC_REQUIRING_IMPORTS =
	 * ImmutableList.of( "BAG", "BAL", "BDL", "BKL", "BOU", "BOX", "BTL", "BUN",
	 * "CAN", "CBM", "CCM", "CMS", "CTN", "DOZ", "DRM", "GGR", "GMS", "GRS",
	 * "GYD", "KGS", "KLR", "KME", "MLT", "MTR", "NOS", "PAC", "PCS", "PRS",
	 * "QTL", "ROL", "SET", "SQF", "SQM", "SQY", "TBS", "TGM", "THD", "TON",
	 * "TUB", "UGS", "UNT", "YDS");
	 */
	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredHsnEntity item,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (item.getUqc() == null || item.getUqc().isEmpty()) {
			item.setUqc(GSTConstants.OTH);
		}
		String hsnOrSac = item.getHsn();

		/*if (GSTConstants.NA.equalsIgnoreCase(item.getUqc())){
			return errors;
		}*/
		String uom = item.getUqc();
		
		
		uomCache = StaticContextHolder.getBean("DefaultUomCache",
				UomCache.class);
		int n = uomCache.finduom(trimAndConvToUpperCase(uom));

		if (n <= 0) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.UQC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER5706",
					"Invalid UQC.", location));
			return errors;
		}

		if(hsnOrSac != null && !hsnOrSac.isEmpty()){
			 if(GSTConstants.NA.equalsIgnoreCase(item.getUqc()) 
     			 && !hsnOrSac.startsWith("99")){
      		
				 Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.UQC);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER5706",
							"Invalid UQC.", location));
					return errors;
      	}
		}
		
		return errors;
	}

}
