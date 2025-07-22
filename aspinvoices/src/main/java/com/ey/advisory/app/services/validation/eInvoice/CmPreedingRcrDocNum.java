package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("CmPreedingRcrDocNum")
public class CmPreedingRcrDocNum
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLYTYPE1 = ImmutableList.of(

			GSTConstants.EXPT, GSTConstants.EXPWT);
	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.RNV, GSTConstants.RDR, GSTConstants.RCR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (document.getSupplyType() != null
					&& !document.getSupplyType().isEmpty()) {

				if (SUPPLYTYPE1.contains(
						trimAndConvToUpperCase(document.getSupplyType()))
						&& DOCTYPE.contains(trimAndConvToUpperCase(
								document.getDocType()))) {
					IntStream.range(0, items.size()).forEach(idx -> {
						OutwardTransDocLineItem item = items.get(idx);
						if (item.getPreceedingInvoiceNumber() == null
								|| item.getPreceedingInvoiceNumber().isEmpty()) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.PRECEEDING_INV_NUMBER);
							TransDocProcessingResultLoc location 
							   = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0039",
									"Preceeding Invoice Number cannot be left balnk.",
									location));
						}
					});
				}

			}
		}

		return errors;
	}
}
