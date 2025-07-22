package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class DocumentDateValidator1 
                            implements DocRulesValidator<OutwardTransDocument> 
{

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
	ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		// Get the document date from the document and convert it to a
		// LocalDate object.
		
		if(document.getDocDate()!=null){
			if(document.getTaxperiod()!=null 
					                   && !document.getTaxperiod().isEmpty()){
				
		
		String tax="01"+document.getTaxperiod();
			LocalDate DocDate=document.getDocDate();
		
		DateTimeFormatter formatter = 
					DateTimeFormatter.ofPattern("ddMMyyyy");
		
		// Calculate the last day of the month.
		LocalDate firstDayOfMon = LocalDate.parse(tax, formatter);
		LocalDate lastDayOfMon = firstDayOfMon
				.with(TemporalAdjusters.lastDayOfMonth());
	LocalDate gstStartDate = LocalDate.parse("01072017", formatter);

		List<String> errorLocations = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {

			if (DocDate.compareTo(gstStartDate) < 0
				|| DocDate.compareTo(lastDayOfMon) > 0) {
				errorLocations.add(DOC_DATE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER056",
						"Invalid Document Date",
						location));
			}

		});
			}
		}
		return errors;
	}

	}




