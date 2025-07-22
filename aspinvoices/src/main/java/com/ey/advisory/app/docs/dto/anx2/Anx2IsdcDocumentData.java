package com.ey.advisory.app.docs.dto.anx2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx2IsdcDocumentData {

	@Expose
	@SerializedName("org")
	private Anx2IsdcOriginalDetails orginal;

	@Expose
	@SerializedName("amd")
	private Anx2IsdcAmendementDetails amedement;

	public Anx2IsdcOriginalDetails getOrginal() {
		return orginal;
	}

	public void setOrginal(Anx2IsdcOriginalDetails orginal) {
		this.orginal = orginal;
	}

	public Anx2IsdcAmendementDetails getAmedement() {
		return amedement;
	}

	public void setAmedement(Anx2IsdcAmendementDetails amedement) {
		this.amedement = amedement;
	}

}
