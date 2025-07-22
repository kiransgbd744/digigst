/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.data.views.client.Gstr1GstnHsnSummaryTableDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */

@Component("Gstr1AaRateHsnSummaryTablesDaoImpl")
@Slf4j
public class Gstr1AaRateHsnSummaryTablesDaoImpl implements Gstr1AaGetDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnOrSacRepository;

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

		String queryStr = creategstnHsnQueryString(buildQuery.toString());
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
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<HsnOrSacMasterEntity> hsnOrSacMasterEntities = hsnOrSacRepository
					.findAll();
			Map<String, String> hsnMap = new HashMap<String, String>();
			hsnOrSacMasterEntities.forEach(entity -> {
				hsnMap.put(entity.getHsnSac(), entity.getDescription());
			});

			for (Object arr[] : list) {
				verticalHsnList.add(convertProcessedHsn(arr, hsnMap));
			}
		}
		return verticalHsnList;
	}

	private Gstr1GstnHsnSummaryTableDto convertProcessedHsn(Object[] arr,
			Map<String, String> hsnMap) {
		Gstr1GstnHsnSummaryTableDto obj = new Gstr1GstnHsnSummaryTableDto();

		obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setHsn(arr[3] != null ? arr[3].toString() : null);
		obj.setDescription(arr[4] != null ? arr[4].toString() : null);
		obj.setUqc(arr[5] != null ? arr[5].toString() : null);
		obj.setTotalQuantity(arr[6] != null ? arr[6].toString() : null);
		obj.setTaxableValue(arr[7] != null ? arr[7].toString() : null);
		obj.setIgstAmount(arr[8] != null ? arr[8].toString() : null);
		obj.setCgstAmount(arr[9] != null ? arr[9].toString() : null);
		obj.setsGSTUTGSTAmount(arr[10] != null ? arr[10].toString() : null);
		obj.setCessAmount(arr[11] != null ? arr[11].toString() : null);
		obj.setTaxRate(arr[12] != null ? arr[12].toString() : null);
		
		/*if (arr[13] == null) {
			obj.setIsFiled(APIConstants.N);
		}
		if (arr[13] != null) {
			Boolean b=(Boolean) arr[13];
			if (b) {
				obj.setIsFiled(APIConstants.Y);
			} else {
				obj.setIsFiled(APIConstants.N);
			}
		}*/
		obj.setIsFiled(isDecimal(arr[13]));
		obj.setSerialNumber(arr[16] != null ? arr[16].toString() : null);
		// obj.setIsFiled((arr[13] != null || )?
		// !arr[13].toString().equalsIgnoreCase(APIConstants.N):APIConstants.Y:
		// APIConstants.N);
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


	private String creategstnHsnQueryString(String buildQuery) {

		return "select ID,GSTIN,RETURN_PERIOD,HSNORSAC,ITM_DESC,"
				+ "ITM_UQC,ITM_QTY,TAXABLE_VALUE,IGST_AMT,CGST_AMT,"
				+ "SGST_AMT,CESS_AMT,TAX_RATE,IS_FILED,DERIVED_RET_PERIOD,"
				+ "TABLE_TYPE,(ROW_NUMBER () OVER (ORDER BY GSTIN)) AS  SNO "
				+ "from (SELECT HDR.ID,HDR.GSTIN,HDR.RETURN_PERIOD,"
				+ "HSNORSAC,"
				+ " ITM_DESC,ITM_UQC,ITM_QTY,SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT,"
				+ "SUM(TAX_RATE) TAX_RATE,BT.IS_FILED,HDR.DERIVED_RET_PERIOD,"
				+ " 'HSN' AS TABLE_TYPE "
				+ "FROM GETGSTR1A_HSNORSAC HDR LEFT JOIN GETANX1_BATCH_TABLE BT "
				+ "ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE=FALSE "
				+ " GROUP BY HDR.ID,HDR.GSTIN,HDR.RETURN_PERIOD,HSNORSAC,"
				+ "ITM_DESC,ITM_UQC,ITM_QTY,BT.IS_FILED,HDR.DERIVED_RET_PERIOD) "
				+ buildQuery;

	}
}
