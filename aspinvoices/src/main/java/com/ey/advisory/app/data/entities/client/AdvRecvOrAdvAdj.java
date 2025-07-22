package com.ey.advisory.app.data.entities.client;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.Document;

/**
 * This an instance of this class represents the rolled up information of 
 * advances received during a particular tax period OR advances adjusted during
 * the specified tax period OR amendments to advances received during a 
 * previous period OR amendments to advances adjusted during a previous period.
 * 
 * The 'taxPeriod' from the base class represents the taxPeriod for which 
 * this document has to be filed.
 * 
 * Advance Received and Adjusted are stored as rolled up information for each
 * POS for each Tax Period. The line items for this object will represent the
 * rolled up information at Rate Level.
 * 
 * @author Sai.Pakanati
 *
 */
public class AdvRecvOrAdvAdj extends Document {
	
	protected String sgstin;
	
	/**
	 * Can be INTRA or INTER state.
	 */
	protected String transType;
	
	protected String pos;
	
	/**
	 * Required for amendments.
	 */
	protected String origTaxPeriod;
	
	protected String origPos;	

	protected List<AdvRecvOrAdvAdjLineItem> lineItems = new ArrayList<>();	

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getOrigTaxPeriod() {
		return origTaxPeriod;
	}

	public void setOrigTaxPeriod(String origTaxPeriod) {
		this.origTaxPeriod = origTaxPeriod;
	}

	public String getOrigPos() {
		return origPos;
	}

	public void setOrigPos(String origPos) {
		this.origPos = origPos;
	}

	public List<AdvRecvOrAdvAdjLineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<AdvRecvOrAdvAdjLineItem> lineItems) {
		this.lineItems = lineItems;
	}

	@Override
	public String toString() {
		return "AdvRecvOrAdvAdj [sgstin=" + sgstin + ", transType=" + transType
			+ ", pos=" + pos + ", origTaxPeriod=" + origTaxPeriod
			+ ", origPos=" + origPos + "]";
	}	
	
}
