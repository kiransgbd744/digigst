package com.ey.advisory.app.services.docs;

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
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.data.entities.client.Gstr1B2csDetailsEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToB2CSDetailsConvertion")
@Slf4j
public class SRFileToB2CSDetailsConvertion {

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

	public List<Gstr1B2csDetailsEntity> convertSRFileToOutwardTransDoc(
			List<Gstr1AsEnteredB2csEntity> businessProcessed,
			Gstr1FileStatusEntity fileStatusId) {

		List<Gstr1B2csDetailsEntity> listOfB2cs = new ArrayList<>();

		Gstr1B2csDetailsEntity b2cs = null;
		for (Gstr1AsEnteredB2csEntity obj : businessProcessed) {

			b2cs = new Gstr1B2csDetailsEntity();

			if (fileStatusId != null) {
				b2cs.setFileId(fileStatusId.getId());
			}

			BigDecimal qnt = BigDecimal.ZERO;
			String quentity = obj.getOrgQnt();
			if (quentity != null && !quentity.isEmpty()) {
				qnt = NumberFomatUtil.getBigDecimal(quentity);
				b2cs.setOrgQnt(qnt.setScale(3, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal rate = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				rate = NumberFomatUtil.getBigDecimal(orgRate);
				b2cs.setOrgRate(rate.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal taxValue = BigDecimal.ZERO;
			String orgTaxValue = obj.getOrgTaxVal();
			if (orgTaxValue != null && !orgTaxValue.isEmpty()) {
				taxValue = NumberFomatUtil.getBigDecimal(orgTaxValue);
				b2cs.setOrgTaxVal(
						taxValue.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal orgSupVal = BigDecimal.ZERO;
			String oSupVal = obj.getOrgSupVal();
			if (oSupVal != null && !oSupVal.isEmpty()) {
				orgSupVal = NumberFomatUtil.getBigDecimal(oSupVal);
				b2cs.setOrgSupVal(
						orgSupVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal newQnt = BigDecimal.ZERO;
			String nQnt = obj.getNewQnt();
			if (nQnt != null && !nQnt.isEmpty()) {
				newQnt = NumberFomatUtil.getBigDecimal(nQnt);
				b2cs.setNewQnt(newQnt.setScale(3, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal newRate = BigDecimal.ZERO;
			String nRate = obj.getNewRate();
			if (nRate != null && !nRate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(nRate);
				b2cs.setNewRate(
						newRate.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal newTaxVal = BigDecimal.ZERO;
			String nTaxVal = obj.getNewTaxVal();
			if (nTaxVal != null && !nTaxVal.isEmpty()) {
				newTaxVal = NumberFomatUtil.getBigDecimal(nTaxVal);
				b2cs.setNewTaxVal(
						newTaxVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal newSupval = BigDecimal.ZERO;
			String nSupVal = obj.getNewSupVal();
			if (nSupVal != null && !nSupVal.isEmpty()) {
				newSupval = NumberFomatUtil.getBigDecimal(nSupVal);
				b2cs.setNewSupVal(
						newSupval.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igstAnt = obj.getIgstAmt();
			if (igstAnt != null && !igstAnt.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igstAnt);
				b2cs.setIgstAmt(igst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgstAmt = obj.getCgstAmt();
			if (cgstAmt != null && !cgstAmt.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgstAmt);
				b2cs.setCgstAmt(cgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgstAmt = obj.getSgstAmt();
			if (sgstAmt != null && !sgstAmt.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgstAmt);
				b2cs.setSgstAmt(sgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmt();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				b2cs.setCessAmt(cess.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal total = BigDecimal.ZERO;
			String totalVal = obj.getTotalValue();
			if (totalVal != null && !totalVal.isEmpty()) {
				total = NumberFomatUtil.getBigDecimal(totalVal);
				b2cs.setTotalValue(
						total.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}
			b2cs.setAsEnterId(obj.getId());
			b2cs.setSgstin(obj.getSgstin());
			b2cs.setReturnPeriod(obj.getReturnPeriod());
			b2cs.setTransType(obj.getTransType());
			b2cs.setMonth(obj.getMonth());
			b2cs.setOrgPos(obj.getOrgPos());
			b2cs.setOrgHsnOrSac(obj.getOrgHsnOrSac());
			if(obj.getOrgUom()!=null){
			b2cs.setOrgUom(obj.getOrgUom().toUpperCase());
			}
			b2cs.setOrgCGstin(obj.getOrgCGstin());
			b2cs.setNewPos(obj.getNewPos());
			b2cs.setNewHsnOrSac(obj.getNewHsnOrSac());
			if(obj.getNewUom()!=null){
			b2cs.setNewUom(obj.getNewUom().toUpperCase());
			}
			b2cs.setNewGstin(obj.getNewGstin());
			b2cs.setGstnB2csKey(obj.getGstnB2csKey());
			b2cs.setInvB2csKey(obj.getInvB2csKey());
			b2cs.setInfo(obj.isInfo());
			b2cs.setProfitCentre(obj.getProfitCentre());
			b2cs.setPlant(obj.getPlant());
			b2cs.setDivision(obj.getDivision());
			b2cs.setLocation(obj.getLocation());
			b2cs.setSalesOrganisation(obj.getSalesOrganisation());
			b2cs.setDistributionChannel(obj.getDistributionChannel());
			b2cs.setSectionType(obj.isSectionType());
			b2cs.setUserAccess1(obj.getUserAccess1());
			b2cs.setUserAccess2(obj.getUserAccess2());
			b2cs.setUserAccess3(obj.getUserAccess3());
			b2cs.setUserAccess4(obj.getUserAccess4());
			b2cs.setUserAccess5(obj.getUserAccess5());
			b2cs.setUserAccess6(obj.getUserAccess6());
			b2cs.setUserDef1(obj.getUserDef1());
			b2cs.setUserDef2(obj.getUserDef2());
			b2cs.setUserDef3(obj.getUserDef3());
			b2cs.setDerivedRetPeriod(deriRetPeriod);
			b2cs.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			b2cs.setCreatedOn(convertNow);
			listOfB2cs.add(b2cs);

		}

		return listOfB2cs;
	}

	public List<Gstr1AsEnteredB2csEntity> convertSRFileToOutward(
			List<Gstr1AsEnteredB2csEntity> strucProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr1AsEnteredB2csEntity> listB2cs = new ArrayList<>();

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

		Gstr1AsEnteredB2csEntity b2cs = null;
		for (Gstr1AsEnteredB2csEntity arr : strucProcessedRecords) {
			b2cs = new Gstr1AsEnteredB2csEntity();
			Long entityId = gstinAndEntityMap.get(arr.getSgstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + arr.getSgstin() + " - entityId "
						+ entityId);
			}
			b2cs.setGroupId(groupId);
			b2cs.setEntityId(entityId);
			b2cs.setEntityConfigParamMap(map);
			b2cs.setEntityAtConfMap(entityAtConfigMap);
			b2cs.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (b2cs.getSgstin() != null
						&& !b2cs.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(b2cs.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			b2cs.setId(arr.getId());
			b2cs.setReturnPeriod(arr.getReturnPeriod());
			b2cs.setSgstin(arr.getSgstin());
			b2cs.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			b2cs.setTransType(arr.getTransType());
			b2cs.setMonth(arr.getMonth());
			b2cs.setOrgPos(arr.getOrgPos());
			b2cs.setOrgHsnOrSac(arr.getOrgHsnOrSac());
			b2cs.setOrgUom(arr.getOrgUom());
			b2cs.setOrgQnt(arr.getOrgQnt());
			b2cs.setOrgRate(arr.getOrgRate());
			b2cs.setOrgTaxVal(arr.getOrgTaxVal());
			b2cs.setOrgCGstin(arr.getOrgCGstin());
			b2cs.setOrgSupVal(arr.getOrgSupVal());
			b2cs.setNewPos(arr.getNewPos());
			b2cs.setNewHsnOrSac(arr.getNewHsnOrSac());
			b2cs.setNewUom(arr.getNewUom());
			b2cs.setNewQnt(arr.getNewQnt());
			b2cs.setNewRate(arr.getNewRate());
			b2cs.setNewTaxVal(arr.getNewTaxVal());
			b2cs.setNewGstin(arr.getNewGstin());
			b2cs.setNewSupVal(arr.getNewSupVal());
			b2cs.setTotalValue(arr.getTotalValue());
			b2cs.setIgstAmt(arr.getIgstAmt());
			b2cs.setCgstAmt(arr.getCgstAmt());
			b2cs.setSgstAmt(arr.getSgstAmt());
			b2cs.setCessAmt(arr.getCessAmt());
			b2cs.setProfitCentre(arr.getProfitCentre());
			b2cs.setPlant(arr.getPlant());
			b2cs.setDivision(arr.getDivision());
			b2cs.setLocation(arr.getLocation());
			b2cs.setSalesOrganisation(arr.getSalesOrganisation());
			b2cs.setDistributionChannel(arr.getDistributionChannel());
			b2cs.setUserAccess1(arr.getUserAccess1());
			b2cs.setUserAccess2(arr.getUserAccess2());
			b2cs.setUserAccess3(arr.getUserAccess3());
			b2cs.setUserAccess4(arr.getUserAccess4());
			b2cs.setUserAccess5(arr.getUserAccess5());
			b2cs.setUserAccess6(arr.getUserAccess6());

			if (arr.getGstnB2csKey() != null
					&& arr.getGstnB2csKey().length() > 1000) {
				b2cs.setGstnB2csKey(arr.getGstnB2csKey().substring(0, 1000));
			} else {
				b2cs.setGstnB2csKey(arr.getGstnB2csKey());
			}
			if (arr.getInvB2csKey() != null
					&& arr.getInvB2csKey().length() > 2200) {
				b2cs.setInvB2csKey(arr.getInvB2csKey().substring(0, 2200));
			} else {
				b2cs.setInvB2csKey(arr.getInvB2csKey());
			}

			b2cs.setUserDef1(arr.getUserDef1());
			b2cs.setUserDef2(arr.getUserDef2());
			b2cs.setUserDef3(arr.getUserDef3());
			if (updateFileStatus != null) {
				b2cs.setFileId(updateFileStatus.getId());
				b2cs.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			b2cs.setCreatedOn(convertNow);
			b2cs.setInfo(arr.isInfo());
			b2cs.setDelete(arr.isDelete());
			b2cs.setError(arr.isError());
			b2cs.setSectionType(arr.isSectionType());
			listB2cs.add(b2cs);
		}
		return listB2cs;
	}
}
