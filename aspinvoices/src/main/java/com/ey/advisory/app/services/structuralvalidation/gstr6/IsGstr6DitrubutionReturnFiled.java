package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Ravindra V S
 *
 */
public class IsGstr6DitrubutionReturnFiled
		implements  BusinessRuleValidator<Gstr6DistributionExcelEntity> {
	


	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String gstin = document.getIsdGstin();
		String taxPeriod = document.getRetPeriod();
		
		EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder.getBean("EhcacheGstinTaxperiod",
				EhcacheGstinTaxperiod.class);
		
		GstrReturnStatusEntity entity = ehcachegstinTaxPeriod.isGstinFiled(gstin,
				taxPeriod, "GSTR6", "FILED", TenantContext.getTenantId());
		
		if(entity != null){
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
						"GSTR6 for selected tax period  is already filed", location));
		}
		
		return errors;
	}

}
