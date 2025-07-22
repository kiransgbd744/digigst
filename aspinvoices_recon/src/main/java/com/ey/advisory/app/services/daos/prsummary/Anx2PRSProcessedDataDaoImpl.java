package com.ey.advisory.app.services.daos.prsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

@Component("Anx2PRSProcessedDataDaoImpl")
public class Anx2PRSProcessedDataDaoImpl implements Anx2PRSProcessedDataDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Override
	public List<Anx2PRSProcessedResponseDto> getAnx2PRSProcessedRecs(
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
							&& !dataSecAttrs.get(OnboardingConstant.PC)
									.isEmpty()) {
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
							&& !dataSecAttrs.get(OnboardingConstant.PO)
									.isEmpty()) {
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

		String queryStr = createProcessedQueryString(buildQuery.toString());
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
		List<String> dataGstinList = new ArrayList<>();
		list.forEach(dto -> dataGstinList.add(dto[0].toString()));
		if (gstinList != null && gstinList.size() > 0) {
			for (String gstin : gstinList) {
				Object[] dummy = null;

				if (!dataGstinList.contains(gstin)) {
					dummy = new Object[17];

					dummy[0] = gstin;
					dummy[2] = BigInteger.ZERO;
					dummy[4] = BigDecimal.ZERO;
					dummy[3] = BigDecimal.ZERO;
					dummy[5] = BigDecimal.ZERO;
					dummy[6] = BigDecimal.ZERO;
					dummy[7] = BigDecimal.ZERO;
					dummy[8] = BigDecimal.ZERO;
					dummy[9] = BigDecimal.ZERO;
					dummy[10] = BigDecimal.ZERO;
					dummy[11] = BigDecimal.ZERO;
					dummy[12] = BigDecimal.ZERO;
					dummy[13] = BigDecimal.ZERO;
					dummy[14] = BigDecimal.ZERO;
					String stateCode = gstin.substring(0, 2);
					String stateName = statecodeRepository
							.findStateNameByCode(stateCode);
					dummy[15] = stateName;
					String gstintoken = defaultGSTNAuthTokenService
							.getAuthTokenStatusForGstin(gstin);
					if (gstintoken != null) {
						if ("A".equalsIgnoreCase(gstintoken)) {
							dummy[16] = "Active";
						} else {
							dummy[16] = "Inactive";
						}
					} else {
						dummy[16] = "Inactive";
					}
				}
				if (dummy != null) {

					list.add(dummy);
				}
			}
		}

		return list.parallelStream().map(o -> convertProcessedData(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx2PRSProcessedResponseDto convertProcessedData(Object[] arr) {
		Anx2PRSProcessedResponseDto obj = new Anx2PRSProcessedResponseDto();

		String GSTIN = (String) arr[0];
		obj.setGstin(GSTIN);
		obj.setReturnPeriod((String) arr[1]);
		int count = (GenUtil.getBigInteger(arr[2])).intValue();
		obj.setCount(count);
		obj.setInvValue((BigDecimal) arr[4]);
		obj.setTaxableValue((BigDecimal) arr[3]);
		obj.setTotalTaxPayable((BigDecimal) arr[5]);
		obj.setTpIGST((BigDecimal) arr[6]);
		obj.setTpCGST((BigDecimal) arr[7]);
		obj.setTpSGST((BigDecimal) arr[8]);
		obj.setTpCess((BigDecimal) arr[9]);
		obj.setTotalCreditEligible((BigDecimal) arr[10]);
		obj.setCeIGST((BigDecimal) arr[11]);
		obj.setCeCGST((BigDecimal) arr[12]);
		obj.setCeSGST((BigDecimal) arr[13]);
		obj.setCeCess((BigDecimal) arr[14]);
		obj.setHighlight(true);
		obj.setLastUpdated(LocalDateTime.now());
		obj.setState((String) arr[15]);
		obj.setStatus("Success");
		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin(GSTIN);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				obj.setAuthToken("Active");
			} else {
				obj.setAuthToken("Inactive");
			}
		} else {
			obj.setAuthToken("Inactive");
		}

		return obj;

	}

	private String createProcessedQueryString(String buildQuery) {
		return "SELECT CUST_GSTIN,RETURN_PERIOD, count(ID) AS ID_COUNT,"
				+ "(SUM(IFNULL(TAX_VALUE,'0'))-SUM(IFNULL(CR_TAXABLE_VALUE,'0'))) "
				+ "AS TAXABLE_VALUE,"
				+ "(SUM(IFNULL(DOC_AMT2,'0'))-SUM(IFNULL(DOC_AMT1,'0'))) AS INVOICE_VALUE,"
				+ "(SUM(IFNULL(TAX_PAYABLE2,'0'))-SUM(IFNULL(TAX_PAYABLE1,'0'))) AS TAX_PAYABLE,"
				+ "(SUM(IFNULL(IGST_AMT2,'0'))-SUM(IFNULL(IGST_AMT1,'0'))) AS IGST_AMT,"
				+ "(SUM(IFNULL(CGST_AMT2,'0'))-SUM(IFNULL(CGST_AMT1,'0'))) AS CGST_AMT,"
				+ "(SUM (IFNULL(SGST_AMT2,'0'))-SUM(IFNULL(SGST_AMT1,'0'))) AS SGST_AMT,"
				+ "(SUM(IFNULL(CESS_AMT_SPECIFIC2,'0') +IFNULL(CESS_AMT_ADVALOREM2,'0'))"
				+ "-SUM(IFNULL(CESS_AMT_SPECIFIC1,'0') +IFNULL(CESS_AMT_ADVALOREM1,'0'))) AS CESS_AMT,"
				+ "(SUM(IFNULL(AVAILABLE_IGST2,'0') +IFNULL(AVAILABLE_CGST2,'0') + "
				+ "IFNULL(AVAILABLE_SGST2,'0')+ IFNULL(AVAILABLE_CESS2,'0'))"
				+ "-SUM(IFNULL(AVAILABLE_IGST1,'0') +IFNULL(AVAILABLE_CGST1,'0')"
				+ "+IFNULL(AVAILABLE_SGST1,'0')+IFNULL(AVAILABLE_CESS1,'0'))) AS TOTAL_CREDIT,"
				+ "(SUM(IFNULL(AVAILABLE_IGST2,'0'))-SUM(IFNULL(AVAILABLE_IGST1,'0'))) "
				+ "AS AVAILABLE_IGST,(SUM(IFNULL(AVAILABLE_CGST2,'0'))"
				+ "-SUM(IFNULL(AVAILABLE_CGST1,'0'))) AS AVAILABLE_CGST,"
				+ "(SUM(IFNULL(AVAILABLE_SGST2,'0'))-SUM(IFNULL(AVAILABLE_SGST1,'0'))) "
				+ "AS AVAILABLE_SGST,(SUM(IFNULL(AVAILABLE_CESS2,'0'))"
				+ "-SUM(IFNULL(AVAILABLE_CESS1,'0'))) AS AVAILABLE_CESS,"
				+ " fnGetState(CUST_GSTIN) FROM "
				+ "(SELECT CUST_GSTIN,RETURN_PERIOD,ID,CASE WHEN DOC_TYPE "
				+ "IN ('CR','RCR') THEN SUM(IFNULL(TAXABLE_VALUE,'0')) END "
				+ "AS CR_TAXABLE_VALUE, CASE WHEN DOC_TYPE IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(TAXABLE_VALUE,'0')) END AS TAX_VALUE,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') THEN SUM(IFNULL(DOC_AMT,'0')) "
				+ "END AS DOC_AMT1,CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(DOC_AMT,'0')) END AS DOC_AMT2,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "SUM(IFNULL(TAX_PAYABLE,'0')) END AS TAX_PAYABLE1,"
				+ "CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(TAX_PAYABLE,'0')) END AS TAX_PAYABLE2,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') "
				+ "THEN SUM(IFNULL(IGST_AMT,'0')) END AS IGST_AMT1, "
				+ "CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(IGST_AMT,'0')) END AS IGST_AMT2,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "SUM(IFNULL(CGST_AMT,'0')) END AS CGST_AMT1,"
				+ "CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(CGST_AMT,'0')) END AS CGST_AMT2,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "SUM(IFNULL(SGST_AMT,'0')) END AS SGST_AMT1,"
				+ "CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(SGST_AMT,'0')) END AS SGST_AMT2,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "SUM(IFNULL(CESS_AMT_SPECIFIC,'0')) END AS CESS_AMT_SPECIFIC1,"
				+ "CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(CESS_AMT_SPECIFIC,'0')) END AS CESS_AMT_SPECIFIC2,"
				+ "CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "SUM(IFNULL(CESS_AMT_ADVALOREM,'0')) END AS CESS_AMT_ADVALOREM1,"
				+ "CASE WHEN DOC_TYPE  IN ('INV','DR','RNV','DR') "
				+ "THEN SUM(IFNULL(CESS_AMT_ADVALOREM,'0')) END AS CESS_AMT_ADVALOREM2,"
				+ "CASE WHEN DOC_TYPE = 'CR' THEN SUM(IFNULL(AVAILABLE_IGST,'0')) "
				+ "END AS AVAILABLE_IGST1,CASE WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN SUM(IFNULL(AVAILABLE_IGST,'0')) END AS AVAILABLE_IGST2, "
				+ "CASE WHEN DOC_TYPE = 'CR' THEN "
				+ "SUM(IFNULL(AVAILABLE_CGST,'0')) END AS AVAILABLE_CGST1,"
				+ "CASE WHEN DOC_TYPE IN ('INV','DR') THEN "
				+ "SUM(IFNULL(AVAILABLE_CGST,'0')) END AS AVAILABLE_CGST2,"
				+ "CASE WHEN DOC_TYPE = 'CR' THEN "
				+ "SUM(IFNULL(AVAILABLE_SGST,'0')) END AS AVAILABLE_SGST1,"
				+ "CASE WHEN DOC_TYPE IN ('INV','DR') "
				+ "THEN SUM(IFNULL(AVAILABLE_SGST,'0')) END AS AVAILABLE_SGST2,"
				+ "CASE WHEN DOC_TYPE = 'CR' THEN "
				+ "SUM(IFNULL(AVAILABLE_CESS,'0')) END AS AVAILABLE_CESS1,"
				+ "CASE WHEN DOC_TYPE IN ('INV','DR') THEN "
				+ "SUM(IFNULL(AVAILABLE_CESS,'0')) END AS AVAILABLE_CESS2 "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND AN_RETURN_TYPE = 'ANX2' AND "
				+ "AN_TAX_DOC_TYPE IN ('B2B','DXP','SEZWP','SEZWOP') "
				+ buildQuery
				+ " GROUP BY (CUST_GSTIN),(RETURN_PERIOD),(DOC_TYPE),ID) "
				+ " GROUP BY CUST_GSTIN,RETURN_PERIOD";
	}

}
