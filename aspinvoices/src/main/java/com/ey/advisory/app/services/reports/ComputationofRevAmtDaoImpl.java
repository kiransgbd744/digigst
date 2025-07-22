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

import com.ey.advisory.app.data.views.client.ComptReversalAmtDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

	@Component("ComputationofRevAmtDaoImpl")
	public class ComputationofRevAmtDaoImpl implements ItcReversalInwardDao {

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
			  
			 buildHeader.append(" AND C.DERIVED_RET_PERIOD = :taxperiod ");
			 
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

		private ComptReversalAmtDto convertApiInwardProcessedRecords(Object[] arr) {
			ComptReversalAmtDto obj = new ComptReversalAmtDto();

			obj.setGstin(arr[0] != null ? arr[0].toString() : null); 
			obj.setSubsectionName(arr[1] != null ? arr[1].toString() : null);
			obj.setTotalTax(arr[2] != null ? convert(arr[2]).toString() : null);
			obj.setIgst(arr[3] != null ? convert(arr[3]).toString() : null);
			obj.setCgst(arr[4] != null ? convert(arr[4]).toString() : null);
			obj.setSgst(arr[5] != null ? convert(arr[5]).toString() : null);
			obj.setCess(arr[6] != null ? convert(arr[6]).toString() : null);
			return obj;
		}

		private String createInwardApiProcessedRecQueryString(String buildHeader) {
			return "SELECT GSTIN,SUB_SECTION_NAME,TAXABLE_VALUE,"
					+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT "
					+ "FROM ( SELECT GSTIN,SUB_SECTION_NAME,TAXABLE_VALUE, "
					+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT FROM "
					+ "GSTR3B_RULE42_COMPUTE C WHERE IS_ACTIVE = TRUE "
					+ "AND SUB_SECTION_NAME IN ('Total Tax Amount','A','B','C','D_T1','D_T2', "
					+ " 'D_T3','D_T4','E', 'Ratio_1_R42','Ratio_2_R42','Ratio_3_R42', "
					+ " 'D2','Total Reversal', 'ITC Reversal Ratio','Total Reversal_RATIO_3', "
					+ " 'Total Reversal_RATIO_2','Total Reversal_RATIO_1') "
					+ buildHeader
					+ " UNION ALL "
					+ "SELECT C.GSTIN,'ITC Reversal Ratio1' AS SUB_SECTION_NAME, "
					+ "IFNULL(CASE WHEN R.RATIO1=0 THEN NULL ELSE R.RATIO1 END,CASE WHEN substring(RATIO_1, 6, 1) > 5 "
					+ "THEN (LEFT(RATIO_1,5)+0.01) ELSE LEFT(RATIO_1,5) END )"
					+ "AS TAXABLE_VALUE , 0 as IGST_AMT,0 as CGST_AMT,"
					+ "0 as SGST_AMT,0 as CESS_AMT "
					+ "FROM GSTR3B_RULE42_COMPUTE C LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN AND C.TAX_PERIOD=R.TAX_PERIOD "
					+ "AND R.IS_DELETE=FALSE AND C.SECTION_NAME='ITC Reversal Ratio'  WHERE IS_ACTIVE = TRUE "
					+ "AND SUB_SECTION_NAME ='ITC Reversal Ratio' "
					+ buildHeader
					+ " UNION ALL "
					+ "SELECT C.GSTIN,'ITC Reversal Ratio2' AS SUB_SECTION_NAME, "
					+ "IFNULL(CASE WHEN R.RATIO2=0 THEN NULL ELSE R.RATIO2 END,CASE WHEN substring(RATIO_2, 6, 1) > 5 "
					+ "THEN (LEFT(RATIO_2)+0.01) ELSE LEFT(RATIO_2,5) END) "
					+ "AS TAXABLE_VALUE, 0 as IGST_AMT,0 as CGST_AMT,"
					+ "0 as SGST_AMT,0 as CESS_AMT "
					+ "FROM GSTR3B_RULE42_COMPUTE C "
					+ "LEFT JOIN TBL_GSTR3B_42_RATIO_USER R ON C.GSTIN=R.GSTIN "
					+ "AND C.TAX_PERIOD=R.TAX_PERIOD AND R.IS_DELETE=FALSE AND C.SECTION_NAME='ITC Reversal Ratio' "
					+ "WHERE IS_ACTIVE = TRUE "
					+ "AND SUB_SECTION_NAME ='ITC Reversal Ratio' "
					+ buildHeader
					+ " UNION ALL "
					+ "SELECT GSTIN,'ITC Reversal Ratio3' AS SUB_SECTION_NAME, "
					+ "CASE WHEN substring(RATIO_3, 6, 1) > 5 THEN (LEFT(RATIO_3)+0.01) ELSE LEFT(RATIO_3,5) "
					+ "END  AS TAXABLE_VALUE, 0 as IGST_AMT,0 as CGST_AMT,"
					+ "0 as SGST_AMT,0 as CESS_AMT "
					+ "FROM GSTR3B_RULE42_COMPUTE C WHERE IS_ACTIVE = TRUE "
					+ "AND SUB_SECTION_NAME ='ITC Reversal Ratio' "
					+ buildHeader + " ) " 
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

	}

