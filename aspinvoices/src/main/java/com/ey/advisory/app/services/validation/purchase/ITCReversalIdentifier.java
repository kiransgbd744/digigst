package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.ITCReversalIdentifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;

public class ITCReversalIdentifier implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> Itcflag = ImmutableList
			.of("T1","T2","T3","T4");
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
	if(item.getItcReversalIdentifier()!=null && !item.getItcReversalIdentifier().isEmpty()){
		if(!Itcflag.contains(item.getItcReversalIdentifier())){
			errorLocations.add(ITCReversalIdentifier);
			TransDocProcessingResultLoc location = 
					                  new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER110",
					"Invalid ITC Reversal Identifier",
					location));
		}
	}
		
		});
		return errors;
	}

}
