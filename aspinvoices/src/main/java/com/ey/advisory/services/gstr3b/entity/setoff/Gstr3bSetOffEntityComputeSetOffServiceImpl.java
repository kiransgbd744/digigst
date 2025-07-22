package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository;
import com.ey.advisory.app.gstr3b.Gstr3BSaveChangesLiabilitySetOffEntity;
import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3bSetOffEntityComputeSetOffServiceImpl")
public class Gstr3bSetOffEntityComputeSetOffServiceImpl
		implements Gstr3bSetOffEntityComputeSetOffService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffRepository")
	private Gstr3BSaveChangesLiabilitySetOffRepository pdItcRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeDetailsRepository")
	private Gstr3BSetOffEntityComputeDetailsRepository computeDetailsRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeConfigRepository")
	private Gstr3BSetOffEntityComputeConfigRepository computeConfigRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository")
	private Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository ledgerRepo;

	@Autowired
	@Qualifier("Gstr3BGstnSaveToAspRepository")
	private Gstr3BGstnSaveToAspRepository gstnComputeRepo;

	@Override
	public String getComputeStatus(List<String> gstinList, String taxPeriod) {
		LOGGER.debug("Inside Gstr3bSetOffEntityComputeSetOffServiceImpl"
				+ ".getComputeStatus() method");

		BigDecimal zeroVal = BigDecimal.ZERO;

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		LocalDateTime now = LocalDateTime.now();
		try {
			for (String gstin : gstinList) {

				Long reqId = getNextSequencevalue(entityManager);
				computeConfigRepo.softDelete(taxPeriod, gstin);

				Gstr3BSetOffEntityComputeConfigEntity configEntity = new Gstr3BSetOffEntityComputeConfigEntity();

				Gstr3BSaveChangesLiabilitySetOffEntity paidThrEntiy = new Gstr3BSaveChangesLiabilitySetOffEntity();

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

				// 5.1(a) + 5.1(b)
				List<Gstr3bGstnSaveToAspEntity> feeList = gstnComputeRepo
						.findInterestAndLateFee(taxPeriod, gstin);

				List<Gstr3BIntrFeeDto> intrAndLateFees = feeList.stream()
						.map(o -> convertInteAndFee(o))
						.collect(Collectors.toList());

				Gstr3BIntrFeeDto fee = feeList.stream().map(o -> convertRcn(o))
						.collect(Collectors.reducing(new Gstr3BIntrFeeDto(),
								(acc, ele) -> acc.merge(ele)));

				// 3.1(d) and 3.1(a) & (b) and 4(c)
				List<Gstr3bGstnSaveToAspEntity> liabilityAndRevChrg = gstnComputeRepo
						.findOtherLiabilityAndReverseChargeTax(taxPeriod,
								gstin);

				Gstr3BSetOffLiabilityAndRevChrgDto liabilityAndRevChrgDto = convert(
						liabilityAndRevChrg);

				Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity ledgerData = ledgerRepo
						.findByGstinAndTaxPeriod(taxPeriod, gstin);

				BigDecimal itcBalanceCess = ledgerData != null
						? (ledgerData.getItcBalanceCess() != null
								&& liabilityAndRevChrgDto
										.getNetItcAvailCess() != null
												? ledgerData.getItcBalanceCess()
														.add(liabilityAndRevChrgDto
																.getNetItcAvailCess())
												: zeroVal)
						: (liabilityAndRevChrgDto.getNetItcAvailCess() != null
								? liabilityAndRevChrgDto.getNetItcAvailCess()
								: zeroVal);

				BigDecimal itcBalanceCgst = ledgerData != null
						? (ledgerData.getItcBalanceCgst() != null
								&& liabilityAndRevChrgDto
										.getNetItcAvailCgst() != null
												? ledgerData.getItcBalanceCgst()
														.add(liabilityAndRevChrgDto
																.getNetItcAvailCgst())
												: zeroVal)
						: (liabilityAndRevChrgDto.getNetItcAvailCgst() != null
								? liabilityAndRevChrgDto.getNetItcAvailCgst()
								: zeroVal);

				BigDecimal itcBalanceIgst = ledgerData != null
						? (ledgerData.getItcBalanceIgst() != null
								&& liabilityAndRevChrgDto
										.getNetItcAvailIgst() != null
												? ledgerData.getItcBalanceIgst()
														.add(liabilityAndRevChrgDto
																.getNetItcAvailIgst())
												: zeroVal)
						: (liabilityAndRevChrgDto.getNetItcAvailIgst() != null
								? liabilityAndRevChrgDto.getNetItcAvailIgst()
								: zeroVal);
				BigDecimal itcBalanceSgst = ledgerData != null
						? (ledgerData.getItcBalanceSgst() != null
								&& liabilityAndRevChrgDto
										.getNetItcAvailSgst() != null
												? ledgerData.getItcBalanceSgst()
														.add(liabilityAndRevChrgDto
																.getNetItcAvailSgst())
												: zeroVal)
						: (liabilityAndRevChrgDto.getNetItcAvailSgst() != null
								? liabilityAndRevChrgDto.getNetItcAvailSgst()
								: zeroVal);

				// getting tax latefee and intrest from ledger call
				BigDecimal ledgerTaxIgst = BigDecimal.ZERO;
				BigDecimal ledgerTaxCgst = BigDecimal.ZERO;
				BigDecimal ledgerTaxSgst = BigDecimal.ZERO;
				BigDecimal ledgerTaxCess = BigDecimal.ZERO;

				BigDecimal ledgerLateFeeIgst = BigDecimal.ZERO;
				BigDecimal ledgerLateFeeCgst = BigDecimal.ZERO;
				BigDecimal ledgerLateFeeSgst = BigDecimal.ZERO;
				BigDecimal ledgerLateFeeCess = BigDecimal.ZERO;

				BigDecimal ledgerIntrestIgst = BigDecimal.ZERO;
				BigDecimal ledgerIntrestCgst = BigDecimal.ZERO;
				BigDecimal ledgerIntrestSgst = BigDecimal.ZERO;
				BigDecimal ledgerIntrestCess = BigDecimal.ZERO;

				if (ledgerData != null) {

					ledgerTaxIgst = ledgerData.getTaxIgst();
					ledgerTaxCgst = ledgerData.getTaxCgst();
					ledgerTaxSgst = ledgerData.getTaxSgst();
					ledgerTaxCess = ledgerData.getTaxCess();

					ledgerLateFeeIgst = ledgerData.getFeeIgst();
					ledgerLateFeeCgst = ledgerData.getFeeCgst();
					ledgerLateFeeSgst = ledgerData.getFeeSgst();
					ledgerLateFeeCess = ledgerData.getFeeCess();

					ledgerIntrestIgst = ledgerData.getInterestIgst();
					ledgerIntrestCgst = ledgerData.getInterestCgst();
					ledgerIntrestSgst = ledgerData.getInterestSgst();
					ledgerIntrestCess = ledgerData.getInterestCess();

				}

				// intrAndLateFee 5.1(a) & 5.1(b)
				BigDecimal interIgst = BigDecimal.ZERO;
				BigDecimal interCgst = BigDecimal.ZERO;
				BigDecimal interSgst = BigDecimal.ZERO;
				BigDecimal interCess = BigDecimal.ZERO;

				BigDecimal LateFeeIgst = BigDecimal.ZERO;
				BigDecimal LateFeeCgst = BigDecimal.ZERO;
				BigDecimal LateFeeSgst = BigDecimal.ZERO;
				BigDecimal LateFeeCess = BigDecimal.ZERO;

				if (intrAndLateFees != null) {
					for (Gstr3BIntrFeeDto intrAndLateFee : intrAndLateFees) {
						if (intrAndLateFee.getSectionName()
								.equalsIgnoreCase("INTEREST")) {
							interCess = intrAndLateFee.getCess();
							interCgst = intrAndLateFee.getCgst();
							interIgst = intrAndLateFee.getIgst();
							interSgst = intrAndLateFee.getSgst();
						} else {
							LateFeeCess = intrAndLateFee.getCess();
							LateFeeCgst = intrAndLateFee.getCgst();
							LateFeeIgst = intrAndLateFee.getIgst();
							LateFeeSgst = intrAndLateFee.getSgst();
						}

					}

				}

				// Liability other thn rev chrg - 3 (3.1(a))

				BigDecimal otsCess = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getOtsCess() != null
								? liabilityAndRevChrgDto.getOtsCess()
								: zeroVal
						: zeroVal;
				BigDecimal otsIgst = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getOtsIgst() != null
								? liabilityAndRevChrgDto.getOtsIgst()
								: zeroVal
						: zeroVal;
				BigDecimal otsCgst = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getOtsCgst() != null
								? liabilityAndRevChrgDto.getOtsCgst()
								: zeroVal
						: zeroVal;
				BigDecimal otsSgst = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getOtsSgst() != null
								? liabilityAndRevChrgDto.getOtsSgst()
								: zeroVal
						: zeroVal;

				// a.compareTo(b) == 1 ---> a>b
				// computation logic

				if (otsCess.compareTo(itcBalanceCess) == 1) {
					paidThrEntiy.setCsPdCess(
							itcBalanceCess);
				} else {
					paidThrEntiy.setCsPdCess(
							otsCess);
				}

				BigDecimal igstDiff = null;
				BigDecimal igstDiffDiv = null;

				BigDecimal cgstDiff = null;
				BigDecimal cgstDiffDiv = null;

				BigDecimal sgstDiff = null;
				BigDecimal sgstDiffDiv = null;

				if (otsIgst.compareTo(itcBalanceIgst) == 1) {

					paidThrEntiy.setIPDIgst(itcBalanceIgst);

					igstDiff = otsIgst.subtract(itcBalanceIgst);
					igstDiffDiv = igstDiff.divide(new BigDecimal(2));

					paidThrEntiy.setIPDSgst(zeroVal);
					paidThrEntiy.setIPDCgst(zeroVal);

				} else {
					paidThrEntiy.setIPDIgst(otsIgst);

					igstDiff = itcBalanceIgst.subtract(otsIgst);
					igstDiffDiv = igstDiff.divide(new BigDecimal(2));

					paidThrEntiy.setCPDIgst(igstDiffDiv);
					paidThrEntiy.setSPDIgst(igstDiffDiv);

					paidThrEntiy.setCPDCgst(otsCgst.subtract(igstDiffDiv));

					paidThrEntiy.setSPDSgst(otsSgst.subtract(igstDiffDiv));
				}

				// Cgst
				if (otsCgst.compareTo(itcBalanceCgst) == 1) {

					if (paidThrEntiy.getCPDCgst() == null)
						paidThrEntiy.setCPDCgst(itcBalanceCgst);

					cgstDiff = otsCgst.subtract(itcBalanceCgst);
					cgstDiffDiv = cgstDiff.divide(new BigDecimal(2));

					paidThrEntiy.setCPDIgst(zeroVal);

				} else {

					if (paidThrEntiy.getCPDCgst() == null)
						paidThrEntiy.setCPDCgst(otsCgst);

					cgstDiff = itcBalanceCgst.subtract(otsCgst);
					cgstDiffDiv = cgstDiff.divide(new BigDecimal(2));

					if (otsIgst.compareTo(itcBalanceIgst) == 1) {

						if (cgstDiff.compareTo(igstDiff) == 1
								|| cgstDiff.compareTo(igstDiff) == 0) {

							paidThrEntiy.setCPDIgst(igstDiffDiv);
						} else {

							paidThrEntiy.setCPDIgst(cgstDiff);
						}
					}

					paidThrEntiy.setCPDIgst(zeroVal);

				}

				// Sgst
				if (otsSgst.compareTo(itcBalanceSgst) == 1) {

					if (paidThrEntiy.getSPDSgst() == null)
						paidThrEntiy.setSPDSgst(itcBalanceSgst);

					sgstDiff = otsSgst.subtract(itcBalanceSgst);
					sgstDiffDiv = sgstDiff.divide(new BigDecimal(2));
					paidThrEntiy.setSPDIgst(zeroVal);

				} else {

					if (paidThrEntiy.getSPDSgst() == null)
						paidThrEntiy.setSPDSgst(otsSgst);

					sgstDiff = itcBalanceSgst.subtract(otsSgst);
					sgstDiffDiv = sgstDiff.divide(new BigDecimal(2));

					if (otsIgst.compareTo(itcBalanceIgst) == 1) {

						if (sgstDiff.compareTo(igstDiff) == 1
								|| sgstDiff.compareTo(igstDiff) == 0) {

							paidThrEntiy.setSPDIgst(igstDiffDiv);
						} else {

							paidThrEntiy.setSPDIgst(sgstDiff);
						}
					}
					paidThrEntiy.setSPDIgst(zeroVal);
				}
				
				/*if (otsIgst.compareTo(itcBalanceIgst) == 1) {
					
				paidThrEntiy.setIPDIgst(itcBalanceIgst);
					//new logic 
				igstDiff = otsIgst.subtract(itcBalanceIgst);
				igstDiffDiv = igstDiff.divide(new BigDecimal(2));
					
					paidThrEntiy.setIPDSgst(zeroVal);
					paidThrEntiy.setIPDCgst(zeroVal);
					if (otsCgst.compareTo(itcBalanceCgst) == 1) {
						paidThrEntiy.setCPDCgst(itcBalanceCgst);
						paidThrEntiy.setCPDIgst(zeroVal);
					} else {
						paidThrEntiy.setCPDCgst(
								otsCgst);
						paidThrEntiy.setCPDIgst(zeroVal);

						BigDecimal j = itcBalanceCgst.subtract(otsCgst);
						if (j != BigDecimal.ZERO && itcBalanceIgst != otsIgst) {
							if (itcBalanceIgst.subtract(otsIgst).compareTo(
									itcBalanceCgst.subtract(otsCgst)) == 1) {
								// need to check
								paidThrEntiy.setIPDCgst(otsIgst
										.subtract(itcBalanceIgst));
							} else {
								paidThrEntiy.setIPDCgst(
										j);
							}
						}
					}
					if (otsSgst.compareTo(itcBalanceSgst) == 1) {
						paidThrEntiy.setSPDSgst(itcBalanceSgst);
						paidThrEntiy.setSPDIgst(zeroVal);
					} else {
						paidThrEntiy.setSPDSgst(
								otsSgst);
						paidThrEntiy.setSPDIgst(zeroVal);
						BigDecimal k = itcBalanceSgst.subtract(otsSgst);
						if (paidThrEntiy.getIPDIgst()
								.add(paidThrEntiy.getIPDCgst()) != otsIgst) {
							if (k != BigDecimal.ZERO && paidThrEntiy
									.getIPDIgst().add(paidThrEntiy
											.getIPDSgst()) != otsIgst) {
								if ((paidThrEntiy.getIPDIgst()
										.add(paidThrEntiy.getIPDCgst())
										.subtract(otsIgst)
										.compareTo(itcBalanceSgst
												.subtract(paidThrEntiy
														.getSPDSgst())) == 1)) {
									paidThrEntiy.setIPDSgst((otsIgst.subtract(
											paidThrEntiy.getIPDIgst())).add(
													paidThrEntiy.getIPDCgst())
													);
								} else {
									paidThrEntiy.setIPDSgst(k);
								}
							}
						}
					}

				} else {
					if (otsIgst == BigDecimal.ZERO) {
						paidThrEntiy.setIPDIgst(
								otsIgst);
						paidThrEntiy.setIPDCgst(
								otsIgst);
						paidThrEntiy.setIPDSgst(
								otsIgst);
					}
					paidThrEntiy.setIPDIgst(
							otsIgst);
					if (paidThrEntiy.getIPDIgst() == otsIgst) {
						paidThrEntiy.setIPDCgst(zeroVal);
						paidThrEntiy.setIPDSgst(zeroVal);
					}
					itcBalanceIgst = itcBalanceIgst.subtract(otsIgst);
					if (itcBalanceIgst.compareTo(otsCgst) == 1
							|| itcBalanceIgst == otsCgst) {
						paidThrEntiy.setCPDIgst(
								otsCgst);
						paidThrEntiy.setCPDCgst(
								zeroVal);
						itcBalanceIgst = itcBalanceIgst.subtract(otsCgst);
						if (itcBalanceIgst.compareTo(otsSgst) == 1
								|| itcBalanceIgst == otsSgst) {
							paidThrEntiy.setSPDIgst(
									otsSgst);
							paidThrEntiy.setSPDSgst(
									zeroVal);
						} else {
							paidThrEntiy.setSPDIgst(itcBalanceIgst);
							if (itcBalanceSgst.compareTo(otsSgst) == 1
									|| itcBalanceSgst == otsSgst) {

								paidThrEntiy.setSPDSgst(otsSgst
										.subtract(paidThrEntiy.getSPDIgst())
										);
								// creditStateTax = creditStateTax -
								// (data.gstr3bDetails[2].pdi +
								// data.gstr3bDetails[2].pds);
								itcBalanceSgst = itcBalanceSgst.subtract(
										(paidThrEntiy.getSPDIgst().add(
												paidThrEntiy.getSPDSgst())));
							} else {
								paidThrEntiy.setSPDSgst(itcBalanceSgst
										);
							}
						}
					} else {
						paidThrEntiy.setCPDIgst(itcBalanceIgst);
						itcBalanceIgst = itcBalanceIgst
								.subtract(paidThrEntiy.getCPDIgst());
						paidThrEntiy.setSPDIgst(itcBalanceIgst);

						if (itcBalanceCgst
								.compareTo(otsCgst.subtract(
										paidThrEntiy.getCPDIgst())) == 1
								|| itcBalanceCgst == otsCgst
										.subtract(paidThrEntiy.getCPDIgst())) {
							paidThrEntiy.setCPDCgst(
									otsCgst.subtract(paidThrEntiy.getCPDIgst())
											);
						} else if (itcBalanceCgst
								.compareTo(otsCgst.subtract(
										paidThrEntiy.getCPDIgst())) == 1
								|| itcBalanceCgst == otsCgst
										.subtract(paidThrEntiy.getCPDIgst())) {
							paidThrEntiy.setCPDCgst(itcBalanceCgst);
						} else {
							
							 * paidThrEntiy.setCPDCgst(itcBalanceCgst
							 * .subtract(paidThrEntiy.getCPDIgst()));
							 

							paidThrEntiy.setCPDCgst(itcBalanceCgst);
						}

						if (itcBalanceIgst == BigDecimal.ZERO
								&& (itcBalanceSgst.compareTo(otsSgst) == 1
										|| itcBalanceSgst == otsSgst)) {
							paidThrEntiy.setSPDIgst(zeroVal);
							paidThrEntiy.setSPDSgst(
									otsSgst);
						} else {
							paidThrEntiy.setSPDIgst(zeroVal);
							paidThrEntiy.setSPDSgst(itcBalanceSgst);
						}

					}
				}
*/
				paidThrEntiy.setGstin(gstin);
				paidThrEntiy.setTaxPeriod(taxPeriod);
				paidThrEntiy.setCreatedOn(now);
				paidThrEntiy.setUpdatedOn(now);
				paidThrEntiy.setIsActive(true);
				pdItcRepo.updateActiveFlag(taxPeriod, gstin);

				// saving pditc data
				pdItcRepo.save(paidThrEntiy);

				// summing of pditc data for Net liability calculation

				BigDecimal cPdCgst = paidThrEntiy != null
						? paidThrEntiy.getCPDCgst() != null
								? paidThrEntiy.getCPDCgst() : zeroVal
						: zeroVal;
				BigDecimal cPdIgst = paidThrEntiy != null
						? paidThrEntiy.getCPDIgst() != null
								? paidThrEntiy.getCPDIgst() : zeroVal
						: zeroVal;
				BigDecimal cessPdCess = paidThrEntiy != null
						? paidThrEntiy.getCsPdCess() != null
								? paidThrEntiy.getCsPdCess() : zeroVal
						: zeroVal;
				BigDecimal iPdCgst = paidThrEntiy != null
						? paidThrEntiy.getIPDCgst() != null
								? paidThrEntiy.getIPDCgst() : zeroVal
						: zeroVal;
				BigDecimal iPdIgst = paidThrEntiy != null
						? paidThrEntiy.getIPDIgst() != null
								? paidThrEntiy.getIPDIgst() : zeroVal
						: zeroVal;
				BigDecimal iPdSgst = paidThrEntiy != null
						? paidThrEntiy.getIPDSgst() != null
								? paidThrEntiy.getIPDSgst() : zeroVal
						: zeroVal;
				BigDecimal sPdIgst = paidThrEntiy != null
						? paidThrEntiy.getSPDIgst() != null
								? paidThrEntiy.getSPDIgst() : zeroVal
						: zeroVal;
				BigDecimal sPdSgst = paidThrEntiy != null
						? paidThrEntiy.getSPDSgst() != null
								? paidThrEntiy.getSPDSgst() : zeroVal
						: zeroVal;

				// net liability - 5 ---> inner col(7)

				BigDecimal netLiaIgst = otsIgst
						.subtract(iPdIgst.add(iPdCgst.add(iPdSgst)));
				BigDecimal netLiaCgst = otsCgst.subtract(
						cPdCgst.add(cPdIgst));
				BigDecimal netLiaSgst = otsSgst.subtract(
						sPdSgst.add(sPdIgst));
				BigDecimal netLiaCess = otsCess
						.subtract(cessPdCess);

				// Liability rev chrg - 6 - (3.1(d)) --> inner(9)

				BigDecimal libRevChrgIgst = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getLibRevChrgIgst() != null
								? liabilityAndRevChrgDto.getLibRevChrgIgst()
								: zeroVal
						: zeroVal;
				BigDecimal libRevChrgCgst = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getLibRevChrgCgst() != null
								? liabilityAndRevChrgDto.getLibRevChrgCgst()
								: zeroVal
						: zeroVal;
				BigDecimal libRevChrgSgst = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getLibRevChrgSgst() != null
								? liabilityAndRevChrgDto.getLibRevChrgSgst()
								: zeroVal
						: zeroVal;
				BigDecimal libRevChrgCess = liabilityAndRevChrgDto != null
						? liabilityAndRevChrgDto.getLibRevChrgCess() != null
								? liabilityAndRevChrgDto.getLibRevChrgCess()
								: zeroVal
						: zeroVal;

				// Interest + late Fee 5.1(a) + 5.1(b)

				BigDecimal intrFeeIgst = fee.getIgst();
				BigDecimal intrFeeCgst = fee.getCgst();
				BigDecimal intrFeeSgst = fee.getSgst();
				BigDecimal intrFeeCess = fee.getCess();

				/*
				 * IGST Utilizable Cash balance (col 14) -> min [( IGST col 7+
				 * IGST col 9) , cash ledger total(IGST tax)] + min( col 11 ,
				 * cash ledger total(IGST interest)) + min( col 13 , cash ledger
				 * total(IGST latefee))
				 */

				// min [( IGST col 7+ IGST col 9) , cash ledger total(IGST tax)]
				BigDecimal minIgst9And7 = (netLiaIgst.add(libRevChrgIgst)
						)
								.min(ledgerTaxIgst);
				BigDecimal minCgst9And7 = (netLiaCgst.add(libRevChrgCgst)
						)
								.min(ledgerTaxCgst);
				BigDecimal minSgst9And7 = (netLiaSgst.add(libRevChrgSgst)
						)
								.min(ledgerTaxSgst);
				BigDecimal minCess9And7 = (netLiaCess
						.add(libRevChrgCess))
								.min(ledgerTaxCess);

				// min( col 11 , cash ledger total(IGST interest))
				BigDecimal minLedgerIntrAndIntrIgst = ledgerIntrestIgst
						.min(interIgst);
				BigDecimal minLedgerIntrAndIntrCgst = ledgerIntrestCgst
						.min(interCgst);
				BigDecimal minLedgerIntrAndIntrSgst = ledgerIntrestSgst
						.min(interSgst);
				BigDecimal minLedgerIntrAndIntrCess = ledgerIntrestCess
						.min(interCess);

				// min( col 13 , cash ledger total(IGST latefee))
				BigDecimal minLedgerLateFeeAndFeeIgst = ledgerLateFeeIgst
						.min(LateFeeIgst);
				BigDecimal minLedgerLateFeeAndFeeCgst = ledgerLateFeeCgst
						.min(LateFeeCgst);
				BigDecimal minLedgerLateFeeAndFeeSgst = ledgerLateFeeSgst
						.min(LateFeeSgst);
				BigDecimal minLedgerLateFeeAndFeeCess = ledgerLateFeeCess
						.min(LateFeeCess);

				// Net Gst(Liability) 5+6+7 = 8

				BigDecimal netGstIgst = minIgst9And7
						.add(minLedgerIntrAndIntrIgst
								.add(minLedgerLateFeeAndFeeIgst));
				BigDecimal netGstCgst = minCgst9And7
						.add(minLedgerIntrAndIntrCgst
								.add(minLedgerLateFeeAndFeeCgst));
				BigDecimal netGstSgst = minSgst9And7
						.add(minLedgerIntrAndIntrSgst
								.add(minLedgerLateFeeAndFeeSgst));
				BigDecimal netGstCess = minCess9And7
						.add(minLedgerIntrAndIntrCess
								.add(minLedgerLateFeeAndFeeCess));

				// additional cash required 9 = 8-1
				BigDecimal addcashIgst = BigDecimal.ZERO;
				BigDecimal addcashCgst = BigDecimal.ZERO;
				BigDecimal addcashSgst = BigDecimal.ZERO;
				BigDecimal addcashCess = BigDecimal.ZERO;

				/*
				 * Additional Cash required ( col 15) IGST = A + B + C A = (col
				 * 7+ col 9 - (min ( col 7+ col 9, cash ledger total(IGST tax)))
				 * B = col 11 - min( col 11 , cash ledger total(IGST interest))
				 * C = col 13 - min( col 13 , cash ledger total(IGST latefee))
				 */

				BigDecimal addcashIgstA = (netLiaIgst.add(libRevChrgIgst))
						.subtract(minIgst9And7);
				BigDecimal addcashIgstB = interIgst
						.subtract(minLedgerIntrAndIntrIgst);
				BigDecimal addcashIgstC = LateFeeIgst
						.subtract(minLedgerLateFeeAndFeeIgst);

				addcashIgst = addcashIgstA.add(addcashIgstB).add(addcashIgstC);

				BigDecimal addcashCgstA = (netLiaCgst.add(libRevChrgCgst))
						.subtract(minCgst9And7);
				BigDecimal addcashCgstB = interCgst
						.subtract(minLedgerIntrAndIntrCgst);
				BigDecimal addcashCgstC = LateFeeCgst
						.subtract(minLedgerLateFeeAndFeeCgst);

				addcashCgst = addcashCgstA.add(addcashCgstB).add(addcashCgstC);

				BigDecimal addcashSgstA = (netLiaSgst.add(libRevChrgSgst))
						.subtract(minSgst9And7);
				BigDecimal addcashSgstB = interSgst
						.subtract(minLedgerIntrAndIntrSgst);
				BigDecimal addcashSgstC = LateFeeSgst
						.subtract(minLedgerLateFeeAndFeeSgst);

				addcashSgst = addcashSgstA.add(addcashSgstB).add(addcashSgstC);

				BigDecimal addcashCessA = (netLiaCess.add(libRevChrgCess))
						.subtract(minCess9And7);
				BigDecimal addcashCessB = interCess
						.subtract(minLedgerIntrAndIntrCess);
				BigDecimal addcashCessC = LateFeeCess
						.subtract(minLedgerLateFeeAndFeeCess);

				addcashCess = addcashCessA.add(addcashCessB).add(addcashCessC);

				// if results in negative value consider value 0
				addcashIgst = addcashIgst.signum() != -1 ? addcashIgst
						: zeroVal;
				addcashCgst = addcashCgst.signum() != -1 ? addcashCgst
						: zeroVal;
				addcashSgst = addcashSgst.signum() != -1 ? addcashSgst
						: zeroVal;
				addcashCess = addcashCess.signum() != -1 ? addcashCess
						: zeroVal;

				// converting to compute entity
				List<Gstr3BSetOffEntityComputeDetailsEntity> computeEntityList = new ArrayList<>();

				Gstr3BSetOffEntityComputeDetailsEntity computeEntity1 = new Gstr3BSetOffEntityComputeDetailsEntity();
				computeEntity1.setGstin(gstin);
				computeEntity1.setTaxPeriod(taxPeriod);
				computeEntity1.setSection("ADDNL_CASH_REQ");
				computeEntity1.setRequestId(reqId);
				computeEntity1.setIgst(addcashIgst);
				computeEntity1.setCgst(addcashCgst);
				computeEntity1.setSgst(addcashSgst);
				computeEntity1.setCess(addcashCess);
				computeEntity1.setIsDelete(false);
				computeEntity1.setUpdatedBy(userName);
				computeEntity1.setUpdatedOn(now);
				computeEntity1.setCreatedBy(userName);
				computeEntity1.setCreatedOn(now);
				// adding to list
				computeEntityList.add(computeEntity1);

				Gstr3BSetOffEntityComputeDetailsEntity computeEntity2 = new Gstr3BSetOffEntityComputeDetailsEntity();
				computeEntity2.setGstin(gstin);
				computeEntity2.setTaxPeriod(taxPeriod);
				computeEntity2.setSection("NET_GST_LIABILITY");
				computeEntity2.setRequestId(reqId);
				computeEntity2.setIgst(netGstIgst.add(addcashIgst));
				computeEntity2.setCgst(netGstCgst.add(addcashCgst));
				computeEntity2.setSgst(netGstSgst.add(addcashSgst));
				computeEntity2.setCess(netGstCess.add(addcashCess));
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
				computeEntity3.setIgst(intrFeeIgst);
				computeEntity3.setCgst(intrFeeCgst);
				computeEntity3.setSgst(intrFeeSgst);
				computeEntity3.setCess(intrFeeCess);
				computeEntity3.setIsDelete(false);
				computeEntity3.setUpdatedBy(userName);
				computeEntity3.setUpdatedOn(now);
				computeEntity3.setCreatedBy(userName);
				computeEntity3.setCreatedOn(now);
				// adding to list
				computeEntityList.add(computeEntity3);

				Gstr3BSetOffEntityComputeDetailsEntity computeEntity4 = new Gstr3BSetOffEntityComputeDetailsEntity();
				computeEntity4.setGstin(gstin);
				computeEntity4.setTaxPeriod(taxPeriod);
				computeEntity4.setSection("LIABILITY");
				computeEntity4.setRequestId(reqId);
				computeEntity4.setIgst(libRevChrgIgst);
				computeEntity4.setCgst(libRevChrgCgst);
				computeEntity4.setSgst(libRevChrgSgst);
				computeEntity4.setCess(libRevChrgCess);
				computeEntity4.setIsDelete(false);
				computeEntity4.setUpdatedBy(userName);
				computeEntity4.setUpdatedOn(now);
				computeEntity4.setCreatedBy(userName);
				computeEntity4.setCreatedOn(now);
				// adding to list
				computeEntityList.add(computeEntity4);

				Gstr3BSetOffEntityComputeDetailsEntity computeEntity5 = new Gstr3BSetOffEntityComputeDetailsEntity();
				computeEntity5.setGstin(gstin);
				computeEntity5.setTaxPeriod(taxPeriod);
				computeEntity5.setSection("NET_LIABILITY");
				computeEntity5.setRequestId(reqId);
				computeEntity5.setIgst(netLiaIgst);
				computeEntity5.setCgst(netLiaCgst);
				computeEntity5.setSgst(netLiaSgst);
				computeEntity5.setCess(netLiaCess);
				computeEntity5.setIsDelete(false);
				computeEntity5.setUpdatedBy(userName);
				computeEntity5.setUpdatedOn(now);
				computeEntity5.setCreatedBy(userName);
				computeEntity5.setCreatedOn(now);
				// adding to list
				computeEntityList.add(computeEntity5);

				// saving to compute detail table

				computeDetailsRepo.softDelete(taxPeriod, gstin);
				computeDetailsRepo.saveAll(computeEntityList);

				// updating config table
				computeConfigRepo.updateStatus(taxPeriod, gstin, now);

			}

		} catch (Exception ex) {

			ex.printStackTrace();
			LOGGER.error("error occured in Gstr3B compute ");
			throw new AppException(ex);

		}

		return "Success";
	}

	private Gstr3BSetOffLiabilityAndRevChrgDto convert(
			List<Gstr3bGstnSaveToAspEntity> gstr3bGstnSaveToAspEntity) {

		Gstr3BSetOffLiabilityAndRevChrgDto obj = new Gstr3BSetOffLiabilityAndRevChrgDto();
		
		BigDecimal igstB = BigDecimal.ZERO;
		BigDecimal cgstB = BigDecimal.ZERO;
		BigDecimal sgstB = BigDecimal.ZERO;
		BigDecimal cessB = BigDecimal.ZERO;
		
		for (Gstr3bGstnSaveToAspEntity o : gstr3bGstnSaveToAspEntity) {

			if (o != null && o.getSectionName().equalsIgnoreCase("3.1(b)")) {

				igstB = o.getIgst() != null ? o.getIgst() : BigDecimal.ZERO;
				cgstB = o.getCgst() != null ? o.getCgst() : BigDecimal.ZERO;
				sgstB = o.getSgst() != null ? o.getSgst() : BigDecimal.ZERO;
				cessB = o.getCess() != null ? o.getCess() : BigDecimal.ZERO;
			}
		}
		for (Gstr3bGstnSaveToAspEntity o : gstr3bGstnSaveToAspEntity) {
			
			
			if (o.getSectionName().equalsIgnoreCase("3.1(a)")) {

				obj.setOtsIgst(o.getIgst().add(igstB));
				obj.setOtsCgst(o.getCgst().add(cgstB));
				obj.setOtsSgst(o.getSgst().add(sgstB));
				obj.setOtsCess(o.getCess().add(cessB));

			}
			if (o.getSectionName().equalsIgnoreCase("3.1(d)")) {

				obj.setLibRevChrgIgst(o.getIgst());
				obj.setLibRevChrgCgst(o.getCgst());
				obj.setLibRevChrgSgst(o.getSgst());
				obj.setLibRevChrgCess(o.getCess());

			}
			if (o.getSectionName().equalsIgnoreCase("4(c)")) {

				obj.setNetItcAvailIgst(o.getIgst());
				obj.setNetItcAvailCgst(o.getCgst());
				obj.setNetItcAvailSgst(o.getSgst());
				obj.setNetItcAvailCess(o.getCess());

			}

		}

		return obj;
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private Gstr3BIntrFeeDto convertRcn(Gstr3bGstnSaveToAspEntity obj) {

		Gstr3BIntrFeeDto dto = new Gstr3BIntrFeeDto();
		dto.setCess(obj.getCess());
		dto.setCgst(obj.getCgst());
		dto.setIgst(obj.getIgst());
		dto.setSgst(obj.getSgst());

		return dto;

	}

	private Gstr3BIntrFeeDto convertInteAndFee(Gstr3bGstnSaveToAspEntity obj) {

		Gstr3BIntrFeeDto dto = new Gstr3BIntrFeeDto();
		dto.setCess(obj.getCess());
		dto.setCgst(obj.getCgst());
		dto.setIgst(obj.getIgst());
		dto.setSgst(obj.getSgst());
		dto.setSectionName(obj.getSubSectionName());
		return dto;

	}
	
	/*public static void main(String[] args) {
		
		BigDecimal a = new BigDecimal(351);
		BigDecimal ans = a.divide(new BigDecimal(2));
		System.out.println(ans);
	}*/

}
