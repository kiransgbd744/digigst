package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CAN;
import static com.ey.advisory.common.GSTConstants.ORIGINAL_DOC_NO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.docs.DefaultOutwardTransDocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
/**
 * This class is Responsible for In case Supply Type is "CAN" the 
 * tax payer is expected to provide the entire original record 
 * detail with Document Type updated as "CAN", 
 * find the original details of such record based on
 *  key: "SupplierGSTIN + DocumentNumber +  DocumentDate + CustomerGSTIN " and 
 *  delete the record from ASP. The entire record need to be 
 *  same except the Supply type which shall be CAN.
 */

/**
 * BR_OUTWARD_64
 * BR_OUTWARD_50
 * BR_OUTWARD_51
 * BR_OUTWARD_52
 * @author Siva.Nandam
 *
 */
@Component("CanASpValidator")
public class CanASpValidator 
            implements DocRulesValidator<OutwardTransDocument> {
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository DocRepository;
	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	DefaultOutwardTransDocKeyGenerator DefaultOutwardTransDocKeyGenerator;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		
		List<ProcessingResult> errors = new ArrayList<>();
		
		List<OutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx->{
			OutwardTransDocLineItem item = items.get(idx);
			if(item.getSupplyType()!=null && !item.getSupplyType().isEmpty()){
				
			
			if(CAN.equalsIgnoreCase(item.getSupplyType())){
				
				DocRepository = 
						StaticContextHolder.getBean("DocRepository",
								DocRepository.class);	
				DefaultOutwardTransDocKeyGenerator = 
			   StaticContextHolder.getBean("DefaultOutwardTransDocKeyGenerator",
			    		DefaultOutwardTransDocKeyGenerator.class);	
					 
				String dockey = 
					DefaultOutwardTransDocKeyGenerator.generateKey(document);
				
			int n=DocRepository.
				findSubmitTag(dockey);
			if(n>0){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(ORIGINAL_DOC_NO);
				TransDocProcessingResultLoc location = new 
						       TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER236 ",
					"Cancelled invoice cannot be reported once the "
							+ "return for the same tax period is filed",
						location));
			}
			if(n<=0){
				Set<String> errorLocations = new HashSet<>();
				DocRepository.
						findDetailsOfDoc(dockey);
				
				errorLocations.add(ORIGINAL_DOC_NO);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ERRXXX ",
						"Original Document is not reported in "
						+ "the current tax period",
						location));
			
			}
			}				
		}
		});
		
		return errors;
	}

}
