/*package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.SGSTIN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.entities.client.MasterProductEntity;
import com.ey.advisory.admin.data.repositories.client.EligibilityIndicatorRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.businessvalidation.table4.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;


	public class EligibilityIndicator
	implements BusinessRuleValidator<MasterItemEntity> {
@Autowired
@Qualifier("EligibilityIndicatorRepository")
private EligibilityIndicatorRepository eligibilityIndicatorRepository;

@Override
public List<ProcessingResult> validate(MasterItemEntity document,
		ProcessingContext context) {
	List<ProcessingResult> errors = new ArrayList<>();
	if (document.getGstinPan() != null && !document.getGstinPan().isEmpty()
			&& document.getGstinPan().length() == 15) {
		eligibilityIndicatorRepository = StaticContextHolder.getBean(
				"EligibilityIndicatorRepository", EligibilityIndicatorRepository.class);
		String gstin = document.getGstinPan();
		List<GSTNDetailEntity> out = eligibilityIndicatorRepository.findByGstin(gstin);

		if (out == null || out.isEmpty()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(SGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1613",
					"GSTIN/PAN is not as per On-Boarding data", location));
		}
	}
	return errors;
}
}

}
*/