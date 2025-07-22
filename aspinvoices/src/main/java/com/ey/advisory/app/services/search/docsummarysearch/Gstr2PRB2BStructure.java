package com.ey.advisory.app.services.search.docsummarysearch;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;

@Service("Gstr2PRB2BStructure")
public class Gstr2PRB2BStructure {

	public List<Gstr2PRSummarySectionDto> prB2BResp(
			List<Gstr2PRSummarySectionDto> b2bSummaryList, String a) {

		List<Gstr2PRSummarySectionDto> defaultEYList = null;
		if ("1-B2B".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", "INV", null,
					null, null, null, null);
		}
		if ("2-B2BA".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", null, "RNV",
					null, null, null, null);
		}
		if ("3-CDN".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", null, null,
					"DR", "CR", null, null);
		}
		if ("4-CDNA".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", null, null,
					null, null, "RDR", "RCR");
		}
		if ("5-ISD".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", "INV", null,
					null, "CR", null, null);
		}
		if ("6-ISDA".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", null, "RNV",
					null, null, null, "RCR");
		}
		if ("10-IMP".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, null, null, null, null,
					null, null, null);
		}
		if ("11-IMPA".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, null, null, null,
					null, null, null, null);
		}
		if ("12-RCURD".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", "INV", null,
					"DR", "CR", null, null);
		}
		if ("13-RCURDA".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", null, "RNV",
					null, null, "RDR", "RCR");
		}
		if ("14-RCMADV".equalsIgnoreCase(a)) {

			defaultEYList = getDefaultB2CEYStructure(a, "TOTAL", null, null,
					"ADV", "ADJ", null, null);
		}

		List<Gstr2PRSummarySectionDto> b2baRespbody = getaspEyList(
				defaultEYList, b2bSummaryList);

		return b2baRespbody;

	}

	private List<Gstr2PRSummarySectionDto> getDefaultB2CEYStructure(
			String table, String total, String inv, String rnv, String dr,
			String cr, String rdr, String rcr) {

		List<Gstr2PRSummarySectionDto> defaultB2CEY = new ArrayList<>();

		// Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
		if ("1-B2B".equalsIgnoreCase(table)) {
			// Gstr2PRSummarySectionDto b2bEyTotal = new
			// Gstr2PRSummarySectionDto();
			/*
			 * b2bEyTotal.setTable(table); b2bEyTotal.setIndex(0);
			 * b2bEyTotal.setDocType(total); b2bEyTotal =
			 * gstr1DefaultStructure(b2bEyTotal);
			 */

			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(7);
			b2cEy3AInv.setDocType(inv);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			// defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
		}
		if ("2-B2BA".equalsIgnoreCase(table)) {
			/*
			 * Gstr2PRSummarySectionDto b2bEyTotal = new
			 * Gstr2PRSummarySectionDto(); b2bEyTotal.setTable(table);
			 * b2bEyTotal.setIndex(0); b2bEyTotal.setDocType(total); b2bEyTotal
			 * = gstr1DefaultStructure(b2bEyTotal);
			 */
			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(10);
			b2cEy3AInv.setDocType(rnv);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			// defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
		}
		if ("3-CDN".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setIndex(6);
			b2bEyTotal.setDocType(total);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(8);
			b2cEy3AInv.setDocType(dr);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			Gstr2PRSummarySectionDto b2cEy3Acr = new Gstr2PRSummarySectionDto();
			b2cEy3Acr.setTable(table);
			b2cEy3Acr.setIndex(9);
			b2cEy3Acr.setDocType(cr);
			b2cEy3Acr = gstr1DefaultStructure(b2cEy3Acr);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
			defaultB2CEY.add(b2cEy3Acr);
		}
		if ("4-CDNA".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setIndex(6);
			b2bEyTotal.setDocType(total);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(11);
			b2cEy3AInv.setDocType(rdr);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			Gstr2PRSummarySectionDto b2cEy3Acr = new Gstr2PRSummarySectionDto();
			b2cEy3Acr.setTable(table);
			b2cEy3Acr.setIndex(12);
			b2cEy3Acr.setDocType(rcr);
			b2cEy3Acr = gstr1DefaultStructure(b2cEy3Acr);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
			defaultB2CEY.add(b2cEy3Acr);
		}
		if ("5-ISD".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setIndex(6);
			b2bEyTotal.setDocType(total);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(7);
			b2cEy3AInv.setDocType(inv);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			Gstr2PRSummarySectionDto b2cEy3Acr = new Gstr2PRSummarySectionDto();
			b2cEy3Acr.setTable(table);
			b2cEy3Acr.setIndex(9);
			b2cEy3Acr.setDocType(cr);
			b2cEy3Acr = gstr1DefaultStructure(b2cEy3Acr);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
			defaultB2CEY.add(b2cEy3Acr);
		}
		if ("6-ISDA".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setIndex(6);
			b2bEyTotal.setDocType(total);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(10);
			b2cEy3AInv.setDocType(rnv);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			Gstr2PRSummarySectionDto b2cEy3Acr = new Gstr2PRSummarySectionDto();
			b2cEy3Acr.setTable(table);
			b2cEy3Acr.setIndex(12);
			b2cEy3Acr.setDocType(rcr);
			b2cEy3Acr = gstr1DefaultStructure(b2cEy3Acr);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
			defaultB2CEY.add(b2cEy3Acr);
		}
		if ("10-IMP".equalsIgnoreCase(table)) {
			
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setDocType("TOTAL");
			b2bEyTotal.setIndex(6);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);
			
			Gstr2PRSummarySectionDto b2bEyImpg = new Gstr2PRSummarySectionDto();
			b2bEyImpg.setTable(table);
			b2bEyImpg.setDocType("IMPG");
			b2bEyImpg.setIndex(1);
			b2bEyImpg = gstr1DefaultStructure(b2bEyImpg);

			Gstr2PRSummarySectionDto b2cEyImps = new Gstr2PRSummarySectionDto();
			b2cEyImps.setTable(table);
			b2cEyImps.setDocType("IMPS");
			b2cEyImps.setIndex(0);
			b2cEyImps = gstr1DefaultStructure(b2cEyImps);

			Gstr2PRSummarySectionDto b2cEyImpgs = new Gstr2PRSummarySectionDto();
			b2cEyImpgs.setTable(table);
			b2cEyImpgs.setDocType("IMPGS");
			b2cEyImpgs.setIndex(2);
			b2cEyImpgs = gstr1DefaultStructure(b2cEyImpgs);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2bEyImpg);
			defaultB2CEY.add(b2cEyImps);
			defaultB2CEY.add(b2cEyImpgs);
		}

		if ("11-IMPA".equalsIgnoreCase(table)) {
			
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setDocType("TOTAL");
			b2bEyTotal.setIndex(6);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);
			
			Gstr2PRSummarySectionDto b2bEyImpg = new Gstr2PRSummarySectionDto();
			b2bEyImpg.setTable(table);
			b2bEyImpg.setDocType("IMPGA");
			b2bEyImpg.setIndex(4);
			b2bEyImpg = gstr1DefaultStructure(b2bEyImpg);

			Gstr2PRSummarySectionDto b2cEyImps = new Gstr2PRSummarySectionDto();
			b2cEyImps.setTable(table);
			b2cEyImps.setDocType("IMPSA");
			b2cEyImps.setIndex(3);
			b2cEyImps = gstr1DefaultStructure(b2cEyImps);

			Gstr2PRSummarySectionDto b2cEyImpgs = new Gstr2PRSummarySectionDto();
			b2cEyImpgs.setTable(table);
			b2cEyImpgs.setDocType("IMPGSA");
			b2cEyImpgs.setIndex(5);
			b2cEyImpgs = gstr1DefaultStructure(b2cEyImpgs);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2bEyImpg);
			defaultB2CEY.add(b2cEyImps);
			defaultB2CEY.add(b2cEyImpgs);
		}

		if ("12-RCURD".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setDocType(total);
			b2bEyTotal.setIndex(6);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			/*
			 * Gstr2PRSummarySectionDto b2cEy3AInv = new
			 * Gstr2PRSummarySectionDto(); b2cEy3AInv.setTable(table);
			 * b2cEy3AInv.setIndex(0); b2cEy3AInv.setDocType(inv); b2cEy3AInv =
			 * gstr1DefaultStructure(b2cEy3AInv);
			 */
			/*
			 * Gstr2PRSummarySectionDto b2cEy3Adr = new
			 * Gstr2PRSummarySectionDto(); b2cEy3Adr.setTable(table);
			 * b2cEy3Adr.setIndex(2); b2cEy3Adr.setDocType(dr); b2cEy3Adr =
			 * gstr1DefaultStructure(b2cEy3Adr);
			 * 
			 * Gstr2PRSummarySectionDto b2cEy3Acr = new
			 * Gstr2PRSummarySectionDto(); b2cEy3Acr.setTable(table);
			 * b2cEy3Acr.setIndex(3); b2cEy3Acr.setDocType(cr); b2cEy3Acr =
			 * gstr1DefaultStructure(b2cEy3Acr);
			 */

			defaultB2CEY.add(b2bEyTotal);
			/*
			 * defaultB2CEY.add(b2cEy3AInv); defaultB2CEY.add(b2cEy3Adr);
			 * defaultB2CEY.add(b2cEy3Acr);
			 */
		}
		if ("13-RCURDA".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setIndex(6);
			b2bEyTotal.setDocType(total);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			/*
			 * Gstr2PRSummarySectionDto b2cEy3AInv = new
			 * Gstr2PRSummarySectionDto(); b2cEy3AInv.setTable(table);
			 * b2cEy3AInv.setIndex(4); b2cEy3AInv.setDocType(rnv); b2cEy3AInv =
			 * gstr1DefaultStructure(b2cEy3AInv);
			 * 
			 * Gstr2PRSummarySectionDto b2cEy3Adr = new
			 * Gstr2PRSummarySectionDto(); b2cEy3Adr.setTable(table);
			 * b2cEy3Adr.setIndex(5); b2cEy3Adr.setDocType(rdr); b2cEy3Adr =
			 * gstr1DefaultStructure(b2cEy3Adr);
			 * 
			 * Gstr2PRSummarySectionDto b2cEy3Acr = new
			 * Gstr2PRSummarySectionDto(); b2cEy3Acr.setTable(table);
			 * b2cEy3Acr.setIndex(6); b2cEy3Acr.setDocType(rcr); b2cEy3Acr =
			 * gstr1DefaultStructure(b2cEy3Acr);
			 */

			defaultB2CEY.add(b2bEyTotal);
			/*
			 * defaultB2CEY.add(b2cEy3AInv); defaultB2CEY.add(b2cEy3Adr);
			 * defaultB2CEY.add(b2cEy3Acr);
			 */
		}
		if ("14-RCMADV".equalsIgnoreCase(table)) {
			Gstr2PRSummarySectionDto b2bEyTotal = new Gstr2PRSummarySectionDto();
			b2bEyTotal.setTable(table);
			b2bEyTotal.setIndex(6);
			b2bEyTotal.setDocType(total);
			b2bEyTotal = gstr1DefaultStructure(b2bEyTotal);

			Gstr2PRSummarySectionDto b2cEy3AInv = new Gstr2PRSummarySectionDto();
			b2cEy3AInv.setTable(table);
			b2cEy3AInv.setIndex(12);
			b2cEy3AInv.setDocType(dr);
			b2cEy3AInv = gstr1DefaultStructure(b2cEy3AInv);

			Gstr2PRSummarySectionDto b2cEy3Acr = new Gstr2PRSummarySectionDto();
			b2cEy3Acr.setTable(table);
			b2cEy3Acr.setIndex(13);
			b2cEy3Acr.setDocType(cr);
			b2cEy3Acr = gstr1DefaultStructure(b2cEy3Acr);

			defaultB2CEY.add(b2bEyTotal);
			defaultB2CEY.add(b2cEy3AInv);
			defaultB2CEY.add(b2cEy3Acr);
		}

		return defaultB2CEY;
	}

	public List<Gstr2PRSummarySectionDto> getaspEyList(
			List<Gstr2PRSummarySectionDto> defaultEYList,
			List<Gstr2PRSummarySectionDto> eySummaryList) {

		List<Gstr2PRSummarySectionDto> viewSummaryTotalFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryInvFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryRnvFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryDrFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryRdrFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryCrFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryRcrFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryIMPSFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryIMPGFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryIMPGSFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryIMPSAFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryIMPGAFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryIMPGSAFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryADVFiltered = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> viewSummaryADJFiltered = new ArrayList<>();

			
		if(eySummaryList != null && eySummaryList.size()>0){
			viewSummaryTotalFiltered = eySummaryList
				.stream().filter(p -> "TOTAL".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryInvFiltered = eySummaryList
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryRnvFiltered = eySummaryList
				.stream().filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryDrFiltered = eySummaryList
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryRdrFiltered = eySummaryList
				.stream().filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryCrFiltered = eySummaryList
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryRcrFiltered = eySummaryList
				.stream().filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryIMPSFiltered = eySummaryList
				.stream().filter(p -> "IMPS".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryIMPGFiltered = eySummaryList
				.stream().filter(p -> "IMPG".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryIMPGSFiltered = eySummaryList
				.stream().filter(p -> "IMPGS".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryIMPSAFiltered = eySummaryList
				.stream().filter(p -> "IMPSA".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryIMPGAFiltered = eySummaryList
				.stream().filter(p -> "IMPGA".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		 viewSummaryIMPGSAFiltered = eySummaryList
				.stream().filter(p -> "IMPGSA".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		 
		 viewSummaryADVFiltered = eySummaryList
					.stream().filter(p -> "ADV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());
		 
		 viewSummaryADJFiltered = eySummaryList
					.stream().filter(p -> "ADJ".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

		}
		if (viewSummaryIMPSFiltered != null
				& viewSummaryIMPSFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "IMPS".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryIMPSFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(0);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryIMPGFiltered != null
				& viewSummaryIMPGFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "IMPG".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryIMPGFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(1);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryIMPGSFiltered != null
				& viewSummaryIMPGSFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "IMPGS".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryIMPGSFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(2);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryIMPSAFiltered != null
				& viewSummaryIMPSAFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "IMPSA".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryIMPSAFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(3);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryIMPGAFiltered != null
				& viewSummaryIMPGAFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "IMPGA".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryIMPGAFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(4);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryIMPGSAFiltered != null
				& viewSummaryIMPGSAFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "IMPGSA".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryIMPGSAFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(5);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}

		if (viewSummaryTotalFiltered != null
				& viewSummaryTotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "TOTAL".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryTotalFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(6);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryInvFiltered != null
				& viewSummaryInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryInvFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(7);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
			if (viewSummaryDrFiltered != null & viewSummaryDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryDrFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(8);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
			if (viewSummaryCrFiltered != null & viewSummaryCrFiltered.size() > 0) {
				// then filter default List for 4A
				List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
						.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
						.collect(Collectors.toList());

				// If the default filtered list is not null
				defaultB2baFiltered.forEach(defaultB2ba -> {
					// then remove it from List
					defaultEYList.remove(defaultB2ba);
				});

				viewSummaryCrFiltered.forEach(viewB2ba -> {
					Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
					summaryResp.setTable(viewB2ba.getTable());
					summaryResp.setIndex(9);
					summaryResp.setCount(viewB2ba.getCount());
					summaryResp.setDocType(viewB2ba.getDocType());
					summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
					summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
					summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
					summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
					summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
					summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
					summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
					summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
					summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
					summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
					summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
					summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
					defaultEYList.add(summaryResp);
				});
			}

		if (viewSummaryRnvFiltered != null
				& viewSummaryRnvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryRnvFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(10);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryRdrFiltered != null
				& viewSummaryRdrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryRdrFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(11);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}

		if (viewSummaryRcrFiltered != null
				& viewSummaryRcrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryRcrFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(12);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryADVFiltered != null
				& viewSummaryADVFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream()
					.filter(p -> "ADV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryADVFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(12);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}
		if (viewSummaryADJFiltered != null & viewSummaryADJFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr2PRSummarySectionDto> defaultB2baFiltered = defaultEYList
					.stream().filter(p -> "ADJ".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				defaultEYList.remove(defaultB2ba);
			});

			viewSummaryADJFiltered.forEach(viewB2ba -> {
				Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();
				summaryResp.setTable(viewB2ba.getTable());
				summaryResp.setIndex(13);
				summaryResp.setCount(viewB2ba.getCount());
				summaryResp.setDocType(viewB2ba.getDocType());
				summaryResp.setInvoiceValue(viewB2ba.getInvoiceValue());
				summaryResp.setTaxableValue(viewB2ba.getTaxableValue());
				summaryResp.setTaxPayable(viewB2ba.getTaxPayable());
				summaryResp.setTaxPayableCess(viewB2ba.getTaxPayableCess());
				summaryResp.setTaxPayableCgst(viewB2ba.getTaxPayableCgst());
				summaryResp.setTaxPayableIgst(viewB2ba.getTaxPayableIgst());
				summaryResp.setTaxPayableSgst(viewB2ba.getTaxPayableSgst());
				summaryResp.setCrEligibleTotal(viewB2ba.getCrEligibleTotal());
				summaryResp.setCrEligibleCess(viewB2ba.getCrEligibleCess());
				summaryResp.setCrEligibleCgst(viewB2ba.getCrEligibleCgst());
				summaryResp.setCrEligibleIgst(viewB2ba.getCrEligibleIgst());
				summaryResp.setCrEligibleSgst(viewB2ba.getCrEligibleSgst());
				defaultEYList.add(summaryResp);
			});
		}

		
		if(defaultEYList.size()>1){
			Collections.sort(defaultEYList, new Comparator<Gstr2PRSummarySectionDto>() {
			@Override
			public int compare(Gstr2PRSummarySectionDto respDto1,
					Gstr2PRSummarySectionDto respDto2) {
				return respDto1.getIndex()
						.compareTo(respDto2.getIndex());
			}
		}); 
		}
      		return defaultEYList;
	}

	public Gstr2PRSummarySectionDto gstr1DefaultStructure(
			Gstr2PRSummarySectionDto respDto) {

		respDto.setCount(0);
		respDto.setInvoiceValue(BigDecimal.ZERO);
		respDto.setTaxableValue(BigDecimal.ZERO);
		respDto.setTaxPayable(BigDecimal.ZERO);
		respDto.setTaxPayableIgst(BigDecimal.ZERO);
		respDto.setTaxPayableSgst(BigDecimal.ZERO);
		respDto.setTaxPayableCgst(BigDecimal.ZERO);
		respDto.setTaxPayableCess(BigDecimal.ZERO);
		respDto.setCrEligibleTotal(BigDecimal.ZERO);
		respDto.setCrEligibleIgst(BigDecimal.ZERO);
		respDto.setCrEligibleSgst(BigDecimal.ZERO);
		respDto.setCrEligibleCgst(BigDecimal.ZERO);
		respDto.setCrEligibleCess(BigDecimal.ZERO);

		return respDto;

	}

}
