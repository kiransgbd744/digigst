package com.ey.advisory.app.asprecon.reconresponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

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
@Component("Gstr2APRReconResponseDaoImpl")
public class Gstr2APRReconResponseDaoImpl implements Gstr2APRReconResponseDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Pair<List<Gstr2ReconResponseDashboardDto>, Integer> getReconResponseData(
			Gstr2ReconResultReqDto reqDto, int pageNum, int pageSize) {

		List<Gstr2ReconResponseDashboardDto> respList = null;
		Integer totalCnt = 0;
		try {

			List<String> gstins = reqDto.getGstins();

			Integer toTaxPerd = Integer.parseInt(reqDto.getToTaxPeriod());

			Integer fromTaxPerd = Integer.parseInt(reqDto.getFromTaxPeriod());
			String reconCriteria = reqDto.getReconCriteria();
			List<String> docNoList = reqDto.getDocNumber();
			//docNoList.replaceAll(String::toUpperCase);
			String queryString = createQueryString(reqDto, true);

			if (LOGGER.isDebugEnabled()) {
				String msg = "query string created for recon response 2APR data :"
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

			/*if (!Collections.isEmpty(reqDto.getDocNumber())) {
				q.setParameter("docNum", "%"+reqDto.getDocNumber()+"%");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1){
					q.setParameter("docNum", docNoList);
				}else{
					q.setParameter("docNum", "%" + docNoList.get(0) + "%");
			}
			}
			if (!Collections.isEmpty(reqDto.getReportType())) {
				q.setParameter("rptType", reqDto.getReportType());
			}

			if (!Collections.isEmpty(reqDto.getDocType())) {
				q.setParameter("docType", reqDto.getDocType());
			}

			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate()))

			{
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
			/*if (!Strings.isNullOrEmpty(reqDto.getDocNumber())) {
				q.setParameter("boeNum", "%" + reqDto.getDocNumber() + "%");
			}
			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate()))

			{
				q.setParameter("fromBoeDate", reqDto.getFromDocDate());
				q.setParameter("toBoeDate", reqDto.getToDocDate());
			}*/

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Number of Records got Gstins and its Status is :"
						+ list.size();
				LOGGER.debug(msg);
			}
			if ("2A".equalsIgnoreCase(reqDto.getType())) {
				respList = list.stream().map(o -> convert2APR(o, reconCriteria))
						.collect(Collectors.toCollection(ArrayList::new));
			} else {
				respList = list.stream().map(o -> convertPR(o, reconCriteria))
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

			/*if (!Collections.isEmpty(reqDto.getDocNumber())) {
				q1.setParameter("docNum", "%" + reqDto.getDocNumber() + "%");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1){
					q1.setParameter("docNum", docNoList);
					
				}else{
					q1.setParameter("docNum", "%" + docNoList.get(0) + "%");
			}
			}
			if (!Collections.isEmpty(reqDto.getReportType())) {
				q1.setParameter("rptType", reqDto.getReportType());
			}

			if (!Collections.isEmpty(reqDto.getDocType())) {
				q1.setParameter("docType", reqDto.getDocType());
			}

			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate()))

			{
				q1.setParameter("fromDocDate", reqDto.getFromDocDate());
				q1.setParameter("toDocDate", reqDto.getToDocDate());
			}

			if (!Collections.isEmpty(reqDto.getPos())) {
				q1.setParameter("pos", reqDto.getPos());
			}
			/*if (!Strings.isNullOrEmpty(reconCriteria)) {
				q1.setParameter("reconCriteria", reconCriteria);
			}*/
			/*if (!Strings.isNullOrEmpty(reqDto.getDocNumber())) {
				q1.setParameter("boeNum", "%" + reqDto.getDocNumber() + "%");
			}
			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate()))

			{
				q1.setParameter("fromBoeDate", reqDto.getFromDocDate());
				q1.setParameter("toBoeDate", reqDto.getToDocDate());
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

	private Gstr2ReconResponseDashboardDto convert2APR(Object[] arr, String reconCriteria) {

		Gstr2ReconResponseDashboardDto dto = new Gstr2ReconResponseDashboardDto();
		try {
			dto.setGstin(checkNull(arr[0]));
			dto.setVendorGstin(checkNull(arr[1]));
			dto.setDocType(checkNull(arr[3]));
			String docType = (arr[3] != null ? arr[3].toString() : null);
			
		if("Import".equalsIgnoreCase(reconCriteria)){
			dto.setBoeNumber(checkNull(arr[2]));
			dto.setBoedate(checkNull(arr[4]));

		}else{
			dto.setDocNumber(checkNull(arr[2]));
			dto.setDocdate(checkNull(arr[4]));
		}
			dto.setReportType(checkNull(arr[5]));

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
			dto.setRcmFlag(checkNull(arr[15]));
			dto.setCfs(checkNull(arr[18]));
			dto.setReturnPeriod((arr[19].toString()));
			dto.setVendorName(checkNull(arr[20]));
			dto.setSource("2A/6A");
			
		} catch (Exception ex) {
			String msg = String.format("Error while converting dto 2A %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private Gstr2ReconResponseDashboardDto convertPR(Object[] arr, String reconCriteria) {

		Gstr2ReconResponseDashboardDto dto = new Gstr2ReconResponseDashboardDto();
		try {
			dto.setGstin(checkNull(arr[0]));
			dto.setVendorGstin(checkNull(arr[1]));
			dto.setDocType(checkNull(arr[3]));
			String docType = (arr[3] != null ? arr[3].toString():null);

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
				
				dto.setAvalIgst(checkForNegativeValue(arr[18]));
				dto.setAvalCgst(checkForNegativeValue(arr[19]));
				dto.setAvalSgst(checkForNegativeValue(arr[20]));
				dto.setAvalCess(checkForNegativeValue(arr[21]));
			} else {
				dto.setTotalTax(checkNull(arr[6]));

				dto.setTaxablevalue(checkNull(arr[7]));
				dto.setIgst(checkNull(arr[8]));
				dto.setCgst(checkNull(arr[9]));
				dto.setSgst(checkNull(arr[10]));
				dto.setCess(checkNull(arr[11]));
				dto.setInvoiceVale(checkNull(arr[12]));
				dto.setAvalIgst((arr[18] != null) ? arr[18].toString() : "0.00");
				dto.setAvalCgst((arr[19] != null) ? arr[19].toString() : "0.00");
				dto.setAvalSgst((arr[20] != null) ? arr[20].toString() : "0.00");
				dto.setAvalCess((arr[21] != null) ? arr[21].toString() : "0.00");
			}

			dto.setPos(checkNull(arr[13]));
			dto.setReconLinkId(arr[14].toString());
			dto.setRcmFlag(checkNull(arr[15]));

			

			dto.setReturnPeriod((arr[22].toString()));
			dto.setVendorName(checkNull(arr[24]));
			dto.setSource("PR");
			dto.setItcReversal(arr[23]!=null?arr[23].toString():"");
			
		} catch (Exception ex) {
			String msg = String.format("Error while converting dto PR %s", ex);
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
			StringBuffer condition1 = new StringBuffer();

			StringBuffer condition2 = new StringBuffer();

			if (!Collections.isEmpty(req.getVendorGstins())) {
				// condition1.append("AND CUST_GSTIN IN (:vendrGstins)");
				condition2.append(" AND PR_SUPPLIER_GSTIN IN (:vendrGstins)");

			}
			if (!Collections.isEmpty(req.getVendorPans())) {
				condition2.append(
						" AND SUBSTR(PR_SUPPLIER_GSTIN,3,10) IN (:vendrPans) ");
			}

			if (!Collections.isEmpty(req.getReportType())) {
				condition2.append(" AND CURRENT_REPORT_TYPE IN (:rptType) ");
			}

			/*if (!Collections.isEmpty(req.getDocNumber())) {
				condition2.append(" AND PR_DOC_NUM LIKE (:docNum) ");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1){
					condition2.append(" AND PR_DOC_NUM IN (:docNum) ");
				}else{
					condition2.append(" AND PR_DOC_NUM LIKE :docNum ");
			}
			}
			if (!Collections.isEmpty(req.getDocType())) {
				condition2.append(
						"  AND (CASE WHEN PR_SUPPLY_TYPE IN ('IMPG','SEZG') THEN PR_SUPPLY_TYPE ELSE PR_INV_TYPE END) IN (:docType) ");
			}

			if (!Strings.isNullOrEmpty(req.getFromDocDate())
					&& !Strings.isNullOrEmpty(req.getToDocDate()))

			{
				condition2.append(
						" AND (DOC_DATE) BETWEEN :fromDocDate AND :toDocDate ");
			}

			if (!Collections.isEmpty(req.getPos())) {
				condition2.append(" AND PR_POS IN (:pos) ");
			}
			
			if("Import".equalsIgnoreCase(req.getReconCriteria())){
		
				condition2.append(" AND RECON_TYPE IN ('G2IMPG01','G2IMPG02') ");
	
			}
			if("Regular".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2NRML00','G6NRML00') ");
	
			}
			if("ISD".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2ISD00') ");
	
			}

			querry1.append(" WITH SUP_NAME AS" + " ("
					+ " 	SELECT DISTINCT SUPPLIER_GSTIN,CUST_SUPP_NAME AS VENDOR_NAME"
					+ " 	,(CASE WHEN MONTH(DOC_DATE)<4 "
					+ " THEN TO_VARCHAR(YEAR(DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(DOC_DATE)),2)"
					+ " ELSE TO_VARCHAR(YEAR(DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(DOC_DATE)+1),2) "
					+ " END||'|'||SUPPLIER_GSTIN||'|'"
					+ " || (CASE WHEN DOC_TYPE='INV' THEN 'R'"
					+ " WHEN DOC_TYPE='RNV' THEN 'R'"
					+ " WHEN DOC_TYPE='CR' THEN 'C'"
					+ " WHEN DOC_TYPE='DR' THEN 'D'"
					+ " WHEN DOC_TYPE='RCR' THEN 'C'"
					+ " WHEN DOC_TYPE='RDR' THEN 'D'" + " ELSE DOC_TYPE"
					+ " END)||'|'||DOC_NUM||'|'||CUST_GSTIN) AS INV_KEY "
					+ " FROM ANX_INWARD_DOC_HEADER"
					+ " 						WHERE DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod "
					+ "							AND IS_DELETE = FALSE"
					+ " 						AND IS_PROCESSED = TRUE"
					+ " 						AND SUPPLY_TYPE NOT IN ('CAN')"
					// + condition1
					+ " AND CUST_GSTIN IN (:gstins)"
					+ " )," + " PR AS" + " (" + " 			SELECT TOP 10000 * FROM (SELECT "
					+ " 			  PR_RECIPIENT_GSTIN,"
					+ " 			  PR_SUPPLIER_GSTIN,"
					+ " 			  (CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN PR_BILL_OF_ENTRY ELSE PR_DOC_NUM END) AS PR_DOC_NUM, "
					+ " 			  PR_INV_TYPE AS PR_DOC_TYPE,"
					+ " 			  TO_CHAR((CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN PR_BILL_OF_ENTRY_DATE ELSE PR_DOC_DATE END), 'YYYY-MM-DD') as DOC_DATE, "
					+ " 			  CURRENT_REPORT_TYPE,"
					+ " 			  IFNULL(PR_IGST, 0) + IFNULL(PR_CGST, 0) + IFNULL(PR_SGST, 0) + IFNULL(PR_CESS, 0) AS PR_TOTAL_TAX,"
					+ " 			  IFNULL(PR_TAXABLE_VALUE,0) AS PR_TAXABLE_VALUE,"
					+ " 			  IFNULL(PR_IGST, 0) AS PR_IGST, "
					+ " 			  IFNULL(PR_CGST, 0) AS PR_CGST,"
					+ " 			  IFNULL(PR_SGST, 0) AS PR_SGST,"
					+ " 			  IFNULL(PR_CESS, 0) AS PR_CESS,"
					+ " 			  IFNULL(PR_TAXABLE_VALUE,0) + IFNULL(PR_IGST, 0) + IFNULL(PR_CGST, 0) + IFNULL(PR_SGST, 0) + IFNULL(PR_CESS, 0) AS PR_INVOICE_VALUE,"
					+ " 			  PR_POS," + " 			  RECON_LINK_ID,"
					+ " 			  PR_REVERSE_CHARGE,"
					+ " 			  PR_INVOICE_KEY," + " 			  PR_ID,"
					+ " 			  IFNULL(AVAILABLE_IGST,0) AS AVAILABLE_IGST,"
					+ " 			  IFNULL(AVAILABLE_CGST,0) AS AVAILABLE_CGST,"
					+ " 			  IFNULL(AVAILABLE_SGST,0) AS AVAILABLE_SGST,"
					+ " 			  IFNULL(AVAILABLE_CESS,0) AS AVAILABLE_CESS,"
					+ " 			  PR_RET_PERIOD, ITC_REVERSAL_IDENTIFER,"
					+ "               RECON_TYPE, REPORT_TYPE_ID, PR_SUPPLY_TYPE, PR_INV_TYPE "
					+ " 			  FROM TBL_AUTO_2APR_LINK "
					+ " 			  WHERE PR_IS_ACTIVE = TRUE AND A2_IS_ACTIVE = (CASE WHEN REPORT_TYPE_ID IN (14,20) THEN FALSE ELSE TRUE END) "
					+ " 			  AND IS_ACTIVE = TRUE"
					//+ " 			  AND REPORT_TYPE_ID NOT IN (1,2,18,15,16,17)	"
					+ " 			  AND PR_RECIPIENT_GSTIN IN (:gstins)"
					+ " 			  AND PR_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod ) " 
					+ "               WHERE REPORT_TYPE_ID NOT IN (1,2,18,15,16,17) "
					 //+ condition2
					// + " AND PR_SUPPLIER_GSTIN IN (?)"
					// + " AND SUBSTR(PR_SUPPLIER_GSTIN,3,10) IN (?)"
					// + " AND PR_DOC_NUM LIKE '%(?)%'"
					// + " AND PR_DOC_TYPE IN (?) "
					// + " AND CURRENT_REPORT_TYPE IN (?) "
					//+ " UNION ALL" + " SELECT" + "  PR_RECIPIENT_GSTIN,"
					//+ "  PR_SUPPLIER_GSTIN," + "  PR_DOC_NUM,"
					//+ " (CASE WHEN PR_SUPPLY_TYPE IN ('IMPG','SEZG') THEN PR_SUPPLY_TYPE ELSE PR_INV_TYPE END) AS PR_DOC_TYPE,"
					//+ "   TO_CHAR(PR_DOC_DATE,'DD-MM-YYYY') as DOC_DATE, " + "  CURRENT_REPORT_TYPE,"
					//+ "  IFNULL(PR_IGST, 0) + IFNULL(PR_CGST, 0) + IFNULL(PR_SGST, 0) + IFNULL(PR_CESS, 0) AS PR_TOTAL_TAX,"
					//+ "  IFNULL(PR_TAXABLE_VALUE,0) AS PR_TAXABLE_VALUE,"
					//+ "  IFNULL(PR_IGST, 0) AS PR_IGST, "
					//+ "  IFNULL(PR_CGST, 0) AS PR_CGST,"
					//+ "  IFNULL(PR_SGST, 0) AS PR_SGST,"
					//+ "  IFNULL(PR_CESS, 0) AS PR_CESS,"
					//+ "  IFNULL(PR_TAXABLE_VALUE,0) + IFNULL(PR_IGST, 0) + IFNULL(PR_CGST, 0) + IFNULL(PR_SGST, 0) + IFNULL(PR_CESS, 0) AS PR_INVOICE_VALUE,"
					//+ "  PR_POS," + " RECON_LINK_ID," + "  PR_REVERSE_CHARGE,"
					//+ "  PR_INVOICE_KEY," + " PR_ID,"
					//+ " IFNULL(AVAILABLE_IGST,0) AS AVAILABLE_IGST,"
					//+ "  IFNULL(AVAILABLE_CGST,0) AS AVAILABLE_CGST,"
					//+ "  IFNULL(AVAILABLE_SGST,0) AS AVAILABLE_SGST,"
					//+ "  IFNULL(AVAILABLE_CESS,0) AS AVAILABLE_CESS,"
					//+ "  PR_RET_PERIOD, ITC_REVERSAL_IDENTIFER, RECON_TYPE " + "  FROM TBL_AUTO_2APR_LINK"
					//+ "  WHERE PR_IS_ACTIVE = TRUE AND A2_IS_ACTIVE = FALSE"
					//+ "  AND IS_ACTIVE = TRUE" + "   AND REPORT_TYPE_ID IN (14,20) "
					//+ "  AND PR_ID IS NOT NULL"
					//+ "  AND PR_RECIPIENT_GSTIN IN (:gstins)"
					//+ "  AND PR_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod "
					+ condition2);

			// + " AND PR_SUPPLIER_GSTIN IN (?)"
			// + " AND SUBSTR(PR_SUPPLIER_GSTIN,3,10) IN (?)"
			// + " AND PR_DOC_NUM LIKE '%(?)%'"
			// + " AND PR_DOC_TYPE IN (?) "
			// + " AND CURRENT_REPORT_TYPE IN (?);"

			if (flag) {
				querry1.append(" ) SELECT TOP 10000 PR.*,SUP.VENDOR_NAME"
						+ " FROM PR " + " LEFT JOIN SUP_NAME SUP"
						+ " ON PR.PR_INVOICE_KEY= SUP.INV_KEY ORDER BY PR_INVOICE_KEY LIMIT :pageSize OFFSET :pageNum ; ");
			} else {
				querry1.append(" ) SELECT COUNT(*) " + " FROM PR "
						+ " LEFT JOIN SUP_NAME SUP"
						+ " ON PR.PR_INVOICE_KEY= SUP.INV_KEY; ");
			}
		} else {

			StringBuffer condition2 = new StringBuffer();

			if (!Collections.isEmpty(req.getVendorGstins())) {
				// condition1.append("AND CUST_GSTIN IN (:vendrGstins)");
				condition2.append(" AND A2_SUPPLIER_GSTIN IN (:vendrGstins)");

			}
			if (!Collections.isEmpty(req.getVendorPans())) {
				condition2.append(
						" AND SUBSTR(A2_SUPPLIER_GSTIN,3,10) IN (:vendrPans) ");
			}
			/*if (!Collections.isEmpty(req.getDocNumber())) {
				condition2.append(" AND A2_DOC_NUM LIKE :docNum ");
			}*/
			if (docNoList != null && !docNoList.isEmpty()) {
				if (docNoList.size() > 1){
					condition2.append(" AND A2_DOC_NUM IN (:docNum) ");
				}else{
					condition2.append(" AND A2_DOC_NUM LIKE :docNum ");
			}
			}
			if (!Collections.isEmpty(req.getReportType())) {
				condition2.append(" AND CURRENT_REPORT_TYPE IN (:rptType) ");
			}

			if (!Collections.isEmpty(req.getDocType())) {
				condition2.append(
						" AND  (CASE WHEN A2_SUPPLY_TYPE IN ('IMPG','SEZG') THEN A2_SUPPLY_TYPE ELSE A2_INV_TYPE END) IN (:docType) ");

			}

			if (!Strings.isNullOrEmpty(req.getFromDocDate())
					&& !Strings.isNullOrEmpty(req.getToDocDate()))

			{
				condition2.append(
						" AND (DOC_DATE) BETWEEN :fromDocDate AND :toDocDate ");
			}

			if (!Collections.isEmpty(req.getPos())) {
				condition2.append(" AND A2_POS IN (:pos) ");
			}
			/*if (!Strings.isNullOrEmpty(req.getReconCriteria())) {
				condition2.append(" AND A2_RECON_CRITERIA = :reconCriteria ");
			}*/
			/*if (!Strings.isNullOrEmpty(req.getDocNumber())) {
				condition2.append(" AND A2_BOE_NUM LIKE :boeNum ");
			}
			if (!Strings.isNullOrEmpty(req.getFromDocDate())
					&& !Strings.isNullOrEmpty(req.getToDocDate()))

			{
				condition2.append(
						" AND TO_CHAR(A2_BOE_DATE,'YYYY-MM-DD') BETWEEN :fromBoeDate AND :toBoeDate ");
			}*/
			if("Import".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2IMPG01','G2IMPG02') ");
	
			}
			if("Regular".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2NRML00','G6NRML00') ");
	
			}
			if("ISD".equalsIgnoreCase(req.getReconCriteria())){
				
				condition2.append(" AND RECON_TYPE IN ('G2ISD00') ");
	
			}

			querry1.append(" WITH VEN AS " + " ( "
					+ " 	SELECT DISTINCT VENDOR_GSTIN AS GSTIN,LEGAL_NAME AS VENDOR_NAME "
					+ " 	FROM TBL_VENDOR_MASTER_CONFIG "
					+ " 	WHERE GSTIN_STATUS IN ('Active','Cancelled','Suspended') "
					+ " 	AND LEGAL_NAME IS NOT NULL "
				//	+ " 	AND RECIPIENT_GSTIN IN (:gstins) " 
					+ " ), "
					+ " A2 AS " + " ( " + "SELECT * FROM ( SELECT "
					+ " 			  A2_RECIPIENT_GSTIN, "
					+ " 			  A2_SUPPLIER_GSTIN, "
					+ " 			  (CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN cast(A2_BILL_OF_ENTRY as varchar) ELSE A2_DOC_NUM END) AS A2_DOC_NUM, "
					+ " 			  (CASE WHEN A2_SUPPLY_TYPE IN ('IMPG','SEZG') THEN A2_SUPPLY_TYPE ELSE A2_INV_TYPE END) AS A2_DOC_TYPE, "
					+ " 			  TO_CHAR((CASE WHEN RECON_TYPE LIKE '%IMPG%' THEN to_date(A2_BILL_OF_ENTRY_DATE,'DD-MM-YYYY') ELSE A2_DOC_DATE END), 'YYYY-MM-DD') as DOC_DATE, "
					+ " 			  CURRENT_REPORT_TYPE, "
					+ " 			  IFNULL(A2_IGST, 0) + IFNULL(A2_CGST, 0) + IFNULL(A2_SGST, 0) + IFNULL(A2_CESS, 0) AS A2_TOTAL_TAX, "
					+ " 			  IFNULL(A2_TAXABLE_VALUE,0) AS A2_TAXABLE_VALUE, "
					+ " 			  IFNULL(A2_IGST, 0) AS A2_IGST,  "
					+ " 			  IFNULL(A2_CGST, 0) AS A2_CGST, "
					+ " 			  IFNULL(A2_SGST, 0) AS A2_SGST, "
					+ " 			  IFNULL(A2_CESS, 0) AS A2_CESS, "
					+ " 			  IFNULL(A2_TAXABLE_VALUE,0) + IFNULL(A2_IGST, 0) + IFNULL(A2_CGST, 0) + IFNULL(A2_SGST, 0) + IFNULL(A2_CESS, 0) AS A2_INVOICE_VALUE, "
					+ " 			  A2_POS, "
					+ " 			  RECON_LINK_ID, "
					+ " 			  A2_REVERSE_CHARGE, "
					+ " 			  A2_INVOICE_KEY, "
					+ " 			  A2_ID, " + " 			  A2_CFS AS CFS, "
					+ " 			  A2_RET_PERIOD, RECON_TYPE, REPORT_TYPE_ID, A2_SUPPLY_TYPE, A2_INV_TYPE "
					+ " 			  FROM TBL_AUTO_2APR_LINK "
					+ " 			  WHERE PR_IS_ACTIVE = (CASE WHEN REPORT_TYPE_ID IN (13,21) THEN FALSE ELSE TRUE END) AND A2_IS_ACTIVE = TRUE "
					+ " 			  AND IS_ACTIVE = TRUE "
					//+ " 			  AND REPORT_TYPE_ID NOT IN (1,2,18,15,16,17) "
					+ " 			  AND A2_ID IS NOT NULL		 "
					+ " 			  AND A2_RECIPIENT_GSTIN IN (:gstins) "
					+ " AND A2_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod) WHERE REPORT_TYPE_ID NOT IN (1,2,18,15,16,17) "
					//+ condition2
					// + " AND A2_SUPPLIER_GSTIN IN (?) "
					// + " AND SUBSTR(A2_SUPPLIER_GSTIN,3,10) IN (?) "
					// + " AND A2_DOC_NUM LIKE '%(?)%' "
					// + " AND A2_DOC_TYPE IN (?) "
					// + " AND CURRENT_REPORT_TYPE IN (?) "
					//+ " 			  UNION ALL " + " 			  SELECT "
					//+ " 			  A2_RECIPIENT_GSTIN, "
					//+ " 			  A2_SUPPLIER_GSTIN, "
					/*+ " 			  A2_DOC_NUM, "
					+ " 			  (CASE WHEN A2_SUPPLY_TYPE IN ('IMPG','SEZG') THEN A2_SUPPLY_TYPE ELSE A2_INV_TYPE END) AS A2_DOC_TYPE, "
					+ " 			  TO_CHAR(A2_DOC_DATE,'DD-MM-YYYY') as DOC_DATE, "
					+ " 			  CURRENT_REPORT_TYPE, "
					+ " 			  IFNULL(A2_IGST, 0) + IFNULL(A2_CGST, 0) + IFNULL(A2_SGST, 0) + IFNULL(A2_CESS, 0) AS A2_TOTAL_TAX, "
					+ " 			  IFNULL(A2_TAXABLE_VALUE,0) AS A2_TAXABLE_VALUE, "
					+ " 			  IFNULL(A2_IGST, 0) AS A2_IGST,  "
					+ " 			  IFNULL(A2_CGST, 0) AS A2_CGST, "
					+ " 			  IFNULL(A2_SGST, 0) AS A2_SGST, "
					+ " 			  IFNULL(A2_CESS, 0) AS A2_CESS, "
					+ " 			  IFNULL(A2_TAXABLE_VALUE,0) + IFNULL(A2_IGST, 0) + IFNULL(A2_CGST, 0) + IFNULL(A2_SGST, 0) + IFNULL(A2_CESS, 0) AS A2_INVOICE_VALUE, "
					+ " 			  A2_POS, "
					+ " 			  RECON_LINK_ID, "
					+ " 			  A2_REVERSE_CHARGE, "
					+ " 			  A2_INVOICE_KEY, "
					+ " 			  A2_ID, " + " 			  A2_CFS AS CFS, "
					+ " 			  A2_RET_PERIOD, RECON_TYPE "
					+ " 			  FROM TBL_AUTO_2APR_LINK "
					+ " 			  WHERE PR_IS_ACTIVE = FALSE AND A2_IS_ACTIVE = TRUE "
					+ " 			  AND IS_ACTIVE = TRUE "
					+ " 			 AND REPORT_TYPE_ID IN (13,21) "
					+ " 			  AND A2_ID IS NOT NULL "
					+ " 			  AND A2_RECIPIENT_GSTIN IN (:gstins) "
					+ " AND A2_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod "*/
					+ condition2
					// + " AND A2_SUPPLIER_GSTIN IN (?) "
					// + " AND SUBSTR(A2_SUPPLIER_GSTIN,3,10) IN (?) "
					// + " AND A2_DOC_NUM LIKE '%(?)%' "
					// + " AND A2_DOC_TYPE IN (?) "
					// + " AND CURRENT_REPORT_TYPE IN (?) "
					+ " ) ");

			if (flag) {

				querry1.append(" SELECT TOP 10000 A2.*,VEN.VENDOR_NAME "
						+ " FROM A2  " + " LEFT JOIN VEN  " + " ON  "
				//		+ " A2.A2_RECIPIENT_GSTIN = VEN.RECIPIENT_GSTIN "
						+ " A2.A2_SUPPLIER_GSTIN= VEN.GSTIN LIMIT :pageSize OFFSET :pageNum ; ");
			} else {
				querry1.append(" SELECT COUNT(*) " + " FROM A2  "
						+ " LEFT JOIN VEN  " + " ON  "
				//		+ " A2.A2_RECIPIENT_GSTIN = VEN.RECIPIENT_GSTIN "
						+ " A2.A2_SUPPLIER_GSTIN= VEN.GSTIN ; ");
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
	
	public static void main(String[] args) {
		Map<Integer, Integer> table = new HashMap<>();
		int count = table.containsKey(5) ? table.get(5) : 0;
		 table.put(5, count + 1);
		 System.out.println(table);
		 int count1 = table.containsKey(5) ? table.get(5) : 0;
		 table.put(5, count1 + 1);
		 System.out.println(table);
	}
}
