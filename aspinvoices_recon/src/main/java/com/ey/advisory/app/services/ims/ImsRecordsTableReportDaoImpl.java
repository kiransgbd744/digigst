package com.ey.advisory.app.services.ims;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;


@Component("ImsRecordsTableReportDaoImpl")
@Slf4j
public class ImsRecordsTableReportDaoImpl
		implements ImsRecordsTableReportDao {
	

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getEntityLevelSummary(ImsEntitySummaryReqDto criteria) {


		List<String> gstinList = criteria.getGstins();
		

		List<String> tableType = criteria.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());
		
		StringBuilder build = new StringBuilder();

		
		if (gstinList != null && gstinList.size() > 0) {
				build.append("WHERE GSTIN IN (:gstinList)");
			}
		
		
		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			build.append(" AND TAX_DOC_TYPE IN :tableTypeUpperCase ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			
			if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			
			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				q.setParameter("tableTypeUpperCase", tableTypeUpperCase);
			}
			

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			// List<Object[]> retList = q.getResultList();
			return list.parallelStream().map(o -> convertEntityLevelSummary(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception e) {
			LOGGER.error("Ims Table Summary Report Data Error {}", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private ImsSummaryResponseReportDto convertEntityLevelSummary(
			Object[] arr) {
		ImsSummaryResponseReportDto obj = new ImsSummaryResponseReportDto();

		obj.setGstin(arr[0] != null ? arr[0].toString() : null);		
		obj.setGstnTotalCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setGstnNoActionCount((GenUtil.getBigInteger(arr[2])).intValue());
		obj.setGstnAcceptedCount((GenUtil.getBigInteger(arr[3])).intValue());
		obj.setGstnRejectedCount((GenUtil.getBigInteger(arr[4])).intValue());
		obj.setGstnPendingCount((GenUtil.getBigInteger(arr[5])).intValue());
		obj.setTotalCount((GenUtil.getBigInteger(arr[6])).intValue());
		obj.setNoActionCount((GenUtil.getBigInteger(arr[7])).intValue());
		obj.setAcceptedCount((GenUtil.getBigInteger(arr[8])).intValue());
		obj.setRejectedCount((GenUtil.getBigInteger(arr[9])).intValue());
		obj.setPendingCount((GenUtil.getBigInteger(arr[10])).intValue());


		return obj;

	}

  private static String createQueryString(String buildQuery) {

	String queryString = "WITH STATUS AS(SELECT SUPPLIER_GSTIN,(CASE WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%2%'THEN 'Inprogress' "
         + "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') LIKE '%1%' THEN 'Initiated' WHEN STRING_AGG(GSTN_SAVE_STATUS,',') ='5' THEN 'Failed' "
         + "WHEN STRING_AGG(GSTN_SAVE_STATUS,',') ='3' THEN 'Saved' ELSE 'Partially Saved' END) AS STATUS, "
         + "MAX(IFNULL(MODIFIED_ON,NOW())) AS CREATED_ON FROM (SELECT SUPPLIER_GSTIN,MAP(GSTN_SAVE_STATUS,'IN',1,'IP',2,'P',3,'PE',4,'REC',5,'ER',5) GSTN_SAVE_STATUS, "
         + "GSTN_SAVE_STATUS AS GSTN_SAVE_STATUS1,MODIFIED_ON FROM GSTR1_GSTN_SAVE_BATCH WHERE IS_DELETE = FALSE AND RETURN_TYPE ='IMS' "
         + "AND SUPPLIER_GSTIN IN (?)) GROUP BY SUPPLIER_GSTIN) ,GSTN AS( SELECT GSTIN,SUM(GSTN_TOTAL) AS GSTN_TOTAL, "
         + "SUM(GSTN_NO_ACTION) AS GSTN_NO_ACTION, SUM(GSTN_ACCEPTED) AS GSTN_ACCEPTED, SUM(GSTN_REJECTED) AS GSTN_REJECTED,SUM(GSTN_PENDING) AS GSTN_PENDING "
         + "FROM TBL_GETIMS_GSTN_COUNTS WHERE IS_DELETE = FALSE AND GSTIN IN (?) AND MAP(SECTION,('B2BA','B2B - Amendment', "
         + "'CN', 'CDN', 'DN','CDN','CNA','CDN - Amendment','DNA','CDN - Amendment','ISDCN','ISD','ISDA','ISD - Amendment','ISDCNA','ISD - Amendment' "
         + "'ECOMA','ECOM - Amendment','IMPGA','IMPG','IMPGSEZA','IMPGSEZ',SECTION,SECTION) in (?) GROUP BY GSTIN), DIGI AS( "
         + "SELECT RECIPIENT_GSTIN, COUNT(*) AS DIGI_TOTAL, SUM(CASE WHEN ACTION_RESPONSE ='N' THEN 1 ELSE 0 END) AS DIGI_NO_ACTION, "
         + "SUM(CASE WHEN ACTION_RESPONSE ='A' THEN 1 ELSE 0 END) AS DIGI_ACCEPTED,SUM(CASE WHEN ACTION_RESPONSE ='R' THEN 1 ELSE 0 END) AS DIGI_REJECTED, "
         + "SUM(CASE WHEN ACTION_RESPONSE ='P' THEN 1 ELSE 0 END) AS DIGI_PENDING FROM TBL_GETIMS_PROCESSED WHERE IS_DELETE = FALSE "
         + "AND IS_SAVED_TO_GSTIN = FALSE AND RECIPIENT_GSTIN IN (?) AND MAP( "
         + "TABLE_TYPE,'B2BA','B2B - Amendment','CN', 'CDN','DN','CDN','CNA','CDN - Amendment','DNA','CDN - Amendment','ECOMA','ECOM - Amendment',TABLE_TYPE) "
         + "IN (?) GROUP BY RECIPIENT_GSTIN) SELECT STATUS.SUPPLIER_GSTIN,STATUS.STATUS,STATUS.CREATED_ON, "
         + "GSTN.GSTN_TOTAL,GSTN.GSTN_NO_ACTION, GSTN.GSTN_ACCEPTED,GSTN.GSTN_REJECTED,GSTN.GSTN_PENDING, "
         + "DIGI.DIGI_TOTAL,DIGI.DIGI_NO_ACTION, DIGI.DIGI_ACCEPTED,DIGI.DIGI_REJECTED,DIGI.DIGI_PENDING "
         + "FROM STATUS INNER JOIN GSTN ON GSTN.GSTIN = STATUS.SUPPLIER_GSTIN INNER JOIN DIGI ON DIGI.RECIPIENT_GSTIN = STATUS.SUPPLIER_GSTIN; ";
         

		
	                  

		return queryString;
	}


}
