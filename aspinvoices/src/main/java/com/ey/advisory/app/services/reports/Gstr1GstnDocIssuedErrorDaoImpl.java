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

import com.ey.advisory.app.data.views.client.Gstr1GstnDOCISSUEErrorDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1GstnDocIssuedErrorDaoImpl")
public class Gstr1GstnDocIssuedErrorDaoImpl implements Gstr1GstnErrorDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnDocIssuedErrorDaoImpl.class);

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

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.GSTIN IN :gstinList");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
		}
		if (refId != null) {// Reference Id
			buildQuery.append(" AND HDR.BATCH_ID  = :refId ");
		}

		String queryStr = creategstnDocIssuedErrorQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}
		
		if (refId != null) {
			q.setParameter("refId", refId);
		}
		

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertgstnDocIssuedError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnDOCISSUEErrorDto convertgstnDocIssuedError(Object[] arr) {
		Gstr1GstnDOCISSUEErrorDto obj = new Gstr1GstnDOCISSUEErrorDto();

		obj.setSerialNumber(arr[0] != null ? arr[0].toString() : null);
		obj.setFromserialNumber(arr[1] != null ? arr[1].toString() : null);
		obj.setToserialNumber(arr[2] != null ? arr[2].toString() : null);
		obj.setTotalNumber(arr[3] != null ? arr[3].toString() : null);
		obj.setCancelled(arr[4] != null ? arr[4].toString() : null);
		obj.setNetIssued(arr[5] != null ? arr[5].toString() : null);
		obj.setErrorCode(arr[6] != null ? arr[6].toString() : null);
		obj.setGstnErrorMessage(arr[7] != null ? arr[7].toString() : null);

		return obj;
	}

	private String creategstnDocIssuedErrorQueryString(String buildQuery) {

		return "SELECT HDR.SERIAL_NUM,HDR.FROM_SERIAL_NUM,HDR.TO_SERIAL_NUM,"
				+ "HDR.TOT_NUM,HDR.CANCEL,HDR.NET_ISSUE, TRIM(', ' FROM "
				+ "IFNULL(ERROR_CODE,'')) AS GSTN_ERROR_CODE, TRIM(', ' FROM "
				+ "IFNULL(ERROR_DESCRIPTION,'')) AS GSTN_ERROR_DESCRIPTION "
				+ "FROM GETGSTR1_DOC_ISSUED HDR LEFT OUTER JOIN "
				+ "ANX_OUTWARD_DOC_ERROR ERR ON HDR.ID = ERR.DOC_HEADER_ID "
				+ "WHERE ERROR_SOURCE = 'GSTN'" + buildQuery;
	}
}
