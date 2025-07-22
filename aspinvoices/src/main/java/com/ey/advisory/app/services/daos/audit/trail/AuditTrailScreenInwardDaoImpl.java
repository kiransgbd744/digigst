/**
 * 
 */
package com.ey.advisory.app.services.daos.audit.trail;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.docs.dto.AuditTrailScreenReqDto;
import com.ey.advisory.app.docs.dto.AuditTrailScreenSummaryItemRespDto;
import com.ey.advisory.app.docs.dto.AuditTrailScreenSummaryRespDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("AuditTrailScreenInwardDaoImpl")
@Slf4j
public class AuditTrailScreenInwardDaoImpl
		implements AuditTrailScreenInwardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final List<String> DATA_ORIGION_TYPE_API = ImmutableList
			.of("A");

	@Override
	public AuditTrailScreenSummaryRespDto getAuditTrailInwardData(
			AuditTrailScreenReqDto request) {

		LocalDate docDate = request.getDocDate();
		String docNum = request.getDocNum();
		String docType = request.getDocType();
		String gstin = request.getGstin();
		// String returnPeriod = request.getReturnPeriod();
		String docKey = request.getDocKey();
		String supplierGstin = request.getSgstin();
		String supplierLegalName = request.getLegalName();

		StringBuilder buildQuery = new StringBuilder();

		if (docDate != null) {
			buildQuery.append(" AND HDR.DOC_DATE = :docDate ");
		}
		if (docNum != null && !docNum.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_NUM = :docNum ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_TYPE = :docType ");
		}

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND HDR.CUST_GSTIN = :gstin ");
		}

		if (supplierGstin != null && !supplierGstin.isEmpty()) {
			buildQuery.append(
					" AND (CASE WHEN HDR.SUPPLIER_GSTIN IS NULL THEN  HDR.CUST_SUPP_NAME ELSE HDR.SUPPLIER_GSTIN END)= :supplierGstin ");
		} else if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			buildQuery.append(
					" AND (CASE WHEN HDR.SUPPLIER_GSTIN IS NULL THEN  HDR.CUST_SUPP_NAME ELSE HDR.SUPPLIER_GSTIN END)= :supplierLegalName ");
		}

		/*
		 * if (returnPeriod != null && !returnPeriod.isEmpty()) {
		 * buildQuery.append(" AND HDR.RETURN_PERIOD = :returnPeriod "); }
		 */

		if (docKey != null && !docKey.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_KEY = :docKey ");
		}

		String queryStr = createAuditTrailOutwardQuery(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading query auditoutwardscreen" + queryStr);
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

		/*
		 * if (returnPeriod != null && !returnPeriod.isEmpty()) {
		 * q.setParameter("returnPeriod", returnPeriod); }
		 */

		if (docKey != null && !docKey.isEmpty()) {
			q.setParameter("docKey", docKey);
		}
		if (supplierGstin != null && !supplierGstin.isEmpty()) {
			q.setParameter("supplierGstin", supplierGstin);
		} else if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			q.setParameter("supplierLegalName", supplierLegalName);
		}

		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading Resultset for auditinwardscreen" + list);
		}
		AuditTrailScreenSummaryRespDto retResponse = convertAuditTrailOutwardQuery(
				list, docType);
		return retResponse;
	}

	private AuditTrailScreenSummaryRespDto convertAuditTrailOutwardQuery(
			List<Object[]> arrList, String docType) {

		List<AuditTrailScreenSummaryItemRespDto> itemList = new ArrayList<>();
		Object[] latestRecord = null;

		int frequency = 0;
		if (arrList != null && !arrList.isEmpty()) {
			frequency = arrList.size();
			latestRecord = arrList.get(0);
		}
		AuditTrailScreenSummaryRespDto auditTrailOutwScreen = new AuditTrailScreenSummaryRespDto();
		auditTrailOutwScreen.setProcessingFrequency(frequency);
		if (latestRecord != null) {
			auditTrailOutwScreen.setLatestStatus(latestRecord[3] != null
					? latestRecord[3].toString() : null);
			auditTrailOutwScreen.setTableType(latestRecord[6] != null
					? latestRecord[6].toString() : null);

			String tableNo = (String) latestRecord[6];
			String docCategory = (String) latestRecord[12];
			String tableNumber = null;
			if ((tableNo != null && !tableNo.isEmpty())
					&& (docCategory != null && !docCategory.isEmpty())) {
				tableNumber = docCategory.concat("-").concat(tableNo);
			}
			auditTrailOutwScreen.setTableType(
					tableNumber != null ? String.valueOf(tableNumber) : null);
			if (latestRecord[1] != null) {

				LOGGER.debug("processing Date and Time :"
						+ latestRecord[1].toString());

				Timestamp timeStamp = (Timestamp) latestRecord[1];
				LocalDateTime dt = timeStamp.toLocalDateTime();
				LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dt);
				String formattedDate = formatter1.format(ist).toString();

				String[] dateTime = formattedDate.split(" ");

				String date = dateTime[0];
				String time = dateTime[1];

				auditTrailOutwScreen.setLatestStatusDate(date);
				auditTrailOutwScreen.setLatestStatusTime(time);
			}
		}

		for (Object[] arr : arrList) {
			AuditTrailScreenSummaryItemRespDto auditTrailItemData = new AuditTrailScreenSummaryItemRespDto();

			auditTrailItemData
					.setProcessingFrequency(String.valueOf(frequency));
			frequency--;

			if (arr[1] != null) {
				Timestamp timeStamp = (Timestamp) arr[1];
				LocalDateTime ist = EYDateUtil
						.toISTDateTimeFromUTC(timeStamp.toLocalDateTime());
				auditTrailItemData.setProcessingDateTime(
						formatter1.format(ist).toString());
			}

			String createdBy = arr[0] != null ? arr[0].toString() : null;
			String dataOriginTypeCode = arr[13] != null ? arr[13].toString()
					: null;
			String userId = arr[14] != null ? arr[14].toString() : null;

			List<String> entity1 = repo.findEmailByUser(createdBy);

			if (DATA_ORIGION_TYPE_API
					.contains(trimAndConvToUpperCase(dataOriginTypeCode))) {
				auditTrailItemData.setUserID(userId);
			} else {
				if (entity1 != null && !entity1.isEmpty()) {
					String user = entity1.get(0);
					String usernameEmail = "";
					usernameEmail = createdBy + "  (" + user + ")";
					auditTrailItemData.setUserID(usernameEmail);
				} else {
					auditTrailItemData.setUserID(createdBy);
				}
			}

			auditTrailItemData.setProcessingSource(
					arr[2] != null ? arr[2].toString() : null);
			auditTrailItemData.setProcessingStatus(
					arr[3] != null ? arr[3].toString() : null);
			auditTrailItemData.setWhetherCancel(
					arr[5] != null ? arr[5].toString() : null);
			auditTrailItemData.setCustomerGSTIN(
					arr[7] != null ? arr[7].toString() : null);

			BigInteger lineItems = GenUtil.getBigInteger(arr[8]);
			Long noLineItems = lineItems.longValue();

			auditTrailItemData
					.setNoLineItems(noLineItems != null ? noLineItems : null);
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR")
					|| docType.equalsIgnoreCase("ADJ"))) {
				auditTrailItemData
						.setTotalTaxableValue(CheckForNegativeValue(arr[9]));
				auditTrailItemData.setTotalTax(CheckForNegativeValue(arr[10]));
				auditTrailItemData
						.setInvoiceValue(CheckForNegativeValue(arr[11]));
			} else {
				auditTrailItemData.setTotalTaxableValue(
						arr[9] != null ? (BigDecimal) arr[9] : null);
				auditTrailItemData.setTotalTax(
						arr[10] != null ? (BigDecimal) arr[10] : null);
				auditTrailItemData.setInvoiceValue(
						arr[11] != null ? (BigDecimal) arr[11] : null);
			}

			itemList.add(auditTrailItemData);
		}

		auditTrailOutwScreen.setItems(itemList);
		return auditTrailOutwScreen;
	}

	private String createAuditTrailOutwardQuery(String buildQuery) {

		return "select HDR.CREATED_BY,	HDR.CREATED_ON,CASE WHEN "
				+ "DATAORIGINTYPECODE IN('E') THEN 'Web Upload' "
				+ "WHEN DATAORIGINTYPECODE IN('A') THEN 'API Push' "
				+ "WHEN DATAORIGINTYPECODE IN('S') THEN 'SFTP' "
				+ "WHEN DATAORIGINTYPECODE IN('C') THEN 'CSV'  "
				+ "WHEN DATAORIGINTYPECODE IN('EI','AI','CI','SI') THEN 'Invoice Management'  "
				+ "END PROCESSING_SOURCE, CASE WHEN IS_PROCESSED = FALSE "
				+ "THEN 'Error at DigiGST' WHEN IS_PROCESSED = TRUE "
				+ "THEN 'Processed at DigiGST' END PROCESSING_STATUS,IS_PROCESSED,"
				+ "CASE WHEN HDR.SUPPLY_TYPE = 'CAN' AND IS_PROCESSED = TRUE THEN 'Yes' ELSE 'No' END "
				+ "WHETHER_CANCEL,HDR.AN_TABLE_SECTION,HDR.SUPPLIER_GSTIN,CASE "
				+ "WHEN ITM_ROW_COUNT > 0 THEN ITM_ROW_COUNT ELSE "
				+ "COUNT(ITM.DOC_HEADER_ID) END ITEM_CNT,HDR.TAXABLE_VALUE,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+ "
				+ "IFNULL(ITM.SGST_AMT,0)+ IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ "
				+ "IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX,"
				+ "HDR.DOC_AMT INV_VALUE,AN_TAX_DOC_TYPE,DATAORIGINTYPECODE,HDR.USER_ID"
				+ " FROM ANX_INWARD_DOC_HEADER HDR, "
				+ "ANX_INWARD_DOC_ITEM ITM WHERE HDR.ID = ITM.doc_header_id "
				+ buildQuery
				+ " GROUP BY HDR.CREATED_BY,HDR.CREATED_ON,DATAORIGINTYPECODE,"
				+ "HDR.USER_ID,IS_PROCESSED,HDR.AN_TABLE_SECTION,HDR.SUPPLY_TYPE,"
				+ "HDR.SUPPLIER_GSTIN,ITM_ROW_COUNT,HDR.TAXABLE_VALUE,"
				+ "AN_TAX_DOC_TYPE,HDR.ID,HDR.DOC_AMT "
				+ "ORDER BY HDR.CREATED_ON DESC ";
	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}
}
