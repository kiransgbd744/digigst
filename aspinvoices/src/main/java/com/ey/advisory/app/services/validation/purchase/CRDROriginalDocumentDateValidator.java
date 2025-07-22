package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.ORIGINAL_DOC_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class CRDROriginalDocumentDateValidator 
                  implements DocRulesValidator<InwardTransDocument> 
{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
	ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		// Get the document date from the document and convert it to a
		// LocalDate object.
		if(document.getCrDrPreGst()!=null && !document.getCrDrPreGst().isEmpty()){
			if("Y".equalsIgnoreCase(document.getCrDrPreGst())){
		if(document.getOrigDocDate()!=null){
		
			LocalDate OrigDocDate=document.getOrigDocDate();
		
		DateTimeFormatter formatter = 
					DateTimeFormatter.ofPattern("ddMMyyyy");
			
	LocalDate gstStartDate = LocalDate.parse("01072017", formatter);

		List<String> errorLocations = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {

			if (OrigDocDate.compareTo(gstStartDate) > 0
				) {
				errorLocations.add(ORIGINAL_DOC_DATE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER058",
						"Invalid Original Document Date "
						+ "or Original Document Date is missing ",
						location));
			}

		});
		}
		}
		}
		return errors;
	}

	}




