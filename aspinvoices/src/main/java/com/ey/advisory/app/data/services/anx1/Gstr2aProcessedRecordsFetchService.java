package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr2aProcessedRecordsFetchDao;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedRecordsRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

@Component("Gstr2aProcessedRecordsFetchService")
public class Gstr2aProcessedRecordsFetchService {

	@Autowired
	@Qualifier("Gstr2aProcessedRecordsFetchDaoImpl")
	private Gstr2aProcessedRecordsFetchDao gstr2aProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<Gstr2aProcessedRecordsRespDto> findGstr2aProcessedRecords(
			Gstr2AProcessedRecordsReqDto req) {
		Gstr2AProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr2aDataSecuritySearchParams(req);
		List<Gstr2aProcessedRecordsRespDto> processedRecordsRespDtos = gstr2aProcessedRecordsFetchDao
				.loadGstr2aProcessedRecords(reqDto);
		return processedRecordsRespDtos;
	}

}
