package com.ey.advisory.app.asprecon.reconresponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;
import com.ey.advisory.common.AppException;

import io.jsonwebtoken.lang.Collections;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2BPRReconResponseDaoImpl")
public class Gstr2BPRReconResponseDaoImpl implements Gstr2BPRReconResponseDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Override
	public Pair<List<Gstr2ReconResponseDashboardDto>, Integer> getReconResponseData(
			Gstr2ReconResultReqDto reqDto, int pageNum, int pageSize) {

		List<Gstr2ReconResponseDashboardDto> respList = null;
		Integer totalCnt = 0;
		try {

			final String optedOption3B = onbrdOptionOpted(
					Long.valueOf(reqDto.getEntityId()));

			List<String> gstins = reqDto.getGstins();

			Integer toTaxPerd = Integer.parseInt(reqDto.getToTaxPeriod());

			Integer fromTaxPerd = Integer.parseInt(reqDto.getFromTaxPeriod());

			String reconCriteria = reqDto.getReconCriteria();
			List<String> docNoList = reqDto.getDocNumber();

			String queryString = createQueryString(reqDto, true);

			if (LOGGER.isDebugEnabled()) {
				String msg = "qyery string created for recon response 2APR data :"
						+ queryString.toString() + "with params - "
						+ reqDto.toString();
				LOGGER.debug(msg);
			}

			Query q = entityManager.createNativeQuery(queryString);

			q.setParameter("gstins", gstins);
			q.setParameter("fromTaxPeriod", fromTaxPerd);
			q.setParameter("toTaxPeriod", toTaxPerd);

			if (!Collections.isEmpty(reqDto.getVendorGstins())) {
				q.setParameter("vendrGstins", reqDto.getVendorGstins());
			}

			if (!Collections.isEmpty(reqDto.getVendorPans())) {
				q.setParameter("vendrPans", reqDto.getVendorPans());
			}

			/*if (!Strings.isNullOrEmpty(reqDto.getDocNumber())) {
				q.setParameter("docNum", "%" + reqDto.getDocNumber() + "%");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1)
					q.setParameter("docNum", docNoList);
				else
					q.setParameter("docNum", "%" + docNoList.get(0) + "%");
			}
			if (!Collections.isEmpty(reqDto.getReportType())) {
				q.setParameter("rptType", reqDto.getReportType());
			}

			if (!Collections.isEmpty(reqDto.getDocType())) {
				q.setParameter("docType", reqDto.getDocType());
			}

			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate())) {
				q.setParameter("fromDocDate", reqDto.getFromDocDate());
				q.setParameter("toDocDate", reqDto.getToDocDate());
			}

			if (!Collections.isEmpty(reqDto.getPos())) {
				q.setParameter("pos", reqDto.getPos());
			}

			q.setParameter("pageNum", (pageNum*pageSize));

			q.setParameter("pageSize", pageSize);
			
			/*if (!Strings.isNullOrEmpty(reconCriteria)) {
				q.setParameter("reconCriteria", reconCriteria);
			}*/

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Number of Records got Gstins and its Status is :"
						+ list.size();
				LOGGER.debug(msg);
			}

			if ("2B".equalsIgnoreCase(reqDto.getType())) {
				respList = list.parallelStream().map(o -> convert2B(o, reconCriteria))
						.collect(Collectors.toCollection(ArrayList::new));
			} else {
				respList = list.parallelStream()
						.map(o -> convertPR(o, optedOption3B, reconCriteria))
						.collect(Collectors.toCollection(ArrayList::new));
			}

			String queryStringTotal = createQueryString(reqDto, false);

			if (LOGGER.isDebugEnabled()) {
				String msg = "query string created for recon response 2APR total count :"
						+ queryStringTotal.toString() + "with params - "
						+ reqDto.toString();
				LOGGER.debug(msg);
			}
			Query q1 = entityManager.createNativeQuery(queryStringTotal);

			q1.setParameter("gstins", gstins);
			q1.setParameter("fromTaxPeriod", fromTaxPerd);
			q1.setParameter("toTaxPeriod", toTaxPerd);

			if (!Collections.isEmpty(reqDto.getVendorGstins())) {
				q1.setParameter("vendrGstins", reqDto.getVendorGstins());
			}

			if (!Collections.isEmpty(reqDto.getVendorPans())) {
				q1.setParameter("vendrPans", reqDto.getVendorPans());
			}

			/*if (!Strings.isNullOrEmpty(reqDto.getDocNumber())) {
				q1.setParameter("docNum", "%" + reqDto.getDocNumber() + "%");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1)
					q1.setParameter("docNum", docNoList);
				else
					q1.setParameter("docNum", "%" + docNoList.get(0) + "%");
			}
			if (!Collections.isEmpty(reqDto.getReportType())) {
				q1.setParameter("rptType", reqDto.getReportType());
			}

			if (!Collections.isEmpty(reqDto.getDocType())) {
				q1.setParameter("docType", reqDto.getDocType());
			}

			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate())) {
				q1.setParameter("fromDocDate", reqDto.getFromDocDate());
				q1.setParameter("toDocDate", reqDto.getToDocDate());
			}

			if (!Collections.isEmpty(reqDto.getPos())) {
				q1.setParameter("pos", reqDto.getPos());
			}
			/*if (!Strings.isNullOrEmpty(reqDto.getReconCriteria())) {
				q1.setParameter("reconCriteria", reqDto.getReconCriteria());
			}*/

			@SuppressWarnings("unchecked")
			List<Long> totalCntlist = q1.getResultList();
			int obj = Integer.parseInt(totalCntlist.get(0).toString());
			if (obj <= 1000) {
				totalCnt = Integer.valueOf(obj);
			} else {
				totalCnt = Integer.valueOf(1000);
			}

			// totalcnt to be obtained
			if (LOGGER.isDebugEnabled()) {
				String msg = "Number of total records:" + totalCntlist.size();
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing query %s",
					ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		return new Pair<>(respList, totalCnt);
	}

	private Gstr2ReconResponseDashboardDto convertPR(Object[] arr,
			String optedOption3B, String reconCriteria) {
		Gstr2ReconResponseDashboardDto dto = new Gstr2ReconResponseDashboardDto();
		try {
			dto.setGstin(checkNull(arr[0]));
			dto.setVendorGstin(checkNull(arr[1]));
			dto.setDocType(checkNull(arr[3]));
			String docType = (arr[3] != null ? arr[3].toString() : null);
			dto.setReportType(checkNull(arr[5]));

			if("Import".equalsIgnoreCase(reconCriteria)){
				dto.setBoeNumber(checkNull(arr[2]));
				dto.setBoedate(checkNull(arr[4]));

			}else{
				dto.setDocNumber(checkNull(arr[2]));
				dto.setDocdate(checkNull(arr[4]));
			}
			
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR"))) {
				dto.setTotalTax(checkForNegativeValue(arr[6]));

				dto.setTaxablevalue(checkForNegativeValue(arr[7]));
				dto.setIgst(checkForNegativeValue(arr[8]));
				dto.setCgst(checkForNegativeValue(arr[9]));
				dto.setSgst(checkForNegativeValue(arr[10]));
				dto.setCess(checkForNegativeValue(arr[11]));
				
				dto.setInvoiceVale(checkForNegativeValue(arr[12]));
				
				dto.setAvalIgst(checkForNegativeValue(arr[19]));
				dto.setAvalCgst(checkForNegativeValue(arr[20]));
				dto.setAvalSgst(checkForNegativeValue(arr[21]));
				dto.setAvalCess(checkForNegativeValue(arr[22]));
			} else {
				dto.setTotalTax(checkNull(arr[6]));

				dto.setTaxablevalue(checkNull(arr[7]));
				dto.setIgst(checkNull(arr[8]));
				dto.setCgst(checkNull(arr[9]));
				dto.setSgst(checkNull(arr[10]));
				dto.setCess(checkNull(arr[11]));
				dto.setInvoiceVale(checkNull(arr[12]));
				
				dto.setAvalIgst((arr[19] != null) ? arr[19].toString() : "0.00");
				dto.setAvalCgst((arr[20] != null) ? arr[20].toString() : "0.00");
				dto.setAvalSgst((arr[21] != null) ? arr[21].toString() : "0.00");
				dto.setAvalCess((arr[22] != null) ? arr[22].toString() : "0.00");
			}

			dto.setPos(checkNull(arr[13]));
			dto.setReconLinkId(arr[14].toString());
			dto.setRcmFlag(checkNull(arr[15]));

			dto.setVendorName(checkNull(arr[18]));

			dto.setReturnPeriod((arr[23].toString()));
			dto.setSource("PR");
			dto.setItcReversal((arr[24] != null) ? arr[24].toString() : "");

			if ("A".equalsIgnoreCase(optedOption3B)) {
				if ("Addition in PR".equalsIgnoreCase(dto.getReportType()))
					dto.setIsHideFlag(true);
			}

		} catch (Exception ex) {
			String msg = String.format("Error while converting dto %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private Gstr2ReconResponseDashboardDto convert2B(Object[] arr, String reconCriteria) {
		
		Gstr2ReconResponseDashboardDto dto = new Gstr2ReconResponseDashboardDto();
		try {
			dto.setGstin(checkNull(arr[0]));
			dto.setVendorGstin(checkNull(arr[1]));
			dto.setDocType(checkNull(arr[3]));

			String docType = (arr[3] != null ? arr[3].toString() : null);

			dto.setReportType(checkNull(arr[5]));
			
			if("Import".equalsIgnoreCase(reconCriteria)){
				dto.setBoeNumber(checkNull(arr[2]));
				dto.setBoedate(checkNull(arr[4]));

			}else{
				dto.setDocNumber(checkNull(arr[2]));
				dto.setDocdate(checkNull(arr[4]));
			}

			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR"))) {
				dto.setTotalTax(checkForNegativeValue(arr[6]));

				dto.setTaxablevalue(checkForNegativeValue(arr[7]));
				dto.setIgst(checkForNegativeValue(arr[8]));
				dto.setCgst(checkForNegativeValue(arr[9]));
				dto.setSgst(checkForNegativeValue(arr[10]));
				dto.setCess(checkForNegativeValue(arr[11]));
				dto.setInvoiceVale(checkForNegativeValue(arr[12]));
			} else {
				dto.setTotalTax(checkNull(arr[6]));

				dto.setTaxablevalue(checkNull(arr[7]));
				dto.setIgst(checkNull(arr[8]));
				dto.setCgst(checkNull(arr[9]));
				dto.setSgst(checkNull(arr[10]));
				dto.setCess(checkNull(arr[11]));
				dto.setInvoiceVale(checkNull(arr[12]));
			}

			dto.setPos(checkNull(arr[13]));
			dto.setReconLinkId(arr[14].toString());
			dto.setVendorName(checkNull(arr[17]));
			dto.setRcmFlag(checkNull(arr[18]));

			dto.setReturnPeriod((arr[19].toString()));
			dto.setAvalIgst("0.00");
			dto.setAvalCgst("0.00");
			dto.setAvalSgst("0.00");
			dto.setAvalCess("0.00");

			dto.setSource("2B");
			
		} catch (Exception ex) {
			String msg = String.format("Error while converting dto %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private String checkNull(Object obj) {
		return (obj != null) ? obj.toString() : null;
	}

	public String createQueryString(Gstr2ReconResultReqDto req, boolean flag) {

		StringBuffer querry1 = new StringBuffer();
		List<String> docNoList = req.getDocNumber();


		if ("PR".equalsIgnoreCase(req.getType())) {

			StringBuffer condition2 = new StringBuffer();

			if (!Collections.isEmpty(req.getVendorGstins())) {
				condition2.append("  AND PR_SUPPLIER_GSTIN IN (:vendrGstins) ");

			}
			if (!Collections.isEmpty(req.getVendorPans())) {
				condition2.append(
						" AND SUBSTR(PR_SUPPLIER_GSTIN,3,10) IN (:vendrPans) ");
			}
			/*if (!Strings.isNullOrEmpty(req.getDocNumber())) {
				condition2.append("AND PR_DOC_NUM LIKE :docNum ");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1)
					condition2.append(" AND PR_DOC_NUM IN (:docNum) ");
				else
					condition2.append(" AND PR_DOC_NUM LIKE :docNum ");
			}
			if (!Collections.isEmpty(req.getReportType())) {
				condition2.append("AND CURRENT_REPORT_TYPE IN (:rptType)");
			}

			if (!Collections.isEmpty(req.getDocType())) {
				condition2.append(
						" AND PR_INV_TYPE IN (:docType) ");
			}

			if (!Strings.isNullOrEmpty(req.getFromDocDate())
					&& !Strings.isNullOrEmpty(req.getToDocDate())) {
				condition2.append(
						"  AND (DOC_DATE) BETWEEN :fromDocDate AND :toDocDate ");
			}

			if (!Collections.isEmpty(req.getPos())) {
				condition2.append("  AND PR_POS IN (:pos) ");
			}
			/*if (!Strings.isNullOrEmpty(req.getReconCriteria())) {
				condition2.append(" AND PR_RECON_CRITERIA = :reconCriteria ");
			}
*/
			if("Import".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2IMPG01','G2IMPG02') ");
	
			}
			if("Regular".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2NRML00','G6NRML00') ");
	
			}
			if("ISD".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2ISD01') ");
	
			}
			
			if (flag) {
				querry1.append(" SELECT TOP 10000 * FROM ( SELECT "
						+ " 			  PR_RECIPIENT_GSTIN, "
						+ " 			  PR_SUPPLIER_GSTIN, "
						+ " 			  (CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN PR_BILL_OF_ENTRY ELSE PR_DOC_NUM END) AS PR_DOC_NUM, "
						+ " 			  PR_INV_TYPE AS PR_DOC_TYPE, "
						+ " 			  TO_CHAR((CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN PR_BILL_OF_ENTRY_DATE ELSE PR_DOC_DATE END), 'YYYY-MM-DD') as DOC_DATE, "
						+ " 			  CURRENT_REPORT_TYPE, "
						+ " 			  IFNULL(PR_IGST, 0) + IFNULL(PR_CGST, 0) + IFNULL(PR_SGST, 0) + IFNULL(PR_CESS, 0) AS PR_TOTAL_TAX, "
						+ " 			  IFNULL(PR_TAXABLE_VALUE,0) AS PR_TAXABLE_VALUE, "
						+ " 			  IFNULL(PR_IGST, 0) AS PR_IGST,  "
						+ " 			  IFNULL(PR_CGST, 0) AS PR_CGST, "
						+ " 			  IFNULL(PR_SGST, 0) AS PR_SGST, "
						+ " 			  IFNULL(PR_CESS, 0) AS PR_CESS, "
						+ " 			  IFNULL(PR_TAXABLE_VALUE,0) + IFNULL(PR_IGST, 0) + IFNULL(PR_CGST, 0) + IFNULL(PR_SGST, 0) + IFNULL(PR_CESS, 0) AS PR_INVOICE_VALUE, "
						+ " 			  PR_POS, "
						+ " 			  RECON_LINK_ID, "
						+ " 			  REVERSE_CHARGE as PR_REVERSE_CHARGE, "
						+ " 			  PR_INVOICE_KEY, "
						+ " 			  PR_ID, "
						+ " 			  HDR.CUST_SUPP_NAME AS VENDOR_NAME, "
						+ " 			  IFNULL(AVAILABLE_IGST,0) AS AVAILABLE_IGST, "
						+ " 			  IFNULL(AVAILABLE_CGST,0) AS AVAILABLE_CGST, "
						+ " 			  IFNULL(AVAILABLE_SGST,0) AS AVAILABLE_SGST, "
						+ " 			  IFNULL(AVAILABLE_CESS,0) AS AVAILABLE_CESS, "
						+ " 			  PR_RET_PERIOD , ITC_REVERSAL_IDENTIFIER, RECON_TYPE, REPORT_TYPE_ID, PR_SUPPLY_TYPE, PR_INV_TYPE ");
			} else {
				querry1.append(" 	SELECT COUNT(RECON_LINK_ID) FROM (SELECT * ");
			}
			querry1.append(" 		  FROM TBL_LINK_2B_PR LNK "
					+ " 			  LEFT JOIN  "
					+ " 			  (SELECT DISTINCT SUPPLIER_GSTIN,CUST_SUPP_NAME,DOC_KEY,ID,REVERSE_CHARGE,DOC_DATE,DOC_TYPE,DOC_NUM,CUST_GSTIN,DERIVED_RET_PERIOD, "
					+ " 			   (CASE WHEN MONTH(DOC_DATE)<4  "
					+ " 					THEN TO_VARCHAR(YEAR(DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(DOC_DATE)),2) "
					+ " 					ELSE TO_VARCHAR(YEAR(DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(DOC_DATE)+1),2)  "
					+ " 				END||'|'||SUPPLIER_GSTIN||'|' "
					+ " 				 || (CASE WHEN DOC_TYPE='INV' THEN 'R' "
					+ " 				   WHEN DOC_TYPE='RNV' THEN 'R' "
					+ " 				   WHEN DOC_TYPE='CR' THEN 'C' "
					+ " 				   WHEN DOC_TYPE='DR' THEN 'D' "
					+ " 				   WHEN DOC_TYPE='RCR' THEN 'C' "
					+ " 				   WHEN DOC_TYPE='RDR' THEN 'D' "
					+ " 				   ELSE DOC_TYPE "
					+ " 				   END)||'|'||DOC_NUM||'|'||CUST_GSTIN) AS INV_KEY "
					+ " 			   FROM ANX_INWARD_DOC_HEADER  "
					+ " 			   WHERE IS_DELETE = FALSE AND IS_PROCESSED = TRUE "
					+ " 			   AND SUPPLY_TYPE<>'CAN' AND SUPPLIER_GSTIN IS NOT NULL "
					+ " 			   AND CUST_GSTIN IN (:gstins) "
					+ " 			   AND DERIVED_RET_PERIOD BETWEEN (:fromTaxPeriod) AND (:toTaxPeriod) "
					+ " 			   )HDR "
					+ " 			  ON LNK.PR_DOC_DATE = HDR.DOC_DATE " 
					+ " AND LNK.PR_SUPPLIER_GSTIN = HDR.SUPPLIER_GSTIN "
					+ " AND LNK.PR_RECIPIENT_GSTIN = HDR.CUST_GSTIN "
					+ " AND LNK.PR_INV_TYPE= HDR.DOC_TYPE "
					+ " AND LNK.PR_DOC_NUM = HDR.DOC_NUM "
					+ " WHERE IS_ACTIVE = TRUE AND PR_ID IS NOT NULL "
					//+ " AND REPORT_TYPE_ID not in (17,15,16,1,2,18) "
					+ " AND PR_RECIPIENT_GSTIN IN (:gstins) "
					+ " AND PR_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod ) "
					+ " WHERE REPORT_TYPE_ID not in (17,15,16,1,2,18) "
					// + " AND PR_SUPPLIER_GSTIN IN (?) "
					// + " AND SUBSTR(PR_SUPPLIER_GSTIN,3,10) IN (?) "
					// + " AND PR_DOC_NUM LIKE '%(?)%' "
					// + " AND PR_DOC_TYPE IN (?) "
					// + " AND CURRENT_REPORT_TYPE IN (?)"
					+ condition2);
			// + " AND B2_SUPPLIER_GSTIN IN (?) "
			// + " AND SUBSTR(B2_SUPPLIER_GSTIN,3,10) IN (?) "
			// + " AND B2_DOC_NUM LIKE '%(?)%' "
			// + " AND B2_DOC_TYPE IN (?) "
			// + " AND CURRENT_REPORT_TYPE IN (?);
			if (flag) {
				querry1.append("LIMIT :pageSize OFFSET :pageNum ; ");
			} else {
				querry1.append(" ;");
			}

		} else {

			StringBuffer condition2 = new StringBuffer();

			if (!Collections.isEmpty(req.getVendorGstins())) {
				condition2.append("  AND B2_SUPPLIER_GSTIN IN (:vendrGstins)");

			}
			if (!Collections.isEmpty(req.getVendorPans())) {
				condition2.append(
						" AND SUBSTR(B2_SUPPLIER_GSTIN,3,10) IN (:vendrPans)");
			}
			/*if (!Strings.isNullOrEmpty(req.getDocNumber())) {
				condition2.append("AND B2_DOC_NUM LIKE :docNum ");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1)
					condition2.append(" AND B2_DOC_NUM IN (:docNum) ");
				else
					condition2.append(" AND B2_DOC_NUM LIKE :docNum ");
			}
			if (!Collections.isEmpty(req.getReportType())) {
				condition2.append("AND CURRENT_REPORT_TYPE IN (:rptType)");
			}

			if (!Collections.isEmpty(req.getDocType())) {
				condition2.append(
						" AND (CASE WHEN B2_SUPPLY_TYPE IN ('IMPG','SEZG') THEN B2_SUPPLY_TYPE ELSE B2_INV_TYPE END) IN (:docType) ");
			}

			if (!Strings.isNullOrEmpty(req.getFromDocDate())
					&& !Strings.isNullOrEmpty(req.getToDocDate())) {
				condition2.append(
						"  AND (B2_DOC_DATE) BETWEEN :fromDocDate AND :toDocDate ");
			}

			if (!Collections.isEmpty(req.getPos())) {
				condition2.append("  AND B2_POS IN (:pos) ");
			}
			/*if (!Strings.isNullOrEmpty(req.getReconCriteria())) {
				condition2.append(" AND B2_RECON_CRITERIA = :reconCriteria ");
			}*/
            if("Import".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2IMPG01','G2IMPG02') ");
	
			}
			if("Regular".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2NRML00','G6NRML00') ");
	
			}
			if("ISD".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2ISD01') ");
	
			}

			if (flag) {
				querry1.append(" SELECT TOP 10000 * FROM ( SELECT "
						+ " 			  B2_RECIPIENT_GSTIN, "
						+ " 			  B2_SUPPLIER_GSTIN, "
						+ " 			  (CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN B2_BILL_OF_ENTRY ELSE B2_DOC_NUM END) AS B2_DOC_NUM, "
						+ " 			  (CASE WHEN B2_SUPPLY_TYPE IN ('IMPG','SEZG') THEN B2_SUPPLY_TYPE ELSE B2_INV_TYPE END) AS B2_DOC_TYPE, "
						+ " 			  TO_CHAR((CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN B2_BILL_OF_ENTRY_DATE ELSE B2_DOC_DATE END), 'YYYY-MM-DD') as B2_DOC_DATE, "
						+ " 			  CURRENT_REPORT_TYPE, "
						+ " 			  IFNULL(B2_IGST, 0) + IFNULL(B2_CGST, 0) + IFNULL(B2_SGST, 0) + IFNULL(B2_CESS, 0) AS B2_TOTAL_TAX, "
						+ " 			  IFNULL(B2_TAXABLE_VALUE,0) AS B2_TAXABLE_VALUE, "
						+ " 			  IFNULL(B2_IGST, 0) AS B2_IGST,  "
						+ " 			  IFNULL(B2_CGST, 0) AS B2_CGST, "
						+ " 			  IFNULL(B2_SGST, 0) AS B2_SGST, "
						+ " 			  IFNULL(B2_CESS, 0) AS B2_CESS, "
						+ " 			  IFNULL(B2_TAXABLE_VALUE,0) + IFNULL(B2_IGST, 0) + IFNULL(B2_CGST, 0) + IFNULL(B2_SGST, 0) + IFNULL(B2_CESS, 0) AS B2_INVOICE_VALUE, "
						+ " 			  B2_POS, "
						+ " 			  RECON_LINK_ID, "
						+ " 			  B2_INVOICE_KEY, "
						+ " 			  B2_ID, "
						+ " 			  VEN.VENDOR_NAME, "
						+ " 			  B2_REVERSE_CHARGE AS CFS, "
						+ " 			  B2_RET_PERIOD, RECON_TYPE, REPORT_TYPE_ID, B2_SUPPLY_TYPE, B2_INV_TYPE ");
			} else {
				querry1.append(" 	SELECT COUNT(RECON_LINK_ID) FROM (SELECT * ");
			}
			querry1.append(" 			  FROM TBL_LINK_2B_PR LNK "
					+ " 			  LEFT JOIN  " + " 			  ( "
					+ " 			  	SELECT DISTINCT VENDOR_GSTIN AS GSTIN,LEGAL_NAME AS VENDOR_NAME "
					+ " 				FROM TBL_VENDOR_MASTER_CONFIG "
					+ " 				WHERE GSTIN_STATUS IN ('Active','Cancelled','Suspended') "
					+ " 				AND LEGAL_NAME IS NOT NULL "
	//				+ " 				AND RECIPIENT_GSTIN IN (:gstins) "
					+ " 			   )VEN "
	//				+ " 			  ON LNK.B2_RECIPIENT_GSTIN = VEN.RECIPIENT_GSTIN "
					+ " 			  ON LNK.B2_SUPPLIER_GSTIN = VEN.GSTIN "
					+ " 			  WHERE IS_ACTIVE = TRUE AND B2_ID IS NOT NULL "
					//+ " 			  AND REPORT_TYPE_ID not in (17,15,16,1,2,18) "
					+ " 			  AND B2_RECIPIENT_GSTIN IN (:gstins) "
					+ " AND B2_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod ) WHERE REPORT_TYPE_ID not in (17,15,16,1,2,18) "
					+ condition2);
			// + " AND B2_SUPPLIER_GSTIN IN (?) "
			// + " AND SUBSTR(B2_SUPPLIER_GSTIN,3,10) IN (?) "
			// + " AND B2_DOC_NUM LIKE '%(?)%' "
			// + " AND B2_DOC_TYPE IN (?) "
			// + " AND CURRENT_REPORT_TYPE IN (?);
			if (flag) {
				querry1.append("LIMIT :pageSize OFFSET :pageNum ; ");
			} else {
				querry1.append(" ;");
			}

		}
		return querry1.toString();
	}

	private String checkForNegativeValue(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if (!value.toString().isEmpty()) {
					return "-" + value.toString().replaceFirst("-", "");
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("opted option for onboarding question - {}", optAns);
		}
		return optAns;
	}

}
