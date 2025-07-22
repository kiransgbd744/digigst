package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

public class CMBillOfEntryDate  implements DocRulesValidator<InwardTransDocument> 
{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
	ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		// Get the document date from the document and convert it to a
		// LocalDate object.
		
		if(document.getBillOfEntryDate()!=null){
			if(document.getTaxperiod()!=null 
					                   && !document.getTaxperiod().isEmpty()){
				
		
		String tax="01"+document.getTaxperiod();
			//LocalDate BillEntryDate=document.getBillOfEntryDate().toLocalDate();
		
		DateTimeFormatter formatter = 
					DateTimeFormatter.ofPattern("ddMMyyyy");
		
		// Calculate the last day of the month.
		LocalDate firstDayOfMon = LocalDate.parse(tax, formatter);
		LocalDate lastDayOfMon = firstDayOfMon
				.with(TemporalAdjusters.lastDayOfMonth());
	LocalDate gstStartDate = LocalDate.parse("01072017", formatter);

		List<String> errorLocations = new ArrayList<>();

		IntStream.range(0, items.size()).forEach(idx -> {
/*
			if (BillEntryDate.compareTo(gstStartDate) < 0
				|| BillEntryDate.compareTo(lastDayOfMon) > 0) {
				errorLocations.add(GSTConstants.BillOfEntryDate);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER101",
						"Invalid Bill of Entry Date",
						location));
			}

*/		});
			}
		}
		return errors;
	}

}
