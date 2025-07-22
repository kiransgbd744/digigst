package com.ey.advisory.app.data.services.customisedreport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.services.common.CustomisedReportCommonUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.domain.master.CustomisedMasterFieldEntity;
import com.ey.advisory.core.async.domain.master.CustomisedReportMasterEntity;
import com.ey.advisory.core.async.repositories.master.CustomisedMasterFieldRepo;
import com.ey.advisory.core.async.repositories.master.CustomisedReportMasterRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Component("CustomisedReportServiceImpl")
@Slf4j
public class CustomisedReportServiceImpl implements CustomisedReportService {

	@Autowired
	CustomisedMasterFieldRepo custMasterRepo;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	CustomisedReportMasterRepo custRetMasterRepo;

	@Override
	@Transactional(value = "clientTransactionManager")
	public String saveSelectedFields(CustomisedReportReqDto reqDto ) {

		String createdBy = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		List<CustomisedReportListDto> selectedList = new ArrayList<>();
		LOGGER.debug("Inside SaveSelected Fields");
		/*Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		CustomisedReportReqDto reqDto = gson.fromJson(requestObject,
				CustomisedReportReqDto.class);
		String entityId = requestObject.get("entityId").getAsString();
		String reportType = requestObject.get("reportType").getAsString();
		*/
		
		String entityId = reqDto.getEntityId().toString();
		String reportType = reqDto.getReportType();
		
		try {
			if (reqDto.getBasicDetails() != null
					&& !reqDto.getBasicDetails().isEmpty()) {
				selectedList.addAll(reqDto.getBasicDetails());
			}

			if (reqDto.getCustDetails() != null
					&& !reqDto.getCustDetails().isEmpty()) {
				selectedList.addAll(reqDto.getCustDetails());
			}

			if (reqDto.getDigiGstSpecFields() != null
					&& !reqDto.getDigiGstSpecFields().isEmpty()) {
				selectedList.addAll(reqDto.getDigiGstSpecFields());
			}

			if (reqDto.getExportDetails() != null
					&& !reqDto.getExportDetails().isEmpty()) {
				selectedList.addAll(reqDto.getExportDetails());
			}

			if (reqDto.getEinvEwbGstResp() != null
					&& !reqDto.getEinvEwbGstResp().isEmpty()) {
				selectedList.addAll(reqDto.getEinvEwbGstResp());
			}

			if (reqDto.getGlDetails() != null
					&& !reqDto.getGlDetails().isEmpty()) {
				selectedList.addAll(reqDto.getGlDetails());
			}

			if (reqDto.getItemValDetails() != null
					&& !reqDto.getItemValDetails().isEmpty()) {
				selectedList.addAll(reqDto.getItemValDetails());
			}

			if (reqDto.getSupplierDetails() != null
					&& !reqDto.getSupplierDetails().isEmpty()) {
				selectedList.addAll(reqDto.getSupplierDetails());
			}

			if (reqDto.getCustDetails() != null
					&& !reqDto.getCustDetails().isEmpty()) {
				selectedList.addAll(reqDto.getCustDetails());
			}

			if (reqDto.getDispDetails() != null
					&& !reqDto.getDispDetails().isEmpty()) {
				selectedList.addAll(reqDto.getDispDetails());
			}

			if (reqDto.getShipDetails() != null
					&& !reqDto.getShipDetails().isEmpty()) {
				selectedList.addAll(reqDto.getShipDetails());
			}

			if (reqDto.getOrderDetails() != null
					&& !reqDto.getOrderDetails().isEmpty()) {
				selectedList.addAll(reqDto.getOrderDetails());
			}

			if (reqDto.getIncTaxDetails() != null
					&& !reqDto.getIncTaxDetails().isEmpty()) {
				selectedList.addAll(reqDto.getIncTaxDetails());
			}

			if (reqDto.getOrderRefDetails() != null
					&& !reqDto.getOrderRefDetails().isEmpty()) {
				selectedList.addAll(reqDto.getOrderRefDetails());
			}

			if (reqDto.getPartiesInv() != null
					&& !reqDto.getPartiesInv().isEmpty()) {
				selectedList.addAll(reqDto.getPartiesInv());
			}

			if (reqDto.getOtherDetails() != null
					&& !reqDto.getOtherDetails().isEmpty()) {
				selectedList.addAll(reqDto.getOtherDetails());
			}

			if (reqDto.getTransDetails() != null
					&& !reqDto.getTransDetails().isEmpty()) {
				selectedList.addAll(reqDto.getTransDetails());
			}

			if (reqDto.getOrgheiDetails() != null
					&& !reqDto.getOrgheiDetails().isEmpty()) {
				selectedList.addAll(reqDto.getOrgheiDetails());
			}

			if (reqDto.getUserDefDetails() != null
					&& !reqDto.getUserDefDetails().isEmpty()) {
				selectedList.addAll(reqDto.getUserDefDetails());
			}

			List<String> headerMappingList = selectedList.stream()
					.sorted(Comparator
							.comparing(CustomisedReportListDto::getSequence))
					.map(CustomisedReportListDto::getField)
					.collect(Collectors.toList());

			LOGGER.debug("HeaderList {} ", headerMappingList);

			List<String> javaMappingList = selectedList.stream()
					.sorted(Comparator
							.comparing(CustomisedReportListDto::getSequence))
					.map(o -> getJavaMapping(o)).collect(Collectors.toList());

			LOGGER.debug("JavaMappingList {} ", javaMappingList);

			Optional<CustomisedReportMasterEntity> getReportDtls = custRetMasterRepo
					.findByReportNameAndIsActiveTrue(reportType);
			if (!getReportDtls.isPresent()) {

				String msg = String.format("reportType is not configured %s ",
						reportType);
				LOGGER.error(msg);
				return msg;
			}

			List<String> dbMappingList = custMasterRepo.findDBColumn(
					headerMappingList, getReportDtls.get().getId());

			String headerString = String.join(",", headerMappingList);
			String javaMappString = String.join(",", javaMappingList);
			String dbMappString = String.join(",", dbMappingList);

			CustomisedFieldSelEntity fieldSelEntity = new CustomisedFieldSelEntity();
			fieldSelEntity.setEntityId(Long.valueOf(entityId));
			fieldSelEntity.setHeaderMapping(headerString);
			fieldSelEntity.setJavaMapp(javaMappString);
			fieldSelEntity.setIsActive(true);
			fieldSelEntity.setReportType(reportType);
			fieldSelEntity.setReportId(getReportDtls.get().getId());
			fieldSelEntity.setDbMapping(dbMappString);
			fieldSelEntity.setColumnList(dbMappString);
			fieldSelEntity.setCreatedBy(createdBy);
			fieldSelEntity.setCreatedOn(LocalDateTime.now());

			int updatedCount = custFieldSeleRepo.updateActiveExistingRecords(
					Long.valueOf(entityId), createdBy);
			
			custFieldSeleRepo.save(fieldSelEntity);
			/*if (updatedCount > 0) {
				custFieldSeleRepo.save(fieldSelEntity);
			}*/
			// making entry for stock transfer 
			
			CustomisedFieldSelEntity fieldSelEntity1 = new CustomisedFieldSelEntity();
			fieldSelEntity1.setEntityId(Long.valueOf(entityId));
			fieldSelEntity1.setHeaderMapping(headerString);
			fieldSelEntity1.setJavaMapp(javaMappString);
			fieldSelEntity1.setIsActive(true);
			fieldSelEntity1.setReportType("STOCK_TRANSFER");
			fieldSelEntity1.setReportId(5L);
			fieldSelEntity1.setDbMapping(dbMappString);
			fieldSelEntity1.setColumnList(dbMappString);
			fieldSelEntity1.setCreatedOn(LocalDateTime.now());
			fieldSelEntity1.setCreatedBy(createdBy);
			custFieldSeleRepo.save(fieldSelEntity1);
			
			
			return "Success";
		} catch (Exception e) {
			String msg = String.format(
					"Error while Saving the Data for Entity %s and ReportType %s",
					entityId, reportType);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	private String getJavaMapping(CustomisedReportListDto fieldDto) {
		return CustomisedReportCommonUtility
				.getJavaMappingFromField(fieldDto.getField());
	}

	@Override
	public JsonObject getSelectedFields(String entityId, String reportType) {

		JsonObject finalObj = new JsonObject();

		try {
			Optional<CustomisedReportMasterEntity> getReportDtls = custRetMasterRepo
					.findByReportNameAndIsActiveTrue(reportType);

			List<CustomisedMasterFieldEntity> masterValues = custMasterRepo
					.findByReportIdAndIsActiveTrue(
							getReportDtls.get().getId());

			Map<String, List<CustomisedMasterFieldEntity>> groupByTabs = masterValues
					.stream().collect(Collectors.groupingBy(
							CustomisedMasterFieldEntity::getTabName));

			Optional<CustomisedFieldSelEntity> fieldSelEntity = custFieldSeleRepo
					.findByReportTypeAndEntityIdAndIsActiveTrue(reportType,
							Long.valueOf(entityId));

			if (fieldSelEntity == null || !fieldSelEntity.isPresent()) {
				String msg = String.format(
						"No Data Available for Entity %s and ReportType %s"
						+ " fetching defualt values",
						entityId, reportType);
				LOGGER.debug(msg);
				
				fieldSelEntity = custFieldSeleRepo
						.findByReportTypeAndEntityIdAndIsActiveTrue(reportType,
								Long.valueOf(0L));

			}

			List<String> headerMapping = Arrays
					.asList(fieldSelEntity.get().getHeaderMapping().split(","));

			for (Entry<String, List<CustomisedMasterFieldEntity>> entry : groupByTabs
					.entrySet()) {
				JsonArray reqarr = new JsonArray();
				String tabName = entry.getKey();
				List<CustomisedMasterFieldEntity> tabMasterDetails = entry
						.getValue();
				for (CustomisedMasterFieldEntity custMaster : tabMasterDetails) {
					JsonObject reqBodyObj = new JsonObject();
					if (headerMapping.contains(custMaster.getFieldName())) {
						reqBodyObj.addProperty("visible", true);
					} else {
						reqBodyObj.addProperty("visible", false);
					}
					reqBodyObj.addProperty("field", custMaster.getFieldName());
					reqBodyObj.addProperty("sequence",
							custMaster.getSeqId().intValue());
					reqarr.add(reqBodyObj);
				}
				finalObj.add(tabName, reqarr);
			}

			return finalObj;

		} catch (Exception e) {
			String msg = String.format(
					"Error while Fetching the Data for Entity %s and ReportType %s",
					entityId, reportType);
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}
	}
}
