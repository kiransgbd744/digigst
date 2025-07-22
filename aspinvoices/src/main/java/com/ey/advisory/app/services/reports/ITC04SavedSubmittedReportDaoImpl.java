/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ITC04SavedSubmittedReportsDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Slf4j
@Component("ITC04SavedSubmittedReportDaoImpl")
public class ITC04SavedSubmittedReportDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<ITC04SavedSubmittedReportsDto> getSavedSubmittedReports(
			Gstr6SummaryRequestDto request) {
		String taxperiod = request.getTaxPeriod();
		List<String> tableno = request.getTableno();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		String ProfitCenter = null;
		String ProfitCenter2 = null;
		String plant = null;
		String sales = null;
		String purchase = null;
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
		List<String> pc2List = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purchaseList = null;
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
				if (key.equalsIgnoreCase(OnboardingConstant.PC2)) {
					ProfitCenter2 = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC2).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC2)
									.size() > 0) {
						pc2List = dataSecAttrs.get(OnboardingConstant.PC2);
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

				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
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

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND QRET_PERIOD = :taxperiod");
		}
		if (tableno != null && tableno.size() > 0) {
			buildQuery.append(" AND TABLE_NUMBER IN :tableno");
		}
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE1 IN :pcList");
			}
		}
		if (ProfitCenter2 != null && !ProfitCenter2.isEmpty()) {
			if (pc2List != null && pc2List.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE2 IN :pc2List");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND PLANT_CODE IN :plantList");
			}
		}

		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				buildQuery
						.append(" AND PURCHASE_ORGANIZATION IN :purchaseList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND USERACCESS6 IN :ud6List");
			}
		}

		String queryStr = EntityLevelSummaryQueryString(buildQuery.toString());

		Query q = entityManager.createNativeQuery(queryStr);

		if (taxperiod != null) {
			q.setParameter("taxperiod", taxperiod);
		}

		if (tableno != null && tableno.size() > 0) {
			q.setParameter("tableno", tableno);
		}

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
		if (ProfitCenter2 != null && !ProfitCenter2.isEmpty()) {
			if (pc2List != null && !pc2List.isEmpty() && pc2List.size() > 0) {
				q.setParameter("pc2List", pc2List);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && !plantList.isEmpty()
					&& plantList.size() > 0) {
				q.setParameter("plantList", plantList);
			}
		}

		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && !purchaseList.isEmpty()
					&& purchaseList.size() > 0) {
				q.setParameter("purchaseList", purchaseList);
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
		return list.parallelStream().map(o -> convertSavedSubmitted(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ITC04SavedSubmittedReportsDto convertSavedSubmitted(Object[] arr) {
		ITC04SavedSubmittedReportsDto obj = new ITC04SavedSubmittedReportsDto();

		// obj.setFy(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGstin(arr[2] != null ? arr[2].toString() : null);
		obj.setTableNumber(arr[3] != null ? arr[3].toString() : null);
		obj.setDeliveryChallanNumber(arr[4] != null ? arr[4].toString() : null);
		obj.setDeliveryChallanDate(fmtDate(arr[5]));
		obj.setjWDeliveryChallanNumber(
				arr[6] != null ? arr[6].toString() : null);
		obj.setjWDeliveryChallanDate(fmtDate(arr[7]));
		obj.setInvoiceNumber(arr[8] != null ? arr[8].toString() : null);
		obj.setInvoiceDate(fmtDate(arr[9]));
		obj.setJobWorkerGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setJobWorkerStateCode(arr[11] != null ? arr[11].toString() : null);
		obj.setJobWorkerType(arr[12] != null ? arr[12].toString() : null);
		obj.setJobWorkerName(arr[13] != null ? arr[13].toString() : null);
		obj.setTypeOfGoods(arr[14] != null ? arr[14].toString() : null);
		obj.setItemSerialNumber(arr[15] != null ? arr[15].toString() : null);
		obj.setProductDescription(arr[16] != null ? arr[16].toString() : null);
		obj.setNatureOfJW(arr[17] != null ? arr[17].toString() : null);
		obj.setUqc(arr[18] != null ? arr[18].toString() : null);
		obj.setQuantity(removeDecimalDigit(arr[19]));
		obj.setLossesUQC(arr[20] != null ? arr[20].toString() : null);
		obj.setLossesQuantity(removeDecimalDigit(arr[21]));
		obj.setItemAssessableAmount(appendDoubleDecimalDigit(
				arr[22]));
		obj.setiGSTRate(appendDoubleDecimalDigit(arr[23]));
		obj.setcGSTRate(appendDoubleDecimalDigit(arr[24]));
		obj.setsGSTRate(appendDoubleDecimalDigit(arr[25]));
		obj.setCessRate(appendDoubleDecimalDigit(arr[26]));
		obj.setIsFilled(arr[27] != null ? arr[27].toString() : null);
		obj.setFy(arr[28] != null ? arr[28].toString() : null);
		return obj;
	}

	private String EntityLevelSummaryQueryString(String buildQuery) {

		return "SELECT QRET_PERIOD, (CASE WHEN SUBSTR(QRET_PERIOD,1,2)=13 "
				+ "THEN 'Apr-Jun' WHEN SUBSTR(QRET_PERIOD,1,2)=14 THEN 'Jul-Sep' "
				+ "WHEN SUBSTR(QRET_PERIOD,1,2)=15 THEN 'Oct-Dec' "
				+ "WHEN SUBSTR(QRET_PERIOD,1,2)=16 THEN 'Jan-Mar' "
				+ "WHEN SUBSTR(QRET_PERIOD,1,2)=17 THEN 'Apr-Sep' "
				+ "WHEN SUBSTR(QRET_PERIOD,1,2)=18 THEN 'Oct-Mar' END) RETURN_PERIOD, "
				+ "SUPPLIER_GSTIN,TABLE_NUMBER, (CASE WHEN TABLE_NUMBER='4' "
				+ "THEN CHALLAN_NO ELSE ORG_CHALLAN_NO END) "
				+ "AS DELIVERY_CHALLEN_NUMBER, (CASE WHEN TABLE_NUMBER='4' "
				+ "THEN CHALLAN_DATE ELSE ORG_CHALLAN_DATE END) "
				+ "AS DELIVERY_CHALLEN_DATE, (CASE WHEN TABLE_NUMBER IN ('5A','5B') "
				+ "THEN JW2_CHALLAN_NO ELSE NULL END) AS JW2_CHALLAN_NO, "
				+ "(CASE WHEN TABLE_NUMBER IN ('5A','5B') "
				+ "THEN JW2_CHALLAN_DATE ELSE NULL END) "
				+ "AS JW2_CHALLAN_DATE, (CASE WHEN TABLE_NUMBER IN ('5C') "
				+ "THEN INV_NUM ELSE NULL END) AS INV_NUM, "
				+ "(CASE WHEN TABLE_NUMBER IN ('5C') THEN INV_DATE ELSE NULL END) "
				+ "AS INV_DATE, (CASE WHEN TABLE_NUMBER IN ('5A','5B','5C') "
				+ "THEN JW_GSTIN ELSE NULL END) AS JW_GSTIN, JW_STATE_CODE,"
				+ " ''JW_WORKER_TYPE,''JW_NAME, (CASE WHEN TABLE_NUMBER IN ('4') "
				+ "THEN GOODS_TYPE ELSE NULL END) AS GOODS_TYPE, "
				+ "ITM_NO,PRODUCT_DESC, (CASE "
				+ "WHEN TABLE_NUMBER IN ('5A','5B','5C') "
				+ "THEN NATURE_OF_JW ELSE NULL END) AS NATURE_OF_JW, "
				+ "ITM_UQC,ITM_QTY, (CASE WHEN TABLE_NUMBER IN ('5A','5B','5C') "
				+ "THEN LOSSES_UQC ELSE NULL END) AS LOSSES_UQC, "
				+ "(CASE WHEN TABLE_NUMBER IN ('5A','5B','5C') "
				+ "THEN LOSSES_QTY ELSE NULL END) AS LOSSES_QTY, "
				+ "(CASE WHEN TABLE_NUMBER IN ('4') THEN "
				+ "TAXABLE_VALUE ELSE NULL END) AS TAXABLE_VALUE, "
				+ "(CASE WHEN TABLE_NUMBER IN ('4') THEN IGST_RATE ELSE NULL END) "
				+ "AS IGST_RATE, (CASE WHEN TABLE_NUMBER IN ('4') "
				+ "THEN CGST_RATE ELSE NULL END) AS CGST_RATE, "
				+ "(CASE WHEN TABLE_NUMBER IN ('4') THEN SGST_RATE ELSE NULL END) "
				+ "AS SGST_RATE, (CASE WHEN TABLE_NUMBER IN ('4') "
				+ "THEN CESS_RATE ELSE NULL END) AS CESS_RATE,BT.IS_FILED,"
				+ "(case WHEN QDERIVED_RET_PERIOD BETWEEN 201604 AND 201703 THEN '201617' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 201704 AND 201803 THEN '2017-2018' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 201804 AND 201903 THEN '2018-2019' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 201904 AND 202003 THEN '2019-2020' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202004 AND 202103 THEN '2020-2021' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202104 AND 202203 THEN '2021-2022' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202204 AND 202303 THEN '2022-2023' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202304 AND 202403 THEN '2023-2024' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202404 AND 202503 THEN '2024-2025' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202504 AND 202603 THEN '2025-2026' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202604 AND 202703 THEN '2026-2027' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202704 AND 202803 THEN '2027-2028' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202804 AND 202903 THEN '2028-2029' "
				+ "WHEN QDERIVED_RET_PERIOD BETWEEN 202904 AND 203003 THEN '2029-2030' END) FI_YEAR "
				+ "FROM GETITC04_INVOICE HDR LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE "
				// + "AND BT.IS_DELETE = FALSE "
				+ buildQuery;
	}
	

	private String removeDecimalDigit(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				String val = ((BigDecimal) value)
						.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				String[] s = val.split("\\.");
				if (s.length == 2) {
					return s[0];
				} else {
					return val;
				}
			} else if (value instanceof String) {
				String[] s = ((String) value).split("\\.");
				if (s.length == 2) {
					return s[0];
				} else {
					return (String) value;
				}
			}
			return value.toString();
		}
		return "0";
	}

	private String appendDoubleDecimalDigit(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				String val = ((BigDecimal) value)
						.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				String[] s = val.split("\\.");
				if (s.length == 2) {
					if (s[1].length() == 1)
						return (s[0] + "." + s[1] + "0");
					else if (s[1].length() == 0)
						return s[0] + ".00";
					else {
						return s[0] + "." + s[1].substring(0, 2);
					}
				} else
					return "'" + (s[0] + ".00");
			} else if (value instanceof String) {
				String[] s = ((String) value).split("\\.");
				if (s.length == 2) {
					if (s[1].length() == 1)
						return (s[0] + "." + s[1] + "0");
					else if (s[1].length() == 0)
						return s[0] + ".00";
					else {
						return s[0] + "." + s[1].substring(0, 2);
					}
				} else
					return "'" + (s[0] + ".00");
			}
		}
		return "0.00";
	}

	public static String fmtDate(Object ldt) {
		if (ldt != null) {
			if (ldt instanceof LocalDate) {
				DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				return f.format((LocalDate) ldt);
			} else if (ldt instanceof String) {
				String[] s = ((String) ldt).split("-|/");
				if (s.length == 3) {
					if (s[0].length() == 4)
						return s[2] + "-" + s[1] + "-" + s[0];
					else if (s[2].length() == 4)
						return s[0] + "-" + s[1] + "-" + s[2];
				} else {
					return null;
				}
			} else {
				String dateString = ldt.toString();
				String[] s = dateString.split("-|/");
				if (s.length == 3) {
					if (s[0].length() == 4)
						return s[2] + "-" + s[1] + "-" + s[0];
					else if (s[2].length() == 4)
						return s[0] + "-" + s[1] + "-" + s[2];
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
