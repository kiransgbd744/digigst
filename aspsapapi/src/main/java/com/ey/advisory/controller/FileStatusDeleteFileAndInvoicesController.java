/**
 * 
 */
package com.ey.advisory.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Gstr2BReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ReconResultRespPsdRepository;
import com.ey.advisory.app.docs.dto.DocDeleteReqDto;
import com.ey.advisory.app.services.delete.DocDeleteService;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardDocSave240Service;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@RestController
public class FileStatusDeleteFileAndInvoicesController {

	@Autowired
	@Qualifier("Gstr2ReconResultRespPsdRepository")
	private Gstr2ReconResultRespPsdRepository gstr2ReconResultRespPsdRepository;

	@Autowired
	@Qualifier("Gstr2BReconResultRespPsdRepository")
	private Gstr2BReconResultRespPsdRepository gstr2BReconResultRespPsdRepository;

	@Autowired
	@Qualifier("DefaultDocDeleteServiceImpl")
	DocDeleteService docDeleteService;

	@PostMapping(value = "/ui/deleteFile", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteInwardFile(
			@RequestBody String jsonString) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject obj = requestObject.get("req").getAsJsonObject();
			Long fileId = obj.get("fileId").getAsLong();

			docDeleteService.deleteFile(fileId);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(
					"Records in the selected file deleted Successfully.");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

	@PostMapping(value = "/ui/deleteInvoices", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteInwardInvoices(
			@RequestBody String jsonString) {

		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			DocDeleteReqDto dto = gson.fromJson(reqJson, DocDeleteReqDto.class);

			String msg = null;

			Set<String> filedList = new HashSet<String>();

			List<InwardTransDocument> gstinInfoDetails = docDeleteService
					.getInvoices(dto);
			for (InwardTransDocument inwardTransDocument : gstinInfoDetails) {
				String gstin = inwardTransDocument.getCgstin();
				String taxPeriod = inwardTransDocument.getTaxperiod();
				if (isDocLocked(inwardTransDocument)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("docKey :{} is already locked",
								inwardTransDocument.getDocKey());
					}

					msg = "Selected records are locked under Recon. Kindly "
							+ "select only unlocked records for deletion";
					JsonObject resp = new JsonObject();
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));

					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

				}

				EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder
						.getBean("EhcacheGstinTaxperiod",
								EhcacheGstinTaxperiod.class);
				GstrReturnStatusEntity entity = ehcachegstinTaxPeriod
						.isGstinFiled(gstin, taxPeriod, "GSTR6", "FILED",
								TenantContext.getTenantId());
				if (entity != null) {
					filedList.add("Filed");
				} else {
					filedList.add("Not Filed");
				}
			}

			if (filedList.contains("Filed")
					&& filedList.contains("Not Filed")) {
				msg = "Selected Records cannot be deleted as Return for one "
						+ " or more selected records has already been filed";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if (filedList.contains("Filed")) {
				msg = "Selected Records cannot be deleted as the return has "
						+ "already been filed for the selected records";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			int rowsAffected = docDeleteService.deleteInvoices(dto);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("Selected " + rowsAffected
					+ " records has been deleted successfully.");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

	private boolean isDocLocked(InwardTransDocument i) {
		try {
			String invoiceKey = DefaultInwardDocSave240Service
					.reconDocKey2aprgeneration(i);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("invoiceKey :{} While checking isDocLocked",
						invoiceKey);
			}

			String islocked = gstr2ReconResultRespPsdRepository
					.islocked(invoiceKey);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("islocked :{} While checking isDocLocked under islocked",
						islocked);
			}
			if (islocked == null) {
				invoiceKey = DefaultInwardDocSave240Service
						.reconDocKeygeneration(i);
				islocked = gstr2BReconResultRespPsdRepository
						.islocked(invoiceKey);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("islocked == null :{} While checking isDocLocked under islocked",
							islocked);
				}
			}
			if (islocked != null) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(
					"Error while checking record is locked/not condition for docKey{}",
					i.getDocKey(), e);
		}
		return false;
	}
}
