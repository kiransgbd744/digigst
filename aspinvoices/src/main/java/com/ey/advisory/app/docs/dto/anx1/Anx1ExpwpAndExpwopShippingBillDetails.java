package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ExpwpAndExpwopShippingBillDetails {

	@Expose
	@SerializedName("num")
	private String shipNum;

	@Expose
	@SerializedName("dt")
	private String shipDate;

	@Expose
	@SerializedName("pcode")
	private String portCode;

	public String getShipNum() {
		return shipNum;
	}

	public void setShipNum(String shipNum) {
		this.shipNum = shipNum;
	}

	public String getShipDate() {
		return shipDate;
	}

	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

}
