package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bProcessSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

@Component("Gstr2aVsGstr3bPrSummaryFetchDaoImpl")
public class Gstr2aVsGstr3bPrSummaryFetchDaoImpl {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr2aVsGstr3bPrSummaryFetchDaoImpl.class);

    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    @Autowired
    @Qualifier("DefaultGSTNAuthTokenService")
    private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

    @Autowired
    @Qualifier("getAnx1BatchRepository")
    private GetAnx1BatchRepository getAnx1BatchRepository;

    @Autowired
    @Qualifier("StatecodeRepository")
    private StatecodeRepository statecodeRepository;

    public List<Gstr2aVsGstr3bProcessSummaryRespDto> fetchGstr2aVsGstr3bRecords(
            Gstr1VsGstr3bProcessSummaryReqDto reqDto) {

        List<Long> entityId = reqDto.getEntityId();
        String taxPeriodFrom = reqDto.getTaxPeriodFrom();
        String taxPeriodTo = reqDto.getTaxPeriodTo();
        Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
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

        List<Gstr2aVsGstr3bProcessSummaryRespDto> finalDtoList = new ArrayList<>();
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

    private List<Gstr2aVsGstr3bProcessSummaryRespDto> convertGstr1vs3bRecordsIntoObject(
            List<Object[]> savedDataList,
            Gstr1VsGstr3bProcessSummaryReqDto reqDto) throws Exception {
        List<Gstr2aVsGstr3bProcessSummaryRespDto> summaryList = new ArrayList<Gstr2aVsGstr3bProcessSummaryRespDto>();
        if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
            for (Object[] data : savedDataList) {
                Gstr2aVsGstr3bProcessSummaryRespDto dto = new Gstr2aVsGstr3bProcessSummaryRespDto();
                dto.setGstin(String.valueOf(data[0]));
                dto.setSectionName(String.valueOf(data[1]));
                dto.setStatus(String.valueOf(data[2]));
                if (data[3] == null || data[3] == "null") {
                    dto.setTimestamp("");
                } else {
                    Timestamp date = (Timestamp) data[3];
                    LocalDateTime dt = date.toLocalDateTime();
                    LocalDateTime dateTimeFormatter = EYDateUtil
                            .toISTDateTimeFromUTC(dt);
                    DateTimeFormatter FOMATTER = DateTimeFormatter
                            .ofPattern("dd-MM-yyyy : HH:mm:ss");
                    String newdate = FOMATTER.format(dateTimeFormatter);

                    dto.setTimestamp(newdate);
                }
                dto.setIgst((BigDecimal) data[4]);
                dto.setCgst((BigDecimal) data[5]);
                dto.setSgst((BigDecimal) data[6]);
                dto.setCess((BigDecimal) data[7]);

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
                    .append(" AND ST.FROM_DERIVED_RET_PERIOD = :taxPeriodFrom and "
                            + " ST.TO_DERIVED_RET_PERIOD  = :taxPeriodTo"
                            + " and CMP.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom "
                            + " AND :taxPeriodTo  ");
        }

        if (gstinList != null && gstinList.size() > 0) {
            queryBuilder.append(" AND CMP.GSTIN IN :sgstin");
        }

        String condition = queryBuilder.toString();
        StringBuilder bufferString = new StringBuilder();
        bufferString.append(" select CMP.GSTIN,CMP.SECTION_NAME,"
                + "ST.STATUS as RECON_STATUS,ST.CREATED_ON AS RECON_DATE,"
                + "IFNULL(SUM(CMP.IGST_AMT),0) AS IGST,IFNULL(SUM(CMP.CGST_AMT)"
                + ",0) AS CGST, IFNULL(SUM(CMP.SGST_AMT),0) AS SGST,"
                + "IFNULL(SUM(CMP.CESS_AMT),0) AS CESS FROM GSTR2A_VS_3B_STATUS"
                + " ST INNER JOIN GSTR2A_VS_3B_COMPUTE CMP ON ST.GSTIN = "
                + "CMP.GSTIN AND ST.IS_DELETE = FALSE AND CMP.IS_ACTIVE = TRUE"
                + " where CMP.SECTION_NAME IN ('A','B','C') ");
        if (!condition.equals("")) {
            bufferString.append(condition);
        }
        bufferString.append(
                " GROUP BY CMP.GSTIN,CMP.SECTION_NAME,ST.STATUS,ST.CREATED_ON");
        LOGGER.debug("Gstr1vs3b Query from database is-->" + bufferString);

        LOGGER.error("bufferString-------------------------->" + bufferString);
        return bufferString;
    }

}
