package com.ey.advisory.app.services.validation.eInvoice;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class CanceltionReasonValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> CACELATION = ImmutableList
			.of("1","2","3","4");

	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String cancellationReason = document.getCancellationReason();
		if(cancellationReason!=null && !cancellationReason.isEmpty()){
			if(!CACELATION.contains(cancellationReason)){
				
			}
		}
		return null;
	}

}
