package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.eligibiltIndicator;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

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

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6115EligibilityIndicator
		implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> EligibilityIndicator = ImmutableList
			.of("IS", "NO");
	private static final List<String> EligibilityIndicators = ImmutableList
			.of("IG", "CG", "IS");

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		// YYYYMM format for October of the next FY

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

				if (!EligibilityIndicator.contains(trimAndConvToUpperCase(
						item.getEligibilityIndicator()))) {
					errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1101",
							"Invalid ITC Eligibility Indicator", location));
				}else if ((GSTConstants.A.equalsIgnoreCase(paramtrvalue))
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

	public static void main(String[] args) {

		String docDate = "2022-12-17";
		LocalDate date = LocalDate.parse(docDate);
		//int docFiscalYear = date.getYear();
		int derivedTaxPeriod = 202301;

		String docFy = (docDate != null) ? GenUtil.getFinYear(date) : null;
		int docFiscalYear = (docFy != null)
				? Integer.parseInt(docFy.substring(0, 4)) : 0;
		//int derivedTaxPeriod = document.getDerivedTaxperiod();
		String currentFy = GenUtil.getCurrentFinancialYear();
	    int currentFiscalYear = Integer.parseInt(currentFy.substring(0, 4));		
       
		int nextFiscalYear = docFiscalYear + 1;

		int octoberOfNextFY = (nextFiscalYear) * 100 + 10;

		

		if (EligibilityIndicators.contains(trimAndConvToUpperCase("IS"))
				&& ((docFiscalYear < currentFiscalYear)
						&& (derivedTaxPeriod > octoberOfNextFY))) {

			System.out.println("ITC is time barred hence cannot be availed");
		} else {
			System.out.println("Process it");

		}
		// });
	}
}
