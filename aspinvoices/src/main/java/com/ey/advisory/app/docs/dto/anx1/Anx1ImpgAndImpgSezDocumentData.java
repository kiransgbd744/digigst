package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ImpgAndImpgSezDocumentData {

	@Expose
	@SerializedName("doctyp")
	private String doctyp;

	@Expose
	@SerializedName("rfndelg")
	private String rfndelg;
	
	@Expose
	@SerializedName("pos")
	private String impgSezPos;

	@Expose
	@SerializedName("flag")
	private String flag;
	
	@Expose
	@SerializedName("boe")
	private Anx1ImpgAndImpgSezBillEntryDetails boe;

	@Expose
	@SerializedName("items")
	private List<Anx1ImpgAndImpgSezItemDetails> items;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;
	
	/**
	 * Anx1 Get API fields which is not part of Anx1 saveToGstn
	 */
	@Expose
	@SerializedName("chksum")
	private String checkSum;
	

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

	public String getRfndelg() {
		return rfndelg;
	}

	public void setRfndelg(String rfndelg) {
		this.rfndelg = rfndelg;
	}

	public String getImpgSezPos() {
		return impgSezPos;
	}

	public void setImpgSezPos(String impgSezPos) {
		this.impgSezPos = impgSezPos;
	}

	public Anx1ImpgAndImpgSezBillEntryDetails getBoe() {
		return boe;
	}

	public void setBoe(Anx1ImpgAndImpgSezBillEntryDetails boe) {
		this.boe = boe;
	}

	public List<Anx1ImpgAndImpgSezItemDetails> getItems() {
		return items;
	}

	public void setItems(List<Anx1ImpgAndImpgSezItemDetails> items) {
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

}
