package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.EInvoiceDocTypeCache;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
public class DocumentTypeValidationRule implements ValidationRule {

	@Autowired
	@Qualifier("DefaultEInvoiceDocTypeCache")
	private EInvoiceDocTypeCache docTypeCache;
	
	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0027",
					"Document Type cannot be left blank.", location));
			return errors;
		}

		
		docTypeCache = StaticContextHolder.getBean(
				"DefaultEInvoiceDocTypeCache", EInvoiceDocTypeCache.class);
		int n = docTypeCache
				.finddocType(trimAndConvToUpperCase(obj.toString().trim()));
		if (n <= 0) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_TYPE);
			TransDocProcessingResultLoc location 
			            = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0028",
					"Invalid Document Type", location));
		}
		return errors;
	}

}
