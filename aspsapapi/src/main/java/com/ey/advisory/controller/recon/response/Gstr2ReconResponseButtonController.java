package com.ey.advisory.controller.recon.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.asprecon.reconresponse.Gstr2ReconResponseButtonReqDto;
import com.ey.advisory.app.asprecon.reconresponse.Gstr2ReconResponseDashboardDto;
import com.ey.advisory.app.asprecon.reconresponse.Gstr2ReconResponseService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/*
 Recon Response 


*/
@RestController
@Slf4j
public class Gstr2ReconResponseButtonController {

	@Autowired
	Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr2ReconResponseServiceImpl")
	Gstr2ReconResponseService reconResponseService;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static ImmutableList<String> VALID_ITC_IDENTIFIER = ImmutableList
			.of("T1", "T2", "T3", "T4");
	private static ImmutableList<String> IMPG_REPORT_TYPES = ImmutableList.of(
			"Exact Match IMPG", "Mismatch IMPG", "Addition in 2B IMPG",
			"Addition in PR IMPG", "Addition in 2A IMPG");

	@RequestMapping(value = "/ui/gstr2ReconResponseButton", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2ReconResponseButton(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject reqObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		Long id = null;
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Gstr2ReconResponseButtonReqDto reqDto = gson.fromJson(reqJson,
					Gstr2ReconResponseButtonReqDto.class);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" inside gstr2ReconResponseButton for jsonReq %s",
						reqJson.toString());
				LOGGER.debug(msg);
			}

			switch (reqDto.getIndentifier()) {
			case "Force":
				id = createFileStatusEntity(reqDto, userName);
				if (impgRecordsValid(reqDto)) {
					throw new AppException(
							"Import record cannot be locked with Non-import record.");
				}
				break;
			case "3B":
				
				if (Strings.isNullOrEmpty(reqDto.getTaxPeriodGstr3b()))
					throw new AppException(
							"No Tax Period for GSTR-3B has been selected.");
				if (validateITCIdentifier(reqDto.getItcReversal())) {
					throw new AppException("Invalid ITC Reversal Identifier, Only T1/T2/T3/T4/Blank responses are allowed");
				} if (impgRecordsValid(reqDto)) {
					throw new AppException(
							"Import record cannot be locked with Non-import record.");
				} if (optedOptionGstr3BValidation(reqDto)){	
					throw new AppException(
							"Documents cannot be locked without corresponding GSTR-2B record.");
				}
			
				
				List<Gstr2ReconResponseButtonReqDto> validationResp = reconResponseService
						.validations(reqDto);

				if (validationResp.isEmpty()) {
					id = createFileStatusEntity(reqDto, userName);

				} else {
					JsonObject obj = new JsonObject();

					validationResp.sort(Comparator.comparing(
							Gstr2ReconResponseButtonReqDto::getType));

					obj.add("errorDesc", gson.toJsonTree(validationResp));
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.add("resp", obj);
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
				}
				break;
			}

			String reqId = getBatchid(id);
			List<Gstr2ReconResponseButtonReqDto> respList = reconResponseService
					.reconResponseLockProc(reqDto, Long.valueOf(reqId), id);
			if (respList.isEmpty()) {
				gstr1FileStatusRepository.updateFileStatus(id, "Completed");
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp",
						gson.toJsonTree("Records locked successfully"));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			} else {
				JsonObject obj = new JsonObject();
				gstr1FileStatusRepository.updateFileStatus(id, "Completed");
				obj.add("errorDesc", gson.toJsonTree(respList));
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", obj);
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
		} catch (Exception ex) {
			String msg = "Exception on gstr2ReconResultResponse" + ex;
			String msg1 = "Technical error while loading reconciliation data on screen. Please connect with EY Team for the solution";
			
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			gstr1FileStatusRepository.updateFailedStatus(id,
					ex.getLocalizedMessage(), "Failed");
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg1));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}

	private String getBatchid(Long fileId) {

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth))
				+ (currentDay < 10 ? ("0" + currentDay)
						: String.valueOf(currentDay));

		return currentDate.concat(String.valueOf(fileId));
	}

	private Long createFileStatusEntity(Gstr2ReconResponseButtonReqDto reqDto,
			String userName) {
		Gstr1FileStatusEntity savedEntity = new Gstr1FileStatusEntity();

		Gstr1FileStatusEntity entity = new Gstr1FileStatusEntity();

		entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
		entity.setTotal(0);
		entity.setFileStatus("In progress");
		entity.setProcessed(0);
		entity.setError(0);
		entity.setFileType("Recon Response");
		switch (reqDto.getIndentifier()) {

		case "Force":
			entity.setDataType("Force Match");
			break;

		case "3B":
			entity.setDataType("3B Response");
			break;
		}

		entity.setSource(reqDto.getReconType().equalsIgnoreCase("2A_PR")
				? "2A/6AvsPR" : "2BvsPR");
		entity.setUpdatedBy(userName);
		entity.setUpdatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		savedEntity = gstr1FileStatusRepository.save(entity);
		return savedEntity.getId();

	}

	private boolean validateITCIdentifier(String itcIdentifer) {

		if (Strings.isNullOrEmpty(itcIdentifer)
				|| VALID_ITC_IDENTIFIER.contains(itcIdentifer))
			return false;
		else
			return true;
	}

	private boolean impgRecordsValid(Gstr2ReconResponseButtonReqDto reqDto) {
		int count = 0;

		List<Gstr2ReconResponseDashboardDto> respList = reqDto.getRespList();
		for (Gstr2ReconResponseDashboardDto dto : respList) {
			if (dto.getReportType() != null
					&& IMPG_REPORT_TYPES.contains(dto.getReportType())) {
				count++;
			}
		}
		if (count != respList.size() && count > 0) {
			return true;
		} else
			return false;
	}

	private boolean optedOptionGstr3BValidation(
			Gstr2ReconResponseButtonReqDto reqDto) {

		if(reqDto.getReconType().equalsIgnoreCase("2A_PR"))
			return false;
		else{
		String optionOpted = "A";
		optionOpted = onbrdOptionOpted(Long.valueOf(reqDto.getEntityId()));

		if ("B".equalsIgnoreCase(optionOpted) || "C".equalsIgnoreCase(optionOpted)) {

			List<Gstr2ReconResponseDashboardDto> respList = reqDto
					.getRespList();
			List<Gstr2ReconResponseDashboardDto> respListPr = new ArrayList<>();
			
			List<Gstr2ReconResponseDashboardDto> respList2b = new ArrayList<>();

			respListPr = respList.stream()
					.filter(s -> s.getSource().equals("PR"))
					.collect(Collectors.toList());
			
			respList2b = respList.stream()
			.filter(s -> s.getSource().equals("2B"))
			.collect(Collectors.toList());
			
			
			if(!respListPr.isEmpty() && respList2b.isEmpty() ) {
				return true;
			} else
				return false;

		} else
			return false;
		}
	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

}
