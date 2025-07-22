package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class ITC04PopupRespDto {

	private String type;
	private int count;
	private BigDecimal quantity = BigDecimal.ZERO;
	private BigDecimal lossesQuantity = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private BigDecimal stateCess = BigDecimal.ZERO;
	private BigDecimal totalValue = BigDecimal.ZERO;

	private List<ITC04PopupRespDto> items;

}
