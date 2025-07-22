package com.ey.advisory.app.docs.service.gstr6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DeterminationResponseDto;
import com.ey.advisory.app.services.daos.gstr6.Gstr6CalculateTurnOverGstnServiceImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Component("Gstr6TurnOverDaoImpl")
@Slf4j
public class Gstr6TurnOverDaoImpl implements Gstr6DeterminationDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("Gstr6CalculateTurnOverGstnServiceImpl")
	private Gstr6CalculateTurnOverGstnServiceImpl gstr6CalculateTurnOverGstnServiceImpl;

	private static final String N_I = "Not Initiated";

	@Override
	public SearchResult<Gstr6DeterminationResponseDto> gstr6DeterminationDetails(
			SearchCriteria criteria) {
		return null;
	}

	private Gstr6DeterminationResponseDto convertTransactionalLevel(
			Object[] arr, int dertaxperiodFrom, int dertaxperiodTo) {
		Gstr6DeterminationResponseDto obj = new Gstr6DeterminationResponseDto();

		obj.setId(arr[0] != null ? arr[0].toString() : null);
		String gstn = arr[1] != null ? arr[1].toString() : null;
		obj.setGstin(gstn);
		String stateCode = arr[2] != null ? arr[2].toString() : null;
		String stateName = stateCode != null
				? statecodeRepository.findStateNameByCode(stateCode) : null;
		obj.setState(stateCode);
		obj.setStateName(stateName);

		if (gstn != null && !gstn.isEmpty()) {
			// AuthToken
			String gstintoken = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstin(gstn);
			if (gstintoken != null) {
				if ("A".equalsIgnoreCase(gstintoken)) {
					obj.setAuthToken(APIConstants.ACTIVE);
				} else {
					obj.setAuthToken(APIConstants.IN_ACTIVE);
				}
			} else {
				obj.setAuthToken(APIConstants.IN_ACTIVE);
			}
			// getGstr1 Status
			String gstr1Status = gstr6CalculateTurnOverGstnServiceImpl
					.getGstr1Status(gstn, dertaxperiodFrom, dertaxperiodTo);
			obj.setGetGstr1Status(
					gstr1Status != null ? gstr1Status.toString() : "");
		} else {
			obj.setAuthToken(APIConstants.IN_ACTIVE);
			obj.setGetGstr1Status(arr[3] != null ? arr[3].toString() : null);
		}

		BigDecimal toDigiGst = BigDecimal.ZERO;
		if (arr[4] != null && !arr[4].toString().isEmpty()) {
			toDigiGst = new BigDecimal(arr[4].toString());
			obj.setTurnoverDigiGST(toDigiGst);
		} else {
			obj.setTurnoverDigiGST(toDigiGst);
		}

		BigDecimal toGstn = BigDecimal.ZERO;
		if (arr[5] != null && !arr[5].toString().isEmpty()) {
			toGstn = new BigDecimal(arr[5].toString());
			obj.setTurnoverGstn(toGstn);
		} else {
			obj.setTurnoverGstn(toGstn);
		}

		String gstr1GetTime = arr[6] != null ? arr[6].toString() : null;
		obj.setGstr1GetTime(gstr1GetTime);

		BigDecimal userInput = BigDecimal.ZERO;
		if (arr[7] != null && !arr[7].toString().isEmpty()) {
			userInput = new BigDecimal(arr[7].toString());
			obj.setTurnoverUserEdited(userInput);
		} else {
			obj.setTurnoverUserEdited(userInput);
		}

		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {
		StringBuilder build = new StringBuilder();

		build.append("select id,GSTIN,STATE_NAME,");
		build.append("GET_GSTR1_STATUS AS GET_GSTR1_STATUS,");
		build.append("IFNULL(COMPUTE_VALUE,0) AS TURN_OVER_DIGIGST,");
		build.append("IFNULL(GSTIN_COMPUTE_VALUE,0) AS TURN_OVER_GSTIN,");
		build.append("GET_GSTR1_TIME,USER_INPUT FROM ");
		build.append("GSTR6_TURN_OVER_USERINPUT WHERE IS_DELETE = FALSE ");
		build.append(buildQuery);
		build.append("  ORDER BY GSTIN");
		return build.toString();
	}

	@Override
	public SearchResult<Gstr6DeterminationResponseDto> gstr6TurnOverDetails(
			SearchCriteria criteria) {
		try {
			Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;

			String taxPeriod = request.getReturnPeriod();
			List<Long> entityId = request.getEntityId();
			Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
			String GSTIN = null;

		/*	String ProfitCenter = null;
			String plant = null;
			String sales = null;
			String division = null;
			String location = null;
			String distChannel = null;
			String ud1 = null;
			String ud2 = null;
			String ud3 = null;
			String ud4 = null;
			String ud5 = null;
			String ud6 = null;

			List<String> pcList = null;
			List<String> plantList = null;
			List<String> divisionList = null;
			List<String> locationList = null;
			List<String> salesList = null;
			List<String> distList = null;
			List<String> ud1List = null;
			List<String> ud2List = null;
			List<String> ud3List = null;
			List<String> ud4List = null;
			List<String> ud5List = null;
			List<String> ud6List = null; */
			List<String> gstinList = null;

			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						GSTIN = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}

				/*	if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
						ProfitCenter = key;
						if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PC)
										.size() > 0) {
							pcList = dataSecAttrs.get(OnboardingConstant.PC);
						}
					}

					if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

						plant = key;
						if (!dataSecAttrs.get(OnboardingConstant.PLANT)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PLANT)
										.size() > 0) {
							plantList = dataSecAttrs
									.get(OnboardingConstant.PLANT);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
						division = key;
						if (!dataSecAttrs.get(OnboardingConstant.DIVISION)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.DIVISION)
										.size() > 0) {
							divisionList = dataSecAttrs
									.get(OnboardingConstant.DIVISION);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
						location = key;
						if (!dataSecAttrs.get(OnboardingConstant.LOCATION)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.LOCATION)
										.size() > 0) {
							locationList = dataSecAttrs
									.get(OnboardingConstant.LOCATION);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
						sales = key;
						if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.SO)
										.size() > 0) {
							salesList = dataSecAttrs.get(OnboardingConstant.SO);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
						distChannel = key;
						if (dataSecAttrs.get(OnboardingConstant.DC) != null
								&& dataSecAttrs.get(OnboardingConstant.DC)
										.size() > 0) {
							distList = dataSecAttrs.get(OnboardingConstant.DC);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
						ud1 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD1) != null
								&& dataSecAttrs.get(OnboardingConstant.UD1)
										.size() > 0) {
							ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
						ud2 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD2) != null
								&& dataSecAttrs.get(OnboardingConstant.UD2)
										.size() > 0) {
							ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
						ud3 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD3) != null
								&& dataSecAttrs.get(OnboardingConstant.UD3)
										.size() > 0) {
							ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
						ud4 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD4) != null
								&& dataSecAttrs.get(OnboardingConstant.UD4)
										.size() > 0) {
							ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
						ud5 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD5) != null
								&& dataSecAttrs.get(OnboardingConstant.UD5)
										.size() > 0) {
							ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
						ud6 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD6) != null
								&& dataSecAttrs.get(OnboardingConstant.UD6)
										.size() > 0) {
							ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
						}
					} */
				}
			}

			StringBuilder buildHeader = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildHeader.append(" AND ISD_GSTIN IN :gstinList");

				}
			}
			/* if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {

					buildHeader.append(" AND PROFIT_CENTRE IN :pcList");

				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {

					buildHeader.append(" AND PLANT_CODE IN :plantList");

				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && salesList.size() > 0) {

					buildHeader.append(" AND SALES_ORGANIZATION IN :salesList");

				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {

					buildHeader
							.append(" AND DISTRIBUTION_CHANNEL IN :distList");

				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {

					buildHeader.append(" AND DIVISION IN :divisionList");

				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && locationList.size() > 0) {

					buildHeader.append(" AND LOCATION IN :locationList");

				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {

					buildHeader.append(" AND USERACCESS1 IN :ud1List");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {

					buildHeader.append(" AND USERACCESS2 IN :ud2List");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {

					buildHeader.append(" AND USERACCESS3 IN :ud3List");
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && ud4List.size() > 0) {

					buildHeader.append(" AND USERACCESS4 IN :ud4List");
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && ud5List.size() > 0) {

					buildHeader.append(" AND USERACCESS5 IN :ud5List");
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && ud6List.size() > 0) {

					buildHeader.append(" AND USERACCESS6 IN :ud6List");
				}
			} */

			if (taxPeriod != null && !taxPeriod.isEmpty()) {
				buildHeader.append(" AND CURRENT_RET_PERIOD = :taxPeriod");
			}
			String queryStr = createApiProcessedQueryString(
					buildHeader.toString());

			Query q = entityManager.createNativeQuery(queryStr);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("DB Query {} ", queryStr);
			}

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}

			if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
			}
		/*	if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && !plantList.isEmpty()
						&& plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && !salesList.isEmpty()
						&& salesList.size() > 0) {
					q.setParameter("salesList", salesList);
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && !divisionList.isEmpty()
						&& divisionList.size() > 0) {
					q.setParameter("divisionList", divisionList);
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && !locationList.isEmpty()
						&& locationList.size() > 0) {
					q.setParameter("locationList", locationList);
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && !distList.isEmpty()
						&& distList.size() > 0) {
					q.setParameter("distList", distList);
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && !ud1List.isEmpty()
						&& ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
						&& ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
						&& ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
						&& ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
						&& ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
						&& ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			} */
			List<Object[]> list = q.getResultList();

			List<String> nIGstinList = new ArrayList<>();

			for (Object[] gstn : list) {
				String sgtin = gstn[1] != null && !gstn[1].toString().isEmpty()
						? gstn[1].toString().trim() : null;
				if (sgtin != null) {
					nIGstinList.add(sgtin);
				}
			}

			List<String> isdGstins = gstnDetailRepository
					.findgstinByEntityIdWithOutISD(entityId);

			if (CollectionUtils.isNotEmpty(isdGstins)
					|| CollectionUtils.isNotEmpty(nIGstinList)) {
				list = addedNotIntiatedValues(nIGstinList, isdGstins, list);
			}
			List<Gstr6DeterminationResponseDto> verticalHsnList = Lists
					.newArrayList();

			// Added this piece of code for getgstr1 status method (Bhavya)
			int dertaxperiodFrom = 0;
			int dertaxperiodTo = 0;
			if (request.getReturnFrom() != null
					&& !request.getReturnFrom().isEmpty()) {
				dertaxperiodFrom = GenUtil
						.convertTaxPeriodToInt(request.getReturnFrom());
			}
			if (request.getReturnTo() != null
					&& !request.getReturnTo().isEmpty()) {
				dertaxperiodTo = GenUtil
						.convertTaxPeriodToInt(request.getReturnTo());
			}
			// Added data list for concurrent modification exception
			List<Object[]> data = new ArrayList<>();
			data.addAll(list);

			for (Object[] gstn : list) {
				String sgtin = gstn[1] != null && !gstn[1].toString().isEmpty()
						? gstn[1].toString().trim() : null;

				if (gstinList != null && gstinList.contains(sgtin)) {
					data.remove(gstn);
				}
			}

			if (CollectionUtils.isNotEmpty(data)) {
				for (Object arr[] : data) {
					verticalHsnList.add(convertTransactionalLevel(arr,
							dertaxperiodFrom, dertaxperiodTo));
				}
			}
			return new SearchResult<>(verticalHsnList);

		} catch (Exception e) {
			LOGGER.debug("Exception Occur in Gstr6DeterminationDaoImpl", e);
			throw new AppException(e);

		}
	}

	private List<Object[]> addedNotIntiatedValues(List<String> unRegGstins,
			List<String> isdGstins, List<Object[]> listDummy) {

		if (isdGstins != null && !isdGstins.isEmpty()) {
			for (String isdGstin : isdGstins) {
				Object[] dummy = null;
				if (!unRegGstins.contains(isdGstin)) {
					dummy = new Object[8];
					dummy[1] = isdGstin;
					dummy[2] = isdGstin != null ? isdGstin.substring(0, 2)
							: null;
					dummy[3] = N_I;
					dummy[4] = BigDecimal.ZERO;
					dummy[5] = BigDecimal.ZERO;
					dummy[7] = BigDecimal.ZERO;
				}
				if (dummy != null) {
					listDummy.add(dummy);
				}
			}
		}
		return listDummy;
	}
}
