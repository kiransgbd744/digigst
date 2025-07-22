package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;

import joptsimple.internal.Strings;

/**
 * @author Siva.Reddy
 *
 */
@Component("GSTR7TransOriginalDocDate")
public class GSTR7TransOriginalDocDate
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.RNV);

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	private LocalDate gstinRegDate;

	/**
	 * @param Gstr7TransDocHeaderEntity
	 * @param ProcessingContext
	 */

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;

		if (!DOCTYPE.contains(document.getDocType()))
			return errors;

		if ("RNV".equalsIgnoreCase(document.getDocType())) {
			String originalDocNum = document.getOriginalDocNum();

			if (Strings.isNullOrEmpty(originalDocNum)) {

				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);

				errors.add(new ProcessingResult(APP_VALIDATION, "ER63030",
						"Original Document Date cannot be left blank",
						location));
			} else if (document.getOriginalDocDate() != null) {
				LocalDate originalDocDate = document.getOriginalDocDate();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("ddMMyyyy");

				LocalDate gstStartDate = LocalDate.parse("01072017", formatter);
				ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
						Ehcachegstin.class);

				GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
						document.getDeducteeGstin());
				if (gstin != null && gstin.getRegDate() != null) {
					gstinRegDate = gstin.getRegDate();

					if ((originalDocDate.compareTo(gstinRegDate) < 0)) {
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER0036",
										"Document Date cannot be before "
												+ "Date of Registration.",
										location));

					} else if (originalDocDate.compareTo(gstStartDate) < 0) {
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER0036",
										"Document Date cannot be before "
												+ "Date of Registration.",
										location));
					}

				}
			}
		}
		return errors;
	}

}