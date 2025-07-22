package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class Gstr1ErrorReport {
	
	@Expose
	@SerializedName("b2b")
	private List<B2BInvoices> b2bInvoice;
	
	@Expose
	@SerializedName("b2ba")
	private List<B2BInvoices> b2baInvoice;

	@Expose
	@SerializedName("b2cl")
	private List<B2CLInvoices> b2clInvoice;

	@Expose
	@SerializedName("b2cla")
	private List<B2CLInvoices> b2claInvoice;

	/*@Expose
	@SerializedName("b2cs")
	private List<B2CSInvoices> b2csInvoice;

	@Expose
	@SerializedName("b2csa")
	private List<B2CSInvoices> b2csaInvoice;

	@Expose
	@SerializedName("at")
	private List<ATInvoices> atInvoice;

	@Expose
	@SerializedName("ata")
	private List<ATInvoices> ataInvoice;*/

	@Expose
	@SerializedName("exp")
	private List<EXPInvoices> expInvoice;

	@Expose
	@SerializedName("expa")
	private List<EXPInvoices> expaInvoice;

	@Expose
	@SerializedName("cdnr")
	private List<CDNRInvoices> cdnrInvoice;

	@Expose
	@SerializedName("cdnra")
	private List<CDNRInvoices> cdnraInvoice;

	@Expose
	@SerializedName("cdnur")
	private List<CDNURInvoices> cdnurInvoice;

	@Expose
	@SerializedName("cdnura")
	private List<CDNURInvoices> cdnuraInvoice;

	/*@Expose
	@SerializedName("txpd")
	private List<TXPInvoices> txpInvoices;

	@Expose
	@SerializedName("txpda")
	private List<TXPInvoices> txpaInvoices;

	@Expose
	@SerializedName("hsn")
	private HSNSummaryInvoices hsnSummaryInvoices;

	@Expose
	@SerializedName("nil")
	private NilSupplies nilSupplies;

	@Expose
	@SerializedName("doc_issue")
	private DocIssueInvoices docIssueInvoices;*/
	
	// Extra variables added for GSTR1_SAVE for cancelled invoices(Auto Draft Delete GSTN Data)
	
	@Expose
	@SerializedName("expwop")
	private List<EXPInvoices> expwopInvoice;
	
	@Expose
	@SerializedName("expwp")
	private List<EXPInvoices> expwpInvoice;
	
	@Expose
	@SerializedName("hsnsum")
	private List<HSNSummaryInvoices> hsnSumInvoices;
	
	@Expose
	@SerializedName("hsn")
	private List<HSNSummaryInvoices> hsnInvoices;
	
}
