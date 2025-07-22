package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class CgstinAndSgstinSameValidation 
             implements DocRulesValidator<OutwardTransDocument>{


	private static final String[] FIELD_LOCATIONS = { GSTConstants.SGSTIN,
			GSTConstants.RecipientGSTIN };

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if(GSTConstants.SLF.equalsIgnoreCase(document.getDocType())){
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			
				if (!document.getSgstin()
						.equalsIgnoreCase(document.getCgstin())) {

					TransDocProcessingResultLoc location 
					         = new TransDocProcessingResultLoc(
							null, FIELD_LOCATIONS);
					errors.add(new ProcessingResult(APP_VALIDATION, "ER11001",
							"For self invoice transactions, Supplier "
							+ "GSTIN should be same as Customer GSTIN",
							location));

					}
				}
			}
		
		return errors;
	}



}
