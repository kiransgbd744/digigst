package com.ey.advisory.app.services.search.docsummarysearch;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.ITC04SummaryRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.ITC04SimpleDocSummarySearchService;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.core.search.SearchResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ITC04SummaryScreenReqRespHandler")
public class ITC04SummaryScreenReqRespHandler {

	public static final String m2jwsold = "M2JW (Section 4)";
	public static final String jw2m = "JW2M (Section 5A)";
	public static final String otherjw2m = "OtherJW2M (Section 5B)";
	public static final String m2jwsoldFrom = "M2JWSoldfromJW (Section 5C)";

	@Autowired
	@Qualifier("ITC04SimpleDocSummarySearchService")
	ITC04SimpleDocSummarySearchService searchService;

	@SuppressWarnings("unchecked")
	public List<ITC04SummaryRespDto> handleItc04ReqAndResp(
			ITC04RequestDto itcSummaryRequest) {

		SearchResult<ITC04SummaryRespDto> summary = searchService
				.find(itcSummaryRequest, null, ITC04SummaryRespDto.class);

		List<? extends ITC04SummaryRespDto> list = summary.getResult();

		List<ITC04SummaryRespDto> list1 = new ArrayList<>();
		ITC04SummaryRespDto dto1 = new ITC04SummaryRespDto();

		List<ITC04SummaryRespDto> list2 = new ArrayList<>();
		ITC04SummaryRespDto dto2 = new ITC04SummaryRespDto();

		List<ITC04SummaryRespDto> list3 = new ArrayList<>();
		ITC04SummaryRespDto dto3 = new ITC04SummaryRespDto();

		List<ITC04SummaryRespDto> list4 = new ArrayList<>();
		ITC04SummaryRespDto dto4 = new ITC04SummaryRespDto();

		for (ITC04SummaryRespDto dto : list) {

			if (m2jwsold.equalsIgnoreCase(dto.getTable())) {
				dto1.setTable(dto.getTable());
				dto1.setAspCount(dto.getAspCount());
				dto1.setAspTaxableValue(dto.getAspTaxableValue());
				dto1.setGstnCount(dto.getGstnCount());
				dto1.setGstnTaxableValue(dto.getGstnTaxableValue());
				dto1.setDiffCount(dto.getAspCount() - dto.getGstnCount());
				dto1.setDiffTaxableValue(dto.getAspTaxableValue()
						.subtract(dto.getGstnTaxableValue()));
				list1.add(dto1);
			}
			if (jw2m.equalsIgnoreCase(dto.getTable())) {
				dto2.setTable(dto.getTable());
				dto2.setAspCount(dto.getAspCount());
				dto2.setAspTaxableValue(dto.getAspTaxableValue());
				dto2.setGstnCount(dto.getGstnCount());
				dto2.setGstnTaxableValue(dto.getGstnTaxableValue());
				dto2.setDiffCount(dto.getAspCount() - dto.getGstnCount());
				dto2.setDiffTaxableValue(dto.getAspTaxableValue()
						.subtract(dto.getGstnTaxableValue()));
				list2.add(dto2);
			}
			if (otherjw2m.equalsIgnoreCase(dto.getTable())) {

				dto3.setTable(dto.getTable());
				dto3.setAspCount(dto.getAspCount());
				dto3.setAspTaxableValue(dto.getAspTaxableValue());
				dto3.setGstnCount(dto.getGstnCount());
				dto3.setGstnTaxableValue(dto.getGstnTaxableValue());
				dto3.setDiffCount(dto.getAspCount() - dto.getGstnCount());
				dto3.setDiffTaxableValue(dto.getAspTaxableValue()
						.subtract(dto.getGstnTaxableValue()));
				list3.add(dto3);
			}
			if (m2jwsoldFrom.equalsIgnoreCase(dto.getTable())) {

				dto4.setTable(dto.getTable());
				dto4.setAspCount(dto.getAspCount());
				dto4.setAspTaxableValue(dto.getAspTaxableValue());
				dto4.setGstnCount(dto.getGstnCount());
				dto4.setGstnTaxableValue(dto.getGstnTaxableValue());
				dto4.setDiffCount(dto.getAspCount() - dto.getGstnCount());
				dto4.setDiffTaxableValue(dto.getAspTaxableValue()
						.subtract(dto.getGstnTaxableValue()));
				list4.add(dto4);

			}

		}

		List<ITC04SummaryRespDto> finalList = new ArrayList<>();

		finalList.addAll(list1);
		finalList.addAll(list2);
		finalList.addAll(list3);
		finalList.addAll(list4);
		return finalList;

	}

}
