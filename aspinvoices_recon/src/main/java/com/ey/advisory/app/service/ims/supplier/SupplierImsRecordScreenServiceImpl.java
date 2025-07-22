package com.ey.advisory.app.service.ims.supplier;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.gstr2.userdetails.EntityService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("SupplierImsRecordScreenServiceImpl")
@Slf4j
public class SupplierImsRecordScreenServiceImpl implements SupplierImsRecordScreenService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Override
	public List<SupplierImsGstnTrailPopUpResponseDto> getSupplierImsGstnTrailPopupData(
			SupplierImsEntityReqDto criteria) {

		LOGGER.debug("Processing SUPPLIER_IMS Trail GSTN PopUp Data for gstin: {}", criteria);

		Optional.ofNullable(criteria.getDocKey()).filter(s -> !s.isEmpty())
				.orElseThrow(() -> new AppException("DocKey is null or empty"));
		List<SupplierImsGstnTrailPopUpResponseDto> responseList = new ArrayList<>();

		try {
			List<Object[]> savedDataList = getSupplierImsGstnTrailStatusDetails(criteria);

			if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
				for (Object[] data : savedDataList) {
					SupplierImsGstnTrailPopUpResponseDto responseDto = new SupplierImsGstnTrailPopUpResponseDto();
					responseDto.setActionGST(data[0] != null ? data[0].toString() : null);
					responseDto.setActionGSTTimeStamp(formatDateTime((Timestamp) data[1]));
					responseList.add(responseDto);
				}
			} else {
				LOGGER.info("No data found for Trail Query with docKey: {} ", criteria.getDocKey());
				return responseList;
			}
		} catch (Exception e) {
			throw new AppException("Error processing SUPPLIER_IMS Trail GSTN PopUp : {}", e);
		}
		return responseList;

	}

	private List<Object[]> getSupplierImsGstnTrailStatusDetails(SupplierImsEntityReqDto criteria) {
		String returnType = criteria.getReturnType(); // GSTR1 or GSTR1A
		String tableType = criteria.getTableType(); // e.g., CDNRA, B2B
		String docKey = criteria.getDocKey();

		if (returnType == null || tableType == null || docKey == null) {
			LOGGER.error("Invalid input: returnType, tableType, or docKey is null.");
			return Collections.emptyList();
		}

		String type = tableType.toUpperCase();
		String rt = returnType.toUpperCase();

		// Construct 2 table names: base + staging
		String baseTable = "TBL_GETIMS_" + rt + "_" + type + "_HEADER";
		String archivalTable = "TBL_GETIMS_STAGING_" + rt + "_" + type + "_HEADER_ARCHIVAL";

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT IMS_ACTION, CREATED_ON FROM ")
					.append(baseTable).append(" WHERE DOC_KEY = :docKey ")
				    .append(" UNION ")
				    .append("SELECT IMS_ACTION, CREATED_ON FROM ")
				    .append(archivalTable)
				    .append(" WHERE DOC_KEY = :docKey ")
				    .append(" ORDER BY CREATED_ON DESC");

		String queryStr = queryBuilder.toString();
		LOGGER.info("Executing GET trail query: {}", queryStr);

		try {
			Query query = entityManager.createNativeQuery(queryStr).setParameter("docKey", docKey);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();
			return resultList;
		} catch (Exception e) {
			LOGGER.error("Exception while fetching GET trail status for docKey {}: {}", docKey, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	private String formatDateTime(Timestamp timestamp) {
		if (timestamp == null)
			return null;
		LocalDateTime dateTimeInIst = EYDateUtil.toISTDateTimeFromUTC(timestamp);
		return dateTimeInIst.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
	}

}
