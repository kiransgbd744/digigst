package com.ey.advisory.app.gstr1a.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1AspVerticalInvoiceDto;
import com.ey.advisory.app.services.reports.Gstr1ASPInvSavableDao;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

@Component("Gstr1aASPInvSavableDaoImpl")
public class Gstr1aASPInvSavableDaoImpl implements Gstr1ASPInvSavableDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1aASPInvSavableDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1InvSavableReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		
		String GSTIN = null;
		
		
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			
				
			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND INV.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		
		
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND INV.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = createInvSavableSummaryQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		
		
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessedInvoice(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1AspVerticalInvoiceDto convertProcessedInvoice(Object[] arr) {
		Gstr1AspVerticalInvoiceDto obj = new Gstr1AspVerticalInvoiceDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setSerialNo(arr[2] != null ? arr[2].toString() : null);
		obj.setNatureOfDocument(arr[3] != null ? arr[3].toString() : null);
		obj.setFrom(arr[4] != null ? arr[4].toString() : null);
		obj.setTo(arr[5] != null ? arr[5].toString() : null);
		obj.setTotalNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setCancelled(arr[7] != null ? arr[7].toString() : null);
		obj.setNetNumber(arr[8] != null ? arr[8].toString() : null);
		obj.setSaveStatus(arr[9] != null ? arr[9].toString() : null);
		obj.setgSTNRefID(arr[10] != null ? arr[10].toString() : null);
		obj.setgSTNRefIDTime(arr[11] != null ? arr[11].toString() : null);
		obj.setgSTNErrorcode(arr[12] != null ? arr[12].toString() : null);
		obj.setgSTNErrorDescription(
				arr[13] != null ? arr[13].toString() : null);
		obj.setSourceId(arr[14] != null ? arr[14].toString() : null);
		obj.setFileName(arr[15] != null ? arr[15].toString() : null);
		obj.setAspDateTime(arr[16] != null ? arr[16].toString() : null);

		return obj;
	}

	private String createInvSavableSummaryQueryString(String buildQuery) {

		return "select SUPPLIER_GSTIN, RETURN_PERIOD,SERIAL_NUM, NATURE_OF_DOC,"
				+ "DOC_SERIES_FROM,DOC_SERIES_TO, IFNULL(SUM(TOT_NUM),0) TOT_NUM, "
				+ "IFNULL(SUM(CANCELED),0) CANCELED, IFNULL(SUM(NET_NUM),0) "
				+ "NET_NUM, MAX(SAVE_STATUS)SAVE_STATUS,MAX(GSTN_REFID)GSTN_REFID, "
				+ "MAX(GSTN_REFID_TIME)GSTN_REFID_TIME,"
				+ "MAX(GSTN_ERRORCODE)GSTN_ERRORCODE,"
				+ "MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION, "
				+ "MAX(SOURCE_ID)SOURCE_ID, MAX(FILE_NAME)FILE_NAME, "
				+ "MAX(CREATED_ON)CREATED_ON FROM "
				+ "( select INV.SUPPLIER_GSTIN,INV.RETURN_PERIOD, "
				+ "INV.SERIAL_NUM,INV.NATURE_OF_DOC, INV.DOC_SERIES_FROM,"
				+ "INV.DOC_SERIES_TO, INV.TOT_NUM,INV.CANCELED,INV.NET_NUM, "
				+ "case when INV.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
				+ "WHEN INV.IS_SAVED_TO_GSTN = FALSE AND INV.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN INV.IS_SAVED_TO_GSTN = FALSE "
				+ "AND INV.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END "
				+ "AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTN_REFID, "
				+ "GSTNBATCH.BATCH_DATE AS GSTN_REFID_TIME,"
				+ " '' AS GSTN_ERRORCODE,'' AS GSTN_ERROR_DESCRIPTION, "
				+ " '' SOURCE_ID,'' FILE_NAME, '' CREATED_ON "
				+ "FROM GSTR1A_PROCESSED_INV_SERIES INV LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON FIL.ID = INV.FILE_ID "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = INV.BATCH_ID "
				+ " WHERE INV.IS_DELETE = FALSE "
				+ buildQuery + " ) "
				+ " GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,SERIAL_NUM, "
				+ "NATURE_OF_DOC,DOC_SERIES_FROM,DOC_SERIES_TO ";
	}
}
