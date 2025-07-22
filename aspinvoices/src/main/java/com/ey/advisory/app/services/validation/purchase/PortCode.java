package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.PortCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import com.google.common.base.Strings;
/**
 * 
 * @author Siva.Nandam
 *
 */
public class PortCode implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("DefaultPortCache")
	private PortCache portCache;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (GSTConstants.IMPG.equalsIgnoreCase(document.getSupplyType())
					|| GSTConstants.SEZG
							.equalsIgnoreCase(document.getSupplyType())) {
				if (document.getPortCode() != null
						&& !document.getPortCode().isEmpty()) {
					portCache = StaticContextHolder.getBean("DefaultPortCache",
							PortCache.class);
					int n = portCache.findPortCode(document.getPortCode());
					if (n <= 0) {

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.PORT_CODE);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER1061", "Invalid Port Code", location));

					}
				}
			} else {
				if (!Strings.isNullOrEmpty(document.getPortCode())) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.PORT_CODE);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER1061", "Invalid Port Code", location));
				}
			}
		}
		return errors;
	}
}
