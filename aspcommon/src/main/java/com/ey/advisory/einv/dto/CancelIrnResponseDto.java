package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CancelIrnResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;
	
	@SerializedName("Irn")
	@Expose
	private String irn;
	
	@SerializedName("CancelDate")
	@Expose
	private LocalDateTime cancelDate;
	
	@SerializedName("ErrorCode")
	@Expose
	private String errorCode;
	
	@SerializedName("ErrorMessage")
	@Expose
	private String errorMessage;

}
