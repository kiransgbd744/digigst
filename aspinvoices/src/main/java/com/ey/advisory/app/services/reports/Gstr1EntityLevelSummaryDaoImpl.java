/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import com.ey.advisory.app.data.views.client.GSTR1EntityLevelSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1EntityLevelSummaryDaoImpl")
public class Gstr1EntityLevelSummaryDaoImpl
		implements Gstr1EntityLevelSummaryDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1EntityLevelSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<GSTR1EntityLevelSummaryDto> getEntityLevelSummary(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String taxperiod = request.getTaxperiod();

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

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" DERIVED_RET_PERIOD = :taxPeriod");
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

		if (taxperiod != null && !taxperiod.isEmpty()) {
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
		return list.parallelStream().map(o -> convertEntityLevelSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GSTR1EntityLevelSummaryDto convertEntityLevelSummary(Object[] arr) {
		GSTR1EntityLevelSummaryDto obj = new GSTR1EntityLevelSummaryDto();

		obj.setGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setTableNo(arr[1] != null ? arr[1].toString() : null);
		obj.setTableDescription(arr[2] != null ? arr[2].toString() : null);
		int AspCount = (GenUtil.getBigInteger(arr[3])).intValue();
		obj.setAspCount(AspCount);

		BigDecimal bigDecimalAspTaxableValue = (BigDecimal) arr[4];

		BigDecimal aspTaxableValue = new BigDecimal(
				bigDecimalAspTaxableValue.doubleValue());
		obj.setAspTaxableValue(aspTaxableValue);
		BigDecimal bigDecimalAspIGST = (BigDecimal) arr[5];
		if (bigDecimalAspIGST != null) {
			BigDecimal aspIGST = new BigDecimal(bigDecimalAspIGST.doubleValue());
			obj.setAspIGST(aspIGST);
		}
		BigDecimal bigDecimalAspCGST = (BigDecimal) arr[6];
		if (bigDecimalAspCGST != null) {
			BigDecimal aspCGST = new BigDecimal(bigDecimalAspCGST.doubleValue());
			obj.setAspCGST(aspCGST);
		}
		BigDecimal bigDecimalAspSGST = (BigDecimal) arr[7];
		if (bigDecimalAspSGST != null) {
			BigDecimal aspSGST = new BigDecimal(bigDecimalAspSGST.doubleValue());
			obj.setAspSGST(aspSGST);
		}
		BigDecimal bigDecimalAspCess = (BigDecimal) arr[8];
		if (bigDecimalAspCess != null) {
			BigDecimal aspCess = new BigDecimal(bigDecimalAspCess.doubleValue());
			obj.setAspCess(aspCess);
		}
		BigDecimal bigDecimalAspInvoiceValue = (BigDecimal) arr[9];

		if (bigDecimalAspInvoiceValue != null) {
			BigDecimal aspInvoiceValue = new BigDecimal(
					bigDecimalAspInvoiceValue.doubleValue());
			obj.setAspInvoiceValue(aspInvoiceValue);
		}

		int Avialablecount = (GenUtil.getBigInteger(arr[10])).intValue();
		obj.setAvailableCount(Avialablecount);

		BigDecimal bigDecimalAvailableTaxableValue = (BigDecimal) arr[11];
		if (bigDecimalAvailableTaxableValue != null) {

			BigDecimal availableTaxableValue = new BigDecimal(
					bigDecimalAvailableTaxableValue.doubleValue());
			obj.setAvailableTaxableValue(availableTaxableValue);
		}

		BigDecimal bigDecimalAvailableIGST = (BigDecimal) arr[12];
		if (bigDecimalAvailableIGST != null) {

			BigDecimal availableIGST = new BigDecimal(
					bigDecimalAvailableIGST.doubleValue());
			obj.setAvailableIGST(availableIGST);
		}

		BigDecimal bigDecimalAvailableCGST = (BigDecimal) arr[13];

		if (bigDecimalAvailableCGST != null) {

			BigDecimal availableCGST = new BigDecimal(
					bigDecimalAvailableCGST.doubleValue());
			obj.setAvailableCGST(availableCGST);
		}

		BigDecimal bigDecimalAvailableSGST = (BigDecimal) arr[14];

		if (bigDecimalAvailableSGST != null) {

			BigDecimal availableSGST = new BigDecimal(
					bigDecimalAvailableSGST.doubleValue());
			obj.setAvailableSGST(availableSGST);
		}

		BigDecimal bigDecimalAvailableCess = (BigDecimal) arr[15];

		if (bigDecimalAvailableCess != null) {

			BigDecimal availableCess = new BigDecimal(
					bigDecimalAvailableCess.doubleValue());
			obj.setAvailableCess(availableCess);
		}

		BigDecimal bigDecimalAvailableInvoiceValue = (BigDecimal) arr[16];

		if (bigDecimalAvailableInvoiceValue != null) {
			BigDecimal availableInvoiceValue = new BigDecimal(
					bigDecimalAvailableInvoiceValue.doubleValue());
			obj.setAvailableInvoiceValue(availableInvoiceValue);
		}
		
		obj.setReturnPeriod(arr[17] != null ? arr[17].toString() : null);

		// Difference calculation
		
		obj.setDiffCount( obj.getAspCount() - obj.getAvailableCount());
		obj.setDiffTaxableValue(
				obj.getAspTaxableValue() != BigDecimal.ZERO
						? obj.getAspTaxableValue()
								.subtract(obj.getAvailableTaxableValue())
						: BigDecimal.ZERO);
		obj.setDiffIGST(obj.getAspIGST() != BigDecimal.ZERO
				? obj.getAspIGST().subtract(obj.getAvailableIGST())
				: BigDecimal.ZERO);
		obj.setDiffCGST(obj.getAspCGST() != BigDecimal.ZERO
				? obj.getAspCGST().subtract(obj.getAvailableCGST())
				: BigDecimal.ZERO);
		obj.setDiffSGST(obj.getAspSGST() != BigDecimal.ZERO
				? obj.getAspSGST().subtract(obj.getAvailableSGST())
				: BigDecimal.ZERO);
		obj.setDiffCess(obj.getAspCess() != BigDecimal.ZERO
				? obj.getAspCess().subtract(obj.getAvailableCess())
				: BigDecimal.ZERO);
		obj.setDiffInvoiceValue(
				obj.getAspInvoiceValue() != BigDecimal.ZERO
						? obj.getAspInvoiceValue()
								.subtract(obj.getAvailableInvoiceValue())
						: BigDecimal.ZERO);
		return obj;
	}

	private String EntityLevelSummaryQueryString(String buildQuery) {

		return "SELECT * FROM GSTR1_ENTITY_LEVEL_SUMMARY_VW WHERE " + buildQuery;
	}
}
