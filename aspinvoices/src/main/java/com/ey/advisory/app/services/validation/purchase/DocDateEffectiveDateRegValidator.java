package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class DocDateEffectiveDateRegValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private LocalDate gstinRegDate;
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getDocDate() != null) {
			LocalDate docDate = document.getDocDate();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");

			LocalDate gstStartDate = LocalDate.parse("01072017", formatter);
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getCgstin());
			if (gstin != null && gstin.getRegDate() != null) {
					gstinRegDate = gstin.getRegDate();
					if ((docDate.compareTo(gstinRegDate) < 0)
							|| (docDate.compareTo(gstStartDate) < 0)) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(DOC_DATE);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1046",
										"Document Date cannot be before "
												+ "Date of Registration.",
										location));

					} 
				
			}
		}
		return errors;
	}

}
