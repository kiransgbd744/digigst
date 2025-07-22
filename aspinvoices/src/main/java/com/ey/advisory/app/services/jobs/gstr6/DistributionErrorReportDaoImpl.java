package com.ey.advisory.app.services.jobs.gstr6;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.DistributionTotalDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

@Component("DistributionErrorReportDaoImpl")
public class DistributionErrorReportDaoImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(DistributionErrorReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	public List<Object> getErrorReports(Gstr6SummaryRequestDto request) {
		String taxperiod = request.getTaxPeriod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstn = null;

		List<String> gstinList = new ArrayList<>();

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN) && dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()) {
					gstn = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}

			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if (gstn != null && !gstn.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND ISD_GSTIN IN :gstinList");
			}
		}

		if (StringUtils.isNotBlank(taxperiod) && StringUtils.isNotBlank(taxperiod)) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
		}
		String queryStr = creategstnTransQueryString(buildQuery.toString());

		LOGGER.error("bufferString-------------------------->" + queryStr);
		Query outquery = entityManager.createNativeQuery(queryStr);

		if (gstn != null && !gstn.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {
				outquery.setParameter("gstinList", gstinList);
			}
		}
		if (StringUtils.isNotBlank(taxperiod)) {
			outquery.setParameter("taxperiod", GenUtil.convertTaxPeriodToInt(taxperiod));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = outquery.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private DistributionTotalDto convertTransactionalLevel(Object[] arr) {
		DistributionTotalDto obj = new DistributionTotalDto();

		obj.setReturnPeriod(arr[0] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString()) : null);
		obj.setiSDGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setRecipientGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setStateCode(arr[3] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString()) : null);
		obj.setOriginalRecipeintGSTIN(arr[4] != null ? arr[4].toString() : null);
		obj.setOriginalStatecode(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentType(arr[6] != null ? arr[6].toString() : null);
		obj.setSupplyType(arr[7] != null ? arr[7].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		obj.setOriginalDocumentNumber(arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalDocumentDate(arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalCreditNoteNumber(arr[12] != null ? arr[12].toString() : null);
		obj.setOriginalCreditNoteDate(arr[13] != null ? arr[13].toString() : null);
		obj.setEligibleIndicator(arr[14] != null ? arr[14].toString() : null);
		obj.setiGSTasIGST(arr[15] != null ? arr[15].toString() : null);
		obj.setiGSTasSGST(arr[16] != null ? arr[16].toString() : null);
		obj.setiGSTasCGST(arr[17] != null ? arr[17].toString() : null);
		obj.setsGSTasSGST(arr[18] != null ? arr[18].toString() : null);
		obj.setsGSTasIGST(arr[19] != null ? arr[19].toString() : null);
		obj.setcGSTasCGST(arr[20] != null ? arr[20].toString() : null);
		obj.setcGSTasIGST(arr[21] != null ? arr[21].toString() : null);
		obj.setcESSAmount(arr[22] != null ? arr[22].toString() : null);
		obj.setAspErrorCode(arr[23] != null ? arr[23].toString() : null);
		obj.setAspErrorDesc(arr[24] != null ? arr[24].toString() : null);
		obj.setAspInformationID(arr[25] != null ? arr[25].toString() : null);
		obj.setAspInformationDesc(arr[26] != null ? arr[26].toString() : null);
		return obj;
	}

	private String creategstnTransQueryString(String buildQuery) {
		return "SELECT TAX_PERIOD,ISD_GSTIN,CUST_GSTIN," + "STATE_CODE,ORG_CUST_GSTIN,ORG_STATE_CODE,"
				+ "DOC_TYPE,SUPPLY_TYPE, DOC_NUM,DOC_DATE," + "ORG_DOC_NUM,ORG_DOC_DATE,ORG_CR_NUM,ORG_CR_DATE,"
				+ "ELIGIBLE_INDICATOR, IGST_AMT_AS_IGST ," + "IGST_AMT_AS_SGST,IGST_AMT_AS_CGST,SGST_AMT_AS_SGST,"
				+ "SGST_AMT_AS_IGST, CGST_AMT_AS_CGST," + "CGST_AMT_AS_IGST,CESS_AMT,"
				+ "TRIM(', ' FROM IFNULL(GFN.ERROR_CODE_ASP,'')) " + "ERROR_CODE_ASP, TRIM(', ' FROM "
				+ "IFNULL(GFN.ERROR_DESCRIPTION_ASP,'')) " + "ERROR_DESCRIPTION_ASP, TRIM(', ' FROM "
				+ "IFNULL(GFN.INFO_ERROR_CODE_ASP,'')) INFO_CODE_ASP,"
				+ "TRIM(', ' FROM IFNULL(GFN.INFO_ERROR_DESCRIPTION_ASP,'')) "
				+ "INFO_DESCRIPTION_ASP, CASE WHEN IS_DELETE = FALSE "
				+ "THEN 'ACTIVE' WHEN IS_DELETE = TRUE THEN 'INACTIVE' "
				+ "END RECORD_STATUS FROM GSTR6_ISD_DISTRIBUTION_AS_ENTERED "
				+ "GID LEFT JOIN UTF_GSTR6_ISD_DISTRIBUTION_ERROR_INFO() " + "GFN ON GID.ID = GFN.DOC_HEADER_ID  "
				+ " WHERE IS_ERROR = TRUE AND IS_DELETE = FALSE " + buildQuery;

	}
}
