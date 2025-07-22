
package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6CmOriginalDocDateValidator 
 implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	private LocalDate gstinRegDate;

	/**
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 * @param ProcessingContext
	 */

	@Override
	public List<ProcessingResult> validate(Gstr6DistributionExcelEntity document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getOrgDocDate() != null) {
			/*String docDate1 = document.getOrgDocDate().toString();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate docDate = LocalDate.parse(docDate1, formatter);
			LocalDate gstStartDate = LocalDate.parse("01072017", formatter);*/
			
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate docDate = document.getOrgDocDate();
			LocalDate gstStartDate = LocalDate.parse("01072017", formatter);
			
			
			ehcachegstin = StaticContextHolder.
					getBean("Ehcachegstin",Ehcachegstin.class);
			
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getIsdGstin());
			if (gstin != null) {
				if (gstin.getRegDate() != null) {
					gstinRegDate = gstin.getRegDate();

					if ((docDate.compareTo(gstinRegDate) < 0)) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER3054",
										"Original Document Date cannot be before "
										+ "Date of Registration.",
										location));

					} else if (docDate.compareTo(gstStartDate) < 0) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER3054",
										"Original Document Date cannot be before "
										+ "Date of Registration.",
										location));
					}

				}
			}
		}
		return errors;
	}

}
