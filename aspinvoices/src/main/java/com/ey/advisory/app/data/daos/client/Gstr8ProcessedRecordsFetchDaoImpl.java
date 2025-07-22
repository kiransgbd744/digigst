package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr8ProcessedRecordsRespDto;
import com.ey.advisory.app.processors.handler.GstnReturnStatusUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr8ProcessedRecordsFetchDaoImpl")
public class Gstr8ProcessedRecordsFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private GstnReturnStatusUtil returnStatusUtil;

	@Autowired
	DefaultStateCache defaultStateCache;

	public List<Gstr8ProcessedRecordsRespDto> loadGstr8ProcessedRecords(
			Gstr1ProcessedRecordsReqDto req) {

		String taxPeriod = req.getRetunPeriod();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		String gstin = null;
		List<String> gstinList = null;
		List<Gstr8ProcessedRecordsRespDto> retList = new ArrayList<>();
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
		if (gstinList != null) {
			StringBuilder build = new StringBuilder();

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND GSTIN IN :gstinList ");

				}
			}
			if (taxPeriod != null) {
				build.append(" AND RET_PERIOD = :taxPeriod ");
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
				if (taxPeriod != null) {
					q.setParameter("taxPeriod", taxPeriod);
				}

				@SuppressWarnings("unchecked")
				List<Object[]> list = q.getResultList();

				retList = list.stream().map(o -> Convertingtodto(o, taxPeriod))
						.collect(Collectors.toCollection(ArrayList::new));
				fillTheDataFromDataSecAttr(retList, gstinList);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AppException("Unexpected error in query execution.",
						e);
			}
		}
		return retList;
	}

	public Gstr8ProcessedRecordsRespDto Convertingtodto(Object[] obj,
			String taxPeriod) {

		Gstr8ProcessedRecordsRespDto dto = new Gstr8ProcessedRecordsRespDto();

		String gstin = (String) obj[0];
		dto.setGstin(gstin);
		String returnStatus = (String) obj[1];
		dto.setReturnStatus(returnStatus);
		Integer count = Integer.parseInt(obj[2].toString());
		dto.setTotalCount(count);
		dto.setTotalSupplies((BigDecimal) obj[3]);
		dto.setIgst((BigDecimal) obj[4]);
		dto.setCgst((BigDecimal) obj[5]);
		dto.setSgst((BigDecimal) obj[6]);
		String stateCode = obj[0].toString().substring(0, 2);

		String stateName = defaultStateCache.getStateName(stateCode);
		dto.setState(stateName);
		List<String> regType = gSTNDetailRepository
				.findgstr2avs3bRegTypeByGstin(gstin);
		dto.setRegType(regType.get(0));

		Optional<GetAnx1BatchEntity> batchEntity = batchRepo
				.findByTypeAndApiSectionAndSgstinAndTaxPeriodAndIsDeleteFalse(
						APIIdentifiers.GSTR8_GETSUM,
						APIConstants.GSTR8.toUpperCase(), gstin, taxPeriod);

		if (!batchEntity.isPresent()) {
			dto.setGetGstr8Status(APIConstants.NOT_INITIATED);
		} else {
			dto.setGetGstr8Status(batchEntity.get().getStatus());
			dto.setGetGstr8Time(
					EYDateUtil.fmtDate(batchEntity.get().getCreatedOn()));
		}

		Pair<String, String> lastTransactionReturnStatus = returnStatusUtil
				.getLastTransactionReturnStatus(dto.getGstin(), taxPeriod,
						APIConstants.GSTR8.toUpperCase());
		if (lastTransactionReturnStatus != null) {
			dto.setReturnStatus(lastTransactionReturnStatus.getValue0()
					.replace(" ", "_").toUpperCase());
			dto.setReturnStatusTime(lastTransactionReturnStatus.getValue1());
		}

		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin((String) obj[0]);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				dto.setAuthToken("Active");
			} else {
				dto.setAuthToken("Inactive");
			}
		} else {
			dto.setAuthToken("Inactive");
		}

		return dto;
	}

	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT GSTIN, "
				+ "NULL AS RETURN_STATUS,COUNT(DOC_KEY), "
				+ "SUM(NET_SUPPLIES),SUM(IGST_AMT) AS IGST_AMT, "
				+ "SUM(CGST_AMT) AS CGST_AMT,SUM(SGST_AMT) AS SGST_AMT "
				+ "FROM TBL_GSTR8_UPLOAD_PSD WHERE IS_ACTIVE=TRUE AND SUPPLY_TYPE = 'TAX' "
				+ buildQuery + " GROUP BY GSTIN ";
		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		return queryStr;
	}

	public void fillTheDataFromDataSecAttr(
			List<Gstr8ProcessedRecordsRespDto> retList,
			List<String> gstinList) {
		List<String> dataGstinList = new ArrayList<>();
		retList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstin : gstinList) {
				if (!dataGstinList.contains(gstin)) {
					Gstr8ProcessedRecordsRespDto dummy = new Gstr8ProcessedRecordsRespDto();

					dummy.setGstin(gstin);
					dummy.setTotalCount(new Integer("0"));
					dummy.setIgst(new BigDecimal("0.0"));
					dummy.setCgst(new BigDecimal("0.0"));
					dummy.setSgst(new BigDecimal("0.0"));
					dummy.setTotalSupplies(new BigDecimal("0.0"));
					dummy.setGetGstr8Status(APIConstants.NOT_INITIATED);
					dummy.setReturnStatus(APIConstants.NOT_INITIATED);
					String stateCode = gstin.substring(0, 2);
					String stateName = defaultStateCache
							.getStateName(stateCode);
					dummy.setState(stateName);
					List<String> regType = gSTNDetailRepository
							.findgstr2avs3bRegTypeByGstin(gstin);
					dummy.setRegType(regType.get(0));
					String gstintoken = defaultGSTNAuthTokenService
							.getAuthTokenStatusForGstin(gstin);
					if (gstintoken != null) {
						if ("A".equalsIgnoreCase(gstintoken)) {
							dummy.setAuthToken("Active");
						} else {
							dummy.setAuthToken("Inactive");
						}
					} else {
						dummy.setAuthToken("Inactive");
					}
					dataGstinList.add(gstin);
					retList.add(dummy);
				}
			}
		}

	}
}
