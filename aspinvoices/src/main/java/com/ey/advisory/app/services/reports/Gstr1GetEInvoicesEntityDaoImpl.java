package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesEntityDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1EInvReportsReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1GetEInvoicesEntityDaoImpl")
public class Gstr1GetEInvoicesEntityDaoImpl
		implements Gstr1GetEInvoicesEntityDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GetEInvoicesEntityDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("batchSaveStatusRepository") private Gstr1BatchRepository
	 * gstr1BatchRepository;
	 */

	static Integer cutoffPeriod = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<GSTR1GetEInvoicesEntityDto> getGstr1EIReports(
			SearchCriteria criteria) {

		Gstr1EInvReportsReqDto request = (Gstr1EInvReportsReqDto) criteria;
		List<Long> entityId = request.getEntityId();
		String fromPeriod = request.getReturnFrom();
		String toPeriod = request.getReturnTo();
		List<String> tableType = request.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());
		LocalDate docFrom = request.getDocDateFrom();
		LocalDate docTo = request.getDocDateTo();
		List<String> einv = request.getEInvGenerated();
		List<String> einvGenerated = einv.stream().map(String::toUpperCase)
				.collect(Collectors.toList());
		List<String> auto = request.getAutoDraftedGSTN();
		List<String> autoDrafted = auto.stream().map(String::toUpperCase)
				.collect(Collectors.toList());
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;

		try {
			if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
					&& dataSecAttrs.size() > 0) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase("GSTIN")) {
						cgstin = key;
						gstinList = dataSecAttrs.get("GSTIN");
					}
				}
			}

			StringBuffer queryStr = createQueryString(entityId, gstinList,
					fromPeriod, toPeriod, cgstin, tableTypeUpperCase, docFrom,
					docTo, einvGenerated, autoDrafted, dataSecAttrs);

			Query q = entityManager.createNativeQuery(queryStr.toString());
			LOGGER.debug("query -->" + q);

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

			if (CollectionUtils.isNotEmpty(tableType)) {
				q.setParameter("tableType", tableType);
			}

			/*
			 * String docDateStr = null; SimpleDateFormat dateFormat = new
			 * SimpleDateFormat("yyyy-MM-dd"); docDateStr =
			 * dateFormat.format(docFrom); LocalDate docDate = null; if
			 * (docDateStr != null) { docDate = LocalDate.parse(docDateStr,
			 * DateUtil.SUPPORTED_DATE_FORMAT2); q.setParameter("docFrom",
			 * docDate); }
			 * 
			 * String docDatStr = null; SimpleDateFormat datForm = new
			 * SimpleDateFormat("yyyy-MM-dd"); docDatStr =
			 * dateFormat.format(datForm); LocalDate docDat = null; if
			 * (docDatStr != null) { docDat = LocalDate.parse(docDatStr,
			 * DateUtil.SUPPORTED_DATE_FORMAT2); q.setParameter("docTo",
			 * docDat); }
			 */

			// if (CollectionUtils.isNotEmpty(einvGenerated)) {
			// // docTypeUpperCase.add("TOTAL");
			// q.setParameter("einvGenerated", einvGenerated);
			// }
			// if (CollectionUtils.isNotEmpty(autoDrafted)) {
			// // docTypeUpperCase.add("TOTAL");
			// q.setParameter("autoDrafted", autoDrafted);
			// }

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			List<GSTR1GetEInvoicesEntityDto> respDtos = list.parallelStream()
					.map(o -> convertTransactionalLevel(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return respDtos;
		} catch (Exception e) {
			throw new AppException("Error in data process ->", e);
		}
	}

	private StringBuffer createQueryString(List<Long> entityId,
			List<String> gstinList, String fromPeriod, String toPeriod,
			String cgstin, List<String> tableType, LocalDate docFrom,
			LocalDate docTo, List<String> einvGenerated,
			List<String> autoDrafted, Map<String, List<String>> dataSecAttrs) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("GSTIN")) {
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
		if (CollectionUtils.isNotEmpty(tableType)) {
			queryBuilder.append(" AND TABLE_TYPE IN (:tableType) ");
		}
		/*
		 * if (CollectionUtils.isNotEmpty(einvGenerated)) {
		 * queryBuilder.append(" AND DOC_TYPE IN (:docTypeUpperCase) "); } if
		 * (CollectionUtils.isNotEmpty(autoDrafted)) {
		 * queryBuilder.append(" AND DOC_TYPE IN (:docTypeUpperCase) "); }
		 */

		String condition = queryBuilder.toString();
		StringBuffer bufferString = new StringBuffer();
		bufferString.append("");
		LOGGER.debug("Gstr2a Query from database is-->" + bufferString);

		bufferString
				.append("SELECT GSTIN, GET_EINV_STATUS,TABLE_TYPE, SUM(COUNT) COUNT, "
						+ "SUM(SUPPLIES) TAXABLE_VALUE, SUM(IGST_AMT) IGST_AMT, SUM(CGST_AMT) CGST_AMT, "
						+ "SUM(SGST_AMT) SGST_AMT, SUM(CESS_AMT) CESS_AMT, SUM(TOTAL_TAX) TOTAL_TAX "
						+ "FROM( SELECT GSTIN,DERIVED_RET_PERIOD,GET_EINV_STATUS ,TABLE_TYPE, COUNT, SUPPLIES, "
						+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,TOTAL_TAX "
						+ "FROM ( SELECT GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,GET_EINV_STATUS ,'ALL_SECTION' "
						+ "AS TABLE_TYPE, SUM(COUNT) COUNT, IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('B2B','EXPORTS') "
						+ "THEN SUPPLIES END),0)- IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('CDNR','CDNUR') "
						+ "THEN SUPPLIES END),0) AS SUPPLIES, IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('B2B','EXPORTS') "
						+ "THEN IGST_AMT END),0)- IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('CDNR','CDNUR') THEN IGST_AMT END),0) "
						+ "AS IGST_AMT, IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('B2B','EXPORTS') THEN CGST_AMT END),0)- "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('CDNR','CDNUR') THEN CGST_AMT END),0) AS CGST_AMT, "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('B2B','EXPORTS') THEN SGST_AMT END),0)- "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('CDNR','CDNUR') THEN SGST_AMT END),0) AS SGST_AMT, "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('B2B','EXPORTS') THEN CESS_AMT END),0)- "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('CDNR','CDNUR') THEN CESS_AMT END),0) AS CESS_AMT, "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('B2B','EXPORTS') THEN TOTAL_TAX END),0)- "
						+ "IFNULL(SUM(CASE WHEN TABLE_TYPE IN ('CDNR','CDNUR') THEN TOTAL_TAX END),0) AS TOTAL_TAX "
						+ "FROM( SELECT HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS GET_EINV_STATUS , "
						+ "'B2B' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, "
						+ "SUM(IFNULL(IGST_AMT,0)) IGST_AMT, SUM(IFNULL(CGST_AMT,0)) CGST_AMT, SUM(IFNULL(SGST_AMT,0)) "
						+ "SGST_AMT, SUM(IFNULL(CESS_AMT,0)) CESS_AMT, SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
						+ "+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_B2B_HEADER HDR LEFT OUTER JOIN GETANX1_BATCH_TABLE BT ON "
						+ "HDR.BATCH_ID = BT.ID WHERE BT.IS_DELETE = FALSE GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
						+ "HDR.DERIVED_RET_PERIOD,BT.STATUS UNION ALL SELECT HDR.GSTIN,HDR.RETURN_PERIOD,"
						+ "HDR.DERIVED_RET_PERIOD,BT.STATUS GET_EINV_STATUS , 'EXPORTS' TABLE_TYPE,COUNT(HDR.ID) "
						+ "AS COUNT, SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, SUM(IFNULL(IGST_AMT,0)) IGST_AMT, "
						+ "0 CGST_AMT, 0 SGST_AMT, SUM(IFNULL(CESS_AMT,0)) CESS_AMT, SUM(IFNULL(IGST_AMT,0)"
						+ "+IFNULL(CESS_AMT,0)) AS TOTAL_TAX FROM GETGSTR1_EXP_HEADER HDR LEFT "
						+ "OUTER JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE "
						+ "GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS "
						+ "UNION ALL SELECT HDR.GSTIN, HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
						+ "BT.STATUS GET_EINV_STATUS ,'CDNR' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, "
						+ "SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, SUM(IFNULL(IGST_AMT,0)) IGST_AMT, "
						+ "SUM(IFNULL(CGST_AMT,0)) CGST_AMT, SUM(IFNULL(SGST_AMT,0)) SGST_AMT, "
						+ "SUM(IFNULL(CESS_AMT,0)) CESS_AMT, SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
						+ "+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_CDNR_HEADER HDR LEFT OUTER JOIN GETANX1_BATCH_TABLE "
						+ "BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE GROUP BY HDR.GSTIN ,"
						+ "HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS UNION ALL SELECT HDR.GSTIN,"
						+ "HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS GET_EINV_STATUS , "
						+ "'CDNUR' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, "
						+ "SUM(IFNULL(IGST_AMT,0)) IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, SUM(IFNULL(CESS_AMT,0)) CESS_AMT, "
						+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_CDNUR_HEADER HDR LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
						+ "ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD,"
						+ "HDR.DERIVED_RET_PERIOD,BT.STATUS ) GROUP BY GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,"
						+ "GET_EINV_STATUS UNION ALL SELECT GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,GET_EINV_STATUS,"
						+ "TABLE_TYPE, SUM(COUNT) COUNT, SUM(SUPPLIES) TAXABLE_VALUE, SUM(IGST_AMT) IGST_AMT, "
						+ "SUM(CGST_AMT) CGST_AMT, SUM(SGST_AMT) SGST_AMT, SUM(CESS_AMT) CESS_AMT, "
						+ "SUM(TOTAL_TAX) TOTAL_TAX FROM( SELECT HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
						+ "BT.STATUS GET_EINV_STATUS , 'B2B' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, "
						+ "SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, SUM(IFNULL(IGST_AMT,0)) IGST_AMT, "
						+ "SUM(IFNULL(CGST_AMT,0)) CGST_AMT, SUM(IFNULL(SGST_AMT,0)) SGST_AMT, "
						+ "SUM(IFNULL(CESS_AMT,0)) CESS_AMT, SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
						+ "+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_B2B_HEADER HDR LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
						+ "ON HDR.BATCH_ID = BT.ID WHERE BT.IS_DELETE = FALSE GROUP BY HDR.GSTIN,"
						+ "HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS UNION ALL SELECT HDR.GSTIN,"
						+ "HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS GET_EINV_STATUS , "
						+ "'EXPORTS' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, "
						+ "SUM(IFNULL(IGST_AMT,0)) IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, "
						+ "SUM(IFNULL(CESS_AMT,0)) CESS_AMT, SUM(IFNULL(IGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_EXP_HEADER HDR LEFT "
						+ "OUTER JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE "
						+ "GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS "
						+ "UNION ALL SELECT HDR.GSTIN, HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
						+ "BT.STATUS GET_EINV_STATUS ,'CDNR' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, "
						+ "SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, SUM(IFNULL(IGST_AMT,0)) IGST_AMT, "
						+ "SUM(IFNULL(CGST_AMT,0)) CGST_AMT, SUM(IFNULL(SGST_AMT,0)) SGST_AMT, "
						+ "SUM(IFNULL(CESS_AMT,0)) CESS_AMT, SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
						+ "+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_CDNR_HEADER HDR LEFT OUTER JOIN GETANX1_BATCH_TABLE BT ON "
						+ "HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE GROUP BY HDR.GSTIN ,"
						+ "HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS UNION ALL SELECT HDR.GSTIN,"
						+ "HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,BT.STATUS GET_EINV_STATUS , "
						+ "'CDNUR' TABLE_TYPE,COUNT(HDR.ID) AS COUNT, SUM(IFNULL(TAXABLE_VALUE,0)) SUPPLIES, "
						+ "SUM(IFNULL(IGST_AMT,0)) IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, SUM(IFNULL(CESS_AMT,0)) CESS_AMT, "
						+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX "
						+ "FROM GETGSTR1_CDNUR_HEADER HDR LEFT OUTER JOIN GETANX1_BATCH_TABLE BT ON "
						+ "HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE GROUP BY HDR.GSTIN ,HDR.RETURN_PERIOD,"
						+ "HDR.DERIVED_RET_PERIOD,BT.STATUS ) "
						+ "GROUP BY GSTIN,TABLE_TYPE,RETURN_PERIOD,"
						+ "DERIVED_RET_PERIOD,GET_EINV_STATUS)) ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append("GROUP BY GSTIN, GET_EINV_STATUS,TABLE_TYPE "
				+ "ORDER BY GSTIN,TABLE_TYPE");

		/*
		 * bufferString.append(
		 * "  GROUP BY TAX_DOC_TYPE, DOC_TYPE,GSTIN order by 2 desc");
		 * LOGGER.error("bufferString-------------------------->" +
		 * bufferString);
		 */
		return bufferString;
	}

	private GSTR1GetEInvoicesEntityDto convertTransactionalLevel(Object[] arr) {
		GSTR1GetEInvoicesEntityDto obj = new GSTR1GetEInvoicesEntityDto();

		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setInvoiceStatus(arr[1] != null ? arr[1].toString() : null);
		obj.setTableType(arr[2] != null ? arr[2].toString() : null);
		obj.setCount(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplies(arr[4] != null ? arr[4].toString() : null);

		BigDecimal igst = (BigDecimal) arr[5];
		if (igst != null) {
			obj.setIgstAmount(igst.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal cgst = (BigDecimal) arr[6];
		if (cgst != null) {
			obj.setCgstAmount(cgst.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal sgst = (BigDecimal) arr[7];
		if (sgst != null) {
			obj.setSgstAmount(sgst.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal cess = (BigDecimal) arr[8];
		if (cess != null) {
			obj.setCessAmount(cess.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal taxValue = (BigDecimal) arr[9];
		if (taxValue != null) {
			obj.setTotalTax(taxValue.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		return obj;
	}
}
