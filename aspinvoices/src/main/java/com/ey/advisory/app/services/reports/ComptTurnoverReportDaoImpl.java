/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.ReversalComputeDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("ComptTurnoverReportDaoImpl")
public class ComptTurnoverReportDaoImpl implements ItcReversalInwardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getItcReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		/*String dataType = request.getDataType();*/
		 String returnperiod =request.getTaxperiod();
		 int taxperiod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String GSTIN = null;

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

			}

		}
		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" AND C.GSTIN IN :gstinList");

			}
		}
        if (returnperiod != null && !returnperiod.isEmpty()) {
		  
		 buildHeader.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
		 
		 }
		 
		String queryStr = createInwardApiProcessedRecQueryString(
				buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (returnperiod != null && !returnperiod.isEmpty()) {
			q.setParameter("taxperiod", taxperiod);
		}
		
		List<Object[]> list = q.getResultList();
		return list.parallelStream()
				.map(o -> convertApiInwardProcessedRecords(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ReversalComputeDto convertApiInwardProcessedRecords(Object[] arr) {
		ReversalComputeDto obj = new ReversalComputeDto();
		
		//arr[0] = CommonUtility.singleQuoteCheck(arr[0]);
		obj.setGstin(arr[0] != null ? arr[0].toString() : null); 
		obj.setSubSectionName(arr[1] != null ? arr[1].toString() : null);
		obj.setTaxbleValue(
				arr[2] != null ? convert(arr[2]).toString() : null);
		return obj;
	}

	private static String createInwardApiProcessedRecQueryString(String buildHeader) {
		return "SELECT GSTIN,SUB_SECTION_NAME,TAXABLE_VALUE "
				+ "FROM (SELECT GSTIN,SUB_SECTION_NAME,TAXABLE_VALUE "
				+ "FROM GSTR3B_RULE42_COMPUTE C " + "WHERE IS_ACTIVE = TRUE AND "
				+ "SUB_SECTION_NAME IN ('TAX','B2CS <>0','SEZ','DXP','EXPT','EXPWT', "
				+ " 'DTA','EXT','NIL','B2CS = 0','Non GST /SCH3', 'TOTAL_GSTIN_SUPPLIES',"
				+ " 'TTL_RCS', 'TAX_REVERSE_CHARGE','DTA_REVERSE_CHARGE',"
				+ " 'DXP_REVERSE_CHARGE', 'SEZ_REVERSE_CHARGE','T_GSTIN_SUPPLIES') " 
				+ buildHeader
				+ " UNION ALL "
				+ "SELECT C.GSTIN,'ITC Reversal Ratio1' AS SUB_SECTION_NAME, "
				+ "IFNULL(CASE WHEN R.RATIO1=0 THEN NULL ELSE R.RATIO1 END,CASE WHEN substring(RATIO_1, 6, 1) > 5 "
				+ " THEN (LEFT(RATIO_1,5)+0.01) ELSE LEFT(RATIO_1,5)END )"
				+ "AS TAXABLE_VALUE FROM GSTR3B_RULE42_COMPUTE C "
				+ " LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN\r\n" + 
				"AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE AND C.SECTION_NAME='ITC Reversal Ratio' "
				+ " WHERE IS_ACTIVE = TRUE AND SUB_SECTION_NAME ='ITC Reversal Ratio' "
				+ buildHeader
				+ " UNION ALL SELECT C.GSTIN,'ITC Reversal Ratio2' AS SUB_SECTION_NAME, "
				+ "IFNULL(CASE WHEN R.RATIO2=0 THEN NULL ELSE R.RATIO2 END,CASE WHEN substring(RATIO_2, 6, 1) > 5 \r\n" + 
				"THEN (LEFT(RATIO_2)+0.01) ELSE LEFT(RATIO_2,5) END)"
				+ "AS TAXABLE_VALUE "
				+ "FROM GSTR3B_RULE42_COMPUTE C LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN\r\n" + 
				"AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE AND C.SECTION_NAME='ITC Reversal Ratio' WHERE IS_ACTIVE = TRUE "
				+ "AND SUB_SECTION_NAME ='ITC Reversal Ratio' " + buildHeader
				+ " UNION ALL "
				+ "SELECT GSTIN,'ITC Reversal Ratio3' AS SUB_SECTION_NAME, "
				+ "CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ELSE LEFT(RATIO_3,5) END "
				+ "AS TAXABLE_VALUE FROM GSTR3B_RULE42_COMPUTE C "
				+ " WHERE IS_ACTIVE = TRUE "
				+ "AND SUB_SECTION_NAME ='ITC Reversal Ratio' " + buildHeader
				+ " UNION ALL SELECT GSTIN,case when SUB_SECTION_NAME = 'EXT' THEN 'EXT_19' "
				+ "WHEN SUB_SECTION_NAME = 'NIL' THEN 'NIL_20' "
				+ "WHEN SUB_SECTION_NAME = 'Non GST /SCH3' THEN 'Non GST /SCH3_27' "
				+ "WHEN SUB_SECTION_NAME = 'TTL_RCS' THEN 'TTL_RCS_21' "
				+ "WHEN SUB_SECTION_NAME = 'TOTAL_GSTIN_SUPPLIES' "
				+ "THEN 'TOTAL_GSTIN_SUPPLIES__31' END AS SUB_SECTION_NAME, TAXABLE_VALUE "
				+ "FROM GSTR3B_RULE42_COMPUTE C WHERE IS_ACTIVE = TRUE "
				+ "AND SUB_SECTION_NAME IN ('EXT','NIL','Non GST /SCH3','TTL_RCS','TOTAL_GSTIN_SUPPLIES') "
				+ buildHeader
				+ " UNION ALL "
				+ "SELECT GSTIN,case when SUB_SECTION_NAME = 'EXT' THEN 'EXT_33' "
				+ "WHEN SUB_SECTION_NAME = 'NIL' THEN 'NIL_34' WHEN SUB_SECTION_NAME = 'Non GST /SCH3' "
				+ "THEN 'Non GST /SCH3_35' WHEN SUB_SECTION_NAME = 'TTL_RCS' "
				+ "THEN 'TTL_RCS_36' WHEN SUB_SECTION_NAME = 'TOTAL_GSTIN_SUPPLIES' "
				+ "THEN 'TOTAL_GSTIN_SUPPLIES__38' END AS SUB_SECTION_NAME, "
				+ "TAXABLE_VALUE FROM GSTR3B_RULE42_COMPUTE C WHERE IS_ACTIVE = TRUE "
				+ "AND SUB_SECTION_NAME IN ('EXT','NIL','Non GST /SCH3','TTL_RCS',"
				+ " 'TOTAL_GSTIN_SUPPLIES') "
				+ buildHeader
				+ "union all SELECT GSTIN,case when SUB_SECTION_NAME = 'EXT' "
				+ "THEN 'EXT_25' WHEN SUB_SECTION_NAME = 'NIL' THEN 'NIL_26' "
				+ "WHEN SUB_SECTION_NAME = 'TTL_RCS' THEN 'TTL_RCS_29' "
				+ "WHEN SUB_SECTION_NAME = 'TOTAL_GSTIN_SUPPLIES' THEN 'TOTAL_GSTIN_SUPPLIES_18' END "
				+ "AS SUB_SECTION_NAME, TAXABLE_VALUE FROM "
				+ "GSTR3B_RULE42_COMPUTE C WHERE IS_ACTIVE = TRUE "
				+ "AND SUB_SECTION_NAME IN ('EXT','NIL','TTL_RCS','TOTAL_GSTIN_SUPPLIES') " + buildHeader +  " ) "
				+ " ORDER BY GSTIN "; 
				
	}
	private Object convert(Object obj) {
		if(obj==null) return obj;
		if(obj.toString().contains("E")){
			BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
			docNoDecimalFormat = new BigDecimal(obj.toString());
			Long taxablevalue = docNoDecimalFormat.longValue();
			//obj=new BigDecimal("taxablevalue");
			obj = (Object)taxablevalue;
			}
		return obj;
	}
	
	public static void main(String[] args) {
		
		StringBuilder buildHeader = new StringBuilder();

		if (true) {
			if (true) {
				buildHeader.append(" AND C.GSTIN IN :gstinList");

			}
		}
        if (true) {
		  
		 buildHeader.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
		 
		 }
		 
		String queryStr = createInwardApiProcessedRecQueryString(
				buildHeader.toString());
	
		
		System.out.println(queryStr);
	}
}



