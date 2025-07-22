package com.ey.advisory.common.zip;

import java.util.List;

/**
 * 
 * The generic class for compressing the list of files and dumping the 
 * compressed file to the specified output directory. 
 * 
 * @author Sai.Pakanati
 *
 */
public interface DirAndFilesCompressor {
	
	public void compressFiles(
			String outputDir, String compressedFileName, 
			List<String> filePaths);
	
}
