package com.ey.advisory.app.services.search.simplified.docsummary;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.SetOffAndUtilEntity;
import com.ey.advisory.app.data.repositories.client.SetOffUtilRepository;
import com.ey.advisory.app.docs.dto.simplified.Ret1PaymentTaxDetailSummaryDto;
import com.ey.advisory.app.services.docs.SRFileToSetOffAndUtilExcelConvertion;
import com.ey.advisory.common.GenUtil;

@Service("Ret1PaymentUpdateService")
public class Ret1PaymentUpdateService {

	@Autowired
	@Qualifier("SetOffUtilRepository")
	SetOffUtilRepository paymentRepo;

	@Autowired
	@Qualifier("SRFileToSetOffAndUtilExcelConvertion")
	SRFileToSetOffAndUtilExcelConvertion paymentConversion;

	@Transactional(value = "clientTransactionManager")
	public SetOffAndUtilEntity updatePaymentData(
			List<Ret1PaymentTaxDetailSummaryDto> list) {

		SetOffAndUtilEntity save = null;
		for (Ret1PaymentTaxDetailSummaryDto dto : list) {

			SetOffAndUtilEntity entity = new SetOffAndUtilEntity();
			entity.setId(dto.getId());
			entity.setSgstin(dto.getGstin());
			entity.setRetPeriod(dto.getTaxPeriod());

			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getTaxPeriod());
			entity.setDerivedRetPeriod(derivedRetPeriod);
			entity.setDesc(dto.getDesc());
			entity.setTaxPayableRevCharge(dto.getTaxPayableRc());
			entity.setTaxPayableOthRevCharge(dto.getTaxPayableOtherRc());
			entity.setTaxAlreadyPaidRevCharge(dto.getTaxPaidRc());
			entity.setTaxAlreadyPaidOthRevCharge(dto.getTaxPaidOtherRc());
			entity.setAdjRevCharge(dto.getAdjLiabilityRc());
			entity.setAdjOthRevCharge(dto.getAdjLiabilityOtherRc());
			entity.setPaidThrouhItcIgst(dto.getItcPaidIgst());
			entity.setPaidThrouhItcSgst(dto.getItcPaidIgst());
			entity.setPaidThrouhItcCgst(dto.getItcPaidCgst());
			entity.setPaidThrouhItcCess(dto.getItcPaidCess());
			entity.setPaidInCashTaxInterest(dto.getCashPaidInterest());
			entity.setPaidInCashLateFee(dto.getCashPaidLateFee());
			entity.setPaidInCashTaxCess(dto.getCashPaidTax());
			entity.setSNo(dto.getsNo());
			entity.setUserDef1(dto.getUserDefined1());
			entity.setUserDef2(dto.getUserDefined2());
			entity.setUserDef3(dto.getUserDefined3());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");
			// generating key

			Object[] arrInvKey = { dto.getsNo(), entity.getRetType(),
					entity.getSgstin(), entity.getRetPeriod(),
					entity.getDesc() };

			Object[] arrGstnKey = { dto.getsNo(), entity.getRetType(),
					entity.getSgstin(), entity.getRetPeriod(),
					entity.getDesc() };

			String setOffInvKey = paymentConversion.getSetOffInvKey(arrInvKey);
			String setOffGstnKey = paymentConversion
					.getSetOffGstnKey(arrGstnKey);

			entity.setSetOffInvKey(setOffInvKey);
			entity.setSetOffGstnKey(setOffGstnKey);

			SetOffAndUtilEntity findId = paymentRepo.findId(entity.getId());
			if (findId != null) {
				if (findId.getId() != null) {
					// entity.setDelete(true);
					entity.setAsEnterTableId(findId.getAsEnterTableId());
					entity.setRetType(findId.getRetType());

					entity.setDerivedRetPeriod(findId.getDerivedRetPeriod());
					entity.setRetPeriod(findId.getRetPeriod());
					entity.setSgstin(findId.getSgstin());
					entity.setDesc(findId.getDesc());
					entity.setTaxPayableRevCharge(
							findId.getTaxAlreadyPaidRevCharge());
					entity.setTaxPayableOthRevCharge(
							findId.getTaxPayableOthRevCharge());
					entity.setTaxAlreadyPaidRevCharge(
							findId.getTaxAlreadyPaidRevCharge());
					entity.setTaxAlreadyPaidOthRevCharge(
							findId.getTaxAlreadyPaidOthRevCharge());
					entity.setAdjRevCharge(findId.getAdjRevCharge());
					entity.setAdjOthRevCharge(findId.getAdjOthRevCharge());
					entity.setPaidThrouhItcSgst(findId.getPaidThrouhItcSgst());
					entity.setPaidThrouhItcIgst(findId.getPaidThrouhItcIgst());
					entity.setPaidThrouhItcCgst(findId.getPaidThrouhItcCgst());
					entity.setPaidThrouhItcCess(findId.getPaidThrouhItcCess());
					entity.setPaidInCashTaxCess(findId.getPaidInCashTaxCess());
					entity.setPaidInCashTaxInterest(
							findId.getPaidInCashTaxInterest());
					entity.setPaidInCashLateFee(findId.getPaidInCashLateFee());
					entity.setUserDef1(findId.getUserDef1());
					entity.setUserDef2(findId.getUserDef2());
					entity.setUserDef3(findId.getUserDef3());
					entity.setSetOffInvKey(findId.getSetOffInvKey());
					entity.setSetOffGstnKey(findId.getSetOffGstnKey());
					entity.setFileId(findId.getFileId());
					entity.setInfo(findId.isInfo());
					entity.setCreatedBy(findId.getCreatedBy());
					entity.setCreatedOn(findId.getCreatedOn());
					entity.setModifiedBy("SYSTEM");
					entity.setModifiedOn(LocalDateTime.now());
					entity.setSNo(findId.getSNo());

				}
				if (findId.getId() != null) {
					if (setOffInvKey != null) {
						paymentRepo.UpdateSameInvKey(setOffInvKey);
					}

				}
			}

			save = paymentRepo.save(entity);
		}

		return save;
	}

}
