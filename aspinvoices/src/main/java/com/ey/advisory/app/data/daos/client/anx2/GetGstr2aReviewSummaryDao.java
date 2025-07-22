package com.ey.advisory.app.data.daos.client.anx2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1a.GetGstr2aReviewSummaryRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("GetGstr2aReviewSummaryDao")
public class GetGstr2aReviewSummaryDao {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr2aReviewSummaryDao.class);
	private static final String GSTIN = "GSTIN";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<GetGstr2aReviewSummaryRespDto> loadSummaryDetails(
			Gstr2AProcessedRecordsReqDto criteria) {
		List<Long> entityId = criteria.getEntityId();
		String taxPeriod = criteria.getRetunPeriod();
		String fromPeriod = criteria.getFromPeriod();
		String toPeriod = criteria.getToPeriod();
		List<String> tableType = criteria.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());
		List<String> docType = criteria.getDocType();
		List<String> docTypeUpperCase = docType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());
		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;

		try {
			if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
					&& dataSecAttrs.size() > 0) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(GSTIN)) {
						cgstin = key;
						gstinList = dataSecAttrs.get(GSTIN);
					}
				}
			}

			StringBuilder queryStr = createQueryString(gstinList, fromPeriod,
					toPeriod, cgstin, tableTypeUpperCase, docTypeUpperCase,
					dataSecAttrs);

			Query q = entityManager.createNativeQuery(queryStr.toString());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("query --> {} ", q);
			}

			if (CollectionUtils.isNotEmpty(gstinList)) {
				q.setParameter("gstinList", gstinList);
			}

			// using fromPeriod and toPeriod
			if (StringUtils.isNotBlank(fromPeriod)
					&& StringUtils.isNotBlank(toPeriod)) {
				q.setParameter("fromPeriod",
						GenUtil.convertTaxPeriodToInt(fromPeriod));
				q.setParameter("toPeriod",
						GenUtil.convertTaxPeriodToInt(toPeriod));
			}

			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				q.setParameter("tableTypeUpperCase", tableTypeUpperCase);
			}
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {

				q.setParameter("docTypeUpperCase", docTypeUpperCase);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			List<GetGstr2aReviewSummaryRespDto> respDtos = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			getAbsValue(respDtos);
			return respDtos;
		} catch (Exception e) {
			throw new AppException("Error in data process ->", e);
		}
	}

	private StringBuilder createQueryString(List<String> gstinList,
			String fromPeriod, String toPeriod, String cgstin,
			List<String> tableTypeUpperCase, List<String> docTypeUpperCase,
			Map<String, List<String>> dataSecAttrs) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(GSTIN)) {
					cgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			queryBuilder.append(
					" WHERE DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");
		}

		if (CollectionUtils.isNotEmpty(gstinList)) {
			queryBuilder.append(" AND GSTIN IN (:gstinList) ");
		}
		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			queryBuilder.append(" AND TAX_DOC_TYPE IN (:tableTypeUpperCase) ");
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			queryBuilder.append(" AND DOC_TYPE IN (:docTypeUpperCase) ");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append("");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2a Query from database is--> {} ", bufferString);
		}

		bufferString.append(
				" SELECT TAX_DOC_TYPE, DOC_TYPE, GSTIN, SUM(COUNT) COUNT, SUM(INV_VALUE) INV_VALUE, ");
		bufferString.append(
				"SUM(TAXABLE_VALUE)TAXABLE_VALUE, SUM(TOT_TAX)TOT_TAX, SUM(IGST_AMT)IGST_AMT, SUM(CGST_AMT)CGST_AMT, ");
		bufferString.append(
				"SUM(SGST_AMT)SGST_AMT, SUM(CESS_AMT)CESS_AMT FROM ( SELECT CGSTIN AS GSTIN,");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD,'B2B' AS TAX_DOC_TYPE, 'INV' AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, IFNULL(SUM(ITM.TAX_VALUE ),0) ");
		bufferString.append(
				"AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)");
		bufferString.append(
				"+IFNULL(SUM(ITM.SGST_AMT), 0)+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append(
				"IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_B2B_HEADER HDR INNER JOIN GETGSTR2A_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");//B2b
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"GROUP BY CGSTIN,HDR.DERIVED_RET_PERIOD UNION ALL SELECT CGSTIN AS GSTIN,HDR.DERIVED_RET_PERIOD, ");
		bufferString.append(
				"'B2B' AS TAX_DOC_TYPE, 'TOTAL' AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, IFNULL(SUM(ITM.TAX_VALUE ),0) ");
		bufferString.append(
				"AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)");
		bufferString.append(
				"+IFNULL(SUM(ITM.SGST_AMT),0)+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append(
				"IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_B2B_HEADER HDR INNER JOIN GETGSTR2A_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");//B2b item
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"GROUP BY CGSTIN,HDR.DERIVED_RET_PERIOD UNION ALL SELECT CGSTIN AS GSTIN,HDR.DERIVED_RET_PERIOD,");
		bufferString.append(
				"'B2BA' AS TAX_DOC_TYPE, 'RNV' AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, IFNULL(SUM(ITM.TAX_VALUE ),0) ");
		bufferString.append(
				"AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)");
		bufferString.append(
				"+IFNULL(SUM(ITM.SGST_AMT),0)+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) ");
		bufferString.append(
				"AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_B2BA_HEADER HDR ");
		bufferString.append(
				"INNER JOIN GETGSTR2A_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"GROUP BY CGSTIN,HDR.DERIVED_RET_PERIOD UNION ALL SELECT CGSTIN AS GSTIN,HDR.DERIVED_RET_PERIOD,");
		bufferString.append(
				"'B2BA' AS TAX_DOC_TYPE,'TOTAL' AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, IFNULL(SUM(ITM.TAX_VALUE ),0) ");
		bufferString.append(
				"AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)");
		bufferString.append(
				"+IFNULL(SUM(ITM.SGST_AMT),0)+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append(
				"IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_B2BA_HEADER HDR INNER JOIN GETGSTR2A_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");//B2ba
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString
				.append("GROUP BY CGSTIN,HDR.DERIVED_RET_PERIOD UNION ALL ");
		
		
		bufferString.append(" SELECT CGSTIN AS GSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" ,'ECOM' AS TAX_DOC_TYPE ");
		bufferString.append(" ,'INV' AS DOC_TYPE ");
		bufferString.append(" ,COUNT(DISTINCT HDR.ID) AS COUNT ");
		bufferString.append(" ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0) + IFNULL(ITM.TAX_VALUE, 0)) INV_VALUE ");
		bufferString.append(" ,IFNULL(SUM(ITM.TAX_VALUE), 0) AS TAXABLE_VALUE ");
		bufferString.append(" ,(IFNULL(SUM(ITM.IGST_AMT), 0) + IFNULL(SUM(ITM.CGST_AMT), 0) + IFNULL(SUM(ITM.SGST_AMT), 0) + IFNULL(SUM(ITM.CESS_AMT), 0)) AS TOT_TAX ");
		bufferString.append(" ,IFNULL(SUM(ITM.IGST_AMT), 0) AS IGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CGST_AMT), 0) AS CGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.SGST_AMT), 0) AS SGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CESS_AMT), 0) AS CESS_AMT ");
		bufferString.append(" FROM GETGSTR2A_ECOM_HEADER HDR ");
		bufferString.append(" INNER JOIN GETGSTR2A_ECOM_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(" AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		bufferString.append(" WHERE IS_DELETE = FALSE ");
		bufferString.append(" GROUP BY CGSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" UNION ALL ");
		bufferString.append(" SELECT CGSTIN AS GSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" ,'ECOM' AS TAX_DOC_TYPE ");
		bufferString.append(" ,'TOTAL' AS DOC_TYPE ");
		bufferString.append(" ,COUNT(DISTINCT HDR.ID) AS COUNT ");
		bufferString.append(" ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0) + IFNULL(ITM.TAX_VALUE, 0)) INV_VALUE ");
		bufferString.append(" ,IFNULL(SUM(ITM.TAX_VALUE), 0) AS TAXABLE_VALUE ");
		bufferString.append(" ,(IFNULL(SUM(ITM.IGST_AMT), 0) + IFNULL(SUM(ITM.CGST_AMT), 0) + IFNULL(SUM(ITM.SGST_AMT), 0) + IFNULL(SUM(ITM.CESS_AMT), 0)) AS TOT_TAX ");
		bufferString.append(" ,IFNULL(SUM(ITM.IGST_AMT), 0) AS IGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CGST_AMT), 0) AS CGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.SGST_AMT), 0) AS SGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CESS_AMT), 0) AS CESS_AMT ");
		bufferString.append(" FROM GETGSTR2A_ECOM_HEADER HDR ");
		bufferString.append(" INNER JOIN GETGSTR2A_ECOM_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(" AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		bufferString.append(" WHERE IS_DELETE = FALSE ");
		bufferString.append(" GROUP BY CGSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" UNION ALL ");
		bufferString.append(" SELECT CGSTIN AS GSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" ,'ECOMA' AS TAX_DOC_TYPE ");
		bufferString.append(" ,'RNV' AS DOC_TYPE ");
		bufferString.append(" ,COUNT(DISTINCT HDR.ID) AS COUNT ");
		bufferString.append(" ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0) + IFNULL(ITM.TAX_VALUE, 0)) INV_VALUE ");
		bufferString.append(" ,IFNULL(SUM(ITM.TAX_VALUE), 0) AS TAXABLE_VALUE ");
		bufferString.append(" ,(IFNULL(SUM(ITM.IGST_AMT), 0) + IFNULL(SUM(ITM.CGST_AMT), 0) + IFNULL(SUM(ITM.SGST_AMT), 0) + IFNULL(SUM(ITM.CESS_AMT), 0)) AS TOT_TAX ");
		bufferString.append(" ,IFNULL(SUM(ITM.IGST_AMT), 0) AS IGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CGST_AMT), 0) AS CGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.SGST_AMT), 0) AS SGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CESS_AMT), 0) AS CESS_AMT ");
		bufferString.append(" FROM GETGSTR2A_ECOMA_HEADER HDR ");
		bufferString.append(" INNER JOIN GETGSTR2A_ECOMA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(" AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		bufferString.append(" WHERE IS_DELETE = FALSE ");
		bufferString.append(" GROUP BY CGSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" UNION ALL ");
		bufferString.append(" SELECT CGSTIN AS GSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" ,'ECOMA' AS TAX_DOC_TYPE ");
		bufferString.append(" ,'TOTAL' AS DOC_TYPE ");
		bufferString.append(" ,COUNT(DISTINCT HDR.ID) AS COUNT ");
		bufferString.append(" ,SUM(IFNULL(ITM.IGST_AMT, 0) + IFNULL(ITM.CGST_AMT, 0) + IFNULL(ITM.SGST_AMT, 0) + IFNULL(ITM.CESS_AMT, 0) + IFNULL(ITM.TAX_VALUE, 0)) INV_VALUE ");
		bufferString.append(" ,IFNULL(SUM(ITM.TAX_VALUE), 0) AS TAXABLE_VALUE ");
		bufferString.append(" ,(IFNULL(SUM(ITM.IGST_AMT), 0) + IFNULL(SUM(ITM.CGST_AMT), 0) + IFNULL(SUM(ITM.SGST_AMT), 0) + IFNULL(SUM(ITM.CESS_AMT), 0)) AS TOT_TAX ");
		bufferString.append(" ,IFNULL(SUM(ITM.IGST_AMT), 0) AS IGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CGST_AMT), 0) AS CGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.SGST_AMT), 0) AS SGST_AMT ");
		bufferString.append(" ,IFNULL(SUM(ITM.CESS_AMT), 0) AS CESS_AMT ");
		bufferString.append(" FROM GETGSTR2A_ECOMA_HEADER HDR ");
		bufferString.append(" INNER JOIN GETGSTR2A_ECOMA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(" AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		bufferString.append(" WHERE IS_DELETE = FALSE ");
		bufferString.append(" GROUP BY CGSTIN ");
		bufferString.append(" ,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" UNION ALL ");
		
		
		
		
		bufferString.append(
				"SELECT CTIN AS GSTIN,HDR.DERIVED_RET_PERIOD,'CDN' AS TAX_DOC_TYPE ,(CASE WHEN NOTE_TYPE IN ('C') THEN 'CR' WHEN NOTE_TYPE IN ('D') ");
		bufferString.append(
				"THEN 'DR' END) AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, IFNULL(SUM(ITM.TAX_VALUE ),0) AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0) ");
		bufferString.append(
				"+IFNULL(SUM(ITM.CGST_AMT),0)+IFNULL(SUM(ITM.SGST_AMT),0)+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append(
				"IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_CDN_HEADER HDR INNER JOIN ");
		bufferString.append(
				"GETGSTR2A_CDN_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"AND NOTE_TYPE IN ('C','D') GROUP BY CTIN,HDR.DERIVED_RET_PERIOD,NOTE_TYPE UNION ALL SELECT CTIN AS GSTIN,HDR.DERIVED_RET_PERIOD, ");
		bufferString.append(
				"'CDN' AS TAX_DOC_TYPE,'TOTAL' AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, IFNULL(CASE WHEN NOTE_TYPE IN ('D') ");
		bufferString.append(
				"THEN SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0))END ,0) ");
		bufferString.append(
				"-IFNULL(CASE WHEN NOTE_TYPE IN ('C') THEN SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0))END ,0) AS INV_VALUE, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.TAX_VALUE END),0) ");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.TAX_VALUE END),0) AS TAXABLE_VALUE, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') ");
		bufferString.append(
				"THEN IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)- ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0) END),0) AS TOT_TAX, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.IGST_AMT END),0) ");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.IGST_AMT END),0) AS IGST_AMT, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') ");
		bufferString.append(
				"THEN ITM.CGST_AMT END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.CGST_AMT END),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.SGST_AMT END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.SGST_AMT END),0) ");
		bufferString.append(
				"AS SGST_AMT, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.CESS_AMT END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') ");
		bufferString.append(
				"THEN ITM.CESS_AMT END),0) AS CESS_AMT FROM GETGSTR2A_CDN_HEADER HDR INNER JOIN GETGSTR2A_CDN_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE AND NOTE_TYPE IN ('C', 'D') GROUP BY CTIN, ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD, NOTE_TYPE UNION ALL SELECT CTIN AS GSTIN, HDR.DERIVED_RET_PERIOD, 'CDNA' AS TAX_DOC_TYPE, ");
		bufferString.append(
				"(CASE WHEN NOTE_TYPE IN ('C') THEN 'RCR' WHEN NOTE_TYPE IN ('D') THEN 'RDR' END) AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) AS INV_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(ITM.TAX_VALUE ),0) AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)+IFNULL(SUM(ITM.SGST_AMT),0) ");
		bufferString.append(
				"+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_CDNA_HEADER HDR INNER JOIN ");
		bufferString.append(
				"GETGSTR2A_CDNA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"GROUP BY CTIN, HDR.DERIVED_RET_PERIOD, NOTE_TYPE UNION ALL SELECT CTIN AS GSTIN, HDR.DERIVED_RET_PERIOD, 'CDNA' AS TAX_DOC_TYPE, ");
		bufferString.append(
				"'TOTAL' AS DOC_TYPE, COUNT(DISTINCT HDR.ID) AS COUNT, IFNULL(CASE WHEN NOTE_TYPE IN ('D') THEN SUM(IFNULL(ITM.IGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0))END ,0) ");
		bufferString.append(
				"-IFNULL(CASE WHEN NOTE_TYPE IN ('C') THEN SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0))END ,0) AS INV_VALUE, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') ");
		bufferString.append(
				"THEN ITM.TAX_VALUE END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.TAX_VALUE END),0) AS TAXABLE_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.CESS_AMT,0) END),0)- IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0) AS TOT_TAX, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.IGST_AMT END),0) ");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.IGST_AMT END),0) AS IGST_AMT, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') ");
		bufferString.append(
				"THEN ITM.CGST_AMT END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.CGST_AMT END),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.SGST_AMT END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') THEN ITM.SGST_AMT END),0) ");
		bufferString.append(
				"AS SGST_AMT, IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('D') THEN ITM.CESS_AMT END),0)-IFNULL(SUM(CASE WHEN NOTE_TYPE IN ('C') ");
		bufferString.append(
				"THEN ITM.CESS_AMT END),0) AS CESS_AMT FROM GETGSTR2A_CDNA_HEADER HDR INNER JOIN GETGSTR2A_CDNA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE IS_DELETE=FALSE AND NOTE_TYPE IN ('C','D') ");
		bufferString.append(
				"GROUP BY CTIN, HDR.DERIVED_RET_PERIOD, NOTE_TYPE UNION ALL SELECT CTIN AS GSTIN, DERIVED_RET_PERIOD, ");
		bufferString.append(
				"'ISD' AS TAX_DOC_TYPE, (CASE WHEN ISD_DOC_TYPE='ISD' THEN 'INV' WHEN ISD_DOC_TYPE='ISDCN' ");
		bufferString.append(
				"THEN 'CR' END) AS DOC_TYPE, COUNT(DISTINCT ID) AS COUNT, 0 AS INV_VALUE, 0 AS TAXABLE_VALUE, ");
		bufferString.append(
				"(IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CGST_AMT), 0)+IFNULL(SUM(SGST_AMT), 0)");
		bufferString.append(
				"+IFNULL(SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT FROM GETGSTR2A_ISD_HEADER WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"AND ISD_DOC_TYPE IN ('ISD', 'ISDCN') GROUP BY CTIN, DERIVED_RET_PERIOD, ISD_DOC_TYPE ");
		bufferString.append(
				"UNION ALL SELECT CTIN AS GSTIN, DERIVED_RET_PERIOD, 'ISD' AS TAX_DOC_TYPE, 'TOTAL' AS DOC_TYPE, ");
		bufferString.append(
				"COUNT(DISTINCT ID) AS COUNT, 0 AS INV_VALUE, 0 AS TAXABLE_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)");
		bufferString.append(
				"+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0) END), 0)- IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') ");
		bufferString.append(
				"THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0) END), 0) ");
		bufferString.append(
				"AS TOT_TAX, IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN IGST_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN IGST_AMT END), 0) AS IGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN CGST_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN CGST_AMT END), 0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN SGST_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN SGST_AMT END), 0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN CESS_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN CESS_AMT END), 0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_ISD_HEADER WHERE IS_DELETE=FALSE AND ISD_DOC_TYPE IN ('ISD', 'ISDCN') ");
		bufferString.append(
				"GROUP BY CTIN, DERIVED_RET_PERIOD, ISD_DOC_TYPE UNION ALL SELECT CTIN AS GSTIN, ");
		bufferString.append(
				"DERIVED_RET_PERIOD, 'ISDA' AS TAX_DOC_TYPE, (CASE WHEN ISD_DOC_TYPE='ISD' THEN 'RNV' ");
		bufferString.append(
				"WHEN ISD_DOC_TYPE='ISDCN' THEN 'RCR' END) AS DOC_TYPE, COUNT(DISTINCT ID) AS COUNT, ");
		bufferString.append(
				"0 AS INV_VALUE, 0 AS TAXABLE_VALUE, (IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CGST_AMT), 0)");
		bufferString.append(
				"+IFNULL(SUM(SGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM(IGST_AMT), 0) ");
		bufferString.append(
				"AS IGST_AMT, IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT FROM GETGSTR2A_ISDA_HEADER WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"AND ISD_DOC_TYPE IN ('ISD', 'ISDCN') GROUP BY CTIN, DERIVED_RET_PERIOD, ISD_DOC_TYPE ");
		bufferString.append(
				"UNION ALL SELECT CTIN AS GSTIN, DERIVED_RET_PERIOD, 'ISDA' AS TAX_DOC_TYPE, ");
		bufferString.append(
				"'TOTAL' AS DOC_TYPE, COUNT(DISTINCT ID) AS COUNT, 0 AS INV_VALUE, 0 AS TAXABLE_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)");
		bufferString.append(
				"+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0) END), 0)- IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') ");
		bufferString.append(
				"THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0) END), 0) ");
		bufferString.append(
				"AS TOT_TAX, IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN IGST_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN IGST_AMT END), 0) AS IGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN CGST_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN CGST_AMT END), 0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN SGST_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN SGST_AMT END), 0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISD') THEN CESS_AMT END), 0)");
		bufferString.append(
				"-IFNULL(SUM(CASE WHEN ISD_DOC_TYPE IN ('ISDCN') THEN CESS_AMT END), 0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_ISDA_HEADER WHERE IS_DELETE=FALSE AND ISD_DOC_TYPE IN ('ISD', 'ISDCN') ");
		bufferString.append(
				"GROUP BY CTIN, DERIVED_RET_PERIOD, ISD_DOC_TYPE UNION ALL SELECT GSTIN AS GSTIN, ");
		bufferString.append(
				"DERIVED_RET_PERIOD, 'IMPG' AS TAX_DOC_TYPE, 'TOTAL' AS DOC_TYPE, COUNT(DISTINCT ID) AS COUNT, ");
		bufferString.append(
				"(IFNULL(SUM(TAXABLE_VALUE), 0)+IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) AS INV_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(TAXABLE_VALUE ), 0) AS TAXABLE_VALUE, (IFNULL(SUM(IGST_AMT), 0)");
		bufferString.append(
				"+IFNULL (SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM (IGST_AMT), 0) AS IGST_AMT, 0 CGST_AMT, ");
		bufferString.append(
				"0 SGST_AMT, IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT FROM GETGSTR2A_IMPG_HEADER WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"GROUP BY GSTIN, DERIVED_RET_PERIOD UNION ALL SELECT GSTIN AS GSTIN, DERIVED_RET_PERIOD, ");
		bufferString.append(
				"'IMPG' AS TAX_DOC_TYPE, 'INV' AS DOC_TYPE, COUNT(DISTINCT ID) AS COUNT, ");
		bufferString.append(
				"(IFNULL(SUM(TAXABLE_VALUE), 0)+IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) AS INV_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(TAXABLE_VALUE ), 0) AS TAXABLE_VALUE, (IFNULL(SUM(IGST_AMT), 0)");
		bufferString.append(
				"+IFNULL (SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM (IGST_AMT), 0) AS IGST_AMT, 0 CGST_AMT, ");
		bufferString.append(
				"0 SGST_AMT, IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT FROM GETGSTR2A_IMPG_HEADER ");
		bufferString.append(
				"WHERE IS_DELETE=FALSE GROUP BY GSTIN, DERIVED_RET_PERIOD UNION ALL SELECT GSTIN, ");
		bufferString.append(
				"DERIVED_RET_PERIOD, 'IMPGSEZ' AS TAX_DOC_TYPE, 'INV' AS DOC_TYPE, COUNT(DISTINCT ID) AS COUNT, ");
		bufferString.append(
				"(IFNULL(SUM(TAXABLE_VALUE), 0)+IFNULL (SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) AS INV_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(TAXABLE_VALUE ), 0) AS TAXABLE_VALUE, (IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) ");
		bufferString.append(
				"AS TOT_TAX, IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, IFNULL(SUM(CESS_AMT), 0) ");
		bufferString.append(
				"AS CESS_AMT FROM GETGSTR2A_IMPGSEZ_HEADER WHERE IS_DELETE=FALSE GROUP BY GSTIN, DERIVED_RET_PERIOD ");
		bufferString.append(
				"UNION ALL SELECT GSTIN AS GSTIN, DERIVED_RET_PERIOD, 'IMPGSEZ' AS TAX_DOC_TYPE, 'TOTAL' AS DOC_TYPE, ");
		bufferString.append(
				"COUNT(DISTINCT ID) AS COUNT, (IFNULL(SUM(TAXABLE_VALUE), 0)+IFNULL(SUM(IGST_AMT), 0)");
		bufferString.append(
				"+IFNULL(SUM(CESS_AMT), 0)) AS INV_VALUE, IFNULL(SUM(TAXABLE_VALUE ), 0) AS TAXABLE_VALUE, ");
		bufferString.append(
				"(IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM(IGST_AMT), 0) ");
		bufferString.append(
				"AS IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_IMPGSEZ_HEADER WHERE IS_DELETE=FALSE GROUP BY GSTIN, DERIVED_RET_PERIOD ");
		bufferString.append(
				"UNION ALL SELECT GSTIN, DERIVED_RET_PERIOD, (CASE WHEN AMD_TYPE='IMPG' THEN 'AMDIMPG' ");
		bufferString.append(
				"WHEN AMD_TYPE='IMPGSEZ' THEN 'AMDIMPG' END) TAX_DOC_TYPE, 'RNV' AS DOC_TYPE, COUNT(DISTINCT ID) ");
		bufferString.append(
				"AS COUNT, (IFNULL(SUM(TAXABLE_VALUE), 0)+IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) ");
		bufferString.append(
				"AS INV_VALUE, IFNULL(SUM(TAXABLE_VALUE ), 0) AS TAXABLE_VALUE, (IFNULL(SUM(IGST_AMT), 0)");
		bufferString.append(
				"+IFNULL(SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, 0 CGST_AMT, ");
		bufferString.append(
				"0 SGST_AMT, IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT FROM GETGSTR2A_AMDHIST_HEADER ");
		bufferString.append(
				"WHERE IS_DELETE=FALSE GROUP BY GSTIN, DERIVED_RET_PERIOD, AMD_TYPE ");
		bufferString.append(
				"UNION ALL SELECT GSTIN AS GSTIN, DERIVED_RET_PERIOD, (CASE WHEN AMD_TYPE='IMPG' THEN 'AMDIMPG' ");
		bufferString.append(
				"WHEN AMD_TYPE='IMPGSEZ' THEN 'AMDIMPG' END) TAX_DOC_TYPE, 'TOTAL' AS DOC_TYPE, COUNT(DISTINCT ID) ");
		bufferString.append(
				"AS COUNT, (IFNULL(SUM(TAXABLE_VALUE), 0)+IFNULL(SUM(IGST_AMT), 0)+IFNULL(SUM(CESS_AMT), 0)) ");
		bufferString.append(
				"AS INV_VALUE, IFNULL(SUM(TAXABLE_VALUE ), 0) AS TAXABLE_VALUE, (IFNULL(SUM(IGST_AMT), 0)");
		bufferString.append(
				"+IFNULL(SUM(CESS_AMT), 0)) AS TOT_TAX, IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(CESS_AMT), 0) AS CESS_AMT FROM GETGSTR2A_AMDHIST_HEADER WHERE IS_DELETE=FALSE ");
		bufferString.append("GROUP BY GSTIN,DERIVED_RET_PERIOD,AMD_TYPE ) ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(
				"  GROUP BY TAX_DOC_TYPE, DOC_TYPE,GSTIN order by 2 desc");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("bufferString--------------------------> {} ",
					bufferString);
		}
		

		return bufferString;
	}

	private GetGstr2aReviewSummaryRespDto convert(Object[] obj) {
		GetGstr2aReviewSummaryRespDto dto = new GetGstr2aReviewSummaryRespDto();
		dto.setTable((String) obj[0]);
		dto.setTaxDocType((String) obj[1]);
		String docType = (String) obj[1];
		BigInteger integer = GenUtil.getBigInteger(obj[3]);
		dto.setCount(integer);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR"))) {
			dto.setInvoiceValue(CheckForNegativeValue((BigDecimal) obj[4]));
			dto.setTaxableValue(CheckForNegativeValue((BigDecimal) obj[5]));
			dto.setTaxPayble(CheckForNegativeValue((BigDecimal) obj[6]));
			dto.setIgst(CheckForNegativeValue((BigDecimal) obj[7]));
			dto.setCgst(CheckForNegativeValue((BigDecimal) obj[8]));
			dto.setSgst(CheckForNegativeValue((BigDecimal) obj[9]));
			dto.setCess(CheckForNegativeValue((BigDecimal) obj[10]));
		} else {
			dto.setInvoiceValue((BigDecimal) obj[4]);
			dto.setTaxableValue((BigDecimal) obj[5]);
			dto.setTaxPayble((BigDecimal) obj[6]);
			dto.setIgst((BigDecimal) obj[7]);
			dto.setCgst((BigDecimal) obj[8]);
			dto.setSgst((BigDecimal) obj[9]);
			dto.setCess((BigDecimal) obj[10]);
		}
		return dto;
	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}

	private void getAbsValue(List<GetGstr2aReviewSummaryRespDto> list) {

		for (GetGstr2aReviewSummaryRespDto respDto : list) {
			if (GSTConstants.CR.equalsIgnoreCase(respDto.getTaxDocType())) {
				respDto.setInvoiceValue(respDto.getInvoiceValue().abs());
				respDto.setTaxableValue(respDto.getTaxableValue().abs());
				respDto.setTaxPayble(respDto.getTaxPayble().abs());
				respDto.setIgst(respDto.getIgst().abs());
				respDto.setCgst(respDto.getCgst().abs());
				respDto.setSgst(respDto.getSgst().abs());
				respDto.setCess(respDto.getCess().abs());

			}
		}

	}
}
