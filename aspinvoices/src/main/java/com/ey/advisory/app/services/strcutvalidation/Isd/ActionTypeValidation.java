package com.ey.advisory.app.services.strcutvalidation.Isd;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.repositories.client.IsdDistributionPsdRepository;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class ActionTypeValidation implements ValidationRule {

	@Autowired
	@Qualifier("IsdDistributionPsdRepository")
	private IsdDistributionPsdRepository isdDistributionPsdRepository;

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (isPresent(obj)) {
			String actionType = obj.toString().trim();

			if (!actionType.equalsIgnoreCase("CAN")) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ACTION_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0035",
						"Invalid Action Type.", location));
				return errors;
			}
		}

		return errors;
	}

}
