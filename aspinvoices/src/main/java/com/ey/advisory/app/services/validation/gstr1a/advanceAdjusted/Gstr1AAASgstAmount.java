package com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AAASgstAmount implements
		B2csBusinessRuleValidator<Gstr1AAsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr1AAsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		BigDecimal sgstAmt = BigDecimal.ZERO;
		String sgst = (document.getStateUTTaxAmount() != null)
				? document.getStateUTTaxAmount().trim() : null;
		if (sgst != null) {
			sgstAmt = NumberFomatUtil.getBigDecimal(sgst);
		}

		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getNewPOS() != null
					&& !document.getNewPOS().isEmpty()) {
				if (!document.getSgstin().trim().substring(0, 2)
						.equalsIgnoreCase(document.getNewPOS().trim())) {
					if (sgstAmt.compareTo(BigDecimal.ZERO) != 0) {
						errorLocations.add(SGST_AMOUNT);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER5173",
										"if First two digits of supplier GSTIN "
												+ "are different from New POS then  "
												+ "SGST Amount should be Blank or 0. ",
										location));
					}
				}
			}
		}
		return errors;
	}

}