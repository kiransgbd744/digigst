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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.views.client.AuditTrailOutwardSummaryDto;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Component("AuditTrailOutwardSummaryReportsDaoImpl")
@Slf4j
public class AuditTrailOutwardSummaryReportsDaoImpl
		implements AuditTrailReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

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

		StringBuilder buildQuery = new StringBuilder();

		if (docKey != null && !docKey.isEmpty()) {
			buildQuery.append(" AND DOC_KEY = :docKey ");
		}
		if (docNum != null && !docNum.isEmpty()) {
			buildQuery.append(" AND DOC_NUM = :docNum ");
		}
		if (docDate != null) {
			buildQuery.append(" AND DOC_DATE = :docDate ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND DOC_TYPE = :docType ");
		}

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND SUPPLIER_GSTIN = :gstin ");
		}

		/*if (returnPeriod != null && !returnPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD = :returnPeriod ");
		}*/
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

		/*if (returnPeriod != null && !returnPeriod.isEmpty()) {
			q.setParameter("returnPeriod", returnPeriod);
		}*/

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
		List<Object[]> findDocsByIds = docRepository.findDocsByIds(ids);
		findDocsByIds.forEach(object -> {
			Long id = Long.valueOf(object[0].toString());
			String itemCount = String.valueOf(object[1]);
			map.put(id, itemCount);
		});

		for (Object[] arr : arrList) {
			AuditTrailOutwardSummaryDto auditTrailOutwSum = new AuditTrailOutwardSummaryDto();
			auditTrailOutwSum
					.setSupGSTIN(arr[0] != null ? arr[0].toString() : null);
			auditTrailOutwSum
					.setDocNumber(arr[1] != null ? arr[1].toString() : null);
			auditTrailOutwSum.setDocDate(
					arr[2] != null ? new SimpleDateFormat("dd-MM-yyyy")
							.format(arr[2]).toString() : null);
			auditTrailOutwSum.setProFrqcy(String.valueOf(frequency));
			frequency--;

			Timestamp status = arr[4] != null && !arr[4].toString().isEmpty()
					? (java.sql.Timestamp) arr[4] : null;
			if (status != null && !status.toString().isEmpty()) {
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy HH:mm:ss");
				auditTrailOutwSum.setProDateTime(FOMATTER.format(EYDateUtil
						.toISTDateTimeFromUTC(status.toLocalDateTime())));
			}

			String createdBy = arr[5] != null ? arr[5].toString() : null;
			String dataOriginTypeCode = arr[15] != null ? arr[15].toString()
					: null;
			String userId = arr[16] != null ? arr[16].toString() : null;
			List<String> entity1 = repo.findEmailByUser(createdBy);
			
			if (DATA_ORIGION_TYPE_API
					.contains(trimAndConvToUpperCase(dataOriginTypeCode))) {
				auditTrailOutwSum.setUserID(userId);
			} else {

			if (entity1 != null && !entity1.isEmpty()) {
				String user = entity1.get(0);
				String usernameEmail = "";
				usernameEmail = createdBy + "  (" + user + ")";
				auditTrailOutwSum.setUserID(usernameEmail);
			} else {
				auditTrailOutwSum.setUserID(createdBy);
			}
			}
			auditTrailOutwSum
					.setProSource(arr[6] != null ? arr[6].toString() : null);
			auditTrailOutwSum
					.setProStatus(arr[7] != null ? arr[7].toString() : null);
			auditTrailOutwSum
					.setWhetherCan(arr[8] != null ? arr[8].toString() : null);
			auditTrailOutwSum
					.setCustGSTIN(arr[9] != null ? arr[9].toString() : null);
			String string = map.get(Long.valueOf(arr[3].toString()));
			auditTrailOutwSum.setNoOflineitems(string);
			auditTrailOutwSum.setTotalTaxVal(
					arr[11] != null ? arr[11].toString() : null);
			auditTrailOutwSum
					.setTotalTax(arr[12] != null ? arr[12].toString() : null);
			auditTrailOutwSum
					.setInvValue(arr[13] != null ? arr[13].toString() : null);
			auditTrailOutwSum
					.setDocType(arr[14] != null ? arr[14].toString() : null);
			objObject = auditTrailOutwSum;
			obj.add(objObject);
		}
		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT SUPPLIER_GSTIN,DOC_NUM,DOC_DATE,ID,CREATED_ON,");
		builder.append("CREATED_BY ,CASE WHEN  ");
		builder.append("DATAORIGINTYPECODE IN('E') THEN 'Web Upload' ");
		builder.append("WHEN DATAORIGINTYPECODE IN('A') THEN 'API Push' ");
		builder.append("WHEN DATAORIGINTYPECODE IN('S') THEN 'SFTP' ");
		builder.append("WHEN DATAORIGINTYPECODE IN('C') THEN 'CSV'  ");
		builder.append(
				"WHEN DATAORIGINTYPECODE IN('B') THEN 'API Push (Auto)' ");
		builder.append(
				"WHEN DATAORIGINTYPECODE IN('AI','EI','BI','SI','CI')  ");
		builder.append("THEN 'Invoice Management'  ");
		builder.append("END PROCESSING_SOURCE  ");
		builder.append(
				",CASE WHEN ASP_INVOICE_STATUS = 1 THEN 'Error at DigiGST' ");
		builder.append(
				" WHEN ASP_INVOICE_STATUS = 2 THEN 'Processed at DigiGST' ");
		builder.append("END PROCESSING_STATUS ");
		builder.append(
				",CASE WHEN SUPPLY_TYPE = 'CAN' THEN 'Yes' ELSE 'No' END WHETHER_CANCEL ");
		builder.append(
				"  ,CUST_GSTIN,ITM_ROW_COUNT CNT,SUM(TAXABLE_VALUE) TAXABLE_VALUE ");
		builder.append(
				",SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+ ");
		builder.append(
				" IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX ");
		builder.append(
				",DOC_AMT INV_VALUE,DOC_TYPE,DATAORIGINTYPECODE,USER_ID FROM ANX_OUTWARD_DOC_HEADER WHERE ");
		builder.append(buildQuery);// DOC_NUM = 'KA192012900647' --'1234H'--
		builder.append(
				" GROUP BY ID,SUPPLIER_GSTIN,DOC_NUM,DOC_DATE,CREATED_BY ");
		builder.append(" ,DOC_NUM,ASP_INVOICE_STATUS,DATAORIGINTYPECODE,USER_ID, ");
		builder.append(
				" CREATED_ON,SUPPLY_TYPE,CUST_GSTIN,ITM_ROW_COUNT,DOC_TYPE,DOC_AMT ");
		builder.append(" ORDER BY CREATED_ON DESC ");
		return builder.toString();
	}

}
