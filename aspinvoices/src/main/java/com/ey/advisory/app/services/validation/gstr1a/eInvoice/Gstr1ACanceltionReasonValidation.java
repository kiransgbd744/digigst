package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1ACanceltionReasonValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> CACELATION = ImmutableList.of("1", "2",
			"3", "4");

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		String cancellationReason = document.getCancellationReason();
		if (cancellationReason != null && !cancellationReason.isEmpty()) {
			if (!CACELATION.contains(cancellationReason)) {

			}
		}
		return null;
	}

}
