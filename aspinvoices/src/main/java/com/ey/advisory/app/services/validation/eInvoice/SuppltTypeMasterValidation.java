package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.EInvoiceSupplyTypeCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("SuppltTypeMasterValidation")
public class SuppltTypeMasterValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultEInvoiceSupplyTypeCache")
	private EInvoiceSupplyTypeCache supplyTypeCache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {

			OutwardTransDocLineItem item = items.get(idx);
			String supType = item.getSupplyType();

			if (supType == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLY_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0029",
						"Supply Type Cannot be Empty", location));
				return;
			}

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLY_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			supplyTypeCache = StaticContextHolder.getBean(
					"DefaultEInvoiceSupplyTypeCache",
					EInvoiceSupplyTypeCache.class);
			int n = supplyTypeCache.findSupplyType(
					trimAndConvToUpperCase(supType));
			if (n == 0) {
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0030",
						"Invalid Supply Type", location));
			}
		});
		return errors;
	}

}
