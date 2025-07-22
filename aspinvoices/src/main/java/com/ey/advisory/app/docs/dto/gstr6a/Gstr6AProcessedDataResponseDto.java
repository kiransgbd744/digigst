package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6AProcessedDataResponseDto {

	private String gstin;
	private String retPeriod;
	private String state;
	private String authToken;
	private String gstrStatus;
	private String status;
	private String timeStamp;
	private String regType;
	private BigInteger count = BigInteger.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private BigDecimal inVoiceVal = BigDecimal.ZERO;

}
