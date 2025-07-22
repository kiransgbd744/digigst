package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Transient;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.ey.advisory.common.ProcessingResult;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class Gstr1VerticalSummaryRespDto {

	private Long sNo;
	private Long id;
	private String section;
	private String sgstn;
	private String taxPeriod; 
	private String transType;
	private String month;
    private String orgPos;
    private String orgStateName;
    private String newStateName;
	private String orgHsnOrSac;
	private String orgUom;
	private BigDecimal orgQunty;
	private BigDecimal orgRate;
	private BigDecimal orgTaxableValue;
	private String orgEcomGstin;
	private BigDecimal orgEcomSupplValue;
	private String newPos;
	private String newHsnOrSac;
	private String newUom;
	private BigDecimal newQunty;
	private BigDecimal newRate;
	private BigDecimal newTaxableValue;
	private String newEcomGstin;
	private BigDecimal newEcomSupplValue;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private BigDecimal totalValue;
	private String profitCntr;
	private String plant;
	private String division;
	private String location;
	private String salesOrg;
	private String distrChannel;
	private String usrAccess1;
	private String usrAccess2;
	private String usrAccess3;
	private String usrAccess4;
	private String usrAccess5;
	private String usrAccess6;
	private String usrDefined1;
	private String usrDefined2;
	private String usrDefined3;
	private List<ErrorDescriptionDto> errorList;
	private String errorField;
	private String returnType;
	@Transient
	private Long entityId;
	
	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;
	
	
}
