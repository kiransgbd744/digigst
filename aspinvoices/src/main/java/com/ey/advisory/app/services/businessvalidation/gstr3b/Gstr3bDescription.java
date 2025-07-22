package com.ey.advisory.app.services.businessvalidation.gstr3b;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.ehcache.EhcacheGstr3bItc;
import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class Gstr3bDescription
		implements BusinessRuleValidator<Gstr3bExcelEntity> {

	@Autowired
	@Qualifier("DefaultGstr3BItcCache")
	private EhcacheGstr3bItc ehcacheGstr3bItc;

	@Override
	public List<ProcessingResult> validate(Gstr3bExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDescription() != null
				&& !document.getDescription().isEmpty()) {
			ehcacheGstr3bItc = StaticContextHolder
					.getBean("DefaultGstr3BItcCache", EhcacheGstr3bItc.class);
			String description = document.getDescription().replaceAll("\\s", ""); 

			int i = ehcacheGstr3bItc.findDesCription(description);
			if (i <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DESCRIPTION);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6106",
						"Invalid Description.", location));
			}
		}
		return errors;
	}
}
