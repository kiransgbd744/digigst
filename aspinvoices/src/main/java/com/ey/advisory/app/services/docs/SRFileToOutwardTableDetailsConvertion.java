package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.NA;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.admin.services.onboarding.MasterCustomerService;
import com.ey.advisory.admin.services.onboarding.OrganizationService;
import com.ey.advisory.app.data.entities.client.OutwardTable4Entity;
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
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
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToOutwardTableDetailsConvertion")
@Slf4j
public class SRFileToOutwardTableDetailsConvertion {

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

	public List<OutwardTable4Entity> convertSRFileToOutwardTable4(
			List<OutwardTable4ExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<OutwardTable4Entity> listTable4s = new ArrayList<>();
		OutwardTable4Entity table4 = null;
		for (OutwardTable4ExcelEntity arr : businessProcessedRecords) {
			table4 = new OutwardTable4Entity();

			BigDecimal valueofSuppMade = BigDecimal.ZERO;
			String valofSumade = arr.getValueOfSupMade();
			if (valofSumade != null && !valofSumade.isEmpty()) {
				valueofSuppMade = NumberFomatUtil.getBigDecimal(valofSumade);
				table4.setValueOfSupMade(valueofSuppMade);
			}

			BigDecimal valueOfSuppRet = BigDecimal.ZERO;
			String valofSupRet = arr.getValueOfSupRet();
			if (valofSupRet != null && !valofSupRet.isEmpty()) {
				valueOfSuppRet = NumberFomatUtil.getBigDecimal(valofSupRet);
				table4.setValueOfSupRet(valueOfSuppRet);
			}

			BigDecimal valueOfSuppNet = BigDecimal.ZERO;
			String valofSuNet = arr.getNetValueOfSup();
			if (valofSuNet != null && !valofSuNet.isEmpty()) {
				valueOfSuppNet = NumberFomatUtil.getBigDecimal(valofSuNet);
				table4.setNetValueOfSup(valueOfSuppNet);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igsts = arr.getIgstAmt();
			if (igsts != null && !igsts.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igsts);
				table4.setIgstAmt(igst);
			}

			BigDecimal cgst = BigDecimal.ZERO;
			String cgsts = arr.getCgstAmt();
			if (cgsts != null && !cgsts.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgsts);
				table4.setCgstAmt(cgst);
			}

			BigDecimal sgst = BigDecimal.ZERO;
			String sgats = arr.getSgstAmt();
			if (sgats != null && !sgats.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgats);
				table4.setSgstAmt(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cesses = arr.getCessAmt();
			if (cesses != null && !cesses.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cesses);
				table4.setCessAmt(cess);
			}
			String table4GstnKey = arr.getTable4Gstnkey();
			String table4InvKey = arr.getTable4Invkey();
			Integer deriRetPeriod = null;
			if(arr.getRetPeriod() != null && !arr.getRetPeriod().isEmpty()){
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(arr.getRetPeriod());	
			}
			

			table4.setRetType(arr.getRetType());
			table4.setSgstin(arr.getSgstin());
			table4.setRetPeriod(arr.getRetPeriod());
			table4.setEcomGstin(arr.getEcomGstin());
			table4.setProfitCentre(arr.getProfitCentre());
			table4.setPlant(arr.getPlant());
			table4.setDivision(arr.getDivision());
			table4.setLocation(arr.getLocation());
			table4.setSalesOrganisation(arr.getSalesOrganisation());
			table4.setDistributionChannel(arr.getDistributionChannel());
			table4.setUserAccess1(arr.getUserAccess1());
			table4.setUserAccess2(arr.getUserAccess2());
			table4.setUserAccess3(arr.getUserAccess3());
			table4.setUserAccess4(arr.getUserAccess4());
			table4.setUserAccess5(arr.getUserAccess5());
			table4.setUserAccess6(arr.getUserAccess6());
			String userDef1 = arr.getUserDef1();
			String userDef2 = arr.getUserDef2();
			String userDef3 = arr.getUserDef3();

			if (userDef1 != null && !userDef1.isEmpty()) {
				if (userDef1.length() > 100) {
					table4.setUserDef1(userDef1.substring(0, 100).trim());
				}
			} else {
				table4.setUserDef1(userDef1);
			}
			if (userDef2 != null && !userDef2.isEmpty()) {
				if (userDef2.length() > 100) {
					table4.setUserDef2(userDef2.substring(0, 100).trim());
				}
			} else {
				table4.setUserDef2(userDef2);
			}
			if (userDef3 != null && !userDef3.isEmpty()) {
				if (userDef3.length() > 100) {
					table4.setUserDef3(userDef3.substring(0, 100).trim());
				}
			} else {
				table4.setUserDef3(userDef3);
			}
			table4.setTable4Gstnkey(table4GstnKey);

			table4.setDerivedRetPeriod(deriRetPeriod);
			if (updateFileStatus != null) {
				table4.setFileId(updateFileStatus.getId());
			}
			table4.setAsEnterTableId(arr.getId());
			table4.setInfo(arr.isInfo());
			table4.setTable4Invkey(table4InvKey);
			table4.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			table4.setCreatedOn(convertNow);
			listTable4s.add(table4);

		}

		return listTable4s;
	}

	public String getTable4GstnKey(Object[] arr) {
		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String ecomGstin = (arr[3] != null) ? (String.valueOf(arr[3])).trim()
				: "";

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(ecomGstin).toString();
	}

	public String getTable4InvKey(Object[] arr) {
		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String ecomGstin = (arr[3] != null) ? (String.valueOf(arr[3])).trim()
				: "";

		String profitCentre = (arr[11] != null)
				? (String.valueOf(arr[11])).trim() : NA;
		String plant = (arr[12] != null) ? (String.valueOf(arr[12])).trim()
				: NA;
		String division = (arr[13] != null) ? (String.valueOf(arr[13])).trim()
				: NA;
		String location = (arr[14] != null) ? (String.valueOf(arr[14])).trim()
				: NA;
		String salesOrg = (arr[15] != null) ? (String.valueOf(arr[15])).trim()
				: NA;
		String distributeChannel = (arr[16] != null)
				? (String.valueOf(arr[16])).trim() : NA;
		String userAccess1 = (arr[17] != null)
				? (String.valueOf(arr[17])).trim() : NA;
		String userAccess2 = (arr[18] != null)
				? (String.valueOf(arr[18])).trim() : NA;
		String userAccess3 = (arr[19] != null)
				? (String.valueOf(arr[19])).trim() : NA;
		String userAccess4 = (arr[20] != null)
				? (String.valueOf(arr[20])).trim() : NA;
		String userAccess5 = (arr[21] != null)
				? (String.valueOf(arr[21])).trim() : NA;
		String userAccess6 = (arr[22] != null)
				? (String.valueOf(arr[22])).trim() : NA;
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(ecomGstin)
				.add(profitCentre).add(plant).add(division).add(location)
				.add(salesOrg).add(distributeChannel).add(userAccess1)
				.add(userAccess2).add(userAccess3).add(userAccess4)
				.add(userAccess5).add(userAccess6).toString();

	}

	public List<OutwardTable4ExcelEntity> convertSRFileToOutwardTable4Processed(
			List<OutwardTable4ExcelEntity> strucProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardTable4ExcelEntity> listTable4 = new ArrayList<>();
		 
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

		OutwardTable4ExcelEntity table4 = null; 
		for (OutwardTable4ExcelEntity arr : strucProcessedRecords) {
			table4 = new OutwardTable4ExcelEntity();
			Long entityId = gstinAndEntityMap.get(arr.getSgstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + arr.getSgstin() + " - entityId "
						+ entityId);
			} 
			table4.setGroupId(groupId);
			table4.setEntityId(entityId);
			table4.setEntityConfigParamMap(map);
			table4.setEntityAtConfMap(entityAtConfigMap);
			table4.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (table4.getSgstin() != null
						&& !table4.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(table4.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			table4.setId(arr.getId());
			table4.setRetType(arr.getRetType());
			table4.setSgstin(arr.getSgstin());
			table4.setRetPeriod(arr.getRetPeriod());
			table4.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			table4.setEcomGstin(arr.getEcomGstin());
			table4.setValueOfSupMade(arr.getValueOfSupMade());
			table4.setValueOfSupRet(arr.getValueOfSupRet());
			table4.setNetValueOfSup(arr.getNetValueOfSup());
			table4.setIgstAmt(arr.getIgstAmt());
			table4.setCgstAmt(arr.getCgstAmt());
			table4.setSgstAmt(arr.getSgstAmt());
			table4.setCessAmt(arr.getCessAmt());
			table4.setProfitCentre(arr.getProfitCentre());
			table4.setPlant(arr.getPlant());
			table4.setDivision(arr.getDivision());
			table4.setLocation(arr.getLocation());
			table4.setSalesOrganisation(arr.getSalesOrganisation());
			table4.setDistributionChannel(arr.getDistributionChannel());
			table4.setUserAccess1(arr.getUserAccess1());
			table4.setUserAccess2(arr.getUserAccess2());
			table4.setUserAccess3(arr.getUserAccess3());
			table4.setUserAccess4(arr.getUserAccess4());
			table4.setUserAccess5(arr.getUserAccess5());
			table4.setUserAccess6(arr.getUserAccess6());

			if (arr.getTable4Gstnkey() != null
					&& arr.getTable4Gstnkey().length() > 1000) {
				table4.setTable4Gstnkey(
						arr.getTable4Gstnkey().substring(0, 1000));
			} else {
				table4.setTable4Gstnkey(arr.getTable4Gstnkey());
			}
			if (arr.getTable4Invkey() != null
					&& arr.getTable4Invkey().length() > 2200) {
				table4.setTable4Invkey(
						arr.getTable4Invkey().substring(0, 2200));
			} else {
				table4.setTable4Invkey(arr.getTable4Invkey());
			}

			table4.setUserDef1(arr.getUserDef1());
			table4.setUserDef2(arr.getUserDef2());
			table4.setUserDef3(arr.getUserDef3());
			if (updateFileStatus != null) {
				table4.setFileId(updateFileStatus.getId());
				table4.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			table4.setCreatedOn(convertNow);
			table4.setInfo(arr.isInfo());
			table4.setDelete(arr.isDelete());
			table4.setError(arr.isError());
			listTable4.add(table4);
		}
		return listTable4;
	}
}
