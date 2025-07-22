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

import com.ey.advisory.app.docs.dto.simplified.Geetgstr2xScreensRespDto;
import com.ey.advisory.app.docs.dto.simplified.TDSDownlaodRequestDto;
import com.ey.advisory.common.GenUtil;

@Component("GetGstr2xScreensDaoImpl")
public class GetGstr2xScreensDaoImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GetGstr2xScreensDaoImpl.class);

    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    public List<Geetgstr2xScreensRespDto> responsebyTdsReq(
            TDSDownlaodRequestDto criteria) {

        String taxperiod = criteria.getTaxPeriod();
        String type = criteria.getType();
        List<String> gstinList = criteria.getGstin();
        StringBuilder buildQuery = new StringBuilder();
        StringBuilder buildQuery1 = new StringBuilder();

        if (gstinList != null && gstinList.size() > 0) {
            buildQuery.append(" AND HDR.GSTIN IN :gstinList");
        }
        if (StringUtils.isNotEmpty(type) && type!=null) {
            buildQuery.append(" AND HDR.RECORD_TYPE = :type");
        }
        if (StringUtils.isNotBlank(taxperiod)
                && StringUtils.isNotBlank(taxperiod)) {
            buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
        }

        String queryStr = creategstnTransQueryString(buildQuery.toString(),
                buildQuery1.toString());

        Query outquery = entityManager.createNativeQuery(queryStr);

        if (StringUtils.isNotBlank(taxperiod)) {
            outquery.setParameter("taxperiod",
                    GenUtil.convertTaxPeriodToInt(taxperiod));
        }
        if (CollectionUtils.isNotEmpty(gstinList)) {
            outquery.setParameter("gstinList", gstinList);
        }
        if (StringUtils.isNotEmpty(type) && type!=null) {
            outquery.setParameter("type", type);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> list = outquery.getResultList();
        return list.parallelStream().map(o -> convertTransactionalLevel(o))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Geetgstr2xScreensRespDto convertTransactionalLevel(Object[] arr) {
        Geetgstr2xScreensRespDto obj = new Geetgstr2xScreensRespDto();
        obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
        obj.setTaxPayerGSTIN(arr[1] != null ? arr[1].toString() : null);
        obj.setType(arr[2] != null ? arr[2].toString() : null);
        obj.setAccNoofRecord(arr[3] != null ? arr[3].toString() : null);
        obj.setAccTotalAmoun(arr[4] != null ? arr[4].toString() : null);
        obj.setAccIgst(arr[5] != null ? arr[5].toString() : null);
        obj.setAccCgst(arr[6] != null ? arr[6].toString() : null);
        obj.setAccSgst(arr[7] != null ? arr[7].toString() : null);
        
        obj.setRejNoofRecord(arr[8] != null ? arr[8].toString() : null);
        obj.setRejTotalAmoun(arr[9] != null ? arr[9].toString() : null);
        obj.setRejIgst(arr[10] != null ? arr[10].toString() : null);
        obj.setRejCgst(arr[11] != null ? arr[11].toString() : null);
        obj.setRejSgst(arr[12] != null ? arr[12].toString() : null);
        return obj;

    }

    private String creategstnTransQueryString(String buildQuery,
            String buildQuery1) {

        return " SELECT RET_PERIOD,GSTIN,TYPE, SUM(ACC_NO_OF_RECORDS)ACC_NO_OF_RECORDS,"
        		+ "SUM(ACC_TOT_AMT)ACC_TOT_AMT, SUM(ACC_IGST)ACC_IGST,SUM(ACC_CGST)ACC_CGST,"
        		+ "SUM(ACC_SGST)ACC_SGST, SUM(REJ_NO_OF_RECORDS)REJ_NO_OF_RECORDS,SUM(REJ_TOT_AMT)REJ_TOT_AMT, "
        		+ "SUM(REJ_IGST)REJ_IGST,SUM(REJ_CGST)REJ_CGST,SUM(REJ_SGST)REJ_SGST "
        		+ "FROM ( SELECT RET_PERIOD,GSTIN,TYPE,ACC_NO_OF_RECORDS,ACC_TOT_AMT,ACC_IGST,ACC_CGST,ACC_SGST, "
        		+ "0 REJ_NO_OF_RECORDS,0 REJ_TOT_AMT,0 REJ_IGST,0 REJ_CGST,0 REJ_SGST "
        		+ "FROM ( select HDR.RET_PERIOD,HDR.GSTIN, RECORD_TYPE AS TYPE, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'A' THEN TOT_RECORD END AS ACC_NO_OF_RECORDS, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'A' THEN TOT_VALUE END AS ACC_TOT_AMT, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'A' THEN TOT_IGST END AS ACC_IGST, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'A' THEN TOT_CGST END AS ACC_CGST, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'A' THEN TOT_SGST END AS ACC_SGST "
        		+ "FROM GETGSTR2X_SUMMARY HDR LEFT JOIN GETANX1_BATCH_TABLE BT "
        		+ "ON HDR.BATCH_ID = BT.ID "
        		+ "WHERE HDR.IS_DELETE=FALSE "
        		+ buildQuery + " ) "
        		+ " UNION ALL "
        		+ "SELECT RET_PERIOD,GSTIN,TYPE,0 ACC_NO_OF_RECORDS,0 ACC_TOT_AMT,0 ACC_IGST,0 ACC_CGST,"
        		+ "0 ACC_SGST, REJ_NO_OF_RECORDS,REJ_TOT_AMT,REJ_IGST,REJ_CGST,REJ_SGST "
        		+ "FROM ( select HDR.RET_PERIOD,HDR.GSTIN, RECORD_TYPE AS TYPE, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'R' THEN TOT_RECORD END AS REJ_NO_OF_RECORDS, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'R' THEN TOT_VALUE END AS REJ_TOT_AMT, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'R' THEN TOT_IGST END AS REJ_IGST, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'R' THEN TOT_CGST END AS REJ_CGST, "
        		+ "CASE WHEN RECORD_TYPE_PARAMTR = 'R' THEN TOT_SGST END AS REJ_SGST "
        		+ " FROM GETGSTR2X_SUMMARY HDR LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
        		+ "WHERE HDR.IS_DELETE=FALSE "
        		+ buildQuery + " ) ) "
        		+ " GROUP BY RET_PERIOD,GSTIN,TYPE " 
                + " ORDER BY GSTIN,TYPE ";
        
                

    }

}
