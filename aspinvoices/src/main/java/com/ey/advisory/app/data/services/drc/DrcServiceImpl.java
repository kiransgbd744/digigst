package com.ey.advisory.app.data.services.drc;

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

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.caches.DefaultDrcReasonCache;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.entities.drc.TblDrcChallanDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcGetRetCompListDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcSaveStatus;
import com.ey.advisory.app.data.entities.drc.TblUserDrcReasonDetails;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcChallanDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcGetRetCompListDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcSaveStatusRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblUserDrcReasonDetailsRepo;
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

/**
 * 
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("DrcServiceImpl")
public class DrcServiceImpl implements DrcService {

	@Autowired
	TblDrcDetailsRepo drcDetailsRepo;

	@Autowired
	TblDrcChallanDetailsRepo drcChallanDetailsRepo;

	@Autowired
	TblDrcSaveStatusRepo drcSaveStatusRepo;

	@Autowired
	TblUserDrcReasonDetailsRepo drcUserReasonRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	DefaultDrcReasonCache reasonCache;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	TblDrcGetRetCompListDetailsRepo drcCompListDetailsRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	DefaultStateCache defaultStateCache;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public DrcGetRespDto getDRCGetDetails(List<String> gstinList,
			String taxPeriod, Long entityId, String refId) {
		try {
			List<TblDrcSaveStatus> saveGstinList = drcSaveStatusRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod);
			// List<GstrReturnStatusEntity> signedStatusP = returnstatusRepo
			// .findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstinFalse(
			// gstinList, APIIdentifiers.DRC,
			// Arrays.asList(taxPeriod));

			Map<String, TblDrcGetRetCompListDetails> refIdGstinMap = drcCompListDetailsRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream()
					.collect(Collectors.toMap(
							TblDrcGetRetCompListDetails::getGstin,
							Function.identity()));

			Map<String, TblDrcDetails> gstinGetDetailsMap = drcDetailsRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream().collect(Collectors.toMap(TblDrcDetails::getGstin,
							Function.identity()));

			Map<String, List<TblDrcChallanDetails>> gstinChallanMap = drcChallanDetailsRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream().collect(Collectors
							.groupingBy(TblDrcChallanDetails::getGstin));

			Map<String, List<TblUserDrcReasonDetails>> userReasonsMap = drcUserReasonRepo
					.findByGstinInAndTaxPeriodAndIsActiveTrue(gstinList,
							taxPeriod)
					.stream().collect(Collectors
							.groupingBy(TblUserDrcReasonDetails::getGstin));

			Map<String, Pair<String, LocalDateTime>> saveGstinMap = saveGstinList
					.stream()
					.collect(Collectors.toMap(TblDrcSaveStatus::getGstin,
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
			/*	screenDetailsDto.setReasonUsr(userReasonsMap
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
			Map<String, TblDrcDetails> gstinGetDetailsMap) {
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

	private void setRefIdStatus(String gstin,
			Map<String, TblDrcGetRetCompListDetails> refIdGstinMap,
			DrcGetCompSummaryDetails screenDetailsDto) {
		if (refIdGstinMap.containsKey(gstin)) {
			TblDrcGetRetCompListDetails retCompListDtls = refIdGstinMap
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
		Optional<TblDrcGetRetCompListDetails> compList = drcCompListDetailsRepo
				.findByGstinAndTaxPeriodAndIsActiveTrue(gstin, taxPeriod);
		if (compList.isPresent()) {
			if (compList.get().getGetStatus()
					.equalsIgnoreCase(APIConstants.SUCCESS)) {
				Optional<GetAnx1BatchEntity> batchEntity = batchRepo
						.findByTypeAndApiSectionAndSgstinAndTaxPeriodAndIsDeleteFalse(
								APIIdentifiers.DRC_GETRETCOMP_SUMMARY,
								APIIdentifiers.DRC.toUpperCase(), gstin,
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
			Map<String, List<TblDrcChallanDetails>> challanDetails) {
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
	
	
	private List<UsrReasonList> createReasonList(String gstin,
			Map<String, List<TblUserDrcReasonDetails>> userReasonsMap) {
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

			List<TblUserDrcReasonDetails> listOfReasons = new ArrayList<>();
			for (Reason reason : reasonDetails) {
				for (UsrReasonList usrRsnObj : reason.getReasonUsr()) {
					TblUserDrcReasonDetails reasonEntity = new TblUserDrcReasonDetails();
					reasonEntity.setGstin(reason.getGstin());
					reasonEntity.setTaxPeriod(reason.getTaxPeriod());
					reasonEntity.setReasonCode(usrRsnObj.getReadonCode());
					reasonEntity.setReasonDesc(
							reasonCache.getReasonDescription(usrRsnObj
									.getReadonCode()));
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
			String refId, List<TblDrcChallanDetails> diffDetails) {
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
		try {
			List<TblDrcChallanDetails> listOfChallanDtls = new ArrayList<>();
			for (TblDrcChallanDetails challanDtls : diffDetails) {
				TblDrcChallanDetails challanEntity = new TblDrcChallanDetails();
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