package com.ey.advisory.app.services.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Gstr1CommonUtility {

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	public File createTempDir(Long batchId) throws IOException {

		return Files.createTempDirectory("DownloadReports").toFile();
	}

	public String getFinalGstinString(Gstr1ReviwSummReportsReqDto criteria)
			throws IOException {

		LOGGER.debug("Gstr1CommonUtility DataStatus Adapter Filters Setting to Request BEGIN");

		Gstr1ReviwSummReportsReqDto setDataSecurity = basicCommonSecParamRSReports
				.setDataSecuritySearchParams(criteria);

		List<String> selectedGstins = setDataSecurity.getDataSecAttrs()
				.get(OnboardingConstant.GSTIN);
		StringBuffer buffer = new StringBuffer();
		selectedGstins.forEach(gstin -> buffer.append(gstin).append("_"));
		String finalGstinString = buffer.toString().substring(0,
				buffer.toString().length() - 1);
		LOGGER.debug("Gstr1CommonUtility DataStatus Adapter Filters Setting to Request END");

		return finalGstinString;
	}

	public Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL templateDir = classLoader.getResource(folderName + "/");
			String templatePath = templateDir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}

		return workbook;
	}

}
