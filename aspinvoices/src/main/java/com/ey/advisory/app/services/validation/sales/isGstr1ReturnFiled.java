package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Ravindra V S
 *
 */
public class isGstr1ReturnFiled
		implements DocRulesValidator<OutwardTransDocument> {
	
	private static final String PIPE = "|";
	
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
	
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
	
		DocKeyGenerator documentKeyBuilder = StaticContextHolder
			.getBean("DefaultOutwardTransDocKeyGenerator",
					DocKeyGenerator.class);
	
		Map<String,List<String>> filedSet = (Map<String, List<String>>) context.getAttribute("freezeMap");
		String key = documentKeyBuilder.generateKey(document).toString();
		if (filedSet.containsKey(key)) {
				errorLocations.add(GSTConstants.DOC_NO);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				
				List<String> errorsList = filedSet.get(key);
				
				
				if(errorsList.get(0).equalsIgnoreCase("GSTR1 for this tax period is already filed"))
				{
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
						"GSTR1 for this tax period is already filed", location));
				}else
				{
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1277",
						errorsList.get(0), location));
				
				}
				
				
				document.setDeleted(true);
		}
		
		return errors;
	}

}
