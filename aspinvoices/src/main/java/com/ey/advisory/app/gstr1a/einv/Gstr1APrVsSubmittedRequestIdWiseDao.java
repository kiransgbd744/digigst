package com.ey.advisory.app.gstr1a.einv;

import java.util.List;

import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * 
 * @author Shashikant.Shukla
 */
public interface Gstr1APrVsSubmittedRequestIdWiseDao {

	List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrVsSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);

}
