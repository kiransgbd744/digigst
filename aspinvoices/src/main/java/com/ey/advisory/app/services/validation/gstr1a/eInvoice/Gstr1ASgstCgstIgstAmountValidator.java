package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

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

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("Gstr1ASgstCgstIgstAmountValidator")
public class Gstr1ASgstCgstIgstAmountValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT, SGST_AMOUNT,
			GSTConstants.IGST_AMOUNT };
	private static final String[] FIELD_RATE_LOCATIONS = {
			GSTConstants.CGST_RATE, GSTConstants.SGST_RATE,
			GSTConstants.IGST_RATE };

	private boolean isValidTax(Gstr1AOutwardTransDocLineItem firstType,
			Gstr1AOutwardTransDocLineItem curType) {

		BigDecimal cgstAmount = firstType.getCgstAmount();
		BigDecimal sgstAmount = firstType.getSgstAmount();
		BigDecimal igstAmount = firstType.getIgstAmount();

		BigDecimal curcgstAmount = curType.getCgstAmount();
		BigDecimal cursgstAmount = curType.getSgstAmount();
		BigDecimal curigstAmount = curType.getIgstAmount();

		if (curigstAmount == null) {
			curigstAmount = BigDecimal.ZERO;
		}
		if (curcgstAmount == null) {
			curcgstAmount = BigDecimal.ZERO;
		}
		if (cursgstAmount == null) {
			cursgstAmount = BigDecimal.ZERO;
		}

		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}

		if ((cgstAmount.compareTo(BigDecimal.ZERO) != 0
				&& igstAmount.compareTo(BigDecimal.ZERO) != 0)
				|| (sgstAmount.compareTo(BigDecimal.ZERO) != 0
						&& igstAmount.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}

		if ((cgstAmount.compareTo(BigDecimal.ZERO) != 0
				&& curigstAmount.compareTo(BigDecimal.ZERO) != 0)
				|| (sgstAmount.compareTo(BigDecimal.ZERO) != 0
						&& curigstAmount.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}
		if ((igstAmount.compareTo(BigDecimal.ZERO) != 0
				&& curcgstAmount.compareTo(BigDecimal.ZERO) != 0)
				|| (igstAmount.compareTo(BigDecimal.ZERO) != 0
						&& cursgstAmount.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}
		if ((curcgstAmount.compareTo(BigDecimal.ZERO) != 0
				&& curigstAmount.compareTo(BigDecimal.ZERO) != 0)
				|| (cursgstAmount.compareTo(BigDecimal.ZERO) != 0
						&& curigstAmount.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}

		return true;

	}

	private boolean isValidtaxRate(Gstr1AOutwardTransDocLineItem firstType,
			Gstr1AOutwardTransDocLineItem curType) {

		BigDecimal cgstRate = firstType.getCgstRate();
		BigDecimal sgstRate = firstType.getSgstRate();
		BigDecimal igstRate = firstType.getIgstRate();

		BigDecimal curcgstRate = curType.getCgstRate();
		BigDecimal cursgstRate = curType.getSgstRate();
		BigDecimal curigstRate = curType.getIgstRate();

		if (curcgstRate == null) {
			curcgstRate = BigDecimal.ZERO;
		}
		if (cursgstRate == null) {
			cursgstRate = BigDecimal.ZERO;
		}
		if (curigstRate == null) {
			curigstRate = BigDecimal.ZERO;
		}

		if (cgstRate == null) {
			cgstRate = BigDecimal.ZERO;
		}
		if (sgstRate == null) {
			sgstRate = BigDecimal.ZERO;
		}
		if (igstRate == null) {
			igstRate = BigDecimal.ZERO;
		}

		if ((cgstRate.compareTo(BigDecimal.ZERO) != 0
				&& igstRate.compareTo(BigDecimal.ZERO) != 0)
				|| (sgstRate.compareTo(BigDecimal.ZERO) != 0
						&& igstRate.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}
		if ((curcgstRate.compareTo(BigDecimal.ZERO) != 0
				&& curigstRate.compareTo(BigDecimal.ZERO) != 0)
				|| (cursgstRate.compareTo(BigDecimal.ZERO) != 0
						&& curigstRate.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}
		if ((cgstRate.compareTo(BigDecimal.ZERO) != 0
				&& curigstRate.compareTo(BigDecimal.ZERO) != 0)
				|| (sgstRate.compareTo(BigDecimal.ZERO) != 0
						&& curigstRate.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}
		if ((igstRate.compareTo(BigDecimal.ZERO) != 0
				&& curcgstRate.compareTo(BigDecimal.ZERO) != 0)
				|| (igstRate.compareTo(BigDecimal.ZERO) != 0
						&& cursgstRate.compareTo(BigDecimal.ZERO) != 0)) {
			return false;
		}
		return true;

	}

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {

			Gstr1AOutwardTransDocLineItem item = items.get(0);
			if (!isValidTax(item, items.get(idx))) {

				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, FIELD_LOCATIONS);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0502",
						"A document can either be intra-State or inter-State",
						location));
			} else if (!isValidtaxRate(item, items.get(idx))) {

				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, FIELD_RATE_LOCATIONS);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0502",
						"A document can either be intra-State or inter-State",
						location));
			}

		});

		return errors;
	}

}
