package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class OriginalSupplierGstin implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = ImmutableList
			.of("RNV","RCR","RDR");
	private static final List<String> Doc_Types = ImmutableList
			.of("SLF","RSLF");
	private static final List<String> Supply_types = ImmutableList
			.of("IMPG","IMPS");
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			
				if (ORGDOCNUM_REQUIRING_IMPORTS
						.contains(document.getDocType())) {
					if((!(document.getOrigSgstin() != null )) 
							|| document.getOrigSgstin().isEmpty()){
							
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGSTIN);
						TransDocProcessingResultLoc location = new 
								TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER099",
								"Invalid Original Supplier GSTIN",
								location));
					}

				
			}
				else if(Doc_Types.contains(document.getDocType())){
					if(document.getOrigSgstin()!=null 
							|| !document.getOrigSgstin().isEmpty()){
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGSTIN);
						TransDocProcessingResultLoc location = new 
								TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER099",
								"Invalid Original Supplier GSTIN",
								location));
							}
						}
		}
		else if(document.getSupplyType()!=null 
				&& !document.getSupplyType().isEmpty()){
			if(Supply_types.contains(document.getSupplyType())){
				if(document.getOrigSgstin()!=null 
						|| !document.getOrigSgstin().isEmpty()){
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new 
							TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER052",
							"Invalid Supplier GSTIN",
							location));	
					
				}
			}
					}
		return errors;
	}
	}


