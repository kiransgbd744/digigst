package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AInWardCgstinValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())) {
			if (GSTConstants.URP.equalsIgnoreCase(document.getCgstin())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15168",
						"Customer GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
			if (document.getCgstin() != null
					&& !document.getCgstin().isEmpty()) {
				ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
						Ehcachegstin.class);

				GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
						document.getCgstin());
				if (gstin == null) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.CGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15168",
							"Customer GSTIN is not as per On-Boarding data",
							location));
					return errors;
				}
			}
		}

		return errors;
	}
}
