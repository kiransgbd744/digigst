package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * 
 * @author Rajesh N K
 *
 */
public interface Gstr1EinvInitiateReconReptReqStatusService {

	List<Gstr1EinvInitiateReconReportRequestStatusDto> getReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);

	List<Gstr1EinvoiceAndReconStatusDto> getReportRequestStatus(
			List<String> gstnsList, String taxPeriod);

	List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);

	

}
