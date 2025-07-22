package com.ey.advisory.controller.gstr9;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeConfigStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeReadyStatusFYEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeConfigStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeProcedureRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeReadyStatusFYRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@Slf4j
@RestController
public class Gstr9ComputeGstr1vs3BController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr9ComputeConfigStatusRepository")
	Gstr9ComputeConfigStatusRepository configStatusRepo;

	@Autowired
	@Qualifier("Gstr9ComputeReadyStatusFYRepository")
	Gstr9ComputeReadyStatusFYRepository readyStatusRepo;

	@Autowired
	@Qualifier("Gstr9ComputeProcedureRepository")
	Gstr9ComputeProcedureRepository procedureRepo;

	@PostMapping(value = "/ui/gstr9ComputeGstr1vs3B", produces = {
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
					"Begin Gstr9Compute" + ".getComputeChecked() method, "
							+ "Parsing Input request %s ",
					jsonString);
			LOGGER.debug(msg);
		}
		try {

			String groupCode = TenantContext.getTenantId();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			Integer getConvertedFy = convertFyFromString(formattedFy);

			Gstr9ComputeConfigStatusEntity entity = configStatusRepo
					.findByGstinAndFyAndIsDeleteFalse(gstin, getConvertedFy);
			if (entity != null && ("initiated"
					.equalsIgnoreCase(entity.getStatus())
					|| "inprogress".equalsIgnoreCase(entity.getStatus()))) {
				String msg = "GSTR9 Compute is already Inprogress";
				LOGGER.error(msg);
				throw new AppException(msg);

			}
			Gstr9ComputeReadyStatusFYEntity fyEntity = readyStatusRepo
					.findByGstinAndFy(gstin, getConvertedFy);

			if (fyEntity == null) {

				String msg = "No Get Call Data available for the Selected Gstin"
						+ " and Fy";
				LOGGER.error(msg);
				throw new AppException(msg);

			}
			Boolean isGstr1GetCompFlag = fyEntity.isGstr1GetCompleted();
			Boolean isGstr3bGetCompFlag = fyEntity.isGstr3bGetCompleted();
			Boolean isGstr1AmdGetCompFlag = fyEntity.isGstr1AmdGetCompleted();

			if (!isGstr1GetCompFlag || !isGstr3bGetCompFlag
					|| !isGstr1AmdGetCompFlag) {
				String msg = "Compute cannot be initiated, either Get GSTR1 or "
						+ "Get3B call is not complete ";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			int softdeleteCount = configStatusRepo
					.updateActiveExistingRecords(gstin, getConvertedFy);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"{} records soft deleted for Gstin {} and FY"
								+ " {}  in Gstr9ConfigStatus Table",
						softdeleteCount, gstin, getConvertedFy);
			}

			Gstr9ComputeConfigStatusEntity configStatusEntity = new Gstr9ComputeConfigStatusEntity();
			configStatusEntity.setCreatedBy(userName);
			configStatusEntity.setCreatedOn(LocalDateTime.now());
			configStatusEntity.setStatus("INITIATED");
			configStatusEntity.setGstin(gstin);
			configStatusEntity.setFy(getConvertedFy);
			configStatusRepo.save(configStatusEntity);
			Long configID = configStatusEntity.getConfigId();

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configID);
			jsonParams.addProperty("isAmdReq",
					fyEntity.isGstr1AmdGetCompleted());

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR9COMPUTEGSTR1VS3B, jsonParams.toString(),
					userName, 1L, null, null);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("resp", "Compute Initiated Successfully");

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Error while submitting the job Gstr9ComputeGstr1vs3BController";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", ex.getMessage());

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private Integer convertFyFromString(String fy) {
		String[] fyArr = fy.split("-");
		String fyNew = fyArr[0] + fyArr[1].replaceFirst("20", "");
		return Integer.parseInt(fyNew);
	}
}