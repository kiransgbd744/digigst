/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RCR;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RDR;
import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.ORIGINAL_DOC_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
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
 *         BR_OUTWARD_14
 *
 *         This class is responsible for performing Business rule Original
 *         Credit Note / Original Debit Note should have been reported as part
 *         of GSTR-1 of earlier Tax Period ((Financial Year) + 6 Months or
 *         Annual Return Filing Date, whichever is earlier)
 * 
 *//*
@Component("RcrAndRdrAmendmentValidator")
public class RcrAndRdrAmendmentValidator
		implements DocRulesValidator<OutwardTransDocument> {
	*//**
	 * Amendment of any Original Document (Original Credit Note / Original Debit
	 * Note) cannot be done after March 31 of the next Financial Year or Annual
	 * Return Filing whichever is earlier. Applicable in case of Document Type
	 * is RCR, RDR.
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
			if(document.getOrigDocDate()!=null){
		String doctype = document.getDocType();
		if (RCR.equalsIgnoreCase(doctype) || RDR.equalsIgnoreCase(doctype)) {
			LocalDate originalDocDate = document.getOrigDocDate();
			int monthValue=originalDocDate.getMonthValue();
			String key;
			if(monthValue<=3){
			 key = originalDocDate.minusYears(1)
					 .getYear()+"-"+originalDocDate.getYear();
		}else{
			 key = originalDocDate.getYear()+"-"+originalDocDate
					                        .plusYears(1).getYear();
				 //nextFinanYear = originalDocDate.plusYears(2).getYear();
			}
			*//**
			 * 
			 * March 31 is the end of financial year
			 *//*
			//GET the nextfinanYearEnd date from the 
			//eyconfig table using nextFinanYear value
			//LocalDate nextfinanYearEnd = LocalDate.of(key, 3, 31);
			LocalDate nextfinanYearEnd = null ;
			eyConfigRepository = StaticContextHolder
					                           .getBean("EYConfigRepository", 
					EYConfigRepository.class);
			String value=eyConfigRepository.findNextfinanYearEnd(key);
			if(value == null || value.isEmpty()) {
				int year = Integer.parseInt(key.substring(5));
				nextfinanYearEnd = LocalDate.of(year, 9, 30);
			}
			if(value!=null && !value.isEmpty()) {
				//String value1 = value.replaceAll("-", "");
			 nextfinanYearEnd = LocalDate.parse(
					value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
			
			if (LocalDate.now().compareTo(nextfinanYearEnd) > 0) {
				errorLocations.add(ORIGINAL_DOC_DATE);
				TransDocProcessingResultLoc location 
				                       = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				if (RCR.equals(doctype)) {
					errors.add(new ProcessingResult(APP_VALIDATION, "ER214",
							"Original Credit Note cannot be amended now",
							location));
				}
					if (RDR.equals(doctype)) {
					errors.add(new ProcessingResult(APP_VALIDATION, "ER215",
							"Original Debit Note cannot be amended now",
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