package com.ey.advisory.app.inward.einvoice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.google.common.base.Strings;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Component("InwardEinvoiceJsonDaoImpl")
@Slf4j
public class InwardEinvoiceJsonDaoImpl implements InwardEinvoiceJsonDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	@Override
	public List<Object> getInwardEinvoiceJsonData(
			FileStatusDownloadReportEntity request) {
		List<String> supplyType = null;
		List<String> irnSts = null;
		List<Long> ids = null;
		
		try{
		if(request.getListingIds()!=null)
		{
		List<String> idsList = new ArrayList<>();
		
		idsList.addAll(Arrays.asList(GenUtil.convertClobtoString(request.getListingIds()).split(",")));
	
		List<Long> longList = idsList.stream()
                .map(obj -> Long.valueOf(obj))
                .collect(Collectors.toList());
		ids = longList;
		
		}
		List<String> gstinList = new ArrayList<>();
		gstinList.addAll(Arrays.asList(GenUtil.convertClobtoString(request.getGstins()).split(",")));
		
		if(!Strings.isNullOrEmpty(request.getIrnSts()))
		{
		irnSts = Arrays.asList(request.getIrnSts().split(","));
		}
		if(!Strings.isNullOrEmpty(request.getSupplyType()))
		{
			supplyType = Arrays.asList(request.getSupplyType().split(","));
		}
		

	
		String fromTaxPeriod = request.getTaxPeriodFrom();
		String toTaxPeriod = request.getTaxPeriodTo();
		String docNum = request.getDocNum();
		String type = request.getType();
		List<String> gstin = gstinList;
		String returnPeriod = request.getTaxPeriod();
		String vendorGstin = request.getVendorGstin();
		String irn = request.getIrnNum();
		String criteria = request.getCriteria();
		
		
		StringBuilder buildSummQuery = new StringBuilder();
		
		if (!Collections.isEmpty(gstin)) {
			buildSummQuery.append(" AND LIST.CUST_GSTIN IN (:gstin) ");
		}

		if (!Collections.isEmpty(supplyType) && supplyType !=null) {
			buildSummQuery.append(" AND LIST.SUPPLY_TYPE IN (:SupplyType) ");
		}
		
		if (!Collections.isEmpty(ids) && ids !=null) {
			buildSummQuery.append(" AND LIST.ID IN (:ids) ");
		}
				
		if ("SUMMARY".equalsIgnoreCase(type)) {
			if (returnPeriod != null && !returnPeriod.isEmpty()) {
				buildSummQuery.append(" AND LIST.MONTH_YEAR = :returnPeriod ");
			}
			
			if (!Collections.isEmpty(irnSts) && irnSts != null) {
				buildSummQuery.append(" AND LIST.IRN_STATUS IN (:irnSts) ");
			}
		}
		if ("INVMNGT".equalsIgnoreCase(type)) {
			buildSummQuery.append(" AND LIST.IS_DELETE = false ");
			
			if (!Strings.isNullOrEmpty(docNum)) {
				buildSummQuery.append(" AND LIST.DOC_NUM = :docNum ");
			}

			if (vendorGstin != null && !vendorGstin.isEmpty()) {
				buildSummQuery
						.append(" AND LIST.SUPPLIER_GSTIN = :vendorGstin ");
			}

			if (irn != null && !irn.isEmpty()) {
				buildSummQuery.append(" AND LIST.IRN = :irn ");
			}

			if (!Collections.isEmpty(irnSts) && irnSts != null) {
				buildSummQuery.append(" AND LIST.IRN_STATUS IN (:irnSts) ");
			}
			if (criteria != null) {
				if ("Date".equalsIgnoreCase(criteria)) {

					if (request.getDocDateFrom()!=null) {
						buildSummQuery.append(
								" AND TO_VARCHAR(LIST.ACK_DATE,'YYYY-MM-DD') BETWEEN :fromDate ");
					}
					if (request.getDocDateTo() != null) {
						buildSummQuery.append(" AND :toDate ");
					}
				} else {
					if (fromTaxPeriod != null && (!fromTaxPeriod.isEmpty())) {
						buildSummQuery.append(
								" AND LIST.DERIVED_MONTHYEAR BETWEEN :fromTaxPeriod ");
					}

					if (toTaxPeriod != null && (!toTaxPeriod.isEmpty())) {
						buildSummQuery.append(" AND :toTaxPeriod ");
					}

				}
			}
		}
		String querySummStr = createSummaryQuery(buildSummQuery.toString());
		Query q = entityManager.createNativeQuery(querySummStr);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading query inwardeinvoicescreen" + querySummStr);
		}
		if (gstin != null && !gstin.isEmpty()) {
			q.setParameter("gstin", gstin);
		}
		if (!Collections.isEmpty(supplyType) && supplyType !=null) {
			q.setParameter("SupplyType", supplyType);
		}
		if (!Collections.isEmpty(ids) && ids !=null) {
			q.setParameter("ids", ids);
		}

		if ("SUMMARY".equalsIgnoreCase(type)) {

			if (returnPeriod != null && !returnPeriod.isEmpty()) {
				q.setParameter("returnPeriod", returnPeriod);
			}
			
			if (!CollectionUtils.isEmpty(irnSts) && irnSts!=null) {
				q.setParameter("irnSts", irnSts);
			}
		}
		if ("INVMNGT".equalsIgnoreCase(type)) {

			if (docNum != null && !docNum.isEmpty()) {
				q.setParameter("docNum", docNum );
			}
			if (vendorGstin != null && !vendorGstin.isEmpty()) {
				q.setParameter("vendorGstin", vendorGstin);
			}
			if (irn != null && !irn.isEmpty()) {
				q.setParameter("irn", irn);
			}
			if (!CollectionUtils.isEmpty(irnSts) && irnSts!=null) {
				q.setParameter("irnSts", irnSts);
			}
			
			if (criteria != null) {
				if ("Month".equalsIgnoreCase(criteria)) {
					
					if (fromTaxPeriod != null && (!fromTaxPeriod.isEmpty())) {
					q.setParameter("fromTaxPeriod",Integer.valueOf(fromTaxPeriod.substring(2, 6)+fromTaxPeriod.substring(0, 2)));
					}
					if (toTaxPeriod != null && (!toTaxPeriod.isEmpty())) {
					q.setParameter("toTaxPeriod", Integer.valueOf(toTaxPeriod.substring(2, 6)+toTaxPeriod.substring(0, 2)));
					}
				} else {
					if (request.getDocDateFrom() != null) {
					q.setParameter("fromDate", request.getDocDateFrom());
					}
					if (request.getDocDateTo() != null ) {
					q.setParameter("toDate", request.getDocDateTo());
					}
				}
			}
		}
		
		List<Object> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading Resultset for einvoiceinward" + list);
		}
		
		return list;
		}catch(Exception ex)
		{
			LOGGER.error("exception in json download dao impl ",ex);
			
			throw new AppException(ex);
			
		}
		
	}

	private String createSummaryQuery(String buildSummQuery) {

		return "SELECT PAY.PAYLOAD FROM TBL_GETIRN_LIST LIST "
				+ "INNER JOIN TBL_GETIRN_PAYLOADS PAY ON LIST.IRN=PAY.IRN "
				+ "AND LIST.IRN_STATUS=PAY.IRN_STATUS AND LIST.BATCH_ID=PAY.BATCH_ID WHERE "
				+ " UPPER(LIST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' AND LIST.IS_DELETE = false "
				+ buildSummQuery;

	}

}