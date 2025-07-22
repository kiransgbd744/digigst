package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Siva.Nandam
 *
 */
public class Anx1ErrorReport {

	@Expose
	@SerializedName("data")
	private Anx1ErrorData anx1ErrorData;

	/**
	 * @return the anx1ErrorData
	 */
	public Anx1ErrorData getAnx1ErrorData() {
		return anx1ErrorData;
	}

	/**
	 * @param anx1ErrorData the anx1ErrorData to set
	 */
	public void setAnx1ErrorData(Anx1ErrorData anx1ErrorData) {
		this.anx1ErrorData = anx1ErrorData;
	}
	
	

}

