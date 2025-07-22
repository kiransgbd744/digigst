/**
 * 
 */
package com.ey.advisory.common;

import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
public class DashboardCommonUtility {

	public static String getDashboardFolderName(String returnType,
			String fileType) {

		String folderName = null;

		if (JobStatusConstants.JSON_FILE.equalsIgnoreCase(fileType)) {
			if ("GSTR1".equalsIgnoreCase(returnType))
				folderName = "GSTR1GetJsons";
			else if ("GSTR2A".equalsIgnoreCase(returnType))
				folderName = "GSTR2AGetJsons";
			else if ("GSTR3B".equalsIgnoreCase(returnType))
				folderName = "GSTR3BGetJsons";
			else if ("GSTR9".equalsIgnoreCase(returnType))
				folderName = "GSTR9GetJsons";
			else if ("ITC04".equalsIgnoreCase(returnType))
					folderName = "ITC04GetJsons";
			else if ("GSTR6".equalsIgnoreCase(returnType))
				folderName = "GSTR6GetJsons";
			else if ("GSTR2X".equalsIgnoreCase(returnType))
				folderName = "GSTR2XGetJsons";
			else if ("GSTR7".equalsIgnoreCase(returnType))
				folderName = "GSTR7GetJsons";
			else if ("GSTR8".equalsIgnoreCase(returnType))
				folderName = "GSTR8GetJsons";
			else if ("GSTR1A".equalsIgnoreCase(returnType))
				folderName = "GSTR1AGetJsons";
		} else if (JobStatusConstants.CSV_FILE.equalsIgnoreCase(fileType)) {
			if ("GSTR1".equalsIgnoreCase(returnType))
				folderName = "GSTR1GetCsvs";
			else if ("GSTR2A".equalsIgnoreCase(returnType))
				folderName = "GSTR2AGetCsvs";
			else if ("GSTR3B".equalsIgnoreCase(returnType))
				folderName = "GSTR3BGetCsvs";
			else if ("GSTR9".equalsIgnoreCase(returnType))
				folderName = "GSTR9GetCsvs";
			else if ("ITC04".equalsIgnoreCase(returnType))
				folderName = "ITC04GetCsvs";
			else if ("GSTR6".equalsIgnoreCase(returnType))
				folderName = "GSTR6GetCsvs";
			else if ("GSTR2X".equalsIgnoreCase(returnType))
				folderName = "GSTR2XGetCsvs";
			else if ("GSTR7".equalsIgnoreCase(returnType))
				folderName = "GSTR7GetCsvs";
			else if ("GSTR8".equalsIgnoreCase(returnType))
				folderName = "GSTR8GetCsvs";
			else if ("GSTR1A".equalsIgnoreCase(returnType))
				folderName = "GSTR1AGetCsvs";
		} else if (JobStatusConstants.ZIP_FILE.equalsIgnoreCase(fileType)) {
			if ("GSTR1".equalsIgnoreCase(returnType))
				folderName = "GSTR1GetZips";
			else if ("GSTR2A".equalsIgnoreCase(returnType))
				folderName = "GSTR2AGetZips";
			else if ("GSTR3B".equalsIgnoreCase(returnType))
				folderName = "GSTR3BGetZips";
			else if ("GSTR9".equalsIgnoreCase(returnType))
				folderName = "GSTR9GetZips";
			else if ("ITC04".equalsIgnoreCase(returnType))
				folderName = "ITC04GetZips";
			else if ("GSTR6".equalsIgnoreCase(returnType))
				folderName = "GSTR6GetZips";
			else if ("GSTR2X".equalsIgnoreCase(returnType))
				folderName = "GSTR2XGetZips";
			else if ("GSTR7".equalsIgnoreCase(returnType))
				folderName = "GSTR7GetZips";
			else if ("GSTR8".equalsIgnoreCase(returnType))
				folderName = "GSTR8GetZips";
			else if ("GSTR1A".equalsIgnoreCase(returnType))
				folderName = "GSTR1AGetZips";
		}

		if (folderName == null) {
			String errMsg = String.format(
					"Doc repository folder is not configured "
							+ "for the returnType : %s, fileType : %s",
					returnType, fileType);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		return folderName;

	}

}
