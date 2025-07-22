package com.ey.advisory.app.services.doc.gstr1a;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.admin.services.onboarding.MasterCustomerService;
import com.ey.advisory.admin.services.onboarding.OrganizationService;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AARDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Service("SRFileToGstr1AATDetailsConvertion")
@Slf4j
public class SRFileToGstr1AATDetailsConvertion {

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("OrganizationServiceImpl")
	private OrganizationService orgSvc;

	@Autowired
	@Qualifier("masterCustomerService")
	private MasterCustomerService masterCustomerSvc;

	public List<Gstr1AARDetailsEntity> convertSRFileToOutwardTransDoc(
			List<Gstr1AAsEnteredAREntity> businessProcessed,
			Gstr1FileStatusEntity fileStatusId) {

		List<Gstr1AARDetailsEntity> listOfAt = new ArrayList<>();

		Gstr1AARDetailsEntity at = null;
		for (Gstr1AAsEnteredAREntity obj : businessProcessed) {

			at = new Gstr1AARDetailsEntity();

			if (fileStatusId != null) {
				at.setFileId(fileStatusId.getId());
			}

			BigDecimal rate = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				rate = NumberFomatUtil.getBigDecimal(orgRate);
				at.setOrgRate(rate.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrossAdv = obj.getOrgGrossAdvRec();
			if (orgGrossAdv != null && !orgGrossAdv.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrossAdv);
				at.setOrgGrossAdvRec(
						orgGross.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String nRate = obj.getNewRate();
			if (nRate != null && !nRate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(nRate);
				at.setNewRate(newRate.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGrossAdv = obj.getNewGrossAdvRec();
			if (newGrossAdv != null && !newGrossAdv.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGrossAdv);
				at.setNewGrossAdvRec(
						newGross.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igstAnt = obj.getIgstAmt();
			if (igstAnt != null && !igstAnt.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igstAnt);
				at.setIgstAmt(igst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgstAmt = obj.getCgstAmt();
			if (cgstAmt != null && !cgstAmt.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgstAmt);
				at.setCgstAmt(cgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgstAmt = obj.getSgstAmt();
			if (sgstAmt != null && !sgstAmt.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgstAmt);
				at.setSgstAmt(sgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmt();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				at.setCessAmt(cess.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}

			if (obj.getId() != null)
				at.setAsEnterId(obj.getId());
			at.setSgstin(obj.getSgstin());
			at.setReturnPeriod(obj.getReturnPeriod());
			at.setTransType(obj.getTransType());
			at.setMonth(obj.getMonth());
			at.setOrgPos(obj.getOrgPos());
			at.setNewPos(obj.getNewPos());
			at.setProfitCentre(obj.getProfitCentre());
			at.setPlant(obj.getPlant());
			at.setDivision(obj.getDivision());
			at.setLocation(obj.getLocation());
			at.setSalesOrganisation(obj.getSalesOrganisation());
			at.setDistributionChannel(obj.getDistributionChannel());
			at.setUserAccess1(obj.getUserAccess1());
			at.setUserAccess2(obj.getUserAccess2());
			at.setUserAccess3(obj.getUserAccess3());
			at.setUserAccess4(obj.getUserAccess4());
			at.setUserAccess5(obj.getUserAccess5());
			at.setUserAccess6(obj.getUserAccess6());
			at.setInfo(obj.isInfo());
			at.setUserDef1(obj.getUserDef1());
			at.setUserDef2(obj.getUserDef2());
			at.setUserDef3(obj.getUserDef3());
			at.setDerivedRetPeriod(deriRetPeriod);
			at.setSectionType(obj.isSectionType());
			at.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			at.setCreatedOn(convertNow);
			at.setGstnAtKey(obj.getGstnAtKey());
			at.setInvAtKey(obj.getInvAtKey());
			listOfAt.add(at);

		}

		return listOfAt;
	}

	public List<Gstr1AAsEnteredAREntity> convertSRFileToOutward(
			List<Gstr1AAsEnteredAREntity> strucProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr1AAsEnteredAREntity> listAt = new ArrayList<>();

		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		Map<EntityAtConfigKey, Map<Long, String>> entityAtConfigMap = orgSvc
				.getEntityAtConfMap(
						AspDocumentConstants.TransDocTypes.OUTWARD.getType());
		Map<Long, List<Pair<String, String>>> entityAtValMap = orgSvc
				.getAllEntityAtValMap();
		String groupCode = TenantContext.getTenantId();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		List<String> allMasterCustomerGstins = masterCustomerSvc
				.getAllMasterCustomerGstins();

		Gstr1AAsEnteredAREntity at = null;
		for (Gstr1AAsEnteredAREntity arr : strucProcessedRecords) {
			at = new Gstr1AAsEnteredAREntity();
			Long entityId = gstinAndEntityMap.get(arr.getSgstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + arr.getSgstin() + " - entityId "
						+ entityId);
			}
			at.setGroupId(groupId);
			at.setEntityId(entityId);
			at.setEntityConfigParamMap(map);
			at.setEntityAtConfMap(entityAtConfigMap);
			at.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (at.getSgstin() != null
						&& !at.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(at.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			at.setId(arr.getId());
			at.setReturnPeriod(arr.getReturnPeriod());
			at.setSgstin(arr.getSgstin());
			at.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			at.setTransType(arr.getTransType());
			at.setMonth(arr.getMonth());
			at.setOrgPos(arr.getOrgPos());
			at.setOrgRate(arr.getOrgRate());
			at.setOrgGrossAdvRec(arr.getOrgGrossAdvRec());
			at.setNewPos(arr.getNewPos());
			at.setNewRate(arr.getNewRate());
			at.setNewGrossAdvRec(arr.getNewGrossAdvRec());
			at.setIgstAmt(arr.getIgstAmt());
			at.setCgstAmt(arr.getCgstAmt());
			at.setSgstAmt(arr.getSgstAmt());
			at.setCessAmt(arr.getCessAmt());
			at.setProfitCentre(arr.getProfitCentre());
			at.setPlant(arr.getPlant());
			at.setDivision(arr.getDivision());
			at.setLocation(arr.getLocation());
			at.setSalesOrganisation(arr.getSalesOrganisation());
			at.setDistributionChannel(arr.getDistributionChannel());
			at.setUserAccess1(arr.getUserAccess1());
			at.setUserAccess2(arr.getUserAccess2());
			at.setUserAccess3(arr.getUserAccess3());
			at.setUserAccess4(arr.getUserAccess4());
			at.setUserAccess5(arr.getUserAccess5());
			at.setUserAccess6(arr.getUserAccess6());

			if (arr.getGstnAtKey() != null
					&& arr.getGstnAtKey().length() > 1000) {
				at.setGstnAtKey(arr.getGstnAtKey().substring(0, 1000));
			} else {
				at.setGstnAtKey(arr.getGstnAtKey());
			}
			if (arr.getInvAtKey() != null
					&& arr.getInvAtKey().length() > 2200) {
				at.setInvAtKey(arr.getInvAtKey().substring(0, 2200));
			} else {
				at.setInvAtKey(arr.getInvAtKey());
			}

			at.setUserDef1(arr.getUserDef1());
			at.setUserDef2(arr.getUserDef2());
			at.setUserDef3(arr.getUserDef3());
			if (updateFileStatus != null) {
				at.setFileId(updateFileStatus.getId());
				at.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			at.setCreatedOn(convertNow);
			at.setInfo(arr.isInfo());
			at.setDelete(arr.isDelete());
			at.setError(arr.isError());
			at.setSectionType(arr.isSectionType());
			listAt.add(at);
		}
		return listAt;
	}
}
