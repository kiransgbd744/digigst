package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

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
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("CMgstr1OrgDocdateValidator")
public class CMgstr1OrgDocdateValidator 
                implements DocRulesValidator<OutwardTransDocument> {
	
	
	private static final List<String> DOCTYPE_2_VERSION = ImmutableList
			.of(GSTConstants.RNV,GSTConstants.RDR, GSTConstants.RCR);
	
	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.CR,GSTConstants.DR,
					GSTConstants.RNV,GSTConstants.RDR, GSTConstants.RCR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			
				if (document.getSupplyType() != null
						&& !document.getSupplyType().isEmpty()) {
					List<String> docType=new ArrayList<>();
					
					//if (gstnApi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase())) {
					if(CommonContext.getDelinkingFlagContext()){
						docType.addAll(DOCTYPE_2_VERSION);
					}else{
						docType.addAll(DOCTYPE);
					}
					
					
					if (docType.contains(
							trimAndConvToUpperCase(document.getDocType()))
							) {
						IntStream.range(0, items.size()).forEach(idx -> {
							OutwardTransDocLineItem item = items.get(idx);
							if (item.getPreceedingInvoiceDate() == null) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations
										.add(GSTConstants.PRECEEDING_INV_DATE);
								TransDocProcessingResultLoc location 
								= new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0042",
										"Original Document Date "
												+ "cannot be left balnk.",
										location));
							}
							if (item.getPreceedingInvoiceNumber()==null 
									|| item.getPreceedingInvoiceNumber().isEmpty()) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations
										.add(GSTConstants.PRECEEDING_INV_NUMBER);
								TransDocProcessingResultLoc location 
								        = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0039",
										"Original Document Number "
												+ "cannot be left balnk.",
										location));
							}
						});
					}

				}
			}

		

		return errors;
	}
}
