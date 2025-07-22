package com.ey.advisory.app.services.jobs.gstr6;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr6DistrubutionStatusReportsRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Lists;

@Component("Gstr6DistrubutiionsStatusDownloadV2DaoImpl")
public class Gstr6DistrubutiionsStatusDownloadV2DaoImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr6SaveStatusDownloadDaoImpl.class);

    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    public List<Gstr6DistrubutionStatusReportsRespDto> fetchDistributionDataByReq(
            List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
        List<Gstr6DistrubutionStatusReportsRespDto> respDtos = Lists
                .newLinkedList();
        reqDtos.forEach(regDto -> {
            respDtos.addAll(fetchSaveSectionsDataByReq(regDto));
        });
        return respDtos;
    }

    private List<Gstr6DistrubutionStatusReportsRespDto> fetchSaveSectionsDataByReq(
            Gstr6SaveStatusDownloadReqDto regDto) {
        // String status = regDto.getStatus();
        String gstin = regDto.getGstin();
        String taxperiod = regDto.getTaxPeriod();
        List<String> gstr6Sections = regDto.getGstr6Sections();

        StringBuilder buildQuery = new StringBuilder();
        StringBuilder buildQuery1 = new StringBuilder();
        // if (StringUtils.isNotEmpty(status)) {
        // buildQuery1.append(" CFS = :status");
        // }
        if (StringUtils.isNotEmpty(gstin)) {
            buildQuery.append(" AND GSTIN = :gstin");
        }
        if (CollectionUtils.isNotEmpty(gstr6Sections)) {
            buildQuery1.append(" where CATEGORY IN :gstr6Sections");
        }

        if (StringUtils.isNotBlank(taxperiod)
                && StringUtils.isNotBlank(taxperiod)) {
            buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
        }
        String queryStr = creategstnTransQueryString(buildQuery.toString(),
                buildQuery1.toString());

        LOGGER.error("bufferString-------------------------->" + queryStr);
        Query outquery = entityManager.createNativeQuery(queryStr);

        if (StringUtils.isNotEmpty(gstin)) {
            outquery.setParameter("gstin", gstin);
        }
        if (StringUtils.isNotBlank(taxperiod)) {
            outquery.setParameter("taxperiod",
                    GenUtil.convertTaxPeriodToInt(taxperiod));
        }
        // if (StringUtils.isNotBlank(status)) {
        // outquery.setParameter("status", status);
        // }
        if (CollectionUtils.isNotEmpty(gstr6Sections)) {
            outquery.setParameter("gstr6Sections", gstr6Sections);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> list = outquery.getResultList();
        return list.parallelStream().map(o -> convertTransactionalLevel(o))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Gstr6DistrubutionStatusReportsRespDto convertTransactionalLevel(
            Object[] arr) {
        Gstr6DistrubutionStatusReportsRespDto obj = new Gstr6DistrubutionStatusReportsRespDto();

        obj.setCategory(arr[0] != null ? arr[0].toString() : null);
        obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
        obj.setIsdGstin(arr[2] != null ? arr[2].toString() : null);
        obj.setRecipentGstin(arr[3] != null ? arr[3].toString() : null);
        obj.setStateCode(arr[4] != null ? arr[4].toString() : null);
        obj.setOriginalRecipeintGSTIN(
                arr[5] != null ? arr[5].toString() : null);
        obj.setOriginalStatecode(arr[6] != null ? arr[6].toString() : null);
        obj.setDocumentType(arr[7] != null ? arr[7].toString() : null);
        obj.setSupplyType(arr[8] != null ? arr[8].toString() : null);
        obj.setDocumentNumber(arr[9] != null ? arr[9].toString() : null);
        obj.setDocumentDate(arr[10] != null ? arr[10].toString() : null);
        obj.setOriginalDocumentNumber(
                arr[11] != null ? arr[11].toString() : null);
        obj.setOriginalDocumentDate(
                arr[12] != null ? arr[12].toString() : null);
        obj.setOriginalCreditNoteNumber(
                arr[13] != null ? arr[13].toString() : null);
        obj.setOriginalCreditNoteDate(
                arr[14] != null ? arr[14].toString() : null);
        obj.setEligibleIndicator(arr[15] != null ? arr[15].toString() : null);
        obj.setIgstAsIgst(arr[16] != null ? arr[16].toString() : null);
        obj.setIgstAsSgst(arr[17] != null ? arr[17].toString() : null);
        obj.setIgstAsCgst(arr[18] != null ? arr[18].toString() : null);
        obj.setSgstAsSgst(arr[19] != null ? arr[19].toString() : null);
        obj.setSgstAsIgst(arr[20] != null ? arr[20].toString() : null);
        obj.setCgstAsCgst(arr[21] != null ? arr[21].toString() : null);
        obj.setCgstAsIgst(arr[22] != null ? arr[22].toString() : null);
        obj.setCessAmount(arr[23] != null ? arr[23].toString() : null);

        return obj;

    }

    private String creategstnTransQueryString(String buildQuery,
            String buildQuery1) {

        LOGGER.error("bufferString-------------------------->" + buildQuery);
        return " SELECT Category,TAX_PERIOD, ISD_GSTIN, case when "
                + "length(REC_GSTIN) = 15 then REC_GSTIN else 'URD' END REC_GSTIN, "
                + "STATE_CODE, ORG_REC_GSTIN, ORG_STATE_CODE,ISD_DOC_TYPE,"
                + " SUPPLY_TYPE,DOC_NUMBER,DOC_DATE,ORG_CRDR_DOC_NUMBER,"
                + " ORG_CRDR_DOC_DATE, ORG_DOC_NUMBER,ORG_DOC_DATE, "
                + "ELIGIBLE_INDICATOR, IGST_AMT_AS_IGST,IGST_AMT_AS_SGST,"
                + "IGST_AMT_AS_CGST, SGST_AMT_AS_SGST,SGST_AMT_AS_IGST,"
                + "CGST_AMT_AS_CGST, CGST_AMT_AS_IGST,CESS_AMT FROM (select "
                + "CASE WHEN ISD_DOC_TYPE IN ('ISD','ISDUR','ISDCN','ISDCNUR')"
                + " THEN 'DISTRIBUTION' END as Category, TAX_PERIOD,GSTIN as"
                + " ISD_GSTIN,CPTY AS REC_GSTIN, STATE_CODE, NULL AS "
                + "ORG_REC_GSTIN,null as ORG_STATE_CODE,ISD_DOC_TYPE,"
                + " NULL AS SUPPLY_TYPE,DOC_NUMBER,DOC_DATE, NULL AS"
                + " ORG_DOC_NUMBER, NULL AS ORG_DOC_DATE,CRDR_DOC_NUMBER as"
                + " ORG_CRDR_DOC_NUMBER, CRDR_DOC_DATE AS ORG_CRDR_DOC_DATE,"
                + "ELIGIBLE_INDICATOR, IGST_AMT_AS_IGST,IGST_AMT_AS_SGST,"
                + "IGST_AMT_AS_CGST, SGST_AMT_AS_SGST,SGST_AMT_AS_IGST,"
                + "CGST_AMT_AS_CGST, CGST_AMT_AS_IGST,CESS_AMT FROM "
                + "GETGSTR6_ISD_DETAILS WHERE IS_DELETE = FALSE " + buildQuery
                + " union all select CASE WHEN ISD_DOC_TYPE IN ('ISDA',"
                + "'ISDURA','ISDCNA','ISDCNURA') THEN 'REDISTRIBUTION' END "
                + "as Category,TAX_PERIOD,GSTIN as ISD_GSTIN,CPTY AS REC_GSTIN,"
                + " STATE_CODE, NULL AS ORG_REC_GSTIN,null as ORG_STATE_CODE,"
                + "ISD_DOC_TYPE, NULL AS SUPPLY_TYPE,DOC_NUMBER,DOC_DATE,"
                + " ORG_DOC_NUMBER, ORG_DOC_DATE,O_CRDR_DOC_NUMBER as"
                + " ORG_CRDR_DOC_NUMBER, O_CRDR_DOC_DATE AS ORG_CRDR_DOC_DATE,"
                + "ELIGIBLE_INDICATOR, IGST_AMT_AS_IGST,IGST_AMT_AS_SGST,"
                + "IGST_AMT_AS_CGST, SGST_AMT_AS_SGST,SGST_AMT_AS_IGST,"
                + "CGST_AMT_AS_CGST, CGST_AMT_AS_IGST,CESS_AMT FROM "
                + "GETGSTR6_ISDA_DETAILS WHERE IS_DELETE = FALSE " + buildQuery
                + " )" + buildQuery1;

    }

}
