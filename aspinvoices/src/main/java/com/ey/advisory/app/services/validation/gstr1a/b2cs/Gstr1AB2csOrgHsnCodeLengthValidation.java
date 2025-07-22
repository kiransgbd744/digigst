package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.service.GstnApi;
import com.google.common.base.Strings;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

public class Gstr1AB2csOrgHsnCodeLengthValidation
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {
	@Autowired
	@Qualifier("GstnApi")
	private GstnApi gstnApi;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		gstnApi = StaticContextHolder.getBean("GstnApi", GstnApi.class);

		String hsnCode = document.getOrgHsnOrSac();
		if (!Strings.isNullOrEmpty(hsnCode)
				&& gstnApi.isRateIncludedInHsn(document.getReturnPeriod())
				&& hsnCode.length() < 6) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.OrgHsnOrSac);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION,
					ProcessingResultType.INFO, "IN0516",
					"Minimum 6 digits Org HSN code should be provided",
					location));
		}

		return errors;

	}

}
