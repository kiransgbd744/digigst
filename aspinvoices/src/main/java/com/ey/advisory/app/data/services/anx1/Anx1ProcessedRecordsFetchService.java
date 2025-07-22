package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Anx1ProcessedRecordsFetchDao;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;

@Component("Anx1ProcessedRecordsFetchService")
public class Anx1ProcessedRecordsFetchService {

	@Autowired
	@Qualifier("Anx1ProcessedRecordsFetchDaoImpl")
	private Anx1ProcessedRecordsFetchDao anx1ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<Anx1ProcessedRecordsFinalRespDto> find(
			Anx1ProcessedRecordsReqDto req, String functionType) {
		Anx1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setDataSecuritySearchParams(req);
		List<Anx1ProcessedRecordsFinalRespDto> processedRecordsRespDtos = anx1ProcessedRecordsFetchDao
				.loadAnx1ProcessedRecords(reqDto, functionType);
		return processedRecordsRespDtos;
	}

}
