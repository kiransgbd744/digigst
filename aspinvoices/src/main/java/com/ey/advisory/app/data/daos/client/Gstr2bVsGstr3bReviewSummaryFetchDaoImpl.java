package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr2bVsGstr3bReviewSummaryFetchDaoImpl")
public class Gstr2bVsGstr3bReviewSummaryFetchDaoImpl {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr2bVsGstr3bReviewSummaryFetchDaoImpl.class);

    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    public List<Gstr2aVsGstr3bReviewSummaryRespDto> fetchGstr2aVsGstr3bRecords(
            Gstr1VsGstr3bProcessSummaryReqDto reqDto) {

        List<Long> entityId = reqDto.getEntityId();
        String taxPeriodFrom = reqDto.getTaxPeriodFrom();
        String taxPeriodTo = reqDto.getTaxPeriodTo();
        Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Gstr2bVsGstr3bReviewSummaryFetchDaoImpl->"
                    + "and selected criteria are:: entityId-> {}, "
                    + "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
                    reqDto);
        }

        String sgstin = null;

        List<String> gstinList = null;

        if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
                && dataSecAttrs.size() > 0) {
            for (String key : dataSecAttrs.keySet()) {

                if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
                    sgstin = key;
                    gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
                }
            }
        }

        StringBuilder queryStr = createQueryString(entityId, gstinList,
                taxPeriodFrom, taxPeriodTo, dataSecAttrs, sgstin);

        LOGGER.debug("outQueryStr-->" + queryStr);

        List<Gstr2aVsGstr3bReviewSummaryRespDto> finalDtoList = new ArrayList<>();
        try {

            Query Q = entityManager.createNativeQuery(queryStr.toString());
            if (StringUtils.isNotEmpty(taxPeriodFrom)
                    && StringUtils.isNotEmpty(taxPeriodTo)) {
                Q.setParameter("taxPeriodFrom",
                        GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
                Q.setParameter("taxPeriodTo",
                        GenUtil.convertTaxPeriodToInt(taxPeriodTo));
            }
            if (gstinList != null && gstinList.size() > 0
                    && !gstinList.contains("")) {
                Q.setParameter("sgstin", gstinList);
            }
            @SuppressWarnings("unchecked")
            List<Object[]> Qlist = Q.getResultList();
            LOGGER.error("bufferString-------------------------->" + Qlist);
            finalDtoList = convertGstr1vs3bRecordsIntoObject(Qlist, reqDto);

            LOGGER.debug("Data list from database is-->" + Qlist);

        } catch (Exception e) {
            LOGGER.error("Error in data process ->", e);
        }
        return finalDtoList;
    }

    private List<Gstr2aVsGstr3bReviewSummaryRespDto> convertGstr1vs3bRecordsIntoObject(
            List<Object[]> savedDataList,
            Gstr1VsGstr3bProcessSummaryReqDto reqDto) throws Exception {
        List<Gstr2aVsGstr3bReviewSummaryRespDto> summaryList = new ArrayList<Gstr2aVsGstr3bReviewSummaryRespDto>();
        if (CollectionUtils.isNotEmpty(savedDataList)) {
            for (Object[] data : savedDataList) {
                Gstr2aVsGstr3bReviewSummaryRespDto dto = new Gstr2aVsGstr3bReviewSummaryRespDto();

                dto.setCalFeild(String.valueOf(data[0]));
                dto.setIgst((BigDecimal) data[1]);
                dto.setCgst((BigDecimal) data[2]);
                dto.setSgst((BigDecimal) data[3]);
                dto.setCess((BigDecimal) data[4]);
                summaryList.add(dto);
            }
        }
        return summaryList;

    }

    public StringBuilder createQueryString(List<Long> entityId,
            List<String> gstinList, String taxPeriodFrom, String taxPeriodTo,
            Map<String, List<String>> dataSecAttrs, String sgstin) {

        if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
                && dataSecAttrs.size() > 0) {
            for (String key : dataSecAttrs.keySet()) {

                if (key.equalsIgnoreCase("GSTIN")) {
                    sgstin = key;
                }
            }
        }

        StringBuilder queryBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(taxPeriodFrom)
                && StringUtils.isNotEmpty(taxPeriodTo)) {
            queryBuilder
                    .append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom "
                            + " AND :taxPeriodTo");
        }

        if (gstinList != null && gstinList.size() > 0) {
            queryBuilder.append(" AND GSTIN IN :sgstin");
        }

        String condition = queryBuilder.toString();
        StringBuilder bufferString = new StringBuilder();
        bufferString.append("select SECTION_NAME,IFNULL(SUM(IGST_AMT),0)"
                + " AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,"
                + " IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0)"
                + " AS CESS FROM GSTR2A_VS_3B_COMPUTE where IS_ACTIVE = TRUE "
                + "and SECTION_NAME IN ('A','A1','A2','A3','B','B1','B2','B3',"
                + "'B4','B5','B6','B7','B8','C') ");
        if (!condition.equals("")) {
            bufferString.append(condition);
        }
        bufferString.append(" GROUP BY GSTIN,SECTION_NAME");
        LOGGER.debug("Gstr1vs3b Query from database is-->" + bufferString);

        LOGGER.error("bufferString-------------------------->" + bufferString);
        return bufferString;
    }

}
