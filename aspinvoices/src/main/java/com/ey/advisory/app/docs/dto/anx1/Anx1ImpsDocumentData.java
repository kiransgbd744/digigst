package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ImpsDocumentData {

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("rfndelg")
	private String rfndelg;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("items")
	private List<Anx1ImpsItemDetails> items;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;
	
	/**
	 * Anx1 Get API fields which is not part of Anx1 saveToGstn
	 */
	@Expose
	@SerializedName("doctyp")
	private String doctyp;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;


	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public List<Anx1ImpsItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx1ImpsItemDetails> items) {
		this.items = items;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getDoctyp() {
		return doctyp;
	}

	public void setDoctyp(String doctyp) {
		this.doctyp = doctyp;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
}