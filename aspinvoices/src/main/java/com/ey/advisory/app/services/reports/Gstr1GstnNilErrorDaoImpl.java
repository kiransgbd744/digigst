/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1GstnNILErrorDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1GstnNilErrorDaoImpl")
public class Gstr1GstnNilErrorDaoImpl implements Gstr1GstnErrorDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnNilErrorDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public List<Object> getGstr1GstnErrorReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		
		String gstnRefId = request.getGstnRefId();
		Long refId = null;
		
		if (request.getGstnRefId() != null) {// Reference Id
			List<Gstr1SaveBatchEntity> refDetails = gstr1BatchRepository
					.selectByrefId(request.getGstnRefId());
			if (refDetails != null) {
				for (Gstr1SaveBatchEntity ref : refDetails) {
					 refId = ref.getId();
				}
			}
		}
		
		

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
				buildQuery.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
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
				buildQuery.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND ITM.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
		}
		if(refId == null){
			buildQuery.append(" AND HDR.IS_DELETE=FALSE ");
		}
		if (refId != null) {// Reference Id
			buildQuery.append(" AND HDR.BATCH_ID  = :refId ");
		}
		
String queryStr = null;
		
		if(request.getReturnType()!=null && APIConstants.GSTR1A.equalsIgnoreCase(request.getReturnType()))
			
		{
			queryStr = creategstnGstr1ANilErrorQueryString(buildQuery.toString());
		}
		else
		{
			queryStr = creategstnNilErrorQueryString(buildQuery.toString());
		}
		Query q = entityManager.createNativeQuery(queryStr);

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
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}
		
		if (refId != null) {
			q.setParameter("refId", refId);
		}
		

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertgstnNilError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnNILErrorDto convertgstnNilError(Object[] arr) {
		Gstr1GstnNILErrorDto obj = new Gstr1GstnNILErrorDto();

		obj.setSupplyType(arr[0] != null ? arr[0].toString() : null);
		obj.setExemptedOutwardAmount(arr[1] != null ? arr[1].toString() : null);
		obj.setNilratedOutwardSupplyAmount(
				arr[2] != null ? arr[2].toString() : null);
		obj.setNongstOutwardSupplyAmount(
				arr[3] != null ? arr[3].toString() : null);
		obj.setErrorCode(arr[4] != null ? arr[4].toString() : null);
		obj.setGstnErrorMessage(arr[5] != null ? arr[5].toString() : null);
		obj.setRefId(arr[6] != null ? arr[6].toString() : null);

		return obj;
	}

	private String creategstnNilErrorQueryString(String buildQuery) {

		return "SELECT HDR.SUPPLY_TYPE,(CASE WHEN HDR.SUPPLY_TYPE='EXT' "
				+ "THEN ITM.TAXABLE_VALUE END) AS EXPT_AMT,"
				+ "(CASE WHEN HDR.SUPPLY_TYPE='NIL' "
				+ "THEN ITM.TAXABLE_VALUE END) AS NIL_AMT,"
				+ "(CASE WHEN HDR.SUPPLY_TYPE IN ('NON','SCH3') "
				+ "THEN ITM.ONB_LINE_ITEM_AMT END) AS NONGST_SUP_AMT, "
				+ "TRIM(', ' FROM "
				+ "IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_GSTN,'')) AS GSTN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) "
				+ "AS GSTN_ERROR_DESCRIPTION, "
				+ "GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID "
				+ "FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR LEFT OUTER JOIN TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO () "
				+ "ERRH ON HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON "
				+ "HDR.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO () ERRI "
				+ "ON ITM.DOC_HEADER_ID = ERRI.DOC_HEADER_ID "
				+ "AND IFNULL(ITM.ITEM_INDEX,'-1') = IFNULL(ERRI.ITEM_INDEX,'-1') "
				+ "AND IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0') "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = HDR.BATCH_ID "
				+ "WHERE HDR.RETURN_TYPE='GSTR1' AND "
				+ "HDR.GSTN_ERROR=TRUE "
				+ "AND HDR.TAX_DOC_TYPE='NILEXTNON' AND HDR.IS_DELETE=FALSE "
				+ "AND GSTN_SAVE_STATUS='PE' " + buildQuery;
	}
	
	private String creategstnGstr1ANilErrorQueryString(String buildQuery) {

		return "SELECT HDR.SUPPLY_TYPE,(CASE WHEN HDR.SUPPLY_TYPE='EXT' "
				+ "THEN ITM.TAXABLE_VALUE END) AS EXPT_AMT,"
				+ "(CASE WHEN HDR.SUPPLY_TYPE='NIL' "
				+ "THEN ITM.TAXABLE_VALUE END) AS NIL_AMT,"
				+ "(CASE WHEN HDR.SUPPLY_TYPE IN ('NON','SCH3') "
				+ "THEN ITM.ONB_LINE_ITEM_AMT END) AS NONGST_SUP_AMT, "
				+ "TRIM(', ' FROM "
				+ "IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_CODE_GSTN,'')) AS GSTN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') ||','|| "
				+ "IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) "
				+ "AS GSTN_ERROR_DESCRIPTION, "
				+ "GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A "
				+ "HDR LEFT OUTER JOIN TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO_1A () "
				+ "ERRH ON HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM_1A ITM ON "
				+ "HDR.ID = ITM.DOC_HEADER_ID LEFT OUTER JOIN "
				+ "TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO_1A () ERRI "
				+ "ON ITM.DOC_HEADER_ID = ERRI.DOC_HEADER_ID "
				+ "AND IFNULL(ITM.ITEM_INDEX,'-1') = IFNULL(ERRI.ITEM_INDEX,'-1') "
				+ "AND IFNULL(ITM.ITM_NO,'0') = IFNULL(ERRI.ITM_NO,'0') "
				+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH "
				+ "ON GSTNBATCH.ID = HDR.BATCH_ID "
				+ "WHERE HDR.RETURN_TYPE='GSTR1A' AND "
				+ "HDR.GSTN_ERROR=TRUE "
				+ "AND HDR.TAX_DOC_TYPE='NILEXTNON' AND HDR.IS_DELETE=FALSE "
				+ "AND GSTN_SAVE_STATUS='PE' " + buildQuery;
	}
}
