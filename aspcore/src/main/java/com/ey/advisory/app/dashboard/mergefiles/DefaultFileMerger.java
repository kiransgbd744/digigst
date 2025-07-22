package com.ey.advisory.app.dashboard.mergefiles;

import static com.ey.advisory.app.util.MergeFilesUtil.createDownloadDir;
import static com.ey.advisory.app.util.MergeFilesUtil.createOutputdDir;
import static com.ey.advisory.app.util.MergeFilesUtil.createTempDir;
import static com.ey.advisory.app.util.MergeFilesUtil.downloadFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.MergeFilesUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.zip.DirAndFilesCompressor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.RFC4180ParserBuilder;

import lombok.extern.slf4j.Slf4j;

@Component("DefaultFileMerger")
@Slf4j
public class DefaultFileMerger implements FileMerger {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("DirAndFilesCompressorImpl")
	private DirAndFilesCompressor compressor;

	@Autowired
	private GetAnx1BatchRepository getAnx1BatchRepo;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	private static final String GSTR1_CHUNK_ROWS = "api.call.dash.gstr1.trnx.report.chunk.rows";
	private static final String CONF_CATEG = "GSTR1_REPORTS";

	private static final ImmutableList<String> transactionalSectionList = ImmutableList
			.of("B2B", "B2BA", "EXP", "EXPA", "B2CL", "B2CLA", "CDNR", "CDNRA",
					"CDNUR", "CDNURA");

	private static final ImmutableList<String> GSTR6_TransactionalSectionList = ImmutableList
			.of("B2B", "B2BA", "CDN", "CDNA");

	protected static final String[] headerRecord = { "SupplierGSTIN",
			"ReturnPeriod", "TableType", "DocumentType", "DelinkFlag", "Type",
			"DocumentNo", "DocumentDate", "OriginalDocumentNo",
			"OriginalDocumentDate", "CRDRPreGST", "RecipientGSTIN", "POS",
			"LineNumber", "TaxRate", "DifferentialPercentageRate",
			"TaxableValue", "IGSTAmount", "CGSTAmount", "SGST/UTGSTAmount",
			"CessAmount", "InvoiceValue", "ReverseCharge", "EcomGSTIN",
			"OriginalInvoiceDate(RCR/RDR)", "OriginalInvoiceNumber(RCR/RDR)",
			"PortCode", "ShippingBillNumber", "ShippingBillDate", "IsFiled",
			"IRN Number", "IRN Generation Date", "Source Type of IRN" };

	static final Map<Integer, Integer> headerB2B = ImmutableMap
			.<Integer, Integer>builder().put(0, 11).put(5, 6).put(6, 7)
			.put(7, 21).put(8, 12).put(9, 22).put(10, 23).put(11, 3).put(14, 15)
			.put(15, 13).put(16, 14).put(17, 16).put(18, 17).put(19, 18)
			.put(20, 19).put(21, 20).put(22, 32).put(23, 31).put(24, 30)
			.build();

	static final Map<Integer, Integer> headerB2BA = ImmutableMap
			.<Integer, Integer>builder().put(0, 11).put(5, 8).put(6, 9)
			.put(7, 6).put(8, 7).put(9, 21).put(10, 12).put(11, 22).put(12, 23)
			.put(13, 3).put(16, 15).put(17, 13).put(18, 14).put(19, 16)
			.put(20, 17).put(21, 18).put(22, 19).put(23, 20).build();

	static final Map<Integer, Integer> headerEXP = ImmutableMap
			.<Integer, Integer>builder().put(0, 3).put(3, 6).put(4, 7)
			.put(5, 21).put(6, 26).put(7, 27).put(8, 28).put(9, 15).put(10, 16)
			.put(11, 14).put(12, 17).put(13, 20).put(14, 32).put(15, 31)
			.put(16, 30).build();

	static final Map<Integer, Integer> headerEXPA = ImmutableMap
			.<Integer, Integer>builder().put(0, 3).put(3, 8).put(4, 9).put(5, 6)
			.put(6, 7).put(7, 21).put(8, 26).put(9, 27).put(10, 28).put(11, 15)
			.put(12, 16).put(13, 14).put(14, 17).put(15, 20).build();

	static final Map<Integer, Integer> headerB2CL = ImmutableMap
			.<Integer, Integer>builder().put(0, 12).put(3, 6).put(4, 7)
			.put(5, 21).put(6, 23).put(7, 3).put(8, 15).put(9, 13).put(10, 14)
			.put(11, 16).put(12, 17).put(13, 20).build();

	static final Map<Integer, Integer> headerB2CLA = ImmutableMap
			.<Integer, Integer>builder().put(0, 12).put(3, 8).put(4, 9)
			.put(5, 6).put(6, 7).put(7, 21).put(8, 23).put(9, 3).put(10, 15)
			.put(11, 13).put(12, 14).put(13, 16).put(14, 17).put(15, 20)
			.build();

	static final Map<Integer, Integer> headerCDNR = ImmutableMap
			.<Integer, Integer>builder().put(0, 11).put(7, 3).put(8, 6)
			.put(9, 7).put(10, 10).put(11, 8).put(12, 9).put(13, 21).put(14, 15)
			.put(15, 13).put(16, 14).put(17, 16).put(18, 17).put(19, 18)
			.put(20, 19).put(21, 20).put(22, 32).put(23, 31).put(24, 30)
			.put(25, 4).put(26, 12).put(27, 22).build();

	static final Map<Integer, Integer> headerCDNRA = ImmutableMap
			.<Integer, Integer>builder().put(0, 11).put(7, 8).put(8, 9)
			.put(9, 3).put(10, 6).put(11, 7).put(12, 24).put(13, 25).put(14, 21)
			.put(15, 10).put(16, 15).put(17, 4).put(18, 12).put(19, 22)
			.put(20, 13).put(21, 16).put(22, 16).put(23, 17).put(24, 18)
			.put(25, 19).put(26, 20).build();

	static final Map<Integer, Integer> headerCDNUR = ImmutableMap
			.<Integer, Integer>builder().put(2, 5).put(3, 3).put(4, 6).put(5, 7)
			.put(6, 10).put(7, 8).put(8, 9).put(9, 21).put(10, 15).put(11, 13)
			.put(12, 14).put(13, 16).put(14, 17).put(15, 20).put(16, 32)
			.put(17, 31).put(18, 30).put(19, 4).put(20, 12).put(21, 22).build();

	static final Map<Integer, Integer> headerCDNURA = ImmutableMap
			.<Integer, Integer>builder().put(2, 5).put(3, 3).put(4, 8).put(5, 9)
			.put(6, 6).put(7, 7).put(8, 10).put(9, 24).put(10, 25).put(11, 21)
			.put(12, 15).put(13, 4).put(14, 12).put(15, 22).put(16, 13)
			.put(17, 14).put(18, 16).put(19, 17).put(20, 20).build();

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	@Override
	public String merge(Long id, List<FileMergeInfo> fileMergeInfoList,
			String returnType) {

		File tempDir = null;
		File downloadDir = null;
		File outputDir = null;
		String zipFileName = "";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Created request parameter with id - {}, About to create Temp directories",
					id);
		}

		try {
			tempDir = createTempDir();
			downloadDir = createDownloadDir(tempDir);
			outputDir = createOutputdDir(tempDir);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Created temporary, downloaded and output directores");
			}

			for (FileMergeInfo info : fileMergeInfoList) {
				createOutputFile(info, downloadDir, outputDir, returnType);
			}

			if (downloadDir.listFiles().length == 0
					|| outputDir.listFiles().length == 0) {

				LOGGER.error("No CSV Files Found for the selected combinations,"
						+ " Hence marking the Request ID {} as No Data Found",
						id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return ReportStatusConstants.NO_DATA_FOUND;
			}

			File[] files = outputDir.listFiles();

			// Deleting the Empty Files
			for (File file : files) {
				if (file.length() == 0) {
					boolean isDeleted = file.delete();
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"File {} is Empty, Hence deleting and status is {}",
								file.getName(), isDeleted);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Number of files in output Dir - {}",
						files.length);
			}

			List<String> filesToZip = Arrays.stream(files)
					.map(f -> f.getAbsolutePath())
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Files to be zipped - {}", filesToZip);
			}

			LocalDateTime now = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());

			String suffixStr = now.format(FORMATTER);

			if (id == null) {
				FileInfo info = fileMergeInfoList.get(0).getFileInfoList()
						.get(0);
				zipFileName = new StringJoiner("_").add(returnType)
						.add(info.getGstin()).add(info.getTaxPeriod())
						.toString() + ".zip";
			} else {

				zipFileName = returnType + "_" + suffixStr + ".zip";

			}

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("All Files in output Dir - %s",
						outputDir.getAbsolutePath());
				LOGGER.debug(msg);
			}

			compressor.compressFiles(outputDir.getAbsolutePath(), zipFileName,
					filesToZip);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("ZipFile name : %s", zipFileName);
				LOGGER.debug(msg);
			}

			File zipFile = new File(outputDir, zipFileName);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to upload zip file");
			}

			String uploadedDocName = DocumentUtility.uploadFileWithFileName(
					zipFile, "Anx1FileStatusReport", zipFileName);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format(
						"Zip file uploaded successfully with name : %s",
						uploadedDocName);
				LOGGER.debug(msg);
			}

			if (id != null) {
				fileStatusDownloadReportRepo.updateStatus(id,
						"REPORT_GENERATED", uploadedDocName,
						LocalDateTime.now());
			}
		} catch (Exception e) {
			if (id != null) {
				fileStatusDownloadReportRepo.updateStatus(id,
						"REPORT_GENERATION_FAILED", null, LocalDateTime.now());
			}
			LOGGER.error(
					"error occured while generating api call Gstr1 download",
					e);
			throw new AppException(
					"error occured while generating api call Gstr1 download",
					e);

		} finally {
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(outputDir);
			GenUtil.deleteTempDir(downloadDir);
		}

		return zipFileName;
	}

	private void createOutputFile(FileMergeInfo info, File downloadDir,
			File outputDir, String returnType) throws IOException {

		List<FileInfo> fileList = info.getFileInfoList();

		if (returnType.equalsIgnoreCase("GSTR1") || returnType.equalsIgnoreCase("GSTR1A")) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("fileList - {}", fileList);
			}
			
			boolean isHeaderReq = true;
			
			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Output file name- {}", outputFileName);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("output File- {}", outFile.getAbsolutePath());
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			String isFiled = "N";
			
			int srNo = 1;
			int txnRowsCount = 0;
			int verticalRowsCount = 0;
			int fileIndex = 0;
			int maxLimitPerFile = getChunkingSizes();
//			int maxLimitPerVerticalFile = 6;
			
			int finalTxnRowsCount = 0;
			int finalVerticalRowsCount = 0;
			String[] outValuesHeader = new String[40];
			boolean isVerHeaderReq = true;

				for (FileInfo fileInfo : fileList) {
					File inFile = null;
					Object obj = null;
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("fileInfo - {}", fileInfo);
					}
					
					if(returnType!=null && APIConstants.GSTR1A.equalsIgnoreCase(returnType))
					{
					inFile = downloadFile(fileInfo, downloadDir, "GSTR1A");
					
					if (inFile == null)
						continue;

					obj = getAnx1BatchRepo.findIsFiled(
							fileInfo.getGstin(),
							fileInfo.getTaxPeriod(),
							fileInfo.getSection(),"GSTR1A");

					}else
					{
						inFile = downloadFile(fileInfo, downloadDir, "GSTR1");
						
						if (inFile == null)
							continue;

						obj = getAnx1BatchRepo.findIsFiled(
								fileInfo.getGstin(),
								fileInfo.getTaxPeriod(),
								fileInfo.getSection(),"GSTR1");

						
					}
					if (obj != null) {
						Integer i = Integer.parseInt(obj.toString());
						if (i == 1)
							isFiled = APIConstants.Y;
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("input File-{}", inFile.getAbsolutePath());
					}

					if (transactionalSectionList
							.contains(fileInfo.getSection())) {

						if (isHeaderReq) {
							csvWriter.writeNext(headerRecord);
							isHeaderReq = false;
						}

						Map<Integer, Integer> columnMapping = getHeaderMapping(
								fileInfo.getSection());

						try (CSVReader csvReader = new CSVReaderBuilder(
								new FileReader(inFile))
										.withSkipLines(0)
										.withCSVParser(
												new RFC4180ParserBuilder()
														.build())
										.build();) {
							String[] inValues = null;

							csvReader.readNext();// skips header
							txnRowsCount++;

							while ((inValues = csvReader.readNext()) != null) {
								
								txnRowsCount++;
								finalTxnRowsCount++;
								
								if (txnRowsCount >= maxLimitPerFile) {
									
									try {
								        csvWriter.flush();
								        csvWriter.close();
								    } catch (IOException e) {
								        System.err.println("Error closing CSVWriter: " + e.getMessage());
								    }
				                    
									txnRowsCount = 0;
									fileIndex++;

									csvWriter = reinitializeWriter(outFile, fileIndex, headerRecord);
								    
									isHeaderReq = false; // Already written inside the method
								}
								
								String[] outValues = new String[40];
								outValues[0] = fileInfo.getGstin();
								outValues[1] = DownloadReportsConstant.CSVCHARACTER
										.concat(fileInfo.getTaxPeriod());
								outValues[2] = fileInfo.getSection();
								outValues[29] = isFiled;

								for (int i = 0; i < inValues.length; i++) {

									if (columnMapping.containsKey(i)) {
										int destIndex = columnMapping.get(i);
										outValues[destIndex] = inValues[i];
									}

								}
								csvWriter.writeNext(outValues);
							}
							
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("transactionalCount : " + txnRowsCount);
							}
						}
					} else {
						try (CSVReader csvReader = new CSVReaderBuilder(
								new FileReader(inFile))
										.withSkipLines(0)
										.withCSVParser(
												new RFC4180ParserBuilder()
														.build())
										.build();) {
							String[] inValues = null;
							String[] outValues = new String[40];
							
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Inside  : transactionalSectionList.contains(fileInfo.getSection()) else isHeaderReq " + isHeaderReq);
							}

							if (!isHeaderReq) {
								csvReader.readNext();// skip header except for
														// first file
							}
							// isHeaderReq = false;

							while ((inValues = csvReader.readNext()) != null) {
								
								if (verticalRowsCount >= maxLimitPerFile) {
									
									try {
								        csvWriter.flush();
								        csvWriter.close();
								    } catch (IOException e) {
								        System.err.println("Error closing CSVWriter: " + e.getMessage());
								    }
				                    
									verticalRowsCount = 0;
									fileIndex++;

									writer = Files.newBufferedWriter(Paths.get(outFile + "_" + fileIndex + ".csv"));
							        csvWriter = new CSVWriter(writer,
							                CSVWriter.DEFAULT_SEPARATOR,
							                CSVWriter.NO_QUOTE_CHARACTER,
							                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
							                CSVWriter.DEFAULT_LINE_END);
							        
							        for (int i = 0; i < outValuesHeader.length; i++) {
									    String str = outValuesHeader[i];
									    // Process the character
									    
									    if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("Inside Header : "+ i+ " " + str);
										}
									}
							        
							        csvWriter.writeNext(outValuesHeader); // Write header in new file
							        ++verticalRowsCount;
							        ++finalVerticalRowsCount;
								    
//									isHeaderReq = true;
								}

								if (isHeaderReq) {
									outValues[0] = "S.No";
									outValues[1] = "SupplierGSTIN";
									outValues[2] = "ReturnPeriod";
									isHeaderReq = false;
								} else {
									outValues[0] = (srNo++) + "";
									outValues[1] = fileInfo.getGstin();
									outValues[2] = DownloadReportsConstant.CSVCHARACTER
											.concat(fileInfo.getTaxPeriod());
								}
								for (int i = 0; i < inValues.length; i++) {

									int destIndex = i + 3;
									outValues[destIndex] = inValues[i];

								}
								
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("isVerHeaderReq : " + isVerHeaderReq);
								}
								if (isVerHeaderReq) {
//								  outValuesHeader=outValues;
									
									for (int i = 0; i < outValues.length; i++) {
									    outValuesHeader[i]=outValues[i];
									}
								  isVerHeaderReq = false;
								  
								  for (int i = 0; i < outValuesHeader.length; i++) {
									    String str = outValuesHeader[i];
									    // Process the character
									    
									    if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("Header : "+ i+ " " + str);
										}
									}
								}
								csvWriter.writeNext(outValues);
								
								++verticalRowsCount;
								++finalVerticalRowsCount;
							}
							
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("verticalCount : " + verticalRowsCount);
								LOGGER.debug("finalVerticalRowsCount : " + finalVerticalRowsCount);
								LOGGER.debug("outValuesHeader : " + outValuesHeader);
							}
						}

					}

				}
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Final transactionalCount : " + finalTxnRowsCount);
					LOGGER.debug("Final verticalCount : " + finalVerticalRowsCount);
				}


				csvWriter.flush();
				csvWriter.close();
			

		} else if (returnType.equalsIgnoreCase("GSTR3B")) {

			boolean isHeaderReq = true;

			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("Output file name - %s",
						outputFileName);
				LOGGER.debug(msg);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("output File - %s",
						outFile.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			for (FileInfo fileInfo : fileList) {
				File inFile = downloadFile(fileInfo, downloadDir, "GSTR3B");

				if (inFile == null)
					continue;

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("input File - %s",
							inFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				/*
				 * if (isHeaderReq) { csvWriter.writeNext(headerRecord);
				 * isHeaderReq = false; }
				 */

				try (CSVReader csvReader = new CSVReaderBuilder(
						new FileReader(inFile))
								.withSkipLines(0)
								.withCSVParser(
										new RFC4180ParserBuilder().build())
								.build();) {
					String[] inValues = null;

					if (!isHeaderReq) {
						csvReader.readNext();
					}

					while ((inValues = csvReader.readNext()) != null) {

						String[] outValues = new String[40];

						if (isHeaderReq) {
							outValues[0] = "Supplier GSTIN";
							outValues[1] = "Return Period";

							for (int i = 1; i < inValues.length; i++) {
								outValues[i + 1] = inValues[i];
							}
							csvWriter.writeNext(outValues);

							isHeaderReq = false;
							continue;
						} else {
							outValues[0] = fileInfo.getGstin();
							outValues[1] = DownloadReportsConstant.CSVCHARACTER
									.concat(fileInfo.getTaxPeriod());
						}

						for (int i = 1; i < inValues.length; i++) {

							/*
							 * if(i == 4){
							 * if(Strings.isNullOrEmpty(inValues[i])){
							 * outValues[i + 1] = inValues[i]; }else{
							 * outValues[i + 1] = DownloadReportsConstant
							 * .CSVCHARACTER.concat(inValues[i]); } } else if(i
							 * > 4) { outValues[i + 1] = DownloadReportsConstant
							 * .CSVCHARACTER.concat(inValues[i]); } else {
							 * outValues[i + 1] = inValues[i]; }
							 */

							outValues[i + 1] = inValues[i];

						}
						csvWriter.writeNext(outValues);
					}
				}

			}
			csvWriter.flush();
			csvWriter.close();

		}

		else if (returnType.equalsIgnoreCase("GSTR9")) {

			boolean isHeaderReq = true;

			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("Output file name - %s",
						outputFileName);
				LOGGER.debug(msg);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("output File - %s",
						outFile.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			for (FileInfo fileInfo : fileList) {
				File inFile = downloadFile(fileInfo, downloadDir,
						GSTR9Constants.GSTR9);
				if (inFile == null)
					continue;

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("input File - %s",
							inFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try (CSVReader csvReader = new CSVReaderBuilder(
						new FileReader(inFile))
								.withSkipLines(0)
								.withCSVParser(
										new RFC4180ParserBuilder().build())
								.build();) {
					String[] inValues = null;

					if (!isHeaderReq) {
						csvReader.readNext();
					}
					isHeaderReq = false;

					while ((inValues = csvReader.readNext()) != null) {
						csvWriter.writeNext(inValues);
					}
				}
			}
			csvWriter.flush();
			csvWriter.close();

		} else if (returnType.equalsIgnoreCase("GSTR7")) {

			boolean isHeaderReq = true;

			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("Output file name - %s",
						outputFileName); 
				LOGGER.debug(msg);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("output File - %s",
						outFile.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			for (FileInfo fileInfo : fileList) {
				File inFile = downloadFile(fileInfo, downloadDir, "GSTR7");

				if (inFile == null)
					continue;

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("input File - %s",
							inFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try (CSVReader csvReader = new CSVReaderBuilder(
						new FileReader(inFile))
								.withSkipLines(0)
								.withCSVParser(
										new RFC4180ParserBuilder().build())
								.build();) {
					String[] inValues = null;

					if (!isHeaderReq) {
						csvReader.readNext();
					}
					isHeaderReq = false;

					while ((inValues = csvReader.readNext()) != null) {
						csvWriter.writeNext(inValues);
					}
				}

			}
			csvWriter.flush();
			csvWriter.close();

		} else if (returnType.equalsIgnoreCase("GSTR8")) {

			boolean isHeaderReq = true;

			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("Output file name - %s",
						outputFileName); 
				LOGGER.debug(msg);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("output File - %s",
						outFile.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			for (FileInfo fileInfo : fileList) {
				File inFile = downloadFile(fileInfo, downloadDir, "GSTR8");

				if (inFile == null)
					continue;

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("input File - %s",
							inFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try (CSVReader csvReader = new CSVReaderBuilder(
						new FileReader(inFile))
								.withSkipLines(0)
								.withCSVParser(
										new RFC4180ParserBuilder().build())
								.build();) {
					String[] inValues = null;

					if (!isHeaderReq) {
						csvReader.readNext();
					}
					isHeaderReq = false;

					while ((inValues = csvReader.readNext()) != null) {
						csvWriter.writeNext(inValues);
					}
				}

			}
			csvWriter.flush();
			csvWriter.close();

		} else if (returnType.equalsIgnoreCase("GSTR2X")) {

			boolean isHeaderReq = true;

			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("Output file name - %s",
						outputFileName);
				LOGGER.debug(msg);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("output File - %s",
						outFile.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			for (FileInfo fileInfo : fileList) {
				File inFile = downloadFile(fileInfo, downloadDir, "GSTR2X");

				if (inFile == null)
					continue;

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("input File - %s",
							inFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try (CSVReader csvReader = new CSVReaderBuilder(
						new FileReader(inFile))
								.withSkipLines(0)
								.withCSVParser(
										new RFC4180ParserBuilder().build())
								.build();) {
					String[] inValues = null;

					if (!isHeaderReq) {
						csvReader.readNext();
					}
					isHeaderReq = false;

					while ((inValues = csvReader.readNext()) != null) {
						csvWriter.writeNext(inValues);
					}
				}

			}
			csvWriter.flush();
			csvWriter.close();

		} else if (returnType.equalsIgnoreCase("ITC04")) {

			boolean isHeaderReq = true;

			String timeMilli = FORMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String outputFileName = info.getOutputFileName() + "_" + timeMilli;

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("Output file name - %s",
						outputFileName);
				LOGGER.debug(msg);
			}

			File outFile = MergeFilesUtil.createOutputFile(outputFileName,
					outputDir);

			if (LOGGER.isDebugEnabled()) {

				String msg = String.format("output File - %s",
						outFile.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Writer writer = Files
					.newBufferedWriter(Paths.get(outFile + ".csv"));

			CSVWriter csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);

			for (FileInfo fileInfo : fileList) {
				File inFile = downloadFile(fileInfo, downloadDir, "ITC04");

				if (inFile == null)
					continue;

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("input File - %s",
							inFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try (CSVReader csvReader = new CSVReaderBuilder(
						new FileReader(inFile))
								.withSkipLines(0)
								.withCSVParser(
										new RFC4180ParserBuilder().build())
								.build();) {
					String[] inValues = null;

					if (!isHeaderReq) {
						csvReader.readNext();
					}
					isHeaderReq = false;

					while ((inValues = csvReader.readNext()) != null) {
						csvWriter.writeNext(inValues);
					}
				}

			}
			csvWriter.flush();
			csvWriter.close();

		} else if (returnType.equalsIgnoreCase("GSTR6")) {

			Map<String, List<FileInfo>> fileInfoQuaterList = formQuaterList(
					fileList);

			for (Map.Entry<String, List<FileInfo>> fileInfoMap : fileInfoQuaterList
					.entrySet()) {

				boolean isHeaderReq = true;

				String outputFileName = info.getOutputFileName() + "_"
						+ fileInfoMap.getKey();

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("Output file name - %s",
							outputFileName);
					LOGGER.debug(msg);
				}

				File outFile = MergeFilesUtil.createOutputFile(outputFileName,
						outputDir);

				if (LOGGER.isDebugEnabled()) {

					String msg = String.format("output File - %s",
							outFile.getAbsolutePath());
					LOGGER.debug(msg);
				}

				Writer writer = Files
						.newBufferedWriter(Paths.get(outFile + ".csv"));

				CSVWriter csvWriter = new CSVWriter(writer,
						CSVWriter.DEFAULT_SEPARATOR,
						CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.DEFAULT_LINE_END);

				for (FileInfo fileInfo : fileInfoMap.getValue()) {
					File inFile = downloadFile(fileInfo, downloadDir, "GSTR6");

					if (inFile == null)
						continue;

					if (LOGGER.isDebugEnabled()) {

						String msg = String.format("input File - %s",
								inFile.getAbsolutePath());
						LOGGER.debug(msg);
					}
					if (GSTR6_TransactionalSectionList
							.contains(fileInfo.getSection())) {

						try (CSVReader csvReader = new CSVReaderBuilder(
								new FileReader(inFile))
										.withSkipLines(0)
										.withCSVParser(
												new RFC4180ParserBuilder()
														.build())
										.build();) {
							String[] inValues = null;

							if (!isHeaderReq) {
								csvReader.readNext();
							}
							isHeaderReq = false;

							while ((inValues = csvReader.readNext()) != null) {
								csvWriter.writeNext(inValues);
							}
						}
					} else {

						try (CSVReader csvReader = new CSVReaderBuilder(
								new FileReader(inFile))
										.withSkipLines(0)
										.withCSVParser(
												new RFC4180ParserBuilder()
														.build())
										.build();) {
							String[] inValues = null;

							if (!isHeaderReq) {
								csvReader.readNext();
							}
							isHeaderReq = false;

							while ((inValues = csvReader.readNext()) != null) {
								csvWriter.writeNext(inValues);
							}
						}

					}
				}
				csvWriter.flush();
				csvWriter.close();
			}

		}

	}

	private Map<String, List<FileInfo>> formQuaterList(
			List<FileInfo> fileInfoList) {

		List<FileInfo> q1List = new ArrayList<>();
		List<FileInfo> q2List = new ArrayList<>();
		List<FileInfo> q3List = new ArrayList<>();
		List<FileInfo> q4List = new ArrayList<>();

		List<String> q1Months = Arrays.asList("04", "05", "06");
		List<String> q2Months = Arrays.asList("07", "08", "09");
		List<String> q3Months = Arrays.asList("10", "11", "12");
		List<String> q4Months = Arrays.asList("01", "02", "03");

		Map<String, List<FileInfo>> fyList = new HashMap<>();

		for (FileInfo info : fileInfoList) {

			String month = info.getTaxPeriod().substring(0, 2);

			if (q1Months.contains(month))
				q1List.add(info);
			if (q2Months.contains(month))
				q2List.add(info);
			if (q3Months.contains(month))
				q3List.add(info);
			if (q4Months.contains(month))
				q4List.add(info);

		}

		if (!q1List.isEmpty())
			fyList.put("Q1", q1List);

		if (!q2List.isEmpty())
			fyList.put("Q2", q2List);

		if (!q3List.isEmpty())
			fyList.put("Q3", q3List);

		if (!q4List.isEmpty())
			fyList.put("Q4", q4List);

		return fyList;
	}

	private Map<Integer, Integer> getHeaderMapping(String section) {

		if ("B2B".equalsIgnoreCase(section))
			return headerB2B;
		else if ("B2BA".equalsIgnoreCase(section))
			return headerB2BA;
		else if ("EXP".equalsIgnoreCase(section))
			return headerEXP;
		else if ("EXPA".equalsIgnoreCase(section))
			return headerEXPA;
		else if ("B2CL".equalsIgnoreCase(section))
			return headerB2CL;
		else if ("B2CLA".equalsIgnoreCase(section))
			return headerB2CLA;
		else if ("CDNR".equalsIgnoreCase(section))
			return headerCDNR;
		else if ("CDNRA".equalsIgnoreCase(section))
			return headerCDNRA;
		else if ("CDNUR".equalsIgnoreCase(section))
			return headerCDNUR;
		else if ("CDNURA".equalsIgnoreCase(section))
			return headerCDNURA;
		else
			return null;

	}
	
	public Integer getChunkingSizes() {

		Config config = configManager.getConfig(CONF_CATEG, GSTR1_CHUNK_ROWS);
		String chunkSize = config.getValue() != null ? config.getValue()
				: "500000";
		return (Integer.valueOf(chunkSize));
	}
	
	private CSVWriter reinitializeWriter(File outFile, int fileIndex, String[] headerRecord) {
	    try {
	        Writer writer = Files.newBufferedWriter(Paths.get(outFile + "_" + fileIndex + ".csv"));
	        CSVWriter csvWriter = new CSVWriter(writer,
	                CSVWriter.DEFAULT_SEPARATOR,
	                CSVWriter.NO_QUOTE_CHARACTER,
	                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                CSVWriter.DEFAULT_LINE_END);
	        
	        csvWriter.writeNext(headerRecord); // Write header in new file
	        
	        return csvWriter;
	    } catch (IOException e) {
	        System.err.println("Error reinitializing CSVWriter: " + e.getMessage());
	        throw new RuntimeException("Failed to initialize CSVWriter", e);
	    }
	}

}
