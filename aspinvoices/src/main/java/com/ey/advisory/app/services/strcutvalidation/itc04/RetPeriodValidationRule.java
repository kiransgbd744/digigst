package com.ey.advisory.app.services.strcutvalidation.itc04;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.Itc04ReturnPeriodCache;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class RetPeriodValidationRule implements ValidationRule {

	@Autowired
	@Qualifier("DefaultItc04ReturnPeriodCache")
	private Itc04ReturnPeriodCache itc04ReturnPeriodCache;

	private static final List<String> validReturns_2020 = ImmutableList
			.of("APR-JUN", "JUL-SEP", "OCT-DEC", "JAN-MAR");
	private static final List<String> validReturns_2021 = ImmutableList
			.of("APR-JUN", "JUL-SEP", "OCT-DEC", "JAN-MAR", "OCT-MAR");
	private static final List<String> validReturns_2022 = ImmutableList.of(
			"APR-JUN", "JUL-SEP", "APR-SEP", "OCT-DEC", "JAN-MAR", "OCT-MAR");

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RET_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5806",
					"ReturnPeriod cannot be left Blank.", location));
			return errors;
		}
		itc04ReturnPeriodCache = StaticContextHolder.getBean(
				"DefaultItc04ReturnPeriodCache", Itc04ReturnPeriodCache.class);

		int n = itc04ReturnPeriodCache
				.findRetunPeriod(trimAndConvToUpperCase(obj.toString().trim()));

		if (n <= 0) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RET_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5807",
					"Invalid ReturnPeriod.", location));
			return errors;

		} else {
			String fy = row[2] != null ? (String) row[2] : null;
			if (fy == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RET_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5807",
						"Invalid FY.", location));
				return errors;
			} else if (!isValidReturnPeriod(Integer.parseInt(fy.substring(0,4)),
					trimAndConvToUpperCase(obj.toString().trim()))) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RET_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5807",
						"Invalid Return Period.", location));
				return errors;
			}
		}
		if (obj.toString().trim().length() > 20) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RET_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5807",
					"Invalid Return Period.", location));
			return errors;
		}
		return errors;
	}

	private boolean isValidReturnPeriod(Integer fy, String returnPeriod) {
		if (fy < 2021) {
			if (validReturns_2020.contains(returnPeriod)) {
				return true;
			}
		} else if (fy == 2021) {
			if (validReturns_2021.contains(returnPeriod)) {
				return true;
			}
		} else if (fy > 2021) {
			if (validReturns_2022.contains(returnPeriod)) {
				return true;
			}
		}
		return false;
	}
}
