package com.ey.advisory.app.services.ims;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
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


@Component("ImsAmdOrgTrackReportServiceImpl")
@Slf4j
public class ImsAmdOrgTrackReportServiceImpl implements AsyncReportDownloadService {

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
    @Qualifier("ImsRecordsChunkReportServiceImpl")
    private EinvoiceSummaryChunkReportService chunkService;

    @Autowired
    @Qualifier("GSTNDetailRepository")
    GSTNDetailRepository regRepo;

    @Autowired
    @Qualifier("taxPayerDetailsServiceImpl")
    TaxPayerDetailsService taxPayerService;

    private static final String CONF_KEY = "ims.record.report.chunk.size";
    private static final String CONF_CATEG = "IMS_RECORD_REPORTS";
    private static final int CSV_BUFFER_SIZE = 8192;

    @Override
    public void generateReports(Long id) {
        String fullPath = null;
        File tempDir = null;
        String msg = null;
        Integer noOfChunk = 0;
        Writer writer = null;
        if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Ims Track Report Download " + "with id:'%s'",
					id);
			LOGGER.debug(msg);
		}

        try {
            Optional<FileStatusDownloadReportEntity> opt = downloadRepository.findById(id);
            if (!opt.isPresent()) {
                return;
            }
            FileStatusDownloadReportEntity entity = opt.get();
            Pair<Integer, Integer> maxReportSizes = chunkService.getChunkingSizes();

            tempDir = createTempDir();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");
            ZonedDateTime istNow = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            String timeMilli = istNow.format(formatter);
            String fileName = "IMS AMD-ORG Track Report";
            fullPath = tempDir.getAbsolutePath() + File.separator + fileName + "_" + id + "_" + timeMilli + "_1.csv";

            Integer chunkSize = getChunkSize();
            
            if(chunkSize <= 0){
            	chunkSize = 5000;
            }

            StoredProcedureQuery storedProc = entityManager
                    .createStoredProcedureQuery("USP_IMS_INS_CHUNK_AMD_TRACK_RPT");
            storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);
            storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);
            storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL", Integer.class, ParameterMode.IN);
            storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

            noOfChunk = (Integer) storedProc.getSingleResult();
            noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

            if (noOfChunk <= 0) {
                downloadRepository.updateStatus(id, ReconStatusConstants.NO_DATA_FOUND, null,
                        EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
                return;
            }
            String response = executeAllProcedures(id);
            
            if("Success".equalsIgnoreCase(response)){
            	writer = new BufferedWriter(new FileWriter(fullPath), CSV_BUFFER_SIZE);
                String invoiceHeadersTemplate = commonUtility.getProp("ims.amdorg.track.headers");
                writer.append(invoiceHeadersTemplate);
                String[] columnMappings = commonUtility.getProp("ims.amdorg.track.column.mapping").split(",");
                StatefulBeanToCsv<ImsAmdOrgTrackReportDto> beanWriter = getBeanWriter(columnMappings, writer);

                boolean isReportAvailable = false;
                int fileIndex = 1;
                int count = 0;
                int maxLimitPerFile = maxReportSizes.getValue0();

                for (int j = 1; j <= noOfChunk; j++) {
                    StoredProcedureQuery storedProcSummaryReport = entityManager
                            .createStoredProcedureQuery("USP_IMS_DISP_CHUNK_AMD_TRACK_RPT");
                    storedProcSummaryReport.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID", Long.class,
                            ParameterMode.IN);
                    storedProcSummaryReport.setParameter("P_REPORT_DOWNLOAD_ID", id);
                    storedProcSummaryReport.registerStoredProcedureParameter("P_CHUNK_VAL", Integer.class, ParameterMode.IN);
                    storedProcSummaryReport.setParameter("P_CHUNK_VAL", j);

                    @SuppressWarnings("unchecked")
                    List<Object[]> records = storedProcSummaryReport.getResultList();

                    if (records != null && !records.isEmpty()) {
                        isReportAvailable = true;
                        List<ImsAmdOrgTrackReportDto> dataList = records.stream().map(this::convert)
                                .collect(Collectors.toCollection(ArrayList::new));

                        if (count >= maxLimitPerFile) {
                            flushWriter(writer);
                            chunkService.chunkZipFiles(tempDir, id, id, fileName, maxReportSizes.getValue1(), fileIndex);
                            count = 0;
                            fileIndex += 1;
                            fullPath = tempDir.getAbsolutePath() + File.separator + getUniqueFileName(fileName.replace(" ", "_")) + "_"
                                    + fileIndex + ".csv";
                            writer = new BufferedWriter(new FileWriter(fullPath), CSV_BUFFER_SIZE);
                            writer.append(invoiceHeadersTemplate);
                            beanWriter = getBeanWriter(columnMappings, writer);
                        }
                        count += dataList.size();
                        beanWriter.write(dataList);

                        if (noOfChunk == 1 || j == noOfChunk) {
                            flushWriter(writer);
                            chunkService.chunkZipFiles(tempDir, id, id, fileName, maxReportSizes.getValue1(), fileIndex);
                        }
                    }
                }

                if (noOfChunk == 0 || !isReportAvailable) {
                    downloadRepository.updateStatus(id, ReconStatusConstants.NO_DATA_FOUND, null,
                            EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
                } else {
                    downloadRepository.updateStatusAndCompltdOn(id, ReconStatusConstants.REPORT_GENERATED,
                            EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
                }
            } else {
            	LOGGER.error("Failed to generate IMS amendment track report - becuase we didn't get success response from procs ");
                downloadRepository.updateStatus(id, ReconStatusConstants.REPORT_GENERATION_FAILED, null,
                        EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
            }
            
        } catch (Exception ex) {
        	LOGGER.error("Failed to generate IMS amendment track report", ex);
            downloadRepository.updateStatus(id, ReconStatusConstants.REPORT_GENERATION_FAILED, null,
                    EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
            throw new AppException(ex);
        } finally {
            deleteTemporaryDirectory(tempDir);
        }
    }
    
    public String executeAllProcedures(Long id) {
		
		//Executing Procs
		String response = executingProcs(id);
		
		return response;
	}
    
    private String executingProcs(Long id) {
		String queryStr = "SELECT PROCEDURE_NAME FROM TBL_IMS_AMD_TRACK_RPT_UPD_PROCEDURE "
				+ "WHERE IS_ACTIVE = true ORDER BY SEQUENCE;";
		
		String response = null;

		try {
			@SuppressWarnings("unchecked")
			List<Object> procList = entityManager.createNativeQuery(queryStr)
					.getResultList();

			for (Object procObj : procList) {
				String procName = (String) procObj;
				LOGGER.debug("Calling procedure {} for batchId {}", procName,
						id);

				StoredProcedureQuery proc = entityManager
						.createStoredProcedureQuery(procName);

				proc.registerStoredProcedureParameter("BATCH_ID", Long.class,
						ParameterMode.IN);

				proc.setParameter("BATCH_ID", id);

				response = (String) proc.getSingleResult();
				LOGGER.info("Executed procedure: {}", procName);
			}
		} catch (Exception e) {
			LOGGER.error("Error executing procedures for batchId {}", id,
					e);
			throw new AppException("Failed during IMS bulk response execution",
					e);
		}
		return response;
	}

    private void flushWriter(Writer writer) {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new AppException("Exception while closing the file writer", e);
            }
        }
    }

    private void deleteTemporaryDirectory(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            try {
                FileUtils.deleteDirectory(tempFile);
            } catch (Exception ex) {
            	LOGGER.error("Failed to remove the temp directory", ex);
            }
        }
    }

    private ImsAmdOrgTrackReportDto convert(Object[] arr) {
        ImsAmdOrgTrackReportDto obj = new ImsAmdOrgTrackReportDto();

        //  0: Difference
        obj.setDifference(getVal(arr,  0));
        //  1: Table Type – AMD
        obj.setTableTypeAmd(getVal(arr,  1));
        //  2: Table Type – ORG
        obj.setTableTypeOrg(getVal(arr,  2));
        //  3: Recipient GSTIN – AMD
        obj.setRecipientGstinAmd(getVal(arr,  3));
        //  4: Recipient GSTIN – ORG
        obj.setRecipientGstinOrg(getVal(arr,  4));
        //  5: Supplier GSTIN – AMD
        obj.setSupplierGstinAmd(getVal(arr,  5));
        //  6: Supplier Legal Name – AMD
        obj.setSupplierLegalNameAmd(getVal(arr,  6));
        //  7: Supplier Trade Name – AMD
        obj.setSupplierTradeNameAmd(getVal(arr,  7));
        //  8: Supplier GSTIN – ORG
        obj.setSupplierGstinOrg(getVal(arr,  8));
        //  9: Supplier Legal Name – ORG
        obj.setSupplierLegalNameOrg(getVal(arr,  9));
        // 10: Supplier Trade Name – ORG
        obj.setSupplierTradeNameOrg(getVal(arr, 10));
        // 11: Document Type – AMD
        obj.setDocumentTypeAmd(getVal(arr, 11));
        // 12: Document Type – ORG
        obj.setDocumentTypeOrg(getVal(arr, 12));
        // 13: Document Number – AMD
        obj.setDocumentNumberAmd(prefixQuote(arr, 13));
        // 14: Document Number – ORG
        obj.setDocumentNumberOrg(prefixQuote(arr, 14));
        // 15: Document Date – AMD
        obj.setDocumentDateAmd(getVal(arr, 15));
        // 16: Document Date – ORG
        obj.setDocumentDateOrg(getVal(arr, 16));
        // 17: Taxable Value – AMD
        obj.setTaxableValueAmd(getVal(arr, 17));
        // 18: Taxable Value – ORG
        obj.setTaxableValueOrg(getVal(arr, 18));
        // 19: IGST – AMD
        obj.setIgstAmd(getVal(arr, 19));
        // 20: IGST – ORG
        obj.setIgstOrg(getVal(arr, 20));
        // 21: CGST – AMD
        obj.setCgstAmd(getVal(arr, 21));
        // 22: CGST – ORG
        obj.setCgstOrg(getVal(arr, 22));
        // 23: SGST – AMD
        obj.setSgstAmd(getVal(arr, 23));
        // 24: SGST – ORG
        obj.setSgstOrg(getVal(arr, 24));
        // 25: Cess – AMD
        obj.setCessAmd(getVal(arr, 25));
        // 26: Cess – ORG
        obj.setCessOrg(getVal(arr, 26));
        // 27: Total Tax – AMD
        obj.setTotalTaxAmd(getVal(arr, 27));
        // 28: Total Tax – ORG
        obj.setTotalTaxOrg(getVal(arr, 28));
        // 29: Invoice Value – AMD
        obj.setInvoiceValueAmd(getVal(arr, 29));
        // 30: Invoice Value – ORG
        obj.setInvoiceValueOrg(getVal(arr, 30));
        // 31: POS – AMD
        obj.setPosAmd(getVal(arr, 31));
        // 32: POS – ORG
        obj.setPosOrg(getVal(arr, 32));
        // 33: Form Type – AMD
        obj.setFormTypeAmd(getVal(arr, 33));
        // 34: Form Type – ORG
        obj.setFormTypeOrg(getVal(arr, 34));
        // 35: GSTR1-Filing Status – AMD
        obj.setGstr1FilingStatusAmd(getVal(arr, 35));
        // 36: GSTR1-Filing Status – ORG
        obj.setGstr1FilingStatusOrg(getVal(arr, 36));
        // 37: GSTR1-Filing Period – AMD
        obj.setGstr1FilingPeriodAmd(getVal(arr, 37));
        // 38: GSTR1-Filing Period – ORG
        obj.setGstr1FilingPeriodOrg(getVal(arr, 38));
        // 39: Original Document Number – AMD
        obj.setOriginalDocumentNumberAmd(prefixQuote(arr, 39));
        // 40: Original Document Date – AMD
        obj.setOriginalDocumentDateAmd(getVal(arr, 40));
        // 41: IMS Response Remarks – AMD
        obj.setImsResponseRemarksAmd(getVal(arr, 41));
        // 42: IMS Response Remarks – ORG
        obj.setImsResponseRemarksOrg(getVal(arr, 42));
        // 43: Action (GSTN) – AMD
        obj.setActionGstnAmd(getVal(arr, 43));
        // 44: Action (GSTN) – ORG
        obj.setActionGstnOrg(getVal(arr, 44));
        // 45: Action (DigiGST) – AMD
        obj.setActionDigiGstAmd(getVal(arr, 45));
        // 46: Action (DigiGST) – ORG
        obj.setActionDigiGstOrg(getVal(arr, 46));
        // 47: Action (DigiGST) DateTime – AMD
        obj.setActionDigiGstDateTimeAmd(getVal(arr, 47));
        // 48: Action (DigiGST) DateTime – ORG
        obj.setActionDigiGstDateTimeOrg(getVal(arr, 48));
        // 49: Saved to GSTN – AMD
        obj.setSavedToGstnAmd(getVal(arr, 49));
        // 50: Saved to GSTN – ORG
        obj.setSavedToGstnOrg(getVal(arr, 50));
        // 51: Active in IMS (GSTN) – AMD
        obj.setActiveInImsGstnAmd(getVal(arr, 51));
        // 52: Active in IMS (GSTN) – ORG
        obj.setActiveInImsGstnOrg(getVal(arr, 52));
        // 53: Pending Action Blocked – AMD
        obj.setPendingActionBlockedAmd(getVal(arr, 53));
        // 54: Pending Action Blocked – ORG
        obj.setPendingActionBlockedOrg(getVal(arr, 54));
        // 55: GSTN Error – AMD
        obj.setGstnErrorAmd(getVal(arr, 55));
        // 56: GSTN Error – ORG
        obj.setGstnErrorOrg(getVal(arr, 56));
        // 57: Checksum – AMD
        obj.setChecksumAmd(getVal(arr, 57));
        // 58: Checksum – ORG
        obj.setChecksumOrg(getVal(arr, 58));
        // 59: Get Call Date Time – AMD
        obj.setGetCallDateTimeAmd(formatTimestamp(arr, 59));
        // 60: Get Call Date Time – ORG
        obj.setGetCallDateTimeOrg(formatTimestamp(arr, 60));
        // 61: IMS UniqueID – AMD
        obj.setImsUniqueIdAmd(getVal(arr, 61));
        // 62: IMS UniqueID – ORG
        obj.setImsUniqueIdOrg(getVal(arr, 62));
        //added columns
        obj.setItcReductionRequiredAmd(getVal(arr, 63));
        obj.setItcReductionRequiredOrg(getVal(arr, 64));
        obj.setIgstDeclToRedItcAmd(getVal(arr, 65));
        obj.setIgstDeclToRedItcOrg(getVal(arr, 66));
        obj.setCgstDeclToRedItcAmd(getVal(arr, 67));
        obj.setCgstDeclToRedItcOrg(getVal(arr, 68));
        obj.setSgstDeclToRedItcAmd(getVal(arr, 69));
        obj.setSgstDeclToRedItcOrg(getVal(arr, 70));
        obj.setCessDeclToRedItcAmd(getVal(arr, 71));
        obj.setCessDeclToRedItcOrg(getVal(arr, 72));
        return obj;
    }


    private String getVal(Object[] arr, int index) {
        return arr.length > index && arr[index] != null ? arr[index].toString() : null;
    }

    private String prefixQuote(Object[] arr, int index) {
        return arr.length > index && arr[index] != null && !arr[index].toString().isEmpty() ? "'" + arr[index].toString() : null;
    }

    private String formatTimestamp(Object[] arr, int index) {
        if (arr.length > index && arr[index] != null && !arr[index].toString().isEmpty()) {
            String timestamp = arr[index].toString();
            // trim off any trailing fraction/nanos
            if (timestamp.length() > 19) {
                timestamp = timestamp.substring(0, 19);
            }
            // parse with the correct pattern for yyyy-MM-dd
            DateTimeFormatter inputFmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime utcLdt = LocalDateTime.parse(timestamp, inputFmt);

            // convert from UTC to IST
            ZonedDateTime utcZdt = utcLdt.atZone(ZoneId.of("UTC"));
            ZonedDateTime istZdt = utcZdt.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

            // output in dd-MM-yyyy if you still want that
            DateTimeFormatter outputFmt =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return DownloadReportsConstant.CSVCHARACTER + istZdt.format(outputFmt);
        }
        return null;
    }


    private static File createTempDir() throws IOException {
        return Files.createTempDirectory("DownloadReports").toFile();
    }

    private static String getUniqueFileName(String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
        timeMilli = timeMilli.replace(".", "").replace("-", "").replace(":", "");
        return fileName + "_" + timeMilli;
    }

    private Integer getChunkSize() {
        Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
        String chunkSize = config.getValue();
        return Integer.valueOf(chunkSize);
    }

    private StatefulBeanToCsv<ImsAmdOrgTrackReportDto> getBeanWriter(String[] columnMappings, Writer writer) {
        ColumnPositionMappingStrategy<ImsAmdOrgTrackReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
        mappingStrategy.setType(ImsAmdOrgTrackReportDto.class);
        mappingStrategy.setColumnMapping(columnMappings);
        StatefulBeanToCsvBuilder<ImsAmdOrgTrackReportDto> builder = new StatefulBeanToCsvBuilder<>(writer);
        return builder.withMappingStrategy(mappingStrategy).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withLineEnd(CSVWriter.DEFAULT_LINE_END).withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
    }
}