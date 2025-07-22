package com.ey.advisory.app.data.services.drc;

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
import com.ey.advisory.app.data.entities.drc.TblDrcDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcGetRetCompListDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcPaymentDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcReasonDetails;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01AutoGetCallDetailsRepository;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcChallanDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcGetRetCompListDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcSaveStatusRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblUserDrcReasonDetailsRepo;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
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

/**
 * 
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("DrcGstnServiceImpl")
public class DrcGstnServiceImpl implements DrcGstnService {

	@Autowired
	TblDrcDetailsRepo drcDetailsRepo;

	@Autowired
	TblDrcGetRetCompListDetailsRepo drcCompListDetailsRepo;

	@Autowired
	TblDrcChallanDetailsRepo drcChallanDetailsRepo;

	@Autowired
	TblDrcSaveStatusRepo drcSaveStatusRepo;

	@Autowired
	TblUserDrcReasonDetailsRepo drcUserReasonRepo;

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
	@Qualifier("Drc01AutoGetCallDetailsRepository")
	private Drc01AutoGetCallDetailsRepository drc01AutoGetCallDetailsRepo;

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

	private static final String GSTR1 = "GSTR-1";
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
		List<TblDrcGetRetCompListDetails> drcGetRetCompList = new ArrayList<>();
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
						 * TblDrcGetRetCompListDetails drcDetails = new
						 * TblDrcGetRetCompListDetails();
						 * drcDetails.setGstin(gstin);
						 * drcDetails.setTaxPeriod(taxPeriod);
						 * drcDetails.setIsActive(true);
						 * drcDetails.setCreatedBy("PERIODIC");
						 * drcDetails.setCreatedOn(LocalDateTime.now());
						 * drcDetails.setGetPayload("Auth Token is Inactive.");
						 */
						TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

						entity.setCommType(APIConstants.DRC01B.toUpperCase());
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
					TblDrcGetRetCompListDetails drcDetails = new TblDrcGetRetCompListDetails();
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

							entity.setCommType(APIConstants.DRC01B.toUpperCase());
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

									entity.setCommType(APIConstants.DRC01B.toUpperCase());
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

							entity.setCommType(APIConstants.DRC01B.toUpperCase());
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

							entity.setCommType(APIConstants.DRC01B.toUpperCase());
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
					getDrcRetCompSummResp.getResponse(), APIIdentifiers.DRC);
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
				APIParam param3 = new APIParam("frmtyp", APIIdentifiers.DRC);
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
						APIIdentifiers.DRC_GETRETCOMP_SUMMARY, param1, param2,
						param3);
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
				JobConstants.DRC_GETCOMP_SUMMARY, jobParamsObj.toString(),
				"SYSTEM", 1L, null, null));

	}

	private Long createBatchAndSave(String gstin, String retPeriod,
			String userName) {
		batchRepo.softlyDelete(APIIdentifiers.DRC_GETRETCOMP_SUMMARY,
				APIIdentifiers.DRC.toUpperCase(), gstin, retPeriod);
		GetAnx1BatchEntity batch = batchUtil.makeBatch(gstin, retPeriod,
				APIIdentifiers.DRC_GETRETCOMP_SUMMARY,
				APIIdentifiers.DRC.toUpperCase(), userName);
		batch = batchRepo.save(batch);
		return batch.getId();
	}

	private void processSuccessResponse(String response, String gstin,
			String taxPeriod, String refId, String userName) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		DrcGetCompSummaryDetails getSummaryResp = gson.fromJson(response,
				DrcGetCompSummaryDetails.class);
		TblDrcDetails drcDetails = new TblDrcDetails();
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

		drcDetailsRepo.inActivateRecords(gstin, taxPeriod, userName);
		drcDetailsRepo.save(drcDetails);
	}

	private void processDifferentialDetails(
			List<DifferentialDetail> differentialDetails,
			TblDrcDetails drcDetails) {
		List<TblDrcPaymentDetails> paymentDtlsList = new ArrayList<>();

		for (DifferentialDetail differentialPaymentDtls : differentialDetails) {
			TblDrcPaymentDetails paymentDtls = new TblDrcPaymentDetails();
			paymentDtls.setDrcArnNo(differentialPaymentDtls.getDrc03arn());
			paymentDtls.setDrcDetailsId(drcDetails);
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
			TblDrcDetails drcDetails) {
		for (DifferenceAmtDetails misMatcDtls : mismatchDetails) {
			String rtntyp = misMatcDtls.getRtntyp();
			if (rtntyp.equalsIgnoreCase(GSTR1)
					|| rtntyp.equalsIgnoreCase("R1")) {
				drcDetails.setGstr1IgstAmt(misMatcDtls.getIgst());
				drcDetails.setGstr1CgstAmt(misMatcDtls.getCgst());
				drcDetails.setGstr1SgstAmt(misMatcDtls.getSgst());
				drcDetails.setGstr1CessAmt(misMatcDtls.getCess());
				drcDetails.setGstr1TtlAmt(misMatcDtls.getTtl());
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
			TblDrcDetails drcDetails) {
		List<TblDrcReasonDetails> reasonsList = new ArrayList<>();

		for (Reason reasonDtls : reasons) {
			TblDrcReasonDetails reasonDetails = new TblDrcReasonDetails();
			reasonDetails.setDrcDetailsId(drcDetails);
			reasonDetails.setReasonCode(reasonDtls.getRsnco());
			reasonDetails.setReasonDesc(reasonDtls.getReasonDesc());
			reasonsList.add(reasonDetails);
		}

		if (!reasonsList.isEmpty()) {
			drcDetails.setReasonDetails(reasonsList);
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