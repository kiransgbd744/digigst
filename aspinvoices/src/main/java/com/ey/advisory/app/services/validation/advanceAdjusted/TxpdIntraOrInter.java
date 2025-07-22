/**
 * 
 */
package com.ey.advisory.app.services.validation.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Mahesh.Golla
 *
 */
public class TxpdIntraOrInter
		implements B2csBusinessRuleValidator<Gstr1AsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		BigDecimal common = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		String igsts = (document.getIntegratedTaxAmount() != null)
				? document.getIntegratedTaxAmount().trim() : null;
		if (igsts != null && !igsts.isEmpty()) {
			igst = NumberFomatUtil.getBigDecimal(igsts);
		}
		BigDecimal sgst = BigDecimal.ZERO;
		String sgsts = (document.getStateUTTaxAmount() != null)
				? document.getStateUTTaxAmount().trim() : null;
		if (sgsts != null && !sgsts.isEmpty()) {
			sgst = NumberFomatUtil.getBigDecimal(sgsts);
		}
		BigDecimal cgst = BigDecimal.ZERO;
		String cgsts = (document.getCentralTaxAmount() != null)
				? document.getCentralTaxAmount().trim() : null;
		if (cgsts != null && !cgsts.isEmpty()) {
			cgst = NumberFomatUtil.getBigDecimal(cgsts);
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
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5511",
					" A document can either be intra-State or inter-State.",
					location));
			return errors;

		}

		return errors;

	}
}
