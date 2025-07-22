package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.TDS_DEDUCTOR_GSTIN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
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
public class Gstr7TdsDeductorValidator
		implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getTdsGstin() != null && !document.getTdsGstin().isEmpty()
				&& document.getTdsGstin().length() == 15) {
			String groupCode = TenantContext.getTenantId();
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);
			GSTNDetailEntity gstinEntity = ehcachegstin.getGstinInfo(groupCode,
					document.getTdsGstin());
			if (gstinEntity == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(TDS_DEDUCTOR_GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2027",
						"TDS Deductor GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
			if (!GSTConstants.TDS
					.equalsIgnoreCase(gstinEntity.getRegistrationType())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(TDS_DEDUCTOR_GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2027",
						"TDS Deductor GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
		}
		return errors;
	}
}