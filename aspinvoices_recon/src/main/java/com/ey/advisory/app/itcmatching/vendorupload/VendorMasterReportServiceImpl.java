package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterErrorReportEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service("VendorMasterReportServiceImpl")
@Slf4j
public class VendorMasterReportServiceImpl
		implements VendorMasterReportService {

	private static final String VENDOR_MASTER_DATA_NOT_FOUND = "Vendor Master data not found";

	@Autowired
	private VendorMasterErrorReportEntityRepository vendorMasterErrorReportEntityRepository;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Override
	public Workbook getVendorMasterErrorReport(Long batchId,
			String refId,String typeOfFlag) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("");
			String msg = String.format(
					"reconSummary() " + "Parameter batchId %s:: ", batchId);
			LOGGER.debug(msg);
		}
		List<VendorMasterErrorReportEntity> vendorMasterErrorReportEntities = null;
		if(batchId !=null)
		{	vendorMasterErrorReportEntities = vendorMasterErrorReportEntityRepository
				.findByFileId(batchId);
		if (CollectionUtils.isEmpty(vendorMasterErrorReportEntities)) {
			throw new AppException(String.format(
					"Error or Informatipon record for the given batch id %s is not found",
					batchId));
		}
		}else{
			if(refId!=null)
			{
				vendorMasterErrorReportEntities = vendorMasterErrorReportEntityRepository
						.findByRefId(refId);
				vendorMasterErrorReportEntities.addAll(vendorMasterErrorReportEntityRepository
						.findByPayloadId(refId));
				if (CollectionUtils.isEmpty(vendorMasterErrorReportEntities)) {
					throw new AppException(String.format(
							"Error or Informatipon record for the given ref id %s is not found",
							refId));
			}
				
		}
		}
		
		List<VendorMasterErrorReportEntity> errorRecords = vendorMasterErrorReportEntities
				.stream()
				.filter(eachObject -> Objects.nonNull(eachObject.getError())
						&& !Objects.equals("null", eachObject.getError()))
				.collect(Collectors.toList());
		List<VendorMasterErrorReportEntity> informatonRecords = vendorMasterErrorReportEntities
				.stream()
				.filter(eachObject -> Objects
						.nonNull(eachObject.getInformation())
						&& !Objects.equals("null", eachObject.getInformation()))
				.collect(Collectors.toList());
		if (typeOfFlag.equalsIgnoreCase("informationrecords")) {
			if (!CollectionUtils.isEmpty(informatonRecords)
					&& informatonRecords != null
					&& !informatonRecords.equals("null")
					&& !informatonRecords.contains("null")) {
				List<VendorMasterErrorReportEntity> listInfoRecords = new ArrayList<>();

				informatonRecords.forEach(e -> {
					VendorMasterErrorReportEntity info = new VendorMasterErrorReportEntity();
					info.setInformation(e.getInformation());
					info.setRecipientPAN(e.getRecipientPAN());
					info.setVendorPAN(e.getVendorPAN());
					info.setVendorGstin(e.getVendorGstin());
					info.setVendorCode(e.getVendorCode());
					info.setVendorName(e.getVendorName());
					info.setVendPrimEmailId(e.getVendPrimEmailId());
					if (e.getVendorContactNumber() != null) {
						info.setVendorContactNumber(
								"'" + e.getVendorContactNumber());
					} else {
						info.setVendorContactNumber(e.getVendorContactNumber());
					}
					info.setVendorEmailId1(e.getVendorEmailId1());
					info.setVendorEmailId2(e.getVendorEmailId2());
					info.setVendorEmailId3(e.getVendorEmailId3());
					info.setVendorEmailId4(e.getVendorEmailId4());
					info.setRecipientEmailId1(e.getRecipientEmailId1());
					info.setRecipientEmailId2(e.getRecipientEmailId2());
					info.setRecipientEmailId3(e.getRecipientEmailId3());
					info.setRecipientEmailId4(e.getRecipientEmailId4());
					info.setRecipientEmailId5(e.getRecipientEmailId5());
					info.setVendorType(e.getVendorType());
					info.setHsn(e.getHsn());
					info.setVendorRiskCategory(e.getVendorRiskCategory());
					info.setVendorPaymentTerms(e.getVendorPaymentTerms());
					info.setVendorRemarks(e.getVendorRemarks());
					info.setApprovalStatus(e.getApprovalStatus());
					info.setExcludeVendorRemarks(e.getExcludeVendorRemarks());
					info.setIsVendorCom(e.getIsVendorCom());
					info.setIsExcludeVendor(e.getIsExcludeVendor());
					info.setIsNonComplaintCom(e.getIsNonComplaintCom());
					info.setIsCreditEligibility(e.getIsCreditEligibility());
					info.setIsDelete(e.getIsDelete());
					listInfoRecords.add(info);
				});

				return writeToExcel(listInfoRecords, "information");
			} else {
				throw new AppException(
						"Information Record for the given batch id not found ");
			}
		} else if (typeOfFlag.equalsIgnoreCase("errorrecords")) {
			if (!CollectionUtils.isEmpty(errorRecords) && errorRecords != null
					&& !errorRecords.equals("null")
					&& !errorRecords.contains("null")) {

				List<VendorMasterErrorReportEntity> listErrorRecords = new ArrayList<>();

				errorRecords.forEach(e -> {
					VendorMasterErrorReportEntity error = new VendorMasterErrorReportEntity();
					error.setError(e.getError());
					error.setRecipientPAN(e.getRecipientPAN());
					error.setVendorPAN(e.getVendorPAN());
					error.setVendorGstin(e.getVendorGstin());
					error.setVendorCode(e.getVendorCode());
					error.setVendorName(e.getVendorName());
					error.setVendPrimEmailId(e.getVendPrimEmailId());
					if (e.getVendorContactNumber() != null) {
						error.setVendorContactNumber(
								"'" + e.getVendorContactNumber());
					} else {
						error.setVendorContactNumber(
								e.getVendorContactNumber());
					}
					error.setVendorEmailId1(e.getVendorEmailId1());
					error.setVendorEmailId2(e.getVendorEmailId2());
					error.setVendorEmailId3(e.getVendorEmailId3());
					error.setVendorEmailId4(e.getVendorEmailId4());
					error.setRecipientEmailId1(e.getRecipientEmailId1());
					error.setRecipientEmailId2(e.getRecipientEmailId2());
					error.setRecipientEmailId3(e.getRecipientEmailId3());
					error.setRecipientEmailId4(e.getRecipientEmailId4());
					error.setRecipientEmailId5(e.getRecipientEmailId5());
					error.setVendorType(e.getVendorType());
					error.setHsn(e.getHsn());
					error.setVendorRiskCategory(e.getVendorRiskCategory());
					error.setVendorPaymentTerms(e.getVendorPaymentTerms());
					error.setVendorRemarks(e.getVendorRemarks());
					error.setApprovalStatus(e.getApprovalStatus());
					error.setExcludeVendorRemarks(e.getExcludeVendorRemarks());
					error.setIsVendorCom(e.getIsVendorCom());
					error.setIsExcludeVendor(e.getIsExcludeVendor());
					error.setIsNonComplaintCom(e.getIsNonComplaintCom());
					error.setIsCreditEligibility(e.getIsCreditEligibility());
					error.setIsDelete(e.getIsDelete());
					listErrorRecords.add(error);
				});
				return writeToExcel(listErrorRecords, "error");
			} else {
				throw new AppException(
						"error Record for the given batch id not found ");
			}
		} else {
			throw new AppException(
					"Please provide proper value for type record");
		}
	}

	private Workbook writeToExcel(
			List<VendorMasterErrorReportEntity> gstr2ReconSummary,
			String typeOfFlag) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (gstr2ReconSummary != null && !gstr2ReconSummary.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("itc.vendor.master." + typeOfFlag + ".data")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"VendorMaster" + typeOfFlag + "Report.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "VendorMasterReportServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(gstr2ReconSummary, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					gstr2ReconSummary.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorMasterReportServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.VENDORDATAUPLOAD,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	@Override
	public List<String> getListOfvendorGstinList(
			List<String> recipientGstinList, boolean flagForPan) {
		List<VendorMasterUploadEntity> vendorMasterUploadEntities;
		if (CollectionUtils.isEmpty(recipientGstinList)
				|| recipientGstinList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
			if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
				throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
			List<String> listofVendorGstIn = vendorMasterUploadEntities.stream()
					.map(VendorMasterUploadEntity::getVendorGstin).distinct()
					.collect(Collectors.toList());
			if (flagForPan)
				listofVendorGstIn
						.forEach(eachVGstinObj -> eachVGstinObj = eachVGstinObj
								.substring(2, 12));
			Collections.sort(listofVendorGstIn);
			return listofVendorGstIn;
		}
		List<String> recipientPanList = recipientGstinList.stream()
				.map(eachObj -> eachObj = eachObj.substring(2, 12))
				.collect(Collectors.toList());
		vendorMasterUploadEntities = vendorMasterUploadEntityRepository
				.findByRecipientPANIn(recipientPanList,
						Sort.by("vendorGstin").ascending());
		if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		List<String> listofVendorGstIn = vendorMasterUploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorGstin).distinct()
				.collect(Collectors.toList());
		/**
		 * this last enhancement match recipient state id with vendorGstin state
		 * id
		 */
		List<String> actualListOfVendorGstIn = new ArrayList<>();
		recipientGstinList.forEach(eacrecipientGstin -> listofVendorGstIn
				.forEach(eachVendorGstIn -> {
					if ((eacrecipientGstin.substring(0, 2)
							.equals(eachVendorGstIn.substring(0, 2)))
							&& !actualListOfVendorGstIn
									.contains(eachVendorGstIn)) {
						actualListOfVendorGstIn.add(eachVendorGstIn);
					}
				}));
		if (CollectionUtils.isEmpty(actualListOfVendorGstIn))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		if (flagForPan)
			actualListOfVendorGstIn
					.forEach(eachVGstinObj -> eachVGstinObj = eachVGstinObj
							.substring(2, 12));
		Collections.sort(actualListOfVendorGstIn);
		return actualListOfVendorGstIn;
	}

	@Override
	public List<VendorGstinDto> getListOfvendorGstinList(Long entityId) {
		List<String> vendorMasterUploadEntities = new ArrayList<>();
		List<String> recipientGstinList = gSTNDetailRepository
				.findgstinByEntityId(entityId);
		List<String> recipientPansList = new ArrayList<>();
		if (!recipientGstinList.isEmpty()) {
			recipientPansList = recipientGstinList.stream()
					.map(eachobj -> eachobj.substring(2, 12))
					.collect(Collectors.toList());
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.getDistinctActiveVendorGstin(recipientPansList);
		}
		List<VendorGstinDto> vendorGstinList = null;
		if (!vendorMasterUploadEntities.isEmpty()) {
			vendorGstinList = vendorMasterUploadEntities.stream()
					.map(e -> listVehicleList(e))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		return vendorGstinList;

	}

	private VendorGstinDto listVehicleList(String e) {
		VendorGstinDto dto = new VendorGstinDto();
		dto.setVendorGstin(e);
		return dto;
	}

	@Override
	public List<String> getListOfvendorName(List<String> recipientGstinList,
			List<String> vendorPanList, List<String> vendorGstInList) {
		List<VendorMasterUploadEntity> vendorMasterUploadEntities;
		if (CollectionUtils.isEmpty(recipientGstinList))
			throw new AppException("recipient gstin list cannot be empty");
		if (vendorGstInList.isEmpty())
			throw new AppException("vendor gstin list cannot be empty");
		if (vendorGstInList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
		} else {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findByVendorGstinInAndIsDeleteFalse(vendorGstInList);
		}
		if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		List<String> vendorNameList = vendorMasterUploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorName).sorted()
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(vendorNameList))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		return vendorNameList;
	}

	@Override
	public List<String> getListOfvendorCode(List<String> vendorNameList,
			List<String> vendorGstInList, Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<VendorMasterUploadEntity> vendorMasterUploadEntities;
		if (CollectionUtils.isEmpty(vendorGstInList))
			throw new AppException("vendor gstin list cannot be empty");
		if (CollectionUtils.isEmpty(vendorNameList))
			throw new AppException("vendor name list cannot be empty");
		if (vendorNameList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
		} else {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findByRecipientPANAndVendorNameInAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrue(
							entityInfoEntity.getPan(), vendorNameList,
							vendorGstInList);
		}
		List<String> vendorCodeList = vendorMasterUploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorCode).sorted()
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(vendorCodeList))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		return vendorCodeList;
	}

	@Override
	public List<GstinDto> getListOfRecipientPan(List<String> recipientPanList) {

		return recipientPanList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public List<GstinDto> getListOfRecipientGstin(Long entityId) {
		List<String> recipientGstinList = null;

		recipientGstinList = gSTNDetailRepository.findgstinByEntityId(entityId);

		return recipientGstinList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private GstinDto listRecipientPan(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}

	@Override
	public List<GstinDto> getListOfVendorPans(Long entityId) {
		List<String> vendorPanList = new ArrayList<>();
		List<String> recipientGstinList = gSTNDetailRepository
				.findgstinByEntityId(entityId);
		List<String> recipientPansList = new ArrayList<>();
		if (!recipientGstinList.isEmpty()) {
			recipientPansList = recipientGstinList.stream()
					.map(eachobj -> eachobj.substring(2, 12))
					.collect(Collectors.toList());

			vendorPanList = vendorMasterUploadEntityRepository
					.getDistinctActiveVendorPans(recipientPansList);
			Collections.sort(vendorPanList);
		}
		return vendorPanList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<VendorGstinDto> getListOfvendorPan(
			List<String> recipientGstinList, boolean flagForPan) {

		List<VendorMasterUploadEntity> vendorMasterUploadEntities;
		List<VendorGstinDto> vendorGstinDtoList = new ArrayList<>();
		List<String> listOfVendorPan = new ArrayList<>();
		if (CollectionUtils.isEmpty(recipientGstinList)
				|| recipientGstinList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
			if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
				throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
			List<String> listofVendorGstIn = vendorMasterUploadEntities.stream()
					.map(VendorMasterUploadEntity::getVendorGstin).distinct()
					.collect(Collectors.toList());
			if (flagForPan)
				listOfVendorPan = listofVendorGstIn.stream()
						.map(eachObj -> eachObj = eachObj.substring(2, 12))
						.collect(Collectors.toList());
			Collections.sort(listOfVendorPan);
			listOfVendorPan.forEach(eachObje -> {
				VendorGstinDto vendorGstinDto = new VendorGstinDto();
				vendorGstinDto.setVendorPan(eachObje);
				vendorGstinDtoList.add(vendorGstinDto);
			});

			return vendorGstinDtoList;
		}
		List<String> recipientPanList = recipientGstinList.stream()
				.map(eachObj -> eachObj = eachObj.substring(2, 12))
				.collect(Collectors.toList());
		vendorMasterUploadEntities = vendorMasterUploadEntityRepository
				.findByRecipientPANIn(recipientPanList,
						Sort.by("vendorGstin").ascending());
		if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		List<String> listofVendorGstIn = vendorMasterUploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorGstin).distinct()
				.collect(Collectors.toList());
		/**
		 * this last enhancement match recipient state id with vendorGstin state
		 * id
		 */
		List<String> actualListOfVendorGstIn = new ArrayList<>();
		List<String> actualListOfVendorPan = new ArrayList<>();
		recipientGstinList.forEach(eacrecipientGstin -> listofVendorGstIn
				.forEach(eachVendorGstIn -> {
					if ((eacrecipientGstin.substring(0, 2)
							.equals(eachVendorGstIn.substring(0, 2)))
							&& !actualListOfVendorGstIn
									.contains(eachVendorGstIn)) {
						actualListOfVendorGstIn.add(eachVendorGstIn);
					}
				}));
		if (CollectionUtils.isEmpty(actualListOfVendorGstIn))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		if (flagForPan)
			actualListOfVendorPan = recipientGstinList.stream()
					.map(eachObj -> eachObj = eachObj.substring(2, 12))
					.collect(Collectors.toList());
		Collections.sort(actualListOfVendorPan);
		actualListOfVendorPan.forEach(eachObje -> {
			VendorGstinDto vendorGstinDto = new VendorGstinDto();
			vendorGstinDto.setVendorPan(eachObje);
			vendorGstinDtoList.add(vendorGstinDto);
		});

		return vendorGstinDtoList;
	}

	@Override
	public List<VendorGstinDto> getvendorNameForGstin(
			List<String> vendorGstInList, Long entityId) {
		List<VendorGstinDto> vendorGstinDtoList = new ArrayList<>();
		List<VendorMasterUploadEntity> vendorMasterUploadEntities;

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		if (vendorGstInList.isEmpty())
			throw new AppException("vendor gstin list cannot be empty");
		if (vendorGstInList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
		} else {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrue(
							entityInfoEntity.getPan(), vendorGstInList);
		}

		if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);

		vendorMasterUploadEntities.forEach(eachObje -> {
			VendorGstinDto vendorGstinDto = new VendorGstinDto();
			vendorGstinDto.setVendorName(eachObje.getVendorName());
			vendorGstinDtoList.add(vendorGstinDto);
		});
		if (CollectionUtils.isEmpty(vendorGstinDtoList))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		return vendorGstinDtoList;
	}

	@Override
	public List<GstinDto> getListOfVendorGstin(List<String> vendorPans,
			Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<String> vendorGstinList = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorPans, 2000);

		for (List<String> chunk : chunks) {
			vendorGstinList.addAll(vendorMasterUploadEntityRepository
					.findAllVendorGstinByVendorPans(chunk,
							entityInfoEntity.getPan()));
		}
		Collections.sort(vendorGstinList);
		return vendorGstinList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<GstinDto> getListOfNonComplaintVendorPans(Long entityId) {
		List<String> vendorPanList = new ArrayList<>();
		List<String> recipientGstinList = gSTNDetailRepository
				.findgstinByEntityId(entityId);
		List<String> recipientPansList = new ArrayList<>();
		if (!recipientGstinList.isEmpty()) {
			recipientPansList = recipientGstinList.stream()
					.map(eachobj -> eachobj.substring(2, 12))
					.collect(Collectors.toList());

			vendorPanList = vendorMasterUploadEntityRepository
					.getDistinctActiveNonComplaintVendorPans(recipientPansList);
			vendorPanList.removeIf(Objects::isNull);

			Collections.sort(vendorPanList);
		}
		return vendorPanList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<GstinDto> getListOfNonComplaintVendorGstin(
			List<String> vendorPans, Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<String> vendorGstinList = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorPans, 2000);

		for (List<String> chunk : chunks) {
			vendorGstinList.addAll(vendorMasterUploadEntityRepository
					.findAllNonCompVendorGstinByVendorPans(chunk,
							entityInfoEntity.getPan()));
		}
		vendorGstinList.removeIf(Objects::isNull);
		Collections.sort(vendorGstinList);
		return vendorGstinList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
