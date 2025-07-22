package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Jithendra.B
 *
 */
@Getter
@Setter
public class Gst9SaveStatusDto {

	private BigInteger id;
	private String refId;
	private String status;
	private BigInteger errorCnt;
	private String createdTime;

}
