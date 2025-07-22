package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class RecipientGSTINValidator
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		/*gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);*/
		String groupCode = TenantContext.getTenantId();	
		if (document.getRecipientGstin() != null
				&& !document.getRecipientGstin().isEmpty()) {
			String recipientGstin = document.getRecipientGstin();
			ehcachegstin = StaticContextHolder.
					getBean("Ehcachegstin",Ehcachegstin.class);
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					GenUtil.trimAndConvToUpperCase(recipientGstin));
		/*	int n = gstinInfoRepository
					.findgstin(GenUtil.trimAndConvToUpperCase(recipientGstin));*/
			if (gstin == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RECIPIENT_GSTIN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1204",
						"Recipient GSTIN is not as per On-Boarding data",
						location));
			}
		}
		return errors;
	}
}
