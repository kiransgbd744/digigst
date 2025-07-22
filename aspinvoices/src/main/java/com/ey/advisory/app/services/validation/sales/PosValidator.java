/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.
																APP_VALIDATION;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
*//**
 * 
 * @author Murali.s
 * BR_OUTWARD_53
 *
 *//*

@Component("PosValidator")
public class PosValidator implements DocRulesValidator<OutwardTransDocument> {
	
	private static final List<String> SUPPLY_TYPES_REQUIRING_IMPORTS = 
			ImmutableList.of("EXPT", "EXPWT");

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String supplyType = document.getSupplyType();
		String pos = document.getPos();
		if(SUPPLY_TYPES_REQUIRING_IMPORTS.contains(supplyType)) {
			return errors;
		}
		if (pos == null || pos.isEmpty()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.POS);
			TransDocProcessingResultLoc location = 
			   new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER102",
					"Pos is Mandatory if customer gstin is not available",
					location));
			return errors;
		}
		return errors;

	}
}*/