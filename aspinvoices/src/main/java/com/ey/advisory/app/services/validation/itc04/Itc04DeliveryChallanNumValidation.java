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
 * @author Laxmi.Salukuti
 *
 */
public class Itc04DeliveryChallanNumValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String tbleNumber = document.getTableNumber();
		String deliveryChallanNum = document.getDeliveryChallanaNumber();

		if (tbleNumber != null
				&& GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(tbleNumber)) {
			if (Strings.isNullOrEmpty(deliveryChallanNum)) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DELIVERY_CHALLAN_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5811",
						"DeliveryChallanNumber cannot be left blank.",
						location));
				return errors;
			}
		}

		if (Strings.isNullOrEmpty(deliveryChallanNum)) {
			return errors;
		} else {
			deliveryChallanNum = deliveryChallanNum.trim();
			String regex = "^[a-zA-Z0-9/-]*$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(deliveryChallanNum);
			if (deliveryChallanNum.length() > 16 || !matcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DELIVERY_CHALLAN_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5810",
						"Invalid DeliveryChallanNumber.", location));
				return errors;
			}
		}

		return errors;
	}
}
