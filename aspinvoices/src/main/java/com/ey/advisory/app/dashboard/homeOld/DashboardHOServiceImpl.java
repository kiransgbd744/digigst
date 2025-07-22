package com.ey.advisory.app.dashboard.homeOld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

/**
 * 
 * @author mohit.basak
 *
 */
@Component("DashboardHOServiceImpl")
public class DashboardHOServiceImpl implements DashboardHOService {

	@Autowired
	@Qualifier("DashboardHODaoImpl")
	private DashboardHODao dashboardHODao;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	Map<String, String> authTokenStatusMap = null;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Override
	public DashboardHOReturnsStatusUIDto getDashBoardReturnStatus(Long entityId,
			String taxPeriod) {

		List<DashboardHOReturnStatusDto> list = dashboardHODao
				.getDashBoardReturnStatus(entityId, taxPeriod);

		return convertUIDto(list);
	}

	private DashboardHOReturnsStatusUIDto convertUIDto(
			List<DashboardHOReturnStatusDto> list) {
		DashboardHOReturnsStatusUIDto uiDto = new DashboardHOReturnsStatusUIDto();

		for (DashboardHOReturnStatusDto d : list) {

			if (d.getTaxType().equalsIgnoreCase("GSTR1")) {
				uiDto.setGstr1TotalCount(d.getTotalCount());
				uiDto.setGstr1TotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr1TotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("GSTR1A")) {
				uiDto.setGstr1ATotalCount(d.getTotalCount());
				uiDto.setGstr1ATotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr1ATotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("GSTR3B")) {
				uiDto.setGstr3bTotalCount(d.getTotalCount());
				uiDto.setGstr3bTotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr3bTotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("GSTR9")) {
				uiDto.setGstr9TotalCount(d.getTotalCount());
				uiDto.setGstr9TotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr9TotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("GSTR6")) {
				uiDto.setGstr6TotalCount(d.getTotalCount());
				uiDto.setGstr6TotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr6TotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("GSTR7")) {
				uiDto.setGstr7TotalCount(d.getTotalCount());
				uiDto.setGstr7TotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr7TotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("GSTR8")) {
				uiDto.setGstr8TotalCount(d.getTotalCount());
				uiDto.setGstr8TotalAvailableCount(d.getAvailableCount());
				uiDto.setGstr8TotalPercentage(d.getTotalPercentage());
			} else if (d.getTaxType().equalsIgnoreCase("ITC04")) {
				uiDto.setItc04TotalCount(d.getTotalCount());
				uiDto.setItc04TotalAvailableCount(d.getAvailableCount());
				uiDto.setItc04TotalPercentage(d.getTotalPercentage());
			}

		}
		return uiDto;
	}

	@Override
	public DashboardHOReturnComplinceWithAuthCountDto getDashBoardReturnComplianceStatus(
			Long entityId, String taxPeriod) {

		List<String> gstiNsForEntity = gSTNDetailRepository
				.findgstinByEntityId(entityId);

		authTokenStatusMap = authTokenService
				.getAuthTokenStatusForGstins(gstiNsForEntity);

		int authTotal = gstiNsForEntity.size();
		int authInactive = 0;
		int authActive = 0;
		for (String gstin : gstiNsForEntity) {
			if (authTokenStatusMap.get(gstin).equals("I")) {
				authInactive++;
			}
			if (authTokenStatusMap.get(gstin).equals("A")) {
				authActive++;
			}
		}
		DashboardHOReturnComplinceWithAuthCountDto dto = new DashboardHOReturnComplinceWithAuthCountDto();

		dto.setAuthTokenActive(authActive);
		dto.setAuthTokenInActive(authInactive);
		dto.setAuthTokenTotal(authTotal);

		List<DashboardHOReturnComplianceStatusDto> list = dashboardHODao
				.getDashBoardReturnComplianceStatus(entityId, taxPeriod,
						gstiNsForEntity);
		List<DashboardHOReturnComplianceStatusDto> listWithStatus = list
				.stream().map(o -> setStaus(o)).collect(Collectors.toList());

		listWithStatus.sort(Comparator
				.comparing(
						DashboardHOReturnComplianceStatusDto::getOutwardSupply)
				.reversed());

		dto.setSupplyDetailsDto(listWithStatus);

		return dto;
	}

	private DashboardHOReturnComplianceStatusDto setStaus(
			DashboardHOReturnComplianceStatusDto input) {
		input.setStatus(authTokenStatusMap.get(input.getSupplierGstin()));

		return input;

	}

	@Override
	public DashboardHOOutwardSupplyUIDto getDashBoardOutwardStatus(
			Long entityId, String taxPeriod) {
		
		List<String> gstiNsForEntity = gSTNDetailRepository
				.findgstinByEntityId(entityId);
		
		DashboardHOOutwardSupplyDto outwardStatusDto = dashboardHODao
				.getDashBoardOutwardStatus(entityId, taxPeriod, gstiNsForEntity);
		
		DashboardHOOutwardSupplyUIDto dtos = convertToDtos(outwardStatusDto);

		return dtos;
	}

	private DashboardHOOutwardSupplyUIDto convertToDtos(
			DashboardHOOutwardSupplyDto outwardStatusDto) {

		DashboardInnerDto gstr1TaxVal = new DashboardInnerDto("GSTR-1",
				outwardStatusDto.getGSTR1TaxableValue());

		DashboardInnerDto gstr3bTaxVal = new DashboardInnerDto("GSTR-3B",
				outwardStatusDto.getGSTR3BTaxableValue());

		DashboardInnerDto taxValDiff = new DashboardInnerDto("Difference",
				outwardStatusDto.getDifferenceInTaxableValue());

		DashboardInnerDto gstr1TotalTax = new DashboardInnerDto("GSTR-1",
				outwardStatusDto.getGSTR1ToatalTax());

		DashboardInnerDto gstr3bTotalTax = new DashboardInnerDto("GSTR-3B",
				outwardStatusDto.getGSTR3BTotalTax());

		DashboardInnerDto totalTaxDiff = new DashboardInnerDto("Difference",
				outwardStatusDto.getDifferenceInTotalTax());

		List<DashboardInnerDto> totalTax = new ArrayList<DashboardInnerDto>();

		totalTax.add(gstr1TotalTax);
		totalTax.add(gstr3bTotalTax);
		totalTax.add(totalTaxDiff);

		List<DashboardInnerDto> taxableVal = new ArrayList<DashboardInnerDto>();
		taxableVal.add(gstr1TaxVal);
		taxableVal.add(gstr3bTaxVal);
		taxableVal.add(taxValDiff);

		DashboardHOOutwardSupplyUIDto dto = new DashboardHOOutwardSupplyUIDto(
				taxableVal, totalTax);

		return dto;
	}

	@Override
	public DashboardHOReconSummaryDto getDashBoardReconSummary(Long entityId,
			String taxPeriod) {

		DashboardHOReconSummaryDto dtos = dashboardHODao
				.getDashBoardReconSummary(entityId, taxPeriod);

		return dtos;

	}
	
	@Override
	public DashboardHOReconSummary2bprDto getDashBoardReconSummary2bpr(Long entityId,
			String taxPeriod) {

		DashboardHOReconSummary2bprDto dtos = dashboardHODao
				.getDashBoardReconSummary2bpr(entityId, taxPeriod);

		return dtos;

	}
	
	

}
