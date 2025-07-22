/**
 * 
 */
package com.ey.advisory.app.service.report.gstr7;

import lombok.Data;

/**
 * @author vishal.verma	
 *
 */

@Data
public class Gstr7EntitySmryStringDto {

	private String section;
	
	private String sectionDesc;
	
	private Integer aspCount;
	
	private String aspTotalAmount ;
	
	private String aspIgst ;
	
	private String aspCgst ;
	
	private String aspSgst ;
	
	private Integer gstnCount;
	
	private String gstnTotalAmount ;
	
	private String gstnIgst ;
	
	private String gstnCgst ;
	
	private String gstnSgst ;
	
	private Integer diffCount;
	
	private String diffTotalAmount ;
	
	private String diffIgst ;
	
	private String diffCgst ;
	
	private String diffSgst ;
	
	private String gstin;
}
