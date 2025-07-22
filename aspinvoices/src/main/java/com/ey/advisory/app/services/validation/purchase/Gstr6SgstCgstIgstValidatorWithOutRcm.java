package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6SgstCgstIgstValidatorWithOutRcm
		implements DocRulesValidator<InwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = {
			GSTConstants.IGST_AMOUNT };
	private static final String[] FIELD_LOCATIONS1 = { GSTConstants.CGST_AMOUNT,
			GSTConstants.SGST_AMOUNT };

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();

		// If any one of sgstin or POS or Supply Type is not there
		// return immediately without any errors. We are assuming that
		// these conditions will be validated in other rules.
		if (!sgstinExists(document) || !posExists(document)
		/* || !rcmFlagExists(document) */ || !supplyTypeExists(document))
			return errors;

		// If it is not a tax document or a Deemed Exports document
		// then return the empty errors array.
		if (!isTaxOrDxp(document))
			return errors;
		if (document.getReverseCharge() == null
				|| document.getReverseCharge().equalsIgnoreCase("N")) {
			for (int idx = 0; idx < items.size(); idx++) {

				InwardTransDocLineItem item = items.get(idx);
				boolean isIntra = isIntraState(document);
				if (document.getSection7OfIgstFlag() == null
						|| document.getSection7OfIgstFlag().isEmpty()
						|| document.getSection7OfIgstFlag()
								.equalsIgnoreCase(GSTConstants.N)) {
					if (isIntra && igstExists(item)) {
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, FIELD_LOCATIONS);
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1306",
										"IGST cannot be "
												+ "applied in case of INTRA state supply",
										location));

					}
				}
				if (!isIntra && cgstOrSgstExists(item)) {

					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, FIELD_LOCATIONS1);
					errors.add(
							new ProcessingResult(APP_VALIDATION, "ER1307",
									"CGST / SGST cannot be applied in "
											+ "case of INTER state supply",
									location));

				}
			}
		}
		return errors;
	}

	private boolean sgstinExists(InwardTransDocument document) {
		return (document.getSgstin() != null)
				&& (!document.getSgstin().isEmpty())
				&& (document.getSgstin().length() == 15);
	}

	private boolean posExists(InwardTransDocument doc) {
		return (doc.getPos() != null) && (!doc.getPos().isEmpty());
	}

	private boolean supplyTypeExists(InwardTransDocument doc) {
		return (doc.getSupplyType() != null)
				&& (!doc.getSupplyType().isEmpty());
	}

	private String getFirst2CharsOfSgstin(InwardTransDocument doc) {
		return doc.getSgstin().substring(0, 2);
	}

	private boolean isTaxOrDxp(InwardTransDocument document) {
		String sType = document.getSupplyType();
		return GSTConstants.TAX.equalsIgnoreCase(sType);
	}

	private boolean isIntraState(InwardTransDocument doc) {
		String first2Chars = getFirst2CharsOfSgstin(doc);
		return first2Chars.equals(doc.getPos());
	}

	private boolean igstExists(InwardTransDocLineItem item) {

		BigDecimal igstAmount = item.getIgstAmount();
		BigDecimal igstrate = item.getIgstRate();

		if (igstrate == null) {
			igstrate = BigDecimal.ZERO;
		}
		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}

		return (BigDecimal.ZERO.compareTo(igstAmount) != 0)
				|| BigDecimal.ZERO.compareTo(igstrate) != 0;
	}

	private boolean cgstOrSgstExists(InwardTransDocLineItem item) {
		BigDecimal cgstAmount = item.getCgstAmount();
		BigDecimal sgstAmount = item.getSgstAmount();
		BigDecimal cgstRate = item.getCgstRate();
		BigDecimal sgstrate = item.getSgstRate();
		if (cgstRate == null) {
			cgstRate = BigDecimal.ZERO;
		}
		if (sgstrate == null) {
			sgstrate = BigDecimal.ZERO;
		}
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}
		return (BigDecimal.ZERO.compareTo(cgstAmount) != 0)
				|| BigDecimal.ZERO.compareTo(sgstAmount) != 0
				|| (BigDecimal.ZERO.compareTo(cgstRate) != 0)
				|| BigDecimal.ZERO.compareTo(sgstrate) != 0;
	}

}
