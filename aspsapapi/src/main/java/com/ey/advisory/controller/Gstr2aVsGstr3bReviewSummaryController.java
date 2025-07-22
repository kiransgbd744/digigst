package com.ey.advisory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.anx1.Gstr2aVsGstr3bReviewSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author V.Mule
 *
 */
@Slf4j
@RestController
public class Gstr2aVsGstr3bReviewSummaryController {

    @Autowired
    @Qualifier("Gstr2aVsGstr3bReviewSummaryFetchService")
    Gstr2aVsGstr3bReviewSummaryFetchService gstr2aVsGstr3bReviewSummaryFetchService;

    @RequestMapping(value = "/ui/gstr2aVsGstr3bReviewSummary", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> gstr2aVsGstr3bReviewSummary(
            @RequestBody String jsonString) {

        JsonObject requestObject = (new JsonParser()).parse(jsonString)
                .getAsJsonObject();

        JsonObject json = requestObject.get("req").getAsJsonObject();
        Gson gson = GsonUtil.newSAPGsonInstance();
        LOGGER.debug("The selected criteria for processed records is:->"
                + requestObject.get("req"));
        // Execute the service method and get the result.
        try {

            Gstr1VsGstr3bProcessSummaryReqDto recordsReqDto = gson
                    .fromJson(json, Gstr1VsGstr3bProcessSummaryReqDto.class);

            List<Gstr2aVsGstr3bReviewSummaryRespDto> response = gstr2aVsGstr3bReviewSummaryFetchService
                    .gstr2aVsGstr3bReviewSummaryRecords(recordsReqDto);
            JsonObject resp = new JsonObject();
            if (response.isEmpty()) {
                String msg = "No data found";
                resp.add("hdr",
                        new Gson().toJsonTree(new APIRespDto("S", msg)));
            } else {
                resp.add("hdr",
                        gson.toJsonTree(APIRespDto.createSuccessResp()));
                resp.add("resp", gson.toJsonTree(response));
            }

            LOGGER.debug(
                    "Response data for given criteria for processed records is :->"
                            + resp.toString());
            return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

        } catch (JsonParseException ex) {
            String msg = "Error while parsing the input Json";
            LOGGER.error(msg, ex);

            JsonObject resp = new JsonObject();
            resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

            return new ResponseEntity<>(resp.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            String msg = "Unexpected error while retriving Gstr2avs3b processed records";
            LOGGER.error(msg, ex);
            JsonObject resp = new JsonObject();
            resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
            return new ResponseEntity<>(resp.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
