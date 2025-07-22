/**
 * 
 */
package com.ey.advisory.app.docs.services.gstr6;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.services.gstr6.Gstr6DigiGstProcessedComputeServiceImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Service("Gstr6ProcessedDataDaoImpl")
public class Gstr6ProcessedDataDaoImpl implements Gstr6ProcessedDataDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Override
	public List<Object[]> getGstr6ProcessedRec(Gstr6SummaryRequestDto request) {

		String taxperiod = request.getTaxPeriod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		List<Object[]> list = new ArrayList<>();

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
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstn = key;
					gstinList = gstnDetailRepository.getGstinRegTypeISD(
							dataSecAttrs.get(OnboardingConstant.GSTIN));
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
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
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

		list = q.getResultList();

		return list;
	}

	private String createProcessedQueryString(String buildQuery) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT CUST_GSTIN,RETURN_PERIOD,COUNT(ID), ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR')");
		sql.append(" THEN IFNULL(DOC_AMT,0) END),0) - IFNULL(SUM(CASE ");
		sql.append(
				" WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(DOC_AMT,0) END ),0) ");
		sql.append(" AS INVOICE_VALUE, IFNULL(SUM(CASE WHEN DOC_TYPE IN ");
		sql.append(" ('INV','DR','RNV','RDR') THEN TAXABLE_VALUE END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN ");
		sql.append(" TAXABLE_VALUE END),0) AS TAX_VALUE, IFNULL(SUM(CASE ");
		sql.append(" WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN ");
		sql.append(" IFNULL(TAX_PAYABLE,0) END),0) - IFNULL(SUM(CASE WHEN ");
		sql.append(
				" DOC_TYPE IN ('CR','RCR') THEN IFNULL(TAX_PAYABLE,0) END),0) ");
		sql.append(" AS TOTAL_TAX, IFNULL(SUM(CASE WHEN DOC_TYPE IN ");
		sql.append(
				" ('INV','DR','RNV','RDR') THEN IFNULL(IGST_AMT,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN ");
		sql.append(" IFNULL(IGST_AMT,0) END),0) AS IGST_AMT, IFNULL(SUM(CASE ");
		sql.append(" WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN ");
		sql.append(
				" IFNULL(CGST_AMT,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE ");
		sql.append(
				" IN ('CR','RCR') THEN IFNULL(CGST_AMT,0)END),0) AS CGST_AMT, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(SGST_AMT,0) END),0) - IFNULL(SUM(CASE WHEN ");
		sql.append(
				" DOC_TYPE IN ('CR','RCR') THEN IFNULL(SGST_AMT,0)END),0) AS ");
		sql.append(" SGST_AMT, (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR',");
		sql.append(" 'RNV','RDR') THEN IFNULL(CESS_AMT_SPECIFIC,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN ");
		sql.append(" IFNULL(CESS_AMT_SPECIFIC,0) END),0)) + (IFNULL(SUM(CASE ");
		sql.append(" WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN ");
		sql.append(" IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE ");
		sql.append(
				" WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(CESS_AMT_ADVALOREM,0) ");
		sql.append(" END),0)) AS CESS_AMT, (IFNULL(SUM(CASE WHEN DOC_TYPE IN ");
		sql.append(
				" ('INV','DR','RNV','RDR') THEN IFNULL(AVAILABLE_IGST,0) END),0) ");
		sql.append(
				" + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(" THEN IFNULL(AVAILABLE_CGST,0) END),0) + IFNULL(SUM(CASE ");
		sql.append(" WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN ");
		sql.append(" IFNULL(AVAILABLE_SGST,0) END),0) + IFNULL(SUM(CASE ");
		sql.append(" WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') THEN ");
		sql.append(" IFNULL(AVAILABLE_CESS,0) END),0) ) - (IFNULL(SUM(CASE ");
		sql.append(
				" WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(AVAILABLE_IGST,0) ");
		sql.append(" END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') ");
		sql.append(" THEN IFNULL(AVAILABLE_CGST,0) END),0) + IFNULL(SUM(CASE ");
		sql.append(
				" WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(AVAILABLE_SGST,0) ");
		sql.append(
				" END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN ");
		sql.append(
				" IFNULL(AVAILABLE_CESS,0) END),0) ) AS TOTAL_CREDIT_ELIGIBLE, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(
				" THEN IFNULL(AVAILABLE_IGST,0)END),0) - IFNULL(SUM(CASE WHEN ");
		sql.append(
				" DOC_TYPE = 'CR' THEN IFNULL(AVAILABLE_IGST,0) END),0) AS ");
		sql.append(
				" AVAILABLE_IGST, IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR',");
		sql.append(" 'RNV','RDR') THEN IFNULL(AVAILABLE_CGST,0) END),0) - ");
		sql.append(" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN ");
		sql.append(" IFNULL(AVAILABLE_CGST,0) END),0) AS AVAILABLE_CGST, ");
		sql.append(
				" IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR') ");
		sql.append(
				" THEN IFNULL(AVAILABLE_SGST,'0') END),0) - IFNULL(SUM(CASE ");
		sql.append(
				" WHEN DOC_TYPE IN ('CR','RCR') THEN IFNULL(AVAILABLE_SGST,0) ");
		sql.append(
				" END),0) AS AVAILABLE_SGST, IFNULL(SUM(CASE WHEN DOC_TYPE ");
		sql.append(
				" IN('INV','DR','RNV','RDR') THEN IFNULL(AVAILABLE_CESS,'0') ");
		sql.append(
				" END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN ");
		sql.append(" IFNULL(AVAILABLE_CESS,0) END),0) AS AVAILABLE_CESS,");
		sql.append(" fnGetState(CUST_GSTIN),");
		sql.append(" COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND ");
		sql.append(
				" IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,");
		sql.append(" COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND ");
		sql.append(
				" IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,");
		sql.append(" COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND ");
		sql.append(
				" IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,");
		sql.append(" COUNT(CASE WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE ");
		sql.append(" THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,");
		sql.append(" COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) ");
		sql.append(" TOTAL_COUNT_IN_ASP,MAX(MODIFIED_ON) MODIFIED_ON ");
		sql.append("  FROM ANX_INWARD_DOC_HEADER ");
		sql.append(" WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND ");
		sql.append(" RETURN_TYPE = 'GSTR6' AND SUPPLY_TYPE <> 'CAN' ");
		sql.append(buildQuery);
		sql.append("  GROUP BY CUST_GSTIN,RETURN_PERIOD ");
		return sql.toString();
	}

	@Override
	public List<Object[]> getGstr6ProcessedProcRecords(
			Gstr6SummaryRequestDto dto) {

		String taxPeriod = dto.getTaxPeriod();
		Integer derivedTaxPeriod = GenUtil.getDerivedTaxPeriod(taxPeriod);
		Map<String, List<String>> dataSecAttrs = dto.getDataSecAttrs();
		List<Object[]> list = new ArrayList<>();

		List<String> gstinList = null;
		List<String> gstins = new ArrayList<>();
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = gstnDetailRepository.getGstinRegTypeISD(
							dataSecAttrs.get(OnboardingConstant.GSTIN));
				}
			}
		}
		if(gstinList != null
				&& !gstinList.isEmpty()){
		
			List<List<String>> chunks = Lists.partition(gstinList, 200);
			for(List<String>chunk : chunks){
				gstins.add(String.join(",", chunk));
			}
			
			for (String gstin : gstins) {
				StoredProcedureQuery storedProc = procCall(gstin,
						Long.valueOf(derivedTaxPeriod));
				list.addAll(storedProc.getResultList());
			}
		}
		
		return list;
	}
	
	private StoredProcedureQuery procCall(String gstin, Long taxPeriod) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_GSTR6_ENTITY_SUMMARY");

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("About to execute GSTR6 SP with gstin :%s", gstin);
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("P_RET_PERIOD", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_RET_PERIOD", taxPeriod);

		return storedProc;
	}

}
