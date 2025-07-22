package com.ey.advisory.app.services.businessvalidation.interestandlatefee;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.master.ReturnTableRepository;
import com.ey.advisory.app.data.entities.client.InterestExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
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
public class InterestReturnTable
		implements BusinessRuleValidator<InterestExcelEntity> {
	@Autowired
	@Qualifier("ReturnTableRepository")
	private ReturnTableRepository rtatecodeRepository;
	
	@Override
	public List<ProcessingResult> validate(InterestExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		rtatecodeRepository = StaticContextHolder
				.getBean("ReturnTableRepository", ReturnTableRepository.class);
		if (document.getReturnTable() != null
				&& !document.getReturnTable().isEmpty()) {
			if (document.getReturnType() != null
					&& !document.getReturnType().isEmpty()) {
				String returnTable = document.getReturnTable().trim();
				String retType = document.getReturnType().trim();
				int n = rtatecodeRepository.
				findReturnTableValue(returnTable,retType,
						GSTConstants.INTEREST_LATEFEE);
				if (n <= 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RETURN_TABLE);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1846",
							"Invalid Return Table.", location));
					return errors;
				}
			}
		}
		return errors;
	}
}