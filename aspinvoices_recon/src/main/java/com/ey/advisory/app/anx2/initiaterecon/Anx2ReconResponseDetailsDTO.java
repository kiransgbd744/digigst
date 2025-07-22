package com.ey.advisory.app.anx2.initiaterecon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Anx2ReconResponseDetailsDTO {

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
	@SerializedName("a2")
	private Anx2ReconResponseTableDTO a2;

	@Expose
	@SerializedName("pr")
	private Anx2ReconResponseTableDTO pr;

	@Expose
	@SerializedName("pra")
	private Anx2ReconResponseTableDTO pra;

	public Anx2ReconResponseDetailsDTO(String gstin,
			Anx2ReconResponseTableDTO a2, Anx2ReconResponseTableDTO pr2,
			Anx2ReconResponseTableDTO pra2) {
		this.gstin = gstin;
		this.a2 = a2;
		this.a2 = pr;
		this.a2 = pra;
	}

	public Anx2ReconResponseDetailsDTO copy(Anx2ReconResponseDetailsDTO obj,
			String authToken, String stateName) {
		Anx2ReconResponseDetailsDTO retObj = new Anx2ReconResponseDetailsDTO(
				obj.gstin, obj.a2, obj.pr, obj.pra);
		retObj.authStatus = authToken;
		retObj.stateName = stateName;
		return retObj;
	}

	public Anx2ReconResponseDetailsDTO(String gstin) {
		this.gstin = gstin;

	}

	public Anx2ReconResponseDetailsDTO() {}

}
