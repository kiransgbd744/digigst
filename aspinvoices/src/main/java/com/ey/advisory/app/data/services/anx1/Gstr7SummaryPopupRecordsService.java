package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.daos.client.Gstr7PopupRecordsFetchDaoImpl;
import com.ey.advisory.app.docs.dto.gstr1.Gstr7PopupRecordsRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr7SummaryPopupRecordsService")
public class Gstr7SummaryPopupRecordsService {

	@Autowired
	@Qualifier("Gstr7PopupRecordsFetchDaoImpl")
	private Gstr7PopupRecordsFetchDaoImpl gstr1ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Transactional(value = "clientTransactionManager")
	public List<Gstr7PopupRecordsRespDto> response(
			Gstr1ProcessedRecordsReqDto req) {

		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr7DataSecuritySearchParams(req);
		List<Gstr7PopupRecordsRespDto> processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadGstr7PopupSummaryRecords(reqDto);
		return processedRespDtos;

	}

	
	
}
