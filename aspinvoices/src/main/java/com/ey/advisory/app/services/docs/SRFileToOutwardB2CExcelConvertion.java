package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.N;
import static com.ey.advisory.common.GSTConstants.NA;

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
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToOutwardB2CExcelConvertion")
@Slf4j
public class SRFileToOutwardB2CExcelConvertion {
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

	public List<OutwardB2cExcelEntity> convertSRFileToOutwardB2cExcel(
			List<Object[]> listOfB2c, Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardB2cExcelEntity> listB2c = new ArrayList<>();

		OutwardB2cExcelEntity outb2c = null;
		for (Object[] arr : listOfB2c) {
			outb2c = new OutwardB2cExcelEntity();

			String returnType = getValues(arr[0]);
			String supplierGstin = getValues(arr[1]);
			String returnPeriod = getValues(arr[2]);
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month <= 12 && month >= 01)
							&& (year <= 9999 && year >= 0000)) {
					Integer derivedRetPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					derivedRePeroid = String.valueOf(derivedRetPeriod);
				}
				}
			}
			String diffPer = (arr[3] != null) ? String.valueOf(arr[3]) : N;
			String section7 = (arr[4] != null) ? String.valueOf(arr[4]) : N;
			String autoPopRefund = (arr[5] != null) ? String.valueOf(arr[5])
					: N;
			String pos = getValues(arr[6]);
			String hsnSac = getValues(arr[7]);
			String uom = getValues(arr[8]);
			String qnt = getValues(arr[9]);
			String rate = getValues(arr[10]);
			String taxableValue = getValues(arr[11]);
			String igst = getValues(arr[12]);
			String cgst = getValues(arr[13]);
			String sgst = getValues(arr[14]);
			String cess = getValues(arr[15]);
			String totalAmt = getValues(arr[16]);
			String stateAppCess = getValues(arr[17]);
			String stateCessRate = getValues(arr[18]);
			String stateCessamt = getValues(arr[19]);
			String tcsFlag = (arr[20] != null) ? String.valueOf(arr[20]) : N;
			String ecomGstin = getValues(arr[21]);
			String ecomSuppMade = getValues(arr[22]);
			String ecomSuppRet = getValues(arr[23]);
			String ecomSuppNet = getValues(arr[24]);
			String tcsAmt = getValues(arr[25]);
			String profitCentre = getNoTrancateValues(arr[26]);
			String plant = getNoTrancateValues(arr[27]);
			String division = getNoTrancateValues(arr[28]);
			String location = getNoTrancateValues(arr[29]);
			String salesOrg = getNoTrancateValues(arr[30]);
			String distributeChannel = getNoTrancateValues(arr[31]);
			String userAccess1 = getNoTrancateValues(arr[32]);
			String userAccess2 = getNoTrancateValues(arr[33]);
			String userAccess3 = getNoTrancateValues(arr[34]);
			String userAccess4 = getNoTrancateValues(arr[35]);
			String userAccess5 = getNoTrancateValues(arr[36]);
			String userAccess6 = getNoTrancateValues(arr[37]);
			String userDef1 = getNoTrancateValues(arr[38]);
			String userDef2 = getNoTrancateValues(arr[39]);
			String userDef3 = getNoTrancateValues(arr[40]);
			String b2cGstnKey = getB2cKeyValues(arr);
			String b2cInvKey = getB2cInvKeyValues(arr);
			if (updateFileStatus != null) {
				outb2c.setFileId(updateFileStatus.getId());
				outb2c.setCreatedBy(updateFileStatus.getUpdatedBy());
			}

			outb2c.setRetType(returnType);
			outb2c.setSgstin(supplierGstin);
			outb2c.setRetPeriod(returnPeriod);
			outb2c.setDiffPercent(diffPer);
			outb2c.setSec7OfIgstFlag(section7);
			outb2c.setAutoPopulateToRefund(autoPopRefund);
			outb2c.setPos(pos);
			outb2c.setHsnSac(hsnSac);
			outb2c.setUom(uom);
			outb2c.setQuentity(qnt);
			outb2c.setRate(rate);
			outb2c.setStateApplyCess(stateAppCess);
			outb2c.setStateCessRate(stateCessRate);
			outb2c.setTaxableValue(taxableValue);
			outb2c.setTcsFlag(tcsFlag);
			outb2c.setEcomGstin(ecomGstin);
			outb2c.setEcomValueSuppMade(ecomSuppMade);
			outb2c.setEcomValSuppRet(ecomSuppRet);
			outb2c.setEcomNetValSupp(ecomSuppNet);
			outb2c.setIgstAmt(igst);
			outb2c.setCgstAmt(cgst);
			outb2c.setSgstAmt(sgst);
			outb2c.setCessAmt(cess);
			outb2c.setTcsAmt(tcsAmt);
			outb2c.setStateCessAmt(stateCessamt);
			outb2c.setTotalValue(totalAmt);
			outb2c.setProfitCentre(profitCentre);
			outb2c.setPlant(plant);
			outb2c.setDivision(division);
			outb2c.setLocation(location);
			outb2c.setSalesOrganisation(salesOrg);
			outb2c.setDistributionChannel(distributeChannel);
			outb2c.setUserAccess1(userAccess1);
			outb2c.setUserAccess2(userAccess2);
			outb2c.setUserAccess3(userAccess3);
			outb2c.setUserAccess4(userAccess4);
			outb2c.setUserAccess5(userAccess5);
			outb2c.setUserAccess6(userAccess6);
			outb2c.setDerivedRetPeriod(derivedRePeroid);
			if (b2cInvKey != null && b2cInvKey.trim().length() > 2200) {
				outb2c.setB2cInvKey(b2cInvKey.substring(0, 2200));
			} else {
				outb2c.setB2cInvKey(b2cInvKey);
			}
			if (b2cGstnKey != null && b2cGstnKey.trim().length() > 100) {
				outb2c.setB2cGstnKey(b2cGstnKey.substring(0, 100));
			} else {
				outb2c.setB2cGstnKey(b2cGstnKey);
			}
			outb2c.setUserDef1(userDef1);
			outb2c.setUserDef2(userDef2);
			outb2c.setUserDef3(userDef3);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			outb2c.setCreatedOn(convertNow);
			listB2c.add(outb2c);
		}
		return listB2c;
	}

	public String getB2cKeyValues(Object[] arr) {

		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";

		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";

		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])) : "";

		String pos = (arr[6] != null) ? (String.valueOf(arr[6])) : "";

		String rate = (arr[10] != null) ? (String.valueOf(arr[10])) : "";

		String diffPer = (arr[3] != null) ? (String.valueOf(arr[3])) : N;

		String sec7 = (arr[4] != null) ? (String.valueOf(arr[4])) : N;
		String autoPopulateRefund = (arr[5] != null) ? (String.valueOf(arr[5]))
				: N;

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(pos).add(rate)
				.add(diffPer).add(sec7).add(autoPopulateRefund).toString();

	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	private String getNoTrancateValues(Object object) {
		String value = (object != null) ? String.valueOf(object).trim() : null;

		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

	public String getB2cInvKeyValues(Object[] arr) {
		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";

		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";

		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";

		String pos = (arr[6] != null) ? (String.valueOf(arr[6])).trim() : "";

		String rate = (arr[10] != null) ? (String.valueOf(arr[10])).trim() : "";

		String diffPer = (arr[3] != null) ? (String.valueOf(arr[3])).trim() : N;

		String sec7 = (arr[4] != null) ? (String.valueOf(arr[4])).trim() : N;
		String autoPopulateRefund = (arr[5] != null)
				? (String.valueOf(arr[5])).trim() : N;

		String profitCentre = (arr[26] != null)
				? (String.valueOf(arr[26])).trim() : NA;
		String plant = (arr[27] != null) ? (String.valueOf(arr[27])).trim()
				: NA;
		String division = (arr[28] != null) ? (String.valueOf(arr[28])).trim()
				: NA;
		String location = (arr[29] != null) ? (String.valueOf(arr[29])).trim()
				: NA;
		String salesOrg = (arr[30] != null) ? (String.valueOf(arr[30])).trim()
				: NA;
		String distributeChannel = (arr[31] != null)
				? (String.valueOf(arr[31])).trim() : NA;
		String userAccess1 = (arr[32] != null)
				? (String.valueOf(arr[32])).trim() : NA;
		String userAccess2 = (arr[33] != null)
				? (String.valueOf(arr[33])).trim() : NA;
		String userAccess3 = (arr[34] != null)
				? (String.valueOf(arr[34])).trim() : NA;
		String userAccess4 = (arr[35] != null)
				? (String.valueOf(arr[35])).trim() : NA;
		String userAccess5 = (arr[36] != null)
				? (String.valueOf(arr[36])).trim() : NA;
		String userAccess6 = (arr[37] != null)
				? (String.valueOf(arr[37])).trim() : NA;

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(pos).add(rate)
				.add(diffPer).add(sec7).add(autoPopulateRefund)
				.add(profitCentre).add(plant).add(division).add(location)
				.add(salesOrg).add(distributeChannel).add(userAccess1)
				.add(userAccess2).add(userAccess3).add(userAccess4)
				.add(userAccess5).add(userAccess6).toString();
	}

	public String getB2cKeyValues(String retType, String sgstin,
			String retPeriod, String pos, String rate, String diffPercent,
			String sec7OfIgstFlag, String autoPopulateToRefund) {
		retType = (retType != null) ? String.valueOf(retPeriod).trim() : "";
		sgstin = (sgstin != null) ? String.valueOf(sgstin).trim() : "";
		retPeriod = (retPeriod != null) ? String.valueOf(retPeriod).trim() : "";
		pos = (pos != null) ? String.valueOf(pos).trim() : "";
		rate = (rate != null) ? String.valueOf(rate).trim() : "";
		diffPercent = (diffPercent != null) ? String.valueOf(diffPercent).trim()
				: N;
		sec7OfIgstFlag = (sec7OfIgstFlag != null)
				? String.valueOf(sec7OfIgstFlag).trim() : N;
		autoPopulateToRefund = (autoPopulateToRefund != null)
				? String.valueOf(autoPopulateToRefund).trim() : N;

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(retType).add(sgstin)
				.add(retPeriod).add(pos).add(rate).add(diffPercent)
				.add(sec7OfIgstFlag).add(autoPopulateToRefund).toString();

	}

	public String getB2cInvKeyValues(String retType, String sgstin,
			String retPeriod, String pos, String rate, String diffPercent,
			String sec7OfIgstFlag, String autoPopulateToRefund,
			String profitCentre, String plant, String division, String location,
			String salesOrganisation, String distributionChannel,
			String userAccess1, String userAccess2, String userAccess3,
			String userAccess4, String userAccess5, String userAccess6) {

		retType = (retType != null) ? String.valueOf(retPeriod).trim() : "";
		sgstin = (sgstin != null) ? String.valueOf(sgstin).trim() : "";
		retPeriod = (retPeriod != null) ? String.valueOf(retPeriod).trim() : "";
		pos = (pos != null) ? String.valueOf(pos).trim() : "";
		rate = (rate != null) ? String.valueOf(rate).trim() : "";
		diffPercent = (diffPercent != null) ? String.valueOf(diffPercent).trim()
				: N;
		sec7OfIgstFlag = (sec7OfIgstFlag != null)
				? String.valueOf(sec7OfIgstFlag).trim() : N;
		autoPopulateToRefund = (autoPopulateToRefund != null)
				? String.valueOf(autoPopulateToRefund).trim() : N;

		profitCentre = (profitCentre != null)
				? String.valueOf(profitCentre).trim() : NA;
		plant = (plant != null) ? String.valueOf(plant).trim() : NA;
		division = (division != null) ? String.valueOf(division).trim() : NA;
		location = (location != null) ? String.valueOf(location).trim() : NA;
		salesOrganisation = (salesOrganisation != null)
				? String.valueOf(salesOrganisation).trim() : NA;
		distributionChannel = (distributionChannel != null)
				? String.valueOf(distributionChannel).trim() : NA;
		userAccess1 = (userAccess1 != null) ? String.valueOf(userAccess1).trim()
				: NA;
		userAccess2 = (userAccess2 != null) ? String.valueOf(userAccess2).trim()
				: NA;

		userAccess3 = (userAccess3 != null) ? String.valueOf(userAccess3).trim()
				: "";
		userAccess4 = (userAccess4 != null) ? String.valueOf(userAccess4).trim()
				: NA;
		userAccess5 = (userAccess5 != null) ? String.valueOf(userAccess5).trim()
				: NA;
		userAccess6 = (userAccess6 != null) ? String.valueOf(userAccess6).trim()
				: NA;

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(retType).add(sgstin)
				.add(retPeriod).add(pos).add(rate).add(diffPercent)
				.add(sec7OfIgstFlag).add(autoPopulateToRefund).add(profitCentre)
				.add(plant).add(division).add(location).add(salesOrganisation)
				.add(distributionChannel).add(userAccess1).add(userAccess2)
				.add(userAccess3).add(userAccess4).add(userAccess5)
				.add(userAccess6).toString();

	}

	public List<OutwardB2cExcelEntity> convertSRFileToOutwardB2c(
			List<OutwardB2cExcelEntity> strucProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardB2cExcelEntity> listB2cs = new ArrayList<>();
 
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

		OutwardB2cExcelEntity b2c = null; 
		for (OutwardB2cExcelEntity arr : strucProcessedRecords) {
			b2c = new OutwardB2cExcelEntity();
			Long entityId = gstinAndEntityMap.get(arr.getSgstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + arr.getSgstin() + " - entityId "
						+ entityId);
			} 
			b2c.setGroupId(groupId);
			b2c.setEntityId(entityId);
			b2c.setEntityConfigParamMap(map);
			b2c.setEntityAtConfMap(entityAtConfigMap);
			b2c.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (b2c.getSgstin() != null
						&& !b2c.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(b2c.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			b2c.setId(arr.getId());
			b2c.setRetType(arr.getRetType());
			b2c.setSgstin(arr.getSgstin());
			b2c.setRetPeriod(arr.getRetPeriod());
			b2c.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			b2c.setDiffPercent(arr.getDiffPercent());
			b2c.setSec7OfIgstFlag(arr.getSec7OfIgstFlag());
			b2c.setAutoPopulateToRefund(arr.getAutoPopulateToRefund());
			b2c.setPos(arr.getPos());
			b2c.setHsnSac(arr.getHsnSac());
			b2c.setUom(arr.getUom());
			b2c.setQuentity(arr.getQuentity());
			b2c.setRate(arr.getRate());
			b2c.setTaxableValue(arr.getTaxableValue());
			b2c.setIgstAmt(arr.getIgstAmt());
			b2c.setCgstAmt(arr.getCgstAmt());
			b2c.setSgstAmt(arr.getSgstAmt());
			b2c.setCessAmt(arr.getCessAmt());
			b2c.setTotalValue(arr.getTotalValue());
			b2c.setStateApplyCess(arr.getStateApplyCess());
			b2c.setStateCessRate(arr.getStateCessRate());
			b2c.setStateCessAmt(arr.getStateCessAmt());
			b2c.setTcsFlag(arr.getTcsFlag());
			b2c.setTcsAmt(arr.getTcsAmt());
			b2c.setEcomGstin(arr.getEcomGstin());
			b2c.setEcomValueSuppMade(arr.getEcomValueSuppMade());
			b2c.setEcomValSuppRet(arr.getEcomValSuppRet());
			b2c.setEcomNetValSupp(arr.getEcomNetValSupp());
			b2c.setProfitCentre(arr.getProfitCentre());
			b2c.setPlant(arr.getPlant());
			b2c.setDivision(arr.getDivision());
			b2c.setLocation(arr.getLocation());
			b2c.setSalesOrganisation(arr.getSalesOrganisation());
			b2c.setDistributionChannel(arr.getDistributionChannel());
			b2c.setUserAccess1(arr.getUserAccess1());
			b2c.setUserAccess2(arr.getUserAccess2());
			b2c.setUserAccess3(arr.getUserAccess3());
			b2c.setUserAccess4(arr.getUserAccess4());
			b2c.setUserAccess5(arr.getUserAccess5());
			b2c.setUserAccess6(arr.getUserAccess6());

			if (arr.getB2cGstnKey() != null
					&& arr.getB2cGstnKey().length() > 1000) {
				b2c.setB2cGstnKey(
						arr.getB2cGstnKey().substring(0, 1000));
			} else {
				b2c.setB2cGstnKey(arr.getB2cGstnKey());
			}
			if (arr.getB2cInvKey() != null
					&& arr.getB2cInvKey().length() > 2200) {
				b2c.setB2cInvKey(
						arr.getB2cInvKey().substring(0, 2200));
			} else {
				b2c.setB2cInvKey(arr.getB2cInvKey());
			}

			b2c.setUserDef1(arr.getUserDef1());
			b2c.setUserDef2(arr.getUserDef2());
			b2c.setUserDef3(arr.getUserDef3());
			if (updateFileStatus != null) {
				b2c.setFileId(updateFileStatus.getId());
				b2c.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			b2c.setCreatedOn(convertNow);
			b2c.setInfo(arr.isInfo());
			b2c.setDelete(arr.isDelete());
			b2c.setError(arr.isError());
			listB2cs.add(b2c);
		}
		return listB2cs;
	}
}
