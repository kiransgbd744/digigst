package com.ey.advisory.app.data.services.drc01c;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.caches.DefaultDrc01cReasonCache;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cChallanDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cGetRetCompListDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cSaveStatus;
import com.ey.advisory.app.data.entities.drc01c.TblUserDrc01cReasonDetails;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cChallanDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cGetRetCompListDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cSaveStatusRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblUserDrc01cReasonDetailsRepo;
import com.ey.advisory.app.data.services.drc.DifferenceAmtDetails;
import com.ey.advisory.app.data.services.drc.DifferentialDetail;
import com.ey.advisory.app.data.services.drc.DrcGetCompSummaryDetails;
import com.ey.advisory.app.data.services.drc.DrcGetRespDto;
import com.ey.advisory.app.data.services.drc.Reason;
import com.ey.advisory.app.data.services.drc.UsrReasonList;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component("Drc01cServiceImpl")
public class Drc01cServiceImpl implements Drc01cService {

	@Autowired
	TblDrc01cDetailsRepo drcDetailsRepo;

	@Autowired
	TblDrc01cChallanDetailsRepo drcChallanDetailsRepo;

	@Autowired
	TblDrc01cSaveStatusRepo drcSaveStatusRepo;

	@Autowired
	TblUserDrc01cReasonDetailsRepo drcUserReasonRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	@Qualifier("DefaultDrc01cReasonCache")
	DefaultDrc01cReasonCache reasonCache;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	TblDrc01cGetRetCompListDetailsRepo drcCompListDetailsRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	DefaultStateCache defaultStateCache;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private CommonUtility commonUtility;


	@Transactional(value = "clientTransactionManager")
	@Override
	public DrcGetRespDto getDRCGetDetails(List<String> gstinList,
			String taxPeriod, Long entityId, String refId) {
		try {
			List<TblDrc01cSaveStatus> saveGstinList = drcSaveStatusRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod);
			// List<GstrReturnStatusEntity> signedStatusP = returnstatusRepo
			// .findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstinFalse(
			// gstinList, APIIdentifiers.DRC,
			// Arrays.asList(taxPeriod));

			Map<String, TblDrc01cGetRetCompListDetails> refIdGstinMap = drcCompListDetailsRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream()
					.collect(Collectors.toMap(
							TblDrc01cGetRetCompListDetails::getGstin,
							Function.identity()));

			
			Map<String, TblDrc01cDetails> gstinGetDetailsMap = drcDetailsRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream().collect(Collectors.toMap(
							TblDrc01cDetails::getGstin, Function.identity()));

			Map<String, List<TblDrc01cChallanDetails>> gstinChallanMap = drcChallanDetailsRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream().collect(Collectors
							.groupingBy(TblDrc01cChallanDetails::getGstin));

			Map<String, List<TblUserDrc01cReasonDetails>> userReasonsMap = drcUserReasonRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream().collect(Collectors
							.groupingBy(TblUserDrc01cReasonDetails::getGstin));

			Map<String, Pair<String, LocalDateTime>> saveGstinMap = saveGstinList
					.stream()
					.collect(Collectors.toMap(TblDrc01cSaveStatus::getGstin,
							e -> new Pair<>(e.getStatus(), e.getCreatedOn())));
			// Map<String, Pair<String, LocalDateTime>> gstrReturnStatusMap =
			// signedStatusP
			// .stream()
			// .collect(Collectors.toMap(GstrReturnStatusEntity::getGstin,
			// e -> new Pair<>(e.getStatus(), e.getCreatedOn())));

			DrcGetRespDto getRespDetails = new DrcGetRespDto();
			List<DrcGetCompSummaryDetails> resDtoDtls = new ArrayList<>();
			Map<String, String> gstinAuthMap = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstins(gstinList);
			Map<String, String> regTypeMap = commonUtility.getGstnRegMap()
					.getValue1();

			for (String gstin : gstinList) {
				DrcGetCompSummaryDetails screenDetailsDto = new DrcGetCompSummaryDetails();
				screenDetailsDto.setAuthStatus(gstinAuthMap.get(gstin));
				screenDetailsDto.setGstin(gstin);
				screenDetailsDto.setRtnprd(taxPeriod);
				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				screenDetailsDto.setStateName(stateName);
				if (!regTypeMap.isEmpty()) {
					String regTypeName = regTypeMap.get(gstin);
					if (!Strings.isNullOrEmpty(regTypeName)) {
						screenDetailsDto.setRegType(regTypeName.toUpperCase());
					} else {
						screenDetailsDto.setRegType("");
					}
				}
				setRefIdStatus(gstin, refIdGstinMap, screenDetailsDto);
				DifferenceAmtDetails diffAmtDto = createDifferenceAmtDetails(
						gstin, gstinGetDetailsMap);
				screenDetailsDto.setDiffAmtDetails(diffAmtDto);
				updateGetStatus(gstin, taxPeriod, screenDetailsDto);
				List<DifferentialDetail> differentialDetails = createDifferentialDetailsList(
						gstin, gstinChallanMap);
				screenDetailsDto
						.setDifferentialDetailsList(differentialDetails);
				/*screenDetailsDto.setReasonUsr(userReasonsMap
						.getOrDefault(gstin, Collections.emptyList()).stream()
						.map(obj -> obj.getReasonCode())
						.collect(Collectors.toList()));*/
				
				List<UsrReasonList> reasonList = 
						createReasonList(gstin, userReasonsMap);
						screenDetailsDto.setReasonUsr(reasonList);
						
				setSaveStatusAndTime(gstin, screenDetailsDto, saveGstinMap);
				setFilingStatusAndTime(gstin, taxPeriod, screenDetailsDto);

				resDtoDtls.add(screenDetailsDto);
			}
			getRespDetails.setRespList(resDtoDtls);
			Collections.sort(resDtoDtls,
					Comparator.comparing(DrcGetCompSummaryDetails::getGstin));
			if (APIConstants.NO_REF_ID.equalsIgnoreCase(refId)) {
				List<DrcGetCompSummaryDetails> filteredList = getRespDetails
						.getRespList().stream()
						.filter(obj -> obj.getRefId()
								.equalsIgnoreCase(APIConstants.NO_REF_ID))
						.collect(Collectors.toList());
				getRespDetails.setRespList(filteredList);
			} else if (!Strings.isNullOrEmpty(refId)
					&& !APIConstants.NO_REF_ID.equalsIgnoreCase(refId)) {
				List<DrcGetCompSummaryDetails> filteredList = getRespDetails
						.getRespList().stream()
						.filter(obj -> !obj.getRefId()
								.equalsIgnoreCase(APIConstants.NO_REF_ID))
						.collect(Collectors.toList());
				getRespDetails.setRespList(filteredList);
			}

			return getRespDetails;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Fetching the data for TaxPeriod %s",
					taxPeriod);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private DifferenceAmtDetails createDifferenceAmtDetails(String gstin,
			Map<String, TblDrc01cDetails> gstinGetDetailsMap) {
		DifferenceAmtDetails diffAmtDto = new DifferenceAmtDetails();
		if (gstinGetDetailsMap.containsKey(gstin)) {
			diffAmtDto.setTtl(gstinGetDetailsMap.get(gstin).getTtlDiffAmt());
			diffAmtDto.setIgst(gstinGetDetailsMap.get(gstin).getIgstDiffAmt());
			diffAmtDto.setSgst(gstinGetDetailsMap.get(gstin).getSgstDiffAmt());
			diffAmtDto.setCgst(gstinGetDetailsMap.get(gstin).getCgstDiffAmt());
			diffAmtDto.setCess(gstinGetDetailsMap.get(gstin).getCessDiffAmt());
			return diffAmtDto;
		} else {
			return diffAmtDto;
		}
	}
	
	private List<UsrReasonList> createReasonList(String gstin,
			Map<String, List<TblUserDrc01cReasonDetails>> userReasonsMap) {
		if (userReasonsMap.containsKey(gstin)) {
			return userReasonsMap.get(gstin).stream()
					.map(reasonDeatils -> {
						UsrReasonList dto = new UsrReasonList();
						dto.setExplanation(reasonDeatils.getExplanation());
						dto.setId(reasonDeatils.getId());
						dto.setReadonCode(reasonDeatils.getReasonCode());
						return dto;
					}).collect(Collectors.toList());
		} else {
			return new ArrayList<UsrReasonList>();
		}

	}

	private void setRefIdStatus(String gstin,
			Map<String, TblDrc01cGetRetCompListDetails> refIdGstinMap,
			DrcGetCompSummaryDetails screenDetailsDto) {
		if (refIdGstinMap.containsKey(gstin)) {
			TblDrc01cGetRetCompListDetails retCompListDtls = refIdGstinMap
					.get(gstin);
			if (retCompListDtls.getGetStatus()
					.equalsIgnoreCase(APIConstants.FAILED)) {
				screenDetailsDto.setRefId(null);
			} else if (retCompListDtls.getGetStatus()
					.equalsIgnoreCase(APIConstants.SUCCESS_WITH_NO_DATA)) {
				screenDetailsDto.setRefId(APIConstants.NO_REF_ID);
			} else {
				screenDetailsDto.setRefId(retCompListDtls.getRefId());
			}
		} else {
			screenDetailsDto.setRefId(APIConstants.NO_REF_ID);
		}

	}

	private void updateGetStatus(String gstin, String taxPeriod,
			DrcGetCompSummaryDetails screenDetailsDto) {
		Optional<TblDrc01cGetRetCompListDetails> compList = drcCompListDetailsRepo
				.findByGstinAndTaxPeriodAndIsActiveTrue(gstin, taxPeriod);
		if (compList.isPresent()) {
			if (compList.get().getGetStatus()
					.equalsIgnoreCase(APIConstants.SUCCESS)) {
				Optional<GetAnx1BatchEntity> batchEntity = batchRepo
						.findByTypeAndApiSectionAndSgstinAndTaxPeriodAndIsDeleteFalse(
								APIIdentifiers.DRC01C_GETRETCOMP_SUMMARY,
								APIIdentifiers.DRC01C.toUpperCase(), gstin,
								taxPeriod);
				if (batchEntity.isPresent()) {
					screenDetailsDto
							.setGetCallStatus(batchEntity.get().getStatus());
					screenDetailsDto.setGetCallTime(
							EYDateUtil.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(
									batchEntity.get().getCreatedOn())));
				} else {
					screenDetailsDto
							.setGetCallStatus(compList.get().getGetStatus());
					screenDetailsDto.setGetCallTime(
							EYDateUtil.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(
									compList.get().getCreatedOn())));
				}
			} else {
				screenDetailsDto
						.setGetCallStatus(compList.get().getGetStatus());
				screenDetailsDto.setGetCallTime(EYDateUtil.fmtDate3(EYDateUtil
						.toISTDateTimeFromUTC(compList.get().getCreatedOn())));
			}
		} else {
			screenDetailsDto.setGetCallStatus(APIConstants.NOT_INITIATED);
		}
	}

	private List<DifferentialDetail> createDifferentialDetailsList(String gstin,
			Map<String, List<TblDrc01cChallanDetails>> challanDetails) {
		if (challanDetails.containsKey(gstin)) {
			return challanDetails.get(gstin).stream()
					.map(tblDrcChallanDetails -> {
						DifferentialDetail differentialDto = new DifferentialDetail();
						differentialDto.setDrc03arn(
								tblDrcChallanDetails.getDrcArnNo());
						differentialDto.setChallanNo(
								tblDrcChallanDetails.getChallanNo());
						differentialDto.setChallanDate(EYDateUtil.fmtDateOnly(
								tblDrcChallanDetails.getChallanDate(),
								DateUtil.SUPPORTED_DATE_FORMAT4));
						differentialDto
								.setIgst(tblDrcChallanDetails.getIgstAmt());
						differentialDto
								.setSgst(tblDrcChallanDetails.getSgstAmt());
						differentialDto
								.setCgst(tblDrcChallanDetails.getCgstAmt());
						differentialDto
								.setCess(tblDrcChallanDetails.getCessAmt());
						return differentialDto;
					}).collect(Collectors.toList());
		} else {
			return new ArrayList<DifferentialDetail>();
		}

	}

	private void setSaveStatusAndTime(String gstin,
			DrcGetCompSummaryDetails screenDetailsDto,
			Map<String, Pair<String, LocalDateTime>> saveGstinMap) {
		if (saveGstinMap.containsKey(gstin)) {
			String status = saveGstinMap.get(gstin).getValue0();
			LocalDateTime createdOn = saveGstinMap.get(gstin).getValue1();
			// if (APIConstants.SUCCESS.equalsIgnoreCase(status)) {
			screenDetailsDto.setSaveStatus(status.toUpperCase());
			screenDetailsDto.setSaveStatusTime(EYDateUtil
					.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(createdOn)));
		} else {
			screenDetailsDto.setSaveStatus(APIConstants.NOT_INITIATED);
		}
	}

	private void setFilingStatusAndTime(String gstin, String taxPeriod,
			DrcGetCompSummaryDetails screenDetailsDto) {

		SignAndFileEntity signAndFileEntity = signAndFileRepo
				.findFirstByGstinAndTaxPeriodAndReturnTypeOrderByIdDesc(gstin,
						taxPeriod, APIIdentifiers.DRC);
		if (signAndFileEntity != null) {
			if (signAndFileEntity.getStatus()
					.equalsIgnoreCase(APIConstants.SUCCESS)) {
				screenDetailsDto.setFilingStatus(APIConstants.FILED);
			} else {
				screenDetailsDto.setFilingStatus(
						signAndFileEntity.getStatus().toUpperCase());
				screenDetailsDto.setFilingStatusTime(
						EYDateUtil.fmtDate3(EYDateUtil.toISTDateTimeFromUTC(
								signAndFileEntity.getCreatedOn())));
			}
		} else {
			screenDetailsDto.setFilingStatus(APIConstants.NOT_INITIATED);
		}
	}

	@Override
	public String saveReasonDetails(List<Reason> reasonDetails) {
		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";

			List<TblUserDrc01cReasonDetails> listOfReasons = new ArrayList<>();
			for (Reason reason : reasonDetails) {
				for (UsrReasonList usrRsnObj : reason.getReasonUsr()) {
					TblUserDrc01cReasonDetails reasonEntity = new TblUserDrc01cReasonDetails();
				    reasonEntity.setGstin(reason.getGstin());
					reasonEntity.setTaxPeriod(reason.getTaxPeriod());
					reasonEntity.setReasonCode(usrRsnObj.getReadonCode());
					reasonEntity.setReasonDesc(
							reasonCache.getReasonDescription(usrRsnObj.getReadonCode()));
					reasonEntity.setExplanation(usrRsnObj.getExplanation());
					reasonEntity.setIsActive(true);
					reasonEntity.setCreatedBy(userName);
					reasonEntity.setCreatedOn(LocalDateTime.now());
					listOfReasons.add(reasonEntity);
				}
			}
			
				drcUserReasonRepo.inActivateRecords(
						reasonDetails.stream().map(obj -> obj.getGstin())
								.collect(Collectors.toList()),
						reasonDetails.get(0).getTaxPeriod(), userName);
				
			if (!listOfReasons.isEmpty()) {
				drcUserReasonRepo.saveAll(listOfReasons);
			}
			return "Records Updated Successfully.";
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while saving the Reason Details for TaxPeriod %s",
					reasonDetails.get(0).getTaxPeriod());
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	@Override
	public String saveDifferentialDetails(String gstin, String taxPeriod,
			String refId, List<TblDrc01cChallanDetails> diffDetails) {
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
		try {
			List<TblDrc01cChallanDetails> listOfChallanDtls = new ArrayList<>();
			for (TblDrc01cChallanDetails challanDtls : diffDetails) {
				TblDrc01cChallanDetails challanEntity = new TblDrc01cChallanDetails();
				challanEntity.setGstin(gstin);
				challanEntity.setTaxPeriod(taxPeriod);
				challanEntity.setRefId(refId);
				challanEntity.setDrcArnNo(challanDtls.getDrcArnNo());
				challanEntity.setChallanNo(challanDtls.getChallanNo());
				challanEntity.setChallanDate(DateUtil.tryConvertUsingFormat(
						challanDtls.getChallanDateStr(),
						DateUtil.SUPPORTED_DATE_FORMAT4));
				challanEntity.setIgstAmt(challanDtls.getIgstAmt());
				challanEntity.setCgstAmt(challanDtls.getCgstAmt());
				challanEntity.setSgstAmt(challanDtls.getCgstAmt());
				challanEntity.setCessAmt(challanDtls.getSgstAmt());
				challanEntity.setIsActive(true);
				challanEntity.setCreatedBy(userName);
				challanEntity.setCreatedOn(LocalDateTime.now());
				listOfChallanDtls.add(challanEntity);
			}
			if (!listOfChallanDtls.isEmpty()) {
				drcChallanDetailsRepo.inActivateRecords(gstin, taxPeriod,
						userName);
				drcChallanDetailsRepo.saveAll(listOfChallanDtls);
			}
			return "Records Saved Successfully.";
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while saving the Reason Details for TaxPeriod %s",
					taxPeriod);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

}