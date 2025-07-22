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
@Component("Anx2B2bReviewSummeryBigTableImpl")
public class Anx2B2bReviewSummeryBigTableImpl {

	
	public Anx2BigTableReviewSummeryHeaderDto getreviewSummery(
			List<Anx2BigTableResponseDto> entityResponse,
			List<String> docType) {

		Anx2BigTableReviewSummeryHeaderDto reviewSummeryfile = new Anx2BigTableReviewSummeryHeaderDto();
		List<Anx2BigTableResponseDto> sectionFileList = new ArrayList<>();
		Anx2BigTableResponseDto critem = new Anx2BigTableResponseDto();
		Anx2BigTableResponseDto dritem = new Anx2BigTableResponseDto();
		Anx2BigTableResponseDto inv = new Anx2BigTableResponseDto();
		Integer a2totalRec_Count = 0;
		BigDecimal Invoicevalue = new BigDecimal("0.0");
		BigDecimal a2taxablevalue = new BigDecimal("0.0");
		BigDecimal a2totigst = new BigDecimal("0.0");
		BigDecimal a2totcgst = new BigDecimal("0.0");
		BigDecimal a2totsgst = new BigDecimal("0.0");
		BigDecimal a2totcess = new BigDecimal("0.0");

		BigDecimal totalTaxPayable = new BigDecimal("0.0");

		if (entityResponse.size() <= 0) {

			critem.setTable("CR");
			critem.setCount(0);
			critem.setInvValue(new BigDecimal("0.0"));
			critem.setTaxableValue(new BigDecimal("0.0"));
			critem.setTotalTaxPayable(new BigDecimal("0.0"));
			critem.setTpIGST(new BigDecimal("0.0"));
			critem.setTpCGST(new BigDecimal("0.0"));
			critem.setTpCess(new BigDecimal("0.0"));
			critem.setTpSGST(new BigDecimal("0.0"));

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

			sectionFileList.add(inv);
			

		}
		if (entityResponse.size() > 0) {

			List<Anx2BigTableResponseDto> anxinv = entityResponse
					.stream().filter(p -> "INV".equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			int count = 0;
			BigDecimal invValue = new BigDecimal("0.0");
			BigDecimal invtaxableValue = new BigDecimal("0.0");
			BigDecimal TaxPayable = new BigDecimal("0.0");
			BigDecimal invtpigst = new BigDecimal("0.0");
			BigDecimal invtpcgst = new BigDecimal("0.0");
			BigDecimal invtpsgst = new BigDecimal("0.0");
			BigDecimal invtpcess = new BigDecimal("0.0");

			if (anxinv != null && anxinv.size() > 0) {

				for (Anx2BigTableResponseDto invdata : anxinv) {
					count = count + invdata.getCount();
					invValue = invValue.add(invdata.getInvValue());
					invtaxableValue = invtaxableValue
							.add(invdata.getTaxableValue());
					invtpigst = invtpigst.add(invdata.getTpIGST());
					TaxPayable = TaxPayable.add(invdata.getTotalTaxPayable());
					invtpcgst = invtpcgst.add(invdata.getTpCGST());
					invtpsgst = invtpsgst.add(invdata.getTpSGST());
					invtpcess = invtpcess.add(invdata.getTpCess());

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
			sectionFileList.add(inv);

			List<Anx2BigTableResponseDto> anxcr = entityResponse.stream()
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

			if (anxinv != null && anxinv.size() > 0) {

				for (Anx2BigTableResponseDto invdata : anxcr) {
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
			sectionFileList.add(critem);

			List<Anx2BigTableResponseDto> anxdr = entityResponse.stream()
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

			if (anxinv != null && anxinv.size() > 0) {

				for (Anx2BigTableResponseDto invdata : anxdr) {
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
			sectionFileList.add(dritem);
		}
		a2totalRec_Count = critem.getCount() + dritem.getCount()
		+ inv.getCount();
		
		Invoicevalue = inv.getInvValue().add(dritem.getInvValue())
				.subtract(critem.getInvValue());
		a2taxablevalue = inv.getTaxableValue().add(dritem.getTaxableValue())
				.subtract(critem.getTaxableValue());
		totalTaxPayable = inv.getTotalTaxPayable().add(dritem.getTotalTaxPayable())
				.subtract(critem.getTotalTaxPayable());
		a2totigst = inv.getTpIGST().add(dritem.getTpIGST())
				.subtract(critem.getTpIGST());
		a2totcgst = inv.getTpCGST().add(dritem.getTpCGST())
				.subtract(critem.getTpCGST());
		a2totsgst = inv.getTpSGST().add(dritem.getTpSGST())
				.subtract(critem.getTpSGST());
		a2totcess = inv.getTpCess()
				.add(dritem.getTpCess().subtract(critem.getTpCess()));
		

		reviewSummeryfile.setTable("3A-B2B");
		reviewSummeryfile.setCount(a2totalRec_Count);
		reviewSummeryfile.setInvValue(Invoicevalue);
		reviewSummeryfile.setTaxableValue(a2taxablevalue);
		reviewSummeryfile.setTotalTaxPayable(totalTaxPayable);
		reviewSummeryfile.setTpIGST(a2totigst);
		reviewSummeryfile.setTpCGST(a2totcgst);
		reviewSummeryfile.setTpSGST(a2totsgst);
		reviewSummeryfile.setTpCess(a2totcess);

		reviewSummeryfile.setLineItems(sectionFileList);

		return reviewSummeryfile;
	}
}
