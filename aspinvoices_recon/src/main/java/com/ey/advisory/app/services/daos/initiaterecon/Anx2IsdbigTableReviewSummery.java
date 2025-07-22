package com.ey.advisory.app.services.daos.initiaterecon;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx2IsdbigTableReviewSummery")
public class Anx2IsdbigTableReviewSummery implements Anx2reviewSummery {

	@Override
	public Anx2PrReviewSummeryHeaderDto getreviewSummery(
			List<Anx2PRReviewSummeryResponseDto> entityResponse,
			List<String> docType) {

		Anx2PrReviewSummeryHeaderDto reviewSummeryfile = new Anx2PrReviewSummeryHeaderDto();
		List<Anx2PRReviewSummeryResponseDto> sectionFileList = new ArrayList<>();
		Anx2PRReviewSummeryResponseDto critem = new Anx2PRReviewSummeryResponseDto();
		Anx2PRReviewSummeryResponseDto dritem = new Anx2PRReviewSummeryResponseDto();
		Anx2PRReviewSummeryResponseDto inv = new Anx2PRReviewSummeryResponseDto();

		Integer a2totalRec_Count = 0;
		BigDecimal Invoicevalue = new BigDecimal("0.0");
		BigDecimal a2taxablevalue = new BigDecimal("0.0");
		BigDecimal a2totigst = new BigDecimal("0.0");
		BigDecimal a2totcgst = new BigDecimal("0.0");
		BigDecimal a2totsgst = new BigDecimal("0.0");
		BigDecimal a2totcess = new BigDecimal("0.0");
		BigDecimal totalCreditEligible = new BigDecimal("0.0");
		BigDecimal totalTaxPayable = new BigDecimal("0.0");
	
		BigDecimal prtotigst = new BigDecimal("0.0");
		BigDecimal prtotcgst = new BigDecimal("0.0");
		BigDecimal prtotsgst = new BigDecimal("0.0");
		BigDecimal prtotcess = new BigDecimal("0.0");

		Anx2PRReviewSummeryResponseDto response = entityResponse.stream()                        
                .filter(x -> "ISD".equals(x.getTaxDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		if (entityResponse.size() <= 0 || response==null ) {

			if (docType.contains(trimAndConvToUpperCase("CR"))) {
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
				sectionFileList.add(critem);

			}

			if (docType.contains(trimAndConvToUpperCase("DR"))) {
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
				sectionFileList.add(dritem);

			}

			if (docType.contains(trimAndConvToUpperCase("INV"))) {
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
				sectionFileList.add(inv);

			}

		}
		if (entityResponse.size() > 0) {
			for (Anx2PRReviewSummeryResponseDto apires : entityResponse) {
				// invtype= apires.getDoctype();
				if (apires.getTaxDocType() != null
						&& !apires.getTaxDocType().isEmpty()) {
					if (apires.getTaxDocType().equalsIgnoreCase("ISD")) {
						if (apires.getTable() != null
								&& !apires.getTable().isEmpty()) {
							if (apires.getTable().equalsIgnoreCase("CR")
									&& docType.contains(
											trimAndConvToUpperCase("CR"))) {
								critem.setTable("CR");
								critem.setCount(apires.getCount());
								critem.setInvValue(apires.getInvValue());
								critem.setTaxableValue(
										apires.getTaxableValue());
								critem.setTotalTaxPayable(
										apires.getTotalTaxPayable());
								critem.setTpIGST(apires.getTpIGST());
								critem.setTpCGST(apires.getTpIGST());
								critem.setTpCess(apires.getTpCess());
								critem.setTpSGST(apires.getTpSGST());
								critem.setCeIGST(apires.getCeIGST());
								critem.setCeSGST(apires.getCeSGST());
								critem.setCeCGST(apires.getCeCGST());
								critem.setCeCess(apires.getCeCess());
								sectionFileList.add(critem);

							}
							if (!apires.getTable().equalsIgnoreCase("CR")
									&& docType.contains(
											trimAndConvToUpperCase("CR"))) {
								critem.setTable("CR");
								critem.setCount(0);
								critem.setInvValue(new BigDecimal("0.0"));
								critem.setTaxableValue(new BigDecimal("0.0"));
								critem.setTotalTaxPayable(
										new BigDecimal("0.0"));
								critem.setTpIGST(new BigDecimal("0.0"));
								critem.setTpCGST(new BigDecimal("0.0"));
								critem.setTpCess(new BigDecimal("0.0"));
								critem.setTpSGST(new BigDecimal("0.0"));
								critem.setCeIGST(new BigDecimal("0.0"));
								critem.setCeSGST(new BigDecimal("0.0"));
								critem.setCeCGST(new BigDecimal("0.0"));
								critem.setCeCess(new BigDecimal("0.0"));
								sectionFileList.add(critem);

							}
							if (apires.getTable().equalsIgnoreCase("DR")
									&& docType.contains(
											trimAndConvToUpperCase("DR"))) {
								dritem.setTable("DR");
								dritem.setCount(apires.getCount());
								dritem.setInvValue(apires.getInvValue());
								dritem.setTaxableValue(
										apires.getTaxableValue());
								dritem.setTotalTaxPayable(
										apires.getTotalTaxPayable());
								dritem.setTpIGST(apires.getTpIGST());
								dritem.setTpCGST(apires.getTpIGST());
								dritem.setTpCess(apires.getTpCess());
								dritem.setTpSGST(apires.getTpSGST());
								dritem.setCeIGST(apires.getCeIGST());
								dritem.setCeSGST(apires.getCeSGST());
								dritem.setCeCGST(apires.getCeCGST());
								dritem.setCeCess(apires.getCeCess());
								sectionFileList.add(dritem);

							}
							if ((!apires.getTable().equalsIgnoreCase("DR"))
									&& (docType.contains(
											trimAndConvToUpperCase("DR")))) {
								dritem.setTable("DR");
								dritem.setCount(0);
								dritem.setInvValue(new BigDecimal("0.0"));
								dritem.setTaxableValue(new BigDecimal("0.0"));
								dritem.setTotalTaxPayable(
										new BigDecimal("0.0"));
								dritem.setTpIGST(new BigDecimal("0.0"));
								dritem.setTpCGST(new BigDecimal("0.0"));
								dritem.setTpCess(new BigDecimal("0.0"));
								dritem.setTpSGST(new BigDecimal("0.0"));
								dritem.setCeIGST(new BigDecimal("0.0"));
								dritem.setCeSGST(new BigDecimal("0.0"));
								dritem.setCeCGST(new BigDecimal("0.0"));
								dritem.setCeCess(new BigDecimal("0.0"));
								sectionFileList.add(dritem);

							}

							if (apires.getTable().equalsIgnoreCase("INV")
									&& docType.contains(
											trimAndConvToUpperCase("INV"))) {
								inv.setTable("INV");
								inv.setCount(apires.getCount());
								inv.setInvValue(apires.getInvValue());
								inv.setTaxableValue(apires.getTaxableValue());
								inv.setTotalTaxPayable(
										apires.getTotalTaxPayable());
								inv.setTpIGST(apires.getTpIGST());
								inv.setTpCGST(apires.getTpIGST());
								inv.setTpCess(apires.getTpCess());
								inv.setTpSGST(apires.getTpSGST());
								inv.setCeIGST(apires.getCeIGST());
								inv.setCeSGST(apires.getCeSGST());
								inv.setCeCGST(apires.getCeCGST());
								inv.setCeCess(apires.getCeCess());
								sectionFileList.add(inv);

							}
							if (!apires.getTable().equalsIgnoreCase("INV")
									&& docType.contains(
											trimAndConvToUpperCase("INV"))) {
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
								sectionFileList.add(inv);

							}
						}
					}
				}
			}
		}
		a2totalRec_Count = critem.getCount() + dritem.getCount()
				+ inv.getCount();
		Invoicevalue = critem.getInvValue().add(dritem.getInvValue())
				.add(inv.getInvValue());
		a2taxablevalue = critem.getTaxableValue().add(dritem.getTaxableValue())
				.add(inv.getTaxableValue());
		totalTaxPayable = critem.getTotalTaxPayable()
				.add(dritem.getTotalTaxPayable()).add(inv.getTotalTaxPayable());
		a2totigst = critem.getTpIGST().add(dritem.getTpIGST())
				.add(inv.getTpIGST());
		a2totcgst = critem.getTpCGST().add(dritem.getTpCGST())
				.add(inv.getTpCGST());
		a2totsgst = critem.getTpSGST().add(dritem.getTpSGST())
				.add(inv.getTpSGST());
		a2totcess = critem.getTpCess()
				.add(dritem.getTpCess().add(inv.getTpCess()));
		totalCreditEligible = critem.getTotalCreditEligible().add(dritem
				.getTotalCreditEligible().add(inv.getTotalCreditEligible()));

		prtotigst = critem.getCeIGST().add(dritem.getCeIGST())
				.add(inv.getCeIGST());
		prtotcgst = critem.getCeCGST().add(dritem.getCeCGST())
				.add(inv.getCeCGST());
		prtotsgst = critem.getCeSGST().add(dritem.getCeSGST())
				.add(inv.getCeSGST());
		prtotcess = critem.getCeCess().add(dritem.getCeCess())
				.add(inv.getCeCess());

		reviewSummeryfile.setTable("5-ISD");
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
