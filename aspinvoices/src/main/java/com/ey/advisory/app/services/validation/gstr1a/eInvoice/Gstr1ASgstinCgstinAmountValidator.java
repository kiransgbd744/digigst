package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
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

/*
 * BR_OUTWARD_37
 */
@Component("Gstr1ASgstinCgstinAmountValidator")
public class Gstr1ASgstinCgstinAmountValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { GSTConstants.IGST_AMOUNT,
			GSTConstants.CESS_AMT_ADV, GSTConstants.CESS_AMT_ADVALOREM };

	private static final String[] FIELD_LOCATIONS1 = { GSTConstants.CGST_AMOUNT,
			GSTConstants.SGST_AMOUNT };

	private static final List<String> SUPPLY_TYPES_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.SEZWP, GSTConstants.EXPT);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal cessAmountSpecific = item.getCessAmountSpecific();
			BigDecimal cessAmountAdvalorem = item.getCessAmountAdvalorem();

			if (cessAmountSpecific == null) {
				cessAmountSpecific = BigDecimal.ZERO;
			}
			if (cessAmountAdvalorem == null) {
				cessAmountAdvalorem = BigDecimal.ZERO;
			}
			if (cgstAmount == null) {
				cgstAmount = BigDecimal.ZERO;
			}
			if (sgstAmount == null) {
				sgstAmount = BigDecimal.ZERO;
			}
			if (igstAmount == null) {
				igstAmount = BigDecimal.ZERO;
			}
			BigDecimal cessAmount = cessAmountSpecific.add(cessAmountAdvalorem);
			if (cessAmount == null) {
				cessAmount = BigDecimal.ZERO;
			}

			if (document.getSupplyType() != null
					&& !document.getSupplyType().isEmpty()) {
				if (SUPPLY_TYPES_REQUIRING_IMPORTS.contains(
						trimAndConvToUpperCase(document.getSupplyType()))) {
					if (igstAmount.add(cessAmount)
							.compareTo(BigDecimal.ZERO) <= 0

					) {
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, FIELD_LOCATIONS);
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER0504",
								"In case Supply type is SEZWP/EXPT, "
										+ "then either " + "IGST or Cess "
										+ "should be applied.",
								location));
					} else if (cgstAmount.compareTo(BigDecimal.ZERO) != 0
							|| sgstAmount.compareTo(BigDecimal.ZERO) != 0) {
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, FIELD_LOCATIONS1);
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER15134",
								"In case Supply type is SEZWP/EXPT, "
										+ "then CGST / SGST "
										+ "cannot be applied.",
								location));
					}
				}
			}

		});

		return errors;
	}

}
