package com.ey.advisory.app.glrecon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.zip.DirAndFilesCompressor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * This default GLRecon upload represent service for uploading the files from
 * various sources reading the data from document and it will store or sent
 * excel file to Document Service
 */
@Component("GlReconProcessReportServiceImpl")
@Slf4j
public class GlReconProcessReportServiceImpl
		implements GlReconProcessReportService {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;

	@Autowired
	@Qualifier("GLReconSFTPFileTransferServiceImpl")
	private GLReconSFTPFileTransferService sftpService;
	
	@Autowired
	@Qualifier("DirAndFilesCompressorImpl")
	private DirAndFilesCompressor compressor;
	

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final String GL_RECON_SFTP_SOURCE = "SFTP_ROOT/INTG_OUTBOUND/GLRECON/";

	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	private static final List<String> FILE_EXT = ImmutableList
			.of(JobStatusConstants.XLSX_TYPE, JobStatusConstants.CSV_TYPE);

	@Override
	public String processReconFiles(String groupCode, Long configId) {
		File tempDir = null;
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName()!=null?user.getUserPrincipalName():null;
		try {
			
			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s", tempDir);
				LOGGER.debug(msg);
			}
			
			sftpService.downloadFiles(tempDir.getAbsolutePath(),
					GL_RECON_SFTP_SOURCE + configId.toString() + "/", FILE_EXT);

			if (tempDir.list().length == 0) {
				String errMsg = String.format("Temp Directory  %s is empty",
						tempDir);
				LOGGER.error(errMsg);
				GenUtil.deleteTempDir(tempDir);
				return null;
			}

			File[] downloadedFiles = tempDir.listFiles();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Total No of files downloaded to remote directory is {}",
						downloadedFiles.length);

			List<String> uploadedFiles = new ArrayList<>();
			int fileIndex = 1;
			for (File file : downloadedFiles) {

				if (LOGGER.isDebugEnabled())
					LOGGER.debug("index {}", fileIndex);

				/*
				 * LocalDateTime now = LocalDateTime.now(); String timeMilli =
				 * DATE_TIME_FORMAT.format(now);
				 * 
				 * String fileNameToBeUploaded = "GL_RECON_REPORT" + "_" +
				 * configId.toString() + "_" + timeMilli + "_" +
				 * fileIndex+".csv";
				 */
				/*
				 * Pair<String, String> uploadFileWithFileName = DocumentUtility
				 * .uploadFile(file,"GLReconWebUploads");
				 */
				// uploadedFilesDocIds.add(uploadFileWithFileName.getValue1());
				uploadedFiles.add(file.getPath());

				++fileIndex;
			}

			LOGGER.debug("uploadedFiles {} ", uploadedFiles.size());

			String compressedFileName = "GL_RECON_REPORT" + "_" + configId
					+ ".zip";

			compressor.compressFiles(tempDir.getAbsolutePath(),
					compressedFileName, uploadedFiles);

			String zipPath = tempDir.getAbsolutePath() + File.separator
					+ compressedFileName;

			File zipFile = new File(zipPath);

			// Pair<String, String> zipFilePathDocId =
			// zipFiles(uploadedFilesDocIds, configId);

			// save the file

			Pair<String, String> zipFilePathDocId = DocumentUtility
					.uploadFile(zipFile, "GLReconWebUploads");

			String filePath = zipFilePathDocId.getValue0();
			String documentId = zipFilePathDocId.getValue1();

			LOGGER.debug("File uploaded successfully. FilePath: {}, DocumentId: {}", filePath, documentId);

			

			glReconReportConfig.updateReconConfigStatusAndReportName(
			    "REPORT_GENERATION_IN_PROGRESS", 
			    filePath, 
			    documentId, 
			    LocalDateTime.now(), 
			    configId);
			
			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("reconId", configId);

			
			asyncJobsService.createJob(groupCode,
					JobConstants.GL_PROCESSED_RECON_FILE, jsonParams.toString(),
					userName, 1L, null, null);


		} catch (Exception ex) {
			LOGGER.error("Error occured while downloading the ");
			throw new AppException(ex);

		}finally
		{
			GenUtil.deleteTempDir(tempDir);
		}
		return "SUCCESS";
	}

	private static File createTempDir() throws IOException {
		String tempFolderPrefix = "GL_RECON";
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}
	
	
	/*public static void main(String[] args) {
		File tempDir = null;
		try {
		tempDir = createTempDir();
		File fil = new File("C:/Users/AZ956YX/OneDrive - EY/Desktop/readings/GL_RECON/Business_unit.xlsx");
		byte[] fileContent = Files.readAllBytes(fil.toPath());
		InputStream stream = new ByteArrayInputStream(fileContent);
		FileUtils.copyInputStreamToFile(
				stream, new File(tempDir.getAbsolutePath()+"/GL1.csv"));
		File[] downloadedFiles = tempDir.listFiles();
		List<String> filenames =  new ArrayList<>();
		for(File fil1 : downloadedFiles)
		{
			filenames.add(fil1.getPath());
		}
		String compressedFileName = "GLRECON_REPORT" + "_" + "234" + ".zip";
		
		combineAndZipCsvFiles.zipfolder(
				Long.parseLong(String.valueOf(1)), tempDir,
				String.valueOf(234), "Inward E-invoice_JSONs", "");
		
		compressor.compressFiles(tempDir.getAbsolutePath(), compressedFileName,
				filenames);

		String zipPath = tempDir.getAbsolutePath() + File.separator
				+ compressedFileName;
		
		File zipFile = new File(zipPath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			GenUtil.deleteTempDir(tempDir);
			e.printStackTrace();
			
		}
		
*/		
		
	

	/*public static void compressFiles(
			String outputDir, String compressedFileName, 
			List<String> inputFiles)
	{
	// Check if each of the files in the specified file paths exist, 
	// Otherwise throw an exception.
	List<String> filePaths = validateInputs(outputDir, inputFiles);
    
	// Create the full output file path.
	String outFilePath = outputDir + File.separator + 
				compressedFileName;
	
	byte[] buffer = new byte[1024];
			
	try(FileOutputStream fos = new FileOutputStream(outFilePath); 
			ZipOutputStream zos = new ZipOutputStream(fos);) {
		
		// Iterate over the specified individual files and write them
		// the zip output stream.
        for (String aFile : filePaths) {
        	
        	File srcFile = new File(aFile);
     
        	// Open an input stream to the file to be read for zipping.
        	FileInputStream fis = new FileInputStream(srcFile);
            zos.putNextEntry(new ZipEntry(srcFile.getName()));                
            int length;
            while((length = fis.read(buffer)) > 0) {
            	zos.write(buffer, 0, length);
            }
            
            // Close the file entry within the zip output stream.
            zos.closeEntry(); 
            fis.close(); // Close the intermediate zip file stream.
        }		
    } catch(IOException ex) {
    	String msg = String.format(
    			"IO Error while creating the compressed file '%s'",
    			outFilePath);
    	LOGGER.error(msg);
    	throw new AppException(msg);
    } 
	}*/
	
}