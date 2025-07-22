package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class LUSgstinValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;
	
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;
		if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;

		String paramkryId = GSTConstants.I5;

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);

		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		
		if (paramtrvalue == null || paramtrvalue.isEmpty() 
				|| paramtrvalue.equalsIgnoreCase(GSTConstants.C)) {
			if (document.getSgstin() != null
					&& !document.getSgstin().isEmpty()) {
				if (document.getSgstin().length() == 15) {
					String sgstin = document.getSgstin().substring(0, 2);
					stateCache = StaticContextHolder.
							getBean("DefaultStateCache",StateCache.class);
					int n=stateCache.findStateCode(sgstin);
						if(n<=0){

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGSTIN);
						TransDocProcessingResultLoc location 
						                 = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER1052", "Invalid Supplier GSTIN.", location));
					}
				}
			}
		}
		return errors;
	}

}
