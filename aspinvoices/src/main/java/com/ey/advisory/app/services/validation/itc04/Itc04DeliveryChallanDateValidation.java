/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.Itc04ReturnPeriodCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
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
public class Itc04DeliveryChallanDateValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {
	@Autowired
	@Qualifier("DefaultItc04ReturnPeriodCache")
	private Itc04ReturnPeriodCache itc04ReturnPeriodCache;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String tbleNumber = document.getTableNumber();
		LocalDate deliveryChallanDate = document.getDeliveryChallanaDate();

		if (tbleNumber != null
				&& GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(tbleNumber)) {
			if (deliveryChallanDate == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DELIVERY_CHALLAN_DATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5813",
						"DeliveryChallanDate cannot be left blank.", location));
				return errors;
			}
		}
		if (document.getRetPeriod() != null
				&& !document.getRetPeriod().isEmpty()
				&& document.getFinYear() != null
				&& !document.getFinYear().isEmpty()) {
			itc04ReturnPeriodCache = StaticContextHolder.getBean(
					"DefaultItc04ReturnPeriodCache",
					Itc04ReturnPeriodCache.class);
			int n = itc04ReturnPeriodCache.findRetunPeriod(
					trimAndConvToUpperCase(document.getRetPeriod().trim()));
			if (n <= 0) {
				return errors;
			} else {
				String retPeriod = document.getRetPeriod().trim();
				String finYear = document.getFinYear().trim();
				LocalDate deliveryChallanaDate = document
						.getDeliveryChallanaDate();
				LocalDate retPer = null;
				if (retPeriod.equalsIgnoreCase(GSTConstants.Apr_Jun)
						|| retPeriod.equalsIgnoreCase(GSTConstants.Jul_Sep)
						|| retPeriod.equalsIgnoreCase(GSTConstants.Oct_Dec)
						|| retPeriod.equalsIgnoreCase(GSTConstants.Apr_Sep)) {

					String strRetperiod = retPeriod
							.substring(retPeriod.lastIndexOf("-") + 1);
					String strYear = finYear.substring(0,
							finYear.lastIndexOf("-"));
					retPer = OutwardFileUploadUtil.getLocalDate(strRetperiod,
							strYear);

				}
				if (retPeriod.equalsIgnoreCase(GSTConstants.Jan_Mar)
						|| retPeriod.equalsIgnoreCase(GSTConstants.Oct_Mar)) {

					String strRetperiod = retPeriod
							.substring(retPeriod.lastIndexOf("-") + 1);
					String strYear = finYear
							.substring(finYear.lastIndexOf("-") + 1);
					retPer = OutwardFileUploadUtil.getLocalDate(strRetperiod,
							strYear);
				}

				if (deliveryChallanaDate != null
						&& deliveryChallanaDate.compareTo(retPer) > 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.DELIVERY_CHALLAN_DATE);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5859",
							"DeliveryChallanDate cannot be greater than last date of return period.",
							location));
					return errors;
				}
			}
		}

		return errors;
	}
}
