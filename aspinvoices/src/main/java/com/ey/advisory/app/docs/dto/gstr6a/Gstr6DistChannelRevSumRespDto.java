package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6DistChannelRevSumRespDto {

	private String docType;
	private String distribution;
	private BigInteger aspCount =  BigInteger.ZERO;
	private BigDecimal aspIgstasIgst = BigDecimal.ZERO;
	private BigDecimal aspIgstasSgst = BigDecimal.ZERO;
	private BigDecimal aspIgstasCgst = BigDecimal.ZERO;
	private BigDecimal aspSgstasSgst = BigDecimal.ZERO;
	private BigDecimal aspSgstasIgst = BigDecimal.ZERO;
	private BigDecimal aspCgstasCgst = BigDecimal.ZERO;
	private BigDecimal aspCgstasIgst = BigDecimal.ZERO;
	private BigDecimal aspCessAmount = BigDecimal.ZERO;
	
	private BigInteger gstnCount =  BigInteger.ZERO;
	private BigDecimal gstnIgstasIgst = BigDecimal.ZERO;
	private BigDecimal gstnIgstasSgst = BigDecimal.ZERO;
	private BigDecimal gstnIgstasCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgstasSgst = BigDecimal.ZERO;
	private BigDecimal gstnSgstasIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgstasCgst = BigDecimal.ZERO;
	private BigDecimal gstnCgstasIgst = BigDecimal.ZERO;
	private BigDecimal gstnCessAmount = BigDecimal.ZERO;
	
	private BigInteger diffCount =  BigInteger.ZERO;
	private BigDecimal diffIgstasIgst = BigDecimal.ZERO;
	private BigDecimal diffIgstasSgst = BigDecimal.ZERO;
	private BigDecimal diffIgstasCgst = BigDecimal.ZERO;
	private BigDecimal diffSgstasSgst = BigDecimal.ZERO;
	private BigDecimal diffSgstasIgst = BigDecimal.ZERO;
	private BigDecimal diffCgstasCgst = BigDecimal.ZERO;
	private BigDecimal diffCgstasIgst = BigDecimal.ZERO;
	private BigDecimal diffCessAmount = BigDecimal.ZERO;

	private List<Gstr6DistChannelRevSumRespItemDto> items;

}
