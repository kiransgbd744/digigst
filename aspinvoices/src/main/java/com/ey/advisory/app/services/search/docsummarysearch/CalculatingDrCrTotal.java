/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("CalculatingDrCrTotal")
public class CalculatingDrCrTotal {

	public Gstr1SummarySectionDto addregateCDNRCredtiDebit(
			Gstr1SummaryCDSectionDto cdnrDr, Gstr1SummaryCDSectionDto cdnrCr,
			Gstr1SummaryCDSectionDto cdnrRfv) {
	
		Gstr1SummarySectionDto crnrFinalValue = new Gstr1SummarySectionDto();
		
		BigDecimal invValueDr = BigDecimal.ZERO;
		BigDecimal taxableDr = BigDecimal.ZERO;
		BigDecimal taxPayableDr = BigDecimal.ZERO;
		BigDecimal igstDr = BigDecimal.ZERO;
		BigDecimal sgstDr = BigDecimal.ZERO;
		BigDecimal cgstDr = BigDecimal.ZERO;
		BigDecimal cessDr = BigDecimal.ZERO;
		Integer countDr = 0;
		
		if(cdnrDr == null){
		 invValueDr = BigDecimal.ZERO;
		 taxableDr = BigDecimal.ZERO;
		 taxPayableDr = BigDecimal.ZERO;
		 igstDr = BigDecimal.ZERO;
		 sgstDr = BigDecimal.ZERO;
		 cgstDr = BigDecimal.ZERO;
		 cessDr = BigDecimal.ZERO;
		 countDr = 0;
		}else{
			invValueDr = cdnrDr.getInvValue();
			 taxableDr = cdnrDr.getTaxableValue();
			 taxPayableDr = cdnrDr.getTaxPayable();
			 igstDr = cdnrDr.getIgst();
			 sgstDr = cdnrDr.getSgst();
			 cgstDr = cdnrDr.getCgst();
			 cessDr = cdnrDr.getCess();
			 countDr = cdnrDr.getRecords();
		}
			BigDecimal invValueCr = BigDecimal.ZERO;
			BigDecimal taxableCr = BigDecimal.ZERO;
			BigDecimal taxPayableCr = BigDecimal.ZERO;
			BigDecimal igstCr = BigDecimal.ZERO;
			BigDecimal sgstCr = BigDecimal.ZERO;
			BigDecimal cgstCr = BigDecimal.ZERO;
			BigDecimal cessCr = BigDecimal.ZERO;
			Integer countCr = 0;
			if(cdnrCr == null){
				 invValueCr = BigDecimal.ZERO;
				 taxableCr = BigDecimal.ZERO;
				 taxPayableCr = BigDecimal.ZERO;
				 igstCr = BigDecimal.ZERO;
				 sgstCr = BigDecimal.ZERO;
				 cgstCr = BigDecimal.ZERO;
				 cessCr = BigDecimal.ZERO;
				 countCr = 0;
			}else{
				invValueCr = cdnrCr.getInvValue();
				 taxableCr = cdnrCr.getTaxableValue();
				 taxPayableCr = cdnrCr.getTaxPayable();
				 igstCr = cdnrCr.getIgst();
				 sgstCr = cdnrCr.getSgst();
				 cgstCr = cdnrCr.getCgst();
				 cessCr = cdnrCr.getCess();
				 countCr = cdnrCr.getRecords();
			}
			
			
			BigDecimal invValueRfv = BigDecimal.ZERO;
			BigDecimal taxableRfv = BigDecimal.ZERO;
			BigDecimal taxPayableRfv = BigDecimal.ZERO;
			BigDecimal igstRfv = BigDecimal.ZERO;
			BigDecimal sgstRfv = BigDecimal.ZERO;
			BigDecimal cgstRfv = BigDecimal.ZERO;
			BigDecimal cessRfv = BigDecimal.ZERO;
			Integer countRfv = 0;
			if(cdnrRfv == null){
				invValueRfv = BigDecimal.ZERO;
				taxableRfv = BigDecimal.ZERO;
				taxPayableRfv = BigDecimal.ZERO;
				igstRfv = BigDecimal.ZERO;
				sgstRfv = BigDecimal.ZERO;
				 cgstRfv = BigDecimal.ZERO;
				 cessRfv = BigDecimal.ZERO;
				 countRfv = 0;
			}else{
				invValueRfv = cdnrRfv.getInvValue();
				taxableRfv = cdnrRfv.getTaxableValue();
				taxPayableRfv = cdnrRfv.getTaxPayable();
				igstRfv = cdnrRfv.getIgst();
				sgstRfv = cdnrRfv.getSgst();
				cgstRfv = cdnrRfv.getCgst();
				cessRfv = cdnrRfv.getCess();
				countRfv = cdnrRfv.getRecords();
			}
		
		
		
		BigDecimal invValue = subMethod(invValueDr,invValueCr);
		BigDecimal taxable = subMethod(taxableDr,taxableCr);
		BigDecimal taxPayable = subMethod(taxPayableDr,taxPayableCr);
		BigDecimal igst = subMethod(igstDr,igstCr);
		BigDecimal sgst = subMethod(sgstDr,sgstCr);
		BigDecimal cgst = subMethod(cgstDr,cgstCr);
		BigDecimal cess = subMethod(cessDr,cessCr);
		
      
		BigDecimal invValueTotal = subMethod(invValue,invValueRfv);
		BigDecimal taxableTotal = subMethod(taxable,taxableRfv);
		BigDecimal taxPayableTotal = subMethod(taxPayable,taxPayableRfv);
		BigDecimal igstTotal = subMethod(igst,igstRfv);
		BigDecimal sgstTotal = subMethod(sgst,sgstRfv);
		BigDecimal cgstTotal = subMethod(cgst,cgstRfv);
		BigDecimal cessTotal = subMethod(cess,cessRfv);
		
		
		
		crnrFinalValue.setRecords(countDr + countCr + countRfv);
		crnrFinalValue.setTaxDocType("CDNR");
		crnrFinalValue.setTaxPayable(taxPayableTotal);
		crnrFinalValue.setTaxableValue(taxableTotal);
		crnrFinalValue.setInvValue(invValueTotal);
		crnrFinalValue.setIgst(igstTotal);
		crnrFinalValue.setSgst(sgstTotal);
		crnrFinalValue.setCgst(cgstTotal);
		crnrFinalValue.setCess(cessTotal);

		return crnrFinalValue;

	}
// CDNRA Calculation
	public Gstr1SummarySectionDto addregateCDNRACredtiDebit(
			Gstr1SummaryCDSectionDto cdnrDr, Gstr1SummaryCDSectionDto cdnrCr,
			Gstr1SummaryCDSectionDto cdnrRfv) {
	
		Gstr1SummarySectionDto crnraFinalValue = new Gstr1SummarySectionDto();
		
		BigDecimal invValueDr = BigDecimal.ZERO;
		BigDecimal taxableDr = BigDecimal.ZERO;
		BigDecimal taxPayableDr = BigDecimal.ZERO;
		BigDecimal igstDr = BigDecimal.ZERO;
		BigDecimal sgstDr = BigDecimal.ZERO;
		BigDecimal cgstDr = BigDecimal.ZERO;
		BigDecimal cessDr = BigDecimal.ZERO;
		Integer countDr = 0;
		
		if(cdnrDr == null){
		 invValueDr = BigDecimal.ZERO;
		 taxableDr = BigDecimal.ZERO;
		 taxPayableDr = BigDecimal.ZERO;
		 igstDr = BigDecimal.ZERO;
		 sgstDr = BigDecimal.ZERO;
		 cgstDr = BigDecimal.ZERO;
		 cessDr = BigDecimal.ZERO;
		 countDr = 0;
		}else{
			invValueDr = cdnrDr.getInvValue();
			 taxableDr = cdnrDr.getTaxableValue();
			 taxPayableDr = cdnrDr.getTaxPayable();
			 igstDr = cdnrDr.getIgst();
			 sgstDr = cdnrDr.getSgst();
			 cgstDr = cdnrDr.getCgst();
			 cessDr = cdnrDr.getCess();
			 countDr = cdnrDr.getRecords();
		}
			BigDecimal invValueCr = BigDecimal.ZERO;
			BigDecimal taxableCr = BigDecimal.ZERO;
			BigDecimal taxPayableCr = BigDecimal.ZERO;
			BigDecimal igstCr = BigDecimal.ZERO;
			BigDecimal sgstCr = BigDecimal.ZERO;
			BigDecimal cgstCr = BigDecimal.ZERO;
			BigDecimal cessCr = BigDecimal.ZERO;
			Integer countCr = 0;
			if(cdnrCr == null){
				 invValueCr = BigDecimal.ZERO;
				 taxableCr = BigDecimal.ZERO;
				 taxPayableCr = BigDecimal.ZERO;
				 igstCr = BigDecimal.ZERO;
				 sgstCr = BigDecimal.ZERO;
				 cgstCr = BigDecimal.ZERO;
				 cessCr = BigDecimal.ZERO;
				 countCr = 0;
			}else{
				invValueCr = cdnrCr.getInvValue();
				 taxableCr = cdnrCr.getTaxableValue();
				 taxPayableCr = cdnrCr.getTaxPayable();
				 igstCr = cdnrCr.getIgst();
				 sgstCr = cdnrCr.getSgst();
				 cgstCr = cdnrCr.getCgst();
				 cessCr = cdnrCr.getCess();
				 countCr = cdnrCr.getRecords();
			}
			
			
			BigDecimal invValueRfv = BigDecimal.ZERO;
			BigDecimal taxableRfv = BigDecimal.ZERO;
			BigDecimal taxPayableRfv = BigDecimal.ZERO;
			BigDecimal igstRfv = BigDecimal.ZERO;
			BigDecimal sgstRfv = BigDecimal.ZERO;
			BigDecimal cgstRfv = BigDecimal.ZERO;
			BigDecimal cessRfv = BigDecimal.ZERO;
			Integer countRfv = 0;
			if(cdnrRfv == null){
				invValueRfv = BigDecimal.ZERO;
				taxableRfv = BigDecimal.ZERO;
				taxPayableRfv = BigDecimal.ZERO;
				igstRfv = BigDecimal.ZERO;
				sgstRfv = BigDecimal.ZERO;
				 cgstRfv = BigDecimal.ZERO;
				 cessRfv = BigDecimal.ZERO;
				 countRfv = 0;
			}else{
				invValueRfv = cdnrRfv.getInvValue();
				taxableRfv = cdnrRfv.getTaxableValue();
				taxPayableRfv = cdnrRfv.getTaxPayable();
				igstRfv = cdnrRfv.getIgst();
				sgstRfv = cdnrRfv.getSgst();
				cgstRfv = cdnrRfv.getCgst();
				cessRfv = cdnrRfv.getCess();
				countRfv = cdnrRfv.getRecords();
			}
		
		
		
		BigDecimal invValue = subMethod(invValueDr,invValueCr);
		BigDecimal taxable = subMethod(taxableDr,taxableCr);
		BigDecimal taxPayable = subMethod(taxPayableDr,taxPayableCr);
		BigDecimal igst = subMethod(igstDr,igstCr);
		BigDecimal sgst = subMethod(sgstDr,sgstCr);
		BigDecimal cgst = subMethod(cgstDr,cgstCr);
		BigDecimal cess = subMethod(cessDr,cessCr);
		
      
		BigDecimal invValueTotal = subMethod(invValue,invValueRfv);
		BigDecimal taxableTotal = subMethod(taxable,taxableRfv);
		BigDecimal taxPayableTotal = subMethod(taxPayable,taxPayableRfv);
		BigDecimal igstTotal = subMethod(igst,igstRfv);
		BigDecimal sgstTotal = subMethod(sgst,sgstRfv);
		BigDecimal cgstTotal = subMethod(cgst,cgstRfv);
		BigDecimal cessTotal = subMethod(cess,cessRfv);
		
		
		
		crnraFinalValue.setRecords(countDr + countCr + countRfv);
		crnraFinalValue.setTaxDocType("CDNRA");
		crnraFinalValue.setTaxPayable(taxPayableTotal);
		crnraFinalValue.setTaxableValue(taxableTotal);
		crnraFinalValue.setInvValue(invValueTotal);
		crnraFinalValue.setIgst(igstTotal);
		crnraFinalValue.setSgst(sgstTotal);
		crnraFinalValue.setCgst(cgstTotal);
		crnraFinalValue.setCess(cessTotal);

		return crnraFinalValue;

	}
	
	// CDNUR Calculation
	public Gstr1SummarySectionDto addregateCDNURCredtiDebit(
			Gstr1SummaryCDSectionDto cdnrDr, Gstr1SummaryCDSectionDto cdnrCr,
			Gstr1SummaryCDSectionDto cdnrRfv) {
	
		Gstr1SummarySectionDto crnurFinalValue = new Gstr1SummarySectionDto();
		
		BigDecimal invValueDr = BigDecimal.ZERO;
		BigDecimal taxableDr = BigDecimal.ZERO;
		BigDecimal taxPayableDr = BigDecimal.ZERO;
		BigDecimal igstDr = BigDecimal.ZERO;
		BigDecimal sgstDr = BigDecimal.ZERO;
		BigDecimal cgstDr = BigDecimal.ZERO;
		BigDecimal cessDr = BigDecimal.ZERO;
		Integer countDr = 0;
		
		if(cdnrDr == null){
		 invValueDr = BigDecimal.ZERO;
		 taxableDr = BigDecimal.ZERO;
		 taxPayableDr = BigDecimal.ZERO;
		 igstDr = BigDecimal.ZERO;
		 sgstDr = BigDecimal.ZERO;
		 cgstDr = BigDecimal.ZERO;
		 cessDr = BigDecimal.ZERO;
		 countDr = 0;
		}else{
			invValueDr = cdnrDr.getInvValue();
			 taxableDr = cdnrDr.getTaxableValue();
			 taxPayableDr = cdnrDr.getTaxPayable();
			 igstDr = cdnrDr.getIgst();
			 sgstDr = cdnrDr.getSgst();
			 cgstDr = cdnrDr.getCgst();
			 cessDr = cdnrDr.getCess();
			 countDr = cdnrDr.getRecords();
		}
			BigDecimal invValueCr = BigDecimal.ZERO;
			BigDecimal taxableCr = BigDecimal.ZERO;
			BigDecimal taxPayableCr = BigDecimal.ZERO;
			BigDecimal igstCr = BigDecimal.ZERO;
			BigDecimal sgstCr = BigDecimal.ZERO;
			BigDecimal cgstCr = BigDecimal.ZERO;
			BigDecimal cessCr = BigDecimal.ZERO;
			Integer countCr = 0;
			if(cdnrCr == null){
				 invValueCr = BigDecimal.ZERO;
				 taxableCr = BigDecimal.ZERO;
				 taxPayableCr = BigDecimal.ZERO;
				 igstCr = BigDecimal.ZERO;
				 sgstCr = BigDecimal.ZERO;
				 cgstCr = BigDecimal.ZERO;
				 cessCr = BigDecimal.ZERO;
				 countCr = 0;
			}else{
				invValueCr = cdnrCr.getInvValue();
				 taxableCr = cdnrCr.getTaxableValue();
				 taxPayableCr = cdnrCr.getTaxPayable();
				 igstCr = cdnrCr.getIgst();
				 sgstCr = cdnrCr.getSgst();
				 cgstCr = cdnrCr.getCgst();
				 cessCr = cdnrCr.getCess();
				 countCr = cdnrCr.getRecords();
			}
			
			
			BigDecimal invValueRfv = BigDecimal.ZERO;
			BigDecimal taxableRfv = BigDecimal.ZERO;
			BigDecimal taxPayableRfv = BigDecimal.ZERO;
			BigDecimal igstRfv = BigDecimal.ZERO;
			BigDecimal sgstRfv = BigDecimal.ZERO;
			BigDecimal cgstRfv = BigDecimal.ZERO;
			BigDecimal cessRfv = BigDecimal.ZERO;
			Integer countRfv = 0;
			if(cdnrRfv == null){
				invValueRfv = BigDecimal.ZERO;
				taxableRfv = BigDecimal.ZERO;
				taxPayableRfv = BigDecimal.ZERO;
				igstRfv = BigDecimal.ZERO;
				sgstRfv = BigDecimal.ZERO;
				 cgstRfv = BigDecimal.ZERO;
				 cessRfv = BigDecimal.ZERO;
				 countRfv = 0;
			}else{
				invValueRfv = cdnrRfv.getInvValue();
				taxableRfv = cdnrRfv.getTaxableValue();
				taxPayableRfv = cdnrRfv.getTaxPayable();
				igstRfv = cdnrRfv.getIgst();
				sgstRfv = cdnrRfv.getSgst();
				cgstRfv = cdnrRfv.getCgst();
				cessRfv = cdnrRfv.getCess();
				countRfv = cdnrRfv.getRecords();
			}
		
		
		
		BigDecimal invValue = subMethod(invValueDr,invValueCr);
		BigDecimal taxable = subMethod(taxableDr,taxableCr);
		BigDecimal taxPayable = subMethod(taxPayableDr,taxPayableCr);
		BigDecimal igst = subMethod(igstDr,igstCr);
		BigDecimal sgst = subMethod(sgstDr,sgstCr);
		BigDecimal cgst = subMethod(cgstDr,cgstCr);
		BigDecimal cess = subMethod(cessDr,cessCr);
		
      
		BigDecimal invValueTotal = subMethod(invValue,invValueRfv);
		BigDecimal taxableTotal = subMethod(taxable,taxableRfv);
		BigDecimal taxPayableTotal = subMethod(taxPayable,taxPayableRfv);
		BigDecimal igstTotal = subMethod(igst,igstRfv);
		BigDecimal sgstTotal = subMethod(sgst,sgstRfv);
		BigDecimal cgstTotal = subMethod(cgst,cgstRfv);
		BigDecimal cessTotal = subMethod(cess,cessRfv);
		
		
		
		crnurFinalValue.setRecords(countDr + countCr + countRfv);
		crnurFinalValue.setTaxDocType("CDNUR");
		crnurFinalValue.setTaxPayable(taxPayableTotal);
		crnurFinalValue.setTaxableValue(taxableTotal);
		crnurFinalValue.setInvValue(invValueTotal);
		crnurFinalValue.setIgst(igstTotal);
		crnurFinalValue.setSgst(sgstTotal);
		crnurFinalValue.setCgst(cgstTotal);
		crnurFinalValue.setCess(cessTotal);

		return crnurFinalValue;

	}
	
// CDNURA --Calculation
	public Gstr1SummarySectionDto addregateCDNURACredtiDebit(
			Gstr1SummaryCDSectionDto cdnrDr, Gstr1SummaryCDSectionDto cdnrCr,
			Gstr1SummaryCDSectionDto cdnrRfv) {
	
		Gstr1SummarySectionDto crnuraFinalValue = new Gstr1SummarySectionDto();
		
		BigDecimal invValueDr = BigDecimal.ZERO;
		BigDecimal taxableDr = BigDecimal.ZERO;
		BigDecimal taxPayableDr = BigDecimal.ZERO;
		BigDecimal igstDr = BigDecimal.ZERO;
		BigDecimal sgstDr = BigDecimal.ZERO;
		BigDecimal cgstDr = BigDecimal.ZERO;
		BigDecimal cessDr = BigDecimal.ZERO;
		Integer countDr = 0;
		
		if(cdnrDr == null){
		 invValueDr = BigDecimal.ZERO;
		 taxableDr = BigDecimal.ZERO;
		 taxPayableDr = BigDecimal.ZERO;
		 igstDr = BigDecimal.ZERO;
		 sgstDr = BigDecimal.ZERO;
		 cgstDr = BigDecimal.ZERO;
		 cessDr = BigDecimal.ZERO;
		 countDr = 0;
		}else{
			invValueDr = cdnrDr.getInvValue();
			 taxableDr = cdnrDr.getTaxableValue();
			 taxPayableDr = cdnrDr.getTaxPayable();
			 igstDr = cdnrDr.getIgst();
			 sgstDr = cdnrDr.getSgst();
			 cgstDr = cdnrDr.getCgst();
			 cessDr = cdnrDr.getCess();
			 countDr = cdnrDr.getRecords();
		}
			BigDecimal invValueCr = BigDecimal.ZERO;
			BigDecimal taxableCr = BigDecimal.ZERO;
			BigDecimal taxPayableCr = BigDecimal.ZERO;
			BigDecimal igstCr = BigDecimal.ZERO;
			BigDecimal sgstCr = BigDecimal.ZERO;
			BigDecimal cgstCr = BigDecimal.ZERO;
			BigDecimal cessCr = BigDecimal.ZERO;
			Integer countCr = 0;
			if(cdnrCr == null){
				 invValueCr = BigDecimal.ZERO;
				 taxableCr = BigDecimal.ZERO;
				 taxPayableCr = BigDecimal.ZERO;
				 igstCr = BigDecimal.ZERO;
				 sgstCr = BigDecimal.ZERO;
				 cgstCr = BigDecimal.ZERO;
				 cessCr = BigDecimal.ZERO;
				 countCr = 0;
			}else{
				invValueCr = cdnrCr.getInvValue();
				 taxableCr = cdnrCr.getTaxableValue();
				 taxPayableCr = cdnrCr.getTaxPayable();
				 igstCr = cdnrCr.getIgst();
				 sgstCr = cdnrCr.getSgst();
				 cgstCr = cdnrCr.getCgst();
				 cessCr = cdnrCr.getCess();
				 countCr = cdnrCr.getRecords();
			}
			
			
			BigDecimal invValueRfv = BigDecimal.ZERO;
			BigDecimal taxableRfv = BigDecimal.ZERO;
			BigDecimal taxPayableRfv = BigDecimal.ZERO;
			BigDecimal igstRfv = BigDecimal.ZERO;
			BigDecimal sgstRfv = BigDecimal.ZERO;
			BigDecimal cgstRfv = BigDecimal.ZERO;
			BigDecimal cessRfv = BigDecimal.ZERO;
			Integer countRfv = 0;
			if(cdnrRfv == null){
				invValueRfv = BigDecimal.ZERO;
				taxableRfv = BigDecimal.ZERO;
				taxPayableRfv = BigDecimal.ZERO;
				igstRfv = BigDecimal.ZERO;
				sgstRfv = BigDecimal.ZERO;
				 cgstRfv = BigDecimal.ZERO;
				 cessRfv = BigDecimal.ZERO;
				 countRfv = 0;
			}else{
				invValueRfv = cdnrRfv.getInvValue();
				taxableRfv = cdnrRfv.getTaxableValue();
				taxPayableRfv = cdnrRfv.getTaxPayable();
				igstRfv = cdnrRfv.getIgst();
				sgstRfv = cdnrRfv.getSgst();
				cgstRfv = cdnrRfv.getCgst();
				cessRfv = cdnrRfv.getCess();
				countRfv = cdnrRfv.getRecords();
			}
		
		
		
		BigDecimal invValue = subMethod(invValueDr,invValueCr);
		BigDecimal taxable = subMethod(taxableDr,taxableCr);
		BigDecimal taxPayable = subMethod(taxPayableDr,taxPayableCr);
		BigDecimal igst = subMethod(igstDr,igstCr);
		BigDecimal sgst = subMethod(sgstDr,sgstCr);
		BigDecimal cgst = subMethod(cgstDr,cgstCr);
		BigDecimal cess = subMethod(cessDr,cessCr);
		
      
		BigDecimal invValueTotal = subMethod(invValue,invValueRfv);
		BigDecimal taxableTotal = subMethod(taxable,taxableRfv);
		BigDecimal taxPayableTotal = subMethod(taxPayable,taxPayableRfv);
		BigDecimal igstTotal = subMethod(igst,igstRfv);
		BigDecimal sgstTotal = subMethod(sgst,sgstRfv);
		BigDecimal cgstTotal = subMethod(cgst,cgstRfv);
		BigDecimal cessTotal = subMethod(cess,cessRfv);
		
		
		
		crnuraFinalValue.setRecords(countDr + countCr + countRfv);
		crnuraFinalValue.setTaxDocType("CDNURA");
		crnuraFinalValue.setTaxPayable(taxPayableTotal);
		crnuraFinalValue.setTaxableValue(taxableTotal);
		crnuraFinalValue.setInvValue(invValueTotal);
		crnuraFinalValue.setIgst(igstTotal);
		crnuraFinalValue.setSgst(sgstTotal);
		crnuraFinalValue.setCgst(cgstTotal);
		crnuraFinalValue.setCess(cessTotal);

		return crnuraFinalValue;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}
	
	
}
