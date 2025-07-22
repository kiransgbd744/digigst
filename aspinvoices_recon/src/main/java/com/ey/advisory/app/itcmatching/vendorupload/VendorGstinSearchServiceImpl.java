package com.ey.advisory.app.itcmatching.vendorupload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetGstinVendorMasterDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorGstinSearchServiceImpl")
public class VendorGstinSearchServiceImpl implements VendorGstinSearchService {

	@Autowired
	VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private GetGstinVendorMasterDetailRepository getGstinVendorMasterDetailRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Override
	public GstinSearchResponse getVendorGstinSearchResult(
			List<String> einvoiceApplicabilityList,
			List<String> gstinStatusList, Long entityId,
			String statusNotUpdatedInLastDays, int pageSize, int pageNum) {
		List<VendorMasterUploadEntity> vendormasterEntities = null;
		List<VendorMappingRespDto> vendorMappingRespDtoList = new ArrayList<>();
		List<VendorMappingRespDto> mappingRespDtoList = new ArrayList<>();
		int recordsToStart = pageNum;

		 if (einvoiceApplicabilityList != null && !einvoiceApplicabilityList.isEmpty()) {
		        for (int i = 0; i < einvoiceApplicabilityList.size(); i++) {
		            String value = einvoiceApplicabilityList.get(i);
		            if ("No".equalsIgnoreCase(value)) {
		                einvoiceApplicabilityList.set(i, "Not Applicable");
		            } else if ("Yes".equalsIgnoreCase(value)) {
		                einvoiceApplicabilityList.set(i, "Applicable");
		            }
		        }
		    }

		int noOfRowstoFetch = pageSize;

		int totalCount = 0;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");
		try {
			LocalDateTime thresholdDate = LocalDateTime.now()
					.minusDays(Long.valueOf(
							statusNotUpdatedInLastDays));
			if (statusNotUpdatedInLastDays.equals("0")) {
				thresholdDate = LocalDateTime.now();
			}
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");

			List<String> vendorGstinsList = new ArrayList<>();
			List<String> recipientPanList = entityInfoRepository
					.findPanByEntityId(entityId);

			vendormasterEntities = vendorMasterUploadEntityRepository
					.getByRecipientPANInAndIsDeleteIsFalse(
							recipientPanList);
			totalCount = vendormasterEntities.size();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"getting the vendormasterEntities from the vendor master table based on the recipent pan  : %d",
						vendormasterEntities.size());
				LOGGER.debug(msg);
			}
			for (VendorMasterUploadEntity entity : vendormasterEntities) {
				VendorMappingRespDto dto = new VendorMappingRespDto();
				vendorGstinsList.add(entity.getVendorGstin());
				dto.setVendorGstin(entity.getVendorGstin());
				dto.setVendorName(entity.getVendorName());
				mappingRespDtoList.add(dto);
			}

			if (einvoiceApplicabilityList == null && gstinStatusList == null) {
				Map<String, GetGstinVendorMasterDetailEntity> vendorMap = new HashMap<String, GetGstinVendorMasterDetailEntity>();
				if (vendorGstinsList != null && !vendorGstinsList.isEmpty()) {
				    List<GetGstinVendorMasterDetailEntity> gstinEntities = getGstinVendorMasterDetailRepository
				            .findByVendorGstinIn(vendorGstinsList, entityId);
				 // mapping all the values get table values to gstins
					for (GetGstinVendorMasterDetailEntity entity : gstinEntities) {
						vendorMap.put(entity.getVendorGstin(), entity);
					}

					List<String> gstinList = new ArrayList<>();
					for (GetGstinVendorMasterDetailEntity entity : gstinEntities) {
						gstinList.add(entity.getVendorGstin());
					}

					for (VendorMappingRespDto vendorMappingDto : mappingRespDtoList) {
					if (gstinList.contains(vendorMappingDto.getVendorGstin())) {
						// vendorGstnEntity is the entity from get call table
						GetGstinVendorMasterDetailEntity vendorGstnEntity = vendorMap
								.get(vendorMappingDto.getVendorGstin());
						String lastUpdated=null;
						if (vendorGstnEntity.getLastUpdated() != null) {
							 lastUpdated = vendorGstnEntity.getLastUpdated()
									.toString();
						}
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Last Updated String: {}",
									lastUpdated);
						}
						converter(vendorMap, vendorMappingDto);

						if (lastUpdated != null) {
							LocalDateTime lastUpdatedDateTime = LocalDateTime
									.parse(lastUpdated);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Last Updated DateTime: {}",
										lastUpdatedDateTime);
							}
							if (lastUpdatedDateTime.isBefore(thresholdDate)) {
								vendorMappingRespDtoList.add(vendorMappingDto);
							}
						} // we are displaying lastupdated date and time only
							// for success records
							// added logic as per requirment if lastUpdated is
							// null then also we have to show the records
						else if (lastUpdated == null) {
							vendorMappingRespDtoList.add(vendorMappingDto);
						}

					} else {
						vendorMappingDto
								.setStatusOfLastGetCall("Not Initiated");
						vendorMappingRespDtoList.add(vendorMappingDto);
					}
				}
			}

			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"entred in the else block if the filter applied : %d");
					LOGGER.debug(msg);
				}

				List<String> einvoiceApplicabil = new ArrayList<>();
				if (einvoiceApplicabilityList == null) {
					einvoiceApplicabil.add("Applicable");
					einvoiceApplicabil.add("Not Applicable");
					einvoiceApplicabilityList = einvoiceApplicabil;
				}
				if (gstinStatusList == null) {
					List<String> gstinStsList = new ArrayList<>();
					gstinStsList.add("Active");
					gstinStsList.add("Suspended");
					gstinStsList.add("Cancelled");
					gstinStsList.add("Inactive");
					gstinStatusList = gstinStsList;
				}

				List<GetGstinVendorMasterDetailEntity> getResults = getGstinVendorMasterDetailRepository
						.findByEinvApplicableInAndGstinStatusInAndLastUpdated(
								einvoiceApplicabilityList, gstinStatusList,
								thresholdDate, entityId);

				if (getResults != null) {
				    totalCount = getResults.size();

				    for (GetGstinVendorMasterDetailEntity getResult : getResults) {
				        VendorMappingRespDto vendorMappingRespDto = converter1(getResult);
				        vendorMappingRespDtoList.add(vendorMappingRespDto);
				    }
				}
				 else {
					    // Handle the case when getResults is null
					    LOGGER.warn("getResults is null. Skipping further processing.");
					}
			}

		} catch (Exception e) {
			LOGGER.error(
					"Exception in creating getting the data for the search result inside getVendorGstinSearchResult method ",
					e);
		}

		List<VendorMappingRespDto> activeGstinStatusSummeryResult = getActiveGstinStatusSummeryResult(
				vendorMappingRespDtoList);
		List<VendorMappingRespDto> suspendedGstinStatusSummeryResult = getSuspendedGstinStatusSummeryResult(
				vendorMappingRespDtoList);
		List<VendorMappingRespDto> cancelledGstinStatusSummeryResult = getCancelledGstinStatusSummeryResult(
				vendorMappingRespDtoList);
		List<VendorMappingRespDto> inActiveGstinStatusSummeryResult = getInActiveGstinStatusSummeryResult(
				vendorMappingRespDtoList);
		List<VendorMappingRespDto> eInvApplicableStatusSummeryResult = getEInvApplicableStatusSummeryResult(
				vendorMappingRespDtoList);
		List<VendorMappingRespDto> eInvNotApplicableStatusSummeryResult = getEInvNotApplicableStatusSummeryResult(
				vendorMappingRespDtoList);
		int size = vendorMappingRespDtoList.size();
		if (vendorMappingRespDtoList != null
				&& !vendorMappingRespDtoList.isEmpty()) {
			int startIndex = pageNum * pageSize;
			int endIndex = Math.min(startIndex + pageSize,
					vendorMappingRespDtoList.size());

			startIndex = Math.min(startIndex, vendorMappingRespDtoList.size());
			endIndex = Math.min(endIndex, vendorMappingRespDtoList.size());
			List<VendorMappingRespDto> paginatedList = vendorMappingRespDtoList
					.subList(startIndex, endIndex);

			return new GstinSearchResponse(
					paginatedList,
					activeGstinStatusSummeryResult,
					suspendedGstinStatusSummeryResult,
					cancelledGstinStatusSummeryResult,
					inActiveGstinStatusSummeryResult,
					eInvApplicableStatusSummeryResult,
					eInvNotApplicableStatusSummeryResult,
					size);
		} else {
			return new GstinSearchResponse(
					Collections.emptyList(),
					activeGstinStatusSummeryResult,
					suspendedGstinStatusSummeryResult,
					cancelledGstinStatusSummeryResult,
					inActiveGstinStatusSummeryResult,
					eInvApplicableStatusSummeryResult,
					eInvNotApplicableStatusSummeryResult,
					0);
		}

	}

	/**
	 * @param getResult
	 * @return
	 */
	public VendorMappingRespDto converter1(
			GetGstinVendorMasterDetailEntity getResult) {
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("dd-MM-yyyy");

		VendorMappingRespDto vendorMappingRespDto = new VendorMappingRespDto();
		vendorMappingRespDto.setBuildingName(getResult.getBuildingName() != null
				? getResult.getBuildingName() : "");
		vendorMappingRespDto
				.setCentreJurisdiction(getResult.getCentreJurisdiction() != null
						? getResult.getCentreJurisdiction() : "");
		vendorMappingRespDto.setConstitutionOfBusiness(
				getResult.getBusinessConstitution() != null
						? getResult.getBusinessConstitution() : "");
		vendorMappingRespDto
				.setDateOfCancellation(getResult.getCancellationDate() != null
						? getResult.getCancellationDate().format(formatter1)
						: "");
		vendorMappingRespDto
				.setDateOfRegistration(getResult.getRegistrationDate() != null
						? getResult.getRegistrationDate().format(formatter1)
						: "");
		vendorMappingRespDto.setDoorNumber(
				getResult.getDoorNum() != null ? getResult.getDoorNum() : "");
		vendorMappingRespDto
				.setEinvoiceApplicability(getResult.getEinvApplicable() != null
						? getResult.getEinvApplicable() : "");
		vendorMappingRespDto.setFloorNumber(
				getResult.getFloorNum() != null ? getResult.getFloorNum() : "");
		vendorMappingRespDto.setGstinStatus(getResult.getGstinStatus() != null
				? getResult.getGstinStatus() : "");

		vendorMappingRespDto.setLastUpdated(getResult.getLastUpdated() != null
				? EYDateUtil.toISTDateTimeFromUTC(getResult.getLastUpdated())
						.format(formatter)
				: null);
		vendorMappingRespDto
				.setLegalNameOfBusiness(getResult.getLegalName() != null
						? getResult.getLegalName() : "");
		vendorMappingRespDto.setLocation(
				getResult.getLocation() != null ? getResult.getLocation() : "");
		vendorMappingRespDto.setNatureOfBusinessActivity(
				getResult.getBusinessNatureActivity() != null
						? getResult.getBusinessNatureActivity() : "");
		vendorMappingRespDto.setPinCode(
				getResult.getPin() != null ? getResult.getPin() : "");
		vendorMappingRespDto
				.setStateJurisdiction(getResult.getStateJurisdiction() != null
						? getResult.getStateJurisdiction() : "");
		vendorMappingRespDto.setStateName(
				getResult.getState() != null ? getResult.getState() : "");
		vendorMappingRespDto
				.setStatusOfLastGetCall(getResult.getLastGetCallStatus() != null
						? getResult.getLastGetCallStatus() : "");
		vendorMappingRespDto.setStreet(
				getResult.getStreet() != null ? getResult.getStreet() : "");
		vendorMappingRespDto
				.setTaxpayerType(getResult.getTaxpayerType() != null
						? getResult.getTaxpayerType() : "");
		vendorMappingRespDto.setTradeName(getResult.getTradeName() != null
				? getResult.getTradeName() : "");
		vendorMappingRespDto.setVendorGstin(getResult.getVendorGstin() != null
				? getResult.getVendorGstin() : "");
		vendorMappingRespDto
				.setVendorName(getResult.getVendorNameAsUploaded() != null
						? getResult.getVendorNameAsUploaded() : "");
		vendorMappingRespDto
				.setErrorCode(getResult.getErrorCode() != null
						? getResult.getErrorCode() : null);
		vendorMappingRespDto.setErrorDiscription(
				getResult.getErrorDiscription() != null
						? getResult.getErrorDiscription() : null);

		vendorMappingRespDto.setUpdatedBy(getResult.getUpdatedBy() != null
				? getResult.getUpdatedBy() : null);

		return vendorMappingRespDto;
	}

	/**
	 * @param vendorMap
	 * @param vendorMappingDto
	 */
	public void converter(
			Map<String, GetGstinVendorMasterDetailEntity> vendorMap,
			VendorMappingRespDto vendorMappingDto) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			DateTimeFormatter formatter1 = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");
			GetGstinVendorMasterDetailEntity vendorGstnEntity = vendorMap
					.get(vendorMappingDto.getVendorGstin());

			vendorMappingDto
					.setBuildingName(vendorGstnEntity.getBuildingName() != null
							? vendorGstnEntity.getBuildingName() : null);
			vendorMappingDto.setCentreJurisdiction(
					vendorGstnEntity.getCentreJurisdiction() != null
							? vendorGstnEntity.getCentreJurisdiction() : null);
			vendorMappingDto.setConstitutionOfBusiness(
					vendorGstnEntity.getBusinessConstitution() != null
							? vendorGstnEntity.getBusinessConstitution()
							: null);
			vendorMappingDto.setDateOfCancellation(vendorGstnEntity != null
					&& vendorGstnEntity.getCancellationDate() != null
							? vendorGstnEntity.getCancellationDate()
									.format(formatter1)
							: null);
			vendorMappingDto.setDateOfRegistration(vendorGstnEntity != null
					&& vendorGstnEntity.getRegistrationDate() != null
							? vendorGstnEntity.getRegistrationDate()
									.format(formatter1)
							: null);
			vendorMappingDto.setDoorNumber(vendorGstnEntity.getDoorNum() != null
					? vendorGstnEntity.getDoorNum() : null);
			vendorMappingDto.setEinvoiceApplicability(
					vendorGstnEntity.getEinvApplicable() != null
							? vendorGstnEntity.getEinvApplicable() : null);
			vendorMappingDto
					.setGstinStatus(vendorGstnEntity.getGstinStatus() != null
							? vendorGstnEntity.getGstinStatus() : null);
			vendorMappingDto
					.setFloorNumber(vendorGstnEntity.getFloorNum() != null
							? vendorGstnEntity.getFloorNum() : null);
			vendorMappingDto
					.setLastUpdated(vendorGstnEntity.getLastUpdated() != null
							? EYDateUtil.toISTDateTimeFromUTC(
									vendorGstnEntity.getLastUpdated())
									.format(formatter)
							: null);

			vendorMappingDto.setLegalNameOfBusiness(
					vendorGstnEntity.getLegalName() != null
							? vendorGstnEntity.getLegalName() : null);
			vendorMappingDto.setLocation(vendorGstnEntity.getLocation() != null
					? vendorGstnEntity.getLocation() : null);
			vendorMappingDto
					.setNatureOfBusinessActivity(
							vendorGstnEntity.getBusinessNatureActivity() != null
									? vendorGstnEntity
											.getBusinessNatureActivity()
									: null);
			vendorMappingDto.setPinCode(vendorGstnEntity.getPin() != null
					? vendorGstnEntity.getPin() : null);
			vendorMappingDto.setStateJurisdiction(
					vendorGstnEntity.getStateJurisdiction() != null
							? vendorGstnEntity.getStateJurisdiction() : null);
			vendorMappingDto.setStateName(vendorGstnEntity.getState() != null
					? vendorGstnEntity.getState() : null);
			vendorMappingDto.setStatusOfLastGetCall(
					vendorGstnEntity.getLastGetCallStatus() != null
							? vendorGstnEntity.getLastGetCallStatus() : null);
			vendorMappingDto.setStreet(vendorGstnEntity.getStreet() != null
					? vendorGstnEntity.getStreet() : null);
			vendorMappingDto
					.setTaxpayerType(vendorGstnEntity.getTaxpayerType() != null
							? vendorGstnEntity.getTaxpayerType() : null);
			vendorMappingDto
					.setTradeName(vendorGstnEntity.getTradeName() != null
							? vendorGstnEntity.getTradeName() : null);
			vendorMappingDto.setVendorName(
					vendorGstnEntity.getVendorNameAsUploaded() != null
							? vendorGstnEntity.getVendorNameAsUploaded()
							: null);
			vendorMappingDto
					.setErrorCode(vendorGstnEntity.getErrorCode() != null
							? vendorGstnEntity.getErrorCode() : null);
			vendorMappingDto.setErrorDiscription(
					vendorGstnEntity.getErrorDiscription() != null
							? vendorGstnEntity.getErrorDiscription() : null);
			vendorMappingDto
					.setUpdatedBy(vendorGstnEntity.getUpdatedBy() != null
							? vendorGstnEntity.getUpdatedBy() : null);

		} catch (Exception e) {
			LOGGER.error(
					"Exception in creating convert method ",
					e);
		}

	}

	public List<VendorMappingRespDto> getActiveGstinStatusSummeryResult(
			List<VendorMappingRespDto> vendorMappingRespDtoList) {

		return vendorMappingRespDtoList
				.stream()
				.filter(dto -> "Active".equalsIgnoreCase(dto.getGstinStatus()))
				.collect(Collectors.toList());

	}

	public List<VendorMappingRespDto> getSuspendedGstinStatusSummeryResult(
			List<VendorMappingRespDto> vendorMappingRespDtoList) {

		return vendorMappingRespDtoList
				.stream()
				.filter(dto -> "Suspended"
						.equalsIgnoreCase(dto.getGstinStatus()))
				.collect(Collectors.toList());

	}

	public List<VendorMappingRespDto> getCancelledGstinStatusSummeryResult(
			List<VendorMappingRespDto> vendorMappingRespDtoList) {

		return vendorMappingRespDtoList
				.stream()
				.filter(dto -> "Cancelled"
						.equalsIgnoreCase(dto.getGstinStatus()))
				.collect(Collectors.toList());

	}

	public List<VendorMappingRespDto> getInActiveGstinStatusSummeryResult(
			List<VendorMappingRespDto> vendorMappingRespDtoList) {

		return vendorMappingRespDtoList
				.stream()
				.filter(dto -> "Inactive"
						.equalsIgnoreCase(dto.getGstinStatus()))
				.collect(Collectors.toList());
	}

	public List<VendorMappingRespDto> getEInvApplicableStatusSummeryResult(
			List<VendorMappingRespDto> vendorMappingRespDtoList) {

		return vendorMappingRespDtoList.stream()
				.filter(dto -> "Applicable"
						.equalsIgnoreCase(dto.getEinvoiceApplicability()))
				.collect(Collectors.toList());
	}

	public List<VendorMappingRespDto> getEInvNotApplicableStatusSummeryResult(
			List<VendorMappingRespDto> vendorMappingRespDtoList) {

		return vendorMappingRespDtoList.stream()
				.filter(dto -> "Not Applicable"
						.equalsIgnoreCase(dto.getEinvoiceApplicability()))
				.collect(Collectors.toList());
	}

}
