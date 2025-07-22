package com.ey.advisory.app.data.daos.client.simplified;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.simplified.GetGstnSubmitStatusDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1FlagRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.ITC04RequestDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("UpdatedMofifiedDateFetchDaoImpl")
public class UpdatedMofifiedDateFetchDaoImpl {

	public static final String Success = "Success";
	public static final String inProgress = "In Progress";
	public static final String Failed = "Failed";
	// LastUpdatedDateDto

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	GstnSubmitRepository repo;

	@Autowired
	@Qualifier("SignAndFileRepository")
	SignAndFileRepository repoSignFile;

	@Autowired
	GSTNDetailRepository gstnDetailRepo;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UpdatedMofifiedDateFetchDaoImpl.class);

	public String loadBasicSummarySection(Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String lastUpdatedDate = "";
		String taxPeriodReq = request.getTaxPeriod();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != null) {

			build.append(" AND RET_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);

					/*
					 * SimpleDateFormat formatter = new
					 * SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); Date
					 * updatedDate =
					 * formatter.parse(String.valueOf(list.get(0))); //
					 * System.out.println(updateDateStr);
					 * 
					 * 
					 * formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
					 * lastUpdatedDate = formatter.format(updatedDate);
					 */
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			/*
			 * e.printStackTrace(); throw new
			 * AppException("Unexpected error in query execution.", e);
			 */
		}
		return lastUpdatedDate;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT MAX(CREATED_ON) LAST_UPDATED_DATE "
				+ "FROM GETGSTR1_RATE_SUMMARY WHERE " + "IS_DELETE = FALSE AND "
				+ buildQuery;

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		return queryStr;
	}

	public String loadgst7BasicSummarySection(Anx2GetInvoicesReqDto request) {
		// TODO Auto-generated method stub

		String lastUpdatedDate = "";
		String taxPeriodReq = request.getReturnPeriod();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		String gstin = request.getGstin();

		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {

			build.append(" AND GSTIN = :gstin");

		}
		if (taxPeriodReq != null) {
			build.append(" AND RETURN_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = creategstr7QueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {

				q.setParameter("gstin", gstin);

			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return lastUpdatedDate;
	}

	private String creategstr7QueryString(String buildQuery) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT MAX(CREATED_ON) LAST_UPDATED_DATE "
				+ "FROM GETGSTR7_SECTIONWISE_SUMMARY_TDS WHERE "
				+ "IS_DELETE = FALSE " + buildQuery;

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		LOGGER.error("bufferString-------------------------->" + buildQuery);
		return queryStr;
	}

	/**
	 * Getting GSTN SUBMIT STATUS
	 */

	public GetGstnSubmitStatusDto loadSubmitBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String taxPeriodReq = request.getTaxPeriod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		GetGstnSubmitStatusDto returnSubmit = new GetGstnSubmitStatusDto();
		String returnType = request.getReturnType();
		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		StringBuilder build = new StringBuilder();

		if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {

			build.append(" GSTIN  IN :gstinList");

		}
		if (returnType != null) {

			build.append(" AND RETURN_TYPE = :returnType");

		}
		if (taxPeriodReq != null) {
			build.append(" AND RET_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createSubmitQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstinList != null && !gstinList.isEmpty()) {

				q.setParameter("gstinList", gstinList);

			}
			if (returnType != null && !returnType.isEmpty()) {

				q.setParameter("returnType", returnType);

			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			List<GetGstnSubmitStatusDto> retList = list.parallelStream()
					.map(o -> convertSubmit(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (retList != null && retList.size() > 0) {
				String gstnStatus = retList.get(0).getGstnStatus();
				String status = retList.get(0).getAspStatus();
				LocalDateTime modifiedOn = retList.get(0).getModified_on();

				if ((!"P".equalsIgnoreCase(gstnStatus) || gstnStatus == null)
						&& (status == "POLLING_FAILED"
								|| status == "SUBMIT_FAILED")) {

					returnSubmit.setStatus(Failed);

					if (modifiedOn != null) {
						LocalDateTime istDateTimeFromUTC = EYDateUtil
								.toISTDateTimeFromUTC(modifiedOn);

						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("yyyy-MM-dd HH:mm:ss");

						returnSubmit.setTimeStamp(
								FOMATTER.format(istDateTimeFromUTC));
					}

				} else if (gstnStatus == null) {

					returnSubmit.setStatus(inProgress);

					if (modifiedOn != null) {
						LocalDateTime istDateTimeFromUTC = EYDateUtil
								.toISTDateTimeFromUTC(modifiedOn);

						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("yyyy-MM-dd HH:mm:ss");

						returnSubmit.setTimeStamp(
								FOMATTER.format(istDateTimeFromUTC));
					}

				} else if ("P".equalsIgnoreCase(gstnStatus)) {

					returnSubmit.setStatus(Success);

					if (modifiedOn != null) {
						LocalDateTime istDateTimeFromUTC = EYDateUtil
								.toISTDateTimeFromUTC(modifiedOn);

						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("yyyy-MM-dd HH:mm:ss");

						returnSubmit.setTimeStamp(
								FOMATTER.format(istDateTimeFromUTC));
					}

				}
			}
		} catch (Exception e) {
			LOGGER.error("While Executing Query getting Error...{}", e);
		}

		return returnSubmit;
	}

	private String createSubmitQueryString(String buildQuery) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT GSTN_SUBMIT_STATUS,STATUS,MODIFIED_ON "
				+ "FROM GSTN_SUBMIT WHERE " + "IS_DELETE = FALSE AND "
				+ buildQuery + " ORDER BY ID DESC LIMIT 1 ";

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		LOGGER.error("bufferString-------------------------->" + buildQuery);
		return queryStr;
	}

	/**
	 * Getting GSTN SAVE STATUS
	 */

	public GetGstnSubmitStatusDto loadForGetGstnSaveStatus(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub
		String taxPeriodReq = request.getTaxPeriod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		GetGstnSubmitStatusDto returnSubmit = new GetGstnSubmitStatusDto();
		String returnType = request.getReturnType();
		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		StringBuilder build = new StringBuilder();

		if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {

			build.append(" GSTIN  IN :gstinList");

		}
		if (returnType != null) {

			build.append(" AND RETURN_TYPE = :returnType");

		}
		if (taxPeriodReq != null) {
			build.append(" AND TAX_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createSaveFileQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstinList != null && !gstinList.isEmpty()) {

				q.setParameter("gstinList", gstinList);

			}
			if (returnType != null && !returnType.isEmpty()) {

				q.setParameter("returnType", returnType);

			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			// Bhavya changes for sigin and file status
			Object[] data = null;
			if (list != null && !list.isEmpty()) {
				data = list.get(0);
			}
			if (data != null && !data.toString().isEmpty()) {
				returnSubmit.setStatus((String) data[0]);
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(
								((Timestamp) data[1]).toLocalDateTime());

				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyy-MM-dd HH:mm:ss");

				returnSubmit.setTimeStamp(FOMATTER.format(istDateTimeFromUTC));
			}

			/*
			 * List<GetGstnSubmitStatusDto> retList = list.parallelStream()
			 * .map(o -> convertSubmit(o))
			 * .collect(Collectors.toCollection(ArrayList::new));
			 * 
			 * if (retList != null && retList.size() > 0) { String gstnStatus =
			 * retList.get(0).getGstnStatus(); String status =
			 * retList.get(0).getAspStatus(); LocalDateTime modifiedOn =
			 * retList.get(0).getModified_on();
			 * 
			 * if ((!"P".equalsIgnoreCase(gstnStatus) || gstnStatus == null) &&
			 * (status == "POLLING_FAILED" || status == "SUBMIT_FAILED")) {
			 * 
			 * returnSubmit.setStatus(Failed);
			 * 
			 * if (modifiedOn != null) { LocalDateTime istDateTimeFromUTC =
			 * EYDateUtil .toISTDateTimeFromUTC(modifiedOn);
			 * 
			 * DateTimeFormatter FOMATTER = DateTimeFormatter
			 * .ofPattern("yyyy-MM-dd HH:mm:ss");
			 * 
			 * returnSubmit.setTimeStamp( FOMATTER.format(istDateTimeFromUTC));
			 * }
			 * 
			 * } else if (gstnStatus == null) {
			 * 
			 * returnSubmit.setStatus(inProgress);
			 * 
			 * if (modifiedOn != null) { LocalDateTime istDateTimeFromUTC =
			 * EYDateUtil .toISTDateTimeFromUTC(modifiedOn);
			 * 
			 * DateTimeFormatter FOMATTER = DateTimeFormatter
			 * .ofPattern("yyyy-MM-dd HH:mm:ss");
			 * 
			 * returnSubmit.setTimeStamp( FOMATTER.format(istDateTimeFromUTC));
			 * }
			 * 
			 * } else if ("P".equalsIgnoreCase(gstnStatus)) {
			 * 
			 * returnSubmit.setStatus(Success);
			 * 
			 * if (modifiedOn != null) { LocalDateTime istDateTimeFromUTC =
			 * EYDateUtil .toISTDateTimeFromUTC(modifiedOn);
			 * 
			 * DateTimeFormatter FOMATTER = DateTimeFormatter
			 * .ofPattern("yyyy-MM-dd HH:mm:ss");
			 * 
			 * returnSubmit.setTimeStamp( FOMATTER.format(istDateTimeFromUTC));
			 * }
			 * 
			 * } }
			 */
		} catch (Exception e) {
			LOGGER.error("While Executing Query getting Error...{}", e);
		}

		return returnSubmit;
	}

	private String createSaveFileQueryString(String buildQuery) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT STATUS,MODIFIED_ON " + "FROM SIGN_FILE WHERE "
				+ "IS_DELETE = FALSE AND " + buildQuery
				+ " ORDER BY ID DESC LIMIT 1 ";

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		LOGGER.error("bufferString-------------------------->" + buildQuery);
		return queryStr;
	}

	// ITC 04

	public String loadBasicITC04SummarySection(ITC04RequestDto request) {
		// TODO Auto-generated method stub

		String lastUpdatedDate = "";
		String taxPeriodReq = request.getTaxPeriod();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN (:gstinList) ");
			}
		}
		if (taxPeriodReq != null) {

			build.append(" AND RETURN_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString().substring(4);
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createItc04QueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			/*
			 * e.printStackTrace(); throw new
			 * AppException("Unexpected error in query execution.", e);
			 */
		}
		return lastUpdatedDate;
	}

	private String createItc04QueryString(String buildQuery) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT MAX(CREATED_ON) LAST_UPDATED_DATE "
				+ "FROM GETITC04_SUMMARY WHERE " + "IS_DELETE = FALSE AND "
				+ buildQuery;

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		LOGGER.error("bufferString-------------------------->" + buildQuery);
		return queryStr;
	}

	private GetGstnSubmitStatusDto convertSubmit(Object[] arr) {
		GetGstnSubmitStatusDto obj = new GetGstnSubmitStatusDto();
		obj.setGstnStatus((String) arr[0]);
		obj.setAspStatus((String) arr[1]);
		/*
		 * Timestamp timestamp = ((Timestamp) arr[2]).toLocalDateTime();
		 * timestamp.toLocalDateTime()
		 */
		obj.setModified_on(((Timestamp) arr[2]).toLocalDateTime());

		return obj;
	}

	// SaveGstn

	public String loadSaveGstnStatus(Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String lastUpdatedDate = "";
		String taxPeriodReq = request.getTaxPeriod();
		String returnType = request.getReturnType();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		// added Extra logic to show the status at PS screen
		if (gstinList == null || gstinList.isEmpty()) {
			List<Long> entityIds = request.getEntityId();
			if (entityIds != null) {
				gstinList = gstnDetailRepo.findByEntityId(entityIds);
			}
		}

		StringBuilder build = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != null) {

			build.append(" AND RETURN_PERIOD = :taxPeriodReq  ");
		}
		if (returnType != null) {

			build.append(" AND RETURN_TYPE = :returnType  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createSaveGstnQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}
			if (returnType != null) {
				q.setParameter("returnType", returnType);
			}

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return lastUpdatedDate;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createSaveGstnQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "select max(CREATED_ON) from GSTN_USER_REQUEST "
				+ "WHERE IS_DELETE = FALSE AND REQUEST_TYPE = 'SAVE' AND "
				+ buildQuery;

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		return queryStr;
	}

	/**
	 * This Method is Used for Giving Flags for HSN & NIL to Review Summary
	 */

	public Gstr1FlagRespDto loadHsnFlagection(Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String taxPeriodReq = request.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN :gstinList");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND RETURN_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createHSNFlagQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		Gstr1FlagRespDto dto = new Gstr1FlagRespDto();
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> listObjs = q.getResultList();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("HSN Flag For seperating DIGIGst & USER Inputs"
						+ listObjs);
			}

			if (listObjs != null && !listObjs.isEmpty()) {

				listObjs.forEach(obj -> {

					/*
					 * int nil = ((Byte) obj[0]).intValue(); int hsn = ((Byte)
					 * obj[1]).intValue(); if(nil == 0){
					 * dto.setNilUserInput(false); } if(nil == 1){
					 * dto.setNilUserInput(true); } if(hsn == 0){
					 * dto.setHsnUserInput(false); } if(hsn == 1){
					 * dto.setHsnUserInput(true); }
					 */
					if (obj[1] != null) {
						String hsnUser = String.valueOf(obj[1]);
						dto.setIsHsnUserInput(Boolean.parseBoolean(hsnUser));
					} else {
						dto.setIsHsnUserInput(false);
					}
					if (obj[0] != null) {
						String nilUser = String.valueOf(obj[0]);
						dto.setIsNilUserInput(Boolean.parseBoolean(nilUser));
					} else {
						dto.setIsNilUserInput(false);
					}
				});

			} else {
				dto.setIsHsnUserInput(false);
				dto.setIsNilUserInput(false);
			}

		} catch (Exception e) {
			LOGGER.error(
					"Retriving data From GSTN_USER_REQUEST table getting issue #####",
					e);

		}
		return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createHSNFlagQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "select IS_NIL_UI,IS_HSN_UI from GSTN_USER_REQUEST WHERE "
				+ "REQUEST_TYPE = 'SAVE' "
				+ "AND RETURN_TYPE = 'GSTR1' AND IS_DELETE = FALSE AND "
				+ buildQuery + " ORDER BY ID DESC LIMIT 1 ";

		/*
		 * String queryStr =
		 * "select case when IS_NIL_UI = TRUE THEN 'TRUE' ELSE 'FALSE' END AS IS_NIL_UI,"
		 * +
		 * "CASE WHEN IS_HSN_UI = TRUE THEN 'TRUE' ELSE 'FALSE' END AS IS_HSN_UI,ID "
		 * + "FROM GSTN_USER_REQUEST WHERE REQUEST_TYPE = 'SAVE' " +
		 * "AND RETURN_TYPE = 'GSTR1' AND IS_DELETE = FALSE AND " + buildQuery +
		 * " ORDER BY ID DESC LIMIT 1 ";
		 */
		LOGGER.debug(" HSN & NIL Flags From USER Input Table ");
		return queryStr;
	}

	/**
	 * THis Method is Created For Last Updated TimeStamp For Hsn Section
	 * 
	 * @param request
	 * @return
	 */

	public String loadLastUpdatedDateForHsnSection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String lastUpdatedHsnDate = "";
		String taxPeriod = request.getTaxPeriod();

		int taxPeriodReq = GenUtil.convertTaxPeriodToInt(taxPeriod);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createLastHsnQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != 0) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedHsnDate = FOMATTER.format(istDateTimeFromUTC);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			/*
			 * e.printStackTrace(); throw new
			 * AppException("Unexpected error in query execution.", e);
			 */
		}
		return lastUpdatedHsnDate;
	}

	/**
	 * 
	 * @param buildQuery
	 * @return
	 */
	private String createLastHsnQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT HSN_LAST_UPDATED FROM ( "
				+ "SELECT CREATED_ON AS HSN_LAST_UPDATED,ROWNUM_DT FROM ( "
				+ "SELECT SECTION,GSTN_SAVE_REF_ID, CREATED_ON,"
				+ "ROW_NUMBER() OVER(PARTITION BY DERIVED_RET_PERIOD,"
				+ "SUPPLIER_GSTIN,SECTION ORDER BY CREATED_ON DESC) AS ROWNUM_DT "
				+ "FROM GSTR1_GSTN_SAVE_BATCH WHERE IS_DELETE=FALSE AND "
				+ buildQuery
				+ "AND SECTION in ('HSNSUM') AND GSTN_SAVE_REF_ID IS NOT NULL  ) "
				+ "WHERE ROWNUM_DT=1)";

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		return queryStr;
	}

	/**
	 * THis Method is Created For Last Updated TimeStamp For NIL Section
	 * 
	 * @param request
	 * @return
	 */

	public String loadLastUpdatedDateForNilSection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String lastUpdatedNilDate = "";
		String taxPeriod = request.getTaxPeriod();

		int taxPeriodReq = GenUtil.convertTaxPeriodToInt(taxPeriod);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createLastNilQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != 0) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			@SuppressWarnings("unchecked")
			List<Object> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedNilDate = FOMATTER.format(istDateTimeFromUTC);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			/*
			 * e.printStackTrace(); throw new
			 * AppException("Unexpected error in query execution.", e);
			 */
		}
		return lastUpdatedNilDate;
	}

	/**
	 * 
	 * @param buildQuery
	 * @return
	 */
	private String createLastNilQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT NIL_LAST_UPDATED FROM ( "
				+ "SELECT CREATED_ON AS NIL_LAST_UPDATED,ROWNUM_DT FROM ("
				+ "SELECT SECTION,GSTN_SAVE_REF_ID, CREATED_ON,"
				+ "ROW_NUMBER() OVER(PARTITION BY DERIVED_RET_PERIOD,"
				+ "SUPPLIER_GSTIN,SECTION ORDER BY CREATED_ON DESC) AS ROWNUM_DT "
				+ "FROM GSTR1_GSTN_SAVE_BATCH WHERE IS_DELETE=FALSE AND "
				+ buildQuery
				+ "AND SECTION in ('NIL') AND GSTN_SAVE_REF_ID IS NOT NULL  ) "
				+ "WHERE ROWNUM_DT=1)";

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		return queryStr;
	}

	public String getLastUpdatedGstr1SummStatus(
			Annexure1SummaryReqDto request) {

		String lastUpdatedDate = "";
		String taxPeriodReq = request.getTaxPeriod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		try {
			String queryStr = "SELECT TOP 1 CREATED_ON,STATUS "
					+ " FROM GETANX1_BATCH_TABLE WHERE IS_DELETE = FALSE"
					+ " AND GSTIN IN :gstinList AND "
					+ " RETURN_PERIOD =:taxPeriodReq AND API_SECTION = 'GSTR1'"
					+ " AND GET_TYPE = 'GSTR1_GETSUM' ORDER BY CREATED_ON DESC";
			Query q = entityManager.createNativeQuery(queryStr);
			q.setParameter("gstinList", gstinList);
			q.setParameter("taxPeriodReq", taxPeriodReq);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			
			Object[] obj = list.get(0);
			if (list != null && !list.isEmpty() && list.get(0) != null) {
				LocalDateTime localdateTime = ((Timestamp) obj[0])
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);
				}
				String status = (String) obj[1];
				if(status != null){
					lastUpdatedDate = lastUpdatedDate + " -" + status;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return lastUpdatedDate;
	}

}
