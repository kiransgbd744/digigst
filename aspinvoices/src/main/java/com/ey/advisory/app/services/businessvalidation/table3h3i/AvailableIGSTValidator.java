package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class AvailableIGSTValidator
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = 
			ImmutableList
			.of("CG", "IG", "IS");

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getEligibilityIndicator() != null
				&& !document.getEligibilityIndicator().isEmpty()) {
			if (ORGDOCNUM_REQUIRING_IMPORTS
					.contains(document.getEligibilityIndicator())) {
				
				BigDecimal availIgsts = BigDecimal.ZERO;
				String availIgst = document.getAvailableIGST();
				if(availIgst != null && !availIgst.isEmpty()){
					availIgsts = NumberFomatUtil.getBigDecimal(availIgst);
				}
				
				BigDecimal igst = BigDecimal.ZERO;
				String igsts = document.getIntegratedTaxAmount();
				if(igsts != null && !igsts.isEmpty()){
					igst = NumberFomatUtil.getBigDecimal(igsts);
				}

                if(availIgst != null){
				if (availIgsts.compareTo(BigDecimal.ZERO) < 0
						|| availIgsts.compareTo(igst) > 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.AVAILABLE_IGST);
					TransDocProcessingResultLoc location =
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, " ER1229",
							" Available credit cannot exceed than Tax on "
									+ "document or zero where eligibility "
									+ "indicator is IG / CG/ IS.",
							location));
					return errors;
				}}
			}
			if (document.getEligibilityIndicator().equalsIgnoreCase("NO")) {
				
				BigDecimal availIgsts = BigDecimal.ZERO;
				String availIgst = document.getAvailableIGST();
				if(availIgst != null){
					availIgsts = NumberFomatUtil.getBigDecimal(availIgst);
				}
				if (availIgst != null &&
						availIgsts.compareTo(BigDecimal.ZERO) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.AVAILABLE_IGST);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1230",
							"Available credit should be zero or Blank"
									+ " where value in Eligibility "
									+ "Indicator in 'NO'.",
							location));
					return errors;
				}
			}
		}
		return errors;
	}
}
