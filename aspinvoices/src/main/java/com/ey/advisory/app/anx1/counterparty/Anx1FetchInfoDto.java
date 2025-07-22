package com.ey.advisory.app.anx1.counterparty;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Anx1FetchInfoDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Expose
	private String lastFetchStatus;
	
	@Expose
	private Date lastFetchDate;
	
	@Expose
	private String gstin;

	
}
