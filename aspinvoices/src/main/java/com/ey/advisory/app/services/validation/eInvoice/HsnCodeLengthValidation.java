package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.service.GstnApi;
import com.google.common.base.Strings;


public class HsnCodeLengthValidation 
          implements DocRulesValidator<OutwardTransDocument> {
	@Autowired
	@Qualifier("GstnApi")
	private GstnApi gstnApi;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			String hsnCode=item.getHsnSac();
			gstnApi = StaticContextHolder.getBean("GstnApi",
					GstnApi.class);
			if(!Strings.isNullOrEmpty(hsnCode) 
					&& gstnApi.isRateIncludedInHsn(document.getTaxperiod()) 
					&& hsnCode.length() < 6){
				Set<String> errorLocations = new HashSet<>();
				errorLocations
						.add(GSTConstants.HSNORSAC);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN0514",
						"Minimum 6 digits HSN code should be provided",
						location));
			}
			
		});
		return errors;

	}


}
