package com.ey.advisory.app.docs.dto.gstr2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is User For Review Summary in Gstr2
 * @author Balakrishna.S
 *
 */

public class Gstr2SummaryDto {
	
	@Expose
	@SerializedName("b2b")  //B2B Invoices
	private Gstr2BasicSummaryDto b2b;

	@Expose
	@SerializedName("cdnr")  //credit/debit Notes Register
	private Gstr2BasicSummaryDto cdnr;

	@Expose
	@SerializedName("cdnur") // credit/debit Notes Un Register
	private Gstr2BasicSummaryDto cdnur;

	@Expose
	@SerializedName("ab2b")  // Ammended B2B
	private Gstr2BasicSummaryDto ab2b;

	@Expose
	@SerializedName("ab2bur") // Ammended B2BUR
	private Gstr2BasicSummaryDto ab2bur;

	@Expose
	@SerializedName("aios") // Ammended Import of Service
	private Gstr2BasicSummaryDto aios;

	@Expose
	@SerializedName("b2bur") // B2B Un Registered
	private Gstr2BasicSummaryDto b2bur;
	
	@Expose
	@SerializedName("acdnr") // Ammended credit/debit Notes Register
	private Gstr2BasicSummaryDto acdnr;
	
	@Expose
	@SerializedName("acdnur")// Ammended credit/debit Notes Un Register
	private Gstr2BasicSummaryDto acdnur;
	
	@Expose
	@SerializedName("igcg") //import of Goods / Capital Goods
	private Gstr2BasicSummaryDto igcg;
	
	@Expose
	@SerializedName("imos") //import of services 
	private Gstr2BasicSummaryDto imos;
	
	@Expose
	@SerializedName("aiog") // Ammended Import of Goods 
	private Gstr2BasicSummaryDto aiog;
	
	@Expose
	@SerializedName("nil") // Nil/Composition/Exempted/Non Gst Supplies
	private Gstr2NilBasicSummaryDto nil;
	
	@Expose
	@SerializedName("ap") // Advances Paid
	private Gstr2BasicSummaryDto ap;
	
	@Expose
	@SerializedName("at") // Advances Adjusted
	private Gstr2BasicSummaryDto at;

	@Expose
	@SerializedName("apa")// Advances Paid Ammended
	private Gstr2BasicSummaryDto apa;
	
	@Expose
	@SerializedName("ata") // Advances Adjusted Ammended
	private Gstr2BasicSummaryDto ata;
	
	@Expose
	@SerializedName("itcr") // ITC Reversal
	private Gstr2BasicSummaryDto itcr;

	@Expose
	@SerializedName("aitcr") //Ammended ITC Reversal
	private Gstr2BasicSummaryDto aitcr;
	
	@Expose
	@SerializedName("hsn") // HSN/SAC
	private Gstr2BasicSummaryDto hsn;

	/**
	 * @return the b2b
	 */
	public Gstr2BasicSummaryDto getB2b() {
		return b2b;
	}

	/**
	 * @param b2b the b2b to set
	 */
	public void setB2b(Gstr2BasicSummaryDto b2b) {
		this.b2b = b2b;
	}

	/**
	 * @return the cdnr
	 */
	public Gstr2BasicSummaryDto getCdnr() {
		return cdnr;
	}

	/**
	 * @param cdnr the cdnr to set
	 */
	public void setCdnr(Gstr2BasicSummaryDto cdnr) {
		this.cdnr = cdnr;
	}

	/**
	 * @return the cdnur
	 */
	public Gstr2BasicSummaryDto getCdnur() {
		return cdnur;
	}

	/**
	 * @param cdnur the cdnur to set
	 */
	public void setCdnur(Gstr2BasicSummaryDto cdnur) {
		this.cdnur = cdnur;
	}

	/**
	 * @return the ab2b
	 */
	public Gstr2BasicSummaryDto getAb2b() {
		return ab2b;
	}

	/**
	 * @param ab2b the ab2b to set
	 */
	public void setAb2b(Gstr2BasicSummaryDto ab2b) {
		this.ab2b = ab2b;
	}

	/**
	 * @return the ab2bur
	 */
	public Gstr2BasicSummaryDto getAb2bur() {
		return ab2bur;
	}

	/**
	 * @param ab2bur the ab2bur to set
	 */
	public void setAb2bur(Gstr2BasicSummaryDto ab2bur) {
		this.ab2bur = ab2bur;
	}

	/**
	 * @return the aios
	 */
	public Gstr2BasicSummaryDto getAios() {
		return aios;
	}

	/**
	 * @param aios the aios to set
	 */
	public void setAios(Gstr2BasicSummaryDto aios) {
		this.aios = aios;
	}

	/**
	 * @return the b2bur
	 */
	public Gstr2BasicSummaryDto getB2bur() {
		return b2bur;
	}

	/**
	 * @param b2bur the b2bur to set
	 */
	public void setB2bur(Gstr2BasicSummaryDto b2bur) {
		this.b2bur = b2bur;
	}

	/**
	 * @return the acdnr
	 */
	public Gstr2BasicSummaryDto getAcdnr() {
		return acdnr;
	}

	/**
	 * @param acdnr the acdnr to set
	 */
	public void setAcdnr(Gstr2BasicSummaryDto acdnr) {
		this.acdnr = acdnr;
	}

	/**
	 * @return the acdnur
	 */
	public Gstr2BasicSummaryDto getAcdnur() {
		return acdnur;
	}

	/**
	 * @param acdnur the acdnur to set
	 */
	public void setAcdnur(Gstr2BasicSummaryDto acdnur) {
		this.acdnur = acdnur;
	}

	/**
	 * @return the igcg
	 */
	public Gstr2BasicSummaryDto getIgcg() {
		return igcg;
	}

	/**
	 * @param igcg the igcg to set
	 */
	public void setIgcg(Gstr2BasicSummaryDto igcg) {
		this.igcg = igcg;
	}

	/**
	 * @return the imos
	 */
	public Gstr2BasicSummaryDto getImos() {
		return imos;
	}

	/**
	 * @param imos the imos to set
	 */
	public void setImos(Gstr2BasicSummaryDto imos) {
		this.imos = imos;
	}

	/**
	 * @return the aiog
	 */
	public Gstr2BasicSummaryDto getAiog() {
		return aiog;
	}

	/**
	 * @param aiog the aiog to set
	 */
	public void setAiog(Gstr2BasicSummaryDto aiog) {
		this.aiog = aiog;
	}

	/**
	 * @return the ap
	 */
	public Gstr2BasicSummaryDto getAp() {
		return ap;
	}

	/**
	 * @param ap the ap to set
	 */
	public void setAp(Gstr2BasicSummaryDto ap) {
		this.ap = ap;
	}

	/**
	 * @return the at
	 */
	public Gstr2BasicSummaryDto getAt() {
		return at;
	}

	/**
	 * @param at the at to set
	 */
	public void setAt(Gstr2BasicSummaryDto at) {
		this.at = at;
	}

	/**
	 * @return the apa
	 */
	public Gstr2BasicSummaryDto getApa() {
		return apa;
	}

	/**
	 * @param apa the apa to set
	 */
	public void setApa(Gstr2BasicSummaryDto apa) {
		this.apa = apa;
	}

	/**
	 * @return the ata
	 */
	public Gstr2BasicSummaryDto getAta() {
		return ata;
	}

	/**
	 * @param ata the ata to set
	 */
	public void setAta(Gstr2BasicSummaryDto ata) {
		this.ata = ata;
	}

	/**
	 * @return the itcr
	 */
	public Gstr2BasicSummaryDto getItcr() {
		return itcr;
	}

	/**
	 * @param itcr the itcr to set
	 */
	public void setItcr(Gstr2BasicSummaryDto itcr) {
		this.itcr = itcr;
	}

	/**
	 * @return the aitcr
	 */
	public Gstr2BasicSummaryDto getAitcr() {
		return aitcr;
	}

	/**
	 * @param aitcr the aitcr to set
	 */
	public void setAitcr(Gstr2BasicSummaryDto aitcr) {
		this.aitcr = aitcr;
	}

	/**
	 * @return the hsn
	 */
	public Gstr2BasicSummaryDto getHsn() {
		return hsn;
	}

	/**
	 * @param hsn the hsn to set
	 */
	public void setHsn(Gstr2BasicSummaryDto hsn) {
		this.hsn = hsn;
	}

	/**
	 * @return the nil
	 */
	public Gstr2NilBasicSummaryDto getNil() {
		return nil;
	}

	/**
	 * @param nil the nil to set
	 */
	public void setNil(Gstr2NilBasicSummaryDto nil) {
		this.nil = nil;
	}
	
	
	
	


}
