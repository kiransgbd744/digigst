/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class Gstr6ProcessedSummResponseDto {

	private String gstin;

	private String retPer;

	private String timeStamp;

	private String state;

	private String regType;

	private String authToken;

	private String status;

	private String digiStatus;

	private String digiUpdatedOn;

	private int count = 0;

	private BigDecimal invoiceValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTax = BigDecimal.ZERO;

	private BigDecimal tpIgst = BigDecimal.ZERO;

	private BigDecimal tpCgst = BigDecimal.ZERO;

	private BigDecimal tpSgst = BigDecimal.ZERO;

	private BigDecimal tpCess = BigDecimal.ZERO;

	private BigDecimal totCreElig = BigDecimal.ZERO;

	private BigDecimal ceIgst = BigDecimal.ZERO;

	private BigDecimal ceCgst = BigDecimal.ZERO;

	private BigDecimal ceSgst = BigDecimal.ZERO;

	private BigDecimal ceCess = BigDecimal.ZERO;

}
