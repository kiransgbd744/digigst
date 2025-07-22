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
import com.ey.advisory.app.data.entities.client.Ret1And1AEntity;
import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
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

@Component("SRFileToRet1And1AExcelConvertion")
@Slf4j
public class SRFileToRet1And1AExcelConvertion {

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

	public List<Ret1And1AExcelEntity> convertSRFileToRet1And1AExcel(
			List<Object[]> ret1And1Adata,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Ret1And1AExcelEntity> listRet11As = new ArrayList<>();
		Ret1And1AExcelEntity ret11A = null;
		for (Object[] arr : ret1And1Adata) {
			ret11A = new Ret1And1AExcelEntity();
			String retType = getValues(arr[0]);
			String supplierGstin = getValues(arr[1]);
			String returnPeriod = getValues(arr[2]);
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month < 12 && month > 01)
							&& (year < 9999 && year > 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String returnTable = getValues(arr[3]);
			String value = getValues(arr[4]);
			String igst = getValues(arr[5]);
			String cgst = getValues(arr[6]);
			String sgst = getValues(arr[7]);
			String cess = getValues(arr[8]);
			String profitCentre = getNoTrancateValues(arr[9]);
			String plant = getNoTrancateValues(arr[10]);
			String division = getNoTrancateValues(arr[11]);
			String location = getNoTrancateValues(arr[12]);
			String salesOrg = getNoTrancateValues(arr[13]);
			String distributeChannel = getNoTrancateValues(arr[14]);
			String userAccess1 = getNoTrancateValues(arr[15]);
			String userAccess2 = getNoTrancateValues(arr[16]);
			String userAccess3 = getNoTrancateValues(arr[17]);
			String userAccess4 = getNoTrancateValues(arr[18]);
			String userAccess5 = getNoTrancateValues(arr[19]);
			String userAccess6 = getNoTrancateValues(arr[20]);
			String userDef1 = getNoTrancateValues(arr[21]);
			String userDef2 = getNoTrancateValues(arr[22]);
			String userDef3 = getNoTrancateValues(arr[23]);
			String ret1And1AGstnKey = getRet1And1AGstnKey(arr);
			String ret1And1AInvKey = getRet1And1AInvKey(arr);

			ret11A.setRetType(retType);
			ret11A.setSgstin(supplierGstin);
			ret11A.setRetPeriod(returnPeriod);
			ret11A.setDerivedRetPeriod(derivedRePeroid);
			ret11A.setReturnTable(returnTable);
			ret11A.setTaxableValue(value);
			ret11A.setIgstAmt(igst);
			ret11A.setCgstAmt(cgst);
			ret11A.setSgstAmt(sgst);
			ret11A.setCessAmt(cess);
			ret11A.setProfitCentre(profitCentre);
			ret11A.setPlant(plant);
			ret11A.setDivision(division);
			ret11A.setLocation(location);
			ret11A.setSalesOrganisation(salesOrg);
			ret11A.setDistributionChannel(distributeChannel);
			ret11A.setUserAccess1(userAccess1);
			ret11A.setUserAccess2(userAccess2);
			ret11A.setUserAccess3(userAccess3);
			ret11A.setUserAccess4(userAccess4);
			ret11A.setUserAccess5(userAccess5);
			ret11A.setUserAccess6(userAccess6);

			if (ret1And1AGstnKey != null && ret1And1AGstnKey.length() > 1000) {
				ret11A.setRet1And1AGstnkey(ret1And1AGstnKey.substring(0, 1000));
			} else {
				ret11A.setRet1And1AGstnkey(ret1And1AGstnKey);
			}
			if (ret1And1AInvKey != null && ret1And1AInvKey.length() > 2200) {
				ret11A.setRet1And1AInvkey(ret1And1AInvKey.substring(0, 2200));
			} else {
				ret11A.setRet1And1AInvkey(ret1And1AInvKey);
			}

			ret11A.setUserDef1(userDef1);
			ret11A.setUserDef2(userDef2);
			ret11A.setUserDef3(userDef3);
			if (updateFileStatus != null) {
				ret11A.setFileId(updateFileStatus.getId());
				ret11A.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ret11A.setCreatedOn(convertNow);
			listRet11As.add(ret11A);
		}
		return listRet11As;
	}

	public String getRet1And1AInvKey(Object[] arr) {
		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String returnTable = (arr[3] != null) ? (String.valueOf(arr[3])).trim()
				: "";
		String profitCentre = (arr[9] != null) ? (String.valueOf(arr[9])).trim()
				: NA;
		String plant = (arr[10] != null) ? (String.valueOf(arr[10])).trim()
				: NA;
		String division = (arr[11] != null) ? (String.valueOf(arr[11])).trim()
				: NA;
		String location = (arr[12] != null) ? (String.valueOf(arr[12])).trim()
				: NA;
		String salesOrg = (arr[13] != null) ? (String.valueOf(arr[13])).trim()
				: NA;
		String distributeChannel = (arr[14] != null)
				? (String.valueOf(arr[14])).trim() : NA;
		String userAccess1 = (arr[15] != null)
				? (String.valueOf(arr[15])).trim() : NA;
		String userAccess2 = (arr[16] != null)
				? (String.valueOf(arr[16])).trim() : NA;
		String userAccess3 = (arr[17] != null)
				? (String.valueOf(arr[17])).trim() : NA;
		String userAccess4 = (arr[18] != null)
				? (String.valueOf(arr[18])).trim() : NA;
		String userAccess5 = (arr[19] != null)
				? (String.valueOf(arr[19])).trim() : NA;
		String userAccess6 = (arr[20] != null)
				? (String.valueOf(arr[20])).trim() : NA;
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(returnTable)
				.add(profitCentre).add(plant).add(division).add(location)
				.add(salesOrg).add(distributeChannel).add(userAccess1)
				.add(userAccess2).add(userAccess3).add(userAccess4)
				.add(userAccess5).add(userAccess6).toString();

	}

	public String getRet1And1AGstnKey(Object[] arr) {
		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String returnTable = (arr[3] != null) ? (String.valueOf(arr[3])).trim()
				: "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(returnTable)
				.toString();
	}

	private String getNoTrancateValues(Object object) {
		String value = (object != null) ? String.valueOf(object).trim() : null;
		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public List<Ret1And1AEntity> convertSRFileToRet1And1ABusinessErrorOut(
			List<Ret1And1AExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Ret1And1AEntity> listB2c = new ArrayList<>();

		Ret1And1AEntity ret = null;
		for (Ret1And1AExcelEntity arr : businessProcessedRecords) {
			ret = new Ret1And1AEntity();

			BigDecimal igstAmt = BigDecimal.ZERO;
			String igst = arr.getIgstAmt();
			if (igst != null && !igst.isEmpty()) {
				igstAmt = NumberFomatUtil.getBigDecimal(igst);
				ret.setIgstAmt(igstAmt);
			}

			BigDecimal cgstAmt = BigDecimal.ZERO;
			String cgst = arr.getCgstAmt();
			if (cgst != null && !cgst.isEmpty()) {
				cgstAmt = NumberFomatUtil.getBigDecimal(cgst);
				ret.setCgstAmt(cgstAmt);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgsts = arr.getSgstAmt();
			if (sgsts != null && !sgsts.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgsts);
				ret.setSgstAmt(sgst);
			}

			BigDecimal cess = BigDecimal.ZERO;
			String cesss = arr.getCessAmt();
			if (cesss != null && !cesss.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cesss);
				ret.setCessAmt(cess);
			}
			BigDecimal taxableValue = BigDecimal.ZERO;
			String taxableVal = arr.getTaxableValue();
			if (taxableVal != null && !taxableVal.isEmpty()) {
				taxableValue = NumberFomatUtil.getBigDecimal(taxableVal);
				ret.setTaxableValue(taxableValue);
			}
			String userDef1 = arr.getUserDef1();
			String userDef2 = arr.getUserDef2();
			String userDef3 = arr.getUserDef3();

			Integer deriRetPeriod = null;

			if (arr.getRetPeriod() != null && !arr.getRetPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(arr.getRetPeriod());
			}

			String retGstnKey = arr.getRet1And1AGstnkey();
			String retInvKey = arr.getRet1And1AInvkey();

			ret.setRetType(arr.getRetType());
			ret.setSgstin(arr.getSgstin());
			ret.setRetPeriod(arr.getRetPeriod());
			ret.setDerivedRetPeriod(deriRetPeriod);
			ret.setReturnTable(arr.getReturnTable());
			if (updateFileStatus != null) {
				ret.setFileId(updateFileStatus.getId());
			}
			ret.setAsEnterTableId(arr.getId());
			ret.setInfo(arr.isInfo());
			ret.setTaxableValue(taxableValue);
			ret.setProfitCentre(arr.getProfitCentre());
			ret.setPlant(arr.getPlant());
			ret.setDivision(arr.getDivision());
			ret.setLocation(arr.getLocation());
			ret.setSalesOrganisation(arr.getSalesOrganisation());
			ret.setDistributionChannel(arr.getDistributionChannel());
			ret.setUserAccess1(arr.getUserAccess1());
			ret.setUserAccess2(arr.getUserAccess2());
			ret.setUserAccess3(arr.getUserAccess3());
			ret.setUserAccess4(arr.getUserAccess4());
			ret.setUserAccess5(arr.getUserAccess5());
			ret.setUserAccess6(arr.getUserAccess6());

			if (userDef1 != null && !userDef1.isEmpty()) {
				if (userDef1.length() > 100) {
					ret.setUserDef1(userDef1.substring(0, 100));
				}
			} else {
				ret.setUserDef1(userDef1);
			}
			if (userDef2 != null && !userDef2.isEmpty()) {
				if (userDef2.length() > 100) {
					ret.setUserDef2(userDef2.substring(0, 100));
				}
			} else {
				ret.setUserDef2(userDef2);
			}
			if (userDef3 != null && !userDef3.isEmpty()) {
				if (userDef3.length() > 100) {
					ret.setUserDef3(userDef3.substring(0, 100));
				}
			} else {
				ret.setUserDef3(userDef3);
			}
			ret.setRetGstnKey(retGstnKey);
			ret.setRetInvKey(retInvKey);
			ret.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ret.setCreatedOn(convertNow);
			listB2c.add(ret);
		}
		return listB2c;
	}

	public List<Ret1And1AExcelEntity> convertSRFileToRet1And1A(
			List<Ret1And1AExcelEntity> strucProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Ret1And1AExcelEntity> listRet11As = new ArrayList<>();

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

		Ret1And1AExcelEntity ret11A = null;
		for (Ret1And1AExcelEntity arr : strucProcessedRecords) {
			ret11A = new Ret1And1AExcelEntity();
			Long entityId = gstinAndEntityMap.get(arr.getSgstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + arr.getSgstin() + " - entityId "
						+ entityId);
			}
			ret11A.setGroupId(groupId);
			ret11A.setEntityId(entityId);
			ret11A.setEntityConfigParamMap(map);
			ret11A.setEntityAtConfMap(entityAtConfigMap);
			ret11A.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (ret11A.getSgstin() != null
						&& !ret11A.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(ret11A.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			ret11A.setId(arr.getId());
			ret11A.setRetType(arr.getRetType());
			ret11A.setSgstin(arr.getSgstin());
			ret11A.setRetPeriod(arr.getRetPeriod());
			ret11A.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			ret11A.setReturnTable(arr.getReturnTable());
			ret11A.setTaxableValue(arr.getTaxableValue());
			ret11A.setIgstAmt(arr.getIgstAmt());
			ret11A.setCgstAmt(arr.getCgstAmt());
			ret11A.setSgstAmt(arr.getSgstAmt());
			ret11A.setCessAmt(arr.getCessAmt());
			ret11A.setProfitCentre(arr.getProfitCentre());
			ret11A.setPlant(arr.getPlant());
			ret11A.setDivision(arr.getDivision());
			ret11A.setLocation(arr.getLocation());
			ret11A.setSalesOrganisation(arr.getSalesOrganisation());
			ret11A.setDistributionChannel(arr.getDistributionChannel());
			ret11A.setUserAccess1(arr.getUserAccess1());
			ret11A.setUserAccess2(arr.getUserAccess2());
			ret11A.setUserAccess3(arr.getUserAccess3());
			ret11A.setUserAccess4(arr.getUserAccess4());
			ret11A.setUserAccess5(arr.getUserAccess5());
			ret11A.setUserAccess6(arr.getUserAccess6());

			if (arr.getRet1And1AGstnkey() != null
					&& arr.getRet1And1AGstnkey().length() > 1000) {
				ret11A.setRet1And1AGstnkey(
						arr.getRet1And1AGstnkey().substring(0, 1000));
			} else {
				ret11A.setRet1And1AGstnkey(arr.getRet1And1AGstnkey());
			}
			if (arr.getRet1And1AInvkey() != null
					&& arr.getRet1And1AInvkey().length() > 2200) {
				ret11A.setRet1And1AInvkey(
						arr.getRet1And1AInvkey().substring(0, 2200));
			} else {
				ret11A.setRet1And1AInvkey(arr.getRet1And1AInvkey());
			}

			ret11A.setUserDef1(arr.getUserDef1());
			ret11A.setUserDef2(arr.getUserDef2());
			ret11A.setUserDef3(arr.getUserDef3());
			if (updateFileStatus != null) {
				ret11A.setFileId(updateFileStatus.getId());
				ret11A.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ret11A.setCreatedOn(convertNow);
			ret11A.setInfo(arr.isInfo());
			ret11A.setDelete(arr.isDelete());
			ret11A.setError(arr.isError());
			listRet11As.add(ret11A);
		}
		return listRet11As;
	}
}
