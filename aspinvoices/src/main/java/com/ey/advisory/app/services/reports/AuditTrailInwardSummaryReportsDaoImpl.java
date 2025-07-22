package com.ey.advisory.app.services.reports;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.data.views.client.AuditTrailOutwardSummaryDto;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Component("AuditTrailInwardSummaryReportsDaoImpl")
@Slf4j
public class AuditTrailInwardSummaryReportsDaoImpl
		implements AuditTrailReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;
	
	private static final List<String> DATA_ORIGION_TYPE_API = ImmutableList
			.of("B", "A", "S");


	@Override
	public List<Object> getAuditTrailReports(AuditTrailReportsReqDto request) {

		LocalDate docDate = request.getDocDate();
		String docNum = request.getDocNum();
		String docType = request.getDocType();
		String gstin = request.getGstin();
		//String returnPeriod = request.getReturnPeriod();
		String docKey = request.getDocKey();
		String supplierGstin = request.getSgstin();
		String supplierLegalName = request.getLegalName();

		StringBuilder buildQuery = new StringBuilder();

		if (docKey != null && !docKey.isEmpty()) {
			buildQuery.append(" AND DOC_KEY = :docKey ");
		}

		if (docNum != null && !docNum.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_NUM = :docNum ");
		}
		if (docDate != null) {
			buildQuery.append(" AND HDR.DOC_DATE = :docDate ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_TYPE = :docType ");
		}

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND HDR.CUST_GSTIN = :gstin ");
		}
		if(supplierGstin != null && !supplierGstin.isEmpty()){
			buildQuery.append(" AND (CASE WHEN HDR.SUPPLIER_GSTIN IS NULL THEN  HDR.CUST_SUPP_NAME ELSE HDR.SUPPLIER_GSTIN END)= :supplierGstin ");
		} else if(supplierLegalName != null && !supplierLegalName.isEmpty()){
			buildQuery.append(" AND (CASE WHEN HDR.SUPPLIER_GSTIN IS NULL THEN  HDR.CUST_SUPP_NAME ELSE HDR.SUPPLIER_GSTIN END)= :supplierLegalName ");
		}
		

		String queryStr = createVerticalErrorQueryString(
				buildQuery.toString().substring(4));
		Query q = entityManager.createNativeQuery(queryStr);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading query " + queryStr);
		}

		if (docKey != null && !docKey.isEmpty()) {
			q.setParameter("docKey", docKey);
		}

		if (docDate != null) {
			q.setParameter("docDate", docDate);
		}
		if (docNum != null && !docNum.isEmpty()) {
			q.setParameter("docNum", docNum);
		}

		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}

		if (gstin != null && !gstin.isEmpty()) {
			q.setParameter("gstin", gstin);
		}
		if (supplierGstin != null && !supplierGstin.isEmpty()) {
			q.setParameter("supplierGstin", supplierGstin);
		} else if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			q.setParameter("supplierLegalName", supplierLegalName);
		}

		/*if (returnPeriod != null && !returnPeriod.isEmpty()) {
			q.setParameter("returnPeriod", returnPeriod);
		}
*/
		List<Object[]> list = q.getResultList();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading Resultset" + list);
		}
		List<Object> retList = convertVerticalError(list);
		return retList;
	}

	private List<Object> convertVerticalError(List<Object[]> arrList) {
		List<Object> obj = new ArrayList<>();
		Object objObject = null;

		int frequency = 0;
		if (arrList != null && !arrList.isEmpty()) {
			frequency = arrList.size();
		}

		List<Long> ids = new ArrayList<>();
		for (Object[] arr : arrList) {
			Long id = Long.valueOf(arr[3].toString());
			ids.add(id);
		}
		Map<Long, String> map = new HashMap<>();
		List<Object[]> findDocsByIds = inwardTransDocRepository
				.findDocsByIds(ids);
		findDocsByIds.forEach(object -> {
			Long id = Long.valueOf(object[0].toString());
			String itemCount = String.valueOf(object[1]);
			map.put(id, itemCount);
		});

		for (Object[] arr : arrList) {
			AuditTrailOutwardSummaryDto auditTrailInwwSum = new AuditTrailOutwardSummaryDto();
			auditTrailInwwSum
					.setSupGSTIN(arr[0] != null ? arr[0].toString() : null);
			auditTrailInwwSum
					.setDocNumber(arr[1] != null ? arr[1].toString() : null);
			auditTrailInwwSum.setDocDate(
					arr[2] != null ? new SimpleDateFormat("dd-MM-yyyy")
							.format(arr[2]).toString() : null);
			auditTrailInwwSum.setProFrqcy(String.valueOf(frequency));
			frequency--;
			Timestamp status = arr[4] != null && !arr[4].toString().isEmpty()
					? (java.sql.Timestamp) arr[4] : null;
			if (status != null && !status.toString().isEmpty()) {
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy HH:mm:ss");
				auditTrailInwwSum.setProDateTime(FOMATTER.format(EYDateUtil
						.toISTDateTimeFromUTC(status.toLocalDateTime())));
			}

			String createdBy = arr[5] != null ? arr[5].toString() : null;
			String dataOriginTypeCode = arr[15] != null ? arr[15].toString()
					: null;
			String userId = arr[16] != null ? arr[16].toString() : null;
			List<String> entity1 = repo.findEmailByUser(createdBy);
			if (DATA_ORIGION_TYPE_API
					.contains(trimAndConvToUpperCase(dataOriginTypeCode))) {
				auditTrailInwwSum.setUserID(userId);
			} else {
			if (entity1 != null && !entity1.isEmpty()) {
				String user = entity1.get(0);
				String usernameEmail = "";
				usernameEmail = createdBy + "  (" + user + ")";
				auditTrailInwwSum.setUserID(usernameEmail);
			} else {
				auditTrailInwwSum.setUserID(createdBy);
			}
			}

			auditTrailInwwSum
					.setProSource(arr[6] != null ? arr[6].toString() : null);
			auditTrailInwwSum
					.setProStatus(arr[7] != null ? arr[7].toString() : null);
			auditTrailInwwSum
					.setWhetherCan(arr[8] != null ? arr[8].toString() : null);
			auditTrailInwwSum
					.setCustGSTIN(arr[9] != null ? arr[9].toString() : null);
			String string = map.get(Long.valueOf(arr[3].toString()));
			auditTrailInwwSum.setNoOflineitems(string);
			auditTrailInwwSum.setTotalTaxVal(
					arr[11] != null ? arr[11].toString() : null);
			auditTrailInwwSum
					.setTotalTax(arr[12] != null ? arr[12].toString() : null);
			auditTrailInwwSum
					.setInvValue(arr[13] != null ? arr[13].toString() : null);
			auditTrailInwwSum
					.setDocType(arr[14] != null ? arr[14].toString() : null);
			objObject = auditTrailInwwSum;
			obj.add(objObject);
		}
		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT HDR.SUPPLIER_GSTIN,HDR.DOC_NUM,HDR.DOC_DATE,");
		builder.append("HDR.ID,HDR.CREATED_ON,HDR.CREATED_BY ");
		builder.append(",CASE WHEN  ");
		builder.append("DATAORIGINTYPECODE IN('E') THEN 'Web Upload' ");
		builder.append("WHEN DATAORIGINTYPECODE IN('A') THEN 'API Push' ");
		builder.append("WHEN DATAORIGINTYPECODE IN('S') THEN 'SFTP' ");
		builder.append("WHEN DATAORIGINTYPECODE IN('C') THEN 'CSV'  ");
		builder.append(
				"WHEN DATAORIGINTYPECODE IN('EI','AI','CI','SI') THEN 'Invoice Management'  ");
		builder.append(" END PROCESSING_SOURCE ");
		builder.append(
				" ,CASE WHEN IS_PROCESSED = FALSE THEN 'Error at DigiGST' ");
		builder.append(
				"  WHEN IS_PROCESSED = TRUE THEN 'Processed at DigiGST' END PROCESSING_STATUS ");
		builder.append(
				" ,CASE WHEN HDR.SUPPLY_TYPE = 'CAN' THEN 'Yes' ELSE 'No' END WHETHER_CANCEL ");
		builder.append(" ,HDR.CUST_GSTIN ");
		builder.append(" ,ITM_ROW_COUNT CNT ");
		builder.append(",SUM(TAXABLE_VALUE) TAXABLE_VALUE ");
		builder.append(
				",SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+ ");
		builder.append(
				" IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX ");
		builder.append(",HDR.DOC_AMT INV_VALUE , DOC_TYPE,DATAORIGINTYPECODE,HDR.USER_ID   ");
		builder.append("FROM ANX_INWARD_DOC_HEADER HDR WHERE ");
		builder.append(buildQuery);
		builder.append(
				" GROUP BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.DOC_NUM,HDR.DOC_DATE,HDR.CREATED_BY  ");
		builder.append(
				" ,DOC_NUM,IS_PROCESSED,DATAORIGINTYPECODE,HDR.USER_ID,HDR.CREATED_ON,HDR.SUPPLY_TYPE, ");
		builder.append(
				" HDR.CUST_GSTIN,HDR.DOC_TYPE,ITM_ROW_COUNT,HDR.DOC_AMT ");
		builder.append(" ORDER BY HDR.CREATED_ON DESC ");

		return builder.toString();
	}

}
