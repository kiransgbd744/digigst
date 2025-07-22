package com.ey.advisory.app.services.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Itc04InterStateValidator
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		Set<String> errorLocations = new HashSet<>();

		String supplierGstin = (document.getSupplierGstin() != null)
				? document.getSupplierGstin().trim().substring(0, 2) : null;
		String pos = getStateCode(document.getJobWorkerGstin(),
				document.getJobWorkerStateCode());
		BigDecimal igstRate = document.getLineItems().get(0)
				.getIgstAmount() != null
						? document.getLineItems().get(0).getIgstAmount()
						: BigDecimal.ZERO;
		BigDecimal cgstRate = document.getLineItems().get(0)
				.getCgstRate() != null
						? document.getLineItems().get(0).getCgstRate()
						: BigDecimal.ZERO;
		BigDecimal sgstRate = document.getLineItems().get(0)
				.getSgstRate() != null
						? document.getLineItems().get(0).getSgstRate()
						: BigDecimal.ZERO;
		BigDecimal addRate = cgstRate.add(sgstRate);

		// backup
		/*
		 * if (supplierGstin != null && pos != null) { if
		 * (!supplierGstin.equalsIgnoreCase(pos)) { if
		 * (addRate.compareTo(BigDecimal.ZERO) > 0) {
		 * errorLocations.add(GSTConstants.CGST_RATE);
		 * errorLocations.add(GSTConstants.SGST_RATE);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER5864",
		 * "CGST / SGST  Rate cannot be applied in case of Inter state supply.",
		 * location)); return errors; } } }
		 */
		//

		if (supplierGstin != null && pos != null
				&& !supplierGstin.equalsIgnoreCase(pos)
				&& "S".equalsIgnoreCase(document.getJobWorkerType())) {
			if (addRate.compareTo(BigDecimal.ZERO) > 0) {
				errorLocations.add(GSTConstants.CGST_RATE);
				errorLocations.add(GSTConstants.SGST_RATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5864",
						"CGST / SGST  Rate cannot be applied in case of Inter state supply.",
						location));
				return errors;
			}

		}
		return errors;
	}

	private String getStateCode(String jobWorkerGstin,
			String jobWorkerStateCode) {
		if (jobWorkerGstin != null && !jobWorkerGstin.isEmpty()
				&& jobWorkerStateCode != null
				&& !jobWorkerStateCode.isEmpty()) {
			return jobWorkerGstin.substring(0, 2);
		}
		if (jobWorkerStateCode != null && !jobWorkerStateCode.isEmpty()) {
			return jobWorkerStateCode;
		}
		if (jobWorkerGstin != null && !jobWorkerGstin.isEmpty()) {
			return jobWorkerGstin.substring(0, 2);
		}
		return jobWorkerGstin;
	}

}
