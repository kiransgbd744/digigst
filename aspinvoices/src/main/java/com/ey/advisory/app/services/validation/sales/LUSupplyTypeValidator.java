package com.ey.advisory.app.services.validation.sales;

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

import com.ey.advisory.app.caches.SupplyTypeCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("LUSupplyTypeValidator")
public class LUSupplyTypeValidator
		implements DocRulesValidator<OutwardTransDocument> {
	
	@Autowired
	@Qualifier("DefaultSupplyTypeCache")
	private SupplyTypeCache supplyTypeCache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {

			OutwardTransDocLineItem item = items.get(idx);
			if (item.getSupplyType() != null
					&& !item.getSupplyType().isEmpty()) {

				supplyTypeCache = StaticContextHolder.getBean(
						"DefaultSupplyTypeCache",
						SupplyTypeCache.class);
				int n = supplyTypeCache.findSupplyType(
						trimAndConvToUpperCase(item.getSupplyType()));
				if (n <= 0) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SUPPLY_TYPE);
					TransDocProcessingResultLoc location 
					             = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, " ER0030",
							"Invalid Supply Type", location));
				}
			}
		});
		return errors;
	}
}
