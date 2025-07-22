package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("summaryTotalCalculation")
public class SummaryTotalCalculation {

	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;
	
	
	public Annexure1SummaryResp1Dto totalCalculation(
			Annexure1SummaryResp1Dto invT, Annexure1SummaryResp1Dto drT,
			Annexure1SummaryResp1Dto crT, Annexure1SummaryResp1Dto rnvT,
			Annexure1SummaryResp1Dto rdrT, Annexure1SummaryResp1Dto rcrT,
			Annexure1SummarySectionDto gstnResult) {

		Annexure1SummaryResp1Dto inv = new Annexure1SummaryResp1Dto();
		Annexure1SummaryResp1Dto cr = new Annexure1SummaryResp1Dto();
		Annexure1SummaryResp1Dto dr = new Annexure1SummaryResp1Dto();

		Annexure1SummaryResp1Dto rnv = new Annexure1SummaryResp1Dto();
		Annexure1SummaryResp1Dto rdr = new Annexure1SummaryResp1Dto();
		Annexure1SummaryResp1Dto rcr = new Annexure1SummaryResp1Dto();
		if (invT != null) {
			inv = invT;
		}
		if (drT != null) {
			dr = drT;
		}
		if (crT != null) {
			cr = crT;
		}
		if (rnvT != null) {
			rnv = rnvT;
		}
		if (rdrT != null) {
			rdr = rdrT;
		}
		if (rcrT != null) {
			rcr = rcrT;
		}

		Integer eyCount = (inv.getEyCount() != null) ? inv.getEyCount() : 0;
		Integer drEyCount = (dr.getEyCount() != null) ? dr.getEyCount() : 0;
		Integer crEyCount = (cr.getEyCount() != null) ? cr.getEyCount() : 0;

		int countEyTotal = (eyCount + drEyCount + crEyCount);
		BigDecimal invEyTotal = addMethod(inv.getEyInvoiceValue(),
				dr.getEyInvoiceValue(), cr.getEyInvoiceValue());
		BigDecimal taxableEyTotal = addMethod(inv.getEyTaxableValue(),
				dr.getEyTaxableValue(), cr.getEyTaxableValue());

		BigDecimal taxPayEyTotal = addMethod(inv.getEyTaxPayble(),
				dr.getEyTaxPayble(), cr.getEyTaxPayble());
		BigDecimal igstEyTotal = addMethod(inv.getEyIgst(), dr.getEyIgst(),
				cr.getEyIgst());
		BigDecimal sgstEyTotal = addMethod(inv.getEySgst(), dr.getEySgst(),
				cr.getEySgst());
		BigDecimal cgstEyTotal = addMethod(inv.getEyCgst(), dr.getEyCgst(),
				cr.getEyCgst());

		BigDecimal cessEyTotal = addMethod(inv.getEyCess(), dr.getEyCess(),
				cr.getEyCess());

   
		Integer invMemoCount = (inv.getMemoCount() != null) ? inv.getMemoCount()
				: 0;
		Integer drMemoCount = (dr.getMemoCount() != null) ? dr.getMemoCount()
				: 0;
		Integer crMemoCount = (cr.getMemoCount() != null) ? cr.getMemoCount()
				: 0;

		int countMemoTotal = (invMemoCount + drMemoCount + crMemoCount);

		BigDecimal invMemoTotal = addMethod(inv.getMemoInvoiceValue(),
				dr.getMemoInvoiceValue(), cr.getMemoInvoiceValue());
		BigDecimal taxableMemoTotal = addMethod(inv.getMemoTaxableValue(),
				dr.getMemoTaxableValue(), cr.getMemoTaxableValue());
		BigDecimal taxPayMemoTotal = addMethod(inv.getMemoTaxPayble(),
				dr.getMemoTaxPayble(), cr.getMemoTaxPayble());
		BigDecimal igstMemoTotal = addMethod(inv.getMemoIgst(),
				dr.getMemoIgst(), cr.getMemoIgst());
		BigDecimal sgstMemoTotal = addMethod(inv.getMemoSgst(),
				dr.getMemoSgst(), cr.getMemoSgst());
		BigDecimal cgstMemoTotal = addMethod(inv.getMemoCgst(),
				dr.getMemoCgst(), cr.getMemoCgst());

		BigDecimal cessMemoTotal = addMethod(inv.getMemoCess(),
				dr.getMemoCess(), cr.getMemoCess());


		// RNV + RDR - RCR

		Integer rnvEyCount = (rnv.getEyCount() != null) ? rnv.getEyCount() : 0;
		Integer rdrEyCount = (rdr.getEyCount() != null) ? rdr.getEyCount() : 0;
		Integer rcrEyCount = (rcr.getEyCount() != null) ? rcr.getEyCount() : 0;

		int countREyTotal = (rnvEyCount + rdrEyCount + rcrEyCount);
		BigDecimal invREyTotal = addMethod(rnv.getEyInvoiceValue(),
				rdr.getEyInvoiceValue(), rcr.getEyInvoiceValue());
		BigDecimal taxableREyTotal = addMethod(rnv.getEyTaxableValue(),
				rdr.getEyTaxableValue(), rcr.getEyTaxableValue());
		BigDecimal taxPayREyTotal = addMethod(rnv.getEyTaxPayble(),
				rdr.getEyTaxPayble(), rcr.getEyTaxPayble());
		BigDecimal igstREyTotal = addMethod(rnv.getEyIgst(), rdr.getEyIgst(),
				rcr.getEyIgst());
		BigDecimal sgstREyTotal = addMethod(rnv.getEySgst(), rdr.getEySgst(),
				rcr.getEySgst());
		BigDecimal cgstREyTotal = addMethod(rnv.getEyCgst(), rdr.getEyCgst(),
				rcr.getEyCgst());
		BigDecimal cessREyTotal = addMethod(rnv.getEyCess(), rdr.getEyCess(),
				rcr.getEyCess());

		Integer rnvMemoCount = (rnv.getMemoCount() != null) ? rnv.getMemoCount()
				: 0;
		Integer rdrMemoCount = (rdr.getMemoCount() != null) ? rdr.getMemoCount()
				: 0;
		Integer rcrMemoCount = (rcr.getMemoCount() != null) ? rcr.getMemoCount()
				: 0;

		int countRMemoTotal = (rnvMemoCount + rdrMemoCount + rcrMemoCount);
		BigDecimal invRMemoTotal = addMethod(rnv.getMemoInvoiceValue(),
				rdr.getMemoInvoiceValue(), rcr.getMemoInvoiceValue());
		BigDecimal taxableRMemoTotal = addMethod(rnv.getMemoTaxableValue(),
				rdr.getMemoTaxableValue(), rcr.getMemoTaxableValue());
		BigDecimal taxPayRMemoTotal = addMethod(rnv.getMemoTaxPayble(),
				rdr.getMemoTaxPayble(), rcr.getMemoTaxPayble());
		BigDecimal igstRMemoTotal = addMethod(rnv.getMemoIgst(),
				rdr.getMemoIgst(), rcr.getMemoIgst());
		BigDecimal sgstRMemoTotal = addMethod(rnv.getMemoSgst(),
				rdr.getMemoSgst(), rcr.getMemoSgst());
		BigDecimal cgstRMemoTotal = addMethod(rnv.getMemoCgst(),
				rdr.getMemoCgst(), rcr.getMemoCgst());

		BigDecimal cessRMemoTotal = addMethod(rnv.getMemoCess(),
				rdr.getMemoCess(), rcr.getMemoCess());

		int EyCount = countEyTotal + countREyTotal;
		BigDecimal EyInvoice = invEyTotal.add(invREyTotal);
		BigDecimal EyTaxable = taxableEyTotal.add(taxableREyTotal);
		BigDecimal EyTaxPayable = taxPayEyTotal.add(taxPayREyTotal);
		BigDecimal EyIgst = igstEyTotal.add(igstREyTotal);
		BigDecimal EySgst = sgstEyTotal.add(sgstREyTotal);
		BigDecimal EyCgst = cgstEyTotal.add(cgstREyTotal);
		BigDecimal EyCess = cessEyTotal.add(cessREyTotal);

		int MemoCount = countMemoTotal + countRMemoTotal;
		BigDecimal MemoInvoice = invMemoTotal.add(invRMemoTotal);
		BigDecimal MemoTaxable = taxableMemoTotal.add(taxableRMemoTotal);
		BigDecimal MemoTaxPable = taxPayMemoTotal.add(taxPayRMemoTotal);
		BigDecimal MemoIgst = igstMemoTotal.add(igstRMemoTotal);
		BigDecimal MemoSgst = sgstMemoTotal.add(sgstRMemoTotal);
		BigDecimal MemoCgst = cgstMemoTotal.add(cgstRMemoTotal);
		BigDecimal MemoCess = cessMemoTotal.add(cessRMemoTotal);

 // GSTN Data From Get Summary API
		
		int countGstnTotal = (gstnResult.getRecords());
		BigDecimal invGstnTotal = gstnResult.getInvValue();
		BigDecimal taxableGstnTotal = gstnResult.getTaxableValue();
		BigDecimal taxPayGstnTotal = gstnResult.getTaxPayble();
		BigDecimal igstGstnTotal = gstnResult.getIgst();
		BigDecimal sgstGstnTotal = gstnResult.getSgst();
		BigDecimal cgstGstnTotal = gstnResult.getCgst();
		BigDecimal cessGstnTotal = gstnResult.getCess();

	
		Annexure1SummaryResp1Dto b2bTotal = new Annexure1SummaryResp1Dto();
		b2bTotal.setEyCount(EyCount);
		b2bTotal.setEyInvoiceValue(EyInvoice);
		b2bTotal.setEyTaxableValue(EyTaxable);
		b2bTotal.setEyTaxPayble(EyTaxPayable);
		b2bTotal.setEyIgst(EyIgst);
		b2bTotal.setEySgst(EySgst);
		b2bTotal.setEyCgst(EyCgst);
		b2bTotal.setEyCess(EyCess);
		
		// Setting GSTN Data 
		b2bTotal.setGstnCount(countGstnTotal);
		b2bTotal.setGstnInvoiceValue(invGstnTotal);
		b2bTotal.setGstnTaxableValue(taxableGstnTotal);
		b2bTotal.setGstnTaxPayble(taxPayGstnTotal);
		b2bTotal.setGstnIgst(igstGstnTotal);
		b2bTotal.setGstnSgst(sgstGstnTotal);
		b2bTotal.setGstnCgst(cgstGstnTotal);
		b2bTotal.setGstnCess(cessGstnTotal);
		// Setting Memo Value 
		b2bTotal.setMemoCount(MemoCount);
		b2bTotal.setMemoInvoiceValue(MemoInvoice);
		b2bTotal.setMemoTaxableValue(MemoTaxable);
		b2bTotal.setMemoTaxPayble(MemoTaxPable);
		b2bTotal.setMemoIgst(MemoIgst);
		b2bTotal.setMemoSgst(MemoSgst);
		b2bTotal.setMemoCgst(MemoCgst);
		b2bTotal.setMemoCess(MemoCess);
		// Calculating Diff = Memo Value - Gstn Value 
		b2bTotal.setDiffCount(MemoCount-countGstnTotal);
		b2bTotal.setDiffInvoiceValue(MemoInvoice.subtract(invGstnTotal));
		b2bTotal.setDiffTaxableValue(MemoTaxable.subtract(taxableGstnTotal));
		b2bTotal.setDiffTaxPayble(MemoTaxPable.subtract(taxPayGstnTotal));
		b2bTotal.setDiffIgst(MemoIgst.subtract(igstGstnTotal));
		b2bTotal.setDiffSgst(MemoSgst.subtract(sgstGstnTotal));
		b2bTotal.setDiffCgst(MemoCgst.subtract(cgstGstnTotal));
		b2bTotal.setDiffCess(MemoCess.subtract(cessGstnTotal));
		b2bTotal.setDocType("total");
		b2bTotal.setTableSection(inv.getTableSection());
		return b2bTotal;

	}

	public Annexure1SummaryResp1Dto totalThreeDocCalculation(
			Annexure1SummaryResp1Dto invT, Annexure1SummaryResp1Dto drT,
			Annexure1SummaryResp1Dto crT,Annexure1SummarySectionDto gstnResult) {

		Annexure1SummaryResp1Dto inv = new Annexure1SummaryResp1Dto();
		Annexure1SummaryResp1Dto cr = new Annexure1SummaryResp1Dto();
		Annexure1SummaryResp1Dto dr = new Annexure1SummaryResp1Dto();
		if (invT != null) {
			inv = invT;
		}
		if (drT != null) {
			dr = drT;
		}
		if (crT != null) {
			cr = crT;
		}

		Integer eyCount = (inv.getEyCount() != null) ? inv.getEyCount() : 0;
		Integer drEyCount = (dr.getEyCount() != null) ? dr.getEyCount() : 0;
		Integer crEyCount = (cr.getEyCount() != null) ? cr.getEyCount() : 0;

		int countEyTotal = (eyCount + drEyCount + crEyCount);
		BigDecimal invEyTotal = addMethod(inv.getEyInvoiceValue(),
				dr.getEyInvoiceValue(), cr.getEyInvoiceValue());
		BigDecimal taxableEyTotal = addMethod(inv.getEyTaxableValue(),
				dr.getEyTaxableValue(), cr.getEyTaxableValue());

		BigDecimal taxPayEyTotal = addMethod(inv.getEyTaxPayble(),
				dr.getEyTaxPayble(), cr.getEyTaxPayble());
		BigDecimal igstEyTotal = addMethod(inv.getEyIgst(), dr.getEyIgst(),
				cr.getEyIgst());
		BigDecimal sgstEyTotal = addMethod(inv.getEySgst(), dr.getEySgst(),
				cr.getEySgst());
		BigDecimal cgstEyTotal = addMethod(inv.getEyCgst(), dr.getEyCgst(),
				cr.getEyCgst());

		BigDecimal cessEyTotal = addMethod(inv.getEyCess(), dr.getEyCess(),
				cr.getEyCess());

		//Gstn Total Calculating
		
	
		
	/*	Integer invGstnCount = (inv.getGstnCount() != null) ? inv.getGstnCount()
				: 0;
		Integer drGstnCount = (dr.getGstnCount() != null) ? dr.getGstnCount()
				: 0;
		Integer crGstnCount = (cr.getGstnCount() != null) ? cr.getGstnCount()
				: 0;*/

		int countGstnTotal = (gstnResult.getRecords() != null) ? gstnResult.getRecords() : 0;
		BigDecimal invGstnTotal = gstnResult.getInvValue();

		BigDecimal taxableGstnTotal = gstnResult.getTaxableValue();
		BigDecimal taxPayGstnTotal = gstnResult.getTaxPayble();
		BigDecimal igstGstnTotal = gstnResult.getIgst();
		BigDecimal sgstGstnTotal = gstnResult.getSgst();
		BigDecimal cgstGstnTotal = gstnResult.getCgst();
		BigDecimal cessGstnTotal = gstnResult.getCess();

		Integer invMemoCount = (inv.getMemoCount() != null) ? inv.getMemoCount()
				: 0;
		Integer drMemoCount = (dr.getMemoCount() != null) ? dr.getMemoCount()
				: 0;
		Integer crMemoCount = (cr.getMemoCount() != null) ? cr.getMemoCount()
				: 0;

		int countMemoTotal = (invMemoCount + drMemoCount + crMemoCount);

		BigDecimal invMemoTotal = addMethod(inv.getMemoInvoiceValue(),
				dr.getMemoInvoiceValue(), cr.getMemoInvoiceValue());
		BigDecimal taxableMemoTotal = addMethod(inv.getMemoTaxableValue(),
				dr.getMemoTaxableValue(), cr.getMemoTaxableValue());
		BigDecimal taxPayMemoTotal = addMethod(inv.getMemoTaxPayble(),
				dr.getMemoTaxPayble(), cr.getMemoTaxPayble());
		BigDecimal igstMemoTotal = addMethod(inv.getMemoIgst(),
				dr.getMemoIgst(), cr.getMemoIgst());
		BigDecimal sgstMemoTotal = addMethod(inv.getMemoSgst(),
				dr.getMemoSgst(), cr.getMemoSgst());
		BigDecimal cgstMemoTotal = addMethod(inv.getMemoCgst(),
				dr.getMemoCgst(), cr.getMemoCgst());

		BigDecimal cessMemoTotal = addMethod(inv.getMemoCess(),
				dr.getMemoCess(), cr.getMemoCess());

			
		//int countDiffTotal = (invDiffCount + drDiffCount + crDiffCount);
		int countDiffTotal = countEyTotal-countGstnTotal;
		BigDecimal invDiffTotal = subMethod(invEyTotal,invGstnTotal);
		BigDecimal taxableDiffTotal = subMethod(taxableEyTotal,taxableGstnTotal);
		BigDecimal taxPayDiffTotal = subMethod(taxPayEyTotal,taxPayGstnTotal);
		BigDecimal igstDiffTotal = subMethod(igstEyTotal,igstGstnTotal);
		BigDecimal sgstDiffTotal = subMethod(sgstEyTotal,sgstGstnTotal);
		BigDecimal cgstDiffTotal = subMethod(cgstEyTotal,cgstGstnTotal);
		BigDecimal cessDiffTotal = subMethod(cessEyTotal,cessGstnTotal);

		Annexure1SummaryResp1Dto b2cTotal = new Annexure1SummaryResp1Dto();
		b2cTotal.setEyCount(countEyTotal);
		b2cTotal.setEyInvoiceValue(invEyTotal);
		b2cTotal.setEyTaxableValue(taxableEyTotal);
		b2cTotal.setEyTaxPayble(taxPayEyTotal);
		b2cTotal.setEyIgst(igstEyTotal);
		b2cTotal.setEySgst(sgstEyTotal);
		b2cTotal.setEyCgst(cgstEyTotal);
		b2cTotal.setEyCess(cessEyTotal);
		b2cTotal.setGstnCount(countGstnTotal);
		b2cTotal.setGstnInvoiceValue(invGstnTotal);
		b2cTotal.setGstnTaxableValue(taxableGstnTotal);
		b2cTotal.setGstnTaxPayble(taxPayGstnTotal);
		b2cTotal.setGstnIgst(igstGstnTotal);
		b2cTotal.setGstnSgst(sgstGstnTotal);
		b2cTotal.setGstnCgst(cgstGstnTotal);
		b2cTotal.setGstnCess(cessGstnTotal);
		b2cTotal.setMemoCount(countMemoTotal);
		b2cTotal.setMemoInvoiceValue(invMemoTotal);
		b2cTotal.setMemoTaxableValue(taxableMemoTotal);
		b2cTotal.setMemoTaxPayble(taxPayMemoTotal);
		b2cTotal.setMemoIgst(igstMemoTotal);
		b2cTotal.setMemoSgst(sgstMemoTotal);
		b2cTotal.setMemoCgst(cgstMemoTotal);
		b2cTotal.setMemoCess(cessMemoTotal);
		b2cTotal.setDiffCount(countDiffTotal);
		b2cTotal.setDiffInvoiceValue(invDiffTotal);
		b2cTotal.setDiffTaxableValue(taxableDiffTotal);
		b2cTotal.setDiffTaxPayble(taxPayDiffTotal);
		b2cTotal.setDiffIgst(igstDiffTotal);
		b2cTotal.setDiffSgst(sgstDiffTotal);
		b2cTotal.setDiffCgst(cgstDiffTotal);
		b2cTotal.setDiffCess(cessDiffTotal);
		b2cTotal.setDocType("total");
		b2cTotal.setTableSection(inv.getTableSection());
		return b2cTotal;

	}

	public Annexure1SummaryResp1Dto totalSlfDocCalculation(
			Annexure1SummaryResp1Dto slf) {

		Annexure1SummaryResp1Dto total = new Annexure1SummaryResp1Dto();

		if (slf.getTableSection() != null) {
			total.setTableSection(slf.getTableSection());
		}
		total.setDocType("total");
		if (slf.getEyCount() != null) {
			total.setEyCount(slf.getEyCount());
		}
		if (slf.getEyInvoiceValue() != null) {
			total.setEyInvoiceValue(slf.getEyInvoiceValue());
		}
		if (slf.getEyTaxableValue() != null) {
			total.setEyTaxableValue(slf.getEyTaxableValue());
		}
		if (slf.getEyTaxPayble() != null) {
			total.setEyTaxPayble(slf.getEyTaxPayble());
		}
		if (slf.getEyIgst() != null) {
			total.setEyIgst(slf.getEyIgst());
		}
		if (slf.getEySgst() != null) {
			total.setEySgst(slf.getEySgst());
		}
		if (slf.getEyCgst() != null) {
			total.setEyCgst(slf.getEyCgst());
		}
		if (slf.getEyCess() != null) {
			total.setEyCess(slf.getEyCess());
		}
		if (slf.getGstnCount() != null) {
			total.setGstnCount(slf.getGstnCount());
		}
		if (slf.getGstnInvoiceValue() != null) {
			total.setGstnInvoiceValue(slf.getGstnInvoiceValue());
		}
		if (slf.getGstnTaxableValue() != null) {
			total.setGstnTaxableValue(slf.getGstnTaxableValue());
		}
		if (slf.getGstnTaxPayble() != null) {
			total.setGstnTaxPayble(slf.getGstnTaxPayble());
		}
		if (slf.getGstnIgst() != null) {
			total.setGstnIgst(slf.getGstnIgst());
		}
		if (slf.getGstnSgst() != null) {
			total.setGstnSgst(slf.getGstnSgst());
		}
		if (slf.getGstnCgst() != null) {
			total.setGstnCgst(slf.getGstnCgst());
		}
		if (slf.getGstnCess() != null) {
			total.setGstnCess(slf.getGstnCess());
		}
		if (slf.getMemoCount() != null) {
			total.setMemoCount(slf.getMemoCount());
		}
		if (slf.getMemoInvoiceValue() != null) {
			total.setMemoInvoiceValue(slf.getMemoInvoiceValue());
		}
		if (slf.getMemoTaxPayble() != null) {
			total.setMemoTaxPayble(slf.getMemoTaxPayble());
		}
		if (slf.getMemoTaxPayble() != null) {
			total.setMemoTaxableValue(slf.getMemoTaxPayble());
		}
		if (slf.getMemoIgst() != null) {
			total.setMemoIgst(slf.getMemoIgst());
		}
		if (slf.getMemoCgst() != null) {
			total.setMemoCgst(slf.getMemoCgst());
		}
		if (slf.getMemoSgst() != null) {
			total.setMemoSgst(slf.getMemoSgst());
		}
		if (slf.getMemoCess() != null) {
			total.setMemoCess(slf.getMemoCess());
		}
		if (slf.getDiffCount() != null) {
			total.setDiffCount(slf.getDiffCount());
		}
		if (slf.getDiffInvoiceValue() != null) {
			total.setDiffInvoiceValue(slf.getDiffInvoiceValue());
		}
		if (slf.getDiffTaxableValue() != null) {
			total.setDiffTaxPayble(slf.getDiffTaxableValue());
		}
		if (slf.getDiffTaxableValue() != null) {
			total.setDiffTaxableValue(slf.getDiffTaxableValue());
		}
		if (slf.getDiffIgst() != null) {
			total.setDiffIgst(slf.getDiffIgst());
		}
		if (slf.getDiffCgst() != null) {
			total.setDiffCgst(slf.getDiffCgst());
		}
		if (slf.getDiffSgst() != null) {
			total.setDiffSgst(slf.getDiffSgst());
		}
		if (slf.getDiffCess() != null) {
			total.setDiffCess(slf.getDiffCess());
		}

		return total;

	}

	@SuppressWarnings("unused")
	private BigDecimal addMethod(BigDecimal a, BigDecimal b, BigDecimal c) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;
		BigDecimal c1 = (c == null) ? BigDecimal.ZERO : c;
		return (a1.add(b1)).subtract(c1);

	}
	
	@SuppressWarnings("unused")
	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;
		return (a1.subtract(b1));

	}

}
