package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_RATE;
import static com.ey.advisory.common.GSTConstants.SGST_RATE;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
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
@Component("Gstr1ACgstAndSgstSameValidation")
public class Gstr1ACgstAndSgstSameValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> DOC_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.CR, GSTConstants.DR, GSTConstants.RCR,
			GSTConstants.RDR);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();

		if (document.getDocType() != null && !document.getDocType().isEmpty()) {

			if (DOC_TYPE_IMPORTS
					.contains(trimAndConvToUpperCase(document.getDocType()))
					&& GSTConstants.Y
							.equalsIgnoreCase(document.getCrDrPreGst()))

				return errors;

			/**
			 * Here we are getting rates from Cilent and comparing those rates
			 * like cgst and sgst rates
			 */
			IntStream.range(0, items.size()).forEach(idx -> {
				Gstr1AOutwardTransDocLineItem item = items.get(idx);
				BigDecimal cgstRate = item.getCgstRate();
				BigDecimal sgstRate = item.getSgstRate();
				if (cgstRate == null) {
					cgstRate = BigDecimal.ZERO;
				}
				if (sgstRate == null) {
					sgstRate = BigDecimal.ZERO;
				}
				BigDecimal taxAmount = (cgstRate.compareTo(sgstRate) > 0)
						? cgstRate.subtract(sgstRate).abs()
						: sgstRate.subtract(cgstRate).abs();

				if (taxAmount.compareTo(BigDecimal.ZERO) > 0) {
					errorLocations.add(CGST_RATE);
					errorLocations.add(SGST_RATE);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0512",
							"CGST and SGST Rates cannot be different",
							location));
				}
			});
		}

		return errors;
	}
}
