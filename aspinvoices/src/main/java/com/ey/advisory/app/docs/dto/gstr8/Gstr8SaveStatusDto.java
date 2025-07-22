package com.ey.advisory.app.docs.dto.gstr8;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Getter
@Setter
public class Gstr8SaveStatusDto {

	private BigInteger id;
	private String refId;
	private String status;
	private BigInteger errorCnt;
	private String createdTime;

}
