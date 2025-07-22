/**
 * 
 */
package com.ey.advisory.app.services.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BSummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNSummaryEntity;
import com.ey.advisory.app.dashboard.mergefiles.FileMergeInput;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8AB2BASummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8AB2BSummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ACDNASummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ACDNSummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ASummaryDetailsRepository;
import com.ey.advisory.app.data.services.gstr8A.Gstr8AGetSuccessHandler;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Component
@Slf4j
public class DownloadGstr8AServiceImpl {

	@Autowired
	Gstr8ASummaryDetailsRepository gstr8ASummaryDetailsRepo;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	Gstr8AB2BSummaryDetailsRepository gstr8AB2BSummaryDetailsRepository;

	@Autowired
	Gstr8AB2BASummaryDetailsRepository gstr8AB2BASummaryDetailsRepository;

	@Autowired
	Gstr8ACDNSummaryDetailsRepository gstr8ACDNSummaryDetailsRepository;

	@Autowired
	Gstr8ACDNASummaryDetailsRepository gstr8ACDNASummaryDetailsRepository;

	@Autowired
	Gstr8AGetSuccessHandler gstr8AGetSuccessHandler;

	private static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	public void mergeReport(Long id, List<FileMergeInput> fileMergeInputList,
			String fy) {

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		List<Gstr8AGetB2BSummaryEntity> b2breportList = new ArrayList<>();
		List<Gstr8AGetB2BASummaryEntity> b2bareportList = new ArrayList<>();
		List<Gstr8AGetCDNSummaryEntity> cdnReportList = new ArrayList<>();
		List<Gstr8AGetCDNASummaryEntity> cdnaReportList = new ArrayList<>();
		List<Gstr8AGetB2BSummaryEntity> b2bList = new ArrayList<>();
		List<Gstr8AGetB2BASummaryEntity> b2baList = new ArrayList<>();
		List<Gstr8AGetCDNSummaryEntity> cdnList = new ArrayList<>();
		List<Gstr8AGetCDNASummaryEntity> cdnaList = new ArrayList<>();

		for (FileMergeInput inp : fileMergeInputList) {
			b2bList = gstr8AB2BSummaryDetailsRepository
					.findByCgstinAndFinYearAndIsDeleteFalse(inp.getGstin(), fy);
			b2baList = gstr8AB2BASummaryDetailsRepository
					.findByCgstinAndFinYearAndIsDeleteFalse(inp.getGstin(), fy);
			cdnList = gstr8ACDNSummaryDetailsRepository
					.findByCgstinAndFinYearAndIsDeleteFalse(inp.getGstin(), fy);
			cdnaList = gstr8ACDNASummaryDetailsRepository
					.findByCgstinAndFinYearAndIsDeleteFalse(inp.getGstin(), fy);

			b2breportList.addAll(b2bList);
			b2bareportList.addAll(b2baList);
			cdnReportList.addAll(cdnList);
			cdnaReportList.addAll(cdnaList);
		}

		List<Gstr8AReportConvertor> reportListData = getReportData(
				b2breportList, b2bareportList, cdnReportList, cdnaReportList,
				id);

		if (reportListData.isEmpty()) {
			LOGGER.error("No Data found for report id : %d", id);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.NO_DATA_FOUND, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			return;
		}

		LOGGER.debug("GSTR8A Report List Size {} for Group {}",
				reportListData.size(), TenantContext.getTenantId());

		Writer writer = null;
		File tempDir = null;
		try {

			tempDir = Files.createTempDirectory("TempDirectory").toFile();

			String timeMilli = dtf.format(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ "Table-8A" + "_" + timeMilli + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			String invoiceHeadersTemplate = commonUtility
					.getProp("gstr8a.apicall.async.csv.report.headers");

			writer.append(invoiceHeadersTemplate);
			String[] columnMappings = commonUtility
					.getProp("gstr8a.apicall.async.csv.report.headers.mapping")
					.split(",");

			// writing the data into the csv
			if (LOGGER.isDebugEnabled()) {
				String errMsg = String.format("writsing the data into the csv");
				LOGGER.debug(errMsg);
			}

			ColumnPositionMappingStrategy<Gstr8AReportConvertor> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(Gstr8AReportConvertor.class);
			mappingStrategy.setColumnMapping(columnMappings);
			StatefulBeanToCsvBuilder<Gstr8AReportConvertor> builder = new StatefulBeanToCsvBuilder<>(
					writer);
			StatefulBeanToCsv<Gstr8AReportConvertor> beanWriter = builder
					.withMappingStrategy(mappingStrategy)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withLineEnd(CSVWriter.DEFAULT_LINE_END)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
			beanWriter.write(reportListData);

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

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
						"GSTR8A", null, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile, "Anx1FileStatusReport");
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now(), docId);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
		} finally {
			deleteTempDir(tempDir);
		}
	}

	private List<Gstr8AReportConvertor> getReportData(
			List<Gstr8AGetB2BSummaryEntity> b2bList,
			List<Gstr8AGetB2BASummaryEntity> b2baList,
			List<Gstr8AGetCDNSummaryEntity> cdnList,
			List<Gstr8AGetCDNASummaryEntity> cdnaList, Long id) {
		List<Gstr8AReportConvertor> dtoList = new ArrayList<>();
		try {

			for (Gstr8AGetB2BSummaryEntity ent : b2bList) {

				Gstr8AReportConvertor dto = new Gstr8AReportConvertor();

				dto.setCgstin(ent.getCgstin() != null ? ent.getCgstin() : null);
				dto.setReturnPeriod(ent.getRetPeriod() != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(ent.getRetPeriod())
						: null);
				dto.setSgstin(ent.getSgstin() != null ? ent.getSgstin() : null);
				dto.setTableType("B2B");
				dto.setSupplyType("R");
				dto.setDocType("INV");
				dto.setDocNum(ent.getInvNum() != null ? ent.getInvNum() : null);
				dto.setPos(ent.getPos() != null ? ent.getPos() : null);
				dto.setReverseCharge(ent.getReverseCharge() != null
						? ent.getReverseCharge() : null);
				dto.setInvValue(
						ent.getInvValue() != null ? ent.getInvValue() : null);
				
				dto.setIgst(ent.getIgst() != null ? ent.getIgst() : null);
				dto.setCgst(ent.getCgst() != null ? ent.getCgst() : null);
				dto.setSgst(ent.getSgst() != null ? ent.getSgst() : null);
				dto.setCess(ent.getCess() != null ? ent.getCess() : null);
				// taxpayable=addission of igst+cgst+sgst+cess
				BigDecimal igst = ent.getIgst() != null ? ent.getIgst()
						: BigDecimal.ZERO;
				BigDecimal cgst = ent.getCgst() != null ? ent.getCgst()
						: BigDecimal.ZERO;
				BigDecimal sgst = ent.getSgst() != null ? ent.getSgst()
						: BigDecimal.ZERO;
				BigDecimal cess = ent.getCess() != null ? ent.getCess()
						: BigDecimal.ZERO;

				BigDecimal taxPayable = igst.add(cgst).add(sgst).add(cess);

				dto.setTaxPayable(taxPayable);

				dto.setEligibleItc(ent.getEligibleItc() != null
						? ent.getEligibleItc() : null);
				dto.setReason(ent.getReason() != null ? ent.getReason() : null);
				try {
					dto.setDocDate(ent.getInvDate()!=null ? DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getInvDate())) :null);
				} catch (Exception e) {
				    if (LOGGER.isDebugEnabled()) {
				        LOGGER.debug("Exception occurred during date conversion: {}", e.getMessage(), e);
				    }
				}

				dtoList.add(dto);
			}

			for (Gstr8AGetB2BASummaryEntity ent : b2baList) {

				Gstr8AReportConvertor b2baDto = new Gstr8AReportConvertor();

				b2baDto.setCgstin(
						ent.getCgstin() != null ? ent.getCgstin() : null);
				b2baDto.setReturnPeriod(ent.getRetPeriod() != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(ent.getRetPeriod())
						: null);
				b2baDto.setSgstin(
						ent.getSgstin() != null ? ent.getSgstin() : null);
				b2baDto.setTableType("B2BA");
				b2baDto.setSupplyType("R");
				b2baDto.setDocType("RNV");
				b2baDto.setDocNum(
						ent.getInvNum() != null ? ent.getInvNum() : null);
				b2baDto.setOriginalDocNum(ent.getOriginalInvNum() != null
						? ent.getOriginalInvNum() : null); 

				b2baDto.setPos(ent.getPos() != null ? ent.getPos() : null);
				b2baDto.setReverseCharge(ent.getReverseCharge() != null
						? ent.getReverseCharge() : null);
				b2baDto.setInvValue(
						ent.getInvValue() != null ? ent.getInvValue() : null);

				b2baDto.setIgst(ent.getIgst() != null ? ent.getIgst() : null);
				b2baDto.setCgst(ent.getCgst() != null ? ent.getCgst() : null);
				b2baDto.setSgst(ent.getSgst() != null ? ent.getSgst() : null);
				b2baDto.setCess(ent.getCess() != null ? ent.getCess() : null);
				BigDecimal igst = ent.getIgst() != null ? ent.getIgst()
						: BigDecimal.ZERO;
				BigDecimal cgst = ent.getCgst() != null ? ent.getCgst()
						: BigDecimal.ZERO;
				BigDecimal sgst = ent.getSgst() != null ? ent.getSgst()
						: BigDecimal.ZERO;
				BigDecimal cess = ent.getCess() != null ? ent.getCess()
						: BigDecimal.ZERO;

				BigDecimal taxPayable = igst.add(cgst).add(sgst).add(cess);

				b2baDto.setTaxPayable(taxPayable);
				b2baDto.setEligibleItc(ent.getEligibleItc() != null
						? ent.getEligibleItc() : null);
				b2baDto.setReason(
						ent.getReason() != null ? ent.getReason() : null);
				
				try {
					b2baDto.setDocDate(ent.getInvDate()!=null ? DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getInvDate())) : null);
					b2baDto.setOriginalDocDate(ent.getOriginalInvDate()!=null ? DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getOriginalInvDate())) : null);
					
				} catch (Exception e) {
				    if (LOGGER.isDebugEnabled()) {
				        LOGGER.debug("Exception occurred during date conversion: {}", e.getMessage(), e);
				    }
				}

				dtoList.add(b2baDto);
			}

			for (Gstr8AGetCDNSummaryEntity ent : cdnList) {

				Gstr8AReportConvertor cdnDto = new Gstr8AReportConvertor();

				cdnDto.setCgstin(
						ent.getCgstin() != null ? ent.getCgstin() : null);
				cdnDto.setReturnPeriod(ent.getRetPeriod() != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(ent.getRetPeriod())
						: null);
				cdnDto.setSgstin(
						ent.getSgstin() != null ? ent.getSgstin() : null);
				cdnDto.setTableType("CDN");
				cdnDto.setSupplyType("R");
				cdnDto.setDocType(getDocType(ent.getNoteType()));
				cdnDto.setDocNum(ent.getNoteNumber() != null ? ent.getNoteNumber() : ent.getInvNum());//if nt_num not available then -->inv_num
				
				cdnDto.setPos(ent.getPos() != null ? ent.getPos() : null);
				cdnDto.setReverseCharge(ent.getReverseCharge() != null
						? ent.getReverseCharge() : null);
				cdnDto.setInvValue(
						ent.getInvValue() != null ? ent.getInvValue() : null);
				
				cdnDto.setIgst(ent.getIgst() != null ? ent.getIgst() : null);
				cdnDto.setCgst(ent.getCgst() != null ? ent.getCgst() : null);
				cdnDto.setSgst(ent.getSgst() != null ? ent.getSgst() : null);
				cdnDto.setCess(ent.getCess() != null ? ent.getCess() : null);
				BigDecimal igst = ent.getIgst() != null ? ent.getIgst()
						: BigDecimal.ZERO;
				BigDecimal cgst = ent.getCgst() != null ? ent.getCgst()
						: BigDecimal.ZERO;
				BigDecimal sgst = ent.getSgst() != null ? ent.getSgst()
						: BigDecimal.ZERO;
				BigDecimal cess = ent.getCess() != null ? ent.getCess()
						: BigDecimal.ZERO;

				BigDecimal taxPayable = igst.add(cgst).add(sgst).add(cess);

				cdnDto.setTaxPayable(taxPayable);

				cdnDto.setEligibleItc(ent.getEligibleItc() != null
						? ent.getEligibleItc() : null);
				cdnDto.setReason(
						ent.getReason() != null ? ent.getReason() : null);
				try {
					cdnDto.setDocDate(ent.getNoteDate()!=null ? DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getNoteDate())) : DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getInvDate())) );//if nt_date not available then -->inv_date
					
				} catch (Exception e) {
				    if (LOGGER.isDebugEnabled()) {
				        LOGGER.debug("Exception occurred during date conversion: {}", e.getMessage(), e);
				    }
				}
				dtoList.add(cdnDto);
			}
			for (Gstr8AGetCDNASummaryEntity ent : cdnaList) {

				Gstr8AReportConvertor cdnaDto = new Gstr8AReportConvertor();

				cdnaDto.setCgstin(
						ent.getCgstin() != null ? ent.getCgstin() : null);
				cdnaDto.setReturnPeriod(ent.getRetPeriod() != null
						? DownloadReportsConstant.CSVCHARACTER
								.concat(ent.getRetPeriod())
						: null);
				cdnaDto.setSgstin(
						ent.getSgstin() != null ? ent.getSgstin() : null);
				cdnaDto.setTableType("CDNA"); //
				cdnaDto.setSupplyType("R"); 
				cdnaDto.setDocType(getDocType(ent.getNoteType())); 
				cdnaDto.setDocNum(
						ent.getNoteNumber() != null ? ent.getNoteNumber() : ent.getInvNum()); 
				
				cdnaDto.setOriginalDocNum(ent.getOrigiNoteNum() != null
						? ent.getOrigiNoteNum() : ent.getInvNum()); // oriDocNum =//doubt
				cdnaDto.setOriginalNoteType(gstr8AGetSuccessHandler
						.getOriginNoteType("CDNA", ent.getOrigiNoteType())); 
				cdnaDto.setPos(ent.getPos() != null ? ent.getPos() : null);
				cdnaDto.setReverseCharge(ent.getReverseCharge() != null
						? ent.getReverseCharge() : null);
				cdnaDto.setInvValue(
						ent.getInvValue() != null ? ent.getInvValue() : null);
			
				cdnaDto.setIgst(ent.getIgst() != null ? ent.getIgst() : null);
				cdnaDto.setCgst(ent.getCgst() != null ? ent.getCgst() : null);
				cdnaDto.setSgst(ent.getSgst() != null ? ent.getSgst() : null);
				cdnaDto.setCess(ent.getCess() != null ? ent.getCess() : null);
				BigDecimal igst = ent.getIgst() != null ? ent.getIgst()
						: BigDecimal.ZERO;
				BigDecimal cgst = ent.getCgst() != null ? ent.getCgst()
						: BigDecimal.ZERO;
				BigDecimal sgst = ent.getSgst() != null ? ent.getSgst()
						: BigDecimal.ZERO;
				BigDecimal cess = ent.getCess() != null ? ent.getCess()
						: BigDecimal.ZERO;

				BigDecimal taxPayable = igst.add(cgst).add(sgst).add(cess);

				cdnaDto.setTaxPayable(taxPayable);

				cdnaDto.setEligibleItc(ent.getEligibleItc() != null
						? ent.getEligibleItc() : null);
				cdnaDto.setReason(
						ent.getReason() != null ? ent.getReason() : null);
				try {
					
					cdnaDto.setDocDate(ent.getNoteDate()!=null ? DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getNoteDate())) : DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getInvDate())));//--idt or ndt

					cdnaDto.setOriginalDocDate(ent.getOrigiNoteDate()!=null ? DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getOrigiNoteDate())) : DownloadReportsConstant.CSVCHARACTER
							.concat(EYDateUtil.fmtLocalDate(ent.getInvDate())));
					
					
				} catch (Exception e) {
				    if (LOGGER.isDebugEnabled()) {
				        LOGGER.debug("Exception occurred during date conversion: {}", e.getMessage(), e);
				    }
				}

				dtoList.add(cdnaDto);
			}

		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in getReportData method for report id : %d",
					id);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String errMsg = "Error Occured in DownloadGstr8AServiceImpl";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
		return dtoList;
	}

	/**
	 * 
	 */
	public String getDocType(String noteType) {

		if (Strings.isNullOrEmpty(noteType)) {
			return null;
		}

		String docType = null;
		if (noteType.equalsIgnoreCase("C"))
			docType = "CR";
		else
			docType = "DR";
		return docType;
	}

	private void deleteTempDir(File tempDir) {

		if (tempDir != null && tempDir.exists()) {
			try {
				FileUtils.deleteDirectory(tempDir);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempDir.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempDir.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}
	}
}
