package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Row;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.docs.einvoice.ImsRepositoryHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GlMasterFileProcessService")
public class GlMasterFileProcessService {

	@Autowired
	private GlMasterFileRepositoryHandler repoHandler;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final Map<String, List<String>> FILE_TYPE_HEADERS_MAP = new HashMap<>();
	static {
		FILE_TYPE_HEADERS_MAP.put("Business_Unit_code",
				Arrays.asList("BusinessPlace", "BusinessArea", "PlantCode",
						"ProfitCentre", "CostCentre", "GSTIN"));
		FILE_TYPE_HEADERS_MAP.put("GL_Code_Mapping_Master_GL", Arrays.asList(
			    "CGST Tax GL Codes",
			    "SGST Tax GL Code",
			    "IGST Tax GL Code",
			    "UGST Tax GL Code",
			    "Compensation Cess GL Code",
			    "Kerala Cess GL Code",
			    "Revenue GLs",
			    "Expence GLs",
			    "Exchange rate diff GL",
			    "Export GL",
			    "Forex Gls Part of revenue",
			    "Taxable Advance (Liability) GLs",
			    "Non-Taxable Advance (Liability) GLs",
			    "Cross-Charge and Stock transfer GLs",
			    "Unbilled Revenue GLs",
			    "Bank A/C GLs",
			    "Input Tax GLs",
			    "Fixed Asset GLs"
			));

		FILE_TYPE_HEADERS_MAP.put("Document_type", Arrays.asList(
			    "Document_Type",
			    "Document_Type_MS"
			));
		
		FILE_TYPE_HEADERS_MAP.put("Supply_Type", Arrays.asList(
			    "Supply_Type_Reg",
			    "Supply_Type_MS"
			));
	
		FILE_TYPE_HEADERS_MAP.put("Tax_code", Arrays.asList(
			    "Transaction_Type_GL",
			    "Tax_Code_Description_MS",
			    "Tax_Type_MS",
			    "Eligibility_MS",
			    "Tax_Rate_MS"
			));

	}

	public void processGlMasterFile(InputStream inputStream, String fileName,
			Gstr1GetInvoicesReqDto dto) throws Exception {
	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("GlMasterFileProcessService -> processGl Master file with fileName: [{}]", fileName);
	    }

	    LoadOptions options = new LoadOptions(FileFormatType.XLSX);
	    CommonUtility.setAsposeLicense();
	    Workbook workbook = new Workbook(inputStream, options);
	    workbook.getSettings().setMemorySetting(MemorySetting.MEMORY_PREFERENCE);

	    WorksheetCollection collection = workbook.getWorksheets();
	    Worksheet worksheet = collection.get(0);
	    String sheetName = worksheet.getName();

	    long startTime = System.currentTimeMillis();
	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("Processing the work sheet::[{}]", sheetName);
	    }

	    Cells cells = worksheet.getCells();

	    // ✅ HEADER VALIDATION BASED ON FILE TYPE
	    String fileType = dto.getFileType();
	    List<String> expectedHeaders = FILE_TYPE_HEADERS_MAP.get(fileType);

	    if (expectedHeaders == null) {
	        throw new RuntimeException("Unsupported Headers: " + fileType);
	    }

	    Row headerRow = cells.getRows().get(0);
	    List<String> actualHeaders = new ArrayList<>();
	    int actualColumnCount = cells.getMaxColumn() + 1;

	    for (int col = 0; col < actualColumnCount; col++) {
	        Cell cell = headerRow.get(col);
	        String headerValue = (cell == null || cell.getValue() == null)
	                ? ""
	                : cell.getStringValue().trim();
	        actualHeaders.add(headerValue);
	    }

	    // ✅ Trim trailing empty headers before comparison
	    int lastNonEmptyIndex = actualHeaders.size() - 1;
	    while (lastNonEmptyIndex >= 0 && actualHeaders.get(lastNonEmptyIndex).isEmpty()) {
	        lastNonEmptyIndex--;
	    }
	    actualHeaders = actualHeaders.subList(0, lastNonEmptyIndex + 1);

	    if (!actualHeaders.equals(expectedHeaders)) {
	        throw new RuntimeException(
	            "Invalid Header/Mismatch in Header");
	    }

	    Object[][] objList = cells.exportArray(1, 0, cells.getMaxDataRow(), 22);
	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("Object list [{}] for the work sheet::[{}]", objList.length, sheetName);
	    }

	    repoHandler.processGlMasterWorkSheetData(objList, 22, dto);

	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("Total time taken:: [{}] ms to process the sheet:: [{}]",
	                (System.currentTimeMillis() - startTime), sheetName);
	    }}



}
