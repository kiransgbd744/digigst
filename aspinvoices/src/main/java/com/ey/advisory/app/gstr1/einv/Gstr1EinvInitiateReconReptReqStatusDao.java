package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
/**
 * 
 * @author Rajesh N K
 *
 */
public interface Gstr1EinvInitiateReconReptReqStatusDao {

	List<Gstr1EinvInitiateReconReportRequestStatusDto> getReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);

	List<Gstr1EinvoiceAndReconStatusDto> getGstr1status(List<String> gstnsList,
			String taxPeriod);

	List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrVsSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);


}
