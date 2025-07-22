package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1AspVerticalInvoiceDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

	@Component("AspSecDocDaoImpl")
	public class AspSecDocDaoImpl implements Gstr1ASPInvSavableDao {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(AspSecDocDaoImpl.class);

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;

		@Override
		public List<Object> getGstr1InvSavableReports(SearchCriteria criteria) {
			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			String dataType = request.getDataType();
			String taxDocType = request.getTaxDocType();
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
			StringBuilder buildQuery = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
				}
			}
			
			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					buildQuery.append(" AND INV.PROFIT_CENTRE IN :pcList");
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {
					buildQuery.append(" AND INV.PLANT_CODE IN :plantList");
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && salesList.size() > 0) {
					buildQuery.append(" AND INV.SALES_ORGANIZATION IN :salesList");
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {
					buildQuery.append(" AND INV.DISTRIBUTION_CHANNEL IN :distList");
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					buildQuery.append(" AND INV.DIVISION IN :divisionList");
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && locationList.size() > 0) {
					buildQuery.append(" AND INV.LOCATION IN :locationList");
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {
					buildQuery.append(" AND INV.USERACCESS1 IN :ud1List");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {
					buildQuery.append(" AND INV.USERACCESS2 IN :ud2List");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {
					buildQuery.append(" AND INV.USERACCESS3 IN :ud3List");
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && ud4List.size() > 0) {
					buildQuery.append(" AND INV.USERACCESS4 IN :ud4List");
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && ud5List.size() > 0) {
					buildQuery.append(" AND INV.USERACCESS5 IN :ud5List");
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && ud6List.size() > 0) {
					buildQuery.append(" AND INV.USERACCESS6 IN :ud6List");
				}
			}
			if (taxPeriodFrom != null && taxPeriodTo != null) {
				buildQuery.append(" AND DERIVED_RET_PERIOD BETWEEN ");
				buildQuery.append(":taxPeriodFrom AND :taxPeriodTo");
				
			}

			String queryStr = createInvSavableSummaryQueryString(buildQuery.toString());
			Query q = entityManager.createNativeQuery(queryStr);

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
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
			if (taxPeriodFrom != null && taxPeriodTo != null) {
				int derivedRetPeriodFrom = GenUtil
						.convertTaxPeriodToInt(request.getTaxPeriodFrom());
				int derivedRetPeriodTo = GenUtil
						.convertTaxPeriodToInt(request.getTaxPeriodTo());
				q.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
				q.setParameter("taxPeriodTo", derivedRetPeriodTo);
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
					+ "AS SAVE_STATUS, '' AS GSTN_REFID,'' AS GSTN_REFID_TIME, "
					+ " '' AS GSTN_ERRORCODE,'' AS GSTN_ERROR_DESCRIPTION, "
					+ " '' SOURCE_ID,'' FILE_NAME, '' CREATED_ON "
					+ "FROM GSTR1_PROCESSED_INV_SERIES INV LEFT OUTER JOIN "
					+ "FILE_STATUS FIL ON FIL.ID = INV.FILE_ID WHERE IS_DELETE = FALSE "
					+ buildQuery + " ) "
					+ "GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,SERIAL_NUM, "
					+ "NATURE_OF_DOC,DOC_SERIES_FROM,DOC_SERIES_TO ";
		}
	}
