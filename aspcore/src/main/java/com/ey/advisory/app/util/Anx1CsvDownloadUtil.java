/**
 * 
 */
package com.ey.advisory.app.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Anx1CsvDownloadUtil")
@Slf4j
public class Anx1CsvDownloadUtil {
	
	public void deleteTemporaryDirectory(File tempFile) {

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


}
