/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.client.Gstr3BSaveStatusEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bGenerateStatusEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bDigiStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("Gstr3BEntityDashboardServiceImpl")
@Slf4j
public class Gstr3BEntityDashboardServiceImpl
		implements Gstr3BEntityDashboardService {

	private static DateTimeFormatter FORMATTER1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy : HH:mm:ss");

	private static DateTimeFormatter FORMATTER2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	private static final BigDecimal BigDecimal = new BigDecimal(0.0);

	@Autowired
	EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository statusRepo;

	@Autowired
	@Qualifier("Gstr3BTotalLiabilities")
	private Gstr3BTotalLiabilities totalLiabilities;

	@Autowired
	@Qualifier("Gstr3BTotalITC")
	private Gstr3BTotalITC totalItc;

	@Autowired
	@Qualifier("Gstr3BGstinDashboardDaoImpl")
	private Gstr3BGstinDashboardDaoImpl dashBoardDao;

	@Autowired
	private Gstr3BSaveStatusRepository gstr3BSaveStatusRepo;
	
	@Autowired
	@Qualifier("Gstr3bDigiStatusRepository")
	private Gstr3bDigiStatusRepository gstr3bDigiStatusRepository;
	
	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository respRepo;

	private static String retuenType = "GSTR3B";
	private static String requestType = "GET";

	@Override
	public List<Gstr3BEntityDashboardDto> getEntityDashBoard(String taxPeriod,
			List<String> gstnsList, Long entityId) throws AppException {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("In GSTR3B Entity Dashboard,"
					+ "Fetching state names for gstins %s", gstnsList);
			LOGGER.debug(msg);
		}
		Map<String, String> stateNamesMap = entityService
				.getStateNames(gstnsList);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"In GSTR3B Entity Dashboard,"
							+ "state names for gstins %s are %s",
					gstnsList, stateNamesMap);
			LOGGER.debug(msg);
		}

		Map<String, String> authMap = authTokenService
				.getAuthTokenStatusForGstins(gstnsList);

		String msg = String.format(
				"Getting the list of AuthToken statuses "
						+ "for %d GSTINS: [%s]",
				gstnsList.size(), StringUtils.join(gstnsList, ", "));
		LOGGER.debug(msg);

		List<GstrReturnStatusEntity> statusInfoList = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Calling Gstr3BEntityDashboardServiceImpl"
					+ ".getStatuswithTaxPeriod   "
					+ " fetching Save status, taxPeriod and last modifiled date");
		}

		List<GstnUserRequestEntity> interestStatusList = respRepo
				.findByGstinInAndTaxPeriodAndReturnTypeAndRequestType(gstnsList,
						taxPeriod, retuenType, requestType);

		String optionOpted = "A";
		if (entityId != null) {
			optionOpted = optionOpted(entityId);
		}
		Map<String, GstnUserRequestEntity> InterstStatusInfoMap = interestStatusList
				.stream()
				.collect(Collectors.toMap(
						o -> o.getGstin() + "" + o.getTaxPeriod(),
						Function.identity()));

		statusInfoList = statusRepo.getStatuswithTaxPeriod(gstnsList,
				taxPeriod);

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"In GSTR3B Entity Dashboard, getStatuswithTaxPeriod "
							+ "gstnsList %s, taxPeriod %s, statusInfoList %s",
					gstnsList, taxPeriod, statusInfoList);
			LOGGER.debug(msg);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Invoking Gstr3BEntityDashboardServiceImpl"
					+ ".getStatuswithTaxPeriod   "
					+ " fetched Save status, taxPeriod and last modifiled date");
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"converting GstrReturnStatusEntity to GstrReturnStatusDto");
		}
		List<GstrReturnStatusDto> retDto = convertDto(statusInfoList);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"converted GstrReturnStatusEntity into GstrReturnStatusDto");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Creating statusInfoMap with key gstin + taxPeriod");
		}
		Map<String, GstrReturnStatusDto> statusInfoMap = retDto.stream()
				.collect(Collectors.toMap(
						o -> o.getGstin() + "" + o.getTaxPeriod(),
						Function.identity()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("In Gstr3BEntityDashboardServiceImpl"
					+ ".getEntityDashBoard creating Gstr3BEntityDashboardDto "
					+ "list");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Invoking totalLiabilities.getTotalLiabilities");
		}
		Map<String, BigDecimal> lmap = totalLiabilities
				.getTotalLiabilities(gstnsList, taxPeriod);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Invoking totalItc.getTotalItc");
		}
		Map<String, BigDecimal> imap = totalItc.getTotalItc(gstnsList,
				taxPeriod);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Setting Values in Gstr3BEntityDashboardDto");
		}

		List<String> keyList = imap.keySet().stream().distinct()
				.collect(Collectors.toList());
		if (LOGGER.isDebugEnabled()) {
			msg = String.format("In GSTR3B Entity Dashboard," + "keyList %s ",
					keyList);
			LOGGER.debug(msg);
		}

		List<Gstr3BEntityDashboardDto> resList = new ArrayList<>();
		Set<String> subSet = new HashSet<>();

		if (!keyList.isEmpty()) {
			for (String taxPeriodList : keyList) {
				String subGstin = taxPeriodList.substring(0, 15);
				subSet.add(subGstin);
			}

		}
		List<String> subListGstin = subSet.stream()
				.collect(Collectors.toList());

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Before -In GSTR3B Entity Dashboard,"
							+ "subListGstin %s and gstnsList '%s' ",
					subListGstin, gstnsList);
			LOGGER.debug(msg);
		}

		if (subListGstin != null && !subListGstin.isEmpty())
			gstnsList.removeAll(subListGstin);

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"After removeAll -In GSTR3B Entity Dashboard,"
							+ "subListGstin %s and gstnsList '%s' ",
					subListGstin, gstnsList);
			LOGGER.debug(msg);
		}

		if (!keyList.isEmpty()) {
			for (String taxPeriodList : keyList) {
				String tp = taxPeriodList.substring(15);
				String gstin = taxPeriodList.substring(0, 15);
				Gstr3BEntityDashboardDto res = new Gstr3BEntityDashboardDto();
				res.setGstin(gstin);
				res.setOptionSelected(optionOpted);
				res.setAuth(authMap.get(gstin));
				res.setStateName(stateNamesMap.get(gstin));
				if (statusInfoMap.get(gstin + "" + tp) != null && statusInfoMap
						.get(gstin + "" + tp).getUpdatedOn() != null)
					res.setLastModifiedOn(convertDateFormat(
							statusInfoMap.get(gstin + "" + tp).getUpdatedOn()));
				if (statusInfoMap.get(gstin + "" + tp) != null && statusInfoMap
						.get(gstin + "" + tp).getStatus() != null) {
					if (statusInfoMap.get(gstin + "" + taxPeriod).getStatus()
							.equalsIgnoreCase("FILED")) {
						res.setSavedStatus(statusInfoMap
								.get(gstin + "" + taxPeriod).getStatus());
						res.setFiledDate(convertDateFormat(statusInfoMap
								.get(gstin + "" + taxPeriod).getFiledDate()));
					} else {
						res.setSavedStatus(statusInfoMap
								.get(gstin + "" + taxPeriod).getStatus());
					}
				} else {

					res.setSavedStatus("New");
				}
				if (imap.get(gstin + "" + tp) != null)
					res.setTotalItc(imap.get(gstin + "" + tp));
				else {
					res.setTotalItc(BigDecimal);
				}
				if (lmap.get(gstin + "" + tp) != null)
					res.setTotalLiability(lmap.get(gstin + "" + tp));
				else {
					res.setTotalLiability(BigDecimal);
				}
				res.setTaxPeriod(tp);

				Gstr3BSaveStatusEntity saveEntity = gstr3BSaveStatusRepo
						.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin, tp);
				
             
				if (!res.getSavedStatus().equalsIgnoreCase("FILED") && saveEntity != null ) {
					LocalDateTime recentSaveDate = saveEntity.getCreatedOn();
					res.setLastUpdatedOn(EYDateUtil.fmtDate(
							EYDateUtil.toISTDateTimeFromUTC(recentSaveDate)));
					res.setSavedStatus(saveEntity.getStatus());
					res.setFiledDate(null);
				}
				Gstr3bGenerateStatusEntity gstr3bDigiStatusEntity = 
						gstr3bDigiStatusRepository.findByGstinAndTaxPeriodAndIsActive(gstin, taxPeriod, true);
				
				if(gstr3bDigiStatusEntity != null){
					res.setDigiStatus(gstr3bDigiStatusEntity.getStatus());
					if (gstr3bDigiStatusEntity.getModifiedOn() != null) {
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(EYDateUtil
								.toISTDateTimeFromUTC(gstr3bDigiStatusEntity
										.getModifiedOn()));
						res.setDigiUpdateOn(newdate.toString());
					}
				} else {
					res.setDigiStatus("Not_Initiated");
				}
				if (InterstStatusInfoMap.get(gstin + "" + tp) != null
						&& InterstStatusInfoMap.get(gstin + "" + tp)
								.getIntrtAutoCalcResponse() != null)
					res.setInterestFalg(true);

				resList.add(res);

			}
		}
		if (!gstnsList.isEmpty()) {
			for (String gstin : gstnsList) {
				Gstr3BEntityDashboardDto res = new Gstr3BEntityDashboardDto();
				res.setGstin(gstin);
				res.setOptionSelected(optionOpted);
				res.setAuth(authMap.get(gstin));
				res.setStateName(stateNamesMap.get(gstin));
				
				Gstr3bGenerateStatusEntity gstr3bDigiStatusEntity = 
						gstr3bDigiStatusRepository.findByGstinAndTaxPeriodAndIsActive(gstin, taxPeriod, true);
				
				if(gstr3bDigiStatusEntity != null){
					res.setDigiStatus(gstr3bDigiStatusEntity.getStatus());
					if (gstr3bDigiStatusEntity.getModifiedOn() != null) {
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(EYDateUtil
								.toISTDateTimeFromUTC(gstr3bDigiStatusEntity
										.getModifiedOn()));
						res.setDigiUpdateOn(newdate.toString());
					}
				} else {
					res.setDigiStatus("Not_Initiated");
				}
				
				if (statusInfoMap.get(gstin + "" + taxPeriod) != null
						&& statusInfoMap.get(gstin + "" + taxPeriod)
								.getUpdatedOn() != null)
					res.setLastModifiedOn(convertDateFormat(statusInfoMap
							.get(gstin + "" + taxPeriod).getUpdatedOn()));
				if (statusInfoMap.get(gstin + "" + taxPeriod) != null
						&& statusInfoMap.get(gstin + "" + taxPeriod)
								.getStatus() != null) {
					if (statusInfoMap.get(gstin + "" + taxPeriod).getStatus()
							.equalsIgnoreCase("FILED")) {
						res.setSavedStatus(statusInfoMap
								.get(gstin + "" + taxPeriod).getStatus());
						res.setFiledDate(convertDateFormat(statusInfoMap
								.get(gstin + "" + taxPeriod).getFiledDate()));
					} else {
						res.setSavedStatus(statusInfoMap
								.get(gstin + "" + taxPeriod).getStatus());
					}
				} else {
					res.setSavedStatus("New");
				}
				if (imap.get(gstin + "" + taxPeriod) != null)
					res.setTotalItc(imap.get(gstin + "" + taxPeriod));
				else {
					res.setTotalItc(BigDecimal);
				}
				if (lmap.get(gstin + "" + taxPeriod) != null)
					res.setTotalLiability(lmap.get(gstin + "" + taxPeriod));
				else {
					res.setTotalLiability(BigDecimal);
				}
				res.setTaxPeriod(taxPeriod);
				resList.add(res);
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("inside Gstr3BEntityDashboardServiceImpl, Before "
					+ "Returning Gstr3BEntityDashboardDto resList : "
					+ resList);
		}
		return resList;
	}

	private List<GstrReturnStatusDto> convertDto(
			List<GstrReturnStatusEntity> statusInfoList) {

		List<GstrReturnStatusDto> retList = new ArrayList<GstrReturnStatusDto>();

		for (GstrReturnStatusEntity gstrReturnStatusEntity : statusInfoList) {

			GstrReturnStatusDto returnDto = new GstrReturnStatusDto();
			returnDto.setGstin(gstrReturnStatusEntity.getGstin());
			if (gstrReturnStatusEntity.getStatus() != null) {
				if (gstrReturnStatusEntity.getStatus()
						.equalsIgnoreCase("FILED")) {
					returnDto.setStatus(
							gstrReturnStatusEntity.getStatus().toUpperCase());
					returnDto.setFiledDate(
							gstrReturnStatusEntity.getFilingDate());
				} else {
					returnDto.setStatus(gstrReturnStatusEntity.getStatus());
				}
			} else {
				returnDto.setStatus("New");
			}
			returnDto.setTaxPeriod(gstrReturnStatusEntity.getTaxPeriod());
			returnDto.setUpdatedOn(gstrReturnStatusEntity.getUpdatedOn());
			retList.add(returnDto);
		}
		return retList;
	}

	public String convertDateFormat(LocalDate dateFormat1) {
		try {
			return FORMATTER2.format(dateFormat1);
		} catch (Exception ex) {
			String errMsg = String.format("Invalid Date format", dateFormat1);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
	}

	public String convertDateFormat(LocalDateTime inputString) {
		try {
			return inputString.format(FORMATTER1);
		} catch (Exception ex) {
			String errMsg = String.format("Invalid Date format", inputString);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}

	}

	private String optionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		if("A".equalsIgnoreCase(optAns))
			return "A";
		else if("B".equalsIgnoreCase(optAns))
		return "B";
		else return "C";
	}

}
