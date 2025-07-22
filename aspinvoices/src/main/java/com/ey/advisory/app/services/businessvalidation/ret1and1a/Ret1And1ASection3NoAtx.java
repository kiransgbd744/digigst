package com.ey.advisory.app.services.businessvalidation.ret1and1a;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
/**
 * 
 * @author Mahesh.Golla
 *
 */
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class Ret1And1ASection3NoAtx
		implements BusinessRuleValidator<Ret1And1AExcelEntity> {

	private static final List<String> RETURN_TABLES = ImmutableList.of("3D1",
			"3D2", "3D3", "3D4");

	@Override
	public List<ProcessingResult> validate(Ret1And1AExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		BigDecimal igstAmt = BigDecimal.ZERO;
		BigDecimal cgstAmt = BigDecimal.ZERO;
		BigDecimal sgstAmt = BigDecimal.ZERO;
		BigDecimal cessAmt = BigDecimal.ZERO;
		if (document.getIgstAmt() != null
				&& !document.getIgstAmt().trim().isEmpty()) {
			igstAmt = NumberFomatUtil.getBigDecimal(document.getIgstAmt());
		}
		if (document.getCgstAmt() != null
				&& !document.getCgstAmt().trim().isEmpty()) {
			cgstAmt = NumberFomatUtil.getBigDecimal(document.getCgstAmt());
		}
		if (document.getSgstAmt() != null
				&& !document.getSgstAmt().trim().isEmpty()) {
			sgstAmt = NumberFomatUtil.getBigDecimal(document.getSgstAmt());
		}
		if (document.getCessAmt() != null
				&& !document.getCessAmt().trim().isEmpty()) {
			cessAmt = NumberFomatUtil.getBigDecimal(document.getCessAmt());
		}
		BigDecimal sumOfTotalAmt = igstAmt.add(cgstAmt).add(sgstAmt)
				.add(cessAmt);

		if (document.getReturnTable() != null
				&& !document.getReturnTable().isEmpty()) {
			String returnTable = document.getReturnTable().trim().toUpperCase();
			if (RETURN_TABLES.contains(returnTable)) {
				if (sumOfTotalAmt.compareTo(BigDecimal.ZERO) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.IGST_AMOUNT);
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					errorLocations.add(GSTConstants.CESS_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1889",
							"IGST/CGST/SGST/Cess cannot be applied.",
							location));
					return errors;
				}
			}
		}
		return errors;
	}
}