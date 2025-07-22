package com.ey.advisory.app.services.search.filestatussearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.views.client.GSTR1EntityLevelSummaryDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.service.GstnApi;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Component("AsyncGstr1EntityReportDownloadServiceImpl")
@Slf4j
public class AsyncGstr1EntityReportDownloadServiceImpl
		implements AsyncReportDownloadService {

	public static final String b2b = "B2B(4,6B,6C)";
	public static final String b2ba = "B2B(Amendment)(9A)";
	public static final String b2cl = "B2CL(5)";
	public static final String b2cla = "B2CL(Amendment)(9A)";
	public static final String exp = "Exports(6A)";
	public static final String expa = "Exports(Amendment)(9A)";
	public static final String cdnr = "CDNR(9B)";
	public static final String cdnra = "CDNR(Amendment)(9C)";
	public static final String cdnur = "CDNUR(9B)";
	public static final String cdnura = "CDNUR(Amendment)(9C)";
	public static final String b2cs = "B2CS(7)";
	public static final String b2csa = "B2CS(Amendment)(10)";
	public static final String nilAsp = "NILEXTNON - As per DigiGST Computed";
	public static final String nilUi = "NILEXTNON - As per User Edited";
	public static final String at = "Advance Received(11A- Part I)";
	public static final String txpd = "Advance Adjusted(11B- Part I)";
	public static final String ata = "Advance Received(Amendment)(11A- Part II)";
	public static final String txpda = "Advance Adjusted(Amendment)(11B- Part II)";
	public static final String hsnAsp = "HSN - As per DigiGST Computed";
	public static final String hsnAspB2B = "HSN - As per DigiGST Computed - B2B";
	public static final String hsnAspB2C = "HSN - As per DigiGST Computed - B2C";
	public static final String hsnUi = "HSN - As per User Edited";
	public static final String hsnUiB2B = "HSN - As per User Edited - B2B";
	public static final String hsnUiB2C = "HSN - As per User Edited - B2C";
	public static final String tbl14ofOne = "Supplies  through e-Commerce (To be reported by Supplier) u/s 52";
	public static final String tbl14ofTwo = "Supplies  through e-Commerce (To be reported by Supplier) u/s 9(5)";
	public static final String tbl14AmdofOne = "Amendments of Supplies  through e-Commerce (To be reported by Supplier) u/s 52";
	public static final String tbl14AmdofTwo = "Amendments of Supplies  through e-Commerce (To be reported by Supplier) u/s 9(5)";
	public static final String tbl15Sec = "E-com - Supplies made through e-Commerce 15";
	public static final String tbl15AmdofOne = "E-com - Amendment of Supplies made through e-Commerce 15A(I)";
	public static final String tbl15AmdofTwo = "E-com - Amendment of Supplies made through e-Commerce 15A(II)";

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ProcessedReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;

	@Override
	public void generateReports(Long id) {

		Writer writer = null;
		File tempDir = null;

		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched file entity based on report ID : %s",
						entity.toString());
				LOGGER.debug(msg);
			}

			String reportType = entity.getReportType();
			String reportCateg = entity.getReportCateg();
			String dataType = entity.getDataType();
			String returnPerod = entity.getTaxPeriod();
			String derivedTaxPeriodFrom = entity.getDerivedRetPeriodFrom()
					.toString();
			String derivedTaxPeriodTo = entity.getDerivedRetPeriodFromTo()
					.toString();
			List<String> taxPeriods = deriveTaxPeriodsGivenFromAndToPeriod(
					derivedTaxPeriodFrom, derivedTaxPeriodTo);

			String gstins = GenUtil.convertClobtoString(entity.getGstins());
			String[] split = gstins.split(",");
			List<String> gstnList = new ArrayList<>();
			for (String gstn : split) {
				String gstn1 = gstn.replace(GSTConstants.SPE_CHAR, "");
				gstnList.add(gstn1);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("GSTIN Size {}", gstnList.size());
				LOGGER.debug(msg);
			}

			List<String> combinedList = gstnList.stream()
					.flatMap(gstin -> taxPeriods.stream()
							.map(taxPeriod -> gstin + "-" + taxPeriod))
					.collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Created COmbination Size {}",
						combinedList.size());
				LOGGER.debug(msg);
			}

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(LocalDateTime.now());

			String fullPath = null;
			
			if ("OUTWARD_1A".equalsIgnoreCase(dataType) || "OUTWARD-1A".equalsIgnoreCase(dataType))
			{
				 fullPath = tempDir.getAbsolutePath() + File.separator
							+ "GSTR1A" + "_" + "EntityLevelSummaryReport" + "_"
							+ timeMilli + ".csv";
			}
				
			else
			{
			 fullPath = tempDir.getAbsolutePath() + File.separator
					+ "GSTR1" + "_" + "EntityLevelSummaryReport" + "_"
					+ timeMilli + ".csv";
			}
		
			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			ReportConfig reportConfig = reportConfigFactory
					.getReportConfig(reportType, reportCateg, dataType);

			ReportConvertor reportConvertor = reportConfig.getReportConvertor();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped Reportconvertor based on report type and "
						+ "Report Category";
				LOGGER.debug(msg);
			}

			StatefulBeanToCsv<GSTR1EntityLevelSummaryDto> beanWriter = getBeanWriter(
					reportConfig, writer);

			WritetoCsv(combinedList, id, reportConvertor, reportType,
					beanWriter, reportCateg, returnPerod, dataType);

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
				zipFileName = combinAndZipReportFiles.zipfolder(tempDir,
						reportType, id);

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
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);
			} else {

				LOGGER.error("No Data found for report id : %d", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}
		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {

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

	private void WritetoCsv(List<String> combinedList, Long id,
			ReportConvertor reportConvertor, String reportType,
			StatefulBeanToCsv<GSTR1EntityLevelSummaryDto> beanWriter,
			String reportCateg, String taxPeriod, String dataType) {

		List<Object[]> list = null;
		List<GSTR1EntityLevelSummaryDto> responseFromDao = null;
		Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(taxPeriod);
		
		String procName = null;
		
		if ("OUTWARD_1A".equalsIgnoreCase(dataType) || "OUTWARD-1A".equalsIgnoreCase(dataType))
		{
			procName = "gstr1AEntitySummaryRespChunkData";
			
		}
		else
		{
		procName = "gstr1EntitySummaryRespChunkData";
		
		}
		try {

			StoredProcedureQuery reportDataProc = entityManager
					.createNamedStoredProcedureQuery(procName);

			reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			reportDataProc.setParameter("HSN_RATE", rateIncludedInHsn);

			list = reportDataProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Executed Stored proc to get ChunckData and "
								+ "got resultset of size: %d", list.size());
				LOGGER.debug(msg);
			}

			responseFromDao = list.stream()
					.map(o -> (GSTR1EntityLevelSummaryDto) reportConvertor
							.convert(o, reportType))
					.collect(Collectors.toCollection(ArrayList::new));
			List<GSTR1EntityLevelSummaryDto> setDefaultValues = setDefaultValues(
					responseFromDao, combinedList);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Record count after converting object array to DTO ",
						responseFromDao.size());
				LOGGER.debug(msg);
			}

			if (setDefaultValues != null && !setDefaultValues.isEmpty()) {
				beanWriter.write(setDefaultValues);
			}

		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private StatefulBeanToCsv<GSTR1EntityLevelSummaryDto> getBeanWriter(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<GSTR1EntityLevelSummaryDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(GSTR1EntityLevelSummaryDto.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<GSTR1EntityLevelSummaryDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<GSTR1EntityLevelSummaryDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	public List<GSTR1EntityLevelSummaryDto> setDefaultValues(
			List<GSTR1EntityLevelSummaryDto> list, List<String> combinedList) {

		Map<String, Map<String, List<GSTR1EntityLevelSummaryDto>>> groupByGstins = list
				.stream()
				.collect(Collectors.groupingBy(
						GSTR1EntityLevelSummaryDto::getGSTIN,
						Collectors.groupingBy(
								GSTR1EntityLevelSummaryDto::getTaxPeriod)));
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("groupByGstins - " + groupByGstins.toString());
		}
		List<GSTR1EntityLevelSummaryDto> sectionList = new ArrayList<>();
		combinedList.forEach(gstnTaxperiod -> {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstnTaxperiod - " + gstnTaxperiod);
			}
			String[] gstinTaxperiodArr = gstnTaxperiod.split("-");
			String gstn = null;
			String taxPeriod = null;
			if (gstinTaxperiodArr.length > 0) {
				gstn = gstinTaxperiodArr[0];
				taxPeriod = gstinTaxperiodArr[1];
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstn - taxPeriod " + gstn, taxPeriod);
			}
			Map<String, List<GSTR1EntityLevelSummaryDto>> taxperiodlevelMap = groupByGstins
					.get(gstn);
			if (LOGGER.isDebugEnabled()) {
				if (taxperiodlevelMap != null) {
					LOGGER.debug("taxperiodlevelMap - "
							+ taxperiodlevelMap.toString());
				}
			}
			List<GSTR1EntityLevelSummaryDto> list2 = null;
			if (taxperiodlevelMap != null) {
				list2 = taxperiodlevelMap.get(
						DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
			}
			if (LOGGER.isDebugEnabled()) {
				if (list2 != null) {
					LOGGER.debug("list2 - " + list2.toString());
				}

			}

			if (list2 == null || list2.isEmpty() || list2.size() == 0) {

				GSTR1EntityLevelSummaryDto b2bDefaultValue = gstr1b2bDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto b2baDefaultValue = gstr1b2baDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto b2clDefaultValue = gstr1b2clDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto b2claDefaultValue = gstr1b2claDefaultStructure(
						gstn, taxPeriod);

				GSTR1EntityLevelSummaryDto expDefaultValue = gstr1EXPDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto expaDefaultValue = gstr1EXPADefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto cdnrDefaultValue = gstr1CDNRDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto cdnraDefaultValue = gstr1CDNRADefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto cdnurDefaultValue = gstr1CDNURDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto cdnuraDefaultValue = gstr1CDNURADefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto b2csDefaultValue = gstr1B2CSDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto b2csaDefaultValue = gstr1B2CSADefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto aspNilDefaultValue = gstr1ASPNILDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto uiNilDefaultValue = gstr1UINILDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto atDefaultValue = gstr1ATDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto txpdDefaultValue = gstr1TXPDDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto ataDefaultValue = gstr1ATADefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto txpdaDefaultValue = gstr1TXPDADefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto hsnAspDefaultValue = gstr1ASPHSNDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto hsnAspB2BDefaultValue = gstr1ASPHSNB2BDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto hsnAspB2CDefaultValue = gstr1ASPHSNB2CDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto hsnUiDefaultValue = gstr1UIHSNDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto hsnUiB2BDefaultValue = gstr1UIHSNB2BDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto hsnUiB2CDefaultValue = gstr1UIHSNB2CDefaultStructure(
						gstn, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl14ofOneDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
						GSTConstants.GSTR1_14I, tbl14ofOne, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl14ofTwoDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
						GSTConstants.GSTR1_14II, tbl14ofTwo, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl14ofOneAmdDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
						GSTConstants.GSTR1_14AI, tbl14AmdofOne, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl14ofTwoAmdDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
						GSTConstants.GSTR1_14AII, tbl14AmdofTwo, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl15SecDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
						GSTConstants.GSTR1_15, tbl15Sec, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl15ofAmdOneDefaultValue = gstr1tbl14and15DefaultStructure(gstn, "15A(I)",
						tbl15AmdofOne, taxPeriod);
				GSTR1EntityLevelSummaryDto tbl15ofAmdTwoDefaultValue = gstr1tbl14and15DefaultStructure(gstn, "15A(II)",
						tbl15AmdofTwo, taxPeriod);

				sectionList.add(b2bDefaultValue);
				sectionList.add(b2baDefaultValue);
				sectionList.add(b2clDefaultValue);
				sectionList.add(b2claDefaultValue);
				sectionList.add(expDefaultValue);
				sectionList.add(expaDefaultValue);
				sectionList.add(cdnrDefaultValue);
				sectionList.add(cdnraDefaultValue);
				sectionList.add(cdnurDefaultValue);
				sectionList.add(cdnuraDefaultValue);
				sectionList.add(b2csDefaultValue);
				sectionList.add(b2csaDefaultValue);
				sectionList.add(aspNilDefaultValue);
				sectionList.add(uiNilDefaultValue);
				sectionList.add(atDefaultValue);
				sectionList.add(txpdDefaultValue);
				sectionList.add(ataDefaultValue);
				sectionList.add(txpdaDefaultValue);
				sectionList.add(hsnAspDefaultValue);
				sectionList.add(hsnAspB2BDefaultValue);
				sectionList.add(hsnAspB2CDefaultValue);
				sectionList.add(hsnUiDefaultValue);
				sectionList.add(hsnUiB2BDefaultValue);
				sectionList.add(hsnUiB2CDefaultValue);
				 sectionList.add(tbl14ofOneDefaultValue);
				 sectionList.add(tbl14ofTwoDefaultValue);
				 sectionList.add(tbl14ofOneAmdDefaultValue);
				 sectionList.add(tbl14ofTwoAmdDefaultValue);
				 sectionList.add(tbl15SecDefaultValue);
				 sectionList.add(tbl15ofAmdOneDefaultValue);
				 sectionList.add(tbl15ofAmdTwoDefaultValue);

			} else {
				Map<String, List<GSTR1EntityLevelSummaryDto>> groupByTableDesc = list2
						.stream().collect(Collectors.groupingBy(
								GSTR1EntityLevelSummaryDto::getTableDescription));

				List<GSTR1EntityLevelSummaryDto> B2Blist = groupByTableDesc
						.get(b2b);
				if (B2Blist == null || B2Blist.isEmpty()) {

					GSTR1EntityLevelSummaryDto b2bDefaultValue = gstr1b2bDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(b2bDefaultValue);
				} else {
					sectionList.addAll(B2Blist);
				}
				List<GSTR1EntityLevelSummaryDto> B2BAlist = groupByTableDesc
						.get(b2ba);
				if (B2BAlist == null || B2BAlist.isEmpty()) {

					GSTR1EntityLevelSummaryDto b2baDefaultValue = gstr1b2baDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(b2baDefaultValue);
				} else {
					sectionList.addAll(B2BAlist);
				}
				List<GSTR1EntityLevelSummaryDto> B2CLlist = groupByTableDesc
						.get(b2cl);
				if (B2CLlist == null || B2CLlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto b2clDefaultValue = gstr1b2clDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(b2clDefaultValue);
				} else {
					sectionList.addAll(B2CLlist);
				}
				List<GSTR1EntityLevelSummaryDto> B2CLAlist = groupByTableDesc
						.get(b2cla);
				if (B2CLAlist == null || B2CLAlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto b2claDefaultValue = gstr1b2claDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(b2claDefaultValue);
				} else {
					sectionList.addAll(B2CLAlist);
				}
				List<GSTR1EntityLevelSummaryDto> EXPlist = groupByTableDesc
						.get(exp);
				if (EXPlist == null || EXPlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto expDefaultValue = gstr1EXPDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(expDefaultValue);
				} else {
					sectionList.addAll(EXPlist);
				}
				List<GSTR1EntityLevelSummaryDto> EXPAlist = groupByTableDesc
						.get(expa);
				if (EXPAlist == null || EXPAlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto expaDefaultValue = gstr1EXPADefaultStructure(
							gstn, taxPeriod);
					sectionList.add(expaDefaultValue);
				} else {
					sectionList.addAll(EXPAlist);
				}

				List<GSTR1EntityLevelSummaryDto> CDNRlist = groupByTableDesc
						.get(cdnr);
				if (CDNRlist == null || CDNRlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto cdnrDefaultValue = gstr1CDNRDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(cdnrDefaultValue);
				} else {
					sectionList.addAll(CDNRlist);
				}
				List<GSTR1EntityLevelSummaryDto> CDNRAlist = groupByTableDesc
						.get(cdnra);
				if (CDNRAlist == null || CDNRAlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto cdnraDefaultValue = gstr1CDNRADefaultStructure(
							gstn, taxPeriod);
					sectionList.add(cdnraDefaultValue);
				} else {
					sectionList.addAll(CDNRAlist);
				}
				List<GSTR1EntityLevelSummaryDto> CDNURlist = groupByTableDesc
						.get(cdnur);
				if (CDNURlist == null || CDNURlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto cdnurDefaultValue = gstr1CDNURDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(cdnurDefaultValue);
				} else {
					sectionList.addAll(CDNURlist);
				}
				List<GSTR1EntityLevelSummaryDto> CDNURAlist = groupByTableDesc
						.get(cdnura);
				if (CDNURAlist == null || CDNURAlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto cdnuraDefaultValue = gstr1CDNURADefaultStructure(
							gstn, taxPeriod);
					sectionList.add(cdnuraDefaultValue);
				} else {
					sectionList.addAll(CDNURAlist);
				}
				List<GSTR1EntityLevelSummaryDto> B2CSlist = groupByTableDesc
						.get(b2cs);
				if (B2CSlist == null || B2CSlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto b2csDefaultValue = gstr1B2CSDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(b2csDefaultValue);
				} else {
					sectionList.addAll(B2CSlist);
				}

				List<GSTR1EntityLevelSummaryDto> B2CSAlist = groupByTableDesc
						.get(b2csa);
				if (B2CSAlist == null || B2CSAlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto b2csaDefaultValue = gstr1B2CSADefaultStructure(
							gstn, taxPeriod);
					sectionList.add(b2csaDefaultValue);
				} else {
					sectionList.addAll(B2CSAlist);
				}

				List<GSTR1EntityLevelSummaryDto> ASP_NILlist = groupByTableDesc
						.get(nilAsp);
				if (ASP_NILlist == null || ASP_NILlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto aspNilDefaultValue = gstr1ASPNILDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(aspNilDefaultValue);
				} else {
					sectionList.addAll(ASP_NILlist);
				}
				List<GSTR1EntityLevelSummaryDto> UI_NILlist = groupByTableDesc
						.get(nilUi);
				if (UI_NILlist == null || UI_NILlist.isEmpty()) {
					GSTR1EntityLevelSummaryDto uiNilDefaultValue = gstr1UINILDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(uiNilDefaultValue);
				} else {
					sectionList.addAll(UI_NILlist);
				}
				List<GSTR1EntityLevelSummaryDto> atList = groupByTableDesc
						.get(at);
				if (atList == null || atList.isEmpty()) {
					GSTR1EntityLevelSummaryDto atDefaultValue = gstr1ATDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(atDefaultValue);
				} else {
					sectionList.addAll(atList);
				}
				List<GSTR1EntityLevelSummaryDto> txpdList = groupByTableDesc
						.get(txpd);
				if (txpdList == null || txpdList.isEmpty()) {
					GSTR1EntityLevelSummaryDto txpdDefaultValue = gstr1TXPDDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(txpdDefaultValue);
				} else {
					sectionList.addAll(txpdList);
				}

				List<GSTR1EntityLevelSummaryDto> ataList = groupByTableDesc
						.get(ata);
				if (ataList == null || ataList.isEmpty()) {
					GSTR1EntityLevelSummaryDto ataDefaultValue = gstr1ATADefaultStructure(
							gstn, taxPeriod);
					sectionList.add(ataDefaultValue);
				} else {
					sectionList.addAll(ataList);
				}
				List<GSTR1EntityLevelSummaryDto> txpdaList = groupByTableDesc
						.get(txpda);
				if (txpdaList == null || txpdaList.isEmpty()) {
					GSTR1EntityLevelSummaryDto txpdaDefaultValue = gstr1TXPDADefaultStructure(
							gstn, taxPeriod);
					sectionList.add(txpdaDefaultValue);
				} else {
					sectionList.addAll(txpdaList);
				}

				List<GSTR1EntityLevelSummaryDto> hsnAspList = groupByTableDesc
						.get(hsnAsp);
				if (hsnAspList == null || hsnAspList.isEmpty()) {
					GSTR1EntityLevelSummaryDto hsnAspDefaultValue = gstr1ASPHSNDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(hsnAspDefaultValue);
				} else {
					sectionList.addAll(hsnAspList);
				}
				
				List<GSTR1EntityLevelSummaryDto> hsnAspListB2B = groupByTableDesc
						.get(hsnAspB2B);
				if (hsnAspListB2B == null || hsnAspListB2B.isEmpty()) {
					GSTR1EntityLevelSummaryDto hsnAspB2BDefaultValue = gstr1ASPHSNB2BDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(hsnAspB2BDefaultValue);
				} else {
					sectionList.addAll(hsnAspListB2B);
				}
				
				List<GSTR1EntityLevelSummaryDto> hsnAspListB2C = groupByTableDesc
						.get(hsnAspB2C);
				if (hsnAspListB2C == null || hsnAspListB2C.isEmpty()) {
					GSTR1EntityLevelSummaryDto hsnAspB2CDefaultValue = gstr1ASPHSNB2CDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(hsnAspB2CDefaultValue);
				} else {
					sectionList.addAll(hsnAspListB2C);
				}

			   List<GSTR1EntityLevelSummaryDto> hsnUiList = groupByTableDesc
						.get(hsnUi);
				if (hsnUiList == null || hsnUiList.isEmpty()) {
					GSTR1EntityLevelSummaryDto hsnUiDefaultValue = gstr1UIHSNDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(hsnUiDefaultValue);
				} else {
					sectionList.addAll(hsnUiList);
				}
				
				List<GSTR1EntityLevelSummaryDto> hsnUiListB2B = groupByTableDesc
						.get(hsnUiB2B);
				if (hsnUiListB2B == null || hsnUiListB2B.isEmpty()) {
					GSTR1EntityLevelSummaryDto hsnUiB2BDefaultValue = gstr1UIHSNB2BDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(hsnUiB2BDefaultValue);
				} else {
					sectionList.addAll(hsnUiListB2B);
				}
				
				List<GSTR1EntityLevelSummaryDto> hsnUiListB2C = groupByTableDesc
						.get(hsnUiB2C);
				if (hsnUiListB2C == null || hsnUiListB2C.isEmpty()) {
					GSTR1EntityLevelSummaryDto hsnUiB2CDefaultValue = gstr1UIHSNB2CDefaultStructure(
							gstn, taxPeriod);
					sectionList.add(hsnUiB2CDefaultValue);
				} else {
					sectionList.addAll(hsnUiListB2C);
				}

				List<GSTR1EntityLevelSummaryDto> tbl14ofOneList = groupByTableDesc.get(tbl14ofOne);
				if (tbl14ofOneList == null || tbl14ofOneList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl14OfOneDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							GSTConstants.GSTR1_14I, tbl14ofOne, taxPeriod);
					sectionList.add(tbl14OfOneDefaultValue);
				} else {
					sectionList.addAll(tbl14ofOneList);
				}

				List<GSTR1EntityLevelSummaryDto> tbl14ofTwoList = groupByTableDesc.get(tbl14ofTwo);
				if (tbl14ofTwoList == null || tbl14ofTwoList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl14OfTwoDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							GSTConstants.GSTR1_14II, tbl14ofTwo, taxPeriod);
					sectionList.add(tbl14OfTwoDefaultValue);
				} else {
					sectionList.addAll(tbl14ofTwoList);
				}

				List<GSTR1EntityLevelSummaryDto> tbl14AmdofOneList = groupByTableDesc.get(tbl14AmdofOne);
				if (tbl14AmdofOneList == null || tbl14AmdofOneList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl14OfOneAmdDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							GSTConstants.GSTR1_14AI, tbl14AmdofOne, taxPeriod);
					sectionList.add(tbl14OfOneAmdDefaultValue);
				} else {
					sectionList.addAll(tbl14AmdofOneList);
				}

				List<GSTR1EntityLevelSummaryDto> tbl14AmdofTwoList = groupByTableDesc.get(tbl14AmdofTwo);
				if (tbl14AmdofTwoList == null || tbl14AmdofTwoList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl14OfTwoAmdDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							GSTConstants.GSTR1_14AII, tbl14AmdofTwo, taxPeriod);
					sectionList.add(tbl14OfTwoAmdDefaultValue);
				} else {
					sectionList.addAll(tbl14AmdofTwoList);
				}

				List<GSTR1EntityLevelSummaryDto> tbl15SecList = groupByTableDesc.get(tbl15Sec);
				if (tbl15SecList == null || tbl15SecList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl15SecDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							GSTConstants.GSTR1_15, tbl15Sec, taxPeriod);
					sectionList.add(tbl15SecDefaultValue);
				} else {
					sectionList.addAll(tbl15SecList);
				}

				List<GSTR1EntityLevelSummaryDto> tbl15AmdofOneList = groupByTableDesc.get(tbl15AmdofOne);
				if (tbl15AmdofOneList == null || tbl15AmdofOneList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl15OfOneAmdDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							"15A(I)", tbl15AmdofOne, taxPeriod);
					sectionList.add(tbl15OfOneAmdDefaultValue);
				} else {
					sectionList.addAll(tbl15AmdofOneList);
				}

				List<GSTR1EntityLevelSummaryDto> tbl15AmdofTwoList = groupByTableDesc.get(tbl15AmdofTwo);
				if (tbl15AmdofTwoList == null || tbl15AmdofTwoList.isEmpty()) {
					GSTR1EntityLevelSummaryDto tbl15ofAmdTwoDefaultValue = gstr1tbl14and15DefaultStructure(gstn,
							"15A(II)", tbl15AmdofTwo, taxPeriod);
					sectionList.add(tbl15ofAmdTwoDefaultValue);
				} else {
					sectionList.addAll(tbl15AmdofTwoList);
				}
			}
		});
		return sectionList;

	}

	public GSTR1EntityLevelSummaryDto gstr1b2bDefaultStructure(String gstn,
			String taxPeriod) {

		GSTR1EntityLevelSummaryDto b2b1 = new GSTR1EntityLevelSummaryDto();
		b2b1.setGSTIN(gstn);
		b2b1.setTableNo("4,6B,6C");
		b2b1.setTableDescription(b2b);
		b2b1.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b1.setAspCount(0);
		b2b1.setAspInvoiceValue(BigDecimal.ZERO);
		b2b1.setAspTaxableValue(BigDecimal.ZERO);
		b2b1.setAspIGST(BigDecimal.ZERO);
		b2b1.setAspCGST(BigDecimal.ZERO);
		b2b1.setAspSGST(BigDecimal.ZERO);
		b2b1.setAspCess(BigDecimal.ZERO);
		b2b1.setAvailableCount(0);
		b2b1.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b1.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b1.setAvailableIGST(BigDecimal.ZERO);
		b2b1.setAvailableCGST(BigDecimal.ZERO);
		b2b1.setAvailableSGST(BigDecimal.ZERO);
		b2b1.setAvailableCess(BigDecimal.ZERO);
		b2b1.setDiffCount(0);
		b2b1.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b1.setDiffTaxableValue(BigDecimal.ZERO);
		b2b1.setDiffIGST(BigDecimal.ZERO);
		b2b1.setDiffCGST(BigDecimal.ZERO);
		b2b1.setAspSGST(BigDecimal.ZERO);
		b2b1.setDiffCess(BigDecimal.ZERO);

		return b2b1;

	}

	public GSTR1EntityLevelSummaryDto gstr1b2baDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9A");
		b2b.setTableDescription(b2ba);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1b2clDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("5A");
		b2b.setTableDescription(b2cl);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1b2claDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9A");
		b2b.setTableDescription(b2cla);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1EXPDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("6A");
		b2b.setTableDescription(exp);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1EXPADefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9A");
		b2b.setTableDescription(expa);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1CDNRDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9B");
		b2b.setTableDescription(cdnr);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1CDNRADefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9C");
		b2b.setTableDescription(cdnra);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1CDNURDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9B");
		b2b.setTableDescription(cdnur);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1CDNURADefaultStructure(String gstn,
			String taxPeriod) {

		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("9C");
		b2b.setTableDescription(cdnura);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1B2CSDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("7");
		b2b.setTableDescription(b2cs);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1B2CSADefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("10");
		b2b.setTableDescription(b2csa);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1ASPNILDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("8");
		b2b.setTableDescription(nilAsp);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1UINILDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("8");
		b2b.setTableDescription(nilUi);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1ATDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("11-P1-A1,11-P1-A2");
		b2b.setTableDescription(at);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1TXPDDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("11-P1-B1,11-P1-B2");
		b2b.setTableDescription(txpd);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1ATADefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("11-P2-A1,11-P2-A2");
		b2b.setTableDescription(ata);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1TXPDADefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("11-P2-B1,11-P2-B2");
		b2b.setTableDescription(txpda);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1ASPHSNDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("12");
		b2b.setTableDescription(hsnAsp);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}
	
	public GSTR1EntityLevelSummaryDto gstr1ASPHSNB2BDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("12");
		b2b.setTableDescription(hsnAspB2B);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}
	
	public GSTR1EntityLevelSummaryDto gstr1ASPHSNB2CDefaultStructure(String gstn,
			String taxPeriod) {
		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("12");
		b2b.setTableDescription(hsnAspB2C);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1UIHSNDefaultStructure(String gstn,
			String taxPeriod) {

		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("12");
		b2b.setTableDescription(hsnUi);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}
	
	public GSTR1EntityLevelSummaryDto gstr1UIHSNB2BDefaultStructure(String gstn,
			String taxPeriod) {

		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("12");
		b2b.setTableDescription(hsnUiB2B);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}
	
	public GSTR1EntityLevelSummaryDto gstr1UIHSNB2CDefaultStructure(String gstn,
			String taxPeriod) {

		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo("12");
		b2b.setTableDescription(hsnUiB2C);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public GSTR1EntityLevelSummaryDto gstr1tbl14and15DefaultStructure(
			String gstn, String tableNo, String tableDesc, String taxPeriod) {

		GSTR1EntityLevelSummaryDto b2b = new GSTR1EntityLevelSummaryDto();
		b2b.setGSTIN(gstn);
		b2b.setTableNo(tableNo);
		b2b.setTableDescription(tableDesc);
		b2b.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		b2b.setAspCount(0);
		b2b.setAspInvoiceValue(BigDecimal.ZERO);
		b2b.setAspTaxableValue(BigDecimal.ZERO);
		b2b.setAspIGST(BigDecimal.ZERO);
		b2b.setAspCGST(BigDecimal.ZERO);
		b2b.setAspSGST(BigDecimal.ZERO);
		b2b.setAspCess(BigDecimal.ZERO);
		b2b.setAvailableCount(0);
		b2b.setAvailableInvoiceValue(BigDecimal.ZERO);
		b2b.setAvailableTaxableValue(BigDecimal.ZERO);
		b2b.setAvailableIGST(BigDecimal.ZERO);
		b2b.setAvailableCGST(BigDecimal.ZERO);
		b2b.setAvailableSGST(BigDecimal.ZERO);
		b2b.setAvailableCess(BigDecimal.ZERO);
		b2b.setDiffCount(0);
		b2b.setDiffInvoiceValue(BigDecimal.ZERO);
		b2b.setDiffTaxableValue(BigDecimal.ZERO);
		b2b.setDiffIGST(BigDecimal.ZERO);
		b2b.setDiffCGST(BigDecimal.ZERO);
		b2b.setDiffSGST(BigDecimal.ZERO);
		b2b.setDiffCess(BigDecimal.ZERO);

		return b2b;

	}

	public static List<String> deriveTaxPeriodsGivenFromAndToPeriod(
			String fromPeriod, String toPeriod) {
		List<String> taxPeriods = new ArrayList<>();

		// Parse input strings to YearMonth
		YearMonth fromYearMonth = YearMonth.parse(fromPeriod,
				DateTimeFormatter.ofPattern("yyyyMM"));
		YearMonth toYearMonth = YearMonth.parse(toPeriod,
				DateTimeFormatter.ofPattern("yyyyMM"));

		// Iterate through the range and add each tax period to the list
		while (!fromYearMonth.isAfter(toYearMonth)) {
			taxPeriods.add(fromYearMonth
					.format(DateTimeFormatter.ofPattern("MMyyyy")));
			fromYearMonth = fromYearMonth.plusMonths(1);
		}

		return taxPeriods;
	}

}
