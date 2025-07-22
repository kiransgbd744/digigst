/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
public class DownloadFailedMasterUploadsController {

	@Autowired
	@Qualifier("DownloadFailedMasterUploadsServiceImpl")
	private DownloadFailedMasterUploadsServiceImpl downloadFailedMasterUploadsService;
	
	@Autowired
	CommonUtility commonUtility;

	@RequestMapping(value = "/ui/downloadFailedMasterUploads", method = RequestMethod.POST, produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public void downloadFailedMasterUploads(@RequestBody String jsonString, HttpServletResponse response) {

	    Gson gson = GsonUtil.newSAPGsonInstance();

	    try {
	        JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();
	        JsonObject json = requestObject.get("req").getAsJsonObject();
	        String fileId = json.has("FileId") ? json.get("FileId").getAsString() : null;
	        String fileType = json.has("FileType") ? json.get("FileType").getAsString() : null;

	        String fileTypeName = null;
	        if (fileType != null) {
	            fileTypeName = fileTypeName(fileType);
	        }

	        LOGGER.debug("Calling service to generate failed master upload workbook with fileId: {}, fileType: {}", fileId, fileTypeName);
	        Workbook workbook = downloadFailedMasterUploadsService.generateFailedMasterUploadWorkbook(fileId, fileTypeName);
	        LOGGER.debug("Workbook generated: {}", (workbook != null));

	        // Generate timestamp
	        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
	        String fileName = "Failed_Master_Upload_Report_" + timestamp;
	        LOGGER.debug("Generated failed master upload report file name: {}", fileName);
	        if (fileType != null && !fileType.isEmpty()) {
	        	fileName = fileType.replace(" ", "_") + "_ErrorReport_" + timestamp;
	        }

	        if (workbook != null) {
	            //workbook = new Workbook();
	        

	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
	        workbook.save(response.getOutputStream(), SaveFormat.XLSX);
	        response.getOutputStream().flush();

	        }

	    } catch (JsonParseException ex) {
	        String msg = "Error while parsing the input JSON";
	        LOGGER.error(msg, ex);
	        JsonObject resp = new JsonObject();
	        resp.add("hdr", gson.toJsonTree(new APIRespDto("E", msg)));
	    } catch (Exception ex) {
	        String msg = "Unexpected error while retrieving failed master uploads report";
	        LOGGER.error(msg, ex);
	        JsonObject resp = new JsonObject();
	        resp.add("hdr", gson.toJsonTree(new APIRespDto("E", msg)));
	    }
	}


	private String fileTypeName(String fileType) {
	    String respFileType = null;
	    switch (fileType) {

	        case "Business Unit Code":
	            respFileType = "Business_Unit_code";
	            break;

	        case "Document Type":
	            respFileType = "Document_type";
	            break;

	        case "Supply Type":
	            respFileType = "Supply_Type";
	            break;

	      
	        case "Tax Code":
	            respFileType = "Tax_code";
	            break;

	        case "GL Code Mapping Master - GL":
	            respFileType = "GL_Code_Mapping_Master_GL";
	            break;


	        default:
	            respFileType = fileType;
	            break;
	    }
	    return respFileType;
	}

	
}
