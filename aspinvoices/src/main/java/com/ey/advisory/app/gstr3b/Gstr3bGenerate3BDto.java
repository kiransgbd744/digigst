package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr3bGenerate3BDto  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Expose
	private String level;
	
	@Expose
	private Boolean lastLevel;
	
	@Expose
	private Boolean gstnSuccess = true;
	
	@Expose
	private String table;
	
	@Expose
	private String supplyType;
	
	@Expose
	private BigDecimal computedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computdCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinCess = BigDecimal.ZERO;
	
	
	public Gstr3bGenerate3BDto(String level) {
		this.level = level;
	}

	public Gstr3bGenerate3BDto(String level, Boolean lastLevel,
			String table, String supplyType) {
		super();
		this.level = level;
		this.lastLevel = lastLevel;
		this.table = table;
		this.supplyType = supplyType;
	}
	

}
