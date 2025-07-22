package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr1AdvancedVerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr1AdvancedSummaryHelper")
public class Gstr1AdvancedSummaryHelper {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AdvancedSummaryHelper.class);
	
    @Autowired
    @Qualifier("StatecodeRepositoryMaster")
    private StatecodeRepository statecodeRepository;


	public Gstr1AdvancedVerticalSummaryRespDto convertVertical(Object[] arr,
			String docType) {
		Gstr1AdvancedVerticalSummaryRespDto obj = new Gstr1AdvancedVerticalSummaryRespDto();
		obj.setSection(docType);
		obj.setId(GenUtil.getBigInteger(arr[25]));
		obj.setTaxPeriod((String) arr[24]);
		obj.setSgstn((String) arr[23]);
		obj.setTransType((String) arr[0]);
		obj.setNewPos((String) arr[1]);
		String stateName = statecodeRepository.findStateNameByCode((String) arr[1]);
		if (stateName != null) {
			obj.setNewStateName(stateName);
		}

		if (arr[2] == null || arr[2].toString().isEmpty()) {
			obj.setNewRate(BigDecimal.ZERO);
		} else {
			obj.setNewRate((BigDecimal) arr[2]);
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			obj.setNewTaxableValue(BigDecimal.ZERO);
		} else {
			obj.setNewTaxableValue((BigDecimal) arr[3]);
		}
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			obj.setIgst(BigDecimal.ZERO);
		} else {
			obj.setIgst((BigDecimal) arr[4]);
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			obj.setCgst(BigDecimal.ZERO);
		} else {
			obj.setCgst((BigDecimal) arr[5]);
		}
		if (arr[6] == null || arr[6].toString().isEmpty()) {
			obj.setSgst(BigDecimal.ZERO);
		} else {
			obj.setSgst((BigDecimal) arr[6]);
		}
		if (arr[7] == null || arr[7].toString().isEmpty()) {
			obj.setCess(BigDecimal.ZERO);
		} else {
			obj.setCess((BigDecimal) arr[7]);
		}

		obj.setProfitCntr((String) arr[8]);
		obj.setPlant((String) arr[9]);
		obj.setDivision((String) arr[10]);
		obj.setLocation((String) arr[11]);
		obj.setSalesOrg((String) arr[12]);
		obj.setDistrChannel((String) arr[13]);
		obj.setUsrAccess1((String) arr[14]);
		obj.setUsrAccess2((String) arr[15]);
		obj.setUsrAccess3((String) arr[16]);
		obj.setUsrAccess4((String) arr[17]);
		obj.setUsrAccess5((String) arr[18]);
		obj.setUsrAccess6((String) arr[19]);
		obj.setUsrDefined1((String) arr[20]);
		obj.setUsrDefined2((String) arr[21]);
		obj.setUsrDefined3((String) arr[22]);
		if (docType.equalsIgnoreCase("ATA")
				|| docType.equalsIgnoreCase("TXPDA")) {
			obj.setMonth((String) arr[26]);
			obj.setOrgPos((String) arr[27]);
			String orgStateName = statecodeRepository.findStateNameByCode((String) arr[27]);
			if (orgStateName != null) {
				obj.setOrgStateName(orgStateName);
			}
			if (arr[28] == null || arr[28].toString().isEmpty()) {
				obj.setOrgRate(BigDecimal.ZERO);
			} else {
				obj.setOrgRate((BigDecimal) arr[28]);
			}
			if (arr[29] == null || arr[29].toString().isEmpty()) {
				obj.setOrgTaxableValue(BigDecimal.ZERO);
			} else {
				obj.setOrgTaxableValue((BigDecimal) arr[29]);
			}
		}
		return obj;
	}

	public Ret1AspVerticalSummaryDto convertGstn(Object[] arr) {
		Ret1AspVerticalSummaryDto obj = new Ret1AspVerticalSummaryDto();
		// obj.setPos((String) arr[0]);
		String stateName = statecodeRepository.findStateNameByCode((String) arr[0]);
		obj.setPos(((String) arr[0] + " - " + stateName));
		obj.setTransType((String) arr[1]);
		obj.setRate(arr[2] != null && !arr[2].toString().isEmpty()
				? (BigDecimal) arr[2] : BigDecimal.ZERO);
		obj.setTaxableValue(arr[3] != null && !arr[3].toString().isEmpty()
				? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setIgst(arr[4] != null && !arr[4].toString().isEmpty()
				? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setCgst(arr[5] != null && !arr[5].toString().isEmpty()
				? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setSgst(arr[6] != null && !arr[6].toString().isEmpty()
				? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setCess(arr[7] != null && !arr[7].toString().isEmpty()
				? (BigDecimal) arr[7] : BigDecimal.ZERO);
		return obj;
	}

	public Ret1AspVerticalSummaryDto convertSummary(Object[] arr) {
		Ret1AspVerticalSummaryDto obj = new Ret1AspVerticalSummaryDto();
		obj.setType((String) arr[0]);
		obj.setCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setInvoiceValue(arr[2] != null && !arr[2].toString().isEmpty()
				? (BigDecimal) arr[2] : BigDecimal.ZERO);
		obj.setTaxableValue(arr[3] != null && !arr[3].toString().isEmpty()
				? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setIgst(arr[4] != null && !arr[4].toString().isEmpty()
				? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setCgst(arr[5] != null && !arr[5].toString().isEmpty()
				? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setSgst(arr[6] != null && !arr[6].toString().isEmpty()
				? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setCess(arr[7] != null && !arr[7].toString().isEmpty()
				? (BigDecimal) arr[7] : BigDecimal.ZERO);
		return obj;
	}

}
