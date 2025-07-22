package com.ey.advisory.app.services.daos.gstr6;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataDto;
import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataEntity;
import com.ey.advisory.app.data.entities.client.Gstr6UserInputEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeCredDistDataRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6StatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6UserInputRepo;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CalculateTurnOverRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeGstr1SummaryRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeGstr1SummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeTimeStampResponseDto;
import com.ey.advisory.app.docs.service.gstr6.Gstr6DeterminationService;
import com.ey.advisory.app.gstr1.einv.Gstr6CredDistDaoImpl;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstnImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("Gstr6CalculateTurnOverGstnServiceImpl")
public class Gstr6CalculateTurnOverGstnServiceImpl
		implements Gstr6CalculateTurnOverGstnService {

	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	private Gstr1SummaryAtGstnImpl gstr1SummaryAtGstn;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr6StatusRepository")
	private Gstr6StatusRepository gstr6StatusRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("Gstr6UserInputRepo")
	private Gstr6UserInputRepo gstr6UserInputRepo;

	@Autowired
	@Qualifier("Gstr6DeterminationServiceImpl")
	private Gstr6DeterminationService gstr6DeterminationService;

	@Autowired
	@Qualifier("Gstr6ComputeCredDistDataRepository")
	private Gstr6ComputeCredDistDataRepository gstr6CompCredDistDataRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private Gstr6ComputeCredDistDataRepository gstr6CompCredDistDataRepository;

	@Autowired
	@Qualifier("Gstr6CredDistDaoImpl")
	private Gstr6CredDistDaoImpl requestStatusDao;

	@Override
	public List<String> getListOfTaxperoids(
			Gstr6CalculateTurnOverRequestDto criteria) {
		String toTaxperoid = criteria.getTaxPeriodTo(); // 112020
		String fromTaxperiod = criteria.getTaxPeriodFrom();// 022021
		int toYear = Integer.parseInt(toTaxperoid.subSequence(2, 6).toString());
		int fromYear = Integer
				.parseInt(fromTaxperiod.subSequence(2, 6).toString());
		int toMonth = Integer
				.parseInt(toTaxperoid.subSequence(0, 2).toString());
		int fromMonth = Integer
				.parseInt(fromTaxperiod.subSequence(0, 2).toString());

		/*
		 * String taxperoid = criteria.getTaxPeriod(); int taxYear =
		 * Integer.parseInt(taxperoid.subSequence(2, 6).toString()); int
		 * taxMonth = Integer.parseInt(taxperoid.subSequence(0, 2).toString());
		 * if(((taxYear == fromYear && fromMonth <= taxMonth) || (fromYear <
		 * taxYear)) && ((taxYear == toYear && toMonth <= taxMonth) || (toYear <
		 * taxYear))){
		 * 
		 * }
		 */

		ArrayList<String> result = new ArrayList<String>();

		if ((fromYear == toYear && fromMonth <= toMonth)
				|| (fromYear < toYear)) {
			int yearDiff = toYear - fromYear;
			int monthsDiff = toMonth - fromMonth;
			int totalMonths = yearDiff * 12 + monthsDiff;
			while (totalMonths >= 0) {
				if (fromMonth < 10)
					result.add("0" + fromMonth + "" + fromYear);
				else
					result.add(fromMonth + "" + fromYear);

				if (fromMonth == 12) {
					fromYear++;
					fromMonth = 1;
				} else {
					fromMonth++;
				}
				totalMonths--;
			}
		}

		return result;
	}

	@Override
	public List<Pair<String, String>> getListOfCombinationPairs(
			List<String> gstins, List<String> taxperoidList) {
		List<Pair<String, String>> listOfPairs = new ArrayList<>();
		if (gstins != null && !gstins.isEmpty() && taxperoidList != null
				&& !taxperoidList.isEmpty()) {
			for (int i = 0; i < gstins.size(); i++) {
				for (int j = 0; j < taxperoidList.size(); j++) {
					listOfPairs.add(
							new Pair<>(gstins.get(i), taxperoidList.get(j)));
				}
			}
		}
		return listOfPairs;
	}
	
	@Override
	public void getGstr6CalTurnOverGstnData(
			List<Pair<String, String>> listOfPairs, String groupCode,
			List<String> gstins, Gstr6CalculateTurnOverRequestDto criteria,
			List<String> isdGstins) {
		try {
			List<String> gstr1Status = new ArrayList<String>();
			String status = "";
			for (Pair<String, String> pair : listOfPairs) {
				Gstr1GetInvoicesReqDto dataDto = new Gstr1GetInvoicesReqDto();
				dataDto.setGstin(pair.getValue0());
				dataDto.setReturnPeriod(pair.getValue1());
				status = gstr1SummaryAtGstn.getGstr1SummaryCall(dataDto, groupCode);
				gstr1Status.add(status);
			}

			int dertaxperiodFrom = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriodFrom());
			int dertaxperiodTo = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriodTo());
			// List<String> isdGstins = criteria.getIsdGstin();

			// Binding List of GStins to String
			String endResultGstins = "null";
			if (gstins != null && !gstins.isEmpty()) {
				StringBuilder st = new StringBuilder();
				for (String gstin : gstins) {
					st.append(gstin + "#");
				}
				endResultGstins = st.toString();
				endResultGstins = "'" + endResultGstins
						.subSequence(0, endResultGstins.length() - 1).toString()
						+ "'";
			}
			// Binding List of ISDGStins to String
			String endResultIsdGstins = "null";
			if (isdGstins != null && !isdGstins.isEmpty()) {
				StringBuilder st = new StringBuilder();
				for (String isdGstin : isdGstins) {
					st.append(isdGstin + "#");
				}
				endResultIsdGstins = st.toString();
				endResultIsdGstins = "'" + endResultIsdGstins
						.subSequence(0, endResultIsdGstins.length() - 1)
						.toString() + "'";
			}
			// Binding List of TableType to String
			String tableType = "null";
			if (criteria.getTableType() != null
					&& !criteria.getTableType().isEmpty()) {
				StringBuilder st1 = new StringBuilder();
				for (String type : criteria.getTableType()) {
					st1.append(type + "#");
				}
				tableType = st1.toString();
				tableType = "'" + tableType
						.subSequence(0, tableType.length() - 1).toString()
						+ "'";
			}

			// Procedure Call For TrunOver Computation for GSTN
			status = docRepository.gstr6CalTurnOverGstnProcCall(endResultGstins,
					dertaxperiodFrom, dertaxperiodTo, criteria.getEntityId(),
					tableType, criteria.getTaxPeriod(), endResultIsdGstins,criteria.getBatchId());
			if(status.equalsIgnoreCase(APIConstants.SUCCESS)){
				status = checkStatus(gstr1Status);
				updateGstnStatus(criteria, status);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 Summary Status List {} Gstr1 Summary Final Status : {} ", gstr1Status, status);
				}
			}
			

		} catch (Exception ex) {
			updateGstnStatus(criteria, "FAILED");
			String msg = String.format(
					"Exception occured while Computing GSTR6 Turnover for "
							+ "Tax period %s , Entity Id %s , GSTNs %s and ISDGSTN %s .",
					criteria.getTaxPeriod(), criteria.getEntityId(),
					criteria.getGstin(), criteria.getIsdGstin());

			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	@Override
	public void getGstr6CalTurnOverDigiGstData(
			Gstr6CalculateTurnOverRequestDto criteria) {
		// Binding List of GStins to String
		List<String> gstins = criteria.getGstin();
		String endResultGstins = "null";
		if (gstins != null && !gstins.isEmpty()) {
			StringBuilder st = new StringBuilder();
			for (String gstin : gstins) {
				st.append(gstin + "#");
			}
			endResultGstins = st.toString();
			endResultGstins = "'" + endResultGstins
					.subSequence(0, endResultGstins.length() - 1).toString()
					+ "'";
		}
		// Binding List of TableType to String
		List<String> types = criteria.getTableType();
		String tableType = "null";
		if (types != null && !types.isEmpty()) {
			StringBuilder st1 = new StringBuilder();
			for (String type : types) {
				st1.append(type + "#");
			}
			tableType = st1.toString();
			tableType = "'" + tableType.subSequence(0, tableType.length() - 1)
					.toString() + "'";
		}

		// Binding List of ISDGStins to String
		String endResultIsdGstins = "null";
		List<String> isdGstins = criteria.getIsdGstin();
		if (isdGstins != null && !isdGstins.isEmpty()) {
			StringBuilder st = new StringBuilder();
			for (String isdGstin : isdGstins) {
				st.append(isdGstin + "#");
			}
			endResultIsdGstins = st.toString();
			endResultIsdGstins = "'" + endResultIsdGstins
					.subSequence(0, endResultIsdGstins.length() - 1).toString()
					+ "'";
		}

		docRepository.gstr6CalTurnOverDigiGstProcCall(endResultIsdGstins,
				GenUtil.convertTaxPeriodToInt(criteria.getTaxPeriodFrom()),
				GenUtil.convertTaxPeriodToInt(criteria.getTaxPeriodTo()),
				criteria.getEntityId(), tableType, criteria.getTaxPeriod(),
				endResultGstins);
	}

	@Override
	public void getGstr6ComputeTurnOverUserInputData(
			Gstr6CalculateTurnOverRequestDto criteria) {
		// Binding List of GStins to String
		List<String> gstins = criteria.getGstin();
		Long entityId = criteria.getEntityId();
		List<Long> entityIds = new ArrayList<>();
		entityIds.add(entityId);
		String endResultGstins = "null";
		if (gstins != null && !gstins.isEmpty()) {
			StringBuilder st = new StringBuilder();
			for (String gstin : gstins) {
				st.append(gstin + "#");
			}
			endResultGstins = st.toString();
			endResultGstins = "'" + endResultGstins
					.subSequence(0, endResultGstins.length() - 1).toString()
					+ "'";
		}
		List<Integer> idList = criteria.getId();

		List<String> unSavedGstinList = criteria.getUnSavedGstins();
		List<String> unSavedGstinListFrom = new ArrayList<>();
		List<String> unSavedGstinListTo = new ArrayList<>();

		if (unSavedGstinList == null) {
			// Get gstins by Entityid
			List<String> isdGstins = gstnDetailRepository
					.findgstinByEntityIdWithOutISD(entityIds);

			// fromTaxperiod gstin
			for (String gstin : gstins) {
				List<String> fromTaxperiodGstins = gstr6UserInputRepo
						.findByGstinsAndTaxPeriod(gstin,
								criteria.getFromPeriod());
				if (isdGstins != null && !isdGstins.isEmpty()) {
					for (String isdGstin : isdGstins) {
						if (!fromTaxperiodGstins.contains(isdGstin)) {
							unSavedGstinListFrom.add(isdGstin);
						}
					}
				}
			}
			// toTaxperiod gstin
			for (String gstin : gstins) {
				List<String> toTaxperiodGstins = gstr6UserInputRepo
						.findByGstinsAndTaxPeriod(gstin,
								criteria.getTaxPeriod());
				if (isdGstins != null && !isdGstins.isEmpty()) {
					for (String isdGstin : isdGstins) {
						if (!toTaxperiodGstins.contains(isdGstin)) {
							unSavedGstinListTo.add(isdGstin);
						}
					}
				}
			}
		}

		if (unSavedGstinList != null && !unSavedGstinList.isEmpty()) {
			for (String gstinUnsaved : unSavedGstinList) {
				Gstr6UserInputEntity dto = new Gstr6UserInputEntity();
				dto.setGstin(gstinUnsaved);
				dto.setCurrentRetPer(criteria.getTaxPeriod());
				dto.setGetGstr1Status("NOT INITIATED");
				dto.setStateName(gstinUnsaved.substring(0, 2));
				dto.setIsdGstin(endResultGstins.substring(1, 16));
				dto.setComputeDigiVal(BigDecimal.ZERO);
				dto.setComputeGstnVal(BigDecimal.ZERO);
				dto.setUserInput(BigDecimal.ZERO);
				if (dto != null) {
					gstr6UserInputRepo.save(dto);
				}
				Integer id = gstr6UserInputRepo.findByGstinAndTaxPeriod(
						gstinUnsaved, criteria.getTaxPeriod());
				idList.add(id);
			}
		}

		if (unSavedGstinListFrom != null && !unSavedGstinListFrom.isEmpty()) {
			for (String gstinUnsaved : unSavedGstinListFrom) {
				Gstr6UserInputEntity dto = new Gstr6UserInputEntity();
				dto.setGstin(gstinUnsaved);
				dto.setCurrentRetPer(criteria.getFromPeriod());
				dto.setGetGstr1Status("NOT INITIATED");
				dto.setStateName(gstinUnsaved.substring(0, 2));
				dto.setIsdGstin(endResultGstins.substring(1, 16));
				dto.setComputeDigiVal(BigDecimal.ZERO);
				dto.setComputeGstnVal(BigDecimal.ZERO);
				dto.setUserInput(BigDecimal.ZERO);
				Integer id = gstr6UserInputRepo.findByGstinAndTaxPeriod(
						gstinUnsaved, criteria.getFromPeriod());
				if (dto != null && id == null) {
					gstr6UserInputRepo.save(dto);
				}
			}
		}

		if (unSavedGstinListTo != null && !unSavedGstinListTo.isEmpty()) {
			for (String gstinUnsaved : unSavedGstinListTo) {
				Gstr6UserInputEntity dto = new Gstr6UserInputEntity();
				dto.setGstin(gstinUnsaved);
				dto.setCurrentRetPer(criteria.getTaxPeriod());
				dto.setGetGstr1Status("NOT INITIATED");
				dto.setStateName(gstinUnsaved.substring(0, 2));
				dto.setIsdGstin(endResultGstins.substring(1, 16));
				dto.setComputeDigiVal(BigDecimal.ZERO);
				dto.setComputeGstnVal(BigDecimal.ZERO);
				dto.setUserInput(BigDecimal.ZERO);
				Integer id = gstr6UserInputRepo.findByGstinAndTaxPeriod(
						gstinUnsaved, criteria.getTaxPeriod());
				if (dto != null && id == null) {
					gstr6UserInputRepo.save(dto);
				}
			}
		}
		String ids = "0";
		if (idList != null && !idList.isEmpty()) {
			List<String> isdGstinsInfo = gstnDetailRepository
					.findgstinByEntityIdWithOutISD(entityIds);
			StringBuilder st = new StringBuilder();
			for (Integer id : idList) {
				if (id != null) {
					Long idLong = new Long(id);
					String gstinById = gstr6UserInputRepo
							.findByGstinsAndById(idLong);
					if (isdGstinsInfo != null && !isdGstinsInfo.isEmpty()) {
						if (isdGstinsInfo.contains(gstinById)) {
							st.append(id + "#");
						}
					}
				} else {
					// create id
				}
			}
			ids = st.toString();
			ids = "'" + ids.subSequence(0, ids.length() - 1).toString() + "'";
		}

		docRepository.gstr6ComputeTurnOverUserInputProcCall(endResultGstins,
				criteria.getEntityId(), criteria.getTaxPeriod(),
				criteria.getType(), ids, criteria.getFromPeriod());

	}

	private String createAnswerQueryString(String buildQuery) {
		return "SELECT ANSWER FROM ENTITY_CONFG_PRMTR WHERE CONFG_QUESTION_ID = "
				+ " (SELECT ID FROM CONFG_QUESTION WHERE QUESTION_CODE = 'I31') "
				+ " AND IS_DELETE = FALSE " + buildQuery;
	}

	@Override
	public Gstr6ComputeTimeStampResponseDto getGstr6ComputeTimeStamp(
			Gstr6CalculateTurnOverRequestDto criteria) {
		List<String> gstins = criteria.getGstin();
		String taxPeriod = criteria.getTaxPeriod();

		StringBuilder build = new StringBuilder();
		if (gstins != null && !gstins.isEmpty()) {
			build.append(" ISD_GSTIN IN :gstins ");
		}
		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			build.append(" AND CURRENT_RET_PERIOD  = :taxPeriod ");
			//build.append(" AND IS_DELETE = false ");
		}

		String buildQuery = build.toString();
		String queryStr = createTimeStampQueryString(buildQuery);

		Query q = entityManager.createNativeQuery(queryStr);
		if (gstins != null && !gstins.isEmpty()) {
			q.setParameter("gstins", gstins);
		}
		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			q.setParameter("taxPeriod", taxPeriod);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> timeStampList = q.getResultList();

		Gstr6ComputeTimeStampResponseDto timeStampDto = new Gstr6ComputeTimeStampResponseDto();
		if (timeStampList != null && !timeStampList.isEmpty()) {
			for (Object list[] : timeStampList) {
				if (list[2] != null && !list[2].toString().isEmpty()) {
					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");
					if (list[2].toString().equalsIgnoreCase("DIGIGST")) {
						if (list[0] != null && !list[0].toString().isEmpty()) {
							LocalDateTime localdateTimeDigiGst = ((Timestamp) list[0])
									.toLocalDateTime();
							if (localdateTimeDigiGst != null) {
								LocalDateTime istDateTimeFromUTC = EYDateUtil
										.toISTDateTimeFromUTC(
												localdateTimeDigiGst);
								String lastUpdatedDigiGstDate = FOMATTER
										.format(istDateTimeFromUTC);
								timeStampDto.setLastUpdatedDigiGstTimeStamp(
										lastUpdatedDigiGstDate);
							}
						} else {
							timeStampDto.setLastUpdatedDigiGstTimeStamp("");
						}
						if (list[1] != null && !list[1].toString().isEmpty()) {
							timeStampDto.setDigiGstStatus((String) list[1]);
						} else {
							timeStampDto.setDigiGstStatus("");
						}
					} else if (list[2].toString().equalsIgnoreCase("GSTIN")) {
						if (list[0] != null && !list[0].toString().isEmpty()) {
							LocalDateTime localdateTimeGstn = ((Timestamp) list[0])
									.toLocalDateTime();
							if (localdateTimeGstn != null) {
								LocalDateTime istDateTimeFromUTC = EYDateUtil
										.toISTDateTimeFromUTC(
												localdateTimeGstn);
								String lastUpdatedGstnDate = FOMATTER
										.format(istDateTimeFromUTC);
								timeStampDto.setLastUpdatedGstnTimeStamp(
										lastUpdatedGstnDate);
							}
						} else {
							timeStampDto.setLastUpdatedGstnTimeStamp("");
						}
						if (list[1] != null && !list[1].toString().isEmpty()) {
							timeStampDto.setGstnStatus((String) list[1]);
						} else {
							timeStampDto.setGstnStatus("");
						}

					} else if (list[2].toString().equalsIgnoreCase("CREDIT")) {
						if (list[0] != null && !list[0].toString().isEmpty()) {
							LocalDateTime localdateTimeCredit = ((Timestamp) list[0])
									.toLocalDateTime();
							if (localdateTimeCredit != null) {
								LocalDateTime istDateTimeFromUTC = EYDateUtil
										.toISTDateTimeFromUTC(
												localdateTimeCredit);
								String lastUpdatedCreditDate = FOMATTER
										.format(istDateTimeFromUTC);
								timeStampDto.setLastUpdatedCreditTimeStamp(
										lastUpdatedCreditDate);
							}
						} else {
							timeStampDto.setLastUpdatedCreditTimeStamp("");
						}
						if (list[1] != null && !list[1].toString().isEmpty()) {
							timeStampDto.setCreditStatus((String) list[1]);
						} else {
							timeStampDto.setCreditStatus("");
						}

					} else {
						return timeStampDto;
					}
				}
			}
		}
		return timeStampDto;
	}

	private String createTimeStampQueryString(String buildQuery) {
		return "SELECT MAX(DIGI_GST_TIMESTAMP) AS TURNOVER_TIMESTAMP,"
				+ "MAX(DIGI_GST_STATUS) as STATUS,'DIGIGST' AS T_TYPE "
				+ " FROM GSTR6_STATUS WHERE  " + buildQuery + " UNION ALL "
				+ "SELECT MAX(GSTIN_TIMESTAMP) AS TURNOVER_TIMESTAMP,"
				+ "MAX(GSTIN_STATUS) as STATUS,'GSTIN' AS T_TYPE "
				+ "FROM GSTR6_STATUS WHERE  " + buildQuery + " AND IS_DELETE = false "+" UNION ALL "
				+ "SELECT MAX(CREDIT_TIMESTAMP) AS TURNOVER_TIMESTAMP,"
				+ "MAX(CREDIT_STATUS) as STATUS,'CREDIT' AS T_TYPE "
				+ " FROM GSTR6_STATUS WHERE  " + buildQuery;

		/*
		 * return
		 * "SELECT ISD_GSTIN,MAX(DIGI_GST_TIMESTAMP) AS TURNOVER_DIGI_GST_TIMESTAMP,"
		 * + "MAX(GSTIN_TIMESTAMP) AS TURNOVER_GSTIN_TIMESTAMP," +
		 * "MAX(CREDIT_TIMESTAMP) AS CREDIT_TIMESTAMP,MAX(DIGI_GST_STATUS) as DIGI_GST_STATUS,"
		 * +
		 * "MAX(GSTIN_STATUS) as GSTIN_STATUS,MAX(CREDIT_STATUS) as CREDIT_STATUS "
		 * + " FROM GSTR6_STATUS WHERE IS_DELETE = FALSE " + buildQuery +
		 * " GROUP BY ISD_GSTIN ";
		 */
	}

	@Override
	public List<Gstr6ComputeGstr1SummaryResponseDto> getGstr1SummaryStatus(
			Gstr6ComputeGstr1SummaryRequestDto criteria) {
		String gstin = criteria.getGstin();
		int dertaxperiodFrom = 0;
		int dertaxperiodTo = 0;
		if (criteria.getPeriodFrom() != null
				&& !criteria.getPeriodFrom().isEmpty()) {
			dertaxperiodFrom = GenUtil
					.convertTaxPeriodToInt(criteria.getPeriodFrom());
		}
		if (criteria.getPeriodTo() != null
				&& !criteria.getPeriodTo().isEmpty()) {
			dertaxperiodTo = GenUtil
					.convertTaxPeriodToInt(criteria.getPeriodTo());
		}
		List<Object[]> list = batchRepo.getGstr1StatusAndReturnPeriod(gstin,
				dertaxperiodFrom, dertaxperiodTo);
		List<Gstr6ComputeGstr1SummaryResponseDto> summaryData = new ArrayList<>();
		for (Object[] data : list) {
			Gstr6ComputeGstr1SummaryResponseDto responseDto = new Gstr6ComputeGstr1SummaryResponseDto();
			responseDto.setGstin(gstin);
			responseDto.setTaxPeriod((String) data[1]);
			responseDto.setStatus((String) data[0]);

			if (data[2] != null && !data[2].toString().isEmpty()) {
				// Timestamp timestamp = Timestamp.valueOf(data[2].toString());
				// LocalDateTime localdateTime = ((Timestamp)
				// timestamp).toLocalDateTime();
				// if (localdateTime != null) {
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC((LocalDateTime) data[2]);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyy-MM-dd HH:mm:ss");
				String lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);
				responseDto.setTimeStamp(lastUpdatedDate);
				// }
			} else {
				responseDto.setTimeStamp("");
			}
			summaryData.add(responseDto);
		}
		return summaryData;
	}

	public String getGstr1Status(String gstin, int dertaxperiodFrom,
			int dertaxperiodTo) {
		List<String> statusList = new ArrayList<>();
		Map<String, List<String>> gstinsMap = Maps.newHashMap();

		statusList = batchRepo.getStatusforGstinAndTaxperiod(gstin,
				dertaxperiodFrom, dertaxperiodTo);
		gstinsMap.put(gstin, statusList);

		String finalStatus = "NOT_INITIATED";
		List<String> statusList1 = gstinsMap.get(gstin);
		List<String> unqueSatusList = statusList1.stream()
				.filter(status1 -> status1 != null)
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(unqueSatusList)) {
			if (unqueSatusList.size() == 1) {
				if (unqueSatusList.contains("INPROGRESS")) {
					finalStatus = "INPROGRESS";
				} else if (unqueSatusList.contains("INITIATED")) {
					finalStatus = "INITIATED";
				} else if (unqueSatusList.contains("SUCCESS")
						|| unqueSatusList.contains("SUCCESS_WITH_NO_DATA")) {
					finalStatus = "SUCCESS";
				} else if (unqueSatusList.contains("FAILED")) {
					finalStatus = "FAILED";
				} else if (!unqueSatusList.contains("FAILED")
						&& unqueSatusList.contains("SUCCESS")) {
					finalStatus = "PARTIALLY_SUCCESS";
				}
			} else {
				finalStatus = "PARTIALLY_SUCCESS";
			}
		}
		return finalStatus;
	}

	@Override
	public List<String> getRegGstins(Long entityId) {
		List<String> Gstins = gstnDetailRepository
				.findRegGstinByEntityId(entityId);
		return Gstins;
	}

	@Override
	public void getGstr6CalTurnOverDigiGstProcessedData(
			Gstr6CalculateTurnOverRequestDto criteria, Long entityId) {
		List<String> gstins = getRegGstins(entityId);

		// Binding List of GStins to String
		String endResultGstins = "null";
		if (gstins != null && !gstins.isEmpty()) {
			StringBuilder st = new StringBuilder();
			for (String gstin : gstins) {
				st.append(gstin + "#");
			}
			endResultGstins = st.toString();
			endResultGstins = "'" + endResultGstins
					.subSequence(0, endResultGstins.length() - 1).toString()
					+ "'";
		}
		// Binding List of TableType to String
		List<String> types = criteria.getTableType();
		String tableType = "null";
		if (types != null && !types.isEmpty()) {
			StringBuilder st1 = new StringBuilder();
			for (String type : types) {
				st1.append(type + "#");
			}
			tableType = st1.toString();
			tableType = "'" + tableType.subSequence(0, tableType.length() - 1)
					.toString() + "'";
		}

		// Binding List of ISDGStins to String
		String endResultIsdGstins = "null";
		List<String> isdGstins = criteria.getGstin();
		if (isdGstins != null && !isdGstins.isEmpty()) {
			StringBuilder st = new StringBuilder();
			for (String isdGstin : isdGstins) {
				st.append(isdGstin + "#");
			}
			endResultIsdGstins = st.toString();
			endResultIsdGstins = "'" + endResultIsdGstins
					.subSequence(0, endResultIsdGstins.length() - 1).toString()
					+ "'";
		}

		docRepository.gstr6CalTurnOverDigiGstProcCall(endResultIsdGstins,
				GenUtil.convertTaxPeriodToInt(criteria.getTaxPeriodFrom()),
				GenUtil.convertTaxPeriodToInt(criteria.getTaxPeriodTo()),
				criteria.getEntityId(), tableType, criteria.getTaxPeriod(),
				endResultGstins);

	}

	@Override
	public String gstr6ComputeCredDistData(
			Gstr6CalculateTurnOverRequestDto criteria) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside InitiateReconserviceimpl" + " method";
			LOGGER.debug(msg);
		}
		try {

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			LocalDateTime now = LocalDateTime.now();
			Integer derTaxPeriod;
			Gstr6ComputeCredDistDataEntity entity = new Gstr6ComputeCredDistDataEntity();
			derTaxPeriod = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriod());
			Set<String> gstinSet = new HashSet<>(criteria.getGstin());
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstinSet)));
			entity.setEntityId(criteria.getEntityId());
			entity.setNoOfGstin((long) gstinSet.size());
			entity.setInitiatedOn(now);
			entity.setInitiatedBy(userName);
			entity.setTaxPeriod(criteria.getTaxPeriod());
			entity.setDerivedTaxPeriod(derTaxPeriod);
			entity.setStatus(ReconStatusConstants.COMPUTE_INITIATED);
			gstr6CompCredDistDataRepo.save(entity);
			Long requestId = entity.getRequestId();
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("requestId", requestId);
			// gstr6CompCredDistDataProcessorTesting.execute(requestId);
			// --need to uncomment this code
			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR6_COMP_CRED_DIST, jsonParams.toString(),
					userName, 1L, null, null);

		} catch (Exception ex) {
			String msg = "Exception occured in Gstr1-Pr VS Sub InitiatRecon";
			LOGGER.error(msg, ex);
			return "failure";
		}

		return "Success";

	}

	@Override
	public String getGstr6ComputeCreditDistributionData(
			Gstr6CalculateTurnOverRequestDto criteria, Long requestId) {

		gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
				ReconStatusConstants.COMPUTE_INPROGRESS, null,
				LocalDateTime.now(), requestId, null);
		String status = null;

		try {
			// Binding List of GStins to String
			List<String> gstins = criteria.getGstin();
			Long entityId = criteria.getEntityId();
			String endResultGstins = "null";
			if (gstins != null && !gstins.isEmpty()) {
				StringBuilder st = new StringBuilder();
				for (String gstin : gstins) {
					st.append(gstin + "#");
				}
				endResultGstins = st.toString();
				endResultGstins = "'" + endResultGstins
						.subSequence(0, endResultGstins.length() - 1).toString()
						+ "'";
			}
			// Binding List of TableType to String
			// List<String> types = criteria.getTableType();
			String tableType = "'INV#CR#DR'";
			/*
			 * if (types != null && !types.isEmpty()) { StringBuilder st1 = new
			 * StringBuilder(); for (String type : types) { st1.append(type +
			 * "#"); } tableType = st1.toString(); tableType = "'" +
			 * tableType.subSequence(0, tableType.length() - 1).toString() +
			 * "'"; }
			 */

			StringBuilder build = new StringBuilder();

			if (entityId != null) {
				build.append(" AND ENTITY_ID  = :entityId ");
			}

			String buildQuery = build.toString();
			String queryStr = createAnswerQueryString(buildQuery);

			Query q = entityManager.createNativeQuery(queryStr);
			if (entityId != null) {
				q.setParameter("entityId", entityId);
			}
			@SuppressWarnings("unchecked")
			Object Answer = q.getSingleResult();

			docRepository.gstr6ComputeCreditDistributionProcCall(
					endResultGstins, criteria.getTaxPeriod(), tableType,
					criteria.getEntityId(), Answer.toString());
			status = "Success";
			return status;
		} catch (Exception e) {

			gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
					ReconStatusConstants.COMPUTE_FAILED, null,
					LocalDateTime.now(), requestId, null);
			status = "Failed";
			return status;

		}

	}

	private String convertToQueryFormat(Set<String> gstinset) {

		List<String> list = new ArrayList<String>();
		list.addAll(gstinset);

		String queryData = null;
		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;
	}

	@Override
	public List<Gstr6ComputeCredDistDataDto> getGstr6reqIdWiseScreenData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin getGstr6reqIdWiseScreenData"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}

		List<Gstr6ComputeCredDistDataDto> response = requestStatusDao
				.getGstr6CredReqIdWiseStatus(reqDto, userName);

		// response.sort(Comparator.comparing(Gstr6ComputeCredDistDataDto::getRequestId));
		response.sort(
				Comparator.comparing(Gstr6ComputeCredDistDataDto::getRequestId)
						.reversed());

		return response;
	}

	@Override
	public void updateGstnStatus(Gstr6CalculateTurnOverRequestDto dto,
			String status) {
		Long id = dto.getId().get(0).longValue();
		int count = 0;
		/*
		 * count = gstr6StatusRepository
		 * .UpdateStatusAndTimeStampForComputeGstn(status, LocalDateTime.now(),
		 * dto.getIsdGstin(), dto.getTaxPeriod());
		 */
		count = gstr6StatusRepository
				.updateStatusAndTimeStampForComputeGstnById(id,
						LocalDateTime.now(), status);
		if (count > 0) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Record is updated with Id {} Status {} ", id,
						status);
			}
		}

	}
	
	@Override
	public String checkStatus(List<String> list) {
        boolean allFailed = true;
        boolean allSuccess = true;
        for (String status : list) {
            if (!status.equals("FAILED")) {
                allFailed = false;
            }
            if (!(status.contentEquals("SUCCESS") ||
            		status.contentEquals("SUCCESS_WITH_NO_DATA"))) {
                allSuccess = false;
            }
        }

        if (allFailed) {
            return "FAILED";
        } else if (allSuccess) {
            return "SUCCESS";
        } else {
            return "PARTIALLY SUCCESS";
        }
    }

	
}
