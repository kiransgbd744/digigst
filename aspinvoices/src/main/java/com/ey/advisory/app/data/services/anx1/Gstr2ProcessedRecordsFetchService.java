package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr2ProcessedRecordsFetchDao;
import com.ey.advisory.app.docs.dto.anx1.Gstr2ProcessedRecordsFinalRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

@Component("Gstr2ProcessedRecordsFetchService")
public class Gstr2ProcessedRecordsFetchService {

	@Autowired
	@Qualifier("Gstr2ProcessedRecordsFetchDaoImpl")
	private Gstr2ProcessedRecordsFetchDao gstr2ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<Gstr2ProcessedRecordsFinalRespDto> findGstr2ProcessedRecords(
			Gstr2ProcessedRecordsReqDto req) {
		Gstr2ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr2DataSecuritySearchParams(req);
		List<Gstr2ProcessedRecordsFinalRespDto> processedRecordsRespDtos = gstr2ProcessedRecordsFetchDao
				.loadGstr2ProcessedRecords(reqDto);
		return processedRecordsRespDtos;
	}

}
