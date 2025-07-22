package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ashutosh.kar
 *
 */

@Slf4j
@Component("SupplierImsRecodsDataDaoImpl")
public class SupplierImsRecodsDataDaoImpl implements SupplierImsRecodsDataDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	private List<String> actionList = ImmutableList.of("P", "A", "R", "N", "NE", "R3B");

	private final List<String> tableTypeList = Arrays.asList("B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA");

	private List<String> returnTypesList = ImmutableList.of("GSTR1", "GSTR1A");

	private List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository regRepo;

	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerService;

	@Override
	public Pair<List<SupplierImsRecordDataResponseDto>, Integer> findSupplierImsRecodsScreenData(
			SupplierImsEntityReqDto reqDto, Pageable pageReq) {

		LOGGER.debug("Inside SupplierImsRecodsDataDaoImpl.findSupplierImsRecodsScreenData() " + " methode reqDto {} :",
				reqDto.toString());

		List<SupplierImsRecordDataResponseDto> respList = new ArrayList<>();
		Pair<List<SupplierImsRecordDataResponseDto>, Integer> resp = null;
		Integer totalCount = 0;

		try {

			List<String> gstins = reqDto.getGstins();
			// if GSTINs are empty getting it from entity

			List<String> gstnsList = new ArrayList<>();

			if (CollectionUtils.isEmpty(gstins)) {
				List<Long> entityIds = new ArrayList<>();
				entityIds.add(reqDto.getEntityId());
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds, outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs.get(OnboardingConstant.GSTIN);

				gstnsList = gstinDetailRepo.filterGstinBasedByRegType(dataSecList, regTypeList);

				reqDto.setGstins(gstnsList);

				LOGGER.debug("GSTINs were empty, fetched GSTINs based on " + " entityId: {}", gstnsList);
			}

			String actionType = reqDto.getActionType();
			List<String> actionTypes = new ArrayList<>();
			if (actionType == null || actionType.isEmpty() || actionType.equalsIgnoreCase("ALL")) {
				actionTypes = actionList;
			} else {
				actionTypes.add(actionType);
			}

			String fromDocDate = reqDto.getFromRetPeriod();
			String toDocDate = reqDto.getToRetPeriod();

			List<String> tableTypes = reqDto.getTableTypes();
			if (tableTypes == null || tableTypes.isEmpty()) {

				tableTypes = tableTypeList;
			}
			List<String> returnTypes = reqDto.getReturnTypes();
			if (returnTypes == null || returnTypes.isEmpty()) {

				returnTypes = returnTypesList;
			}

			int pageNumber = pageReq.getPageNumber();
			int pageSize = pageReq.getPageSize();
			gstins = reqDto.getGstins();

			StoredProcedureQuery storedProc = procCall(actionTypes, fromDocDate, toDocDate, gstins, tableTypes,
					returnTypes, pageNumber, pageSize);

			long dbLoadStartTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();

			long dbLoadEndTime = System.currentTimeMillis();

			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStartTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data" + " from DB is '%d' millisecs,"
								+ " proc Name and Data count:" + " '%s' - '%s'",
						dbLoadTimeDiff, "USP_SUPP_IMS_RECORDS_SCREEN_UI", records.size());
				LOGGER.debug(msg);
			}
			if (records != null && !records.isEmpty()) {

				respList = records.stream().map(o -> convert(o)).collect(Collectors.toCollection(ArrayList::new));

				totalCount = respList.get(0).getTotalCount();
			}

			resp = new Pair<>(respList, totalCount);

		} catch (Exception e) {
			LOGGER.error("Error Occured inside SupplierImsRecodsDataDaoImap {}", e);
			throw new AppException();
		}
		return resp;
	}

	private SupplierImsRecordDataResponseDto convert(Object[] arr) {

		SupplierImsRecordDataResponseDto dto = new SupplierImsRecordDataResponseDto();

		try {

			dto.setActionGstn((arr[1] != null) ? arr[1].toString() : null);
			dto.setGstinSupplier((arr[2] != null) ? arr[2].toString() : null);
			dto.setSupplierLegalName((arr[3] != null) ? arr[3].toString() : null);//
			dto.setReturnPeriod((arr[4] != null) ? arr[4].toString() : null);
			dto.setTableType((arr[5] != null) ? arr[5].toString() : null);
			dto.setDocType((arr[6] != null) ? arr[6].toString() : null);
			dto.setGstinRecipient((arr[7] != null) ? arr[7].toString() : null);
			dto.setRecipientName((arr[8] != null) ? arr[8].toString() : null);//
			dto.setDocNumber((arr[9] != null) ? arr[9].toString() : null);
			dto.setDocDate((arr[10] != null) ? arr[10].toString() : null);
			dto.setTotalTax((arr[11] != null) ? new BigDecimal(arr[11].toString()) : BigDecimal.ZERO);
			dto.setTaxableValue((arr[12] != null) ? new BigDecimal(arr[12].toString()) : BigDecimal.ZERO);
			dto.setIgst((arr[13] != null) ? new BigDecimal(arr[13].toString()) : BigDecimal.ZERO);
			dto.setCgst((arr[14] != null) ? new BigDecimal(arr[14].toString()) : BigDecimal.ZERO);
			dto.setSgst((arr[15] != null) ? new BigDecimal(arr[15].toString()) : BigDecimal.ZERO);
			dto.setCess((arr[16] != null) ? new BigDecimal(arr[16].toString()) : BigDecimal.ZERO);
			dto.setInvoiceValue((arr[17] != null) ? new BigDecimal(arr[17].toString()) : BigDecimal.ZERO);
			dto.setReturnType((arr[18] != null) ? arr[18].toString() : null);
			dto.setGstr1FillingStatus((arr[19] != null) ? arr[19].toString() : null);
			dto.setGstrRecipient3BStatus((arr[20] != null) ? arr[20].toString() : null);
			dto.setEstimatedGstr3BPeriod((arr[21] != null) ? arr[21].toString() : null);
			dto.setOrgDocNumber((arr[22] != null) ? arr[22].toString() : null);
			dto.setOrgDocDate((arr[23] != null) ? arr[23].toString() : null);
			dto.setCheckSum((arr[24] != null) ? arr[24].toString() : null);
			dto.setDockey((arr[25] != null) ? arr[25].toString() : null);
			dto.setSupplyType((arr[26] != null) ? arr[26].toString() : null);
			dto.setTotalCount(
					(arr[0] != null) ? Integer.parseInt(arr[0].toString())
							: 0);
		} catch (Exception e) {
			LOGGER.error("Error Occurred in convert method SupplierImsRecodsDataDaoImpl {} :", e);
			throw new AppException(e);
		}
		return dto;
	}

	private StoredProcedureQuery procCall(List<String> actionTypes, String fromDocDate, String toDocDate,
			List<String> gstins, List<String> tableTypes, List<String> returnTypes, int pageNumber, int pageSize) {

		StoredProcedureQuery storedProc = entityManager.createStoredProcedureQuery("USP_SUPP_IMS_RECORDS_SCREEN_UI");

		if (LOGGER.isDebugEnabled()) {
			String msg = ("About to execute USP_SUPP_IMS_RECORDS_SCREEN_UI proc");
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("RGSTIN", String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("FROM_RET_PRD", Long.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("TO_RET_PRD", Long.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("RETURN_TYPE", String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("TABLE_TYPE", String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("ACTION_TYPE", String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_PAGESIZE", Integer.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_PAGENO", Integer.class, ParameterMode.IN);

		storedProc.setParameter("RGSTIN", String.join(",", gstins));
		storedProc.setParameter("FROM_RET_PRD", GenUtil.convertTaxPeriodToInt(fromDocDate).longValue());
		storedProc.setParameter("TO_RET_PRD", GenUtil.convertTaxPeriodToInt(toDocDate).longValue());
		storedProc.setParameter("RETURN_TYPE", String.join(",", returnTypes));
		storedProc.setParameter("TABLE_TYPE", String.join(",", tableTypes));
		storedProc.setParameter("ACTION_TYPE", String.join(",", actionTypes));
		storedProc.setParameter("IN_PAGESIZE", pageSize);
		storedProc.setParameter("IN_PAGENO", pageNumber);

		return storedProc;

	}

}
