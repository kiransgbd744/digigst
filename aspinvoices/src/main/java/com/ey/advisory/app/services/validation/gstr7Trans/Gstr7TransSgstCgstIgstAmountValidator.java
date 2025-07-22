package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocItemEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr7TransSgstCgstIgstAmountValidator")
public class Gstr7TransSgstCgstIgstAmountValidator
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT, SGST_AMOUNT,
			GSTConstants.IGST_AMOUNT };

	private boolean isValidTax(Gstr7TransDocItemEntity item,
			Gstr7TransDocItemEntity curType) {

		BigDecimal cgstAmount = item.getCgstAmt();
		BigDecimal sgstAmount = item.getSgstAmt();
		BigDecimal igstAmount = item.getIgstAmt();

		BigDecimal curcgstAmount = curType.getCgstAmt();
		BigDecimal cursgstAmount = curType.getSgstAmt();
		BigDecimal curigstAmount = curType.getIgstAmt();

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

		if (igstAmount.compareTo(BigDecimal.ZERO) == 0
				&& cgstAmount.compareTo(BigDecimal.ZERO) == 0
				&& sgstAmount.compareTo(BigDecimal.ZERO) == 0) {
			return false; // Invalid: All tax fields are zero or null
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

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr7TransDocItemEntity> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr7TransDocItemEntity item = items.get(0);
			if (!isValidTax(item, items.get(idx))) {
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, FIELD_LOCATIONS);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER63043",
						"Either IGST or CGST and SGST are mandatory.",
						location));
			}
		});

		return errors;
	}

}
