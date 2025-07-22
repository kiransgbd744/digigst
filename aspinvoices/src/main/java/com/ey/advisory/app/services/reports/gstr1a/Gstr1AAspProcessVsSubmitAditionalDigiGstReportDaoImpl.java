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

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.views.client.AspProcessVsSubmitAdditionalDigiGstDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
@Component("Gstr1AAspProcessVsSubmitAditionalDigiGstReportDaoImpl")
public class Gstr1AAspProcessVsSubmitAditionalDigiGstReportDaoImpl
		implements Gstr1AAspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	static Integer cutoffPeriod = null;

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

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND HDR.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND HDR.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND HDR.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND HDR.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND HDR.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");

		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString());
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
			List<StateCodeInfoEntity> MasterEntities = statecodeRepository
					.findAll();
			Map<String, String> hsnMap = new HashMap<String, String>();
			MasterEntities.forEach(entity -> {
				hsnMap.put(entity.getStateCode(), entity.getStateName());
			});

			for (Object arr[] : list) {
				verticalHsnList.add(createApiProcessedConvertions(arr, hsnMap));
			}
		}
		return verticalHsnList;
	}

	private AspProcessVsSubmitAdditionalDigiGstDto createApiProcessedConvertions(
			Object[] arr, Map<String, String> map) {
		AspProcessVsSubmitAdditionalDigiGstDto obj = new AspProcessVsSubmitAdditionalDigiGstDto();

		obj.setDigiGstStatus(arr[0] != null ? arr[0].toString() : null);
		obj.setEInvStatus(arr[1] != null ? arr[1].toString() : null);
		obj.setEwbStatus(arr[2] != null ? arr[2].toString() : null);
		obj.setGstnStatus(arr[3] != null ? arr[3].toString() : null);
		obj.setErrorPoint(arr[4] != null ? arr[4].toString() : null);
		obj.setDigiGstErrorCode(arr[5] != null ? arr[5].toString() : null);
		obj.setDigiGstErrorDescription(
				arr[6] != null ? arr[6].toString() : null);
		obj.setDigiGstInfoCode(arr[7] != null ? arr[7].toString() : null);
		obj.setDigiGstInfoDescription(
				arr[8] != null ? arr[8].toString() : null);
		obj.setEInvErrorCode(arr[9] != null ? arr[9].toString() : null);
		obj.setEInvErrorDescription(
				arr[10] != null ? arr[10].toString() : null);
		obj.setEwbInfoCode(arr[11] != null ? arr[11].toString() : null);
		obj.setEwbInfoDescription(arr[12] != null ? arr[12].toString() : null);
		obj.setEwbErrorCode(arr[13] != null ? arr[13].toString() : null);
		obj.setEwbErrorDescription(arr[14] != null ? arr[14].toString() : null);
		obj.setIrnIrp(arr[15] != null ? arr[15].toString() : null);
		obj.setEInvAckNo(arr[16] != null ? arr[16].toString() : null);
		obj.setEInvAckDate(arr[17] != null ? arr[17].toString() : null);
		obj.setEInvAckTime(arr[18] != null ? arr[18].toString() : null);
		obj.setSignedQrCode(arr[19] != null ? arr[19].toString() : null);
		obj.setSignedInv(arr[20] != null ? arr[20].toString() : null);

		obj.setEInvCanDate(arr[21] != null ? arr[21].toString() : null);
		obj.setEInvCanTime(arr[22] != null ? arr[22].toString() : null);
		obj.setEwbNo(arr[23] != null ? arr[23].toString() : null);
		obj.setEwbDate(arr[24] != null ? arr[24].toString() : null);
		obj.setEwbTime(arr[25] != null ? arr[25].toString() : null);
		obj.setEwbValidUptoDate(arr[26] != null ? arr[26].toString() : null);
		obj.setEwbValidUptoTime(arr[27] != null ? arr[27].toString() : null);
		obj.setEwbCanDate(arr[28] != null ? arr[28].toString() : null);
		obj.setEwbCanTime(arr[29] != null ? arr[29].toString() : null);

		obj.setRetType(arr[30] != null ? arr[30].toString() : null);
		obj.setTableNumber(arr[31] != null ? arr[31].toString() : null);
		obj.setGstnRefId(arr[32] != null ? arr[32].toString() : null);
		obj.setGstnRefDate(arr[33] != null ? arr[33].toString() : null);
		obj.setGstnRefTime(arr[34] != null ? arr[34].toString() : null);
		obj.setGstnErrorCode(arr[35] != null ? arr[35].toString() : null);
		obj.setGstnErrorDescription(
				arr[36] != null ? arr[36].toString() : null);
		obj.setIrn(arr[37] != null ? arr[37].toString() : null);
		obj.setIrdDate(arr[38] != null ? arr[38].toString() : null);

		obj.setTaxScheme(arr[39] != null ? arr[39].toString() : null);
		obj.setCanReason(arr[40] != null ? arr[40].toString() : null);
		obj.setCanRemarks(arr[41] != null ? arr[41].toString() : null);
		obj.setSupplyType(arr[42] != null ? arr[42].toString() : null);
		obj.setDocCategory(arr[43] != null ? arr[43].toString() : null);
		obj.setDocType(arr[44] != null ? arr[44].toString() : null);
		obj.setDocNumber(arr[45] != null ? arr[45].toString() : null);
		obj.setDocDate(arr[46] != null ? arr[46].toString() : null);
		obj.setRevChargeFlag(arr[47] != null ? arr[47].toString() : null);

		obj.setSuppGstin(arr[48] != null ? arr[48].toString() : null);
		obj.setSuppTradeName(arr[49] != null ? arr[49].toString() : null);
		obj.setSuppLeagalName(arr[50] != null ? arr[50].toString() : null);
		obj.setSuppAddress1(arr[51] != null ? arr[51].toString() : null);
		obj.setSuppAddress2(arr[52] != null ? arr[52].toString() : null);
		obj.setSuppLocation(arr[53] != null ? arr[53].toString() : null);
		obj.setSuppPincode(arr[54] != null ? arr[54].toString() : null);
		obj.setSuppStateCode(arr[55] != null ? arr[55].toString() : null);
		obj.setSuppPhone(arr[56] != null ? arr[56].toString() : null);
		obj.setSuppEmail(arr[57] != null ? arr[57].toString() : null);

		obj.setCustGstin(arr[58] != null ? arr[58].toString() : null);
		obj.setCustTradeName(arr[59] != null ? arr[59].toString() : null);
		obj.setCustLeagalName(arr[60] != null ? arr[60].toString() : null);
		obj.setCustAddress1(arr[61] != null ? arr[61].toString() : null);
		obj.setCustAddress2(arr[62] != null ? arr[62].toString() : null);
		obj.setCustLocation(arr[63] != null ? arr[63].toString() : null);
		obj.setCustPincode(arr[64] != null ? arr[64].toString() : null);
		obj.setCustStateCode(arr[65] != null ? arr[65].toString() : null);
		obj.setBillingPos(arr[66] != null ? arr[66].toString() : null);
		obj.setCustPhone(arr[67] != null ? arr[67].toString() : null);
		obj.setCustEmail(arr[68] != null ? arr[68].toString() : null);
		obj.setDispGstin(arr[69] != null ? arr[69].toString() : null);

		obj.setDispTradeName(arr[70] != null ? arr[70].toString() : null);
		obj.setDispAddress1(arr[71] != null ? arr[71].toString() : null);
		obj.setDispAddress2(arr[72] != null ? arr[72].toString() : null);
		obj.setDispLocation(arr[73] != null ? arr[73].toString() : null);
		obj.setDispPincode(arr[74] != null ? arr[74].toString() : null);
		obj.setDispStateCode(arr[75] != null ? arr[75].toString() : null);
		obj.setShipToGstin(arr[76] != null ? arr[76].toString() : null);
		obj.setShipToTradeName(arr[77] != null ? arr[77].toString() : null);
		obj.setShipToLegalName(arr[78] != null ? arr[78].toString() : null);
		obj.setShipToAddress1(arr[79] != null ? arr[79].toString() : null);

		obj.setShipToAddress2(arr[80] != null ? arr[80].toString() : null);
		obj.setShipToLocation(arr[81] != null ? arr[81].toString() : null);
		obj.setShipToPincode(arr[82] != null ? arr[82].toString() : null);
		obj.setShipToStateCode(arr[83] != null ? arr[83].toString() : null);
		obj.setItemSerialNumber(arr[84] != null ? arr[84].toString() : null);
		obj.setProductSerialNumber(arr[85] != null ? arr[85].toString() : null);
		obj.setProductName(arr[86] != null ? arr[86].toString() : null);
		obj.setProductDescription(arr[87] != null ? arr[87].toString() : null);
		obj.setIsService(arr[88] != null ? arr[88].toString() : null);
		obj.setHsn(arr[89] != null ? arr[89].toString() : null);

		obj.setBarCode(arr[90] != null ? arr[90].toString() : null);
		obj.setBatchName(arr[91] != null ? arr[91].toString() : null);
		obj.setBatchexpirtDate(arr[92] != null ? arr[92].toString() : null);
		obj.setWarrantyDate(arr[93] != null ? arr[93].toString() : null);
		obj.setOrderLineRef(arr[94] != null ? arr[94].toString() : null);
		obj.setAttName(arr[95] != null ? arr[95].toString() : null);
		obj.setAttValue(arr[96] != null ? arr[96].toString() : null);
		obj.setOriginCountry(arr[97] != null ? arr[97].toString() : null);
		obj.setUqc(arr[98] != null ? arr[98].toString() : null);
		obj.setQnty(arr[99] != null ? arr[99].toString() : null);

		obj.setFreeQnty(arr[100] != null ? arr[100].toString() : null);
		obj.setUnitPrice(arr[101] != null ? arr[101].toString() : null);
		obj.setItemAmnt(arr[102] != null ? arr[102].toString() : null);
		obj.setItemDiscount(arr[103] != null ? arr[103].toString() : null);
		obj.setPreTaxAmt(arr[104] != null ? arr[104].toString() : null);
		obj.setItemAccessableAmt(arr[105] != null ? arr[105].toString() : null);
		obj.setIgstRate(arr[106] != null ? arr[106].toString() : null);
		obj.setIgstAmt(arr[107] != null ? arr[107].toString() : null);
		obj.setCgstRate(arr[108] != null ? arr[108].toString() : null);
		obj.setCgstAmt(arr[109] != null ? arr[109].toString() : null);

		obj.setSgstRate(arr[110] != null ? arr[110].toString() : null);
		obj.setSgstAmt(arr[111] != null ? arr[111].toString() : null);
		obj.setCessAdvRate(arr[112] != null ? arr[112].toString() : null);
		obj.setCessAdvAmt(arr[113] != null ? arr[113].toString() : null);
		obj.setCessSpecificRate(arr[114] != null ? arr[114].toString() : null);
		obj.setCessSpecificAmt(arr[115] != null ? arr[115].toString() : null);
		obj.setStateCessAdvRate(arr[116] != null ? arr[116].toString() : null);
		obj.setStateCessAdvAmt(arr[117] != null ? arr[117].toString() : null);
		obj.setStateCessSpecificRate(
				arr[118] != null ? arr[118].toString() : null);
		obj.setStateCessSpecificAmt(
				arr[119] != null ? arr[119].toString() : null);

		obj.setItemOthCharge(arr[120] != null ? arr[120].toString() : null);
		obj.setTotalItemAmt(arr[121] != null ? arr[121].toString() : null);
		obj.setInvOthCharge(arr[122] != null ? arr[122].toString() : null);
		obj.setInvAccessableAmt(arr[123] != null ? arr[123].toString() : null);
		obj.setInvIgstAmt(arr[124] != null ? arr[124].toString() : null);
		obj.setInvCgstAmt(arr[125] != null ? arr[125].toString() : null);
		obj.setInvSgstAmt(arr[126] != null ? arr[126].toString() : null);
		obj.setInvCessAdvAmt(arr[127] != null ? arr[127].toString() : null);
		obj.setInvCessSpecificAmt(
				arr[128] != null ? arr[128].toString() : null);
		obj.setInvStateCessAdvAmt(
				arr[129] != null ? arr[129].toString() : null);

		obj.setInvStateCessSpecificAmt(
				arr[130] != null ? arr[130].toString() : null);
		obj.setInvValue(arr[131] != null ? arr[131].toString() : null);
		obj.setRoundOff(arr[132] != null ? arr[132].toString() : null);
		obj.setTotInvValue(arr[133] != null ? arr[133].toString() : null);
		obj.setTcsFlagIncomeTax(arr[134] != null ? arr[134].toString() : null);
		obj.setTcsRateIncomeTax(arr[135] != null ? arr[135].toString() : null);
		obj.setTcsAmtIncomeTax(arr[136] != null ? arr[136].toString() : null);
		obj.setCustAdharPan(arr[137] != null ? arr[137].toString() : null);
		obj.setCurrencyCode(arr[138] != null ? arr[138].toString() : null);
		obj.setCountryCode(arr[139] != null ? arr[139].toString() : null);

		obj.setInvValueFc(arr[140] != null ? arr[140].toString() : null);
		obj.setPortCode(arr[141] != null ? arr[141].toString() : null);
		obj.setSippingBillNumber(arr[142] != null ? arr[142].toString() : null);
		obj.setSippingBillDate(arr[143] != null ? arr[143].toString() : null);
		obj.setInvRemarks(arr[144] != null ? arr[144].toString() : null);
		obj.setInvPeriodStartDate(
				arr[145] != null ? arr[145].toString() : null);
		obj.setInvPeriodEndDate(arr[146] != null ? arr[146].toString() : null);
		obj.setPreceedingNumber(arr[147] != null ? arr[147].toString() : null);
		obj.setPreceedingDate(arr[148] != null ? arr[148].toString() : null);
		obj.setOtherRef(arr[149] != null ? arr[149].toString() : null);

		obj.setRecAdviceRef(arr[150] != null ? arr[150].toString() : null);
		obj.setRecAdviceDate(arr[151] != null ? arr[151].toString() : null);
		obj.setTenderRef(arr[152] != null ? arr[152].toString() : null);
		obj.setContractRef(arr[153] != null ? arr[153].toString() : null);
		obj.setExternalRef(arr[154] != null ? arr[154].toString() : null);
		obj.setProjectRef(arr[155] != null ? arr[155].toString() : null);
		obj.setCustPoRefNumber(arr[156] != null ? arr[156].toString() : null);
		obj.setCustPoRefDate(arr[157] != null ? arr[157].toString() : null);
		obj.setPayeeName(arr[158] != null ? arr[158].toString() : null);
		obj.setModeOfPayment(arr[159] != null ? arr[159].toString() : null);

		obj.setBranchOrIfscCode(arr[160] != null ? arr[160].toString() : null);
		obj.setPaymentTerms(arr[161] != null ? arr[161].toString() : null);

		obj.setPaymentInstructions(
				arr[162] != null ? arr[162].toString() : null);
		obj.setCreditTransfer(arr[163] != null ? arr[163].toString() : null);
		obj.setDirectDebit(arr[164] != null ? arr[164].toString() : null);
		obj.setCreditDays(arr[165] != null ? arr[165].toString() : null);
		obj.setPaidAmt(arr[166] != null ? arr[166].toString() : null);
		obj.setBalanceAmt(arr[167] != null ? arr[167].toString() : null);
		obj.setPaymentDueDate(arr[168] != null ? arr[168].toString() : null);
		obj.setAccountDetails(arr[169] != null ? arr[169].toString() : null);
		obj.setEcomGstin(arr[170] != null ? arr[170].toString() : null);
		obj.setEcomTransId(arr[171] != null ? arr[171].toString() : null);

		obj.setSuppDocUrl(arr[172] != null ? arr[172].toString() : null);
		obj.setSuppDoc(arr[173] != null ? arr[173].toString() : null);
		obj.setAddionalInfo(arr[174] != null ? arr[174].toString() : null);
		obj.setTransType(arr[175] != null ? arr[175].toString() : null);
		obj.setSubSuppType(arr[176] != null ? arr[176].toString() : null);
		obj.setOthSupDescription(arr[177] != null ? arr[177].toString() : null);
		obj.setTransporterId(arr[178] != null ? arr[178].toString() : null);
		obj.setTransporterName(arr[179] != null ? arr[179].toString() : null);

		obj.setTransporterMode(arr[180] != null ? arr[180].toString() : null);
		obj.setTransporterDocNo(arr[181] != null ? arr[181].toString() : null);
		obj.setTransporterDocDate(
				arr[182] != null ? arr[182].toString() : null);
		obj.setDistane(arr[183] != null ? arr[183].toString() : null);
		obj.setVehicleNo(arr[184] != null ? arr[184].toString() : null);
		obj.setVehicleType(arr[185] != null ? arr[185].toString() : null);
		obj.setRetPeriod(arr[186] != null ? arr[186].toString() : null);
		obj.setOrgDocType(arr[187] != null ? arr[187].toString() : null);
		obj.setOrgCustGstin(arr[188] != null ? arr[188].toString() : null);
		obj.setDiffPerflag(arr[189] != null ? arr[189].toString() : null);

		obj.setSec7OfIgstFlag(arr[190] != null ? arr[190].toString() : null);
		obj.setClaimRefundFlag(arr[191] != null ? arr[191].toString() : null);
		obj.setAutoPopulateRef(arr[192] != null ? arr[192].toString() : null);
		obj.setCrdrPreGst(arr[193] != null ? arr[193].toString() : null);
		obj.setCustType(arr[194] != null ? arr[194].toString() : null);
		obj.setCustCode(arr[195] != null ? arr[195].toString() : null);
		obj.setProductCode(arr[196] != null ? arr[196].toString() : null);
		obj.setCategoryOfProduct(arr[197] != null ? arr[197].toString() : null);
		obj.setItcFlag(arr[198] != null ? arr[198].toString() : null);
		obj.setStateAppCess(arr[199] != null ? arr[199].toString() : null);
		obj.setFob(arr[200] != null ? arr[200].toString() : null);
		obj.setExportDuty(arr[201] != null ? arr[201].toString() : null);

		obj.setExchangeRate(arr[202] != null ? arr[202].toString() : null);
		obj.setReasonForCrDrNote(arr[203] != null ? arr[203].toString() : null);
		obj.setTcsFlagGst(arr[204] != null ? arr[204].toString() : null);
		obj.setTcsIgsAmt(arr[205] != null ? arr[205].toString() : null);
		obj.setTcsCgsAmt(arr[206] != null ? arr[206].toString() : null);
		obj.setTcsSgsAmt(arr[207] != null ? arr[207].toString() : null);
		obj.setTdsFlagGst(arr[208] != null ? arr[208].toString() : null);
		obj.setTdsIgstAmt(arr[209] != null ? arr[209].toString() : null);

		obj.setTdsCgstAmt(arr[210] != null ? arr[210].toString() : null);
		obj.setTdsSgstAmt(arr[211] != null ? arr[211].toString() : null);
		obj.setUserId(arr[212] != null ? arr[212].toString() : null);
		obj.setCompanyCode(arr[213] != null ? arr[213].toString() : null);
		obj.setSourceId(arr[214] != null ? arr[214].toString() : null);
		obj.setSourceFileName(arr[215] != null ? arr[215].toString() : null);
		obj.setPlantCode(arr[216] != null ? arr[216].toString() : null);
		obj.setDivision(arr[217] != null ? arr[217].toString() : null);
		obj.setSubdivision(arr[218] != null ? arr[218].toString() : null);
		obj.setLocation(arr[219] != null ? arr[219].toString() : null);
		obj.setSalesOrg(arr[220] != null ? arr[220].toString() : null);
		obj.setDistributeChannel(arr[221] != null ? arr[221].toString() : null);
		obj.setProfitCenter1(arr[222] != null ? arr[222].toString() : null);
		obj.setProfitCenter2(arr[223] != null ? arr[223].toString() : null);

		obj.setProfitCenter3(arr[224] != null ? arr[224].toString() : null);
		obj.setProfitCenter4(arr[225] != null ? arr[225].toString() : null);
		obj.setProfitCenter5(arr[226] != null ? arr[226].toString() : null);
		obj.setProfitCenter6(arr[227] != null ? arr[227].toString() : null);
		obj.setProfitCenter7(arr[228] != null ? arr[228].toString() : null);
		obj.setProfitCenter8(arr[229] != null ? arr[229].toString() : null);
		obj.setGlAcesasbleValue(arr[230] != null ? arr[230].toString() : null);
		obj.setGlIgst(arr[231] != null ? arr[231].toString() : null);
		obj.setGlCgst(arr[232] != null ? arr[232].toString() : null);
		obj.setGlSgst(arr[233] != null ? arr[233].toString() : null);
		obj.setGlAdvCess(arr[234] != null ? arr[234].toString() : null);
		obj.setGlSpecificeCess(arr[235] != null ? arr[235].toString() : null);
		obj.setGlStateAdvCess(arr[236] != null ? arr[236].toString() : null);
		obj.setGlStateSpecificeCess(
				arr[237] != null ? arr[237].toString() : null);
		obj.setGlPostingDate(arr[238] != null ? arr[238].toString() : null);
		obj.setSalesOrderNumber(arr[239] != null ? arr[239].toString() : null);

		obj.setEwbNo1(arr[240] != null ? arr[240].toString() : null);
		obj.setEwbDate1(arr[241] != null ? arr[241].toString() : null);
		obj.setAccVoucharNo(arr[242] != null ? arr[242].toString() : null);
		obj.setAccVoucharDate(arr[243] != null ? arr[243].toString() : null);
		obj.setDocRefNo(arr[244] != null ? arr[244].toString() : null);
		obj.setCustTan(arr[245] != null ? arr[245].toString() : null);
		obj.setUserDef1(arr[246] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[246].toString()) : null);
		obj.setUserDef2(arr[247] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[247].toString()) : null);
		obj.setUserDef3(arr[248] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[248].toString()) : null);
		obj.setUserDef4(arr[249] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[249].toString()) : null);

		obj.setUserDef5(arr[250] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[250].toString()) : null);
		obj.setUserDef6(arr[251] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[251].toString()) : null);
		obj.setUserDef7(arr[252] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[252].toString()) : null);
		obj.setUserDef8(arr[253] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[253].toString()) : null);
		obj.setUserDef9(arr[254] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[254].toString()) : null);
		obj.setUserDef10(arr[255] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[255].toString()) : null);
		obj.setUserDef11(arr[256] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[256].toString()) : null);
		obj.setUserDef12(arr[257] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[257].toString()) : null);
		obj.setUserDef13(arr[258] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[258].toString()) : null);
		obj.setUserDef14(arr[259] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[259].toString()) : null);

		obj.setUserDef15(arr[260] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[260].toString()) : null);
		obj.setUserDef16(arr[261] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[261].toString()) : null);
		obj.setUserDef17(arr[262] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[262].toString()) : null);
		obj.setUserDef18(arr[263] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[263].toString()) : null);
		obj.setUserDef19(arr[264] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[264].toString()) : null);
		obj.setUserDef20(arr[265] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[265].toString()) : null);
		obj.setUserDef21(arr[266] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[266].toString()) : null);
		obj.setUserDef22(arr[267] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[267].toString()) : null);
		obj.setUserDef23(arr[268] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[268].toString()) : null);
		obj.setUserDef24(arr[269] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[269].toString()) : null);
		obj.setUserDef25(arr[270] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[270].toString()) : null);
		obj.setUserDef26(arr[271] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[271].toString()) : null);

		obj.setUserDef27(arr[272] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[272].toString()) : null);
		obj.setUserDef28(arr[273] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[273].toString()) : null);
		obj.setUserDef29(arr[274] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[274].toString()) : null);
		obj.setUserDef30(arr[275] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[275].toString()) : null);
		obj.setDerivedSupplyType(arr[276] != null ? arr[276].toString() : null);
		obj.setDistanceProvidedByUser(
				arr[277] != null ? arr[277].toString() : null);

		obj.setDistanceComputedByDigiGst(
				arr[278] != null ? arr[278].toString() : null);
		obj.setDistanceNic(arr[279] != null ? arr[279].toString() : null);

		obj.setUserId(arr[280] != null ? arr[280].toString() : null);
		obj.setFileId(arr[281] != null ? arr[281].toString() : null);
		obj.setFileName(arr[282] != null ? arr[282].toString() : null);
		obj.setInvOthChargeDigiGst(
				arr[283] != null ? arr[283].toString() : null);
		obj.setInvAccesableAmountDigiGst(
				arr[284] != null ? arr[284].toString() : null);
		obj.setInvIgstDigiGst(arr[285] != null ? arr[285].toString() : null);
		obj.setInvCgstDigiGst(arr[286] != null ? arr[286].toString() : null);
		obj.setInvSgstDigiGst(arr[287] != null ? arr[287].toString() : null);
		obj.setInvCessAdvDigiGst(arr[288] != null ? arr[288].toString() : null);
		obj.setInvSpeciCessAdvDigiGst(
				arr[289] != null ? arr[289].toString() : null);

		obj.setInvStateCessAdvDigiGst(
				arr[290] != null ? arr[290].toString() : null);
		obj.setInvStateSpeciCessAdvDigiGst(
				arr[291] != null ? arr[291].toString() : null);
		obj.setInvValueDigiGst(arr[292] != null ? arr[292].toString() : null);
		obj.setIgstDigiGst(arr[293] != null ? arr[293].toString() : null);
		obj.setCgstDigiGst(arr[294] != null ? arr[294].toString() : null);
		obj.setSgstDigiGst(arr[295] != null ? arr[295].toString() : null);
		obj.setCessAdvDigiGst(arr[296] != null ? arr[296].toString() : null);
		obj.setStateCessAdvDigiGst(
				arr[297] != null ? arr[297].toString() : null);
		obj.setIgstDifference(arr[298] != null ? arr[298].toString() : null);
		obj.setCgstDifference(arr[299] != null ? arr[299].toString() : null);
		obj.setSgstDifference(arr[300] != null ? arr[300].toString() : null);
		obj.setCessAdvDifference(arr[301] != null ? arr[301].toString() : null);

		obj.setSpeciCessAdvDifference(
				arr[302] != null ? arr[302].toString() : null);

		obj.setRecordStatus(arr[303] != null ? arr[303].toString() : null);

		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {
		StringBuilder build = new StringBuilder();
		
		
		build.append("SELECT CASE "
				+ "WHEN HDR.asp_invoice_status = 1 THEN 'ASPERROR' "
				+ "WHEN HDR.asp_invoice_status = 2 THEN 'ASPPROCESSED' "
				+ "END AS DIGI_GST_STATUS, "
				+ "CASE "
				+ "WHEN HDR.irn_status = 1 THEN 'Not Opted' "
				+ "WHEN HDR.irn_status = 2 THEN 'Not Applicable' "
				+ "WHEN HDR.irn_status = 3 THEN 'Pending' "
				+ "WHEN HDR.irn_status = 4 THEN 'Generation Error' "
				+ "WHEN HDR.irn_status = 5 THEN 'Generated' "
				+ "WHEN HDR.irn_status = 6 THEN 'Cancelled' "
				+ "WHEN HDR.irn_status = 7 THEN 'ASP Error' "
				+ "WHEN HDR.irn_status = 8 THEN 'Duplicate_Irn' "
				+ "WHEN HDR.irn_status = 10 THEN 'Pushed to NIC' "
				+ "END AS EINV_STATUS, "
				+ "CASE "
				+ "WHEN HDR.ewb_status = 1 THEN 'Not Applicable' "
				+ "WHEN HDR.ewb_status = 2 THEN 'Pending' "
				+ "WHEN HDR.ewb_status = 3 THEN 'Generation Error' "
				+ "WHEN HDR.ewb_status = 4 THEN 'Part A Generated' "
				+ "WHEN HDR.ewb_status = 5 THEN 'EWB Active' "
				+ "WHEN HDR.ewb_status = 6 THEN 'Cancelled' "
				+ "WHEN HDR.ewb_status = 7 THEN 'Discarded' "
				+ "WHEN HDR.ewb_status = 8 THEN 'Rejected' "
				+ "WHEN HDR.ewb_status = 9 THEN 'Expired' "
				+ "WHEN HDR.ewb_status = 10 THEN 'Pushed to NIC' "
				+ "WHEN HDR.ewb_status = 11 THEN 'ASP Error' "
				+ "WHEN HDR.ewb_status = 12 THEN 'Not_Opted' "
				+ "END AS EWB_STATUS, "
				+ "CASE "
				+ "WHEN HDR.is_saved_to_gstn = TRUE THEN 'IS_SAVED' "
				+ "WHEN HDR.is_saved_to_gstn = FALSE "
				+ "AND HDR.gstn_error = TRUE THEN 'IS_ERROR' "
				+ "WHEN HDR.is_saved_to_gstn = FALSE "
				+ "AND HDR.gstn_error = FALSE THEN 'NOT_SAVED' "
				+ "END AS GSTN_STATUS, "
				+ "NULL AS ErrorPoint, "
				+ "Trim(', ' FROM Ifnull(HDR.error_codes, '') "
				+ "||',' "
				+ "|| Ifnull(ITM.error_codes, ''))     AS DIGIGST_ERROR_CODE, "
				+ "''                                                 AS "
				+ "DIGIGST_ERROR_DESCRIPTION, "
				+ "Trim(',' FROM Ifnull(HDR.information_codes, '') "
				+ "||',' "
				+ "||Ifnull(ITM.information_codes, '')) AS DIGIGST_INFO_CODE, "
				+ "''                                                 AS "
				+ "DIGIGST_INFO_DESCRIPTION, "
				+ "HDR.einv_error_code                                AS EINV_ERROR_CODE, "
				+ "HDR.einv_error_desc                                AS "
				+ "EINV_ERROR_DESCRIPTION, "
				+ "HDR.info_error_code                                AS EWB_INFO_CODE, "
				+ "HDR.info_error_msg                                 AS "
				+ "EWB_INFO_DESCRIPTION, "
				+ "HDR.ewb_error_code                                 AS EWB_ERROR_CODE, "
				+ "HDR.ewb_error_desc                                 AS "
				+ "EWB_ERROR_DESCRIPTION, "
				+ "HDR.irn_response                                   AS IRN_NO_IRP, "
				+ "HDR.ack_num                                        AS EINV_ACK_NO, "
				+ "To_char(HDR.ack_date, 'DD-MM-YYYY')                AS EINV_ACK_DATE, "
				+ "To_char(HDR.ack_date, 'HH24:MI:SS')                AS EINV_ACK_TIME, "
				+ "EINV.signed_qr                                     AS SIGNED_QR_CODE, "
				+ "NULL                                               AS SIGNED_INVOICE, "
				+ "To_char(EINV.cancellation_date, 'DD-MM-YYYY')      AS "
				+ "EINV_CANCELLATION_DATE, "
				+ "To_char(EINV.cancellation_date, 'HH24:MI:SS')      AS "
				+ "EINV_CANCELLATION_TIME, "
				+ "HDR.ewb_no_resp                                    AS EWB_NUM, "
				+ "To_char(HDR.ewb_date_resp, 'DD-MM-YYYY')           AS EWB_DATE, "
				+ "To_char(HDR.ewb_date_resp, 'HH24:MI:SS')           AS EWB_TIME, "
				+ "To_char(EWB.valid_upto, 'DD-MM-YYYY')              AS EWBValidUpToDate, "
				+ "To_char(EWB.valid_upto, 'HH24:MI:SS')              AS EWBValidUpToTime, "
				+ "To_char(EWB.cancellation_date, 'DD-MM-YYYY')       AS "
				+ "EWB_CANCELLATION_DATE, "
				+ "To_char(EWB.cancellation_date, 'HH24:MI:SS')       AS "
				+ "EWB_CANCELLATION_TIME, "
				+ "HDR.return_type                                    AS RETURN_TYPE, "
				+ "CASE WHEN HDR.table_section  = '15(i)'   THEN '15(i)' "
				+ "     WHEN HDR.table_section  = '15(iii)'  THEN '15(iii)' "
				+ " ELSE HDR.table_section "
				+ " END AS TABLE_NUMBER, "
				+ "GSTNBATCH.gstn_save_ref_id                         AS GSTIN_REF_ID, "
				+ "To_char(GSTNBATCH.batch_date, 'DD-MM-YYYY')        AS GSTIN_REFID_DATE, "
				+ "To_char(GSTNBATCH.batch_date, 'HH24:MI:SS')        AS GSTIN_REFID_TIME, "
				+ "Trim(', ' FROM Ifnull(HDR.gstn_error_code, ''))    AS GSTN_ERROR_CODE, "
				+ "Trim(', ' FROM Ifnull(HDR.gstn_error_desc, ''))    AS "
				+ "GSTN_ERROR_DESCRIPTION_ASP, "
				+ "HDR.irn, "
				+ "HDR.irn_date, "
				+ "HDR.tax_scheme, "
				+ "HDR.cancel_reason, "
				+ "HDR.cancel_remarks, "
				+ "ITM.supply_type, "
				+ "HDR.doc_category, "
				+ "HDR.doc_type, "
				+ "HDR.doc_num, "
				+ "HDR.doc_date, "
				+ "HDR.reverse_charge, "
				+ "CASE WHEN TABLE_SECTION IN ('15(i)','15(iii)') THEN HDR.ECOM_GSTIN ELSE HDR.supplier_gstin END AS supplier_gstin, "
				+ "HDR.supp_trade_name, "
				+ "HDR.supp_legal_name, "
				+ "HDR.supp_building_num, "
				+ "HDR.supp_building_name AS SUPPLIERADDRESS2, "
				+ "HDR.supp_location, "
				+ "HDR.supp_pincode, "
				+ "HDR.supp_state_code, "
				+ "HDR.supp_phone, "
				+ "HDR.supp_email, "
				+ "HDR.cust_gstin, "
				+ "HDR.cust_trade_name, "
				+ "HDR.cust_supp_name, "
				+ "HDR.cust_supp_address1, "
				+ "HDR.cust_supp_address2, "
				+ "HDR.cust_supp_address4, "
				+ "HDR.cust_pincode, "
				+ "HDR.bill_to_state, "
				+ "HDR.pos, "
				+ "HDR.cust_phone, "
				+ "HDR.cust_email, "
				+ "HDR.dispatcher_gstin, "
				+ "HDR.dispatcher_trade_name, "
				+ "HDR.dispatcher_building_num, "
				+ "HDR.dispatcher_building_name, "
				+ "HDR.dispatcher_location, "
				+ "HDR.dispatcher_pincode, "
				+ "HDR.dispatcher_state_code, "
				+ "HDR.ship_to_gstin, "
				+ "HDR.ship_to_trade_name, "
				+ "HDR.ship_to_legal_name, "
				+ "HDR.ship_to_building_num, "
				+ "HDR.ship_to_building_name, "
				+ "HDR.ship_to_location, "
				+ "ITM.ship_to_pincode, "
				+ "ITM.ship_to_state, "
				+ "ITM.itm_no, "
				+ "ITM.serial_num2, "
				+ "ITM.product_name, "
				+ "ITM.itm_description, "
				+ "ITM.is_service, "
				+ "ITM.itm_hsnsac, "
				+ "ITM.bar_code, "
				+ "ITM.batch_name_or_num, "
				+ "ITM.batch_expiry_date, "
				+ "ITM.warranty_date, "
				+ "ITM.order_item_reference, "
				+ "ITM.attribute_name, "
				+ "ITM.attribute_value, "
				+ "ITM.origin_country, "
				+ "ITM.itm_uqc, "
				+ "ITM.itm_qty, "
				+ "ITM.free_qty, "
				+ "ITM.unit_price, "
				+ "ITM.item_amt_up_qty, "
				+ "ITM.item_discount, "
				+ "ITM.pre_tax_amount, "
				+ "ITM.taxable_value, "
				+ "ITM.igst_rate, "
				+ "ITM.igst_amt, "
				+ "ITM.cgst_rate, "
				+ "ITM.cgst_amt, "
				+ "ITM.sgst_rate, "
				+ "ITM.sgst_amt, "
				+ "ITM.cess_rate_advalorem, "
				+ "ITM.cess_amt_advalorem, "
				+ "ITM.cess_rate_specific, "
				+ "ITM.cess_amt_specific, "
				+ "ITM.statecess_rate, "
				+ "ITM.statecess_amt, "
				+ "ITM.state_cess_specific_rate, "
				+ "ITM.state_cess_specific_amount, "
				+ "ITM.other_values, "
				+ "tot_item_amt, "
				+ "ITM.inv_other_charges, "
				+ "ITM.inv_assessable_amt, "
				+ "ITM.inv_igst_amt, "
				+ "ITM.inv_cgst_amt, "
				+ "ITM.inv_sgst_amt, "
				+ "ITM.inv_cess_advlrm_amt, "
				+ "ITM.inv_cess_specific_amt, "
				+ "ITM.inv_state_cess_amt, "
				+ "ITM.inv_state_cess_specific_amount, "
				+ "ITM.line_item_amt, "
				+ "HDR.round_off, "
				+ "HDR.tot_inv_val_worlds, "
				+ "HDR.tcs_flag_income_tax, "
				+ "ITM.tcs_rate_income_tax, "
				+ "ITM.tcs_amount_income_tax, "
				+ "HDR.customer_pan_or_aadhaar, "
				+ "HDR.foreign_currency, "
				+ "HDR.country_code, "
				+ "HDR.inv_val_fc, "
				+ "HDR.ship_port_code, "
				+ "HDR.ship_bill_num, "
				+ "HDR.ship_bill_date, "
				+ "HDR.inv_remarks, "
				+ "HDR.inv_period_start_date, "
				+ "HDR.inv_period_end_date, "
				+ "ITM.preceeding_inv_num, "
				+ "ITM.preceeding_inv_date, "
				+ "ITM.inv_reference, "
				+ "ITM.receipt_advice_reference, "
				+ "ITM.receipt_advice_date, "
				+ "ITM.tender_reference, "
				+ "ITM.contract_reference, "
				+ "ITM.external_reference, "
				+ "ITM.project_reference, "
				+ "ITM.cust_po_ref_num, "
				+ "ITM.cust_po_ref_date, "
				+ "HDR.payee_name, "
				+ "HDR.mode_of_payment, "
				+ "HDR.branch_ifsc_code, "
				+ "HDR.payment_terms, "
				+ "HDR.payment_instruction, "
				+ "HDR.cr_transfer, "
				+ "HDR.db_direct, "
				+ "HDR.cr_days, "
				+ "ITM.paid_amt, "
				+ "ITM.bal_amt, "
				+ "HDR.payment_due_date, "
				+ "HDR.account_detail, "
				+ "CASE WHEN TABLE_SECTION IN ('15(i)','15(iii)') THEN HDR.SUPPLIER_GSTIN ELSE  HDR.ecom_gstin END AS ecom_gstin , "
				+ "HDR.ecom_trans_id, "
				+ "ITM.supporting_doc_url, "
				+ "ITM.supporting_doc_base64, "
				+ "ITM.additional_information, "
				+ "HDR.trans_type, "
				+ "HDR.sub_supp_type, "
				+ "HDR.other_supp_type_desc, "
				+ "HDR.transporter_id, "
				+ "HDR.transporter_name, "
				+ "HDR.transport_mode, "
				+ "HDR.transport_doc_num, "
				+ "HDR.transport_doc_date, "
				+ "HDR.distance, "
				+ "HDR.vehicle_num, "
				+ "HDR.vehicle_type, "
				+ "HDR.return_period, "
				+ "HDR.original_doc_type, "
				+ "HDR.original_cust_gstin, "
				+ "HDR.diff_percent, "
				+ "HDR.section7_of_igst_flag, "
				+ "HDR.claim_refund_flag, "
				+ "HDR.autopopulate_to_refund, "
				+ "HDR.crdr_pre_gst, "
				+ "HDR.cust_supp_type, "
				+ "HDR.cust_supp_code  AS CUSTOMERCODE, "
				+ "ITM.product_code, "
				+ "ITM.itm_type, "
				+ "ITM.itc_flag, "
				+ "HDR.state_applying_cess, "
				+ "ITM.fob, "
				+ "ITM.export_duty, "
				+ "HDR.exchange_rate, "
				+ "ITM.crdr_reason, "
				+ "HDR.tcs_flag, "
				+ "ITM.tcs_amt, "
				+ "tcs_cgst_amt, "
				+ "tcs_sgst_amt, "
				+ "HDR.tds_flag, "
				+ "tds_igst_amt, "
				+ "tds_cgst_amt, "
				+ "tds_sgst_amt, "
				+ "HDR.user_id  AS userid, "
				+ "HDR.company_code, "
				+ "HDR.source_identifier, "
				+ "HDR.source_filename, "
				+ "ITM.plant_code, "
				+ "HDR.division, "
				+ "sub_division, "
				+ "ITM.location, "
				+ "HDR.sales_organization, "
				+ "HDR.distribution_channel, "
				+ "ITM.profit_centre, "
				+ "HDR.profit_centre2, "
				+ "HDR.useraccess1, "
				+ "HDR.useraccess2, "
				+ "HDR.useraccess3, "
				+ "HDR.useraccess4, "
				+ "HDR.useraccess5, "
				+ "HDR.useraccess6, "
				+ "ITM.glcode_taxablevalue, "
				+ "ITM.glcode_igst, "
				+ "ITM.glcode_cgst, "
				+ "ITM.glcode_sgst, "
				+ "ITM.glcode_adv_cess, "
				+ "ITM.glcode_sp_cess, "
				+ "ITM.glcode_state_cess, "
				+ "HDR.gl_state_cess_specific, "
				+ "HDR.gl_posting_date, "
				+ "HDR.sales_ord_num, "
				+ "HDR.eway_bill_num, "
				+ "Cast(HDR.eway_bill_date AS DATE), "
				+ "HDR.accounting_voucher_num, "
				+ "HDR.accounting_voucher_date, "
				+ "ITM.document_reference_number, "
				+ "HDR.cust_tan, "
				+ "ITM.userdefined_field1, "
				+ "ITM.userdefined_field2, "
				+ "ITM.userdefined_field3, "
				+ "ITM.userdefined_field4, "
				+ "ITM.userdefined_field5, "
				+ "ITM.userdefined_field6, "
				+ "ITM.userdefined_field7, "
				+ "ITM.userdefined_field8, "
				+ "ITM.userdefined_field9, "
				+ "ITM.userdefined_field10, "
				+ "ITM.userdefined_field11, "
				+ "ITM.userdefined_field12, "
				+ "ITM.userdefined_field13, "
				+ "ITM.userdefined_field14, "
				+ "ITM.userdefined_field15, "
				+ "ITM.userdefined_field16, "
				+ "ITM.userdefined_field17, "
				+ "ITM.userdefined_field18, "
				+ "ITM.userdefined_field19, "
				+ "ITM.userdefined_field20, "
				+ "ITM.userdefined_field21, "
				+ "ITM.userdefined_field22, "
				+ "ITM.userdefined_field23, "
				+ "ITM.userdefined_field24, "
				+ "ITM.userdefined_field25, "
				+ "ITM.userdefined_field26, "
				+ "ITM.userdefined_field27, "
				+ "ITM.userdefined_field28, "
				+ "ITM.userdefined_field29, "
				+ "ITM.userdefined_field30, "
				+ "HDR.supply_type AS DIGIGST_SUPPLY_TYPE, "
				+ "EWB. asp_distance                                  AS "
				+ "USER_GIVEN_DISTANCE, "
				+ "EWB.remaining_distance                             AS "
				+ "DigiGST_Computed_Distance, "
				+ "''   NIC_Distance, "
				+ "HDR.created_by    AS USER_ID, "
				+ "HDR.file_id, "
				+ "FIL.file_name                                      AS FILE_NAME, "
				+ "HDR.inv_other_charges                              AS "
				+ "InvoiceOtherChargesDigigst, "
				+ "HDR.inv_assessable_amt                             AS "
				+ "InvoiceAssessableAmount_DigiGST, "
				+ "HDR.inv_igst_amt                                   AS "
				+ "InvoiceIGSTAmount_DigiGST, "
				+ " HDR.inv_cgst_amt                                   AS "
				+ "InvoiceCGSTAmount_DigiGST, "
				+ "HDR.inv_sgst_amt                                   AS "
				+ "InvoiceSGSTAmount_DigiGST, "
				+ "HDR.inv_cess_advlrm_amt                            AS "
				+ "InvoiceCessAdvaloremAmount_DigiGST, "
				+ "HDR.inv_cess_specific_amt                          AS "
				+ "InvoiceCessSpecificAmount_DigiGST, "
				+ "HDR.inv_state_cess_amt                             AS "
				+ "InvoiceStateCessAdvaloremAmount_DigiGST, "
				+ "HDR.inv_state_cess_specific_amount                 AS "
				+ "InvoiceStateCessSpecificAmount_DigiGST, "
				+ "HDR.doc_amt                                        InvoiceValue_DigiGST, "
				+ "HDR.memo_value_igst                                IGSTAmount_DigiGST, "
				+ "HDR.memo_value_cgst                                CGSTAmount_DigiGST, "
				+ "HDR.memo_value_sgst                                SGSTAmount_DigiGST, "
				+ "ITM.memo_value_cess_adv                            AS "
				+ "CessAdvaloremAmount_DigiGST, "
				+ "ITM.memo_value_state_cess_adv                      AS "
				+ "StateCessAdvaloremAmount_DigiGST, "
				+ "ITM.igst_ret1_impact          IGSTAmount_Difference, "
				+ "ITM.cgst_ret1_impact          CGSTAmount_Difference, "
				+ "ITM.sgst_ret1_impact          SGSTAmount_Difference, "
				+ "ITM.cess_adv_ret1_impact                           AS "
				+ "CessAdvaloremAmount_Difference, "
				+ "ITM.state_cess_adv_ret1_impact                     AS "
				+ "StateCessAdvaloremAmount_Difference, "
				+ "CASE "
				+ "WHEN HDR.is_delete = TRUE THEN 'INACTIVE' "
				+ "WHEN HDR.is_delete = FALSE THEN 'ACTIVE' "
				+ "END                                                AS RECORD_STATUS "
				+ "FROM   anx_outward_doc_header_1a HDR "
				+ "inner join anx_outward_doc_item_1a ITM "
				+ "ON HDR.id = ITM.doc_header_id "
				+ "AND HDR.derived_ret_period = ITM.derived_ret_period "
				+ "inner join (select TABLE_ID from GSTR1A_SUBMITTED_PS_TRANS WHERE "
				+ "REPORT_TYPE='ADDITIONAL IN DIGIGST' AND IS_DELETE = FALSE) TRANS "
				+ "ON HDR.ID = TRANS.TABLE_ID "
				+ "left outer join file_status FIL "
				+ "ON HDR.file_id = FIL.id "
				+ "left outer join gstr1_gstn_save_batch GSTNBATCH "
				+ "ON GSTNBATCH.id = HDR.batch_id "
				+ "left outer join einv_master EINV "
				+ "ON HDR.irn_response = EINV.irn "
				+ "left outer join ewb_master EWB "
				+ "ON HDR.ewb_no_resp = EWB.ewb_num "
				+ "WHERE  HDR.asp_invoice_status = 2 "
				+ "AND compliance_applicable = TRUE "
				+ "AND HDR.is_delete = FALSE "
//				+ "AND HDR.TCS_FLAG = 'O' "
				+ "AND HDR.return_type = 'GSTR1A' "
//				+ "AND HDR.TABLE_SECTION IN ('15(i)','15(iii)') "
				+ buildQuery);
		
		return build.toString();
	}
}
