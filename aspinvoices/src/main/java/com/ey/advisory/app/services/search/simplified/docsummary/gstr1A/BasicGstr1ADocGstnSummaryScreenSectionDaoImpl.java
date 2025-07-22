/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("BasicGstr1ADocGstnSummaryScreenSectionDaoImpl")
public class BasicGstr1ADocGstnSummaryScreenSectionDaoImpl
		implements BasicGstr1ADocGstnSummaryScreenSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;


	@Override
	public List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
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
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != null) {

			build.append(" AND RET_PERIOD = :taxPeriodReq  ");
		}

		String buildQuery = build.toString();

		
		 

		
		String queryStr = createQueryStringForGstr1a(buildQuery);

		
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
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			List<Object[]> list = q.getResultList();
			List<Gstr1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("After Execution getting the data ----->{}",
						retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummarySectionDto convert(Object[] arr) {
		Gstr1SummarySectionDto obj = new Gstr1SummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		// obj.setRecords((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setRecords((Integer) arr[1]);
		obj.setInvValue((BigDecimal) arr[2]);
		obj.setIgst((BigDecimal) arr[3]);
		obj.setCgst((BigDecimal) arr[4]);
		obj.setSgst((BigDecimal) arr[5]);
		obj.setCess((BigDecimal) arr[6]);
		obj.setTaxableValue((BigDecimal) arr[7]);
		obj.setTaxPayable((BigDecimal) arr[8]);

		return obj;
	}


	private String createQueryStringForGstr1a(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT TAX_DOC_TYPE,SUM(RECORD_COUNT) RECORD_COUNT,"
				+ "SUM(DOC_AMT)DOC_AMT,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
				+ "SUM(CESS_AMT) CESS_AMT,SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE "
				+ "FROM ( SELECT SECTION_NAME AS TAX_DOC_TYPE,"
				+ "IFNULL(SUM(TOT_RECORD),0) AS RECORD_COUNT,"
				+ "IFNULL(SUM(TOT_VALUE),0) AS DOC_AMT, "
				+ "IFNULL(SUM(TOT_IGST),0) AS IGST_AMT,"
				+ "IFNULL(SUM(TOT_CGST),0) AS CGST_AMT,"
				+ "IFNULL(SUM(TOT_SGST),0) AS SGST_AMT,"
				+ "IFNULL(SUM(TOT_CESS),0) AS CESS_AMT,"
				+ "IFNULL(SUM(TOT_TAX),0)  AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(TOT_IGST,0)+IFNULL(TOT_CGST,0)+"
				+ "IFNULL(TOT_SGST,0)+IFNULL(TOT_CESS,0)) AS TAX_PAYABLE "
				+ "FROM GETGSTR1A_RATE_SUMMARY WHERE "
				+ "IS_DELETE = FALSE AND SECTION_NAME IN "
				+ "('B2B','B2BA','B2CL','B2CLA','B2CS','B2CSA','EXP',"
				+ "'EXPA','CDNR','CDNRA','CDNUR','CDNURA','AT',"
				+ "'ATA','TXPD','TXPDA','HSN','HSN_B2B','HSN_B2C','SUPECOM','14(i)','14(ii)','SUPECOMA','14A(i)','14A(ii)','15','15A(i)','15A(ii)') AND "
				+ buildQuery + " GROUP BY SECTION_NAME ) "
				+ "GROUP BY TAX_DOC_TYPE ";

		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		return queryStr;
	}

	@Override
	public List<Gstr1SummaryNilSectionDto> loadBasicSummarySectionNil(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;

		List<String> gstinList = null;
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
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN :gstinList");
			}
		}

		if (taxPeriodReq != null) {

			build.append(" AND RET_PERIOD = :taxPeriodReq ");
		}

		String buildQuery1 = build.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Prepared where Condition and apply in "
					+ "Nil,Non Exempted Query BEGIN");
		}

		
		String queryStr = createQueryStringNilForGstr1a(buildQuery1);

		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Nil,Non Exempted Query For Sections is -->"
					+ queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			List<Object[]> list = q.getResultList();
			List<Gstr1SummaryNilSectionDto> retList = list.parallelStream()
					.map(o -> convertNilSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution Nil,Exempted Query "
					+ "getting the data ----->{}", retList.size());
			return retList;
		} catch (Exception e) {
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryNilSectionDto convertNilSection(Object[] arr) {
		Gstr1SummaryNilSectionDto obj = new Gstr1SummaryNilSectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		obj.setAspNitRated((BigDecimal) arr[1]);
		obj.setAspExempted((BigDecimal) arr[2]);
		obj.setAspNonGst((BigDecimal) arr[3]);

		return obj;
	}

	private String createQueryStringNilForGstr1a(String buildQuery1) {
		// TODO Auto-generated method stub

		String queryStr = "SELECT 'NILEXTNON' SECTION_NAME, "
				+ "IFNULL(TOT_NILSUP_AMT,0) NIL_RATED_SUPPLIES,"
				+ "IFNULL(TOT_EXPT_AMT,0) EXMPTED_SUPPLIES,"
				+ "IFNULL(TOT_NGSUP_AMT,0) NON_GST_SUPPLIES "
				+ "FROM GETGSTR1A_NIL_SUMMARY WHERE IS_DELETE = FALSE AND "
				+ buildQuery1;
		return queryStr;
	}

	@Override
	public List<Gstr1SummaryDocSectionDto> loadBasicSummaryDocSection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		String gstin = null;

		List<String> gstinList = null;
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
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != null) {

			build.append(" AND RET_PERIOD = :taxPeriodReq ");

		}

		String buildQuery = build.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Prepared where Condition and apply in DocIssued Query BEGIN");
		}

		
		String queryStr = createQueryDocStringForGstr1a(buildQuery);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Executing DocIssued Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}

			List<Object[]> list = q.getResultList();
			List<Gstr1SummaryDocSectionDto> retList = list.parallelStream()
					.map(o -> convertToDoc(o))
					.collect(Collectors.toCollection(ArrayList::new));
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("After Execution DocIssued Query "
						+ "getting the data ----->{}", retList.size());
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryDocSectionDto convertToDoc(Object[] arr) {
		Gstr1SummaryDocSectionDto obj = new Gstr1SummaryDocSectionDto();
		obj.setTaxDocType((String) arr[0]);
		obj.setTotal((Integer) arr[1]);
		obj.setDocCancelled((Integer) arr[2]);
		obj.setNetIssued((Integer) arr[3]);
		return obj;
	}

	private String createQueryDocStringForGstr1a(String buildQuery) {
		// TODO Auto-generated method stub

		String queryStr = "SELECT 'DOC_ISSUE' AS TAX_DOC_TYPE,"
				+ "IFNULL(SUM(TOT_DOC_ISSUES),0) AS TOT_NUM_DOC_ISSUED,"
				+ "IFNULL(SUM(TOT_DOC_CANCELLED),0) AS TOT_DOC_CANCELLED,"
				+ "IFNULL(SUM(NET_DOC_ISSUED),0) AS NET_DOC_ISSUED "
				+ "FROM GETGSTR1A_DOCISSUED_SUMMARY "
				+ "WHERE IS_DELETE = FALSE AND " + buildQuery;
		return queryStr;
	}
}
