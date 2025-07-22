package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.TDSSummaryFetchDaoImpl;
import com.ey.advisory.app.data.daos.client.TDSUpdateActionDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.TDSSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04RequestDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("TDSSummaryScreenFetchService")
public class TDSSummaryScreenFetchService {

	
	@Autowired
	@Qualifier("TDSSummaryFetchDaoImpl")
	TDSSummaryFetchDaoImpl summaryFetchDaoImpl;
	
	@Autowired
	@Qualifier("TDSUpdateActionDaoImpl")
	TDSUpdateActionDaoImpl tdsUpdateActionDaoImpl;
	
	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<TDSSummaryRespDto> response(ITC04RequestDto req) {

		ITC04RequestDto reqDto = processedRecordsCommonSecParam
				.setItc04DataSecuritySearchParams(req);

		List<TDSSummaryRespDto> processedRespDtos = summaryFetchDaoImpl
				.fetchTdsSummaryRecords(reqDto);

		return processedRespDtos;

	}
	
	// Updation Action
	
//public List<TDSSummaryRespDto> updateResponse(ITC04RequestDto req) {
//
//		ITC04RequestDto reqDto = processedRecordsCommonSecParam
//				.setItc04DataSecuritySearchParams(req);
//
//		List<TDSSummaryRespDto> processedRespDtos = summaryFetchDaoImpl
//				.fetchTdsSummaryRecords(reqDto);
//
//		return processedRespDtos;
//
//	}
	
	
	
}
