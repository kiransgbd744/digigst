package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;

/**
 * @author Balakrishna.S
 *
 */
@Service("Gstr1B2CSCalculation")
public class Gstr1B2CSCalculation {

	public Gstr1SummarySectionDto addB2csGstnData(
			List<Gstr1SummaryCDSectionDto> gstnSummary) {

		Gstr1SummarySectionDto b2csSummaryFinal = new Gstr1SummarySectionDto();
		
		if(gstnSummary != null && gstnSummary.size()>0){
		Gstr1SummaryCDSectionDto b2csInv = gstnSummary.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto b2csBos = gstnSummary.stream()
				.filter(x -> "BOS".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto b2csDr = gstnSummary.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto b2csCr = gstnSummary.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);
		b2csSummaryFinal = sumOfB2CSInvDrCr(b2csInv,b2csBos,
				b2csDr,b2csCr);

		}
		return b2csSummaryFinal;

	}
	
	public Gstr1SummarySectionDto sumOfB2CSInvDrCr(Gstr1SummaryCDSectionDto b2csInv,Gstr1SummaryCDSectionDto b2csBos,
			Gstr1SummaryCDSectionDto b2csDr,Gstr1SummaryCDSectionDto b2csCr){
		
		Gstr1SummarySectionDto totalCal = new Gstr1SummarySectionDto();
		
		Gstr1SummaryCDSectionDto inv = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto bos = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto cr = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto dr = new Gstr1SummaryCDSectionDto();
		if (b2csInv != null) {
			inv = b2csInv;
		}
		if (b2csBos != null) {
			bos = b2csBos;
		}
		if (b2csDr != null) {
			dr = b2csDr;
		}
		if (b2csCr != null) {
			cr = b2csCr;
		}

		Integer eyCount = (inv.getRecords() != null) ? inv.getRecords() : 0;
		Integer bosEyCount = (bos.getRecords() != null) ? bos.getRecords() : 0;
		Integer drEyCount = (dr.getRecords() != null) ? dr.getRecords() : 0;
		Integer crEyCount = (cr.getRecords() != null) ? cr.getRecords() : 0;

		int countEyTotal = (eyCount + bosEyCount + drEyCount + crEyCount);
		
	
		BigDecimal invEyTotal = addBosMethod(inv.getInvValue(),bos.getInvValue(),
				dr.getInvValue(), cr.getInvValue());
		BigDecimal taxableEyTotal = addBosMethod(inv.getTaxableValue(),bos.getTaxableValue(),
				dr.getTaxableValue(), cr.getTaxableValue());

		BigDecimal taxPayEyTotal = addBosMethod(inv.getTaxPayable(),bos.getTaxPayable(),
				dr.getTaxPayable(), cr.getTaxPayable());
		BigDecimal igstEyTotal = addBosMethod(inv.getIgst(),bos.getIgst(), dr.getIgst(),
				cr.getIgst());
		BigDecimal sgstEyTotal = addBosMethod(inv.getSgst(),bos.getSgst(), dr.getSgst(),
				cr.getSgst());
		BigDecimal cgstEyTotal = addBosMethod(inv.getCgst(),bos.getCgst(), dr.getCgst(),
				cr.getCgst());

		BigDecimal cessEyTotal = addBosMethod(inv.getCess(),bos.getCess(), dr.getCess(),
				cr.getCess());


		totalCal.setRecords(countEyTotal);
		totalCal.setInvValue(invEyTotal);
		totalCal.setTaxPayable(taxPayEyTotal);
		totalCal.setTaxableValue(taxableEyTotal);
		totalCal.setIgst(igstEyTotal);
		totalCal.setSgst(sgstEyTotal);
		totalCal.setCgst(cgstEyTotal);
		totalCal.setCess(cessEyTotal);
		totalCal.setTaxDocType("B2CS");
		
		
		return totalCal;
		
	}
	public Gstr1SummarySectionDto addB2csaGstnData(
			List<Gstr1SummaryCDSectionDto> gstnSummary) {

		Gstr1SummarySectionDto b2csaSummaryFinal = new Gstr1SummarySectionDto();
		if(gstnSummary != null && gstnSummary.size()>0){
		Gstr1SummaryCDSectionDto b2csaInv = gstnSummary.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto b2csaDr = gstnSummary.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto b2csaCr = gstnSummary.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);
		b2csaSummaryFinal = sumOfB2CSAInvDrCr(b2csaInv,
				b2csaDr,b2csaCr);

		}
		return b2csaSummaryFinal;

	}
	
	public Gstr1SummarySectionDto sumOfB2CSAInvDrCr(Gstr1SummaryCDSectionDto b2csaInv,
			Gstr1SummaryCDSectionDto b2csaDr,Gstr1SummaryCDSectionDto b2csaCr){
		
		Gstr1SummarySectionDto totalCal = new Gstr1SummarySectionDto();
		
		Gstr1SummaryCDSectionDto inv = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto cr = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto dr = new Gstr1SummaryCDSectionDto();
		if (b2csaInv != null) {
			inv = b2csaInv;
		}
		if (b2csaDr != null) {
			dr = b2csaDr;
		}
		if (b2csaCr != null) {
			cr = b2csaCr;
		}

		Integer eyCount = (inv.getRecords() != null) ? inv.getRecords() : 0;
		Integer drEyCount = (dr.getRecords() != null) ? dr.getRecords() : 0;
		Integer crEyCount = (cr.getRecords() != null) ? cr.getRecords() : 0;

		int countEyTotal = (eyCount + drEyCount + crEyCount);
		
	
		BigDecimal invEyTotal = addMethod(inv.getInvValue(),
				dr.getInvValue(), cr.getInvValue());
		BigDecimal taxableEyTotal = addMethod(inv.getTaxableValue(),
				dr.getTaxableValue(), cr.getTaxableValue());

		BigDecimal taxPayEyTotal = addMethod(inv.getTaxPayable(),
				dr.getTaxPayable(), cr.getTaxPayable());
		BigDecimal igstEyTotal = addMethod(inv.getIgst(), dr.getIgst(),
				cr.getIgst());
		BigDecimal sgstEyTotal = addMethod(inv.getSgst(), dr.getSgst(),
				cr.getSgst());
		BigDecimal cgstEyTotal = addMethod(inv.getCgst(), dr.getCgst(),
				cr.getCgst());

		BigDecimal cessEyTotal = addMethod(inv.getCess(), dr.getCess(),
				cr.getCess());


		totalCal.setRecords(countEyTotal);
		totalCal.setInvValue(invEyTotal);
		totalCal.setTaxPayable(taxPayEyTotal);
		totalCal.setTaxableValue(taxableEyTotal);
		totalCal.setIgst(igstEyTotal);
		totalCal.setSgst(sgstEyTotal);
		totalCal.setCgst(cgstEyTotal);
		totalCal.setCess(cessEyTotal);
		totalCal.setTaxDocType("B2CSA");
		
		
		return totalCal;
		
	}

	public List<Gstr1SummaryNilSectionDto> addNilGstnData(
			List<Gstr1SummaryNilSectionDto> gstnSummary) {
		
		List<Gstr1SummaryNilSectionDto> nilRnvSummaryFinal = new ArrayList<>();
		if(gstnSummary != null && gstnSummary.size()>0){
		Gstr1SummaryNilSectionDto nilInv = gstnSummary.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryNilSectionDto nilBos = gstnSummary.stream()
				.filter(x -> "BOS".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryNilSectionDto nilDr = gstnSummary.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryNilSectionDto nilCr = gstnSummary.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		
		
		Gstr1SummaryNilSectionDto nilRnv = gstnSummary.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryNilSectionDto nilRDr = gstnSummary.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryNilSectionDto nilRCr = gstnSummary.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);
		 nilRnvSummaryFinal = sumOfNILInvDrCr(
				nilInv,nilBos,nilDr,nilCr,nilRnv,nilRDr,nilRCr);

		}
		return nilRnvSummaryFinal;

	}
	
	public List<Gstr1SummaryNilSectionDto> sumOfNILInvDrCr(
			Gstr1SummaryNilSectionDto nilInv,Gstr1SummaryNilSectionDto nilBos,Gstr1SummaryNilSectionDto nilDr,
			Gstr1SummaryNilSectionDto nilCr,Gstr1SummaryNilSectionDto nilRnv,Gstr1SummaryNilSectionDto nilRDr,Gstr1SummaryNilSectionDto nilRCr){
		
		
		List<Gstr1SummaryNilSectionDto> totalCalList = new ArrayList<>();
		Gstr1SummaryNilSectionDto totalCal = new Gstr1SummaryNilSectionDto();
		
		Gstr1SummaryNilSectionDto inv = new Gstr1SummaryNilSectionDto();
		Gstr1SummaryNilSectionDto bos = new Gstr1SummaryNilSectionDto();
		Gstr1SummaryNilSectionDto cr = new Gstr1SummaryNilSectionDto();
		Gstr1SummaryNilSectionDto dr = new Gstr1SummaryNilSectionDto();
		
		Gstr1SummaryNilSectionDto rnv = new Gstr1SummaryNilSectionDto();
		Gstr1SummaryNilSectionDto rcr = new Gstr1SummaryNilSectionDto();
		Gstr1SummaryNilSectionDto rdr = new Gstr1SummaryNilSectionDto();
		
		if (nilInv != null) {
			inv = nilInv;
		}
		if (nilBos != null) {
			bos = nilBos;
		}
		if (nilDr != null) {
			dr = nilDr;
		}
		if (nilCr != null) {
			cr = nilCr;
		}
		if (nilRnv != null) {
			rnv = nilRnv;
		}
		if (nilRDr != null) {
			rdr = nilRDr;
		}
		if (nilRCr != null) {
			rcr = nilRCr;
		}
		
		
		BigDecimal aspInvExemptedTotal = addBosMethod(inv.getAspExempted(),bos.getAspExempted(),
				dr.getAspExempted(), cr.getAspExempted());
		BigDecimal aspInvNilTotal = addBosMethod(inv.getAspNitRated(),bos.getAspNitRated(),
				dr.getAspNitRated(), cr.getAspNitRated());

		BigDecimal aspInvNonTotal = addBosMethod(inv.getAspNonGst(),bos.getAspNonGst(),
				dr.getAspNonGst(), cr.getAspNonGst());
		
		
		BigDecimal aspRnvExemptedTotal = addMethod(rnv.getAspExempted(),
				rdr.getAspExempted(), rcr.getAspExempted());
		BigDecimal aspRnvNilTotal = addMethod(rnv.getAspNitRated(),
				rdr.getAspNitRated(), rcr.getAspNitRated());

		BigDecimal aspRnvNonTotal = addMethod(rnv.getAspNonGst(),
				rdr.getAspNonGst(), rcr.getAspNonGst());
        
		totalCal.setAspExempted(aspInvExemptedTotal.add(aspRnvExemptedTotal));
		totalCal.setAspNitRated(aspInvNilTotal.add(aspRnvNilTotal));
		totalCal.setAspNonGst(aspInvNonTotal.add(aspRnvNonTotal));
		totalCal.setTaxDocType("NILEXTNON");
		
		totalCalList.add(totalCal);
		return totalCalList;
		
	}	
	public Gstr1SummarySectionDto addHSNGstnData(
			List<Gstr1SummaryCDSectionDto> gstnSummary) {

		Gstr1SummarySectionDto hsnSummaryFinal = new Gstr1SummarySectionDto();
		
		if(gstnSummary != null && gstnSummary.size()>0){
		Gstr1SummaryCDSectionDto hsnInv = gstnSummary.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto hsnBos = gstnSummary.stream()
				.filter(x -> "BOS".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto hsnDr = gstnSummary.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto hsnCr = gstnSummary.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);
		
	/*	Gstr1SummaryCDSectionDto hsnRnv = gstnSummary.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto hsnRdr = gstnSummary.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);
		Gstr1SummaryCDSectionDto hsnRcr = gstnSummary.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);*/
		hsnSummaryFinal = sumOfHSNInvDrCr(hsnInv,hsnBos,
				hsnDr,hsnCr);
		

		}
		return hsnSummaryFinal;

	}
	
	public Gstr1SummarySectionDto sumOfHSNInvDrCr(Gstr1SummaryCDSectionDto hsnInv,
			Gstr1SummaryCDSectionDto hsnBos,
			Gstr1SummaryCDSectionDto hsnDr,Gstr1SummaryCDSectionDto hsnCr){
		
		Gstr1SummarySectionDto totalCal = new Gstr1SummarySectionDto();
		
		Gstr1SummaryCDSectionDto inv = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto bos = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto cr = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto dr = new Gstr1SummaryCDSectionDto();
		
	/*	Gstr1SummaryCDSectionDto rnv = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto rcr = new Gstr1SummaryCDSectionDto();
		Gstr1SummaryCDSectionDto rdr = new Gstr1SummaryCDSectionDto();*/
		
		if (hsnInv != null) {
			inv = hsnInv;
		}
		if (hsnBos != null) {
			bos = hsnBos;
		}
		if (hsnDr != null) {
			dr = hsnDr;
		}
		if (hsnCr != null) {
			cr = hsnCr;
		}

		Integer eyCount = (inv.getRecords() != null) ? inv.getRecords() : 0;
		Integer eybosCount = (bos.getRecords() != null) ? bos.getRecords() : 0;
		Integer drEyCount = (dr.getRecords() != null) ? dr.getRecords() : 0;
		Integer crEyCount = (cr.getRecords() != null) ? cr.getRecords() : 0;
		
		int countEyTotal = (eyCount + drEyCount + crEyCount+eybosCount);
		
	// INV+DR-CR
		
		BigDecimal invEyTotal = addBosMethod(inv.getInvValue(),bos.getInvValue(),
				dr.getInvValue(), cr.getInvValue());
		BigDecimal taxableEyTotal = addBosMethod(inv.getTaxableValue(),bos.getTaxableValue(),
				dr.getTaxableValue(), cr.getTaxableValue());

		BigDecimal taxPayEyTotal = addBosMethod(inv.getTaxPayable(),bos.getTaxPayable(),
				dr.getTaxPayable(), cr.getTaxPayable());
		BigDecimal igstEyTotal = addBosMethod(inv.getIgst(), dr.getIgst(),bos.getIgst(),
				cr.getIgst());
		BigDecimal sgstEyTotal = addBosMethod(inv.getSgst(), dr.getSgst(),bos.getSgst(),
				cr.getSgst());
		BigDecimal cgstEyTotal = addBosMethod(inv.getCgst(), dr.getCgst(),bos.getCgst(),
				cr.getCgst());

		BigDecimal cessEyTotal = addBosMethod(inv.getCess(), dr.getCess(),bos.getCess(),
				cr.getCess());
		
	/*	// RNV+RDR-RCR
		
		BigDecimal invREyTotal = addMethod(rnv.getInvValue(),
				rdr.getInvValue(), rcr.getInvValue());
		BigDecimal taxableREyTotal = addMethod(rnv.getTaxableValue(),
				rdr.getTaxableValue(), rcr.getTaxableValue());

		BigDecimal taxPayREyTotal = addMethod(rnv.getTaxPayable(),
				rdr.getTaxPayable(), rcr.getTaxPayable());
		BigDecimal igstREyTotal = addMethod(rnv.getIgst(), rdr.getIgst(),
				rcr.getIgst());
		BigDecimal sgstREyTotal = addMethod(rnv.getSgst(), rdr.getSgst(),
				rcr.getSgst());
		BigDecimal cgstREyTotal = addMethod(rnv.getCgst(), rdr.getCgst(),
				rcr.getCgst());

		BigDecimal cessREyTotal = addMethod(rnv.getCess(), rdr.getCess(),
				rcr.getCess());

*/

		totalCal.setRecords(countEyTotal);
		totalCal.setInvValue(invEyTotal);
		totalCal.setTaxPayable(taxPayEyTotal);
		totalCal.setTaxableValue(taxableEyTotal);
		totalCal.setIgst(igstEyTotal);
		totalCal.setSgst(sgstEyTotal);
		totalCal.setCgst(cgstEyTotal);
		totalCal.setCess(cessEyTotal);
		totalCal.setTaxDocType("HSN");
		
		
		return totalCal;
		
	}

	private BigDecimal addBosMethod(BigDecimal a,BigDecimal b, BigDecimal c, BigDecimal d) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;
		BigDecimal c1 = (c == null) ? BigDecimal.ZERO : c;
		BigDecimal d1 = (d == null) ? BigDecimal.ZERO : d;
		return (a1.add(b1).add(c1)).subtract(d1);

	}
	
	
	private BigDecimal addMethod(BigDecimal a, BigDecimal b, BigDecimal c) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;
		BigDecimal c1 = (c == null) ? BigDecimal.ZERO : c;
		return (a1.add(b1)).subtract(c1);

	}

}
