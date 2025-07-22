/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1GstnnilnonexemptSummarylevelDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Gstr1GetNilNonExemptSummaryTablesDaoImpl")
@Slf4j
public class Gstr1GetNilNonExemptSummaryTablesDaoImpl implements Gstr1aGetDao {

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

		String queryStr = creategstnNILQueryString(
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
		return list.parallelStream().map(o -> convertgstnnilnonexemptSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnnilnonexemptSummarylevelDto convertgstnnilnonexemptSummary(
			Object[] arr) {
		Gstr1GstnnilnonexemptSummarylevelDto obj = new Gstr1GstnnilnonexemptSummarylevelDto();

		obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setNilAmt(arr[4] != null ? arr[4].toString() : null);
		obj.setExemptedAmt(arr[5] != null ? arr[5].toString() : null);
		obj.setNonGstSupAmt(arr[6] != null ? arr[6].toString() : null);
		//obj.setIsFiled(arr[7] != null ? arr[7].toString() : null);
		obj.setIsFiled(isDecimal(arr[7]));
		obj.setSerialNo(arr[10] != null ? arr[10].toString() : null);
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

	private String creategstnNILQueryString(String buildQuery) {

		return "select ID,GSTIN,RETURN_PERIOD,SUPPLY_TYPE,NIL_AMT,"
				+ "EXPT_AMT,NONGST_SUP_AMT,IS_FILED,"
				+ "DERIVED_RET_PERIOD,TABLE_TYPE,"
				+ "(ROW_NUMBER () OVER (ORDER BY GSTIN)) AS  SNO from (SELECT HDR.ID,"
				+ "HDR.GSTIN,HDR.RETURN_PERIOD,SUPPLY_TYPE,"
				+ "SUM(NIL_AMT) NIL_AMT,SUM(EXPT_AMT) EXPT_AMT,"
				+ "SUM(NONGST_SUP_AMT) NONGST_SUP_AMT,BT.IS_FILED,HDR.DERIVED_RET_PERIOD,"
				+ " 'NILEXTNON' AS TABLE_TYPE "
				+ "FROM GETGSTR1_NILEXTNON HDR LEFT JOIN GETANX1_BATCH_TABLE BT "
				+ "ON HDR.BATCH_ID = BT.ID WHERE " + " HDR.IS_DELETE=FALSE "
				+ " GROUP BY HDR.ID,HDR.GSTIN,HDR.RETURN_PERIOD,"
				+ "SUPPLY_TYPE,BT.IS_FILED,HDR.DERIVED_RET_PERIOD)"
				+ buildQuery;
	}
}
