package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import com.google.common.base.Strings;

/**
 * @author Mahesh.Golla
 * 
 *         BR_OUTWARD_35
 */

@Component("Gstr1AExportWithoutTaxAmountValidator")
public class Gstr1AExportWithoutTaxAmountValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> SUPPLYTYPE = ImmutableList
			.of(GSTConstants.EXPWT, GSTConstants.SEZWOP);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		if (Strings.isNullOrEmpty(document.getSupplyType()))
			return errors;
		if (!SUPPLYTYPE
				.contains(trimAndConvToUpperCase(document.getSupplyType())))
			return errors;

		IntStream.range(0, items.size()).forEach(idx -> {

			Gstr1AOutwardTransDocLineItem item = items.get(idx);
			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal cessAmountSpecific = item.getCessAmountSpecific();
			BigDecimal cessAmountAdvalorem = item.getCessAmountAdvalorem();
			if (cgstAmount == null) {
				cgstAmount = BigDecimal.ZERO;
			}
			if (sgstAmount == null) {
				sgstAmount = BigDecimal.ZERO;
			}
			if (igstAmount == null) {
				igstAmount = BigDecimal.ZERO;
			}
			if (cessAmountSpecific == null) {
				cessAmountSpecific = BigDecimal.ZERO;
			}
			if (cessAmountAdvalorem == null) {
				cessAmountAdvalorem = BigDecimal.ZERO;
			}
			BigDecimal cessAmount = cessAmountSpecific.add(cessAmountAdvalorem);
			BigDecimal totalTaxAmount = igstAmount.add(sgstAmount)
					.add(cgstAmount).add(cessAmount);

			if (sgstAmount.compareTo(BigDecimal.ZERO) != 0
					|| cgstAmount.compareTo(BigDecimal.ZERO) != 0
					|| igstAmount.compareTo(BigDecimal.ZERO) != 0
					|| cessAmount.compareTo(BigDecimal.ZERO) != 0
					|| totalTaxAmount.compareTo(BigDecimal.ZERO) != 0) {
				Set<String> errorLocations = new HashSet<>();
				if (sgstAmount.compareTo(BigDecimal.ZERO) != 0) {
					errorLocations.add(GSTConstants.SGST_AMOUNT);
				}
				if (cgstAmount.compareTo(BigDecimal.ZERO) != 0) {
					errorLocations.add(GSTConstants.CGST_AMOUNT);
				}
				if (igstAmount.compareTo(BigDecimal.ZERO) != 0) {
					errorLocations.add(GSTConstants.IGST_AMOUNT);
				}
				if (cessAmount.compareTo(BigDecimal.ZERO) != 0) {
					errorLocations.add(GSTConstants.CESS_AMT_ADV);
					errorLocations.add(GSTConstants.CESS_AMT_SPECIFIC);
				}
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0505",
						"Tax cannot be applied", location));
			}

		});

		return errors;
	}

}
