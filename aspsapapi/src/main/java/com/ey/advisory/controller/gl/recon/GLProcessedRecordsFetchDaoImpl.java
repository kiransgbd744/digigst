package com.ey.advisory.controller.gl.recon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GLProcessedRecordsFetchDaoImpl")
public class GLProcessedRecordsFetchDaoImpl {
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
 
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	public List<GLProcessedRecordsRespDto> loadEinvProcessedRecords(GLProcessedSummaryReqDto glProcessedSummaryDto) {

//		Long entityId = glProcessedSummaryDto.getEntityId();
		String taxPeriodFrom = glProcessedSummaryDto.getTaxPeriodFrom();
		String taxPeriodTo = glProcessedSummaryDto.getTaxPeriodTo();

		List<String> transType = glProcessedSummaryDto.getTransType();

		List<String> gstins = glProcessedSummaryDto.getGstins();
		
		 
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

		// Process if GSTINs are empty
		
		if (CollectionUtils.isEmpty(glProcessedSummaryDto.getGstins()))
		{
			List<String> gstnsList = new ArrayList<>();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(glProcessedSummaryDto.getEntityId()));
			Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();
			Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQueryOutward(entityIds,
							outwardSecurityAttributeMap);

			List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
			gstins = gstNDetailRepository
					.filterGstinBasedByRegType(ttlGstinList, regTypeList);
			
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Entity IDs [{}]", entityIds);
			    LOGGER.debug("Data Security Attributes [{}]", dataSecAttrs);
			    LOGGER.debug("Total GSTIN list from data security [{}]", ttlGstinList);
			    LOGGER.debug("Filtered GSTINs based on registration types [{}]", gstins);
			}

		}
		
		/*if (CollectionUtils.isEmpty(glProcessedSummaryDto.getGstins())) {
		List<String> gstnsList = new ArrayList<>();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(glProcessedSummaryDto.getEntityId()));
			
			gstins=gstinDetailRepo.findActiveGstinsByEntityIds(entityIds);
			
			Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();
			Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds, outwardSecurityAttributeMap);
			List<String> dataSecList = dataSecAttrs.get(OnboardingConstant.GSTIN);

			gstnsList = gstinDetailRepo.filterGstinBasedByRegType(dataSecList, regTypeList);
			
			

			//glProcessedSummaryDto.setGstins(gstnsList);
			LOGGER.debug("GSTINs were empty, fetched GSTINs based on entityId: {}", gstnsList);
		}*/

		int taxPeriod1 = 0;
		int taxPeriod2 = 0;
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			taxPeriod2 = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		}
		List<GLProcessedRecordsRespDto> response =new ArrayList<>();

		try {
			List<GSTNDetailEntity> regList = gstNDetailRepository.findRegTypeByGstinList(gstins);
			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(GSTNDetailEntity::getGstin, GSTNDetailEntity::getRegistrationType));

			Map<String, String> stateNames = entityService.getStateNames(gstins);
			Map<String, String> authTokenStatus = authTokenService.getAuthTokenStatusForGstins(gstins);

			LOGGER.debug("Fetched auth token statuses: {}", authTokenStatus);

			String procName = "USP_GL_RECON_PSD_SMRY";

			StoredProcedureQuery dispProc = entityManager.createStoredProcedureQuery(procName);

			 dispProc.registerStoredProcedureParameter("GL_GSTIN", String.class, ParameterMode.IN);
			    dispProc.registerStoredProcedureParameter("GL_TAX_PERIOD_FROM", Integer.class, ParameterMode.IN);
			    dispProc.registerStoredProcedureParameter("GL_TAX_PERIOD_TO", Integer.class, ParameterMode.IN);
			    dispProc.registerStoredProcedureParameter("GL_TRANS_TYPE", String.class, ParameterMode.IN);
			    
			    dispProc.setParameter("GL_GSTIN", String.join(",", gstins));
			    dispProc.setParameter("GL_TAX_PERIOD_FROM", taxPeriod1);
			    dispProc.setParameter("GL_TAX_PERIOD_TO", taxPeriod2);
			    dispProc.setParameter("GL_TRANS_TYPE", String.join(",", transType));

			List<Object[]> resultList = dispProc.getResultList();
			

			if (resultList.isEmpty()) {
				LOGGER.info("GL Processed Records Entity Proc No data found.");

				for (String gstin : gstins) {
					GLProcessedRecordsRespDto dto = new GLProcessedRecordsRespDto();
					dto.setGstin(gstin);
					dto.setAuthToken("A".equalsIgnoreCase(
							authTokenStatus.getOrDefault(gstin, ""))
									? "Active"
									: "Inactive");
					dto.setState(stateNames.get(gstin));
					dto.setRegType(regMap.get(gstin));
					// count, assessableAmt, igst, cgst, sgst, cess are already
					// defaulted to ZERO
					response.add(dto);
				}

				return response;
			} else {

				LOGGER.debug("GL Processed Records Entity Proc result is: {}", resultList.size());

				response.addAll(resultList.stream().map(row -> convert(row, authTokenStatus, regMap, stateNames))
						.collect(Collectors.toList()));	
				
				

			}

		} catch (Exception e) {
			throw new AppException("Exception occurred while processing GL Processed Records Entity", e);

		}
		response.sort(Comparator.comparing(GLProcessedRecordsRespDto::getGstin));

		return response;
		
	}

	private GLProcessedRecordsRespDto convert(Object[] row, Map<String, String> authTokenStatus,
			Map<String, String> regMap, Map<String, String> stateNames) {

		GLProcessedRecordsRespDto dto = new GLProcessedRecordsRespDto();

		// Convert count to BigDecimal safely
		String gstin = row[0].toString();

		dto.setGstin(gstin);

		dto.setAuthToken("A".equalsIgnoreCase(authTokenStatus.getOrDefault(gstin, "")) ? "Active" : "Inactive");
		dto.setState(stateNames.get(gstin));
		dto.setRegType(regMap.get(gstin));

		dto.setCount(convertToBigDecimal(row[1]));
		dto.setAssessableAmt(convertToBigDecimal(row[2]));
		dto.setIgst(convertToBigDecimal(row[3]));
		dto.setCgst(convertToBigDecimal(row[4]));
		dto.setSgst(convertToBigDecimal(row[5]));
		dto.setCess(convertToBigDecimal(row[6]));

		return dto;
	}

	private BigDecimal convertToBigDecimal(Object value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		if (value instanceof Number) {
			return new BigDecimal(((Number) value).toString());
		} else if (value instanceof String) {
			return new BigDecimal((String) value);
		}
		// Handle other types if necessary
		return BigDecimal.ZERO; // Default case
	}

}
