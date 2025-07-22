package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.ACTION_TYPE;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr7ActionTypeValidator implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String actionType = document.getActType();
		if (StringUtils.isNoneBlank(actionType) && !"CAN".equalsIgnoreCase(actionType)) {
			errorLocations.add(ACTION_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2002", "Invalid Action Type", location));
			return errors;
		}
		return errors;
	}

}
