package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1AARSgstAmount
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		BigDecimal sgstAmt = BigDecimal.ZERO;
		String sgsts = document.getSgstAmt();
		if (sgsts != null) {
			sgstAmt = NumberFomatUtil.getBigDecimal(sgsts);
		}
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getNewPos() != null
					&& !document.getNewPos().isEmpty()) {
				if (!document.getSgstin().substring(0, 2)
						.equals(document.getNewPos())) {
					if (BigDecimal.ZERO.compareTo(sgstAmt) != 0) {
						errorLocations.add(SGST_AMOUNT);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER5124",
										"if First two digits of supplier GSTIN are different from New POS then  SGST Amount should be Blank or 0",
										location));
					}
				}
			}
		}
		return errors;
	}
}
