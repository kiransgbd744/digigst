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

import com.ey.advisory.app.data.views.client.Gstr1GstnAmendmentsErrorDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1GstnAmendmentsDaoImpl")
public class Gstr1GstnAmendmentsDaoImpl implements Gstr1GstnErrorDao {
	
	private static final String GSTR1_GSTN_AMENDMENTS_ERRORS_VW = "REPORTS/GSTN_AMENDMENTS";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnAmendmentsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public List<Object> getGstr1GstnErrorReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		
		String gstnRefId = request.getGstnRefId();
		Long refId = null;
		
		if (request.getGstnRefId() != null) {// Reference Id
			List<Gstr1SaveBatchEntity> refDetails = gstr1BatchRepository
					.selectByrefId(request.getGstnRefId());
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
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" WHERE DERIVED_RET_PERIOD = :taxperiod");
		}

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		
		if (refId != null) {// Reference Id
			buildQuery.append(" AND BATCH_ID  = :refId ");
		}

String queryStr = null;
		
		if(request.getReturnType()!=null && APIConstants.GSTR1A.equalsIgnoreCase(request.getReturnType()))
			
		{
			queryStr = creategstnGstr1AAmendmentsQueryString(buildQuery.toString());
		}
		else
		{
			queryStr = creategstnAmendmentsQueryString(
				buildQuery.toString());
		
		}
		Query q = entityManager.createNativeQuery(queryStr);
		
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		
		if (refId != null) {
			q.setParameter("refId", refId);
		}
		
		
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertgstnAmendmentsError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnAmendmentsErrorDto convertgstnAmendmentsError(
			Object[] arr) {
		Gstr1GstnAmendmentsErrorDto obj = new Gstr1GstnAmendmentsErrorDto();

		obj.setTypeofTransaction(arr[0] != null ? arr[0].toString() : null);
		obj.setOriginalMonth(arr[1] != null ? arr[1].toString() : null);
		obj.setOriginalPlaceofSupply(arr[2] != null ? arr[2].toString() : null);
		obj.setOriginalSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setCustomerGSTINUIN(arr[4] != null ? arr[4].toString() : null);
		obj.setInvoiceNumber(arr[5] != null ? arr[5].toString() : null);
		obj.setInvoiceDate(arr[6] != null ? arr[6].toString() : null);
		obj.setCreditdebitNoteNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setCreditdebitNoteDate(arr[8] != null ? arr[8].toString() : null);
		obj.setShippingbillNumber(arr[9] != null ? arr[9].toString() : null);
		obj.setShippingbillDate(arr[10] != null ? arr[10].toString() : null);
		obj.setPortCode(arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalinvoiceNumber(
				arr[12] != null ? arr[12].toString() : null);
		obj.setOriginalinvoiceDate(arr[13] != null ? arr[13].toString() : null);
		obj.setTaxableValue(arr[14] != null ? arr[14].toString() : null);
		obj.setRate(arr[15] != null ? arr[15].toString() : null);
		obj.setIntegratedAmount(arr[16] != null ? arr[16].toString() : null);
		obj.setCentralTaxAmount(arr[17] != null ? arr[17].toString() : null);
		obj.setStateTaxAmount(arr[18] != null ? arr[18].toString() : null);
		obj.setCessAmount(arr[19] != null ? arr[19].toString() : null);
		obj.setInvoiceValue(arr[20] != null ? arr[20].toString() : null);
		obj.setReverseCharge(arr[21] != null ? arr[21].toString() : null);
		obj.setSupplyType(arr[22] != null ? arr[22].toString() : null);
		obj.setPlaceofSupply(arr[23] != null ? arr[23].toString() : null);
		obj.setType(arr[24] != null ? arr[24].toString() : null);
		obj.setEcomOperator(arr[25] != null ? arr[25].toString() : null);
		obj.setErrorCode(arr[26] != null ? arr[26].toString() : null);
		obj.setGstnErrorMessage(arr[27] != null ? arr[27].toString() : null);
		obj.setGstnRefId(arr[30] != null ? arr[30].toString() : null);

		return obj;
	}

	private String creategstnAmendmentsQueryString(String buildQuery) {

		return "SELECT * FROM GSTR1_GSTN_AMENDMENTS_ERRORS_VW " + buildQuery;
	}
	
	private String creategstnGstr1AAmendmentsQueryString(String buildQuery) {

		return "SELECT * FROM GSTR1A_GSTN_AMENDMENTS_ERRORS_VW " + buildQuery;
	}
}
