package com.ey.advisory.app.services.search.apisummarysearch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ApiSummaryReqDto;
import com.ey.advisory.app.docs.dto.ApiSummaryResDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("apiSummarySearchService")
public class ApiSummarySearchService {

	@Autowired
	@Qualifier("DefaultApiSummaryFetcher")
	private ApiSummaryFecther apiSummaryFecther;

	public List<ApiSummaryResDto> find(SearchCriteria criteria) {
		ApiSummaryReqDto apiSummaryReqDto = (ApiSummaryReqDto) criteria;

		LocalDate docDateFrom = apiSummaryReqDto.getDocDateFrom();
		LocalDate docDateTo = apiSummaryReqDto.getDocDateTo();
		LocalDate recvDateFrom = apiSummaryReqDto.getDataRecvFrom();
		LocalDate recvDateTo = apiSummaryReqDto.getDataRecvTo();
		List<Long> entityId = apiSummaryReqDto.getEntityId();

		String fromTaxPeriod = apiSummaryReqDto.getRetPeriodFrom();
		String toTaxPeriod = apiSummaryReqDto.getRetPeriodTo();
		int derRetPeriodFrom = 0;
		int derRetPeriodTo = 0;
		if (fromTaxPeriod != null) {
			derRetPeriodFrom = GenUtil.convertTaxPeriodToInt(fromTaxPeriod);
			derRetPeriodTo = GenUtil.convertTaxPeriodToInt(toTaxPeriod);
		}

		List<LocalDate> selectedDates = apiSummaryReqDto.getDates();
		List<String> selectedSgtins = apiSummaryReqDto.getGstin();
		List<ApiSummaryResDto> repEntity = new ArrayList<>();

		try {

			StringBuilder build = new StringBuilder();
			if (selectedDates != null && selectedDates.size() > 0) {
				if (docDateFrom != null && docDateTo != null) {
					build.append(" AND DOC_DATE IN :dates ");
				} else {
				build.append(" AND RECEIVED_DATE IN :dates");
				}
			}
			if (entityId != null && entityId.size() > 0) {
				build.append(
						" AND SUPPLIER_GSTIN IN (SELECT DISTINCT GSTIN FROM "
								+ "GSTIN_INFO WHERE ENTITY_ID IN :entityId)");
			}

			if (selectedSgtins != null && selectedSgtins.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstin");
			}
			/**
			 * @Required field
			 */
			if (recvDateFrom != null && recvDateTo != null) {
				build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
						+ "AND :dataRecvTo ORDER BY TABLE_SECTION, RECEIVED_DATE ");
			} else if (docDateFrom != null && docDateTo != null) {
				build.append(" AND DOC_DATE BETWEEN :docDateFrom "
						+ "AND :docDateTo ORDER BY TABLE_SECTION, RECEIVED_DATE");
			} else if (derRetPeriodFrom != 0 && derRetPeriodTo != 0) {
				build.append(" AND DERIVED_RET_PERIOD BETWEEN "
						+ ":retPeriodFrom AND :retPeriodTo ORDER BY "
						+ "TABLE_SECTION, RECEIVED_DATE ");
			} else {
				throw new AppException("Insufficient Search.");
			}
			String where = build.toString().substring(4);
			repEntity = apiSummaryFecther.findApiSummary(where, selectedSgtins,
					selectedDates, docDateFrom, docDateTo, recvDateFrom, 
					recvDateTo,	derRetPeriodFrom, derRetPeriodTo, entityId);
			List<ApiSummaryResDto> addFile =  
					          convertToSameTableSctionsSumItUp(repEntity);
			return addFile;
			
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Data Status Data",
					ex);

		}
		
 
	}
	
		private List<ApiSummaryResDto> convertToSameTableSctionsSumItUp(
			List<ApiSummaryResDto> repEntity) {
		Map<String,ApiSummaryResDto> map = new HashMap<>();
		for(ApiSummaryResDto apiSummary : repEntity){
			
			ApiSummaryResDto apiKey = map.get(apiSummary.getSection());
			
			apiKey = (apiKey == null) ?
					new ApiSummaryResDto(apiSummary.getSection()) : apiKey;
					ApiSummaryResDto newObj = new ApiSummaryResDto(
							apiSummary.getReceivedDate(),
							apiSummary.getDocDate(),apiSummary.getGstin(),
							apiSummary.getPeriod(),apiSummary.getSection(),
							apiSummary.getCount(),apiSummary.getTaxableValue(),
							apiSummary.getToatlTaxes(),apiSummary.getIgst(),
							apiSummary.getCgst(),apiSummary.getSgst(),
							apiSummary.getCess(),apiSummary.getAuthToken(),
							apiSummary.getStatus());
					map.put(apiKey.getSection(),apiKey.add(newObj));
		}

		List<ApiSummaryResDto> valueList = 
				map.values().stream().collect(Collectors.toList()); 
		Collections.sort(valueList,
				(o1,o2)-> o1.getSection().compareTo(o2.getSection()));
		return new ArrayList<>(valueList);
	}

}
