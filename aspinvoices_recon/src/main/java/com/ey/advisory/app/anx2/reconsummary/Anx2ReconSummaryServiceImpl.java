/**
 * 
 */
package com.ey.advisory.app.anx2.reconsummary;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/*
 * @author Nikhil.Duseja
 *
 *
 *
 */
@Slf4j
@Service("Anx2ReconSummaryServiceImpl")
public class Anx2ReconSummaryServiceImpl implements Anx2ReconSummaryService {

	@Autowired
	@Qualifier("Anx2ReconSummaryDaoImpl")
	Anx2ReconSummaryDao anx2ReconDaoImpl;

	@Override
	public List<Anx2ReconSummaryDto> getReconSummaryDetails(
			ReconSummaryReqDto reqDto, int taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Calling Anx2ReconSummaryDao Layer "
					+ "from Anx2ReconSummaryServiceImpl "
					+ " Where Request is %s", reqDto);
			LOGGER.debug(msg);
		}
		String validQuery = " ";
		/*
		 * DataSecurities are commited as per discussion with Saurabh Dhariwal
		 * and Anand Srinivasan as we cannot get datasecurity for reconsolidate
		 * removing it 
		 */
		
		/*Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(
						Arrays.asList(reqDto.getEntityId()),
						inwardSecurityAttributeMap);
		setDataSecurityAttributes(reqDto, dataSecurityAttrMap);
		validQuery = queryCondition(reqDto);*/

		return anx2ReconDaoImpl.findReconSummDetails(reqDto, taxPeriod,
				validQuery);
	}
	
	
	/*
	 * DataSecurities are commited as per discussion with Saurabh Dhariwal
	 * and Anand Srinivasan as we cannot get datasecurity for reconsolitate
	 * removing it 
	 */
	

	// refactoring required put this method in generic class it is used in
	// many services
	/*public static String queryCondition(ReconSummaryReqDto req) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Anx2ReconSummaryQueryConditon "
					+ ".queryCondition ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();
		*/
		/*if (!CollectionUtils.isEmpty(req.getProfitCentres())) {
			condition1.append(" AND DH.\"PROFIT_CENTRE\" in (:profitCenters)");
		}

		if (!CollectionUtils.isEmpty(req.getPlants())) {
			condition1.append(" AND DH.\"PLANT_CODE\" in (:plants)");
		}

		if (!CollectionUtils.isEmpty(req.getDivisions())) {
			condition1.append(" AND DH.\"DIVISION\" in (:divisions)");
		}

		if (!CollectionUtils.isEmpty(req.getLocations())) {
			condition1.append(" AND DH.\"LOCATION\" in (:locations)");
		}

		if (!CollectionUtils.isEmpty(req.getPurchaseOrgs())) {
			condition1.append(" AND DH.\"PURCHASE_ORGANIZATION\" in "
					+ "(:purchaseOrgs)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess1())) {
			condition1.append(" AND DH.\"USERACCESS1\" in " + "(:userAccess1)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess2())) {
			condition1.append(" AND DH.\"USERACCESS2\" in " + "(:userAccess2)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess3())) {
			condition1.append(" AND DH.\"USERACCESS3\" in " + "(:userAccess3)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess4())) {
			condition1.append(" AND DH.\"USERACCESS4\" in " + "(:userAccess4)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess5())) {
			condition1.append(" AND DH.\"USERACCESS5\" in " + "(:userAccess5)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess6())) {
			condition1.append(" AND DH.\"USERACCESS6\" in " + "(:userAccess6)");
		}

		return condition1.toString();*/
/*	}

	private void setDataSecurityAttributes(ReconSummaryReqDto reconReq,
			Map<String, List<String>> dataSecurityAttrMap) {*/
		/*if (CollectionUtils.isEmpty(reconReq.getGstins())) {
			reconReq.setGstins(
					dataSecurityAttrMap.get(OnboardingConstant.GSTIN));
		}

		if (CollectionUtils.isEmpty(reconReq.getProfitCentres())) {
			reconReq.setProfitCentres(
					dataSecurityAttrMap.get(OnboardingConstant.PC));
		}

		if (CollectionUtils.isEmpty(reconReq.getPlants())) {
			reconReq.setPlants(
					dataSecurityAttrMap.get(OnboardingConstant.PLANT));
		}

		if (CollectionUtils.isEmpty(reconReq.getDivisions())) {
			reconReq.setDivisions(
					dataSecurityAttrMap.get(OnboardingConstant.DIVISION));
		}

		if (CollectionUtils.isEmpty(reconReq.getLocations())) {
			reconReq.setLocations(
					dataSecurityAttrMap.get(OnboardingConstant.LOCATION));
		}

		if (CollectionUtils.isEmpty(reconReq.getPurchaseOrgs())) {
			reconReq.setPurchaseOrgs(
					dataSecurityAttrMap.get(OnboardingConstant.PO));
		}

		if (CollectionUtils.isEmpty(reconReq.getUserAccess1())) {
			reconReq.setUserAccess1(
					dataSecurityAttrMap.get(OnboardingConstant.UD1));
		}

		if (CollectionUtils.isEmpty(reconReq.getUserAccess2())) {
			reconReq.setUserAccess2(
					dataSecurityAttrMap.get(OnboardingConstant.UD2));
		}

		if (CollectionUtils.isEmpty(reconReq.getUserAccess3())) {
			reconReq.setUserAccess3(
					dataSecurityAttrMap.get(OnboardingConstant.UD3));
		}

		if (CollectionUtils.isEmpty(reconReq.getUserAccess4())) {
			reconReq.setUserAccess4(
					dataSecurityAttrMap.get(OnboardingConstant.UD4));
		}

		if (CollectionUtils.isEmpty(reconReq.getUserAccess5())) {
			reconReq.setUserAccess5(
					dataSecurityAttrMap.get(OnboardingConstant.UD5));
		}

		if (CollectionUtils.isEmpty(reconReq.getUserAccess6())) {
			reconReq.setUserAccess6(
					dataSecurityAttrMap.get(OnboardingConstant.UD6));
		}
	}*/

}
