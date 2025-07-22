/**
 * 
 */
package com.ey.advisory.app.services.reports.gstr1a;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.app.data.views.client.DocSeriesSavedSubmittedDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * @author Shashikant.Shukkla
 *
 * 
 */

@Component("Gstr1AAspProcessVsSubmitDocSeriesReportDaoImpl")
public class Gstr1AAspProcessVsSubmitDocSeriesReportDaoImpl
		implements Gstr1AAspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;

	@Override
	public List<Object> aspProcessVsSubmitDaoReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String GSTIN = null;

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

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}

		StringBuilder buildHeader = new StringBuilder();
		StringBuilder buildQueryForGstn = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" AND INV.SUPPLIER_GSTIN IN :gstinList");
				buildQueryForGstn.append(" AND HDR.GSTIN IN :gstinList ");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND INV.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND INV.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND INV.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader.append(" AND INV.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND INV.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND INV.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND INV.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND INV.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND INV.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND INV.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND INV.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND INV.USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND INV.DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");
			
			buildQueryForGstn.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");


		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString(),buildQueryForGstn.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodTo());
			q.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
				q.setParameter("pcList", pcList);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && !plantList.isEmpty()
					&& plantList.size() > 0) {
				q.setParameter("plantList", plantList);
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && !salesList.isEmpty()
					&& salesList.size() > 0) {
				q.setParameter("salesList", salesList);
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && !divisionList.isEmpty()
					&& divisionList.size() > 0) {
				q.setParameter("divisionList", divisionList);
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && !locationList.isEmpty()
					&& locationList.size() > 0) {
				q.setParameter("locationList", locationList);
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && !distList.isEmpty()
					&& distList.size() > 0) {
				q.setParameter("distList", distList);
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && !ud1List.isEmpty() && ud1List.size() > 0) {
				q.setParameter("ud1List", ud1List);
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && !ud2List.isEmpty() && ud2List.size() > 0) {
				q.setParameter("ud2List", ud2List);
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && !ud3List.isEmpty() && ud3List.size() > 0) {
				q.setParameter("ud3List", ud3List);
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && !ud4List.isEmpty() && ud4List.size() > 0) {
				q.setParameter("ud4List", ud4List);
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && !ud5List.isEmpty() && ud5List.size() > 0) {
				q.setParameter("ud5List", ud5List);
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && !ud6List.isEmpty() && ud6List.size() > 0) {
				q.setParameter("ud6List", ud6List);
			}
		}

		List<Object[]> list = q.getResultList();
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<NatureOfDocEntity> hsnOrSacMasterEntities = natureDocTypeRepo
					.findAll();
			Map<Long, String> hsnMap = new HashMap<Long, String>();
			hsnOrSacMasterEntities.forEach(entity -> {
				hsnMap.put(entity.getId(), entity.getNatureDocType());
			});

			for (Object arr[] : list) {
				verticalHsnList.add(convertgstnInvoiceSeries(arr, hsnMap));
			}
		}
		return verticalHsnList;
	}

	private DocSeriesSavedSubmittedDto convertgstnInvoiceSeries(Object[] arr,
			Map<Long, String> hsnMap) {
		DocSeriesSavedSubmittedDto obj = new DocSeriesSavedSubmittedDto();

		obj.setSno(arr[0] != null ? arr[0].toString() : null);
		obj.setGstnSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		String nature = null;
		Long serialno = (arr[4] != null ? Long.valueOf(arr[4].toString())
				: null);

		if (serialno != null) {
			nature = hsnMap.get(serialno);
		}
		obj.setGstnNatureOfDocument(nature);
		/*
		 * obj.setGstnSerialNum(arr[4] != null ? arr[4].toString() : null);
		 * obj.setGstnNatureOfDocument(arr[5] != null ? arr[5].toString() :
		 * null);
		 */
		obj.setGstnFrom(arr[6] != null ? arr[6].toString() : null);
		obj.setGstnTo(arr[7] != null ? arr[7].toString() : null);
		obj.setGstnTotalNumber(arr[8] != null ? arr[8].toString() : null);
		obj.setGstnCancelled(arr[9] != null ? arr[9].toString() : null);
		obj.setGstnNetNumber(arr[10] != null ? arr[10].toString() : null);
		/*
		 * obj.setSupplierGSTIN(arr[11] != null ? arr[11].toString() : null);
		 * obj.setReturnPeriod(arr[12] != null ? arr[12].toString() : null);
		 */
		String naturedoc = null;
		Long serialNo = (arr[11] != null ? Long.valueOf(arr[11].toString())
				: null);
		if (serialNo != null) {
			obj.setSerialNo(serialNo.toString());
			naturedoc = hsnMap.get(serialNo);
		}
		obj.setNatureOfDocument(naturedoc);
		/*
		 * obj.setSerialNo(arr[14] != null ? arr[14].toString() : null);
		 * obj.setNatureOfDocument(arr[15] != null ? arr[15].toString() : null);
		 */
		obj.setFrom(arr[13] != null ? arr[13].toString() : null);
		obj.setTo(arr[14] != null ? arr[14].toString() : null);
		obj.setTotalNumber(arr[15] != null ? arr[15].toString() : null);
		obj.setCancelled(arr[16] != null ? arr[16].toString() : null);
		obj.setNetNumber(arr[17] != null ? arr[17].toString() : null);
		obj.setSaveStatus(arr[18] != null ? arr[18].toString() : null);
		obj.setgSTNRefID(arr[19] != null ? arr[19].toString() : null);
		obj.setgSTNRefIDTime(arr[20] != null ? arr[20].toString() : null);
		obj.setgSTNErrorcode(arr[21] != null ? arr[21].toString() : null);
		obj.setgSTNErrorDescription(
				arr[22] != null ? arr[22].toString() : null);
		obj.setSourceId(arr[23] != null ? arr[23].toString() : null);
		obj.setFileName(arr[24] != null ? arr[24].toString() : null);
		obj.setAspDateTime(arr[25] != null ? arr[25].toString() : null);

		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery,String buildQueryForGstn) {
		StringBuilder build = new StringBuilder();
		
		
		build.append("WITH GSTN as ( SELECT GSTIN SUPPLIER_GSTIN,RETURN_PERIOD ,");
		build.append("HDR.DERIVED_RET_PERIOD DERIVED_RET_PERIOD,DOC_NUM AS GSTN_SERIALNUM,");
		build.append("'' GSTN_DOC_TYPE, FROM_SERIAL_NUM GSTN_FROM_SERIAL_NUM,");
		build.append("TO_SERIAL_NUM GSTN_TO_SERIAL_NUM, TOT_NUM GSTN_TOT_NUM,");
		build.append("CANCEL GSTN_CANCEL,NET_ISSUE GSTN_NET_ISSUE, 'INV SERIES' AS TABLE_TYPE ");
		build.append("FROM GETGSTR1A_DOC_ISSUED HDR WHERE HDR.IS_DELETE=FALSE ");
		build.append(buildQueryForGstn);
			//	and HDR.GSTIN='27GSPMH0482G1ZM'
			//	and HDR.RETURN_PERIOD='072022'
		build.append(" ) ");
		build.append(", DIGI as ( select INV.SUPPLIER_GSTIN SUPPLIER_GSTIN,");
		build.append("INV.RETURN_PERIOD RETURN_PERIOD, INV.DERIVED_RET_PERIOD DERIVED_RET_PERIOD,");
		build.append("INV.SERIAL_NUM DG_SERIALNUM,'' DG_NATURE_OF_DOC,");
		build.append("INV.DOC_SERIES_FROM DG_FROM_SERIAL_NUM,");
		build.append("INV.DOC_SERIES_TO DG_TO_SERIAL_NUM, INV.TOT_NUM DG_TOT_NUM,");
		build.append("INV.CANCELED DG_CANCEL,INV.NET_NUM DG_NET_ISSUE,");
		build.append("case when INV.IS_SAVED_TO_GSTN = TRUE ");
		build.append("AND INV.IS_DELETE= FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' ");
		build.append("END AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTN_REFID,");
		build.append("GSTNBATCH.BATCH_DATE AS GSTN_REFID_TIME,");
		build.append("'' AS GSTN_ERRORCODE,	'' AS GSTN_ERROR_DESCRIPTION,");
		build.append("'' SOURCE_ID,'' FILE_NAME, '' CREATED_ON FROM GSTR1A_PROCESSED_INV_SERIES INV ");
		build.append("LEFT OUTER JOIN FILE_STATUS FIL ON FIL.ID = INV.FILE_ID ");
		build.append("LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = INV.BATCH_ID ");
		build.append("WHERE INV.IS_DELETE = FALSE ");
		build.append(buildQuery);
				//	and INV.SUPPLIER_GSTIN='27GSPMH0482G1ZM'
			//	 and INV.RETURN_PERIOD='072022'	 
		build.append(" ) SELECT  ROW_NUMBER () OVER ( ");
		build.append("ORDER BY IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN)) SNO,");
		build.append("IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN),");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD),");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD),");
		build.append("GSTN_SERIALNUM,GSTN_DOC_TYPE, GSTN_FROM_SERIAL_NUM, GSTN_TO_SERIAL_NUM,");
		build.append("SUM(GSTN_TOT_NUM) GSTN_TOT_NUM, SUM(GSTN_CANCEL) GSTN_CANCEL,");
		build.append("SUM(GSTN_NET_ISSUE) GSTN_NET_ISSUE,DG_SERIALNUM, DG_NATURE_OF_DOC,");
		build.append("DG_FROM_SERIAL_NUM, DG_TO_SERIAL_NUM, SUM(DG_TOT_NUM) DG_TOT_NUM,");
		build.append("SUM(DG_CANCEL) DG_CANCEL, SUM(DG_NET_ISSUE) DG_NET_ISSUE,");
		build.append("SAVE_STATUS,GSTN_REFID,");
		build.append("TO_CHAR(ADD_SECONDS(GSTN_REFID_TIME,19800),'DD-MM-YYYY HH24:MI:SS'),");
		build.append("GSTN_ERRORCODE,GSTN_ERROR_DESCRIPTION, SOURCE_ID,");
		build.append("FILE_NAME,null as ASP_DATE_TIME from DIGI full outer join ");
		build.append("GSTN on GSTN.SUPPLIER_GSTIN = DIGI.SUPPLIER_GSTIN ");
		build.append("and GSTN.RETURN_PERIOD = DIGI.RETURN_PERIOD ");
		build.append("and GSTN.GSTN_FROM_SERIAL_NUM = DIGI.DG_FROM_SERIAL_NUM ");
		build.append("and GSTN.GSTN_TO_SERIAL_NUM = DIGI.DG_TO_SERIAL_NUM ");
		build.append("group by IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN),");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD),");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD),");
		build.append("GSTN_SERIALNUM,GSTN_DOC_TYPE, GSTN_FROM_SERIAL_NUM,");
		build.append("GSTN_TO_SERIAL_NUM, DG_SERIALNUM, DG_NATURE_OF_DOC,");
		build.append("DG_FROM_SERIAL_NUM, DG_TO_SERIAL_NUM, SAVE_STATUS,");
		build.append("GSTN_REFID,GSTN_REFID_TIME,GSTN_ERRORCODE,GSTN_ERROR_DESCRIPTION, SOURCE_ID,FILE_NAME ");
		
		return build.toString();
	
	}
}
