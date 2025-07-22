package com.ey.advisory.app.services.daos.anx2prsdetail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

@Component("Anx2PRSDetailDaoImpl")
public class Anx2PRSDetailDaoImpl implements Anx2PRSDetailDao {

@PersistenceContext(unitName = "clientDataUnit")
private EntityManager entityManager;

@Override
public List<Anx2PRSDetailResponseDto> getAnx2PRSDetail(
		Anx2PRSProcessedRequestDto request) {

	String taxperiod = request.getTaxPeriod();
	List<String> docType = request.getDocType();
	List<String> recordtype = request.getRecordType();
	LocalDate fromDate = request.getFromdate();
	LocalDate toDate = request.getToDate();

	Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

	String profitCenter = null;
	String plant = null;
	String purchase = null;
	String division = null;
	String location = null;
	String ud1 = null;
	String ud2 = null;
	String ud3 = null;
	String ud4 = null;
	String ud5 = null;
	String ud6 = null;
	String gstn = null;

	List<String> pcList = null;
	List<String> plantList = null;
	List<String> divisionList = null;
	List<String> locationList = null;
	List<String> purchaseList = null;
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
				gstn = key;
				if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}

			if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
				profitCenter = key;
				if (dataSecAttrs.get(OnboardingConstant.PC) != null
						&& !dataSecAttrs.get(OnboardingConstant.PC).isEmpty()) {
					pcList = dataSecAttrs.get(OnboardingConstant.PC);
				}
			}

			if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

				plant = key;
				if (dataSecAttrs.get(OnboardingConstant.PLANT) != null
						&& !dataSecAttrs.get(OnboardingConstant.PLANT)
								.isEmpty()) {
					plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
				division = key;
				if (dataSecAttrs.get(OnboardingConstant.DIVISION) != null
						&& !dataSecAttrs.get(OnboardingConstant.DIVISION)
								.isEmpty()) {
					divisionList = dataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
				location = key;
				if (dataSecAttrs.get(OnboardingConstant.LOCATION) != null
						&& !dataSecAttrs.get(OnboardingConstant.LOCATION)
								.isEmpty()) {
					locationList = dataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
				purchase = key;
				if (dataSecAttrs.get(OnboardingConstant.PO) != null
						&& !dataSecAttrs.get(OnboardingConstant.PO).isEmpty()) {
					purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
				}
			}

			if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
				ud1 = key;
				if (dataSecAttrs.get(OnboardingConstant.UD1) != null
						&& !dataSecAttrs.get(OnboardingConstant.UD1)
								.isEmpty()) {
					ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
				ud2 = key;
				if (dataSecAttrs.get(OnboardingConstant.UD2) != null
						&& !dataSecAttrs.get(OnboardingConstant.UD2)
								.isEmpty()) {
					ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
				ud3 = key;
				if (dataSecAttrs.get(OnboardingConstant.UD3) != null
						&& !dataSecAttrs.get(OnboardingConstant.UD3)
								.isEmpty()) {
					ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
				ud4 = key;
				if (dataSecAttrs.get(OnboardingConstant.UD4) != null
						&& !dataSecAttrs.get(OnboardingConstant.UD4)
								.isEmpty()) {
					ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
				ud5 = key;
				if (dataSecAttrs.get(OnboardingConstant.UD5) != null
						&& !dataSecAttrs.get(OnboardingConstant.UD5)
								.isEmpty()) {
					ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
				}
			}
			if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
				ud6 = key;
				if (dataSecAttrs.get(OnboardingConstant.UD6) != null
						&& !dataSecAttrs.get(OnboardingConstant.UD6)
								.isEmpty()) {
					ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
				}
			}
		}
	}

	StringBuilder buildQuery = new StringBuilder();

	if (gstn != null && !gstn.isEmpty() && gstinList != null
			&& !gstinList.isEmpty()) {
		buildQuery.append(" AND CUST_GSTIN IN :gstinList");
	}
	if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
			&& !pcList.isEmpty()) {
		buildQuery.append(" AND PROFIT_CENTRE IN :pcList");
	}
	if (plant != null && !plant.isEmpty() && plantList != null
			&& !plantList.isEmpty()) {
		buildQuery.append(" AND PLANT_CODE IN :plantList");
	}
	if (purchase != null && !purchase.isEmpty() && purchaseList != null
			&& !purchaseList.isEmpty()) {
		buildQuery.append(" AND PURCHASE_ORGANIZATION IN :purchaseList");
	}

	if (division != null && !division.isEmpty() && divisionList != null
			&& !divisionList.isEmpty()) {
		buildQuery.append(" AND DIVISION IN :divisionList");
	}

	if (location != null && !location.isEmpty() && locationList != null
			&& !locationList.isEmpty()) {
		buildQuery.append(" AND LOCATION IN :locationList");
	}
	if (ud1 != null && !ud1.isEmpty() && ud1List != null
			&& !ud1List.isEmpty()) {
		buildQuery.append(" AND USERACCESS1 IN :ud1List");
	}
	if (ud2 != null && !ud2.isEmpty() && ud2List != null
			&& !ud2List.isEmpty()) {
		buildQuery.append(" AND USERACCESS2 IN :ud2List");
	}
	if (ud3 != null && !ud3.isEmpty() && ud3List != null
			&& !ud3List.isEmpty()) {
		buildQuery.append(" AND USERACCESS3 IN :ud3List");
	}
	if (ud4 != null && !ud4.isEmpty() && ud4List != null
			&& !ud4List.isEmpty()) {
		buildQuery.append(" AND USERACCESS4 IN :ud4List");
	}
	if (ud5 != null && !ud5.isEmpty() && ud5List != null
			&& !ud5List.isEmpty()) {
		buildQuery.append(" AND USERACCESS5 IN :ud5List");
	}
	if (ud6 != null && !ud6.isEmpty() && ud6List != null
			&& !ud6List.isEmpty()) {
		buildQuery.append(" AND USERACCESS6 IN :ud6List");
	}
	if (fromDate != null && toDate != null) {
		buildQuery.append(" AND DOC_DATE BETWEEN ");
		buildQuery.append(":fromDate AND :toDate");
	}

	if (taxperiod != null && !taxperiod.isEmpty()) {
		buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
	}
	if (recordtype != null && !recordtype.isEmpty()) {
		buildQuery.append(" AND AN_TAX_DOC_TYPE IN :recordType ");
	}

	if (docType != null && !docType.isEmpty()) {
		buildQuery.append(" AND DOC_TYPE IN :docType ");
	}

	String queryStr = createPRSDetailQueryString(buildQuery.toString());
	Query q = entityManager.createNativeQuery(queryStr);

	if (gstn != null && !gstn.isEmpty() && gstinList != null
			&& !gstinList.isEmpty()) {
		q.setParameter("gstinList", gstinList);
	}

	if (taxperiod != null && !taxperiod.isEmpty()) {
		int derivedRetPeriodFrom = GenUtil
				.convertTaxPeriodToInt(request.getTaxPeriod());
		q.setParameter("taxperiod", derivedRetPeriodFrom);
	}
	if (fromDate != null && toDate != null) {
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);
	}
	if (recordtype != null && !recordtype.isEmpty()) {
		q.setParameter("recordType", recordtype);
	}
	if (docType != null && !docType.isEmpty()) {
		q.setParameter("docType", docType);
	}
	if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
			&& !pcList.isEmpty()) {
		q.setParameter("pcList", pcList);
	}
	if (plant != null && !plant.isEmpty() && plantList != null
			&& !plantList.isEmpty()) {
		q.setParameter("plantList", plantList);
	}
	if (purchase != null && !purchase.isEmpty() && purchaseList != null
			&& !purchaseList.isEmpty()) {
		q.setParameter("purchaseList", purchaseList);
	}
	if (division != null && !division.isEmpty() && divisionList != null
			&& !divisionList.isEmpty()) {
		q.setParameter("divisionList", divisionList);
	}
	if (location != null && !location.isEmpty() && locationList != null
			&& !locationList.isEmpty()) {
		q.setParameter("locationList", locationList);
	}
	if (ud1 != null && !ud1.isEmpty() && ud1List != null
			&& !ud1List.isEmpty()) {
		q.setParameter("ud1List", ud1List);
	}
	if (ud2 != null && !ud2.isEmpty() && ud2List != null
			&& !ud2List.isEmpty()) {
		q.setParameter("ud2List", ud2List);
	}
	if (ud3 != null && !ud3.isEmpty() && ud3List != null
			&& !ud3List.isEmpty()) {
		q.setParameter("ud3List", ud3List);
	}
	if (ud4 != null && !ud4.isEmpty() && ud4List != null
			&& !ud4List.isEmpty()) {
		q.setParameter("ud4List", ud4List);
	}
	if (ud5 != null && !ud5.isEmpty() && ud5List != null
			&& !ud5List.isEmpty()) {
		q.setParameter("ud5List", ud5List);
	}
	if (ud6 != null && !ud6.isEmpty() && ud6List != null
			&& !ud6List.isEmpty()) {
		q.setParameter("ud6List", ud6List);
	}

	List<Object[]> list = q.getResultList();
	return list.parallelStream().map(o -> convertPRSDetail(o))
			.collect(Collectors.toCollection(ArrayList::new));
}

private Anx2PRSDetailResponseDto convertPRSDetail(Object[] arr) {
	Anx2PRSDetailResponseDto obj = new Anx2PRSDetailResponseDto();

	if (arr[4] == null || arr[4].toString().isEmpty()) {
		BigDecimal invValue = BigDecimal.ZERO;
		arr[4] = invValue;
	}
	if (arr[5] == null || arr[5].toString().isEmpty()) {
		BigDecimal taxableVal = BigDecimal.ZERO;
		arr[5] = taxableVal;
	}
	if (arr[6] == null || arr[6].toString().isEmpty()) {
		BigDecimal totalTaxableVal = BigDecimal.ZERO;
		arr[6] = totalTaxableVal;
	}
	if (arr[7] == null || arr[7].toString().isEmpty()) {
		BigDecimal igst = BigDecimal.ZERO;
		arr[7] = igst;
	}
	if (arr[8] == null || arr[8].toString().isEmpty()) {
		BigDecimal cgst = BigDecimal.ZERO;
		arr[8] = cgst;
	}
	if (arr[9] == null || arr[9].toString().isEmpty()) {
		BigDecimal sgst = BigDecimal.ZERO;
		arr[9] = sgst;
	}
	if (arr[10] == null || arr[10].toString().isEmpty()) {
		BigDecimal cess = BigDecimal.ZERO;
		arr[10] = cess;
	}
	if (arr[11] == null || arr[11].toString().isEmpty()) {
		BigDecimal totalCE = BigDecimal.ZERO;
		arr[11] = totalCE;
	}
	if (arr[12] == null || arr[12].toString().isEmpty()) {
		BigDecimal ceIgst = BigDecimal.ZERO;
		arr[12] = ceIgst;
	}
	if (arr[13] == null || arr[13].toString().isEmpty()) {
		BigDecimal ceCgst = BigDecimal.ZERO;
		arr[13] = ceCgst;
	}
	if (arr[14] == null || arr[14].toString().isEmpty()) {
		BigDecimal ceSgst = BigDecimal.ZERO;
		arr[14] = ceSgst;
	}
	if (arr[15] == null || arr[15].toString().isEmpty()) {
		BigDecimal ceCess = BigDecimal.ZERO;
		arr[15] = ceCess;
	}

	BigInteger count = GenUtil.getBigInteger(arr[0]);
	obj.setCount(count.intValue());
	obj.setInvType((String) arr[2]);
	obj.setTaxDocType((String) arr[3]);
	obj.setInvValue((BigDecimal) arr[4]);
	obj.setTaxableValue((BigDecimal) arr[5]);
	obj.setTotalTaxPayable((BigDecimal) arr[6]);
	obj.setIGST((BigDecimal) arr[7]);
	obj.setCGST((BigDecimal) arr[8]);
	obj.setSGST((BigDecimal) arr[9]);
	obj.setCess((BigDecimal) arr[10]);
	obj.setTotalCreditEligible((BigDecimal) arr[11]);
	obj.setCeIGST((BigDecimal) arr[12]);
	obj.setCeCGST((BigDecimal) arr[13]);
	obj.setCeSGST((BigDecimal) arr[14]);
	obj.setCeCess((BigDecimal) arr[15]);

	return obj;

}

private String createPRSDetailQueryString(String buildQuery) {
	return "SELECT count(ID) AS count,RETURN_PERIOD, DOC_TYPE,"
			+ "AN_TAX_DOC_TYPE,SUM(DOC_AMT) AS INVOICE_VALUE,SUM(TAXABLE_VALUE) "
			+ "AS TAXABLE_VALUE,SUM(TAX_PAYABLE)AS TOTAL_TAX_PAYABLE,"
			+ "SUM(IGST_AMT) AS IGST_AMT,"
			+ "SUM(CGST_AMT) AS CGST_AMT,SUM(SGST_AMT) AS SGST_AMT,"
			+ "SUM(CESS_AMT_SPECIFIC + CESS_AMT_ADVALOREM) AS CESS_AMT,"
			+ "SUM(AVAILABLE_IGST+AVAILABLE_CGST+AVAILABLE_SGST+AVAILABLE_CESS) AS  "
			+ "TOTAL_CREDIT_ELIGIBLE,SUM(AVAILABLE_IGST) AS AVAILABLE_IGST,"
			+ "SUM(AVAILABLE_CGST) AS AVAILABLE_CGST,"
			+ "SUM(AVAILABLE_SGST) AS AVAILABLE_SGST,"
			+ "SUM(AVAILABLE_CESS) AS AVAILABLE_CESS FROM "
			+ "ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
			+ "IS_DELETE = FALSE AND AN_RETURN_TYPE = 'ANX2' AND AN_TAX_DOC_TYPE "
			+ "IN ('B2B','DXP','SEZWP','SEZWOP') " + buildQuery
			+ " GROUP BY (RETURN_PERIOD),( DOC_TYPE),(AN_TAX_DOC_TYPE)";

}

}
