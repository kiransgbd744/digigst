package com.ey.advisory.app.getr2.ap.recon.summary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryDto;
import com.ey.advisory.common.AppException;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr2BPRReconSummaryDaoImpl")
public class Gstr2BPRReconSummaryDaoImpl
		implements Gstr2BPRReconSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2ReconSummaryDto> findReconSummary(Long configId,
			List<String> gstins, Integer toTaxPeriod, Integer fromTaxPeriod,
			String reconType, Integer toTaxPeriod_2B, Integer fromTaxPeriod_2B, String criteria) {

	
		String gstin = "";
		
		if(configId != 0L) {
			
			fromTaxPeriod_2B = 0;
			toTaxPeriod_2B = 0;
		}

		try {

			if (gstins != null && !gstins.isEmpty()) {
				gstin = String.join(",", gstins);
			}

			StoredProcedureQuery storedProc = null;

			LOGGER.info(
					"Inside Gstr2BPRReconSummaryDaoImpl.findReconSummary():: "
							+ "Invoking USP_2BPR_MANUAL_RECON_SUMMARY Stored Proc");
				
			storedProc = entityManager.createStoredProcedureQuery(
					"USP_2BPR_MANUAL_RECON_SUMMARY");
			
			storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
					ParameterMode.IN);

			storedProc.setParameter("P_GSTIN", gstin);
			
			storedProc.registerStoredProcedureParameter("P_FROM_RETURN_PERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_FROM_RETURN_PERIOD", fromTaxPeriod);

			storedProc.registerStoredProcedureParameter("P_TO_RETURN_PERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_TO_RETURN_PERIOD", toTaxPeriod);
			
			storedProc.registerStoredProcedureParameter("P_B2_FROM_RETURN_PERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_B2_FROM_RETURN_PERIOD", fromTaxPeriod_2B);

			storedProc.registerStoredProcedureParameter("P_B2_TO_RETURN_PERIOD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_B2_TO_RETURN_PERIOD", toTaxPeriod_2B);
			
			if(criteria == null){
				criteria = "BOTH";
			} else if(criteria.equalsIgnoreCase("PRtaxperiod")){
				criteria = "PR";
			} else if(criteria.equalsIgnoreCase("2Btaxperiod")){
				criteria = "2B";
			} else{
				criteria = "BOTH";
			}
			storedProc.registerStoredProcedureParameter("P_TAXPRDBASE",
						String.class, ParameterMode.IN);

			storedProc.setParameter("P_TAXPRDBASE", criteria);
			
	/*		storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId); */


			
			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Converting Query And converting to List BEGIN");
			List<Gstr2ReconSummaryDto> retList = list.stream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Set<String> uniqueParticular = new HashSet<>();

			for (Gstr2ReconSummaryDto particular : retList) {
				uniqueParticular.add(particular.getPerticulas());
			}
			uniqueParticular.remove("TOTAL");
			uniqueParticular.remove("ForceMatch/GSTR3B");
			uniqueParticular.remove("Dropped Records");
			uniqueParticular.remove("GRAND TOTAL");
			List<String> particulars = Lists.newArrayList(uniqueParticular);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("convertScreenLevelData() "
						+ "Parameter particulars = %s ", particulars);
				LOGGER.debug(msg);
			}

			List<Gstr2ReconSummaryDto> respList = convertScreenLevelData(
					particulars, retList);

			LOGGER.debug("Converting Query And converting to List END");

			Collections.sort(respList, (a, b) -> a.getOrderPosition()
					.compareToIgnoreCase(b.getOrderPosition()));

			return respList;

		} catch (Exception e) {
			String msg = String.format(
					"Error while calling store proc "
							+ "USP_AUTO_2APR_MANUAL_RECON_SUMMARY :: configId %s ",
					configId.toString());
			LOGGER.error(msg);
			e.printStackTrace();
			throw new AppException(msg,e);
		}
	}

	private List<Gstr2ReconSummaryDto> convertScreenLevelData(
			List<String> particular, List<Gstr2ReconSummaryDto> retList) {
		// Match
				Long prcountMatch = 0L;
				BigDecimal prPercentMatch = BigDecimal.ZERO;
				BigDecimal prTaxableValueMatch = BigDecimal.ZERO;
				BigDecimal totalTaxMatch = BigDecimal.ZERO;
				
				BigDecimal prIgstMatch = BigDecimal.ZERO;
				BigDecimal prCgstMatch = BigDecimal.ZERO;
				BigDecimal prSgstMatch = BigDecimal.ZERO;
				BigDecimal prCessMatch = BigDecimal.ZERO;
				
				Long gstr2ACountMatch = 0L;
				BigDecimal gstr2APercentMatch = BigDecimal.ZERO;
				BigDecimal gstr2ATaxValueMatch = BigDecimal.ZERO;
				BigDecimal gstr2AtotalTaxMatch = BigDecimal.ZERO;
				
				BigDecimal a2IgstMatch = BigDecimal.ZERO;
				BigDecimal a2CgstMatch = BigDecimal.ZERO;
				BigDecimal a2SgstMatch = BigDecimal.ZERO;
				BigDecimal a2CessMatch = BigDecimal.ZERO;
				
				// MisMatch
				Long prcountMisMatch = 0L;
				BigDecimal prPercentMisMatch = BigDecimal.ZERO;
				BigDecimal prTaxableValueMisMatch = BigDecimal.ZERO;
				BigDecimal totalTaxMisMatch = BigDecimal.ZERO;
				
				BigDecimal prIgstMisMatch = BigDecimal.ZERO;
				BigDecimal prCgstMisMatch = BigDecimal.ZERO;
				BigDecimal prSgstMisMatch = BigDecimal.ZERO;
				BigDecimal prCessMisMatch = BigDecimal.ZERO;
				
				Long gstr2ACountMisMatch = 0L;
				BigDecimal gstr2APercentMisMatch = BigDecimal.ZERO;
				BigDecimal gstr2ATaxValueMisMatch = BigDecimal.ZERO;
				BigDecimal gstr2AtotalTaxMisMatch = BigDecimal.ZERO;
				
				BigDecimal a2IgstMisMatch = BigDecimal.ZERO;
				BigDecimal a2CgstMisMatch = BigDecimal.ZERO;
				BigDecimal a2SgstMisMatch = BigDecimal.ZERO;
				BigDecimal a2CessMisMatch = BigDecimal.ZERO;
				
				// potential
				Long prcountPot = 0L;
				BigDecimal prPercentPot = BigDecimal.ZERO;
				BigDecimal prTaxableValuePot = BigDecimal.ZERO;
				BigDecimal totalTaxPot = BigDecimal.ZERO;
				
				BigDecimal prIgstPot = BigDecimal.ZERO;
				BigDecimal prCgstPot = BigDecimal.ZERO;
				BigDecimal prSgstPot = BigDecimal.ZERO;
				BigDecimal prCessPot = BigDecimal.ZERO;
				
				Long gstr2ACountPot = 0L;
				BigDecimal gstr2APercentPot = BigDecimal.ZERO;
				BigDecimal gstr2ATaxValuePot = BigDecimal.ZERO;
				BigDecimal gstr2AtotalTaxPot = BigDecimal.ZERO;
				
				BigDecimal a2IgstPot = BigDecimal.ZERO;
				BigDecimal a2CgstPot = BigDecimal.ZERO;
				BigDecimal a2SgstPot = BigDecimal.ZERO;
				BigDecimal a2CessPot = BigDecimal.ZERO;
				
				// Logical
				Long prcountLog = 0L;
				BigDecimal prPercentLog = BigDecimal.ZERO;
				BigDecimal prTaxableValueLog = BigDecimal.ZERO;
				BigDecimal totalTaxLog = BigDecimal.ZERO;
				
				BigDecimal prIgstLog = BigDecimal.ZERO;
				BigDecimal prCgstLog = BigDecimal.ZERO;
				BigDecimal prSgstLog = BigDecimal.ZERO;
				BigDecimal prCessLog = BigDecimal.ZERO;
				
				Long gstr2ACountLog = 0L;
				BigDecimal gstr2APercentLog = BigDecimal.ZERO;
				BigDecimal gstr2ATaxValueLog = BigDecimal.ZERO;
				BigDecimal gstr2AtotalTaxLog = BigDecimal.ZERO;
				
				BigDecimal a2IgstLog = BigDecimal.ZERO;
				BigDecimal a2CgstLog = BigDecimal.ZERO;
				BigDecimal a2SgstLog = BigDecimal.ZERO;
				BigDecimal a2CessLog = BigDecimal.ZERO;
				
				// Additional Entries
				Long prcountAE = 0L;
				BigDecimal prPercentAE = BigDecimal.ZERO;
				BigDecimal prTaxableValueAE = BigDecimal.ZERO;
				BigDecimal totalTaxAE = BigDecimal.ZERO;
				
				BigDecimal prIgstAE = BigDecimal.ZERO;
				BigDecimal prCgstAE = BigDecimal.ZERO;
				BigDecimal prSgstAE = BigDecimal.ZERO;
				BigDecimal prCessAE = BigDecimal.ZERO;
				
				Long gstr2ACountAE = 0L;
				BigDecimal gstr2APercentAE = BigDecimal.ZERO;
				BigDecimal gstr2ATaxValueAE = BigDecimal.ZERO;
				BigDecimal gstr2AtotalTaxAE = BigDecimal.ZERO;
				
				BigDecimal a2IgstAE = BigDecimal.ZERO;
				BigDecimal a2CgstAE = BigDecimal.ZERO;
				BigDecimal a2SgstAE = BigDecimal.ZERO;
				BigDecimal a2CessAE = BigDecimal.ZERO;
				
				// Imports/SEZG Matching
				Long prcountImpg = 0L;
				BigDecimal prPercentImpg = BigDecimal.ZERO;
				BigDecimal prTaxableValueImpg = BigDecimal.ZERO;
				BigDecimal totalTaxImpg = BigDecimal.ZERO;
				
				BigDecimal prIgstImpg = BigDecimal.ZERO;
				BigDecimal prCgstImpg = BigDecimal.ZERO;
				BigDecimal prSgstImpg = BigDecimal.ZERO;
				BigDecimal prCessImpg = BigDecimal.ZERO;
				
				Long gstr2ACountImpg = 0L;
				BigDecimal gstr2APercentImpg = BigDecimal.ZERO;
				BigDecimal gstr2ATaxValueImpg = BigDecimal.ZERO;
				BigDecimal gstr2AtotalTaxImpg = BigDecimal.ZERO;
				
				BigDecimal a2IgstImpg = BigDecimal.ZERO;
				BigDecimal a2CgstImpg = BigDecimal.ZERO;
				BigDecimal a2SgstImpg = BigDecimal.ZERO;
				BigDecimal a2CessImpg = BigDecimal.ZERO;

		List<Gstr2ReconSummaryDto> obj = new ArrayList<>();
		final String level = "L1";

		LOGGER.debug("Calculate value for level 1 and level 2 started");

		for (Gstr2ReconSummaryDto resp : retList) {

			Gstr2ReconSummaryDto gstr2Recon = new Gstr2ReconSummaryDto();

			if ("Match".equalsIgnoreCase(resp.getPerticulas())) {
				prcountMatch = prcountMatch + resp.getPrCount();
				prPercentMatch = prPercentMatch.add(resp.getPrPercenatge());
				prTaxableValueMatch = prTaxableValueMatch
						.add(resp.getPrTaxableValue());
				totalTaxMatch = totalTaxMatch.add(resp.getPrTotalTax());
				
				prIgstMatch = prIgstMatch.add(resp.getPrIgst());
				prCgstMatch = prCgstMatch.add(resp.getPrCgst());
				prSgstMatch = prSgstMatch.add(resp.getPrSgst());
				prCessMatch = prCessMatch.add(resp.getPrCess());
				
				gstr2ACountMatch = gstr2ACountMatch + resp.getA2Count();
				gstr2APercentMatch = gstr2APercentMatch
						.add(resp.getA2Percenatge());
				gstr2ATaxValueMatch = gstr2ATaxValueMatch
						.add(resp.getA2TaxableValue());
				gstr2AtotalTaxMatch = gstr2AtotalTaxMatch
						.add(resp.getA2TotalTax());
				
				a2IgstMatch = a2IgstMatch.add(resp.getA2Igst());
				a2CgstMatch = a2CgstMatch.add(resp.getA2Cgst());
				a2SgstMatch = a2SgstMatch.add(resp.getA2Sgst());
				a2CessMatch = a2CessMatch.add(resp.getA2Cess());

			} else if ("MisMatch".equalsIgnoreCase(resp.getPerticulas())) {
				prcountMisMatch = prcountMisMatch + resp.getPrCount();
				prPercentMisMatch = prPercentMisMatch
						.add(resp.getPrPercenatge());
				prTaxableValueMisMatch = prTaxableValueMisMatch
						.add(resp.getPrTaxableValue());
				totalTaxMisMatch = totalTaxMisMatch.add(resp.getPrTotalTax());
				
				prIgstMisMatch = prIgstMisMatch.add(resp.getPrIgst());
				prCgstMisMatch = prCgstMisMatch.add(resp.getPrCgst());
				prSgstMisMatch = prSgstMisMatch.add(resp.getPrSgst());
				prCessMisMatch = prCessMisMatch.add(resp.getPrCess());
				
				gstr2ACountMisMatch = gstr2ACountMisMatch + resp.getA2Count();
				gstr2APercentMisMatch = gstr2APercentMisMatch
						.add(resp.getA2Percenatge());
				gstr2ATaxValueMisMatch = gstr2ATaxValueMisMatch
						.add(resp.getA2TaxableValue());
				gstr2AtotalTaxMisMatch = gstr2AtotalTaxMisMatch
						.add(resp.getA2TotalTax());
				
				a2IgstMisMatch = a2IgstMisMatch.add(resp.getA2Igst());
				a2CgstMisMatch = a2CgstMisMatch.add(resp.getA2Cgst());
				a2SgstMisMatch = a2SgstMisMatch.add(resp.getA2Sgst());
				a2CessMisMatch = a2CessMisMatch.add(resp.getA2Cess());
				
			} else if ("Potential".equalsIgnoreCase(resp.getPerticulas())) {
				prcountPot = prcountPot + resp.getPrCount();
				prPercentPot = prPercentPot.add(resp.getPrPercenatge());
				prTaxableValuePot = prTaxableValuePot
						.add(resp.getPrTaxableValue());
				totalTaxPot = totalTaxPot.add(resp.getPrTotalTax());
				
				prIgstPot = prIgstPot.add(resp.getPrIgst());
				prCgstPot = prCgstPot.add(resp.getPrCgst());
				prSgstPot = prSgstPot.add(resp.getPrSgst());
				prCessPot = prCessPot.add(resp.getPrCess());
				
				
				gstr2ACountPot = gstr2ACountPot + resp.getA2Count();
				gstr2APercentPot = gstr2APercentPot.add(resp.getA2Percenatge());
				gstr2ATaxValuePot = gstr2ATaxValuePot
						.add(resp.getA2TaxableValue());
				gstr2AtotalTaxPot = gstr2AtotalTaxPot.add(resp.getA2TotalTax());
				
				a2IgstPot = a2IgstPot.add(resp.getA2Igst());
				a2CgstPot = a2CgstPot.add(resp.getA2Cgst());
				a2SgstPot = a2SgstPot.add(resp.getA2Sgst());
				a2CessPot = a2CessPot.add(resp.getA2Cess());
				
			} else if ("Logical".equalsIgnoreCase(resp.getPerticulas())) {
				prcountLog = prcountLog + resp.getPrCount();
				prPercentLog = prPercentLog.add(resp.getPrPercenatge());
				prTaxableValueLog = prTaxableValueLog
						.add(resp.getPrTaxableValue());
				totalTaxLog = totalTaxLog.add(resp.getPrTotalTax());
				
				prIgstLog = prIgstLog.add(resp.getPrIgst());
				prCgstLog = prCgstLog.add(resp.getPrCgst());
				prSgstLog = prSgstLog.add(resp.getPrSgst());
				prCessLog = prCessLog.add(resp.getPrCess());
				
				gstr2ACountLog = gstr2ACountLog + resp.getA2Count();
				gstr2APercentLog = gstr2APercentLog.add(resp.getA2Percenatge());
				gstr2ATaxValueLog = gstr2ATaxValueLog
						.add(resp.getA2TaxableValue());
				gstr2AtotalTaxLog = gstr2AtotalTaxLog.add(resp.getA2TotalTax());
				
				a2IgstLog = a2IgstLog.add(resp.getA2Igst());
				a2CgstLog = a2CgstLog.add(resp.getA2Cgst());
				a2SgstLog = a2SgstLog.add(resp.getA2Sgst());
				a2CessLog = a2CessLog.add(resp.getA2Cess());
				
			} else if ("Additional Entries"
					.equalsIgnoreCase(resp.getPerticulas())) {
				prcountAE = prcountAE + resp.getPrCount();
				prPercentAE = prPercentAE.add(resp.getPrPercenatge());
				prTaxableValueAE = prTaxableValueAE
						.add(resp.getPrTaxableValue());
				totalTaxAE = totalTaxAE.add(resp.getPrTotalTax());
				
				prIgstAE = prIgstAE.add(resp.getPrIgst());
				prCgstAE = prCgstAE.add(resp.getPrCgst());
				prSgstAE = prSgstAE.add(resp.getPrSgst());
				prCessAE = prCessAE.add(resp.getPrCess());
				
				gstr2ACountAE = gstr2ACountAE + resp.getA2Count();
				gstr2APercentAE = gstr2APercentAE.add(resp.getA2Percenatge());
				gstr2ATaxValueAE = gstr2ATaxValueAE
						.add(resp.getA2TaxableValue());
				gstr2AtotalTaxAE = gstr2AtotalTaxAE.add(resp.getA2TotalTax());
				
				a2IgstAE = a2IgstAE.add(resp.getA2Igst());
				a2CgstAE = a2CgstAE.add(resp.getA2Cgst());
				a2SgstAE = a2SgstAE.add(resp.getA2Sgst());
				a2CessAE = a2CessAE.add(resp.getA2Cess());
				
			} else if ("Imports/SEZG Matching".equalsIgnoreCase(resp.getPerticulas())) {
				prcountImpg = prcountImpg + resp.getPrCount();
				prPercentImpg = prPercentImpg
						.add(resp.getPrPercenatge());
				prTaxableValueImpg = prTaxableValueImpg
						.add(resp.getPrTaxableValue());
				totalTaxImpg = totalTaxImpg.add(resp.getPrTotalTax());
				
				prIgstImpg = prIgstImpg.add(resp.getPrIgst());
				prCgstImpg = prCgstImpg.add(resp.getPrCgst());
				prSgstImpg = prSgstImpg.add(resp.getPrSgst());
				prCessImpg = prCessImpg.add(resp.getPrCess());
				
				gstr2ACountImpg = gstr2ACountImpg + resp.getA2Count();
				gstr2APercentImpg = gstr2APercentImpg
						.add(resp.getA2Percenatge());
				gstr2ATaxValueImpg = gstr2ATaxValueImpg
						.add(resp.getA2TaxableValue());
				gstr2AtotalTaxImpg = gstr2AtotalTaxImpg
						.add(resp.getA2TotalTax());
				
				a2IgstImpg = a2IgstImpg.add(resp.getA2Igst());
				a2CgstImpg = a2CgstImpg.add(resp.getA2Cgst());
				a2SgstImpg = a2SgstImpg.add(resp.getA2Sgst());
				a2CessImpg = a2CessImpg.add(resp.getA2Cess());
			}

			String perticularName = (resp.getPerticulasName() != null)
					? (String) resp.getPerticulasName() : "";
			gstr2Recon.setPerticulasName(perticularName);

			Long prCount = resp.getPrCount();
			gstr2Recon.setPrCount(prCount);

			gstr2Recon.setPrPercenatge((BigDecimal) resp.getPrPercenatge());
			gstr2Recon.setPrTaxableValue((BigDecimal) resp.getPrTaxableValue());
			gstr2Recon.setPrTotalTax((BigDecimal) resp.getPrTotalTax());
			
			// added for tax breakups PR
			gstr2Recon.setPrIgst((BigDecimal) resp.getPrIgst());
			gstr2Recon.setPrCgst((BigDecimal) resp.getPrCgst());
			gstr2Recon.setPrSgst((BigDecimal) resp.getPrSgst());
			gstr2Recon.setPrCess((BigDecimal) resp.getPrCess());

			Long a2Count = resp.getA2Count();
			gstr2Recon.setA2Count(a2Count);
			gstr2Recon.setA2Percenatge((BigDecimal) resp.getA2Percenatge());
			gstr2Recon.setA2TaxableValue((BigDecimal) resp.getA2TaxableValue());
			gstr2Recon.setA2TotalTax((BigDecimal) resp.getA2TotalTax());

			// added for tax breakups PR
			gstr2Recon.setA2Igst((BigDecimal) resp.getA2Igst());
			gstr2Recon.setA2Cgst((BigDecimal) resp.getA2Cgst());
			gstr2Recon.setA2Sgst((BigDecimal) resp.getA2Sgst());
			gstr2Recon.setA2Cess((BigDecimal) resp.getA2Cess());

			if ("ForceMatch/GSTR3B".equalsIgnoreCase(perticularName)) {
				gstr2Recon.setLevel("L1");
				gstr2Recon.setOrderPosition("Y");
			} else if ("Dropped Records".equalsIgnoreCase(perticularName)) {
				gstr2Recon.setOrderPosition("Y1");
				gstr2Recon.setLevel("L1");
			}else if ("TOTAL".equalsIgnoreCase(perticularName)) {
				gstr2Recon.setOrderPosition("X");
				gstr2Recon.setLevel("L1");
			} else {
				gstr2Recon.setLevel("L2");
			}
			if (perticularName.equalsIgnoreCase("Exact Match")) {
				gstr2Recon.setOrderPosition("B");
			} else if (perticularName
					.equalsIgnoreCase("Match With Tolerance")) {
				gstr2Recon.setOrderPosition("C");
			} else if (perticularName.equalsIgnoreCase("Value Mismatch")) {
				gstr2Recon.setOrderPosition("E");
			} else if (perticularName.equalsIgnoreCase("POS Mismatch")) {
				gstr2Recon.setOrderPosition("F");
			} else if (perticularName.equalsIgnoreCase("Doc Date Mismatch")) {
				gstr2Recon.setOrderPosition("G");
			} else if (perticularName.equalsIgnoreCase("Doc Type Mismatch")) {
				gstr2Recon.setOrderPosition("H");
			} else if (perticularName.equalsIgnoreCase("Doc No Mismatch I")) {
				gstr2Recon.setOrderPosition("I");
			} else if (perticularName.equalsIgnoreCase("Doc No Mismatch II")) {
				gstr2Recon.setOrderPosition("J");
			} else if (perticularName.equalsIgnoreCase("Doc No & Doc Date Mismatch")) {
				gstr2Recon.setOrderPosition("K");
			} else if (perticularName.equalsIgnoreCase("Multi-Mismatch")) {
				gstr2Recon.setOrderPosition("L");
			} else if (perticularName.equalsIgnoreCase("Potential-I")) {
				gstr2Recon.setOrderPosition("N");
			} else if (perticularName.equalsIgnoreCase("Potential-II")) {
				gstr2Recon.setOrderPosition("O");
			} else if (perticularName.equalsIgnoreCase("Logical Match")) {
				gstr2Recon.setOrderPosition("Q");
			}
			
			// Imports/SEZG Matching
			else if (perticularName.equalsIgnoreCase("Match")) {
				gstr2Recon.setOrderPosition("S");
			} else if (perticularName.equalsIgnoreCase("Mismatch")) {
				gstr2Recon.setOrderPosition("T");
			} else if(perticularName.equalsIgnoreCase("Addition in PR(Imports)")) {
				gstr2Recon.setOrderPosition("T1");
			} else if(perticularName.equalsIgnoreCase("Addition in 2A(Imports)")) {
				gstr2Recon.setOrderPosition("T2");
			} else if(perticularName.equalsIgnoreCase("Addition in 2B(Imports)")) {
				gstr2Recon.setOrderPosition("T2");
			} else if (perticularName.equalsIgnoreCase("Addition in PR")) {
				gstr2Recon.setOrderPosition("V");
			} else if (perticularName.equalsIgnoreCase("Addition in 2A")) {
				gstr2Recon.setOrderPosition("W");
			} else if (perticularName.equalsIgnoreCase("Addition in 2B")) {
				gstr2Recon.setOrderPosition("W");}
				else if (perticularName.equalsIgnoreCase("Addition in Inward E-Inv")) {
					gstr2Recon.setOrderPosition("W");
			}else if ("GRAND TOTAL".equalsIgnoreCase(perticularName)) {
				gstr2Recon.setOrderPosition("Z");
				gstr2Recon.setLevel("L1");
			}

			obj.add(gstr2Recon);

		}

		LOGGER.debug("Calculated value for level 1 and level 2 ended");

		LOGGER.debug("Setting values for level 1 started");

		for (int i = 0; i < particular.size(); i++) {
			Gstr2ReconSummaryDto gstr2Recon = new Gstr2ReconSummaryDto();
			if ("Match".equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setPerticulasName(particular.get(i));
				gstr2Recon.setPrCount(prcountMatch);
				gstr2Recon.setPrPercenatge(roundOff(prPercentMatch));
				gstr2Recon.setPrTaxableValue(prTaxableValueMatch);
				gstr2Recon.setPrTotalTax(totalTaxMatch);
				
				gstr2Recon.setPrIgst(prIgstMatch);
				gstr2Recon.setPrCgst(prCgstMatch);
				gstr2Recon.setPrSgst(prSgstMatch);
				gstr2Recon.setPrCess(prCessMatch);
				
				gstr2Recon.setA2Count(gstr2ACountMatch);
				gstr2Recon.setA2Percenatge(roundOff(gstr2APercentMatch));
				gstr2Recon.setA2TaxableValue(gstr2ATaxValueMatch);
				gstr2Recon.setA2TotalTax(gstr2AtotalTaxMatch);
				
				gstr2Recon.setA2Igst(a2IgstMatch);
				gstr2Recon.setA2Cgst(a2CgstMatch);
				gstr2Recon.setA2Sgst(a2SgstMatch);
				gstr2Recon.setA2Cess(a2CessMatch);
				
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("A");

			} else if ("Mismatch".equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setPerticulasName(particular.get(i));
				gstr2Recon.setPrCount(prcountMisMatch);
				gstr2Recon.setPrPercenatge(roundOff(prPercentMisMatch));
				gstr2Recon.setPrTaxableValue(prTaxableValueMisMatch);
				gstr2Recon.setPrTotalTax(totalTaxMisMatch);
				
				gstr2Recon.setPrIgst(prIgstMisMatch);
				gstr2Recon.setPrCgst(prCgstMisMatch);
				gstr2Recon.setPrSgst(prSgstMisMatch);
				gstr2Recon.setPrCess(prCessMisMatch);
				
				gstr2Recon.setA2Count(gstr2ACountMisMatch);
				gstr2Recon.setA2Percenatge(roundOff(gstr2APercentMisMatch));
				gstr2Recon.setA2TaxableValue(gstr2ATaxValueMisMatch);
				gstr2Recon.setA2TotalTax(gstr2AtotalTaxMisMatch);
				
				gstr2Recon.setA2Igst(a2IgstMisMatch);
				gstr2Recon.setA2Cgst(a2CgstMisMatch);
				gstr2Recon.setA2Sgst(a2SgstMisMatch);
				gstr2Recon.setA2Cess(a2CessMisMatch);
				
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("D");

			} else if ("Potential".equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setPerticulasName(particular.get(i));
				gstr2Recon.setPrCount(prcountPot);
				gstr2Recon.setPrPercenatge(roundOff(prPercentPot));
				gstr2Recon.setPrTaxableValue(prTaxableValuePot);
				gstr2Recon.setPrTotalTax(totalTaxPot);
				
				gstr2Recon.setPrIgst(prIgstPot);
				gstr2Recon.setPrCgst(prCgstPot);
				gstr2Recon.setPrSgst(prSgstPot);
				gstr2Recon.setPrCess(prCessPot);
				
				gstr2Recon.setA2Count(gstr2ACountPot);
				gstr2Recon.setA2Percenatge(roundOff(gstr2APercentPot));
				gstr2Recon.setA2TaxableValue(gstr2ATaxValuePot);
				gstr2Recon.setA2TotalTax(gstr2AtotalTaxPot);
				
				gstr2Recon.setA2Igst(a2IgstPot);
				gstr2Recon.setA2Cgst(a2CgstPot);
				gstr2Recon.setA2Sgst(a2SgstPot);
				gstr2Recon.setA2Cess(a2CessPot);
				
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("M");

			} else if ("Logical".equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setPerticulasName(particular.get(i));
				gstr2Recon.setPrCount(prcountLog);
				gstr2Recon.setPrPercenatge(roundOff(prPercentLog));
				gstr2Recon.setPrTaxableValue(prTaxableValueLog);
				gstr2Recon.setPrTotalTax(totalTaxLog);
				
				gstr2Recon.setPrIgst(prIgstLog);
				gstr2Recon.setPrCgst(prCgstLog);
				gstr2Recon.setPrSgst(prSgstLog);
				gstr2Recon.setPrCess(prCessLog);
				
				gstr2Recon.setA2Count(gstr2ACountLog);
				gstr2Recon.setA2Percenatge(roundOff(gstr2APercentLog));
				gstr2Recon.setA2TaxableValue(gstr2ATaxValueLog);
				gstr2Recon.setA2TotalTax(gstr2AtotalTaxLog);
				
				gstr2Recon.setA2Igst(a2IgstLog);
				gstr2Recon.setA2Cgst(a2CgstLog);
				gstr2Recon.setA2Sgst(a2SgstLog);
				gstr2Recon.setA2Cess(a2CessLog);
				
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("P");

			} else if ("Additional Entries"
					.equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setPerticulasName(particular.get(i));
				gstr2Recon.setPrCount(prcountAE);
				gstr2Recon.setPrPercenatge(roundOff(prPercentAE));
				gstr2Recon.setPrTaxableValue(prTaxableValueAE);
				gstr2Recon.setPrTotalTax(totalTaxAE);
				
				gstr2Recon.setPrIgst(prIgstAE);
				gstr2Recon.setPrCgst(prCgstAE);
				gstr2Recon.setPrSgst(prSgstAE);
				gstr2Recon.setPrCess(prCessAE);
				
				gstr2Recon.setA2Count(gstr2ACountAE);
				gstr2Recon.setA2Percenatge(roundOff(gstr2APercentAE));
				gstr2Recon.setA2TaxableValue(gstr2ATaxValueAE);
				gstr2Recon.setA2TotalTax(gstr2AtotalTaxAE);
				
				gstr2Recon.setA2Igst(a2IgstAE);
				gstr2Recon.setA2Cgst(a2CgstAE);
				gstr2Recon.setA2Sgst(a2SgstAE);
				gstr2Recon.setA2Cess(a2CessAE);
				
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("U");
			}
			else if ("Imports/SEZG Matching"
					.equalsIgnoreCase(particular.get(i))) {
				gstr2Recon.setPerticulasName(particular.get(i));
				gstr2Recon.setPrCount(prcountImpg);
				gstr2Recon.setPrPercenatge(roundOff(prPercentImpg));
				gstr2Recon.setPrTaxableValue(prTaxableValueImpg);
				gstr2Recon.setPrTotalTax(totalTaxImpg);
				
				gstr2Recon.setPrIgst(prIgstImpg);
				gstr2Recon.setPrCgst(prCgstImpg);
				gstr2Recon.setPrSgst(prSgstImpg);
				gstr2Recon.setPrCess(prCessImpg);
				
				gstr2Recon.setA2Count(gstr2ACountImpg);
				gstr2Recon.setA2Percenatge(roundOff(gstr2APercentImpg));
				gstr2Recon.setA2TaxableValue(gstr2ATaxValueImpg);
				gstr2Recon.setA2TotalTax(gstr2AtotalTaxImpg);
				
				gstr2Recon.setA2Igst(a2IgstImpg);
				gstr2Recon.setA2Cgst(a2CgstImpg);
				gstr2Recon.setA2Sgst(a2SgstImpg);
				gstr2Recon.setA2Cess(a2CessImpg);
				
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("R");
			}
			obj.add(gstr2Recon);
		}
		LOGGER.debug("Setting values for level 1 ended");

		return obj;

	}

	private Gstr2ReconSummaryDto convert(Object[] arr) {
		Gstr2ReconSummaryDto obj = new Gstr2ReconSummaryDto();

		try {
			String particulars = (arr[0] != null) ? (String) arr[0] : "";

				obj.setPerticulas(particulars);
			String perticularName = (arr[1] != null) ? (String) arr[1] : "";
			LOGGER.debug("perticularName " + perticularName);
			obj.setPerticulasName(perticularName);
			BigInteger b = (BigInteger) arr[2];
			Long prCount = b.longValue();
			obj.setPrCount(prCount);

			obj.setPrPercenatge((BigDecimal) arr[3]);
			obj.setPrTaxableValue((BigDecimal) arr[4]);
			obj.setPrTotalTax((BigDecimal) arr[9]);
			
			// tax breakups -PR
			obj.setPrIgst((BigDecimal) arr[5]);
			obj.setPrCgst((BigDecimal) arr[6]);
			obj.setPrSgst((BigDecimal) arr[7]);
			obj.setPrCess((BigDecimal) arr[8]);

			BigInteger cnt = (BigInteger) arr[10];
			Long a2Count = cnt.longValue();
			obj.setA2Count(a2Count);
			obj.setA2Percenatge((BigDecimal) arr[11]);
			obj.setA2TaxableValue((BigDecimal) arr[12]);
			obj.setA2TotalTax((BigDecimal) arr[17]);
			
			// tax breakups - 2A
			obj.setA2Igst((BigDecimal) arr[13]);
			obj.setA2Cgst((BigDecimal) arr[14]);
			obj.setA2Sgst((BigDecimal) arr[15]);
			obj.setA2Cess((BigDecimal) arr[16]);


			if (perticularName.equalsIgnoreCase("Exact Match")
					|| perticularName.equalsIgnoreCase("Match With Tolerance")
					|| perticularName.equalsIgnoreCase("Value Mismatch")
					|| perticularName.equalsIgnoreCase("POS Mismatch")
					|| perticularName.equalsIgnoreCase("Doc Date Mismatch")
					|| perticularName.equalsIgnoreCase("Doc Type Mismatch")
					|| perticularName.equalsIgnoreCase("Doc No Mismatch I")
					|| perticularName.equalsIgnoreCase("Multi-Mismatch")
					|| perticularName.equalsIgnoreCase("Potential-I")
					|| perticularName.equalsIgnoreCase("Doc No Mismatch II")
					|| perticularName.equalsIgnoreCase("Potential-II")
					|| perticularName.equalsIgnoreCase("Logical Match")
					|| perticularName.equalsIgnoreCase("Addition in PR")
					|| perticularName.equalsIgnoreCase("Addition in 2A") 
					|| perticularName.equalsIgnoreCase("Addition in 2B")
					|| perticularName.equalsIgnoreCase("Addition in Inward E-Inv")
					|| perticularName.equalsIgnoreCase("Doc No & Doc Date Mismatch")){
				obj.setLevel("L2");
			}

		} catch (Exception e) {
			String msg = String.format("Error while converting to dto");
			LOGGER.error(msg);
			e.printStackTrace();
			throw new AppException(msg,e);
		}
		return obj;
	}
	
	private BigDecimal roundOff(BigDecimal b) {
		
		return b.round(new MathContext(4, RoundingMode.HALF_UP));
		
	}

}
