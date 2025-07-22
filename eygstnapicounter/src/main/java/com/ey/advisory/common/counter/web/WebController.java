package com.ey.advisory.common.counter.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.counter.CounterExecControlParams;
import com.ey.advisory.common.counter.services.GSTNPublicApiCounterService;
import com.ey.advisory.common.counter.services.PublicApiLimitDTO;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
@RestController
@Slf4j
public class WebController {

	@Autowired
	@Qualifier("GSTNPublicApiCounterServiceImpl")
	private GSTNPublicApiCounterService counterService;

	@Autowired
	private CounterExecControlParams cntrlParams;

	@RequestMapping("/test")
	public String handler() {
		return "{\"msg\": \"App is UP and RUNNING\"}";
	}

	@PostMapping(value = "/saveLimits", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveLimits(@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {

			PublicApiLimitDTO dto = gson.fromJson(jsonString,
					PublicApiLimitDTO.class);

			if (Strings.isNullOrEmpty(dto.getGroupCode())) {
				String msg = "GroupCode cannot be empty";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to execute SaveLimit request for group {}",
						dto.getGroupCode());
			}
			String rsp = counterService.saveLimitsForGroupCode(dto);
			resp.add("hdr", gson.toJsonTree("S"));
			resp.add("resp", gson.toJsonTree(rsp));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Limit Details";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

	@GetMapping(value = "/getLimitsForGroup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLimitsForGroup(
			@RequestParam String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to getLimits for groupCode{}", groupCode);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resps = new JsonObject();
		JsonElement respBody = null;
		try {
			PublicApiLimitDTO dto = counterService
					.getLimitsForGroupCode(groupCode);

			if (dto != null) {
				resps.add("hdr", gson.toJsonTree("S"));

				respBody = gson.toJsonTree(dto);
			} else {
				resps.add("hdr", gson.toJsonTree("E"));

				respBody = gson.toJsonTree("No Data");
			}
			resps.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Limit Details For Group";
			LOGGER.error(msg, ex);
			resps.add("hdr", gson.toJsonTree("E"));
			resps.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		}

	}

	@GetMapping(value = "/isApiCallAllowed", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> isApiCallAllowed(
			@RequestParam String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to check apiCall eligibility for groupCode{}",
					groupCode);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			boolean isApiCallAllowed = counterService
					.isApiCallAllowedForGroupCode(groupCode);
			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree("S"));
			resps.add("resp", gson.toJsonTree(isApiCallAllowed));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving API call Details";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree("E"));
			errorResp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}
	}

	@GetMapping(value = "/getAllLimits", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllLimits() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			Map<String, Map<Integer, Integer>> limitMap = cntrlParams
					.getLimitMap();

			JsonElement respBody = gson.toJsonTree(limitMap);
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving All Limit Details";
			LOGGER.error(msg, ex);
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@GetMapping(value = "/getAllUsage", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllUsage() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			Map<String, Map<Integer, Integer>> limitMap = cntrlParams
					.getUsageMap();

			JsonElement respBody = gson.toJsonTree(limitMap);
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving All Usage Details";
			LOGGER.error(msg, ex);
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@GetMapping(value = "/getLimitAndUsageCount", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLimitAndUsageCount(
			@RequestParam String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to get Limit & Usage count for groupCode{}",
					groupCode);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Pair<Integer, Integer> respStatus = counterService
					.getUsageAndLimitForGroupCode(groupCode);
			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree("S"));
			resps.add("limit", gson.toJsonTree(respStatus.getValue0()));
			resps.add("usage", gson.toJsonTree(respStatus.getValue1()));

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Limit Details";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree("E"));
			errorResp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}
	}

	@GetMapping(value = "/decrementUsageCount", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> decrementUsageCount(
			@RequestParam String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to decrement Usage count for groupCode{}",
					groupCode);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			counterService.decrementUsageCountForGroupCode(groupCode);
			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree("S"));
			resps.add("resp", gson.toJsonTree(String.format(
					"Usage count decremented for groupCode %s successfully ",
					groupCode)));

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Limit Details";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree("E"));
			errorResp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}
	}

	@GetMapping(value = "/deleteDocFolder", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteDocFolder(
			@RequestParam("groupCode") String groupCode,
			@RequestParam("docFolder") String docFolder) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to decrement Usage count for groupCode{}",
					groupCode);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resps = new JsonObject();
		try {
			TenantContext.setTenantId(groupCode);
			String isDeleted = DocumentUtility.deleteFolder(docFolder);
			String msg = "";
			if (APIConstants.SUCCESS.contains(isDeleted)) {
				msg = String.format(
						"Folder %s has been deleted Successfully in Group %s",
						docFolder, groupCode);
			} else {
				msg = String.format("No Folder %s is available in Group %s",
						docFolder, groupCode);
			}
			resps.add("hdr", gson.toJsonTree("S"));
			resps.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Error while deleting Folder.";
			LOGGER.error(msg, ex);
			resps.add("hdr", gson.toJsonTree("E"));
			resps.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		}
	}

	// POC for AZURE FILE SHARE
	private static final String CLIENT_SECRET = "X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
	private static final String CLIENT_ID = "47b538a9-26dd-4093-a49f-2f7618f70071";
	private static final String TENANT_ID = "33b98860-21ef-4870-9beb-712f0cdac7b9";
	private static final String SECRET_NAME = "ciqadmsblbstr01-sas-123128";
	private static final String SHARE_NAME = "qadmsfs";
	private static final String KEY_VAULT_URL = "https://ci-dv-kvtest01.vault.azure.net";
	private static final String ACCOUNT_NAME = "ciqadmsblbstr01";

	@PostMapping("/uploadFromResources")
	public ResponseEntity<String> uploadFileFromResources(
			@RequestBody String requestBody) {
		try {
			// Parse JSON request body
			JsonObject jsonObject = JsonParser.parseString(requestBody)
					.getAsJsonObject();
			String dirName = jsonObject.has("dirName")
					? jsonObject.get("dirName").getAsString()
					: "DefaultDir";
			String fileName = jsonObject.has("fileName")
					? jsonObject.get("fileName").getAsString()
					: "Test1.txt";

			// Locate the file in the resources folder
			ClassPathResource resource = new ClassPathResource(fileName);
			if (!resource.exists()) {
				return new ResponseEntity<>(
						"File not found in resources folder: " + fileName,
						HttpStatus.NOT_FOUND);
			}

			// Read file content as bytes
			byte[] fileBytes = Files.readAllBytes(resource.getFile().toPath());

			// Create a temporary file
			Path tempFilePath = Files.createTempFile("upload_", "_" + fileName);
			Files.write(tempFilePath, fileBytes, StandardOpenOption.CREATE);

			// Azure authentication
			ClientSecretCredential credential = new ClientSecretCredentialBuilder()
					.clientId(CLIENT_ID).clientSecret(CLIENT_SECRET)
					.tenantId(TENANT_ID).build();

			// Fetch SAS token from Azure Key Vault
			SecretClient secretClient = new SecretClientBuilder()
					.vaultUrl(KEY_VAULT_URL).credential(credential)
					.buildClient();

			KeyVaultSecret secret = secretClient.getSecret(SECRET_NAME);
			String sasToken = secret.getValue();

			// Azure File Share Setup
			String shareURL = String.format("https://%s.file.core.windows.net",
					ACCOUNT_NAME);
			ShareClient shareClient = new ShareClientBuilder()
					.endpoint(shareURL).sasToken(sasToken).shareName(SHARE_NAME)
					.buildClient();

			if (!shareClient.exists()) {
				return new ResponseEntity<>(
						"Share does not exist: " + SHARE_NAME,
						HttpStatus.NOT_FOUND);
			}

			// Create directory if not exists
			ShareDirectoryClient directoryClient = shareClient
					.getDirectoryClient(dirName);
			if (!directoryClient.exists()) {
				directoryClient.create();
			}

			// Upload file to Azure File Share
			ShareFileClient fileClient = new ShareFileClientBuilder()
					.endpoint(shareURL).sasToken(sasToken).shareName(SHARE_NAME)
					.resourcePath(dirName + "/" + fileName).buildFileClient();

			fileClient.create(fileBytes.length);
			fileClient.uploadFromFile(tempFilePath.toString());

			// Cleanup temporary file
			Files.delete(tempFilePath);

			return new ResponseEntity<>(
					"File uploaded successfully from resources: " + fileName,
					HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(
					"Error reading file from resources: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>(
					"Error uploading file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
