/**
 * 
 */
package com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1ATxpdInter implements
		B2csBusinessRuleValidator<Gstr1AAsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr1AAsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String supplierGstin = (document.getSgstin() != null)
				? document.getSgstin().trim().substring(0, 2) : null;
		String pos = (document.getNewPOS() != null)
				? document.getNewPOS().trim() : null;

		BigDecimal cgstAmt = BigDecimal.ZERO;
		String cgst = (document.getCentralTaxAmount() != null)
				? document.getCentralTaxAmount().trim() : null;
		if (cgst != null && !cgst.isEmpty()) {
			cgstAmt = NumberFomatUtil.getBigDecimal(cgst);
		}
		BigDecimal sgstAmt = BigDecimal.ZERO;
		String sgsts = (document.getStateUTTaxAmount() != null)
				? document.getStateUTTaxAmount().trim() : null;
		if (sgsts != null && !sgsts.isEmpty()) {
			sgstAmt = NumberFomatUtil.getBigDecimal(sgsts);
		}

		BigDecimal sum = cgstAmt.add(sgstAmt);

		if (supplierGstin != null && pos != null) {
			if (!supplierGstin.equalsIgnoreCase(pos)) {

				if (sum.compareTo(BigDecimal.ZERO) > 0) {
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5513",
							" CGST / SGST cannot be applied in case of "
									+ "INTER state supply.",
							location));
					return errors;

				}
			}
		}
		return errors;
	}

}
