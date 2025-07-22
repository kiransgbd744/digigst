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
public class Gstr6RgstinValidation
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	private Gstr6DistributionRepository repo;

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {

			repo = StaticContextHolder.getBean("Gstr6DistributionRepository",
					Gstr6DistributionRepository.class);
	
		List<ProcessingResult> errors = new ArrayList<>();
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
				String previousTaxPriod = taxPeriodCalculator(
						document.getRetPeriod());
				LOGGER.debug("Previous Period " + previousTaxPriod);
				if (previousTaxPriod != null) {
					Integer startPeriod = GenUtil
							.getDerivedTaxPeriod(document.getRetPeriod());
					LOGGER.debug("start Period " + startPeriod.toString());
					Integer endPeriod = GenUtil
							.getDerivedTaxPeriod(previousTaxPriod);
					LOGGER.debug("end Period " + endPeriod.toString());
					List<Object[]> data = null;
					if (endPeriod != null && startPeriod != null
							&& (document.getCustGstin() != null
									|| !document.getCustGstin().isEmpty())
							&& (document.getOrgDocNum() != null
									|| !document.getOrgDocNum().isEmpty())
							&& (document.getOrgDocDate() != null)) {
						data = repo.getDistributionData(endPeriod, startPeriod,
								document.getIsdGstin(),
								document.getOrgDocNum(),
								document.getOrgDocDate());
					}
					if (LOGGER.isDebugEnabled()) {
						for (Object[] doc : data) {
							LOGGER.debug(
									"Gstr6RgstinValidation data from repo");
							if (doc[1] != null) {
								LOGGER.debug(doc[1].toString());
							} else if (doc[4] != null) {
								LOGGER.debug(doc[4].toString());
							}
						}
					}
					if (data != null) {

						LOGGER.debug(
								"Gstr6RgstinValidation data is not empty");
						for (Object[] row : data) {
							if (row != null && row.length > 1) {
								String documentType = row[1] != null
										? (String) row[1] : null;
								String recipientGstin = row[4] != null
										? (String) row[4] : null;
								if (documentType != null
										&& recipientGstin != null) {
									if (!recipientGstin.equalsIgnoreCase(
											document.getCustGstin())
											&& "INV".equals(documentType)) {
										Set<String> errorLocations = new HashSet<>();
										errorLocations.add(
												GSTConstants.RecipientGSTIN);
										TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
												null, errorLocations.toArray());
										errors.add(new ProcessingResult(
												APP_VALIDATION, "ER1280",
												"Recipient GSTIN is not matching with Recipient GSTIN of Original Document. Please re-check.",
												location));
									}
								}

							}
						}
					
					} else {}
				}
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Error occurred during Gstr6RgstinValidation validation: "
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
