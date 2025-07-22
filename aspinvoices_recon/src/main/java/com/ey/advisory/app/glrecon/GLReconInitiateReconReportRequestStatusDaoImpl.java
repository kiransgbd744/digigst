/**
 * 
 */
package com.ey.advisory.app.glrecon;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2AERPRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("GLReconInitiateReconReportRequestStatusDaoImpl")
public class GLReconInitiateReconReportRequestStatusDaoImpl
		implements GLReconInitiateReconReportRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("AutoRecon2AERPRequestRepository")
	private AutoRecon2AERPRequestRepository erpRequestRepo;

	@Autowired
	private CommonUtility commonUtility;

	private Gstr2InitiateReconReportRequestStatusDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " GLReconInitiateReconReportRequestStatusDaoImpl object";
			LOGGER.debug(str);
		}

		Gstr2InitiateReconReportRequestStatusDto convert = new Gstr2InitiateReconReportRequestStatusDto();

		convert.setRequestId(arr[0] != null ? Long.valueOf(arr[0].toString())
				: Long.valueOf("0.00"));
		Timestamp date = (Timestamp) arr[1];
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dateTime = String.format("%-20s", dateTime).replace(' ', '0');
			String Date = dateTime.substring(0, 10);
			String Time = dateTime.substring(11, 19);
			String updatedDateTime = (Date + " " + Time);
			convert.setInitiatedOn(updatedDateTime);
		}

		convert.setInitiatedBy((String) arr[2]);

		if (arr[4] != null) {
			date = (Timestamp) arr[4];
			if (date != null) {
				String cdateTime = date != null ? EYDateUtil
						.toISTDateTimeFromUTC(date.toLocalDateTime()).toString()
						: null;
				String cDate = cdateTime.substring(0, 10);
				String cTime = cdateTime.substring(11, 19);
				String cupdatedDateTime = (cDate + " " + cTime);
				convert.setCompletionOn(cupdatedDateTime);
			}
		}
		convert.setStatus((String) arr[5]);
		convert.setPath((String) arr[6]);
		BigInteger bi = (BigInteger) arr[7];
		Integer gstnCount = bi.intValue();
		convert.setGstinCount(gstnCount);

		GstinDto gstinDto = null;
		{
			gstinDto = new GstinDto((String) arr[8]);
		}

		if (arr[3] != null && arr[9] != null) {
			Integer toTaxPeriod = arr[3] != null
					? Integer.parseInt(arr[3].toString())
					: null;
			convert.setToTaxPeriod(toTaxPeriod);

			Integer fromTaxPeriod = arr[9] != null
					? Integer.parseInt(arr[9].toString())
					: null;
			convert.setFromTaxPeriod(fromTaxPeriod);
		} else {

			convert.setToTaxPeriod(null);
			convert.setFromTaxPeriod(null);

		}

		convert.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));
		convert.setReconType("GL RECON");

		return convert;
	}

	@Override
	public List<BigInteger> getRequestIds(String userName, Long entityId,
			Gstr2InitiateReconReqDto reqDto) throws AppException {
		try {
			String initiationDateFrom = reqDto.getInitiationDateFrom();
			String initiationDateTo = reqDto.getInitiationDateTo();

			String ansfromques = commonUtility.getAnsFromQue(entityId,
					"Multiple User Access to Async Reports");
			String queryString = null;
			if (ansfromques.equalsIgnoreCase("A")) {
				queryString = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID "
						+ " FROM GL_RECON_REPORT_CONFIG RRC "
						+ " WHERE RRC.ENTITY_ID =:entityId AND "
						+ " TO_VARCHAR(RRC.CREATED_DATE,'YYYY-MM-DD') "
						+ " BETWEEN :initiationDateFrom  AND :initiationDateTo "
						+ " ORDER BY 1 DESC";
			} else {
				queryString = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID "
						+ " FROM GL_RECON_REPORT_CONFIG RRC "
						+ " WHERE RRC.ENTITY_ID = :entityId "
						+ " AND RRC.CREATED_BY = :createdBy "
						+ " AND TO_VARCHAR(RRC.CREATED_DATE, 'YYYY-MM-DD') "
						+ " BETWEEN :initiationDateFrom AND :initiationDateTo "
						+ " ORDER BY 1 DESC";

			}
			Query q = entityManager.createNativeQuery(queryString);

			q.setParameter("entityId", entityId);
			q.setParameter("initiationDateFrom", initiationDateFrom);
			q.setParameter("initiationDateTo", initiationDateTo);
			if (!ansfromques.equalsIgnoreCase("A")) {
				q.setParameter("createdBy", userName);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data of RequestIds "
						+ ", entityId " + entityId + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<BigInteger> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "GLReconInitiateReconReportRequestStatusDaoImpl.getRequestIds");
		}
	}

	@Override
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		List<Gstr2InitiateReconReportRequestStatusDto> respList = null;
		Long entityId = reqDto.getEntityId();
		List<Long> requestId = reqDto.getRequestId();

		String initiationDateFrom = reqDto.getInitiationDateFrom();
		String initiationDateTo = reqDto.getInitiationDateTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();

		List<Long> entityIds = new ArrayList<Long>();
		entityIds.add(entityId);
		String ansfromques = commonUtility.getAnsFromQue(entityId,
				"Multiple User Access to Async Reports");
		boolean restrictionFlag = false;
		if (ansfromques.equalsIgnoreCase("B")) {
			restrictionFlag = true;
		}
		/*
		 * List<Long> optedEntities = entityConfigPemtRepo
		 * .getAllEntitiesOptedUserRestriction(entityIds, "I29"); boolean
		 * restrictionFlag = true; if (ansfromques == null ||
		 * ansfromques.isEmpty()) { restrictionFlag = false; }
		 */
		String reconStatus = reqDto.getReconStatus();

		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			} else if (reconStatus.equalsIgnoreCase("RECON COMPLETED")) {
				reconStatus = "RECON_COMPLETED";
			}

		}

		try {
			String condtion = queryCondition(reqDto, restrictionFlag);
			String queryString = createQuery(condtion);

			LOGGER.debug("Generated Query String: {}", queryString);

			Query q = entityManager.createNativeQuery(queryString);
			if (reqDto.getEntityId() != null) {
				q.setParameter("entityId", entityId);
			}
			if (reqDto.getInitiationDateFrom() != null
					&& !reqDto.getInitiationDateFrom().isEmpty()) {
				q.setParameter("initiationDateFrom", initiationDateFrom);
			}
			if (reqDto.getInitiationDateTo() != null
					&& !reqDto.getInitiationDateTo().isEmpty()) {
				q.setParameter("initiationDateTo", initiationDateTo);
			}

			if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
				q.setParameter("requestId", requestId);
			}
			if (restrictionFlag) {
				q.setParameter("userName", userName);
			}

			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				q.setParameter("initiationByUserId", initiationByUserId);
			}
			if (reqDto.getReconStatus() != null
					&& !reqDto.getReconStatus().isEmpty()) {
				q.setParameter("reconStatus", reconStatus);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", reqDto);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			respList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing query",
					ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return respList;
	}

	private String createQuery(String condtion) {

		String query = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID, "
				+ "RRC.CREATED_DATE, RRC.CREATED_BY, RRC.TO_TAX_PERIOD, "
				+ "RRC.COMPLETED_ON, "
				+ "RRC.STATUS, RRC.FILE_PATH AS PATH, COUNT(*) "
				+ "AS GSTIN_COUNT, RRGD.GSTIN, RRC.FROM_TAX_PERIOD "
				+ " FROM GL_RECON_REPORT_CONFIG RRC "
				+ "INNER JOIN GL_RECON_REPORT_GSTIN_DETAILS RRGD "
				+ "ON RRGD.RECON_REPORT_CONFIG_ID = RRC.RECON_REPORT_CONFIG_ID "
				+ " WHERE RRC.ENTITY_ID =:entityId " + condtion + " GROUP BY "
				+ "RRC.RECON_REPORT_CONFIG_ID, RRC.CREATED_DATE, "
				+ "RRC.CREATED_BY, RRC.COMPLETED_ON, RRC.STATUS,"
				+ " RRC.TO_TAX_PERIOD,RRC.FROM_TAX_PERIOD, RRGD.GSTIN, "
				+ "RRC.FILE_PATH ";

		return query;
	}

	private String queryCondition(Gstr2InitiateReconReqDto reqDto,
			boolean restrictionFlag) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin GLReconInitiateReconReportRequestStatusDaoImpl.queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (reqDto.getInitiationDateFrom() != null
				&& (!reqDto.getInitiationDateFrom().isEmpty())) {
			condition1.append(
					" AND TO_VARCHAR(RRC.CREATED_DATE,'YYYY-MM-DD') BETWEEN :initiationDateFrom ");
		}

		if (reqDto.getInitiationDateTo() != null
				&& (!reqDto.getInitiationDateTo().isEmpty())) {
			condition1.append(" AND :initiationDateTo ");
		}

		if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
			condition1
					.append(" AND RRC.RECON_REPORT_CONFIG_ID IN (:requestId) ");
		}

		/*
		 * if (restrictionFlag) { if
		 * (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
		 * condition1.append(" AND RRC.CREATED_BY = :userName "); } else {
		 * condition1.append(" AND RRC.CREATED_BY = :userName " +
		 * " OR (RRC.CREATED_BY = 'SYSTEM' AND RRC.ENTITY_ID =:entityId ) "); }
		 * } else if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId()))
		 * { condition1.append(" AND RRC.CREATED_BY IN (:initiationByUserId) ");
		 * }
		 */

		if (restrictionFlag) {
			condition1.append(" AND RRC.CREATED_BY = :userName ");

		} else if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
			condition1.append(" AND RRC.CREATED_BY IN (:initiationByUserId) ");
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			condition1.append(" AND RRC.STATUS =:reconStatus ");
		}

		return condition1.toString();
	}

}
