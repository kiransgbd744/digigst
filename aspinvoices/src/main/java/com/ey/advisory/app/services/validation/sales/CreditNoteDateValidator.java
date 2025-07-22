/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client
                                         .DocAndSupplyTypeConstants.DOC_TYPE;
import static com.ey.advisory.app.data.entities.client.GSTConstants.*;
import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                             .APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
*//**
 * 
 * @author Hemasundar.J
 *
 *         BR_OUTWARD_57
 *
 *         This class is responsible for performing Business rule Credit Note
 *         recording check
 *//*
@Component("CreditNoteDateValidator")
public class CreditNoteDateValidator
		implements DocRulesValidator<OutwardTransDocument> {

	*//**
	 * The Credit Note Date reported by the client in the transaction can not be
	 * a date after the date of Financial Year + 6 Months or Annual Return
	 * Filing Date, whichever is earlier for a particular GSTIN
	 *//*
	@Qualifier("EYConfigRepository")
	@Autowired
	private EYConfigRepository eyConfigRepository;
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
			if(document.getDocDate()!=null){
				if(document.getOrigDocDate()!=null){
					
			
		String doctype = document.getDocType();
		LocalDate creditNoteDate = document.getDocDate();

		if (CR.equals(doctype)) {
			LocalDate originalDocDate = document.getOrigDocDate();
			int monthValue = originalDocDate.getMonthValue();
			
			String key;
			if(monthValue<=3){
			 key = originalDocDate.minusYears(1)
					                .getYear()+"-"+originalDocDate.getYear();
		}else{
			 key = originalDocDate.getYear()+"-"+originalDocDate
					                                  .plusYears(1).getYear();
			}
			*//**
			 * March 31 is the end of financial year + 6 months
			 *//*
			LocalDate nextFinancial = null ;
		eyConfigRepository = StaticContextHolder.getBean("EYConfigRepository", 
					EYConfigRepository.class);
			String value=eyConfigRepository.findNextfinanYearEnd(key);
			if(value == null || value.isEmpty()) {
				int year = Integer.parseInt(key.substring(5));
				nextFinancial = LocalDate.of(year, 9, 30);
			}
		
			if(value!=null && !value.isEmpty()) {
				//String value1 = value.replaceAll("-", "");
				nextFinancial = LocalDate.parse(
					value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
			*//**
			 * If next financial year is less than the credit note date then
			 * throw an error.
			 *//*
			if (nextFinancial.compareTo(creditNoteDate) < 0) {
				errorLocations.add(DOC_TYPE);
				errorLocations.add(DOC_DATE);
				TransDocProcessingResultLoc location = new 
						TransDocProcessingResultLoc(errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER239",
						"Credit Note date for an Invoice reported in a "
						+ "particular FY cannot be a date after 30th Sept of "
								+ "next FY or Annual return filing date",
						location));

			}

		}
				}
			}
		}
		return errors;
	}

}
*/