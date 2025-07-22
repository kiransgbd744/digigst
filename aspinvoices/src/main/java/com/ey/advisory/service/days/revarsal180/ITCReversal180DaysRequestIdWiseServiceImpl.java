/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Config180DaysComputeRepository;
import com.ey.advisory.app.data.repositories.client.ITCRevesal180ComputeGstinDetailsRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ibm.icu.text.SimpleDateFormat;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ITCReversal180DaysRequestIdWiseServiceImpl")
public class ITCReversal180DaysRequestIdWiseServiceImpl
		implements ITCReversal180DaysRequestIdWiseService {

	@Autowired
	@Qualifier("Config180DaysComputeRepository")
	Config180DaysComputeRepository configRepo;

	@Autowired
	@Qualifier("ITCRevesal180ComputeGstinDetailsRepository")
	ITCRevesal180ComputeGstinDetailsRepository gstinRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public List<ITCReversal180DaysRequestIdWiseDto> getRequestStatus(
			ITCReversal180DaysReqDto reqDto, String userName) {

		ArrayList<ITCReversal180DaysRequestIdWiseDto> collect = null;

		Long entityId = reqDto.getEntityId();

		String criteria = reqDto.getCriteria();
		String fromDate = reqDto.getFromDate();
		String toDate = reqDto.getToDate();

		// taxPeriod
		String fromTaxPeriod = reqDto.getFromTaxPeriod();
		String toTaxPeriod = reqDto.getToTaxPeriod();

		List<String> initiationByUserId = reqDto.getInitiationByUserId();

		String reconStatus = reqDto.getReconStatus();
		
		if (!Strings.isNullOrEmpty(reconStatus)) {
			if(reconStatus.contains("_"))
		    reconStatus = reconStatus.replaceAll("_", " ").trim();
		}
		
//		if (Strings.isNullOrEmpty(reconStatus)) {
//			if (reconStatus.equalsIgnoreCase("REPORT_GENERATION_FAILED")) {
//				reconStatus = "REPORT GENERATION FAILED";
//			} else if (reconStatus.equalsIgnoreCase("REPORT_GENERATED")) {
//				reconStatus = "REPORT GENERATED";
//			} else if (reconStatus.equalsIgnoreCase("RECON_INITIATED")) {
//				reconStatus = "RECON INITIATED";
//			} else if (reconStatus.equalsIgnoreCase("RECON_FAILED")) {
//				reconStatus = "RECON FAILED";
//			} else if (reconStatus.equalsIgnoreCase("NO_DATA_FOUND")) {
//				reconStatus = "NO DATA FOUND";
//			}
//		}

		try {
			// userName = "P2001353321"; testing username
			boolean isusernamereq = commonUtility
					.getAnsForQueMultipleUserAccessToAsyncReports(entityId);

			String initiatedby = "";
			if (isusernamereq) {
				initiatedby = " AND CREATED_BY = :userName";
			}
			int returnPeriodFrom = 0;
			int returnPeriodTo = 0;
			if (criteria != null && criteria.equalsIgnoreCase("taxPeriod")) {
				if (!Strings.isNullOrEmpty(fromTaxPeriod)) {
					returnPeriodFrom = GenUtil
							.convertTaxPeriodToInt(fromTaxPeriod);
					reqDto.setReturnPeriodFrom(returnPeriodFrom);
				}

				if (!Strings.isNullOrEmpty(toTaxPeriod)) {
					returnPeriodTo = GenUtil.convertTaxPeriodToInt(toTaxPeriod);
					reqDto.setReturnPeriodTo(returnPeriodTo);
				}
			}

			String condtion = createQueryCondition(reqDto, isusernamereq,
					initiatedby);
			String queryString = createQueryString(userName, condtion);

			Query q = entityManager.createNativeQuery(queryString);

			if (reqDto.getEntityId() != null) {
				q.setParameter("entityId", entityId);
			}
			if (criteria != null && (criteria.equalsIgnoreCase("docDate")
					|| criteria.equalsIgnoreCase("accVoucherDate"))) {
				if (fromDate != null && toDate != null) {
					q.setParameter("fromDate", fromDate);
					q.setParameter("toDate", toDate);
				}
			}

			if (criteria != null && criteria.equalsIgnoreCase("taxPeriod")) {

				if (returnPeriodFrom != 0 && returnPeriodTo != 0) {
					q.setParameter("returnPeriodFrom", returnPeriodFrom);
					q.setParameter("returnPeriodTo", returnPeriodTo);
				} else if (returnPeriodFrom != 0 && returnPeriodTo == 0) {
					q.setParameter("returnPeriodFrom", returnPeriodFrom);
				}
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
					&& !reqDto.getReconStatus().isEmpty()) {
				q.setParameter("reconStatus", reconStatus);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", reqDto);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			int len = list.size();
			System.out.println(len);
			collect = list.stream().map(o -> convertDtoNew(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(
					"Error occure in ITCReversal180DaysRequestIdWiseServiceImpl");
		}
		return collect;

	}

	private String createQueryString(String userName, String condtion) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}

		String query = "SELECT * FROM TBL_180_DAYS_COMPUTE_CONFIG"
				+ " WHERE ENTITY_ID = :entityId " + condtion
				+ " ORDER BY 1 DESC";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					query);
			LOGGER.debug(str);
		}

		return query;
	}

	private String createQueryCondition(ITCReversal180DaysReqDto reqDto,
			boolean isusernamereq, String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Gstr6CredDistDaoImpl.createQueryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder queryBuilder = new StringBuilder();

		String criteria = reqDto.getCriteria();
		String fromDate = reqDto.getFromDate();
		String toDate = reqDto.getToDate();

		if (criteria != null && criteria.equalsIgnoreCase("docDate")) {
			if (fromDate != null && toDate != null) {
				queryBuilder.append(
						" AND ((TO_DATE(FROM_DOC_DATE, 'DD-MM-YYYY')>=TO_DATE(:fromDate,'DD-MM-YYYY')) AND");
				queryBuilder.append(
						" TO_DATE(TO_DOC_DATE,'DD-MM-YYYY')<=TO_DATE(:toDate,'DD-MM-YYYY')) ");
			}
		} else if (criteria != null
				&& criteria.equalsIgnoreCase("accVoucherDate")) {
			if (fromDate != null && toDate != null) {
				queryBuilder.append(
						" AND ((TO_DATE(FROM_ACC_DATE, 'DD-MM-YYYY')>=TO_DATE(:fromDate,'DD-MM-YYYY')) AND");
				queryBuilder.append(
						" TO_DATE(TO_ACC_DATE,'DD-MM-YYYY')<=TO_DATE(:toDate,'DD-MM-YYYY')) ");
			}
		} else if (criteria != null && criteria.equalsIgnoreCase("taxPeriod")) {
			if (reqDto.getReturnPeriodFrom() != 0
					&& reqDto.getReturnPeriodTo() != 0) {
				queryBuilder.append("AND ( FROM_TAX_PERIOD >= :returnPeriodFrom"
						+ " AND TO_TAX_PERIOD <= :returnPeriodTo )");
			} else if (reqDto.getReturnPeriodFrom() != 0
					&& reqDto.getReturnPeriodTo() == 0) {
				queryBuilder.append("AND FROM_TAX_PERIOD >= :returnPeriodFrom");
			}
		}

		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder
						.append(" AND CREATED_BY IN (:initiationByUserId) ");
			}
		}

		if (isusernamereq) {
			queryBuilder.append(initiatedby);
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND STATUS =:reconStatus ");
		}

		return queryBuilder.toString();
	}

	private ITCReversal180DaysRequestIdWiseDto convertDtoNew(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr1Vs3BRequestStatusDto object";
			LOGGER.debug(str);
		}

		ITCReversal180DaysRequestIdWiseDto dto = new ITCReversal180DaysRequestIdWiseDto();
		Long computeId = (GenUtil.getBigInteger(arr[0])).longValue();
		dto.setComputeId(computeId);
		String status = arr[1] != null ? (String) arr[1] : null;

		dto.setCreatedBy((String) arr[8]);

		String criteria = "Document Date";
		String toDate = null;
		String fromDate = null;

		String fromAccDate = arr[4] != null ? (String) arr[4] : null;
		String toAccDate = arr[5] != null ? (String) arr[5] : null;
		String fromDocDate = arr[2] != null ? (String) arr[2] : null;
		String toDocDate = arr[3] != null ? (String) arr[3] : null;

		Integer fromTaxPeriod = arr[15] != null ? ((Integer) arr[15]).intValue()
				: null;
		Integer toTaxPeriod = arr[16] != null ? ((Integer) arr[16]).intValue()
				: null;
		String inputFormat = "yyyyMM";
		String outputFormat = "MMyyyy";
		SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);

		SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
		if (toAccDate != null && fromAccDate != null) {
			criteria = "Voucher Date";
			toDate = toAccDate;
			fromDate = fromAccDate;

		} else if (toDocDate != null && fromDocDate != null) {
			toDate = toDocDate;
			fromDate = fromDocDate;
			criteria = "Document Date";
		} else {
			String toTaxDate = toTaxPeriod.toString();
			String fromTaxDate = fromTaxPeriod.toString();

			try {

				Date date = inputFormatter.parse(toTaxDate);

				toDate = outputFormatter.format(date);

				Date date1 = inputFormatter.parse(fromTaxDate);

				fromDate = outputFormatter.format(date1);

			} catch (ParseException e) {

				LOGGER.debug("error occured while converting Taxperiod Format");

			}

			criteria = "Tax Period";
		}

		dto.setCriteria(criteria);

		dto.setFromDate(fromDate);

		Timestamp creationDate = (Timestamp) arr[9];
		if (creationDate != null) {
			LocalDateTime dt = creationDate.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dto.setInitiatedOn(removingMilliSec(dateTime));
		}

		dto.setStatus(status);
		dto.setToDate(toDate);

		List<ITCRevesal180ComputeGstinDetailsEntity> gstinDetails = gstinRepo
				.FindByComputeId(computeId);

		List<GstinDto> gstins = new ArrayList<>();

		for (ITCRevesal180ComputeGstinDetailsEntity obj : gstinDetails) {
			GstinDto gstinDto = new GstinDto();
			gstinDto.setGstin(obj.getGstin());
			gstins.add(gstinDto);
		}
		dto.setGstin(gstins);

		dto.setGstinCount(gstins != null ? gstins.size() : null);

		return dto;
	}

	private String removingMilliSec(String status) {

		String[] statusArray = status.split("T");
		String time = statusArray[1].substring(0, 8);

		status = statusArray[0] + " " + time;

		return status;
	}

}
