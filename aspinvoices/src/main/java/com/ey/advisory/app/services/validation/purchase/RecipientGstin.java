package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
/**
 * 
 * @author Siva.Nandam
 *
 */
public class RecipientGstin implements DocRulesValidator<InwardTransDocument>{

	
	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		ehcachegstin = StaticContextHolder.
				getBean("Ehcachegstin",Ehcachegstin.class);
		
if(document.getCgstin()!=null && !document.getCgstin().isEmpty()){
	String groupCode = TenantContext.getTenantId();
	//	int n=gstinInfoRepository.findgstin(document.getCgstin());
	GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
			document.getCgstin());
		if(gstin==null){
		
			errorLocations.add(GSTConstants.RecipientGSTIN);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1036",
					" Recipient GSTIN is not as per On-Boarding data",
					location));
		}
}
		return errors;
	}
}