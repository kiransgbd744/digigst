package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.TDSProcessSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.TDSProcessSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04RequestDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("TDSProcessSummaryFetchService")
public class TDSProcessSummaryFetchService {

	@Autowired
	@Qualifier("TDSProcessSummaryFetchDaoImpl")
	TDSProcessSummaryFetchDaoImpl processSummaryFetchDaoImpl;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<TDSProcessSummaryRespDto> response(ITC04RequestDto req) {

		ITC04RequestDto reqDto = processedRecordsCommonSecParam
				.setItc04DataSecuritySearchParams(req);

		List<TDSProcessSummaryRespDto> processedRespDtos = processSummaryFetchDaoImpl
				.fetchTdsPrRecords(reqDto);

		return processedRespDtos;

	}

}
