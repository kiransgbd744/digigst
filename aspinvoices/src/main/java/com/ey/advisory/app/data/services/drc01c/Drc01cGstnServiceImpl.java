package com.ey.advisory.app.data.services.drc01c;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.drc.TblDrc01AutoGetCallDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cGetRetCompListDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cPaymentDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cReasonDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cSuppDocDetails;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01AutoGetCallDetailsRepository;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cChallanDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cGetRetCompListDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cSaveStatusRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblUserDrc01cReasonDetailsRepo;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.app.data.services.drc.DifferenceAmtDetails;
import com.ey.advisory.app.data.services.drc.DifferentialDetail;
import com.ey.advisory.app.data.services.drc.DifferentialHeadDetail;
import com.ey.advisory.app.data.services.drc.DrcGetCompSummaryDetails;
import com.ey.advisory.app.data.services.drc.DrcGetRetCompListRespDto;
import com.ey.advisory.app.data.services.drc.Reason;
import com.ey.advisory.app.data.services.drc.SuppDocdetailsDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Drc01cGstnServiceImpl")
public class Drc01cGstnServiceImpl implements Drc01cGstnService {

	@Autowired
	TblDrc01cDetailsRepo drcDetailsRepo;

	@Autowired
	TblDrc01cGetRetCompListDetailsRepo drcCompListDetailsRepo;

	@Autowired
	TblDrc01cChallanDetailsRepo drcChallanDetailsRepo;

	@Autowired
	TblDrc01cSaveStatusRepo drcSaveStatusRepo;

	@Autowired
	TblUserDrc01cReasonDetailsRepo drcUserReasonRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	GstnCommonServiceUtil gstnCommonUtil;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Drc01AutoGetCallDetailsRepository")
	private Drc01AutoGetCallDetailsRepository drc01AutoGetCallDetailsRepo;

	private static final String GSTR2B = "GSTR-2B";
	private static final String GSTR3B = "GSTR-3B";

	private static final List<String> ERROR_CODES = ImmutableList
			.of("SPIKE004");

	@Override
	public JsonArray getDrcRetComList(List<String> gstinList, String taxPeriod,
			boolean isPeriodicJob, Long entityId) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
		JsonArray respBody = new JsonArray();
		List<TblDrc01cGetRetCompListDetails> drcGetRetCompList = new ArrayList<>();
		List<AsyncExecJob> getDrcSummaryAsyncJobs = new ArrayList<>();
		try {
			Map<String, String> gstinAuthMap = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstins(gstinList);
			Map<String, JsonObject> responseMap = new HashMap<>();
			for (String gstin : gstinList) {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", gstin);
				if (gstinAuthMap.get(gstin).equalsIgnoreCase("I")) {
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate.");
					if (isPeriodicJob) {
						/*
						 * TblDrc01cGetRetCompListDetails drcDetails = new
						 * TblDrc01cGetRetCompListDetails();
						 * drcDetails.setGstin(gstin);
						 * drcDetails.setTaxPeriod(taxPeriod);
						 * drcDetails.setIsActive(true);
						 * drcDetails.setCreatedBy("PERIODIC");
						 * drcDetails.setCreatedOn(LocalDateTime.now());
						 * drcDetails.setGetPayload("Auth Token is Inactive.");
						 */
						TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

						entity.setCommType(APIConstants.DRC01C.toUpperCase());
						entity.setCreatedOn(LocalDateTime.now());
						entity.setTaxPeriod(taxPeriod);
						entity.setGstin(gstin);
						entity.setCreatedBy(userName);
						entity.setRemark("Auth Token is Inactive");
						entity.setEntityId(entityId);

						drc01AutoGetCallDetailsRepo.save(entity);
					}
				} else {
					APIResponse getDrcRetCompListResp = getDrcApiResp(gstin,
							taxPeriod, null);
					TblDrc01cGetRetCompListDetails drcDetails = new TblDrc01cGetRetCompListDetails();
					drcDetails.setGstin(gstin);
					drcDetails.setTaxPeriod(taxPeriod);
					drcDetails.setIsActive(true);
					drcDetails.setCreatedBy(userName);
					drcDetails.setCreatedOn(LocalDateTime.now());
					if (getDrcRetCompListResp.isSuccess()) {
						DrcGetRetCompListRespDto compListResp = gson.fromJson(
								getDrcRetCompListResp.getResponse(),
								DrcGetRetCompListRespDto.class);
						drcDetails.setGetPayload(
								getDrcRetCompListResp.getResponse());
						if (!compListResp.getRefidlist().isEmpty()) {
							drcDetails.setRefId(compListResp.getRefidlist()
									.get(0).getRefId());
							drcDetails.setTaxPayerStatus(compListResp
									.getRefidlist().get(0).getStatus());
							drcDetails.setGetStatus(APIConstants.SUCCESS);
							json.addProperty("msg",
									"Request has been taken for Get Call, Please check the Get Status.");
							if(isPeriodicJob){
							TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

							entity.setCommType(APIConstants.DRC01C.toUpperCase());
							entity.setGetCallDate(LocalDateTime.now());
							entity.setCreatedBy(userName);
							entity.setRefId(compListResp.getRefidlist().get(0)
									.getRefId());
							entity.setTaxPeriod(taxPeriod);
							entity.setGstin(gstin);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setEntityId(entityId);

							drc01AutoGetCallDetailsRepo.save(entity);
							}
							getDRCRetCompSummary(gstin,
									compListResp.getRefidlist().get(0)
											.getRefId(),
									taxPeriod, userName,
									getDrcSummaryAsyncJobs);
						} else {
							drcDetails.setGetStatus(
									APIConstants.SUCCESS_WITH_NO_DATA);
							
							if(isPeriodicJob){
								TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

								entity.setCommType(APIConstants.DRC01C.toUpperCase());
								entity.setGetCallDate(LocalDateTime.now());
								entity.setCreatedBy(userName);
								entity.setTaxPeriod(taxPeriod);
								entity.setGstin(gstin);
								entity.setCreatedOn(LocalDateTime.now());
								entity.setEntityId(entityId);

								drc01AutoGetCallDetailsRepo.save(entity);
								}
						}
					} else {
						String errMsg = "";
						if (ERROR_CODES.contains(getDrcRetCompListResp
								.getError().getErrorCode())) {
							errMsg = String.format("No Records Found.",
									getDrcRetCompListResp.getError()
											.getErrorDesc());
							drcDetails.setGetStatus(
									APIConstants.SUCCESS_WITH_NO_DATA);
							if(isPeriodicJob){
							TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

							entity.setCommType(APIConstants.DRC01C.toUpperCase());
							entity.setGetCallDate(LocalDateTime.now());
							entity.setCreatedBy(userName);
							entity.setTaxPeriod(taxPeriod);
							entity.setGstin(gstin);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setErrorCode(getDrcRetCompListResp.getError()
									.getErrorCode());
							entity.setErrorDesc(getDrcRetCompListResp.getError()
									.getErrorDesc());
							entity.setEntityId(entityId);

							drc01AutoGetCallDetailsRepo.save(entity);
							}
						} else {
							errMsg = String.format(
									"Get Call Failed, GSTN has Returned Error Response %s",
									getDrcRetCompListResp.getError()
											.getErrorDesc());
							drcDetails.setGetStatus(APIConstants.FAILED);
							if(isPeriodicJob){
							TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

							entity.setCommType(APIConstants.DRC01C.toUpperCase());
							entity.setGetCallDate(LocalDateTime.now());
							entity.setCreatedBy(userName);
							entity.setTaxPeriod(taxPeriod);
							entity.setGstin(gstin);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setErrorCode(getDrcRetCompListResp.getError()
									.getErrorCode());
							entity.setErrorDesc(getDrcRetCompListResp.getError()
									.getErrorDesc());
							entity.setEntityId(entityId);

							drc01AutoGetCallDetailsRepo.save(entity);
							}
						}
						LOGGER.debug(errMsg);
						drcDetails.setGetPayload(String.format(
								"Get Call Failed, Error Code %s - ErrorDesc %s",
								getDrcRetCompListResp.getError().getErrorCode(),
								getDrcRetCompListResp.getError()
										.getErrorDesc()));
						json.addProperty("msg", errMsg);
					}
					drcGetRetCompList.add(drcDetails);
					if (!drcGetRetCompList.isEmpty()) {
						drcCompListDetailsRepo.inActivateRecords(gstinList,
								taxPeriod, userName);
						drcCompListDetailsRepo.saveAll(drcGetRetCompList);
					}
					if (!getDrcSummaryAsyncJobs.isEmpty()) {
						asyncJobsService.createJobs(getDrcSummaryAsyncJobs);
					}
			
				}

				responseMap.put(gstin, json);
			}
			for (String gstin : gstinList) {
				respBody.add(responseMap.get(gstin));
			}
			return respBody;
		} catch (Exception e) {
			String errMsg = "Exception while making the get call.";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
	}

	@Override
	public void getDrcRetComSummary(String gstin, String taxPeriod,
			String refId, String userName) {
		APIResponse getDrcRetCompSummResp = getDrcApiResp(gstin, taxPeriod,
				refId);
		Long batchId = createBatchAndSave(gstin, taxPeriod, "");
		try {
			LOGGER.debug(getDrcRetCompSummResp.getResponse());
			LOGGER.debug(getDrcRetCompSummResp.getError().getErrorDesc());
			gstnCommonUtil.saveOrUpdateGstnUserRequest(gstin, taxPeriod,
					getDrcRetCompSummResp.getResponse(), APIIdentifiers.DRC01C);
			if (getDrcRetCompSummResp.isSuccess()) {
				processSuccessResponse(getDrcRetCompSummResp.getResponse(),
						gstin, taxPeriod, refId, userName);
				batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
						false);
			} else {
				processErrorResponse(getDrcRetCompSummResp, batchId);
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while making the Get Summary summary: %s",
					e.getMessage());
			LOGGER.debug(errMsg);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			throw new AppException(errMsg, e);
		}
	}

	public APIResponse getDrcApiResp(String gstin, String taxPeriod,
			String refId) {
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			if (Strings.isNullOrEmpty(refId)) {
				APIParam param3 = new APIParam("frmtyp", APIIdentifiers.DRC01C);
				APIParam param4 = new APIParam("rtnprd", taxPeriod);
				APIParams params = new APIParams(TenantContext.getTenantId(),
						APIProviderEnum.GSTN,
						APIIdentifiers.DRC_GETRETCOMP_LIST, param1, param2,
						param3, param4);
				return apiExecutor.execute(params, null);
			} else {
				APIParam param3 = new APIParam("refid", refId);
				APIParams params = new APIParams(TenantContext.getTenantId(),
						APIProviderEnum.GSTN,
						APIIdentifiers.DRC01C_GETRETCOMP_SUMMARY, param1,
						param2, param3);
				return apiExecutor.execute(params, null);
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking getDrcApiResp ,"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	public void getDRCRetCompSummary(String gstin, String refId,
			String taxPeriod, String userName,
			List<AsyncExecJob> getDRCRetCompSumJobs) {

		String groupCode = TenantContext.getTenantId();

		JsonObject jobParamsObj = new JsonObject();
		jobParamsObj.addProperty("gstin", gstin);
		jobParamsObj.addProperty("refId", refId);
		jobParamsObj.addProperty("taxPeriod", taxPeriod);
		jobParamsObj.addProperty("userName", userName);
		getDRCRetCompSumJobs.add(asyncJobsService.createJobAndReturn(groupCode,
				JobConstants.DRC01C_GETCOMP_SUMMARY, jobParamsObj.toString(),
				"SYSTEM", 1L, null, null));

	}

	private Long createBatchAndSave(String gstin, String retPeriod,
			String userName) {
		batchRepo.softlyDelete(APIIdentifiers.DRC01C_GETRETCOMP_SUMMARY,
				APIIdentifiers.DRC01C.toUpperCase(), gstin, retPeriod);
		GetAnx1BatchEntity batch = batchUtil.makeBatch(gstin, retPeriod,
				APIIdentifiers.DRC01C_GETRETCOMP_SUMMARY,
				APIIdentifiers.DRC01C.toUpperCase(), userName);
		batch = batchRepo.save(batch);
		return batch.getId();
	}

	private void processSuccessResponse(String response, String gstin,
			String taxPeriod, String refId, String userName) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		DrcGetCompSummaryDetails getSummaryResp = gson.fromJson(response,
				DrcGetCompSummaryDetails.class);
		TblDrc01cDetails drcDetails = new TblDrc01cDetails();
		drcDetails.setGstin(gstin);
		drcDetails.setTaxPeriod(taxPeriod);
		drcDetails.setRefId(refId);
		drcDetails.setTaxPayerStatus(getSummaryResp.getStatus());
		drcDetails.setReturnFilingFreq(getSummaryResp.getProfile());
		drcDetails.setIgstDiffAmt(getSummaryResp.getDiffAmtDetails().getIgst());
		drcDetails.setCgstDiffAmt(getSummaryResp.getDiffAmtDetails().getCgst());
		drcDetails.setSgstDiffAmt(getSummaryResp.getDiffAmtDetails().getSgst());
		drcDetails.setCessDiffAmt(getSummaryResp.getDiffAmtDetails().getCess());
		drcDetails.setTtlDiffAmt(getSummaryResp.getDiffAmtDetails().getTtl());
		drcDetails.setIsActive(true);
		drcDetails.setCreatedBy(userName);
		drcDetails.setCreatedOn(LocalDateTime.now());
		// Process mismatch details
		processMismatchDetails(getSummaryResp.getMismatchDetails(), drcDetails);
		// Process differential details
		processDifferentialDetails(getSummaryResp.getDifferentialDetailsList(),
				drcDetails);
		// Process reasons list
		processReasonsList(getSummaryResp.getReasons(), drcDetails);
		// Process supporting Doc Details
		processSuppDocList(getSummaryResp.getSuppDocDetails(), drcDetails,
				userName);

		drcDetailsRepo.inActivateRecords(gstin, taxPeriod, userName);
		drcDetailsRepo.save(drcDetails);
	}

	private void processDifferentialDetails(
			List<DifferentialDetail> differentialDetails,
			TblDrc01cDetails drcDetails) {
		List<TblDrc01cPaymentDetails> paymentDtlsList = new ArrayList<>();

		for (DifferentialDetail differentialPaymentDtls : differentialDetails) {
			TblDrc01cPaymentDetails paymentDtls = new TblDrc01cPaymentDetails();
			paymentDtls.setDrcArnNo(differentialPaymentDtls.getDrc03arn());
			paymentDtls.setDrc01cDetailsId(drcDetails);
			DifferentialHeadDetail headDetails = differentialPaymentDtls
					.getHead();
			if (headDetails.getOth() != null) {
				paymentDtls.setOthersIgstAmt(headDetails.getOth().getIgst());
				paymentDtls.setOthersCgstAmt(headDetails.getOth().getCgst());
				paymentDtls.setOthersSgstAmt(headDetails.getOth().getSgst());
				paymentDtls.setOthersCessAmt(headDetails.getOth().getCess());
				paymentDtls.setOthersTtlAmt(headDetails.getOth().getTtl());
			}
			if (headDetails.getTx() != null) {
				paymentDtls.setTaxIgstAmt(headDetails.getTx().getIgst());
				paymentDtls.setTaxCgstAmt(headDetails.getTx().getCgst());
				paymentDtls.setTaxSgstAmt(headDetails.getTx().getSgst());
				paymentDtls.setTaxCessAmt(headDetails.getTx().getCess());
				paymentDtls.setTaxTtlAmt(headDetails.getTx().getTtl());
			}
			if (headDetails.getFee() != null) {
				paymentDtls.setFeeIgstAmt(headDetails.getFee().getIgst());
				paymentDtls.setFeeCgstAmt(headDetails.getFee().getCgst());
				paymentDtls.setFeeSgstAmt(headDetails.getFee().getSgst());
				paymentDtls.setFeeCessAmt(headDetails.getFee().getCess());
				paymentDtls.setFeeTtlAmt(headDetails.getFee().getTtl());
			}
			if (headDetails.getIntr() != null) {
				paymentDtls.setInterestIgstAmt(headDetails.getIntr().getIgst());
				paymentDtls.setInterestCgstAmt(headDetails.getIntr().getCgst());
				paymentDtls.setInterestSgstAmt(headDetails.getIntr().getSgst());
				paymentDtls.setInterestCessAmt(headDetails.getIntr().getCess());
				paymentDtls.setInterestTtlAmt(headDetails.getIntr().getTtl());
			}
			if (headDetails.getPen() != null) {
				paymentDtls.setPenaltyIgstAmt(headDetails.getPen().getIgst());
				paymentDtls.setPenaltyCgstAmt(headDetails.getPen().getCgst());
				paymentDtls.setPenaltySgstAmt(headDetails.getPen().getSgst());
				paymentDtls.setPenaltyCessAmt(headDetails.getPen().getCess());
				paymentDtls.setPenaltyTtlAmt(headDetails.getPen().getTtl());
			}
			paymentDtlsList.add(paymentDtls);
		}
		if (!paymentDtlsList.isEmpty()) {
			drcDetails.setPaymentDetails(paymentDtlsList);
		}
	}

	private void processMismatchDetails(
			List<DifferenceAmtDetails> mismatchDetails,
			TblDrc01cDetails drcDetails) {
		for (DifferenceAmtDetails misMatcDtls : mismatchDetails) {
			String rtntyp = misMatcDtls.getRtntyp();
			if (rtntyp.equalsIgnoreCase(GSTR2B)
					|| rtntyp.equalsIgnoreCase("R2B")) {
				drcDetails.setGstr2BIgstAmt(misMatcDtls.getIgst());
				drcDetails.setGstr2BCgstAmt(misMatcDtls.getCgst());
				drcDetails.setGstr2BSgstAmt(misMatcDtls.getSgst());
				drcDetails.setGstr2BCessAmt(misMatcDtls.getCess());
				drcDetails.setGstr2BTtlAmt(misMatcDtls.getTtl());
			} else if (rtntyp.equalsIgnoreCase(GSTR3B)
					|| rtntyp.equalsIgnoreCase("R3B")) {
				drcDetails.setGstr3BIgstAmt(misMatcDtls.getIgst());
				drcDetails.setGstr3BCgstAmt(misMatcDtls.getCgst());
				drcDetails.setGstr3BSgstAmt(misMatcDtls.getSgst());
				drcDetails.setGstr3BCessAmt(misMatcDtls.getCess());
				drcDetails.setGstr3BTtlAmt(misMatcDtls.getTtl());
			} else {
				LOGGER.error("Invalid Report Type {}", rtntyp);
			}
		}
	}

	private void processReasonsList(List<Reason> reasons,
			TblDrc01cDetails drcDetails) {
		List<TblDrc01cReasonDetails> reasonsList = new ArrayList<>();

		for (Reason reasonDtls : reasons) {
			TblDrc01cReasonDetails reasonDetails = new TblDrc01cReasonDetails();
			reasonDetails.setDrc01cDetailsId(drcDetails);
			reasonDetails.setReasonCode(reasonDtls.getRsnco());
			reasonDetails.setReasonDesc(reasonDtls.getReasonDesc());
			reasonsList.add(reasonDetails);
		}

		if (!reasonsList.isEmpty()) {
			drcDetails.setReasonDetails(reasonsList);
		}
	}

	private void processSuppDocList(SuppDocdetailsDto suppDocDetails,
			TblDrc01cDetails drcDetails, String userName) {
		List<TblDrc01cSuppDocDetails> suppDocList = new ArrayList<>();

		TblDrc01cSuppDocDetails suppDocumentDetails = new TblDrc01cSuppDocDetails();
		suppDocumentDetails.setDrc01cDetailsId(drcDetails);
		suppDocumentDetails.setSuppDocId(suppDocDetails.getSuppDocId());
		suppDocumentDetails.setHash(suppDocDetails.getHash());
		suppDocumentDetails.setDocName(suppDocDetails.getDocName());
		suppDocumentDetails.setContentType(suppDocDetails.getContentType());
		suppDocumentDetails.setType(suppDocDetails.getType());
		suppDocumentDetails.setCreatedOn(LocalDateTime.now());
		suppDocumentDetails.setCreatedBy(userName);
		suppDocList.add(suppDocumentDetails);

		if (!suppDocList.isEmpty()) {
			drcDetails.setSuppDetails(suppDocList);
		}
	}

	private void processErrorResponse(APIResponse response, Long batchId) {
		String errMsg = String.format(
				"Get Call Failed, GSTN has Returned Error Response %s",
				response.getError().getErrorDesc());
		LOGGER.debug(errMsg);
		batchUtil.updateById(batchId, APIConstants.FAILED,
				response.getError().getErrorCode(),
				response.getError().getErrorDesc(), false);
	}

}