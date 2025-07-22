package com.ey.advisory.controller.gstr2a;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Gstr2avs3bProcessedRecScreenHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Gstr2avs3bScreenReportsController {

    @Autowired
    @Qualifier("ProcessedRecordsCommonSecParam")
    private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

    @Autowired
    @Qualifier("Gstr2avs3bProcessedRecScreenHandler")
    private Gstr2avs3bProcessedRecScreenHandler gstr2avs3bProcessedRecScreenHandler;

    @Autowired
    @Qualifier("BasicGstr6SecCommonParam")
    BasicGstr6SecCommonParam basicGstr6SecCommonParam;

    @RequestMapping(value = "/ui/gstr2avs3bScreenDownloads", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public void gstr6ReportsDownloads(@RequestBody String jsonString,
            HttpServletResponse response) {

        JsonObject requestObject = (new JsonParser()).parse(jsonString)
                .getAsJsonObject();
        JsonObject json = requestObject.get("req").getAsJsonObject();
        // String groupcode = TenantContext.getTenantId();
        // TenantContext.setTenantId(groupcode);
        Gson gson = GsonUtil.newSAPGsonInstance();

        try {
            String fileName = null;
            Workbook workbook = null;
            Gstr1VsGstr3bProcessSummaryReqDto criteria = gson.fromJson(json,
                    Gstr1VsGstr3bProcessSummaryReqDto.class);

         
            DateTimeFormatter format = DateTimeFormatter
                    .ofPattern("yyyyMMddHHmmss");
            LocalDateTime timeOfGeneration = LocalDateTime.now();
            LocalDateTime convertISDDate = EYDateUtil
            		.toISTDateTimeFromUTC(timeOfGeneration);
            if (criteria.getType() != null
                    && criteria.getType().equalsIgnoreCase(
                            DownloadReportsConstant.GSTR2AVS3BPRSUMMARY)) {
                workbook = gstr2avs3bProcessedRecScreenHandler
                        .getGstr2avs3bSumTablesReports(criteria);
                fileName = "DashboardSummary_GSTR2AvsGSTR3B_"+criteria.getTaxPeriodFrom()+"_"+criteria.getTaxPeriodTo()+"_"+format.format(convertISDDate);
            } else if (criteria.getType() != null
                    && criteria.getType().equalsIgnoreCase(
                            DownloadReportsConstant.GSTR2AVS3BREVIEWSUMMARY)) {
            	Map<String, List<String>> dataSecReqMap = criteria.getDataSecAttrs();
                List<String> gstinList = dataSecReqMap
                        .get(OnboardingConstant.GSTIN);
                workbook = gstr2avs3bProcessedRecScreenHandler
                        .getGstr2avs3bRevSumTablesReports(criteria);
                fileName = gstinList.toString()+"_GSTR2AvsGSTR3B_"+criteria.getTaxPeriodFrom()+"_"+criteria.getTaxPeriodTo()+"_"+format.format(convertISDDate);
            }

            else {
                LOGGER.error("invalid request");
                throw new Exception("invalid request");
            }
            if (workbook == null) {
                workbook = new Workbook();
            }
            if (fileName != null) {
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", String
                        .format("attachment; filename=" + fileName + ".xlsx"));
                workbook.save(response.getOutputStream(), SaveFormat.XLSX);
                response.getOutputStream().flush();
            }
            JsonObject resp = new JsonObject();
            resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
        } catch (JsonParseException ex) {
            String msg = "Error while parsing the input Json";
            LOGGER.error(msg, ex);
            JsonObject resp = new JsonObject();
            resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

        } catch (Exception ex) {
            String msg = "Unexpected error while retriving "
                    + "Data from Report ";
            LOGGER.error(msg, ex);
            JsonObject resp = new JsonObject();
            resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
        }
    }
}
