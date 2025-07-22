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
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

/**
 * @author Sujith.Nanga
 *
 */

@Component("Gstr1GstnNewCDNURErrorDaoImpl")
public class Gstr1GstnNewCDNURErrorDaoImpl implements Gstr1GstnErrorDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnNewCDNURErrorDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public List<Object> getGstr1GstnErrorReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String taxperiod = request.getTaxperiod();
		 Long refId = null;
		  
		 if (request.getGstnRefId() != null) {// Reference Id
		 List<Gstr1SaveBatchEntity> refDetails = gstr1BatchRepository.selectByrefId
				 (request.getGstnRefId()); 
		 if (refDetails != null) { 
			 for (Gstr1SaveBatchEntity ref : refDetails) { 
				 refId = ref.getId(); 
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
		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" WHERE SUPPLIER_GSTIN IN :gstinList");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD = :taxperiod ");
		}

		
		 if (refId != null) {// Reference Id
		 buildQuery.append(" AND BATCH_ID  = :refId "); 
		 }
		 

		String queryStr = creategstnB2BErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

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

		
		if (refId != null) { 
			q.setParameter("refId", refId); 
			}
		 
		List<Object[]> list = q.getResultList();
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

	private String creategstnB2BErrorQueryString(String buildQuery) {

		return "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DOCUMENT_TYPE,"
				+ "DOCUMENT_NUMBER,DOCUMENT_DATE,CUST_GSTIN,POS,TABLE_TYPE,GSTN_ERROR_DESC,"
				+ "BATCH_ID  " + " FROM GSTR1_GSTN_DELETE_DATA " + buildQuery;
	}
}
