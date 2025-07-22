package com.ey.advisory.app.gstr1.einv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Slf4j
@Service("Gstr1EinvInitiateReconReptReqStatusServiceImpl")
public class Gstr1EinvInitiateReconReptReqStatusServiceImpl
		implements Gstr1EinvInitiateReconReptReqStatusService {

	@Autowired
	@Qualifier("Gstr1EinvInitiateReconReptReqStatusDaoImpl")
	private Gstr1EinvInitiateReconReptReqStatusDao requestStatusDao;
	
	@Override
	public List<Gstr1EinvInitiateReconReportRequestStatusDto> getReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1EinvInitiateReconReptReqStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}

		List<Gstr1EinvInitiateReconReportRequestStatusDto> response = requestStatusDao
				.getReportRequestStatus(reqDto, userName);

		Collector<Gstr1EinvInitiateReconReportRequestStatusDto, ?, Gstr1EinvInitiateReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new Gstr1EinvInitiateReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, Gstr1EinvInitiateReconReportRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<Gstr1EinvInitiateReconReportRequestStatusDto> res = new ArrayList<>(
				responseList.values());
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1EinvInitiateReconReptReqStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestStatus() method";
			LOGGER.debug(msg);
		}
		return res;
	}
	
	
	@Override
	public List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1EinvInitiateReconReptReqStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}
		

		List<Gstr1PrVsSubmReconReportRequestStatusDto> response = requestStatusDao
				.getPrVsSubReportRequestStatus(reqDto, userName);

		Collector<Gstr1PrVsSubmReconReportRequestStatusDto, ?, Gstr1PrVsSubmReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new Gstr1PrVsSubmReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergePrSubmDtos(Irr1, Irr2));

		Map<Object, Gstr1PrVsSubmReconReportRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<Gstr1PrVsSubmReconReportRequestStatusDto> res = new ArrayList<>(
				responseList.values());
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1EinvInitiateReconReptReqStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestStatus() method";
			LOGGER.debug(msg);
		}
		return res;
	}

	private Gstr1EinvInitiateReconReportRequestStatusDto mergeDtos(
			Gstr1EinvInitiateReconReportRequestStatusDto irr1,
			Gstr1EinvInitiateReconReportRequestStatusDto irr2) {

		Gstr1EinvInitiateReconReportRequestStatusDto irrs = new Gstr1EinvInitiateReconReportRequestStatusDto();

		if (irr1.getGstins() == null || irr1.getGstins().isEmpty()) {
			irrs.setGstins(irr2.getGstins());

		} else {
			irr1.getGstins().addAll(irr2.getGstins());
			irrs.setGstins(irr1.getGstins());

		}
		irrs.setCompletionOn(irr2.getCompletionOn());
		irrs.setInitiatedBy(irr2.getInitiatedBy());
		irrs.setInitiatedOn(irr2.getInitiatedOn());
		irrs.setRequestId(irr2.getRequestId());
		irrs.setStatus(irr2.getStatus());
		irrs.setPath(irr2.getPath());
		irrs.setTaxPeriod(irr2.getTaxPeriod());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		return irrs;
	}
	
	
	private Gstr1PrVsSubmReconReportRequestStatusDto mergePrSubmDtos(
			Gstr1PrVsSubmReconReportRequestStatusDto irr1,
			Gstr1PrVsSubmReconReportRequestStatusDto irr2) {

		Gstr1PrVsSubmReconReportRequestStatusDto irrs = new Gstr1PrVsSubmReconReportRequestStatusDto();

		if (irr1.getGstins() == null || irr1.getGstins().isEmpty()) {
			irrs.setGstins(irr2.getGstins());

		} else {
			irr1.getGstins().addAll(irr2.getGstins());
			irrs.setGstins(irr1.getGstins());

		}
		irrs.setCompletionOn(irr2.getCompletionOn());
		irrs.setInitiatedBy(irr2.getInitiatedBy());
		irrs.setInitiatedOn(irr2.getInitiatedOn());
		irrs.setRequestId(irr2.getRequestId());
		irrs.setStatus(irr2.getStatus());
		irrs.setPath(irr2.getPath());
		irrs.setFromTaxPeriod(irr2.getFromTaxPeriod());
		irrs.setToTaxPeriod(irr2.getToTaxPeriod());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		return irrs;
	}

	@Override
	public List<Gstr1EinvoiceAndReconStatusDto> getReportRequestStatus(
			List<String> gstnsList, String taxPeriod) {
		
		List<Gstr1EinvoiceAndReconStatusDto> resp = 
				requestStatusDao.getGstr1status(gstnsList, taxPeriod);	
		
		return resp;
	}
	
	
	
	
}
