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
public class Anx1B2bDocumentData {

	@Expose
	@SerializedName("doctyp")
	private String doctyp;

	@Expose
	@SerializedName("flag")
	private String flag;
	@Expose
	@SerializedName("errorDetails")
    private GstnErrorCodeDto errorDetails;
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
	@SerializedName("diffprcnt")
	private BigDecimal diffprcntSaveToGstn;

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
	@SerializedName("doc")
	private Anx1B2bDocumentDetails doc;

	@Expose
	@SerializedName("items")
	private List<Anx1B2bItemDetails> items;

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
	@SerializedName("diff_percent")
	private BigDecimal diffprcntGet;

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

	public BigDecimal getDiffprcntSaveToGstn() {
		return diffprcntSaveToGstn;
	}

	public void setDiffprcntSaveToGstn(BigDecimal diffprcntSaveToGstn) {
		this.diffprcntSaveToGstn = diffprcntSaveToGstn;
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

	public Anx1B2bDocumentDetails getDoc() {
		return doc;
	}

	public void setDoc(Anx1B2bDocumentDetails doc) {
		this.doc = doc;
	}

	public List<Anx1B2bItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx1B2bItemDetails> items) {
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

	public BigDecimal getDiffprcntGet() {
		return diffprcntGet;
	}

	public void setDiffprcntGet(BigDecimal diffprcntGet) {
		this.diffprcntGet = diffprcntGet;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
