package com.ey.advisory.common.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

@Component("DirAndFilesCompressorImpl")
public class DirAndFilesCompressorImpl implements DirAndFilesCompressor {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(DirAndFilesCompressorImpl.class);

	private static final int ZIP_BUFFER_SIZE = 1024;
	
	@Override
	public void compressFiles(String outputDir, 
			String compressedFileName, List<String> inputFiles) {// Check if each of the files in the specified file paths exist, 
		// Otherwise throw an exception.
		List<String> filePaths = validateInputs(outputDir, inputFiles);

		// Create the full output file path.
		String outFilePath = outputDir + File.separator + compressedFileName;

		byte[] buffer = new byte[ZIP_BUFFER_SIZE];

		try (FileOutputStream fos = new FileOutputStream(outFilePath);
		     ZipOutputStream zos = new ZipOutputStream(fos)) {

		    // Iterate over the specified individual files and write them to the zip output stream.
		    for (String aFile : filePaths) {
		        File srcFile = new File(aFile);

		        // Use try-with-resources for FileInputStream to ensure it is always closed.
		        try (FileInputStream fis = new FileInputStream(srcFile)) {
		            zos.putNextEntry(new ZipEntry(srcFile.getName()));
		            int length;
		            while ((length = fis.read(buffer)) > 0) {
		                zos.write(buffer, 0, length);
		            }
		            // Close the file entry within the zip output stream.
		            zos.closeEntry();
		        }
		    }
		} catch (IOException ex) {
		    String msg = String.format(
		            "IO Error while creating the compressed file '%s'",
		            outFilePath);
		    LOGGER.error(msg, ex);
		    throw new AppException(msg, ex);
		}}
	
	/**
	 * Filters the input list for valid paths.
	 * 
	 * @param outputDir
	 * @param filePaths
	 * @return
	 */
	private List<String> 
			validateInputs(String outputDir, List<String> filePaths) {
		
		// check if the output directory exists. Otherwise throw an exception.
		File outDir = new File(outputDir);
		if(!outDir.exists() || !outDir.isDirectory()) {
			String msg = String.format("The output dir '%s' does not exist "
					+ "OR is not a directory", outputDir);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		
		if(filePaths.isEmpty()) {
			String msg = "One or more files are required as input for "
					+ "compressing";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		
		// Get all the valid files within the list of input files.
		List<String> validFilePaths = filePaths.stream()
			.map(filePath -> new File(filePath))
			.filter(file -> file.exists() && file.isFile())
			.map(file -> file.getPath())
			.collect(Collectors.toCollection(ArrayList::new));
		
		// Print an error to display number of invalid files.
		if(validFilePaths.size() != filePaths.size()) {	
			String msg = String.format("%d file(s) specified as input for "
					+ "creating the zip do(es) not exist.", 
					filePaths.size() - validFilePaths.size());	
			LOGGER.warn(msg);
			
		}
		
		return validFilePaths;
	}
	

}
