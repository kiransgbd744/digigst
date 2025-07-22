/**
 * 
 */
package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.Gstr1AProcessedVsSubmittedDao;
import com.ey.advisory.app.docs.dto.ProcessedVsSubmittedResponseDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AProcessedVsSubmittedService")
public class Gstr1AProcessedVsSubmittedService {

	@Autowired
	@Qualifier("Gstr1AProcessedVsSubmittedDaoImpl")
	private Gstr1AProcessedVsSubmittedDao processedVsSubmittedDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<ProcessedVsSubmittedResponseDto> findProcVsSubProcessedRecords(
			ProcessedVsSubmittedRequestDto req) {

		ProcessedVsSubmittedRequestDto reqDto = processedRecordsCommonSecParam
				.setProcVsSubDataSecuritySearchParams(req);

		List<ProcessedVsSubmittedResponseDto> procesVsSubRespDtos = processedVsSubmittedDao
				.processedVsSubdRecords(reqDto);

		return procesVsSubRespDtos;
	}

}
