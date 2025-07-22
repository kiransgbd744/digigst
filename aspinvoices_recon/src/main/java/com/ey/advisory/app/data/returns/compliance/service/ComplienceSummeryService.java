package com.ey.advisory.app.data.returns.compliance.service;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

/**
 * 
 * @author Siva.Nandam
 *
 */
public interface ComplienceSummeryService {
	public List<ComplienceSummeryRespDto> findcomplienceSummeryRecords(
			Gstr2aProcessedDataRecordsReqDto reqdto);

}
