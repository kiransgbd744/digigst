package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.daos.client.Gstr7ProcessedRecordsFetchDaoImpl;
import com.ey.advisory.app.docs.dto.gstr1.Gstr7ProcessedRecordsRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;

@Component("Gstr7ProcessedRecordsFetchService")
public class Gstr7ProcessedRecordsFetchService {

	@Autowired
	@Qualifier("Gstr7ProcessedRecordsFetchDaoImpl")
	private Gstr7ProcessedRecordsFetchDaoImpl gstr1ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Transactional(value = "clientTransactionManager")
	public List<Gstr7ProcessedRecordsRespDto> response(
			Gstr1ProcessedRecordsReqDto req) {

		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr7DataSecuritySearchParams(req);
		List<Gstr7ProcessedRecordsRespDto> processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadGstr7ProcessedRecords(reqDto);
		return processedRespDtos;

	}

}
