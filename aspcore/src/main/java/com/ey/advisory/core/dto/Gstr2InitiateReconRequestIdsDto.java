package com.ey.advisory.core.dto;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2InitiateReconRequestIdsDto {

	@Expose
	private BigInteger requestId;
	
	@Expose
	private String UserId;
	
	@Expose
	private String emailId;
}
