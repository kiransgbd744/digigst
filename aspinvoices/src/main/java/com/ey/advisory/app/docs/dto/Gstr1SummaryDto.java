package com.ey.advisory.app.docs.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * @author Mahesh.Golla
 *
 */
public class Gstr1SummaryDto {

	@Expose
	@SerializedName("b2b")
	private Gstr1BasicSummaryDto b2b;
	
	@Expose
	@SerializedName("b2ba")
	private Gstr1BasicSummaryDto b2ba;

	@Expose
	@SerializedName("b2cl")
	private Gstr1BasicSummaryDto b2cl;

	@Expose
	@SerializedName("b2cla")
	private Gstr1BasicSummaryDto bcla;

	@Expose
	@SerializedName("exp")
	private Gstr1BasicSummaryDto exp;

	@Expose
	@SerializedName("expa")
	private Gstr1BasicSummaryDto expa;

	@Expose
	@SerializedName("cdnr")
	private Gstr1BasicSummaryDto cdnr;

	@Expose
	@SerializedName("cdnra")
	private Gstr1BasicSummaryDto cdnra;

	@Expose
	@SerializedName("cdnur")
	private Gstr1BasicSummaryDto cdnur;

	@Expose
	@SerializedName("cdnura")
	private Gstr1BasicSummaryDto cdnura;

	@Expose
	@SerializedName("b2cs")
	private Gstr1BasicSummaryDto b2cs;

	@Expose
	@SerializedName("b2csa")
	private Gstr1BasicSummaryDto b2csa;

	@Expose
	@SerializedName("nil")
	private Gstr1NillratedBasicSummaryDto nil;

	@Expose
	@SerializedName("at") // advance received
	private Gstr1BasicSummaryDto at;

	@Expose
	@SerializedName("ata") // advance received amendment
	private Gstr1BasicSummaryDto ata;

	@Expose
	@SerializedName("txpd") // advance adjestment
	private Gstr1BasicSummaryDto txpd;

	@Expose
	@SerializedName("txpda") // advAdjAmendment
	private Gstr1BasicSummaryDto txpda;

	@Expose
	@SerializedName("hsn")
	private Gstr1BasicSummaryDto hsn;

	@Expose
	@SerializedName("docIssued")
	private Gstr1DocIssuedBasicSummary docIssued;

	/**
	 * SEZ with payment
	 */
	@Expose
	@SerializedName("sezwp")
	private Gstr1BasicSummaryDto sezwp;
	
	
	
	

	/**
	 * SEZ Without payment.
	 */
	@Expose
	@SerializedName("sezwop")
	private Gstr1BasicSummaryDto sezwop;
	
	public Gstr1BasicSummaryDto getB2b() {
		return b2b;
	}

	public void setB2b(Gstr1BasicSummaryDto b2b) {
		this.b2b = b2b;
	}

	public Gstr1BasicSummaryDto getB2ba() {
		return b2ba;
	}

	public void setB2ba(Gstr1BasicSummaryDto b2ba) {
		this.b2ba = b2ba;
	}

	public Gstr1BasicSummaryDto getB2cl() {
		return b2cl;
	}

	public void setB2cl(Gstr1BasicSummaryDto b2cl) {
		this.b2cl = b2cl;
	}

	public Gstr1BasicSummaryDto getBcla() {
		return bcla;
	}

	public void setBcla(Gstr1BasicSummaryDto bcla) {
		this.bcla = bcla;
	}

	public Gstr1BasicSummaryDto getExp() {
		return exp;
	}

	public void setExp(Gstr1BasicSummaryDto exp) {
		this.exp = exp;
	}

	public Gstr1BasicSummaryDto getExpa() {
		return expa;
	}

	public void setExpa(Gstr1BasicSummaryDto expa) {
		this.expa = expa;
	}

	public Gstr1BasicSummaryDto getCdnr() {
		return cdnr;
	}

	public void setCdnr(Gstr1BasicSummaryDto cdnr) {
		this.cdnr = cdnr;
	}

	public Gstr1BasicSummaryDto getCdnra() {
		return cdnra;
	}

	public void setCdnra(Gstr1BasicSummaryDto cdnra) {
		this.cdnra = cdnra;
	}

	public Gstr1BasicSummaryDto getCdnur() {
		return cdnur;
	}

	public void setCdnur(Gstr1BasicSummaryDto cdnur) {
		this.cdnur = cdnur;
	}

	public Gstr1BasicSummaryDto getCdnura() {
		return cdnura;
	}

	public void setCdnura(Gstr1BasicSummaryDto cdnura) {
		this.cdnura = cdnura;
	}

	public Gstr1BasicSummaryDto getB2cs() {
		return b2cs;
	}

	public void setB2cs(Gstr1BasicSummaryDto b2cs) {
		this.b2cs = b2cs;
	}

	public Gstr1BasicSummaryDto getB2csa() {
		return b2csa;
	}

	public void setB2csa(Gstr1BasicSummaryDto b2csa) {
		this.b2csa = b2csa;
	}

	public Gstr1NillratedBasicSummaryDto getNil() {
		return nil;
	}

	public void setNil(Gstr1NillratedBasicSummaryDto nil) {
		this.nil = nil;
	}

	public Gstr1BasicSummaryDto getAt() {
		return at;
	}

	public void setAt(Gstr1BasicSummaryDto at) {
		this.at = at;
	}

	
	public Gstr1BasicSummaryDto getAta() {
		return ata;
	}

	public void setAta(Gstr1BasicSummaryDto ata) {
		this.ata = ata;
	}

	public Gstr1BasicSummaryDto getTxpd() {
		return txpd;
	}

	public void setTxpd(Gstr1BasicSummaryDto txpd) {
		this.txpd = txpd;
	}

	public Gstr1BasicSummaryDto getTxpda() {
		return txpda;
	}

	public void setTxpda(Gstr1BasicSummaryDto txpda) {
		this.txpda = txpda;
	}

	public Gstr1BasicSummaryDto getHsn() {
		return hsn;
	}

	public void setHsn(Gstr1BasicSummaryDto hsn) {
		this.hsn = hsn;
	}

	public Gstr1DocIssuedBasicSummary getDocIssued() {
		return docIssued;
	}

	public void setDocIssued(Gstr1DocIssuedBasicSummary docIssued) {
		this.docIssued = docIssued;
	}

	public Gstr1BasicSummaryDto getSezwp() {
		return sezwp;
	}

	public void setSezwp(Gstr1BasicSummaryDto sezwp) {
		this.sezwp = sezwp;
	}

	public Gstr1BasicSummaryDto getSezwop() {
		return sezwop;
	}

	public void setSezwop(Gstr1BasicSummaryDto sezwop) {
		this.sezwop = sezwop;
	}
}
