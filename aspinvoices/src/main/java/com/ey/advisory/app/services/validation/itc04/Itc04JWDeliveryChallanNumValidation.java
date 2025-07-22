/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

/**
 * @author Siva.Reddy
 *
 */
public class Itc04JWDeliveryChallanNumValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String jwDeliveryChallanNum = document.getJwDeliveryChallanaNumber();

		if (Strings.isNullOrEmpty(jwDeliveryChallanNum)) {
			return errors;
		} else {
			jwDeliveryChallanNum = jwDeliveryChallanNum.trim();
			String regex = "^[a-zA-Z0-9/-]*$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(jwDeliveryChallanNum);
			if (jwDeliveryChallanNum.length() > 16 || !matcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.JW_DELIVERY_CHALLAN_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5814",
						"Invalid JWDeliveryChallanNumber.", location));
				return errors;
			}
		}
		return errors;
	}

}
