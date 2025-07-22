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

import com.ey.advisory.app.caches.Itc04TableNumberCache;
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
@Component("Itc04TableNumberMasterValidation")
public class Itc04TableNumberMasterValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("DefaultItc04TableNumberCache")
	private Itc04TableNumberCache tableNumbereCache;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getTableNumber() != null
				&& !document.getTableNumber().isEmpty()) {

			tableNumbereCache = StaticContextHolder.getBean(
					"DefaultItc04TableNumberCache",
					Itc04TableNumberCache.class);
			int n = tableNumbereCache.findTableNumber(
					trimAndConvToUpperCase(document.getTableNumber()));
			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TABLE_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5802",
						"Invalid TableNumber", location));
			}
		}
		return errors;
	}

}