package com.ey.advisory.controller.gstr2a;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ey.advisory.app.services.reports.Gstr1vs3bProcessedRecScreenHandler;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Gstr1vs3bScreenReportsController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr2avs3bScreenReportsController.class);

    @Autowired
    @Qualifier("ProcessedRecordsCommonSecParam")
    private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

    @Autowired
    @Qualifier("Gstr1vs3bProcessedRecScreenHandler")
    private Gstr1vs3bProcessedRecScreenHandler gstr1vs3bProcessedRecScreenHandler;

    @Autowired
    @Qualifier("BasicGstr6SecCommonParam")
    BasicGstr6SecCommonParam basicGstr6SecCommonParam;

    @RequestMapping(value = "/ui/gstr1vs3bScreenDownloads", method = RequestMethod.POST, produces = {
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

            Gstr1VsGstr3bProcessSummaryReqDto setDataSecurity = processedRecordsCommonSecParam
                    .setGstr1VsGstr3bDataSecuritySearchParams(criteria);

            if (criteria.getType() != null
                    && criteria.getType().equalsIgnoreCase(
                            DownloadReportsConstant.GSTR1VS3BPRSUMMARY)) {
                workbook = gstr1vs3bProcessedRecScreenHandler
                        .getGstr1vs3bSumTablesReports(setDataSecurity);
                fileName = "Gstr1vs3b_Processed_Records_Screen_Download";
            } else if (criteria.getType() != null
                    && criteria.getType().equalsIgnoreCase(
                            DownloadReportsConstant.GSTR1VS3BREVIEWSUMMARY)) {
                workbook = gstr1vs3bProcessedRecScreenHandler
                        .getGstr1vs3bRevSumTablesReports(setDataSecurity);
                fileName = "Gstr1vs3b_ReviewSummary_Screen_Download";
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
