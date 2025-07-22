package com.ey.advisory.app.service.ims.supplier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.GstinTaxRecordRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.ims.EinvoiceSummaryChunkReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Component("ConsolidatedSupplierImsAsyncReportServiceImpl")
@Slf4j
public class ConsolidatedSupplierImsAsyncReportServiceImpl
		implements AsyncReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	EntityInfoRepository entityInfo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("SupplierImsChunkReportServiceImpl")
	private EinvoiceSummaryChunkReportService chunkService;
	
	@Autowired
	@Qualifier("GstinTaxRecordRepository")
	private GstinTaxRecordRepository gstinTaxRecordRepo;
	
	

	private static final String CONF_KEY = "supplier.ims.report.chunk.size";
	private static final String CONF_CATEG = "SUPPLIER_IMS_REPORTS";
	

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void generateReports(Long id)  {

		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Writer writer = null;
		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Consolidated supplier ims Report Download " + "with id:'%s'",
					id);
			LOGGER.debug(msg);
		}

		Optional<FileStatusDownloadReportEntity> opt = downloadRepository
				.findById(id);

		FileStatusDownloadReportEntity entity = opt.get();

		String reportType = entity.getReportType();
		String reqPayload = entity.getReqPayload();
		String type = entity.getType();



		Pair<Integer, Integer> maxReportSizes = chunkService.getChunkingSizes();

		boolean isReportAvailable = false;

		try {
			tempDir = createTempDir();
			
			 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");

			    ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));

		        ZonedDateTime istNow = utcNow.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

		        String timeMilli = istNow.format(formatter);
			
	

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "Supplier IMS_ConsolidatedReportActionWise_" + timeMilli +  "_1"
						+ ".csv";
			
			Integer chunkSize = getChunkSize();

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Config ID is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						id.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
			
			
			
			//String gstinTaxPeriod  = getFormattedGstinTaxPeriods(reqPayload);
			
		     StoredProcedureQuery storedProc = null;

			// chunk value
            if("imsGetCall".equalsIgnoreCase(type)){
            	
            	saveGstinTaxPeriods(reqPayload, id);
            	            		
            	 storedProc = entityManager
        					.createStoredProcedureQuery(
        							"USP_SUPP_IMS_GET_CALL_INS_CONS_RPT");
            	
         
    			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
    					Long.class, ParameterMode.IN);

    			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);
    			

    			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
    					Integer.class, ParameterMode.IN);

    			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);
            	
            }else{
            	
            	 storedProc = entityManager
    					.createStoredProcedureQuery(
    							"USP_SUPP_IMS_RECORDS_INS_CONS_RPT");

    			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
    					Long.class, ParameterMode.IN);

    			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

    			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
    					Integer.class, ParameterMode.IN);

    			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);
            }
			

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_SUPP_IMS_RECORDS_INS_CONS_RPT: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_SUPP_IMS_RECORDS_INS_CONS_RPT: id '%d', "
						+ "noOfChunk %d ", id, noOfChunk);
				LOGGER.debug(msg);
			}

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk <= 0) {
				msg = "No Data To Generate Report";
				LOGGER.error(msg);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

				return;
			}

			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);
				String invoiceHeadersTemplate = commonUtility
						.getProp("consolidated.supplier.ims.report.download.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("consolidated.supplier.ims.report.download.mapping")
						.split(",");

				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				StatefulBeanToCsv<ConsolidatedSupplierImsReportDto> beanWriter = getBeanWriter(
						columnMappings, writer);

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProcSummaryReport = entityManager
							.createStoredProcedureQuery(
									"USP_SUPP_IMS_RECORDS_DISP_CONS_RPT");

					storedProcSummaryReport.registerStoredProcedureParameter(
							"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);

					storedProcSummaryReport.setParameter("P_REPORT_DOWNLOAD_ID", id);

					storedProcSummaryReport.registerStoredProcedureParameter(
							"P_CHUNK_NUM", Integer.class, ParameterMode.IN);
					storedProcSummaryReport.setParameter("P_CHUNK_NUM", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ " chunkNo is %d ", id.toString(), j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					@SuppressWarnings("unchecked")
					List<Object[]> records = storedProcSummaryReport
							.getResultList();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("no of records after proc call {} ",
								records.size());
					}
					long dbLoadEndTime = System.currentTimeMillis();
					long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Total Time taken to load the Data"
										+ " from DB is '%d' millisecs,"
										+ " Report Name and Data count:"
										+ " '%s' - '%s'",
								dbLoadTimeDiff, reportType, records.size());
						LOGGER.debug(msg);
					}

					if (records != null && !records.isEmpty()) {

						isReportAvailable = true;
						List<ConsolidatedSupplierImsReportDto> einvoiceDataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, einvoiceDataList.size());
							LOGGER.debug(msg);
						}

						if (count >= maxLimitPerFile) {
							flushWriter(writer);
						
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Invoking Consolidated Supplier Ims "
													+ "chunkService to"
													+ " upload a zip file : "
													+ "configId {}, ReportName"
													+ " {}, ",
											id,
											"Supplier IMS_ConsolidatedReportActionWise");
								}
							
							// Zipping
							if (isReportAvailable) {
								
									chunkService.chunkZipFiles(tempDir, id, id,
											"Supplier IMS_ConsolidatedReportActionWise",
											maxReportSizes.getValue1(),
											fileIndex);
							}
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"After Invoking Summary "
													+ "chunkService to upload "
													+ "a zip file : configId "
													+ "{}, ReportName {} "
													+ "deleted file "
													+ "from TempDir, ",
											id,
											"Supplier IMS_ConsolidatedReportActionWise");
								}
							
							count = 0;
							fileIndex += 1;
								fullPath = tempDir.getAbsolutePath()
										+ File.separator
										+ getUniqueFileName(
												"Supplier IMS_ConsolidatedReportActionWise")
										+ "_" + fileIndex + ".csv";
					
							writer = new BufferedWriter(
									new FileWriter(fullPath), CSV_BUFFER_SIZE);
							writer.append(invoiceHeadersTemplate);
							beanWriter = getBeanWriter(columnMappings, writer);
						}
						count += einvoiceDataList.size();
						beanWriter.write(einvoiceDataList);

						long generationStTime = System.currentTimeMillis();
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);

							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Total Time taken to"
												+ " Generate the report is '%d' "
												+ "millisecs,"
												+ " Report Name and Data count:"
												+ " '%s' - '%s'",
										generationTimeDiff,
										"Supplier IMS_ConsolidatedReportActionWise",
										records.size());
								LOGGER.debug(msg);
							}
						
						if (noOfChunk == 1 || j == noOfChunk) {
							flushWriter(writer);
								chunkService.chunkZipFiles(tempDir, id, id,
										"Supplier IMS_ConsolidatedReportActionWise",
										maxReportSizes.getValue1(), fileIndex);
							
							break;
						}
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for "
						+ "Report Type :{}", reportType, ex);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				throw new AppException(ex);
			}

			if (noOfChunk == 0) {
				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			} else {

				downloadRepository.updateStatusAndCompltdOn(id,
						ReconStatusConstants.REPORT_GENERATED,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}

		} catch (Exception e) {
			String msz = String.format(
					"Exception occured while creating csv file for %d and reportType %s ",
					id, reportType);
			LOGGER.error(msz, e);

			downloadRepository.updateStatus(id,
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private ConsolidatedSupplierImsReportDto convert(Object[] arr) {

		ConsolidatedSupplierImsReportDto obj = new ConsolidatedSupplierImsReportDto();
		
		obj.setImsAction((arr[0] != null) ? arr[0].toString() : null);
		obj.setReturnPeriod((arr[1] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[1].toString())
				: null);
		obj.setSupplierGSTIN((arr[2] != null) ? arr[2].toString() : null);
		obj.setSupplierName((arr[3] != null) ? arr[3].toString() : null);
		obj.setTableType((arr[4] != null) ? arr[4].toString() : null);
		obj.setDocumentType((arr[5] != null) ? arr[5].toString() : null);
		obj.setSupplyType((arr[6] != null) ? arr[6].toString() : null);
		obj.setRecipientGSTIN((arr[7] != null) ? arr[7].toString() : null);
		obj.setRecipientName((arr[8] != null) ? arr[8].toString() : null);
		obj.setDocumentNumber((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setDocumentDate((arr[10] != null) ? arr[10].toString() : null);
		obj.setTaxableValue((arr[11] != null) ? arr[11].toString() : null);
		obj.setTotalTax((arr[12] != null) ? arr[12].toString() : null);
		obj.setIgst((arr[13] != null) ? arr[13].toString() : null);
		obj.setCgst((arr[14] != null) ? arr[14].toString() : null);
		obj.setSgst((arr[15] != null) ? arr[15].toString() : null);
		obj.setCess((arr[16] != null) ? arr[16].toString() : null);
		obj.setInvoiceValue((arr[17] != null) ? new BigDecimal
				(arr[17].toString()).toPlainString() : null);

		//obj.setInvoiceValue((arr[17] != null) ? arr[17].toString() : null);
		obj.setReverseCharge((arr[18] != null) ? arr[18].toString() : null);
		obj.setPos((arr[19] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[19].toString())
				: null);
		obj.setSource((arr[20] != null) ? arr[20].toString() : null);
		obj.setIrn((arr[21] != null) ? arr[21].toString() : null);
		obj.setIrnDate((arr[22] != null) ? arr[22].toString() : null);
		obj.setReturnType((arr[23] != null) ? arr[23].toString() : null);
		obj.setGstr1FilingStatus((arr[24] != null) ? arr[24].toString() : null);
		obj.setRecipientGstr3BFilingStatus((arr[25] != null) ? arr[25].toString() : null);
		obj.setEstimatedGSTR3BPeriod((arr[26] != null) ? arr[26].toString() : null);
		obj.setOriginalDocumentNumber((arr[27] != null) ? arr[27].toString() : null);
		obj.setOriginalDocumentDate((arr[28] != null) ? arr[28].toString() : null);
		obj.setChksum((arr[29] != null) ? arr[29].toString() : null);
		obj.setImsRemarks((arr[30] != null) ? arr[30].toString() : null);

	
		
		/*if (arr[21] != null) {

			Timestamp timeStamp = (Timestamp) arr[21];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			obj.setCancellationDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newdate));
		}
*/			
		
		return obj;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();

		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");

		String fileNameWithTimeStamp = fileName + "_" + timeMilli;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithTimeStamp);
		}

		return fileNameWithTimeStamp;
	}

	private Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}

	private StatefulBeanToCsv<ConsolidatedSupplierImsReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<ConsolidatedSupplierImsReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(ConsolidatedSupplierImsReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<ConsolidatedSupplierImsReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<ConsolidatedSupplierImsReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	/*private String CheckForNegativeValue(Object value, String docType) {

		if (value != null && !Strings.isNullOrEmpty(docType)) {
			if ("CR".equalsIgnoreCase(docType)) {
				if (value instanceof BigDecimal) {
					return (value != null ? ((((BigDecimal) value)
							.compareTo(BigDecimal.ZERO) > 0)
									? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof Integer) {
					return (value != null ? (((Integer) value > 0)
							? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof Long) {
					return (value != null ? (((Long) value > 0)
							? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof BigInteger) {
					return (value != null ? ((((BigInteger) value)
							.compareTo(BigInteger.ZERO) > 0)
									? "-" + value.toString() : value.toString())
							: null);
				} else {
					if (!value.toString().isEmpty()) {
						return "-" + value.toString().replaceFirst("-", "");
					} else {
						return null;
					}
				}
			} else
				return value.toString();
		}
		return value.toString();
	}*/
	/*public static void main(String[] args) {
		DateTimeFormatter dtf = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss");
		String timeMilli = dtf.format(LocalDateTime.now());

		String fileName =  "Supplier IMS_ConsolidatedReportActionWise_" + "1_"
				+ timeMilli
				+ ".csv";
		
		System.out.println(fileName);
	}*/
	/* public static String getFormattedGstinTaxPeriods(String jsonPayload) {
	        List<String> result = new ArrayList<>();

	        // Step 1: Parse the JSON manually
	        String[] parts = jsonPayload.split("\"gstin\":"); // Split by gstin

	        // Iterate over each "gstin" block (skipping the first one as it is before the first gstin)
	        for (int i = 1; i < parts.length; i++) {
	            // Extract gstin
	            String gstin = parts[i].split(",")[0].replace("\"", "").trim();

	            // Extract taxPeriod (look for "taxPeriod" and extract the values)
	            String taxPeriodBlock = parts[i].split("\"taxPeriod\":")[1].split("]")[0].trim();
	            String[] taxPeriods = taxPeriodBlock.substring(1).split(","); // Remove "[" and split by ","

	            // Format the output as gstin-taxPeriod1-taxPeriod2-...
	            StringBuilder formatted = new StringBuilder(gstin);
	            for (String taxPeriod : taxPeriods) {
	                formatted.append("-").append(taxPeriod.trim().replace("\"", ""));
	            }

	            result.add(formatted.toString());
	        }

	        //return result;
	        return String.join(",", result);

	        
	 }*/
	/* public void saveGstinTaxPeriods(String jsonPayload, Long reportId) {
		 
			User user = SecurityContext.getUser();

		 String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
		 
	        String[] parts = jsonPayload.split("\"gstin\":");

	        List<GstinTaxRecordEntity> recordsToSave = new ArrayList<>();

	        for (int i = 1; i < parts.length; i++) {
	            String gstin = parts[i].split(",")[0].replace("\"", "").trim();
	            String taxPeriodBlock = parts[i].split("\"taxPeriod\":")[1].split("]")[0].trim();
	            String[] taxPeriods = taxPeriodBlock.substring(1).split(",");

	            // Build comma-separated string
	            StringBuilder taxPeriodStr = new StringBuilder();
	            for (int j = 0; j < taxPeriods.length; j++) {
	                taxPeriodStr.append(taxPeriods[j].replace("\"", "").trim());
	                if (j != taxPeriods.length - 1) {
	                    taxPeriodStr.append(",");
	                }
	            }

	            // Create and store entity
	            GstinTaxRecordEntity record = new GstinTaxRecordEntity();
	            record.setGstin(gstin);
	            record.setTaxPeriods(taxPeriodStr.toString());
	            record.setReportId(reportId);
	            record.setCreatedOn(LocalDateTime.now());
	            record.setCreatedBy(userName);


	            recordsToSave.add(record);
	        }

	        // Save all entries in one go
	       gstinTaxRecordRepo.saveAll(recordsToSave);
	    }*/
	public void saveGstinTaxPeriods(String jsonPayload, Long reportId) {
		
		 User user = SecurityContext.getUser();

		 String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
		
        String[] parts = jsonPayload.split("\"gstin\":");

        List<GstinTaxRecordEntity> recordsToSave = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String gstin = parts[i].split(",")[0].replace("\"", "").trim();
            String taxPeriodBlock = parts[i].split("\"taxPeriod\":")[1].split("]")[0].trim();
            String[] taxPeriods = taxPeriodBlock.substring(1).split(",");

            for (String taxPeriod : taxPeriods) {
                String cleanedPeriod = taxPeriod.trim().replace("\"", "");

                GstinTaxRecordEntity record = new GstinTaxRecordEntity();
                record.setGstin(gstin);
                record.setTaxPeriods(cleanedPeriod);
                record.setReportId(reportId);
                record.setCreatedOn(LocalDateTime.now());
	            record.setCreatedBy(userName);

                recordsToSave.add(record);
            }
        }

        gstinTaxRecordRepo.saveAll(recordsToSave);
    }
	 public static void main(String[] args) {
		 
		 
	        // The example JSON payload (as a String)
	        String json = "{ \"req\": [ { \"gstin\": \"29AAAPH9357H000\", \"taxPeriod\": [ \"042024\", \"052024\", \"062024\"] }, { \"gstin\": \"29AAAPH9357H1A2\", \"taxPeriod\": [ \"042024\", \"052024\", \"062024\", \"072024\", \"082024\", \"092024\", \"102024\", \"112024\", \"122024\", \"012025\", \"022025\", \"032025\" ] } ], \"reportType\": [\"A\", \"R\", \"P\", \"N\"] }";

	        String[] parts = json.split("\"gstin\":");

	        //List<GstinTaxRecordEntity> recordsToSave = new ArrayList<>();

	        for (int i = 1; i < parts.length; i++) {
	            String gstin = parts[i].split(",")[0].replace("\"", "").trim();
	            String taxPeriodBlock = parts[i].split("\"taxPeriod\":")[1].split("]")[0].trim();
	            String[] taxPeriods = taxPeriodBlock.substring(1).split(",");

	            for (String taxPeriod : taxPeriods) {
	                String cleanedPeriod = taxPeriod.trim().replace("\"", "");

	               

	                
		            System.out.println(gstin + " " + cleanedPeriod + " " + "101");

	            }
	        }

	        
	    }
}
