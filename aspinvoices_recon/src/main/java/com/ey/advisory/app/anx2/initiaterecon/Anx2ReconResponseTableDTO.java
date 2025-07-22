package com.ey.advisory.app.anx2.initiaterecon;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Anx2ReconResponseTableDTO {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("stateName")
	private String stateName;

	@Expose
	@SerializedName("authStatus")
	private String authStatus;

	@Expose
	@SerializedName("a1")
	private BigDecimal a1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("a2")
	private BigDecimal a2 = BigDecimal.ZERO;

	@Expose
	@SerializedName("a3")
	private BigDecimal a3 = BigDecimal.ZERO;

	@Expose
	@SerializedName("p1")
	private BigDecimal p1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("r1")
	private BigDecimal r1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("na2")
	private BigDecimal na2 = BigDecimal.ZERO;

	@Expose
	@SerializedName("nr")
	private BigDecimal nr = BigDecimal.ZERO;

	@Expose
	@SerializedName("npr")
	private BigDecimal npr = BigDecimal.ZERO;

	@Expose
	@SerializedName("r1u1")
	private BigDecimal r1u1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("r1u2")
	private BigDecimal r1u2 = BigDecimal.ZERO;

	@Expose
	@SerializedName("a1u1")
	private BigDecimal a1u1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("a1u2")
	private BigDecimal a1u2 = BigDecimal.ZERO;

	@Expose
	@SerializedName("aa2e")
	private BigDecimal aa2e = BigDecimal.ZERO;

	@Expose
	@SerializedName("naa")
	private BigDecimal naa = BigDecimal.ZERO;

	@Expose
	@SerializedName("u1")
	private BigDecimal u1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("u2")
	private BigDecimal u2 = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("naA2")//No Action A2
	private BigDecimal naA2 = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("naPr")//No Action Pr
	private BigDecimal naPr = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("naRc")////No Action Rc
	private BigDecimal naRc = BigDecimal.ZERO;

	public Anx2ReconResponseTableDTO() {
	}
	
	

	public Anx2ReconResponseTableDTO copy(Anx2ReconResponseTableDTO obj,
			String authToken, String stateName) {
		Anx2ReconResponseTableDTO retObj = new Anx2ReconResponseTableDTO(
				obj.gstin, obj.a1, obj.a2, obj.a3, obj.p1, obj.r1, obj.r1u1,
				obj.r1u2, obj.a1u1, obj.a1u2, obj.u1, obj.u2,
				obj.naA2, obj.naPr, obj.naRc);
		retObj.authStatus = authToken;
		retObj.stateName = stateName;
		return retObj;
	}

	public Anx2ReconResponseTableDTO(String gstin, BigDecimal a1, BigDecimal a2,
			BigDecimal a3, BigDecimal p1, BigDecimal r1, BigDecimal r1u1,
			BigDecimal r1u2, BigDecimal a1u1, BigDecimal a1u2, BigDecimal u1,
			BigDecimal u2, BigDecimal naA2, BigDecimal naPr, BigDecimal naRc) {
		super();
		this.gstin = gstin;
		if (a1 != null)
			this.a1 = a1;
		if (a2 != null)
			this.a2 = a2;
		if (a3 != null)
			this.a3 = a3;
		if (p1 != null)
			this.p1 = p1;
		if (r1 != null)
			this.r1 = r1;
		if (r1u1 != null)
			this.r1u1 = r1u1;
		if (r1u2 != null)
			this.r1u2 = r1u2;
		if (a1u1 != null)
			this.a1u1 = a1u1;
		if (a1u2 != null)
			this.a1u2 = a1u2;
		if (u1 != null)
			this.u1 = u1;
		if (u2 != null)
			this.u2 = u2;
		if (naA2 != null)
			this.naA2 = naA2;
		if (naPr != null)
			this.naPr = naPr;
		if (naRc != null)
			this.naRc = naRc;
	}

	public Anx2ReconResponseTableDTO add(Anx2ReconResponseTableDTO that) {
		return new Anx2ReconResponseTableDTO(that.gstin, this.a1.add(that.a1),
				this.a2.add(that.a2), this.a3.add(that.a3),
				this.p1.add(that.p1), this.r1.add(that.r1),
				this.r1u1.add(that.r1u1), this.r1u2.add(that.r1u2),
				this.a1u1.add(that.a1u1), this.a1u2.add(that.a1u2),
				this.u1.add(that.u1), this.u2.add(that.u2),
				this.naA2.add(that.naA2), this.naPr.add(that.naPr),
				this.naRc.add(that.naRc));

	}

	public Anx2ReconResponseTableDTO(String gstin, String uResp,
			BigDecimal amt) {
		this.gstin = gstin != null ? gstin:null;
		if (uResp != null && uResp.equalsIgnoreCase("a1"))
			this.a1 = amt != null ? this.a1.add(amt) : this.a1;
		else if (uResp != null && uResp.equalsIgnoreCase("a2"))
			this.a2 = amt != null ? this.a2.add(amt) : this.a2;
		else if (uResp != null && uResp.equalsIgnoreCase("a3"))
			this.a3 = amt != null ? this.a3.add(amt) : this.a3;
		else if (uResp != null && uResp.equalsIgnoreCase("p1"))
			this.p1 = amt != null ? this.p1.add(amt) : this.p1;
		else if (uResp != null && uResp.equalsIgnoreCase("r1"))
			this.r1 = amt != null ? this.r1.add(amt) : this.r1;
		else if (uResp != null && uResp.equalsIgnoreCase("r1u1"))
			this.r1u1 = amt != null ? this.r1u1.add(amt) : this.r1u1;
		else if (uResp != null && uResp.equalsIgnoreCase("r1u2"))
			this.r1u2 = amt != null ? this.r1u2.add(amt) : this.r1u2;
		else if (uResp != null && uResp.equalsIgnoreCase("a1u1"))
			this.a1u1 = amt != null ? this.a1u1.add(amt) : this.a1u1;
		else if (uResp != null && uResp.equalsIgnoreCase("a1u2"))
			this.a1u2 = amt != null ? this.a1u2.add(amt) : this.a1u2;
		else if (uResp != null && uResp.equalsIgnoreCase("u1"))
			this.u1 = amt != null ? this.u1.add(amt) : this.u1;
		else if (uResp != null && uResp.equalsIgnoreCase("u2"))
			this.u2 = amt != null ? this.u2.add(amt) : this.u2;
		else if (uResp != null && uResp.equalsIgnoreCase("naA2"))
			this.naA2 = amt != null ? this.naA2.add(amt) : this.naA2;
		else if (uResp != null && uResp.equalsIgnoreCase("naPr"))
			this.naPr = amt != null ? this.naPr.add(amt) : this.naPr;
		else if (uResp != null && uResp.equalsIgnoreCase("naRc"))
			this.naRc = amt != null ? this.naRc.add(amt) : this.naRc;
	}



	public Anx2ReconResponseTableDTO(String gstin) {
		super();
		this.gstin = gstin;
	}

}
