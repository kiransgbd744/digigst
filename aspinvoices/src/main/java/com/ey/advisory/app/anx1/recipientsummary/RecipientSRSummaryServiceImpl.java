/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 * 
 *         This Service is used to Summarize The Recipient Summary Data at
 *         various Levels such as Invoice Type,DocType and CGSTIN, CPan we are
 *         using Dao Data to Group in Certain Levels and Returning Summarize
 *         Data.
 *
 */
@Slf4j
@Component("RecipientSRSummaryServiceImpl")
public class RecipientSRSummaryServiceImpl
		implements RecipientSRSummaryService {

	@Autowired
	@Qualifier("RecipientSRSummaryDaoImpl")
	RecipientSRSummaryDao recipientSRSummaryDao;

	@Autowired
	@Qualifier("RecipientDaoImpl")
	RecipientDao recipientService;

	/**
	 * Gets a reducing collector for the specified level. The only difference
	 * between these collectors is the identity object. The identity object will
	 * be set to appropriate level, before the reduction process.
	 *
	 * @param level
	 *            Either L1 or L2
	 * @return The collector capable of reducing a stream of L3 objects to an L2
	 *         object OR a stream of L2 objects to an L1 object.
	 */
	private Collector<RecipientSRSummaryDto, ?, RecipientSRSummaryDto> getCollectorForLevel(
			String level) {
		return Collectors.reducing(new RecipientSRSummaryDto(level),
				(o1, o2) -> add(o1, o2));
	}

	@Override
	public List<RecipientSRSummaryDto> getAnx1SRSummary(
			RecipientSummaryRequestDto recepientSummaryRequestDto,
			String validQuery) {

		/*
		 * if gstin is not provided then the gstins will be loaded from the user
		 * context
		 */
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(
						Arrays.asList(recepientSummaryRequestDto.getEntityId()),
						outwardSecurityAttributeMap);
		setDataSecurityAttributes(recepientSummaryRequestDto,
				dataSecurityAttrMap);
		validQuery = queryCondition(recepientSummaryRequestDto);
		if (CollectionUtils.isEmpty(recepientSummaryRequestDto.getGstins()))
			throw new AppException("User Does not have any gstin");

		// Get the Level 3 collection from the DAO. This collection contains the
		// table section/doc type level information.

		List<RecipientSRSummaryDto> l3List = recipientSRSummaryDao
				.getAllRecipientSRSummary(recepientSummaryRequestDto,
						validQuery);

		if (LOGGER.isDebugEnabled()) {
			String msg = " Data From DAO layer has been Picked and returned in"
					+ " LEVEL 3 Format that is Grouping By DocType";

			LOGGER.debug(msg);
		}

		// Create an L2 Level collection at GSTIN level, by summing up elements
		// in the above Level 3 collection.
		Collection<RecipientSRSummaryDto> l2List = l3List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getCgstin(),
						getCollectorForLevel("L2")))
				.values();

		if (LOGGER.isDebugEnabled()) {
			String msg = " Grouping is Done on CGSTIN on L3 Level Data"
					+ " to Get Data In L2 Format";

			LOGGER.debug(msg);
		}

		// Create an L1 level collection at PAN level, by summing up elements
		// in the above Level 2 collection.
		Collection<RecipientSRSummaryDto> l1List = l2List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getCPan(),
						getCollectorForLevel("L1")))
				.values();

		List<String> cPanList = l1List.stream().map(o -> o.getCPan()).distinct()
				.collect(Collectors.toList());

		Map<String, String> cPanNameMap = recipientService
				.getCNamesForCPans(cPanList);

		l1List.forEach(o -> o.setCName(!cPanNameMap.containsKey(o.getCPan())
				? "NA" : cPanNameMap.get(o.getCPan())));

		if (LOGGER.isDebugEnabled()) {

			String msg = " Grouping is Done on CPan on L3 Level Data"
					+ " to Get Data In L1 Format";
			LOGGER.debug(msg);
		}

		// Merge all the Level 3, Level 2 and Level 1 collections
		// into a single list.
		List<RecipientSRSummaryDto> list = new ImmutableList.Builder<RecipientSRSummaryDto>()
				.addAll(l1List).addAll(l2List).addAll(l3List).build();
		// Sort the above list by a key that orders the elements in a
		// hierarchical manner.
		return list.stream().sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());

	}

	/**
	 * This function adds 2 Recipient Summary DTOs together. The first Dto
	 * should be at the same or higher level than the second Dto. This function
	 * is meant to be used as an accumulator OR combiner in a reducing
	 * operation. Depending on the level of the first Dto, the result Dto will
	 * have the CGSTIN, Table Type and Doc Type as null or a valid non-null
	 * value. For Level 3 objects, the Table Type and Doc Type will have non
	 * null values. For Level 2 and Level 1 objects, the table type and doc type
	 * will have null values. CGSTIN will be present for Level 2 objects and
	 * Level 3 objects, but not for Level 1 objects.
	 *
	 *
	 * @param cpt1
	 *            The object that represents the accumulated result in the
	 *            reduction process.
	 * @param cpt2
	 *            The object that needs to be added to the accumulated result.
	 * @return The sum of the above two parameter objects. This object will be
	 *         at the same level of the first parameter object.
	 */
	private RecipientSRSummaryDto add(RecipientSRSummaryDto cpt1,
			RecipientSRSummaryDto cpt2) {
		String cgstin = "L1".equals(cpt1.getLevel()) ? null : cpt2.getCgstin();
		String taxDocType = "L3".equals(cpt1.getLevel()) ? cpt2.getTableType()
				: null;
		String docType = "L3".equals(cpt1.getLevel()) ? cpt2.getDocType()
				: null;

		if ("L3".equalsIgnoreCase(cpt2.getLevel())
				&& ("CR".equalsIgnoreCase(cpt2.getDocType()) || 
						"RCR".equalsIgnoreCase(cpt2.getDocType()))) {
			return new RecipientSRSummaryDto(cpt2.getCPan(), cpt2.getCName(),
					cgstin, taxDocType, docType,
					cpt1.getCount() + cpt2.getCount(),
					cpt1.getTaxableVal().subtract(cpt2.getTaxableVal()),
					cpt1.getCess().subtract(cpt2.getCess()),
					cpt1.getIgst().subtract(cpt2.getIgst()),
					cpt1.getCgst().subtract(cpt2.getCgst()),
					cpt1.getSgst().subtract(cpt2.getSgst()),
					cpt1.getDocAmt().subtract(cpt2.getDocAmt()),
					cpt1.getLevel());
		}
		return new RecipientSRSummaryDto(cpt2.getCPan(), cpt2.getCName(),
				cgstin, taxDocType, docType, cpt1.getCount() + cpt2.getCount(),
				cpt1.getTaxableVal().add(cpt2.getTaxableVal()),
				cpt1.getCess().add(cpt2.getCess()),
				cpt1.getIgst().add(cpt2.getIgst()),
				cpt1.getCgst().add(cpt2.getCgst()),
				cpt1.getSgst().add(cpt2.getSgst()),
				cpt1.getDocAmt().add(cpt2.getDocAmt()), cpt1.getLevel());
	}

	private RecipientSummaryRequestDto setDataSecurityAttributes(
			RecipientSummaryRequestDto requestRecipientSummary,
			Map<String, List<String>> dataSecurityAttrMap) {
		if (CollectionUtils.isEmpty(requestRecipientSummary.getGstins())) {
			requestRecipientSummary.setGstins(
					dataSecurityAttrMap.get(OnboardingConstant.GSTIN));
		}

		if (CollectionUtils
				.isEmpty(requestRecipientSummary.getProfitCentres())) {
			requestRecipientSummary.setProfitCentres(
					dataSecurityAttrMap.get(OnboardingConstant.PC));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getPlants())) {
			requestRecipientSummary.setPlants(
					dataSecurityAttrMap.get(OnboardingConstant.PLANT));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getDivisions())) {
			requestRecipientSummary.setDivisions(
					dataSecurityAttrMap.get(OnboardingConstant.DIVISION));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getLocations())) {
			requestRecipientSummary.setLocations(
					dataSecurityAttrMap.get(OnboardingConstant.LOCATION));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getSalesOrgs())) {
			requestRecipientSummary.setSalesOrgs(
					dataSecurityAttrMap.get(OnboardingConstant.SO));
		}

		if (CollectionUtils
				.isEmpty(requestRecipientSummary.getPurchaseOrgs())) {
			requestRecipientSummary.setPurchaseOrgs(
					dataSecurityAttrMap.get(OnboardingConstant.PO));
		}

		if (CollectionUtils
				.isEmpty(requestRecipientSummary.getDistributionChannels())) {
			requestRecipientSummary.setDistributionChannels(
					dataSecurityAttrMap.get(OnboardingConstant.DC));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getUserAccess1())) {
			requestRecipientSummary.setUserAccess1(
					dataSecurityAttrMap.get(OnboardingConstant.UD1));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getUserAccess2())) {
			requestRecipientSummary.setUserAccess2(
					dataSecurityAttrMap.get(OnboardingConstant.UD2));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getUserAccess3())) {
			requestRecipientSummary.setUserAccess3(
					dataSecurityAttrMap.get(OnboardingConstant.UD3));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getUserAccess4())) {
			requestRecipientSummary.setUserAccess4(
					dataSecurityAttrMap.get(OnboardingConstant.UD4));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getUserAccess5())) {
			requestRecipientSummary.setUserAccess5(
					dataSecurityAttrMap.get(OnboardingConstant.UD5));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getUserAccess6())) {
			requestRecipientSummary.setUserAccess6(
					dataSecurityAttrMap.get(OnboardingConstant.UD6));
		}
		return requestRecipientSummary;

	}

	// refactoring required put this method in generic class it is used in
	// many services
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
			condition1.append(
					" AND \"PURCHASE_ORGANIZATION\" in " + "(:purchaseOrgs)");

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
