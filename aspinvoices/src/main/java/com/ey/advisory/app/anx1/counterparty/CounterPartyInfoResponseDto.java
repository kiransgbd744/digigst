package com.ey.advisory.app.anx1.counterparty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CounterPartyInfoResponseDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Expose
	private String gstin="";
	
	@Expose
	private BigDecimal notSavedTotal=BigDecimal.ZERO;
	
	@Expose
	private BigDecimal savedToGstnTotal=BigDecimal.ZERO;
	
	@Expose
	private BigDecimal accepted=BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejected=BigDecimal.ZERO;
	
	@Expose
	private BigDecimal pending=BigDecimal.ZERO;
	
	@Expose
	private BigDecimal unlock=BigDecimal.ZERO;
	
	@Expose
	private BigDecimal noAction=BigDecimal.ZERO;
	
	@Expose
	private String stateName="";
	
	@Expose
	private String authTokenStatus="";
	
	@Expose
	private String lastAnnx1FetchStatus=null;
	
	@Expose
	private Date lastAnnx1FetchDate=null;
	
	public CounterPartyInfoResponseDto(){}
	
	public CounterPartyInfoResponseDto(String gstin, BigDecimal notSavedTotal,
			BigDecimal savedToGstnTotal, BigDecimal accepted,
			BigDecimal rejected, BigDecimal pending, BigDecimal unlock,
			BigDecimal noAction) {
		super();
		this.gstin = gstin;
		this.notSavedTotal = notSavedTotal;
		this.savedToGstnTotal = savedToGstnTotal;
		this.accepted = accepted;
		this.rejected = rejected;
		this.pending = pending;
		this.unlock = unlock;
		this.noAction = noAction;
	}

}
