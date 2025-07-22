/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                              .APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                           .ORIGINAL_DOC_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static com.ey.advisory.common.GenUtil.trimAndConvToLowerCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
import com.google.common.collect.ImmutableList;
*//**
 * 
 * @author Hemasundar.J
 *
 * 		   BR_OUTWARD_10
 *
 *         This class is responsible for performing Business rule Original
 *         Invoice should have been reported as part of GSTR-1 of earlier Tax
 *         Period ((Financial Year) + 6 Months or Annual Return Filing Date,
 *         whichever is earlier)
 * 
 *//*
@Component("rnvAmendmentValidator")
public class RnvAmendmentValidator
		implements DocRulesValidator<OutwardTransDocument> {

	*//**
	 * Amendment of any Original Document (Invoice) cannot be done after 
	 * March 31 of the next Financial Year or Annual Return Filing whichever 
	 * is earlier. Applicable in case of Document Type is RNV.
	 *//*
	private static final List<String> DOC_TYPES_IMPORTS 
	= ImmutableList
			.of(GSTConstants.RNV,GSTConstants.CR, GSTConstants.DR);
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
				if (DOC_TYPES_IMPORTS.contains(
						trimAndConvToLowerCase(document.getDocType()))) {
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
			
			*//**
			 * If todays date is greater than 30th sep of next financial year
			 * then Original doument cannot be amended.
			 *//*
			if (LocalDate.now().compareTo(nextfinanYearEnd) > 0) {
				errorLocations.add(ORIGINAL_DOC_DATE);
				TransDocProcessingResultLoc location 
				            = new TransDocProcessingResultLoc(
						             errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER209",
						"Original doument cannot be amended after "
						+ "30th Septmeber of next Financial Year or "
						+ "after filing of Annual Return",
						location));

			}
			
		}
		}
	}
		return errors;
	}

}
*/