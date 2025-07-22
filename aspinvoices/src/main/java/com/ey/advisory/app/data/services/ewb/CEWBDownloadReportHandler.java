/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportChildRequest;
import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportRequest;
import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportResponse;
import com.ey.advisory.common.CombineAndZipXlsxFiles;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("CEWBDownloadReportHandler")
@Slf4j
public class CEWBDownloadReportHandler {

	@Autowired
	@Qualifier("CEWBDownloadReportServiceImpl")
	private CEWBDownloadReportService cewbDownloadReportService;

	@Autowired
	@Qualifier("CEWBDownloadReportDaoImpl")
	private CEWBDownloadReportDao cewbDownloadReportDao;

	public String generateCEWBCsvZip(File tempDir,
			CEWBDownloadReportRequest request) throws Exception {

		String fileName = null;
		String fullPath = null;
		List<CEWBDownloadReportResponse> childResponseList = new ArrayList<>();
		List<CEWBDownloadReportResponse> processResponce = new ArrayList<>();
		List<CEWBDownloadReportResponse> errorResponce = new ArrayList<>();

		List<CEWBDownloadReportResponse> response = cewbDownloadReportDao
				.getCEWBCsvReports(request);

		List<CEWBDownloadReportChildRequest> childRequest = request.getItems();
		if (!childRequest.isEmpty()) {
			for (CEWBDownloadReportChildRequest childReq : childRequest) {
				Long fileId = childReq.getFileId();
				Long serialNo = childReq.getSerialNo();

				Predicate<CEWBDownloadReportResponse> serialNumFilter = (
						dto) -> (dto.getSerialNo().equals(serialNo));
				Predicate<CEWBDownloadReportResponse> fileIdFilter = (
						dto) -> (dto.getFileId().equals(fileId));

				List<CEWBDownloadReportResponse> childResp = response.stream()
						.filter(serialNumFilter).filter(fileIdFilter)
						.collect(Collectors.toList());

				childResponseList.addAll(childResp);
			}
			processResponce = childResponseList.stream()
					.filter(dto -> dto.getCewbNum() != null)
					.sorted(Comparator
							.comparing(CEWBDownloadReportResponse::getFileId)
							.reversed())
					.collect(Collectors.toList());

			errorResponce = childResponseList.stream()
					.filter(dto -> dto.getErrorCode() != null)
					.sorted(Comparator
							.comparing(CEWBDownloadReportResponse::getFileId)
							.reversed())
					.collect(Collectors.toList());

		} else {
			processResponce = response.stream()
					.filter(dto -> dto.getCewbNum() != null)
					.sorted(Comparator
							.comparing(CEWBDownloadReportResponse::getFileId)
							.reversed())
					.collect(Collectors.toList());

			errorResponce = response.stream()
					.filter(dto -> dto.getErrorCode() != null)
					.sorted(Comparator
							.comparing(CEWBDownloadReportResponse::getFileId)
							.reversed())
					.collect(Collectors.toList());
		}

		try {
			if (processResponce != null && !processResponce.isEmpty()) {
				fileName = "CEWBProcessedData";
				fullPath = tempDir.getAbsolutePath() + File.separator + fileName
						+ ".csv";

				cewbDownloadReportService.generateCsvForProcessedCEWB(fullPath,
						processResponce);
			}
			if (errorResponce != null && !errorResponce.isEmpty()) {
				fileName = "CEWBErrorData";
				fullPath = tempDir.getAbsolutePath() + File.separator + fileName
						+ ".csv";

				cewbDownloadReportService.generateCsvForErrorCEWB(fullPath,
						errorResponce);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String zipFileName = zipEinvoicePdfFiles(tempDir);
		return zipFileName;
	}

	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String compressedFileName = "CEWBCsvDownloadReport";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter csvFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		};
		File[] files = tmpDir.listFiles(csvFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		// Return the list of files.
		return retFileNames;
	}

}
