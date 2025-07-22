package com.ey.advisory.controller.gl.recon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.data.entities.client.GlDumpErrorEntity;
import com.ey.advisory.app.data.entities.client.GlreconDumpApiUploadStatusEntity;
import com.ey.advisory.app.data.repositories.client.GlDumpApiUploadStatusRepository;
import com.ey.advisory.app.data.repositories.client.GlReconDumpErrorRepository;
import com.ey.advisory.app.docs.dto.GlDumpAPIPushListDto;
import com.ey.advisory.app.docs.dto.GlDumpDownloadReportDto;
import com.ey.advisory.app.docs.dto.GlReconDumpAPIListDto;
import com.ey.advisory.app.glrecon.dump.GlReconDumpFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class GlReconAPIUploadController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GlDumpApiUploadStatusRepository")
	GlDumpApiUploadStatusRepository apiStatusRepository;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	@Autowired
	@Qualifier("GlReconDumpFileArrivalHandler")
	private GlReconDumpFileArrivalHandler glReconDumpFileArrivalHandler;

	@Autowired
	@Qualifier("GlReconDumpErrorRepository")
	private GlReconDumpErrorRepository glDumpErrorRepository;

	@PostMapping(value = "/api/saveGlDumpData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveVendorMaster(
			@RequestBody String jsonString) {
		JsonObject requestObject = null;
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		GlreconDumpApiUploadStatusEntity apiStatus = null;

		JsonObject json = null;
		JsonElement respBody = null;
		try {

			try {
				requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();

				json = requestObject.get("req").getAsJsonObject();

			} catch (Exception ex) {
				LOGGER.error("Error while parsing the request-{}", jsonString,
						ex);
				throw new AppException("Invalid request payload");
			}

			if (!json.has("glData"))
				throw new AppException("Gl Data is mandatory");
			GlReconDumpAPIListDto reqDto = gson.fromJson(json,
					GlReconDumpAPIListDto.class);
			if (reqDto.getGlData() == null || reqDto.getGlData().isEmpty()) {
				String msg = "gl Data cannot be null";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			apiStatus = logRequestPayload(jsonString, true);

			List<GlDumpAPIPushListDto> glData = reqDto.getGlData();
			String refId = apiStatus.getRefId();

			List<Object[]> req = new ArrayList<>();

			for (GlDumpAPIPushListDto dto : glData) {

				Object[] obj = new Object[75];
				convertDtoToObj(obj, dto);
				req.add(obj);
			}

			
				LOGGER.error("List of objects created -{}", req);
			
			Map<String, List<Object[]>> documentMap = new HashMap<>();
			for (Object[] object : req) {
				String transactionType = "";
				String accountingVoucherNumber = "";
				String accountingVoucherDate = "";
				if (object[0] != null)
					transactionType = object[0].toString();
				if (object[11] != null)
					accountingVoucherNumber = object[11].toString();
				if (object[12] != null)
					accountingVoucherDate = object[12].toString();
				String checkDuplicateKey = transactionType + "|"
						+ accountingVoucherNumber + accountingVoucherDate;

				documentMap.computeIfAbsent(checkDuplicateKey,
						k -> new ArrayList<>()).add(object);
			}
			User user = SecurityContext.getUser();
			String userName = user != null ? user.getUserPrincipalName()
					: "SYSTEM";

			GlReconFileStatusEntity fileStatus = new GlReconFileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName("");
			fileStatus.setFileType("GL_DUMP");
			fileStatus.setCraetedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setSource("API");
			fileStatus.setIsDelete(false);
			fileStatus.setIsActive(true);
			fileStatus.setPayloadId(refId);

			fileStatus = glReconFileStatusRepository.save(fileStatus);

			glReconDumpFileArrivalHandler.validation(documentMap, fileStatus,
					refId);

			Optional<GlreconDumpApiUploadStatusEntity> errorCount = apiStatusRepository
					.findByRefId(refId);

			if (errorCount.isPresent()) {
				if (errorCount.get().getErrorDesc() != null) {

					List<GlDumpErrorEntity> errorEntities = glDumpErrorRepository
							.findAllByFileId(fileStatus.getId());

					List<GlDumpDownloadReportDto> errorDataList = errorEntities
							.stream().map(o -> convert(o))
							.collect(Collectors.toCollection(ArrayList::new));
					respBody = gson.toJsonTree(errorDataList);

				} else {
					// List<JsonObject> responseList = new ArrayList<>();
					JsonObject obj = new JsonObject();
					obj.addProperty("refId", apiStatus.getRefId());
					obj.addProperty("msg",
							"Gl Data has been processed successfully");

					
						LOGGER.error("records has been processed successfully");
					
					// responseList.add(obj);
					respBody = gson.toJsonTree(Arrays.asList(obj));
				}
			}

			apiStatusRepository.updateFileStatus(refId, "Success");
			JsonObject resps = new JsonObject();

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			if (respBody != null)
				resps.add("resp", respBody);
			else
				resps.add("resp", resp);

			if (errorCount.isPresent()) {
				errorCount.get().setRespPayload(
						GenUtil.convertStringToClob(resps.toString()));
				apiStatusRepository.save(errorCount.get());
			}
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching vendor api Status ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("errMsg", gson.toJsonTree(ex.getMessage()));

			apiStatus = logRequestPayload(jsonString, false);

			apiStatus.setRespPayload(
					GenUtil.convertStringToClob(resp.toString()));
			apiStatusRepository.save(apiStatus);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private GlreconDumpApiUploadStatusEntity logRequestPayload(
			String jsonString, boolean value) {
		GlreconDumpApiUploadStatusEntity apiStatus = new GlreconDumpApiUploadStatusEntity();

		UUID uuid = UUID.randomUUID();
		apiStatus.setRefId(uuid.toString());
		apiStatus.setCreatedBy("API");
		apiStatus.setCreatedOn(LocalDateTime.now());
		apiStatus.setReqPayload(
				GenUtil.convertStringToClob(jsonString.toString()));
		if (value) {
			apiStatus.setApiStatus(JobStatusConstants.UPLOADED);
		} else {
			apiStatus.setApiStatus("FAILED");
		}
		return apiStatusRepository.save(apiStatus);

	}

	private static void convertDtoToObj(Object[] obj,
			GlDumpAPIPushListDto reqDto) {

		obj[0] = reqDto.getTransactionType();
		obj[1] = reqDto.getCompanyCode();
		obj[2] = reqDto.getFiscalYear();
		obj[3] = reqDto.getTaxPeriod();
		obj[4] = reqDto.getBusinessPlace();
		obj[5] = reqDto.getBusinessArea();
		obj[6] = reqDto.getGlAccount();
		obj[7] = reqDto.getGlDescription();
		obj[8] = reqDto.getText();
		obj[9] = reqDto.getAssignmentNumber();
		obj[10] = reqDto.getErpDocumentType();
		obj[11] = reqDto.getAccountingVoucherNumber();
		obj[12] = reqDto.getAccountingVoucherDate();
		obj[13] = reqDto.getItemSerialNumber();
		obj[14] = reqDto.getPostingKey();
		obj[15] = reqDto.getPostingDate();
		obj[16] = reqDto.getAmountInLocalCurrency();
		obj[17] = reqDto.getLocalCurrencyCode();
		obj[18] = reqDto.getClearingDocumentNumber();
		obj[19] = reqDto.getClearingDocumentDate();
		obj[20] = reqDto.getCustomerCode();
		obj[21] = reqDto.getCustomerName();
		obj[22] = reqDto.getCustomerGstin();
		obj[23] = reqDto.getSupplierCode();
		obj[24] = reqDto.getSupplierName();
		obj[25] = reqDto.getSupplierGstin();
		obj[26] = reqDto.getPlantCode();
		obj[27] = reqDto.getCostCentre();
		obj[28] = reqDto.getProfitCentre();
		obj[29] = reqDto.getSpecialGlIndicator();
		obj[30] = reqDto.getReference();
		obj[31] = reqDto.getAmountinDocumentCurrency();
		obj[32] = reqDto.getEffectiveExchangeRate();
		obj[33] = reqDto.getDocumentCurrencyCode();
		obj[34] = reqDto.getAccountType();
		obj[35] = reqDto.getTaxCode();
		obj[36] = reqDto.getWithholdingTaxAmount();
		obj[37] = reqDto.getWithholdingExemptAmount();
		obj[38] = reqDto.getWithholdingTaxBaseAmount();
		obj[39] = reqDto.getInvoiceReference();
		obj[40] = reqDto.getDebitCreditIndicator();
		obj[41] = reqDto.getPaymentDate();
		obj[42] = reqDto.getPaymentBlock();
		obj[43] = reqDto.getPaymentReference();
		obj[44] = reqDto.getTermsofPayment();
		obj[45] = reqDto.getMaterial();
		obj[46] = reqDto.getReferenceKey1();
		obj[47] = reqDto.getOffsettingAccountType();
		obj[48] = reqDto.getOffsettingAccountNumber();
		obj[49] = reqDto.getDocumentHeaderText();
		obj[50] = reqDto.getBillingDocumentNumber();
		obj[51] = reqDto.getBillingDocumentDate();
		obj[52] = reqDto.getMigoNumber();
		obj[53] = reqDto.getMigoDate();
		obj[54] = reqDto.getMiroNumber();
		obj[55] = reqDto.getMiroDate();
		obj[56] = reqDto.getExpenseGlMapping();
		obj[57] = reqDto.getSegment();
		obj[58] = reqDto.getGeoLevel();
		obj[59] = reqDto.getStateName();
		obj[60] = reqDto.getUserId();
		obj[61] = reqDto.getParkedBy();
		obj[62] = reqDto.getEntryDate();
		obj[63] = reqDto.getTimeOfEntry();
		obj[64] = reqDto.getRemarks();
		obj[65] = reqDto.getUserDefinedField1();
		obj[66] = reqDto.getUserDefinedField2();
		obj[67] = reqDto.getUserDefinedField3();
		obj[68] = reqDto.getUserDefinedField4();
		obj[69] = reqDto.getUserDefinedField5();
		obj[70] = reqDto.getUserDefinedField6();
		obj[71] = reqDto.getUserDefinedField7();
		obj[72] = reqDto.getUserDefinedField8();
		obj[73] = reqDto.getUserDefinedField9();
		obj[74] = reqDto.getUserDefinedField10();

	}

	private GlDumpDownloadReportDto convert(GlDumpErrorEntity entity) {

		GlDumpDownloadReportDto dto = new GlDumpDownloadReportDto();

		dto.setTransactionType(entity.getTransactionType());
		dto.setCompanyCode(entity.getCompanyCode());
		dto.setFiscalYear(entity.getFiscalYear());
		dto.setTaxPeriod(entity.getTaxPeriod());
		dto.setBusinessPlace(entity.getBussinessPlace());
		dto.setBusinessArea(entity.getBusinessArea());
		dto.setGlAccount(entity.getGlAccount());
		dto.setGlDescription(entity.getGlDescription());
		dto.setText(entity.getText());
		dto.setAssignmentNumber(entity.getAssignmentNumber());
		dto.setErpDocumentType(entity.getErpDocType());
		dto.setAccountingVoucherNumber(entity.getAccountingVoucherNumber());
		dto.setAccountingVoucherDate(entity.getAccountingVoucherDate());
		dto.setItemSerialNumber(entity.getItemNumber());
		dto.setPostingKey(entity.getPostingKey());
		dto.setPostingDate(entity.getPostingDate());
		dto.setAmountInLocalCurrency(entity.getAmountInLocalCurrency());
		dto.setLocalCurrencyCode(entity.getLocalCurrencyCode());
		dto.setClearingDocumentNumber(entity.getClearingDocNumber());
		dto.setClearingDocumentDate(entity.getClearingDocDate());
		dto.setCustomerCode(entity.getCustomerCode());
		dto.setCustomerName(entity.getCustomerName());
		dto.setCustomerGstin(entity.getCustomerGstin());
		dto.setSupplierCode(entity.getSupplierCode());
		dto.setSupplierName(entity.getSupplierName());
		dto.setSupplierGstin(entity.getSupplierGstin());
		dto.setPlantCode(entity.getPlantCode());
		dto.setCostCentre(entity.getCostCentre());
		dto.setProfitCentre(entity.getProfitCentre());
		dto.setSpecialGLIndicator(entity.getSpecialGlIndicator());
		dto.setReference(entity.getReference());
		dto.setAmountinDocumentCurrency(entity.getAmountinDocumentCurrency());
		dto.setEffectiveExchangeRate(entity.getEffectiveExchangeRate());
		dto.setDocumentCurrencyCode(entity.getDocumentCurrencyCode());
		dto.setAccountType(entity.getAccountType());
		dto.setTaxCode(entity.getTaxCode());
		dto.setWithholdingTaxAmount(entity.getWithHoldingTaxAmount());
		dto.setWithholdingExemptAmount(entity.getWithHoldingExemptAmount());
		dto.setWithholdingTaxBaseAmount(entity.getWithHoldingTaxBaseAmount());
		dto.setInvoiceReference(entity.getInvoiceReference());
		dto.setDebitCreditIndicator(entity.getDebitCreditIndicator());
		dto.setPaymentDate(entity.getPaymentDate());
		dto.setPaymentBlock(entity.getPaymentBlock());
		dto.setPaymentReference(entity.getPaymentReference());
		dto.setTermsofPayment(entity.getTermsOfPayment());
		dto.setMaterial(entity.getMaterial());
		dto.setReferenceKey1(entity.getReferenceKey1());
		dto.setOffsettingAccountType(entity.getOffSettingAccountType());
		dto.setOffsettingAccountNumber(entity.getOffSettingAccountNumber());
		dto.setDocumentHeaderText(entity.getDocumentHeaderText());
		dto.setBillingDocumentNumber(entity.getBillingDocNumber());
		dto.setBillingDocumentDate(entity.getBillingDocDate());
		dto.setMigoNumber(entity.getMigoNumber());
		dto.setMigoDate(entity.getMigoDate());
		dto.setMiroNumber(entity.getMiroNumber());
		dto.setMiroDate(entity.getMiroDate());
		dto.setExpenseGLMapping(entity.getExpenseGlMapping());
		dto.setSegment(entity.getSegment());
		dto.setGeoLevel(entity.getGeoLevel());
		dto.setStateName(entity.getStateName());
		dto.setUserID(entity.getUserId());
		dto.setParkedBy(entity.getParkedBy());
		dto.setEntryDate(entity.getEntryDate());
		dto.setTimeofEntry(entity.getTimeOfEntry());
		dto.setRemarks(entity.getRemarks());
		dto.setUserDefinedField1(entity.getUserDefinedField1());
		dto.setUserDefinedField2(entity.getUserDefinedField2());
		dto.setUserDefinedField3(entity.getUserDefinedField3());
		dto.setUserDefinedField4(entity.getUserDefinedField4());
		dto.setUserDefinedField5(entity.getUserDefinedField5());
		dto.setUserDefinedField6(entity.getUserDefinedField6());
		dto.setUserDefinedField7(entity.getUserDefinedField7());
		dto.setUserDefinedField8(entity.getUserDefinedField8());
		dto.setUserDefinedField9(entity.getUserDefinedField9());
		dto.setUserDefinedField10(entity.getUserDefinedField10());
		dto.setDigigstErrorDesc(entity.getErrorDesc());

		return dto;
	}

}
