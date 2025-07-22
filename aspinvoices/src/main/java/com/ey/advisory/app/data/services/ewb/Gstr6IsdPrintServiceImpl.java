/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.Gstr6DistributionDto;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@Service("Gstr6IsdPrintServiceImpl")
public class Gstr6IsdPrintServiceImpl implements Gstr6IsdPdfService {

	@Autowired
	@Qualifier("Gstr6DistributionPDFDaoImpl")
	private Gstr6DistributionPDFDaoImpl gstr6PDFDaoImpl;

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository gstr6DistributionRepository;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	public String getGstr6PdfPrint(List<Gstr6DistributionDto> reqDto,
			Long entityId) throws IOException

	{
		String doctype = null;
		String sgstin = null;
		String id = null;

		String fileName = null;
		JasperPrint jasperPrint = null;
		String zipFileName = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside gstr6ServiceImpl having reqDto as  {} for GSTR6 distribution",
					reqDto.toString());
		}
		File tempDir = createTempDir();
		try {
			
			for (Gstr6DistributionDto req : reqDto) {

				doctype = req.getDocumentType();
				sgstin = req.getSgstin();
				id = req.getId();

				Optional<Gstr6DistributionEntity> gstr6distributionList = gstr6DistributionRepository
						.findById(Long.valueOf(id));
			
				
				Gstr6DistributionEntity gstr6Entity = gstr6distributionList
						.get();
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("optional gstr6 distributionList {}",
							gstr6Entity);
				}
				String docNum = gstr6Entity.getDocNum();
				docNum = docNum.replaceAll("/", "");

				fileName = sgstin + "_" + docNum;

				String fullPath = tempDir.getAbsolutePath() + File.separator
						+ fileName + ".pdf";

				jasperPrint = gstr6PDFDaoImpl
						.isdTaxInvoicePdfReport(id, doctype, sgstin);

				BufferedOutputStream outStream = new BufferedOutputStream(
						new FileOutputStream(fullPath), 8192);
				JasperExportManager.exportReportToPdfStream(jasperPrint,
						outStream);

				outStream.flush();
				outStream.close();

			}
			zipFileName = zipEinvoicePdfFiles(tempDir, Long.valueOf(entityId));
			File zipFile = new File(tempDir, zipFileName);

			/*String uploadedFileName = DocumentUtility.uploadZipFile(zipFile,
					"PdfGstr6Reports");*/
			
			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(zipFile, "PdfGstr6Reports");
			String uploadedFileName = uploadedDocName.getValue0();
			String docId = uploadedDocName.getValue1();

			downloadRepository.updateStatus(entityId, "REPORT_GENERATED",
					uploadedFileName, LocalDateTime.now(),docId);
			
			return "SUCCESS";
			
		} catch (Exception ex) {
			LOGGER.error("Exception occured in GSTR6 service impl" + ex);
			throw new AppException(ex);
		}finally 
		{
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);

		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfGstr6Reports").toFile();
	}

	private String zipEinvoicePdfFiles(File tempDir, Long id) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "GSTR6_ISD_Invoice" + id;
		String compressedFileName = fileName;

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
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".pdf");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
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
