package com.ey.advisory.app.docs.dto;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Transient;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;

import lombok.Data;

@Data
public class Gstr1HsnSummarySectionDto {

	private Long id;
	private String docKey;
	private Long sNo;
    private String sgstn;
    private String taxPeriod;
    private String recordType;
    private String hsn;
    private String aspDesc;
    private String uiDesc;
    private String uqc;
    private BigDecimal taxRate =  BigDecimal.ZERO;
    private BigDecimal aspQunty =  BigDecimal.ZERO;
    private BigDecimal aspTotalValue =  BigDecimal.ZERO;
    private BigDecimal aspTaxableValue =  BigDecimal.ZERO;
    private BigDecimal aspIgst =  BigDecimal.ZERO;
    private BigDecimal aspCgst =  BigDecimal.ZERO;
    private BigDecimal aspSgst =  BigDecimal.ZERO;
    private BigDecimal aspCess =  BigDecimal.ZERO;
    private BigDecimal usrQunty =  BigDecimal.ZERO;
    private BigDecimal usrTotalValue =  BigDecimal.ZERO;
    private BigDecimal usrTaxableValue =  BigDecimal.ZERO;
    private BigDecimal usrIgst =  BigDecimal.ZERO;
    private BigDecimal usrCgst =  BigDecimal.ZERO;
    private BigDecimal usrSgst =  BigDecimal.ZERO;
    private BigDecimal usrCess =  BigDecimal.ZERO;
    private List<ErrorDescriptionDto> errorList;
    @Transient
	private Long entityId;
	
	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;

	
}
