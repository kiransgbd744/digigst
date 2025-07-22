package com.ey.advisory.controller.gstr2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResult2BPRDao;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultDao;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultDto;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultService;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultValidations;
import com.ey.advisory.app.asprecon.gstr2.recon.result.ReconSummaryDto;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.GSTR2aAutoReconRevIntgService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@RestController
public class Gstr2ReconResultController {

	@Autowired
	@Qualifier("GSTR2aAutoReconRevIntgServiceImpl")
	private GSTR2aAutoReconRevIntgService service;

	@Autowired
	private Gstr2ReconResultValidations validService;

	@Autowired
	@Qualifier("Gstr2ReconResultServiceImpl")
	private Gstr2ReconResultService reconService;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("Gstr2ReconResultDaoImpl")
	private Gstr2ReconResultDao dao;

	@Autowired
	@Qualifier("Gstr2ReconResult2BPRDaoImpl")
	private Gstr2ReconResult2BPRDao dao2bpr;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@RequestMapping(value = "/ui/gstr2getReconResult", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResultData(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
		int pageNum = hdrObject.get("pageNum").getAsInt();
		int pageSize = hdrObject.get("pageSize").getAsInt();
		Integer totalCount = 0;
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<String> gstnsList = null;
		List<Gstr2ReconResultDto> recResponse = new ArrayList<>();
		List<ReconSummaryDto> summResponse = new ArrayList<>();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside Gstr2ReconResultController.getReconResultData() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);
		String optionOpted = "A";
		boolean optedMultiResponse = false;
		try {

			Gstr2ReconResultReqDto reqDto = gson.fromJson(json,
					Gstr2ReconResultReqDto.class);
			//168474
			 Optional<List<String>> optionalAccVoucherNos = Optional
					.ofNullable(reqDto.getAccVoucherNums());

			Optional<List<String>> optionalDocNumbers = Optional
					.ofNullable(reqDto.getDocNumberList());
			
			Optional<List<String>> optionalVendorGstins = Optional
					.ofNullable(reqDto.getVndrGstins());

		
			if (optionalAccVoucherNos.isPresent()) {

				 List<String> accVoucherNos = optionalAccVoucherNos.get(); // This is a List<String>
				    String accVoucherNoString = String.join(",", accVoucherNos);
				if (accVoucherNoString.length() > 2000) {
					APIRespDto dto = new APIRespDto("Failed",
							"Accounting Voucher Numbers have exceeded the limit of 2000 characters");
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					 msg = "Accounting Voucher Numbers have exceeded the limit of 2000 characters";
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					// LOGGER.error(msg, e);
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
				}
			}
			
			 if (optionalDocNumbers.isPresent()) {

					List<String> docNumber = optionalDocNumbers.get();
					 String docNumberString = String.join(",", docNumber);
					if (docNumberString.length() > 4000) {

						APIRespDto dto = new APIRespDto("Failed",
								"Document Numbers have exceeded the limit of 3000 characters");
						JsonObject resp = new JsonObject();
						JsonElement respBody = gson.toJsonTree(dto);
						 msg = "Document Numbers have exceeded the limit of 3000 characters";
						resp.add("hdr",
								new Gson().toJsonTree(new APIRespDto("E", msg)));
						resp.add("resp", respBody);
						return new ResponseEntity<String>(resp.toString(),
								HttpStatus.OK);
					}
				}
			 
			 if (optionalVendorGstins.isPresent()) {

				 List<String> vendorGstins = optionalVendorGstins.get(); // This is a List<String>
				    String vendorGstinsString = String.join(",", vendorGstins);
				if (vendorGstinsString.length() > 4000) {
					APIRespDto dto = new APIRespDto("Failed",
							"Vendor GSTIN has exceeded the limit of 3000 characters");
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					 msg = "Vendor GSTIN has exceeded the limit of 3000 characters";
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					// LOGGER.error(msg, e);
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
				}
			}
			
			Long entityId = Long.valueOf(reqDto.getEntityId());
			String reconType = reqDto.getReconType();

			List<String> rptTyp = reqDto.getReportType();

			if (!reqDto.getReportType().isEmpty() && reqDto.getReportType()
					.contains("Doc Number & Doc Date Mismatch")) {
				for (String rptType : reqDto.getReportType()) {
					if ("Doc Number & Doc Date Mismatch"
							.equalsIgnoreCase(rptType))
						continue;
					else
						rptTyp.add(rptType);
				}
				rptTyp.add("Doc No & Doc Date Mismatch");
				reqDto.setReportType(rptTyp);
			}

			optionOpted = onbrdOptionOpted(entityId);
			optedMultiResponse = onbrdMultiResponseOptionOpted(entityId);
	
			if (optionOpted.equalsIgnoreCase("A"))
				optionOpted = "A";
			else if (optionOpted.equalsIgnoreCase("B"))
				optionOpted = "B";
			else
				optionOpted = "C";

			if (reqDto.getGstins().isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Invoking service to get gstins for entity %s ",
							reqDto);
					LOGGER.debug(msg);
				}

				try {
					List<Long> entityIds = new ArrayList<>();
					entityIds.add(entityId);
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					reqDto.setGstins(gstnsList);
				} catch (Exception ex) {
					msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			}
			try {
				validService.validations(reqDto);
			} catch (Exception ex) {
				msg = "Validation Error Occured";
				LOGGER.error(msg, ex);
				JsonObject resp1 = new JsonObject();
				resp1.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp1.add("resp", gson.toJsonTree(ex.getMessage()));
				return new ResponseEntity<>(resp1.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}

			/*
			 * if (reqDto.getFrmTaxPrd3b() != null && reqDto.getToTaxPrd3b() !=
			 * null && (reqDto.getReportType().isEmpty())) {
			 * reqDto.getReportType().add(APIConstants.RESPONSE_B3); }
			 */

			JsonObject resps = new JsonObject();
			Triplet<
		    List<Gstr2ReconResultDto>,    
		    List<ReconSummaryDto>,        
		    Integer> resp = null;

			if ("2A_PR".equalsIgnoreCase(reconType)) {

				resp = dao.getReconResult(reqDto, pageNum, pageSize);
			} else {
				resp = dao2bpr.getReconResult2BPR(reqDto, pageNum, pageSize);
			}

			if (resp == null) {
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
								pageNum, pageSize, "S", "No Records Found")));
				resps.add("resp", gson.toJsonTree("No Records Found"));
				resps.add("optionOpted", gson.toJsonTree(optionOpted));
				resps.add("optedMultiResponse",
						gson.toJsonTree(optedMultiResponse));

			}

			else {
				recResponse = resp.getValue0();
				summResponse = resp.getValue1();
				totalCount = resp.getValue2();
				 ReconSummaryDto summaryObj = summResponse.isEmpty()
	                        ? null
	                        : summResponse.get(0);
				JsonObject respBody = new JsonObject();
				respBody.add("reconResponse", gson.toJsonTree(recResponse));
				respBody.add("summary", gson.toJsonTree(summaryObj));
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
								pageNum, pageSize, "S", "Successfully fetched Recon Results")));
				resps.add("resp", respBody);
				resps.add("optionOpted", gson.toJsonTree(optionOpted));
				resps.add("optedMultiResponse",
						gson.toJsonTree(optedMultiResponse));

			}

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("optionOpted", gson.toJsonTree(optionOpted));
			resp.add("optedMultiResponse", gson.toJsonTree(optedMultiResponse));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			String msg1 = "Technical error while loading reconciliation data on screen. Please connect with EY Team for the solution";

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg1));
			resp.add("optionOpted", gson.toJsonTree(optionOpted));
			resp.add("optedMultiResponse", gson.toJsonTree(optedMultiResponse));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
		}

	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

	private boolean onbrdMultiResponseOptionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"Whether Pagination should be disabled on screen Recon Result (Multi Unlock) for handling scenarios of more than 50 records locked together?");
		if ("A".equalsIgnoreCase(optAns))
			return true;
		else
			return false;
	}
	
}
