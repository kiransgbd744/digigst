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

import com.ey.advisory.app.data.views.client.Gstr1GstnB2BErrorDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */
@Slf4j
@Component("Gstr1GstnNewB2BErrorDaoImpl")
public class Gstr1GstnNewB2BErrorDaoImpl implements Gstr1GstnErrorDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnNewB2BErrorDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public List<Object> getGstr1GstnErrorReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		String taxperiod = request.getTaxperiod();
		Long batchId = null;

		if (request.getRefId() != null) {// Reference Id
			List<Gstr1SaveBatchEntity> refDetails = gstr1BatchRepository
					.selectByrefId(request.getRefId());
			if (refDetails != null) {
				for (Gstr1SaveBatchEntity ref : refDetails) {
					batchId = ref.getId();
				}
			}
		}

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
		//StringBuilder buildQuery = new StringBuilder();
		List<Object[]> list = first(GSTIN, gstinList, taxperiod,
				batchId);
		if (list==null || list.isEmpty()) {
			List<Object[]> second = second(GSTIN, gstinList,
					taxperiod, batchId);
			list.addAll(second);
		}

		/*
		 * if (GSTIN != null && !GSTIN.isEmpty()) { if (gstinList != null &&
		 * gstinList.size() > 0) {
		 * buildQuery.append(" WHERE SUPPLIER_GSTIN IN :gstinList"); } }
		 * 
		 * if (taxperiod != null && !taxperiod.isEmpty()) {
		 * buildQuery.append(" AND RETURN_PERIOD = :taxperiod "); }
		 * 
		 * 
		 * if (batchId != null) {// Reference Id
		 * buildQuery.append(" AND BATCH_ID  = :batchId "); }
		 * 
		 * buildQuery.append(" AND GSTN_ERROR  = TRUE "); String queryStr =
		 * creategstnB2BErrorQueryString(buildQuery.toString()); Query q =
		 * entityManager.createNativeQuery(queryStr);
		 * 
		 * if (GSTIN != null && !GSTIN.isEmpty()) { if (gstinList != null &&
		 * !gstinList.isEmpty() && gstinList.size() > 0) {
		 * q.setParameter("gstinList", gstinList); } }
		 * 
		 * if (taxperiod != null && !taxperiod.isEmpty()) {
		 * 
		 * int derivedRetPeriod = GenUtil
		 * .convertTaxPeriodToInt(request.getTaxperiod());
		 * 
		 * q.setParameter("taxperiod", taxperiod); }
		 * 
		 * 
		 * if (batchId != null) { q.setParameter("batchId", batchId); }
		 */

		// List<Object[]> list = q.getResultList();

		return list.parallelStream().map(o -> convertgstnB2BError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnB2BErrorDto convertgstnB2BError(Object[] arr) {
		Gstr1GstnB2BErrorDto obj = new Gstr1GstnB2BErrorDto();

		obj.setSgstin(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setDocType(arr[2] != null ? arr[2].toString() : null);
		obj.setDocNum(arr[3] != null ? arr[3].toString() : null);
		obj.setDocDate(arr[4] != null ? arr[4].toString() : null);
		obj.setCustomerGstin(arr[5] != null ? arr[5].toString() : null);
		obj.setPos(arr[6] != null ? arr[6].toString() : null);
		obj.setTableType(arr[7] != null ? arr[7].toString() : null);
		obj.setGstnErrorMessage(arr[8] != null ? arr[8].toString() : null);
		return obj;
	}

	private StringBuffer creategstnB2BErrorQueryString(String buildQuery) {

		StringBuffer bufferString = new StringBuffer();
		bufferString.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DOCUMENT_TYPE,");
		bufferString.append("DOCUMENT_NUMBER,DOCUMENT_DATE,CUST_GSTIN,POS,TABLE_TYPE,");
		bufferString.append("GSTN_ERROR_DESC,BATCH_ID  FROM GSTR1_GSTN_DELETE_DATA ");
		bufferString.append(buildQuery);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("1st query{}",bufferString);
			
		}
		return bufferString;
		/*return "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DOCUMENT_TYPE,"
				+ "DOCUMENT_NUMBER,DOCUMENT_DATE,CUST_GSTIN,POS,TABLE_TYPE,"
				+ "GSTN_ERROR_DESC,BATCH_ID  " + " FROM GSTR1_GSTN_DELETE_DATA "
				+ buildQuery;*/
	}

	

	public List<Object[]> first(String GSTIN, List<String> gstinList,
			 String taxperiod, Long batchId) {
		StringBuilder buildQuery = new StringBuilder();
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" WHERE SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD = :taxperiod ");
		}
		if (batchId != null) {// Reference Id
			buildQuery.append(" AND BATCH_ID  = :batchId ");
		}
		buildQuery.append(" AND GSTN_ERROR  = TRUE ");
		StringBuffer queryStr = creategstnB2BErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr.toString());
if(LOGGER.isDebugEnabled()){
	LOGGER.debug("1st syntax query{} ",q);
	
}
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			/*
			 * int derivedRetPeriod = GenUtil
			 * .convertTaxPeriodToInt(request.getTaxperiod());
			 */
			q.setParameter("taxperiod", taxperiod);
		}

		if (batchId != null) {
			q.setParameter("batchId", batchId);
		}

		List<Object[]> list = q.getResultList();
		return list;
	}
	private StringBuilder creategstnautoErrorQueryString(String buildQuery) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT SGSTIN_EINV,RET_PERIOD_EINV,DOC_TYPE_EINV,");
		query.append("DOC_NUM_EINV,DOC_DATE_EINV,CGSTIN_EINV,BILLING_POS_EINV,TABLE_TYPE_EINV,");
		query.append("GSTN_ERROR_DESC,BATCH_ID  FROM TBL_EINV_RECON_RESP_PSD  ");
		query.append(buildQuery);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug(" second query{}",query);
		}
		return query;
		/*return "SELECT SGSTIN_EINV,RET_PERIOD_EINV,DOC_TYPE_EINV,"
				+ "DOC_NUM_EINV,DOC_DATE_EINV,CGSTIN_EINV,BILLING_POS_EINV,TABLE_TYPE_EINV,"
				+ "GSTN_ERROR_DESC,BATCH_ID  "
				+ " FROM TBL_EINV_RECON_RESP_PSD " + buildQuery;*/
	}
	public List<Object[]> second(String GSTIN, List<String> gstinList,
			 String taxperiod, Long batchId) {
		StringBuilder buildQuery = new StringBuilder();
		if (GSTIN != null && !GSTIN.isEmpty()) {
			
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" WHERE SGSTIN_EINV IN :gstinList");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND RET_PERIOD_EINV = :taxperiod ");
		}

		if (batchId != null) {// Reference Id
			buildQuery.append(" AND BATCH_ID  = :batchId ");
		}

		buildQuery.append(" AND GSTN_ERROR  = TRUE ");
		StringBuilder queryStr = creategstnautoErrorQueryString(buildQuery.toString());
		
		Query q = entityManager.createNativeQuery(queryStr.toString());

if(LOGGER.isDebugEnabled()){
	LOGGER.debug("second query  {}",q);
}
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			/*
			 * int derivedRetPeriod = GenUtil
			 * .convertTaxPeriodToInt(request.getTaxperiod());
			 */
			q.setParameter("taxperiod", taxperiod);
		}

		if (batchId != null) {
			q.setParameter("batchId", batchId);
		}

		List<Object[]> list = q.getResultList();
		return list;
	}
}
