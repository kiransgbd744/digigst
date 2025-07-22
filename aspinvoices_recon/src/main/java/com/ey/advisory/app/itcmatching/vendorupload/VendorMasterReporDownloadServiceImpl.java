/**
 * kiran 
 */
package com.ey.advisory.app.itcmatching.vendorupload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetGstinVendorMasterDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.config.ConfigConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("VendorMasterReporDownloadServiceImpl")
public class VendorMasterReporDownloadServiceImpl
		implements VendorMasterReporDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;
	
	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	@Autowired
	@Qualifier("GetGstinVendorMasterDetailRepository")
	private GetGstinVendorMasterDetailRepository getGstinVendorMasterDetailRepository;

	@Autowired
	@Qualifier("ProcessedReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;
	
	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;



	private static int CSV_BUFFER_SIZE = 8192;

	public void getVendorMasterGstinReport(List<GstinDetailsdto> reqDto,
			Long id,Long entityId)
			throws IOException

	{
		
		File tempDir = null;
		tempDir = createTempDir();
		
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside CreditLedgerReportServiceImpl having reqDto as  {} for credit ledger report",
						reqDto.toString());
			}
		
			LocalDateTime utcDateTime = LocalDateTime.now();
			 ZoneId istZone = ZoneId.of("Asia/Kolkata");
		        LocalDateTime istDateTime = utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(istZone).toLocalDateTime();

		        // Format the IST LocalDateTime using the desired pattern
		        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		        String timeMilli = dtf.format(istDateTime);
			
			String filename1 = tempDir.getAbsolutePath() + File.separator
					+ "Vendor" + "_" + "EInvoiceStatus" + "_" + id
					+ "_" + timeMilli
					+ ".csv";
			
			try (Writer writer = new BufferedWriter(new FileWriter(filename1),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("vendor.gstin.details.screen.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("vendor.gstin.details.screen.report.column")
						.split(",");
				//getting data
				List<VendorMappingRespDto> gstinData = getGstinData(reqDto, id,entityId);
				
				if (gstinData != null && !gstinData.isEmpty()) {


					ColumnPositionMappingStrategy<VendorMappingRespDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
					mappingStrategy.setType(
							VendorMappingRespDto.class);
					mappingStrategy.setColumnMapping(columnMappings);
					StatefulBeanToCsvBuilder<VendorMappingRespDto> builder = new StatefulBeanToCsvBuilder<>(
							writer);
					StatefulBeanToCsv<VendorMappingRespDto> beanWriter = builder
							.withMappingStrategy(mappingStrategy)
							.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
							.withLineEnd(CSVWriter.DEFAULT_LINE_END)
							.withEscapechar(
									CSVWriter.DEFAULT_ESCAPE_CHARACTER)
							.build();
					beanWriter.write(gstinData);
				flushWriter(writer);
				String reportType = "Vendor E-Invoice Applicability Status";

				String zipFileName = "";
				String uploadedDocName=null;


				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combinAndZipReportFiles.zipfolder(tempDir,
						reportType, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				/* uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"vendorGstinDetailsReportDownload");*/

				
				Pair<String, String> uploadedFileName = DocumentUtility
						.uploadFile(zipFile, "vendorGstinDetailsReportDownload");
				uploadedDocName = uploadedFileName.getValue0();
				String docId = uploadedFileName.getValue1();
				
				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),docId);
				}
				 else {

					LOGGER.error("No Data found for report id : %d", id);
					fileStatusDownloadReportRepo.updateStatus(id,
							ReportStatusConstants.NO_DATA_FOUND, null,
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				}
			} catch (Exception ex) {
				LOGGER.error(
						"Exception occured in getGstinData method" + ex);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(
								LocalDateTime.now()));
				throw new AppException(ex);
			} finally {
				// deleting dir
				anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
			}
			
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}
	}

	public List<VendorMappingRespDto> getGstinData(List<GstinDetailsdto> reqDto,
			Long id,Long entityId) {
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
//getting all the gstins from reqDto
		  List<String> vendorGstinsList = reqDto.stream()
	                .map(GstinDetailsdto::getGstin)
	                .collect(Collectors.toList());
		  persitNotInitiatedRecords(vendorGstinsList, userName,entityId);
		  
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("GSTIN List: {}", vendorGstinsList);
	        }
	        
	        Map<String, GetGstinVendorMasterDetailEntity> vendorMap = new HashMap<String, GetGstinVendorMasterDetailEntity>();
			// gstinEntities are the values from get table
			List<GetGstinVendorMasterDetailEntity> gstinEntities = getGstinVendorMasterDetailRepository
					.findByVendorGstinIn(vendorGstinsList,entityId);

			// mapping all the values get table values to gstins
			for (GetGstinVendorMasterDetailEntity entity : gstinEntities) {
				vendorMap.put(entity.getVendorGstin(), entity);
			}
		
		List<VendorMappingRespDto> allDetailsDto = new ArrayList<>();
		try {
			

			for (GstinDetailsdto req : reqDto) {
				String gstin = req.getGstin();

				GetGstinVendorMasterDetailEntity vendorDetails = vendorMap
						.get(gstin);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstn %s", gstin);
				}

					if (vendorDetails != null) {
					VendorMappingRespDto vendorMappingRespDto = new VendorMappingRespDto();

					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy");
					DateTimeFormatter formatter1 = DateTimeFormatter
							.ofPattern("dd-MM-yyyy HH:mm:ss");

					vendorMappingRespDto
							.setBuildingName(
									vendorDetails.getBuildingName() != null
											? vendorDetails.getBuildingName()
											: null);
					vendorMappingRespDto.setCentreJurisdiction(
							vendorDetails.getCentreJurisdiction() != null
									? vendorDetails.getCentreJurisdiction()
									: null);
					vendorMappingRespDto.setConstitutionOfBusiness(
							vendorDetails.getBusinessConstitution()!=null?vendorDetails.getBusinessConstitution():null);
					vendorMappingRespDto.setDateOfCancellation(
							vendorDetails.getCancellationDate() != null
									? vendorDetails.getCancellationDate()
											.format(formatter)
									: null);
					vendorMappingRespDto.setDateOfRegistration(
							vendorDetails.getRegistrationDate() != null
									? vendorDetails.getRegistrationDate()
											.format(formatter)
									: null);
					vendorMappingRespDto
							.setDoorNumber(vendorDetails.getDoorNum()!=null?vendorDetails.getDoorNum():null);
					vendorMappingRespDto.setEinvoiceApplicability(
							vendorDetails.getEinvApplicable()!=null?vendorDetails.getEinvApplicable():null);
					vendorMappingRespDto
							.setGstinStatus(vendorDetails.getGstinStatus()!=null?vendorDetails.getGstinStatus():null);
					vendorMappingRespDto
							.setFloorNumber(vendorDetails.getFloorNum()!=null?vendorDetails.getFloorNum():null);
					
					
					vendorMappingRespDto.setLastUpdated(
						    vendorDetails.getLastUpdated() != null
						        ? "'".concat(EYDateUtil.toISTDateTimeFromUTC(vendorDetails.getLastUpdated())
						                        .format(formatter1))
						        : null);
					vendorMappingRespDto.setLegalNameOfBusiness(
							vendorDetails.getLegalName() != null
									? vendorDetails.getLegalName() : null);
					vendorMappingRespDto
							.setLocation(vendorDetails.getLocation()!=null?vendorDetails.getLocation():null);
					vendorMappingRespDto.setNatureOfBusinessActivity(
							vendorDetails.getBusinessNatureActivity() != null
									? vendorDetails.getBusinessNatureActivity()
									: null);
					vendorMappingRespDto.setPinCode(vendorDetails.getPin()!=null?vendorDetails.getPin():null);
					vendorMappingRespDto.setStateJurisdiction(
							vendorDetails.getStateJurisdiction() != null
									? vendorDetails.getStateJurisdiction()
									: null);
					vendorMappingRespDto
							.setStateName(vendorDetails.getState() != null
									? vendorDetails.getState() : null);
					vendorMappingRespDto.setStatusOfLastGetCall(
							vendorDetails.getLastGetCallStatus() != null
									? vendorDetails.getLastGetCallStatus()
									: null);
					vendorMappingRespDto.setStreet(vendorDetails.getStreet()!=null?vendorDetails.getStreet():null);
					vendorMappingRespDto.setTaxpayerType(
							vendorDetails.getTaxpayerType() != null
									? vendorDetails.getTaxpayerType() : null);
					vendorMappingRespDto
							.setTradeName(vendorDetails.getTradeName() != null
									? vendorDetails.getTradeName() : null);
					vendorMappingRespDto
							.setVendorGstin(vendorDetails.getVendorGstin()!=null?vendorDetails.getVendorGstin():null);
					vendorMappingRespDto.setVendorName(
							vendorDetails.getVendorNameAsUploaded() != null
									? vendorDetails.getVendorNameAsUploaded()
									: null);
					
					vendorMappingRespDto.setErrorCode(
					        vendorDetails.getErrorCode() != null ? vendorDetails.getErrorCode() : "");

					vendorMappingRespDto.setErrorDiscription(
					        vendorDetails.getErrorDiscription() != null ? vendorDetails.getErrorDiscription() : "");

					vendorMappingRespDto.setUpdatedBy(
					        vendorDetails.getUpdatedBy() != null ? vendorDetails.getUpdatedBy() : "");


					allDetailsDto.add(vendorMappingRespDto);

				}
			}
			

		} catch (Exception ex) {
			LOGGER.error(
					"Exception occured in getGstinData method" + ex);
			
		}

		return allDetailsDto;
	}




	public void persitNotInitiatedRecords(List<String> vendorGstinList,
			String userName,Long entityId) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDateTime = currentDateTime
				.format(formatter1);
		LocalDateTime parsedDateTime = LocalDateTime
				.parse(formattedDateTime, formatter1);
		List<Object[]> vendorGstinNameList = vendorMasterUploadEntityRepository
				.getVendorGstinAndVendorName(vendorGstinList);

		Map<String, String> vendorGstinNameMap = new HashMap<>();

		for (Object[] result : vendorGstinNameList) {
			String vendorGstin = (String) result[0];
			String vendorName = (String) result[1];

			vendorGstinNameMap.put(vendorGstin, vendorName);
		}

		@SuppressWarnings("unchecked")
		final List<String>[] vendorGstinListArray = new List[] {
				vendorGstinList };
		List<GetGstinVendorMasterDetailEntity> findByVendorGstinIn = getGstinVendorMasterDetailRepository
				.findByVendorGstinIn(vendorGstinList,entityId);

		Set<String> findByVendorGstinSet = findByVendorGstinIn.stream()
				.map(GetGstinVendorMasterDetailEntity::getVendorGstin)
				.collect(Collectors.toSet());

		// Find values in vendorGstinListArray that are not in
		// findByVendorGstinSet
		List<String> notInFindByVendorGstin = Arrays
				.stream(vendorGstinListArray)
				.flatMap(Collection::stream)
				.filter(gstin -> !findByVendorGstinSet.contains(gstin))
				.collect(Collectors.toList());

		List<GetGstinVendorMasterDetailEntity> newEntityList = notInFindByVendorGstin
				.stream()
				.map(gstin -> {
					GetGstinVendorMasterDetailEntity newEntity = new GetGstinVendorMasterDetailEntity();
					newEntity.setVendorGstin(gstin);
					String vendorName = vendorGstinNameMap.get(gstin);
					newEntity.setVendorNameAsUploaded(vendorName);
					newEntity.setLastGetCallStatus("Not Initiated");
					newEntity.setIsDelete(false);
					newEntity.setEntityId(entityId);
					newEntity.setUpdatedBy(userName);
					return newEntity;
				})
				.collect(Collectors.toList());

		getGstinVendorMasterDetailRepository.saveAll(newEntityList);
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory(
				ConfigConstants.lEDGER_REPORT_DOWNLOAD)
				.toFile();
	}

}
