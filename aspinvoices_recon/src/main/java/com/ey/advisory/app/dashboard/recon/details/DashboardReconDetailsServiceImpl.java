package com.ey.advisory.app.dashboard.recon.details;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 
 * @author vishal.verma
 *
 */
@Component("DashboardReconDetailsServiceImpl")
public class DashboardReconDetailsServiceImpl
		implements DashboardReconDetailsService {

	@Autowired
	@Qualifier("DashboardReconDetailsDaoImpl")
	private DashboardReconDetailsDao dao;

	@Override
	public DashboardReconDetailsDto getDashboardReconDetails(
			Long entityId, String taxPeriod) {
	
		
		List<DbResponseDto> rcnList = dao.getReconDetails(entityId, taxPeriod);
		List<DbResponseDto> respList = dao.getRespDetails(entityId, taxPeriod);
		
		ReconSummaryDto rcn = rcnList.stream()
				.map(o -> convertRcn(o))				
				.collect(Collectors.reducing(new ReconSummaryDto(), 
						(acc, ele) -> acc.merge(ele)));
		
		
		
		RespSummaryDto rsp = respList.stream()
				.map(o -> convertResp(o))
				.collect(Collectors.reducing(new RespSummaryDto(), 
						(acc, ele) -> acc.merge(ele)));

		DashboardReconDetailsDto resultList = new DashboardReconDetailsDto(rsp,
				rcn);
		
		return resultList;
		
	}
	
	private ReconSummaryDto convertRcn(DbResponseDto obj) {
		ReconSummaryDto dto = new ReconSummaryDto();
		
		if (obj.getResponse().equalsIgnoreCase("Matched")) {
			dto.setMatchedPercent(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Mismatched")) {
			dto.setMismatchedPercent(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Probable")) {
			dto.setProbableMatchedPercent(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Addition in ANX-2")) {
			dto.setAddlA2(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Addition in PR")) {
			dto.setAddlPR(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Force Match")) {
			dto.setForcedMatchPercent(obj.getPercentage());
		}
	
		return dto;
	}

	private RespSummaryDto convertResp(DbResponseDto obj) {

		RespSummaryDto dto = new RespSummaryDto();

		if (obj.getResponse().equalsIgnoreCase("Accept (ANX–2)")) {
			dto.setAcceptAnx2Percent(obj.getPercentage());
		}

		if (obj.getResponse().equalsIgnoreCase("Pending (ANX–2)")) {
			dto.setPendingAnx2Percent(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Reject (ANX–2)")) {
			dto.setRejectAnx2Percent(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("No Action (ANX–2)")) {
			dto.setNoActionAnx2Percent(obj.getPercentage());
		}
		if (obj.getResponse()
				.equalsIgnoreCase("Provisional Credit (Addition in PR)")) {
			dto.setProvisinalAddlPRCreditPercent(obj.getPercentage());
		}
		if (obj.getResponse().equalsIgnoreCase("Pending (Addition in PR)")) {
			dto.setPendingPRPercent(obj.getPercentage());
		}

		return dto;
	}

}
