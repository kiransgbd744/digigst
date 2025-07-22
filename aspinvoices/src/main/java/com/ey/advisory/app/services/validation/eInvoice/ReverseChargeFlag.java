package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("ReverseChargeFlag")
public class ReverseChargeFlag
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> FLAG = ImmutableList
			.of(GSTConstants.Y, GSTConstants.N);
	
	private static final List<String> FLAG_2_VERSION = ImmutableList
			.of(GSTConstants.Y, GSTConstants.N,GSTConstants.L);
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getReverseCharge() != null
				&& !document.getReverseCharge().isEmpty()) {
			
			List<String> flags=new ArrayList<>();
			//if (gstnApi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase())) {
			if(CommonContext.getDelinkingFlagContext()){
				flags.addAll(FLAG_2_VERSION);
			}else{
				flags.addAll(FLAG);
			}
			if (!flags.contains(document.getReverseCharge().toUpperCase())) {
				
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ReverseCharge);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0094",
						"Invalid Reverse Charge Flag", location));

			}
		}
		return errors;
	}

}
