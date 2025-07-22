package com.ey.advisory.app.services.reports.gstr1a;
/*package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

import com.ey.advisory.app.data.views.client.Gstr1SavableB2CSTransactionalDto;
import com.ey.advisory.app.docs.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

*//**
 * @author Sujith.Nanga
 *
 * 
 *//*

@Component("Gstr1ASPB2CSSavableTransactionalDaoImpl")
public class Gstr1ASPB2CSSavableTransactionalDaoImpl
		implements Gstr1ASPB2CSSavableTransactionalDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ASPB2CSSavableTransactionalDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Object> getGstr1B2CSSavableReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

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
				buildQuery.append(" AND B2CS.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND ITM.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND ITM.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND B2CS.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery
						.append(" AND B2CS.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND B2CS.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND ITM.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND B2CS.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND B2CS.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND B2CS.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND B2CS.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND B2CS.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND B2CS.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND B2CS.DERIVED_RET_PERIOD = :taxperiod ");
			buildQuery.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = createB2CSSavableTransactionalQueryString(
				buildQuery.toString());
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
		return list.parallelStream().map(o -> convertB2CSSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1SavableB2CSTransactionalDto convertB2CSSummary(Object[] arr) {
		Gstr1SavableB2CSTransactionalDto obj = new Gstr1SavableB2CSTransactionalDto();

		obj.setUserID(arr[0] != null ? arr[0].toString() : null);
		obj.setSourceFileName(arr[1] != null ? arr[1].toString() : null);
		obj.setProfitCentre(arr[2] != null ? arr[2].toString() : null);
		obj.setPlant(arr[3] != null ? arr[3].toString() : null);
		obj.setDivision(arr[4] != null ? arr[4].toString() : null);
		obj.setLocation(arr[5] != null ? arr[5].toString() : null);
		obj.setSalesOrganisation(arr[6] != null ? arr[6].toString() : null);
		obj.setDistributionChannel(arr[7] != null ? arr[7].toString() : null);
		obj.setUserAccess1(arr[8] != null ? arr[8].toString() : null);
		obj.setUserAccess2(arr[9] != null ? arr[9].toString() : null);
		obj.setUserAccess3(arr[10] != null ? arr[10].toString() : null);
		obj.setUserAccess4(arr[11] != null ? arr[11].toString() : null);
		obj.setUserAccess5(arr[12] != null ? arr[12].toString() : null);
		obj.setUserAccess6(arr[13] != null ? arr[13].toString() : null);
		obj.setgLCodeTaxableValue(arr[14] != null ? arr[14].toString() : null);
		obj.setgLCodeIGST(arr[15] != null ? arr[15].toString() : null);
		obj.setgLCodeCGST(arr[16] != null ? arr[16].toString() : null);
		obj.setgLCodeSGST(arr[17] != null ? arr[17].toString() : null);
		obj.setgLCodeAdvlormCess(arr[18] != null ? arr[18].toString() : null);
		obj.setgLCodeSpecificCess(arr[19] != null ? arr[19].toString() : null);
		obj.setgLCodeStateCess(arr[20] != null ? arr[20].toString() : null);
		obj.setReturnPeriod(arr[21] != null ? arr[21].toString() : null);
		obj.setSgstin(arr[22] != null ? arr[22].toString() : null);
		obj.setDocType(arr[23] != null ? arr[23].toString() : null);
		obj.setSupplyType(arr[24] != null ? arr[24].toString() : null);
		obj.setDocNum(arr[25] != null ? arr[25].toString() : null);
		if (arr[26] != null) {
			String strdate = arr[26].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocDate(newDate);
		} else {
			obj.setDocDate(null);
		}
		obj.setOrgDocType(arr[27] != null ? arr[27].toString() : null);
		obj.setPrecedNum(arr[28] != null ? arr[28].toString() : null);
		if (arr[29] != null) {
			String strdate = arr[29].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPrecedDate(newDate);
		} else {
			obj.setPrecedDate(null);
		}
		obj.setCrdrPreGST(arr[30] != null ? arr[30].toString() : null);
		obj.setItemNumber(arr[31] != null ? arr[31].toString() : null);
		obj.setRecpGSTIN(arr[32] != null ? arr[32].toString() : null);
		obj.setDiffPerFlag(arr[33] != null ? arr[33].toString() : null);
		obj.setOrgRecpGSTIN(arr[34] != null ? arr[34].toString() : null);
		obj.setRecpType(arr[35] != null ? arr[35].toString() : null);
		obj.setRecpName(arr[36] != null ? arr[36].toString() : null);
		obj.setRecpCode(arr[37] != null ? arr[37].toString() : null);
		obj.setRecpAdd1(arr[38] != null ? arr[38].toString() : null);
		obj.setRecpAdd2(arr[39] != null ? arr[39].toString() : null);
		obj.setRecpAdd3(arr[40] != null ? arr[40].toString() : null);
		obj.setRecpAdd4(arr[41] != null ? arr[41].toString() : null);
		obj.setBillToState(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToState(arr[43] != null ? arr[43].toString() : null);
		obj.setPos(arr[44] != null ? arr[44].toString() : null);
		obj.setStateAplyCess(arr[45] != null ? arr[45].toString() : null);
		obj.setPortCode(arr[46] != null ? arr[46].toString() : null);
		obj.setShippingBillNum(arr[47] != null ? arr[47].toString() : null);
		if (arr[48] != null) {
			String strdate = arr[48].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {
			obj.setShippingBillDate(null);
		}
		obj.setFob(arr[49] != null ? arr[49].toString() : null);
		obj.setExportDuty(arr[50] != null ? arr[50].toString() : null);
		obj.setHsnOrSac(arr[51] != null ? arr[51].toString() : null);
		obj.setProductCode(arr[52] != null ? arr[52].toString() : null);
		obj.setProductDesc(arr[53] != null ? arr[53].toString() : null);
		obj.setCategoryOfProduct(arr[54] != null ? arr[54].toString() : null);
		obj.setUom(arr[55] != null ? arr[55].toString() : null);
		obj.setQuantity(arr[56] != null ? arr[56].toString() : null);
		obj.setSec7ofIGSTFlag(arr[57] != null ? arr[57].toString() : null);
		obj.setTaxableValue(arr[58] != null ? arr[58].toString() : null);
		obj.setIntegTaxAmt(arr[59] != null ? arr[59].toString() : null);
		obj.setIntegTaxRate(arr[60] != null ? arr[60].toString() : null);
		obj.setCentralTaxAmt(arr[61] != null ? arr[61].toString() : null);
		obj.setCentralTaxRate(arr[62] != null ? arr[62].toString() : null);
		obj.setStateUTTaxAmt(arr[63] != null ? arr[63].toString() : null);
		obj.setStateUTTaxRate(arr[64] != null ? arr[64].toString() : null);
		obj.setSpecfcCessRate(arr[65] != null ? arr[65].toString() : null);
		obj.setSpecfcCessAmt(arr[66] != null ? arr[66].toString() : null);
		obj.setAdvmCessRate(arr[67] != null ? arr[67].toString() : null);
		obj.setAdvmCessAmt(arr[68] != null ? arr[68].toString() : null);
		obj.setStateCessAmt(arr[69] != null ? arr[69].toString() : null);
		obj.setStateCessRate(arr[70] != null ? arr[70].toString() : null);
		obj.setOtherValue(arr[71] != null ? arr[71].toString() : null);
		obj.setInvoiceValue(arr[72] != null ? arr[72].toString() : null);
		obj.setAdjRefNo(arr[73] != null ? arr[73].toString() : null);
		if (arr[74] != null) {
			String strdate = arr[74].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAdjRefDate(newDate);
		} else {
			obj.setAdjRefDate(null);
		}
		obj.setTaxableValueAdj(arr[75] != null ? arr[75].toString() : null);
		obj.setIntegdTaxAmtAdj(arr[76] != null ? arr[76].toString() : null);
		obj.setCentralTaxAmtAdj(arr[77] != null ? arr[77].toString() : null);
		obj.setStateUTTaxAmtAdj(arr[78] != null ? arr[78].toString() : null);
		obj.setAdvlmCessAmtAdj(arr[79] != null ? arr[79].toString() : null);
		obj.setSpecfCessAmtAdj(arr[80] != null ? arr[80].toString() : null);
		obj.setStateCessAmtAdj(arr[81] != null ? arr[81].toString() : null);
		obj.setReverseChargeFlag(arr[82] != null ? arr[82].toString() : null);
		obj.setTcsFlag(arr[83] != null ? arr[83].toString() : null);
		obj.seteComGSTIN(arr[84] != null ? arr[84].toString() : null);
		obj.setTcsAmt(arr[85] != null ? arr[85].toString() : null);
		obj.setItcFlag(arr[86] != null ? arr[86].toString() : null);
		obj.setClaimRefndFlag(arr[87] != null ? arr[87].toString() : null);
		obj.setAutoPopltToRefund(arr[88] != null ? arr[88].toString() : null);
		obj.setReasonForCrDbNote(arr[89] != null ? arr[89].toString() : null);
		obj.setAccVoucherNum(arr[90] != null ? arr[90].toString() : null);
		if (arr[91] != null) {
			String strdate = arr[91].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccVoucherDate(newDate);
		} else {
			obj.setAccVoucherDate(null);
		}
		obj.setUserDefField1(arr[92] != null ? arr[92].toString() : null);
		obj.setUserDefField2(arr[93] != null ? arr[93].toString() : null);
		obj.setUserDefField3(arr[94] != null ? arr[94].toString() : null);
		obj.setUserDefField4(arr[95] != null ? arr[95].toString() : null);
		obj.setUserDefField5(arr[96] != null ? arr[96].toString() : null);
		obj.setUserDefField6(arr[97] != null ? arr[97].toString() : null);
		obj.setUserDefField7(arr[98] != null ? arr[98].toString() : null);
		obj.setUserDefField8(arr[99] != null ? arr[99].toString() : null);
		obj.setUserDefField9(arr[100] != null ? arr[100].toString() : null);
		obj.setUserDefField10(arr[101] != null ? arr[101].toString() : null);
		obj.setUserDefField11(arr[102] != null ? arr[102].toString() : null);
		obj.setUserDefField12(arr[103] != null ? arr[103].toString() : null);
		obj.setUserDefField13(arr[104] != null ? arr[104].toString() : null);
		obj.setUserDefField14(arr[105] != null ? arr[105].toString() : null);
		obj.setUserDefField15(arr[106] != null ? arr[106].toString() : null);
		obj.setEwaybillNum(arr[107] != null ? arr[107].toString() : null);
		obj.setEwaybillDate(arr[108] != null ? arr[108].toString() : null);
		if (arr[108] != null) {
			String strdate = arr[108].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setEwaybillDate(newDate);
		} else {
			obj.setEwaybillDate(null);
		}
		obj.setReturnType(arr[109] != null ? arr[109].toString() : null);
		obj.setDataCategory(arr[110] != null ? arr[110].toString() : null);
		obj.setTableNumber(arr[111] != null ? arr[111].toString() : null);
		obj.setAspInformationID(arr[112] != null ? arr[112].toString() : null);
		obj.setAspInformationDesc(
				arr[113] != null ? arr[113].toString() : null);
		obj.setInvoiceValueAsp(arr[114] != null ? arr[114].toString() : null);
		obj.setIntegratedTaxamountASP(
				arr[115] != null ? arr[115].toString() : null);
		obj.setCentralTaxamountASP(
				arr[116] != null ? arr[116].toString() : null);
		obj.setStateUTTaxamountASP(
				arr[117] != null ? arr[117].toString() : null);
		obj.setIntegratedTaxamountdiff(
				arr[118] != null ? arr[118].toString() : null);
		obj.setCentralTaxamountdiff(
				arr[119] != null ? arr[119].toString() : null);
		obj.setStateUTTaxamountdiff(
				arr[120] != null ? arr[120].toString() : null);
		obj.setSaveStatus(arr[121] != null ? arr[121].toString() : null);
		obj.setGstnRefId(arr[122] != null ? arr[122].toString() : null);
		obj.setGstnRefIdTime(arr[123] != null ? arr[123].toString() : null);
		obj.setGstnErrorCode(arr[124] != null ? arr[124].toString() : null);
		obj.setGstnErrorDesc(arr[125] != null ? arr[125].toString() : null);
		obj.setUploadeSource(arr[126] != null ? arr[126].toString() : null);
		obj.setSourceId(arr[127] != null ? arr[127].toString() : null);
		obj.setFileName(arr[128] != null ? arr[128].toString() : null);
		obj.setAspDateTime(arr[129] != null ? arr[129].toString() : null);

		return obj;
	}

	private String createB2CSSavableTransactionalQueryString(
			String buildQuery) {

		return "SELECT B2CS.USER_ID,B2CS.SOURCE_FILENAME,"
				+ "ITM.PROFIT_CENTRE,ITM.PLANT_CODE,B2CS.DIVISION,"
				+ "ITM.LOCATION, B2CS.SALES_ORGANIZATION,B2CS.DISTRIBUTION_CHANNEL,"
				+ "B2CS.USERACCESS1,B2CS.USERACCESS2,B2CS.USERACCESS3,"
				+ "B2CS.USERACCESS4,B2CS.USERACCESS5,B2CS.USERACCESS6,"
				+ "ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,ITM.GLCODE_CGST,"
				+ "ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS,"
				+ "ITM.GLCODE_STATE_CESS,ITM.RETURN_PERIOD,B2CS.SUPPLIER_GSTIN,"
				+ "B2CS.DOC_TYPE,B2CS.SUPPLY_TYPE, B2CS.DOC_NUM,B2CS.DOC_DATE,"
				+ "B2CS.ORIGINAL_DOC_TYPE, ITM.ORIGINAL_DOC_NUM,ITM.ORIGINAL_DOC_DATE,"
				+ "ITM.CRDR_PRE_GST,ITM.ITM_NO,B2CS.CUST_GSTIN,"
				+ "B2CS.DIFF_PERCENT,B2CS.ORIGINAL_CUST_GSTIN,"
				+ "B2CS.CUST_SUPP_TYPE,"
				+ "B2CS.CUST_SUPP_NAME,B2CS.CUST_SUPP_CODE,B2CS.CUST_SUPP_ADDRESS1,"
				+ "B2CS.CUST_SUPP_ADDRESS2,B2CS.CUST_SUPP_ADDRESS3,"
				+ "B2CS.CUST_SUPP_ADDRESS4, B2CS.BILL_TO_STATE,"
				+ "B2CS.SHIP_TO_STATE,B2CS.POS,B2CS.STATE_APPLYING_CESS,"
				+ "B2CS.SHIP_PORT_CODE, B2CS.SHIP_BILL_NUM,B2CS.SHIP_BILL_DATE,"
				+ "ITM.FOB, ITM.EXPORT_DUTY,ITM.ITM_HSNSAC,ITM.PRODUCT_CODE,"
				+ "ITM.ITM_DESCRIPTION,ITM.ITM_TYPE,ITM.ITM_UQC,ITM.ITM_QTY,"
				+ "ITM.SECTION7_OF_IGST_FLAG,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.IGST_RATE,ITM.CGST_AMT,ITM.CGST_RATE,ITM.SGST_AMT,"
				+ "ITM.SGST_RATE,ITM.CESS_RATE_SPECIFIC,ITM.CESS_AMT_SPECIFIC,"
				+ "ITM.CESS_RATE_ADVALOREM, ITM.CESS_AMT_ADVALOREM,"
				+ "ITM.STATECESS_AMT, ITM.STATECESS_RATE,ITM.OTHER_VALUES,"
				+ "ITM.LINE_ITEM_AMT, ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE,"
				+ "ITM.ADJ_TAXABLE_VALUE, ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,"
				+ "ITM.ADJ_SGST_AMT, ITM.ADJ_CESS_AMT_ADVALOREM,"
				+ "ITM.ADJ_CESS_AMT_SPECIFIC, ITM.ADJ_STATECESS_AMT,"
				+ "ITM.REVERSE_CHARGE,ITM.TCS_FLAG, ITM.ECOM_GSTIN,"
				+ "ITM.TCS_AMT,ITM.ITC_FLAG, ITM.CLAIM_REFUND_FLAG,"
				+ "ITM.AUTOPOPULATE_TO_REFUND,ITM.CRDR_REASON,"
				+ "ITM.ACCOUNTING_VOUCHER_NUM,ITM.ACCOUNTING_VOUCHER_DATE,"
				+ "ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
				+ "ITM.USERDEFINED_FIELD3, ITM.USERDEFINED_FIELD4,"
				+ "ITM.USERDEFINED_FIELD5, ITM.USERDEFINED_FIELD6,"
				+ "ITM.USERDEFINED_FIELD7, ITM.USERDEFINED_FIELD8 ,"
				+ "ITM.USERDEFINED_FIELD9, ITM.USERDEFINED_FIELD10,"
				+ "ITM.USERDEFINED_FIELD11, ITM.USERDEFINED_FIELD12,"
				+ "ITM.USERDEFINED_FIELD13, ITM.USERDEFINED_FIELD14,"
				+ "ITM.USERDEFINED_FIELD15, ITM.EWAY_BILL_NUM,"
				+ "ITM.EWAY_BILL_DATE,B2CS.RETURN_TYPE, B2CS.TAX_DOC_TYPE,"
				+ "B2CS.TABLE_SECTION,"
				+ "TRIM(', ' FROM IFNULL(ASPH_ER.ERROR_CODE_ASP,'') ||','|| "
				+ "IFNULL(ASPI_ER.ERROR_CODE_ASP,'')) "
				+ "AS INFO_ERROR_CODE_ASP, TRIM(', ' FROM "
				+ "IFNULL(ASPH_ER.ERROR_DESCRIPTION_ASP,'') ||','|| "
				+ "IFNULL(ASPI_ER.ERROR_DESCRIPTION_ASP,'')) "
				+ "AS INFO_ERROR_DESCRIPTION_ASP,DOC_AMT,B2CS.IGST_AMT "
				+ "AS HDR_IGST_AMT, B2CS.CGST_AMT AS HDR_CGST_AMT,"
				+ "B2CS.SGST_AMT AS HDR_SGST_AMT,(B2CS.IGST_AMT-ITM.IGST_AMT) "
				+ "AS DIFF_IGST_AMT,(B2CS.CGST_AMT-ITM.CGST_AMT) "
				+ "AS DIFF_CGST_AMT,(B2CS.SGST_AMT-ITM.SGST_AMT) "
				+ "AS DIFF_SGST_AMT,(CASE WHEN B2CS.IS_SAVED_TO_GSTN = TRUE "
				+ "THEN 'IS_SAVED' WHEN B2CS.IS_SAVED_TO_GSTN = FALSE "
				+ "AND B2CS.GSTN_ERROR = TRUE THEN 'IS_ERROR' "
				+ "WHEN B2CS.IS_SAVED_TO_GSTN = FALSE AND B2CS.GSTN_ERROR = FALSE "
				+ "THEN 'NOT_SAVED' END) AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID "
				+ "AS GSTIN_REF_ID,GSTNBATCH.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME,"
				+ "TRIM(', ' FROM IFNULL(ASPH_ER.ERROR_CODE_GSTIN,'') ||','|| "
				+ "IFNULL(ASPI_ER.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(ASPH_ER.ERROR_DESCRIPTION_GSTIN,'') "
				+ "||','|| IFNULL(ASPI_ER.ERROR_DESCRIPTION_GSTN,'')) "
				+ "AS GSTIN_ERROR_DESCRIPTION,FIL.SOURCE AS UPLOAD_SOURCE,"
				+ "FIL.CREATED_BY AS SOURCE_ID,FIL.FILE_NAME AS FILE_NAME,"
				+ "B2CS.CREATED_ON AS ASP_DATE_TIME "
				+ "FROM ANX_OUTWARD_DOC_HEADER B2CS LEFT OUTER JOIN "
				+ "TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO () ASPH_ER "
				+ "ON B2CS.ID = ASPH_ER.DOC_HEADER_ID INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON B2CS.ID = ITM.DOC_HEADER_ID "
				+ "AND B2CS.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO() ASPI_ER "
				+ "ON ITM.DOC_HEADER_ID = ASPI_ER.DOC_HEADER_ID AND "
				+ "ITM.ITEM_INDEX = ASPI_ER.ITEM_INDEX AND "
				+ "ITM.ITM_NO = ASPI_ER.ITM_NO INNER JOIN FILE_STATUS "
				+ "FIL ON B2CS.FILE_ID=FIL.ID LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON "
				+ "GSTNBATCH.ID = B2CS.BATCH_ID "
				+ "WHERE B2CS.RETURN_TYPE = 'GSTR1' "
				+ "AND B2CS.IS_PROCESSED=TRUE AND B2CS.IS_DELETE=FALSE "
				+ "AND B2CS.TAX_DOC_TYPE='B2CS' " + buildQuery;
	}
}
*/