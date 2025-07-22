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

public class AvailableSGSTValidator
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
				BigDecimal availSgsts = BigDecimal.ZERO;
				String availSgst = document.getAvailableSGST();
				if(availSgst != null && !availSgst.isEmpty()){
					availSgsts =NumberFomatUtil.getBigDecimal(availSgst);
				}
				
				BigDecimal sgst = BigDecimal.ZERO;
				String sgsts = document.getStateUTTaxAmount();
				if(sgsts != null && !sgsts.isEmpty()){
					sgst =NumberFomatUtil.getBigDecimal(sgsts);
				}
				if(availSgst != null){
				if (availSgsts.compareTo(BigDecimal.ZERO) < 0
						|| availSgsts.compareTo(sgst) > 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.AVAILABLE_SGST);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, " ER1233",
							" Available credit cannot exceed than Tax on "
									+ "document or zero where eligibility "
									+ "indicator is IG / CG/ IS.",
							location));
					return errors;
				}}
			}
			if (document.getEligibilityIndicator() != null && 
					document.getEligibilityIndicator().equalsIgnoreCase("NO")) {
				BigDecimal availSgsts = BigDecimal.ZERO;
				String availSgst = document.getAvailableSGST();
				if(availSgst != null){
					availSgsts = NumberFomatUtil.getBigDecimal(availSgst);
				}
				if (availSgst != null && 
						availSgsts.compareTo(BigDecimal.ZERO) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.AVAILABLE_SGST);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1234",
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
