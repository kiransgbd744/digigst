/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Anx1ProcessedScreenDto1 {

	private Anx1ProcessedScreenDto inward;
	private Anx1ProcessedScreenDto outward;
	
	public Anx1ProcessedScreenDto getInward() {
		return inward;
	}
	public void setInward(Anx1ProcessedScreenDto inward) {
		this.inward = inward;
	}
	public Anx1ProcessedScreenDto getOutward() {
		return outward;
	}
	public void setOutward(Anx1ProcessedScreenDto outward) {
		this.outward = outward;
	}
	
	@Override
	public String toString() {
		return "Anx1ProcessedScreenDto1 [inward=" + inward + ", outward="
				+ outward + "]";
	}
	
	
}
