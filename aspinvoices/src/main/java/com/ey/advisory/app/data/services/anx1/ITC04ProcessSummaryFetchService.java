package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.ITC04ProcessSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.ITC04ProcessSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04RequestDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("ITC04ProcessSummaryFetchService")
public class ITC04ProcessSummaryFetchService {
	
	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;
	
	@Autowired
	@Qualifier("ITC04ProcessSummaryFetchDaoImpl")
	private ITC04ProcessSummaryFetchDaoImpl processSummaryFetchDaoImpl;


	
	public List<ITC04ProcessSummaryRespDto> response(
			ITC04RequestDto req) {
		
	/*	List<ITC04ProcessSummaryRespDto> summaryResponses = Lists
				.newArrayList();*/
		
		ITC04RequestDto reqDto = processedRecordsCommonSecParam
				.setItc04DataSecuritySearchParams(req);
		
		List<ITC04ProcessSummaryRespDto> processedRespDtos = processSummaryFetchDaoImpl
				.fetchITC04Records(reqDto);
		
		
				return processedRespDtos;

}
}
