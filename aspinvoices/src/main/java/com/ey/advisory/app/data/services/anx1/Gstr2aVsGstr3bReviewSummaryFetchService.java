package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr2aVsGstr3bReviewSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component("Gstr2aVsGstr3bReviewSummaryFetchService")
public class Gstr2aVsGstr3bReviewSummaryFetchService {

    @Autowired
    @Qualifier("ProcessedRecordsCommonSecParam")
    private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

    @Autowired
    @Qualifier("Gstr2aVsGstr3bReviewSummaryFetchDaoImpl")
    private Gstr2aVsGstr3bReviewSummaryFetchDaoImpl gstr2aVsGstr3bProcessSummaryFetchDaoImpl;

    public List<Gstr2aVsGstr3bReviewSummaryRespDto> gstr2aVsGstr3bReviewSummaryRecords(
            Gstr1VsGstr3bProcessSummaryReqDto req) throws Exception {
        Gstr1VsGstr3bProcessSummaryReqDto reqDto = processedRecordsCommonSecParam
                .setGstr2aVsGstr3bDataSecuritySearchParams(req);
        List<Gstr2aVsGstr3bReviewSummaryRespDto> finalResp = Lists
                .newLinkedList();
        List<Gstr2aVsGstr3bReviewSummaryRespDto> processedRespDtos = gstr2aVsGstr3bProcessSummaryFetchDaoImpl
                .fetchGstr2aVsGstr3bRecords(reqDto);
        Map<String, String> descMap = buildDescriptionMap();
        Set<String> callFields = descMap.keySet();

        callFields.forEach(callField -> {
            List<Gstr2aVsGstr3bReviewSummaryRespDto> dtos = processedRespDtos
                    .stream().filter(dto -> dto.getCalFeild().equals(callField))
                    .collect(Collectors.toList());
            finalResp.add(calculateCallFieldData(dtos, callField));
        });

        finalResp.forEach(dto -> {
            dto.setDescription(descMap.get(dto.getCalFeild()));
            if (dto.getCalFeild().equalsIgnoreCase("C")) {
                dto.setCalFeild("C=A-B");
            }
        });

        return finalResp;
    }

    private Gstr2aVsGstr3bReviewSummaryRespDto calculateCallFieldData(
            List<Gstr2aVsGstr3bReviewSummaryRespDto> dtos,
            String callFieldSection) {
        Gstr2aVsGstr3bReviewSummaryRespDto respDto = new Gstr2aVsGstr3bReviewSummaryRespDto();
        String description = null;
        String calField = null;
        BigDecimal igst = BigDecimal.ZERO;
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal cess = BigDecimal.ZERO;

        if (CollectionUtils.isNotEmpty(dtos)) {
            for (Gstr2aVsGstr3bReviewSummaryRespDto dto : dtos) {
                description = dto.getDescription();
                calField = dto.getCalFeild();
                igst = igst.add(dto.getIgst());
                sgst = sgst.add(dto.getSgst());
                cgst = cgst.add(dto.getCgst());
                cess = cess.add(dto.getCess());
            }
        } else {
            calField = callFieldSection;
        }

        respDto.setDescription(description);
        respDto.setCalFeild(calField);
        respDto.setIgst(igst);
        respDto.setCgst(cgst);
        respDto.setSgst(sgst);
        respDto.setCess(cess);

        return respDto;
    }

    private Map<String, String> buildDescriptionMap() {
        Map<String, String> natureSuppliesMap = Maps.newLinkedHashMap();
        natureSuppliesMap.put("A", "Taxes as per GSTR-2A ");
        natureSuppliesMap.put("A1",
				"Table 3 and 5* of GSTR-2A (* original invoice of RNV must "
				+ "be present in table 3 of GSTR-2A of previous months. "
				+ "Original document for RCR and RDR must be present in "
				+ "table 5 of GSTR-2A of previous months.)");
		  natureSuppliesMap.put("A2",
	                "Table 6 of GSTR-2A (ISD Credit eligible and in-eligible)");
	        natureSuppliesMap.put("A3",
	                "IMPG and IMPGSEZ of GSTR-2A");
        natureSuppliesMap.put("B", "Taxes as per GSTR-3B    ");
        natureSuppliesMap.put("B1",
                "Table 4A (1) of GSTR-3B (Import of Goods)  ");
        natureSuppliesMap.put("B2",
                "Table 4A (2) of GSTR-3B (Import of Services)   ");
        natureSuppliesMap.put("B3",
                "Table 4A (3) of GSTR-3B (Inward supplies liable to reverse charge) ");
        natureSuppliesMap.put("B4",
                "Table 4A (4) of GSTR-3B (Inward supplies from ISD) ");
        natureSuppliesMap.put("B5",
                "Table 4A (5) of GSTR-3B (All other ITC)    ");
        natureSuppliesMap.put("B6", "Table 4D of GSTR-3B (Ineligible ITC)   ");
        natureSuppliesMap.put("B7",
                "less: Table 3.1(d) of GSTR-3B (Inward supplies liable to reverse charge)   ");
        natureSuppliesMap.put("B8",
                "less: In-eligible credit on Import of Goods based on PR    ");
        natureSuppliesMap.put("C", "Difference ");
        return natureSuppliesMap;
    }
}
