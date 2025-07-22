package com.ey.advisory.app.services.doc.gstr1a;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAdvanceAdjustmentFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("SRFileToGstr1ATxpdDetailsConvertion")
@Slf4j
public class SRFileToGstr1ATxpdDetailsConvertion {

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

	public List<Gstr1AAdvanceAdjustmentFileUploadEntity> convertSRFileToAtaDoc(
			List<Gstr1AAsEnteredTxpdFileUploadEntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {
		List<Gstr1AAdvanceAdjustmentFileUploadEntity> listOfAta = new ArrayList<>();
		Gstr1AAdvanceAdjustmentFileUploadEntity ataObj = null;
		for (Gstr1AAsEnteredTxpdFileUploadEntity obj : busProcessRecords) {
			ataObj = new Gstr1AAdvanceAdjustmentFileUploadEntity();

			BigDecimal orgRates = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				orgRates = NumberFomatUtil.getBigDecimal(orgRate);
				ataObj.setOrgRate(orgRates);
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrosss = obj.getOrgGrossAdvanceAdjusted();
			if (orgGrosss != null && !orgGrosss.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrosss);
				ataObj.setOrgGrossAdvanceAdjusted(orgGross);
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String rate = obj.getNewRate();
			if (rate != null && !rate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(rate);
				ataObj.setNewRate(newRate);
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGros = obj.getNewGrossAdvanceAdjusted();
			if (newGros != null && !newGros.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGros);
				ataObj.setNewGrossAdvanceAdjusted(newGross);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igsts = obj.getIntegratedTaxAmount();
			if (igsts != null && !igsts.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igsts);
				ataObj.setIntegratedTaxAmount(igst);
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgsts = obj.getCentralTaxAmount();
			if (cgsts != null && !cgsts.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgsts);
				ataObj.setCentralTaxAmount(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgsts = obj.getStateUTTaxAmount();
			if (sgsts != null && !sgsts.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgsts);
				ataObj.setStateUTTaxAmount(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmount();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				ataObj.setCessAmount(cess);
			}

			ataObj.setSgstin(obj.getSgstin());
			if (fileStatus != null) {
				ataObj.setFileId(fileStatus.getId());
			}
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}
			ataObj.setReturnPeriod(obj.getReturnPeriod());
			ataObj.setTransactionType(obj.getTransactionType());
			ataObj.setAsEnterId(obj.getId());
			ataObj.setMonth(obj.getMonth());
			ataObj.setOrgPOS(obj.getOrgPOS());
			ataObj.setNewPOS(obj.getNewPOS());
			ataObj.setDerivedRetPeriod(deriRetPeriod);
			ataObj.setProfitCentre(obj.getProfitCentre());
			ataObj.setPlant(obj.getPlant());
			ataObj.setLocation(obj.getLocation());
			ataObj.setDivision(obj.getDivision());
			ataObj.setSalesOrganisation(obj.getSalesOrganisation());
			ataObj.setDistributionChannel(obj.getDistributionChannel());
			ataObj.setInfo(obj.isInfo());
			ataObj.setUserAccess1(obj.getUserAccess1());
			ataObj.setUserAccess2(obj.getUserAccess2());
			ataObj.setUserAccess3(obj.getUserAccess3());
			ataObj.setUserAccess4(obj.getUserAccess4());
			ataObj.setUserAccess5(obj.getUserAccess5());
			ataObj.setUserAccess6(obj.getUserAccess6());
			ataObj.setUserDef1(obj.getUserDef1());
			ataObj.setUserDef2(obj.getUserDef2());
			ataObj.setUserDef3(obj.getUserDef3());
			ataObj.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ataObj.setCreatedOn(convertNow);
			ataObj.setSectionType(obj.isSectionType());
			ataObj.setGstnTxpdKey(obj.getGstnTxpdKey());
			ataObj.setTxpdInvKey(obj.getTxpdInvKey());
			listOfAta.add(ataObj);
		}

		return listOfAta;
	}

	public List<Gstr1AAsEnteredTxpdFileUploadEntity> convertSRFileToTxpdExcel(
			List<Object[]> listOfAtas, Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AAsEnteredTxpdFileUploadEntity> list = new ArrayList<>();

		Gstr1AAsEnteredTxpdFileUploadEntity obj = null;
		for (Object[] arr : listOfAtas) {
			obj = new Gstr1AAsEnteredTxpdFileUploadEntity();
			String sgstin = getValues(arr[0]);
			String retPeriod = getValues(arr[1]);
			String transType = getValues(arr[2]);
			String month = getValues(arr[3]);
			String orgPos = getValues(arr[4]);
			String orgRate = getValues(arr[5]);
			String orgGrossTxpd = getValues(arr[6]);
			String newPos = getValues(arr[7]);
			String newRate = getValues(arr[8]);
			String newGrossTxpd = getValues(arr[9]);
			String igst = getValues(arr[10]);
			String cgst = getValues(arr[11]);
			String sgst = getValues(arr[12]);
			String cess = getValues(arr[13]);
			String profitCenter = getUserAttribuesValues(arr[14]);
			String plant = getUserAttribuesValues(arr[15]);
			String division = getUserAttribuesValues(arr[16]);
			String location = getUserAttribuesValues(arr[17]);
			String salesOrg = getUserAttribuesValues(arr[18]);
			String distributedChannel = getUserAttribuesValues(arr[19]);
			String user01 = getUserAttribuesValues(arr[20]);
			String user02 = getUserAttribuesValues(arr[21]);
			String user03 = getUserAttribuesValues(arr[22]);
			String user04 = getUserAttribuesValues(arr[23]);
			String user05 = getUserAttribuesValues(arr[24]);
			String user06 = getUserAttribuesValues(arr[25]);
			String userdef01 = getValues(arr[26]);
			String userdef02 = getValues(arr[27]);
			String userdef03 = getValues(arr[28]);
			String invKey = getTxpdKey(arr);
			if (invKey != null && invKey.length() > 2200) {
				invKey = invKey.substring(0, 2200);
			}
			String gstnKey = getGstnKey(arr);
			if (gstnKey != null && gstnKey.length() > 1000) {
				gstnKey = gstnKey.substring(0, 1000);
			}
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
				obj.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			String derivedRePeroid = null;
			if (retPeriod != null && retPeriod.matches("[0-9]+")) {
				if (retPeriod.length() == 6) {
					int months = Integer.valueOf(retPeriod.substring(0, 2));
					int year = Integer.valueOf(retPeriod.substring(2));
					if ((months < 12 && months > 01)
							&& (year < 9999 && year > 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(retPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			obj.setSgstin(sgstin);
			obj.setReturnPeriod(retPeriod);
			obj.setDerivedRetPeriod(derivedRePeroid);
			obj.setTransactionType(transType);
			obj.setMonth(month);
			obj.setOrgPOS(orgPos);
			obj.setOrgRate(orgRate);
			obj.setOrgGrossAdvanceAdjusted(orgGrossTxpd);
			obj.setNewPOS(newPos);
			obj.setNewRate(newRate);
			obj.setNewGrossAdvanceAdjusted(newGrossTxpd);
			obj.setIntegratedTaxAmount(igst);
			obj.setCentralTaxAmount(cgst);
			obj.setStateUTTaxAmount(sgst);
			obj.setCessAmount(cess);
			obj.setProfitCentre(profitCenter);
			obj.setPlant(plant);
			obj.setLocation(location);
			obj.setDivision(division);
			obj.setSalesOrganisation(salesOrg);
			obj.setDistributionChannel(distributedChannel);
			obj.setUserAccess1(user01);
			obj.setUserAccess2(user02);
			obj.setUserAccess3(user03);
			obj.setUserAccess4(user04);
			obj.setUserAccess5(user05);
			obj.setUserAccess6(user06);
			obj.setUserDef1(userdef01);
			obj.setUserDef2(userdef02);
			obj.setUserDef3(userdef03);
			obj.setTxpdInvKey(invKey);
			obj.setGstnTxpdKey(gstnKey);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			obj.setCreatedOn(convertNow);
			if (month != null && !month.isEmpty()) {
				obj.setSectionType(true);
			}
			list.add(obj);
		}
		return list;
	}

	private String getGstnKey(Object[] obj) {
		String sgstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transType = (obj[2] != null) ? String.valueOf(obj[2]).trim()
				: GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim()
				: GSTConstants.N;
		String orgPos = (obj[4] != null) ? String.valueOf(obj[4]).trim()
				: GSTConstants.N;
		String orgRate = (obj[5] != null) ? String.valueOf(obj[5]).trim()
				: GSTConstants.N;
		String newPos = (obj[7] != null) ? String.valueOf(obj[7]).trim() : "";
		String newRate = (obj[8] != null) ? String.valueOf(obj[8]).trim() : "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sgstin)
				.add(retPeriod).add(transType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).toString();
	}

	public String getTxpdKey(Object[] obj) {

		String sgstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transType = (obj[2] != null) ? String.valueOf(obj[2]).trim()
				: GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim() : "";
		String orgPos = (obj[4] != null) ? String.valueOf(obj[4]).trim() : "";
		String orgRate = (obj[5] != null) ? String.valueOf(obj[5]).trim() : "";
		String newPos = (obj[7] != null) ? String.valueOf(obj[7]).trim() : "";
		String newRate = (obj[8] != null) ? String.valueOf(obj[8]).trim() : "";
		String profitCenter = (obj[14] != null) ? String.valueOf(obj[14]).trim()
				: GSTConstants.NA;
		String plant = (obj[15] != null) ? String.valueOf(obj[15]).trim()
				: GSTConstants.NA;
		String divison = (obj[16] != null) ? String.valueOf(obj[16]).trim()
				: GSTConstants.NA;
		String location = (obj[17] != null) ? String.valueOf(obj[17]).trim()
				: GSTConstants.NA;
		String salesOrg = (obj[18] != null) ? String.valueOf(obj[18]).trim()
				: GSTConstants.NA;
		String distributedChannel = (obj[19] != null)
				? String.valueOf(obj[19]).trim() : GSTConstants.NA;
		String user1 = (obj[20] != null) ? String.valueOf(obj[20]).trim()
				: GSTConstants.NA;
		String user2 = (obj[21] != null) ? String.valueOf(obj[21]).trim()
				: GSTConstants.NA;
		String user3 = (obj[22] != null) ? String.valueOf(obj[22]).trim()
				: GSTConstants.NA;
		String user4 = (obj[23] != null) ? String.valueOf(obj[23]).trim()
				: GSTConstants.NA;
		String user5 = (obj[24] != null) ? String.valueOf(obj[24]).trim()
				: GSTConstants.NA;
		String user6 = (obj[25] != null) ? String.valueOf(obj[25]).trim()
				: GSTConstants.NA;
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sgstin)
				.add(retPeriod).add(transType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).add(profitCenter)
				.add(plant).add(divison).add(location).add(salesOrg)
				.add(distributedChannel).add(user1).add(user2).add(user3)
				.add(user4).add(user5).add(user6).toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	private String getUserAttribuesValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

	public List<Gstr1AAsEnteredTxpdFileUploadEntity> convertSRFileToTxpd(
			List<Gstr1AAsEnteredTxpdFileUploadEntity> strProcessRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr1AAsEnteredTxpdFileUploadEntity> listTxpd = new ArrayList<>();

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

		Gstr1AAsEnteredTxpdFileUploadEntity txpd = null;
		for (Gstr1AAsEnteredTxpdFileUploadEntity arr : strProcessRecords) {
			txpd = new Gstr1AAsEnteredTxpdFileUploadEntity();
			Long entityId = gstinAndEntityMap.get(arr.getSgstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + arr.getSgstin() + " - entityId "
						+ entityId);
			}
			txpd.setGroupId(groupId);
			txpd.setEntityId(entityId);
			txpd.setEntityConfigParamMap(map);
			txpd.setEntityAtConfMap(entityAtConfigMap);
			txpd.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (txpd.getSgstin() != null
						&& !txpd.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(txpd.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			txpd.setId(arr.getId());
			txpd.setReturnPeriod(arr.getReturnPeriod());
			txpd.setSgstin(arr.getSgstin());
			txpd.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			txpd.setTransactionType(arr.getTransactionType());
			txpd.setMonth(arr.getMonth());
			txpd.setOrgPOS(arr.getOrgPOS());
			txpd.setOrgRate(arr.getOrgRate());
			txpd.setOrgGrossAdvanceAdjusted(arr.getOrgGrossAdvanceAdjusted());
			txpd.setNewPOS(arr.getNewPOS());
			txpd.setNewRate(arr.getNewRate());
			txpd.setNewGrossAdvanceAdjusted(arr.getNewGrossAdvanceAdjusted());
			txpd.setIntegratedTaxAmount(arr.getIntegratedTaxAmount());
			txpd.setCentralTaxAmount(arr.getCentralTaxAmount());
			txpd.setStateUTTaxAmount(arr.getStateUTTaxAmount());
			txpd.setCessAmount(arr.getCessAmount());
			txpd.setProfitCentre(arr.getProfitCentre());
			txpd.setPlant(arr.getPlant());
			txpd.setDivision(arr.getDivision());
			txpd.setLocation(arr.getLocation());
			txpd.setSalesOrganisation(arr.getSalesOrganisation());
			txpd.setDistributionChannel(arr.getDistributionChannel());
			txpd.setUserAccess1(arr.getUserAccess1());
			txpd.setUserAccess2(arr.getUserAccess2());
			txpd.setUserAccess3(arr.getUserAccess3());
			txpd.setUserAccess4(arr.getUserAccess4());
			txpd.setUserAccess5(arr.getUserAccess5());
			txpd.setUserAccess6(arr.getUserAccess6());

			if (arr.getGstnTxpdKey() != null
					&& arr.getGstnTxpdKey().length() > 1000) {
				txpd.setGstnTxpdKey(arr.getGstnTxpdKey().substring(0, 1000));
			} else {
				txpd.setGstnTxpdKey(arr.getGstnTxpdKey());
			}
			if (arr.getTxpdInvKey() != null
					&& arr.getTxpdInvKey().length() > 2200) {
				txpd.setTxpdInvKey(arr.getTxpdInvKey().substring(0, 2200));
			} else {
				txpd.setTxpdInvKey(arr.getTxpdInvKey());
			}

			txpd.setUserDef1(arr.getUserDef1());
			txpd.setUserDef2(arr.getUserDef2());
			txpd.setUserDef3(arr.getUserDef3());
			if (updateFileStatus != null) {
				txpd.setFileId(updateFileStatus.getId());
				txpd.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			txpd.setCreatedOn(convertNow);
			txpd.setInfo(arr.isInfo());
			txpd.setDelete(arr.isDelete());
			txpd.setError(arr.isError());
			txpd.setSectionType(arr.isSectionType());
			listTxpd.add(txpd);
		}
		return listTxpd;
	}
}
