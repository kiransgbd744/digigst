package com.ey.advisory.app.anx2.initiaterecon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("InitiateReconReportRequestStatusServiceImpl")
public class InitiateReconReportRequestStatusServiceImpl
		implements InitiateReconReportRequestStatusService {

	@Autowired
	@Qualifier("InitiateReconReportRequestStatusDaoImpl")
	private InitiateReconReportRequestStatusDao requestStatusDao;

	@Override
	public List<InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin InitiateReconReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
			}

		List<InitiateReconReportRequestStatusDto> response = requestStatusDao
				.getReportRequestStatus(userName);

		Collector<InitiateReconReportRequestStatusDto, ?,
				InitiateReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new InitiateReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, InitiateReconReportRequestStatusDto> responseList = response
				.stream()
				.collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<InitiateReconReportRequestStatusDto> res = new ArrayList<>(
				responseList.values());
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin InitiateReconReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestStatus() method";
			LOGGER.debug(msg);
			}
		return res;

	}

	private InitiateReconReportRequestStatusDto mergeDtos(
			InitiateReconReportRequestStatusDto irr1,
			InitiateReconReportRequestStatusDto irr2) {

		InitiateReconReportRequestStatusDto irrs = 
				new InitiateReconReportRequestStatusDto();

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

}
