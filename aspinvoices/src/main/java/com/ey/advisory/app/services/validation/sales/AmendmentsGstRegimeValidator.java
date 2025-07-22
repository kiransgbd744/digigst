/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RCR;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RDLC;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RDR;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RNV;
import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.DOC_TYPE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
*//**
 * This Class responsible for validate the  RNV or RDR or RCR document
 *  types  cannot exist for the first tax period in GST regime and amendments 
 *  should not happen with in the same taxperiod 
 * @author Mahesh.Golla
 * 
 * BR_OUTWARD_43
 *
 *//*


@Component("AmendmentsGstRegimeValidator")
public class AmendmentsGstRegimeValidator implements 
                              DocRulesValidator<OutwardTransDocument> {
	
	private  LocalDate originalDocDate;
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	
	private LocalDate gstinRegDate;
	
	
*//**
	 * Here the logic tells if doc type RNV or RDR or RCR cannot exist within 
	 * same taxperiod
	 * Get the registration date from GSTIN info then it will check the 
	 * doc types if its RNV or RCR or RDR doc types it will check the tax period 
	 * tax period and reg Date both are same with doc types it will throw the 
	 * business error description
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 *//*
	

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		
		List<String> errorLocations = new ArrayList<>();
		
		String docType = document.getDocType();
		
		
		
		if(document.getOrigDocDate()!=null){
         originalDocDate = document.getOrigDocDate();
		}
        DateTimeFormatter formatter = 
				DateTimeFormatter.ofPattern("ddMMyyyy");
	    if(document.getTaxperiod()!=null){
       String tax = "01" + document.getTaxperiod();
		// Calculate the last day of the month.
		LocalDate firstDayOfMonTax = LocalDate.parse(tax, formatter);
		LocalDate lastDayOfMonTax = 
				firstDayOfMonTax.with(TemporalAdjusters.lastDayOfMonth());
		
	    	    	    
		gstinInfoRepository = StaticContextHolder.
				getBean("GSTNDetailRepository",GSTNDetailRepository.class);

		List<GSTNDetailEntity> gstin = gstinInfoRepository
				              .findByGstin(document.getSgstin());
				           
	
		if(gstin!=null && gstin.size() > 0){
			if(gstin.get(0).getRegDate()!=null){
				gstinRegDate =	gstin.get(0).getRegDate();
			
		
			if(document.getOrigDocDate()!=null){
				
			if((docType.compareToIgnoreCase(RNV)==0) ||
						(docType.compareToIgnoreCase(RDR)==0) ||
						(docType.compareToIgnoreCase(RDLC)==0) ||
						(docType.compareToIgnoreCase(RCR)==0)){
				
				
		if( (lastDayOfMonTax.getMonth() == gstinRegDate.getMonth() 
				  && lastDayOfMonTax.getYear() == gstinRegDate.getYear()) 
				  ||(originalDocDate.getMonth() == lastDayOfMonTax.getMonth() 
				  && originalDocDate.getYear() == lastDayOfMonTax.getYear())) {
						errorLocations.add(DOC_TYPE);
					TransDocProcessingResultLoc location =
							new TransDocProcessingResultLoc(null, 
									errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER230",
							"RNV or RDR or RCR document types cannot exist "
							+ "for the first tax period in GST regime",
							location));
				
			}
			}
			}		
		
	    }}	
	    }
	return errors;
}
		}
					
					
				


*/