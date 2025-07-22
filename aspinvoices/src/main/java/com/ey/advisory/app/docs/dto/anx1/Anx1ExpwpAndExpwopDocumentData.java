package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ExpwpAndExpwopDocumentData {

	@Expose
	@SerializedName("doctyp")
	private String doctyp;
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
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("rfndelg")
	private String rfndelg;

	@Expose
	@SerializedName("doc")
	private Anx1ExpwpAndExpwopDocumentDetails doc;

	@Expose
	@SerializedName("sb")
	private Anx1ExpwpAndExpwopShippingBillDetails shipBill;

	@Expose
	@SerializedName("items")
	private List<Anx1ExpwpAndExpwopItemDetials> items;

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

	public Anx1ExpwpAndExpwopDocumentDetails getDoc() {
		return doc;
	}

	public void setDoc(Anx1ExpwpAndExpwopDocumentDetails doc) {
		this.doc = doc;
	}

	public Anx1ExpwpAndExpwopShippingBillDetails getShipBill() {
		return shipBill;
	}

	public void setShipBill(Anx1ExpwpAndExpwopShippingBillDetails shipBill) {
		this.shipBill = shipBill;
	}

	public List<Anx1ExpwpAndExpwopItemDetials> getItems() {
		return items;
	}

	public void setItems(List<Anx1ExpwpAndExpwopItemDetials> items) {
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
