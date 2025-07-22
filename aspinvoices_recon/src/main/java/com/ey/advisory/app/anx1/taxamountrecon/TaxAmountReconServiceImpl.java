package com.ey.advisory.app.anx1.taxamountrecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 **/

@Slf4j
@Service("TaxAmountReconService")
public class TaxAmountReconServiceImpl implements TaxAmountReconService {

	@Autowired
	@Qualifier("TaxAmountReconDaoImpl")
	TaxAmountReconDaoImpl taxAmountReconDaoImpl;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	public Pair<List<TaxAmountReconDto>, TaxAmountReconRet1Dto> getAllTaxAmountRecon(
			TaxAmountReconRequestDto reqDto) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin TaxAmountReconServiceImpl"
					+ ".getAllTaxAmountRecon ,calling TaxAmountReconDao"
					+ "to get List<TaxAmountReconDto>";
			LOGGER.debug(msg);
		}
		
		 Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		  Map<String, String> outwardSecurityAttributeMap =
		  DataSecurityAttributeUtil .getOutwardSecurityAttributeMap();
		  dataSecurityAttrMap = DataSecurityAttributeUtil
		  .dataSecurityAttrMapForQuery( Arrays.asList(reqDto.getEntityId()),
		  outwardSecurityAttributeMap);
		  
		  setDataSecurityAttributes(reqDto, dataSecurityAttrMap); if
		  (CollectionUtils.isEmpty(reqDto.getGstins())) throw new
		  AppException("User Does not have any gstin");


		List<String> reqGstnsList = new ArrayList<String>(reqDto.getGstins());

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Gstin request for Tax Recon  : %s",
					reqGstnsList);
			LOGGER.debug(msg);
		}

		
		 
		 

		List<TaxAmountReconDto> result = taxAmountReconDaoImpl
				.getAllTaxAmountReconDetails(reqDto);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Result after running query : %s",result);
			LOGGER.debug(msg);
		}
		
		List<String> resultgstnList = result.stream().map(o -> o.getGstin())
				.collect(Collectors.toList());
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Result gstin : %s",resultgstnList);
			LOGGER.debug(msg);
		}

		reqGstnsList.removeAll(resultgstnList);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"After filtering gstin : %s",reqGstnsList);
			LOGGER.debug(msg);
		}

		List<TaxAmountReconDto> listDto = reqGstnsList.stream()
				.map(o -> new TaxAmountReconDto(o))
				.collect(Collectors.toList());
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"List gstin : %s",listDto);
			LOGGER.debug(msg);
		}

		result.addAll(listDto);


		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Query result for TaxAmountReconServiceImpl : %s", result);
			LOGGER.debug(msg);
		}

		Map<String, String> authTokenStatusMap = authTokenService
				.getAuthTokenStatusForGstins(reqDto.getGstins());

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Auth Token Status for TaxAmountReconServiceImpl : %s",
					authTokenStatusMap.toString());
			LOGGER.debug(msg);
		}

		Map<String, String> stateNamesMap = entityService
				.getStateNames(reqDto.getGstins());

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"StateName for TaxAmountReconServiceImpl : %s",
					stateNamesMap.toString());
			LOGGER.debug(msg);
		}

		result.stream().forEach(obj -> {
			obj.setAuthTokenStatus(authTokenStatusMap.get(obj.getGstin()));
			obj.setStateName(stateNamesMap.get(obj.getGstin()));
		});

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Result for TaxAmountReconServiceImpl : %s", result);
			LOGGER.debug(msg);
		}

		BigDecimal retTotalIgst = BigDecimal.ZERO;
		BigDecimal retTotalCgst = BigDecimal.ZERO;
		BigDecimal retTotalSgst = BigDecimal.ZERO;

		for (TaxAmountReconDto dto : result) {

			BigDecimal retIgst = dto.getUpdIgstAmt()
					.subtract(dto.getMemoIgstAmt());
			if (retIgst.compareTo(BigDecimal.ZERO) < 0) {
				retIgst = BigDecimal.ZERO;
			}
			retTotalIgst = retTotalIgst.add(retIgst);
			dto.setRetIgstAmt(retIgst);

			BigDecimal retCgst = dto.getUpdCgstAmt()
					.subtract(dto.getMemoCgstAmt());
			if (retCgst.compareTo(BigDecimal.ZERO) < 0) {
				retCgst = BigDecimal.ZERO;
			}
			retTotalCgst = retTotalCgst.add(retCgst);
			dto.setRetCgstAmt(retCgst);

			BigDecimal retsgst = dto.getUpdSgstAmt()
					.subtract(dto.getMemoSgstAmt());
			if (retsgst.compareTo(BigDecimal.ZERO) < 0) {
				retsgst = BigDecimal.ZERO;
			}
			retTotalSgst = retTotalSgst.add(retsgst);
			dto.setRetSgstAmt(retsgst);
		}

		TaxAmountReconRet1Dto ret1Total = new TaxAmountReconRet1Dto();
		ret1Total.setRetTotalCgstAmt(retTotalCgst);
		ret1Total.setRetTotalIgstAmt(retTotalIgst);
		ret1Total.setRetTotalSgstAmt(retTotalSgst);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Response result for TaxAmountRecon: %s",
					result);
			LOGGER.debug(msg);
		}

		return new Pair<>(result, ret1Total);

	}

	private TaxAmountReconRequestDto setDataSecurityAttributes(
			TaxAmountReconRequestDto request,
			Map<String, List<String>> dataSecurityAttrMap) {
		if (CollectionUtils.isEmpty(request.getGstins())) {
			request.setGstins(
					dataSecurityAttrMap.get(OnboardingConstant.GSTIN));
		}

		if (CollectionUtils.isEmpty(request.getProfitCentres())) {
			request.setProfitCentres(
					dataSecurityAttrMap.get(OnboardingConstant.PC));
		}

		if (CollectionUtils.isEmpty(request.getPlants())) {
			request.setPlants(
					dataSecurityAttrMap.get(OnboardingConstant.PLANT));
		}

		if (CollectionUtils.isEmpty(request.getDivisions())) {
			request.setDivisions(
					dataSecurityAttrMap.get(OnboardingConstant.DIVISION));
		}

		if (CollectionUtils.isEmpty(request.getLocations())) {
			request.setLocations(
					dataSecurityAttrMap.get(OnboardingConstant.LOCATION));
		}

		if (CollectionUtils.isEmpty(request.getSalesOrgs())) {
			request.setSalesOrgs(
					dataSecurityAttrMap.get(OnboardingConstant.SO));
		}

		if (CollectionUtils.isEmpty(request.getPurchaseOrgs())) {
			request.setPurchaseOrgs(
					dataSecurityAttrMap.get(OnboardingConstant.PO));
		}

		if (CollectionUtils.isEmpty(request.getDistributionChannels())) {
			request.setDistributionChannels(
					dataSecurityAttrMap.get(OnboardingConstant.DC));
		}

		if (CollectionUtils.isEmpty(request.getUserAccess1())) {
			request.setUserAccess1(
					dataSecurityAttrMap.get(OnboardingConstant.UD1));
		}

		if (CollectionUtils.isEmpty(request.getUserAccess2())) {
			request.setUserAccess2(
					dataSecurityAttrMap.get(OnboardingConstant.UD2));
		}

		if (CollectionUtils.isEmpty(request.getUserAccess3())) {
			request.setUserAccess3(
					dataSecurityAttrMap.get(OnboardingConstant.UD3));
		}

		if (CollectionUtils.isEmpty(request.getUserAccess4())) {
			request.setUserAccess4(
					dataSecurityAttrMap.get(OnboardingConstant.UD4));
		}

		if (CollectionUtils.isEmpty(request.getUserAccess5())) {
			request.setUserAccess5(
					dataSecurityAttrMap.get(OnboardingConstant.UD5));
		}

		if (CollectionUtils.isEmpty(request.getUserAccess6())) {
			request.setUserAccess6(
					dataSecurityAttrMap.get(OnboardingConstant.UD6));
		}
		return request;

	}

}
