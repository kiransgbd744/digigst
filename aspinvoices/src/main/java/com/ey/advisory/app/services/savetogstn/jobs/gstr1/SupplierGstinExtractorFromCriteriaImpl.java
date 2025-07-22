package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("supplierGstinExtractorFromCriteriaImpl")
public class SupplierGstinExtractorFromCriteriaImpl
		implements SupplierGstinExtractorFromCriteria {

	@Autowired
	private GSTNDetailRepository gstinRepo;

	private String findDataStatusCriteria(Gstr1SaveToGstnReqDto dto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Building the sgstin and returnPeriod combination from "
							+ "given criteria");
		}
		String criteria = null;
		LocalDate firstDataRecv = dto.getDataRecvFrom();
		LocalDate secondDataRecv = dto.getDataRecvTo();
		LocalDate firstDocDate = dto.getDocDateFrom();
		LocalDate secondDocDate = dto.getDocDateTo();
		int firstDerRetPeriod = 0;
		int secondDerRetPeriod = 0;
		if (dto.getRetPeriodFrom() != null) {
			firstDerRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getRetPeriodFrom());
			secondDerRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getRetPeriodTo());
		}
		List<LocalDate> userDates = dto.getDates();
		List<String> multipleGstins = dto.getSgstins();
		String returnPeriod = dto.getReturnPeriod();
		Long entityId = dto.getEntityId();
		if (entityId != null) {
			List<String> entityGstins = gstinRepo.findgstinByEntityIdWithRegTypeForGstr1(entityId);
			if (entityGstins != null && !entityGstins.isEmpty()) {
				multipleGstins.addAll(entityGstins);
				dto.setSgstins(multipleGstins);
			}
		}
		
		
		if (userDates != null && !userDates.isEmpty()) { //Mandatory value

			StringBuilder build = new StringBuilder();
			if (!userDates.isEmpty() && firstDocDate != null
					&& secondDocDate != null) {
				build.append(" AND doc.docDate IN :userDates");
			} else if (!userDates.isEmpty()) {
				build.append(" AND doc.receivedDate IN :userDates");
			}
			if (firstDataRecv != null && secondDataRecv != null) {
				build.append(" AND doc.receivedDate BETWEEN :firstDataRecv "
						+ "AND :secondDataRecv");
			} else if (firstDocDate != null && secondDocDate != null) {
				build.append(" AND doc.docDate BETWEEN :firstDocDate "
						+ "AND :secondDocDate");
			} else if (firstDerRetPeriod != 0 && secondDerRetPeriod != 0) {
				build.append(" AND doc.derivedTaxperiod BETWEEN "
						+ ":firstDerRetPeriod AND :secondDerRetPeriod");
			}
			if (multipleGstins!=null && !multipleGstins.isEmpty()) {
				build.append(" AND doc.sgstin IN :gstins");
			}
			criteria = build.toString();

		}
		return criteria;
	}
	
	@Override
	public String buildSgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto) {
		
		String criteria = findDataStatusCriteria(dto);
		if (criteria != null && criteria.trim().length() > 0) {
			return "SELECT DISTINCT doc.sgstin, doc.taxperiod FROM "
					+ "OutwardTransDocument doc JOIN DocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.isProcessed = TRUE "
					+ "AND doc.isSent = FALSE  " + criteria
					+ " ORDER BY doc.sgstin, doc.taxperiod";
		}
		return null ;
	}
	
	@Override
	public String buildGStr7SgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto) {
		
		String criteria = findGstr7DataStatusCriteria(dto);
		if (criteria != null && criteria.trim().length() > 0) {
			return "SELECT DISTINCT doc.tdsGstin, doc.returnPeriod FROM "
					+ "Gstr7ProcessedTdsEntity doc where "
					/*+ " doc.isProcessed = TRUE AND"*/
					+ " doc.isSentGstn = FALSE  " + criteria
					+ " ORDER BY doc.tdsGstin, doc.returnPeriod";
		}
		return null ;
	}
	
	private String findGstr7DataStatusCriteria(Gstr1SaveToGstnReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Building the sgstin and returnPeriod combination from " + "given criteria");
		}
		String criteria = null;
		List<String> multipleGstins = dto.getSgstins();
		String returnPeriod = dto.getReturnPeriod();
		Long entityId = dto.getEntityId();
		if (entityId != null) {			
			List<String> entityGstins = gstinRepo.findgstinByEntityIdWithRegTypeForGstr7(entityId);
			if (entityGstins != null && !entityGstins.isEmpty()) {
				multipleGstins.addAll(entityGstins);
				dto.setSgstins(multipleGstins);
			}
		}
		StringBuilder build = new StringBuilder();
		if (multipleGstins!=null && !multipleGstins.isEmpty()) {
			build.append(" AND doc.tdsGstin IN :gstins");
		}
		criteria = build.toString();
		return criteria;
	}
	
	
	@Override
	public String buildItc04SgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto) {
		
		String criteria = findItc04DataStatusCriteria(dto);
		if (criteria != null && criteria.trim().length() > 0) {
			return "SELECT DISTINCT doc.supplierGstin, doc.qRetPeriod FROM "
					+ "Itc04HeaderEntity doc where "
					/*+ " doc.isProcessed = TRUE AND"*/
					+ " doc.isSentToGstn = FALSE  " + criteria
					+ " ORDER BY doc.supplierGstin, doc.qRetPeriod";
		}
		return null ;
	}
	
	private String findItc04DataStatusCriteria(Gstr1SaveToGstnReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Building the sgstin and returnPeriod combination from " + "given criteria");
		}
		String criteria = null;
		List<String> multipleGstins = dto.getSgstins();
		String returnPeriod = dto.getReturnPeriod();
		Long entityId = dto.getEntityId();
		if (entityId != null) {			
			List<String> entityGstins = gstinRepo.findgstinByEntityIdWithRegTypeForItc04(entityId);
			if (entityGstins != null && !entityGstins.isEmpty()) {
				multipleGstins.addAll(entityGstins);
				dto.setSgstins(multipleGstins);
			}
		}
		StringBuilder build = new StringBuilder();
		if (multipleGstins!=null && !multipleGstins.isEmpty()) {
			build.append(" AND doc.supplierGstin IN :gstins");
		}
		criteria = build.toString();
		return criteria;
	}
	
	
	//TODO
	@Override
	public String buildGstr1ASgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto) {
		
		String criteria = findDataStatusCriteria(dto);
		if (criteria != null && criteria.trim().length() > 0) {
			return "SELECT DISTINCT doc.sgstin, doc.taxperiod FROM "
					+ "Gstr1AOutwardTransDocument doc JOIN Gstr1ADocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.isProcessed = TRUE "
					+ "AND doc.isSent = FALSE  " + criteria
					+ " ORDER BY doc.sgstin, doc.taxperiod";
		}
		return null ;
	}

}
