package com.ey.advisory.app.approvalWorkflow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ApprovalCheckerRequestDaoImpl")
public class ApprovalCheckerRequestDaoImpl
		implements ApprovalCheckerRequestDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private UserCreationRepository userRepo;

	@Override
	public List<ApprovalCheckerStatusSummaryDto> getRequestSummary(
			ApprovalCheckerRequestDto dto, String userName) {
		List<ApprovalCheckerStatusSummaryDto> respList = new ArrayList<>();

		try {
			String condtion = queryCondition(dto, userName);
			String queryString = createQuery(condtion);

			Query q = entityManager.createNativeQuery(queryString);

			if (dto.getEntityId() != null) {
				q.setParameter("entityId", dto.getEntityId());
			}

			if (dto.getReqDateFrom() != null
					&& !dto.getReqDateFrom().isEmpty()) {
				q.setParameter("requestDateFrom", dto.getReqDateFrom());
			}

			if (dto.getReqDateTo() != null && !dto.getReqDateTo().isEmpty()) {
				q.setParameter("requestDateTo", dto.getReqDateTo());
			}

			if (dto.getTaxPeriodFrom() != null
					&& !dto.getTaxPeriodFrom().isEmpty()) {
				q.setParameter("taxPeriodFrom", dto.getTaxPeriodFrom());
			}

			if (dto.getTaxPeriodTo() != null
					&& !dto.getTaxPeriodTo().isEmpty()) {
				q.setParameter("taxPeriodTo", dto.getTaxPeriodTo());
			}

			if (!dto.getRetType().isEmpty() && dto.getRetType() != null) {
				q.setParameter("retType", dto.getRetType());
			}

			if (!dto.getGstins().isEmpty() && dto.getGstins() != null) {
				q.setParameter("gstins", dto.getGstins());
			}

			if (userName != null) {
				q.setParameter("userName", userName);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", dto);
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

		String query = " SELECT PRT.REQUEST_ID, PRT.CREATED_ON, PRT.MAKER_NAME, "
				+ " PRT.MAKER_EMAIL, PRT.MAKER_COMMENTS, PRT.ACTION, "
				+ " PRT.GSTIN,PRT.TAX_PERIOD,PRT.RETURN_TYPE, "
				+ " CHD.CHECKER_COMMENTS,CHD.REQUEST_STATUS,CHD.ACTION_BY, "
				+ " CHD.ACTION_DATE_TIME,CHD.REVERT_BY,CHD.REVERT_DATE_TIME FROM"
				+ " APPROVAL_WORKFLOW_REQUEST PRT "
				+ " INNER JOIN CHECKER_APPROVAL_STATUS CHD "
				+ " ON PRT.REQUEST_ID = CHD.REQUEST_ID "
				+ " WHERE CHD.CHECKER_NAME =:userName "
				+ " AND PRT.ENTITY_ID =:entityId" + condtion
				+ " ORDER BY PRT.REQUEST_ID ";

		return query;
	}

	private String queryCondition(ApprovalCheckerRequestDto dto,
			String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin ApprovalCheckerRequestDaoImpl .queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (dto.getReqDateFrom() != null && !dto.getReqDateFrom().isEmpty()) {
			condition1.append(
					" AND TO_VARCHAR(PRT.CREATED_ON,'YYYY-MM-DD') BETWEEN :requestDateFrom ");
		}

		if (dto.getReqDateTo() != null && !dto.getReqDateTo().isEmpty()) {
			condition1.append(" AND :requestDateTo ");
		}

		if (!dto.getGstins().isEmpty() && dto.getGstins() != null) {
			condition1.append(" AND PRT.GSTIN IN (:gstins) ");
		}

		if (dto.getTaxPeriodFrom() != null && !dto.getTaxPeriodTo().isEmpty()) {
			condition1.append(" AND PRT.TAX_PERIOD BETWEEN :taxPeriodFrom ");
		}

		if (dto.getTaxPeriodTo() != null && !dto.getTaxPeriodTo().isEmpty()) {
			condition1.append(" AND :taxPeriodTo ");
		}

		if (!dto.getRetType().isEmpty() && dto.getRetType() != null) {
			condition1.append(" AND PRT.RETURN_TYPE IN (:retType) ");
		}

		return condition1.toString();

	}

	private ApprovalCheckerStatusSummaryDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " ApprovalCheckerStatusSummaryDto object";
			LOGGER.debug(str);
		}

		ApprovalCheckerStatusSummaryDto convert = new ApprovalCheckerStatusSummaryDto();

		convert.setRequestId(
				(arr[0] != null) ? "RFA-" + arr[0].toString() : null);
		convert.setRequestDateTime((arr[1] != null)
				? dateChange(arr[1].toString().substring(0, 19)) : null);
		convert.setReqBy((arr[2] != null && arr[3] != null)
				? String.format("%s(%s)", arr[3].toString(), arr[2].toString())
				: null);
		convert.setCommMakers((arr[4] != null) ? arr[4].toString() : null);
		if (arr[5] != null)
			convert.setTaskDesc("Requested for " + arr[5].toString());
		else
			convert.setTaskDesc(null);
		convert.setGstin((arr[6] != null) ? arr[6].toString() : null);
		convert.setTaxPeriod((arr[7] != null) ? arr[7].toString() : null);
		convert.setRetType((arr[8] != null) ? arr[8].toString() : null);
		convert.setCommChecker((arr[9] != null) ? arr[9].toString() : null);
		convert.setStatus((arr[10] != null) ? arr[10].toString() : null);
		convert.setActionTakenBy(
				(arr[11] != null)
						? String.format("%s(%s)",
								userRepo.findEmailByUser(arr[11].toString())
										.get(0).toString(),
								arr[11].toString())
						: null);
		convert.setActionDateTime((arr[12] != null)
				? dateChange(arr[12].toString().substring(0, 19)) : null);
		convert.setRevertBackAction((arr[13] != null && arr[14] != null)
				? String.format("%s(%s)",
						userRepo.findEmailByUser(arr[13].toString()).get(0)
								.toString(),
						arr[13].toString()) + " "
						+ dateChange(arr[14].toString().substring(0, 19))
				: null);

		return convert;
	}

	public String dateChange(String oldDate) {
		DateTimeFormatter formatter = null;
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTimes = LocalDateTime.parse(oldDate, formatter);
		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

}
