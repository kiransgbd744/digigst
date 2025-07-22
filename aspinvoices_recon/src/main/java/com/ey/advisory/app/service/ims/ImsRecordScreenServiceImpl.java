package com.ey.advisory.app.service.ims;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("ImsRecordScreenServiceImpl")
@Slf4j
public class ImsRecordScreenServiceImpl implements ImsRecordScreenService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;
	
	private final List<String> GET_TYPES = Arrays.asList(APIConstants.IMS_TYPE_B2BA, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DNA, APIConstants.IMS_TYPE_ECOMA, APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_CN,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_ECOM);

	@Override
	public List<ImsDigiGstTrailPopUpResponseDto> getImsDigiGstTrailPopupData(ImsEntitySummaryReqDto criteria) {
		LOGGER.debug("Processing IMS Trail DigiGst PopUp Data for gstin: {}", criteria);

		Optional.ofNullable(criteria.getDocKey()).filter(s -> !s.isEmpty())
				.orElseThrow(() -> new AppException("DocKey is null or empty"));
		List<ImsDigiGstTrailPopUpResponseDto> responseList = new ArrayList<>();

		try {
			List<Object[]> savedDataList = getImsDigiGstTrailStatusDetails(criteria.getDocKey());

			if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
				for (Object[] data : savedDataList) {
					ImsDigiGstTrailPopUpResponseDto responseDto = new ImsDigiGstTrailPopUpResponseDto();
					responseDto.setActionDigiGST(data[0]!=null ? data[0].toString():null);
					responseDto.setActionDigiGSTTimeStamp(formatDateTime((Timestamp) data[1]));
					responseDto.setActionDigiGSTActionTakenBy((String) data[2]);
					responseDto.setIsSavedToGstn((Boolean.TRUE.equals(data[3])) ? "Yes" : "No");
					responseList.add(responseDto);
				}
			} else {
				LOGGER.info("No data found for Trail Query with docKey: {} ", criteria.getDocKey());
				return responseList;
			}
		} catch (Exception e) {
			throw new AppException("Error processing IMS Trail DigiGst PopUp : {}", e);
		}
		return responseList;
	}

	private List<Object[]> getImsDigiGstTrailStatusDetails(String docKey) {

		/*String queryStr = "SELECT ACTION_RESPONSE , CREATED_ON , CREATED_BY FROM TBL_GETIMS_PROCESSED "
				+ "WHERE DOC_KEY = :docKey ORDER BY CREATED_ON DESC";*/
		
		String queryStr = "SELECT TO_VARCHAR(IFNULL(SUBSTR(ACTION_RESP_UPD_REMARKS, 14, 1), "
				+ "ACTION_RESPONSE)) AS ACTION_RESPONSE, " 
                + "CREATED_ON, CREATED_BY, IS_SAVED_TO_GSTIN " 
                + "FROM TBL_GETIMS_PROCESSED " 
                + "WHERE DOC_KEY = :docKey " 
                + "ORDER BY CREATED_ON DESC";

		Query query = entityManager.createNativeQuery(queryStr)
				.setParameter("docKey", docKey);

		@SuppressWarnings("unchecked")
		List<Object[]> itemsList = query.getResultList();

		return itemsList;
	}

	/**
	 * IMS Record Screen GSTN Trail
	 */
	
	@Override
	public List<ImsGstnTrailPopUpResponseDto> getImsGstTrailPopupData(ImsEntitySummaryReqDto criteria) {
		LOGGER.debug("Processing IMS Trail GSTN PopUp Data for gstin: {}", criteria);

		Optional.ofNullable(criteria.getDocKey()).filter(s -> !s.isEmpty())
				.orElseThrow(() -> new AppException("DocKey is null or empty"));
		List<ImsGstnTrailPopUpResponseDto> responseList = new ArrayList<>();

		try {
			List<Object[]> savedDataList = getImsGstnTrailStatusDetails(criteria.getDocKey());

			if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
				for (Object[] data : savedDataList) {
					ImsGstnTrailPopUpResponseDto responseDto = new ImsGstnTrailPopUpResponseDto();
					responseDto.setActionGST(data[0]!=null ? data[0].toString():null);
					responseDto.setActionGSTTimeStamp(formatDateTime((Timestamp) data[1]));
					responseList.add(responseDto);
				}
			} else {
				LOGGER.info("No data found for Trail Query with docKey: {} ", criteria.getDocKey());
				return responseList;
			}
		} catch (Exception e) {
			throw new AppException("Error processing IMS Trail GSTN PopUp : {}", e);
		}
		return responseList;
	}

	private List<Object[]> getImsGstnTrailStatusDetails(String docKey) {

		StringBuilder queryBuilder = new StringBuilder();

		for (String type : GET_TYPES) {
			if (docKey.contains(type)) {
				// Get the corresponding table name
				String currentTable = "TBL_GETIMS_INVOICE_HEADER_" + type.split("-")[0];
				String archivalTable = currentTable + "_ARCHIVAL";

				queryBuilder.append("SELECT ACTION, CREATED_ON FROM ").append(currentTable)
						.append(" WHERE DOC_KEY = :docKey").append(" UNION ")
						.append("SELECT ACTION, CREATED_ON FROM ").append(archivalTable)
						.append(" WHERE DOC_KEY = :docKey ORDER BY CREATED_ON DESC");

				break;
			}
		}

		if (queryBuilder.length() == 0) {
			return Collections.emptyList();
		}
		String queryStr = queryBuilder.toString();

		Query query = entityManager.createNativeQuery(queryStr).setParameter("docKey", docKey);

		@SuppressWarnings("unchecked")
		List<Object[]> itemsList = query.getResultList();

		return itemsList;
	}
	
	private String formatDateTime(Timestamp timestamp) {
		if (timestamp == null) return null;
		LocalDateTime dateTimeInIst = EYDateUtil.toISTDateTimeFromUTC(timestamp);
		return dateTimeInIst.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
	}
	
}
