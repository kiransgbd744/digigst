/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.Itc04ReturnPeriodCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04ReturnPeriodMasterValidation")
public class Itc04ReturnPeriodMasterValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("DefaultItc04ReturnPeriodCache")
	private Itc04ReturnPeriodCache itc04ReturnPeriodCache;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getRetPeriod() != null
				&& !document.getRetPeriod().isEmpty()) {

			itc04ReturnPeriodCache = StaticContextHolder.getBean(
					"DefaultItc04ReturnPeriodCache",
					Itc04ReturnPeriodCache.class);
			int n = itc04ReturnPeriodCache.findRetunPeriod(
					trimAndConvToUpperCase(document.getRetPeriod()));
			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RETURN_PREIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5807",
						"Invalid ReturnPeriod", location));
			}
		}
		return errors;
	}
}
