package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.PortCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("LuPortCodeValidator")
public class LuPortCodeValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLYTYPE = ImmutableList
			.of(GSTConstants.EXPT, GSTConstants.EXPWT);

	@Autowired
	@Qualifier("DefaultPortCache")
	private PortCache portCache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (SUPPLYTYPE.contains(
					trimAndConvToUpperCase(document.getSupplyType()))) {

				if (document.getPortCode() != null
						&& !document.getPortCode().isEmpty()) {
					String portcode = document.getPortCode();
					portCache = StaticContextHolder.
							getBean("DefaultPortCache",PortCache.class);
					int n=portCache.findPortCode(portcode);
					if (n <= 0) {

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.PORT_CODE);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER0059", "Invalid Port Code", location));
					}
				}
			}
		}
		return errors;
	}
}