/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("SalesRegisterRequestStatusDaoImpl")
public class SalesRegisterRequestStatusDaoImpl
		implements SalesRegisterRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private CommonUtility commonUtility;

	private Gstr1SalesRegisterRequestStatusDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr1SalesRegisterRequestStatusDto object";
			LOGGER.debug(str);
		}

		Gstr1SalesRegisterRequestStatusDto convert = new Gstr1SalesRegisterRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		convert.setRequestId(requestId);
		Timestamp date = (Timestamp) arr[6];
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			String dateTime = EYDateUtil.toISTDateTimeFromUTC(dt).toString();
			dateTime = String.format("%-20s", dateTime).replace(' ', '0');
			String Date = dateTime.substring(0, 10);
			String Time = dateTime.substring(11, 19);
			String updatedDateTime = (Date + " " + Time);
			convert.setInitiatedOn(updatedDateTime);
		}

		convert.setInitiatedBy((String) arr[7]);

		date = (Timestamp) arr[8];
		if (date != null) {
			String cdateTime = date != null
					? EYDateUtil.toISTDateTimeFromUTC(date.toLocalDateTime())
							.toString()
					: null;
			String cDate = cdateTime.substring(0, 10);
			String cTime = cdateTime.substring(11, 19);
			String cupdatedDateTime = (cDate + " " + cTime);
			convert.setCompletionOn(cupdatedDateTime);
		}
		convert.setStatus((String) arr[9]);
		BigInteger bi = GenUtil.getBigInteger(arr[1]);
		Integer gstnCount = bi.intValue();
		convert.setGstinCount(gstnCount);
		if (arr[10] != null) {
			String gstins = ((String) arr[10]);
			List<GstinDto> gstinsList = new ArrayList<GstinDto>();

			String[] gstinArray = gstins.split(",");
			for (int i = 0; i < gstinArray.length; i++) {
				GstinDto gstinDto = new GstinDto();
				gstinDto.setGstin(gstinArray[i]);
				gstinsList.add(gstinDto);
			}
			convert.setGstins(gstinsList);
		}

		if (arr[4] != null && arr[5] != null) {
			Integer toTaxPeriod = ((Integer) arr[5]).intValue();
			convert.setToTaxPeriod(toTaxPeriod);

			Integer fromTaxPeriod = ((Integer) arr[4]).intValue();
			convert.setFromTaxPeriod(fromTaxPeriod);
		} else {
			convert.setToTaxPeriod(null);
			convert.setFromTaxPeriod(null);
		}

		convert.setReconType((String) arr[2]);

		return convert;
	}

	@Override
	public List<BigInteger> getRequestIds(String userName, Long entityId)
			throws AppException {
		try {
			String queryString = "SELECT DISTINCT RECON_REPORT_CONFIG_ID AS REQUEST_ID"
					+ " FROM TBL_SRVSDIGIGST_RECON_CONFIG"
					+ " WHERE ENTITY_ID =:entityId ORDER BY 1 DESC";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
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
					+ "SalesRegisterRequestStatusDaoImpl.getRequestIds");
		}
	}

	@Override
	public List<Gstr1SalesRegisterRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		List<Gstr1SalesRegisterRequestStatusDto> respList = null;

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
			} else if (reconStatus.equalsIgnoreCase("RECON INITIATED")) {
				reconStatus = "RECON_INITIATED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			} else if (reconStatus.equalsIgnoreCase("NO DATA FOUND")) {
				reconStatus = "NO_DATA_FOUND";
			}
		}

		boolean isusernamereq = commonUtility
				.getAnsForQueMultipleUserAccessToAsyncReports(entityId);
		String initiatedby = "";

		try {

			if (isusernamereq) {
				initiatedby = " AND INITIATED_BY =:userName ";
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

			String condtion = queryCondition(reqDto, isusernamereq,
					initiatedby);
			String queryString = createQuery(condtion, initiatedby);

			Query q = entityManager.createNativeQuery(queryString);

			if (reqDto.getEntityId() != null) {
				q.setParameter("entityId", entityId);
			}
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

	private String createQuery(String condtion, String initiatedby) {

		String query = " SELECT RECON_REPORT_CONFIG_ID,COUNT(*) CNT_GSTINS,'Sr VS Digi'"
				+ " as recon_type,MAX(criteria) AS criteria,MAX(from_tax_period) AS"
				+ " from_tax_period,MAX(to_tax_period) AS to_tax_period,Min(INITIATED_ON)"
				+ " AS INITIATION,MAX(INITIATED_BY) AS INITIATION_BY,MAX(COMPLETED_ON)"
				+ " AS COMPLETION, MAX(STATUS) AS STATUS,STRING_AGG(GSTIN,',' ORDER BY ID)"
				+ " AS GSTIN_LIST FROM TBL_SRVSDIGIGST_RECON_CONFIG WHERE"
				+ " ENTITY_ID =:entityId " + condtion
				+ " GROUP BY RECON_REPORT_CONFIG_ID ORDER BY RECON_REPORT_CONFIG_ID DESC";

		return query;
	}

	private String queryCondition(Gstr2InitiateReconReqDto reqDto,
			boolean isusernamereq, String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin SalesRegisterRequestStatusDaoImpl .queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder queryBuilder = new StringBuilder();

		// FROM_TAX_PERIOD,TO_TAX_PERIOD -> VARCHAR
		// from_period >= 202305 and to_period <=202307

		if (reqDto.getReturnPeriodFrom() != 0
				&& reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append(" AND ( FROM_TAX_PERIOD >= :returnPeriodFrom"
					+ " AND TO_TAX_PERIOD <= :returnPeriodTo )");
		} else if (reqDto.getReturnPeriodFrom() != 0
				&& reqDto.getReturnPeriodTo() == 0) {
			queryBuilder.append(" AND FROM_TAX_PERIOD >= :returnPeriodFrom");
		}

		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder
						.append(" AND INITIATED_BY IN (:initiationByUserId) ");
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

}
