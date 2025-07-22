/*package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.app.data.entities.client.GSTConstants.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.
                                       InwardTransDocRepository;
import com.ey.advisory.app.services.docs.gstr2.
                                             DefaultInwardTransDocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
*//**
 * 
 * @author Mahesh.Golla
 *
 *//*

 * This validation class is responsible for checking the suppply type and 
 * supplier gstin if supply type is Cancelled and supplier gstin is available 
 * then we need to throw the error as Cancelled invoice cannot be reported once
 *  the return for the same tax period is filed
 
public class CancelledDocumentValidaton 
                             implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;
	
	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DefaultInwardTransDocKeyGenerator defaultInwardTransDocKeyGenerator;
	
	
	 * @return List<ProcessingResult>
	 * @Params document, context
	 * 
	 
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		
		
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx->{
			InwardTransDocLineItem item = items.get(idx);
			if(item.getSupplyType()!=null && !item.getSupplyType().isEmpty()){
				
			
			if(CAN.equalsIgnoreCase(item.getSupplyType())){
				document.setDeleted(true);
				inwardTransDocRepository = 
						StaticContextHolder.getBean("InwardTransDocRepository",
								InwardTransDocRepository.class);	
				defaultInwardTransDocKeyGenerator = 
			    StaticContextHolder.getBean("DefaultInwardTransDocKeyGenerator",
								DefaultInwardTransDocKeyGenerator.class);	
					 
				String dockey = 
						defaultInwardTransDocKeyGenerator.generateKey(document);
				
				inwardTransDocRepository.
						findDetailsOfDoc(dockey);
				
				errorLocations.add(ORIGINAL_DOC_NO);
				TransDocProcessingResultLoc location = 
		                  new TransDocProcessingResultLoc(
		                                      idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER236",
						"Cancelled invoice cannot be reported once the return "
						+ "for the same tax period is filed",
						location));	
			}				
		}
		});
		
		return errors;
	}

}
*/