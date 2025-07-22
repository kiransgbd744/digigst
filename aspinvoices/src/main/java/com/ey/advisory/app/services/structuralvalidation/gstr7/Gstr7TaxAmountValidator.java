package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr7TaxAmountValidator implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document, ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActType()))
			return errors;
		Set<String> errorLocations = new HashSet<>();
		String igst = document.getIgstAmt() != null && !document.getIgstAmt().toString().trim().isEmpty()
				? document.getIgstAmt().trim() : null;
		String cgst = document.getCgstAmt() != null && !document.getCgstAmt().toString().trim().isEmpty()
				? document.getCgstAmt().trim() : null;
		String sgst = document.getSgstAmt() != null && !document.getSgstAmt().toString().trim().isEmpty()
				? document.getSgstAmt().trim() : null;

		if (igst == null || igst.isEmpty()) {
			igst = "0";

		}
		if (cgst == null || cgst.isEmpty()) {
			cgst = "0";

		}
		if (sgst == null || sgst.isEmpty()) {
			sgst = "0";

		}
		BigDecimal bigst = new BigDecimal(igst);
		BigDecimal bcgst = new BigDecimal(cgst);
		BigDecimal bsgst = new BigDecimal(sgst);

		BigDecimal all = bigst.add(bcgst).add(bsgst);
		if (all.compareTo(BigDecimal.ZERO) == 0) {
			String constant = GSTConstants.TDS_IGST + "" + GSTConstants.TDS_CGST + "" + GSTConstants.TDS_SGST;
			errorLocations.add(constant);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2030",
					"Accepted Tax Amount combination is (TDSIGST) or (TDSCGST & TDSSGST).", location));

			return errors;
		}

		if ((bigst.compareTo(BigDecimal.ZERO) > 0) && (bcgst.compareTo(BigDecimal.ZERO) > 0)
				&& (bsgst.compareTo(BigDecimal.ZERO) > 0)) {

			String constant = GSTConstants.TDS_IGST + "" + GSTConstants.TDS_CGST + "" + GSTConstants.TDS_SGST;
			errorLocations.add(constant);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2030",
					"Accepted Tax Amount combination is (TDSIGST) or (TDSCGST & TDSSGST).", location));

			return errors;

		}

		if ((bigst.compareTo(BigDecimal.ZERO) > 0) && (bcgst.compareTo(BigDecimal.ZERO) > 0)) {

			String constant = GSTConstants.TDS_IGST + "" + GSTConstants.TDS_CGST + "" + GSTConstants.TDS_SGST;
			errorLocations.add(constant);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2030",
					"Accepted Tax Amount combination is (TDSIGST) or (TDSCGST & TDSSGST).", location));

			return errors;

		}

		if ((bigst.compareTo(BigDecimal.ZERO) > 0) && (bsgst.compareTo(BigDecimal.ZERO) > 0)) {

			String constant = GSTConstants.TDS_IGST + "" + GSTConstants.TDS_CGST + "" + GSTConstants.TDS_SGST;
			errorLocations.add(constant);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2030",
					"Accepted Tax Amount combination is (TDSIGST) or (TDSCGST & TDSSGST).", location));

			return errors;

		}
		return errors;
	}

}
