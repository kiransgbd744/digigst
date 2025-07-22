package com.ey.advisory.einv.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.EinvEwbStatisticsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.ewb.data.entities.clientBusiness.EinvEwbStatisticsEntity;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BusinessStatisticsLogHelper {

	@Autowired
	EinvEwbStatisticsRepository eInvEwbStatisticsRepository;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	public void logBusinessApiResponse(String ackNum, LocalDateTime ackDate,
			String irn, String docType, String docNum, LocalDate docDate,
			String sellerGstin, String buyerGstin, String ewbNum,
			LocalDateTime ewbDate, LocalDateTime ewbValidUpto,
			LocalDateTime cancellationDate, Integer nicDistance,
			String apiIdentifier, boolean isDuplicate, String signedInvoice,
			String signedQRCode) {
		EinvEwbStatisticsEntity eInvEwbStatEntity = new EinvEwbStatisticsEntity();
		eInvEwbStatEntity.setAckNum(ackNum);
		eInvEwbStatEntity.setAckDate(ackDate);
		eInvEwbStatEntity.setIrn(irn);
		eInvEwbStatEntity.setDocType(docType);
		eInvEwbStatEntity.setDocNum(docNum);
		eInvEwbStatEntity.setDocDate(docDate);
		eInvEwbStatEntity.setSellerGstin(sellerGstin);
		eInvEwbStatEntity.setBuyerGstin(buyerGstin);
		eInvEwbStatEntity.setEwbNum(ewbNum);
		eInvEwbStatEntity.setEwbDate(ewbDate);
		eInvEwbStatEntity.setEwbValidUpto(ewbValidUpto);
		eInvEwbStatEntity.setCancellationDate(cancellationDate);
		eInvEwbStatEntity.setNicDistance(nicDistance);

		if (apiIdentifier.equals(APIIdentifiers.CANCEL_EINV) || apiIdentifier
				.equals(com.ey.advisory.ewb.app.api.APIIdentifiers.CANCEL_EWB)) {
			eInvEwbStatEntity.setCancelled(true);
		}
		eInvEwbStatEntity.setApiIdentifier(apiIdentifier);
		eInvEwbStatEntity.setCreatedOn(LocalDateTime.now());
		eInvEwbStatEntity.setCreatedOnIst(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

		eInvEwbStatEntity.setDuplicate(isDuplicate);
//		if (!Strings.isNullOrEmpty(signedInvoice)) {
//			Clob signedInvoiceClob = null;
//			try {
//				signedInvoiceClob = new SerialClob(signedInvoice.toCharArray());
//			} catch (Exception e) {
//				LOGGER.error("Exception while serialising the Signed Invoice",
//						e);
//			}
//
//			eInvEwbStatEntity.setSignedInvoice(signedInvoiceClob);
//		}
		if (!Strings.isNullOrEmpty(reqLogHelper.getSource())) {
			eInvEwbStatEntity.setIdentifer(reqLogHelper.getSource());
		}
//		eInvEwbStatEntity.setSignedQRCode(signedQRCode);

		try {
			eInvEwbStatisticsRepository.save(eInvEwbStatEntity);
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Logging in Statistics for Group %s",
					TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
		}
	}
}
