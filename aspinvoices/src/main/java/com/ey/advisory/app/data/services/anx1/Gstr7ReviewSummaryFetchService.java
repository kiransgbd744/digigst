package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.daos.client.Gstr7ReviewSummaryFetchDaoImpl;
import com.ey.advisory.app.data.views.client.Gstr7DiffSummaryRespDto;
import com.ey.advisory.app.data.views.client.Gstr7ReviewSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.google.common.collect.Lists;

@Component("Gstr7ReviewSummaryFetchService")
public class Gstr7ReviewSummaryFetchService {

	@Autowired
	@Qualifier("Gstr7ReviewSummaryFetchDaoImpl")
	private Gstr7ReviewSummaryFetchDaoImpl gstr7ReviewSummaryFetchDaoImpl;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Transactional(value = "clientTransactionManager")
	public List<Gstr7ReviewSummaryRespDto> getReviewSummary(
			Gstr2AProcessedRecordsReqDto req) {
		Gstr2AProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr2aDataSecuritySearchParams(req);
		List<Gstr7ReviewSummaryRespDto> respDtos = gstr7ReviewSummaryFetchDaoImpl
				.loadGstr7ReviewSummary(reqDto);
		if (org.apache.commons.collections.CollectionUtils
				.isNotEmpty(respDtos)) {
			respDtos.forEach(dto -> {
				dto.setDiffCount(dto.getAspCount() - dto.getGstnCount());
				dto.setDiffTotalAmount(dto.getAspTotalAmount()
						.subtract(dto.getGstnTotalAmount()));
				dto.setDiffIgst(dto.getAspIgst().subtract(dto.getGstnIgst()));
				dto.setDiffCgst(dto.getAspCgst().subtract(dto.getGstnCgst()));
				dto.setDiffSgst(dto.getAspSgst().subtract(dto.getGstnSgst()));
			});
		}
		List<String> dataGstinList = new ArrayList<>();
		respDtos.forEach(dto -> dataGstinList.add(dto.getSection()));
		if (!dataGstinList.contains("Table-3")) {
			Gstr7ReviewSummaryRespDto dto1 = new Gstr7ReviewSummaryRespDto();
			dto1.setSection("Table-3");
			dto1.setAspCount(0);
			dto1.setAspTotalAmount(BigDecimal.ZERO);
			dto1.setAspIgst(BigDecimal.ZERO);
			dto1.setAspCgst(BigDecimal.ZERO);
			dto1.setAspSgst(BigDecimal.ZERO);
			dto1.setGstnCount(0);
			dto1.setGstnTotalAmount(BigDecimal.ZERO);
			dto1.setGstnIgst(BigDecimal.ZERO);
			dto1.setGstnSgst(BigDecimal.ZERO);
			dto1.setGstnCgst(BigDecimal.ZERO);
			dto1.setDiffCount(0);
			dto1.setDiffTotalAmount(BigDecimal.ZERO);
			dto1.setDiffIgst(BigDecimal.ZERO);
			dto1.setDiffSgst(BigDecimal.ZERO);
			dto1.setDiffCgst(BigDecimal.ZERO);

			respDtos.add(dto1);
		}
		if (!dataGstinList.contains("Table-4")) {
			Gstr7ReviewSummaryRespDto dto1 = new Gstr7ReviewSummaryRespDto();
			dto1.setSection("Table-4");
			dto1.setAspCount(0);
			dto1.setAspTotalAmount(BigDecimal.ZERO);
			dto1.setAspIgst(BigDecimal.ZERO);
			dto1.setAspCgst(BigDecimal.ZERO);
			dto1.setAspSgst(BigDecimal.ZERO);
			dto1.setGstnCount(0);
			dto1.setGstnTotalAmount(BigDecimal.ZERO);
			dto1.setGstnIgst(BigDecimal.ZERO);
			dto1.setGstnSgst(BigDecimal.ZERO);
			dto1.setGstnCgst(BigDecimal.ZERO);
			dto1.setDiffCount(0);
			dto1.setDiffTotalAmount(BigDecimal.ZERO);
			dto1.setDiffIgst(BigDecimal.ZERO);
			dto1.setDiffSgst(BigDecimal.ZERO);
			dto1.setDiffCgst(BigDecimal.ZERO);

			respDtos.add(dto1);
		}

		return respDtos;

	}

	public List<Gstr7DiffSummaryRespDto> getDiffenceSummary(
			Gstr2AProcessedRecordsReqDto reqDto) {
		List<Gstr7DiffSummaryRespDto> diffList = Lists.newArrayList();
		List<Gstr7ReviewSummaryRespDto> dtos = getReviewSummary(reqDto);
		dtos.forEach(dto -> {
			Gstr7DiffSummaryRespDto respDto = new Gstr7DiffSummaryRespDto();
			respDto.setSection(dto.getSection());
			respDto.setDiffCount(dto.getDiffCount());
			respDto.setDiffTotalAmount(dto.getDiffTotalAmount());
			respDto.setDiffIgst(dto.getDiffIgst());
			respDto.setDiffCgst(dto.getDiffCgst());
			respDto.setDiffSgst(dto.getDiffSgst());

			diffList.add(respDto);
		});
		return diffList;
	}

}
