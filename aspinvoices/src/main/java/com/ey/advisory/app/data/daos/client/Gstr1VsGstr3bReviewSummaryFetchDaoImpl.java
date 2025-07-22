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

import com.ey.advisory.app.data.repositories.client.Gstr1vs3BComputeRepository;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("Gstr1VsGstr3bReviewSummaryFetchDaoImpl")
public class Gstr1VsGstr3bReviewSummaryFetchDaoImpl {
   
    @PersistenceContext(unitName = "clientDataUnit")
    private EntityManager entityManager;

    @Autowired
    @Qualifier("Gstr1vs3BComputeRepository")
    Gstr1vs3BComputeRepository gstr1vs3bRepo;

    public List<Gstr1VsGstr3bReviewSummaryRespDto> fetchGstr1VsGstr3bRecords(
            Gstr1VsGstr3bProcessSummaryReqDto reqDto) {

        List<Long> entityId = reqDto.getEntityId();
        String taxPeriodFrom = reqDto.getTaxPeriodFrom();
        String taxPeriodTo = reqDto.getTaxPeriodTo();
        
        Long configId = reqDto.getConfigId();
        
        Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Gstr1VsGstr3bReviewSummaryFetchDaoImpl->"
                    + "and selected criteria are:: entityId-> {}, "
                    + "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
                    reqDto);
        }
        String profitCenter = null, plant = null;
        String sales = null, division = null, location = null;
        String distChannel = null, ud1 = null;
        String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
        String ud6 = null, sgstin = null;
        String cgstin = null;

        List<String> gstinList = null;
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
        List<String> ud6List = null;

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
                taxPeriodFrom, taxPeriodTo, configId, dataSecAttrs, profitCenter, sgstin,
                cgstin, plant, division, location, sales, distChannel, ud1, ud2,
                ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
                locationList, distList, ud1List, ud2List, ud3List, ud4List,
                ud5List, ud6List);

        LOGGER.debug("outQueryStr-->" + queryStr);

        List<Gstr1VsGstr3bReviewSummaryRespDto> finalDtoList = new ArrayList<>();
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
            if (configId != null ) {
                Q.setParameter("configId", configId);
            }

            @SuppressWarnings("unchecked")
            List<Object[]> Qlist = Q.getResultList();
            LOGGER.error("bufferString-------------------------->" + Qlist);
            List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos = convertGstr1RecordsIntoObject(
                    Qlist);
            fillTheDataFromDataSecAttr(respDtos, gstinList, taxPeriodFrom,
                    taxPeriodTo);
            if(respDtos==null || respDtos.size() <=0){
				DefaultValuesForEachGstinAndOneTaxPerod(respDtos,gstinList,String.valueOf(GenUtil.convertTaxPeriodToInt(taxPeriodFrom)));
			}
            finalDtoList.addAll(respDtos);
            LOGGER.debug("Data list from database is-->" + Qlist);

        } catch (Exception e) {
            LOGGER.error("Error in data process ->", e);
            throw new AppException(e);
        }
        return finalDtoList;
    }

    private List<Gstr1VsGstr3bReviewSummaryRespDto> DefaultValuesForEachGstinAndOneTaxPerod(List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos,
			List<String> gstinList, String taxPeriodFrom) {
		List<Gstr1VsGstr3bReviewSummaryRespDto> listDto=new ArrayList<>();
			for (String gstin : gstinList) {
				Gstr1VsGstr3bReviewSummaryRespDto respDto 
					          = new Gstr1VsGstr3bReviewSummaryRespDto();
					respDto.setGstin(gstin);
					respDto.setSupplies("A");
					respDto.setFormula("A");
					respDto.setTaxableValue(BigDecimal.ZERO);
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

	private List<Gstr1VsGstr3bReviewSummaryRespDto> fillTheDataFromDataSecAttr(
            List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos,
            List<String> gstinList, String taxPeriodFrom, String taxPeriodTo) {
        List<Gstr1VsGstr3bReviewSummaryRespDto> finalRespDtos = new ArrayList<>();
        Set<String> dataGstinList = new HashSet<>();
		respDtos.forEach(dto -> dataGstinList.add(dto.getGstin()));
        respDtos.stream().forEach(dto -> {
            for (String gstin : gstinList) {
                int derivedRetPerFrom = GenUtil
                        .convertTaxPeriodToInt(taxPeriodFrom);
                int derivedRetPerTo = GenUtil
                        .convertTaxPeriodToInt(taxPeriodTo);

                int count = gstr1vs3bRepo.gstinCount(gstin, derivedRetPerFrom,
                        derivedRetPerTo);
                if (count == 0 || !dataGstinList.contains(gstin)) {
                    Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
                    respDto.setGstin(gstin);
                    respDto.setSupplies(dto.getSupplies());
                    respDto.setFormula(dto.getFormula());
                    respDto.setTaxableValue(BigDecimal.ZERO);
                    respDto.setIgst(BigDecimal.ZERO);
                    respDto.setCgst(BigDecimal.ZERO);
                    respDto.setSgst(BigDecimal.ZERO);
                    respDto.setCess(BigDecimal.ZERO);
                    respDto.setTaxPeriod(dto.getTaxPeriod());
                    finalRespDtos.add(respDto);
                }
            }
        });
        respDtos.addAll(finalRespDtos);
        return respDtos;
    }

    private List<Gstr1VsGstr3bReviewSummaryRespDto> convertGstr1RecordsIntoObject(
            List<Object[]> savedDataList) {
        List<Gstr1VsGstr3bReviewSummaryRespDto> summaryList = new ArrayList<Gstr1VsGstr3bReviewSummaryRespDto>();
        if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
            for (Object[] data : savedDataList) {
                Gstr1VsGstr3bReviewSummaryRespDto dto = new Gstr1VsGstr3bReviewSummaryRespDto();
                dto.setSupplies(String.valueOf(data[0]));
                dto.setFormula(String.valueOf(data[0]));
                dto.setTaxableValue((BigDecimal) data[1]);
                dto.setIgst((BigDecimal) data[2]);
                dto.setCgst((BigDecimal) data[3]);
                dto.setSgst((BigDecimal) data[4]);
                dto.setCess((BigDecimal) data[5]);
                dto.setGstin(String.valueOf(data[6]));
                dto.setTaxPeriod(String.valueOf(data[7]));

                summaryList.add(dto);
            }
        }
        return summaryList;

    }

    public StringBuilder createQueryString(List<Long> entityId,
            List<String> gstinList, String taxPeriodFrom, String taxPeriodTo, Long configId,
            Map<String, List<String>> dataSecAttrs, String profitCenter,
            String sgstin, String cgstin, String plant, String division,
            String location, String sales, String distChannel, String ud1,
            String ud2, String ud3, String ud4, String ud5, String ud6,
            List<String> pcList, List<String> plantList, List<String> salesList,
            List<String> divisionList, List<String> locationList,
            List<String> distList, List<String> ud1List, List<String> ud2List,
            List<String> ud3List, List<String> ud4List, List<String> ud5List,
            List<String> ud6List) {

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
        
        if(configId!=null)
        {
        	queryBuilder.append(" AND RECON_REPORT_CONFIG_ID =:configId ");
        }
        String condition = queryBuilder.toString();
        StringBuilder bufferString = new StringBuilder();
        bufferString
                .append("select SUB_SECTION_NAME,IFNULL(SUM(TAXABLE_VALUE),0)"
                        + " AS TAXABLE_VALUE ,IFNULL(SUM(IGST_AMT),0) AS "
                        + "IGST_AMT,IFNULL(SUM(CGST_AMT),0) AS CGST_AMT ,"
                        + "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,IFNULL"
                        + "(SUM(CESS_AMT),0)  AS CESS_AMT,GSTIN,DERIVED_RET_PERIOD FROM "
                        + "GSTR1_VS_3B_COMPUTE WHERE "
                        + "SUB_SECTION_NAME IN('A','A1','A2','A3', 'B','B1', 'B2','B3','B4',"
                        + "'B5', 'B6','B7','B8-B9','B10', 'B11','B12','B13','B14', "
                        + "'B15','B16-B17','B18','B19','D1','D2','E1','E2','E3','H','I') "
                        + "AND IS_ACTIVE = TRUE ");
        if (!condition.equals("")) {
            bufferString.append(condition);
        }
        bufferString
                .append(" GROUP BY SUB_SECTION_NAME,GSTIN,DERIVED_RET_PERIOD ");
        if(LOGGER.isDebugEnabled()){
        	LOGGER.debug("gstr1Vs3b download report query{}",bufferString);
        }
        return bufferString;
    }

}
