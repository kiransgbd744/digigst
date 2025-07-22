package com.ey.advisory.app.services.validation.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class ARCgstAmount
		implements ARBusinessRuleValidator<Gstr1AsEnteredAREntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		BigDecimal cgstAmt = BigDecimal.ZERO;
		String cgst = document.getCgstAmt();
		if (cgst != null) {
			cgstAmt = NumberFomatUtil.getBigDecimal(cgst);
		}

		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getNewPos() != null
					&& !document.getNewPos().isEmpty()) {
				if (!document.getSgstin().substring(0, 2)
						.equals(document.getNewPos())) {
					if (BigDecimal.ZERO.compareTo(cgstAmt) != 0) {
						errorLocations.add(CGST_AMOUNT);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER5122",
										"if First two digits of supplier GSTIN are different from New POS then CGST Amount should be Blank or 0",
										location));
					}
				}
			}
		}
		return errors;
	}

}