/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.daos.get2a.GetGstr2aDetailStatusFetchDaoImpl;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("GetProcessedVsSubmittedStatusDaoImpl")
public class GetProcessedVsSubmittedStatusDaoImpl
		implements GetProcessedVsSubmittedStatusDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr2aDetailStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> findDataByCriteria(
			ProcessedVsSubmittedRequestDto criteria, List<String> gstinList) {

		List<String> gstin = gstinList;

		String taxPeriodFrom = criteria.getTaxPeriodFrom();
		String taxPeriodTo = criteria.getTaxPeriodTo();

		int derivedTaxPeriodFrom = 0;
		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			derivedTaxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
		}
		int derivedTaxPeriodTo = 0;
		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			derivedTaxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		}
		StringBuilder queryStr = createQueryString(gstin, derivedTaxPeriodFrom,
				derivedTaxPeriodTo);

		Query query = entityManager.createNativeQuery(queryStr.toString());
		if (CollectionUtils.isNotEmpty(gstin)) {
			query.setParameter("gstin", gstin);
		}
		if (derivedTaxPeriodFrom != 0 && derivedTaxPeriodTo != 0) {
			query.setParameter("derivedTaxPeriodFrom", derivedTaxPeriodFrom);
			query.setParameter("derivedTaxPeriodTo", derivedTaxPeriodTo);
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

	private StringBuilder createQueryString(List<String> gstin,
			int derivedTaxPeriodFrom, int derivedTaxPeriodTo) {

		StringBuilder queryBuilder = new StringBuilder();

		if (derivedTaxPeriodFrom != 0 && derivedTaxPeriodTo != 0) {
			queryBuilder.append(
					" AND DERIVED_RET_PERIOD BETWEEN :derivedTaxPeriodFrom AND :derivedTaxPeriodTo");
		}
		if (CollectionUtils.isNotEmpty(gstin)) {
			queryBuilder.append(" AND GSTIN IN :gstin ");
		}
		String condition = queryBuilder.toString();
		StringBuilder buffer = new StringBuilder();
		buffer.append("SELECT SUPPLIER_GSTIN, RETURN_PERIOD,DERIVED_RET_PERIOD,"
				+ " GET_TYPE, STATUS ,LAST_UPDATED FROM(SELECT GSTIN AS "
				+ "SUPPLIER_GSTIN , RETURN_PERIOD,DERIVED_RET_PERIOD, "
				+ "GET_TYPE, STATUS , CREATED_ON LAST_UPDATED ,ROW_NUMBER() "
				+ "OVER(PARTITION BY GET_TYPE,GSTIN ORDER BY CREATED_ON DESC) "
				+ "AS ROWNUM_DT FROM GETANX1_BATCH_TABLE WHERE "
				+ "IS_DELETE=FALSE AND API_SECTION='GSTR1' AND GET_TYPE IN "
				+ "('B2B','B2BA','B2CL','B2CLA','B2CS','B2CSA','EXP','EXPA', "
				+ "'CDNR','CDNRA','CDNUR','CDNURA','NIL','AT','ATA','TXP',"
				+ "'TXPA','DOC_ISSUE','HSN') "
				+ condition + ")"
				+ " BT WHERE BT.ROWNUM_DT=1 ");
		return buffer;
	}

}
