package com.ey.advisory.app.anx2.vendorsummary;

public class PanDto {
	
	private String pan;
	
	public PanDto() { }

	public PanDto(String pan) {
		super();
		this.pan = pan;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	@Override
	public String toString() {
		return "PanDto [pan=" + pan + "]";
	}
	
	

}
