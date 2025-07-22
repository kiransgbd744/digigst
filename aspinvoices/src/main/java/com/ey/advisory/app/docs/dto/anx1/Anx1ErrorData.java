package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Siva.Nandam
 *
 */
public class Anx1ErrorData {

	@Expose
	@SerializedName("b2c")
	private List<Anx1B2cData> b2cInvoice;

	@Expose
	@SerializedName("b2b")
	private List<Anx1B2bData> b2bInvoice;

	@Expose
	@SerializedName("expwp")
	private List<Anx1ExpwpAndExpwopData> expwpInvoice;

	@Expose
	@SerializedName("expwop")
	private List<Anx1ExpwpAndExpwopData> expwopInvoice;

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
	private List<Anx1ImpsData> impsInvoice;

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

	/**
	 * @return the b2cInvoice
	 */
	public List<Anx1B2cData> getB2cInvoice() {
		return b2cInvoice;
	}

	/**
	 * @param b2cInvoice
	 *            the b2cInvoice to set
	 */
	public void setB2cInvoice(List<Anx1B2cData> b2cInvoice) {
		this.b2cInvoice = b2cInvoice;
	}

	/**
	 * @return the b2bInvoice
	 */
	public List<Anx1B2bData> getB2bInvoice() {
		return b2bInvoice;
	}

	/**
	 * @param b2bInvoice
	 *            the b2bInvoice to set
	 */
	public void setB2bInvoice(List<Anx1B2bData> b2bInvoice) {
		this.b2bInvoice = b2bInvoice;
	}

	/**
	 * @return the expwpInvoice
	 */
	public List<Anx1ExpwpAndExpwopData> getExpwpInvoice() {
		return expwpInvoice;
	}

	/**
	 * @param expwpInvoice
	 *            the expwpInvoice to set
	 */
	public void setExpwpInvoice(List<Anx1ExpwpAndExpwopData> expwpInvoice) {
		this.expwpInvoice = expwpInvoice;
	}

	/**
	 * @return the expwopInvoice
	 */
	public List<Anx1ExpwpAndExpwopData> getExpwopInvoice() {
		return expwopInvoice;
	}

	/**
	 * @param expwopInvoice
	 *            the expwopInvoice to set
	 */
	public void setExpwopInvoice(List<Anx1ExpwpAndExpwopData> expwopInvoice) {
		this.expwopInvoice = expwopInvoice;
	}

	/**
	 * @return the sezwpInvoice
	 */
	public List<Anx1SezwpAndSezwopData> getSezwpInvoice() {
		return sezwpInvoice;
	}

	/**
	 * @param sezwpInvoice
	 *            the sezwpInvoice to set
	 */
	public void setSezwpInvoice(List<Anx1SezwpAndSezwopData> sezwpInvoice) {
		this.sezwpInvoice = sezwpInvoice;
	}

	/**
	 * @return the sezwopInvoice
	 */
	public List<Anx1SezwpAndSezwopData> getSezwopInvoice() {
		return sezwopInvoice;
	}

	/**
	 * @param sezwopInvoice
	 *            the sezwopInvoice to set
	 */
	public void setSezwopInvoice(List<Anx1SezwpAndSezwopData> sezwopInvoice) {
		this.sezwopInvoice = sezwopInvoice;
	}

	/**
	 * @return the deInvoice
	 */
	public List<Anx1DeemadExportsData> getDeInvoice() {
		return deInvoice;
	}

	/**
	 * @param deInvoice
	 *            the deInvoice to set
	 */
	public void setDeInvoice(List<Anx1DeemadExportsData> deInvoice) {
		this.deInvoice = deInvoice;
	}

	/**
	 * @return the revInvoice
	 */
	public List<Anx1RevData> getRevInvoice() {
		return revInvoice;
	}

	/**
	 * @param revInvoice
	 *            the revInvoice to set
	 */
	public void setRevInvoice(List<Anx1RevData> revInvoice) {
		this.revInvoice = revInvoice;
	}

	/**
	 * @return the impsInvoice
	 */
	public List<Anx1ImpsData> getImpsInvoice() {
		return impsInvoice;
	}

	/**
	 * @param impsInvoice
	 *            the impsInvoice to set
	 */
	public void setImpsInvoice(List<Anx1ImpsData> impsInvoice) {
		this.impsInvoice = impsInvoice;
	}

	/**
	 * @return the impgInvoice
	 */
	public List<Anx1ImpgAndImpgSezData> getImpgInvoice() {
		return impgInvoice;
	}

	/**
	 * @param impgInvoice
	 *            the impgInvoice to set
	 */
	public void setImpgInvoice(List<Anx1ImpgAndImpgSezData> impgInvoice) {
		this.impgInvoice = impgInvoice;
	}

	/**
	 * @return the impgsezInvoice
	 */
	public List<Anx1ImpgAndImpgSezData> getImpgsezInvoice() {
		return impgsezInvoice;
	}

	/**
	 * @param impgsezInvoice
	 *            the impgsezInvoice to set
	 */
	public void setImpgsezInvoice(List<Anx1ImpgAndImpgSezData> impgsezInvoice) {
		this.impgsezInvoice = impgsezInvoice;
	}

	/**
	 * @return the misInvoice
	 */
	public List<Anx1MisData> getMisInvoice() {
		return misInvoice;
	}

	/**
	 * @param misInvoice
	 *            the misInvoice to set
	 */
	public void setMisInvoice(List<Anx1MisData> misInvoice) {
		this.misInvoice = misInvoice;
	}

	/**
	 * @return the ecomInvoice
	 */
	public List<Anx1EcomData> getEcomInvoice() {
		return ecomInvoice;
	}

	/**
	 * @param ecomInvoice
	 *            the ecomInvoice to set
	 */
	public void setEcomInvoice(List<Anx1EcomData> ecomInvoice) {
		this.ecomInvoice = ecomInvoice;
	}

}
