package com.ey.advisory.gstr2.initiaterecon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 
 * @author vishal.verma
 *
 */

@Service("EWB3WaySummaryInitiateReconService")
public class EWB3WaySummaryInitiateReconService {

	@Autowired
	@Qualifier("EWB3WaySummaryInitiateReconDaoImpl")
	EWB3WaySummaryInitiateReconDao initiateReconDao;

	public List<EWB3WaySummaryInitiateReconLineItemDto> find (EWB3WaySummaryInitiateReconDto dto) {

		EWB3WaySummaryInitiateReconDto req = (EWB3WaySummaryInitiateReconDto) dto;

		List<EWB3WaySummaryInitiateReconLineItemDto> entityResponse = initiateReconDao
				.ewb3WayInitiateRecon(req);

		return entityResponse;

	}

}
