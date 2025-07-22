package com.ey.advisory.app.services.search.getdatasummarysearch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.GetDataSummaryReqDto;
import com.ey.advisory.app.docs.dto.GetDataSummaryResDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("getDataSummarySearchService")
public class GetDataSummarySearchService {

	@Autowired
	@Qualifier("DefaultGetDataSummaryFetcher")
	private GetDataSummaryFetcher getDataSummaryFetcher;

	public List<GetDataSummaryResDto> find(SearchCriteria criteria) {
		GetDataSummaryReqDto getDataSummaryReqDto = (GetDataSummaryReqDto) criteria;

		LocalDate docDateFrom = getDataSummaryReqDto.getDocDateFrom();
		LocalDate docDateTo = getDataSummaryReqDto.getDocDateTo();
		LocalDate recvDateFrom = getDataSummaryReqDto.getDataRecvFrom();
		LocalDate recvDateTo = getDataSummaryReqDto.getDataRecvTo();
		List<Long> entityId = getDataSummaryReqDto.getEntityId();

		String fromTaxPeriod = getDataSummaryReqDto.getRetPeriodFrom();
		String toTaxPeriod = getDataSummaryReqDto.getRetPeriodTo();
		int derRetPeriodFrom = 0;
		int derRetPeriodTo = 0;
		if (fromTaxPeriod != null) {
			derRetPeriodFrom = GenUtil.convertTaxPeriodToInt(fromTaxPeriod);
			derRetPeriodTo = GenUtil.convertTaxPeriodToInt(toTaxPeriod);
		}

		List<LocalDate> selectedDates = getDataSummaryReqDto.getDates();
		List<String> selectedSgtins = getDataSummaryReqDto.getGstin();
		List<GetDataSummaryResDto> repEntity = new ArrayList<>();

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
			repEntity = getDataSummaryFetcher.findGetDataSummary(where,
					selectedSgtins, selectedDates, docDateFrom, docDateTo,
					recvDateFrom, recvDateTo, derRetPeriodFrom, derRetPeriodTo,
					entityId);
			List<GetDataSummaryResDto> addFile = convertToSameTableSctionsSumItUp(
					repEntity);
			return addFile;

		} catch (Exception ex) {
			throw new AppException("Error in fetching the Data Status Data",
					ex);

		}

	}

	private List<GetDataSummaryResDto> convertToSameTableSctionsSumItUp(
			List<GetDataSummaryResDto> repEntity) {
		Map<String, GetDataSummaryResDto> map = new HashMap<>();
		for (GetDataSummaryResDto getDataSummary : repEntity) {

			GetDataSummaryResDto returnKey = map.get(getDataSummary.getSection());

			returnKey = (returnKey == null)
					? new GetDataSummaryResDto(getDataSummary.getSection())
					: returnKey;
			GetDataSummaryResDto newObj = new GetDataSummaryResDto(
					getDataSummary.getReceivedDate(), getDataSummary.getDocDate(),
					getDataSummary.getGstin(), getDataSummary.getPeriod(), 
					getDataSummary.getType(), getDataSummary.getSection(), 
					getDataSummary.getCount(), getDataSummary.getTaxableValue(),
					getDataSummary.getToatlTaxes(), getDataSummary.getIgst(),
					getDataSummary.getCgst(), getDataSummary.getSgst(),
					getDataSummary.getCess(), getDataSummary.getAuthToken(),
					getDataSummary.getStatus());
			map.put(returnKey.getSection(), returnKey.add(newObj));
		}

		List<GetDataSummaryResDto> valueList = map.values().stream()
				.collect(Collectors.toList());
		Collections.sort(valueList,
				(o1, o2) -> o1.getSection().compareTo(o2.getSection()));
		return new ArrayList<>(valueList);
	}

}
