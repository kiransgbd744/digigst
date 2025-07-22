package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9GstnValidator
		implements BusinessRuleValidator<Gstr9HsnAsEnteredEntity> {
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Gstr9HsnAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);

		if (document.getGstin() != null && !document.getGstin().isEmpty()) {
			String groupCode = TenantContext.getTenantId();
			// int n=gstinInfoRepository.findgstin(document.getCgstin());
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getGstin());
			if (gstin == null) {

				errorLocations.add(GSTConstants.GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6185",
						"GSTIN not on boarded",
						location));
			}
		}
		return errors;
	}
}