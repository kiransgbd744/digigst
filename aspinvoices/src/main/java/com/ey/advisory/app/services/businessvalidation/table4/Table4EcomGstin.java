package com.ey.advisory.app.services.businessvalidation.table4;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class Table4EcomGstin implements 
                                BusinessRuleValidator<OutwardTable4ExcelEntity> {
	
	/*@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository StatecodeRepository;*/
	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(OutwardTable4ExcelEntity document,
			ProcessingContext context) {
		
		
		List<ProcessingResult> errors = new ArrayList<>();

		/*StatecodeRepository = StaticContextHolder.getBean("StatecodeRepository",
				StatecodeRepository.class);*/
		if (document.getEcomGstin() != null
				&& !document.getEcomGstin().isEmpty()) {

			String stateCode = document.getEcomGstin().trim().substring(0, 2);
			if(stateCode.length()==1){
				stateCode=GSTConstants.ZERO+stateCode;
			}
			stateCache = StaticContextHolder.
					getBean("DefaultStateCache",StateCache.class);
			int n=stateCache.findStateCode(stateCode);
			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.E_ComGstin);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0222",
						"Invalid E-Com GSTIN.", location));
				return errors;
			}
		}
		return errors;
}}