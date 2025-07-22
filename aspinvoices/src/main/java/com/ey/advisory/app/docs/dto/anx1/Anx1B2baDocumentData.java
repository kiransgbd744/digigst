package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Anand3.M
 *
 */
public class Anx1B2baDocumentData {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("octin")
	private String octin;

	@Expose
	@SerializedName("doctyp")
	private String doctyp;

	@Expose
	@SerializedName("odoctyp")
	private String odoctyp;

	@Expose
	@SerializedName("invalid")
	private String invalid;

	@Expose
	@SerializedName("diffprcnt")
	private BigDecimal diffprcnt;

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
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("amended")
	private String amended;

	@Expose
	@SerializedName("aprd")
	private String aprd;

	@Expose
	@SerializedName("amdtyp")
	private String amdtyp;

	@Expose
	@SerializedName("doc")
	private Anx1B2bDocumentDetails doc;

	@Expose
	@SerializedName("odoc")
	private Anx1B2bDocumentDetails odoc;

	@Expose
	@SerializedName("items")
	private List<Anx1B2bItemDetails> items;

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getOctin() {
		return octin;
	}

	public void setOctin(String octin) {
		this.octin = octin;
	}

	public String getDoctyp() {
		return doctyp;
	}

	public void setDoctyp(String doctyp) {
		this.doctyp = doctyp;
	}

	public String getOdoctyp() {
		return odoctyp;
	}

	public void setOdoctyp(String odoctyp) {
		this.odoctyp = odoctyp;
	}

	public String getInvalid() {
		return invalid;
	}

	public void setInvalid(String invalid) {
		this.invalid = invalid;
	}

	public BigDecimal getDiffprcnt() {
		return diffprcnt;
	}

	public void setDiffprcnt(BigDecimal diffprcnt) {
		this.diffprcnt = diffprcnt;
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

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getAmended() {
		return amended;
	}

	public void setAmended(String amended) {
		this.amended = amended;
	}

	public String getAprd() {
		return aprd;
	}

	public void setAprd(String aprd) {
		this.aprd = aprd;
	}

	public String getAmdtyp() {
		return amdtyp;
	}

	public void setAmdtyp(String amdtyp) {
		this.amdtyp = amdtyp;
	}

	public Anx1B2bDocumentDetails getDoc() {
		return doc;
	}

	public void setDoc(Anx1B2bDocumentDetails doc) {
		this.doc = doc;
	}

	public Anx1B2bDocumentDetails getOdoc() {
		return odoc;
	}

	public void setOdoc(Anx1B2bDocumentDetails odoc) {
		this.odoc = odoc;
	}

	public List<Anx1B2bItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx1B2bItemDetails> items) {
		this.items = items;
	}

}
