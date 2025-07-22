package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.RefundEntity;
import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToRefundExcelConvertion")
public class SRFileToRefundExcelConvertion {

	/*@Autowired
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
	private MasterCustomerService masterCustomerSvc;*/

	public List<RefundsExcelEntity> convertSRFileToRefundExcel(
			List<Object[]> refundList, Gstr1FileStatusEntity updateFileStatus) {

		/*List<Long> entityIds = clientGroupService
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
*/
		List<RefundsExcelEntity> listRefunds = new ArrayList<>();
		RefundsExcelEntity refund = null;
		for (Object[] arr : refundList) {
			refund = new RefundsExcelEntity();

			String sNumber = getValues(arr[0]);
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

			String desc = getValues(arr[3]);
			String tax = getValues(arr[4]);
			String interest = getValues(arr[5]);
			String penalty = getValues(arr[6]);
			String fee = getValues(arr[7]);
			String other = getValues(arr[8]);
			String total = getValues(arr[9]);
			String userDef1 = getNoTrancateValues(arr[10]);
			String userDef2 = getNoTrancateValues(arr[11]);
			String userDef3 = getNoTrancateValues(arr[12]);
			String refundGstnKey = getRefundGstnKey(arr);
			String refundInvKey = getRefundInvKey(arr);

			/*Long entityId = gstinAndEntityMap.get(supplierGstin);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"sgstin " + supplierGstin + " - entityId " + entityId);
			}
			refund.setGroupId(groupId);
			refund.setEntityId(entityId);
			refund.setEntityConfigParamMap(map);
			refund.setEntityAtConfMap(entityAtConfigMap);
			refund.setEntityAtValMap(entityAtValMap);
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (refund.getSgstin() != null
						&& !refund.getSgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(refund.getSgstin()::equalsIgnoreCase);
				}
			}

			// ret11A.isCgstInMasterCust(isCgstInMasterCustTable);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			// sets form type as ANX1 or GSTR1 based on Entity Tax period
			// setFormReturnType(ret11A, gstinAndEntityMap, questionAnsMap);
			// Onboarding Configurable Parameters
			
			 * outwardConfigParams.configurableParameters(ret11A,
			 * questionAnsMap);
			 */

			refund.setSNo(sNumber);
			refund.setSgstin(supplierGstin);
			refund.setRetPeriod(returnPeriod);
			refund.setDerivedRetPeriod(derivedRePeroid);
			refund.setDesc(desc);
			refund.setTax(tax);
			refund.setInterest(interest);
			refund.setPenalty(penalty);
			refund.setFee(fee);
			refund.setOther(other);
			refund.setTotal(total);
			refund.setUserDefined1(userDef1);
			refund.setUserDefined2(userDef2);
			refund.setUserDefined3(userDef3);

			if (refundGstnKey != null && refundGstnKey.length() > 1000) {
				refund.setRefundGstnkey(refundGstnKey.substring(0, 1000));
			} else {
				refund.setRefundGstnkey(refundGstnKey);
			}
			if (refundInvKey != null && refundInvKey.length() > 2200) {
				refund.setRefundInvkey(refundInvKey.substring(0, 2220));
			} else {
				refund.setRefundInvkey(refundInvKey);
			}
			if (updateFileStatus != null) {
				refund.setFileId(updateFileStatus.getId());
				refund.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			refund.setCreatedOn(convertNow);
			listRefunds.add(refund);
		}
		return listRefunds;
	}

	public String getRefundInvKey(Object[] arr) {
		String sNo = (arr[0] != null) ? (String.valueOf(arr[0])).trim() : "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String desc = (arr[3] != null) ? (String.valueOf(arr[3])).trim() : "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sNo)
				.add(supplierGstin).add(returnPeriod).add(desc).toString();
	}

	public String getRefundGstnKey(Object[] arr) {
		String sNo = (arr[0] != null) ? (String.valueOf(arr[0])).trim() : "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String desc = (arr[3] != null) ? (String.valueOf(arr[3])).trim() : "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sNo)
				.add(supplierGstin).add(returnPeriod).add(desc).toString();
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

	public List<RefundEntity> convertSRFileToRefundProcessed(
			List<RefundsExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<RefundEntity> listRefund = new ArrayList<>();

		RefundEntity refund = null;
		for (RefundsExcelEntity arr : businessProcessedRecords) {
			refund = new RefundEntity();

			BigDecimal taxAmt = BigDecimal.ZERO;
			String tax = arr.getTax();
			if (tax != null && !tax.isEmpty()) {
				taxAmt = NumberFomatUtil.getBigDecimal(tax);
				refund.setTax(taxAmt);
			}

			BigDecimal interest = BigDecimal.ZERO;
			String inter = arr.getInterest();
			if (inter != null && !inter.isEmpty()) {
				interest = NumberFomatUtil.getBigDecimal(inter);
				refund.setInterest(interest);
			}
			BigDecimal penaltyAmt = BigDecimal.ZERO;
			String penalty = arr.getPenalty();
			if (penalty != null && !penalty.isEmpty()) {
				penaltyAmt = NumberFomatUtil.getBigDecimal(penalty);
				refund.setPenalty(penaltyAmt);
			}

			BigDecimal feeAmt = BigDecimal.ZERO;
			String fee = arr.getFee();
			if (fee != null && !fee.isEmpty()) {
				feeAmt = NumberFomatUtil.getBigDecimal(fee);
				refund.setFee(feeAmt);
			}
			BigDecimal other = BigDecimal.ZERO;
			String otherAmt = arr.getOther();
			if (otherAmt != null && !otherAmt.isEmpty()) {
				other = NumberFomatUtil.getBigDecimal(otherAmt);
				refund.setOther(other);
			}
			String userDef1 = arr.getUserDefined1();
			String userDef2 = arr.getUserDefined2();
			String userDef3 = arr.getUserDefined3();

			Integer deriRetPeriod = null;

			if (arr.getRetPeriod() != null && !arr.getRetPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(arr.getRetPeriod());
			}

			String refundGstnKey = arr.getRefundGstnkey();
			String refundInvKey = arr.getRefundInvkey();

			refund.setSNo(Integer.valueOf(arr.getSNo()));
			refund.setSgstin(arr.getSgstin());
			refund.setRetPeriod(arr.getRetPeriod());
			refund.setDerivedRetPeriod(deriRetPeriod);
			refund.setDesc(arr.getDesc());
			if (updateFileStatus != null) {
				refund.setFileId(updateFileStatus.getId());
			}
			refund.setAsEnterTableId(arr.getId());
			refund.setInfo(arr.isInfo());

			if (userDef1 != null && !userDef1.isEmpty()) {
				if (userDef1.length() > 100) {
					refund.setUserDefined1(userDef1.substring(0, 100));
				}
			} else {
				refund.setUserDefined1(userDef1);
			}
			if (userDef2 != null && !userDef2.isEmpty()) {
				if (userDef2.length() > 100) {
					refund.setUserDefined2(userDef2.substring(0, 100));
				}
			} else {
				refund.setUserDefined2(userDef2);
			}
			if (userDef3 != null && !userDef3.isEmpty()) {
				if (userDef3.length() > 100) {
					refund.setUserDefined3(userDef3.substring(0, 100));
				}
			} else {
				refund.setUserDefined3(userDef3);
			}
			refund.setRefundGstnkey(refundGstnKey);
			refund.setRefundInvkey(refundInvKey);
			refund.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			refund.setCreatedOn(convertNow);
			listRefund.add(refund);
		}
		return listRefund;
	}
}
