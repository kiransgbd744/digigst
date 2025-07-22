package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstr2.userdetails.EntityService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("SupplierImsGstinLevelServiceImpl")
@Slf4j
public class SupplierImsGstinLevelServiceImpl implements SupplierImsGstinLevelService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Override
	public SupplierImsGstinSummaryResponseDto getSupplierImsSummaryGstinLvlData(SupplierImsEntityReqDto criteria) {
		SupplierImsGstinSummaryResponseDto responseDto = new SupplierImsGstinSummaryResponseDto();

		List<SupplierImsGstinSectionSummaryDto> imsList = callImsProc(criteria);
		List<SupplierImsGstinSectionSummaryDto> gstr1List = callGstr1Proc(criteria);

		responseDto.setImsData(imsList);
		responseDto.setGstr1And1AData(gstr1List);
		return responseDto;
	}

	@SuppressWarnings("unchecked")
	private List<SupplierImsGstinSectionSummaryDto> callImsProc(SupplierImsEntityReqDto criteria) {
		List<SupplierImsGstinSectionSummaryDto> dtoList = new ArrayList<>();
		try {
			LOGGER.info("Calling stored procedure USP_SUPPLIER_IMS_GSTIN for GSTIN: {}, ReturnPeriod: {}",
					criteria.getGstin(), criteria.getReturnPeriod());

			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("USP_SUPPLIER_IMS_GSTIN");
			query.registerStoredProcedureParameter("IP_SUPPLIER_GSTIN", String.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("IP_RETURN_PERIOD", String.class, ParameterMode.IN);

			query.setParameter("IP_SUPPLIER_GSTIN", criteria.getGstin());
			query.setParameter("IP_RETURN_PERIOD",
					GenUtil.convertTaxPeriodToInt(criteria.getReturnPeriod()).toString());

			List<Object[]> resultList = query.getResultList();

			if (resultList == null || resultList.isEmpty()) {
				LOGGER.warn("No data returned from USP_SUPPLIER_IMS_GSTIN");
				dtoList.add(createDefaultDto("IMS"));
				return dtoList;
			}

			for (Object[] row : resultList) {
				SupplierImsGstinSectionSummaryDto dto = new SupplierImsGstinSectionSummaryDto();
				dto.setSectionType((String) row[0]);
				dto.setCount(toInt(row[1]));
				dto.setTotalTaxableValue(toBigDecimal(row[2]));
				dto.setTotalTax(toBigDecimal(row[3]));
				dto.setIgst(toBigDecimal(row[4]));
				dto.setCgst(toBigDecimal(row[5]));
				dto.setSgst(toBigDecimal(row[6]));
				dto.setCess(toBigDecimal(row[7]));
				dtoList.add(dto);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while calling USP_SUPPLIER_IMS_GSTIN: {}", e.getMessage(), e);
			throw new AppException("Exception while calling USP_SUPPLIER_IMS_GSTIN", e);
		}
		return dtoList;
	}

	@SuppressWarnings("unchecked")
	private List<SupplierImsGstinSectionSummaryDto> callGstr1Proc(SupplierImsEntityReqDto criteria) {
		List<SupplierImsGstinSectionSummaryDto> dtoList = new ArrayList<>();
		try {
			LOGGER.info("Calling stored procedure USP_SUPPLIER_IMS_GSTR1_1A_GSTIN for GSTIN: {}, ReturnPeriod: {}",
					criteria.getGstin(), criteria.getReturnPeriod());

			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("USP_SUPPLIER_IMS_GSTR1_1A_GSTIN");
			query.registerStoredProcedureParameter("IP_SUPPLIER_GSTIN", String.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("IP_RETURN_PERIOD", String.class, ParameterMode.IN);

			query.setParameter("IP_SUPPLIER_GSTIN", criteria.getGstin());
			query.setParameter("IP_RETURN_PERIOD",
					GenUtil.convertTaxPeriodToInt(criteria.getReturnPeriod()).toString());

			List<Object[]> resultList = query.getResultList();

			if (resultList == null || resultList.isEmpty()) {
				LOGGER.warn("No data returned from USP_SUPPLIER_IMS_GSTR1_1A_GSTIN");
				dtoList.add(createDefaultDto("GSTR1/1A"));
				return dtoList;
			}

			for (Object[] row : resultList) {
				SupplierImsGstinSectionSummaryDto dto = new SupplierImsGstinSectionSummaryDto();
				dto.setSectionType((String) row[0]);
				dto.setCount(toInt(row[1]));
				dto.setTotalTaxableValue(toBigDecimal(row[2]));
				dto.setTotalTax(toBigDecimal(row[3]));
				dto.setIgst(toBigDecimal(row[4]));
				dto.setCgst(toBigDecimal(row[5]));
				dto.setSgst(toBigDecimal(row[6]));
				dto.setCess(toBigDecimal(row[7]));
				dtoList.add(dto);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while calling USP_SUPPLIER_IMS_GSTR1_1A_GSTIN: {}", e.getMessage(), e);
			throw new AppException("Exception while calling USP_SUPPLIER_IMS_GSTR1_1A_GSTIN", e);
		}
		return dtoList;
	}

	private Integer toInt(Object obj) {
		return obj != null ? ((Number) obj).intValue() : 0;
	}

	private BigDecimal toBigDecimal(Object obj) {
		return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
	}

	private SupplierImsGstinSectionSummaryDto createDefaultDto(String sectionType) {
		SupplierImsGstinSectionSummaryDto dto = new SupplierImsGstinSectionSummaryDto();
		dto.setSectionType(sectionType);
		dto.setCount(0);
		dto.setTotalTaxableValue(BigDecimal.ZERO);
		dto.setTotalTax(BigDecimal.ZERO);
		dto.setIgst(BigDecimal.ZERO);
		dto.setCgst(BigDecimal.ZERO);
		dto.setSgst(BigDecimal.ZERO);
		dto.setCess(BigDecimal.ZERO);
		return dto;
	}
}