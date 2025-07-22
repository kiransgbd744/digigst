package com.ey.advisory.processing.message;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetCounterPartyMsg {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("date")
	private LocalDate date;
	
	@Expose
	@SerializedName("controlId")
	private Long controlId;
	
	@Expose
	@SerializedName("ewbNo")
	private String ewbNo;
	

}
