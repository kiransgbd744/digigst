/**
 * 
 */
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BRule86BRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffSnapDetailsEntityRepository;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffDto;
import com.ey.advisory.app.gstr3b.Gstr3BRule86BEntity;
import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffStatusEntity;
import com.ey.advisory.app.gstr3b.LedgerDetailsDto;
import com.ey.advisory.app.gstr3b.PaidThroughItcDto;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3BSetOffSnapFetchsServiceImpl")
public class Gstr3BSetOffSnapFetchsServiceImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3BSetOffSnapDetailsEntityRepository")
	private Gstr3BSetOffSnapDetailsEntityRepository snapRepo;

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository liabilitySetOffStatus;

	@Autowired
	@Qualifier("Gstr3BRule86BRepository")
	private Gstr3BRule86BRepository rule86BRepo;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository gstnUserRequestRepo;

	public Gstr3BLiabilitySetOffDto fetchFromSnap(String gstin,
			String taxPeriod) {

		LOGGER.debug("Inside Gstr3BSetOffSnapFetchsServiceImpl"
				+ ".getComputeStatus() method");

		String liabilityStatus = null;
		LocalDateTime statusUptatedOn = null;
		String updatedDateTime = null;

		LedgerDetailsDto dto1 = new LedgerDetailsDto();
		LedgerDetailsDto dto2 = new LedgerDetailsDto();
		LedgerDetailsDto dto3 = new LedgerDetailsDto();

		PaidThroughItcDto dto4 = new PaidThroughItcDto();
		PaidThroughItcDto dto5 = new PaidThroughItcDto();
		PaidThroughItcDto dto6 = new PaidThroughItcDto();
		PaidThroughItcDto dto7 = new PaidThroughItcDto();

		PaidThroughItcDto dto2A = new PaidThroughItcDto();
		PaidThroughItcDto dto2B = new PaidThroughItcDto();

		LedgerDetailsDto dto11 = new LedgerDetailsDto();
		LedgerDetailsDto dto12 = new LedgerDetailsDto();

		// Negative liability
		LedgerDetailsDto dto13 = new LedgerDetailsDto();
		LedgerDetailsDto dto14 = new LedgerDetailsDto();
		LedgerDetailsDto dto15 = new LedgerDetailsDto();
		LedgerDetailsDto dto16 = new LedgerDetailsDto();

		boolean rule86b = false;

		dto4.setDesc("Integrated Tax");
		dto5.setDesc("Central Tax");
		dto6.setDesc("State/UT Tax");
		dto7.setDesc("Cess");

		dto11.setDesc("currMonthUtil");
		dto12.setDesc("clsBal");

		List<LedgerDetailsDto> ledgerDetails = new ArrayList<>();

		List<PaidThroughItcDto> gstr3bDetails = new ArrayList<>();

		try {

			List<Gstr3BRule86BEntity> rule86EntityList = rule86BRepo
					.findByGstinAndTaxPeriodAndIsActiveTrue(gstin, taxPeriod);

			if (rule86EntityList != null && !rule86EntityList.isEmpty()) {
				rule86b = rule86EntityList.get(0).isRule86B();
			}

			Gstr3BSaveLiabilitySetOffStatusEntity liabilitySetoffStatus = liabilitySetOffStatus
					.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin,
							taxPeriod);

			if (liabilitySetoffStatus != null) {
				liabilityStatus = liabilitySetoffStatus.getStatus();
				statusUptatedOn = liabilitySetoffStatus.getUpdatedOn() != null
						? EYDateUtil.toISTDateTimeFromUTC(
								liabilitySetoffStatus.getUpdatedOn())
						: null;
				if (statusUptatedOn != null) {
					String dateTime = statusUptatedOn.toString();
					String date = dateTime.substring(0, 10);
					String time = dateTime.substring(11, 19);
					updatedDateTime = (date + " " + time);
				}
			} else {
				liabilityStatus = "Not Initiated";
			}

			List<Gstr3BSetOffSnapDetailsEntity> entityList = snapRepo
					.findByGstinAndTaxPeriodAndIsDelete(gstin, taxPeriod,
							false);

			for (Gstr3BSetOffSnapDetailsEntity entity : entityList) {

				if (entity.getSection().equalsIgnoreCase("CLB_TAX")) {

					dto1.setDesc("tx");
					dto1.setI(entity.getIgst());
					dto1.setC(entity.getCgst());
					dto1.setS(entity.getSgst());
					dto1.setCs(entity.getCess());
					dto1.setTotal(entity.getTotalTax());
				} else if (entity.getSection()
						.equalsIgnoreCase("CRLB_TAX_C_MONTH")) {

					dto1.setCrc(entity.getCgst());
					dto1.setCrcs(entity.getCess());
					dto1.setCri(entity.getIgst());
					dto1.setCrs(entity.getSgst());
					dto1.setCrTotal(entity.getTotalTax());

				} else if (entity.getSection()
						.equalsIgnoreCase("NEGATIVE_TAX_LIAB")) {

					dto1.setNlbCgst(entity.getCgst());
					dto1.setNlbCess(entity.getCess());
					dto1.setNlbIgst(entity.getIgst());
					dto1.setNlbSgst(entity.getSgst());
					// dto1.setCrTotal(entity.getTotalTax());

				}

				else if (entity.getSection().equalsIgnoreCase("CLB_INTEREST")) {

					dto2.setDesc("intr");
					dto2.setI(entity.getIgst());
					dto2.setC(entity.getCgst());
					dto2.setS(entity.getSgst());
					dto2.setCs(entity.getCess());
					dto2.setTotal(entity.getTotalTax());

				} else if (entity.getSection()
						.equalsIgnoreCase("CLB_LATEFEE")) {

					dto3.setDesc("fee");
					dto3.setI(entity.getIgst());
					dto3.setC(entity.getCgst());
					dto3.setS(entity.getSgst());
					dto3.setCs(entity.getCess());
					dto3.setTotal(entity.getTotalTax());

				} else if (entity.getSection().equalsIgnoreCase("col2")) {

					dto4.setOtrci(entity.getIgst());
					dto5.setOtrci(entity.getCgst());
					dto6.setOtrci(entity.getSgst());
					dto7.setOtrci(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col3")) {

					dto4.setPdi(entity.getIgst());
					dto5.setPdi(entity.getCgst());
					dto6.setPdi(entity.getSgst());

				} else if (entity.getSection().equalsIgnoreCase("col4")) {

					dto4.setPdc(entity.getIgst());
					dto5.setPdc(entity.getCgst());

				} else if (entity.getSection().equalsIgnoreCase("col5")) {

					dto4.setPds(entity.getIgst());
					dto6.setPds(entity.getSgst());

				} else if (entity.getSection().equalsIgnoreCase("col6")) {

					dto7.setPdcs(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col7")) {

					dto4.setOtrc7(entity.getIgst());
					dto5.setOtrc7(entity.getCgst());
					dto6.setOtrc7(entity.getSgst());
					dto7.setOtrc7(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col8")) {

					dto4.setRci8(entity.getIgst());
					dto5.setRci8(entity.getCgst());
					dto6.setRci8(entity.getSgst());
					dto7.setRci8(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col10")) {

					dto4.setInti10(entity.getIgst());
					dto5.setInti10(entity.getCgst());
					dto6.setInti10(entity.getSgst());
					dto7.setInti10(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col12")) {

					dto4.setLateFee12(entity.getIgst());
					dto5.setLateFee12(entity.getCgst());
					dto6.setLateFee12(entity.getSgst());
					dto7.setLateFee12(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col14")) {

					dto4.setUcb14(entity.getIgst());
					dto5.setUcb14(entity.getCgst());
					dto6.setUcb14(entity.getSgst());
					dto7.setUcb14(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col15")) {

					dto4.setAcr15(entity.getIgst());
					dto5.setAcr15(entity.getCgst());
					dto6.setAcr15(entity.getSgst());
					dto7.setAcr15(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col2A")) {

					dto4.setOtrci2A(entity.getIgst());
					dto5.setOtrci2A(entity.getCgst());
					dto6.setOtrci2A(entity.getSgst());
					dto7.setOtrci2A(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("col2B")) {

					dto4.setOtrci2B(entity.getIgst());
					dto5.setOtrci2B(entity.getCgst());
					dto6.setOtrci2B(entity.getSgst());
					dto7.setOtrci2B(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("obj2i")) {

					dto4.setAdjNegative2i(entity.getIgst());
					dto5.setAdjNegative2i(entity.getCgst());
					dto6.setAdjNegative2i(entity.getSgst());
					dto7.setAdjNegative2i(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("obj2ii")) {

					dto4.setNetOthRecTaxPayable2i(entity.getIgst());
					dto5.setAdjNegative8A(entity.getCgst());
					dto6.setAdjNegative8A(entity.getSgst());
					dto7.setAdjNegative8A(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("obj8a")) {

					dto4.setAdjNegative8A(entity.getIgst());
					dto5.setAdjNegative8A(entity.getCgst());
					dto6.setAdjNegative8A(entity.getSgst());
					dto7.setAdjNegative8A(entity.getCess());

				} else if (entity.getSection().equalsIgnoreCase("objRci9")) {

					dto4.setRci9(entity.getIgst());
					dto5.setRci9(entity.getCgst());
					dto6.setRci9(entity.getSgst());
					dto7.setRci9(entity.getCess());

				}

				else if (entity.getSection()
						.equalsIgnoreCase("currMonthUtil_Credit")) {

					dto11.setCrc(entity.getCgst());
					dto11.setCrcs(entity.getCess());
					dto11.setCri(entity.getIgst());
					dto11.setCrs(entity.getSgst());
					dto11.setCrTotal(entity.getTotalTax());

				} else if (entity.getSection()
						.equalsIgnoreCase("currMonthUtil_Cash")) {
					dto11.setI(entity.getIgst());
					dto11.setC(entity.getCgst());
					dto11.setS(entity.getSgst());
					dto11.setCs(entity.getCess());
					dto11.setTotal(entity.getTotalTax());
				}

				else if (entity.getSection()
						.equalsIgnoreCase("currMonthUtil_Negative")) {
					dto11.setNlbIgst(entity.getIgst());
					dto11.setNlbCgst(entity.getCgst());
					dto11.setNlbSgst(entity.getSgst());
					dto11.setNlbCess(entity.getCess());
					dto11.setNlbTotal(entity.getTotalTax());
				} else if (entity.getSection()
						.equalsIgnoreCase("clsBal_Credit")) {

					dto12.setCrc(entity.getCgst());
					dto12.setCrcs(entity.getCess());
					dto12.setCri(entity.getIgst());
					dto12.setCrs(entity.getSgst());
					dto12.setCrTotal(entity.getTotalTax());

				} else if (entity.getSection()
						.equalsIgnoreCase("clsBal_cash")) {
					dto12.setI(entity.getIgst());
					dto12.setC(entity.getCgst());
					dto12.setS(entity.getSgst());
					dto12.setCs(entity.getCess());
					dto12.setTotal(entity.getTotalTax());
				} else if (entity.getSection()
						.equalsIgnoreCase("clsBal_Negative")) {
					dto12.setNlbIgst(entity.getIgst());
					dto12.setNlbCgst(entity.getCgst());
					dto12.setNlbSgst(entity.getSgst());
					dto12.setNlbCess(entity.getCess());
					dto12.setNlbTotal(entity.getTotalTax());
				}

			}
			ledgerDetails.add(dto1);
			ledgerDetails.add(dto2);
			ledgerDetails.add(dto3);
			ledgerDetails.add(dto11);
			ledgerDetails.add(dto12);

			gstr3bDetails.add(dto4);
			gstr3bDetails.add(dto5);
			gstr3bDetails.add(dto6);
			gstr3bDetails.add(dto7);

		} catch (Exception ex) {
			LOGGER.error(
					"Error while saving into"
							+ " Gstr3BSetOffSnapFetchsServiceImpl table {} : ",
					ex);
		}

		return new Gstr3BLiabilitySetOffDto(liabilityStatus, updatedDateTime,
				ledgerDetails, gstr3bDetails, null, rule86b);

	}

}
