package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2avs3BComputeRepository;
import com.ey.advisory.app.docs.dto.Gstr2AVssGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("Gstr2AVsGstr3bReviewSumFetchDaoImpl")
public class Gstr2AVsGstr3bReviewSumFetchDaoImpl {
	

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2avs3BComputeRepository")
	Gstr2avs3BComputeRepository gstr2avs3bRepo;
	public List<Gstr2AVssGstr3bReviewSummaryRespDto> fetchGstr1VsGstr3bRecords(
			Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
		 List<Long> entityId = reqDto.getEntityId();
	        String taxPeriodFrom = reqDto.getTaxPeriodFrom();
	        String taxPeriodTo = reqDto.getTaxPeriodTo();
	        Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("Gstr2aVsGstr3bReviewSummaryFetchDaoImpl->"
	                    + "and selected criteria are:: entityId-> {}, "
	                    + "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
	                    reqDto);
	        }
	        String sgstin = null;
	        List<String> gstinList = null;
	        if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
	                && dataSecAttrs.size() > 0) {
	            for (String key : dataSecAttrs.keySet()) {

	                if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
	                    sgstin = key;
	                    gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
	                }
	            }
	        }
		 StringBuilder queryStr = createQueryString(entityId, gstinList,
	                taxPeriodFrom, taxPeriodTo, dataSecAttrs, sgstin);
		LOGGER.debug("outQueryStr-->" + queryStr);
		List<Gstr2AVssGstr3bReviewSummaryRespDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (StringUtils.isNotEmpty(taxPeriodFrom)
					&& StringUtils.isNotEmpty(taxPeriodTo)) {
				Q.setParameter("taxPeriodFrom",
						GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
				Q.setParameter("taxPeriodTo",
						GenUtil.convertTaxPeriodToInt(taxPeriodTo));
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			LOGGER.error("bufferString-------------------------->" + Qlist);
			List<Gstr2AVssGstr3bReviewSummaryRespDto> respDtos = convertGstr1RecordsIntoObject(
					Qlist);
			fillTheDataFromDataSecAttr(respDtos, gstinList, taxPeriodFrom,
					taxPeriodTo);
			if(respDtos==null || respDtos.size() <=0){
				DefaultValuesForEachGstinAndOneTaxPerod(respDtos,gstinList,taxPeriodFrom);
			}
			finalDtoList.addAll(respDtos);
			LOGGER.debug("Data list from database is-->" + Qlist);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr2AVssGstr3bReviewSummaryRespDto> DefaultValuesForEachGstinAndOneTaxPerod(
			List<Gstr2AVssGstr3bReviewSummaryRespDto> respDtos,
			List<String> gstinList, String taxPeriodFrom) {
		List<Gstr2AVssGstr3bReviewSummaryRespDto> listDto=new ArrayList<>();
			for (String gstin : gstinList) {
					Gstr2AVssGstr3bReviewSummaryRespDto respDto 
					          = new Gstr2AVssGstr3bReviewSummaryRespDto();
					respDto.setGstin(gstin);
					respDto.setSupplies(gstin);
					respDto.setFormula("A");
					respDto.setIgst(BigDecimal.ZERO);
					respDto.setCgst(BigDecimal.ZERO);
					respDto.setSgst(BigDecimal.ZERO);
					respDto.setCess(BigDecimal.ZERO);
					respDto.setTaxPeriod(taxPeriodFrom);
					listDto.add(respDto);
			}
		respDtos.addAll(listDto);
		return respDtos;
	}

	
	private List<Gstr2AVssGstr3bReviewSummaryRespDto> fillTheDataFromDataSecAttr(
			List<Gstr2AVssGstr3bReviewSummaryRespDto> respDtos,
			List<String> gstinList, String taxPeriodFrom, String taxPeriodTo) {
		List<Gstr2AVssGstr3bReviewSummaryRespDto> listDto=new ArrayList<>();
		Set<String> dataGstinList = new HashSet<>();
		respDtos.forEach(dto -> dataGstinList.add(dto.getGstin()));
		respDtos.stream().forEach(dto -> {
			for (String gstin : gstinList) {
				int count = gstr2avs3bRepo.gstinCount(gstin, taxPeriodFrom,
						taxPeriodTo);
				LOGGER.error("Count : "+ count);
				if (count == 0 || !dataGstinList.contains(gstin)) {
					Gstr2AVssGstr3bReviewSummaryRespDto respDto = new Gstr2AVssGstr3bReviewSummaryRespDto();
					respDto.setGstin(gstin);
					respDto.setSupplies(dto.getSupplies());
					respDto.setFormula(dto.getFormula());
					respDto.setIgst(BigDecimal.ZERO);
					respDto.setCgst(BigDecimal.ZERO);
					respDto.setSgst(BigDecimal.ZERO);
					respDto.setCess(BigDecimal.ZERO);
					respDto.setTaxPeriod(dto.getTaxPeriod());
					listDto.add(respDto);
				}
			}
		});
		respDtos.addAll(listDto);
		return respDtos;
	}

	private List<Gstr2AVssGstr3bReviewSummaryRespDto> convertGstr1RecordsIntoObject(
			List<Object[]> savedDataList) {
		List<Gstr2AVssGstr3bReviewSummaryRespDto> summaryList 
		              = new ArrayList<Gstr2AVssGstr3bReviewSummaryRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr2AVssGstr3bReviewSummaryRespDto dto = new Gstr2AVssGstr3bReviewSummaryRespDto();
				dto.setSupplies(String.valueOf(data[2]));
				dto.setFormula(String.valueOf(data[2]));
				dto.setIgst((BigDecimal) data[3]);
				dto.setCgst((BigDecimal) data[4]);
				dto.setSgst((BigDecimal) data[5]);
				dto.setCess((BigDecimal) data[6]);
				dto.setGstin(String.valueOf(data[0]));
				dto.setTaxPeriod(String.valueOf(data[1]));
				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	public StringBuilder createQueryString(List<Long> entityId,
            List<String> gstinList, String taxPeriodFrom, String taxPeriodTo,
            Map<String, List<String>> dataSecAttrs, String sgstin) {

    	if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (StringUtils.isNotEmpty(taxPeriodFrom)
				&& StringUtils.isNotEmpty(taxPeriodTo)) {
			queryBuilder
					.append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom "
							+ " AND :taxPeriodTo");
		}
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND GSTIN IN :sgstin");
		}
		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString
		.append	("select GSTIN,DERIVED_RET_PERIOD,SECTION_NAME,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
		+ " IFNULL(SUM(CGST_AMT),0) AS CGST,"
		+ " IFNULL(SUM(SGST_AMT),0) AS SGST,"
		+ " IFNULL(SUM(CESS_AMT),0) AS CESS "
		+ " FROM GSTR2A_VS_3B_COMPUTE where IS_ACTIVE = TRUE "
		+ "  AND  SECTION_NAME IN ('A','A1','A2','A3','B','B1','B2','B3','B4',"
		+ "'B5','B6','B7','B8','C') ");
		 if (!condition.equals("")) {
	            bufferString.append(condition);
	        }
	        bufferString.append(" GROUP BY GSTIN,SECTION_NAME,DERIVED_RET_PERIOD");
	        LOGGER.debug("Gstr2Avs3b Query from database is-->" + bufferString);

	        LOGGER.error("bufferString-------------------------->" + bufferString);
		
		return bufferString;
	}

}
