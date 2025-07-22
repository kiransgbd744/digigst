package com.ey.advisory.app.util;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component
@Slf4j
public class CommonDocumentUtility {

	public final static String EXCEP_ERROR = "Excep_Error";


	public final static String ERROR = "Error";



	public static File createDownloadDir(File tempDir) throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Creating downloaded directory to download generated csv file for merging");
		}

		File downloadDir = new File(
				tempDir.getAbsolutePath() + File.separator + "DownloadDir");
		downloadDir.mkdirs();

		return downloadDir;
	}

}
