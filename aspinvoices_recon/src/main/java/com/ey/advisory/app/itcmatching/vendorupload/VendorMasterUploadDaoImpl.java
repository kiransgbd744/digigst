package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.javatuples.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorMasterUploadDaoImpl")
public class VendorMasterUploadDaoImpl implements VendorMasterUploadDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public Pair<List<VendorMasterUploadEntityDto>, Integer> getReconResult(
			List<String> reciepientPanList, List<String> vendorGSTNList,
			int pageSize, int pageNum) {

		List<VendorMasterUploadEntityDto> masterUploadEntities = new ArrayList<>();

		try {
			int recordsToStart = pageNum;

			int noOfRowstoFetch = pageSize;

			int totalCount = 0;

			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			List<VendorMasterUploadEntity> vendorMasterUploadEntity = null;
			if (CollectionUtils.isEmpty(vendorGSTNList)) {
				vendorMasterUploadEntity = vendorMasterUploadEntityRepository
						.findByRecipientPANInAndIsDeleteFalse(reciepientPanList,
								pageReq);
				totalCount = vendorMasterUploadEntityRepository
						.findCountByRecipientPANInAndIsDeleteFalse(
								reciepientPanList);

			} else if (CollectionUtils.isEmpty(reciepientPanList)) {

				List<List<String>> chunks = Lists.partition(vendorGSTNList,
						pageSize);
				vendorMasterUploadEntity = vendorMasterUploadEntityRepository
						.findByVendorGstinInAndIsDeleteFalse(
								chunks.get(pageNum));

				List<List<String>> countChunks = Lists.partition(vendorGSTNList,
						2000);
				for (List<String> countChunk : countChunks) {
					totalCount += vendorMasterUploadEntityRepository
							.findCountByVendorGstinInAndIsDeleteFalse(
									countChunk);
				}
			} else {
				List<List<String>> chunks = Lists.partition(vendorGSTNList,
						pageSize);

				vendorMasterUploadEntity = vendorMasterUploadEntityRepository
						.findByRecipientPANInAndVendorGstinInAndIsDeleteFalse(
								reciepientPanList, chunks.get(pageNum));

				List<List<String>> countChunks = Lists.partition(vendorGSTNList,
						2000);
				for (List<String> countChunk : countChunks) {

					totalCount += vendorMasterUploadEntityRepository
							.findCountByRecipientPANInAndVendorGstinInAndIsDeleteFalse(
									reciepientPanList, countChunk);
				}
			}

			vendorMasterUploadEntity.stream().forEach(eachObje -> {
				VendorMasterUploadEntityDto dummmyDto = new VendorMasterUploadEntityDto();
				BeanUtils.copyProperties(eachObje, dummmyDto);
				if (eachObje.isVendorCom()) {
					dummmyDto.setVendorCom("Y");
				} else {
					dummmyDto.setVendorCom("N");
				}
				if (eachObje.isExcludeVendor()) {
					dummmyDto.setExcludeVendor("Y");
				} else {
					dummmyDto.setExcludeVendor("N");
				}
				if (eachObje.isNonComplaintCom()) {
					dummmyDto.setNonComplaintCom("Y");
				} else {
					dummmyDto.setNonComplaintCom("N");
				}
				if (eachObje.isCreditEligibility()) {
					dummmyDto.setCreditEligibility("Y");
				} else {
					dummmyDto.setCreditEligibility("N");
				}
				masterUploadEntities.add(dummmyDto);
			});
			return new Pair<>(masterUploadEntities, totalCount);

		} catch (Exception ex) {
			String msg = String.format(
					"Error Occured while executing query  %s ", ex.toString());
			ex.printStackTrace();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

	@Override
	public Workbook downloadReconResult(List<String> reciepientPanList,
			List<String> vendorGSTNList) {

		List<VendorMasterUploadEntity> vendorMasterUploadEntity = null;
		if (CollectionUtils.isEmpty(vendorGSTNList)) {
			vendorMasterUploadEntity = vendorMasterUploadEntityRepository
					.findByRecipientPANInAndIsDeleteFalse(reciepientPanList);

		} else if (CollectionUtils.isEmpty(reciepientPanList)) {
			vendorMasterUploadEntity = vendorMasterUploadEntityRepository
					.findByVendorGstinInAndIsDeleteFalse(vendorGSTNList);
		} else {
			vendorMasterUploadEntity = vendorMasterUploadEntityRepository
					.findByRecipientPANInAndVendorGstinInAndIsDeleteFalse(
							reciepientPanList, vendorGSTNList);
		}
		if (CollectionUtils.isEmpty(vendorMasterUploadEntity)) {
			throw new AppException(String.format("No Data Found"));
		}
		if (!CollectionUtils.isEmpty(vendorMasterUploadEntity)
				&& vendorMasterUploadEntity != null
				&& !vendorMasterUploadEntity.equals("null")
				&& !vendorMasterUploadEntity.contains("null")) {

			List<VendorMasterReportDto> listOfVendorRecords = new ArrayList<>();

			vendorMasterUploadEntity.forEach(e -> {
				VendorMasterReportDto vendor = new VendorMasterReportDto();
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
				vendor.setHsn(e.getHsn());
				vendor.setVendorRiskCategory(e.getVendorRiskCategory());
				vendor.setVendorPaymentTerms(e.getVendorPaymentTerms());
				vendor.setVendorRemarks(e.getVendorRemarks());
				vendor.setApprovalStatus(e.getApprovalStatus());
				vendor.setExcludeVendorRemarks(e.getExcludeVendorRemarks());
				vendor.setIsVendorCom(e.isVendorCom() ? "Y" : "N");
				vendor.setIsExcludeVendor(e.isExcludeVendor() ? "Y" : "N");
				vendor.setIsNonComplaintCom(e.isNonComplaintCom() ? "Y" : "N");
				vendor.setIsCreditEligibility(e.isCreditEligibility() ? "Y" : "N");
				vendor.setIsDelete(null);
				listOfVendorRecords.add(vendor);
			});
			return writeToExcel(listOfVendorRecords);

		} else {
			throw new AppException("No records found");
		}
	}

	private Workbook writeToExcel(
			List<VendorMasterReportDto> listOfVendorRecords) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (listOfVendorRecords != null && !listOfVendorRecords.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("itc.vendor.master.download.data").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "VendorMasterDownloadReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "VendorMasterReportServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(listOfVendorRecords, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					listOfVendorRecords.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorMasterUploadDaoImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.VENDORMASTERDOWNLOAD,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " vendor master list in the directory : %s",
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

}
