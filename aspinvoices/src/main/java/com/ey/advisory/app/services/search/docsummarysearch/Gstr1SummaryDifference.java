package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("Gstr1SummaryDifference")
public class Gstr1SummaryDifference {

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	public Gstr1SummaryRespDto getDiffStructure(Gstr1SummaryRespDto eyTotal,
			Gstr1SummaryRespDto gstn) {

		Gstr1SummaryRespDto diff = new Gstr1SummaryRespDto();

		diff.setTableSection("-");

		//Subtraction logic : diff = eyTotal - gstn 
		BigDecimal invValue = eyTotal.getInvValue()
				.subtract(gstn.getInvValue());
		BigDecimal taxable = eyTotal.getTaxableValue()
				.subtract(gstn.getTaxableValue());
		BigDecimal taxpayble = eyTotal.getTaxPayble()
				.subtract(gstn.getTaxPayble());
		BigDecimal igst = eyTotal.getIgst().subtract(gstn.getIgst());
		BigDecimal cgst = eyTotal.getCgst().subtract(gstn.getCgst());
		BigDecimal sgst = eyTotal.getSgst().subtract(gstn.getSgst());
		BigDecimal cess = eyTotal.getCess().subtract(gstn.getCess());
		Integer records = eyTotal.getRecords() - (gstn.getRecords());

		diff.setInvValue(invValue);
		diff.setTaxableValue(taxable);
		diff.setTaxPayble(taxpayble);
		diff.setIgst(igst);
		diff.setCgst(cgst);
		diff.setSgst(sgst);
		diff.setCess(cess);
		diff.setRecords(records);

		return diff;
	}
	public Gstr1SummaryRespDto getDiffB2CLStructure(Gstr1SummaryRespDto eyTotal,
			Gstr1SummaryRespDto gstn) {

		Gstr1SummaryRespDto diff = new Gstr1SummaryRespDto();

		diff.setTableSection("-");

		//Subtraction logic : diff = eyTotal - gstn 
		BigDecimal invValue = eyTotal.getInvValue()
				.subtract(gstn.getInvValue());
		BigDecimal taxable = eyTotal.getTaxableValue()
				.subtract(gstn.getTaxableValue());
		BigDecimal taxpayble = eyTotal.getTaxPayble()
				.subtract(gstn.getTaxPayble());
		BigDecimal igst = eyTotal.getIgst().subtract(gstn.getIgst());
	//	BigDecimal cgst = eyTotal.getCgst().subtract(gstn.getCgst());
	//	BigDecimal sgst = eyTotal.getSgst().subtract(gstn.getSgst());
		BigDecimal cess = eyTotal.getCess().subtract(gstn.getCess());
		Integer records = eyTotal.getRecords() - (gstn.getRecords());

		diff.setInvValue(invValue);
		diff.setTaxableValue(taxable);
		diff.setTaxPayble(taxpayble);
		diff.setIgst(igst);
	//	diff.setCgst(cgst);
	//	diff.setSgst(sgst);
		diff.setCess(cess);
		diff.setRecords(records);

		return diff;
	}
	public Gstr1SummaryRespDto getDiffB2CSStructure(Gstr1SummaryRespDto eyTotal,
			Gstr1SummaryRespDto gstn) {

		Gstr1SummaryRespDto diff = new Gstr1SummaryRespDto();

		diff.setTableSection("-");

		//Subtraction logic : diff = eyTotal - gstn 
	/*	BigDecimal invValue = eyTotal.getInvValue()
				.subtract(gstn.getInvValue());*/
		BigDecimal taxable = eyTotal.getTaxableValue()
				.subtract(gstn.getTaxableValue());
		BigDecimal taxpayble = eyTotal.getTaxPayble()
				.subtract(gstn.getTaxPayble());
		BigDecimal igst = eyTotal.getIgst().subtract(gstn.getIgst());
		BigDecimal cgst = eyTotal.getCgst().subtract(gstn.getCgst());
		BigDecimal sgst = eyTotal.getSgst().subtract(gstn.getSgst());
		BigDecimal cess = eyTotal.getCess().subtract(gstn.getCess());
	//	Integer records = eyTotal.getRecords() - (gstn.getRecords());

	//	diff.setInvValue(invValue);
		diff.setTaxableValue(taxable);
		diff.setTaxPayble(taxpayble);
		diff.setIgst(igst);
		diff.setCgst(cgst);
		diff.setSgst(sgst);
		diff.setCess(cess);
	//	diff.setRecords(records);

		return diff;
	}
	
	public Gstr1DocIssuedSummarySectionDto getDiffDOCStructure(Gstr1DocIssuedSummarySectionDto eyTotal,
			Gstr1DocIssuedSummarySectionDto gstn) {

		Gstr1DocIssuedSummarySectionDto diff = new Gstr1DocIssuedSummarySectionDto();

		

		//Subtraction logic : diff = eyTotal - gstn 
	/*	BigDecimal invValue = eyTotal.getInvValue()
				.subtract(gstn.getInvValue());*/
		Integer recordCount = eyTotal.getRecords()-(gstn.getRecords());
		Integer totalIssued = eyTotal.getTotalIssued() - gstn.getTotalIssued();
		Integer netIssued = eyTotal.getNetIssued()- gstn.getNetIssued();
		Integer cancelled = eyTotal.getCancelled()- gstn.getCancelled();
	
	

	//	diff.setInvValue(invValue);
		diff.setRecords(recordCount);
		diff.setTotalIssued(totalIssued);
		diff.setNetIssued(netIssued);
		diff.setCancelled(cancelled);
		return diff;
	}

	public Gstr1NilRatedSummarySectionDto getDiffNILStructure(Gstr1NilRatedSummarySectionDto eyTotal,
			Gstr1NilRatedSummarySectionDto gstn) {

		Gstr1NilRatedSummarySectionDto diff = new Gstr1NilRatedSummarySectionDto();

		

		//Subtraction logic : diff = eyTotal - gstn 
	/*	BigDecimal invValue = eyTotal.getInvValue()
				.subtract(gstn.getInvValue());*/
		Integer recordCount = eyTotal.getRecordCount()-(gstn.getRecordCount());
		BigDecimal totalExempt = eyTotal.getTotalExempted()
				.subtract(gstn.getTotalExempted());
		BigDecimal nilRated = eyTotal.getTotalNilRated().subtract(gstn.getTotalNilRated());
		BigDecimal totalNonGst = eyTotal.getTotalNonGST().subtract(gstn.getTotalNonGST());
	
	

	//	diff.setInvValue(invValue);
		diff.setRecordCount(recordCount);
		diff.setTotalExempted(totalExempt);
		diff.setTotalNilRated(nilRated);
		diff.setTotalNonGST(totalNonGst);
		return diff;
	}




}
