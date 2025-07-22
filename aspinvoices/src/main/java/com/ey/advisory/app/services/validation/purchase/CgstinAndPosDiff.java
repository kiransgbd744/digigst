package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.beust.jcommander.Strings;
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
public class CgstinAndPosDiff implements 
               DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			      ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String cgstin = document.getCgstin();
		String pos = document.getPos();
		String itcEntitlement = document.getItcEntitlement();

		if (Strings.isStringEmpty(cgstin) || Strings.isStringEmpty(pos)
				|| GSTConstants.Y.equalsIgnoreCase(itcEntitlement))
			return errors;
		if (!document.getCgstin().substring(0, 2).equalsIgnoreCase(document.getPos())) {
			List<InwardTransDocLineItem> items = document.getLineItems();
			BigDecimal amount = BigDecimal.ZERO;
			// IntStream.range(0, items.size()).forEach(idx -> {
			for (InwardTransDocLineItem item : items) {
				// InwardTransDocLineItem item = items.get(idx);
				BigDecimal availableIgst = item.getAvailableIgst();
				BigDecimal availableCgst = item.getAvailableCgst();
				BigDecimal availableSgst = item.getAvailableSgst();
				BigDecimal availableCess = item.getAvailableCess();
				if (availableIgst == null) {
					availableIgst = BigDecimal.ZERO;
				}
				if (availableCgst == null) {
					availableCgst = BigDecimal.ZERO;
				}
				if (availableSgst == null) {
					availableSgst = BigDecimal.ZERO;
				}
				if (availableCess == null) {
					availableCess = BigDecimal.ZERO;
				}
				BigDecimal allAmount = availableIgst
						.add(availableCgst).add(availableSgst)
						.add(availableCess);
				amount = amount.add(allAmount);

			}
			if (amount.compareTo(BigDecimal.ZERO) > 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ITC_ENTITLEMENT);
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						"ER1310", "ITC Entitilement cannot be 'N', " 
				+ "where POS & first 2 digits "
								+ "of Recipient GSTIN " 
				+ "are different and credit "
								+ "needs to be availed.",
						location));

			}

		}

		return errors;
	}

}
