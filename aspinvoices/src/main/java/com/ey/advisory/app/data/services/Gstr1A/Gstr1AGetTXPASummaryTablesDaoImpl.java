/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1GstnAdvanceAdjustedAmendmentDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Gstr1AGetTXPASummaryTablesDaoImpl")
@Slf4j
public class Gstr1AGetTXPASummaryTablesDaoImpl implements Gstr1AaGetDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("batchSaveStatusRepository") private Gstr1BatchRepository
	 * gstr1BatchRepository;
	 */

	static Integer cutoffPeriod = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Object> getGstnConsolidatedReports(SearchCriteria criteria) {
		GstnConsolidatedReqDto request = (GstnConsolidatedReqDto) criteria;
		String taxPeriod = request.getTaxPeriod();

		List<String> gstr1aSections = request.getGstr1aSections();
		String gstin = request.getGstin();

		StringBuilder buildQuery = new StringBuilder();

		if (StringUtils.isNotBlank(gstin)) {
			buildQuery.append(" WHERE GSTIN IN :gstin");
		}

		if (CollectionUtils.isNotEmpty(gstr1aSections)) {
			buildQuery.append(" AND TABLE_TYPE IN :gstr1aSections");
		}

		if (taxPeriod != null) {
			buildQuery.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
		}

		String queryStr = creategstnTXPAQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotBlank(gstin)) {
			q.setParameter("gstin", Lists.newArrayList(gstin));

		}
		if (taxPeriod != null) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriod());
			q.setParameter("taxPeriod", derivedRetPeriod);

		}
		if (CollectionUtils.isNotEmpty(gstr1aSections)) {
			q.setParameter("gstr1aSections", gstr1aSections);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertgstnTXPSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnAdvanceAdjustedAmendmentDto convertgstnTXPSummary(
			Object[] arr) {
		Gstr1GstnAdvanceAdjustedAmendmentDto obj = new Gstr1GstnAdvanceAdjustedAmendmentDto();

		obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setOriginalReturnPeriod(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplyType(arr[4] != null ? arr[4].toString() : null);
		obj.setOriginaSupplyType(arr[5] != null ? arr[5].toString() : null);
		obj.setStateName(arr[6] != null ? arr[6].toString() : null);
		obj.setPos(arr[7] != null ? arr[7].toString() : null);
		obj.setOriginalPos(arr[8] != null ? arr[8].toString() : null);
		obj.setRateofTax(arr[9] != null ? arr[9].toString() : null);
		obj.setGrossAdvanceAdjusted(
				arr[10] != null ? arr[10].toString() : null);
		obj.setIgstAmount(arr[11] != null ? arr[11].toString() : null);
		obj.setCgstAmount(arr[12] != null ? arr[12].toString() : null);
		obj.setSgstUTGSTAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setCessAmount(arr[14] != null ? arr[14].toString() : null);
		obj.setDifferentialPercentageRate(
				arr[15] != null ? arr[15].toString() : null);
		//obj.setIsFiled(arr[16] != null ? arr[16].toString() : null);
		obj.setIsFiled(isDecimal(arr[16]));
		obj.setSerialNo(arr[19] != null ? arr[19].toString() : null);
		return obj;
	}
	
	private String isDecimal(Object obj) {
		try{
		 if (obj == null)
		        return "N";
		    if (obj instanceof Long){
		        if(Long.parseLong(obj.toString())==0){
		            return "N";
		        }else{
		            return "Y";
		        }
		    }
		
		    if (obj instanceof Boolean) {
		        Boolean b = (Boolean) obj;
		        if(b){
		            return "Y";
		        }else{
		            return "N";
		        }
		    }
		    } catch (Exception e) {
		    	
		    }
		return "N";
	}

	private String creategstnTXPAQueryString(String buildQuery) {

		return "select ID,GSTIN,RETURN_PERIOD,ORG_MONTH,"
				+ "SUPPLY_TYPE,ORG_SUPPLY_TYPE,STATE_NAME,POS,ORG_POS,TAX_RATE,"
				+ "TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "DIFF_PERCENT,IS_FILED,DERIVED_RET_PERIOD,TABLE_TYPE,"
				+ "(ROW_NUMBER () OVER (ORDER BY GSTIN)) AS  SNO "
				+ "from (SELECT HDR.ID,HDR.GSTIN,HDR.RETURN_PERIOD,"
				+ "HDR.ORG_MONTH,'' SUPPLY_TYPE,SUPPLY_TYPE ORG_SUPPLY_TYPE,"
				+ "MT.STATE_NAME,'' POS,POS ORG_POS,ITM.TAX_RATE TAX_RATE,"
				+ "SUM(ITM.ADVADJ_AMT) TAXABLE_VALUE,SUM(ITM.IGST_AMT) IGST_AMT,"
				+ "SUM(ITM.CGST_AMT) CGST_AMT,SUM(ITM.SGST_AMT) SGST_AMT,"
				+ "SUM(ITM.CESS_AMT) CESS_AMT,HDR.DIFF_PERCENT,BT.IS_FILED,"
				+ "HDR.DERIVED_RET_PERIOD,'ADV ADJ-A' AS TABLE_TYPE "
				+ "FROM GETGSTR1A_TXPA_HEADER HDR INNER JOIN "
				+ "GETGSTR1A_TXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "LEFT JOIN MASTER_STATE MT "
				+ "ON SUBSTR(HDR.GSTIN,1,2) = MT.STATE_CODE "
				+ " WHERE HDR.IS_DELETE=FALSE "
				+ " GROUP BY HDR.ID,HDR.GSTIN,HDR.RETURN_PERIOD,HDR.ORG_MONTH,"
				+ "SUPPLY_TYPE,MT.STATE_NAME,POS,ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT,BT.IS_FILED,HDR.DERIVED_RET_PERIOD)"
				+ buildQuery;
	}
}
