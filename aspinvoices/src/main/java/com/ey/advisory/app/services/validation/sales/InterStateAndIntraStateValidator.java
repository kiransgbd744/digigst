/*package com.ey.advisory.app.services.validation.sales;

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
 * This class is responsible to whether CGST/SGST is applied on all intra state
 * transactions whether IGST is applied on all inter state transactions
 * 
 * BUSINESS RULE_ID --BR_OUTWARD_70
 * 
 * @author Murali.Singanamala
 *
 *//*
@Component("InterStateAndIntraStateValidator")
public class InterStateAndIntraStateValidator
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		List<String> errorLocations = new ArrayList<>();
		String pos = document.getPos();
		String sgstin = document.getSgstin();

		String supplyGstn = sgstin.substring(0, 2);

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			BigDecimal cgstAmount = item.getCgstAmount();
			BigDecimal sgstAmount = item.getSgstAmount();
			BigDecimal igstAmount = item.getIgstAmount();
			BigDecimal f=cgstAmount.add(sgstAmount);
			*//**
			 * This method first check the first 2 digits of supplier gstn and
			 * and pos is same state code
			 *//*
if(pos!=null && !pos.isEmpty()){
			if (!pos.equalsIgnoreCase(supplyGstn)) {
				if (!(igstAmount.compareTo(BigDecimal.ZERO) != 0) || !(igstAmount!=null)) {

					errorLocations.add(IGST_AMOUNT);

					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER237",
							"In case of Inter-State Supply, CGST & SGST/UTGST "
							+ "Tax Rate and/or Tax Amount cannot be applied",
							location));

				}
			}}

			*//**
			 * This method first check the first 2 digits of supplier gstn and
			 * and pos is different state code
			 *//*
if(pos!=null && !pos.isEmpty()){
			if (pos.equalsIgnoreCase(supplyGstn)) {

				if (!((cgstAmount.add(sgstAmount))
						.compareTo(BigDecimal.ZERO) != 0) || !(f!=null)) {

					errorLocations.add(CGST_AMOUNT);
					errorLocations.add(SGST_AMOUNT);

					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER238",
							"  In case of Intra-State Supply, IGST Tax Rate "
							+ "and and/or Tax Amount cannot be applied",
							location));

				}
			}}
		});

		return errors;
	}
}
*/