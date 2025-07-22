package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.SubSupplyTypeCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ASuSupplyTypeValidation")
public class Gstr1ASuSupplyTypeValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultSubSupplyTypeCache")
	private SubSupplyTypeCache stateCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSubSupplyType() != null
				&& !document.getSubSupplyType().isEmpty()) {
			String subSupplyType = document.getSubSupplyType();

			stateCache = StaticContextHolder.getBean(
					"DefaultSubSupplyTypeCache", SubSupplyTypeCache.class);
			int n = stateCache
					.findSubSupplyType(trimAndConvToUpperCase(subSupplyType));

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUB_SUPPLY_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10119",
						"Invalid Sub Supply Type", location));
			}
		}
		return errors;
	}
}
