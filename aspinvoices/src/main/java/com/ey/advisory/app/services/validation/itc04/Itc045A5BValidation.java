/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Itc045A5BValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	private static final List<String> TABLE_NUM = ImmutableList
			.of(GSTConstants.TABLE_NUMBER_5A, GSTConstants.TABLE_NUMBER_5B);

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		String tbleNumber = document.getTableNumber();

		if (TABLE_NUM.contains(tbleNumber)) {
			if (document.getDeliveryChallanaNumber() != null
					&& document.getDeliveryChallanaDate() != null
					&& document.getJwDeliveryChallanaNumber() != null
					&& document.getJwDeliveryChallanaDate() != null) {
				return errors;
			}
			if (document.getDeliveryChallanaNumber() != null
					&& document.getDeliveryChallanaDate() != null
					&& document.getJwDeliveryChallanaNumber() == null
					&& document.getJwDeliveryChallanaDate() == null) {
				return errors;
			}
			if (document.getDeliveryChallanaNumber() == null
					&& document.getDeliveryChallanaDate() == null
					&& document.getJwDeliveryChallanaNumber() != null
					&& document.getJwDeliveryChallanaDate() != null) {
				return errors;
			} 
			
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DELIVERY_CHALLAN_DATE);
				errorLocations.add(GSTConstants.DELIVERY_CHALLAN_NUMBER);
				errorLocations.add(GSTConstants.JW_DELIVERY_CHALLAN_DATE);
				errorLocations.add(GSTConstants.JW_DELIVERY_CHALLAN_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5856",
						"DeliveryChallanNumber & DeliveryChallanDate OR "
								+ "JWDeliveryChallanNumber & JWDeliveryChallanDate "
								+ "either of details are mandatory.",
						location));
				return errors;
			

		}
		return errors;
	}
}
