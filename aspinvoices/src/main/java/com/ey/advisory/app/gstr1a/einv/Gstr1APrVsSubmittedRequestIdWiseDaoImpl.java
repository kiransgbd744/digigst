package com.ey.advisory.app.gstr1a.einv;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmReconReportRequestStatusDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr1APrVsSubmittedRequestIdWiseDaoImpl")
public class Gstr1APrVsSubmittedRequestIdWiseDaoImpl
		implements Gstr1APrVsSubmittedRequestIdWiseDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrVsSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		Long entityId = reqDto.getEntityId();

		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();

		String reconStatus = reqDto.getReconStatus();

		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			} else if (reconStatus.equalsIgnoreCase("RECON REQUESTED")) {
				reconStatus = "RECON_REQUESTED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			} else if (reconStatus.equalsIgnoreCase("RECON COMPLETED")) {
				reconStatus = "RECON_COMPLETED";
			} else if (reconStatus.equalsIgnoreCase("RECON INITIATED")) {
				reconStatus = "RECON_INITIATED";
			}
		}

		boolean isusernamereq = commonUtility
				.getAnsForQueMultipleUserAccessToAsyncReports(entityId);
		String initiatedby = "";
		if (isusernamereq) {
			initiatedby = " AND RC.CREATED_BY = :userName ";
		}

		int returnPeriodFrom = 0;
		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			returnPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			reqDto.setReturnPeriodFrom(returnPeriodFrom);
		}
		int returnPeriodTo = 0;
		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			returnPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
			reqDto.setReturnPeriodTo(returnPeriodTo);
		}

		String condtion = createPrVsSubQueryCondition(reqDto, isusernamereq,
				initiatedby);
		String queryString = createPrVsSubQueryString(userName, condtion);

		Query q = entityManager.createNativeQuery(queryString);

		if (returnPeriodFrom != 0 && returnPeriodTo != 0) {
			q.setParameter("returnPeriodFrom", returnPeriodFrom);
			q.setParameter("returnPeriodTo", returnPeriodTo);
		} else if (returnPeriodFrom != 0 && returnPeriodTo == 0) {
			q.setParameter("returnPeriodFrom", returnPeriodFrom);
		}

		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				q.setParameter("initiationByUserId", initiationByUserId);
			}
		}

		if (isusernamereq) {
			q.setParameter("userName", userName);
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			q.setParameter("reconStatus", reconStatus);
		}

		q.setParameter("entityId", entityId);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<Gstr1PrVsSubmReconReportRequestStatusDto> retList = list.stream()
				.map(o -> convertPrSubmitted(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private Gstr1PrVsSubmReconReportRequestStatusDto convertPrSubmitted(
			Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr1EinvInitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}

		Gstr1PrVsSubmReconReportRequestStatusDto dto = new Gstr1PrVsSubmReconReportRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		dto.setRequestId(requestId);
		Timestamp date = (Timestamp) arr[1];
		LocalDateTime dt = date.toLocalDateTime();
		dto.setInitiatedOn(
				getFormattedTime(EYDateUtil.toISTDateTimeFromUTC(dt)));
		dto.setInitiatedBy((String) arr[2]);

		if (arr[3] != null) {
			String fromTaxPeriod = ((String) arr[3]).toString();
			dto.setFromTaxPeriod(fromTaxPeriod);
		} else {
			dto.setFromTaxPeriod(null);
		}
		if (arr[4] != null) {
			String toTaxPeriod = ((String) arr[4]).toString();
			dto.setToTaxPeriod(toTaxPeriod);
		} else {
			dto.setToTaxPeriod(null);
		}

		date = (Timestamp) arr[5];
		String ldt = date != null
				? getFormattedTime(
						EYDateUtil.toISTDateTimeFromUTC(date.toLocalDateTime()))
				: null;
		dto.setCompletionOn(ldt);
		dto.setStatus((String) arr[6]);
		dto.setPath((String) arr[7]);
		BigInteger bi = GenUtil.getBigInteger(arr[8]);
		Integer gstnCount = bi.intValue();
		dto.setGstinCount(gstnCount);

		GstinDto gstinDto = new GstinDto((String) arr[9]);
		dto.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));

		return dto;
	}

	private String getFormattedTime(LocalDateTime dateStr) {
		if (dateStr == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(dateStr);
	}

	private String createPrVsSubQueryString(String userName, String condtion) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		String queryStr = "SELECT RC.RECON_CONFIG_ID AS REQUEST_ID, "
				+ "RC.CREATED_DATE, RC.CREATED_BY, RC.FROM_TAX_PERIOD, RC.To_TAX_PERIOD, "
				+ "RC.COMPLETED_ON, "
				+ "RC.STATUS, RC.FILE_PATH AS PATH, COUNT(*) "
				+ "AS GSTIN_COUNT, RGD.GSTIN FROM "
				+ "GSTR1A_PR_SUBMITED_RECON_CONFIG RC "
				+ "INNER JOIN GSTR1A_PR_VS_SUBM_RECON_GSTIN_DETAILS RGD "
				+ "ON RGD.RECON_CONFIG_ID = RC.RECON_CONFIG_ID WHERE"
				+ " RC.ENTITY_ID =:entityId " + condtion
				+ " GROUP BY RC.RECON_CONFIG_ID, RC.CREATED_DATE, RC.CREATED_BY, "
				+ "RC.COMPLETED_ON, RC.STATUS, RC.FROM_TAX_PERIOD,RC.To_TAX_PERIOD, RGD.GSTIN, "
				+ "RC.FILE_PATH";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}

	private String createPrVsSubQueryCondition(Gstr2InitiateReconReqDto reqDto,
			boolean isusernamereq, String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Gstr1EinvInitiateReconReptReqStatusDaoImpl.createPrVsSubQueryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder queryBuilder = new StringBuilder();

		if (reqDto.getReturnPeriodFrom() != 0
				&& reqDto.getReturnPeriodTo() != 0) {
			queryBuilder
					.append("AND ( RC.FROM_RETURN_PERIOD >= :returnPeriodFrom"
							+ " AND RC.TO_RETURN_PERIOD <= :returnPeriodTo )");
		} else if (reqDto.getReturnPeriodFrom() != 0
				&& reqDto.getReturnPeriodTo() == 0) {
			queryBuilder
					.append("AND RC.FROM_RETURN_PERIOD >= :returnPeriodFrom");
		}

		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder
						.append(" AND RC.CREATED_BY IN (:initiationByUserId) ");
			}
		}

		if (isusernamereq) {
			queryBuilder.append(initiatedby);
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND RC.STATUS =:reconStatus ");
		}

		return queryBuilder.toString();
	}
}
