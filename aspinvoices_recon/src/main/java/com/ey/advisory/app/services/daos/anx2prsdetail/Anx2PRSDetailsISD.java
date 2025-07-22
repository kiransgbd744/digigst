package com.ey.advisory.app.services.daos.anx2prsdetail;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTConstants;

@Component("Anx2PRSDetailsISD")
public class Anx2PRSDetailsISD implements Anx2PRSDetailsSection {

	@Override
	public Anx2PRSDetailHeaderDto getAnx2PRSummaryDetail(
			List<Anx2PRSDetailResponseDto> entityResponse,
			List<String> docType) {

		List<Object> sectionList = new ArrayList<>();
		Anx2PRSDetailHeaderDto prsDetail = new Anx2PRSDetailHeaderDto();

		Anx2PRSDetailResponseDto critem = new Anx2PRSDetailResponseDto();
		Anx2PRSDetailResponseDto inv = new Anx2PRSDetailResponseDto();
		Anx2PRSDetailResponseDto rcritem = new Anx2PRSDetailResponseDto();
		Anx2PRSDetailResponseDto rnvitem = new Anx2PRSDetailResponseDto();

		Integer totCount = 0;
		BigDecimal invValue = BigDecimal.ZERO;
		BigDecimal tottaxableVal = BigDecimal.ZERO;
		BigDecimal totalTaxPayable = BigDecimal.ZERO;
		BigDecimal totIgst = BigDecimal.ZERO;
		BigDecimal totCgst = BigDecimal.ZERO;
		BigDecimal totSgst = BigDecimal.ZERO;
		BigDecimal totCess = BigDecimal.ZERO;
		BigDecimal totCreditEligible = BigDecimal.ZERO;
		BigDecimal totCeIgst = BigDecimal.ZERO;
		BigDecimal totCeCgst = BigDecimal.ZERO;
		BigDecimal totCeSgst = BigDecimal.ZERO;
		BigDecimal totCeCess = BigDecimal.ZERO;

		List<Anx2PRSDetailResponseDto> b2b = new ArrayList<>();
		if (entityResponse.size() > 0) {
			b2b = entityResponse.stream().filter(
					p -> GSTConstants.ISD.equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

		}

		if (entityResponse.size() <= 0 || b2b.size() <= 0) {
			
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.INV))) {
				inv.setTable(GSTConstants.INVOICE_NAME);
				inv.setCount(0);
				inv.setInvValue(BigDecimal.ZERO);
				inv.setTaxableValue(BigDecimal.ZERO);
				inv.setTotalTaxPayable(BigDecimal.ZERO);
				inv.setIGST(BigDecimal.ZERO);
				inv.setCGST(BigDecimal.ZERO);
				inv.setSGST(BigDecimal.ZERO);
				inv.setCess(BigDecimal.ZERO);
				inv.setTotalCreditEligible(BigDecimal.ZERO);
				inv.setCeIGST(BigDecimal.ZERO);
				inv.setCeCGST(BigDecimal.ZERO);
				inv.setCeSGST(BigDecimal.ZERO);
				inv.setCeCess(BigDecimal.ZERO);
				sectionList.add(inv);
			}
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.CR))) {
				critem.setTable(GSTConstants.CREDIT_NOTE);
				critem.setCount(0);
				critem.setInvValue(BigDecimal.ZERO);
				critem.setTaxableValue(BigDecimal.ZERO);
				critem.setTotalTaxPayable(BigDecimal.ZERO);
				critem.setIGST(BigDecimal.ZERO);
				critem.setCGST(BigDecimal.ZERO);
				critem.setSGST(BigDecimal.ZERO);
				critem.setCess(BigDecimal.ZERO);
				critem.setTotalCreditEligible(BigDecimal.ZERO);
				critem.setCeIGST(BigDecimal.ZERO);
				critem.setCeCGST(BigDecimal.ZERO);
				critem.setCeSGST(BigDecimal.ZERO);
				critem.setCeCess(BigDecimal.ZERO);
				sectionList.add(critem);
			}

			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.RNV))) {
				rnvitem.setTable(GSTConstants.INVOICE_A);
				rnvitem.setCount(0);
				rnvitem.setInvValue(BigDecimal.ZERO);
				rnvitem.setTaxableValue(BigDecimal.ZERO);
				rnvitem.setTotalTaxPayable(BigDecimal.ZERO);
				rnvitem.setIGST(BigDecimal.ZERO);
				rnvitem.setCGST(BigDecimal.ZERO);
				rnvitem.setSGST(BigDecimal.ZERO);
				rnvitem.setCess(BigDecimal.ZERO);
				rnvitem.setTotalCreditEligible(BigDecimal.ZERO);
				rnvitem.setCeIGST(BigDecimal.ZERO);
				rnvitem.setCeCGST(BigDecimal.ZERO);
				rnvitem.setCeSGST(BigDecimal.ZERO);
				rnvitem.setCeCess(BigDecimal.ZERO);
				sectionList.add(rnvitem);
			}
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.RCR))) {
				rcritem.setTable(GSTConstants.CREDIT_NOTE_A);
				rcritem.setCount(0);
				rcritem.setInvValue(BigDecimal.ZERO);
				rcritem.setTaxableValue(BigDecimal.ZERO);
				rcritem.setTotalTaxPayable(BigDecimal.ZERO);
				rcritem.setIGST(BigDecimal.ZERO);
				rcritem.setCGST(BigDecimal.ZERO);
				rcritem.setSGST(BigDecimal.ZERO);
				rcritem.setCess(BigDecimal.ZERO);
				rcritem.setTotalCreditEligible(BigDecimal.ZERO);
				rcritem.setCeIGST(BigDecimal.ZERO);
				rcritem.setCeCGST(BigDecimal.ZERO);
				rcritem.setCeSGST(BigDecimal.ZERO);
				rcritem.setCeCess(BigDecimal.ZERO);
				sectionList.add(rcritem);
			}
		}

		if (entityResponse.size() > 0 || b2b.size() > 0) {
			
			List<Anx2PRSDetailResponseDto> invList = b2b.stream().filter(
					p -> GSTConstants.INV.equalsIgnoreCase(p.getInvType()))
					.collect(Collectors.toList());

			int count = 0;
			BigDecimal invVal = BigDecimal.ZERO;
			BigDecimal invtaxableValue = BigDecimal.ZERO;
			BigDecimal taxPayable = BigDecimal.ZERO;
			BigDecimal igst = BigDecimal.ZERO;
			BigDecimal cgst = BigDecimal.ZERO;
			BigDecimal sgst = BigDecimal.ZERO;
			BigDecimal cess = BigDecimal.ZERO;
			BigDecimal invCreditEligible = BigDecimal.ZERO;
			BigDecimal ceigst = BigDecimal.ZERO;
			BigDecimal cecgst = BigDecimal.ZERO;
			BigDecimal cesgst = BigDecimal.ZERO;
			BigDecimal cecess = BigDecimal.ZERO;

			if (invList != null && invList.size() > 0) {

				for (Anx2PRSDetailResponseDto invdata : invList) {
					count = count + invdata.getCount();
					invVal = invVal.add(invdata.getInvValue());
					invtaxableValue = invtaxableValue
							.add(invdata.getTaxableValue());
					taxPayable = taxPayable.add(invdata.getTotalTaxPayable());
					igst = igst.add(invdata.getIGST());
					cgst = cgst.add(invdata.getCGST());
					sgst = sgst.add(invdata.getSGST());
					cess = cess.add(invdata.getCess());
					invCreditEligible = invCreditEligible
							.add(invdata.getTotalCreditEligible());
					ceigst = ceigst.add(invdata.getCeIGST());
					cecgst = cecgst.add(invdata.getCeCGST());
					cesgst = cesgst.add(invdata.getCeSGST());
					cecess = cecess.add(invdata.getCeCess());
				}
			}
			inv.setTable(GSTConstants.INVOICE_NAME);
			inv.setCount(count);
			inv.setInvValue(invVal);
			inv.setTaxableValue(invtaxableValue);
			inv.setTotalTaxPayable(taxPayable);
			inv.setIGST(igst);
			inv.setCGST(cgst);
			inv.setSGST(sgst);
			inv.setCess(cess);
			inv.setTotalCreditEligible(invCreditEligible);
			inv.setCeIGST(ceigst);
			inv.setCeCGST(cecgst);
			inv.setCeSGST(cesgst);
			inv.setCeCess(cecess);
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.INV))) {
				sectionList.add(inv);
			}

			List<Anx2PRSDetailResponseDto> crList = b2b.stream().filter(
					p -> GSTConstants.CR.equalsIgnoreCase(p.getInvType()))
					.collect(Collectors.toList());

			int crcount = 0;
			BigDecimal crinvVal = BigDecimal.ZERO;
			BigDecimal crinvtaxableValue = BigDecimal.ZERO;
			BigDecimal crtaxPayable = BigDecimal.ZERO;
			BigDecimal crigst = BigDecimal.ZERO;
			BigDecimal crcgst = BigDecimal.ZERO;
			BigDecimal crsgst = BigDecimal.ZERO;
			BigDecimal crcess = BigDecimal.ZERO;
			BigDecimal crinvCreditEligible = BigDecimal.ZERO;
			BigDecimal crceigst = BigDecimal.ZERO;
			BigDecimal crcecgst = BigDecimal.ZERO;
			BigDecimal crcesgst = BigDecimal.ZERO;
			BigDecimal crcecess = BigDecimal.ZERO;

			if (crList != null && crList.size() > 0) {

				for (Anx2PRSDetailResponseDto crdata : crList) {
					crcount = crcount + crdata.getCount();
					crinvVal = crinvVal.add(crdata.getInvValue());
					crinvtaxableValue = crinvtaxableValue
							.add(crdata.getTaxableValue());
					crtaxPayable = crtaxPayable
							.add(crdata.getTotalTaxPayable());
					crigst = crigst.add(crdata.getIGST());
					crcgst = crcgst.add(crdata.getCGST());
					crsgst = crsgst.add(crdata.getSGST());
					crcess = crcess.add(crdata.getCess());
					crinvCreditEligible = crinvCreditEligible
							.add(crdata.getTotalCreditEligible());
					crceigst = crceigst.add(crdata.getCeIGST());
					crcecgst = crcecgst.add(crdata.getCeCGST());
					crcesgst = crcesgst.add(crdata.getCeSGST());
					crcecess = crcecess.add(crdata.getCeCess());
				}
			}
			critem.setTable(GSTConstants.CREDIT_NOTE);
			critem.setCount(crcount);
			critem.setInvValue(crinvVal);
			critem.setTaxableValue(crinvtaxableValue);
			critem.setTotalTaxPayable(crtaxPayable);
			critem.setIGST(crigst);
			critem.setCGST(crcgst);
			critem.setSGST(crsgst);
			critem.setCess(crcess);
			critem.setTotalCreditEligible(crinvCreditEligible);
			critem.setCeIGST(crceigst);
			critem.setCeCGST(crcecgst);
			critem.setCeSGST(crcesgst);
			critem.setCeCess(crcecess);
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.CR))) {
				sectionList.add(critem);
			}

			List<Anx2PRSDetailResponseDto> rnvList = b2b.stream().filter(
					p -> GSTConstants.RNV.equalsIgnoreCase(p.getInvType()))
					.collect(Collectors.toList());

			int rnvcount = 0;
			BigDecimal rnvinvVal = BigDecimal.ZERO;
			BigDecimal rnvinvtaxableValue = BigDecimal.ZERO;
			BigDecimal rnvtaxPayable = BigDecimal.ZERO;
			BigDecimal rnvigst = BigDecimal.ZERO;
			BigDecimal rnvcgst = BigDecimal.ZERO;
			BigDecimal rnvsgst = BigDecimal.ZERO;
			BigDecimal rnvcess = BigDecimal.ZERO;
			BigDecimal rnvinvCreditEligible = BigDecimal.ZERO;
			BigDecimal rnvceigst = BigDecimal.ZERO;
			BigDecimal rnvcecgst = BigDecimal.ZERO;
			BigDecimal rnvcesgst = BigDecimal.ZERO;
			BigDecimal rnvcecess = BigDecimal.ZERO;

			if (rnvList != null && rnvList.size() > 0) {

				for (Anx2PRSDetailResponseDto rnvdata : rnvList) {
					rnvcount = rnvcount + rnvdata.getCount();
					rnvinvVal = rnvinvVal.add(rnvdata.getInvValue());
					rnvinvtaxableValue = rnvinvtaxableValue
							.add(rnvdata.getTaxableValue());
					rnvtaxPayable = rnvtaxPayable
							.add(rnvdata.getTotalTaxPayable());
					rnvigst = rnvigst.add(rnvdata.getIGST());
					rnvcgst = rnvcgst.add(rnvdata.getCGST());
					rnvsgst = rnvsgst.add(rnvdata.getSGST());
					rnvcess = rnvcess.add(rnvdata.getCess());
					rnvinvCreditEligible = rnvinvCreditEligible
							.add(rnvdata.getTotalCreditEligible());
					rnvceigst = rnvceigst.add(rnvdata.getCeIGST());
					rnvcecgst = rnvcecgst.add(rnvdata.getCeCGST());
					rnvcesgst = rnvcesgst.add(rnvdata.getCeSGST());
					rnvcecess = rnvcecess.add(rnvdata.getCeCess());
				}
			}
			rnvitem.setTable(GSTConstants.INVOICE_A);
			rnvitem.setCount(rnvcount);
			rnvitem.setInvValue(rnvinvVal);
			rnvitem.setTaxableValue(rnvinvtaxableValue);
			rnvitem.setTotalTaxPayable(rnvtaxPayable);
			rnvitem.setIGST(rnvigst);
			rnvitem.setCGST(rnvcgst);
			rnvitem.setSGST(rnvsgst);
			rnvitem.setCess(rnvcess);
			rnvitem.setTotalCreditEligible(rnvinvCreditEligible);
			rnvitem.setCeIGST(rnvceigst);
			rnvitem.setCeCGST(rnvcecgst);
			rnvitem.setCeSGST(rnvcesgst);
			rnvitem.setCeCess(rnvcecess);
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.RNV))) {
				sectionList.add(rnvitem);
			}
			List<Anx2PRSDetailResponseDto> rcrList = b2b.stream().filter(
					p -> GSTConstants.RCR.equalsIgnoreCase(p.getInvType()))
					.collect(Collectors.toList());

			int rcrcount = 0;
			BigDecimal rcrinvVal = BigDecimal.ZERO;
			BigDecimal rcrinvtaxableValue = BigDecimal.ZERO;
			BigDecimal rcrtaxPayable = BigDecimal.ZERO;
			BigDecimal rcrigst = BigDecimal.ZERO;
			BigDecimal rcrcgst = BigDecimal.ZERO;
			BigDecimal rcrsgst = BigDecimal.ZERO;
			BigDecimal rcrcess = BigDecimal.ZERO;
			BigDecimal rcrinvCreditEligible = BigDecimal.ZERO;
			BigDecimal rcrceigst = BigDecimal.ZERO;
			BigDecimal rcrcecgst = BigDecimal.ZERO;
			BigDecimal rcrcesgst = BigDecimal.ZERO;
			BigDecimal rcrcecess = BigDecimal.ZERO;

			if (rcrList != null && rcrList.size() > 0) {

				for (Anx2PRSDetailResponseDto rcrdata : rcrList) {
					rcrcount = rcrcount + rcrdata.getCount();
					rcrinvVal = rcrinvVal.add(rcrdata.getInvValue());
					rcrinvtaxableValue = rcrinvtaxableValue
							.add(rcrdata.getTaxableValue());
					rcrtaxPayable = rcrtaxPayable
							.add(rcrdata.getTotalTaxPayable());
					rcrigst = rcrigst.add(rcrdata.getIGST());
					rcrcgst = rcrcgst.add(rcrdata.getCGST());
					rcrsgst = rcrsgst.add(rcrdata.getSGST());
					rcrcess = rcrcess.add(rcrdata.getCess());
					rcrinvCreditEligible = rcrinvCreditEligible
							.add(rcrdata.getTotalCreditEligible());
					rcrceigst = rcrceigst.add(rcrdata.getCeIGST());
					rcrcecgst = rcrcecgst.add(rcrdata.getCeCGST());
					rcrcesgst = rcrcesgst.add(rcrdata.getCeSGST());
					rcrcecess = rcrcecess.add(rcrdata.getCeCess());
				}
			}
			rcritem.setTable(GSTConstants.CREDIT_NOTE_A);
			rcritem.setCount(rcrcount);
			rcritem.setInvValue(rcrinvVal);
			rcritem.setTaxableValue(rcrinvtaxableValue);
			rcritem.setTotalTaxPayable(rcrtaxPayable);
			rcritem.setIGST(rcrigst);
			rcritem.setCGST(rcrcgst);
			rcritem.setSGST(rcrsgst);
			rcritem.setCess(rcrcess);
			rcritem.setTotalCreditEligible(rcrinvCreditEligible);
			rcritem.setCeIGST(rcrceigst);
			rcritem.setCeCGST(rcrcecgst);
			rcritem.setCeSGST(rcrcesgst);
			rcritem.setCeCess(rcrcecess);
			if (docType != null && docType.contains(trimAndConvToUpperCase(GSTConstants.RCR))) {
				sectionList.add(rcritem);
			}
		}
		totCount = inv.getCount() + critem.getCount() + rnvitem.getCount()
				+ rcritem.getCount();
		invValue = inv.getInvValue().subtract(critem.getInvValue())
				.add(rnvitem.getInvValue()).subtract(rcritem.getInvValue());
		tottaxableVal = inv.getTaxableValue().subtract(critem.getTaxableValue())
				.add(rnvitem.getTaxableValue())
				.subtract(rcritem.getTaxableValue());
		totalTaxPayable = inv.getTotalTaxPayable()
				.subtract(critem.getTotalTaxPayable())
				.add(rnvitem.getTotalTaxPayable())
				.subtract(rcritem.getTotalTaxPayable());
		totIgst = inv.getIGST().subtract(critem.getIGST())
				.add(rnvitem.getIGST()).subtract(rcritem.getIGST());
		totCgst = inv.getCGST().subtract(critem.getCGST())
				.add(rnvitem.getCGST()).subtract(rcritem.getCGST());
		totSgst = inv.getSGST().subtract(critem.getSGST())
				.add(rnvitem.getSGST()).subtract(rcritem.getSGST());
		totCess = inv.getCess().subtract(critem.getCess())
				.add(rnvitem.getCess()).subtract(rcritem.getCess());
		totCreditEligible = inv.getTotalCreditEligible()
				.subtract(critem.getTotalCreditEligible())
				.add(rnvitem.getTotalCreditEligible())
				.subtract(rcritem.getTotalCreditEligible());
		totCeIgst = inv.getCeIGST().subtract(critem.getCeIGST())
				.add(rnvitem.getCeIGST()).subtract(rcritem.getCeIGST());
		totCeCgst = inv.getCeCGST().subtract(critem.getCeCGST())
				.add(rnvitem.getCeCGST()).subtract(rcritem.getCeCGST());
		totCeSgst = inv.getCeSGST().subtract(critem.getCeSGST())
				.add(rnvitem.getCeSGST()).subtract(rcritem.getCeSGST());
		totCeCess = inv.getCeCess().subtract(critem.getCeCess())
				.add(rnvitem.getCeCess()).subtract(rcritem.getCeCess());

		prsDetail.setTable("5-ISD");
		prsDetail.setCount(totCount);
		prsDetail.setInvValue(invValue);
		prsDetail.setTaxableValue(tottaxableVal);
		prsDetail.setTotalTaxPayable(totalTaxPayable);
		prsDetail.setIGST(totIgst);
		prsDetail.setCGST(totCgst);
		prsDetail.setSGST(totSgst);
		prsDetail.setCess(totCess);
		prsDetail.setTotalCreditEligible(totCreditEligible);
		prsDetail.setCeIGST(totCeIgst);
		prsDetail.setCeCGST(totCeCgst);
		prsDetail.setCeSGST(totCeSgst);
		prsDetail.setCeCess(totCeCess);
		prsDetail.setLineItems(sectionList);

		return prsDetail;

	}
}
