package com.ey.advisory.app.gstr1a.einv;

import java.util.List;

import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1AReportDownloadRequestIdWiseService {

	List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);
	
	public List<Gstr1EinvRequesIdWiseDownloadTabDto> getPrVsSubmDownloadData(
			Long configId);

}
