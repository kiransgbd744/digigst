package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class SaveAnx1 {

	@Expose
	@SerializedName("gstin")
	private String sgstin;

	@Expose
	@SerializedName("rtnprd")
	private String taxperiod;
	
	//======Static values Begin========
	@Expose
	@SerializedName("chunkId")
	private Integer chunkId = 3569;
	
	@Expose
	@SerializedName("threshold")
	private Integer threshold = 2500;
	
	@Expose
	@SerializedName("returntype")
	private String returntype = "ANX1";
	//======Static values End========

	@Expose
	@SerializedName("error_report")
	Anx1ErrorReport anx1ErrorReport;
	
	
	

	/**
	 * @return the anx1ErrorReport
	 */
	public Anx1ErrorReport getAnx1ErrorReport() {
		return anx1ErrorReport;
	}

	/**
	 * @param anx1ErrorReport the anx1ErrorReport to set
	 */
	public void setAnx1ErrorReport(Anx1ErrorReport anx1ErrorReport) {
		this.anx1ErrorReport = anx1ErrorReport;
	}

	@Expose
	@SerializedName("b2c")
	private List<Anx1B2cData> b2cInvoice;

	@Expose
	@SerializedName("b2b")
	private List<Anx1B2bData> b2bInvoice;

	@Expose
	@SerializedName("expwp")
	private List<Anx1ExpwpAndExpwopDocumentData> expwpInvoice;

	@Expose
	@SerializedName("expwop")
	private List<Anx1ExpwpAndExpwopDocumentData> expwopInvoice;

	@Expose
	@SerializedName("sezwp")
	private List<Anx1SezwpAndSezwopData> sezwpInvoice;

	@Expose
	@SerializedName("sezwop")
	private List<Anx1SezwpAndSezwopData> sezwopInvoice;

	@Expose
	@SerializedName("de")
	private List<Anx1DeemadExportsData> deInvoice;

	@Expose
	@SerializedName("rev")
	private List<Anx1RevData> revInvoice;

	@Expose
	@SerializedName("imps")
	private List<Anx1ImpsDocumentData> impsInvoice;

	@Expose
	@SerializedName("impg")
	private List<Anx1ImpgAndImpgSezData> impgInvoice;

	@Expose
	@SerializedName("impgsez")
	private List<Anx1ImpgAndImpgSezData> impgsezInvoice;

	@Expose
	@SerializedName("mis")
	private List<Anx1MisData> misInvoice;

	@Expose
	@SerializedName("ecom")
	private List<Anx1EcomData> ecomInvoice;

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getTaxperiod() {
		return taxperiod;
	}

	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}
	
	public Integer getChunkId() {
		return chunkId;
	}

	public void setChunkId(Integer chunkId) {
		this.chunkId = chunkId;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}

	public List<Anx1B2cData> getB2cInvoice() {
		return b2cInvoice;
	}

	public void setB2cInvoice(List<Anx1B2cData> b2cInvoice) {
		this.b2cInvoice = b2cInvoice;
	}

	public List<Anx1B2bData> getB2bInvoice() {
		return b2bInvoice;
	}

	public void setB2bInvoice(List<Anx1B2bData> b2bInvoice) {
		this.b2bInvoice = b2bInvoice;
	}

	public List<Anx1ExpwpAndExpwopDocumentData> getExpwpInvoice() {
		return expwpInvoice;
	}

	public void setExpwpInvoice(List<Anx1ExpwpAndExpwopDocumentData> expwpInvoice) {
		this.expwpInvoice = expwpInvoice;
	}

	public List<Anx1ExpwpAndExpwopDocumentData> getExpwopInvoice() {
		return expwopInvoice;
	}

	public void setExpwopInvoice(List<Anx1ExpwpAndExpwopDocumentData> expwopInvoice) {
		this.expwopInvoice = expwopInvoice;
	}

	public List<Anx1SezwpAndSezwopData> getSezwpInvoice() {
		return sezwpInvoice;
	}

	public void setSezwpInvoice(List<Anx1SezwpAndSezwopData> sezwpInvoice) {
		this.sezwpInvoice = sezwpInvoice;
	}

	public List<Anx1SezwpAndSezwopData> getSezwopInvoice() {
		return sezwopInvoice;
	}

	public void setSezwopInvoice(List<Anx1SezwpAndSezwopData> sezwopInvoice) {
		this.sezwopInvoice = sezwopInvoice;
	}

	public List<Anx1DeemadExportsData> getDeInvoice() {
		return deInvoice;
	}

	public void setDeInvoice(List<Anx1DeemadExportsData> deInvoice) {
		this.deInvoice = deInvoice;
	}

	public List<Anx1RevData> getRevInvoice() {
		return revInvoice;
	}

	public void setRevInvoice(List<Anx1RevData> revInvoice) {
		this.revInvoice = revInvoice;
	}

	public List<Anx1ImpsDocumentData> getImpsInvoice() {
		return impsInvoice;
	}

	public void setImpsInvoice(List<Anx1ImpsDocumentData> impsInvoice) {
		this.impsInvoice = impsInvoice;
	}

	public List<Anx1ImpgAndImpgSezData> getImpgInvoice() {
		return impgInvoice;
	}

	public void setImpgInvoice(List<Anx1ImpgAndImpgSezData> impgInvoice) {
		this.impgInvoice = impgInvoice;
	}

	public List<Anx1ImpgAndImpgSezData> getImpgsezInvoice() {
		return impgsezInvoice;
	}

	public void setImpgsezInvoice(List<Anx1ImpgAndImpgSezData> impgsezInvoice) {
		this.impgsezInvoice = impgsezInvoice;
	}

	public List<Anx1MisData> getMisInvoice() {
		return misInvoice;
	}

	public void setMisInvoice(List<Anx1MisData> misInvoice) {
		this.misInvoice = misInvoice;
	}

	public List<Anx1EcomData> getEcomInvoice() {
		return ecomInvoice;
	}

	public void setEcomInvoice(List<Anx1EcomData> ecomInvoice) {
		this.ecomInvoice = ecomInvoice;
	}

}
