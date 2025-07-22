package com.ey.advisory.controllers.anexure1;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.anx1.recipientsummary.RecipientSummaryRequestDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.gstr2.userdetails.EntityServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Anx1RecipientSummaryValidator")
public class Anx1RecipientSummaryValidator {

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityServiceImpl entityService;

	public void recipientValidator(RecipientSummaryRequestDto req) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Anx1RecipientSummaryValidation"
					+ ".recipientValidator ";
			LOGGER.debug(msg);
		}

		String msg = null;

		if (req.getEntityId() == null) {
			msg = "EntityId Can not be empty";
			throw new AppException(msg);
		} else if (StringUtils.isBlank(req.getTaxPeriod())) {
			msg = "Tax period can not be empty.";
			throw new AppException(msg);
		}
	}

	public static String queryCondition(RecipientSummaryRequestDto req) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Anx1RecipientSummaryValidation"
					+ ".queryCondition ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (!(req.getStartDocDate() == null && req.getEndDocDate() == null)) {
			condition1.append(" AND \"DOC_DATE\" BETWEEN :startDocDate AND "
					+ ":docEndDate");
		}
		if (!CollectionUtils.isEmpty(req.getProfitCentres())) {
			condition1.append(" AND \"PROFIT_CENTRE\" in (:profitCenters)");
		}

		if (!CollectionUtils.isEmpty(req.getPlants())) {
			condition1.append(" AND \"PLANT_CODE\" in (:plants)");
		}

		if (!CollectionUtils.isEmpty(req.getDivisions())) {
			condition1.append(" AND \"DIVISION\" in (:divisions)");
		}

		if (!CollectionUtils.isEmpty(req.getLocations())) {
			condition1.append(" AND \"LOCATION\" in (:locations)");
		}

		if (!CollectionUtils.isEmpty(req.getSalesOrgs())) {
			condition1.append(
					" AND \"SALES_ORGANIZATION\" in " + "(:salesOrganization)");
		}

		if (!CollectionUtils.isEmpty(req.getDistributionChannels())) {
			condition1.append(" AND \"DISTRIBUTION_CHANNEL\" in "
					+ "(:distributionChannels)");

		}
		if (!CollectionUtils.isEmpty(req.getPurchaseOrgs())) {
			condition1.append(" AND \"PURCHASE_ORGANIZATION\" in "
					+ "(:purchaseOrgs)");

		}

		if (!CollectionUtils.isEmpty(req.getUserAccess1())) {
			condition1.append(" AND \"USERACCESS1\" in " + "(:userAccess1)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess2())) {
			condition1.append(" AND \"USERACCESS2\" in " + "(:userAccess2)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess3())) {
			condition1.append(" AND \"USERACCESS3\" in " + "(:userAccess3)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess4())) {
			condition1.append(" AND \"USERACCESS4\" in " + "(:userAccess4)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess5())) {
			condition1.append(" AND \"USERACCESS5\" in " + "(:userAccess5)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess6())) {
			condition1.append(" AND \"USERACCESS6\" in " + "(:userAccess6)");
		}

		return condition1.toString();
	}
}
