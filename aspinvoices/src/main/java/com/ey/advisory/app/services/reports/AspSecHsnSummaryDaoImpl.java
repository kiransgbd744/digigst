package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.data.views.client.Gstr1AspVerticalHsnDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import com.google.common.base.Strings;

	@Component("AspSecHsnSummaryDaoImpl")
	public class AspSecHsnSummaryDaoImpl implements Gstr1AspHsnSummaryDao {

		private static final Logger LOGGER = LoggerFactory.getLogger(AspSecHsnSummaryDaoImpl.class);

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;

		@Autowired
		@Qualifier("HsnOrSacRepositoryMaster")
		private HsnOrSacRepository hsnOrSacRepository;
		

		@Autowired
		@Qualifier("GstnApi")
		GstnApi gstnApi;

		@Override
		public List<Object> getGstr1RSReports(SearchCriteria criteria) {

			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			String dataType = request.getDataType();
			String taxPeriodFrom = request.getTaxPeriodFrom();
			String taxPeriodTo = request.getTaxPeriodTo();
			String taxDocType = request.getTaxDocType();

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
								&& dataSecAttrs.get(OnboardingConstant.GSTIN).size() > 0) {
							gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
						}
					}

					if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
						ProfitCenter = key;
						if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PC).size() > 0) {
							pcList = dataSecAttrs.get(OnboardingConstant.PC);
						}
					}

					if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

						plant = key;
						if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.PLANT).size() > 0) {
							plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
						division = key;
						if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.DIVISION).size() > 0) {
							divisionList = dataSecAttrs.get(OnboardingConstant.DIVISION);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
						location = key;
						if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.LOCATION).size() > 0) {
							locationList = dataSecAttrs.get(OnboardingConstant.LOCATION);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
						sales = key;
						if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.SO).size() > 0) {
							salesList = dataSecAttrs.get(OnboardingConstant.SO);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
						distChannel = key;
						if (dataSecAttrs.get(OnboardingConstant.DC) != null
								&& dataSecAttrs.get(OnboardingConstant.DC).size() > 0) {
							distList = dataSecAttrs.get(OnboardingConstant.DC);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
						ud1 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD1) != null
								&& dataSecAttrs.get(OnboardingConstant.UD1).size() > 0) {
							ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
						ud2 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD2) != null
								&& dataSecAttrs.get(OnboardingConstant.UD2).size() > 0) {
							ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
						ud3 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD3) != null
								&& dataSecAttrs.get(OnboardingConstant.UD3).size() > 0) {
							ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
						ud4 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD4) != null
								&& dataSecAttrs.get(OnboardingConstant.UD4).size() > 0) {
							ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
						ud5 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD5) != null
								&& dataSecAttrs.get(OnboardingConstant.UD5).size() > 0) {
							ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
						}
					}
					if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
						ud6 = key;
						if (dataSecAttrs.get(OnboardingConstant.UD6) != null
								&& dataSecAttrs.get(OnboardingConstant.UD6).size() > 0) {
							ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
						}
					}
				}
			}

			StringBuilder buildQuery = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQuery.append(" WHERE HSN.SUPPLIER_GSTIN IN :gstinList");
				}
			}
		/*	if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					buildQuery.append(" AND HSN.PROFIT_CENTRE IN :pcList");
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {
					buildQuery.append(" AND HSN.PLANT_CODE IN :plantList");
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && salesList.size() > 0) {
					buildQuery.append(" AND HSN.SALES_ORGANIZATION IN :salesList");
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {
					buildQuery.append(" AND HSN.DISTRIBUTION_CHANNEL IN :distList");
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					buildQuery.append(" AND HSN.DIVISION IN :divisionList");
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && locationList.size() > 0) {
					buildQuery.append(" AND HSN.LOCATION IN :locationList");
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {
					buildQuery.append(" AND HSN.USERACCESS1 IN :ud1List");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {
					buildQuery.append(" AND HSN.USERACCESS2 IN :ud2List");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {
					buildQuery.append(" AND HSN.USERACCESS3 IN :ud3List");
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && ud4List.size() > 0) {
					buildQuery.append(" AND HSN.USERACCESS4 IN :ud4List");
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && ud5List.size() > 0) {
					buildQuery.append(" AND HSN.USERACCESS5 IN :ud5List");
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && ud6List.size() > 0) {
					buildQuery.append(" AND HSN.USERACCESS6 IN :ud6List");
				}
			}*/

			if (taxPeriodFrom != null && taxPeriodTo != null) {
				buildQuery.append(" AND HSN.DERIVED_RET_PERIOD BETWEEN ");
				buildQuery.append(":taxPeriodFrom AND :taxPeriodTo");
				
			}
			
			
			Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(taxPeriodFrom);

			String queryStr = createHsnSummaryQueryString(buildQuery.toString(),rateIncludedInHsn);
			Query q = entityManager.createNativeQuery(queryStr);

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {
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
		/*	if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && !plantList.isEmpty() && plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && !salesList.isEmpty() && salesList.size() > 0) {
					q.setParameter("salesList", salesList);
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && !divisionList.isEmpty() && divisionList.size() > 0) {
					q.setParameter("divisionList", divisionList);
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && !locationList.isEmpty() && locationList.size() > 0) {
					q.setParameter("locationList", locationList);
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && !distList.isEmpty() && distList.size() > 0) {
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
			}*/

			List<Object[]> list = q.getResultList();
			List<Object> verticalHsnList = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(list)) {
				List<HsnOrSacMasterEntity> hsnOrSacMasterEntities = hsnOrSacRepository.findAll();
				Map<String, String> hsnMap = new HashMap<String, String>();
				hsnOrSacMasterEntities.forEach(entity -> {
					hsnMap.put(entity.getHsnSac(), entity.getDescription());
				});

				for (Object arr[] : list) {
					verticalHsnList.add(convertProcessedHsn(arr, hsnMap,rateIncludedInHsn));
				}
			}
			return verticalHsnList;
		}

		private Gstr1AspVerticalHsnDto convertProcessedHsn(Object[] arr, Map<String, String> hsnMap,Boolean rateIncludedInHsn) {
			Gstr1AspVerticalHsnDto obj = new Gstr1AspVerticalHsnDto();

			if(rateIncludedInHsn){
				
				obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
				obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);

				String hsnDesc = null;
				String hsnCode = (arr[3] != null ? arr[3].toString() : null);
				obj.setHsn(hsnCode);

				if (!Strings.isNullOrEmpty(hsnCode)) {
					hsnDesc = hsnMap.get(hsnCode);
				}
				obj.setDescription(hsnDesc);
				/*
				 * obj.setHsn(arr[2] != null ? arr[2].toString() : null);
				 * obj.setDescription(arr[3] != null ? arr[3].toString() : null);
				 */
				obj.setTaxRate(arr[5] != null ? arr[5].toString() : null);
				obj.setUqc(arr[6] != null ? arr[6].toString() : null);
				BigDecimal bigDecimalTotalQuantity = (BigDecimal) arr[7];
				if (bigDecimalTotalQuantity != null) {
					BigDecimal total = new BigDecimal(bigDecimalTotalQuantity.doubleValue());
					obj.setTotalQuantity(total);
				}
				BigDecimal bigDecimalTaxableValue = (BigDecimal) arr[8];
				if (bigDecimalTaxableValue != null) {
					BigDecimal taxable = new BigDecimal(bigDecimalTaxableValue.doubleValue());
					obj.setTaxableValue(taxable);
				}

				BigDecimal bigDecimalIGST = (BigDecimal) arr[9];
				if (bigDecimalIGST != null) {
					BigDecimal igst = new BigDecimal(bigDecimalIGST.doubleValue());
					obj.setIgstAmount(igst);
				}
				BigDecimal bigDecimalCGST = (BigDecimal) arr[10];
				if (bigDecimalCGST != null) {
					BigDecimal cgst = new BigDecimal(bigDecimalCGST.doubleValue());
					obj.setCgstAmount(cgst);
				}
				BigDecimal bigDecimalSGST = (BigDecimal) arr[11];
				if (bigDecimalSGST != null) {
					BigDecimal sgst = new BigDecimal(bigDecimalSGST.doubleValue());
					obj.setSgstutgstAmount(sgst);
				}
				BigDecimal bigDecimalCess = (BigDecimal) arr[12];
				if (bigDecimalCess != null) {
					BigDecimal cess = new BigDecimal(bigDecimalCess.doubleValue());
					obj.setCessAmount(cess);
				}

				/*
				 * BigDecimal bigDecimalTotalValue = (BigDecimal) arr[11]; if
				 * (bigDecimalTotalValue != null) { BigDecimal total = new
				 * BigDecimal(bigDecimalTotalValue.doubleValue());
				 * obj.setTotalValue(total); }
				 */
				obj.setTotalValue(arr[13] != null ? arr[13].toString() : null);
				obj.setSaveStatus(arr[14] != null ? arr[14].toString() : null);
				obj.setgSTNRefID(arr[15] != null ? arr[15].toString() : null);
				obj.setgSTNRefIDTime(arr[16] != null ? arr[16].toString() : null);
				obj.setgSTNErrorcode(arr[17] != null ? arr[17].toString() : null);
				obj.setgSTNErrorDescription(arr[18] != null ? arr[18].toString() : null);
				
				
				
			}else{
			
			obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
			obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);

			String hsnDesc = null;
			String hsnCode = (arr[3] != null ? arr[3].toString() : null);
			obj.setHsn(hsnCode);

			if (!Strings.isNullOrEmpty(hsnCode)) {
				hsnDesc = hsnMap.get(hsnCode);
			}
			obj.setDescription(hsnDesc);
			/*
			 * obj.setHsn(arr[2] != null ? arr[2].toString() : null);
			 * obj.setDescription(arr[3] != null ? arr[3].toString() : null);
			 */
			obj.setUqc(arr[5] != null ? arr[5].toString() : null);
			BigDecimal bigDecimalTotalQuantity = (BigDecimal) arr[6];
			if (bigDecimalTotalQuantity != null) {
				BigDecimal total = new BigDecimal(bigDecimalTotalQuantity.doubleValue());
				obj.setTotalQuantity(total);
			}
			BigDecimal bigDecimalTaxableValue = (BigDecimal) arr[7];
			if (bigDecimalTaxableValue != null) {
				BigDecimal taxable = new BigDecimal(bigDecimalTaxableValue.doubleValue());
				obj.setTaxableValue(taxable);
			}

			BigDecimal bigDecimalIGST = (BigDecimal) arr[8];
			if (bigDecimalIGST != null) {
				BigDecimal igst = new BigDecimal(bigDecimalIGST.doubleValue());
				obj.setIgstAmount(igst);
			}
			BigDecimal bigDecimalCGST = (BigDecimal) arr[9];
			if (bigDecimalCGST != null) {
				BigDecimal cgst = new BigDecimal(bigDecimalCGST.doubleValue());
				obj.setCgstAmount(cgst);
			}
			BigDecimal bigDecimalSGST = (BigDecimal) arr[10];
			if (bigDecimalSGST != null) {
				BigDecimal sgst = new BigDecimal(bigDecimalSGST.doubleValue());
				obj.setSgstutgstAmount(sgst);
			}
			BigDecimal bigDecimalCess = (BigDecimal) arr[11];
			if (bigDecimalCess != null) {
				BigDecimal cess = new BigDecimal(bigDecimalCess.doubleValue());
				obj.setCessAmount(cess);
			}

			/*
			 * BigDecimal bigDecimalTotalValue = (BigDecimal) arr[11]; if
			 * (bigDecimalTotalValue != null) { BigDecimal total = new
			 * BigDecimal(bigDecimalTotalValue.doubleValue());
			 * obj.setTotalValue(total); }
			 */
			obj.setTotalValue(arr[12] != null ? arr[12].toString() : null);
			obj.setSaveStatus(arr[13] != null ? arr[13].toString() : null);
			obj.setgSTNRefID(arr[14] != null ? arr[14].toString() : null);
			obj.setgSTNRefIDTime(arr[15] != null ? arr[15].toString() : null);
			obj.setgSTNErrorcode(arr[16] != null ? arr[16].toString() : null);
			obj.setgSTNErrorDescription(arr[17] != null ? arr[17].toString() : null);
			}
			return obj;
		}

		private String createHsnSummaryQueryString(String buildQuery,Boolean rateIncludedInHsn) {

			
			if(rateIncludedInHsn){
			
			return "SELECT HSN.SUPPLIER_GSTIN,HSN.RETURN_PERIOD,"
					+ "HSN.DERIVED_RET_PERIOD,HSN.ITM_HSNSAC,HSN.ITM_DESCRIPTION,"
					+ "HSN.TAX_RATE,HSN.ITM_UQC,HSN.ITM_QTY,HSN.TAXABLE_VALUE,"
					+ "HSN.IGST,HSN.CGST,HSN.SGST,HSN.CESS,HSN.TOTAL_VALUE,"
					+ "HSN.SAVE_STATUS,"
					+ "(CASE WHEN SAVE_STATUS='IS_SAVED' THEN GSTN_SAVE_REF_ID ELSE NULL END) AS GSTIN_REF_ID,"
					+ "(CASE WHEN SAVE_STATUS='IS_SAVED' THEN BATCH_DATE ELSE NULL END) AS GSTIN_REF_ID_TIME,"
					+ "ERROR_CODE,ERROR_DESC FROM "
					+ "( SELECT HSN.SUPPLIER_GSTIN,HSN.RETURN_PERIOD,HSN.DERIVED_RET_PERIOD,"
					+ "HSNSAC AS ITM_HSNSAC,'' AS ITM_DESCRIPTION,TAX_RATE,(CASE WHEN LEFT(HSNSAC, 2) = 99 then 'NA' else ITM_UQC end) AS ITM_UQC,"
					+ "SUM(CASE WHEN LEFT(HSNSAC, 2) = 99 then 0 ELSE IFNULL(ITM_QTY,0) END) AS ITM_QTY,"
					
					+ " ( "
					+ "            IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IFNULL(TAXABLE_VALUE, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            )- IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TAXABLE_VALUE, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            ) "
					+ "          ) AS TAXABLE_VALUE, "
					+ "         ( "
					+ "            IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IFNULL(IGST_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            )- IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(IGST_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            ) "
					+ "          ) AS IGST, "
					+ "     ( "
					+ "            IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IFNULL(CGST_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            )- IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CGST_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            ) "
					+ "          ) AS CGST, "
					+ "    ( "
					+ "            IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IFNULL(SGST_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            )- IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(SGST_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            ) "
					+ "          ) AS SGST, "
					+ "             ( "
					+ "            IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IFNULL(CESS_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            )- IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(CESS_AMT, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            ) "
					+ "          ) AS CESS, "
					+ "      ( "
					+ "      IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IFNULL(TOTAL_VALUE, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            )- IFNULL( "
					+ "              SUM( "
					+ "                CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) END "
					+ "              ), "
					+ "              0 "
					+ "            ) "
					+ "          ) AS TOTAL_VALUE, "
					
					
					+ "MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS,"
					+ "MAX(BATCH_ID) BATCH_ID  FROM GSTR1_SUBMITTED_PS_TRANS HSN "
					+ " WHERE HSN.TAX_DOC_TYPE ='HSN_DIGI'  "
					+ "GROUP BY HSN.SUPPLIER_GSTIN, HSN.RETURN_PERIOD,HSN.DERIVED_RET_PERIOD,"
					+ "HSNSAC,TAX_RATE,(CASE WHEN LEFT(HSNSAC, 2) = 99 then 'NA' else ITM_UQC end)) HSN LEFT OUTER JOIN "
					+ "GSTR1_GSTN_SAVE_BATCH BATCH ON BATCH.ID = hsn.BATCH_ID "
					+ buildQuery;
			
			}else {
			
			
			return "SELECT  HSN.SUPPLIER_GSTIN,HSN.RETURN_PERIOD,HSN.DERIVED_RET_PERIOD,"
					+ "HSN.ITM_HSNSAC,HSN.ITM_DESCRIPTION,HSN.ITM_UQC,HSN.ITM_QTY,"
					+ "HSN.TAXABLE_VALUE,HSN.IGST,HSN.CGST,HSN.SGST,HSN.CESS,"
					+ "HSN.TOTAL_VALUE,HSN.SAVE_STATUS,"
					+ "(CASE WHEN SAVE_STATUS='IS_SAVED' THEN GSTN_SAVE_REF_ID ELSE NULL END) AS GSTIN_REF_ID,"
					+ "(CASE WHEN SAVE_STATUS='IS_SAVED' THEN ADD_SECONDS(BATCH_DATE,19800) ELSE NULL END) AS GSTIN_REF_ID_TIME,"
					+ "ERROR_CODE, ERROR_DESC FROM "
					+ "( SELECT HSN.SUPPLIER_GSTIN,HSN.RETURN_PERIOD,HSN.DERIVED_RET_PERIOD, "
					+ "HSNSAC AS ITM_HSNSAC,'' AS ITM_DESCRIPTION,ITM_UQC AS ITM_UQC,SUM(IFNULL(ITM_QTY,0))ITM_QTY,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST,SUM(CGST_AMT) CGST,SUM(SGST_AMT) SGST,"
					+ "SUM(CESS_AMT) CESS ,SUM(TOTAL_VALUE) TOTAL_VALUE,"
					+ "MAX(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' ELSE 'NOT_SAVED' END) SAVE_STATUS,"
					+ "MAX(BATCH_ID) BATCH_ID  FROM GSTR1_SUBMITTED_PS_TRANS HSN "
					+ "WHERE HSN.TAX_DOC_TYPE ='HSN_DIGI'  "
					+ "GROUP BY HSN.SUPPLIER_GSTIN, HSN.RETURN_PERIOD,HSN.DERIVED_RET_PERIOD,HSNSAC,ITM_UQC "
					+ ") HSN LEFT OUTER JOIN  GSTR1_GSTN_SAVE_BATCH BATCH "
					+ "ON BATCH.ID = hsn.BATCH_ID "
					+ buildQuery ;
			}
		}
	}
