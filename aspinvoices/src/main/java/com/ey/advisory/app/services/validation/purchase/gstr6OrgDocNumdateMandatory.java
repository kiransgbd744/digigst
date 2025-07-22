package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class gstr6OrgDocNumdateMandatory implements DocRulesValidator<InwardTransDocument> {
	
	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.CR,GSTConstants.DR,
					GSTConstants.RNV,GSTConstants.RDR, GSTConstants.RCR);

	
	private static final List<String> DOCTYPE_2_VERSION = ImmutableList
			.of(GSTConstants.RNV,GSTConstants.RDR, GSTConstants.RCR);
	
	@Autowired
	@Qualifier("GstnApi")
	private GstnApi gstnApi;
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		List<InwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			
				if (document.getSupplyType() != null
						&& !document.getSupplyType().isEmpty()) {
					List<String> docType=new ArrayList<>();
					gstnApi = StaticContextHolder.getBean("GstnApi",
							GstnApi.class);
					if (gstnApi.isDelinkingEligible(APIConstants.GSTR6.toUpperCase())) {
						docType.addAll(DOCTYPE_2_VERSION);
					}else{
						docType.addAll(DOCTYPE);
					}
					
					if (docType.contains(
							trimAndConvToUpperCase(document.getDocType()))
							) {
						IntStream.range(0, items.size()).forEach(idx -> {
							InwardTransDocLineItem item = items.get(idx);
							if (item.getOrigDocDate()== null) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations
										.add(GSTConstants.ORIGINAL_DOC_DATE);
								TransDocProcessingResultLoc location 
								= new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1160",
										"Original Document Date "
												+ "cannot be left balnk.",
										location));
							}
							if (item.getOrigDocNo()==null 
									|| item.getOrigDocNo().isEmpty()) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations
										.add(GSTConstants.ORIGINAL_DOC_NO);
								TransDocProcessingResultLoc location 
								        = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1159",
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
