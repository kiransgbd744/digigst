package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr1.Gstr7PopupRecordsRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@Component("Gstr7PopupRecordsFetchDaoImpl")
public class Gstr7PopupRecordsFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Gstr7PopupRecordsRespDto> loadGstr7PopupSummaryRecords(
			Gstr1ProcessedRecordsReqDto req) {

		String taxPeriod = req.getRetunPeriod();
		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		String type = req.getType();
		String returnType = req.getReturnType();
		String queryStr ="";
		
		if (returnType == null) {
			returnType = "GSTR7";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ReturnType -->" + returnType);
		}

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<Gstr7PopupRecordsRespDto> retList = new ArrayList<>();

		/*String ProfitCenter = null;
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

		/*List<String> pcList = null;
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
		List<String> ud6List = null;*/
		List<String> gstinList = null;
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
				}*/

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
				}*/
				/*
				 * if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
				 * location = key; if
				 * (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty() &&
				 * dataSecAttrs.get(OnboardingConstant.LOCATION) .size() > 0) {
				 * locationList = dataSecAttrs
				 * .get(OnboardingConstant.LOCATION); } }
				 */
				/*
				 * if (key.equalsIgnoreCase(OnboardingConstant.SO)) { sales =
				 * key; if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
				 * && dataSecAttrs.get(OnboardingConstant.SO) .size() > 0) {
				 * salesList = dataSecAttrs.get(OnboardingConstant.SO); } }
				 */
				/*if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}*/
				/*
				 * if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
				 * distChannel = key; if
				 * (dataSecAttrs.get(OnboardingConstant.DC) != null &&
				 * dataSecAttrs.get(OnboardingConstant.DC) .size() > 0) {
				 * distList = dataSecAttrs.get(OnboardingConstant.DC); } }
				 */
				/*if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
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
				}*/
				/*
				 * if (key.equalsIgnoreCase(OnboardingConstant.UD4)) { ud4 =
				 * key; if (dataSecAttrs.get(OnboardingConstant.UD4) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD4) .size() > 0) {
				 * ud4List = dataSecAttrs.get(OnboardingConstant.UD4); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD5)) { ud5 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD5) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD5) .size() > 0) {
				 * ud5List = dataSecAttrs.get(OnboardingConstant.UD5); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD6)) { ud6 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD6) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD6) .size() > 0) {
				 * ud6List = dataSecAttrs.get(OnboardingConstant.UD6); } }
				 */
			}
		}

		StringBuilder build = new StringBuilder();
		
		
		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND DEDUCTOR_GSTIN IN :gstinList ");

				}
			}

			build.append(" AND RETURN_PERIOD = :taxPeriod ");
		} else {

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" TDS_DEDUCTOR_GSTIN IN :gstinList ");
				}
			}
			/*if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					build.append(" AND PROFIT_CENTRE1 IN :pcList ");
	
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {
					build.append(" AND PLANT_CODE IN :plantList ");
				}
			}*/
			/*
			 * if (sales != null && !sales.isEmpty()) { if (salesList != null &&
			 * salesList.size() > 0) {
			 * build.append(" AND SALES_ORGANIZATION IN :salesList "); } }
			 */
			/*if (purchase != null && !purchase.isEmpty()) {
				if (purcList != null && purcList.size() > 0) {
					build.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
				}
			}*/
			/*
			 * if (distChannel != null && !distChannel.isEmpty()) { if (distList !=
			 * null && distList.size() > 0) {
			 * build.append(" AND DISTRIBUTION_CHANNEL IN :distList "); } }
			 */
			/*if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					build.append(" AND DIVISION IN :divisionList ");
				}
			}*/
			/*
			 * if (location != null && !location.isEmpty()) { if (locationList !=
			 * null && locationList.size() > 0) {
			 * build.append(" AND LOCATION IN :locationList "); } }
			 */
			/*if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {
					build.append(" AND USERDEFINED_FIELD1 IN :ud1List ");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {
					build.append(" AND USERDEFINED_FIELD2 IN :ud2List ");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {
					build.append(" AND USERDEFINED_FIELD3 IN :ud3List ");
				}
			}*/
			/*
			 * if (ud4 != null && !ud4.isEmpty()) { if (ud4List != null &&
			 * ud4List.size() > 0) { build.append(" AND USERACCESS4 IN :ud4List ");
			 * } } if (ud5 != null && !ud5.isEmpty()) { if (ud5List != null &&
			 * ud5List.size() > 0) { build.append(" AND USERACCESS5 IN :ud5List ");
			 * } } if (ud6 != null && !ud6.isEmpty()) { if (ud6List != null &&
			 * ud6List.size() > 0) { build.append(" AND USERACCESS6 IN :ud6List ");
			 * } }
			 */ if (taxPeriod != null) {
	
				build.append(" AND RETURN_PERIOD = :taxPeriod ");
			}
		}

		String buildQuery = build.toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GstinList -->" + gstinList);
			LOGGER.debug("TaxPeriod -->" + taxPeriod);
		}
		

		if("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)){
			queryStr = createGSTR7TransQueryString(buildQuery, type);
		}else{
			queryStr = createQueryString(buildQuery, type);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);
			
           if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {
				
				if (gstin != null && !gstin.isEmpty()) {
					if (gstinList != null && !gstinList.isEmpty()
							&& gstinList.size() > 0) {
						q.setParameter("gstinList", gstinList);
					}
				}

				if (taxPeriod != null) {
					q.setParameter("taxPeriod", taxPeriod);
				}
			} else {
				/*if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
					if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
						q.setParameter("pcList", pcList);
					}
				}
				if (plant != null && !plant.isEmpty()) {
					if (plantList != null && !plantList.isEmpty()
							&& plantList.size() > 0) {
						q.setParameter("plantList", plantList);
					}
				}*/
				/*
				 * if (sales != null && !sales.isEmpty()) { if (salesList != null &&
				 * !salesList.isEmpty() && salesList.size() > 0) {
				 * q.setParameter("salesList", salesList); } }
				 */
				if (gstin != null && !gstin.isEmpty()) {
					if (gstinList != null && !gstinList.isEmpty()
							&& gstinList.size() > 0) {
						q.setParameter("gstinList", gstinList);
					}
				}
				/*if (division != null && !division.isEmpty()) {
					if (divisionList != null && !divisionList.isEmpty()
							&& divisionList.size() > 0) {
						q.setParameter("divisionList", divisionList);
					}
				}*/
				/*
				 * if (location != null && !location.isEmpty()) { if (locationList
				 * != null && !locationList.isEmpty() && locationList.size() > 0) {
				 * q.setParameter("locationList", locationList); } }
				 */ /*if (purchase != null && !purchase.isEmpty()) {
					if (purcList != null && !purcList.isEmpty()
							&& purcList.size() > 0) {
						q.setParameter("purcList", purcList);
					}
				}*/
				/*
				 * if (distChannel != null && !distChannel.isEmpty()) { if (distList
				 * != null && !distList.isEmpty() && distList.size() > 0) {
				 * q.setParameter("distList", distList); } }
				 */
				/*if (ud1 != null && !ud1.isEmpty()) {
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
				}*/
				/*
				 * if (ud4 != null && !ud4.isEmpty()) { if (ud4List != null &&
				 * !ud4List.isEmpty() && ud4List.size() > 0) {
				 * q.setParameter("ud4List", ud4List); } } if (ud5 != null &&
				 * !ud5.isEmpty()) { if (ud5List != null && !ud5List.isEmpty() &&
				 * ud5List.size() > 0) { q.setParameter("ud5List", ud5List); } } if
				 * (ud6 != null && !ud6.isEmpty()) { if (ud6List != null &&
				 * !ud6List.isEmpty() && ud6List.size() > 0) {
				 * q.setParameter("ud6List", ud6List); } }
				 */ 
				if (taxPeriod != null) {
					q.setParameter("taxPeriod", taxPeriod);
				}
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			retList = list.parallelStream().map(o -> convert(o, type))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution getting the data ----->" + retList);
			// return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(
					"Gstr7 Popup Table4/Table3 Query Execution getting error--->{}",
					e);
		}
		return retList;
	}

	public Gstr7PopupRecordsRespDto convert(Object[] obj, String type) {

		Gstr7PopupRecordsRespDto dto = new Gstr7PopupRecordsRespDto();

		if ("Table-3".equalsIgnoreCase(type)) {
			BigInteger id = GenUtil.getBigInteger(obj[0]);

			dto.setId(id.intValue());
			dto.setDocKey((String) obj[1]);
			dto.setGstin((String) obj[2]);
			dto.setAmount((BigDecimal) obj[3]);
			dto.setIgst((BigDecimal) obj[4]);
			dto.setCgst((BigDecimal) obj[5]);
			dto.setSgst((BigDecimal) obj[6]);
		} else if ("Table-4".equalsIgnoreCase(type)) {

			BigInteger id = GenUtil.getBigInteger(obj[0]);

			dto.setId(id.intValue());
			dto.setDocKey((String) obj[1]);
			dto.setOdMonth((String) obj[2]);
			dto.setOdGstin((String) obj[3]);
			dto.setOdAmount((BigDecimal) obj[4]);
			dto.setRdGstin((String) obj[5]);
			dto.setRdAmount((BigDecimal) obj[6]);
			dto.setRdIgst((BigDecimal) obj[7]);
			dto.setRdCgst((BigDecimal) obj[8]);
			dto.setRdSgst((BigDecimal) obj[9]);

		}
		return dto;
	}

	private String createQueryString(String buildQuery, String type) {

		String queryStr = null;

		if ("Table-3".equalsIgnoreCase(type)) {
			queryStr = "SELECT ID,(TDS_DEDUCTOR_GSTIN||'-'||RETURN_PERIOD||'-'||NEW_TDS_DEDUCTEE_GSTIN) AS DOC_KEY,"
					+ "TDS_DEDUCTOR_GSTIN AS GSTIN,"
					+ "IFNULL(SUM(NEW_GROSS_AMT),0) AS AMOUNT_PAID,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT "
					+ "FROM GSTR7_PROCESSED_TDS WHERE IS_DELETE = FALSE "
					+ "AND (ACTION_TYPE IS NULL OR ACTION_TYPE<>'CAN') "
					+ "AND TABLE_NUM='Table-3' AND " + buildQuery
					+ "GROUP BY ID,TDS_DEDUCTOR_GSTIN,RETURN_PERIOD,NEW_TDS_DEDUCTEE_GSTIN";
		} else if ("Table-4".equalsIgnoreCase(type)) {

			queryStr = "SELECT ID,"
					+ "(TDS_DEDUCTOR_GSTIN||'-'||RETURN_PERIOD||'-'"
					+ "||ORG_RETURN_PERIOD||'-'||NEW_TDS_DEDUCTEE_GSTIN) AS DOC_KEY,"
					+ "ORG_RETURN_PERIOD as ORG_MONTH,ORG_TDS_DEDUCTEE_GSTIN AS ORG_GSTIN,"
					+ "IFNULL(SUM(ORG_GROSS_AMT),0) AS ORG_AMT_PAID,"
					+ "NEW_TDS_DEDUCTEE_GSTIN AS REVISED_GSTIN, "
					+ "IFNULL(SUM(NEW_GROSS_AMT),0) AS REVISED_AMT_PAID,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT "
					+ "FROM GSTR7_PROCESSED_TDS WHERE IS_DELETE = FALSE "
					+ "AND (ACTION_TYPE IS NULL OR ACTION_TYPE<>'CAN') "
					+ "AND TABLE_NUM='Table-4' AND " + buildQuery
					+ "GROUP BY ID,TDS_DEDUCTOR_GSTIN,RETURN_PERIOD,"
					+ "ORG_RETURN_PERIOD,NEW_TDS_DEDUCTEE_GSTIN,"
					+ "ORG_RETURN_PERIOD,ORG_TDS_DEDUCTEE_GSTIN";
		}
		return queryStr;
	}
	
	private String createGSTR7TransQueryString(String buildQuery, String type) {

		String queryStr = null;

		if ("Table-3".equalsIgnoreCase(type)) {
			queryStr = 
				      "SELECT ID, "
				    + " DOC_KEY AS KEY, "
				    + "DEDUCTOR_GSTIN AS GSTIN, "
				    + "IFNULL (SUM(TAXABLE_VALUE),0) AS TOTAL_AMT, "
				    + "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, "
				    + "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, "
				    + "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT "
				    + "FROM GSTR7_TRANS_DOC_HEADER TDS "
				    + "WHERE IS_DELETE = FALSE "
				    + "AND SUPPLY_TYPE = 'TAX' "
				    + "AND SECTION = 'TDS' "
				    + buildQuery
				    + "GROUP BY ID, DEDUCTOR_GSTIN, RETURN_PERIOD, DOC_KEY";


		} else if ("Table-4".equalsIgnoreCase(type)) {

			queryStr = 
				      "SELECT ID, "
				    + " DOC_KEY AS DOC_KEY, "
				    + "ORIGINAL_RETURN_PERIOD AS ORG_MONTH, "
				    + "ORIGINAL_DEDUCTEE_GSTIN AS ORG_GSTIN, "
				    + "IFNULL(SUM(ORIGINAL_TAXABLE_VALUE),0) AS ORG_AMT_PAID, "
				    + "DEDUCTEE_GSTIN AS REVISED_GSTIN, "
				    + "IFNULL (SUM(TAXABLE_VALUE),0) AS TOTAL_AMT, "
				    + "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, "
				    + "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, "
				    + "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT "
				    + "FROM GSTR7_TRANS_DOC_HEADER TDS "
				    + "WHERE IS_DELETE = FALSE "
				    + "AND SUPPLY_TYPE = 'TAX' "
				    + "AND SECTION = 'TDSA' "
				    + buildQuery
			        + "GROUP BY ID, DOC_KEY,"
				    + "ORIGINAL_RETURN_PERIOD, "
				    + "ORIGINAL_DEDUCTEE_GSTIN"
				    + "DEDUCTEE_GSTIN";
			
			


		}
		return queryStr;
	}
	
	public static void main(String[] args) {
		
		StringBuilder build = new StringBuilder();

		List<String> gstinList =Arrays.asList("GSTN");
		String taxPeriod = "022025";
//		String type="Table-3";
		String type="Table-4";
		String returnType="GSTR7_TRANSACTIONAL";
		
		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {

				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND DEDUCTOR_GSTIN IN :gstinList ");

				}
			
			build.append(" AND RETURN_PERIOD = :taxPeriod ");
		} else {
		
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" TDS_DEDUCTOR_GSTIN IN :gstinList ");
			}
	   
		
		if (taxPeriod != null) {

			build.append(" AND RETURN_PERIOD = :taxPeriod ");
		}
		}

		String buildQuery = build.toString();

		String queryStr = "";
		
		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {
			queryStr = new Gstr7PopupRecordsFetchDaoImpl().createGSTR7TransQueryString(buildQuery, type);
		} else {
			queryStr = new Gstr7PopupRecordsFetchDaoImpl().createQueryString(buildQuery, type);
		}
		
		System.out.println(queryStr);
		
	}

}
