package com.ey.advisory.app.services.validation.gstr1a.NilNonExpt;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1ANilNonExptSupplierGstin implements
		B2csBusinessRuleValidator<Gstr1ANilNonExemptedAsEnteredEntity> {
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Override
	public List<ProcessingResult> validate(
			Gstr1ANilNonExemptedAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			gstinInfoRepository = StaticContextHolder.getBean(
					"GSTNDetailRepository", GSTNDetailRepository.class);
			String gstin = document.getSgstin();
			int n = gstinInfoRepository.findgstin(gstin);
			if (n <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5602",
						"Supplier GSTIN is not as per On-Boarding data.",
						location));
			}
		}
		return errors;
	}
}
