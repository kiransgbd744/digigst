package com.ey.advisory.app.processors.handler;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GstnReturnStatusUtil")
public class GstnReturnStatusUtil {

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public Pair<String, String> getLastTransactionReturnStatus(String gstin,
			String retPeriod, String returnType) {

		String overallSaveStatus = "Not Initiated";
		String formattedDate = null;
		LocalDateTime lastSaveInitiatedTime = null;
		LocalDate filingDate = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"getLastTransactionReturnStatus method has called with args {}, {}, {} ",
					gstin, retPeriod, returnType);
		}
		try {
			if (gstin != null && retPeriod != null && returnType != null) {
				GstrReturnStatusEntity signedStatusP = returnstatusRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
								gstin, retPeriod, returnType.toUpperCase());
				if (signedStatusP != null && "FILED"
						.equalsIgnoreCase(signedStatusP.getStatus())) {
					overallSaveStatus = "Filed".toUpperCase();
					filingDate = signedStatusP.getFilingDate();
				} else {
					Optional<GstnSubmitEntity> submittedRecords = gstnSubmitRepository
							.findTop1ByGstinAndRetPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
									gstin, retPeriod, returnType.toUpperCase());
					if (submittedRecords.isPresent() && "P".equalsIgnoreCase(
							submittedRecords.get().getGstnStatus())) {
						overallSaveStatus = "Submitted";
						lastSaveInitiatedTime = submittedRecords.get()
								.getCreatedOn();
					} else {
						Object[] resultObject = findLastSaveTransactionStatusby(
								gstin, retPeriod, returnType);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"findLastSaveTransactionStatusby query has returned as {} ",
									resultObject);
						}

						if (resultObject != null) {

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("resultObject[] length is {} ",
										resultObject.length);
							}

							Integer pResponseCount = resultObject[0] != null
									? Integer.parseInt(
											String.valueOf(resultObject[0]))
									: 0;
							Integer peResponseCount = resultObject[1] != null
									? Integer.parseInt(
											String.valueOf(resultObject[1]))
									: 0;
							Integer erResponseCount = resultObject[2] != null
									? Integer.parseInt(
											String.valueOf(resultObject[2]))
									: 0;
							Integer noResponseCount = resultObject[3] != null
									? Integer.parseInt(
											String.valueOf(resultObject[3]))
									: 0;

							if (resultObject[4] != null) {
								Timestamp date = (Timestamp) resultObject[4];
								lastSaveInitiatedTime = date.toLocalDateTime();
							}

							if (noResponseCount > 0) {
								overallSaveStatus = "Save InProgress";

							} else if (peResponseCount > 0) {
								overallSaveStatus = "Partially Saved";

							} else if (pResponseCount > 0
									&& erResponseCount > 0) {
								overallSaveStatus = "Partially Saved";

							} else if (pResponseCount > 0
									&& erResponseCount == 0) {
								overallSaveStatus = "Saved";

							} else if (pResponseCount == 0
									&& erResponseCount > 0) {
								overallSaveStatus = "Saved Failed";

							} else {
								// If Gstn_User_Request is created and about to
								// create Ref_Id
								overallSaveStatus = "Not Initiated";
							}

						} else {
							overallSaveStatus = "Not Initiated";

						}
					}
				}
			} else {
				LOGGER.error(
						"gstin {} retPeriod {} && returnType {} are mandatory",
						gstin, retPeriod, returnType);
			}
			if (overallSaveStatus != null
					&& "FILED".equalsIgnoreCase(overallSaveStatus)) {

				formattedDate = EYDateUtil.fmtLocalDate(filingDate);
			}
			if (lastSaveInitiatedTime != null) {
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(lastSaveInitiatedTime);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				formattedDate = FOMATTER.format(dateTimeFormatter);
			}

		} catch (Exception e) {
			LOGGER.error("Exception occured in ReturnStatus service {}", e);
			throw new AppException("Exception occured in ReturnStatus service",
					e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"getLastTransactionReturnStatus method has returned with Return Status and time stamp as {}, {} ",
					overallSaveStatus, formattedDate);
		}
		return new Pair<>(overallSaveStatus, formattedDate);
	}

	private Object[] findLastSaveTransactionStatusby(String gstin,
			String retPeriod, String returnType) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Return status Query filters gstin, retPeriod, returnType, requestTypes are {}, {}, {} ",
					gstin, retPeriod, returnType);
		}

		String queryStr = "SELECT SUM(CASE WHEN GSTN_SAVE_STATUS ='P' THEN 1 ELSE 0 END) AS PCount,"
				+ "SUM(CASE WHEN GSTN_SAVE_STATUS ='PE' THEN 1 ELSE 0 END) AS PECount,"
				+ "SUM(CASE WHEN GSTN_SAVE_STATUS ='ER' THEN 1 ELSE 0 END) AS ERCount,"
				+ "SUM(CASE WHEN GSTN_SAVE_STATUS ='' OR GSTN_SAVE_STATUS IS NULL THEN 1 ELSE 0 END) "
				+ "AS NostatusCount,MAX(CREATED_ON) AS DateAndTime FROM GSTR1_GSTN_SAVE_BATCH "
				+ "WHERE USER_REQUEST_ID =(SELECT MAX(USER_REQUEST_ID) FROM GSTR1_GSTN_SAVE_BATCH WHERE "
				+ "SUPPLIER_GSTIN =:gstin AND RETURN_PERIOD =:retPeriod AND RETURN_TYPE =:returnType "
				+ "AND IS_DELETE = FALSE) AND IS_DELETE = FALSE";

		Query nativeQuery = entityManager
				.createNativeQuery(queryStr.toString());

		nativeQuery.setParameter("gstin", gstin);
		nativeQuery.setParameter("retPeriod", retPeriod);
		nativeQuery.setParameter("returnType", returnType);

		@SuppressWarnings("unchecked")
		List<Object[]> resultSet = nativeQuery.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Return status Query resultSet is {} ", resultSet);
		}
		if (resultSet != null)
			return resultSet.get(0);

		return null;
	}

}
