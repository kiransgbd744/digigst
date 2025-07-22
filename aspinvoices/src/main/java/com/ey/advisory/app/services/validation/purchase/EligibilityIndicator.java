package com.ey.advisory.app.services.validation.purchase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.*;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * 
 * @author Siva.Nandam
 *
 */

public class EligibilityIndicator
		implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> EligibilityIndicator = ImmutableList
			.of("IG", "CG", "IS", "NO");

	private static final List<String> EligibilityIndicators = ImmutableList
			.of("IG", "CG", "IS");

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		List<InwardTransDocLineItem> items = document.getLineItems();

		String paramkryId = GSTConstants.I53;
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		/*if (paramtrvalue == null || paramtrvalue.isEmpty())
			return errors;*/

		LocalDate docDate = document.getDocDate();
		String docFy = (docDate != null) ? GenUtil.getFinYear(docDate) : null;
		int docFiscalYear = (docFy != null)
				? Integer.parseInt(docFy.substring(0, 4)) : 0;
		int derivedTaxPeriod = document.getDerivedTaxperiod();

		String currentFy = GenUtil.getCurrentFinancialYear();
		int currentFiscalYear = Integer.parseInt(currentFy.substring(0, 4));
		int nextFiscalYear = docFiscalYear + 1;
		int octoberOfNextFY = (nextFiscalYear) * 100 + 10;

		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			if (item.getEligibilityIndicator() != null
					&& !item.getEligibilityIndicator().isEmpty()) {
				item.setEligibilityIndicator(
						item.getEligibilityIndicator().toUpperCase());
				if (!EligibilityIndicator.contains(trimAndConvToUpperCase(
						item.getEligibilityIndicator()))) {
					errorLocations.add(eligibiltIndicator);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1101",
							"Invalid ITC Eligibility Indicator", location));
				} else if (("ADV".equalsIgnoreCase(document.getDocType())
						|| "ADJ".equalsIgnoreCase(document.getDocType()))
						&& (!"NO".equalsIgnoreCase(
								item.getEligibilityIndicator()))) {
					errorLocations.add(eligibiltIndicator);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1312",
							"Eligibility Indicator should be only NO in case of RCM applicable advance records",
							location));

				} else if ((GSTConstants.A.equalsIgnoreCase(paramtrvalue))
						&& (EligibilityIndicators
								.contains(trimAndConvToUpperCase(
										item.getEligibilityIndicator())))
						&& ((docFiscalYear < currentFiscalYear)
								&& (derivedTaxPeriod > octoberOfNextFY))) {
					errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1313",
							"ITC is time barred hence cannot be availed",
							location));
				}
			}

		});
		return errors;
	}

}