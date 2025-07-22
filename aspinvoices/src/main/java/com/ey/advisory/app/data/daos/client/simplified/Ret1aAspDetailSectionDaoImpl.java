package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Ret1AspDetailRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
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
@Component("Ret1aAspDetailSectionDaoImpl")
public class Ret1aAspDetailSectionDaoImpl implements Ret1AspDetailSectionDao{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1aAspDetailSectionDaoImpl.class);

	public static final String table_3A4 = "3A4";
	public static final String table_3C1 = "3C1";
	public static final String table_4A5 = "4A5";
	public static final String table_4B1 = "4B1";
	public static final String table_4B2 = "4B2";
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Ret1AspDetailRespDto> loadBasicSummarySection(
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

		StringBuilder build = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND GSTIN IN :gstinList ");
			}
		}

		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		LOGGER.debug("Building Where Condition for Inward Query..");

		String queryStr = createQueryString(buildQuery, table);
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

			List<Ret1AspDetailRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private Ret1AspDetailRespDto convert(Object[] arr) {
		Ret1AspDetailRespDto obj = new Ret1AspDetailRespDto();

		LOGGER.debug("Converting array Data To dto ");

		obj.setReturnTable((String) arr[1]);
		obj.setValue((BigDecimal) arr[2]);
		obj.setIgstAmt((BigDecimal) arr[3]);
		obj.setCgstAmt((BigDecimal) arr[4]);
		obj.setSgstAmt((BigDecimal) arr[5]);
		obj.setCessAmt((BigDecimal) arr[6]);
		obj.setProfitCenter((String) arr[7]);
		obj.setPlant((String) arr[8]);
		obj.setDivision((String) arr[9]);
		obj.setLocation((String) arr[10]);
		obj.setSalesOrg((String) arr[11]);
		obj.setDistrChannel((String) arr[12]);
		obj.setUserAccess1((String) arr[13]);
		obj.setUserAccess2((String) arr[14]);
		obj.setUserAccess3((String) arr[15]);
		obj.setUserAccess4((String) arr[16]);
		obj.setUserAccess5((String) arr[17]);
		obj.setUserAccess6((String) arr[18]);
		obj.setUserDefined1((String) arr[19]);
		obj.setUserDefined2((String) arr[20]);
		obj.setUserDefined3((String) arr[21]);
		return obj;
	}

	private String createQueryString(String buildQuery, String table) {

		String queryStr = "";
		LOGGER.debug("Executing ASP Query Executing");
		if (table_3A4.equalsIgnoreCase(table)) {
			queryStr = " SELECT RETURN_TYPE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A'"
					+ "AND RETURN_TABLE= '3A4' "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 ";
		}
		if (table_3C1.equalsIgnoreCase(table)) {
			queryStr = " SELECT RETURN_TYPE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A'"
					+ "AND RETURN_TABLE= '3C1' "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 ";
		}
		if (table_4A5.equalsIgnoreCase(table)) {
			queryStr = " SELECT RETURN_TYPE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A'"
					+ "AND RETURN_TABLE= '4A5' "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 ";
		}
		if (table_4B1.equalsIgnoreCase(table)) {
			queryStr = " SELECT RETURN_TYPE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A'"
					+ "AND RETURN_TABLE= '4B1' "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 ";
		}
		if (table_4B2.equalsIgnoreCase(table)) {
			queryStr = " SELECT RETURN_TYPE,"
					+ "IFNULL(SUM(TAXABLE_VALUE),0) TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) CESS_AMT,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 FROM RET_PROCESSED_USERINPUT "
					+ "WHERE IS_DELETE= FALSE AND RETURN_TYPE='RET-1A'"
					+ "AND RETURN_TABLE= '4B2' "
					+ buildQuery
					+ "GROUP BY RETURN_TYPE,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,"
					+ "USERACCESS5,USERACCESS6,USERDEFINED1,USERDEFINED2,"
					+ "USERDEFINED3 ";
		}
		LOGGER.debug("Executing Asp Query For  END ");
		return queryStr;
	}

}
