/**
 * 
 */
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeDetailsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3bSetOffComputeSaveToDBServiceImpl")
public class Gstr3bSetOffComputeSaveToDBServiceImpl
		implements Gstr3bSetOffComputeSaveToDBService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeDetailsRepository")
	private Gstr3BSetOffEntityComputeDetailsRepository computeDetailsRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeConfigRepository")
	private Gstr3BSetOffEntityComputeConfigRepository computeConfigRepo;

	@Autowired
	@Qualifier("Gstr3BGstnSaveToAspRepository")
	private Gstr3BGstnSaveToAspRepository gstnComputeRepo;

	@Override
	public String saveComputedtoDb(Gstr3BSetOffComputeSaveInnerDto innerDto1,
			List<Gstr3BSetOffComputeSaveClosingBalDto> closingBals) {

		LOGGER.debug("Inside Gstr3bSetOffEntityComputeSetOffServiceImpl"
				+ ".getComputeStatus() method");

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		try {

			String gstin = innerDto1.getGstin();
			String taxPeriod = innerDto1.getTaxPeriod();

			BigDecimal zeroVal = BigDecimal.ZERO;

			Long reqId = getNextSequencevalue(entityManager);
			computeConfigRepo.softDelete(taxPeriod, gstin);

			Gstr3BSetOffEntityComputeConfigEntity configEntity = new Gstr3BSetOffEntityComputeConfigEntity();

			configEntity.setComputeStatus("Initiated");
			configEntity.setCreatedBy(userName);
			configEntity.setCreatedOn(LocalDateTime.now());
			configEntity.setGstin(gstin);
			configEntity.setIsActive(true);
			configEntity.setRequestId(reqId);
			configEntity.setTaxPeriod(taxPeriod);
			configEntity.setUpdatedBy(userName);
			configEntity.setUpdatedOn(LocalDateTime.now());

			computeConfigRepo.save(configEntity);

			Gstr3BSetOffEntityComputeDetailsEntity computeEntityClosingBal1 = new Gstr3BSetOffEntityComputeDetailsEntity();

			Gstr3BSetOffEntityComputeDetailsEntity computeEntityClosingBal2 = new Gstr3BSetOffEntityComputeDetailsEntity();

			Gstr3BSetOffEntityComputeDetailsEntity computeEntityClosingBal3 = new Gstr3BSetOffEntityComputeDetailsEntity();

			Gstr3BSetOffEntityComputeDetailsEntity computeEntityClosingBal4 = new Gstr3BSetOffEntityComputeDetailsEntity();
			
			Gstr3BSetOffEntityComputeDetailsEntity computeEntityClosingBal5 = new Gstr3BSetOffEntityComputeDetailsEntity();
			
			Gstr3BSetOffEntityComputeDetailsEntity computeEntityClosingBal6 = new Gstr3BSetOffEntityComputeDetailsEntity();

			List<Gstr3BSetOffEntityComputeDetailsEntity> computeEntityList = new ArrayList<>();

			for (Gstr3BSetOffComputeSaveClosingBalDto closingBal : closingBals) {

				if (closingBal.getDesc().equalsIgnoreCase("currMonthUtil")) {

					computeEntityClosingBal1.setGstin(gstin);
					computeEntityClosingBal1.setTaxPeriod(taxPeriod);
					computeEntityClosingBal1.setSection("currMonthUtil_Cash");
					computeEntityClosingBal1.setRequestId(reqId);
					computeEntityClosingBal1.setIgst(closingBal.getI());
					computeEntityClosingBal1.setCgst(closingBal.getC());
					computeEntityClosingBal1.setSgst(closingBal.getS());
					computeEntityClosingBal1.setCess(closingBal.getCs());
					computeEntityClosingBal1.setIsDelete(false);
					computeEntityClosingBal1.setUpdatedBy(userName);
					computeEntityClosingBal1.setUpdatedOn(now);
					computeEntityClosingBal1.setCreatedBy(userName);
					computeEntityClosingBal1.setCreatedOn(now);
					// adding to list
					computeEntityList.add(computeEntityClosingBal1);

					computeEntityClosingBal2.setGstin(gstin);
					computeEntityClosingBal2.setTaxPeriod(taxPeriod);
					computeEntityClosingBal2.setSection("currMonthUtil_Credit");
					computeEntityClosingBal2.setRequestId(reqId);
					computeEntityClosingBal2.setIgst(closingBal.getCri());
					computeEntityClosingBal2.setCgst(closingBal.getCrc());
					computeEntityClosingBal2.setSgst(closingBal.getCrs());
					computeEntityClosingBal2.setCess(closingBal.getCrcs());
					computeEntityClosingBal2.setIsDelete(false);
					computeEntityClosingBal2.setUpdatedBy(userName);
					computeEntityClosingBal2.setUpdatedOn(now);
					computeEntityClosingBal2.setCreatedBy(userName);
					computeEntityClosingBal2.setCreatedOn(now);
					// adding to list
					computeEntityList.add(computeEntityClosingBal2);
					
					computeEntityClosingBal5.setGstin(gstin);
					computeEntityClosingBal5.setTaxPeriod(taxPeriod);
					computeEntityClosingBal5.setSection("currMonthUtil_NegativeLiab");
					computeEntityClosingBal5.setRequestId(reqId);
					computeEntityClosingBal5.setIgst(closingBal.getNegLiabIgst());
					computeEntityClosingBal5.setCgst(closingBal.getNegLiabCgst());
					computeEntityClosingBal5.setSgst(closingBal.getNegLiabSgst());
					computeEntityClosingBal5.setCess(closingBal.getNegLiabCess());
					computeEntityClosingBal5.setIsDelete(false);
					computeEntityClosingBal5.setUpdatedBy(userName);
					computeEntityClosingBal5.setUpdatedOn(now);
					computeEntityClosingBal5.setCreatedBy(userName);
					computeEntityClosingBal5.setCreatedOn(now);
					// adding to list
					computeEntityList.add(computeEntityClosingBal5);

				}

				if (closingBal.getDesc().equalsIgnoreCase("clsBal")) {

					computeEntityClosingBal3.setGstin(gstin);
					computeEntityClosingBal3.setTaxPeriod(taxPeriod);
					computeEntityClosingBal3.setSection("clsBal_Cash");
					computeEntityClosingBal3.setRequestId(reqId);
					computeEntityClosingBal3.setIgst(closingBal.getI());
					computeEntityClosingBal3.setCgst(closingBal.getC());
					computeEntityClosingBal3.setSgst(closingBal.getS());
					computeEntityClosingBal3.setCess(closingBal.getCs());
					computeEntityClosingBal3.setIsDelete(false);
					computeEntityClosingBal3.setUpdatedBy(userName);
					computeEntityClosingBal3.setUpdatedOn(now);
					computeEntityClosingBal3.setCreatedBy(userName);
					computeEntityClosingBal3.setCreatedOn(now);
					// adding to list
					computeEntityList.add(computeEntityClosingBal3);

					computeEntityClosingBal4.setGstin(gstin);
					computeEntityClosingBal4.setTaxPeriod(taxPeriod);
					computeEntityClosingBal4.setSection("clsBal_Credit");
					computeEntityClosingBal4.setRequestId(reqId);
					computeEntityClosingBal4.setIgst(closingBal.getCri());
					computeEntityClosingBal4.setCgst(closingBal.getCrc());
					computeEntityClosingBal4.setSgst(closingBal.getCrs());
					computeEntityClosingBal4.setCess(closingBal.getCrcs());
					computeEntityClosingBal4.setIsDelete(false);
					computeEntityClosingBal4.setUpdatedBy(userName);
					computeEntityClosingBal4.setUpdatedOn(now);
					computeEntityClosingBal4.setCreatedBy(userName);
					computeEntityClosingBal4.setCreatedOn(now);
					// adding to list
					computeEntityList.add(computeEntityClosingBal4);
					
					computeEntityClosingBal6.setGstin(gstin);
					computeEntityClosingBal6.setTaxPeriod(taxPeriod);
					computeEntityClosingBal6.setSection("clsBal_NegativeLiab");
					computeEntityClosingBal6.setRequestId(reqId);
					computeEntityClosingBal6.setIgst(closingBal.getNegLiabIgst());
					computeEntityClosingBal6.setCgst(closingBal.getNegLiabCgst());
					computeEntityClosingBal6.setSgst(closingBal.getNegLiabSgst());
					computeEntityClosingBal6.setCess(closingBal.getNegLiabCess());
					computeEntityClosingBal6.setIsDelete(false);
					computeEntityClosingBal6.setUpdatedBy(userName);
					computeEntityClosingBal6.setUpdatedOn(now);
					computeEntityClosingBal6.setCreatedBy(userName);
					computeEntityClosingBal6.setCreatedOn(now);
					// adding to list
					computeEntityList.add(computeEntityClosingBal6);

				}

			}

			BigDecimal ucI = innerDto1.getUcbIgst();
			BigDecimal ucC = innerDto1.getUcbCgst();
			BigDecimal ucS = innerDto1.getUcbSgst();
			BigDecimal ucCs = innerDto1.getUcbCess();

			// converting to compute entity

			BigDecimal cess = (innerDto1.getAcrCess().add(ucCs)).signum() != -1
					? innerDto1.getAcrCess().add(ucCs)
					: zeroVal;
			BigDecimal cgst = (innerDto1.getAcrCgst().add(ucC)).signum() != -1
					? innerDto1.getAcrCgst().add(ucC)
					: zeroVal;
			BigDecimal igst = (innerDto1.getAcrIgst().add(ucI)).signum() != -1
					? innerDto1.getAcrIgst().add(ucI)
					: zeroVal;
			BigDecimal sgst = (innerDto1.getAcrSgst().add(ucS)).signum() != -1
					? innerDto1.getAcrSgst().add(ucS)
					: zeroVal;

			BigDecimal cessA = (innerDto1.getAcrCess()).signum() != -1
					? innerDto1.getAcrCess()
					: zeroVal;
			BigDecimal cgstA = (innerDto1.getAcrCgst()).signum() != -1
					? innerDto1.getAcrCgst()
					: zeroVal;
			BigDecimal igstA = (innerDto1.getAcrIgst()).signum() != -1
					? innerDto1.getAcrIgst()
					: zeroVal;
			BigDecimal sgstA = (innerDto1.getAcrSgst()).signum() != -1
					? innerDto1.getAcrSgst()
					: zeroVal;

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity1 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity1.setGstin(gstin);
			computeEntity1.setTaxPeriod(taxPeriod);
			computeEntity1.setSection("ADDNL_CASH_REQ");
			computeEntity1.setRequestId(reqId);
			computeEntity1.setIgst(igstA);
			computeEntity1.setCgst(cgstA);
			computeEntity1.setSgst(sgstA);
			computeEntity1.setCess(cessA);
			computeEntity1.setIsDelete(false);
			computeEntity1.setUpdatedBy(userName);
			computeEntity1.setUpdatedOn(now);
			computeEntity1.setCreatedBy(userName);
			computeEntity1.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity1);

			// Utilzation
			Gstr3BSetOffEntityComputeDetailsEntity computeEntity2 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity2.setGstin(gstin);
			computeEntity2.setTaxPeriod(taxPeriod);
			computeEntity2.setSection("NET_GST_LIABILITY");
			computeEntity2.setRequestId(reqId);
			computeEntity2.setIgst(igst);
			computeEntity2.setCgst(cgst);
			computeEntity2.setSgst(sgst);
			computeEntity2.setCess(cess);
			computeEntity2.setIsDelete(false);
			computeEntity2.setUpdatedBy(userName);
			computeEntity2.setUpdatedOn(now);
			computeEntity2.setCreatedBy(userName);
			computeEntity2.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity2);

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity3 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity3.setGstin(gstin);
			computeEntity3.setTaxPeriod(taxPeriod);
			computeEntity3.setSection("INTEREST_LATE_FEE");
			computeEntity3.setRequestId(reqId);
			computeEntity3.setIgst(
					innerDto1.getIpIgst().add(innerDto1.getLateFeeIgst()));
			computeEntity3.setCgst(
					innerDto1.getIpCgst().add(innerDto1.getLateFeeCgst()));
			computeEntity3.setSgst(
					innerDto1.getIpSgst().add(innerDto1.getLateFeeSgst()));
			computeEntity3.setCess(
					innerDto1.getIpCess().add(innerDto1.getLateFeeCess()));
			computeEntity3.setIsDelete(false);
			computeEntity3.setUpdatedBy(userName);
			computeEntity3.setUpdatedOn(now);
			computeEntity3.setCreatedBy(userName);
			computeEntity3.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity3);

			// 3.1d outer col6 inner 8/9
			Gstr3BSetOffEntityComputeDetailsEntity computeEntity4 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity4.setGstin(gstin);
			computeEntity4.setTaxPeriod(taxPeriod);
			computeEntity4.setSection("LIABILITY");
			computeEntity4.setRequestId(reqId);
			computeEntity4.setIgst(innerDto1.getRcIgst());
			computeEntity4.setCgst(innerDto1.getRcCgst());
			computeEntity4.setSgst(innerDto1.getRcSgst());
			computeEntity4.setCess(innerDto1.getRcCess());
			computeEntity4.setIsDelete(false);
			computeEntity4.setUpdatedBy(userName);
			computeEntity4.setUpdatedOn(now);
			computeEntity4.setCreatedBy(userName);
			computeEntity4.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity4);

			// col7 outer col5
			Gstr3BSetOffEntityComputeDetailsEntity computeEntity5 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity5.setGstin(gstin);
			computeEntity5.setTaxPeriod(taxPeriod);
			computeEntity5.setSection("NET_LIABILITY");
			computeEntity5.setRequestId(reqId);
			if (innerDto1.getOtrIgst().compareTo(BigDecimal.ZERO) < 0) {
				computeEntity5.setIgst(BigDecimal.ZERO);
			} else if (innerDto1.getOtrIgst().compareTo(BigDecimal.ONE) < 1) {
				computeEntity5.setIgst(BigDecimal.ZERO);
			} else {
				computeEntity5.setIgst(innerDto1.getOtrIgst());
			}
			if (innerDto1.getOtrCgst().compareTo(BigDecimal.ZERO) < 0) {
				computeEntity5.setCgst(BigDecimal.ZERO);
			} else if (innerDto1.getOtrCgst().compareTo(BigDecimal.ONE) < 1) {
				computeEntity5.setCgst(BigDecimal.ZERO);
			} else {
				computeEntity5.setCgst(innerDto1.getOtrCgst());
			}
			if (innerDto1.getOtrSgst().compareTo(BigDecimal.ZERO) < 0) {
				computeEntity5.setSgst(BigDecimal.ZERO);
			} else if (innerDto1.getOtrSgst().compareTo(BigDecimal.ONE) < 1) {
				computeEntity5.setSgst(BigDecimal.ZERO);
			} else {
				computeEntity5.setSgst(innerDto1.getOtrSgst());
			}
			if (innerDto1.getOtrCess().compareTo(BigDecimal.ZERO) < 0) {
				computeEntity5.setCess(BigDecimal.ZERO);
			} else if (innerDto1.getOtrCess().compareTo(BigDecimal.ONE) < 1) {
				computeEntity5.setCess(BigDecimal.ZERO);
			} else {
				computeEntity5.setCess(innerDto1.getOtrCess());
			}
			/*
			 * computeEntity5.setIgst(innerDto1.getOtrIgst());
			 * computeEntity5.setCgst(innerDto1.getOtrCgst());
			 * computeEntity5.setSgst(innerDto1.getOtrSgst());
			 * computeEntity5.setCess(innerDto1.getOtrCess());
			 */
			computeEntity5.setIsDelete(false);
			computeEntity5.setUpdatedBy(userName);
			computeEntity5.setUpdatedOn(now);
			computeEntity5.setCreatedBy(userName);
			computeEntity5.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity5);

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity6 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity6.setGstin(gstin);
			computeEntity6.setTaxPeriod(taxPeriod);
			computeEntity6.setSection("LATE_FEE");
			computeEntity6.setRequestId(reqId);
			computeEntity6.setIgst(innerDto1.getLateFeeIgst());
			computeEntity6.setCgst(innerDto1.getLateFeeCgst());
			computeEntity6.setSgst(innerDto1.getLateFeeSgst());
			computeEntity6.setCess(innerDto1.getLateFeeCess());
			computeEntity6.setIsDelete(false);
			computeEntity6.setUpdatedBy(userName);
			computeEntity6.setUpdatedOn(now);
			computeEntity6.setCreatedBy(userName);
			computeEntity6.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity6);

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity7 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity7.setGstin(gstin);
			computeEntity7.setTaxPeriod(taxPeriod);
			computeEntity7.setSection("INTEREST");
			computeEntity7.setRequestId(reqId);
			computeEntity7.setIgst(innerDto1.getIpIgst());
			computeEntity7.setCgst(innerDto1.getIpCgst());
			computeEntity7.setSgst(innerDto1.getIpSgst());
			computeEntity7.setCess(innerDto1.getIpCess());
			computeEntity7.setIsDelete(false);
			computeEntity7.setUpdatedBy(userName);
			computeEntity7.setUpdatedOn(now);
			computeEntity7.setCreatedBy(userName);
			computeEntity7.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity7);

			// negative laibility

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity8 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity8.setGstin(gstin);
			computeEntity8.setTaxPeriod(taxPeriod);
			computeEntity8.setSection("Adj_negliab_2i");
			computeEntity8.setRequestId(reqId);
			computeEntity8.setIgst(innerDto1.getAdjnegliabIgst2i());
			computeEntity8.setCgst(innerDto1.getAdjnegliabCgst2i());
			computeEntity8.setSgst(innerDto1.getAdjnegliabSgst2i());
			computeEntity8.setCess(innerDto1.getAdjnegliabCess2i());
			computeEntity8.setIsDelete(false);
			computeEntity8.setUpdatedBy(userName);
			computeEntity8.setUpdatedOn(now);
			computeEntity8.setCreatedBy(userName);
			computeEntity8.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity8);

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity9 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity9.setGstin(gstin);
			computeEntity9.setTaxPeriod(taxPeriod);
			computeEntity9.setSection("Adj_negliab_8a");
			computeEntity9.setRequestId(reqId);
			computeEntity9.setIgst(innerDto1.getAdjnegliabIgst8a());
			computeEntity9.setCgst(innerDto1.getAdjnegliabCgst8a());
			computeEntity9.setSgst(innerDto1.getAdjnegliabSgst8a());
			computeEntity9.setCess(innerDto1.getAdjnegliabCess8a());
			computeEntity9.setIsDelete(false);
			computeEntity9.setUpdatedBy(userName);
			computeEntity9.setUpdatedOn(now);
			computeEntity9.setCreatedBy(userName);
			computeEntity9.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity9);

			Gstr3BSetOffEntityComputeDetailsEntity computeEntity10 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity10.setGstin(gstin);
			computeEntity10.setTaxPeriod(taxPeriod);
			computeEntity10.setSection("Net_OtherRc_2ii");
			computeEntity10.setRequestId(reqId);
			computeEntity10.setIgst(innerDto1.getNetOtherRc2iiIgst());
			computeEntity10.setCgst(innerDto1.getNetOtherRc2iiCgst());
			computeEntity10.setSgst(innerDto1.getNetOtherRc2iiSgst());
			computeEntity10.setCess(innerDto1.getNetOtherRc2iiCess());
			computeEntity10.setIsDelete(false);
			computeEntity10.setUpdatedBy(userName);
			computeEntity10.setUpdatedOn(now);
			computeEntity10.setCreatedBy(userName);
			computeEntity10.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity10);
			
			Gstr3BSetOffEntityComputeDetailsEntity computeEntity11 = new Gstr3BSetOffEntityComputeDetailsEntity();
			computeEntity11.setGstin(gstin);
			computeEntity11.setTaxPeriod(taxPeriod);
			computeEntity11.setSection("ReverseCharge-9");
			computeEntity11.setRequestId(reqId);
			computeEntity11.setIgst(innerDto1.getRci9Igst());
			computeEntity11.setCgst(innerDto1.getRci9Cgst());
			computeEntity11.setSgst(innerDto1.getRci9Sgst());
			computeEntity11.setCess(innerDto1.getRci9Cess());
			computeEntity11.setIsDelete(false);
			computeEntity11.setUpdatedBy(userName);
			computeEntity11.setUpdatedOn(now);
			computeEntity11.setCreatedBy(userName);
			computeEntity11.setCreatedOn(now);
			// adding to list
			computeEntityList.add(computeEntity11);

			
			// saving to compute detail table

			computeDetailsRepo.softDelete(taxPeriod, gstin);
			computeDetailsRepo.saveAll(computeEntityList);

			// updating config table
			computeConfigRepo.updateStatus(taxPeriod, gstin, now);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "success";

	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}
}
