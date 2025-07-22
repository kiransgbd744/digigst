package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9UqcValidator
		implements BusinessRuleValidator<Gstr9HsnAsEnteredEntity> {

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	@Override
	public List<ProcessingResult> validate(Gstr9HsnAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		String uom = document.getUqc();
		if (uom == null || uom.isEmpty()) {
			document.setUqc(GSTConstants.OTH);
		}
		if (uom != null && !uom.isEmpty()) {
			uomCache = StaticContextHolder.getBean("DefaultUomCache",
					UomCache.class);
			int m = uomCache.finduom(trimAndConvToUpperCase(uom));
			int n = uomCache.finduomDesc(trimAndConvToUpperCase(uom));
			int o = uomCache.finduomMergeDesc(trimAndConvToUpperCase(uom));

			if (m == 1 || n == 1 || o == 1) {
				if (n == 1) {
					document.setUqc(uomCache.uQcDescAndCodemap()
							.get(trimAndConvToUpperCase(uom)));
				}
				if (o == 1) {

					document.setUqc(uomCache.uQcDesc()
							.get(trimAndConvToUpperCase(uom)));
				}
			} else {

				document.setUqc(GSTConstants.OTH);

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.UQC);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.ERROR, "ER6189",
						"Invalid Unit Of Measurement as per Masters",
						location));
			}

		}

		return errors;
	}

}
