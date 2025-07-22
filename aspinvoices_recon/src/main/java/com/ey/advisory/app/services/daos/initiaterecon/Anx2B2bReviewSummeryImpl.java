package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx2B2bReviewSummeryImpl")
public class Anx2B2bReviewSummeryImpl implements Anx2reviewSummery {

	@Override
	public Anx2PrReviewSummeryHeaderDto getreviewSummery(
			List<Anx2PRReviewSummeryResponseDto> entityResponse,
			List<String> docType) {

		Anx2PrReviewSummeryHeaderDto reviewSummeryfile = new Anx2PrReviewSummeryHeaderDto();
		List<Anx2PRReviewSummeryResponseDto> sectionFileList = new ArrayList<>();
		Anx2PRReviewSummeryResponseDto critem = new Anx2PRReviewSummeryResponseDto();
		Anx2PRReviewSummeryResponseDto dritem = new Anx2PRReviewSummeryResponseDto();
		Anx2PRReviewSummeryResponseDto inv = new Anx2PRReviewSummeryResponseDto();

		int a2totalRec_Count = 0;
		BigDecimal Invoicevalue = new BigDecimal("0.0");
		BigDecimal a2taxablevalue = new BigDecimal("0.0");
		BigDecimal a2totigst = new BigDecimal("0.0");
		BigDecimal a2totcgst = new BigDecimal("0.0");
		BigDecimal a2totsgst = new BigDecimal("0.0");
		BigDecimal a2totcess = new BigDecimal("0.0");
		BigDecimal totalCreditEligible = new BigDecimal("0.0");
		BigDecimal totalTaxPayable = new BigDecimal("0.0");
		// BigDecimal prtaxablevalue = new BigDecimal("0.0");
		BigDecimal prtotigst = new BigDecimal("0.0");
		BigDecimal prtotcgst = new BigDecimal("0.0");
		BigDecimal prtotsgst = new BigDecimal("0.0");
		BigDecimal prtotcess = new BigDecimal("0.0");

		
		List<Anx2PRReviewSummeryResponseDto> b2b = new ArrayList<>();
		if (entityResponse.size() > 0) {
			b2b = entityResponse.stream()
					.filter(p -> "B2B".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

		}
		if (entityResponse.size() <= 0 || b2b.size() <= 0) {

			
				critem.setTable("CR");
				critem.setCount(0);
				critem.setInvValue(new BigDecimal("0.0"));
				critem.setTaxableValue(new BigDecimal("0.0"));
				critem.setTotalTaxPayable(new BigDecimal("0.0"));
				critem.setTpIGST(new BigDecimal("0.0"));
				critem.setTpCGST(new BigDecimal("0.0"));
				critem.setTpCess(new BigDecimal("0.0"));
				critem.setTpSGST(new BigDecimal("0.0"));
				critem.setCeIGST(new BigDecimal("0.0"));
				critem.setCeSGST(new BigDecimal("0.0"));
				critem.setCeCGST(new BigDecimal("0.0"));
				critem.setCeCess(new BigDecimal("0.0"));
				critem.setTotalCreditEligible(new BigDecimal("0.0"));
					sectionFileList.add(critem);
				
			

			
				dritem.setTable("DR");
				dritem.setCount(0);
				dritem.setInvValue(new BigDecimal("0.0"));
				dritem.setTaxableValue(new BigDecimal("0.0"));
				dritem.setTotalTaxPayable(new BigDecimal("0.0"));
				dritem.setTpIGST(new BigDecimal("0.0"));
				dritem.setTpCGST(new BigDecimal("0.0"));
				dritem.setTpCess(new BigDecimal("0.0"));
				dritem.setTpSGST(new BigDecimal("0.0"));
				dritem.setCeIGST(new BigDecimal("0.0"));
				dritem.setCeSGST(new BigDecimal("0.0"));
				dritem.setCeCGST(new BigDecimal("0.0"));
				dritem.setCeCess(new BigDecimal("0.0"));
				dritem.setTotalCreditEligible(new BigDecimal("0.0"));
					sectionFileList.add(dritem);
				
			

			
				inv.setTable("INV");
				inv.setCount(0);
				inv.setInvValue(new BigDecimal("0.0"));
				inv.setTaxableValue(new BigDecimal("0.0"));
				inv.setTotalTaxPayable(new BigDecimal("0.0"));
				inv.setTpIGST(new BigDecimal("0.0"));
				inv.setTpCGST(new BigDecimal("0.0"));
				inv.setTpCess(new BigDecimal("0.0"));
				inv.setTpSGST(new BigDecimal("0.0"));
				inv.setCeIGST(new BigDecimal("0.0"));
				inv.setCeSGST(new BigDecimal("0.0"));
				inv.setCeCGST(new BigDecimal("0.0"));
				inv.setCeCess(new BigDecimal("0.0"));
				inv.setTotalCreditEligible(new BigDecimal("0.0"));
					sectionFileList.add(inv);
				
			

		}
		if (entityResponse.size() > 0 && b2b.size() > 0) {

			List<Anx2PRReviewSummeryResponseDto> invlist = b2b.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());
			int count = 0;
			BigDecimal invValue = new BigDecimal("0.0");
			BigDecimal invtaxableValue = new BigDecimal("0.0");
			BigDecimal TaxPayable = new BigDecimal("0.0");
			BigDecimal invtpigst = new BigDecimal("0.0");
			BigDecimal invtpcgst = new BigDecimal("0.0");
			BigDecimal invtpsgst = new BigDecimal("0.0");
			BigDecimal invtpcess = new BigDecimal("0.0");
			BigDecimal invCreditEligible = new BigDecimal("0.0");
			BigDecimal prigst = new BigDecimal("0.0");
			BigDecimal prcgst = new BigDecimal("0.0");
			BigDecimal prsgst = new BigDecimal("0.0");
			BigDecimal prcess = new BigDecimal("0.0");
			if (invlist != null && invlist.size() > 0) {

				for (Anx2PRReviewSummeryResponseDto invdata : invlist) {
					count = count + invdata.getCount();
					invValue = invValue.add(invdata.getInvValue());
					invtaxableValue = invtaxableValue
							.add(invdata.getTaxableValue());
					invtpigst = invtpigst.add(invdata.getTpIGST());
					TaxPayable = TaxPayable.add(invdata.getTotalTaxPayable());
					invtpcgst = invtpcgst.add(invdata.getTpCGST());
					invtpsgst = invtpsgst.add(invdata.getTpSGST());
					invtpcess = invtpcess.add(invdata.getTpCess());
					invCreditEligible = invCreditEligible
							.add(invdata.getTotalCreditEligible());
					prigst = prigst.add(invdata.getCeIGST());
					prcgst = prcgst.add(invdata.getCeCGST());
					prsgst = prsgst.add(invdata.getCeSGST());
					prcess = prcess.add(invdata.getTpCess());
				}
			}
			inv.setTable("INV");
			inv.setCount(count);
			inv.setInvValue(invValue);
			inv.setTaxableValue(invtaxableValue);
			inv.setTotalTaxPayable(TaxPayable);
			inv.setTpIGST(invtpigst);
			inv.setTpCGST(invtpcgst);
			inv.setTpSGST(invtpsgst);
			inv.setTpCess(invtpcess);
			inv.setCeIGST(prigst);
			inv.setCeSGST(prsgst);
			inv.setCeCGST(prcgst);
			inv.setCeCess(prcess);
			inv.setTotalCreditEligible(invCreditEligible);
				sectionFileList.add(inv);
			

			List<Anx2PRReviewSummeryResponseDto> crlist = b2b.stream()
					.filter(p -> "CR".equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			int crcount = 0;
			BigDecimal crinvValue = new BigDecimal("0.0");
			BigDecimal crinvtaxableValue = new BigDecimal("0.0");
			BigDecimal crTaxPayable = new BigDecimal("0.0");
			BigDecimal crinvtpigst = new BigDecimal("0.0");
			BigDecimal crinvtpcgst = new BigDecimal("0.0");
			BigDecimal crinvtpsgst = new BigDecimal("0.0");
			BigDecimal crinvtpcess = new BigDecimal("0.0");
			BigDecimal crinvCreditEligible = new BigDecimal("0.0");
			BigDecimal crprigst = new BigDecimal("0.0");
			BigDecimal crprcgst = new BigDecimal("0.0");
			BigDecimal crprsgst = new BigDecimal("0.0");
			BigDecimal crprcess = new BigDecimal("0.0");
			if (crlist != null && crlist.size() > 0) {
				for (Anx2PRReviewSummeryResponseDto invdata : crlist) {
					crcount = crcount + invdata.getCount();
					crinvValue = crinvValue.add(invdata.getInvValue());
					crinvtaxableValue = crinvtaxableValue
							.add(invdata.getTaxableValue());
					crinvtpigst = crinvtpigst.add(invdata.getTpIGST());
					crTaxPayable = crTaxPayable
							.add(invdata.getTotalTaxPayable());
					crinvtpcgst = crinvtpcgst.add(invdata.getTpCGST());
					crinvtpsgst = crinvtpsgst.add(invdata.getTpSGST());
					crinvtpcess = crinvtpcess.add(invdata.getTpCess());
					crinvCreditEligible = crinvCreditEligible
							.add(invdata.getTotalCreditEligible());
					crprigst = crprigst.add(invdata.getCeIGST());
					crprcgst = crprcgst.add(invdata.getCeCGST());
					crprsgst = crprsgst.add(invdata.getCeSGST());
					crprcess = crprcess.add(invdata.getTpCess());
				}
			}
			critem.setTable("CR");
			critem.setCount(crcount);
			critem.setInvValue(crinvValue);
			critem.setTaxableValue(crinvtaxableValue);
			critem.setTotalTaxPayable(crTaxPayable);
			critem.setTpIGST(crinvtpigst);
			critem.setTpCGST(crinvtpcgst);
			critem.setTpSGST(crinvtpsgst);
			critem.setTpCess(crinvtpcess);
			critem.setCeIGST(crprigst);
			critem.setCeSGST(crprsgst);
			critem.setCeCGST(crprcgst);
			critem.setCeCess(crprcess);
			critem.setTotalCreditEligible(crinvCreditEligible);
				sectionFileList.add(critem);
			

			List<Anx2PRReviewSummeryResponseDto> drlist = b2b.stream()
					.filter(p -> "DR".equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			int drcount = 0;
			BigDecimal drinvValue = new BigDecimal("0.0");
			BigDecimal drinvtaxableValue = new BigDecimal("0.0");
			BigDecimal drTaxPayable = new BigDecimal("0.0");
			BigDecimal drinvtpigst = new BigDecimal("0.0");
			BigDecimal drinvtpcgst = new BigDecimal("0.0");
			BigDecimal drinvtpsgst = new BigDecimal("0.0");
			BigDecimal drinvtpcess = new BigDecimal("0.0");
			BigDecimal drinvCreditEligible = new BigDecimal("0.0");
			BigDecimal drprigst = new BigDecimal("0.0");
			BigDecimal drprcgst = new BigDecimal("0.0");
			BigDecimal drprsgst = new BigDecimal("0.0");
			BigDecimal drprcess = new BigDecimal("0.0");
			if (drlist != null && drlist.size() > 0) {
				for (Anx2PRReviewSummeryResponseDto invdata : drlist) {
					drcount = drcount + invdata.getCount();
					drinvValue = drinvValue.add(invdata.getInvValue());
					drinvtaxableValue = drinvtaxableValue
							.add(invdata.getTaxableValue());
					drinvtpigst = drinvtpigst.add(invdata.getTpIGST());
					drTaxPayable = drTaxPayable
							.add(invdata.getTotalTaxPayable());
					drinvtpcgst = drinvtpcgst.add(invdata.getTpCGST());
					drinvtpsgst = drinvtpsgst.add(invdata.getTpSGST());
					drinvtpcess = drinvtpcess.add(invdata.getTpCess());
					drinvCreditEligible = drinvCreditEligible
							.add(invdata.getTotalCreditEligible());
					drprigst = drprigst.add(invdata.getCeIGST());
					drprcgst = drprcgst.add(invdata.getCeCGST());
					drprsgst = drprsgst.add(invdata.getCeSGST());
					drprcess = drprcess.add(invdata.getTpCess());
				}
			}
			dritem.setTable("DR");
			dritem.setCount(drcount);
			dritem.setInvValue(drinvValue);
			dritem.setTaxableValue(drinvtaxableValue);
			dritem.setTotalTaxPayable(drTaxPayable);
			dritem.setTpIGST(drinvtpigst);
			dritem.setTpCGST(drinvtpcgst);
			dritem.setTpSGST(drinvtpsgst);
			dritem.setTpCess(drinvtpcess);
			dritem.setCeIGST(drprigst);
			dritem.setCeSGST(drprsgst);
			dritem.setCeCGST(drprcgst);
			dritem.setCeCess(drprcess);
			dritem.setTotalCreditEligible(drinvCreditEligible);
				sectionFileList.add(dritem);
			

		}
		
		
		a2totalRec_Count = dritem.getCount() + inv.getCount() + critem.getCount();
		Invoicevalue = dritem.getInvValue().add(inv.getInvValue())
				.subtract(critem.getInvValue());
		a2taxablevalue = dritem.getTaxableValue().add(inv.getTaxableValue())
				.subtract(critem.getTaxableValue());
		totalTaxPayable = dritem.getTotalTaxPayable()
				.add(inv.getTotalTaxPayable())
				.subtract(critem.getTotalTaxPayable());
		a2totigst = dritem.getTpIGST().add(inv.getTpIGST())
				.subtract(critem.getTpIGST());
		a2totcgst = dritem.getTpCGST().add(inv.getTpCGST())
				.subtract(critem.getTpCGST());
		a2totsgst = dritem.getTpSGST().add(inv.getTpSGST())
				.subtract(critem.getTpSGST());
		a2totcess = dritem.getTpCess().add(inv.getTpCess())
				.subtract(critem.getTpCess());
		totalCreditEligible = dritem.getTotalCreditEligible()
				.add(inv.getTotalCreditEligible())
				.subtract(critem.getTotalCreditEligible());
		prtotigst = dritem.getCeIGST().add(inv.getCeIGST())
				.subtract(critem.getCeIGST());
		prtotcgst = dritem.getCeCGST().add(inv.getCeCGST())
				.subtract(critem.getCeCGST());
		prtotsgst = dritem.getCeSGST().add(inv.getCeSGST())
				.subtract(critem.getCeSGST());
		prtotcess = dritem.getCeCess().add(inv.getCeCess())
				.subtract(critem.getCeCess()); 
		
	

		reviewSummeryfile.setTable("3A-B2B");
		reviewSummeryfile.setCount(a2totalRec_Count);
		reviewSummeryfile.setInvValue(Invoicevalue);
		reviewSummeryfile.setTaxableValue(a2taxablevalue);
		reviewSummeryfile.setTotalTaxPayable(totalTaxPayable);
		reviewSummeryfile.setTpIGST(a2totigst);
		reviewSummeryfile.setTpCGST(a2totcgst);
		reviewSummeryfile.setTpSGST(a2totsgst);
		reviewSummeryfile.setTpCess(a2totcess);
		reviewSummeryfile.setTotalCreditEligible(totalCreditEligible);
		reviewSummeryfile.setCeIGST(prtotigst);
		reviewSummeryfile.setCeCGST(prtotcgst);
		reviewSummeryfile.setCeSGST(prtotsgst);
		reviewSummeryfile.setCeCess(prtotcess);
		reviewSummeryfile.setLineItems(sectionFileList);

		return reviewSummeryfile;
	}
}
