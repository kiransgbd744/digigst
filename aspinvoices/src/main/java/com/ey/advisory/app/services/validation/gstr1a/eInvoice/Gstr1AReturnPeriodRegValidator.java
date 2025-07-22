package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

@Component("Gstr1AReturnPeriodRegValidator")
public class Gstr1AReturnPeriodRegValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	private LocalDate gstinRegDate;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();

		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getTaxperiod() != null
				&& !document.getTaxperiod().isEmpty()) {

			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);

			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getSgstin());
			if (gstin != null) {
				if (gstin.getRegDate() != null) {
					gstinRegDate = gstin.getRegDate();
					if (gstinRegDate != null) {
						String regMonth = String
								.valueOf(gstinRegDate.getMonthValue());
						int RegistrationMonth = Integer.parseInt(regMonth);
						String regYear = String.valueOf(gstinRegDate.getYear());
						String ReturnYear = document.getTaxperiod().substring(2,
								6);
						String ReturnMonth = document.getTaxperiod()
								.substring(0, 2);
						int ReturnperiodMonth = Integer.parseInt(ReturnMonth);
						if (ReturnYear.compareTo(regYear) < 0) {
							List<String> errorLocations = new ArrayList<>();

							errorLocations.add(GSTConstants.RETURN_PREIOD);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0022",
									"Return Period cannot be before "
											+ "Date of Registration",
									location));
						}
						if (ReturnYear.compareTo(regYear) == 0) {
							if (ReturnperiodMonth < RegistrationMonth) {
								List<String> errorLocations = new ArrayList<>();

								errorLocations.add(GSTConstants.RETURN_PREIOD);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0022",
										"Return Period cannot be before "
												+ "Date of Registration",
										location));

							}
						}
					}
				}
			}
		}
		return errors;
	}

}
