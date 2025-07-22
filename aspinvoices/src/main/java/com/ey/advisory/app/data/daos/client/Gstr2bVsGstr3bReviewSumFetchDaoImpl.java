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

import com.ey.advisory.app.data.repositories.client.Gstr2bVs3bStatusRepository;
import com.ey.advisory.app.docs.dto.Gstr2AVssGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2bVsGstr3bReviewSumFetchDaoImpl")
public class Gstr2bVsGstr3bReviewSumFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2bVs3bStatusRepository")
	Gstr2bVs3bStatusRepository gstr2bvs3bRepo;

	public List<Gstr2AVssGstr3bReviewSummaryRespDto> fetchGstr1VsGstr3bRecords(
			Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
		List<Long> entityId = reqDto.getEntityId();
		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bVsGstr3bReviewSummaryFetchDaoImpl->"
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
			List<Gstr2AVssGstr3bReviewSummaryRespDto> respDtos = convertGstr1RecordsIntoObject(
					Qlist);
			fillTheDataFromDataSecAttr(respDtos, gstinList, taxPeriodFrom,
					taxPeriodTo);
			if (respDtos == null || respDtos.size() <= 0) {
				DefaultValuesForEachGstinAndOneTaxPerod(respDtos, gstinList,
						taxPeriodFrom);
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
		List<Gstr2AVssGstr3bReviewSummaryRespDto> listDto = new ArrayList<>();
		for (String gstin : gstinList) {
			Gstr2AVssGstr3bReviewSummaryRespDto respDto = new Gstr2AVssGstr3bReviewSummaryRespDto();
			respDto.setGstin(gstin);
			respDto.setSupplies(gstin);
			respDto.setFormula("A");
			respDto.setIgst(BigDecimal.ZERO);
			respDto.setCgst(BigDecimal.ZERO);
			respDto.setSgst(BigDecimal.ZERO);
			respDto.setCess(BigDecimal.ZERO);
			if (GenUtil.convertTaxPeriodToInt(taxPeriodFrom) != null
					&& StringUtils.isNotEmpty(taxPeriodFrom)) {
				respDto.setTaxPeriod(GenUtil
						.convertTaxPeriodToInt(taxPeriodFrom).toString());
			}
			listDto.add(respDto);
		}
		respDtos.addAll(listDto);
		return respDtos;
	}

	private List<Gstr2AVssGstr3bReviewSummaryRespDto> fillTheDataFromDataSecAttr(
			List<Gstr2AVssGstr3bReviewSummaryRespDto> respDtos,
			List<String> gstinList, String taxPeriodFrom, String taxPeriodTo) {
		List<Gstr2AVssGstr3bReviewSummaryRespDto> listDto = new ArrayList<>();
		Set<String> dataGstinList = new HashSet<>();
		respDtos.forEach(dto -> dataGstinList.add(dto.getGstin()));
		respDtos.stream().forEach(dto -> {
			for (String gstin : gstinList) {
				StringBuilder queryStr = createCountQueryString(gstin,
						taxPeriodFrom, taxPeriodTo);
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
					Q.setParameter("gstin", gstin);
				}
				int count = 0;
				Object object = Q.getSingleResult();
				LOGGER.error(object.toString());
				if (object != null) {
					count = Integer.parseInt(object.toString());
				}
				LOGGER.error("Count : " + count);
				if (count == 0 || !dataGstinList.contains(gstin)) {
					LOGGER.error("Inside Loop");
					Gstr2AVssGstr3bReviewSummaryRespDto respDto = new Gstr2AVssGstr3bReviewSummaryRespDto();
					respDto.setGstin(gstin);
					respDto.setSupplies(dto.getSupplies());
					respDto.setFormula(dto.getFormula());
					respDto.setIgst(BigDecimal.ZERO);
					respDto.setCgst(BigDecimal.ZERO);
					respDto.setSgst(BigDecimal.ZERO);
					respDto.setCess(BigDecimal.ZERO);
					if (dto.getTaxPeriod() != null) {
						respDto.setTaxPeriod(dto.getTaxPeriod());
					}
					listDto.add(respDto);
				}
			}
		});
		respDtos.addAll(listDto);
		return respDtos;
	}

	private List<Gstr2AVssGstr3bReviewSummaryRespDto> convertGstr1RecordsIntoObject(
			List<Object[]> savedDataList) {
		List<Gstr2AVssGstr3bReviewSummaryRespDto> summaryList = new ArrayList<Gstr2AVssGstr3bReviewSummaryRespDto>();
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
				if (data[1] != null) {
					dto.setTaxPeriod(GenUtil
							.convertTaxPeriodToInt(String.valueOf(data[1]))
							.toString());
				}
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
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND GSTIN IN :sgstin");
		}
		if (StringUtils.isNotEmpty(taxPeriodFrom)
				&& StringUtils.isNotEmpty(taxPeriodTo)) {
			queryBuilder
					.append(" AND TO_INTEGER(RIGHT(TAXPERIOD,4)||LEFT(TAXPERIOD,2)) BETWEEN :taxPeriodFrom "
							+ " AND :taxPeriodTo");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append("SELECT GSTIN,TAXPERIOD,SECTION,"
				+ "IFNULL(SUM(IGST_AMOUNT),0) AS IGST,"
				+ " IFNULL(SUM(CGST_AMOUNT),0) AS CGST,"
				+ " IFNULL(SUM(SGST_AMOUNT),0) AS SGST,"
				+ " IFNULL(SUM(CESS_AMOUNT),0) AS CESS "
				+ " FROM  TBL_2BVS3B_COMPUTE_2B WHERE IS_DELETE = FALSE ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" GROUP BY GSTIN,TAXPERIOD,DESCRIPTION,SECTION"
				+ " UNION ALL SELECT GSTIN,TAXPERIOD,SECTION,"
				+ " IFNULL(SUM(IGST_AMOUNT),0) AS IGST,"
				+ " IFNULL(SUM(CGST_AMOUNT),0) AS CGST,"
				+ " IFNULL(SUM(SGST_AMOUNT),0) AS SGST,"
				+ " IFNULL(SUM(CESS_AMOUNT),0) AS CESS"
				+ " FROM TBL_2BVS3B_COMPUTE_3B WHERE IS_DELETE = FALSE");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" GROUP BY GSTIN,TAXPERIOD,SECTION"
				+ " ORDER BY GSTIN,TAXPERIOD,SECTION");

		LOGGER.debug("Gstr2Bvs3b Query from database is-->" + bufferString);

		return bufferString;
	}

	public StringBuilder createCountQueryString(String gstin,
			String taxPeriodFrom, String taxPeriodTo) {

		StringBuilder queryBuilder = new StringBuilder();
		if (gstin != null) {
			queryBuilder.append(" AND GSTIN =:gstin");
		}
		if (StringUtils.isNotEmpty(taxPeriodFrom)
				&& StringUtils.isNotEmpty(taxPeriodTo)) {
			queryBuilder
					.append(" AND TO_INTEGER(RIGHT(TAXPERIOD,4)||LEFT(TAXPERIOD,2)) BETWEEN :taxPeriodFrom "
							+ " AND :taxPeriodTo");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append("SELECT SUM(COUNT_DATA) FROM( "
				+ "SELECT COUNT(GSTIN) AS COUNT_DATA"
				+ " FROM TBL_2BVS3B_COMPUTE_2B WHERE IS_DELETE = FALSE");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" UNION ALL SELECT COUNT(GSTIN) AS COUNT_DATA"
				+ " FROM TBL_2BVS3B_COMPUTE_3B WHERE IS_DELETE = FALSE");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" )");
		LOGGER.debug("Gstr2Bvs3b Query from database is-->" + bufferString);

		return bufferString;
	}

}
