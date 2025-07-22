/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                           .APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                           .ORIGINAL_DOC_NO;
import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                         .*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
@Component("LuOrgDateNoValidation")
public class LuOrgDateNoValidation implements 
DocRulesValidator<OutwardTransDocument>{
	private static final List<String> ORGDOCNUM1_IMPORTS = ImmutableList
			.of(CR,DR);
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository DocRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		DocRepository = StaticContextHolder.getBean(
				"DocRepository", DocRepository.class);
		List<ProcessingResult> errorsInfo = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
		
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
			if(document.getCrDrPreGST()!=null 
					&& !document.getCrDrPreGST().isEmpty()){
				
				
					if(document.getSgstin()!=null 
							&& !document.getSgstin().isEmpty()){
						if(document.getOrigDocNo()!=null 
								&& !document.getOrigDocNo().isEmpty()){
							if(document.getOrigDocDate()!=null){
								if(document.getDocType()!=null 
										&& !document.getDocType().isEmpty()){
									
				if(GSTConstants.N.equalsIgnoreCase(document.getCrDrPreGST())){	
						
		if(ORGDOCNUM1_IMPORTS.contains(
				trimAndConvToUpperCase(document.getDocType()))){
			
			List<OutwardTransDocument> doc = DocRepository
					.getDuplicateKeyDocnoAndDocdate(
					document.getSgstin(),document.getOrigDocNo(),
					document.getOrigDocDate());
			if(doc==null || doc.size() <= 0){
				errorLocations.add(ORIGINAL_DOC_DATE);
				errorLocations.add(ORIGINAL_DOC_NO);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorsInfo.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN200",
						"Invalid Original Document Number "
							+ "or Invalid Original Document Date ", location)); 
			}
			if(doc!=null && doc.size() > 0){
				OutwardTransDocument outward=doc.get(0);
			
	Boolean n = LookUpUtil.checkIfBothDocsHaveSameCgstin(outward,document);
	Boolean m =LookUpUtil.checkIfBothDocsHaveSameInterIntra(outward,document);
	Boolean o =LookUpUtil.checkIfBothDocsHaveSameRcmFlag(outward,document);
			if((document.getDocDate().compareTo(document.getOrigDocDate()) < 0)
					                                          || m || n || o){
			
				errorLocations.add(ORIGINAL_DOC_DATE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
				errorsInfo.add(
						new ProcessingResult(APP_VALIDATION, "ER200",
								"Invalid Original Document Date or "
								+ "Original Document Date is missing",
								location));
			         }
			        }
			 
			      }
			}
						}	
			}
				
								
							
						}
					}
				}
			
		});
			 return errorsInfo;
		
		
	}
	

}
*/