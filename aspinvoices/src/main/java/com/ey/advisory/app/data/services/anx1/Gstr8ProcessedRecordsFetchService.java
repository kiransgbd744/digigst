package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.daos.client.Gstr8ProcessedRecordsFetchDaoImpl;
import com.ey.advisory.app.docs.dto.gstr1.Gstr8ProcessedRecordsRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr8ProcessedRecordsFetchService")
public class Gstr8ProcessedRecordsFetchService {

	@Autowired
	@Qualifier("Gstr8ProcessedRecordsFetchDaoImpl")
	private Gstr8ProcessedRecordsFetchDaoImpl gstr1ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Transactional(value = "clientTransactionManager")
	public List<Gstr8ProcessedRecordsRespDto> response(
			Gstr1ProcessedRecordsReqDto req) {

		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr8DataSecuritySearchParams(req);
		LOGGER.debug("reqDto {}",reqDto);
		List<Gstr8ProcessedRecordsRespDto> processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadGstr8ProcessedRecords(reqDto);
		return processedRespDtos;

	}

}
