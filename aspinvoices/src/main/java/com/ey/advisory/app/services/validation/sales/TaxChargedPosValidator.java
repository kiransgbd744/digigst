/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.*;

import static com.ey.advisory.app.data.entities.client.GSTConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * 
 * This class is responsible to IGST is applied on all inter state transactions
 * 
 * BUSINESS RULE_ID --BR_OUTWARD_53
 * 
 * @author Murali.Singanamala
 *
 *//*
@Component("TaxChargedPOS")
public class TaxChargedPosValidator implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
		String supplyType = document.getSupplyType();
		String pos = document.getPos();
		String sgstin = document.getSgstin();
		String cgstin = document.getCgstin();
		// String poscode;

		String supplyGstn = sgstin.substring(0, 2);
		String reveipentgstn = cgstin.substring(0, 2);

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal igstAmount = item.getIgstAmount();

			if (!supplyType.equalsIgnoreCase(NSY)
					&& !supplyType.equalsIgnoreCase(NON)
					&& !supplyType.equalsIgnoreCase(NIL)
					&& !supplyType.equalsIgnoreCase(EXT)
					&& !supplyType.equalsIgnoreCase(EXPT)
					&& !supplyType.equalsIgnoreCase(SEZ)
					&& !supplyType.equalsIgnoreCase(DTA)) {
				*//**
				 * This method first check the first 2 digits of supplier gstn
				 * and Receipent gstn and pos is blank or same state code
				 * 
				 *//*

				if (!supplyGstn.equalsIgnoreCase(reveipentgstn)
						&& (pos.equalsIgnoreCase(reveipentgstn)
								|| (pos.equalsIgnoreCase("")))) {

					if ((cgstAmount.add(sgstAmount))
							.compareTo(BigDecimal.ZERO) != 0) {

						errorLocations.add(CGST_AMOUNT);
						errorLocations.add(SGST_AMOUNT);

						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER237",
								"In case of Inter-State Supply, CGST & "
										+ "SGST/UTGST "
										+ "Tax Rate and/or Tax Amount "
										+ "cannot be applied",
								location));
					}
				}
			}
		});

		return errors;
	}
}
*/