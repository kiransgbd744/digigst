/**
 * 
 */
package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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

/**
 * @author Mahesh.Golla
 *
 */
public class IntraOrInter implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		BigDecimal common = BigDecimal.ZERO;
		BigDecimal igst =  common;
		String igsts = document.getIntegratedTaxAmount();
		if(igsts != null && !igsts.isEmpty()){
			igst = NumberFomatUtil.getBigDecimal(igsts);	
		}
		BigDecimal cgst =  common;
		String cgsts = document.getCentralTaxAmount();
		if(cgsts != null && !cgsts.isEmpty()){ 
			cgst = NumberFomatUtil.getBigDecimal(cgsts);	
		}
		
		BigDecimal sgst =  common;
		String sgstss = document.getStateUTTaxAmount();
		if(sgstss != null && !sgstss.isEmpty()){
			sgst = NumberFomatUtil.getBigDecimal(sgstss);	
		}
		

		if ((igst.compareTo(common) != 0 && sgst.compareTo(common) != 0
				&& cgst.compareTo(common) != 0)
				|| (igst.compareTo(common) != 0 && sgst.compareTo(common) != 0)
				|| (igst.compareTo(common) != 0
						&& cgst.compareTo(common) != 0)) {

			errorLocations.add(GSTConstants.IGST_AMOUNT);
			errorLocations.add(GSTConstants.CGST_AMOUNT);
			errorLocations.add(GSTConstants.SGST_AMOUNT);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0426",
					" A document can either be intra-State or inter-State.",
					location));
			return errors;

		}

		return errors;

	}
}
