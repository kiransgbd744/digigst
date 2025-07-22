package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.ITC04PopupRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04RequestDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("ITC04PopupSectionsDaoImpl")
public class ITC04PopupSectionsDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<ITC04PopupRespDto> loadBasicSummarySection(
			ITC04RequestDto req) {

		ITC04RequestDto reqDto = processedRecordsCommonSecParam
				.setItc04DataSecuritySearchParams(req);

		List<Long> entityId = req.getEntityId();
		String taxPeriod = req.getTaxPeriod();
		String tableType = req.getTableType();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ITC04ProcessSummaryFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, "
					+ "dataSecAttrs -> {}", req);
		}
	/*	String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;*/
		String gstin = null;

	/*	List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
*/		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				/*if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
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
*/
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				/*if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
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
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
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
*/			}
		}

		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstin List is {}", gstinList);
				}
			}
		}
	/*	}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE1 IN :pcList ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("PROFIT_CENTRE1 List is {}", pcList);
				}

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND ITM.PLANT_CODE IN :plantList ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("PLANT_CODE List is {}", plantList);
				}

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN :divisionList ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("DIVISION List is {}", divisionList);
				}

			}
		}
		
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND ITM.USERDEFINED_FIELD1 IN :ud1List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("USERDEFINED_FIELD1 List is {}", ud1List);
				}
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND ITM.USERDEFINED_FIELD2 IN :ud2List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("USERDEFINED_FIELD2 List is {}", ud2List);
				}
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND ITM.USERDEFINED_FIELD3 IN :ud3List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("USERDEFINED_FIELD3 List is {}", ud3List);
				}
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND ITM.USERDEFINED_FIELD4 IN :ud4List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("USERDEFINED_FIELD4 List is {}", ud4List);
				}
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND ITM.USERDEFINED_FIELD5 IN :ud5List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("USERDEFINED_FIELD5 List is {}", ud5List);
				}
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND ITM.USERDEFINED_FIELD6 IN :ud6List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("USERDEFINED_FIELD6 List is {}", ud6List);
				}
			}
		}
*/		if (taxPeriod != null) {

			build.append(" AND QRETURN_PERIOD = :taxPeriod ");
		}
		if (tableType != null) {

			build.append(" AND TABLE_TYPE = :tableType ");
		}
		
	
		String buildQuery = build.toString().substring(4);

		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For ITC04 ProcessedSummary is {}"
					+ queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			/*if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}*/
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
					LOGGER.debug("GSTIN List is ---> {}", gstinList);
				}
			}
		/*	if (division != null && !division.isEmpty()) { 
				if (divisionList!= null && !divisionList.isEmpty() 
						&& divisionList.size() > 0) {
					 q.setParameter("divisionList", divisionList); 
					 } 
				}
			if (plant != null && !plant.isEmpty()) { 
				if (plantList!= null && !plantList.isEmpty() 
						&& divisionList.size() > 0) {
					 q.setParameter("plantList", plantList); 
					 } 
				}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && !ud1List.isEmpty()
						&& ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
						&& ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
						&& ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
						&& ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
						&& ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
						&& ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}*/
			if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
			}
			if (tableType != null) {
				q.setParameter("tableType", tableType);
			}

			List<Object[]> list = q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto--> {}",list);
			}
			List<ITC04PopupRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("After Execution getting the data ----->{}" + retList);
			}
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private ITC04PopupRespDto convert(Object[] arr) {
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Array data Setting to Dto.. {}",arr);
		}
		ITC04PopupRespDto dto = new ITC04PopupRespDto();
		dto.setType((String) arr[3]);
		dto.setCount((GenUtil.getBigInteger(arr[4])).intValue());
		dto.setQuantity((BigDecimal) arr[5]);
		dto.setLossesQuantity((BigDecimal) arr[6]);
		dto.setTaxableValue((BigDecimal) arr[7]);
		dto.setIgst((BigDecimal) arr[8]);
		dto.setCgst((BigDecimal) arr[9]);
		dto.setSgst((BigDecimal) arr[10]);
		dto.setCess((BigDecimal) arr[11]);
		dto.setStateCess((BigDecimal) arr[12]);
		dto.setTotalValue((BigDecimal) arr[13]);
		
			return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");
		
		
		String queryStr = "SELECT SUPPLIER_GSTIN,QRETURN_PERIOD,TABLE_DESC_TYPE,"
				+ "TABLE_TYPE,SUM(COUNT_KEY) COUNT,SUM(ITM_QTY)ITM_QTY,"
				+ "SUM(LOSSES_QTY)LOSSES_QTY,SUM(TAXABLE_VALUE)TAXABLE_VALUE,"
				+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,"
				+ "SUM(SGST_AMT)SGST_AMT,SUM(CGST_AMT)CESS_AMT,"
				+ "SUM(STATECESS_AMT)STATECESS_AMT,"
				+ "SUM(TOTAL_VALUE)TOTAL_VALUE "
				+ "FROM ( SELECT "
				+ "SUPPLIER_GSTIN,HDR.RETURN_PERIOD, HDR.QRETURN_PERIOD,"
				+ "(CASE WHEN TABLE_NUMBER='4' THEN 'Table 4 - M2JW' "
				+ "WHEN TABLE_NUMBER='5A' THEN 'Table 5A - JW2M' "
				+ "WHEN TABLE_NUMBER='5B' THEN 'Table 5B - Other JW2M' "
				+ "WHEN TABLE_NUMBER='5C' THEN 'Table 5C - Sold from JW' END) TABLE_DESC_TYPE,"
				+ "(CASE WHEN TABLE_NUMBER='4' THEN '4' "
				+ "WHEN TABLE_NUMBER='5A' THEN '5A' "
				+ "WHEN TABLE_NUMBER='5B' THEN '5B' "
				+ "WHEN TABLE_NUMBER='5C' THEN '5C' END) TABLE_TYPE,"
				+ "COUNT (DISTINCT DOC_KEY )AS COUNT_KEY,"
				+ "IFNULL(SUM(ITM.ITM_QTY ),0) AS ITM_QTY,"
				+ "IFNULL(SUM(ITM.LOSSES_QTY ),0) AS LOSSES_QTY,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT ),0) AS IGST_AMT,"
				+ "IFNULL(SUM(ITM.CGST_AMT ),0) AS CGST_AMT,"
				+ "IFNULL(SUM(ITM.SGST_AMT ),0) AS SGST_AMT,"
				+ "IFNULL(SUM(ITM.CESS_AMT_SPECIFIC ),0) + "
				+ "IFNULL(SUM(ITM.CESS_AMT_ADVALOREM ),0)  AS CESS_AMT,"
				+ "IFNULL(SUM(ITM.STATE_CESS_AMT_SPECIFIC ),0) + "
				+ "IFNULL(SUM(ITM.STATE_CESS_AMT_ADVALOREM ),0)  AS STATECESS_AMT,"
				+ "IFNULL(SUM(ITM.TOTAL_VALUE ),0) AS TOTAL_VALUE "
				+ "FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ "WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE AND "
				+ "(ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') "
				+ "GROUP BY SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.QRETURN_PERIOD,TABLE_NUMBER) "
				+ "WHERE "
				+ buildQuery 
				+ "GROUP BY TABLE_DESC_TYPE,TABLE_TYPE,SUPPLIER_GSTIN, QRETURN_PERIOD "
				+ "order by TABLE_DESC_TYPE asc";
	
           //           SUPPLIER_GSTIN='33GSPTN0481G1ZA' and  QRETURN_PERIOD='142019' AND TABLE_TYPE = '5A'
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Executing Native Query is,.. {}",queryStr);
		}
		return queryStr;

	}

	
	
}
