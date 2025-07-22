package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bProcessSummaryFinalRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr2bVsGstr3bPrSummaryFetchDaoImpl")
public class Gstr2bVsGstr3bPrSummaryFetchDaoImpl {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bVsGstr3bPrSummaryFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	public List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> fetchGstr2aVsGstr3bRecords(
			Gstr1VsGstr3bProcessSummaryReqDto reqDto) {

		List<Long> entityId = reqDto.getEntityId();
		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
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

		List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> finalDtoList = new ArrayList<>();
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
			LOGGER.error("Qlist --------------------------> {}" + Qlist);
			finalDtoList = convertGstr1vs3bRecordsIntoObject(Qlist, reqDto);

			LOGGER.debug("finalDtoList -->" + finalDtoList);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> convertGstr1vs3bRecordsIntoObject(
			List<Object[]> savedDataList,
			Gstr1VsGstr3bProcessSummaryReqDto reqDto) throws Exception {
		List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> summaryList = new ArrayList<Gstr2aVsGstr3bProcessSummaryFinalRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr2aVsGstr3bProcessSummaryFinalRespDto dto = new Gstr2aVsGstr3bProcessSummaryFinalRespDto();
				dto.setGstin(String.valueOf(data[0]));
				dto.setReconStatus(String.valueOf(data[1]));
				dto.setGstr3BIgst((BigDecimal)data[5]);
				dto.setGstr3BCgst((BigDecimal)data[6]);
				dto.setGstr3BSgst((BigDecimal)data[7]);
				dto.setGstr3BCess((BigDecimal)data[8]);
				dto.setGstr2AIgst((BigDecimal)data[9]);
				dto.setGstr2ACgst((BigDecimal)data[10]);
				dto.setGstr2ASgst((BigDecimal)data[11]);
				dto.setGstr2ACess((BigDecimal)data[12]);
				dto.setDiffIgst((BigDecimal)data[13]);
				dto.setDiffCgst((BigDecimal)data[14]);
				dto.setDiffSgst((BigDecimal)data[15]);
				dto.setDiffCess((BigDecimal)data[16]);
				if (data[2] == null || data[2]  == "null") {
	                dto.setReconTimestamp("");
	            } else {
	            	Timestamp dt = (Timestamp) data[2];
	                LocalDateTime dateTimeFormatter = EYDateUtil
	                        .toISTDateTimeFromUTC(dt.toLocalDateTime());
	                DateTimeFormatter FOMATTER = DateTimeFormatter
	                        .ofPattern("dd-MM-yyyy : HH:mm:ss");
	                String newdate = FOMATTER.format(dateTimeFormatter);

	                dto.setReconTimestamp(newdate);
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

		StringBuilder queryBuilder1 = new StringBuilder();
		StringBuilder queryBuilder = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder1.append(" GSTIN IN :sgstin ");
			queryBuilder.append(" AND GSTIN IN :sgstin");
		}

		if (StringUtils.isNotEmpty(taxPeriodFrom)
				&& StringUtils.isNotEmpty(taxPeriodTo)) {
			queryBuilder1.append(" AND FROM_TAX_PERIOD >=:taxPeriodFrom AND "
					+ "TO_TAX_PERIOD <=:taxPeriodTo ");
			queryBuilder
					.append(" AND TO_INTEGER(RIGHT(TAXPERIOD,4)||LEFT(TAXPERIOD,2)) BETWEEN :taxPeriodFrom and "
							+ ":taxPeriodTo");
		}
		String condition = queryBuilder.toString();
		String condition1 = queryBuilder1.toString();

		StringBuilder bufferString = new StringBuilder();
		bufferString
				.append("SELECT GSTIN,(SELECT MAX(STATUS) FROM TBL_2BVS3B_COMPUTE WHERE " );
		if (!condition1.equals("")) {
			bufferString.append(condition1);
		}
		bufferString.append(" AND IS_DELETE = FALSE) AS RECON_STATUS,(SELECT MAX(COMPLETED_ON) FROM "
				+ "TBL_2BVS3B_COMPUTE WHERE ");
		if (!condition1.equals("")) {
			bufferString.append(condition1);
		}
		bufferString.append(" AND IS_DELETE = FALSE) AS RECON_TIME,NULL AS GSTR3B_STATUS,NULL AS GSTR2B_STATUS,"
				+ " SUM(IGST_3B) AS IGST_3B,SUM(CGST_3B) AS CGST_3B,"
				+ " SUM(SGST_3B) AS SGST_3B,SUM(CESS_3B) AS CESS_3B,"
				+ " SUM(IGST_2B) AS IGST_2B,SUM(CGST_2B) AS CGST_2B,"
				+ " SUM(SGST_2B) AS SGST_2B,SUM(CESS_2B) AS CESS_2B,"
				+ " SUM(IGST_2B) - SUM(IGST_3B) AS DIFF_IGST,"
				+ " SUM(SGST_2B) - SUM(SGST_3B) AS DIFF_SGST,"
				+ " SUM(CGST_2B) - SUM(CGST_3B) AS DIFF_CGST,"
				+ " SUM(CESS_2B) - SUM(CESS_3B) AS DIFF_CESS"
				+ " FROM ( SELECT GSTIN,0 AS IGST_3B,0 AS CGST_3B,"
				+ " 0 AS SGST_3B,0 AS CESS_3B,SUM(IGST_AMOUNT) AS IGST_2B,"
				+ " SUM(CGST_AMOUNT) AS CGST_2B,SUM(SGST_AMOUNT) AS SGST_2B,"
				+ " SUM(CESS_AMOUNT) AS CESS_2B FROM  TBL_2BVS3B_COMPUTE_2B"
				+ " WHERE IS_DELETE = FALSE ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" AND SECTION = 'A' GROUP BY GSTIN UNION ALL SELECT GSTIN,"
				+ " SUM(IGST_AMOUNT) AS IGST_3B,SUM(CGST_AMOUNT) AS CGST_3B,"
				+ " SUM(SGST_AMOUNT) AS SGST_3B,SUM(CESS_AMOUNT) AS CESS_3B,"
				+ " 0 AS IGST_2B,0 AS CGST_2B,0 AS SGST_2B,0 AS CESS_2B"
				+ " FROM  TBL_2BVS3B_COMPUTE_3B WHERE IS_DELETE = FALSE");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" AND SECTION='B' GROUP BY GSTIN ) GROUP BY GSTIN");
		LOGGER.debug("Gstr1vs3b Query from database is-->" + bufferString);

		LOGGER.error("bufferString-------------------------->" + bufferString);
		return bufferString;
	}

}
