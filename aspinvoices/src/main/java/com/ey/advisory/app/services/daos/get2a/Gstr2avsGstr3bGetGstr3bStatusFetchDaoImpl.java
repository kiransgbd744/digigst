package com.ey.advisory.app.services.daos.get2a;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Lists;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessStatusRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl")
public class Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl {

    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    public List<Object[]> getGstinsByEntityId(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) {

        List<String> gstins = getDataSecurity(criteria);
        List<Long> entityId = criteria.getEntityId();
        StringBuffer buffer = new StringBuffer();
        if (CollectionUtils.isNotEmpty(entityId)) {
            buffer.append("SELECT GSTIN FROM "
                    + "GSTIN_INFO WHERE ENTITY_ID = :entityId");
        }

        if (CollectionUtils.isNotEmpty(gstins)) {
            buffer.append(" AND GSTIN IN :gstins");
        }

        Query query = entityManager.createNativeQuery(buffer.toString());

        if (CollectionUtils.isNotEmpty(entityId)) {
            query.setParameter("entityId", entityId);
        }

        if (CollectionUtils.isNotEmpty(gstins)) {
            query.setParameter("gstins", gstins);
        }

        List<Object[]> gstinList = query.getResultList();

        return gstinList;
    }

    private List<String> getDataSecurity(
            Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
        Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
        List<String> gstinList = null;
        if (!dataSecAttrs.isEmpty()) {
            for (String key : dataSecAttrs.keySet()) {
                if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
                        && dataSecAttrs.get(OnboardingConstant.GSTIN) != null
                        && !dataSecAttrs.get(OnboardingConstant.GSTIN)
                                .isEmpty()) {
                    gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
                }
            }
        }
        return gstinList;
    }

    public List<Gstr1VsGstr3bProcessStatusRespDto> getDataUploadedStatusDetails(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) {
        List<String> gstins = getDataSecurity(criteria);
        String taxPeriodFrom = criteria.getTaxPeriodFrom();
        String taxPeriodTo = criteria.getTaxPeriodTo();
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("select GSTIN,RETURN_PERIOD,CREATED_ON,"
        		+ " CASE WHEN GET_RESPONSE_PAYLOAD IS not null THEN 'SUCCESS' "
        		+ "WHEN GET_RESPONSE_PAYLOAD IS NULL THEN 'FAILED' END AS STATUS"
        + " from GSTN_USER_REQUEST where return_type = 'GSTR3B' "
        + "AND REQUEST_TYPE = 'GET' AND GSTIN IN :gstin ");
        if (StringUtils.isNotBlank(taxPeriodFrom)
                && StringUtils.isNotBlank(taxPeriodTo)) {
            buffer.append(" AND DERIVED_RET_PERIOD BETWEEN "
                    + " :taxPeriodFrom AND :taxPeriodTo ");
        }
    
        Query query = entityManager.createNativeQuery(buffer.toString());

        query.setParameter("gstin", gstins);
        if (StringUtils.isNotBlank(taxPeriodFrom)) {
            query.setParameter("taxPeriodFrom",
                    GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
        }
        if (StringUtils.isNotBlank(taxPeriodTo)) {
            query.setParameter("taxPeriodTo",
                    GenUtil.convertTaxPeriodToInt(taxPeriodTo));
        }

        List<Object[]> itemsList = query.getResultList();

        return convertObjectListToDataList(itemsList);
    }

    private List<Gstr1VsGstr3bProcessStatusRespDto> convertObjectListToDataList(
            List<Object[]> itemsList) {
        List<Gstr1VsGstr3bProcessStatusRespDto> dtos = Lists.newArrayList();
        for (Object obj[] : itemsList) {
            Gstr1VsGstr3bProcessStatusRespDto dto = new Gstr1VsGstr3bProcessStatusRespDto();
            dto.setGstin(String.valueOf(obj[0]));
            if (obj[2] == null || obj[2]  == "null") {
                dto.setLastUpdatedTime("");
            } else {
            	Timestamp dt = (Timestamp) obj[2];
                LocalDateTime dateTimeFormatter = EYDateUtil
                        .toISTDateTimeFromUTC(dt.toLocalDateTime());
                DateTimeFormatter FOMATTER = DateTimeFormatter
                        .ofPattern("dd-MM-yyyy : HH:mm:ss");
                String newdate = FOMATTER.format(dateTimeFormatter);

                dto.setLastUpdatedTime(newdate);
            }
            dto.setStatus(String.valueOf(obj[3]));
            dtos.add(dto);
        }

        return dtos;
    }

}
