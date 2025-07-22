package com.ey.advisory.app.services.daos.gstr6a;

import java.time.YearMonth;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
/**
 * 
 * @author SriBhavya
 *
 */
@Component("GetGstr6aDetailStatusFetchDaoImpl")
public class GetGstr6aDetailStatusFetchDaoImpl implements GetGstr6aDetailStatusFetchDao{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr6aDetailStatusFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object[]> findDataByCriteria(
			GetAnx2DetailStatusReqDto criteria) {

		List<String> gstin = criteria.getGstin();
		String taxPeriod = criteria.getTaxPeriod();
		
		String fromPeriod = criteria.getFromPeriod();
		String fromDerivedYearMonth = fromPeriod.substring(2, 6) +
				fromPeriod.substring(0, 2);
		String toPeriod = criteria.getToPeriod();
		String toDerivedYearMonth = toPeriod.substring(2, 6) +
				toPeriod.substring(0, 2);
		StringBuilder queryStr = createQueryString(gstin, taxPeriod, fromPeriod, toPeriod);

		Query query = entityManager.createNativeQuery(queryStr.toString());
		if (CollectionUtils.isNotEmpty(gstin)) {
			query.setParameter("gstin", gstin);
		}

		if (StringUtils.isNotEmpty(taxPeriod)) {
			query.setParameter("taxPeriod", taxPeriod);
		}
		
		if (StringUtils.isNotEmpty(fromPeriod)) {
			query.setParameter("fromDerivedYearMonth", fromDerivedYearMonth);
		}
		
		if (StringUtils.isNotEmpty(toPeriod)) {
			query.setParameter("toDerivedYearMonth", toDerivedYearMonth);
		}

		List<Object[]> itemsList = query.getResultList();
		return itemsList;
	}

	private StringBuilder createQueryString(List<String> gstin,
			String taxPeriod, String fromPeriod, String toPeriod) {

		StringBuilder queryBuilder = new StringBuilder();

		if (StringUtils.isNotEmpty(fromPeriod) && StringUtils.isNotEmpty(toPeriod)) {
			queryBuilder.append(" WHERE DERIVED_RET_PERIOD BETWEEN :fromDerivedYearMonth AND :toDerivedYearMonth");
		} else if(StringUtils.isNotEmpty(taxPeriod)) {
			queryBuilder.append(" WHERE RETURN_PERIOD = :taxPeriod");
		}
		if (CollectionUtils.isNotEmpty(gstin)) {
			if (StringUtils.isNotEmpty(taxPeriod) || StringUtils.isNotEmpty(fromPeriod)) {
				queryBuilder.append(" AND CUST_GSTIN IN :gstin ");
			}else{
				queryBuilder.append(" WHERE CUST_GSTIN IN :gstin ");	
			}
			
		}
		String condition = queryBuilder.toString();
		StringBuilder buffer = new StringBuilder();
		buffer.append(
				" SELECT CUST_GSTIN ,RETURN_PERIOD ,B2B_STATUS ,B2B_LAST_UPDATED ,B2BA_STATUS ,B2BA_LAST_UPDATED ,CDN_STATUS ,CDN_LAST_UPDATED ,CDNA_STATUS ,CDNA_LAST_UPDATED  FROM ( SELECT CUST_GSTIN ,RETURN_PERIOD, DERIVED_RET_PERIOD, CASE WHEN GET_TYPE='B2B' THEN STATUS END B2B_STATUS ,CASE WHEN GET_TYPE='B2B' THEN LAST_UPDATED END B2B_LAST_UPDATED ,'' AS B2BA_STATUS, '' AS B2BA_LAST_UPDATED , '' AS CDN_STATUS , '' AS CDN_LAST_UPDATED , '' AS CDNA_STATUS , '' AS CDNA_LAST_UPDATED  FROM ( SELECT BT.GSTIN AS CUST_GSTIN , BT.RETURN_PERIOD AS RETURN_PERIOD ,DERIVED_RET_PERIOD, GET_TYPE ,STATUS ,CASE WHEN STATUS IN('FAILED','SUCCESS','SUCCESS_WITH_NO_DATA') THEN MAX(BT.END_TIME) WHEN STATUS IN('INITIATED','INPROGRESS') THEN MAX(BT.CREATED_ON) END LAST_UPDATED FROM GETANX1_BATCH_TABLE BT WHERE BT.IS_DELETE=FALSE AND BT.API_SECTION='GSTR6A' AND BT.GET_TYPE IN ('B2B') GROUP BY BT.GSTIN,BT.RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE) ");
		if (!condition.equals("")) {
			buffer.append(condition);
		}
		buffer.append(
				" GROUP BY CUST_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE,LAST_UPDATED UNION ALL SELECT CUST_GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD, '' AS B2B_STATUS , '' AS B2B_LAST_UPDATED , CASE WHEN GET_TYPE='B2BA' THEN STATUS END B2BA_STATUS , CASE WHEN GET_TYPE='B2BA' THEN LAST_UPDATED END B2BA_LAST_UPDATED , '' AS CDN_STATUS , '' AS CDN_LAST_UPDATED , '' AS CDNA_STATUS , '' AS CDNA_LAST_UPDATED  FROM ( SELECT BT.GSTIN AS CUST_GSTIN , BT.RETURN_PERIOD AS RETURN_PERIOD ,DERIVED_RET_PERIOD, GET_TYPE ,STATUS ,CASE WHEN STATUS IN('FAILED','SUCCESS','SUCCESS_WITH_NO_DATA') THEN MAX(BT.END_TIME) WHEN STATUS IN('INITIATED','INPROGRESS') THEN MAX(BT.CREATED_ON) END LAST_UPDATED FROM GETANX1_BATCH_TABLE BT WHERE BT.GET_TYPE IN ('B2BA') AND BT.IS_DELETE=FALSE AND BT.API_SECTION='GSTR6A' GROUP BY BT.GSTIN,BT.RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE) ");
		if (!condition.equals("")) {
			buffer.append(condition);
		}
		buffer.append(
				" GROUP BY CUST_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE,LAST_UPDATED UNION ALL SELECT CUST_GSTIN ,RETURN_PERIOD ,DERIVED_RET_PERIOD, '' AS B2B_STATUS , '' AS B2B_LAST_UPDATED , '' AS B2BA_STATUS , '' AS B2BA_LAST_UPDATED , CASE WHEN GET_TYPE='CDN' THEN STATUS END CDN_STATUS , CASE WHEN GET_TYPE='CDN' THEN LAST_UPDATED END CDN_LAST_UPDATED , '' AS CDNA_STATUS , '' AS CDNA_LAST_UPDATED  FROM ( SELECT BT.GSTIN AS CUST_GSTIN , BT.RETURN_PERIOD AS RETURN_PERIOD ,DERIVED_RET_PERIOD, GET_TYPE , STATUS ,CASE WHEN STATUS IN('FAILED','SUCCESS','SUCCESS_WITH_NO_DATA') THEN MAX(BT.END_TIME) WHEN STATUS IN('INITIATED','INPROGRESS') THEN MAX(BT.CREATED_ON) END LAST_UPDATED FROM GETANX1_BATCH_TABLE BT WHERE BT.IS_DELETE=FALSE AND BT.API_SECTION='GSTR6A' AND BT.GET_TYPE IN ('CDN') GROUP BY BT.GSTIN,BT.RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE) ");
		if (!condition.equals("")) {
			buffer.append(condition);
		}
		buffer.append(
				" GROUP BY CUST_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE,LAST_UPDATED UNION ALL SELECT CUST_GSTIN , RETURN_PERIOD , DERIVED_RET_PERIOD, '' AS B2B_STATUS , '' AS B2B_LAST_UPDATED , '' AS B2BA_STATUS , '' AS B2BA_LAST_UPDATED , '' AS CDN_STATUS , '' AS CDN_LAST_UPDATED , CASE WHEN GET_TYPE='CDNA' THEN STATUS END CDNA_STATUS , CASE WHEN GET_TYPE='CDNA' THEN LAST_UPDATED END CDNA_LAST_UPDATED  FROM ( SELECT BT.GSTIN AS CUST_GSTIN , BT.RETURN_PERIOD AS RETURN_PERIOD ,DERIVED_RET_PERIOD, GET_TYPE ,STATUS,CASE WHEN STATUS IN('FAILED','SUCCESS','SUCCESS_WITH_NO_DATA') THEN MAX(BT.END_TIME) WHEN STATUS IN('INITIATED','INPROGRESS') THEN MAX(BT.CREATED_ON) END LAST_UPDATED FROM GETANX1_BATCH_TABLE BT WHERE BT.IS_DELETE=FALSE AND BT.API_SECTION='GSTR6A' AND BT.GET_TYPE IN ('CDNA') GROUP BY BT.GSTIN,BT.RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE) ");
		if (!condition.equals("")) {
			buffer.append(condition);
		}		
		buffer.append(
				" GROUP BY CUST_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,STATUS,GET_TYPE,LAST_UPDATED)");

		return buffer;
	}


}
