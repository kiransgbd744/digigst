package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeConfigStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeDigiConfigStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9SaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeConfigStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeDigiConfigStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9SummaryDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Service("Gstr9SummaryServiceImpl")
@Slf4j
public class Gstr9SummaryServiceImpl implements Gstr9SummaryService {

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	Gstr9GetSummaryRepository getrepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr9AutoCalculateRepository")
	private Gstr9AutoCalculateRepository gstr9AutoCalcRepo;

	@Autowired
	@Qualifier("Gstr9ComputeDigiConfigStatusRepository")
	private Gstr9ComputeDigiConfigStatusRepository gstr9DigiComputeRepo;

	@Autowired
	@Qualifier("Gstr9ComputeConfigStatusRepository")
	private Gstr9ComputeConfigStatusRepository gstr9ConfigStatus;

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	@Autowired
	EntityService entityService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	static final String INITIATED = "INITIATED";

	static final String NOT_INITIATED = "NOT INITIATED";

	static final String PROCESSED = "PROCESSED";

	static final String PROCESSED_WITH_ERROR = "PROCESSED WITH ERROR";

	static final String SAVE_ERROR = "SAVE ERROR";

	static final String SAVE_FAILED = "FAILED";

	static final String FILED = "FILED";

	@SuppressWarnings({ "unchecked", "null" })
	public List<Gstr9SummaryDto> listGstr9ummary(List<String> gstnsLists,
			String fy) {

		HashMap<String, GstrReturnStatusEntity> gstrReturnStatusMap = new HashMap<String, GstrReturnStatusEntity>();

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("In Gstr9SummaryServiceImpl listGstr9ummary() ,"
							+ "Fetching state names for gstins %s", gstnsLists);
			LOGGER.debug(msg);
		}
		String[] financialYears = fy.split("-");
		String updatedFy = financialYears[0]
				+ financialYears[1].substring(2, 4);
		Map<String, String> stateNamesMap = entityService
				.getStateNames(gstnsLists);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"In GL Recon Summary Dashboard,"
							+ "state names for gstins %s are %s",
					gstnsLists, stateNamesMap);
			LOGGER.debug(msg);
		}

		Map<String, String> authMap = authTokenService
				.getAuthTokenStatusForGstins(gstnsLists);

		String returnPeriod = null;

		if (fy != null) {
			String[] arr = fy.split("-", 0);
			String month = "03";
			returnPeriod = month + arr[1];
		}

		Integer getConvertedFy = convertFyFromString(fy);

		List<GetAnx1BatchEntity> listAutoCalc = batchRepo.findActiveStatus(
				gstnsLists, GSTR9Constants.GSTR9.toUpperCase(),
				GSTR9Constants.GSTR9_AUTOCALC,
				GenUtil.getFinancialPeriodFromFY(fy));

		Map<String, Pair<String, LocalDateTime>> autoCalMap = new HashMap<>();

		listAutoCalc.forEach(e -> {
			autoCalMap.put(e.getSgstin(), new Pair<String, LocalDateTime>(
					e.getStatus(), e.getCreatedOn()));

		});

		List<Gstr9ComputeDigiConfigStatusEntity> listDigiComputeCalc = gstr9DigiComputeRepo
				.findByGstinInAndFyAndIsDeleteFalse(gstnsLists,
						Integer.parseInt(updatedFy));

		Map<String, Pair<String, LocalDateTime>> digiComputeMap = new HashMap<>();

		listDigiComputeCalc.forEach(e -> {
			digiComputeMap.put(e.getGstin(), new Pair<String, LocalDateTime>(
					e.getStatus(), e.getCreatedOn()));
		});

		List<Gstr9SaveStatusEntity> saveGstinList = gstr9SaveStatusRepository
				.findByGstinInAndTaxPeriod(gstnsLists, returnPeriod);

		Map<String, Pair<String, LocalDateTime>> saveGstinMap = new HashMap<>();

		saveGstinList.forEach(e -> {
			saveGstinMap.put(e.getGstin(), new Pair<String, LocalDateTime>(
					e.getStatus(), e.getCreatedOn()));

		});

		StringBuilder buildQuery = new StringBuilder();

		buildQuery.append(" AND GSTIN IN :gstinList");

		buildQuery.append(" AND RET_PERIOD = :returnPeriod");

		String query = createQueryTotalTurnOver(buildQuery.toString());

		Query queryForTurnOver = entityManager.createNativeQuery(query);

		queryForTurnOver.setParameter("gstinList", gstnsLists);

		queryForTurnOver.setParameter("returnPeriod", returnPeriod);

		List<Object[]> listTotalTurnOver = queryForTurnOver.getResultList();

		// Convert List to Dto in TotalTurnOver

		List<Gstr9SummaryDto> listProcessed = listTotalTurnOver.stream()
				.map(o -> convertToDtotalTurnOver(o))
				.collect(Collectors.toCollection(ArrayList::new));

		String query1 = createQueryNetITCAvl(buildQuery.toString());

		Query queryForNetItcAvl = entityManager.createNativeQuery(query1);

		queryForNetItcAvl.setParameter("gstinList", gstnsLists);

		queryForNetItcAvl.setParameter("returnPeriod", returnPeriod);

		List<Object[]> lisNetItcAvl = queryForNetItcAvl.getResultList();

		// Convert List to Dto in Net ITC Available

		List<Gstr9SummaryDto> listNetItcAvl = lisNetItcAvl.stream()
				.map(o -> convertToDtoNetItcAvl(o))
				.collect(Collectors.toCollection(ArrayList::new));

		StringBuilder lapsedItcbuildQuery = new StringBuilder();

		lapsedItcbuildQuery.append(" AND GSTIN IN :gstinList");

		lapsedItcbuildQuery.append(" AND FY = :fy");

		String query2 = createLapsedItc(lapsedItcbuildQuery.toString());

		Query lapsedItc = entityManager.createNativeQuery(query2);

		lapsedItc.setParameter("gstinList", gstnsLists);

		lapsedItc.setParameter("fy", fy);

		List<Object[]> listlapsedITC = lapsedItc.getResultList();

		List<Gstr9SummaryDto> listTotalItcLapsed = listlapsedITC.stream()
				.map(o -> convertToDtoItcLapsed(o))
				.collect(Collectors.toCollection(ArrayList::new));

		String query3 = taxPayable(buildQuery.toString());

		Query taxPayable = entityManager.createNativeQuery(query3);

		taxPayable.setParameter("gstinList", gstnsLists);

		taxPayable.setParameter("returnPeriod", returnPeriod);

		List<Object[]> listtaxPayable = taxPayable.getResultList();

		List<Gstr9SummaryDto> listTaxPayable = listtaxPayable.stream()
				.map(o -> convertToDtoITaxPayable(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Gstr9ComputeConfigStatusEntity> computeStatusEntity = gstr9ConfigStatus
				.findByGstinInAndFyAndIsDeleteFalse(gstnsLists, getConvertedFy);

		Map<String, Pair<String, LocalDateTime>> computeMapStatus = new HashMap<>();

		computeStatusEntity.forEach(e -> {
			computeMapStatus.put(e.getGstin(), new Pair<String, LocalDateTime>(
					e.getStatus(), e.getCreatedOn()));
		});

		Map<String, BigDecimal> mapTotalTurnOver = new HashMap<>();

		listProcessed.forEach(e -> {
			mapTotalTurnOver.put(e.getGstin(), e.getTotalTurnOver());
		});

		Map<String, BigDecimal> mapNetItcAvl = new HashMap<>();

		listNetItcAvl.forEach(e -> {
			mapNetItcAvl.put(e.getGstin(), e.getNetItcAvl());
		});

		Map<String, BigDecimal> mapItcLapsed = new HashMap<>();

		listTotalItcLapsed.forEach(e -> {
			mapItcLapsed.put(e.getGstin(), e.getLapsedItc());
		});

		Map<String, BigDecimal> mapTaxPayable = new HashMap<>();

		listTaxPayable.forEach(e -> {
			mapTaxPayable.put(e.getGstin(), e.getTaxPayable());
		});

		List<Gstr9SummaryDto> listrespDto = new ArrayList<>();

		List<GstrReturnStatusEntity> signedStatusP = returnstatusRepo
				.findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstinFalse(
						gstnsLists, APIConstants.GSTR9,
						Arrays.asList(returnPeriod));
		if (!signedStatusP.isEmpty()) {
			for (GstrReturnStatusEntity gstrReturnStatusEntity : signedStatusP) {
				gstrReturnStatusMap.put(gstrReturnStatusEntity.getGstin(),
						gstrReturnStatusEntity);
			}
		}

		gstnsLists.forEach(gstin -> {

			Gstr9SummaryDto respDto = new Gstr9SummaryDto();

			if (mapTotalTurnOver.containsKey(gstin)) {
				respDto.setTotalTurnOver(mapTotalTurnOver.get(gstin));
			}

			if (mapNetItcAvl.containsKey(gstin)) {
				respDto.setNetItcAvl(mapNetItcAvl.get(gstin));
			}

			if (mapItcLapsed.containsKey(gstin)) {
				respDto.setLapsedItc(mapItcLapsed.get(gstin));
			}

			if (mapTaxPayable.containsKey(gstin)) {
				respDto.setTaxPayable(mapTaxPayable.get(gstin));
			}

			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");

			if (autoCalMap.containsKey(gstin)) {
				respDto.setComputeStatusAsPerGstn(
						autoCalMap.get(gstin).getValue0());

				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(
								autoCalMap.get(gstin).getValue1());

				String newdate = FOMATTER.format(dateTimeFormatter);
				respDto.setComputeStatusAsPerGstnCreatedDate(newdate);
			} else {
				respDto.setComputeStatusAsPerGstn(NOT_INITIATED);
			}

			if (digiComputeMap.containsKey(gstin)) {
				respDto.setDigiProcessCompStatus(
						digiComputeMap.get(gstin).getValue0());

				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(
								digiComputeMap.get(gstin).getValue1());

				String newdate = FOMATTER.format(dateTimeFormatter);
				respDto.setDigiProcessCompStatusCreatedDate(newdate);
			} else {
				respDto.setDigiProcessCompStatus(NOT_INITIATED);
			}

			if (computeMapStatus.containsKey(gstin)) {
				respDto.setDigiCompStatus(
						computeMapStatus.get(gstin).getValue0());

				LocalDateTime dateTimeFormatter1 = EYDateUtil
						.toISTDateTimeFromUTC(
								computeMapStatus.get(gstin).getValue1());

				String newdate1 = FOMATTER.format(dateTimeFormatter1);

				respDto.setDigiCompStatusCreatedDate(newdate1);
			} else {
				respDto.setDigiCompStatus(NOT_INITIATED);
			}

			if (saveGstinMap.containsKey(gstin)) {
				if (saveGstinMap.get(gstin).getValue0().equalsIgnoreCase("P")) {
					respDto.setGstr9SaveStatus(PROCESSED);

					LocalDateTime dateTimeFormatter1 = EYDateUtil
							.toISTDateTimeFromUTC(
									saveGstinMap.get(gstin).getValue1());

					String newdate1 = FOMATTER.format(dateTimeFormatter1);
					respDto.setGstr9SaveStatusCreatedDate(newdate1);
				} else if (saveGstinMap.get(gstin).getValue0()
						.equalsIgnoreCase("PE")) {
					respDto.setGstr9SaveStatus(PROCESSED_WITH_ERROR);
					LocalDateTime dateTimeFormatter1 = EYDateUtil
							.toISTDateTimeFromUTC(
									saveGstinMap.get(gstin).getValue1());

					String newdate1 = FOMATTER.format(dateTimeFormatter1);
					respDto.setGstr9SaveStatusCreatedDate(newdate1);
				} else if (saveGstinMap.get(gstin).getValue0()
						.equalsIgnoreCase("E")) {
					respDto.setGstr9SaveStatus(SAVE_ERROR);
					LocalDateTime dateTimeFormatter1 = EYDateUtil
							.toISTDateTimeFromUTC(
									saveGstinMap.get(gstin).getValue1());

					String newdate1 = FOMATTER.format(dateTimeFormatter1);
					respDto.setGstr9SaveStatusCreatedDate(newdate1);
				} else if (saveGstinMap.get(gstin).getValue0()
						.equalsIgnoreCase("SAVE_INITIATION_FAILED")) {
					respDto.setGstr9SaveStatus(SAVE_FAILED);
					LocalDateTime dateTimeFormatter1 = EYDateUtil
							.toISTDateTimeFromUTC(
									saveGstinMap.get(gstin).getValue1());

					String newdate1 = FOMATTER.format(dateTimeFormatter1);
					respDto.setGstr9SaveStatusCreatedDate(newdate1);

				} else {
					respDto.setGstr9SaveStatus(NOT_INITIATED);
				}
			} else {
				respDto.setGstr9SaveStatus(NOT_INITIATED);
			}

			if (gstrReturnStatusMap != null && !gstrReturnStatusMap.isEmpty()) {
				for (Map.Entry<String, GstrReturnStatusEntity> entry : gstrReturnStatusMap
						.entrySet()) {
					if (entry.getKey().equals(gstin)) {
						if (FILED.equalsIgnoreCase(
								entry.getValue().getStatus())) {
							respDto.setGstr9SaveStatus(FILED);
							respDto.setGstr9SaveStatusCreatedDate(
									EYDateUtil.fmtLocalDate(
											entry.getValue().getFilingDate()));
						}
					}
				}
			}
			GSTNDetailEntity gstinInfo = gstinInfoRepository
					.findRegDates(gstin);
			String registrationType = gstinInfo != null
					? gstinInfo.getRegistrationType() : "";
			respDto.setGstin(gstin);
			respDto.setAuth(authMap.get(gstin));
			respDto.setStateName(stateNamesMap.get(gstin));
			respDto.setRegType(registrationType);
			listrespDto.add(respDto);
		});

		return listrespDto;
	}

	private String taxPayable(String query) {

		String queryStr = "SELECT GSTIN,RET_PERIOD,IFNULL(IGST,0)+IFNULL(CGST,0)+IFNULL(SGST,0)+"
				+ "IFNULL(CESS,0)+IFNULL(INTR,0)+IFNULL(FEE,0)+IFNULL(PEN,0)"
				+ "+IFNULL(OTH,0) AS TAX_PAYABLE "
				+ "FROM TBL_GSTR9_USER_INPUT WHERE SUBSECTION IN ('9') AND "
				+ "IS_ACTIVE = TRUE " + query;

		return queryStr;
	}

	private String createLapsedItc(String query) {

		String queryStr = "SELECT GSTIN,RET_PERIOD,SUM(LAPSED_ITC_8) AS LAPSED_ITC"
				+ " FROM (SELECT GSTIN,RET_PERIOD,SUM(IFNULL(TAXABLEVALUE,0)+IFNULL(IGST,0)+IFNULL(CGST,0)+IFNULL(SGST,0)+IFNULL(CESS,0)) AS LAPSED_ITC_8"
				+ " FROM TBL_GSTR9_USER_INPUT WHERE SUBSECTION IN ('8E','8F','8G') AND IS_ACTIVE = TRUE"
				+ query + " GROUP BY GSTIN,RET_PERIOD UNION ALL"
				+ " SELECT GSTIN,RET_PERIOD,-1*SUM(IFNULL(TAXABLEVALUE,0)+IFNULL(IGST,0)+IFNULL(CGST,0)+IFNULL(SGST,0)+IFNULL(CESS,0)) AS LAPSED_ITC_8"
				+ " FROM TBL_GSTR9_USER_INPUT WHERE SUBSECTION IN ('6E1','6E2') AND IS_ACTIVE = TRUE"
				+ query
				+ " GROUP BY GSTIN,RET_PERIOD) GROUP BY GSTIN,RET_PERIOD"
				+ " ORDER BY GSTIN";

		return queryStr;
	}

	private String createQueryNetITCAvl(String query) {
		String queryStr = "SELECT GSTIN,RET_PERIOD,SUM(NET_ITC_AVAL_6) AS "
				+ "TOTAL_TOVER FROM ( SELECT GSTIN,RET_PERIOD,"
				+ "SUM(IFNULL(TAXABLEVALUE,0)+IFNULL(IGST,0)+IFNULL(CGST,0)+"
				+ "IFNULL(SGST,0)+IFNULL(CESS,0)) AS NET_ITC_AVAL_6 "
				+ "FROM TBL_GSTR9_USER_INPUT "
				+ "WHERE SUBSECTION IN ('6B1','6B1','6B2','6B3','6C1','6C2',"
				+ "'6C3','6D1','6D2','6D3','6E1','6E2','6F','6G','6H','6K','6L',"
				+ "'6M') AND IS_ACTIVE = TRUE " + query
				+ " GROUP BY GSTIN,RET_PERIOD UNION ALL SELECT GSTIN,RET_PERIOD,"
				+ "(-1*SUM(IFNULL(TAXABLEVALUE,0)+IFNULL(IGST,0)+IFNULL(CGST,0)+IFNULL(SGST,0)+IFNULL(CESS,0)))"
				+ " AS NET_ITC_AVAL_7 " + "FROM TBL_GSTR9_USER_INPUT "
				+ "WHERE SUBSECTION IN ('7A','7B','7C','7D','7E','7F','7G','7H') "
				+ query + " AND IS_ACTIVE = TRUE GROUP BY GSTIN,RET_PERIOD) "
				+ "GROUP BY GSTIN,RET_PERIOD "
				+ "ORDER BY GSTIN,RIGHT(RET_PERIOD,4),LEFT(RET_PERIOD,2)";

		return queryStr;
	}

	private String createQueryTotalTurnOver(String query) {

		String queryStr = "SELECT GSTIN,RET_PERIOD,SUM(TOTAL_TOVER_4N) AS TOTAL_TOVER "
				+ "FROM (   "
				+ "SELECT GSTIN,RET_PERIOD, SUM(IFNULL(TAXABLEVALUE,0)) AS TOTAL_TOVER_4N"
				+ " FROM TBL_GSTR9_USER_INPUT "
				+ "WHERE SUBSECTION IN ('4A','4B','4C','4D','4E','4F','4G','4J','4K','5A','5B','5C','5D','5E','5F','5I','5J','10') AND IS_ACTIVE = TRUE "
				+ query + " GROUP BY GSTIN,RET_PERIOD " + "UNION ALL "
				+ "SELECT GSTIN,RET_PERIOD,(-1*SUM(IFNULL(TAXABLEVALUE,0))) AS TOTAL_TOVER_4G "
				+ "FROM TBL_GSTR9_USER_INPUT "
				+ "WHERE SUBSECTION IN ('4G','4I','4L','5H','5K','11') AND IS_ACTIVE = TRUE "
				+ query + " GROUP BY GSTIN,RET_PERIOD" + ") "
				+ "GROUP BY GSTIN,RET_PERIOD ORDER BY GSTIN,RIGHT(RET_PERIOD,4),LEFT(RET_PERIOD,2)";

		return queryStr;
	}

	private Gstr9SummaryDto convertToDtotalTurnOver(Object[] obj) {

		LOGGER.info(
				"Converting to dto Begin for Gstr9Summary Data totalTurnOver");
		Gstr9SummaryDto dto = new Gstr9SummaryDto();
		dto.setGstin(obj[0] != null ? obj[0].toString() : null);
		dto.setReturnPeriod(obj[1] != null ? obj[1].toString() : null);
		dto.setTotalTurnOver(obj[2] != null ? (BigDecimal) obj[2] : null);
		LOGGER.info(
				"Converting to dto end for Gstr9Summary Data totalTurnOver");
		return dto;
	}

	private Gstr9SummaryDto convertToDtoNetItcAvl(Object[] obj) {

		LOGGER.info(
				"Converting to dto Begin for Gstr9Summary Net ITC Available");
		Gstr9SummaryDto dto = new Gstr9SummaryDto();
		dto.setGstin(obj[0] != null ? obj[0].toString() : null);
		dto.setReturnPeriod(obj[1] != null ? obj[1].toString() : null);
		dto.setNetItcAvl(obj[2] != null ? (BigDecimal) obj[2] : null);
		LOGGER.info("Converting to dto end for Gstr9Summary Net ITC Available");
		return dto;

	}

	private Gstr9SummaryDto convertToDtoItcLapsed(Object[] obj) {

		LOGGER.info(
				"Converting to dto Begin for Gstr9Summary Total ITC Lapsed");
		Gstr9SummaryDto dto = new Gstr9SummaryDto();
		dto.setGstin(obj[0] != null ? obj[0].toString() : null);
		dto.setReturnPeriod(obj[1] != null ? obj[1].toString() : null);
		dto.setLapsedItc(obj[2] != null ? (BigDecimal) obj[2] : null);
		LOGGER.info("Converting to dto end for Gstr9Summary Total ITC Lapsed");
		return dto;

	}

	private Gstr9SummaryDto convertToDtoITaxPayable(Object[] obj) {

		LOGGER.info("Converting to dto Begin for Gstr9Summary Tax Payable");
		Gstr9SummaryDto dto = new Gstr9SummaryDto();
		dto.setGstin(obj[0] != null ? obj[0].toString() : null);
		dto.setReturnPeriod(obj[1] != null ? obj[1].toString() : null);
		dto.setTaxPayable(obj[2] != null ? (BigDecimal) obj[2] : null);
		LOGGER.info("Converting to dto end for Gstr9Summary Tax Payable");
		return dto;

	}

	private Integer convertFyFromString(String fy) {
		String[] fyArr = fy.split("-");
		String fyNew = fyArr[0] + fyArr[1].replaceFirst("20", "");
		return Integer.parseInt(fyNew);
	}

}
