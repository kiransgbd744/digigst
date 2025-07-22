package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1EntityLevelSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ANX1EntityLevelSummaryDaoImpl")
public class ANX1EntityLevelSummaryDaoImpl
		implements ANX1EntityLevelSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1EntityLevelSummaryDto> getEntityLevelSummary(
			Gstr1ReviwSummReportsReqDto request) {

		String taxPeriod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
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

		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			buildQuery.append(" DERIVED_RET_PERIOD_ASP = :taxPeriod");
		}

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND GSTIN_NO IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE IN :pcList");
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

		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			int derivedTaxPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxPeriod", derivedTaxPeriodFrom);
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
		List<Anx1EntityLevelSummaryDto> retList = list.parallelStream()
				.map(o -> convertEntityLevelSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx1EntityLevelSummaryDto convertEntityLevelSummary(Object[] arr) {
		Anx1EntityLevelSummaryDto obj = new Anx1EntityLevelSummaryDto();

		obj.setGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setTableNo(arr[1] != null ? arr[1].toString() : null);
		obj.setTableDescription(arr[2] != null ? arr[2].toString() : null);
		int AspCount = (GenUtil.getBigInteger(arr[3])).intValue();
		obj.setAspCount(AspCount);

		BigDecimal bigDecimalAspTaxableValue = (BigDecimal) arr[4];

		BigDecimal aspTaxableValue = new BigDecimal(
				bigDecimalAspTaxableValue.longValue());
		obj.setAspTaxableValue(aspTaxableValue);
		obj.setAspSuppliesReturned(arr[5] != null ? arr[5].toString() : null);
		obj.setAspNetValue(arr[6] != null ? arr[6].toString() : null);
		BigDecimal bigDecimalAspIGST = (BigDecimal) arr[7];
		if (bigDecimalAspIGST != null) {
			BigDecimal aspIGST = new BigDecimal(bigDecimalAspIGST.longValue());
			obj.setAspIGST(aspIGST);
		}
		BigDecimal bigDecimalAspCGST = (BigDecimal) arr[8];
		if (bigDecimalAspCGST != null) {
			BigDecimal aspCGST = new BigDecimal(bigDecimalAspCGST.longValue());
			obj.setAspCGST(aspCGST);
		}
		BigDecimal bigDecimalAspSGST = (BigDecimal) arr[9];
		if (bigDecimalAspSGST != null) {
			BigDecimal aspSGST = new BigDecimal(bigDecimalAspSGST.longValue());
			obj.setAspSGST(aspSGST);
		}
		BigDecimal bigDecimalAspCess = (BigDecimal) arr[10];
		if (bigDecimalAspCess != null) {
			BigDecimal aspCess = new BigDecimal(bigDecimalAspCess.longValue());
			obj.setAspCess(aspCess);
		}
		BigDecimal bigDecimalAspInvoiceValue = (BigDecimal) arr[11];

		if (bigDecimalAspInvoiceValue != null) {
			BigDecimal aspInvoiceValue = new BigDecimal(
					bigDecimalAspInvoiceValue.longValue());
			obj.setAspInvoiceValue(aspInvoiceValue);
		}

		/*
		 * int Memocount = (GenUtil.getBigInteger(arr[13])).intValue();
		 * obj.setAspCount(Memocount);
		 */

		BigInteger Memocount = GenUtil.getBigInteger(arr[13]);
		if (Memocount != null && Memocount.intValue() != 0) {
			obj.setMemoCount(Memocount.intValue());
		}

		BigDecimal bigDecimalMemoTaxableValue = (BigDecimal) arr[14];
		if (bigDecimalMemoTaxableValue != null) {

			BigDecimal memoTaxableValue = new BigDecimal(
					bigDecimalMemoTaxableValue.longValue());
			obj.setMemoTaxableValue(memoTaxableValue);
		}

		BigDecimal bigDecimalMemoSuppliesReturned = (BigDecimal) arr[15];
		if (bigDecimalMemoSuppliesReturned != null) {

			BigDecimal memoSuppliesReturned = new BigDecimal(
					bigDecimalMemoTaxableValue.longValue());
			obj.setMemoSuppliesReturned(memoSuppliesReturned);
		}

		BigDecimal bigDecimalMemoNetValue = (BigDecimal) arr[16];
		if (bigDecimalMemoNetValue != null) {

			BigDecimal memoNetValue = new BigDecimal(
					bigDecimalMemoNetValue.longValue());
			obj.setMemoNetValue(memoNetValue);
		}
		BigDecimal bigDecimalMemoIGST = (BigDecimal) arr[17];
		if (bigDecimalMemoIGST != null) {

			BigDecimal memoIGST = new BigDecimal(
					bigDecimalMemoIGST.longValue());
			obj.setMemoIGST(memoIGST);
		}
		BigDecimal bigDecimalMemoCGST = (BigDecimal) arr[18];

		if (bigDecimalMemoCGST != null) {

			BigDecimal memoCGST = new BigDecimal(
					bigDecimalMemoCGST.longValue());
			obj.setMemoCGST(memoCGST);
		}
		BigDecimal bigDecimalMemoSGST = (BigDecimal) arr[19];
		if (bigDecimalMemoSGST != null) {

			BigDecimal memoSGST = new BigDecimal(
					bigDecimalMemoSGST.longValue());
			obj.setMemoSGST(memoSGST);
		}

		Integer bigDecimalMemoCess = (Integer) arr[20];
		if (bigDecimalMemoCess != null && bigDecimalMemoCess.intValue() != 0) {

			BigDecimal memoCess = new BigDecimal(bigDecimalMemoCess.intValue());
			obj.setMemoCess(memoCess);
		} else {
			obj.setMemoCess(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalMemoInvoiceValue = (BigDecimal) arr[21];

		if (bigDecimalMemoInvoiceValue != null) {
			BigDecimal memoInvoiceValue = new BigDecimal(
					bigDecimalMemoInvoiceValue.longValue());
			obj.setMemoInvoiceValue(memoInvoiceValue);
		}
		BigDecimal bigDecimalAvailableInvoiceValue = (BigDecimal) arr[31];

		if (bigDecimalAvailableInvoiceValue != null) {
			BigDecimal availableInvoiceValue = new BigDecimal(
					bigDecimalAvailableInvoiceValue.longValue());
			obj.setAvailableInvoiceValue(availableInvoiceValue);
		}
		BigDecimal bigDecimalAvailableTaxableValue = (BigDecimal) arr[24];
		if (bigDecimalAvailableTaxableValue != null) {

			BigDecimal availableTaxableValue = new BigDecimal(
					bigDecimalAvailableTaxableValue.longValue());
			obj.setAvailableTaxableValue(availableTaxableValue);
		}
		Integer bigDecimalAvailableSuppliesReturned = (Integer) arr[25];
		if (bigDecimalAvailableTaxableValue != null
				&& bigDecimalAvailableTaxableValue.intValue() != 0) {

			BigDecimal availableSuppliesReturned = new BigDecimal(
					bigDecimalAvailableSuppliesReturned.longValue());
			obj.setAvailableSuppliesReturned(availableSuppliesReturned);
		} else {
			obj.setAvailableSuppliesReturned(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalAvailableNetValue = (BigDecimal) arr[26];
		if (bigDecimalAvailableTaxableValue != null) {

			BigDecimal availableNetValue = new BigDecimal(
					bigDecimalAvailableNetValue.longValue());
			obj.setAvailableNetValue(availableNetValue);
		}

		BigDecimal bigDecimalAvailableIGST = (BigDecimal) arr[27];
		if (bigDecimalAvailableIGST != null) {

			BigDecimal availableIGST = new BigDecimal(
					bigDecimalAvailableIGST.longValue());
			obj.setAvailableIGST(availableIGST);
		}
		BigDecimal bigDecimalAvailableCGST = (BigDecimal) arr[28];

		if (bigDecimalAvailableCGST != null) {

			BigDecimal availableCGST = new BigDecimal(
					bigDecimalAvailableCGST.longValue());
			obj.setAvailableCGST(availableCGST);
		}
		BigDecimal bigDecimalAvailableSGST = (BigDecimal) arr[29];

		if (bigDecimalAvailableSGST != null) {

			BigDecimal availableSGST = new BigDecimal(
					bigDecimalAvailableSGST.longValue());
			obj.setAvailableSGST(availableSGST);
		}
		BigDecimal bigDecimalAvailableCess = (BigDecimal) arr[30];

		if (bigDecimalAvailableCess != null) {

			BigDecimal availableCess = new BigDecimal(
					bigDecimalAvailableCess.longValue());
			obj.setAvailableCess(availableCess);
		}
		int Avialablecount = ((BigDecimal) arr[21]).intValue();
		obj.setAvailableCount(Avialablecount);

		BigDecimal memoTaxableval = BigDecimal.ZERO;
		BigDecimal avilTaxableval = BigDecimal.ZERO;
		BigDecimal diffTaxableval = BigDecimal.ZERO;
		BigDecimal MemoSuppliesReturned = BigDecimal.ZERO;
		BigDecimal AvailableSuppliesReturned = BigDecimal.ZERO;
		BigDecimal diffSuppliesReturned = BigDecimal.ZERO;
		BigDecimal memoCess = BigDecimal.ZERO;
		BigDecimal availableCess = BigDecimal.ZERO;
		BigDecimal diffCess = BigDecimal.ZERO;
		BigDecimal memoIGST = BigDecimal.ZERO;
		BigDecimal availableIGST = BigDecimal.ZERO;
		BigDecimal diffIGST = BigDecimal.ZERO;

		if (obj.getMemoTaxableValue() != null) {
			memoTaxableval = obj.getMemoTaxableValue();
		}
		if (obj.getAvailableTaxableValue() != null) {
			avilTaxableval = obj.getAvailableInvoiceValue();
		}
		diffTaxableval = avilTaxableval.subtract(memoTaxableval);
		obj.setDiffTaxableValue(diffTaxableval);

		if (obj.getMemoSuppliesReturned() != null) {
			MemoSuppliesReturned = obj.getMemoSuppliesReturned();

		}

		if (obj.getAvailableSuppliesReturned() != null) {
			AvailableSuppliesReturned = obj.getAvailableSuppliesReturned();
		}

		diffSuppliesReturned = AvailableSuppliesReturned
				.subtract(MemoSuppliesReturned);
		obj.setDiffSuppliesReturned(diffSuppliesReturned);

		if (obj.getMemoCess() != null) {
			memoCess = obj.getMemoCess();
		}
		if (obj.getAvailableCess() != null) {
			availableCess = obj.getAvailableCess();
		}
		diffCess = availableCess.subtract(memoIGST);
		obj.setDiffCess(diffCess);

		if (obj.getMemoIGST() != null) {
			memoIGST = obj.getMemoIGST();
		}
		if (obj.getAvailableIGST() != null) {
			availableIGST = obj.getAvailableIGST();
		}
		diffIGST = availableIGST.subtract(memoIGST);
		obj.setDiffIGST(diffIGST);

		// Difference calculation
		obj.setDiffCount(obj.getAvailableCount().intValue() != 0
				? obj.getAvailableCount() - obj.getMemoCount() : 0);
		obj.setDiffTaxableValue(
				obj.getAvailableTaxableValue() != BigDecimal.ZERO
						? obj.getAvailableTaxableValue().subtract(
								obj.getMemoTaxableValue())
						: BigDecimal.ZERO);
		obj.setDiffSuppliesReturned(
				obj.getAvailableSuppliesReturned() != BigDecimal.ZERO
						? obj.getAvailableSuppliesReturned()
								.subtract(obj.getMemoSuppliesReturned())
						: BigDecimal.ZERO);
		obj.setDiffNetValue(obj.getAvailableNetValue() != BigDecimal.ZERO
				? obj.getAvailableNetValue().subtract(obj.getMemoNetValue())
				: BigDecimal.ZERO);
		obj.setDiffIGST(obj.getAvailableIGST() != BigDecimal.ZERO
				? obj.getAvailableIGST().subtract(obj.getMemoIGST())
				: BigDecimal.ZERO);
		obj.setDiffCGST(obj.getAvailableCGST() != BigDecimal.ZERO
				? obj.getAvailableCGST().subtract(obj.getMemoCGST())
				: BigDecimal.ZERO);
		obj.setDiffSGST(obj.getAvailableSGST() != BigDecimal.ZERO
				? obj.getAvailableSGST().subtract(obj.getMemoSGST())
				: BigDecimal.ZERO);
		obj.setDiffCess(obj.getAvailableCess() != BigDecimal.ZERO
				? obj.getAvailableCess().subtract(obj.getMemoCess())
				: BigDecimal.ZERO);
		obj.setDiffInvoiceValue(
				obj.getAvailableInvoiceValue() != BigDecimal.ZERO
						? obj.getAvailableInvoiceValue().subtract(
								obj.getMemoInvoiceValue())
						: BigDecimal.ZERO);
		return obj;

	}

	private String EntityLevelSummaryQueryString(String buildQuery) {

		return "SELECT GSTIN_NO,TABLE_SECTION,DESCRIPTION,SUM(ASP_COUNT) ASP_COUNT,"
				+ "SUM(ASP_TAXABLEVALUE_OR_SUPPLIESMADE) "
				+ "ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "SUM(ASP_SUPPLIES_RETURNED) ASP_SUPPLIES_RETURNED,"
				+ "SUM(ASP_NETVALUE) ASP_NETVALUE,SUM(ASP_IGST) AS ASP_IGST,"
				+ "SUM(ASP_CGST) ASP_CGST,SUM(ASP_SGST) ASP_SGST,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "DERIVED_RET_PERIOD_ASP," + "SUM(MEMO_COUNT) MEMO_COUNT,"
				+ "SUM(MEMO_TAXABLEVALUE_OR_SUPPLIESMADE) "
				+ "MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "SUM(MEMO_SUPPLIES_RETURNED) MEMO_SUPPLIES_RETURNED,"
				+ "SUM(MEMO_NET_VALUE) MEMO_NET_VALUE,SUM(MEMO_IGST) MEMO_IGST,"
				+ "SUM(MEMO_CGST) MEMO_CGST,SUM(MEMO_SGST) MEMO_SGST,"
				+ "SUM(MEMO_CESS) MEMO_CESS,SUM(MEMO_INVOICE_VALUE) "
				+ "MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD_MEMO,"
				+ "SUM(GSTN_COUNT) GSTN_COUNT,"
				+ "SUM(GSTN_TAXABLEVALUE_OR_SUPPLIESMADE) "
				+ "GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "SUM(GSTN_SUPPLIES_RETURNED) GSTN_SUPPLIES_RETURNED,"
				+ "SUM(GSTN_NET_VALUE) GSTN_NET_VALUE,SUM(GSTN_IGST) GSTN_IGST,"
				+ "SUM(GSTN_CGST) GSTN_CGST,SUM(GSTN_SGST) GSTN_SGST,"
				+ "SUM(GSTN_CESS) GSTN_CESS,SUM(GSTN_INVOICE_VALUE) GSTN_INVOICE_VALUE,"
				+ "DERIVED_RET_PERIOD_GSTIN "
				+ " FROM( SELECT GSTIN_NO AS GSTIN_NO,TABLE_SECTION AS TABLE_SECTION,"
				+ "DESCRIPTION AS DESCRIPTION,ASP_COUNT,"
				+ "ASP_TAXABLEVALUE_OR_SUPPLIESMADE,ASP_SUPPLIES_RETURNED,"
				+ "ASP_NETVALUE,ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS,"
				+ "ASP_INVOICE_VALUE,DERIVED_RET_PERIOD AS DERIVED_RET_PERIOD_ASP,"
				+ "MEMO_COUNT,"
				+ "MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,MEMO_SUPPLIES_RETURNED,"
				+ "MEMO_NET_VALUE,MEMO_IGST,MEMO_CGST,MEMO_SGST,MEMO_CESS,"
				+ "MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD AS DERIVED_RET_PERIOD_MEMO,"
				+ "0 AS GSTN_COUNT,"
				+ "0 AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS GSTN_SUPPLIES_RETURNED,"
				+ "0 AS GSTN_NET_VALUE,0 AS GSTN_IGST,0 AS GSTN_CGST,0 AS GSTN_SGST,"
				+ "0 AS GSTN_CESS,0 AS GSTN_INVOICE_VALUE,"
				+ " NULL AS DERIVED_RET_PERIOD_GSTIN "
				+ "FROM( SELECT SUPPLIER_GSTIN as GSTIN_NO, AN_TABLE_SECTION "
				+ "AS TABLE_SECTION,"
				+ " 'Supplies made to consumers and un-registered persons "
				+ "(Net of debit / credit notes)' AS DESCRIPTION,"
				+ "COUNT(ID) AS ASP_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST,"
				+ "SUM(SGST_AMT) AS ASP_SGST,"
				+ "SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC) AS ASP_CESS,"
				+ "SUM(DOC_AMT) AS ASP_INVOICE_VALUE,COUNT(ID) AS MEMO_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "SUM(MEMO_VALUE_IGST) AS MEMO_IGST,SUM(MEMO_VALUE_CGST) "
				+ "AS MEMO_CGST,SUM(MEMO_VALUE_SGST) AS MEMO_SGST,0 AS MEMO_CESS,"
				+ "SUM(TAXABLE_VALUE+MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) "
				+ "AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_OUTWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3A') "
				+ "AND IS_SAVED_TO_GSTN = TRUE " + " GROUP BY "
				+ "AN_TABLE_SECTION,SUPPLIER_GSTIN,DERIVED_RET_PERIOD"
				+ " UNION "
				+ "SELECT SUPPLIER_GSTIN as GSTIN_NO,'3A' AS TABLE_SECTION,"
				+ "'Supplies made to consumers and un-registered persons "
				+ "(Net of debit / credit notes)' AS DESCRIPTION,"
				+ "COUNT(ID) AS ASP_COUNT,SUM(TAXABLE_VALUE) "
				+ "AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "SUM ( IGST_AMT) AS ASP_IGST,SUM ( CGST_AMT) AS ASP_CGST,"
				+ "SUM ( SGST_AMT) AS ASP_SGST,SUM( CESS_AMT) AS ASP_CESS,"
				+ "SUM (TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS ASP_INVOICE_VALUE,"
				+ "0 AS MEMO_COUNT, 0 AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "0 AS MEMO_IGST,0 AS MEMO_CGST,0 AS MEMO_SGST,"
				+ "0 AS MEMO_CESS,0 AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_GSTN_B2C "
				+ " GROUP BY SUPPLIER_GSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SUPPLIER_GSTIN as GSTIN_NO, "
				+ "AN_TABLE_SECTION AS TABLE_SECTION,"
				+ "(CASE WHEN AN_TABLE_SECTION ='3B' THEN 'Supplies made to "
				+ "registered persons (other than those attracting reverse charge)"
				+ "(including edit/amendment)' "
				+ "WHEN AN_TABLE_SECTION ='3C' THEN 'Exports with payment of tax' "
				+ "WHEN AN_TABLE_SECTION ='3D' THEN 'Exports without payment of tax' "
				+ "WHEN AN_TABLE_SECTION ='3E' THEN 'Supplies "
				+ "to SEZ units/developers with payment of tax "
				+ "(including edit/amendment)' WHEN AN_TABLE_SECTION ='3F' "
				+ "THEN 'Supplies to SEZ units/developers without "
				+ "payment of tax (including edit/amendment)' "
				+ "WHEN AN_TABLE_SECTION ='3G' THEN 'Deemed "
				+ "exports (including edit/amendment)' END) "
				+ "AS DESCRIPTION,COUNT(ID) AS ASP_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST,"
				+ "SUM(SGST_AMT) AS ASP_SGST,"
				+ "SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC) AS ASP_CESS,"
				+ "SUM(DOC_AMT) AS ASP_INVOICE_VALUE,COUNT(ID) AS MEMO_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "SUM(MEMO_VALUE_IGST) AS MEMO_IGST,"
				+ "SUM(MEMO_VALUE_CGST) AS MEMO_CGST,"
				+ "SUM(MEMO_VALUE_SGST) AS MEMO_SGST,0 AS MEMO_CESS,"
				+ "SUM(TAXABLE_VALUE+MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) "
				+ "AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_OUTWARD_DOC_HEADER " + "WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE AND IS_SAVED_TO_GSTN = TRUE "
				+ "AND AN_TABLE_SECTION IN ('3B','3C','3D','3E','3F','3G')"
				+ " GROUP BY AN_TABLE_SECTION,SUPPLIER_GSTIN,DERIVED_RET_PERIOD"
				+ " UNION "
				+ "SELECT ECOM_GSTIN as GSTIN_NO, '4' AS TABLE_SECTION,"
				+ " 'Supplies made through E-Commerce Operators' AS DESCRIPTION,"
				+ "COUNT(ID) AS ASP_COUNT,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN 0 ELSE TAXABLE_VALUE END) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN TAXABLE_VALUE ELSE 0 END) "
				+ "AS ASP_SUPPLIES_RETURNED,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN TAXABLE_VALUE*-1 ELSE TAXABLE_VALUE END) AS ASP_NETVALUE,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN IGST_AMT*-1 ELSE IGST_AMT END) "
				+ "AS ASP_IGST,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN CGST_AMT*-1 ELSE CGST_AMT END) AS ASP_CGST,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN SGST_AMT*-1 ELSE SGST_AMT END) "
				+ "AS ASP_SGST,"
				+ "(SUM(CASE WHEN DOC_TYPE='CR' THEN CESS_AMT_SPECIFIC*-1 "
				+ "ELSE CESS_AMT_SPECIFIC END) +SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN CESS_AMT_ADVALOREM*-1 ELSE CESS_AMT_ADVALOREM END)) "
				+ "AS ASP_CESS,SUM(DOC_AMT) AS ASP_INVOICE_VALUE,COUNT(ID) "
				+ "AS MEMO_COUNT,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN 0 ELSE TAXABLE_VALUE END) AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN TAXABLE_VALUE ELSE 0 END) "
				+ "AS MEMO_SUPPLIES_RETURNED,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN TAXABLE_VALUE*-1 ELSE TAXABLE_VALUE END) AS MEMO_NET_VALUE,"
				+ "SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_IGST*-1 "
				+ "ELSE MEMO_VALUE_IGST END ) AS MEMO_IGST,SUM(CASE "
				+ "WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST*-1 ELSE MEMO_VALUE_CGST END ) "
				+ "AS MEMO_CGST,SUM(CASE WHEN DOC_TYPE='CR' "
				+ "THEN MEMO_VALUE_SGST*-1 ELSE MEMO_VALUE_SGST END ) AS MEMO_SGST,"
				+ "0 AS MEMO_CESS,"
				+ "SUM(TAXABLE_VALUE+MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) "
				+ "AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_OUTWARD_DOC_HEADER " + "WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3A','3B','3C','3D','3E','3F','3G') "
				+ "AND ECOM_GSTIN IS NOT NULL "
				+ "AND TCS_FLAG = 'Y' AND IS_SAVED_TO_GSTN = TRUE "
				+ " GROUP BY AN_TABLE_SECTION,ECOM_GSTIN,DERIVED_RET_PERIOD "
				+ " UNION "
				+ "SELECT ECOM_GSTIN as GSTIN_NO,'4' AS TABLE_SECTION,"
				+ " 'Supplies made through E-Commerce Operators'AS DESCRIPTION,"
				+ "COUNT(ID) AS ASP_COUNT,"
				+ "SUM(ECOM_VAL_SUPMADE) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "SUM(ECOM_VAL_SUPRET) AS ASP_SUPPLIES_RETURNED,"
				+ "SUM(ECOM_NETVAL_SUP) AS ASP_NETVALUE, "
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST, "
				+ "SUM(SGST_AMT) AS ASP_SGST, SUM(CESS_AMT) AS ASP_CESS, "
				+ "0 AS ASP_INVOICE_VALUE,0 AS MEMO_COUNT,"
				+ "0 AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "0 AS MEMO_IGST,0 AS MEMO_CGST,0 AS MEMO_SGST,0 AS MEMO_CESS,"
				+ "0 AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_GSTN_TABLE4 " + "WHERE IS_DELETE = FALSE "
				+ " GROUP BY ECOM_GSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT ECOM_GSTIN as GSTIN_NO, '4' AS TABLE_SECTION,"
				+ " 'Supplies made through E-Commerce Operators'AS DESCRIPTION,"
				+ "COUNT(ID) AS ASP_COUNT,SUM(ECOM_VAL_SUPMADE) "
				+ "AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,SUM(ECOM_VAL_SUPRET) "
				+ "AS ASP_SUPPLIES_RETURNED,SUM(ECOM_NETVAL_SUP) AS ASP_NETVALUE,"
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST,"
				+ "SUM(SGST_AMT) AS ASP_SGST,SUM(CESS_AMT) AS ASP_CESS, "
				+ "0 AS ASP_INVOICE_VALUE,0 AS MEMO_COUNT,"
				+ "0 AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS MEMO_SUPPLIES_RETURNED,"
				+ "0 AS MEMO_NET_VALUE,0 AS MEMO_IGST,0 AS MEMO_CGST,0 AS MEMO_SGST,"
				+ "0 AS MEMO_CESS,0 AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM ANX_GSTN_B2C "
				+ " WHERE ECOM_GSTIN IS NOT NULL AND IS_DELETE = FALSE "
				+ " GROUP BY ECOM_GSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT CUST_GSTIN as GSTIN_NO, AN_TABLE_SECTION AS TABLE_SECTION,(CASE "
				+ "WHEN AN_TABLE_SECTION ='3J' THEN 'Import of goods'  "
				+ "WHEN AN_TABLE_SECTION ='3K' THEN 'Import of goods "
				+ "from SEZ units / developers on a Bill of Entry' "
				+ "WHEN AN_TABLE_SECTION ='3L' THEN 'Missing documents "
				+ "on which credit has been claimed' END) AS DESCRIPTION,COUNT(ID) "
				+ "AS ASP_COUNT,SUM(TAXABLE_VALUE) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST,"
				+ "SUM(SGST_AMT) AS ASP_SGST,"
				+ "SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC) AS ASP_CESS,"
				+ "SUM(DOC_AMT) AS ASP_INVOICE_VALUE,COUNT(ID) AS MEMO_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "SUM(MEMO_VALUE_IGST) AS MEMO_IGST,SUM(MEMO_VALUE_CGST) "
				+ "AS MEMO_CGST,SUM(MEMO_VALUE_SGST) AS MEMO_SGST,0 AS MEMO_CESS,"
				+ "SUM(TAXABLE_VALUE+MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) "
				+ "AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE AND IS_SAVED_TO_GSTN = TRUE "
				+ "AND AN_TABLE_SECTION IN ('3J','3K','3L') "
				+ " GROUP BY AN_TABLE_SECTION,CUST_GSTIN,DERIVED_RET_PERIOD "
				+ " UNION "
				+ "SELECT CUST_GSTIN as GSTIN_NO, AN_TABLE_SECTION AS TABLE_SECTION,"
				+ "(CASE WHEN AN_TABLE_SECTION ='3H' THEN 'Inward supplies "
				+ "attracting reverse charge (to be reported by the recipient, "
				+ "GSTIN wise for every supplier, net of debit/credit notes "
				+ "and advances paid, if any)' "
				+ "WHEN AN_TABLE_SECTION ='3I' THEN 'Import of services "
				+ "(net of debit/ credit notes and advances paid, if any)' END) "
				+ "AS DESCRIPTION, COUNT(ID) AS ASP_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST,"
				+ "SUM(SGST_AMT) AS ASP_SGST,SUM(CESS_AMT_ADVALOREM+CESS_AMT_SPECIFIC) "
				+ "AS ASP_CESS,SUM(DOC_AMT) AS ASP_INVOICE_VALUE,COUNT(ID) "
				+ "AS MEMO_COUNT,SUM(TAXABLE_VALUE) "
				+ "AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS MEMO_SUPPLIES_RETURNED,"
				+ "0 AS MEMO_NET_VALUE,SUM(MEMO_VALUE_IGST) AS MEMO_IGST,"
				+ "SUM(MEMO_VALUE_CGST) AS MEMO_CGST,SUM(MEMO_VALUE_SGST) "
				+ "AS MEMO_SGST,0 AS MEMO_CESS,"
				+ "SUM(TAXABLE_VALUE+MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) "
				+ "AS MEMO_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "AND IS_SAVED_TO_GSTN = TRUE "
				+ " GROUP BY AN_TABLE_SECTION,CUST_GSTIN,DERIVED_RET_PERIOD "
				+ " UNION "
				+ "SELECT CUST_GSTIN as GSTIN_NO, '3H' AS TABLE_SECTION,"
				+ " 'Inward supplies attracting reverse charge (to be reported by "
				+ "the recipient, GSTIN wise for every supplier, "
				+ "net of debit/credit notes and advances paid, if any)' AS DESCRIPTION, "
				+ "COUNT(ID) AS ASP_COUNT,SUM(TAXABLE_VALUE) "
				+ "AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS ASP_SUPPLIES_RETURNED,"
				+ "0 AS ASP_NETVALUE,SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) "
				+ "AS ASP_CGST,SUM(SGST_AMT) AS ASP_SGST,SUM(CESS_AMT) AS ASP_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS ASP_INVOICE_VALUE,0 AS MEMO_COUNT,"
				+ "0 AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS MEMO_SUPPLIES_RETURNED,"
				+ "0 AS MEMO_NET_VALUE,0 AS MEMO_IGST,0 AS MEMO_CGST,"
				+ "0 AS MEMO_SGST,0 AS MEMO_CESS,0 AS MEMO_INVOICE_VALUE,"
				+ "DERIVED_RET_PERIOD "
				+ "FROM ANX_GSTN_3H_3I WHERE  IS_DELETE = FALSE AND TRAN_FLAG= 'RC' "
				+ " GROUP BY CUST_GSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT CUST_GSTIN as GSTIN_NO, '3I' AS TABLE_SECTION,"
				+ " 'Import of services (net of debit/ credit notes and advances "
				+ "paid, if any)' AS DESCRIPTION,COUNT(ID) AS ASP_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE, "
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "SUM(IGST_AMT) AS ASP_IGST,SUM(CGST_AMT) AS ASP_CGST,"
				+ "SUM(SGST_AMT) AS ASP_SGST,SUM(CESS_AMT) AS ASP_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS ASP_INVOICE_VALUE,0 AS MEMO_COUNT,"
				+ "0 AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "0 AS MEMO_IGST,0 AS MEMO_CGST,0 AS MEMO_SGST,"
				+ "0 AS MEMO_CESS,0 AS MEMO_INVOICE_VALUE,"
				+ " DERIVED_RET_PERIOD "
				+ "FROM ANX_GSTN_3H_3I WHERE  IS_DELETE = FALSE "
				+ "AND TRAN_FLAG= 'IMPS' "
				+ " GROUP BY CUST_GSTIN,DERIVED_RET_PERIOD) " + " UNION "
				+ "SELECT GSTIN_NO,TABLE_SECTION,DESCIPTION,"
				+ "0 AS ASP_COUNT,0 AS ASP_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS ASP_SUPPLIES_RETURNED,0 AS ASP_NETVALUE,"
				+ "0 AS ASP_IGST,0 AS ASP_CGST,0 AS ASP_SGST,0 AS ASP_CESS,"
				+ "0 AS ASP_INVOICE_VALUE,NULL AS DERIVED_RET_PERIOD_ASP,"
				+ "0 AS MEMO_COUNT," + "0 AS MEMO_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS MEMO_SUPPLIES_RETURNED,0 AS MEMO_NET_VALUE,"
				+ "0 AS MEMO_IGST,0 AS MEMO_CGST,0 AS MEMO_SGST,0 AS MEMO_CESS,"
				+ "0 AS MEMO_INVOICE_VALUE,NULL AS DERIVED_RET_PERIOD_MEMO,"
				+ "GSTN_COUNT AS GSTN_COUNT_GSTN,"
				+ "GSTN_TAXABLEVALUE_OR_SUPPLIESMADE "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE_GSTN,GSTN_SUPPLIES_RETURNED "
				+ "AS GSTN_SUPPLIES_RETURNED_GSTN,GSTN_NET_VALUE "
				+ "AS GSTN_NET_VALUE_GSTN,GSTN_IGST AS GSTN_IGST_GSTN,"
				+ "GSTN_CGST AS GSTN_CGST_GSTN,GSTN_SGST AS GSTN_SGST_GSTN,"
				+ "GSTN_CESS AS GSTN_CESS_GSTN,GSTN_INVOICE_VALUE "
				+ "AS GSTN_INVOICE_VALUE_GSTN,"
				+ "DERIVED_RET_PERIOD AS DERIVED_RET_PERIOD_GSTIN "
				+ " FROM( SELECT SGSTIN AS GSTIN_NO,'3A' AS TABLE_SECTION,"
				+ " 'Supplies made to consumers and un-registered persons "
				+ "(Net of debit / credit notes)' AS DESCIPTION,COUNT(ID) "
				+ "AS GSTN_COUNT,IFNULL(SUM(TAXABLE_VALUE),0) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE, "
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,"
				+ "SUM(IGST_AMT) AS GSTN_IGST,SUM(CGST_AMT) AS GSTN_CGST,"
				+ "SUM(SGST_AMT) AS GSTN_SGST,SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_B2C_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3B' AS TABLE_SECTION,"
				+ " 'Supplies made to registered persons (other than those "
				+ "attracting reverse charge)(including edit/amendment)' "
				+ "AS DESCIPTION,COUNT(ID) AS GSTN_COUNT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,"
				+ "SUM(IGST_AMT) AS GSTN_IGST,SUM(CGST_AMT) AS GSTN_CGST,"
				+ "SUM(SGST_AMT) AS GSTN_SGST,SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)"
				+ "+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ "+IFNULL(CESS_AMT,0)) AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_B2B_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD" + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3C' AS TABLE_SECTION,"
				+ " 'Exports with payment of tax' AS DESCIPTION,COUNT(ID) "
				+ "AS GSTN_COUNT,IFNULL(SUM(TAXABLE_VALUE),0) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS GSTN_SUPPLIES_RETURNED,"
				+ "0 AS GSTN_NET_VALUE,SUM(IGST_AMT) AS GSTN_IGST,0 AS CGST,0 AS SGST,"
				+ "SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CESS_AMT) AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_EXPWP_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD" + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3D' AS TABLE_SECTION,"
				+ " 'Exports without payment of tax' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT,IFNULL(SUM(TAXABLE_VALUE),0) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,"
				+ "0 AS IGST,0 AS CGST,0 AS SGST,0 AS CESS,"
				+ "SUM(TAXABLE_VALUE) AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_EXPWOP_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3E' AS TABLE_SECTION,"
				+ " 'Supplies to SEZ units/developers with payment of "
				+ "tax (including edit/amendment)' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT,IFNULL(SUM(TAXABLE_VALUE),0) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,"
				+ "SUM(IGST_AMT) AS GSTN_IGST,0 AS CGST,0 AS SGST,"
				+ "SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CESS_AMT) AS GSTN_INVOICE_VALUE,"
				+ "DERIVED_RET_PERIOD" + " FROM GETANX1_SEZWP_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3F' AS TABLE_SECTION,"
				+ " 'Supplies to SEZ units/developers without "
				+ "payment of tax (including edit/amendment)' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT,IFNULL(SUM(TAXABLE_VALUE),0) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS GSTN_SUPPLIES_RETURNED,"
				+ "0 AS GSTN_NET_VALUE,0 AS IGST,0 AS CGST,0 AS SGST,0 AS CESS,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_SEZWOP_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3G' AS TABLE_SECTION,"
				+ " 'Deemed exports (including edit/amendment)' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT,SUM(TAXABLE_VALUE) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS GSTN_SUPPLIES_RETURNED,"
				+ "0 AS GSTN_NET_VALUE,SUM(IGST_AMT) AS GSTN_IGST,SUM(CGST_AMT) "
				+ "AS GSTN_CGST,SUM(SGST_AMT) AS GSTN_SGST,SUM(CESS_AMT) "
				+ "AS GSTN_CESS,SUM(TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_DE_HEADER  "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3H' AS TABLE_SECTION,"
				+ " 'Inward supplies attracting reverse charge (to be reported "
				+ "by the recipient, GSTIN wise for every supplier, "
				+ "net of debit/credit notes and advances paid, if any)' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT,SUM(TAXABLE_VALUE) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS GSTN_SUPPLIES_RETURNED,"
				+ "0 AS GSTN_NET_VALUE,SUM(IGST_AMT) AS GSTN_IGST,SUM(CGST_AMT) "
				+ "AS GSTN_CGST,SUM(SGST_AMT) AS GSTN_SGST,"
				+ "SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_REV_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3I' AS TABLE_SECTION,"
				+ " 'Import of services (net of debit/ credit notes "
				+ "and advances paid, if any)' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT,SUM(TAXABLE_VALUE) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,0 AS GSTN_SUPPLIES_RETURNED,"
				+ "0 AS GSTN_NET_VALUE,SUM(IGST_AMT) AS GSTN_IGST,0 AS CGST,"
				+ "0 AS SGST,SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CESS_AMT) AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_IMPS_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3J' AS TABLE_SECTION,"
				+ " 'Import of goods' AS DESCIPTION,COUNT(ID) AS GSTN_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,"
				+ "SUM(IGST_AMT) AS GSTN_IGST,0 AS CGST,0 AS SGST,SUM(CESS_AMT) "
				+ "AS GSTN_CESS,SUM(TAXABLE_VALUE+IGST_AMT+CESS_AMT) "
				+ "AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD"
				+ " FROM GETANX1_IMPG_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3K' AS TABLE_SECTION,"
				+ " 'Import of goods from SEZ units / developers on a "
				+ "Bill of Entry' AS DESCIPTION,COUNT(ID) AS GSTN_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,"
				+ "SUM(IGST_AMT) AS GSTN_IGST,0 AS CGST,0 AS SGST,SUM(CESS_AMT) "
				+ "AS GSTN_CESS,SUM(TAXABLE_VALUE+IGST_AMT+CESS_AMT) "
				+ "AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ " FROM GETANX1_IMPGSEZ_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'3L' AS TABLE_SECTION,"
				+ " 'Missing documents on which credit has been claimed' "
				+ "AS DESCIPTION,COUNT(ID) AS GSTN_COUNT,"
				+ "SUM(TAXABLE_VALUE) AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,0 AS GSTN_NET_VALUE,SUM(IGST_AMT) "
				+ "AS GSTN_IGST,SUM(CGST_AMT) AS GSTN_CGST,SUM(SGST_AMT) "
				+ "AS GSTN_SGST,SUM(CESS_AMT) AS GSTN_CESS,"
				+ "SUM(TAXABLE_VALUE+IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT) "
				+ "AS GSTN_INVOICE_VALUE,DERIVED_RET_PERIOD "
				+ " FROM GETANX1_MIS_HEADER "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD " + " UNION "
				+ "SELECT SGSTIN AS GSTIN_NO,'4' AS TABLE_SECTION,"
				+ " 'Supplies made through E-Commerce Operators' AS DESCIPTION,"
				+ "COUNT(ID) AS GSTN_COUNT," + "IFNULL(SUM(SUPP_MADE_VALUE),0) "
				+ "AS GSTN_TAXABLEVALUE_OR_SUPPLIESMADE,"
				+ "0 AS GSTN_SUPPLIES_RETURNED,"
				+ "IFNULL(SUM(SUPP_NET_VALUE),0) AS GSTN_NET_VALUE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS GSTN_IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS GSTN_CGST,IFNULL(SUM(SGST_AMT),0) "
				+ "AS GSTN_SGST,IFNULL(SUM(CESS_AMT),0) AS GSTN_CESS,"
				+ "SUM(IFNULL(SUPP_NET_VALUE,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
				+ " +IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS GSTN_INVOICE_VALUE,"
				+ "DERIVED_RET_PERIOD " + " FROM GETANX1_ECOM_DETAILS  "
				+ " GROUP BY SGSTIN,DERIVED_RET_PERIOD )) WHERE " + buildQuery
				+ " GROUP BY GSTIN_NO,TABLE_SECTION,DESCRIPTION,"
				+ " DERIVED_RET_PERIOD_ASP,DERIVED_RET_PERIOD_MEMO,"
				+ " DERIVED_RET_PERIOD_GSTIN " + " ORDER BY 2,1 ";

	}
}
