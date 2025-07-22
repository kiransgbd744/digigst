package com.ey.advisory.app.services.validation.gstr9.inoutward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
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
public class Gstr9InOutwardGstin implements
		B2csBusinessRuleValidator<Gstr9OutwardInwardAsEnteredEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(
			Gstr9OutwardInwardAsEnteredEntity document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();
		
		if (document.getGstin() != null && !document.getGstin().isEmpty()) {
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					GenUtil.trimAndConvToUpperCase(document.getGstin()));
			if (gstin == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
						"ER6149", "GSTIN not onboarded for process.",
						location));
				return errors;
			}
		}
		return errors;
	}
}
