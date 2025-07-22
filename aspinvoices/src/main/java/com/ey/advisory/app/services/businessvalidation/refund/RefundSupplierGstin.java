package com.ey.advisory.app.services.businessvalidation.refund;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
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

public class RefundSupplierGstin
		implements BusinessRuleValidator<RefundsExcelEntity> {

	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(RefundsExcelEntity document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();	
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			ehcachegstin = StaticContextHolder.
					getBean("Ehcachegstin",Ehcachegstin.class);
			
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					GenUtil.trimAndConvToUpperCase(document.getSgstin()));
			/*gstinInfoRepository = StaticContextHolder.getBean(
					"GSTNDetailRepository", GSTNDetailRepository.class);
			String gstin = document.getSgstin();
			List<GSTNDetailEntity> out = gstinInfoRepository
					.findByGstin(GenUtil.trimAndConvToUpperCase(gstin));*/
			if (gstin == null ) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1877",
						"Supplier GSTIN is not as per On-Boarding data.",
						location));
			}
		}
		return errors;
	}
}
