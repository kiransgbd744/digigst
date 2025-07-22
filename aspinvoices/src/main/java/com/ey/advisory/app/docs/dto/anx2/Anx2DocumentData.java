package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx2DocumentData {

	@Expose
	@SerializedName("doctyp")
	private String invoiceType;

	@Expose
	@SerializedName("diffprcnt")
	private BigDecimal diffPercent;

	@Expose
	@SerializedName("sec7act")
	private String sec7act;

	@Expose
	@SerializedName("rfndelg")
	private String rfndelg;

	@Expose
	@SerializedName("action")
	private String action;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("itcent")
	private String itcent;

	@Expose
	@SerializedName("uplddt")
	private String uploadDate;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("doc")
	private Anx2DocumentDetails doc;

	@Expose
	@SerializedName("items")
	private List<Anx2ItemDetails> items;

	@Expose
	@SerializedName("clmrfnd")
	private String claimRfnd;
	
	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public BigDecimal getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(BigDecimal diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getSec7act() {
		return sec7act;
	}

	public void setSec7act(String sec7act) {
		this.sec7act = sec7act;
	}

	public String getRfndelg() {
		return rfndelg;
	}

	public void setRfndelg(String rfndelg) {
		this.rfndelg = rfndelg;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getItcent() {
		return itcent;
	}

	public void setItcent(String itcent) {
		this.itcent = itcent;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public Anx2DocumentDetails getDoc() {
		return doc;
	}

	public void setDoc(Anx2DocumentDetails doc) {
		this.doc = doc;
	}

	public List<Anx2ItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx2ItemDetails> items) {
		this.items = items;
	}

	public String getClaimRfnd() {
		return claimRfnd;
	}

	public void setClaimRfnd(String claimRfnd) {
		this.claimRfnd = claimRfnd;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

}
