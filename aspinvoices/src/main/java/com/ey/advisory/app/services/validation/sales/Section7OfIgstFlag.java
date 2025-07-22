package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("Section7OfIgstFlag")
public class Section7OfIgstFlag
		implements DocRulesValidator<OutwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT,
			SGST_AMOUNT,GSTConstants.CGST_RATE,GSTConstants.SGST_RATE };

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		if (document.getSection7OfIgstFlag() == null
				|| document.getSection7OfIgstFlag().isEmpty())
			return errors;

if(GSTConstants.Y.equalsIgnoreCase(document.getSection7OfIgstFlag())){
			IntStream.range(0, items.size()).forEach(idx -> {

				OutwardTransDocLineItem item = items.get(idx);
				BigDecimal cgstAmount = item.getCgstAmount();
				BigDecimal sgstAmount = item.getSgstAmount();
				BigDecimal cgstrate = item.getCgstRate();
				BigDecimal sgstRate = item.getSgstRate();
				if(cgstAmount==null){
					cgstAmount=BigDecimal.ZERO;
				}
				if(sgstAmount==null){
					sgstAmount=BigDecimal.ZERO;
				}
				BigDecimal csgstAmount = cgstAmount.add(sgstAmount);
				
				if(cgstrate==null){
					cgstrate=BigDecimal.ZERO;
				}
				if(sgstRate==null){
					sgstRate=BigDecimal.ZERO;
				}
				BigDecimal csgstrate = cgstrate.add(sgstRate);
				if (csgstAmount.compareTo(BigDecimal.ZERO) != 0 
						|| csgstrate.compareTo(BigDecimal.ZERO) != 0) {
					TransDocProcessingResultLoc location 
					                = new TransDocProcessingResultLoc(
							idx, FIELD_LOCATIONS);
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0510",
							"CGST / SGST cannot be applied as where "
									+ "Section 7 of IGST Flag is given as 'Y'.",
							location));
				}

			});
		}
		return errors;
	}

}
