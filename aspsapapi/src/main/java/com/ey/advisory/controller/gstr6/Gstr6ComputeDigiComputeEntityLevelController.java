package com.ey.advisory.controller.gstr6;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.gstr6.Gstr6ComputeDigiConfigStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeDigiConfigStatusRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Gstr6ComputeDigiComputeEntityLevelController {

    @Autowired
    AsyncJobsService asyncJobsService;

    @Autowired
    @Qualifier("Gstr6ComputeDigiConfigStatusRepository")
    Gstr6ComputeDigiConfigStatusRepository digiConfigStatusRepo;

    @PostMapping(value = "/ui/gstr6ComputeDigiGstEntityLevel", produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getComputeChecked(
            @RequestBody String jsonString, HttpServletResponse response) {

        Gson gson = GsonUtil.newSAPGsonInstance();

        String userName = SecurityContext.getUser() != null
                ? (SecurityContext.getUser().getUserPrincipalName() != null
                        ? SecurityContext.getUser().getUserPrincipalName()
                        : "SYSTEM")
                : "SYSTEM";
        if (LOGGER.isDebugEnabled()) {
            String msg = String.format(
                    "Begin DigiGstr6Compute.getComputeChecked() method, "
                            + "Parsing Input request %s ",
                    jsonString);
            LOGGER.debug(msg);
        }
        try {
            String groupCode = TenantContext.getTenantId();

            JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject req = obj.get("req").getAsJsonObject();
            String taxPeriod = req.get("taxPeriod").getAsString();
            JsonArray gstinArray = req.get("gstin").getAsJsonArray();
            List<String> gstins = new ArrayList<>();
            for (int i = 0; i < gstinArray.size(); i++) {
                gstins.add(gstinArray.get(i).getAsString());
            }
            Integer derivedTaxPeriod = GenUtil.getDerivedTaxPeriod(taxPeriod);

            for (String gstin : gstins) {
            	Gstr6ComputeDigiConfigStatusEntity existingConfig = digiConfigStatusRepo
                        .findByGstinAndTaxPeriodAndStatusAndIsActiveTrue(gstin, taxPeriod, "INPROGRESS");
                if (existingConfig != null) {
                	LOGGER.info("Skipping GSTIN {} for tax period {} as it is already in progress", gstin, taxPeriod);
                    continue;
                }
                int softdeleteCount = digiConfigStatusRepo
                        .updateActiveExistingRecords(gstin, derivedTaxPeriod.toString());

                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug(
                            "{} records soft deleted for Gstin {} and FY"
                                    + " {} in Gstr6ConfigStatus Table",
                            softdeleteCount, gstin, derivedTaxPeriod);
                }

                Gstr6ComputeDigiConfigStatusEntity configStatusEntity = new Gstr6ComputeDigiConfigStatusEntity();
                configStatusEntity.setCreatedBy(userName);
                configStatusEntity.setCreatedOn(LocalDateTime.now());
                configStatusEntity.setStatus("INITIATED");
                configStatusEntity.setGstin(gstin);
                configStatusEntity.setTaxPeriod(derivedTaxPeriod.toString());
                configStatusEntity.setActive(true);
                digiConfigStatusRepo.save(configStatusEntity);
                Long configID = configStatusEntity.getConfigId();

                JsonObject jsonParams = new JsonObject();
                jsonParams.addProperty("configId", configID);

                asyncJobsService.createJob(groupCode,
                        JobConstants.GSTR6COMPUTEDIGIGSTPROCESSED,
                        jsonParams.toString(), userName, 1L, null, null);
            }

            JsonObject resp = new JsonObject();
            resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
            resp.addProperty("resp", "Compute Initiated Successfully");

            return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            String msg = "Error while submitting the job Gstr9ComputeDigiGstProcessedController";
            LOGGER.error(msg, ex);

            JsonObject resp = new JsonObject();
            resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
            resp.addProperty("resp", ex.getMessage());

            return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
        }
    }
}
