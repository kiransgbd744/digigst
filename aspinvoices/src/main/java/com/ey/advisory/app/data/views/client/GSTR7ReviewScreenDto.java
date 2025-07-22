/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 */
public class GSTR7ReviewScreenDto {
	
	private String GSTIN;
	private String Section;
	private String digigstCount;
	private String digigstTotalAmt;
	private String digigstIgst;
	private String digigstCgst;
	private String digigstSgst;
	private String gstnCount;
	private String gstntTotalAmt;
	private String gstnIgst;
	private String gstnCgst;
	private String gstnSgst;
	private String diffCount;
	private String diffTotalAmt;
	private String diffIgst;
	private String diffCgst;
	private String diffSgst;
	
	
	/**
	 * @return the section
	 */
	public String getSection() {
		return Section;
	}
	/**
	 * @param section the section to set
	 */
	public void setSection(String section) {
		Section = section;
	}
	/**
	 * @return the gSTIN
	 */
	public String getGSTIN() {
		return GSTIN;
	}
	/**
	 * @param gSTIN the gSTIN to set
	 */
	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
	}
	/**
	 * @return the digigstCount
	 */
	public String getDigigstCount() {
		return digigstCount;
	}
	/**
	 * @param digigstCount the digigstCount to set
	 */
	public void setDigigstCount(String digigstCount) {
		this.digigstCount = digigstCount;
	}
	/**
	 * @return the digigstTotalAmt
	 */
	public String getDigigstTotalAmt() {
		return digigstTotalAmt;
	}
	/**
	 * @param digigstTotalAmt the digigstTotalAmt to set
	 */
	public void setDigigstTotalAmt(String digigstTotalAmt) {
		this.digigstTotalAmt = digigstTotalAmt;
	}
	/**
	 * @return the digigstIgst
	 */
	public String getDigigstIgst() {
		return digigstIgst;
	}
	/**
	 * @param digigstIgst the digigstIgst to set
	 */
	public void setDigigstIgst(String digigstIgst) {
		this.digigstIgst = digigstIgst;
	}
	/**
	 * @return the digigstCgst
	 */
	public String getDigigstCgst() {
		return digigstCgst;
	}
	/**
	 * @param digigstCgst the digigstCgst to set
	 */
	public void setDigigstCgst(String digigstCgst) {
		this.digigstCgst = digigstCgst;
	}
	/**
	 * @return the digigstSgst
	 */
	public String getDigigstSgst() {
		return digigstSgst;
	}
	/**
	 * @param digigstSgst the digigstSgst to set
	 */
	public void setDigigstSgst(String digigstSgst) {
		this.digigstSgst = digigstSgst;
	}
	/**
	 * @return the gstnCount
	 */
	public String getGstnCount() {
		return gstnCount;
	}
	/**
	 * @param gstnCount the gstnCount to set
	 */
	public void setGstnCount(String gstnCount) {
		this.gstnCount = gstnCount;
	}
	/**
	 * @return the gstntTotalAmt
	 */
	public String getGstntTotalAmt() {
		return gstntTotalAmt;
	}
	/**
	 * @param gstntTotalAmt the gstntTotalAmt to set
	 */
	public void setGstntTotalAmt(String gstntTotalAmt) {
		this.gstntTotalAmt = gstntTotalAmt;
	}
	/**
	 * @return the gstnIgst
	 */
	public String getGstnIgst() {
		return gstnIgst;
	}
	/**
	 * @param gstnIgst the gstnIgst to set
	 */
	public void setGstnIgst(String gstnIgst) {
		this.gstnIgst = gstnIgst;
	}
	/**
	 * @return the gstnCgst
	 */
	public String getGstnCgst() {
		return gstnCgst;
	}
	/**
	 * @param gstnCgst the gstnCgst to set
	 */
	public void setGstnCgst(String gstnCgst) {
		this.gstnCgst = gstnCgst;
	}
	/**
	 * @return the gstnSgst
	 */
	public String getGstnSgst() {
		return gstnSgst;
	}
	/**
	 * @param gstnSgst the gstnSgst to set
	 */
	public void setGstnSgst(String gstnSgst) {
		this.gstnSgst = gstnSgst;
	}
	/**
	 * @return the diffCount
	 */
	public String getDiffCount() {
		return diffCount;
	}
	/**
	 * @param diffCount the diffCount to set
	 */
	public void setDiffCount(String diffCount) {
		this.diffCount = diffCount;
	}
	/**
	 * @return the diffTotalAmt
	 */
	public String getDiffTotalAmt() {
		return diffTotalAmt;
	}
	/**
	 * @param diffTotalAmt the diffTotalAmt to set
	 */
	public void setDiffTotalAmt(String diffTotalAmt) {
		this.diffTotalAmt = diffTotalAmt;
	}
	/**
	 * @return the diffIgst
	 */
	public String getDiffIgst() {
		return diffIgst;
	}
	/**
	 * @param diffIgst the diffIgst to set
	 */
	public void setDiffIgst(String diffIgst) {
		this.diffIgst = diffIgst;
	}
	/**
	 * @return the diffCgst
	 */
	public String getDiffCgst() {
		return diffCgst;
	}
	/**
	 * @param diffCgst the diffCgst to set
	 */
	public void setDiffCgst(String diffCgst) {
		this.diffCgst = diffCgst;
	}
	/**
	 * @return the diffSgst
	 */
	public String getDiffSgst() {
		return diffSgst;
	}
	/**
	 * @param diffSgst the diffSgst to set
	 */
	public void setDiffSgst(String diffSgst) {
		this.diffSgst = diffSgst;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GSTR7ReviewScreenDto [GSTIN=" + GSTIN + ", Section=" + Section
				+ ", digigstCount=" + digigstCount + ", digigstTotalAmt="
				+ digigstTotalAmt + ", digigstIgst=" + digigstIgst
				+ ", digigstCgst=" + digigstCgst + ", digigstSgst="
				+ digigstSgst + ", gstnCount=" + gstnCount + ", gstntTotalAmt="
				+ gstntTotalAmt + ", gstnIgst=" + gstnIgst + ", gstnCgst="
				+ gstnCgst + ", gstnSgst=" + gstnSgst + ", diffCount="
				+ diffCount + ", diffTotalAmt=" + diffTotalAmt + ", diffIgst="
				+ diffIgst + ", diffCgst=" + diffCgst + ", diffSgst=" + diffSgst
				+ "]";
	}
	
	

}
