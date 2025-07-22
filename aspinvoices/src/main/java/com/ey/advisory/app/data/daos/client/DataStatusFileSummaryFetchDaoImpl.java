package com.ey.advisory.app.data.daos.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1.DataStatusFilesummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author V.Mule
 *
 */
@Component("DataStatusFileSummaryFetchDaoImpl")
public class DataStatusFileSummaryFetchDaoImpl
		implements DataStatusFileSummaryFetchDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static Integer answer = null;

	@Override
	public List<Object[]> fecthOutwardRawFileData(
			DataStatusFilesummaryReqDto filesummaryReqDto) {
		String queryStr = createOutwardRawQueryString(
				filesummaryReqDto.getFileId());
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, filesummaryReqDto);
		@SuppressWarnings("unchecked")
		List<Object[]> outwardList = q.getResultList();
		return outwardList;
	}

	@Override
	public List<Object[]> fetchOutwardB2cData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createB2cQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		List<Object[]> b2cList = q.getResultList();
		return b2cList;
	}

	@Override
	public List<Object[]> fetchOutwardTable4Data(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createTable4QueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> table4List = q.getResultList();
		return table4List;
	}

	@Override
	public List<Object[]> fecthInwardRawFileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createInwardRawQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> inwardList = q.getResultList();
		return inwardList;
	}

	@Override
	public List<Object[]> fecthInwardTable3h3iFileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createTable3h3iQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> table3h3iList = q.getResultList();
		return table3h3iList;
	}

	@Override
	public List<Object[]> fecthOthersRet1And1aFileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createOthersRet1And1aListQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> othersret1and1aList = q.getResultList();
		return othersret1and1aList;
	}

	@Override
	public List<Object[]> fecthOthersInterestFileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createothersInterestListQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> othersInterestList = q.getResultList();
		return othersInterestList;
	}

	@Override
	public List<Object[]> fecthOthersSetOffAndUtilFileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createothersSetOffAndUtilQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> othersSetOffAndUtilList = q.getResultList();
		return othersSetOffAndUtilList;
	}

	@Override
	public List<Object[]> fecthOthersRefundsFileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		String queryStr = createothersRefundsQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> othersRefundsList = q.getResultList();
		return othersRefundsList;
	}

	private String createothersRefundsQueryString() {
		return "SELECT RECEIVED_DATE,FILE_NAME,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
				+ "TABLE_SECTION,SUM(RECORD_COUNT) RECORD_COUNT, TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ "+IFNULL(CESS,0)) TOTAL_TAX,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,SUM(CESS) CESS"
				+ " FROM (SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,HDR.RETURN_PERIOD,"
				+ "'RET' RETURN_TYPE,'' TABLE_SECTION, COUNT(HDR.ID) AS "
				+ "RECORD_COUNT,0.0  AS TAXABLE_VALUE, SUM(CASE WHEN UPPER"
				+ "(DESCRIPTION) = 'IGST' THEN (IFNULL(TAX,0)+IFNULL(INTEREST,0)"
				+ "+IFNULL(PENALTY,0)+IFNULL(FEE,0)+IFNULL(OTHER,0)) END) IGST_AMT,"
				+ "SUM(CASE WHEN UPPER(DESCRIPTION) = 'CGST' THEN "
				+ "(IFNULL(TAX,0)+IFNULL(INTEREST,0)+IFNULL(PENALTY,0)+IFNULL"
				+ "(FEE,0)+IFNULL(OTHER,0)) END) CGST_AMT,SUM(CASE WHEN"
				+ " UPPER(DESCRIPTION) = 'SGST' THEN (IFNULL(TAX,0)+IFNULL"
				+ "(INTEREST,0)+IFNULL(PENALTY,0)+IFNULL(FEE,0)+IFNULL(OTHER,0))"
				+ " END) SGST_AMT,SUM(CASE WHEN UPPER(DESCRIPTION) = 'CESS' THEN "
				+ "(IFNULL(TAX,0)+IFNULL(INTEREST,0)+IFNULL(PENALTY,0)+IFNULL"
				+ "(FEE,0)+IFNULL(OTHER,0)) END) CESS "
				+ "FROM RET_PROCESSED_REFUND_FROM_E_CASHLEDGER HDR INNER JOIN"
				+ " FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID "
				+ "where HDR.FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ "GROUP BY FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,"
				+ "HDR.RETURN_PERIOD,DESCRIPTION) "
				+ "GROUP BY RECEIVED_DATE,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
				+ "TABLE_SECTION,TAXABLE_VALUE";
	}

	private String createothersSetOffAndUtilQueryString() {
		return "SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,DESCRIPTION TABLE_SECTION,COUNT(HDR.ID) AS"
				+ " RECORD_COUNT, 0.0  AS TAXABLE_VALUE,SUM(IFNULL"
				+ "(PAID_THROUGH_ITC_IGST,0)+IFNULL(PAID_THROUGH_ITC_CGST,0)+"
				+ "IFNULL(PAID_THROUGH_ITC_SGST,0)+IFNULL"
				+ "(PAID_THROUGH_ITC_CESS,0)) AS TOTAL_TAX,"
				+ "SUM(PAID_THROUGH_ITC_IGST) AS IGST_AMT,"
				+ "SUM(PAID_THROUGH_ITC_CGST) AS CGST_AMT,"
				+ " SUM(PAID_THROUGH_ITC_SGST) AS SGST_AMT,"
				+ " SUM(PAID_THROUGH_ITC_CESS) AS CESS FROM "
				+ "RET_PROCESSED_SETOFF_UTILIZATION HDR INNER JOIN "
				+ "FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID where FILE_ID IN "
				+ "(:fileIdsList) AND IS_DELETE = FALSE "
				+ "GROUP BY FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,DESCRIPTION";
	}

	private String createothersInterestListQueryString() {
		return "SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,RETURN_TABLE TABLE_SECTION,COUNT(HDR.ID) AS "
				+ "RECORD_COUNT, 0.0  AS TAXABLE_VALUE,SUM(IFNULL"
				+ "(INTEREST_IGST_AMT,0)+IFNULL(INTEREST_CGST_AMT,0)+"
				+ "IFNULL(INTEREST_SGST_AMT,0)+IFNULL(INTEREST_CESS_AMT,0))"
				+ " AS TOTAL_TAX, SUM(INTEREST_IGST_AMT) AS IGST_AMT,"
				+ " SUM(IFNULL(INTEREST_CGST_AMT,0)+IFNULL(LATEFEE_CGST_AMT,0)) "
				+ "AS CGST_AMT,SUM(IFNULL(INTEREST_SGST_AMT,0)+IFNULL"
				+ "(LATEFEE_SGST_AMT,0)) AS SGST_AMT,SUM(INTEREST_CESS_AMT) "
				+ "AS CESS FROM RET_PROCESSED_INTEREST_LATEFEE HDR "
				+ "INNER JOIN FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID where "
				+ "FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ " GROUP BY FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,RETURN_TABLE";
	}

	private String createOthersRet1And1aListQueryString() {
		return "SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,RETURN_TABLE TABLE_SECTION,COUNT(HDR.ID) AS"
				+ " RECORD_COUNT, SUM(TAXABLE_VALUE)  AS TAXABLE_VALUE,"
				+ " SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ "+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,SUM(IGST_AMT) AS IGST_AMT,"
				+ "SUM(CGST_AMT) AS CGST_AMT, SUM(SGST_AMT) AS SGST_AMT,"
				+ "SUM(CESS_AMT) AS CESS FROM RET_PROCESSED_USERINPUT HDR"
				+ " INNER JOIN FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID where "
				+ "FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ " GROUP BY FIL.RECEIVED_DATE,FIL.FILE_NAME,GSTIN,"
				+ "HDR.RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE ";
	}

	private String createTable3h3iQueryString() {
		return "SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,CUST_GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,TRAN_FLAG TABLE_SECTION,COUNT(HDR.ID) AS "
				+ "RECORD_COUNT,SUM(TAXABLE_VALUE)  AS TAXABLE_VALUE,"
				+ " SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ "+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,SUM(IGST_AMT) AS IGST_AMT,"
				+ "SUM(CGST_AMT) AS CGST_AMT, SUM(SGST_AMT) AS SGST_AMT,"
				+ " SUM(CESS_AMT) AS CESS,'SLF' DOC_TYPE FROM ANX_PROCESSED_3H_3I HDR"
				+ " INNER JOIN FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID "
				+ "where HDR.FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ " GROUP BY FIL.RECEIVED_DATE,FIL.FILE_NAME,CUST_GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE,TRAN_FLAG";
	}

	private String createTable4QueryString() {
		return "SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,RETURN_TYPE,'E-COM' TABLE_SECTION,"
				+ "COUNT(HDR.ID) AS RECORD_COUNT,SUM(TAXABLE_VALUE)  AS "
				+ "TAXABLE_VALUE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
				+ "+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
				+ " SUM(IGST_AMT) AS IGST_AMT, SUM(CGST_AMT) AS CGST_AMT, "
				+ " SUM(SGST_AMT) AS SGST_AMT, SUM(CESS_AMT) AS CESS"
				+ " FROM ANX_PROCESSED_TABLE4 HDR INNER JOIN FILE_STATUS FIL"
				+ " ON FIL.ID = HDR.FILE_ID where HDR.FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ " GROUP BY FIL.FILE_NAME,SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,RETURN_TYPE ";
	}

	private String createB2cQueryString() {
		return "SELECT FIL.RECEIVED_DATE,FIL.FILE_NAME,SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,RETURN_TYPE,'B2C' TABLE_SECTION,"
				+ "COUNT(HDR.ID) AS RECORD_COUNT,SUM(TAXABLE_VALUE)  AS "
				+ "TAXABLE_VALUE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
				+ "SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT, "
				+ "SUM(SGST_AMT) AS SGST_AMT, SUM(CESS_AMT) AS CESS,"
				+ "'INV' DOC_TYPE "
				+ "FROM ANX_PROCESSED_B2C HDR INNER JOIN FILE_STATUS FIL "
				+ "ON FIL.ID = HDR.FILE_ID where HDR.FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ " GROUP BY FIL.RECEIVED_DATE,FIL.FILE_NAME,SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "RETURN_TYPE";
	}

	private String createOutwardRawQueryString(List<Long> fileIdsList) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT HDR.DOC_DATE,FILE_NAME,SUPPLIER_GSTIN, ");
		sql.append("HDR.RETURN_PERIOD,RETURN_TYPE AS RET_TYPE, ");
		sql.append("TAX_DOC_TYPE||'-'||TABLE_SECTION as RETURN_SECTION, ");
		sql.append("COUNT(HDR.ID) AS RECORD_COUNT, ");
		sql.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE, ");
		sql.append("SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+ ");
		sql.append("IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+ ");
		sql.append("IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX, ");
		sql.append("SUM(IGST_AMT) AS IGST_AMT, ");
		sql.append("SUM(CGST_AMT) AS CGST_AMT, ");
		sql.append("SUM(SGST_AMT) AS SGST_AMT, ");
		sql.append("SUM(IFNULL(CESS_AMT_SPECIFIC,0)+ ");
		sql.append("IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS, ");
		sql.append("DOC_TYPE FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ");
		sql.append("FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID  where  ");
		if (fileIdsList != null && !fileIdsList.isEmpty()) {
			sql.append("HDR.FILE_ID IN (:fileIdsList) AND ");
		}
		sql.append("IS_DELETE = FALSE and ASP_INVOICE_STATUS = 2 ");
		sql.append("and COMPLIANCE_APPLICABLE=TRUE GROUP BY  ");
		sql.append("HDR.DOC_DATE,FILE_NAME,SUPPLIER_GSTIN, ");
		sql.append("HDR.RETURN_PERIOD,RETURN_TYPE, TABLE_SECTION, ");
		sql.append("HDR.TAX_DOC_TYPE,DERIVED_RET_PERIOD,DOC_TYPE ");
		return sql.toString();

	}

	private void setParamtersForQuery(Query q,
			DataStatusFilesummaryReqDto filesummaryReqDto) {
		List<Long> fileIds = filesummaryReqDto.getFileId();
		if (fileIds != null && !fileIds.isEmpty()) {
			q.setParameter("fileIdsList", fileIds);
		}
	}

	private String createInwardRawQueryString() {
		return "SELECT HDR.DOC_DATE,FILE_NAME,CUST_GSTIN,HDR.RETURN_PERIOD,"
				+ "CASE WHEN DERIVED_RET_PERIOD  >= " + answer
				+ " THEN AN_RETURN_TYPE ELSE RETURN_TYPE END AS RET_TYPE,"
				+ "AN_TAX_DOC_TYPE ||'-'||  AN_TABLE_SECTION AS"
				+ " RETURN_SECTION,"
				+ "COUNT(HDR.ID) AS RECORD_COUNT, SUM(TAXABLE_VALUE)  AS"
				+ " TAXABLE_VALUE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
				+ "+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL"
				+ "(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX,SUM(IGST_AMT) AS IGST_AMT,"
				+ "SUM(CGST_AMT) AS CGST_AMT,  SUM(SGST_AMT) AS SGST_AMT,"
				+ "SUM(CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS CESS,DOC_TYPE FROM "
				+ "ANX_INWARD_DOC_HEADER HDR INNER JOIN FILE_STATUS FIL "
				+ "ON FIL.ID = HDR.FILE_ID   where HDR.FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ " AND IS_PROCESSED = TRUE"
				+ " GROUP BY HDR.DOC_DATE,FILE_NAME,CUST_GSTIN,HDR.RETURN_PERIOD,RETURN_TYPE,DERIVED_RET_PERIOD,"
				+ "AN_RETURN_TYPE,AN_TABLE_SECTION,DOC_TYPE,AN_TAX_DOC_TYPE";
	}

	@Override
	public List<Object[]> fecthOutwardRaw109FileData(
			DataStatusFilesummaryReqDto summaryRequest) {
		answer = summaryRequest.getAnswer();
		String queryStr = createOutwardRaw109QueryString(answer);
		Query q = entityManager.createNativeQuery(queryStr);
		setParamtersForQuery(q, summaryRequest);
		@SuppressWarnings("unchecked")
		List<Object[]> outwardList = q.getResultList();
		return outwardList;
	}

	private String createOutwardRaw109QueryString(Integer answer) {
		return "SELECT HDR.DOC_DATE,FILE_NAME,SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "CASE WHEN DERIVED_RET_PERIOD  >= " + answer
				+ " THEN AN_RETURN_TYPE "
				+ "ELSE RETURN_TYPE END AS RET_TYPE,AN_TAX_DOC_TYPE"
				+ " ||' -'||  AN_TABLE_SECTION RETURN_SECTION,"
				+ "COUNT(HDR.ID) AS RECORD_COUNT,SUM(TAXABLE_VALUE)  AS "
				+ "TAXABLE_VALUE,  SUM(IFNULL(IGST_AMT,0)"
				+ "+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0))"
				+ " AS TOTAL_TAX,SUM(IGST_AMT) AS IGST_AMT,"
				+ "SUM(CGST_AMT) AS CGST_AMT, SUM(SGST_AMT) AS SGST_AMT,"
				+ "SUM(CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS CESS,DOC_TYPE"
				+ " FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "INNER JOIN FILE_STATUS FIL ON FIL.ID = HDR.FILE_ID "
				+ "  where HDR.FILE_ID IN (:fileIdsList) AND IS_DELETE = FALSE "
				+ "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE "
				+ " GROUP BY HDR.DOC_DATE,FILE_NAME,SUPPLIER_GSTIN,HDR.RETURN_PERIOD,AN_TAX_DOC_TYPE,"
				+ "AN_RETURN_TYPE,AN_TABLE_SECTION,RETURN_TYPE,DERIVED_RET_PERIOD,DOC_TYPE";
	}

}
