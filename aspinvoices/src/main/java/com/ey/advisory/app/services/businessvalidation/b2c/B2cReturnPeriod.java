package com.ey.advisory.app.services.businessvalidation.b2c;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2cReturnPeriod
		implements BusinessRuleValidator<OutwardB2cExcelEntity> {

	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@SuppressWarnings("unused")
	@Override
	public List<ProcessingResult> validate(OutwardB2cExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getRetPeriod() != null
					&& !document.getRetPeriod().isEmpty()) {
				String tax = "01" + document.getRetPeriod().trim();
				/*String gstin = document.getSgstin().trim();*/

				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("ddMMyyyy");
				LocalDate pregst = LocalDate.of(2017, 07, 01);
				// Calculate the last day of the month.
				LocalDate returnPeriod = LocalDate.parse(tax, formatter);
				
				ehcachegstin = StaticContextHolder.
						getBean("Ehcachegstin",Ehcachegstin.class);
				
				GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
						document.getSgstin());
				/*gstinInfoRepository = StaticContextHolder.getBean(
						"GSTNDetailRepository", GSTNDetailRepository.class);

				List<GSTNDetailEntity> regDate = gstinInfoRepository
						.findByGstin(gstin);*/
				if (gstin.getRegDate() != null ) {
					LocalDate regDates = gstin.getRegDate();
					if (regDates != null) {
						if (returnPeriod.compareTo(pregst) < 0
								|| returnPeriod.compareTo(regDates) < 0) {
							errorLocations.add(RETURN_PREIOD);
							TransDocProcessingResultLoc location =
									new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0207",
						"Return Period cannot be before Date of Registration",
									location));
						}

					}
				}
			}
		}
		return errors;
	}
}
