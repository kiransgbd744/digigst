package com.ey.advisory.app.services.validation.tdstcs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.caches.TcsTdsCategoryCache;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
public class TcsTdsCategoryValidation
		implements B2csBusinessRuleValidator<Gstr2XExcelTcsTdsEntity> {

	@Autowired
	@Qualifier("DefaultTcsTdsCategoryCache")
	private TcsTdsCategoryCache categoryCache;

	@Override
	public List<ProcessingResult> validate(Gstr2XExcelTcsTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getRecordType() != null
				&& !document.getRecordType().trim().isEmpty()) {

			categoryCache = StaticContextHolder.getBean(
					"DefaultTcsTdsCategoryCache", TcsTdsCategoryCache.class);

			String recordType = document.getRecordType();
			int i = categoryCache.findCategory(recordType);
			if (i <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1326",
						"Invalid Type", location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid Type error is {} ", errors);
					
				}
				
				return errors;
			}
			if (document.getOrgTaxableValue() == null
					|| document.getOrgTaxableValue().trim().isEmpty()) {
			if ("TCSA".equalsIgnoreCase(document.getRecordType()) ||
					"TDSA".equalsIgnoreCase(document.getRecordType()) ) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORIGINALTAXABLEVALUE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1349",
						"Invalid OriginalTaxableValue", location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid OriginalTaxableValue error is {} ", errors);
					
				}
				
				return errors;
			}
			}
			if (document.getOrgInvoiceValue() == null
					|| document.getOrgInvoiceValue().trim().isEmpty()) {
			if ("TCSA".equalsIgnoreCase(document.getRecordType()) ||
					"TDSA".equalsIgnoreCase(document.getRecordType()) ) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORIGINAL_INVOICE_VAL);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1350",
						"Invalid OriginalInvoiceValue", location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid OriginalInvoiceValue error is {} ", errors);
					
				}
				
				return errors;
			}
			}
		}
		
		return errors;
	}

}
