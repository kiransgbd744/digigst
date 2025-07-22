package com.ey.advisory.app.services.validation.purchase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import com.ey.advisory.app.caches.SupplyTypeCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * siva krishna
 */
@Slf4j
public class SupplyType implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("DefaultInwardSupplyTypeCache")
	private SupplyTypeCache supplyTypeCache;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<InwardTransDocLineItem> items = document.getLineItems();
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside SupplyType {} {}"+document.getSupplyType()+" "+document.getDocType();
			LOGGER.debug(msg);
		}
		if (document.getSupplyType() != null && !document.getSupplyType().isEmpty()
				&& document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				String msg = " Inside SupplyType1 {} {}"+document.getSupplyType()+" "+document.getDocType();
				LOGGER.debug(msg);
			}
			if ((("IMPG".equalsIgnoreCase(document.getSupplyType())
					&& ("SLF".equalsIgnoreCase(document.getDocType()))
							|| ("SEZG".equalsIgnoreCase(document.getSupplyType()))
					&& "RSLF".equalsIgnoreCase(document.getDocType())))) {
				if (LOGGER.isDebugEnabled()) {
					String msg = " Inside Error Condition {} {}"+document.getSupplyType()+" "+document.getDocType();
					LOGGER.debug(msg);
				}
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.SUPPLY_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1040",
						"Invalid Supply Type", location));
			} else {
				IntStream.range(0, items.size()).forEach(idx -> {

					InwardTransDocLineItem item = items.get(idx);
					if (item.getSupplyType() != null
							&& !item.getSupplyType().isEmpty()) {

						supplyTypeCache = StaticContextHolder.getBean(
								"DefaultInwardSupplyTypeCache",
								SupplyTypeCache.class);
						int n = supplyTypeCache.findSupplyType(
								trimAndConvToUpperCase(item.getSupplyType()));
						if (n <= 0) {

							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.SUPPLY_TYPE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1040", "Invalid Supply Type", location));
						}
					}
				});
			}
		}

		return errors;
	}
}
