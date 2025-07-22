package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Data
public class Gstr7PopupRecordsRespDto {


private int id;
private String gstin;
private String docKey;
private BigDecimal amount;
private BigDecimal igst;
private BigDecimal cgst;
private BigDecimal sgst;           




private String odMonth;
private String odGstin;
private BigDecimal odAmount;
private String rdGstin;
private BigDecimal rdAmount;
private BigDecimal rdIgst;
private BigDecimal rdCgst;
private BigDecimal rdSgst;        






	
}
