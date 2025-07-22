package com.ey.advisory.app.itcmatching.vendorupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("VendorExclusionServiceImpl")
public class VendorExclusionServiceImpl implements VendorExclusionService {

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Override
	public Workbook downlaodVendorExclusionReport(
			List<String> reciepientPANList, List<String> vendorGSTINList) {
		List<VendorMasterUploadEntity> vendorMasterUploadEntity = null;
		if (CollectionUtils.isEmpty(vendorGSTINList)) {
			vendorMasterUploadEntity = vendorMasterUploadEntityRepository
					.findByRecipientPANInAndIsDeleteFalseAndIsExcludeVendorTrue(
							reciepientPANList);

		} else if (CollectionUtils.isEmpty(reciepientPANList)) {
			vendorMasterUploadEntity = vendorMasterUploadEntityRepository
					.findByVendorGstinInAndIsDeleteFalseAndIsExcludeVendorTrue(
							vendorGSTINList);
		} else {
			vendorMasterUploadEntity = vendorMasterUploadEntityRepository
					.findByRecipientPANInAndVendorGstinInAndIsDeleteFalseAndIsExcludeVendorTrue(
							reciepientPANList, vendorGSTINList);
		}
		if (CollectionUtils.isEmpty(vendorMasterUploadEntity)) {
			throw new AppException(String.format("No Data Found"));
		}
		if (!CollectionUtils.isEmpty(vendorMasterUploadEntity)
				&& vendorMasterUploadEntity != null
				&& !vendorMasterUploadEntity.equals("null")
				&& !vendorMasterUploadEntity.contains("null")) {

			List<VendorMasterUploadEntity> listOfVendorRecords = new ArrayList<>();

			vendorMasterUploadEntity.forEach(e -> {
				VendorMasterUploadEntity vendor = new VendorMasterUploadEntity();
				vendor.setRecipientPAN(e.getRecipientPAN());
				vendor.setVendorPAN(e.getVendorPAN());
				vendor.setVendorGstin(e.getVendorGstin());
				vendor.setVendorCode(e.getVendorCode());
				vendor.setVendorName(e.getVendorName());
				vendor.setVendPrimEmailId(e.getVendPrimEmailId());
				if (e.getVendorContactNumber() != null) {
					vendor.setVendorContactNumber(
							"'" + e.getVendorContactNumber());
				} else {
					vendor.setVendorContactNumber(e.getVendorContactNumber());
				}
				vendor.setVendorEmailId1(e.getVendorEmailId1());
				vendor.setVendorEmailId2(e.getVendorEmailId2());
				vendor.setVendorEmailId3(e.getVendorEmailId3());
				vendor.setVendorEmailId4(e.getVendorEmailId4());
				vendor.setRecipientEmailId1(e.getRecipientEmailId1());
				vendor.setRecipientEmailId2(e.getRecipientEmailId2());
				vendor.setRecipientEmailId3(e.getRecipientEmailId3());
				vendor.setRecipientEmailId4(e.getRecipientEmailId4());
				vendor.setRecipientEmailId5(e.getRecipientEmailId5());
				vendor.setVendorType(e.getVendorType());
				vendor.setVendorRiskCategory(e.getVendorRiskCategory());
				vendor.setVendorPaymentTerms(e.getVendorPaymentTerms());
				vendor.setVendorRemarks(e.getVendorRemarks());
				vendor.setExcludeVendorRemarks(e.getExcludeVendorRemarks());
				listOfVendorRecords.add(vendor);
			});
			return writeToExcel(listOfVendorRecords);

		} else {
			throw new AppException("No records found");
		}
	}

	private Workbook writeToExcel(
			List<VendorMasterUploadEntity> listOfVendorRecords) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (listOfVendorRecords != null && !listOfVendorRecords.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("itc.vendor.exclusion.download.data").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "VendorMasterDownloadReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "VendorExclusionServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(listOfVendorRecords, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					listOfVendorRecords.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorExclusionServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.VENDOREXCLUSIONdOWNLOAD,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " vendor exclusion list in the directory : %s",
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
	public List<VendorGstinDto> getExcludedVendorGstinList(Long entityId) {
		List<String> vendorExcludedEntities = new ArrayList<>();
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);
		vendorExcludedEntities = vendorMasterUploadEntityRepository
				.getDistinctActiveExcludedVendorGstin(recipientPanList);
		List<VendorGstinDto> vendorExcludedList = null;
		if (!vendorExcludedEntities.isEmpty()) {
			vendorExcludedList = vendorExcludedEntities.stream()
					.map(e -> vendorsList(e))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		return vendorExcludedList;

	}

	private VendorGstinDto vendorsList(String e) {
		VendorGstinDto dto = new VendorGstinDto();
		dto.setVendorGstin(e);
		return dto;
	}

	@Override
	public List<VendorExclusionDto> getVendorExclusionData(
			List<String> reciepientPanList, List<String> vendorGSTINList) {

		List<VendorExclusionDto> vendorExcludedEntityList = new ArrayList<>();
		try {
			List<VendorMasterUploadEntity> vendorExcludedEntity = null;
			if (CollectionUtils.isEmpty(vendorGSTINList)) {
				vendorExcludedEntity = vendorMasterUploadEntityRepository
						.findByRecipientPANInAndIsDeleteFalseAndIsExcludeVendorTrue(
								reciepientPanList);

			} else if (CollectionUtils.isEmpty(reciepientPanList)) {
				vendorExcludedEntity = vendorMasterUploadEntityRepository
						.findByVendorGstinInAndIsDeleteFalseAndIsExcludeVendorTrue(
								vendorGSTINList);

			} else {
				vendorExcludedEntity = vendorMasterUploadEntityRepository
						.findByRecipientPANInAndVendorGstinInAndIsDeleteFalseAndIsExcludeVendorTrue(
								reciepientPanList, vendorGSTINList);
			}

			vendorExcludedEntity.stream().forEach(eachObje -> {
				VendorExclusionDto dummmyDto = new VendorExclusionDto();
				dummmyDto.setVendorGstin(eachObje.getVendorGstin());
				dummmyDto.setVendorName(eachObje.getVendorCode());
				dummmyDto.setExcludeVendorRemarks(
						eachObje.getExcludeVendorRemarks());
				vendorExcludedEntityList.add(dummmyDto);
			});
			return vendorExcludedEntityList;

		} catch (Exception ex) {
			String msg = String.format(
					"Error Occured while executing query  %s ", ex.toString());
			ex.printStackTrace();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

	@Override
	public int softDeleteExcludedVendorGstins(List<String> reciepientPanList,
			List<String> vendorGSTINList) {

		try {
			int count = vendorMasterUploadEntityRepository
					.softDeleteExcludedVendors(reciepientPanList,
							vendorGSTINList, EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()));

			return count;

		} catch (Exception ex) {
			String msg = String.format(
					"Error Occured while executing update query  %s ",
					ex.toString());
			ex.printStackTrace();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}
}
