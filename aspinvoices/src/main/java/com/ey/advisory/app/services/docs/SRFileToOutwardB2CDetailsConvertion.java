package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardB2cEntity;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToOutwardB2CDetailsConvertion")
public class SRFileToOutwardB2CDetailsConvertion {

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("SRFileToOutwardB2CExcelConvertion")
	private SRFileToOutwardB2CExcelConvertion sRFileToOutwardB2CExcelConvertion;

	public List<OutwardB2cEntity> convertSRFileToOutwardB2c(
			List<OutwardB2cExcelEntity> strucProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardB2cEntity> listB2c = new ArrayList<>();

		OutwardB2cEntity outb2c = null;
		for (OutwardB2cExcelEntity arr : strucProcessedRecords) {
			outb2c = new OutwardB2cEntity();

			BigDecimal qnt = BigDecimal.ZERO;
			String quentity = arr.getQuentity();
			if (quentity != null && !quentity.isEmpty()) {
				qnt = NumberFomatUtil.getBigDecimal(quentity);
				outb2c.setQuentity(qnt);
			}

			BigDecimal rate = BigDecimal.ZERO;
			String rates = arr.getRate();
			if (rates != null && !rates.isEmpty()) {
				rate = NumberFomatUtil.getBigDecimal(rates);
				outb2c.setRate(rate);
			}
			BigDecimal taxableValue = BigDecimal.ZERO;
			String taxableVal = arr.getTaxableValue();
			if (taxableVal != null && !taxableVal.isEmpty()) {
				taxableValue = NumberFomatUtil.getBigDecimal(taxableVal);
				outb2c.setTaxableValue(taxableValue);
			}

			BigDecimal igst = BigDecimal.ZERO;
			String igsts = arr.getIgstAmt();
			if (igsts != null && !igsts.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igsts);
				outb2c.setIgstAmt(igst);
			}

			BigDecimal cgst = BigDecimal.ZERO;
			String cgsts = arr.getCgstAmt();
			if (cgsts != null && !cgsts.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgsts);
				outb2c.setCgstAmt(cgst);
			}

			BigDecimal sgst = BigDecimal.ZERO;
			String sgsts = arr.getSgstAmt();
			if (sgsts != null && !sgsts.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgsts);
				outb2c.setSgstAmt(sgst);
			}

			BigDecimal cess = BigDecimal.ZERO;
			String cesss = arr.getCessAmt();
			if (cesss != null && !cesss.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cesss);
				outb2c.setCessAmt(cess);
			}

			BigDecimal totalAmt = BigDecimal.ZERO;
			String totalAm = arr.getTotalValue();
			if (totalAm != null && !totalAm.isEmpty()) {
				totalAmt = NumberFomatUtil.getBigDecimal(totalAm);
				outb2c.setTotalValue(totalAmt);
			}

			BigDecimal stateCessRate = BigDecimal.ZERO;
			String stateCessRat = arr.getStateCessRate();
			if (stateCessRat != null && !stateCessRat.isEmpty()) {
				stateCessRate = NumberFomatUtil.getBigDecimal(stateCessRat);
				outb2c.setStateCessRate(stateCessRate);
			}

			BigDecimal stateCessAmt = BigDecimal.ZERO;
			String stateCessAm = arr.getStateCessAmt();
			if (stateCessAm != null && !stateCessAm.isEmpty()) {
				stateCessAmt = NumberFomatUtil.getBigDecimal(stateCessAm);
				outb2c.setStateCessAmt(stateCessAmt);
			}

			BigDecimal ecomSuppMade = BigDecimal.ZERO;
			String ecomSuppMades = arr.getEcomValueSuppMade();
			if (ecomSuppMades != null && !ecomSuppMades.isEmpty()) {
				ecomSuppMade = NumberFomatUtil.getBigDecimal(ecomSuppMades);
				outb2c.setEcomValueSuppMade(ecomSuppMade);
			}
			BigDecimal ecomSuppRet = BigDecimal.ZERO;
			String ecomSuppRets = arr.getEcomValSuppRet();
			if (ecomSuppRets != null && !ecomSuppRets.isEmpty()) {
				ecomSuppRet = NumberFomatUtil.getBigDecimal(ecomSuppRets);
				outb2c.setEcomValSuppRet(ecomSuppRet);
			}

			BigDecimal ecomSuppNet = BigDecimal.ZERO;
			String ecomSuppNets = arr.getEcomNetValSupp();
			if (ecomSuppNets != null && !ecomSuppNets.isEmpty()) {
				ecomSuppNet = NumberFomatUtil.getBigDecimal(ecomSuppNets);
				outb2c.setEcomNetValSupp(ecomSuppNet);
			}

			BigDecimal tcsAmt = BigDecimal.ZERO;
			String tcsAmts = arr.getTcsAmt();
			if (tcsAmts != null && !tcsAmts.isEmpty()) {
				tcsAmt = NumberFomatUtil.getBigDecimal(tcsAmts);
				outb2c.setTcsAmt(tcsAmt);
			}
			String userDef1 = arr.getUserDef1();
			String userDef2 = arr.getUserDef2();
			String userDef3 = arr.getUserDef3();

			Integer deriRetPeriod = null;
			
			if(arr.getRetPeriod() != null && !arr.getRetPeriod().isEmpty()){
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(arr.getRetPeriod());
			}
			
			
			String b2cGstnKey = arr.getB2cGstnKey();
			String b2cInvKey = arr.getB2cInvKey();
			outb2c.setRetType(arr.getRetType());
			outb2c.setSgstin(arr.getSgstin());
			outb2c.setRetPeriod(arr.getRetPeriod());
			outb2c.setDiffPercent(arr.getDiffPercent());
			outb2c.setSec7OfIgstFlag(arr.getSec7OfIgstFlag());
			outb2c.setAutoPopulateToRefund(arr.getAutoPopulateToRefund());
			if (updateFileStatus != null) {
				outb2c.setFileId(updateFileStatus.getId());
			}
			outb2c.setAsEnterTableId(arr.getId());
			outb2c.setInfo(arr.isInfo());
			outb2c.setPos(arr.getPos());
			outb2c.setHsnSac(arr.getHsnSac());
			outb2c.setUom(arr.getUom());
			outb2c.setStateApplyCess(arr.getStateApplyCess());
			outb2c.setTcsFlag(arr.getTcsFlag());
			outb2c.setEcomGstin(arr.getEcomGstin());
			outb2c.setProfitCentre(arr.getProfitCentre());
			outb2c.setPlant(arr.getPlant());
			outb2c.setDivision(arr.getDivision());
			outb2c.setLocation(arr.getLocation());
			outb2c.setSalesOrganisation(arr.getSalesOrganisation());
			outb2c.setDistributionChannel(arr.getDistributionChannel());
			outb2c.setUserAccess1(arr.getUserAccess1());
			outb2c.setUserAccess2(arr.getUserAccess2());
			outb2c.setUserAccess3(arr.getUserAccess3());
			outb2c.setUserAccess4(arr.getUserAccess4());
			outb2c.setUserAccess5(arr.getUserAccess5());
			outb2c.setUserAccess6(arr.getUserAccess6());

			if (userDef1 != null && !userDef1.isEmpty()) {
				if (userDef1.length() > 100) {
					outb2c.setUserDef1(userDef1.substring(0, 100));
				}
			} else {
				outb2c.setUserDef1(userDef1);
			}
			if (userDef2 != null && !userDef2.isEmpty()) {
				if (userDef2.length() > 100) {
					outb2c.setUserDef2(userDef2.substring(0, 100));
				}
			} else {
				outb2c.setUserDef2(userDef2);
			}
			if (userDef3 != null && !userDef3.isEmpty()) {
				if (userDef3.length() > 100) {
					outb2c.setUserDef3(userDef3.substring(0, 100));
				}
			} else {
				outb2c.setUserDef3(userDef3);
			}
			outb2c.setDerivedRetPeriod(deriRetPeriod);
			outb2c.setB2cGstnKey(b2cGstnKey);
			outb2c.setB2cInvKey(b2cInvKey);
			outb2c.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			outb2c.setCreatedOn(convertNow);
			listB2c.add(outb2c);
		}
		return listB2c;
	}
}
