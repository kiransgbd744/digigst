package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Reddy
 *
 */
@Component("GSTR7TransOriginalTaxableValue")
public class GSTR7TransOriginalTaxableValue
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.RNV);

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;

		if (!DOCTYPE.contains(document.getDocType()))
			return errors;

		if (GSTConstants.RNV.equalsIgnoreCase(document.getDocType())) {
			BigDecimal originalTaxableValue = document
					.getOriginalTaxableValue();
			if (originalTaxableValue == null) {
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.ORIGINAL_TAXABLE_VALUE);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER63035",
						"Original Taxable Value cannot be left blank",
						location));
			}
		}
		return errors;
	}
}