package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class Section7OfIgstFlagValidationRule
		implements DocRulesValidator<InwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT, SGST_AMOUNT,

	};

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();

		if (document.getSection7OfIgstFlag() == null
				|| document.getSection7OfIgstFlag().isEmpty())
			return errors;
		if(GSTConstants.Y.equalsIgnoreCase(document.getSection7OfIgstFlag())){

			IntStream.range(0, items.size()).forEach(idx -> {

				InwardTransDocLineItem item = items.get(idx);
				BigDecimal cgstAmount = item.getCgstAmount();
				BigDecimal sgstAmount = item.getSgstAmount();
				
				BigDecimal cgstRate = item.getCgstRate();
				BigDecimal sgstRate = item.getSgstRate();
				
				if(cgstAmount==null){
					cgstAmount=BigDecimal.ZERO;
				}
				if(sgstAmount==null){
					sgstAmount=BigDecimal.ZERO;
				}
				if(cgstRate==null){
					cgstRate=BigDecimal.ZERO;
				}
				if(sgstRate==null){
					sgstRate=BigDecimal.ZERO;
				}
				BigDecimal csgstAmount = cgstAmount.add(sgstAmount);
				BigDecimal csgstRate = cgstRate.add(sgstRate);
				if (csgstAmount.compareTo(BigDecimal.ZERO) != 0 
						|| csgstRate.compareTo(BigDecimal.ZERO) != 0) {
					TransDocProcessingResultLoc location 
					                     = new TransDocProcessingResultLoc(
							idx, FIELD_LOCATIONS);
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1303",
							"CGST / SGST cannot be applied as where "
									+ "Section 7 of IGST Flag is given as 'Y'.",
							location));
				}

			});
		}
		return errors;
	}

}
