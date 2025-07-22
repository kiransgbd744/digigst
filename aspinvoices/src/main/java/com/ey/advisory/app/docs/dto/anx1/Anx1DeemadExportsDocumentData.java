package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1DeemadExportsDocumentData {

	@Expose
	@SerializedName("errorDetails")
    private GstnErrorCodeDto errorDetails;
	
	@Expose
	@SerializedName("doctyp")
	private String doctyp;

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("diffprcnt")
	private BigDecimal diffprcnt;

	/**
	 * @return the errorDetails
	 */
	public GstnErrorCodeDto getErrorDetails() {
		return errorDetails;
	}

	/**
	 * @param errorDetails the errorDetails to set
	 */
	public void setErrorDetails(GstnErrorCodeDto errorDetails) {
		this.errorDetails = errorDetails;
	}

	@Expose
	@SerializedName("sec7act")
	private String sec7act;

	@Expose
	@SerializedName("rfndelg")
	private String rfndelg;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("clmrfnd")
	private String clmrfnd;

	@Expose
	@SerializedName("doc")
	private Anx1DeemadExportsDocumentDetails doc;

	@Expose
	@SerializedName("items")
	private List<Anx1DeemadExportsItemDetails> items;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	/**
	 * Anx1 Get API fields which is not part of Anx1 saveToGstn
	 */
	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("action")
	private String action;

	public String getDoctyp() {
		return doctyp;
	}

	public void setDoctyp(String doctyp) {
		this.doctyp = doctyp;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getClmrfnd() {
		return clmrfnd;
	}

	public void setClmrfnd(String clmrfnd) {
		this.clmrfnd = clmrfnd;
	}

	public Anx1DeemadExportsDocumentDetails getDoc() {
		return doc;
	}

	public void setDoc(Anx1DeemadExportsDocumentDetails doc) {
		this.doc = doc;
	}

	public List<Anx1DeemadExportsItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx1DeemadExportsItemDetails> items) {
		this.items = items;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
