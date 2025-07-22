package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1VerticalSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1VerticalSectionDaoImpl")
public class Ret1VerticalSectionDaoImpl implements Ret1VerticalSectionDao{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1VerticalSectionDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Ret1VerticalSummaryRespDto> lateBasicSummarySection(
			Ret1SummaryReqDto req) {
		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		String table = req.getTable();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}

		StringBuilder build1 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build1.append(" AND GSTIN IN :gstinList");
			}
		}
		if (taxPeriod != 0) {

			build1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery1 = build1.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery1,table);

		LOGGER.debug("Outward Query BEGIN----> 3A To 3G " + queryStr);

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Ret1VerticalSummaryRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1VerticalSummaryRespDto convert(Object[] arr) {
		Ret1VerticalSummaryRespDto obj = new Ret1VerticalSummaryRespDto();
		 
		obj.setId((GenUtil.getBigInteger(arr[0])).longValue());
		//obj.setId((Long) arr[0]);
		obj.setGstin((String) arr[1]);
		obj.setTaxPeriod((String) arr[2]);
		obj.setReturnType((String) arr[3]);
		obj.setReturnTable((String) arr[4]);
		obj.setValue((BigDecimal) arr[5]);
		obj.setIgstAmt((BigDecimal) arr[6]);
		obj.setCgstAmt((BigDecimal) arr[7]);
		obj.setSgstAmt((BigDecimal) arr[8]);
		obj.setCessAmt((BigDecimal) arr[9]);
		obj.setPlant((String) arr[10]);
		obj.setProfitCenter((String) arr[11]);
		obj.setDivision((String) arr[12]);
		obj.setLocation((String) arr[13]);
		obj.setDistrChannel((String) arr[14]);
		obj.setSalesOrg((String) arr[15]);
		obj.setUserAccess1((String) arr[16]);
		obj.setUserAccess2((String) arr[17]);
		obj.setUserAccess3((String) arr[18]);
		obj.setUserAccess4((String) arr[19]);
		obj.setUserAccess5((String) arr[20]);
		obj.setUserAccess6((String) arr[21]);
		obj.setUserDefined1((String) arr[22]);
		obj.setUserDefined2((String) arr[23]);
		obj.setUserDefined3((String) arr[24]);
		
		return obj;
	}

	private String createQueryString(String buildQuery1,String table) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");

		String queryString = "";
		if("3C3".equalsIgnoreCase(table)){
			queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
					+ "USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE= '3C3' "
					+ buildQuery1
					+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
					+ "RETURN_TABLE, PROFIT_CENTRE,"
					+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3";
		}
		if("3C4".equalsIgnoreCase(table)){
			queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
					+ "USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
					+ "AND RETURN_TABLE= '3C4' "
					+ buildQuery1
					+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
					+ "RETURN_TABLE, PROFIT_CENTRE,"
					+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3";
		}
			if("3D1".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '3D1' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";
			}
			if("3D2".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '3D2' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";	
				}
			if("3D3".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '3D3' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";
			}
			if("3D4".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '3D4' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";
				}
			if("4A10".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '4A10' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";				}
			if("4A11".equalsIgnoreCase(table)){
				
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '4A11' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";

				}
			if("4B2".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '4B2' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";
				}
			if("4B3".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '4B3' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";	
				}
			
			if("4E1".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '4E1' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";	
				}
			if("4E2".equalsIgnoreCase(table)){
				queryString = "SELECT ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,RETURN_TABLE,"
						+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
						+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
						+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
						+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
						+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,"
						+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
						+ "USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
						+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1' "
						+ "AND RETURN_TABLE= '4E2' "
						+ buildQuery1
						+ "GROUP BY ID,GSTIN,RETURN_PERIOD,RETURN_TYPE,"
						+ "RETURN_TABLE, PROFIT_CENTRE,"
						+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
						+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
						+ "USERDEFINED3";
				}
	
		return queryString;
		}

	@Override
	public List<Ret1VerticalSummaryRespDto> lateBasicSummarySectionRet1A(
			Ret1SummaryReqDto req) {
		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		String table = req.getTable();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}

		StringBuilder build1 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build1.append(" AND GSTIN IN :gstinList");
			}
		}
		if (taxPeriod != 0) {

			build1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery1 = build1.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryStringREt1A(buildQuery1,table);

		LOGGER.debug("Outward Query BEGIN----> 3A To 3G " + queryStr);

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Ret1VerticalSummaryRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}
	
	private String createQueryStringREt1A(String buildQuery1,String table) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");

		String queryString = "";
			if("3D1".equalsIgnoreCase(table)){
				queryString = " SELECT RETURN_TYPE,RETURN_TABLE,"
						+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
						+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
						+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,"
						+ "USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
						+ "FROM RET_PROCESSED_USERINPUT WHERE "
						+ "IS_DELETE= FALSE AND RETURN_TYPE='RET-1A' "
						+ "AND RETURN_TABLE= '3D1' "
						+ buildQuery1
						+ "GROUP BY RETURN_TYPE,RETURN_TABLE,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
						+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 ";
			}
			if("3D2".equalsIgnoreCase(table)){
				queryString = " SELECT RETURN_TYPE,RETURN_TABLE,"
						+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
						+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
						+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,"
						+ "USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
						+ "FROM RET_PROCESSED_USERINPUT WHERE "
						+ "IS_DELETE= FALSE AND RETURN_TYPE='RET-1A' "
						+ "AND RETURN_TABLE= '3D2' "
						+ buildQuery1
						+ "GROUP BY RETURN_TYPE,RETURN_TABLE,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
						+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 ";			}
			
			if("3D3".equalsIgnoreCase(table)){
				queryString = " SELECT RETURN_TYPE,RETURN_TABLE,"
						+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
						+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
						+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,"
						+ "USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
						+ "FROM RET_PROCESSED_USERINPUT WHERE "
						+ "IS_DELETE= FALSE AND RETURN_TYPE='RET-1A' "
						+ "AND RETURN_TABLE= '3D3' "
						+ buildQuery1
						+ "GROUP BY RETURN_TYPE,RETURN_TABLE,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
						+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 ";
			}
			if("3D4".equalsIgnoreCase(table)){
				queryString = " SELECT RETURN_TYPE,RETURN_TABLE,"
						+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
						+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
						+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,"
						+ "USERACCESS1,USERACCESS2,USERACCESS3,"
						+ "USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 "
						+ "FROM RET_PROCESSED_USERINPUT WHERE "
						+ "IS_DELETE= FALSE AND RETURN_TYPE='RET-1A' "
						+ "AND RETURN_TABLE= '3D4' "
						+ buildQuery1
						+ "GROUP BY RETURN_TYPE,RETURN_TABLE,"
						+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
						+ "SALES_ORGANIZATION,"
						+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
						+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
						+ "USERDEFINED1,USERDEFINED2,USERDEFINED3 ";
			}
			
		return queryString;
		}

}
