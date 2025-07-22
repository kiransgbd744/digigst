package com.ey.advisory.app.services.dashboard.fiori;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sakshi.jain
 *
 */
@Getter
@Setter
public class Gstr3bReportGraphDetailsDto {

	private String suppGstin;
	private String month;
	private String apiCallStatus;
	private String dateTime;
	
	private String taxIgst;

	private String taxCgst;

	private String taxSgst;
	private String taxCess;
	private String taxTotal;
	
	private String itcIgst;

	private String itcCgst;
	private String itcSgst;
	private String itcCess;
	private String itcTotal;
	
	private String cashIgst;

	private String cashCgst;

	private String cashSgst;
	private String cashCess;
	private String cashTotal;

}
