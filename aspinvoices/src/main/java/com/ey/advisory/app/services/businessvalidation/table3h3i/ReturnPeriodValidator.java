package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class ReturnPeriodValidator
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	private LocalDate gstinRegDate;

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {

		String groupCode = TenantContext.getTenantId();
		/*gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);*/
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getReturnPeriod() != null
				&& !document.getReturnPeriod().isEmpty()) {
			if (document.getSupplierGSTINorpan() != null
					&& !document.getSupplierGSTINorpan().isEmpty()) {
				if (document.getSupplierGSTINorpan().length() == 15) {
					/*List<GSTNDetailEntity> gstin = gstinInfoRepository
							.findByGstin(document.getSupplierGSTINorpan());*/
					ehcachegstin = StaticContextHolder.
							getBean("Ehcachegstin",Ehcachegstin.class);
					
					GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
							document.getSupplierGSTINorpan());
					if (gstin != null ) {
						if (gstin.getRegDate() != null) {
							gstinRegDate = gstin.getRegDate();
							if (gstinRegDate != null) {
								String regMonth = String
										.valueOf(gstinRegDate.getMonthValue());
								int RegistrationMonth = Integer
										.parseInt(regMonth);
								String regYear = String
										.valueOf(gstinRegDate.getYear());
								String ReturnYear = document.getReturnPeriod()
										.substring(2, 6);
								String ReturnMonth = document.getReturnPeriod()
										.substring(0, 2);
								int ReturnperiodMonth = Integer
										.parseInt(ReturnMonth);
								if (ReturnYear.compareTo(regYear) < 0) {
								List<String> errorLocations = new ArrayList<>();

									errorLocations
											.add(GSTConstants.RETURN_PREIOD);
									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, " ER1207",
											"Return Period cannot be before "
													+ "Date of Registration",
											location));
								}
								if (ReturnYear.compareTo(regYear) == 0) {
									// if
									// (ReturnperiodMonth.compareTo(RegistrationMonth)
									// <
									// 0) {
									if (ReturnperiodMonth < RegistrationMonth) {
										List<String> errorLocations =
												new ArrayList<>();

										errorLocations.add(
												GSTConstants.RETURN_PREIOD);
										TransDocProcessingResultLoc location =
												new TransDocProcessingResultLoc(
												null, errorLocations.toArray());
										errors.add(new ProcessingResult(
												APP_VALIDATION, " ER1207",
												"Return Period cannot be before "
														+ "Date of Registration",
												location));

									}
								}
							}
						}
					}
				}
			}
		}
		return errors;
	}

}
