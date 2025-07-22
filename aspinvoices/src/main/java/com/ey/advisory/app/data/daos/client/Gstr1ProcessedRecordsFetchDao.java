package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;

public interface Gstr1ProcessedRecordsFetchDao {
	public List<Gstr1ProcessedRecordsRespDto> loadGstr1ProcessedRecords(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto);



	public String createQueryString(List<Long> entityId,
			List<String> gstinList, int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List,List<String> tableType,List<String> docType,
			LocalDate docFromDate,LocalDate docToDate,List<String> einvGenerated,
			List<String> ewbGenerated,Integer Status);
}
