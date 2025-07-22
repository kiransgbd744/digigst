package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
public class Gstr6EligibilityIndicatorMatchValidator
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {
	
	private Gstr6DistributionRepository repo;

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		repo = StaticContextHolder.getBean("Gstr6DistributionRepository",
				Gstr6DistributionRepository.class);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6EligibilityIndicatorMatchValidator document Details");
			LOGGER.debug("Recepient GSTIN " + document.getCustGstin());
			LOGGER.debug("ISD GSTIN " + document.getIsdGstin());
			LOGGER.debug("Org Doc Num " + document.getOrgDocNum() != null
					|| !document.getOrgDocNum().isEmpty()
							? document.getOrgDocNum() : null);
			LOGGER.debug("Return Period " + document.getRetPeriod().toString());
			LOGGER.debug("Org Doc Date " + document.getOrgDocDate() != null
					? document.getOrgDocDate().toString() : null);
			LOGGER.debug("Document Type " + document.getDocType() != null
					|| !document.getDocType().isEmpty() ? document.getDocType()
							: null);

		}

		try {
			if ("CR".contains(document.getDocType().toUpperCase())) {
				String previousTaxPeriod = taxPeriodCalculator(
						document.getRetPeriod());
				LOGGER.debug("Previous Period " + previousTaxPeriod);
				if (previousTaxPeriod != null) {
					Integer startPeriod = GenUtil
							.getDerivedTaxPeriod(document.getRetPeriod());
					LOGGER.debug("start Period " + startPeriod.toString());
					Integer endPeriod = GenUtil
							.getDerivedTaxPeriod(previousTaxPeriod);
					LOGGER.debug("end Period " + endPeriod.toString());
					List<Object[]> eligibilityIndicators = null;
					if (endPeriod != null && startPeriod != null
							&& (document.getCustGstin() != null
									|| !document.getCustGstin().isEmpty())
							&& (document.getOrgDocNum() != null
									|| !document.getOrgDocNum().isEmpty())
							&& (document.getOrgDocDate() != null)) {
						eligibilityIndicators = repo.getEligibilityIndicators(
								endPeriod, startPeriod, document.getIsdGstin(),
								document.getOrgDocNum(),
								document.getOrgDocDate());
					}

					if (LOGGER.isDebugEnabled()) {
						for (Object[] doc : eligibilityIndicators) {
							LOGGER.debug(
									"Gstr6EligibilityIndicatorMatchValidator data from repo");
							if (doc[0] != null) {
								LOGGER.debug(doc[0].toString());
							} else if (doc[1] != null) {
								LOGGER.debug(doc[1].toString());
							}

						}
					}
					if (eligibilityIndicators != null) {

						LOGGER.debug(
								"Gstr6EligibilityIndicatorMatchValidator data is not empty");
						for (Object[] obj : eligibilityIndicators) {
							if (obj != null && obj.length > 1) {
								String eligibilityIndicator = obj[0] != null
										? (String) obj[0] : null;
								String documentType = obj[1] != null
										? (String) obj[1] : null;
								if (eligibilityIndicator != null
										&& documentType != null) {
									if (!eligibilityIndicator.equalsIgnoreCase(
											document.getEligibleIndicator())
											&& "INV".equalsIgnoreCase(
													documentType)) {
										Set<String> errorLocations = new HashSet<>();
										errorLocations.add(
												GSTConstants.ELIGIBILE_INDICATOR);

										TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
												null, errorLocations.toArray());
										errors.add(new ProcessingResult(
												APP_VALIDATION, "ER1278",
												"Eligibility Indicator is not matching with the Eligibility Indicator of the Original Document. Please re-check.",
												location));
									}
								}

							}
						}
					
					}
				}
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Error occurred during Gstr6EligibilityIndicatorMatchValidator validation: "
							+ ex.getMessage(),
					ex);
		}

		return errors;
	}

	public String taxPeriodCalculator(String taxPeriod) {
		if (taxPeriod != null && taxPeriod.length() >= 4) {
			int currentMonth = Integer.parseInt(taxPeriod.substring(0, 2));
			int currentYear = Integer.parseInt(taxPeriod.substring(2));

			int monthsToSubtract = 18;
			int remainingMonths = currentMonth - monthsToSubtract;
			int subtractedYear = currentYear;

			while (remainingMonths <= 0) {
				subtractedYear--;
				remainingMonths += 12;
			}
			return String.format("%02d%04d", remainingMonths, subtractedYear);
		} else {
			return null;
		}
	}

}
