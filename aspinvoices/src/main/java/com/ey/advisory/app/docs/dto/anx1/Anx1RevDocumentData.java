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
public class Anx1RevDocumentData {

	@Expose
	@SerializedName("flag")
	private String flag;

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
	@SerializedName("suptyp")
	private String suptyp;

	@Expose
	@SerializedName("items")
	private List<Anx1RevItemDetails> items;

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

	public String getSuptyp() {
		return suptyp;
	}

	public void setSuptyp(String suptyp) {
		this.suptyp = suptyp;
	}

	public List<Anx1RevItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx1RevItemDetails> items) {
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

}
