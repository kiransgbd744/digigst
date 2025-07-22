package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1DataStatusSummaryReportView;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;

@Component("Anx1DataStatusSummaryReportsDaoImpl")
public class Anx1DataStatusSummaryReportsDaoImpl
		implements Anx1DataStatusSummaryReportsDao {
	
	public static final String OUTWARD = "outward";
	public static final String INWARD = "inward";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1DataStatusSummaryReportView> getDataStatusSummaryReports(
			Anx1ReportSearchReqDto request) {
		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		LocalDate docFromDate = request.getDocDateFrom();
		LocalDate docToDate = request.getDocDateTo();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		String status = request.getStatus();
		
		String dataType = request.getDataType();

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
				buildQuery.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
			}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND HDR.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" OR HDR.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" OR HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" OR HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" OR HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" OR HDR.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" OR HDR.USERDEFINED_FIELD1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" OR HDR.USERDEFINED_FIELD2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" OR HDR.USERDEFINED_FIELD3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" OR HDR.USERDEFINED_FIELD4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" OR HDR.USERDEFINED_FIELD5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" OR HDR.USERDEFINED_FIELD6 IN :ud6List");
			}
		}

		if (receiveFromDate != null && receiveToDate != null) {
			buildQuery.append(" AND HDR.RECEIVED_DATE BETWEEN ");
			buildQuery.append(
					":receiveFromDate AND :receiveToDate");
		}
		if (docFromDate != null && docToDate != null) {
			buildQuery.append(" AND HDR.DOC_DATE BETWEEN :docFromDate "
					+ "AND :docToDate");
		}
		if (returnFrom != null && returnTo != null) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(
					":returnFrom AND :returnTo");
		}
		String buildQ = buildQuery.toString().substring(4);
	
		String queryStr = createDSSQueryString(buildQ.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if(gstinList!= null && !gstinList.isEmpty() && gstinList.size()>0){
			q.setParameter("gstinList", gstinList);
			}
		}
		
		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("returnFrom", derivedRetPeriodFrom);
			q.setParameter("returnTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if(pcList!=null && !pcList.isEmpty() && pcList.size()>0){
			q.setParameter("pcList", pcList);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if(plantList!= null && !plantList.isEmpty() && plantList.size()>0){
			q.setParameter("plantList", plantList);
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if(salesList!= null && !salesList.isEmpty() && salesList.size()>0){
			q.setParameter("salesList", salesList);
			}
		}
		if (division != null && !division.isEmpty()) {
			if(divisionList!= null && !divisionList.isEmpty() && divisionList.size()>0){
			q.setParameter("divisionList", divisionList);
			}
		}
		if (location != null && !location.isEmpty()) {
			if(locationList!= null && !locationList.isEmpty() && locationList.size()>0){
			q.setParameter("locationList", locationList);
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if(distList!= null && !distList.isEmpty() && distList.size()>0){
			q.setParameter("distList", distList);
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if(ud1List!= null && !ud1List.isEmpty() && ud1List.size()>0){
			q.setParameter("ud1List", ud1List);
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if(ud2List!= null && !ud2List.isEmpty() && ud2List.size()>0){
			q.setParameter("ud2List", ud2List);
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if(ud3List!= null && !ud3List.isEmpty() && ud3List.size()>0){
			q.setParameter("ud3List", ud3List);
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if(ud4List!= null && !ud4List.isEmpty() && ud4List.size()>0){
			q.setParameter("ud4List", ud4List);
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if(ud5List!= null && !ud5List.isEmpty() && ud5List.size()>0){
			q.setParameter("ud5List", ud5List);
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if(ud6List!= null && !ud6List.isEmpty() && ud6List.size()>0){
			q.setParameter("ud6List", ud6List);
			}
		}
		
	
		List<Object[]> list = q.getResultList();
		List<Anx1DataStatusSummaryReportView> retList = list.parallelStream()
				.map(o -> convertDataStatussummry(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx1DataStatusSummaryReportView convertDataStatussummry(
			Object[] arr) {
		Anx1DataStatusSummaryReportView obj = new Anx1DataStatusSummaryReportView();

		obj.setDate(arr[0] != null ? arr[0].toString() : null);
		obj.setSupplierGstin(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setTableNumber(arr[3] != null ? arr[3].toString() : null);
		obj.setCount(arr[4] != null ? arr[4].toString() : null);
		obj.setInvoiceValue(arr[5] != null ? arr[5].toString() : null);
		obj.setTaxableValue(arr[6] != null ? arr[6].toString() : null);
		obj.setTotalTaxes(arr[7] != null ? arr[7].toString() : null);
		obj.setIgst(arr[8] != null ? arr[8].toString() : null);
		obj.setCgst(arr[9] != null ? arr[9].toString() : null);
		obj.setSgst(arr[10] != null ? arr[10].toString() : null);
		obj.setCess(arr[11] != null ? arr[11].toString() : null);
		obj.setReturnType(arr[12] != null ? arr[12].toString() : null);

		return obj;
	}

	private String createDSSQueryString(String buildQuery) {

		String queryStr = "SELECT HDR.RECEIVED_DATE AS DATE,"
				+ "HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,HDR.TABLE_SECTION AS TABLE_NUMBER,"
				+ "COUNT(DISTINCT HDR.ID) AS COUNT,"
				+ "SUM(HDR.DOC_AMT) AS INV_VALUE,"
				+ "SUM(ITM.TAXABLE_VALUE) AS TAXABLE_VALUE,"
				+ "SUM(ITM.IGST_AMT+ITM.CGST_AMT+ITM.SGST_AMT+"
				+ "ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM) AS TOTAL_TAX,"
				+ "SUM(ITM.IGST_AMT) AS IGST,SUM(ITM.CGST_AMT) AS CGST,"
				+ "SUM(ITM.SGST_AMT) AS SGST,SUM(ITM.CESS_AMT_SPECIFIC +"
				+ " ITM.CESS_AMT_ADVALOREM) AS CESS,"
				+ "(CASE WHEN HDR.AN_RETURN_TYPE='ANX1' THEN 'ANX1'"
				+ " WHEN HDR.AN_RETURN_TYPE='ANX2' "
				+ "THEN 'ANX2' END) AS RETURN_TYPE FROM "
				+ "ANX_OUTWARD_DOC_HEADER HDR LEFT OUTER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON "
				+ "HDR.ID= ITM.DOC_HEADER_ID WHERE " + buildQuery 
				+ " GROUP BY "
				+ "HDR.RECEIVED_DATE,HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,HDR.TABLE_SECTION," + "HDR.AN_RETURN_TYPE";

		return queryStr;
	}
}




