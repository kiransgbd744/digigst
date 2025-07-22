/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;

import lombok.Data;

/**
 * @author Balakrishna.S
 *
 */
@Data
public class Gstr6CrossItcRequestDto {

	private Long sNo;
	private String taxPeriod;
	private String docKey;
    private String isdGstin;
    private String status;
    private String digigstComputeTimestamp;
    private BigDecimal digigstIgstigst;
    private BigDecimal digigstSgstigst;
    private BigDecimal digigstCgstigst;
    private BigDecimal digigstSgstSgst;
    private BigDecimal digigstIgstSgst;
    private BigDecimal digigstCgstCgst;
    private BigDecimal digigstIgstCgst;
    private BigDecimal digigstCesscess;
    private BigDecimal userIgstigst;
    private BigDecimal userSgstigst;
    private BigDecimal userCgstigst;
    private BigDecimal userSgstSgst;
    private BigDecimal userIgstSgst;
    private BigDecimal userCgstCgst;
    private BigDecimal userIgstCgst;
    private BigDecimal userCesscess;
    private List<ErrorDescriptionDto> errorList;
    private String state;
    private String authToken;
    private String regType;
    

	
}
