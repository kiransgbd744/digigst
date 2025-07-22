package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx2IsdReviewSummeryBigtableImpl")
public class Anx2IsdReviewSummeryBigtableImpl  {

	
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

		a2totalRec_Count = critem.getCount() + dritem.getCount()
		+ inv.getCount();
Invoicevalue = critem.getInvValue().add(dritem.getInvValue())
		.subtract(inv.getInvValue());
a2taxablevalue = critem.getTaxableValue()
		.subtract(dritem.getTaxableValue()).add(inv.getTaxableValue());
totalTaxPayable = critem.getTotalTaxPayable()
		.subtract(dritem.getTotalTaxPayable())
		.subtract(inv.getTotalTaxPayable());
a2totigst = critem.getTpIGST().add(dritem.getTpIGST())
		.subtract(inv.getTpIGST());
a2totcgst = critem.getTpCGST().add(dritem.getTpCGST())
		.subtract(inv.getTpCGST());
a2totsgst = critem.getTpSGST().add(dritem.getTpSGST())
		.subtract(inv.getTpSGST());
a2totcess = critem.getTpCess()
		.subtract(dritem.getTpCess().add(inv.getTpCess()));

		reviewSummeryfile.setTable("5-ISD");
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
