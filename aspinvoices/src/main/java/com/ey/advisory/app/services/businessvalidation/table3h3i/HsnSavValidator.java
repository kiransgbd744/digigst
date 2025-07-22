package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author mahesh
 *
 */
public class HsnSavValidator implements 
                           BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	/*@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;*/
	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;
	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document, 
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		
			
		/*	hsnOrSacRepository = StaticContextHolder.
					getBean("hsnOrSacRepository",HsnOrSacRepository.class);*/
			if(document.getHsn()!=null && !document.getHsn().isEmpty()){
			String hsnOrSac  =	document.getHsn();
				/*int i=hsnOrSacRepository.findcountByHsnOrSac(hsnOrSac);*/
			hsnCache = StaticContextHolder.getBean(
					"DefaultHsnCache", HsnCache.class);
			
				int i = hsnCache
						.findhsn(hsnOrSac);
				
				if(!ValidatorUtil.isEvenNumber(document.getHsn().length()) 
						|| i<=0){
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
					TransDocProcessingResultLoc location = 
							                  new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1219",
							"Invalid HSN or SAC code",
							location));
				}
				
			}
			
		
		return errors;
	}

}
